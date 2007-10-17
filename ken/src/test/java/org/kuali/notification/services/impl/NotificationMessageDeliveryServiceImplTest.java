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
package org.kuali.notification.services.impl;

import java.util.Collection;

import org.junit.Test;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.test.TestConstants;

/**
 * This class tests the message delivery service implementation
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDeliveryServiceImplTest extends NotificationTestCaseBase {

    @Test
    public void testGetNotificationMessageDelivery_validId() {
        NotificationMessageDeliveryService nSvc = services.getNotificationMessageDeliveryService();

        NotificationMessageDelivery nmd = nSvc.getNotificationMessageDelivery(TestConstants.VALID_MESSAGE_DELIVERY_ID);

        assertNotNull(nmd.getMessageDeliveryStatus());
    }

    @Test
    public void testGetNotification_nonExistentNotification() {
        NotificationMessageDeliveryService nSvc = services.getNotificationMessageDeliveryService();

        NotificationMessageDelivery nmd = nSvc.getNotificationMessageDelivery(TestConstants.NON_EXISTENT_ID);

        assertNull(nmd);
    }

    @Test
    public void testGetAllNotificationMessageDeliveries() {
        NotificationMessageDeliveryService nSvc = services.getNotificationMessageDeliveryService();
        Collection<NotificationMessageDelivery> deliveries = nSvc.getNotificationMessageDeliveries();
        assertNotNull(deliveries);
        assertEquals(TestConstants.NUM_OF_MSG_DELIVS_IN_TEST_DATA, deliveries.size());
    }

    @Test
    public void testGetSpecificNotificationMessageDeliveries() {
        Notification n = services.getNotificationService().getNotification(TestConstants.NOTIFICATION_1);
        NotificationMessageDeliveryService nSvc = services.getNotificationMessageDeliveryService();
        Collection<NotificationMessageDelivery> deliveries = nSvc.getNotificationMessageDeliveries(n, TestConstants.TEST_USER_FIVE);
        assertNotNull(deliveries);
        assertEquals(TestConstants.NUM_OF_MSG_DELIVS_FOR_NOTIF_1_TEST_USER_5, deliveries.size());
    }
}