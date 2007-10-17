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
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.exception.NotificationMessageDeliveryException;
import org.kuali.notification.service.NotificationEmailService;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.test.TestConstants;

/**
 * This class tests the implementation of the email service.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationEmailServiceImplTest extends NotificationTestCaseBase {
    public static final String VALID_EMAIL = "";
    public static final String VALID_FORMAT_VALUE = "text";

    public NotificationEmailServiceImplTest() {
    }

    @Test
    public void testSendNotificationEmail() throws NotificationMessageDeliveryException {
	NotificationMessageDeliveryService nSvc = services.getNotificationMessageDeliveryService();
	NotificationEmailService notificationEmailService = services.getNotificationEmailService();

        NotificationMessageDelivery nmd = nSvc.getNotificationMessageDelivery(TestConstants.VALID_EMAIL_MESSAGE_DELIVERY_ID);
        try {
          Long emailMessageId = notificationEmailService.sendNotificationEmail(nmd, VALID_EMAIL, VALID_FORMAT_VALUE);
        } catch (Exception we) {
	    LOG.error(we.getStackTrace());
	    throw new NotificationMessageDeliveryException(we.getStackTrace().toString());
	}
	
    }
}
