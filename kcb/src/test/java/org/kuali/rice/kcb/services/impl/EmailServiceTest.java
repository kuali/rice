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
package org.kuali.rice.kcb.services.impl;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.test.RollbackKCBTestCase;
import org.kuali.rice.kcb.test.service.MockEmailService;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestSql;

/**
 * This class tests the implementation of the email service.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmailServiceTest extends RollbackKCBTestCase {
    public static final String VALID_EMAIL = "abcd@efghi.jkl";
    public static final String VALID_FORMAT_VALUE = "text";

    @UnitTestData(sqlStatements = {
        @UnitTestSql("insert into KCB_MESSAGES values (1, 'fyi', systimestamp, 'a title', 'channel1', 'a producer', 'some content', 'a content type', 'user1', 0)"),
        @UnitTestSql("insert into KCB_MSG_DELIVS (ID, MESSAGE_ID, DELIVERER_TYPE_NAME, DELIVERER_SYSTEM_ID, DELIVERY_STATUS, LOCKED_DATE, DB_LOCK_VER_NBR) values (1, 1, 'email', 'fake system id', 'fakestatus', NULL, 0)")
    })

    @Test
    public void testSendNotificationEmail() throws Exception {
        MessageDeliveryService mds = GlobalKCBServiceLocator.getInstance().getMessageDeliveryService();
        MockEmailService emailService = (MockEmailService) GlobalKCBServiceLocator.getInstance().getEmailService();
        emailService.getMailBoxes().clear();

        MessageDelivery nmd = mds.getMessageDelivery(Long.valueOf(1));
        Long emailMessageId = emailService.sendEmail(nmd, VALID_EMAIL, VALID_FORMAT_VALUE);
        
        assertEquals(1, emailService.getMailBoxes().size());
        List<Map<String, String>> mailbox = emailService.getMailBoxes().get(VALID_EMAIL);
        assertNotNull(mailbox);
        assertEquals(1, mailbox.size());
    }
}