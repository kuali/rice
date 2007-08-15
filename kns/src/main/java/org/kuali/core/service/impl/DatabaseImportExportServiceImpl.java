/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversionDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.RiceConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.dao.KualiDBPlatformDao;
import org.kuali.core.exceptions.ClassNotPersistableException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DatabaseImportExportService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.OjbKualiEncryptDecryptFieldConversion;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DatabaseImportExportServiceImpl extends PersistenceServiceImplBase implements DatabaseImportExportService {
    private static final Logger LOG = Logger.getLogger(DatabaseImportExportServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private EncryptionService encryptionService;
    private KualiDBPlatformDao dbPlatformDao;
    private KualiConfigurationService configService;

    public void checkArguments(Class businessObjectClass, Set<String> attributeNames) {
        if ((businessObjectClass == null) || (attributeNames == null)) {
            throw new IllegalArgumentException("PostDataLoadEncryptionServiceImpl.encrypt does not allow a null business object Class or attributeNames Set");
        }
        ClassDescriptor classDescriptor = null;
        try {
            classDescriptor = getClassDescriptor(businessObjectClass);
        }
        catch (ClassNotPersistableException e) {
            throw new IllegalArgumentException("PostDataLoadEncryptionServiceImpl.encrypt does not handle business object classes that do not have a corresponding ClassDescriptor defined in the OJB repository", e);
        }
        for (String attributeName : attributeNames) {
            if (classDescriptor.getFieldDescriptorByName(attributeName) == null) {
                throw new IllegalArgumentException(new StringBuffer("Attribute ").append(attributeName).append(" specified to PostDataLoadEncryptionServiceImpl.encrypt is not in the OJB repository ClassDescriptor for Class ").append(businessObjectClass).toString());
            }
            if (!(classDescriptor.getFieldDescriptorByName(attributeName).getFieldConversion() instanceof OjbKualiEncryptDecryptFieldConversion)) {
                throw new IllegalArgumentException(new StringBuffer("Attribute ").append(attributeName).append(" of business object Class ").append(businessObjectClass).append(" specified to PostDataLoadEncryptionServiceImpl.encrypt is not configured for encryption in the OJB repository").toString());
            }
        }
    }

    public void createBackupTable(Class businessObjectClass) {
        dbPlatformDao.createBackupTable(getClassDescriptor(businessObjectClass).getFullTableName());
    }

    public void prepClassDescriptor(Class businessObjectClass, Set<String> attributeNames) {
        ClassDescriptor classDescriptor = getClassDescriptor(businessObjectClass);
        for (String attributeName : attributeNames) {
            classDescriptor.getFieldDescriptorByName(attributeName).setFieldConversionClassName(FieldConversionDefaultImpl.class.getName());
        }
    }

    public void truncateTable(Class businessObjectClass) {
        dbPlatformDao.truncateTable(getClassDescriptor(businessObjectClass).getFullTableName());
    }

    public void encrypt(PersistableBusinessObject businessObject, Set<String> attributeNames) {
        for (String attributeName : attributeNames) {
            try {
                PropertyUtils.setProperty(businessObject, attributeName, encryptionService.encrypt(PropertyUtils.getProperty(businessObject, attributeName)));
            }
            catch (Exception e) {
                throw new RuntimeException(new StringBuffer("PostDataLoadEncryptionServiceImpl caught exception while attempting to encrypt attribute ").append(attributeName).append(" of Class ").append(businessObject.getClass()).toString(), e);
            }
        }
        businessObjectService.save(businessObject);
    }

    public void restoreClassDescriptor(Class businessObjectClass, Set<String> attributeNames) {
        ClassDescriptor classDescriptor = getClassDescriptor(businessObjectClass);
        for (String attributeName : attributeNames) {
            classDescriptor.getFieldDescriptorByName(attributeName).setFieldConversionClassName(OjbKualiEncryptDecryptFieldConversion.class.getName());
        }
        businessObjectService.countMatching(businessObjectClass, new HashMap());
    }

    public void restoreTableFromBackup(Class businessObjectClass) {
        dbPlatformDao.restoreTableFromBackup(getClassDescriptor(businessObjectClass).getFullTableName());
    }

    public void dropBackupTable(Class businessObjectClass) {
        dbPlatformDao.dropBackupTable(getClassDescriptor(businessObjectClass).getFullTableName());
    }
    
    public void purgeDatabase() {
        dbPlatformDao.purgeTables();
        dbPlatformDao.purgeSequences();
        dbPlatformDao.purgeViews();
    }
    
    public void truncateSequenceTables() {
        for ( String sequenceName : dbPlatformDao.getSequenceNames() ) {
            LOG.info( "Purging sequence table: " + sequenceName );
            dbPlatformDao.clearSequenceTable( sequenceName );
        }
    }

    /**
     * @see org.kuali.core.service.DatabaseImportExportService#processSqlFile(java.lang.String)
     */
    public void processSqlFile(String fileName) {
        LOG.info("Processing sql file: " + fileName);
        String fileContents = readFile(fileName);
        String objectName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.indexOf("."));
        String ddl = null;
        do {
            int startIndex;
            int endIndex1;
            int endIndex2;
            startIndex = fileContents.indexOf("CREATE");
            if (startIndex == -1) {
                startIndex = fileContents.indexOf("ALTER");
            }
            if ((fileContents.indexOf("CREATE FUNCTION") > -1) || (fileContents.indexOf("CREATE PROCEDURE") > -1)) {
                // we need to handle these a little differently.
                endIndex1 = fileContents.lastIndexOf("//");
            }
            else {
                endIndex1 = fileContents.indexOf('/', startIndex);
                endIndex2 = fileContents.indexOf(';', startIndex);
                if ((endIndex1 == -1) || ((endIndex2 < endIndex1) && endIndex2 != -1)) {
                    endIndex1 = endIndex2;
                }
            }
            if ((startIndex != -1) && (endIndex1 != -1)) {
                ddl = fileContents.substring(startIndex, endIndex1);
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "DDL: \n" + ddl );
                }
                if (fileName.endsWith(TABLE_FILE_SUFFIX)) {
                    dbPlatformDao.createTable(ddl);
                }
                else if (fileName.endsWith(SEQUENCE_FILE_SUFFIX)) {
                    dbPlatformDao.createSequence(ddl);
                }
                else if (fileName.endsWith(INDEX_FILE_SUFFIX)) {
                    dbPlatformDao.createIndex(ddl);
                }
                else if (fileName.endsWith(VIEW_FILE_SUFFIX)) {
                    dbPlatformDao.createView(ddl);
                }
                else {
                    dbPlatformDao.executeSql(ddl);
                }
                if (fileContents.length() > endIndex1) {
                    fileContents = fileContents.substring(endIndex1 + 1);
                }
            }
            else {
                ddl = null;
            }
        } while (ddl != null);
    }
    
    /**
     * @see org.kuali.core.service.DatabaseImportExportService#processDumpFile(java.lang.String)
     */
    public void processDumpFile(String fileName) {
        LOG.info("Processing dump file " + fileName);
        String tableName = null;
        try {
            dbPlatformDao.setDefaultDateFormatToYYYYMMDD();
            ZipFile dumpZipFile = new ZipFile(fileName);
            Enumeration dumpZipFileEntries = dumpZipFile.entries();
            while (dumpZipFileEntries.hasMoreElements()) {
                ZipEntry dumpZipFileEntry = (ZipEntry)dumpZipFileEntries.nextElement();
                tableName = dumpZipFileEntry.getName().replace(DUMP_FILE_SUFFIX, "");
                // check if the "table" is really a sequence
                if ( dbPlatformDao.isSequence( tableName ) ) {
                    // if it is a sequence
                    BufferedReader dumpFileReader = new BufferedReader(new InputStreamReader(dumpZipFile.getInputStream(dumpZipFileEntry)));
                    char[]data = new char[BUFFER_SIZE];
                    int len = dumpFileReader.read(data);
                    StringBuffer temp = new StringBuffer();
                    temp.append( data, 0, len );
                    dbPlatformDao.setSequenceStart( tableName, new Long( temp.toString() ) );
                } else if ( dbPlatformDao.isTable( tableName ) ) {
                    LOG.info("Processing table: " + tableName);
                    dbPlatformDao.truncateTable(tableName);
                    BufferedReader dumpFileReader = new BufferedReader(new InputStreamReader(dumpZipFile.getInputStream(dumpZipFileEntry)));
                    StringBuffer temp = new StringBuffer();
                    String record;
                    StringBuffer recordInsertStatement = new StringBuffer();
                    Pattern pattern = Pattern.compile("\\D+");
                    Matcher matcher;
                    char[]data = new char[BUFFER_SIZE];
                    int cursorPosition;
                    boolean firstRecord = true;
                    StringBuffer fieldNameList = new StringBuffer( 1000 );
                    while ((cursorPosition = dumpFileReader.read(data)) != -1) {
                        temp.append(data, 0, cursorPosition);
                        while (temp.indexOf("\t\n") > -1) {
                            record = temp.substring(0, temp.indexOf("\t\n"));
                            temp.delete(0, temp.indexOf("\t\n")+2);
                            if ( firstRecord ) {
                            	String[] fields = record.split("\\t");
                            	for (int i=0; i<fields.length; i++) {
                            		if ( i > 0 ) {
                            			fieldNameList.append( ',' );
                            		}
                            		fieldNameList.append( fields[i] );
                            	}
                            	firstRecord = false;
                            	continue;
                            }
                            // at this point, the string record should contain a complete
                            // record.  now we can attempt to do something with it.
                            recordInsertStatement.delete(0, recordInsertStatement.length());
                            recordInsertStatement.append("INSERT INTO " ).append( tableName ).append( " ( " );
                            recordInsertStatement.append( fieldNameList );
                            recordInsertStatement.append( " ) VALUES (" );
                            String[] fields = record.split("\\t");
                            for (int i=0; i<fields.length; i++) {
                                if (fields[i].equalsIgnoreCase("\\N")) {
                                    recordInsertStatement.append("NULL,");
                                } else {
                                    matcher = pattern.matcher(fields[i]);
                                    if (matcher.find() || (fields[i].length() <= 8 && fields[i].startsWith("0"))) {
                                        recordInsertStatement.append("'" + dbPlatformDao.escapeSingleQuotes(fields[i].replace( "//~T~//", "\t" ).replace(  "\\", "\\\\" ) ) + "',");
                                    } else {
                                        recordInsertStatement.append(fields[i] + ",");
                                    }
                                }
                            }
                            // before we append the final parenthesis, we need to
                            // chop off the trailing comma.
                            recordInsertStatement.delete(recordInsertStatement.length()-1, recordInsertStatement.length()); 
                            recordInsertStatement.append(")");
                            if ( recordInsertStatement.length() <= 1000000 ) {
                            	dbPlatformDao.executeSql(recordInsertStatement.toString());
                            }
                        }
                    }
                    dumpFileReader.close();
                } else  {
                    LOG.warn( "Unknown database object type: " + tableName );
                }
            }
        }
        catch (IOException e) {
            LOG.error("Caught exception while processing table: " + tableName);
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.core.service.DatabaseImportExportService#exportData(java.lang.String)
     */
    public void exportData(String exportDirectory) {
        List<String> tableNames = dbPlatformDao.getTableNames();
        List<String> sequenceNames = dbPlatformDao.getSequenceNames();
        for (String tableName : tableNames ) {
            // in some databases, sequences will appear as tables - in this case, we don't want to dump their contents
            if ( !sequenceNames.contains( tableName ) ) {
                dbPlatformDao.dumpTable(tableName, exportDirectory);
            }
        }
        for ( String sequenceName : sequenceNames ) {
            dbPlatformDao.dumpSequence( sequenceName, exportDirectory );
        }
    }
    
    private String readFile(String fileName) {
        try {
            File file = new File(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuffer fileContents = new StringBuffer();
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                fileContents.append(temp);
            }
            bufferedReader.close();
            while (fileContents.toString().contains("/*") && fileContents.toString().contains("*/")) {
                fileContents.delete(fileContents.indexOf("/*"), fileContents.indexOf("*/") + 2);
            }
            return fileContents.toString().toUpperCase();
        }
        catch (FileNotFoundException e) {
            LOG.error("Unable to find file: " + fileName, e);
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            LOG.error("Unable to read file: " + fileName, e);
            throw new RuntimeException(e);
        }
    }
    
    public void updateWorkflowDocHandlerUrls() {
        String baseURL = configService.getPropertyString( RiceConstants.APPLICATION_URL_KEY );
        dbPlatformDao.executeSql( "UPDATE en_doc_typ_t SET doc_typ_hdlr_url_addr = REPLACE( doc_typ_hdlr_url_addr, " +
                "'http://localhost:8080/kuali-dev', ? ) " +
                "WHERE doc_typ_hdlr_url_addr LIKE 'http://localhost:8080/kuali-dev/%' " +
                "  AND doc_typ_nm NOT LIKE 'EDENSERVICE%'", new Object[] { baseURL } );
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the encryptionService attribute value.
     * 
     * @param encryptionService The encryptionService to set.
     */
    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    /**
     * Sets the dbPlatformDao attribute value.
     * @param dbPlatformDao The dbPlatformDao to set.
     */
    public void setDbPlatformDao(KualiDBPlatformDao dbPlatformDao) {
        this.dbPlatformDao = dbPlatformDao;
    }

    public KualiConfigurationService getConfigService() {
        return configService;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
}
