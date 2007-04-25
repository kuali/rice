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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.dao.jdbc.AbstractDBPlatformDaoJdbc;

/**
 * This class is just for MySQL DB code - should be used only as last resort
 */
public class KualiDBPlatformMySQL extends AbstractDBPlatformDaoJdbc {
    private static final Logger LOG = Logger.getLogger(KualiDBPlatformMySQL.class);
    private static int MAX_CHARACTERS_TO_INDEX = 250;

    public void applyLimit(Integer limit, Criteria criteria) {
        if (limit != null) {
            criteria.addSql(" 1 LIMIT 0," + limit.intValue()); // 1 has to be there because the criteria is ANDed
        }
    }
    
    public Long getNextAvailableSequenceNumber(String sequenceName) {
        getJdbcTemplate().execute(new StringBuffer("INSERT INTO ").append(sequenceName).append(" VALUES (NULL)").toString());
        return getJdbcTemplate().queryForLong("SELECT LAST_INSERT_ID()");
    }

    public String getStrToDateFunction() {
        return "STR_TO_DATE";
    }

    public String getDateFormatString(String dateFormatString) {
        String newString = "";
        if ("yyyy-mm-dd".equalsIgnoreCase(dateFormatString)) {
            newString = "'%Y-%m-%d'";
        }
        else if ("DD/MM/YYYY HH12:MI:SS PM".equalsIgnoreCase(dateFormatString)) {
            newString = "'%d/%m/%Y HH:MM:SS'";
        }
        return newString;
    }

    public String getCurTimeFunction() {
        return "NOW()";
    }
    
    public void createSequence(String ddl) {
        String sequenceName = null;
        String[] tokens = ddl.split(" ");
        StringBuffer convertedDdl = new StringBuffer("CREATE TABLE ");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].endsWith("_SEQ") || tokens[i].startsWith("SEQ_") || tokens[i].endsWith("_SEQUENCE") || tokens[i].startsWith("SEQUENCE_")) {
                sequenceName = tokens[i];
                executeSql(new StringBuffer("CREATE TABLE ").append(sequenceName).append(" ( id bigint(19) not null auto_increment, primary key (id) ) ENGINE MyISAM").toString());
            }
            if (tokens[i].equals("WITH")) {
                executeSql(new StringBuffer("ALTER TABLE ").append(sequenceName).append(" auto_increment=").append(tokens[i+1]).toString());
            }
        }
    }
    
    public void dropSequence(String sequenceName) {
        executeSql(new StringBuffer("DROP TABLE ").append(sequenceName).toString());
    }

    public void createView(String ddl) {
        String[] tokens = ddl.split("\\b");
        // loop through once and make the required changes.
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("TRUNC")) {
                tokens[i] = "DATE";
                tokens[i + 2] = "SYSDATE()";
                tokens[i - 2] = "DATE(" + tokens[i - 2] + ")";
            }
        }
        // and reassemble into the full string
        StringBuffer newDDL = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            newDDL.append(tokens[i]);
        }
        executeSql(newDDL.toString().replace("/", "").replace(";", ""));
    }

    public void dropView(String viewName) {
        executeSql("DROP VIEW " + viewName);
    }

    public void createIndex(String ddl) {
        int parseStartIndex = ddl.lastIndexOf(" ON ") + 4;
        StringBuffer newDDL = new StringBuffer(ddl.substring(ddl.indexOf("CREATE"), parseStartIndex));
        String tableName = ddl.substring(parseStartIndex, ddl.indexOf("(")).trim();
        newDDL.append(tableName).append("(");
        String[] columnNames = ddl.substring(ddl.indexOf("(") + 1, ddl.indexOf(")")).replace(" ", "").split(",");
        for (int i = 0; i < columnNames.length; i++) {
            newDDL.append(columnNames[i]);
            if (getColumnLength( tableName, columnNames[i] ) > MAX_CHARACTERS_TO_INDEX) {
                newDDL.append("(").append(MAX_CHARACTERS_TO_INDEX).append(")");
            }
            if (i != (columnNames.length - 1)) {
                newDDL.append(",");
            }
        }
        executeSql(newDDL.append(")").toString());
    }


    /**
     * 
     * This method converts an oracle table definition to mysql. The following things need to be converted (that we know of so far)
     * varchar2 -> varchar number -> numeric sys_guid() -> '' constraint (\w+) not null (enable) --> $1 not null using index
     * tablespace .* -> tablespace xxx -> userenv('sessionid') -> 0 date -> datetime (oracle's date type is really a datetime) clob ->
     * mediumtext remove double-quotes around table/column names
     * 
     * @param ddl the input DDL
     * @return a string containing the converted DDL
     */
    public void createTable(String ddl) {
        String newDDL = ddl.replaceAll("\\bVARCHAR2", " VARCHAR");
        newDDL = newDDL.replaceAll("\\bNUMBER", " NUMERIC");
        newDDL = newDDL.replaceAll("\\bSYS_GUID\\(\\)", "''");
        newDDL = newDDL.replaceAll("CONSTRAINT \\w+? NOT NULL ENABLE", "NOT NULL");
        newDDL = newDDL.replaceAll("CONSTRAINT \\w+? NOT NULL", "NOT NULL");
        newDDL = newDDL.replaceAll("NOT NULL ENABLE", "NOT NULL");
        newDDL = newDDL.replaceAll("USING INDEX TABLESPACE \\w+\\b", "");
        newDDL = newDDL.replaceAll("USING INDEX", "");
        newDDL = newDDL.replaceAll("TABLESPACE \\w+\\b", "");
        newDDL = newDDL.replaceAll("\\bDATE", " DATETIME");
        newDDL = newDDL.replaceAll("\\bCLOB", " MEDIUMTEXT");
        newDDL = newDDL.replaceAll("\\bUSERENV\\('SESSIONID'\\)", " 0");
        newDDL = newDDL.replaceAll("\"", "");
        newDDL = newDDL.replaceAll("/", "");
        newDDL = newDDL.replaceAll(";", " ");
        newDDL = newDDL + " ENGINE InnoDB CHARACTER SET utf8 COLLATE utf8_bin ";
        executeSql(newDDL);
    }
    
    public void dropTable(String tableName) {
        executeSql("DROP TABLE " + tableName);
    }

    protected Integer getFetchSize() {
        // We need this to force MySQL to stream the results 1 row at a time,
        // otherwise we get errors on large tables due to the fact that the JDBC
        // driver tries to load the entire result set into memory.
        return Integer.MIN_VALUE;
    }
    
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList();
        for (String tableName : super.getTableNames()) {
            if (!isSequence(tableName)) {
                tableNames.add(tableName);
            }
        }
        return tableNames;
    }

    public List<String> getSequenceNames() {
        List<String> sequenceNames = new ArrayList();
        for (String tableName: super.getTableNames()) {
            if (isSequence(tableName)) {
                sequenceNames.add(tableName);
            }
        }
        return sequenceNames;
    }
    
    public void setDefaultDateFormatToYYYYMMDD() {
        // do nothing
    }
    
    public String escapeSingleQuotes( String value ) {
        return value.replaceAll( "'", "\\\\'" );
    }

    public void dumpSequence(String sequenceName, String exportDirectory) {
        // do nothing, sequences are dumped as tables        
    }
    
    public void setSequenceStart( String sequenceName, Long value ) {
        LOG.info( "Setting MySQL 'sequence': " + "ALTER TABLE "+sequenceName+" auto_increment="+value );
        executeSql("ALTER TABLE "+sequenceName+" auto_increment="+value);
    }
    
    public boolean isSequence( String sequenceName ) {
        return sequenceName.toUpperCase().startsWith( "SEQ_" ) || sequenceName.toUpperCase().startsWith( "SEQUENCE_" ) || sequenceName.toUpperCase().endsWith( "_SEQ" ) || sequenceName.toUpperCase().endsWith( "_SEQUENCE" );
    }
    
    // We need a way to determine the name of the database that we're connected
    // to (for MySQL at least) - may need this for other dbs, too.
    protected String getSchemaName() {
        try {
            return getJdbcTemplate().getDataSource().getConnection().getCatalog();
        } catch (SQLException e) {
            return "";
        }
    }

    public void clearSequenceTable(String sequenceName) {
        truncateTable( sequenceName );        
    }
    
}
