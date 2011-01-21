/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.core.mail;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.kuali.rice.kew.util.ByteArrayDataSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * Maintains a Java Mail session and can be used for sending emails.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Mailer {

	    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

	    private JavaMailSenderImpl mailSender;	  
	    
		/**
		 * @param mailSender the mailSender to set
		 */
		public void setMailSender(JavaMailSenderImpl mailSender) {
			this.mailSender = mailSender;
		}
	    
	    /**
	     * Sends an email to the given recipients.
	     * 
	     * @param recipients
	     *            list of addresses to which the message is sent
	     * @param subject
	     *            subject of the message
	     * @param messageBody
	     *            body of the message
	     * @param ccRecipients
	     *            list of addresses which are to be cc'd on the message
	     * @param bccRecipients
	     *            list of addresses which are to be bcc'd on the message
	     *
	     * @throws AddressException
	     * @throws MessagingException
	     */
	    public void sendMessage(String sender, Address[] recipients, String subject, String messageBody, Address[] ccRecipients, Address[] bccRecipients, boolean htmlMessage) throws AddressException, MessagingException {
		    MimeMessage message = mailSender.createMimeMessage();

	        // From Address
	        message.setFrom(new InternetAddress(sender));

	        // To Address
	        if (recipients != null && recipients.length > 0) {
	            message.addRecipients(Message.RecipientType.TO, recipients);
	        } else {
	            LOG.warn("No recipients indicated");
	        }

	        // CC Address
	        if (ccRecipients != null && ccRecipients.length > 0) {
	            message.addRecipients(Message.RecipientType.CC, ccRecipients);
	        }

	        // BCC Address
	        if (bccRecipients != null && bccRecipients.length > 0) {
	            message.addRecipients(Message.RecipientType.BCC, bccRecipients);
	        }

	        // The Subject
	        message.setSubject(subject);
	        if (subject == null || "".equals(subject)) {
	            LOG.warn("Empty subject being sent");
	        }

	        // Now the message body.
	        if (htmlMessage) {
	            prepareHtmlMessage(messageBody, message);
	        } else {
	            message.setText(messageBody);
	            if (messageBody == null || "".equals(messageBody)) {
	                LOG.warn("Empty message body being sent.");
	            }
	        }

	        // Finally, send the message!
	        try {
	        	mailSender.send(message);
	        }
	        catch (MailException me) {
	        	LOG.warn(me.getMessage());
	        }
	    }

	    /**
	     * Send a message to the designated recipient with the specified subject and
	     * message. This is a convenience class for simple message addressing
	     * 
	     * @param recipient
	     *            the email address to which the message is sent
	     * @param subject
	     *            subject for the message
	     * @param messageBody
	     *            body of the message to to be sent
	     * @param ccRecipient
	     *            email address of a cc recipient
	     */
	    public void sendMessage(String sender, String recipient, String subject, String messageBody, boolean htmlMessage) throws AddressException, MessagingException {
	        final Address[] NO_RECIPIENTS = null;
	        Address[] recipients = { new InternetAddress(recipient) };
	        sendMessage(sender, recipients, subject, messageBody, NO_RECIPIENTS, NO_RECIPIENTS, htmlMessage);
	    }

	    private void prepareHtmlMessage(String messageText, Message message) throws MessagingException {
	        message.setDataHandler(new DataHandler(new ByteArrayDataSource(messageText, "text/html")));
	    }
}
