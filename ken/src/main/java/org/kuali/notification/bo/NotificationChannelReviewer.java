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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A reviewer for a notification publications to a NotificationChannel
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationChannelReviewer {
    private Long id;
    private NotificationChannel channel;
    private String reviewerType;
    private String reviewerId;

    /**
     * Returns the primary key value
     * @return the primary key value
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the primary key value
     * @param id the primary key value
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Returns the channel with which this reviewer is associated
     * @return the channel with which this reviewer is associated
     */
    public NotificationChannel getChannel() {
        return channel;
    }

    /**
     * Sets the channel with which this reviewer is associated
     * @param channel the channel with which this reviewer is associated
     */
    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    /**
     * Returns the user id of the reviewer.  This is abstract but ultimately
     * will need to be resolved to a KEW user/group
     * @return the user id of the reviewer
     */
    public String getReviewerId() {
        return reviewerId;
    }
    
    /**
     * Sets the user id of the reviewer
     * @param reviewerId the user id of the reviewer
     */
    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }
    
    /**
     * Returns the type of reviewer, USER or GROUP
     * @return the type of reviewer, USER or GROUP
     */
    public String getReviewerType() {
        return reviewerType;
    }

    /**
     * Sets the type of reviewer, USER or GROUP
     * @param reviewerType the type of reviewer, USER or GROUP
     */
    public void setReviewerType(String reviewerType) {
        this.reviewerType = reviewerType;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                                        .append("channel", channel != null ? channel.getId() : null)
                                        .append("reviewerId", reviewerId).toString();
    }
}