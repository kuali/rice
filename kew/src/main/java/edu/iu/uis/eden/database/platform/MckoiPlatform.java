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

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Platform implementation that generates Mckoi-compliant SQL
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MckoiPlatform extends ANSISqlPlatform {

    public String getLockRouteHeaderQuerySQL(Long routeHeaderId, boolean wait) {
        return "SELECT DOC_HDR_ID FROM EN_DOC_HDR_T WHERE DOC_HDR_ID=?";
    }

    public Long getNextValSQL(String sequenceName,	PersistenceBroker persistenceBroker) {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = persistenceBroker.serviceConnectionManager().getConnection();
			statement = connection.prepareStatement("select * NEXTVAL('" + sequenceName + "');");
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

    public String toString() {
        return "[MckoiPlatform]";
    }
}