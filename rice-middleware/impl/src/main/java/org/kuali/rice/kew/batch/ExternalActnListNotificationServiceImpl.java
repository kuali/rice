/*
 * Copyright 2006-2015 The Kuali Foundation
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
package org.kuali.rice.kew.batch;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.service.KEWServiceLocator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @see ExternalActnListNotificationService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExternalActnListNotificationServiceImpl implements ExternalActnListNotificationService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ExternalActnListNotificationServiceImpl.class);

    private String password;
    private String url;
    private String username;

    /**
     * Specifies the default polling interval that should be used with this task.
     */
    private int externalActnListNotificationPollIntervalSeconds = 15;

    /**
     * Specifies the initial delay the poller should wait before starting to poll
     */
    private int externalActnListNotificationInitialDelaySeconds = 30;


    public void run() {
        LOG.info("checking for action items that have changed.");

        Statement statement = null;
        ResultSet rs = null;

        try {
            Connection connection = getConnection();
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = statement.executeQuery("SELECT ACTN_TYP, ACTN_ITM_ID FROM KREW_ACTN_ITM_CHANGED_T");

            while (rs.next()) {
                String actionType = rs.getString("ACTN_TYP");
                String actionItemId = rs.getString("ACTN_ITM_ID");

                boolean success = notifyExternalActionList(actionType, actionItemId);

                if (success) {
                    rs.deleteRow();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving items from KREW_ACTN_ITM_CHANGED_T", e);
        } finally {
            if (statement != null) {
                try {
                 statement.close();
                } catch (SQLException e) {
                    LOG.warn("Could not close statement.");
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOG.warn("Could not close result set.");
                }
            }
        }
    }

    private boolean notifyExternalActionList(String actionType, String actionItemId) {
        LOG.info("actionType: " + actionType + "   actionItemId: " + actionItemId);
        ActionItem actionItem = null;

        // Get the action item unless it was deleted
        if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_INSERTED) || (actionType
                .toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_UPDATED))) {
            actionItem = KEWServiceLocator.getActionListService().findByActionItemId(actionItemId);
        }

        if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_INSERTED)) {
            LOG.info("Code to INSERT into external action list goes here");
        } else if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_UPDATED)) {
            LOG.info("Code to UPDATE external action list goes here");
        } else if (actionType.toString().equalsIgnoreCase(KewApiConstants.ACTION_ITEM_DELETED)) {
            LOG.info("Code to DELETE from external action list goes here");
        }

        // Currently always return true to indicate successful processing.
        // This may change once the above code is implemented.
        return true;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Sets the polling interval time in seconds
     * @param seconds the polling interval time in seconds
     */
    public void setExternalActnListNotificationPollIntervalSeconds(int seconds) {
        this.externalActnListNotificationPollIntervalSeconds = seconds;
    }

    /**
     * Gets the polling interval time in seconds
     * @return the polling interval time in seconds
     */
    public int getExternalActnListNotificationPollIntervalSeconds() {
        return this.externalActnListNotificationPollIntervalSeconds;
    }

    /**
     * Sets the initial delay time in seconds
     * @param seconds the initial delay time in seconds
     */
    public void setExternalActnListNotificationInitialDelaySeconds(int seconds) {
        this.externalActnListNotificationInitialDelaySeconds = seconds;
    }

    /**
     * Gets the initial delay time in seconds
     * @return the initial delay time in seconds
     */
    public int getExternalActnListNotificationInitialDelaySeconds() {
        return this.externalActnListNotificationInitialDelaySeconds;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public synchronized void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
