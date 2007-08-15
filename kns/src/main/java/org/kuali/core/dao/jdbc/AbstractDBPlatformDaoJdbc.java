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
package org.kuali.core.dao.jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.core.dao.KualiDBPlatformDao;
import org.kuali.core.dbplatform.KualiDBPlatform;
import org.kuali.core.service.DatabaseImportExportService;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public abstract class AbstractDBPlatformDaoJdbc extends JdbcDaoSupport implements KualiDBPlatform, KualiDBPlatformDao {
    private static final Logger LOG = Logger.getLogger(AbstractDBPlatformDaoJdbc.class);

    private DatabaseMetaData dbMetaData;
    protected Map<String,Integer> columnLengths = new HashMap<String,Integer>();
    private List<String> tableNames;
    private List<String> viewNames;
        
    protected abstract Integer getFetchSize();

    private DatabaseMetaData getDBMetaData() {
        if ( dbMetaData == null ) {
            try {
                dbMetaData = getJdbcTemplate().getDataSource().getConnection().getMetaData();
            } catch ( SQLException ex ) {
                LOG.fatal( "Unable to get database metadata", ex );
                throw new RuntimeException( "Unable to get database metadata", ex );
            }
        }
        return dbMetaData;
    }
    
    public List<String> getTableNames() {
        if ( tableNames == null ) {
            tableNames = new ArrayList<String>( 300 );
            String schemaName = getSchemaName();
            LOG.info( "Schema Name: " + schemaName );
            try {
                ResultSet rs = getDBMetaData().getTables( null, schemaName, null, new String[] { "TABLE" } );
                while ( rs.next() ) {
                    tableNames.add(rs.getString( "TABLE_NAME" ));
                }
                rs.close();
            } catch ( SQLException ex ) {
                tableNames = null;
                LOG.error( "Unable to get table list", ex );
                throw new RuntimeException( "Unable to get table list", ex );
            }
        }
        
        return tableNames;
    }
    
    public boolean isTable( String tableName ) {
        try {
            ResultSet rs = getDBMetaData().getTables( null, getSchemaName(), tableName, new String[] { "TABLE" } );
            if ( rs.next() ) {
                rs.close();
                return true;
            }
            rs.close();
            return false;
        } catch ( SQLException ex ) {
            tableNames = null;
            LOG.error( "Unable to check if name is a table", ex );
            throw new RuntimeException( "Unable to check if name is a table", ex );
        }
    }

    public abstract boolean isSequence( String sequenceName );

    public List<String> getViewNames() {
        if ( viewNames == null ) {
            viewNames = new ArrayList<String>( 50 );
            try {
                ResultSet rs = getDBMetaData().getTables( null, getSchemaName(), null, new String[] { "VIEW" } );
                while ( rs.next() ) {
                    viewNames.add( rs.getString( "TABLE_NAME" ) );
                }
                rs.close();
            } catch ( SQLException ex ) {
                viewNames = null;
                LOG.error( "Unable to get view list", ex );
                throw new RuntimeException( "Unable to get view list", ex );
            }
        }
        
        return viewNames;
    }
    
    public void executeSql(String sql) {
        if (LOG.isDebugEnabled()) LOG.debug("Executing sql: " + sql);
        getJdbcTemplate().execute(sql);
    }

    public void executeSql(String sql, Object[] params ) {
        if (LOG.isDebugEnabled()) {
            StringBuffer logStatement = new StringBuffer("Executing sql with parameters: ").append(sql);
            for ( Object param : params ) {
                logStatement.append("\n\t").append(param);
            }
            LOG.debug(logStatement);
        }
        getJdbcTemplate().update(sql, params);
    }

    public void purgeTables() {
        for (String tableName : getTableNames()) {
            LOG.info("Dropping table: " + tableName);
            dropTable(tableName);
        }
    }

    public void purgeSequences() {
        for (String sequenceName : getSequenceNames()) {
            LOG.info("Dropping sequence: " + sequenceName);
            dropSequence(sequenceName);
        }
    }

    public void purgeViews() {
        for (String viewName : getViewNames()) {
            LOG.info("Dropping view: " + viewName);
            dropView(viewName);
        }
    }
    
    public void createBackupTable(String tableName) {
        createTable(new StringBuffer("create table ").append(tableName).append("_bak").append(" as select * from ").append(tableName).toString());
    }

    public void truncateTable(String tableName) {
        if ( isTable( tableName ) ) {
            executeSql("truncate table " + tableName);
        }
    }

    public void restoreTableFromBackup(String tableName) {
        truncateTable(tableName);
        executeSql(new StringBuffer("insert into ").append(tableName).append(" select * from ").append(tableName).append("_bak").toString());
    }

    public void dropBackupTable(String tableName) {
        dropTable(tableName + "_bak");
    }
    
    public void dumpTable(String tableName, String exportDirectory) {
        PreparedStatement dumpTableStatement = null;
        String fileName = new StringBuffer(exportDirectory).append(File.separator).append(tableName).append(DatabaseImportExportService.DUMP_FILE_SUFFIX).toString();
        try {
            Connection con = getJdbcTemplate().getDataSource().getConnection();
            dumpTableStatement = con.prepareStatement("SELECT * FROM " + tableName, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if (getFetchSize() != null) {
                dumpTableStatement.setFetchSize(getFetchSize());
            }
            if (LOG.isInfoEnabled()) LOG.info("Exporting data from table: " + tableName);
            ResultSet rows = dumpTableStatement.executeQuery();
            ResultSetMetaData rowMetaData = rows.getMetaData();
            if (rows.next()) {
                PrintWriter dumpFileWriter = new PrintWriter(new FileOutputStream(new File(fileName)));
                for (int i = 1; i <= rowMetaData.getColumnCount(); i++) {
                	dumpFileWriter.print( rowMetaData.getColumnName( i ) );
                	dumpFileWriter.print( FIELD_DELIMITER );
                }
                dumpFileWriter.print(LINE_DELIMITER);
                do {
                    for (int i = 1; i <= rowMetaData.getColumnCount(); i++) {
                        Object columnValue = rows.getObject(i);
                        int columnType = rowMetaData.getColumnType( i );
                        if ( columnType == java.sql.Types.CLOB && columnValue != null ) {
                            java.sql.Clob clob = (java.sql.Clob)columnValue;
                            Reader r = clob.getCharacterStream();
                            columnValue = new StringBuffer();
                            char[] buffer = new char[2000];
                            try {
                                int len;
                                while ( (len = r.read( buffer )) != -1 ) {
                                    ((StringBuffer)columnValue).append( buffer, 0, len );
                                }
                            } catch ( IOException ex ) {
                                LOG.error( "IO exception processing CLOB", ex );
                            }
                        }
                        if (columnValue == null) {
                            dumpFileWriter.print("\\N");
                        }
                        else {
                        	String stringColVal = columnValue.toString();
                            dumpFileWriter.print( stringColVal.replace( "\t", "//~T~//" ) );
                        }
                        dumpFileWriter.print(FIELD_DELIMITER);
                    }
                    dumpFileWriter.print(LINE_DELIMITER);
                } while (rows.next());
                dumpFileWriter.close();
            }
            rows.close();
            dumpTableStatement.close();
            con.close();
        }
        catch (FileNotFoundException e) {
            LOG.error("Unable to create dump file: " + fileName);
            throw new RuntimeException(e);
        }
        catch (SQLException e) {
            LOG.error("Unable to dump table: " + tableName);
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (dumpTableStatement != null) {
                    dumpTableStatement.close();
                }
            }
            catch (SQLException e) {
                LOG.error("Unable to close statement: " + tableName);
                throw new RuntimeException(e);
            }
        }
    }

    // We need a way to determine the name of the database that we're connected
    // to (for MySQL at least) - may need this for other dbs, too.
    private String schemaName;
    protected String getSchemaName() {
        if ( schemaName == null ) {
            try {
                schemaName = getJdbcTemplate().getDataSource().getConnection().getMetaData().getUserName().split( "@" )[0];
            } catch (SQLException e) {
                // do nothing
            }
        }
        return schemaName;
    }
    
    public int getColumnLength( String tableName, String columnName ) {
        String mapKey = tableName+"/"+columnName;
        // attempt to pull the information from the cached map
        Integer length = columnLengths.get( mapKey );
        if ( length != null ) {
            return length;
        }
        // otherwise, pull from database
        try {
            ResultSet rs = getDBMetaData().getColumns( null, getSchemaName(), tableName, columnName );
            if ( rs.next() ) {
                int len = rs.getInt( "COLUMN_SIZE" );
                columnLengths.put( mapKey, len );
                rs.close();
                return len;
            }
            rs.close();
        } catch ( SQLException ex ) {
            LOG.error( "Unable to retrieve column size for " + mapKey, ex );
        }
        return 0;
    }    
    public void clearSequenceTable(String sequenceName) {
        // do nothing unless a DB implementation requires it
    }

    public String escapeBackslashes( String value ) {
        return value;
    }
}
