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
package org.kuali.rice.core.mail;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.subethamail.wiser.Wiser;

/**
 * This class tests the Mailer against a mock SMTP server.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MailerTest {
	
	private String[] configLocations = {"classpath*:org/kuali/rice/core/mail/MailerTestSpringBeans.xml"};
    private ApplicationContext context = new ClassPathXmlApplicationContext(configLocations);
    private JavaMailSenderImpl mailSender = (JavaMailSenderImpl) context.getBean("mailSender");	
    private Mailer mailer = (Mailer)context.getBean("mailer");
    private String sender = "testSender@test.kuali.org";
    private String recipient = "testRecipient@test.kuali.org";
    private String subject = "Test Subject";
    private String messageBody = "Test Message Body";

	/**
	 * Test that an e-mail message gets sent to the SMTP server
	 */
	@Test
	public void testSendMessage() {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(mailSender.getPort());
		smtpServer.start();
		 
		// Test that an e-mail message gets sent to the SMTP server
		mailer.sendEmail(new EmailFrom(sender), new EmailTo(recipient), new EmailSubject(subject), new EmailBody(messageBody), false);
		Assert.assertEquals(1, smtpServer.getMessages().size());

        // Shutdown the SMTP server
        smtpServer.stop();        
	}

	/**
	 * Test that the port number is being retrieved from configuration properties is different from the port number of the SMTP server.
	 * Test that sending to an SMTP server with incorrect settings causes a MessagingException.
	 */
	@Test
	public void testIncorrectSmtpSettings() {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(12345);
		smtpServer.start();
 
		// Test that the port number is being retrieved from configuration properties is different from the port number of the SMTP server.
		Assert.assertTrue(smtpServer.getServer().getPort() != mailSender.getPort());

		// Test that sending to the wrong port causes a MessagingException.
		Exception e = null;
		try {
			mailer.sendEmail(new EmailFrom(sender), new EmailTo(recipient), new EmailSubject(subject), new EmailBody(messageBody), false);
		}
		catch (Exception me){
			e = me;
		}
		Assert.assertNotNull(e);
		
		// Shutdown the SMTP server
        smtpServer.stop();        
	}
	
	/**
	 * Test that sending to an incorrectly formatted e-mail address throws an AddressException.
	 */
	@Test
	public void testIncorrectEmailAddress() {
		// Initialize SMTP server
		Wiser smtpServer = new Wiser();
		smtpServer.setPort(mailSender.getPort());
		smtpServer.start();
				
		// Test that sending to an incorrectly formatted e-mail address throws an AddressException.
		Exception e = null;
		try {
			mailer.sendEmail(new EmailFrom(sender), new EmailTo("recipient@@test.kuali.org"), new EmailSubject(subject), new EmailBody(messageBody), false);
		}
		catch (Exception me){
			e = me;
		}
		Assert.assertNotNull(e);
		
		// Shutdown the SMTP server
        smtpServer.stop();   
	}
}
