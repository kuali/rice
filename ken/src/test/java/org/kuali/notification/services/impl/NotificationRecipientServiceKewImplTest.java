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

import org.junit.Test;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.test.TestConstants;

/**
 * This class tests the recipient service.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationRecipientServiceKewImplTest extends NotificationTestCaseBase {
    
    public NotificationRecipientServiceKewImplTest() {
    }

    @Test
    public void testAreUsersValid() {
	assertTrue(services.getNotificationRecipientService().isUserRecipientValid(TestConstants.TEST_USER_ONE));
	assertTrue(services.getNotificationRecipientService().isUserRecipientValid(TestConstants.TEST_USER_TWO));
    }

    @Test
    public void testIsGroupValid() {
	assertTrue(services.getNotificationRecipientService().isGroupRecipientValid(TestConstants.VALID_GROUP_NAME_1));
    }

    @Test
    public void testGetGroupMembersValid() {
	assertTrue(services.getNotificationRecipientService().getGroupMembers(TestConstants.VALID_GROUP_NAME_1).length == TestConstants.GROUP_1_MEMBERS);
    }

    @Test
    public void testGetAllUsers() {
	assertTrue(services.getNotificationRecipientService().getAllUsers().size()>0);
    }

    @Test
    public void testGetAllWorkgroups() {
	assertTrue(services.getNotificationRecipientService().getAllGroups().size()>0);
    }

    @Test
    public void testGetUserDisplayName() {
	assertEquals(TestConstants.TEST_USER_ONE_DISPLAYNAME, services.getNotificationRecipientService().getUserDisplayName(TestConstants.TEST_USER_ONE));
    }
}
