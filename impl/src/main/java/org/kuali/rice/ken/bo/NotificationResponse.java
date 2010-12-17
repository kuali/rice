/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.ken.bo;

import org.kuali.rice.ken.util.NotificationConstants;


/**
 * This class represents the data structure that will house information for
 * a Notification Response
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationResponse {
    
    private String status;
    
    private String message;

    private Long notificationId;
    
    /**
     * Constructs a NotificationResponse.java instance.
     */
    public NotificationResponse() {
	status = NotificationConstants.RESPONSE_STATUSES.SUCCESS;
    }
    
    /**
     * Gets the status attribute. 
     * @return Returns the response status.
     */
    public String getStatus() {
	return status;
    }

    /**
     * Sets the status attribute value.
     * @param status The status to set.
     */
    public void setStatus(String status) {
	this.status = status;
    }
    
    /**
     * Gets the message attribute. 
     * @return Returns the response message.
     */
    
    public String getMessage() {
	return message;
    }

    /**
     * Sets the message attribute value.
     * @param message The message to set.
     */
    public void setMessage(String message) {
	this.message = message;
    }

    /**
     * Gets the id of the sent notification
     * @return the id of the sent notification
     */
    public Long getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the id of the sent notification
     * @param notificationId the id of the sent notification
     */
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
    
}
