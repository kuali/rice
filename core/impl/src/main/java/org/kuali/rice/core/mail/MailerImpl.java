/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mail.EmailBcList;
import org.kuali.rice.core.api.mail.EmailBody;
import org.kuali.rice.core.api.mail.EmailCcList;
import org.kuali.rice.core.api.mail.EmailFrom;
import org.kuali.rice.core.api.mail.EmailSubject;
import org.kuali.rice.core.api.mail.EmailTo;
import org.kuali.rice.core.api.mail.EmailToList;
import org.kuali.rice.core.api.mail.MailAttachment;
import org.kuali.rice.core.api.mail.MailMessage;
import org.kuali.rice.core.api.mail.Mailer;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a Java Mail session and is used for sending e-mails.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MailerImpl implements Mailer {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MailerImpl.class);

    private JavaMailSenderImpl mailSender;

    /**
     * @param mailSender The injected Mail Sender.
     */
    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Construct and a send simple email message from a Mail Message.
     *
     * @param message the Mail Message
     * @throws MessagingException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void sendEmail(MailMessage message) throws MessagingException {
        if ((message.getToAddresses() == null || message.getToAddresses().isEmpty())
                && (message.getCcAddresses() == null || message.getCcAddresses().isEmpty())
                && (message.getBccAddresses() == null || message.getBccAddresses().isEmpty())) {
            throw new MessagingException("There is no to, cc, or bcc address specified. Refraining from sending mail.");
        }

        boolean htmlMessage = false;
        List<String> toAddresses = null;
        List<String> ccAddresses = null;
        List<String> bcAddresses = null;

        if (message.getToAddresses() != null && !message.getToAddresses().isEmpty()) {
            toAddresses = new ArrayList<String>();
            toAddresses.addAll(message.getToAddresses());
        }

        if (message.getCcAddresses() != null && !message.getCcAddresses().isEmpty()) {
            ccAddresses = new ArrayList<String>();
            ccAddresses.addAll(message.getCcAddresses());
        }

        if (message.getBccAddresses() != null && !message.getBccAddresses().isEmpty()) {
            bcAddresses = new ArrayList<String>();
            bcAddresses.addAll(message.getBccAddresses());
        }

        sendEmail(new EmailFrom(message.getFromAddress()), (toAddresses == null ? null : new EmailToList(toAddresses)),
                new EmailSubject(message.getSubject()), new EmailBody(message.getMessage()),
                (ccAddresses == null ? null : new EmailCcList(ccAddresses)),
                (bcAddresses == null ? null : new EmailBcList(bcAddresses)), htmlMessage, message.getMailAttachments());
    }

    /**
     * Send an email to a single recipient with the specified subject and message. This is a convenience
     * method for simple message addressing.
     *
     * @param from sender of the message
     * @param to list of addresses to which the message is sent
     * @param subject subject of the message
     * @param body body of the message
     */
    @Override
    public void sendEmail(EmailFrom from, EmailTo to, EmailSubject subject, EmailBody body, boolean htmlMessage) {
        if (to.getToAddress() == null) {
            throw new RuntimeException("No To address specified. Refraining from sending mail.");
        }

        try {
            Address[] singleRecipient = {new InternetAddress(to.getToAddress())};
            sendMessage(from.getFromAddress(), singleRecipient, subject.getSubject(), body.getBody(), null, null,
                    htmlMessage, null);
        } catch (Exception e) {
            LOG.error("sendEmail(): ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Send an email to the given "to", "cc", and "bcc" recipients with the specified subject and message.
     *
     * @param from sender of the message
     * @param to list of addresses to which the message is sent
     * @param subject subject of the message
     * @param body body of the message
     * @param cc list of addresses which are to be cc'd on the message
     * @param bc list of addresses which are to be bcc'd on the message
     * @param htmlMessage indicates if the message should be sent as html
     */
    @Override
    public void sendEmail(EmailFrom from, EmailToList to, EmailSubject subject, EmailBody body, EmailCcList cc,
            EmailBcList bc, boolean htmlMessage) {
        sendEmail(from, to, subject, body, cc, bc, htmlMessage, null);
    }

    /**
     * Send an email to the given "to", "cc", and "bcc" recipients with the specified subject,
     * message, and attachments.
     *
     * @param from sender of the message
     * @param to list of addresses to which the message is sent
     * @param subject subject of the message
     * @param body body of the message
     * @param cc list of addresses which are to be cc'd on the message
     * @param bc list of addresses which are to be bcc'd on the message
     * @param htmlMessage indicates if the message should be sent as html
     * @param mailAttachments attachments to add to the message
     */
    @Override
    public void sendEmail(EmailFrom from, EmailToList to, EmailSubject subject, EmailBody body, EmailCcList cc,
            EmailBcList bc, boolean htmlMessage, List<MailAttachment> mailAttachments) {

        if ((to == null || to.getToAddresses().isEmpty())
                && (cc == null || cc.getCcAddresses().isEmpty())
                && (bc == null || bc.getBcAddresses().isEmpty())) {
            throw new RuntimeException("There is no to, cc, or bcc address specified. Refraining from sending mail.");
        }

        try {
            sendMessage(from.getFromAddress(),
                    (to == null ? null : to.getToAddressesAsAddressArray()),
                    subject.getSubject(), body.getBody(),
                    (cc == null ? null : cc.getToAddressesAsAddressArray()),
                    (bc == null ? null : bc.getToAddressesAsAddressArray()), htmlMessage, mailAttachments);
        } catch (Exception e) {
            LOG.error("sendEmail(): ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Send an email to the given recipients with the specified subject and message.  An attachment may also be
     * added to the email.
     *
     * @param from sender of the message
     * @param to list of addresses to which the message is sent
     * @param subject subject of the message
     * @param messageBody body of the message
     * @param cc list of addresses which are to be cc'd on the message
     * @param bcc list of addresses which are to be bcc'd on the message
     * @param htmlMessage indicates if the message should be sent as html
     * @param mailAttachments attachments to add to the message
     */
    protected void sendMessage(String from, Address[] to, String subject, String messageBody, Address[] cc,
            Address[] bcc, boolean htmlMessage, List<MailAttachment> mailAttachments)
            throws AddressException, MessagingException, MailException {
        MimeMessage message = mailSender.createMimeMessage();

        if ((to == null || to.length == 0) && (cc == null || cc.length == 0) && (bcc == null || bcc.length == 0)) {
            throw new RuntimeException("There is no to, cc, or bcc address specified. Refraining from sending mail.");
        }

        // From Address
        message.setFrom(new InternetAddress(from));

        // To Address(es)
        if (to != null && to.length > 0) {
            message.addRecipients(Message.RecipientType.TO, to);
        }

        // CC Address(es)
        if (cc != null && cc.length > 0) {
            message.addRecipients(Message.RecipientType.CC, cc);
        }

        // BCC Address(es)
        if (bcc != null && bcc.length > 0) {
            message.addRecipients(Message.RecipientType.BCC, bcc);
        }

        // Subject
        message.setSubject(subject);
        if (subject == null || "".equals(subject)) {
            LOG.warn("Empty subject being sent.");
        }

        // Message
        if (mailAttachments == null || mailAttachments.isEmpty()) {
            // no attachments
            if (htmlMessage) {
                prepareHtmlMessage(messageBody, message);
            } else {
                message.setText(messageBody);
                if (messageBody == null || "".equals(messageBody)) {
                    LOG.warn("Empty message body being sent.");
                }
            }
        } else {
            // attachments exist
            Multipart multipart = new MimeMultipart();
            MimeBodyPart body = new MimeBodyPart();

            if (htmlMessage) {
                body.setContent(messageBody, "text/html");
            } else {
                body.setText(messageBody);
            }

            multipart.addBodyPart(body);

            for (MailAttachment mailAttachment : mailAttachments) {
                String fileName = mailAttachment.getFileName();
                String type = mailAttachment.getType();
                byte[] content = mailAttachment.getContent();

                if (content != null && StringUtils.isNotEmpty(fileName) && StringUtils.isNotEmpty(type)) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    ByteArrayDataSource ds = new ByteArrayDataSource(content, type);
                    attachment.setDataHandler(new DataHandler(ds));
                    attachment.setFileName(fileName);
                    multipart.addBodyPart(attachment);
                } else {
                    LOG.warn("Attachment was not complete so it was not attached. FileName: " + fileName
                            + " Type: " + type  + " Content: "  + content);
                }
            }

            message.setContent(multipart);
        }

        // Send the message
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("sendEmail() - Sending message: " + message.toString());
            }
            mailSender.send(message);
        } catch (Exception e) {
            LOG.error("sendEmail() - Error sending email.", e);
            throw new RuntimeException(e);
        }
    }

    protected void prepareHtmlMessage(String messageText, Message message) throws MessagingException {
        try {
            message.setDataHandler(new DataHandler(new ByteArrayDataSource(messageText, "text/html")));
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
