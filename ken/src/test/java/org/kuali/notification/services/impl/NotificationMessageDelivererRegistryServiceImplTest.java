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

import java.util.ArrayList;

import org.junit.Test;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.test.TestConstants;

/**
 * This class tests the registry service.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageDelivererRegistryServiceImplTest extends NotificationTestCaseBase {
    /**
     * This method tests the hard coded registry list.
     */
    @Test
    public void testGetAllDeliverTypes() {
	ArrayList<NotificationMessageDeliverer> deliverers = services.getNotificationMessageDelivererRegistryService().getAllDelivererTypes();
	
	assertEquals(2, deliverers.size());
	
	for(NotificationMessageDeliverer deliverer: deliverers) {
	    assertTrue(deliverer.getName() != null);
	    assertTrue(deliverer.getName().length() > 0);
	}
    }
    
    /**
     * This method tests a valid deliverer retrieval from the registry.
     */
    @Test
    public void testGetDeliverer_valid() {
	NotificationMessageDelivery mockValid = new NotificationMessageDelivery();
	mockValid.setMessageDeliveryTypeName(TestConstants.VALID_DELIVERER_NAME);
	
	NotificationMessageDeliverer deliverer = services.getNotificationMessageDelivererRegistryService().getDeliverer(mockValid);
	if (deliverer == null) {
	    throw new RuntimeException("Message deliverer could not be obtained");
	}

	assertEquals(TestConstants.VALID_DELIVERER_NAME, deliverer.getName());
    }
    
    /**
     * This method tests a valid deliverer retrieval from the registry.
     */
    @Test
    public void testGetDeliverer_nonExistent() {
	NotificationMessageDelivery mockInvalid = new NotificationMessageDelivery();
	mockInvalid.setMessageDeliveryTypeName(TestConstants.NON_EXISTENT_DELIVERER_NAME);

	boolean caughtException = false;
	
	NotificationMessageDeliverer deliverer = services.getNotificationMessageDelivererRegistryService().getDeliverer(mockInvalid);

	assertNull(deliverer);
    }
    
    /**
     * This method tests a valid deliverer retrieval by name.
     */
    @Test
    public void testGetDelivererByName_valid() {
	NotificationMessageDeliverer deliverer = services.getNotificationMessageDelivererRegistryService().getDelivererByName(TestConstants.VALID_DELIVERER_NAME);
	
	assertEquals(TestConstants.VALID_DELIVERER_NAME, deliverer.getName());
    }

    @Test
    public void testGetDelivererByName_nonExistent() {
	boolean caughtException = false;
	
	NotificationMessageDeliverer deliverer = services.getNotificationMessageDelivererRegistryService().getDelivererByName(TestConstants.NON_EXISTENT_DELIVERER_NAME);
	
	assertNull(deliverer);
    }
}
