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
 * This class represents an instance of an UserDelivererConfig.  Each UserDelivererConfig instance represents a 
 * user as having applied a delivery type configuration to a channel, such that any messages, targeted at the userId, 
 * will also be delivered to the correlating delivery type (delivererName) for that user.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserDelivererConfig {
    private Long id;
    private String userId;
    private String delivererName;
    private NotificationChannel channel;
    
    

    /**
     * Constructs a UserDelivererConfig instance.
     */
    public UserDelivererConfig() {
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
     * Gets the userId attribute. 
     * @return Returns the userId.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the userId attribute value.
     * @param userId The userId to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the delivererName attribute. 
     * @return Returns the name.
     */
    public String getDelivererName() {
	return delivererName;
    }

    /**
     * Sets the delivererName attribute value.
     * @param delivererName The delivererName to set.
     */
    public void setDelivererName(String delivererName) {
	this.delivererName = delivererName;
    }

    /**
     * Gets the channels attribute. 
     * @return Returns the channels.
     */
    public NotificationChannel getChannel() {
        return channel;
    }

    /**
     * Sets the channel attribute value.
     * @param channel The channel to set.
     */
    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }
}