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

import java.util.HashMap;

import org.junit.Test;
import org.kuali.notification.bo.NotificationChannel;
import org.kuali.notification.bo.NotificationProducer;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.test.TestConstants;
import org.kuali.notification.util.NotificationConstants;

/**
 * This class tests the authz aspects of KEN
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationAuthorizationServiceImplTest extends NotificationTestCaseBase {
    
    public NotificationAuthorizationServiceImplTest() {
    }

    @Test
    public void testIsProducerAuthorizedForNotificationChannel_validInput() {
	HashMap primaryKeys = new HashMap();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID, TestConstants.CHANNEL_ID_1);
	NotificationChannel channel = (NotificationChannel) services.getBusinesObjectDao().findByPrimaryKey(NotificationChannel.class, primaryKeys);
	
	primaryKeys.clear();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID, TestConstants.PRODUCER_3.getId());
	NotificationProducer producer = (NotificationProducer) services.getBusinesObjectDao().findByPrimaryKey(NotificationProducer.class, primaryKeys);
	
	assertTrue(services.getNotificationAuthorizationService().isProducerAuthorizedToSendNotificationForChannel(producer, channel));
    }
    
    @Test
    public void testIsProducerAuthorizedForNotificationChannel_invalidInput() {
	HashMap primaryKeys = new HashMap();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID, TestConstants.CHANNEL_ID_1);
	NotificationChannel channel = (NotificationChannel) services.getBusinesObjectDao().findByPrimaryKey(NotificationChannel.class, primaryKeys);
	
	primaryKeys.clear();
	primaryKeys.put(NotificationConstants.BO_PROPERTY_NAMES.ID,TestConstants. PRODUCER_4.getId());
	NotificationProducer producer = (NotificationProducer) services.getBusinesObjectDao().findByPrimaryKey(NotificationProducer.class, primaryKeys);
	
	assertFalse(services.getNotificationAuthorizationService().isProducerAuthorizedToSendNotificationForChannel(producer, channel));
    }
    
    @Test
    public void testIsUserAdministrator_validAdmin() {
	assertTrue(services.getNotificationAuthorizationService().isUserAdministrator(TestConstants.ADMIN_USER_1));
    }

    @Test
    public void testIsUserAdministrator_nonAdmin() {
	assertFalse(services.getNotificationAuthorizationService().isUserAdministrator(TestConstants.NON_ADMIN_USER_1));
    }

    @Test
    public void testIsUserAdministrator_invalidUser() {
	assertFalse(services.getNotificationAuthorizationService().isUserAdministrator(TestConstants.INVALID_USER_1));
    }
}
