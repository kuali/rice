/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.useroptions.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.useroptions.dao.ReloadActionListDAO;

/**
 * Implementation of ReloadActionListDAO.  This needs to be straight JDBC since it ignores SQLException in one case.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ReloadActionListDaoJdbcImpl implements ReloadActionListDAO {
	
	private static final String RELOAD_ACTION_LIST = "RELOAD_ACTION_LIST";

	private static final Logger LOG = Logger.getLogger(ReloadActionListDaoJdbcImpl.class);
	
	DataSource nonTxDataSource;
	
	/**
	 * This constructs a ReloadActionListDaoJdbcImpl
	 * 
	 */
	public ReloadActionListDaoJdbcImpl(DataSource nonTxDataSource) {
		this.nonTxDataSource = nonTxDataSource; 
	}

	/**
	 * @see org.kuali.rice.kew.useroptions.dao.ReloadActionListDAO#setReloadActionListFlag(java.lang.String)
	 */
	@Override
	public void setReloadActionListFlag(String userId) {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		try {
			conn = nonTxDataSource.getConnection();
			
			preparedStatement = conn.prepareStatement("insert into KREW_USR_OPTN_T (PRSN_OPTN_ID, PRNCPL_ID, VAL, VER_NBR) VALUES (?, ?, ?, ?)");
			preparedStatement.setString(1, RELOAD_ACTION_LIST);
			preparedStatement.setString(2, userId);
			preparedStatement.setString(3, "true");
			preparedStatement.setInt(4, 1);
			int result = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			// this is normal if the preference is already set.
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					LOG.error("couldn't close PreparedStatement", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.error("couldn't close Connection", e);
				}
			}
		}
	}

	
	/**
	 * @see org.kuali.rice.kew.useroptions.dao.ReloadActionListDAO#checkAndResetReloadActionListFlag(java.lang.String)
	 */
	@Override
	public boolean checkAndResetReloadActionListFlag(String userId) {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		int updatedCount = 0;
		
		try {
			conn = nonTxDataSource.getConnection();
			
			preparedStatement = conn.prepareStatement("delete from KREW_USR_OPTN_T where PRSN_OPTN_ID = ? and PRNCPL_ID = ?");
			preparedStatement.setString(1, RELOAD_ACTION_LIST);
			preparedStatement.setString(2, userId);
			updatedCount = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			LOG.error("unable to delete RELOAD_ACTION_LIST preference", e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					LOG.error("couldn't close PreparedStatement", e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					LOG.error("couldn't close Connection", e);
				}
			}
		}
		
		return updatedCount == 1;
	}
	
	
}
