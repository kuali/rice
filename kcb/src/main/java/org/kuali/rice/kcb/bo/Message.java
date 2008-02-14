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
package org.kuali.rice.kcb.bo;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents an abstract message that has been sent to a single user
 * recipient and may result in several {@link MessageDelivery}s.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Message {
    /**
     * Field names
     */
    public static final String ID_FIELD = "id";

    private Long id;
    private String deliveryType;
    private String channel;
    private String producer;
    private Timestamp creationDateTime = new Timestamp(System.currentTimeMillis());
    private String title;
    private String content;
    private String contentType;
    private String recipient;

    /**
     * Lock column for OJB optimistic locking
     */
    private Integer lockVerNbr;

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
     * Returns when this Notification entry was created 
     * @return when this Notification entry was created
     */
    public Timestamp getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Sets the creation date of this Notification entry
     * @param created the creation date of this Notification entry
     */
    public void setCreationDateTime(Timestamp created) {
        this.creationDateTime = created;
    }

    /**
     * Return value of lock column for OJB optimistic locking
     * @return value of lock column for OJB optimistic locking
     */
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    /**
     * Set value of lock column for OJB optimistic locking
     * @param lockVerNbr value of lock column for OJB optimistic locking
     */
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    /**
     * Gets the recipient attribute. 
     * @return Returns the recipient.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient attribute value.
     * @param recipients The recipient to set.
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Gets the content attribute. 
     * @return Returns the content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content attribute value.
     * @param content The content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the contentType attribute. 
     * @return Returns the contentType.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the contentType attribute value.
     * @param contentType The contentType to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the deliveryType attribute. 
     * @return Returns the deliveryType.
     */
    public String getDeliveryType() {
        return deliveryType;
    }

    /**
     * Sets the deliveryType attribute value.
     * @param deliveryType The deliveryType to set.
     */
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType.toUpperCase();
    }

    /**
     * Gets the title
     * @return the title of this notification
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title
     * @param title the title of this notification
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the channel
     * @return the channel
     */
    public String getChannel() {
        return this.channel;
    }

    /**
     * Sets the channel
     * @param channel the channel
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * Gets the producer
     * @return the producer
     */
    public String getProducer() {
        return this.producer;
    }

    /**
     * Sets the producer
     * @param producer the producer
     */
    public void setProducer(String producer) {
        this.producer = producer;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this)
                       .append("id", id)
                       .append("creationDateTime", creationDateTime)
                       .append("deliveryType", deliveryType)
                       .append("recipient", recipient)
                       .append("title", title)
                       .append("channel", channel)
                       .append("producer", producer)
                       .append("content", StringUtils.abbreviate(content, 100))
                       .append("contentType", contentType)
                       .append("lockVerNbr", lockVerNbr)
                       .toString();
    }

}