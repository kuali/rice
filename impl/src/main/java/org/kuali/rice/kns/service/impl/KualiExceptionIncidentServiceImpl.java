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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.exception.ExceptionIncident;
import org.kuali.rice.kns.exception.KualiException;
import org.kuali.rice.kns.exception.KualiExceptionIncident;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.KualiExceptionIncidentService;
import org.kuali.rice.kns.service.MailService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a basic implementation of the KualiReporterService. Currently, it only has
 * a mail service as reporting mechanism.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KualiExceptionIncidentServiceImpl implements KualiExceptionIncidentService {
    private Logger LOG=Logger.getLogger(KualiExceptionIncidentServiceImpl.class);
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
     * A mail service for sending report.
     */
    private MailService mailService;
    /**
     * An email template is used to construct an email to be sent by the mail service.
     */
    private MailMessage messageTemplate;
    /**
     * List of recognized Kuali exceptions in classname. The caught exception not in this
     * list is consider as generic system exception.
     */
    private List<String> kualiExceptionNames;
    /**
     * Id of a Spring Bean defining list of addtional recognized Kuali exceptions in
     * classname. This is to be included with the kualiExceptionNames. Note: The reason
     * for this parameter is allowing injection of additional exception names beside the
     * provided default list in kualiExceptionNames parameter.
     */
    private String additionalExceptionNameList;

    /**
     * This mails the report using the mail service from the mail template.
     * 
     * @see org.kuali.rice.kns.service.KualiExceptionIncidentService#emailReport(java.lang.String, java.lang.String)
     */
    public void emailReport(String subject, String message) throws Exception {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s;%s",
                    (subject==null)?"null":subject.toString(),
                    (message==null)?"null":message.toString());
            LOG.trace(lm);
        }
        
        if (mailService == null) {
            String errMessage="MailService ?";
            LOG.fatal(errMessage);
            throw new KualiException(errMessage);
        }
        
        // Send mail
        MailMessage msg=createMailMessage(subject, message);
        mailService.sendMessage(msg);

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
            throw new KualiException(String.format(
                    "%s.templateMessage is null or not set",
                    this.getClass().getName()));
        }
        
        // Copy input message reference for creating an instance of mail message
        MailMessage msg=new MailMessage();
        msg.setBccAddresses(messageTemplate.getBccAddresses());
        msg.setCcAddresses(messageTemplate.getCcAddresses());
        msg.setFromAddress(messageTemplate.getFromAddress());
        // First check if message template already define mailing list
        Set emails=messageTemplate.getToAddresses();
        if (emails == null || emails.isEmpty()) {
            String mailingList=KNSServiceLocator.getKualiConfigurationService().
            getPropertyString(REPORT_MAIL_LIST);
            if (mailingList == null || mailingList.trim().length() == 0) {
                String em=REPORT_MAIL_LIST+"?";
                LOG.error(em);
                throw new KualiException(em);
            } else {
                msg.setToAddresses(new HashSet<String>(split(mailingList,
                        KNSConstants.FIELD_CONVERSIONS_SEPERATOR)));
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
     * @see org.kuali.rice.kns.service.KualiExceptionIncidentService#report(org.kuali.rice.kns.exception.KualiExceptionIncident)
     */
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
     * @return the mailService
     */
    public final MailService getMailService() {
        return this.mailService;
    }

    /**
     * @param mailService the mailService to set
     */
    public final void setMailService(MailService mailService) {
        this.mailService = mailService;
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
     * @see org.kuali.rice.kns.service.KualiExceptionIncidentService#getExceptionIncident(
     * java.lang.Exception,java.util.Map)
     */
    @SuppressWarnings("unchecked")
    public KualiExceptionIncident getExceptionIncident(Exception exception,
            Map<String, String> properties) {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s;%s", exception.getMessage(),
                    properties.toString());
            LOG.trace(lm);
        }
        
        // Set up list of Kuali exception names
        List<String> exceptionNames=new ArrayList<String>();
        if (kualiExceptionNames != null) {
            exceptionNames.addAll(kualiExceptionNames);
        }
        if (additionalExceptionNameList != null &&
            additionalExceptionNameList.length() > 0 &&
            KNSServiceLocator.isSingleton(additionalExceptionNameList)) {
            List<String> names=KNSServiceLocator.getNervousSystemContextBean (
                    List.class, additionalExceptionNameList);
            exceptionNames.addAll(names);
        }
        KualiExceptionIncident ei=new ExceptionIncident(exception,
                exceptionNames,
                properties);
  
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
     * @see org.kuali.rice.kns.service.KualiExceptionIncidentService#getExceptionIncident(java.util.Map)
     */
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
     * @return the kualiExceptionNames
     */
    public final List<String> getKualiExceptionNames() {
        return this.kualiExceptionNames;
    }

    /**
     * @param kualiExceptionNames the kualiExceptionNames to set
     */
    public final void setKualiExceptionNames(List<String> kualiExceptionNames) {
        this.kualiExceptionNames = kualiExceptionNames;
    }

    /**
     * @return the additionalExceptionNameList
     */
    public final String getAdditionalExceptionNameList() {
        return this.additionalExceptionNameList;
    }

    /**
     * @param additionalExceptionNameList the additionalExceptionNameList to set
     */
    public final void setAdditionalExceptionNameList(String additionalExceptionNameList) {
        this.additionalExceptionNameList = additionalExceptionNameList;
    }

}
