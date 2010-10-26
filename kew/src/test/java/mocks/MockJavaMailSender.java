/*
 * Copyright 2007-2010 The Kuali Foundation
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

import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * This is a description of what this class does - g don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class MockJavaMailSender implements JavaMailSender{

	 protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.MailSender#send(org.springframework.mail.SimpleMailMessage)
	 */
	public void send(SimpleMailMessage arg0) throws MailException {				
		LOG.info("" + arg0.toString());		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.MailSender#send(org.springframework.mail.SimpleMailMessage[])
	 */
	public void send(SimpleMailMessage[] arg0) throws MailException {
		// TODO g - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.javamail.JavaMailSender#createMimeMessage()
	 */
	public MimeMessage createMimeMessage() {
		// TODO g - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.javamail.JavaMailSender#createMimeMessage(java.io.InputStream)
	 */
	public MimeMessage createMimeMessage(InputStream arg0) throws MailException {
		// TODO g - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.javamail.JavaMailSender#send(javax.mail.internet.MimeMessage)
	 */
	public void send(MimeMessage arg0) throws MailException {
		// TODO g - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.javamail.JavaMailSender#send(javax.mail.internet.MimeMessage[])
	 */
	public void send(MimeMessage[] arg0) throws MailException {
		// TODO g - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.javamail.JavaMailSender#send(org.springframework.mail.javamail.MimeMessagePreparator)
	 */
	public void send(MimeMessagePreparator arg0) throws MailException {
		// TODO g - THIS METHOD NEEDS JAVADOCS
		
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.mail.javamail.JavaMailSender#send(org.springframework.mail.javamail.MimeMessagePreparator[])
	 */
	public void send(MimeMessagePreparator[] arg0) throws MailException {
		// TODO g - THIS METHOD NEEDS JAVADOCS
		
	}

	
	
	
	
}
