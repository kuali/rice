/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.mail.MailMessage;
import org.kuali.rice.kns.service.MailService;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailServiceImpl implements MailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MailServiceImpl.class);

    private MailSender mailSender;
    private String batchMailingList;
    private String incidentMailingList;
    
    /**
     * 
     */
    public MailServiceImpl() {
        super();
    }

    public void sendMessage(MailMessage message) throws InvalidAddressException {
        LOG.debug("sendMessage() started");

        // Send email
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo( (String[])message.getToAddresses().toArray(new String[message.getToAddresses().size()]) );
        smm.setBcc( (String[])message.getBccAddresses().toArray(new String[message.getBccAddresses().size()]) );
        smm.setCc( (String[])message.getCcAddresses().toArray(new String[message.getCcAddresses().size()]) );
        smm.setSubject(message.getSubject());
        smm.setText(message.getMessage());
        smm.setFrom(message.getFromAddress());

        try {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug( "About to send message: " + smm.toString() );
        	}
            mailSender.send(smm);
        }
        catch (MailException e) {
            throw new InvalidAddressException(e);
        }
    }

    public void setMailSender(MailSender ms) {
        mailSender = ms;
    }

    /**
     * Sets the batchMailingList attribute value.
     * @param batchMailingList The batchMailingList to set.
     */
    public void setBatchMailingList(String batchMailingList) {
        this.batchMailingList = batchMailingList;
    }

    /**
     * @see org.kuali.rice.kns.service.MailService#getBatchMailingList()
     */
    public String getBatchMailingList() {
        return batchMailingList;
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
