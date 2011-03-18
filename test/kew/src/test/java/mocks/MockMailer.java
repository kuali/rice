/*
 * Copyright 2010 The Kuali Foundation
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
package mocks;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kuali.rice.core.mail.Mailer;

public class MockMailer extends Mailer{

	/* (non-Javadoc)
	 * @see org.kuali.rice.kew.mail.Mailer#sendMessage(java.lang.String, javax.mail.Address[], java.lang.String, java.lang.String, javax.mail.Address[], javax.mail.Address[], boolean)
	 */
	@Override
	public void sendMessage(String sender, Address[] recipients,
			String subject, String messageBody, Address[] ccRecipients,
			Address[] bccRecipients, boolean htmlMessage)
			throws AddressException, MessagingException {
		
		String toValue = "";
		for(Address a: recipients){
			toValue += a.toString();
		}
		//String toValue = (recipients == null) ? "" : recipients.toString();
        String fromValue = (sender == null) ? "" : sender;
        String subjectValue = (subject == null) ? "" : subject;
        String bodyValue = (messageBody == null) ? "" : messageBody;
        LOG.info("\nWILL NOT send e-mail message with to '" + toValue + "'... \nfrom '" + fromValue + "'... \nsubject '" + subjectValue + "'... and \nbody '" + bodyValue);
	}
}
