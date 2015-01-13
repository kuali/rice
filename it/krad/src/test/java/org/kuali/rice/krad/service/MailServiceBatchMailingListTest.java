/*
 * Copyright 2006-2015 The Kuali Foundation
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

package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.mail.Mailer;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.exception.InvalidAddressException;
import org.kuali.rice.krad.service.impl.MailServiceImpl;
import org.kuali.test.KRADTestCase;
import org.subethamail.wiser.Wiser;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by sona on 1/8/15.
 */
public class MailServiceBatchMailingListTest  extends KRADTestCase {
    @Override
    public void setUp() throws Exception {
        // Set the notification properties
        System.setProperty("mailing.list.batch", "testSender@test.kuali.org");
        System.setProperty("nonproduction.notification.mailing.list","testRecipient@test.kuali.org");
        System.setProperty("real.notifications.enabled","false") ;
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        try {
            System.clearProperty("nonproduction.notification.mailing.list");
            System.setProperty("mailing.list.batch", "mailing.list.batch");
            System.setProperty("real.notifications.enabled","true") ;
        } finally {
            super.tearDown();
        }
    }

    @Test
    public void testSendEmailFromMailingListBatch() throws IOException, InvalidAddressException {
        // Initialize SMTP server
        Wiser smtpServer = new Wiser();
        smtpServer.setPort(55000);
        smtpServer.start();

        MailService mailService = GlobalResourceLoader.getService("mailService");
        // Test that a Mailer can be retrieved via the KEWServiceLocator
        Mailer mailer = CoreApiServiceLocator.getMailer();
        assertNotNull(mailer);

        ((MailServiceImpl) mailService).setMailer(mailer);

        MailMessage message = new MailMessage();
        message.setMessage("Test Message");
        message.setSubject("Test Subject");

        try {
            mailService.sendMessage(message);
        } catch (MessagingException e) {
            e.printStackTrace();

        }
        //Assert that the message has been sent
        assertEquals(1, smtpServer.getMessages().size());

        //Assert that the from address is the same as mailing.list.batch
        assertEquals(smtpServer.getMessages().get(0).getEnvelopeSender(),"testSender@test.kuali.org");

        // Shutdown the SMTP server
        smtpServer.stop();
    }

}
