/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import org.apache.log4j.Logger;
import org.kuali.rice.core.Core;
import org.springframework.beans.factory.InitializingBean;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

public class DefaultEmailService implements EmailService, InitializingBean {
    private static final Logger LOG = Logger.getLogger(DefaultEmailService.class);

	public static final String USERNAME_PROPERTY = "mail.smtp.username";
	public static final String PASSWORD_PROPERTY = "mail.smtp.password";
	
	private Mailer mailer;
	
	public void afterPropertiesSet() throws Exception {
		String username = Core.getCurrentContextConfig().getProperty(USERNAME_PROPERTY);
		String password = Core.getCurrentContextConfig().getProperty(PASSWORD_PROPERTY);
		if (username != null && password != null) {
			mailer = new Mailer(getConfigProperties(), username, password);
		} else {
			mailer = new Mailer(getConfigProperties());
		}
	}

	/**
	 * Retrieves the Properties used to configure the Mailer.  The property names configured in the 
	 * Workflow configuration should match those of Java Mail.
	 * @return
	 */
	private Properties getConfigProperties() {
		return Core.getCurrentContextConfig().getProperties();
	}
	
	public void sendEmail(EmailFrom from, EmailTo to, EmailSubject subject, EmailBody body, boolean htmlMessage) {
        if (to.getToAddress() == null) {
            LOG.warn("No To address specified; refraining from sending mail");
            return;
        }
		try {
			mailer.sendMessage(
                        from.getFromAddress(),
                        to.getToAddress(),
                        subject.getSubject(),
                        body.getBody(),
                        htmlMessage);
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
	}
	
}
