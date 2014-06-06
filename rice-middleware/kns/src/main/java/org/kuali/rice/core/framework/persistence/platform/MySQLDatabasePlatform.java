/**
 * Copyright 2005-2014 The Kuali Foundation
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
import org.apache.ojb.broker.accesslayer.LookupException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySQLDatabasePlatform extends ANSISqlDatabasePlatform {

	private static final Pattern APOS_PAT = Pattern.compile("'");
	private static final Pattern BSLASH_PAT = Pattern.compile(Matcher.quoteReplacement("\\"));
	
    public String getLockRouteHeaderQuerySQL(String documentId, boolean wait) {
        return "SELECT DOC_HDR_ID FROM KREW_DOC_HDR_T WHERE DOC_HDR_ID=? FOR UPDATE";
    }

    public String getStrToDateFunction() {
        return "STR_TO_DATE";
    }

    public String getCurTimeFunction() {
        return "NOW()";
    }

    @Override
    public String applyLimitSql(Integer limit) {
        if (limit != null) {
            return " 1 LIMIT 0," + limit.intValue(); // 1 has to be there because the criteria is ANDed
        }
        return null;
    }

    /**
     * Generate next id value for the logical sequence given the JDBC Connection
     * @param sequenceName the logical sequence name
     * @param connection JDBC Connection to use (without closing)
     * @return next id in sequence or RuntimeException on error
     */
    @Override
    protected Long getNextValSqlJdbc(String sequenceName, Connection connection) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("INSERT INTO " + sequenceName + " VALUES (NULL);");
            statement.executeUpdate();
            statement = connection.prepareStatement("SELECT LAST_INSERT_ID()");
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new RuntimeException("Error retrieving next option id for action list from sequence.");
            }
            return new Long(resultSet.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving next option id for action list from sequence.", e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    protected Long getNextValSqlOjb(String sequenceName, PersistenceBroker persistenceBroker) {
  		try {
  			Connection connection = persistenceBroker.serviceConnectionManager().getConnection();
            return getNextValSqlJdbc(sequenceName, connection);
  		} catch (LookupException e) {
  			throw new RuntimeException("Error retrieving next option id for action list from sequence.", e);
  		}
  	}

    public boolean isSITCacheSupported() {
    	return false;
    }

    public String toString() {
        return "[MySQLDatabasePlatform]";
    }
    
    public String getSelectForUpdateSuffix(long waitMillis) {
        return "for update";
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

    /**
     * Performs MySQL-specific escaping of String parameters.
     * 
     * @see DatabasePlatform#escapeString(java.lang.String)
     */
    public String escapeString(String sqlString) {
    	return (sqlString != null) ? BSLASH_PAT.matcher(APOS_PAT.matcher(sqlString).replaceAll("''")).replaceAll(Matcher.quoteReplacement("\\\\")) : null;
    } 
}
