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

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.test.KCBTestCase;
import org.kuali.rice.kcb.test.TestConstants;
import org.kuali.rice.kcb.test.util.MockEmailServiceImpl;

/**
 * This class tests the implementation of the email service.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmailServiceTest extends KCBTestCase {
    public static final String VALID_EMAIL = "abcd@efghi.jkl";
    public static final String VALID_FORMAT_VALUE = "text";

    // TODO: fix once preferences are in place
    @Ignore
    @Test
    public void testSendNotificationEmail() throws Exception {
        MessageDeliveryService mds = GlobalKCBServiceLocator.getInstance().getMessageDeliveryService();
        MockEmailServiceImpl emailService = (MockEmailServiceImpl) GlobalKCBServiceLocator.getInstance().getEmailService();

        /*MessageDelivery nmd = mds.getMessageDelivery(TestConstants.VALID_EMAIL_MESSAGE_DELIVERY_ID);
        Long emailMessageId = emailService.sendEmail(nmd, VALID_EMAIL, VALID_FORMAT_VALUE);
        
        assertEquals(1, emailService.MAILBOXES.size());
        List<Map<String, String>> mailbox = emailService.MAILBOXES.get(VALID_EMAIL);
        assertNotNull(mailbox);
        assertEquals(1, mailbox.size());*/
    }
}