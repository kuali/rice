/**
 * Copyright 2005-2017 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.framework.persistence.platform;

import org.apache.ojb.broker.PersistenceBroker;

import java.sql.Connection;
import java.util.regex.Pattern;

/**
 * DatabasePlatform implementation that generates Derby-compliant SQL
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DerbyDatabasePlatform extends ANSISqlDatabasePlatform {

	private static final Pattern APOS_PAT = Pattern.compile("'");
	
    public String getLockRouteHeaderQuerySQL(String documentId, boolean wait) {
        return "SELECT DOC_HDR_ID FROM KREW_DOC_HDR_T WHERE DOC_HDR_ID=?";
    }

    private static long nextVal = 1000;
    
	public String getCurTimeFunction() {
		return "CURRENT_TIMESTAMP";
	}

	public String getDateFormatString(String dateFormatString) {
		return "'" + dateFormatString + "'";
	}
    
	public String getStrToDateFunction() {
		return null;
	}

    @Override
    protected Long getNextValSqlOjb(String sequenceName, PersistenceBroker persistenceBroker) {
        return nextVal++;
    }

    @Override
    protected Long getNextValSqlJdbc(String sequenceName, Connection connection) {
        return nextVal++;
    }

    public String toString() {
        return "[Derby]";
    }

    public String getSelectForUpdateSuffix(long waitMillis) {
    	throw new UnsupportedOperationException("Implement me!");
    }

    /**
     * Performs Derby-specific escaping of String parameters.
     * 
     * @see DatabasePlatform#escapeString(java.lang.String)
     */
    public String escapeString(String sqlString) {
    	return (sqlString != null) ? APOS_PAT.matcher(sqlString).replaceAll("''") : null;
    }

    @Override
    public String applyLimitSql(Integer limit) {
        // derby has no such concept
        return null;
    }

    @Override
    public String getValidationQuery() {
        return "values 1";
    }

}
