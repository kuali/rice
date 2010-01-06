/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kcb.service.impl;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.service.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.test.KCBTestCase;
import org.kuali.rice.kcb.test.service.MockEmailService;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestSql;

/**
 * This class tests the implementation of the email service.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineMode(Mode.ROLLBACK)
public class EmailServiceTest extends KCBTestCase {
    public static final String VALID_EMAIL = "abcd@efghi.jkl";
    public static final String VALID_FORMAT_VALUE = "text";

    @Test
    @UnitTestData(sqlStatements = {
        @UnitTestSql("insert into KREN_MSG_T (MSG_ID, DELIV_TYP, CRTE_DTTM, TTL, CHNl, PRODCR, CNTNT, CNTNT_TYP, URL, RECIP_ID, VER_NBR) values (1, 'fyi', {d '2009-01-01'}, 'a title', 'channel1', 'a producer', 'some content', 'a content type', 'url', 'user1', 0)"),
        @UnitTestSql("insert into KREN_MSG_DELIV_T (MSG_DELIV_ID, MSG_ID, TYP_NM, SYS_ID, STAT_CD, LOCKD_DTTM, VER_NBR) values (1, 1, 'email', 'fake system id', 'fakestatus', NULL, 0)")
    })
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
