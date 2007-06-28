/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.dbplatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.dao.jdbc.AbstractDBPlatformDaoJdbc;
import org.kuali.core.service.DatabaseImportExportService;

/**
 * This class is just for Oracle DB code - should be used only as last resort
 */
public class KualiDBPlatformOracle extends AbstractDBPlatformDaoJdbc {
    private static final Logger LOG = Logger.getLogger(KualiDBPlatformOracle.class);

    public void applyLimit(Integer limit, Criteria criteria) {
        if (limit != null) {
            criteria.addSql("rownum <= " + limit.intValue());
        }
    }

    public Long getNextAvailableSequenceNumber(String sequenceName) {
        return getJdbcTemplate().queryForLong(new StringBuffer("select ").append(sequenceName).append(".nextval").append(" from dual").toString());
    }
    
    public String getStrToDateFunction() {
        return "TO_DATE";
    }

    public String getDateFormatString(String dateFormatString) {
        return "'" + dateFormatString + "'";
    }

    public String getCurTimeFunction() {
        return "sysdate";
    }
    
    public String getUpperCaseFunction() {
    	return "UPPER";
    }
    
    public void createTable(String ddl) {
        executeSql(ddl);
    }
    
    public void dropTable(String tableName) {
        getJdbcTemplate().execute( "alter session set recyclebin=off" );
        executeSql("DROP TABLE " + tableName);
    }
    
    public void createSequence(String ddl) {
        executeSql(ddl);
    }

    public void dropSequence(String sequenceName) {
        executeSql("DROP SEQUENCE " + sequenceName);
    }
    
    public void createView(String ddl) {
        executeSql(ddl);
    }

    public void dropView(String viewName) {
        executeSql("DROP VIEW " + viewName);
    }
    
    public void createIndex(String ddl) {
        executeSql(ddl);
    }

    protected Integer getFetchSize() {
        return null;
    }

    public List<String> getSequenceNames() {
        return getJdbcTemplate().queryForList("SELECT SEQUENCE_NAME FROM ALL_SEQUENCES WHERE SEQUENCE_OWNER = ?", new Object[] { getSchemaName() }, String.class);
    }

    public boolean isSequence( String sequenceName ) {
        return getJdbcTemplate().queryForInt( "SELECT COUNT(*) FROM ALL_SEQUENCES WHERE SEQUENCE_OWNER = ? AND sequence_name = ?", new Object[] { getSchemaName(), sequenceName } ) > 0;
    }

    public void setDefaultDateFormatToYYYYMMDD() {
        getJdbcTemplate().execute( "alter session set NLS_DATE_FORMAT='YYYY-MM-DD'" );
    }
    
    public String escapeSingleQuotes( String value ) {
        return value.replaceAll( "'", "''" );
    }

    public void dumpSequence(String sequenceName, String exportDirectory) {
        String fileName = exportDirectory + File.separator + sequenceName + DatabaseImportExportService.DUMP_FILE_SUFFIX;
        try {
            if (LOG.isDebugEnabled()) LOG.debug("Exporting next value from sequenceName: " + sequenceName);
            Long nextVal = getJdbcTemplate().queryForLong( "SELECT "+sequenceName+".NEXTVAL FROM dual" );
            PrintWriter dumpFileWriter = new PrintWriter(new FileOutputStream(new File(fileName)));
            dumpFileWriter.print( nextVal );
            dumpFileWriter.close();
        }
        catch (FileNotFoundException e) {
            LOG.error("Unable to create dump file: " + fileName);
            throw new RuntimeException(e);
        }
    }
    
    public void setSequenceStart( String sequenceName, Long value ) {
        Map<String,Object> sequenceInfo = getJdbcTemplate().queryForMap( "select min_value, max_value, increment_by, cache_size from user_sequences where sequence_name = ?", new Object[] { sequenceName.toUpperCase() } );
        
        getJdbcTemplate().execute( "DROP SEQUENCE " + sequenceName );
        String sequenceDDL = "CREATE SEQUENCE " + sequenceName 
                + " INCREMENT BY " + sequenceInfo.get( "INCREMENT_BY" )
                + " START WITH " + value
                + (sequenceInfo.get( "MAX_VALUE" )!=null?" MAXVALUE " + sequenceInfo.get( "MAX_VALUE" ):"")
                + (sequenceInfo.get( "MIN_VALUE" )!=null?" MINVALUE " + sequenceInfo.get( "MIN_VALUE" ):"")
                + (sequenceInfo.get( "CACHE" )!=null?" CACHE " + sequenceInfo.get( "CACHE" ):" NOCACHE ");
        LOG.info( "Updating oracle sequence: " + sequenceDDL );
        getJdbcTemplate().execute( sequenceDDL );
    }
    
}
