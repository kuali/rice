/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.database.platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Platform implementation that generates Oracle-compliant SQL
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class OraclePlatform extends ANSISqlPlatform {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OraclePlatform.class);
	private static final long DEFAULT_TIMEOUT_SECONDS = 60 * 60; // default to 1 hour

	public Long getNextValSQL(String sequenceName,	PersistenceBroker persistenceBroker) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = persistenceBroker.serviceConnectionManager().getConnection();
			statement = connection.prepareStatement("select " + sequenceName + ".nextval from dual");
			resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new WorkflowRuntimeException("Error retrieving next option id for action list from sequence.");
			}
			return new Long(resultSet.getLong(1));
		} catch (SQLException e) {
			throw new WorkflowRuntimeException("Error retrieving next option id for action list from sequence.", e);
		} catch (LookupException e) {
			throw new WorkflowRuntimeException("Error retrieving next option id for action list from sequence.", e);
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

    public String getLockRouteHeaderQuerySQL(Long routeHeaderId, boolean wait) {
    	long timeoutSeconds = getTimeoutSeconds();
    	String waitClause = "";
    	if (!wait) {
    		waitClause = " NOWAIT";
    	} else if (wait && timeoutSeconds > 0) {
    		waitClause = " WAIT " + timeoutSeconds;
    	}
        return "SELECT DOC_HDR_ID FROM EN_DOC_HDR_T WHERE DOC_HDR_ID=? FOR UPDATE" + waitClause;
    }

    public String toString() {
        return "[OraclePlatform]";
    }

    protected long getTimeoutSeconds() {
    	String timeoutValue = Core.getCurrentContextConfig().getDocumentLockTimeout();
    	if (timeoutValue != null) {
    		try {
    			return Long.parseLong(timeoutValue);
    		} catch (NumberFormatException e) {
    			LOG.warn("Failed to parse document lock timeout as it was not a valid number: " + timeoutValue);
    		}
    	}
    	return DEFAULT_TIMEOUT_SECONDS;
    }
}