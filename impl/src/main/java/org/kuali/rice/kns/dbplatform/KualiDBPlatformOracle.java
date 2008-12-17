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
package org.kuali.rice.kns.dbplatform;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.rice.kns.dao.jdbc.KualiDBPlatformBase;

/**
 * @deprecated
 * @see org.kuali.rice.core.database.platform.OraclePlatform
 * This class is just for Oracle DB code - should be used only as last resort
 */
public class KualiDBPlatformOracle extends KualiDBPlatformBase implements KualiDBPlatform {
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

    /**
     * @see org.kuali.rice.core.database.platform.ANSISqlPlatform#getDateSQL(String, String)
     */
    //not copied over
    public String getDateFormatString(String dateFormatString) {
        return "'" + dateFormatString + "'";
    }
    
    /*
     * NOT copied to corresponding Oracle impl in *.core.database.platform.*;
     * nb: this doesn't return the time as would CURRENT_TIME or CURTIME
     */
    public String getCurTimeFunction() {
        return "sysdate";
    }
    
    /**
     * @see org.kuali.rice.core.database.platform.ANSISqlPlatform#getUpperCaseFunction()
     */
    public String getUpperCaseFunction() {
    	return "UPPER";
    }
}
