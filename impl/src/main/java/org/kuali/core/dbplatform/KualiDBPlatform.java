/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.dbplatform;

import javax.sql.DataSource;

import org.apache.ojb.broker.query.Criteria;

/**
 * This interface should represent the bare minimum SQLcalls needed to specifically handle differences between RDBMS
 */
public interface KualiDBPlatform {
    public String getCurTimeFunction();

    public String getStrToDateFunction();

    public String getDateFormatString(String dateFormatString);

    public String getUpperCaseFunction();

    public void applyLimit(Integer limit, Criteria criteria);

    public Long getNextAvailableSequenceNumber(String sequenceName);

    public String getCreateTableFromTableSql(String createTableName, String fromTableName);

    public String getTruncateTableSql(String tableName);

    public String getInsertDataFromTableSql(String restoreTableName, String fromTableName);

    public String getDropTableSql(String tableName);

    public void setDataSource(DataSource dataSource);
    
    /**
     * Returns a SQL expression that acts like nvl(exprToTest, exprToReplaceIfTestExprNull) on oracle.  That is,
     * an expression that will return exprToTest does not evalute to null, and will return exprToReplaceIfTestExprNull
     * if exprToTest does evaluate to null.  NOTE: this method does not provide any protection against SQL injection
     * attacks, nor does it validate any of the parameters.
     * 
     * @param exprToTest a SQL expression that will either evalutate to null or non-null
     * @param exprToReplaceIfTestExprNull the value to return if 
     * @return a SQL expression that acts like nvl on oracle or ifnull() on MySQL
     */
    public String getIsNullFunction(String exprToTest, String exprToReplaceIfTestExprNull);
}
