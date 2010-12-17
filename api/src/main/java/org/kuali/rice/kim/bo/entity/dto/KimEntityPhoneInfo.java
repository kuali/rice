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

import org.kuali.rice.kim.bo.entity.KimEntityPhone;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityPhoneInfo extends KimDefaultableInfo implements KimEntityPhone {
	
	private static final long serialVersionUID = 1L;

	protected String entityPhoneId;
	protected String entityTypeCode;
	protected String phoneTypeCode;
	protected String phoneNumber;
	protected String extensionNumber;
	protected String countryCode;
	
	protected String phoneNumberUnmasked;
    protected String extensionNumberUnmasked;
    protected String countryCodeUnmasked;
    
    protected String formattedPhoneNumber;
	protected String formattedPhoneNumberUnmasked;

	protected boolean suppressPhone = false;
	

	/**
	 * constructs an empty KimEntityPhoneInfo
	 */
	public KimEntityPhoneInfo() {
		super();
		active = true;
	}
	
	/**
	 * constructs a {@link KimEntityPhoneInfo} derived from the given {@link KimEntityPhone}
	 */
	public KimEntityPhoneInfo( KimEntityPhone phone ) {
		this();
		if ( phone != null ) {
		    this.entityPhoneId = phone.getEntityPhoneId();
		    this.entityTypeCode = phone.getEntityTypeCode();
		    this.phoneTypeCode = phone.getPhoneTypeCode();
		    this.phoneNumber = phone.getPhoneNumber();
		    this.extensionNumber = phone.getExtensionNumber();
		    this.countryCode = phone.getCountryCodeUnmasked();
		    this.defaultValue = phone.isDefaultValue();
		    this.active = phone.isActive();
		    this.suppressPhone = phone.isSuppressPhone();
		    
		    this.phoneNumberUnmasked = phone.getPhoneNumberUnmasked();
		    this.extensionNumberUnmasked = phone.getExtensionNumberUnmasked();
		    this.countryCodeUnmasked = phone.getCountryCodeUnmasked();
		    this.formattedPhoneNumber = phone.getFormattedPhoneNumber();
		    this.formattedPhoneNumberUnmasked = phone.getFormattedPhoneNumberUnmasked();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getEntityPhoneId()
	 */
	public String getEntityPhoneId() {
		return entityPhoneId;
	}

	/**
	 * @param entityPhoneId the entityPhoneId to set
	 */
	public void setEntityPhoneId(String entityPhoneId) {
		this.entityPhoneId = entityPhoneId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData#getEntityTypeCode()
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
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneTypeCode()
	 */
	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	/**
	 * @param phoneTypeCode the phoneTypeCode to set
	 */
	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneNumber()
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getExtensionNumber()
	 */
	public String getExtensionNumber() {
		return extensionNumber;
	}

	/**
	 * @param extensionNumber the extensionNumber to set
	 */
	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getCountryCode()
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @param countryCode the countryCode to set
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneNumberUnmasked()
	 */
	public String getPhoneNumberUnmasked() {
		return phoneNumberUnmasked;
	}

	/**
	 * @param phoneNumberUnmasked the phoneNumberUnmasked to set
	 */
	public void setPhoneNumberUnmasked(String phoneNumberUnmasked) {
		this.phoneNumberUnmasked = phoneNumberUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getExtensionNumberUnmasked()
	 */
	public String getExtensionNumberUnmasked() {
		return extensionNumberUnmasked;
	}

	/**
	 * @param extensionNumberUnmasked the extensionNumberUnmasked to set
	 */
	public void setExtensionNumberUnmasked(String extensionNumberUnmasked) {
		this.extensionNumberUnmasked = extensionNumberUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getCountryCodeUnmasked()
	 */
	public String getCountryCodeUnmasked() {
		return countryCodeUnmasked;
	}

	/**
	 * @param countryCodeUnmasked the countryCodeUnmasked to set
	 */
	public void setCountryCodeUnmasked(String countryCodeUnmasked) {
		this.countryCodeUnmasked = countryCodeUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getFormattedPhoneNumber()
	 */
	public String getFormattedPhoneNumber() {
		return formattedPhoneNumber;
	}

	/**
	 * @param formattedPhoneNumber the formattedPhoneNumber to set
	 */
	public void setFormattedPhoneNumber(String formattedPhoneNumber) {
		this.formattedPhoneNumber = formattedPhoneNumber;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getFormattedPhoneNumberUnmasked()
	 */
	public String getFormattedPhoneNumberUnmasked() {
		return formattedPhoneNumberUnmasked;
	}

	/**
	 * @param formattedPhoneNumberUnmasked the formattedPhoneNumberUnmasked to set
	 */
	public void setFormattedPhoneNumberUnmasked(String formattedPhoneNumberUnmasked) {
		this.formattedPhoneNumberUnmasked = formattedPhoneNumberUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#isSuppressPhone()
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
