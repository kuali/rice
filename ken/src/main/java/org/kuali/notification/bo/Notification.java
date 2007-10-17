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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.notification.util.NotificationConstants;

/**
 * This class represents an instace of a notification message that is received by the overall 
 * system.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Notification implements Lockable {
    private Long id;
    private String deliveryType;
    private Timestamp creationDateTime;
    private Timestamp sendDateTime;
    private Timestamp autoRemoveDateTime;
    private String title;
    private String content;
    private String processingFlag;
    private Timestamp lockedDate;
    /**
     * Lock column for OJB optimistic locking
     */
    private Integer lockVerNbr;
    
    // object references
    private NotificationPriority priority;
    private NotificationContentType contentType;
    private NotificationChannel channel;
    private NotificationProducer producer;
    
    // lists
    private List<NotificationRecipient> recipients;
    private List<NotificationSender> senders;
    
    /**
     * Constructs a Notification instance.
     */
    public Notification() {
        recipients = new ArrayList<NotificationRecipient>();
        senders = new ArrayList<NotificationSender>();
        processingFlag = NotificationConstants.PROCESSING_FLAGS.UNRESOLVED;
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
     * Gets the recipients attribute. 
     * @return Returns the recipients.
     */
    public List<NotificationRecipient> getRecipients() {
        return recipients;
    }

    /**
     * Sets the recipients attribute value.
     * @param recipients The recipients to set.
     */
    public void setRecipients(List<NotificationRecipient> recipients) {
        this.recipients = recipients;
    }

    /**
     * Retrieves a recipient at the specified index
     * @param index the index in the recipients collection
     * @return the recipient if found or null
     */
    public NotificationRecipient getRecipient(int index) {
        return (NotificationRecipient) recipients.get(index);
    }
    
    /**
     * Adds a recipient
     * @param recipient The recipient to add
     */
    public void addRecipient(NotificationRecipient recipient) {
        recipients.add(recipient);
    }

    /**
     * Gets the senders attribute. 
     * @return Returns the senders.
     */
    public List<NotificationSender> getSenders() {
        return senders;
    }

    /**
     * Sets the senders attribute value.
     * @param senders The senders to set.
     */
    public void setSenders(List<NotificationSender> senders) {
        this.senders = senders;
    }

    /**
     * Retrieves a sender at the specified index
     * @param index the index in the senders collection
     * @return the sender if found or null
     */
    public NotificationSender getSender(int index) {
        return (NotificationSender) senders.get(index);
    }
    /**
     * Adds a sender
     * @param sender The sender to add
     */
    public void addSender(NotificationSender sender) {
        senders.add(sender);
    }

    /**
     * Gets the autoRemoveDateTime attribute. 
     * @return Returns the autoRemoveDateTime.
     */
    public Timestamp getAutoRemoveDateTime() {
	return autoRemoveDateTime;
    }

    /**
     * Sets the autoRemoveDateTime attribute value.
     * @param autoRemoveDateTime The autoRemoveDateTime to set.
     */
    public void setAutoRemoveDateTime(Timestamp autoRemoveDateTime) {
	this.autoRemoveDateTime = autoRemoveDateTime;
    }

    /**
     * Gets the channel attribute. 
     * @return Returns the channel.
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
    public NotificationContentType getContentType() {
	return contentType;
    }

    /**
     * Sets the contentType attribute value.
     * @param contentType The contentType to set.
     */
    public void setContentType(NotificationContentType contentType) {
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
     * Gets the priority attribute. 
     * @return Returns the priority.
     */
    public NotificationPriority getPriority() {
	return priority;
    }

    /**
     * Sets the priority attribute value.
     * @param priority The priority to set.
     */
    public void setPriority(NotificationPriority priority) {
	this.priority = priority;
    }

    /**
     * Gets the producer attribute. 
     * @return Returns the producer.
     */
    public NotificationProducer getProducer() {
	return producer;
    }

    /**
     * Sets the producer attribute value.
     * @param producer The producer to set.
     */
    public void setProducer(NotificationProducer producer) {
	this.producer = producer;
    }

    /**
     * Gets the sendDateTime attribute. 
     * @return Returns the sendDateTime.
     */
    public Timestamp getSendDateTime() {
	return sendDateTime;
    }

    /**
     * Sets the sendDateTime attribute value.
     * @param sendDateTime The sendDateTime to set.
     */
    public void setSendDateTime(Timestamp sendDateTime) {
	this.sendDateTime = sendDateTime;
    }

    /**
     * Gets the processingFlag attribute. 
     * @return Returns the processingFlag.
     */
    public String getProcessingFlag() {
        return processingFlag;
    }

    /**
     * Sets the processingFlag attribute value.
     * @param processingFlag The processingFlag to set.
     */
    public void setProcessingFlag(String processingFlag) {
        this.processingFlag = processingFlag;
    }
    
    /**
     * Gets the lockedDate attribute. 
     * @return Returns the lockedDate.
     */
    public Timestamp getLockedDate() {
        return lockedDate;
    }
    
    /**
     * Sets the lockedDate attribute value.
     * @param lockedDate The lockedDate to set.
     */
    public void setLockedDate(Timestamp lockedDate) {
        this.lockedDate = lockedDate;
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
     * This method just uses StringUtils to get at the content of the <message> tag 
     * that exists in the notification content.
     * @return String
     */
    public String getContentMessage() {
	return StringUtils.substringBetween(content, NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_OPEN, NotificationConstants.XML_MESSAGE_CONSTANTS.MESSAGE_CLOSE);	
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this)
                       .append("id", id)
                       .append("deliveryType", deliveryType)
                       .append("sendDateTime", sendDateTime)
                       .append("autoRemoveDateTime", autoRemoveDateTime)
                       .append("content", StringUtils.abbreviate(content, 100))
                       .append("processingFlag", processingFlag)
                       .append("lockedDate", lockedDate)
                       .append("lockVerNbr", lockVerNbr)
                       .append("priority", priority)
                       .append("contentType", contentType)
                       .append("channel", channel)
                       .append("producer", producer)
                       .append("recipients", recipients == null ? null : "" + recipients.size())
                       .append("senders", senders == null ? null : "" + senders.size()).toString();
    }
}