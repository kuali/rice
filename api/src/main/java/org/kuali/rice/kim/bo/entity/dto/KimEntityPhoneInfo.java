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

import org.kuali.rice.kim.bo.entity.KimEntityPhone;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityPhoneInfo extends KimDefaultableInfo implements KimEntityPhone {
	
	private static final long serialVersionUID = 1L;

	protected String entityPhoneId = "";
	protected String entityTypeCode = "";
	protected String phoneTypeCode = "";
	protected String phoneNumber = "";
	protected String extensionNumber = "";
	protected String countryCode = "";
	
	protected String phoneNumberUnmasked;
    protected String extensionNumberUnmasked;
    protected String countryCodeUnmasked;
    
    protected String formattedPhoneNumber;
	protected String formattedPhoneNumberUnmasked;

	protected boolean suppressPhone = false;
	

	/**
	 * 
	 */
	public KimEntityPhoneInfo() {
		super();
		active = true;
	}
	
	/**
	 * 
	 */
	public KimEntityPhoneInfo( KimEntityPhone phone ) {
		this();
		if ( phone != null ) {
		    this.entityPhoneId = unNullify( phone.getEntityPhoneId() );
		    this.entityTypeCode = unNullify( phone.getEntityTypeCode() );
		    this.phoneTypeCode = unNullify( phone.getPhoneTypeCode() );
		    this.phoneNumber = unNullify( phone.getPhoneNumber() );
		    this.extensionNumber = unNullify( phone.getExtensionNumber() );
		    this.countryCode = unNullify( phone.getCountryCodeUnmasked() );
		    this.dflt = phone.isDefault();
		    this.active = phone.isActive();
		    this.suppressPhone = phone.isSuppressPhone();
		    
		    this.phoneNumberUnmasked = unNullify(phone.getPhoneNumberUnmasked());
		    this.extensionNumberUnmasked = unNullify(phone.getExtensionNumberUnmasked());
		    this.countryCodeUnmasked = unNullify(phone.getCountryCodeUnmasked());
		    this.formattedPhoneNumber = unNullify(phone.getFormattedPhoneNumber());
		    this.formattedPhoneNumberUnmasked = unNullify(phone.getFormattedPhoneNumberUnmasked());
		}
	}

	/**
	 * @return the entityPhoneId
	 */
	public String getEntityPhoneId() {
		return this.entityPhoneId;
	}

	/**
	 * @param entityPhoneId the entityPhoneId to set
	 */
	public void setEntityPhoneId(String entityPhoneId) {
		this.entityPhoneId = entityPhoneId;
	}

	/**
	 * @return the entityTypeCode
	 */
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}

	/**
	 * @param entityTypeCode the entityTypeCode to set
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	/**
	 * @return the phoneTypeCode
	 */
	public String getPhoneTypeCode() {
		return this.phoneTypeCode;
	}

	/**
	 * @param phoneTypeCode the phoneTypeCode to set
	 */
	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the extensionNumber
	 */
	public String getExtensionNumber() {
		return this.extensionNumber;
	}

	/**
	 * @param extensionNumber the extensionNumber to set
	 */
	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	/**
	 * @return the countryCode
	 */
	public String getCountryCode() {
		return this.countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @return the phoneNumberUnmasked
	 */
	public String getPhoneNumberUnmasked() {
		return this.phoneNumberUnmasked;
	}

	/**
	 * @param phoneNumberUnmasked the phoneNumberUnmasked to set
	 */
	public void setPhoneNumberUnmasked(String phoneNumberUnmasked) {
		this.phoneNumberUnmasked = phoneNumberUnmasked;
	}

	/**
	 * @return the extensionNumberUnmasked
	 */
	public String getExtensionNumberUnmasked() {
		return this.extensionNumberUnmasked;
	}

	/**
	 * @param extensionNumberUnmasked the extensionNumberUnmasked to set
	 */
	public void setExtensionNumberUnmasked(String extensionNumberUnmasked) {
		this.extensionNumberUnmasked = extensionNumberUnmasked;
	}

	/**
	 * @return the countryCodeUnmasked
	 */
	public String getCountryCodeUnmasked() {
		return this.countryCodeUnmasked;
	}

	/**
	 * @param countryCodeUnmasked the countryCodeUnmasked to set
	 */
	public void setCountryCodeUnmasked(String countryCodeUnmasked) {
		this.countryCodeUnmasked = countryCodeUnmasked;
	}

	/**
	 * @return the formattedPhoneNumber
	 */
	public String getFormattedPhoneNumber() {
		return this.formattedPhoneNumber;
	}

	/**
	 * @param formattedPhoneNumber the formattedPhoneNumber to set
	 */
	public void setFormattedPhoneNumber(String formattedPhoneNumber) {
		this.formattedPhoneNumber = formattedPhoneNumber;
	}

	/**
	 * @return the formattedPhoneNumberUnmasked
	 */
	public String getFormattedPhoneNumberUnmasked() {
		return this.formattedPhoneNumberUnmasked;
	}

	/**
	 * @param formattedPhoneNumberUnmasked the formattedPhoneNumberUnmasked to set
	 */
	public void setFormattedPhoneNumberUnmasked(String formattedPhoneNumberUnmasked) {
		this.formattedPhoneNumberUnmasked = formattedPhoneNumberUnmasked;
	}

	/**
	 * @return the suppressPhone
	 */
	public boolean isSuppressPhone() {
		return this.suppressPhone;
	}

	/**
	 * @param suppressPhone the suppressPhone to set
	 */
	public void setSuppressPhone(boolean suppressPhone) {
		this.suppressPhone = suppressPhone;
	}
	
}
