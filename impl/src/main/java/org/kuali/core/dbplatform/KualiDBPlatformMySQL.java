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
 * This class is just for MySQL DB code - should be used only as last resort
 */
public class KualiDBPlatformMySQL extends KualiDBPlatformBase implements KualiDBPlatform {
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

    public String getUpperCaseFunction() {
    	return "UPPER";
    }
    
    public String getDateFormatString(String dateFormatString) {
        String newString = "";
        if ("yyyy-mm-dd".equalsIgnoreCase(dateFormatString)) {
            newString = "'%Y-%m-%d'";
        }
        else if ("DD/MM/YYYY HH12:MI:SS PM".equalsIgnoreCase(dateFormatString)) {
            newString = "'%d/%m/%Y %r'";
        }
        return newString;
    }

    public String getCurTimeFunction() {
        return "NOW()";
    }
            }
    
