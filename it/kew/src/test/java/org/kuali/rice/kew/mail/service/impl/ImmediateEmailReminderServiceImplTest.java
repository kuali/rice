/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.mail.service.impl;

import static org.junit.Assert.assertEquals;
import mocks.MockEmailNotificationService;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.api.mail.ImmediateEmailReminderQueue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;

/**
 * This test case verifies that the the ImmediateEmailReminderQueue can be retrieved from teh KewApiServiceLocator and that calling it
 * will 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ImmediateEmailReminderServiceImplTest extends KEWTestCase {

    private ImmediateEmailReminderQueue immediateEmailReminderQueue; // = new ImmediateEmailReminderServiceImpl();
    
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        immediateEmailReminderQueue = KewApiServiceLocator.getImmediateEmailReminderService();
    }
    
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
    
    @Test
    public void test() {  
        ActionItem actionItem = ActionItem.Builder.create("124", KEWConstants.ACTION_REQUEST_APPROVE_REQ, "123", new DateTime(), "Test", "http://www.test.com", "Test", "125", "user1").build();
        immediateEmailReminderQueue.sendReminder(actionItem, Boolean.FALSE);
        
        assertEquals("user1 should have 1 email", 1, getMockEmailService().immediateReminderEmailsSent("user1", "124", KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("user2 should have no emails", 0, getMockEmailService().immediateReminderEmailsSent("user2", "124", KEWConstants.ACTION_REQUEST_APPROVE_REQ));

        getMockEmailService().resetReminderCounts();

        immediateEmailReminderQueue.sendReminder(actionItem, Boolean.TRUE);
        assertEquals("user1 should have no emails", 0, getMockEmailService().immediateReminderEmailsSent("user1", "124", KEWConstants.ACTION_REQUEST_APPROVE_REQ));

        getMockEmailService().resetReminderCounts();

        actionItem = ActionItem.Builder.create("124", KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "123", new DateTime(), "Test", "http://www.test.com", "Test", "125", "user1").build();
        immediateEmailReminderQueue.sendReminder(actionItem, Boolean.TRUE);
        assertEquals("user1 should have 1 emails", 1, getMockEmailService().immediateReminderEmailsSent("user1", "124", KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
    }

    
    private MockEmailNotificationService getMockEmailService() {
        return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
    }
}
