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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.entity.KimEntityEmail;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityEmailInfo extends KimDefaultableInfo implements KimEntityEmail {

	private static final long serialVersionUID = 1L;

	protected String entityEmailId = "";
	protected String entityTypeCode = "";
	protected String emailTypeCode = "";
	protected String emailAddress = "";
	protected String emailAddressUnmasked = "";
	
	protected boolean suppressEmail = false;
	
	/**
	 * 
	 */
	public KimEntityEmailInfo() {
		super();
		active = true;
	}
	
	/**
	 * 
	 */
	public KimEntityEmailInfo( KimEntityEmail email ) {
		this();
		if ( email != null ) {
			this.entityEmailId = unNullify( email.getEntityEmailId() );
			this.entityTypeCode = unNullify( email.getEntityTypeCode() );
			this.emailTypeCode = unNullify( email.getEmailTypeCode() );
			this.emailAddress = unNullify( email.getEmailAddress() );
			this.emailAddressUnmasked = unNullify( email.getEmailAddressUnmasked() );
			this.dflt = email.isDefault();
			this.active = email.isActive();
			this.suppressEmail = email.isSuppressEmail();
		}
	}

	/**
	 * @return the entityEmailId
	 */
	public String getEntityEmailId() {
		return unNullify(this.entityEmailId);
	}

	/**
	 * @param entityEmailId the entityEmailId to set
	 */
	public void setEntityEmailId(String entityEmailId) {
		this.entityEmailId = entityEmailId;
	}

	/**
	 * @return the entityTypeCode
	 */
	public String getEntityTypeCode() {
		return unNullify(this.entityTypeCode);
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * @return the emailTypeCode
	 */
	public String getEmailTypeCode() {
		return unNullify(this.emailTypeCode);
	}

	/**
	 * @param emailTypeCode the emailTypeCode to set
	 */
	public void setEmailTypeCode(String emailTypeCode) {
		this.emailTypeCode = emailTypeCode;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return unNullify(this.emailAddress);
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the emailAddressUnmasked
	 */
	public String getEmailAddressUnmasked() {
		return unNullify(this.emailAddressUnmasked);
	}

	/**
	 * @param emailAddressUnmasked the emailAddressUnmasked to set
	 */
	public void setEmailAddressUnmasked(String emailAddressUnmasked) {
		this.emailAddressUnmasked = emailAddressUnmasked;
	}

	/**
	 * @return the suppressEmail
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
