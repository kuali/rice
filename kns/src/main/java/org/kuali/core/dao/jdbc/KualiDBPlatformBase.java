/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.core.dbplatform.KualiDBPlatform;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * This class is responsible for low level DB operations that are currently commonly implementable for the DB platforms Kuali
 * supports.
 */
public abstract class KualiDBPlatformBase extends SimpleJdbcDaoSupport implements KualiDBPlatform {
    public String getCreateTableFromTableSql(String createTableName, String fromTableName) {
	return new StringBuffer("create table ").append(createTableName).append(" as select * from ").append(fromTableName)
		.toString();
    }

    public String getTruncateTableSql(String tableName) {
	return "truncate table " + tableName;
    }

    public String getInsertDataFromTableSql(String restoreTableName, String fromTableName) {
	return new StringBuffer("insert into ").append(restoreTableName).append(" select * from ").append(fromTableName)
		.toString();
    }

    public String getDropTableSql(String tableName) {
	return new StringBuffer("drop table ").append(tableName).toString();
    }
}
