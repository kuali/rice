/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import org.kuali.rice.kim.bo.entity.KimEntityEmail;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityEmailInfo extends KimDefaultableInfo implements KimEntityEmail {

	private static final long serialVersionUID = 1L;

	protected String entityEmailId;
	protected String entityTypeCode;
	protected String emailTypeCode;
	protected String emailAddress;
	protected String emailAddressUnmasked;
	
	protected boolean suppressEmail = false;
	
	/**
	 * construct an empty {@link KimEntityEmailInfo}
	 */
	public KimEntityEmailInfo() {
		super();
		active = true;
	}
	
	/**
	 * construct a {@link KimEntityEmailInfo} derived from the give {@link KimEntityEmail}
	 */
	public KimEntityEmailInfo( KimEntityEmail email ) {
		this();
		if ( email != null ) {
			this.entityEmailId = email.getEntityEmailId();
			this.entityTypeCode = email.getEntityTypeCode();
			this.emailTypeCode = email.getEmailTypeCode();
			this.emailAddress = email.getEmailAddress();
			this.emailAddressUnmasked = email.getEmailAddressUnmasked();
			this.defaultValue = email.isDefaultValue();
			this.active = email.isActive();
			this.suppressEmail = email.isSuppressEmail();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEntityEmailId()
	 */
	public String getEntityEmailId() {
		return entityEmailId;
	}

	/**
	 * @param entityEmailId the entityEmailId to set
	 */
	public void setEntityEmailId(String entityEmailId) {
		this.entityEmailId = entityEmailId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailTypeCode()
	 */
	public String getEmailTypeCode() {
		return emailTypeCode;
	}

	/**
	 * @param emailTypeCode the emailTypeCode to set
	 */
	public void setEmailTypeCode(String emailTypeCode) {
		this.emailTypeCode = emailTypeCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailAddress()
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailAddressUnmasked()
	 */
	public String getEmailAddressUnmasked() {
		return emailAddressUnmasked;
	}

	/**
	 * @param emailAddressUnmasked the emailAddressUnmasked to set
	 */
	public void setEmailAddressUnmasked(String emailAddressUnmasked) {
		this.emailAddressUnmasked = emailAddressUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#isSuppressEmail()
	 */
	public boolean isSuppressEmail() {
		return this.suppressEmail;
	}

	/**
	 * @param suppressEmail the suppressEmail to set
	 */
	public void setSuppressEmail(boolean suppressEmail) {
		this.suppressEmail = suppressEmail;
	}
	
}
