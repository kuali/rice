/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.mail.EmailBody;
import org.kuali.rice.core.api.mail.EmailFrom;
import org.kuali.rice.core.api.mail.EmailSubject;
import org.kuali.rice.core.api.mail.EmailTo;
import org.kuali.rice.core.api.mail.MailAttachment;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.mail.Mailer;
import org.kuali.rice.kew.notes.Attachment;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.subethamail.wiser.Wiser;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Tests email content generation
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MailerTest extends KEWTestCase {
    
    private String sender = "testSender@test.kuali.org";
    private String recipient = "testRecipient@test.kuali.org";
	private String subject = "Test Subject";
    private String messageBody = "Test Message Body";
    
	/**
	 * Test that a Mailer can be retrieved via the KEWServiceLocator and can be used 
	 * to send an e-mail message to the SMTP server.
	 */
	@Test
	public void testSendMessage() {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(55000);
		smtpServer.start();
		
		// Test that a Mailer can be retrieved via the KEWServiceLocator
		Mailer mailer = null;
		mailer = CoreApiServiceLocator.getMailer();
		assertNotNull(mailer);
		
		// Test that an e-mail message gets sent to the SMTP server
		mailer.sendEmail(new EmailFrom(sender), new EmailTo(recipient), new EmailSubject(subject),
				new EmailBody(messageBody), false);

		Assert.assertEquals(1, smtpServer.getMessages().size());

        // Shutdown the SMTP server
        smtpServer.stop();        
	}

	/**
	 * Test that an e-mail message can be sent without an attachment being set
	 */
	@Test
	public void testSendEmail() throws IOException {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(55000);
		smtpServer.start();

		// Test that a Mailer can be retrieved via the KEWServiceLocator
		Mailer mailer = CoreApiServiceLocator.getMailer();
		assertNotNull(mailer);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setSubject(subject);
		mailMessage.setFromAddress(sender);
		mailMessage.setMessage(messageBody);

		String[] recipientAddresses = {recipient};
		mailMessage.setToAddresses(new HashSet<String>(Arrays.asList(recipientAddresses)));
		mailMessage.setBccAddresses(null);
		mailMessage.setCcAddresses(null);

		try {
			mailer.sendEmail(mailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		assertEquals(1, smtpServer.getMessages().size());

		// Shutdown the SMTP server
		smtpServer.stop();
	}

	/**
	 * Test that an e-mail message can be sent without an attachment being set
	 */
	@Test
	public void testSendEmailNoRecipient() throws IOException {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(55000);
		smtpServer.start();

		// Test that a Mailer can be retrieved via the KEWServiceLocator
		Mailer mailer = CoreApiServiceLocator.getMailer();
		assertNotNull(mailer);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setSubject(subject);
		mailMessage.setFromAddress(sender);
		mailMessage.setMessage(messageBody);

		mailMessage.setToAddresses(null);
		mailMessage.setBccAddresses(null);
		mailMessage.setCcAddresses(null);

		try {
			mailer.sendEmail(mailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
			assertEquals(e.getMessage(), "There is no to, cc, or bcc address specified. Refraining from sending mail.");
		}

		assertEquals(0, smtpServer.getMessages().size());

		// Shutdown the SMTP server
		smtpServer.stop();
	}

	/**
	 * Test that an e-mail message can be sent without an attachment being set
	 */
	@Test
	public void testSendEmailOnlyCcRecipient() throws IOException {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(55000);
		smtpServer.start();

		// Test that a Mailer can be retrieved via the KEWServiceLocator
		Mailer mailer = CoreApiServiceLocator.getMailer();
		assertNotNull(mailer);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setSubject(subject);
		mailMessage.setFromAddress(sender);
		mailMessage.setMessage(messageBody);
		mailMessage.setToAddresses(null);
		mailMessage.setBccAddresses(null);

		String[] recipientAddresses = {recipient};
		mailMessage.setCcAddresses(new HashSet<String>(Arrays.asList(recipientAddresses)));

		try {
			mailer.sendEmail(mailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		assertEquals(1, smtpServer.getMessages().size());

		// Shutdown the SMTP server
		smtpServer.stop();
	}

	/**
	 * Test that an e-mail message can be sent without an attachment being set
	 */
	@Test
	public void testSendEmailOnlyBccRecipient() throws IOException {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(55000);
		smtpServer.start();

		// Test that a Mailer can be retrieved via the KEWServiceLocator
		Mailer mailer = CoreApiServiceLocator.getMailer();
		assertNotNull(mailer);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setSubject(subject);
		mailMessage.setFromAddress(sender);
		mailMessage.setMessage(messageBody);

		mailMessage.setToAddresses(null);
		mailMessage.setCcAddresses(null);
		String[] recipientAddresses = {recipient};
		mailMessage.setBccAddresses(new HashSet<String>(Arrays.asList(recipientAddresses)));

		try {
			mailer.sendEmail(mailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		assertEquals(1, smtpServer.getMessages().size());

		// Shutdown the SMTP server
		smtpServer.stop();
	}

	/**
	 * Test that an e-mail message can be sent without an attachment being set
	 */
	@Test
	public void testSendEmailToCcAndBccRecipients() throws IOException {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(55000);
		smtpServer.start();

		// Test that a Mailer can be retrieved via the KEWServiceLocator
		Mailer mailer = CoreApiServiceLocator.getMailer();
		assertNotNull(mailer);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setSubject(subject);
		mailMessage.setFromAddress(sender);
		mailMessage.setMessage(messageBody);

		String[] recipientAddresses = {recipient};
		mailMessage.setToAddresses(new HashSet<String>(Arrays.asList(recipientAddresses)));
		mailMessage.setCcAddresses(new HashSet<String>(Arrays.asList(recipientAddresses)));
		mailMessage.setBccAddresses(new HashSet<String>(Arrays.asList(recipientAddresses)));

		try {
			mailer.sendEmail(mailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		assertEquals(3, smtpServer.getMessages().size());

		// Shutdown the SMTP server
		smtpServer.stop();
	}

	/**
	 * Test that an e-mail message can be sent with an attachment.
	 */
	@Test
	public void testSendEmailWithAttachments() throws IOException {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(55000);
		smtpServer.start();

		// Test that a Mailer can be retrieved via the KEWServiceLocator
		Mailer mailer = CoreApiServiceLocator.getMailer();
		assertNotNull(mailer);

		Attachment attachmentToUse = new Attachment();
		attachmentToUse.setFileName("attachedFile1.txt");
		attachmentToUse.setMimeType("text/plain");
		attachmentToUse.setAttachedObject(TestUtilities.loadResource(this.getClass(), "attachedFile1.txt"));

		Attachment attachment2ToUse = new Attachment();
		attachment2ToUse.setFileName("attachedFile1.txt");
		attachment2ToUse.setMimeType("text/plain");
		attachment2ToUse.setAttachedObject(TestUtilities.loadResource(this.getClass(), "attachedFile1.txt"));

		MailAttachment attachedFile1 = new MailAttachment();
		attachedFile1.setFileName(attachmentToUse.getFileName());
		attachedFile1.setType(attachmentToUse.getMimeType());

		try {
			attachedFile1.setContent(IOUtils.toByteArray(attachmentToUse.getAttachedObject()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		MailAttachment attachedFile2 = new MailAttachment();
		attachedFile2.setFileName(attachment2ToUse.getFileName());
		attachedFile2.setType(attachment2ToUse.getMimeType());

		try {
			attachedFile2.setContent(IOUtils.toByteArray(attachment2ToUse.getAttachedObject()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();
		mailAttachments.add(attachedFile1);
		mailAttachments.add(attachedFile2);

		MailMessage mailMessage = new MailMessage();
		mailMessage.setSubject(subject);
		mailMessage.setFromAddress(sender);
		String[] recipientAddresses = {recipient};
		mailMessage.setToAddresses(new HashSet<String>(Arrays.asList(recipientAddresses)));
		mailMessage.setMessage(messageBody);
		mailMessage.setMailAttachments(mailAttachments);
		mailMessage.setBccAddresses(null);
		mailMessage.setCcAddresses(null);

		try {
			mailer.sendEmail(mailMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		assertEquals(1, smtpServer.getMessages().size());

		// Shutdown the SMTP server
		smtpServer.stop();
	}
}
