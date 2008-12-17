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
package org.kuali.rice.kns.dbplatform;

import javax.sql.DataSource;

import org.apache.ojb.broker.query.Criteria;

/*
 * chb: 03Dec2008
 * Active code from this file has been copied to org.kuali.rice.core.database.platform.Platform
 */
/**
 * @deprecated
 * @see org.kuali.rice.core.database.platform.Platform
 * This interface should represent the bare minimum SQLcalls needed to specifically handle differences between RDBMS
 */
public interface KualiDBPlatform {
    /* not copied to org.kuali.rice.core.database.platform.Platform
     * b/c it's not apparently called by anything
     */
    public String getCurTimeFunction();
    
    /* not copied to org.kuali.rice.core.database.platform.Platform
     * b/c it's not apparently called by anything
     */
    public String getStrToDateFunction();

	/* not copied to org.kuali.rice.core.database.platform.Platform
     * b/c it's not apparently called by anything
     */
    public String getDateFormatString(String dateFormatString);

    //copied
    public String getUpperCaseFunction();
    
    /*
     * MySQL impl of this method copied to org.kuali.rice.core.database.platform.MySQLPlatform
     * No other impl exists in this legacy package (Derby impl returned null)
     */
    public void applyLimit(Integer limit, Criteria criteria);
    
    //not copied to org.kuali.rice.core.database.platform.*
    /** 
     * @see org.kuali.rice.core.database.platform.Platform#getNextValSQL(String, javax.persistence.EntityManager)
     * @see org.kuali.rice.core.database.platform.Platform#getNextValSQL(String, org.apache.ojb.broker.PersistenceBroker)
     */
    public Long getNextAvailableSequenceNumber(String sequenceName);

    //copied to Platform and implemented in ANSISqlPlatform
    public String getCreateTableFromTableSql(String createTableName, String fromTableName);

    //copied
    public String getTruncateTableSql(String tableName);
    
    //copied copied to Platform interface and implemented in ANSISqlPlatform
    public String getInsertDataFromTableSql(String restoreTableName, String fromTableName);
    
    //copied to Platform interface and implemented in ANSISqlPlatform
    public String getDropTableSql(String tableName);

    //not copied to Platform interface -- this should not be done programmatically
    public void setDataSource(DataSource dataSource);
    
    //copied
    /**
     * Returns a SQL expression that acts like nvl(exprToTest, exprToReplaceIfTestExprNull) on oracle.  That is,
     * an expression that will return exprToTest does not evaluate to null, and will return exprToReplaceIfTestExprNull
     * if exprToTest does evaluate to null.  NOTE: this method does not provide any protection against SQL injection
     * attacks, nor does it validate any of the parameters.
     * 
     * @param exprToTest a SQL expression that will either evaluate to null or non-null
     * @param exprToReplaceIfTestExprNull the value to return if 
     * @return a SQL expression that acts like nvl on oracle or ifnull() on MySQL
     */
    public String getIsNullFunction(String exprToTest, String exprToReplaceIfTestExprNull);
}
