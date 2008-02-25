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
package org.kuali.notification.bo;

/**
 * This class represents the data structure that will house information about the non-system 
 * sender that a notification message is sent on behalf of.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationSender {
    private Long id;
    private Long notificationId;
    private String senderName;
    
    /**
     * Constructs a NotificationSender.java instance.
     */
    public NotificationSender() {
    }

    /**
     * Gets the id attribute. 
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the notificationId attribute. 
     * @return Returns the notificationId.
     */
    public Long getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the notificationId attribute value.
     * @param notificationId The notificationId to set.
     */
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the senderName attribute. 
     * @return Returns the senderName.
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Sets the senderName attribute value.
     * @param senderName The senderName to set.
     */
    public void setSenderName(String userId) {
        this.senderName = userId;
    }
}