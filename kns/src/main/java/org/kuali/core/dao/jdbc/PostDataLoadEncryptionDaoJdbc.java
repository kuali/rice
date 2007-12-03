/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.core.dao.jdbc;

import org.apache.log4j.Logger;
import org.kuali.core.dao.PostDataLoadEncryptionDao;

public class PostDataLoadEncryptionDaoJdbc extends PlatformAwareDaoBaseJdbc implements PostDataLoadEncryptionDao {
    private static final Logger LOG = Logger.getLogger(PostDataLoadEncryptionDaoJdbc.class);

    private void executeSql(String sql) {
	LOG.info("Executing sql: " + sql);
	getJdbcTemplate().execute(sql);
    }

    public void createBackupTable(String tableName) {
	executeSql(getDbPlatform().getCreateTableFromTableSql(tableName + "_bak", tableName));
    }

    public void truncateTable(String tableName) {
	executeSql(getDbPlatform().getTruncateTableSql(tableName));
    }

    public void restoreTableFromBackup(String tableName) {
	truncateTable(tableName);
	executeSql(getDbPlatform().getInsertDataFromTableSql(tableName, tableName + "_bak"));
    }

    public void dropBackupTable(String tableName) {
	executeSql(getDbPlatform().getDropTableSql(tableName + "_bak"));
    }
}
