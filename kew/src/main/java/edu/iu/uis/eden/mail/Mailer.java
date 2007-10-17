/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.mail;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.iu.uis.eden.util.ByteArrayDataSource;

/**
 * Maintains a Java Mail session and can be used for sending emails.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Mailer {

	    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

	    private Properties configProperties;
	    private Authenticator authenticator;

	    private Session currentSession;

	    /**
	     * Create a AuthenticatedMailer with the default values for authenticated
	     * use of mail-relay.iu.edu.
	     * 
	     * The Kerberos principle and and password should be a system principle
	     * rather than a user principle.
	     * 
	     * @param senderAddress
	     *            return address for the mail being sent
	     * @param kbPrincipleName
	     *            kerberos principle name used to authenticate to mail-relay
	     * @param kbPrinciplePassword
	     *            kerberos principle password used to authenticate to mail-relay
	     */
	    public Mailer(Properties configProperties, Authenticator authenticator) {
	    	this.configProperties = configProperties;
	    	this.authenticator = authenticator;
	    }
	    
	    public Mailer(Properties configProperties) {
	    	this(configProperties, null);
	    }
	    
	    public Mailer(Properties configProperties, String username, String password) {
	    	this(configProperties, new SimpleAuthenticator(username, password));
	    }

	    /**
	     * A method to configure the Mailer properties. This class will default to a
	     * set of properties but this method allows the calling program to set the
	     * specific properties.
	     * 
	     * @param mailConfigProperties
	     *            Proeprties object containing configuration information
	     */
	    public void setConfig(Properties configProperties) {
	        this.configProperties = configProperties;
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
	    	Session session = getCurrentSession();
	    	session.setDebug(LOG.isDebugEnabled());
	        Message message = new MimeMessage(session);

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
	        Transport.send(message);

	    }

	    /**
	     * Send a message to the designated recipient with the specified subject and
	     * message. This is a convience class for simple message addressing
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

	    /**
	     * @return current properties used to configure the mail session
	     */
	    public Properties getConfig() {
	        return configProperties;
	    }
	    
	    public Authenticator getAuthenticator() {
	    	return authenticator;
	    }

	    /**
	     * This allows direct access to the Mail Session. While this offers more
	     * flexibility, the AuthenticatedMailer will no longer be responsible for
	     * management of the this session.
	     * 
	     * @return get the current session. If current session is null it creates a
	     *         new one.
	     */
	    public Session getCurrentSession() throws NoSuchProviderException {
	        if (this.currentSession == null || !this.currentSession.getTransport().isConnected()) {
	            this.currentSession = Session.getInstance(configProperties, authenticator);
	        }
	        return currentSession;
	    }

	    private void prepareHtmlMessage(String messageText, Message message) throws MessagingException {
	        message.setDataHandler(new DataHandler(new ByteArrayDataSource(messageText, "text/html")));
	    }
	    
	    /**
	     * SimpleAuthenticator is used to do simple authentication when the SMTP
	     * server requires it.
	     */
	    private static class SimpleAuthenticator extends javax.mail.Authenticator {
	    	
	    	private final PasswordAuthentication passwordAuthentication;

	        private SimpleAuthenticator(String username, String password) {
	        	this.passwordAuthentication = new PasswordAuthentication(username, password);
	        }

	        public PasswordAuthentication getPasswordAuthentication() {
	            return passwordAuthentication;
	        }
	        
	    }
	
}
