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
package org.kuali.rice.krad.service.impl;

import org.apache.log4j.Logger;
import org.kuali.rice.core.mail.MailMessage;
import org.kuali.rice.core.mail.Mailer;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.krad.exception.ExceptionIncident;
import org.kuali.rice.krad.exception.KualiExceptionIncident;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KualiExceptionIncidentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a basic implementation of the KualiReporterService. Currently, it only has
 * a mail service as reporting mechanism.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiExceptionIncidentServiceImpl implements KualiExceptionIncidentService {
    private Logger LOG=Logger.getLogger(KualiExceptionIncidentServiceImpl.class);
    
    /**
     * An list to send incident emails to.
     */
    private String incidentMailingList;
    
    /**
     * This property must be defined in the base configuration file for specifying
     * the mailing list for the report to be sent.
     * <p>Example:
     * <code>
     * <param name="KualiReporterServiceImpl.REPORT_MAIL_LIST">a@y,b@z</param>
     * </code>
     */
    public static final String REPORT_MAIL_LIST=String.format(
            "%s.REPORT_MAIL_LIST",KualiExceptionIncidentServiceImpl.class.getSimpleName());
    /**
     * A Mailer for sending report.
     */
    private Mailer mailer;
    
    /**
     * The injected Mailer.
     * @param mailService the mailService to set
     */
    public final void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }
    
    /**
     * An email template is used to construct an email to be sent by the mail service.
     */
    private MailMessage messageTemplate;

    /**
     * This mails the report using the mail service from the mail template.
     * 
     * @see org.kuali.rice.krad.service.KualiExceptionIncidentService#emailReport(java.lang.String, java.lang.String)
     */
    @Override
	public void emailReport(String subject, String message) throws Exception {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s;%s",
                    (subject==null)?"null":subject.toString(),
                    (message==null)?"null":message.toString());
            LOG.trace(lm);
        }
        
        if (mailer == null) {
            String errorMessage = "mailer property of KualiExceptionIncidentServiceImpl is null";
            LOG.fatal(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        
        // Send mail
        MailMessage msg=createMailMessage(subject, message);
        mailer.sendEmail(msg);

        if (LOG.isTraceEnabled()) {
            LOG.trace("EXIT");
        }
    }

    /**
     * This method create an instance of MailMessage from the inputs using the given
     * template.
     * 
     * @param subject
     * @param message
     * @return
     * @exception
     */
    @SuppressWarnings("unchecked")
    private MailMessage createMailMessage(String subject, String message)
                throws Exception{
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s",
                    (subject==null)?"null":subject.toString(),
                    (message==null)?"null":message.toString());
            LOG.trace(lm);
        }
        
        if (messageTemplate == null) {
            throw new IllegalStateException(String.format(
                    "%s.templateMessage is null or not set",
                    this.getClass().getName()));
        }
        
        // Copy input message reference for creating an instance of mail message
        MailMessage msg=new MailMessage();
        
        Person actualUser = GlobalVariables.getUserSession().getPerson();
        String fromEmail = actualUser.getEmailAddress();
        if ((fromEmail != null) && (fromEmail != "")) {
        	msg.setFromAddress(fromEmail);
    	} else {
        	msg.setFromAddress(messageTemplate.getFromAddress());
    	}
    	
        msg.setBccAddresses(messageTemplate.getBccAddresses());
        msg.setCcAddresses(messageTemplate.getCcAddresses());
        // First check if message template already define mailing list
        Set emails=messageTemplate.getToAddresses();
        if (emails == null || emails.isEmpty()) {
            String mailingList= KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                    REPORT_MAIL_LIST);
            if (mailingList == null || mailingList.trim().length() == 0) {
                String em=REPORT_MAIL_LIST+" is not set or messageTemplate does not have ToAddresses already set.";
                LOG.error(em);
                throw new IllegalStateException(em);
            } else {
                msg.setToAddresses(new HashSet<String>(split(mailingList,
                        KRADConstants.FIELD_CONVERSIONS_SEPARATOR)));
            }
        } else {
            msg.setToAddresses(emails);
        }

        // Set mail message subject
        msg.setSubject((subject==null)?"":subject);

        // Set mail message body
        msg.setMessage((message==null)?"":message);
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s",
                    (msg==null)?"null":msg.toString());
            LOG.trace(lm);
        }

        return msg;
    }

    /**
     * This overridden method send email to the specified list of addresses.
     * 
     * @see org.kuali.rice.krad.service.KualiExceptionIncidentService#report(org.kuali.rice.krad.exception.KualiExceptionIncident)
     */
    @Override
	public void report(KualiExceptionIncident exceptionIncident) throws Exception {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s",
                    (exceptionIncident==null)?"null":exceptionIncident.toString());
            LOG.trace(lm);
        }
        
        emailReport(
                exceptionIncident.getProperty(
                        KualiExceptionIncident.EXCEPTION_REPORT_SUBJECT),
                exceptionIncident.getProperty(
                        KualiExceptionIncident.EXCEPTION_REPORT_MESSAGE));
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT");
            LOG.trace(lm);
        }
        
    }

    /**
     * This method first separate a composite string of the format
     * "string token string".
     * <p>Example: 1,2,a,b where ',' is the token
     * 
     * @param s
     * @param token
     * @return
     */
    public List<String> split(String s, String token) {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s;%s", s, token);
            LOG.trace(lm);
        }
                
        String[] sarray=s.split(token);
        List<String> list=new ArrayList<String>();
        for (int i=0; i<sarray.length && sarray[i].length() > 0; i++) {
            list.add(sarray[i]);
        }
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", list.toString());
            LOG.trace(lm);
        }
        
        return list;
    }

    /**
     * @return the messageTemplate
     */
    public final MailMessage getMessageTemplate() {
        return this.messageTemplate;
    }

    /**
     * @param messageTemplate the messageTemplate to set
     */
    public final void setMessageTemplate(MailMessage messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    /**
     * This overridden method create an instance of the KualiExceptionIncident.
     * 
     * @see org.kuali.rice.krad.service.KualiExceptionIncidentService#getExceptionIncident(
     * java.lang.Exception,java.util.Map)
     */
    @Override
	public KualiExceptionIncident getExceptionIncident(Exception exception,
            Map<String, String> properties) {
    	if ( exception == null ) {
    		return getExceptionIncident(properties);
    	}
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s;%s", exception.getMessage(),
                    properties.toString());
            LOG.trace(lm);
        }
        
        KualiExceptionIncident ei=new ExceptionIncident(exception, properties);
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", ei.toProperties().toString());
            LOG.trace(lm);
        }
                
        return ei;
    }

    /**
     * This overridden method create an instance of ExceptionIncident from list of
     * name-value pairs as exception incident information.
     * 
     * @see org.kuali.rice.krad.service.KualiExceptionIncidentService#getExceptionIncident(java.util.Map)
     */
    @Override
	public KualiExceptionIncident getExceptionIncident(Map<String, String> properties) {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s", properties.toString());
            LOG.trace(lm);
        }
        
        ExceptionIncident ei=new ExceptionIncident(properties);
                
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", ei.toProperties().toString());
            LOG.trace(lm);
        }
                
        return ei;
    }
    
	/**
	 * @return the incidentMailingList
	 */
	public String getIncidentMailingList() {
		return this.incidentMailingList;
	}

	/**
	 * @param incidentMailingList the incidentMailingList to set
	 */
	public void setIncidentMailingList(String incidentMailingList) {
		this.incidentMailingList = incidentMailingList;
	}

}
