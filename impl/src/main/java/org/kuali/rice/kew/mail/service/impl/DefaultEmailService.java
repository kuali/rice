/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.mail.service.impl;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.mail.EmailBody;
import org.kuali.rice.kew.mail.EmailFrom;
import org.kuali.rice.kew.mail.EmailSubject;
import org.kuali.rice.kew.mail.EmailTo;
import org.kuali.rice.kew.mail.Mailer;
import org.kuali.rice.kew.mail.service.EmailService;
import org.springframework.beans.factory.InitializingBean;



import org.kuali.rice.kew.mail.service.impl.EmailBcList;
import org.kuali.rice.kew.mail.service.impl.EmailCcList;
import org.kuali.rice.kew.mail.service.impl.EmailToList;


public class DefaultEmailService implements EmailService, InitializingBean {
    private static final Logger LOG = Logger.getLogger(DefaultEmailService.class);

	public static final String USERNAME_PROPERTY = "mail.smtp.username";
	public static final String PASSWORD_PROPERTY = "mail.smtp.password";
	
	protected Mailer mailer;
	
	/**
	 * This method is called by Spring on initialization.  
	 */
	public void afterPropertiesSet() throws Exception {
		if(getMailer() == null){
			mailer = createMailer();
		}
	}

	/**
	 * Retrieves the Properties used to configure the Mailer.  The property names configured in the 
	 * Workflow configuration should match those of Java Mail.
	 * @return
	 */
	private Properties getConfigProperties() {
		return ConfigContext.getCurrentContextConfig().getProperties();
	}
	
	public void sendEmail(EmailFrom from, EmailTo to, EmailSubject subject, EmailBody body, boolean htmlMessage) {
        if (to.getToAddress() == null) {
            LOG.warn("No To address specified; refraining from sending mail");
            return;
        }
		try {
			getMailer().sendMessage(
                        from.getFromAddress(),
                        to.getToAddress(),
                        subject.getSubject(),
                        body.getBody(),
                        htmlMessage);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}


	public void sendEmail(EmailFrom from, EmailToList to, EmailSubject subject, EmailBody body, EmailCcList cc, EmailBcList bc, boolean htmlMessage) {
		if(getMailer() == null){
			try {
				mailer = createMailer();
			} catch (Exception e) {
				LOG.error("Error initializing mailer for multi-recipient email.", e);
			}
		}
		if (to.getToAddresses().isEmpty()) {
			LOG.error("List of To addresses must contain at least one entry.");
		} else {
			try {
				getMailer().sendMessage(
						from.getFromAddress(), 
						to.getToAddressesAsAddressArray(), 
						subject.getSubject(), 
						body.getBody(), 
						(cc == null ? null : cc.getToAddressesAsAddressArray()), 
						(bc == null ? null : bc.getToAddressesAsAddressArray()), 
						htmlMessage);
			} catch (Exception e) {
				LOG.error("Error sending email to multiple recipients.", e);
			}
		}
	}

	/**
	 * @return the mailer
	 */
	public Mailer getMailer() {
		return mailer;
	}

	/**
	 * @param mailer the mailer to set
	 * 
	 * Note: If you want to test locally and not have a smtp server, you can, via spring,
	 * inject the MockMailer. That class just prints the email via Log.info
	 */
	public void setMailer(Mailer mailer) {
		this.mailer = mailer;
	}
	
	protected Mailer createMailer(){
		Mailer cMailer = null;
		String username = ConfigContext.getCurrentContextConfig().getProperty(USERNAME_PROPERTY);
		String password = ConfigContext.getCurrentContextConfig().getProperty(PASSWORD_PROPERTY);
		if (username != null && password != null) {
			cMailer = new Mailer(getConfigProperties(), username, password);
			LOG.info("Rice Mailer being used. Username and Pass were found");
		} else {
			cMailer = new Mailer(getConfigProperties());
			LOG.info("Rice Mailer being used. Username and Pass were not found");
		}
		return cMailer;
	}

}
