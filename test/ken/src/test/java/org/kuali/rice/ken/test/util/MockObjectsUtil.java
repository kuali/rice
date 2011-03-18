/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ken.test.util;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.rice.ken.bo.Notification;
import org.kuali.rice.ken.bo.NotificationChannel;
import org.kuali.rice.ken.bo.NotificationChannelReviewer;
import org.kuali.rice.ken.bo.NotificationContentType;
import org.kuali.rice.ken.bo.NotificationPriority;
import org.kuali.rice.ken.bo.NotificationProducer;
import org.kuali.rice.ken.bo.NotificationRecipient;
import org.kuali.rice.ken.bo.NotificationSender;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;

/**
 * This class helps to provide common mock objects for testing and also helper methods to build instances of objects.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class MockObjectsUtil {
	
	private MockObjectsUtil() {
		throw new UnsupportedOperationException("do not call");
	}
	
    /**
     * This method is a helper to build a NotificationChannel instance.
     * @param name
     * @param description
     * @param subscribable
     * @return NotificationChannel
     */
    public static final NotificationChannel buildTestNotificationChannel(String name, String description, boolean subscribable) {
        NotificationChannel channel = new NotificationChannel();
        channel.setName(name);
        channel.setDescription(description);
        channel.setSubscribable(subscribable);
        return channel;
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationChannel
     */
    public static final NotificationChannel getTestChannel1() {
        return buildTestNotificationChannel("Test Channel 1", "Test Channel 1 - description", true);
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationChannel
     */
    public static final NotificationChannel getTestChannel2() {
        return buildTestNotificationChannel("Test Channel 2", "Test Channel 2 - description", false);
    }

    /**
     * This method is a helper to build a NotificationProducer instance.
     * @param name
     * @param description
     * @param contactInfo
     * @return
     */
    public static final NotificationProducer buildTestNotificationProducer(String name, String description, String contactInfo) {
        NotificationProducer producer = new NotificationProducer();
        producer.setName(name);
        producer.setDescription(description);
        producer.setContactInfo(contactInfo);
        return producer;
    }

    /**
     * This method is a helper to build a NotificationChannelReviewer instance.
     * @param reviewerType
     * @param reviewerId
     * @return
     */
    public static final NotificationChannelReviewer buildTestNotificationChannelReviewer(String reviewerType, String reviewerId) {
        NotificationChannelReviewer reviewer = new NotificationChannelReviewer();
        reviewer.setReviewerType(reviewerType);
        reviewer.setReviewerId(reviewerId);
        return reviewer;
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationProducer
     */
    public static final NotificationProducer getTestProducer1() {
        return buildTestNotificationProducer("Produer 1", "Producer 1 - description", "Producer 1 - contact info");
    }

    /**
     * This method is a helper to build a NotificationContentType instance.
     * @param name
     * @param description
     * @param namespace
     * @param xsd
     * @return NotificationContentType
     */
    public static final NotificationContentType buildTestNotificationContentType(String name, String description, String namespace, String xsd, String xsl) {
        NotificationContentType contentType = new NotificationContentType();
        contentType.setName(name);
        contentType.setDescription(description);
        contentType.setNamespace(namespace);
        contentType.setXsd(xsd);
        contentType.setXsl(xsl);
        return contentType;
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationContentType
     */
    public static final NotificationContentType getTestContentType1() {
        return buildTestNotificationContentType("Content Type 1", "Content Type 1 - description", "Content Type 1 - namespace", "Simple.xsd", "Simple.xsl");
    }

    /**
     * This method is a helper to build a NotificationPriority instance.
     * @param name
     * @param description
     * @param order
     * @return NotificationPriority
     */
    public static final NotificationPriority buildTestNotificationPriority(String name, String description, Integer order) {
        NotificationPriority priority = new NotificationPriority();
        priority.setName(name);
        priority.setDescription(description);
        priority.setOrder(order);
        return priority;
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationPriority
     */
    public static final NotificationPriority getTestPriority1() {
        return buildTestNotificationPriority("Priority 1", "Priority 1 - description", new Integer(1));
    }

    /**
     * This method is a helper to build a NotificationRecipient instance.
     * @param recipientId
     * @param recipientType
     * @return NotificationRecipient
     */
    public static final NotificationRecipient buildTestNotificationRecipient(String recipientId, String recipientType) {
        NotificationRecipient recipient = new NotificationRecipient();
        recipient.setRecipientId(recipientId);
        recipient.setRecipientType(recipientType);
        return recipient;
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationRecipient
     */
    public static final NotificationRecipient getTestRecipient1() {
        return buildTestNotificationRecipient("ag266", KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationRecipient
     */
    public static final NotificationRecipient getTestRecipient2() {
	 return buildTestNotificationRecipient("Notification Team", KimGroupMemberTypes.GROUP_MEMBER_TYPE);
    }

    /**
     * This method is a helper to build a Notification instance.
     * @param recipientId
     * @param recipientType
     * @return Notification
     */
    public static final Notification buildTestNotification(String deliveryType, Timestamp sendDateTime, Timestamp autoRemoveDateTime, NotificationContentType contentType,
	    String content, NotificationPriority priority, NotificationProducer producer, NotificationChannel channel, List<NotificationRecipient> recipients,
	    List<NotificationSender> senders) {
        Notification notification = new Notification();
        notification.setCreationDateTime(new Timestamp(System.currentTimeMillis()));
        notification.setDeliveryType(deliveryType);
        notification.setSendDateTime(sendDateTime);
        notification.setAutoRemoveDateTime(autoRemoveDateTime);
        notification.setContentType(contentType);
        notification.setContent(content);
        notification.setPriority(priority);
        notification.setProducer(producer);
        notification.setChannel(channel);
        notification.setRecipients(recipients);
        notification.setSenders(senders);

        return notification;
    }

    /**
     * This method is a helper to build a NotificationSender instance.
     * @param recipientId
     * @param recipientType
     * @return NotificationSender
     */
    public static final NotificationSender buildTestNotificationSender(String userId) {
        NotificationSender sender = new NotificationSender();
        sender.setSenderName(userId);
        return sender;
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationSender
     */
    public static final NotificationSender getTestSender1() {
        return buildTestNotificationSender("Joe Schmoe");
    }

    /**
     * This method returns back a specific test mock object.
     * @return NotificationSender
     */
    public static final NotificationSender getTestSender2() {
	return buildTestNotificationSender("John Doe");
    }
}
