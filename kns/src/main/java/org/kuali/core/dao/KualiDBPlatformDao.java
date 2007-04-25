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
package org.kuali.core.dao;

import java.util.List;

public interface KualiDBPlatformDao {
    public Long getNextAvailableSequenceNumber(String sequenceName);

    public void executeSql(String sql);

    public void executeSql(String sql, Object[] params );
    
    public void createTable(String ddl);

    public void dropTable(String tableName);

    public void purgeTables();

    public void createSequence(String ddl);

    public void dropSequence(String sequenceName);

    public void purgeSequences();

    public void createIndex(String ddl);

    public void createView(String ddl);

    public void dropView(String viewName);

    public void purgeViews();

    public void createBackupTable(String tableName);

    public void truncateTable(String tableName);

    public void restoreTableFromBackup(String tableName);

    public void dropBackupTable(String tableName);

    public List<String> getTableNames();

    public void dumpTable(String tableName, String exportDirectory);
    
    public void setDefaultDateFormatToYYYYMMDD();
    
    public String escapeSingleQuotes( String value );
    
    public List<String> getSequenceNames();
    
    public void dumpSequence( String sequenceName, String exportDirectory );
    
    public void setSequenceStart( String sequenceName, Long value );
    
    public boolean isTable( String tableName );
    
    public boolean isSequence( String sequenceName );
    
    public void clearSequenceTable( String sequenceName );
}
