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

import org.apache.ojb.broker.query.Criteria;
import org.kuali.core.dao.jdbc.KualiDBPlatformBase;

/**
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

    public String getDateFormatString(String dateFormatString) {
        return "'" + dateFormatString + "'";
    }

    public String getCurTimeFunction() {
        return "sysdate";
    }
    
    public String getUpperCaseFunction() {
    	return "UPPER";
    }
}
