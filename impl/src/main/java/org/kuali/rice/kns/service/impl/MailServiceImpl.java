/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import javax.mail.MessagingException;

import org.kuali.rice.core.mail.MailMessage;
import org.kuali.rice.core.mail.Mailer;
import org.kuali.rice.kns.mail.InvalidAddressException;
import org.kuali.rice.kns.service.MailService;

public class MailServiceImpl implements MailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MailServiceImpl.class);

    private String batchMailingList;
    private Mailer mailer;
    
    /**
     * The injected Mailer.
     */
    public void setMailer(Mailer mailer) {
    	this.mailer = mailer;
    }
    
    /**
     * 
     */
    public MailServiceImpl() {
        super();
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
	 * This overridden method ...
	 * @throws MessagingException 
	 * 
	 * @see org.kuali.rice.kns.service.MailService#sendMessage(org.kuali.rice.core.mail.MailMessage)
	 */
	@Override
	public void sendMessage(MailMessage message) throws InvalidAddressException, MessagingException {
		mailer.sendEmail(message);		
	}
}
