/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityPhoneInfo extends KimDefaultableInfo implements KimEntityPhone {
	
	private static final long serialVersionUID = 1L;

	protected String entityPhoneId = "";
	protected String entityTypeCode = "";
	protected String phoneTypeCode = "";
	protected String phoneNumber = "";
	protected String extensionNumber = "";
	protected String countryCode = "";

	/**
	 * 
	 */
	public KimEntityPhoneInfo() {
	}
	
	/**
	 * 
	 */
	public KimEntityPhoneInfo( KimEntityPhone phone ) {
		if ( phone != null ) {
			entityPhoneId = unNullify( phone.getEntityPhoneId() );
			entityTypeCode = unNullify( phone.getEntityTypeCode() );
			phoneTypeCode = unNullify( phone.getPhoneTypeCode() );
			phoneNumber = unNullify( phone.getPhoneNumber() );
			extensionNumber = unNullify( phone.getExtensionNumber() );
			countryCode = unNullify( phone.getCountryCode() );
			dflt = phone.isDefault();
			active = phone.isActive();
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getCountryCode()
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getEntityPhoneId()
	 */
	public String getEntityPhoneId() {
		return entityPhoneId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getExtensionNumber()
	 */
	public String getExtensionNumber() {
		return extensionNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneNumber()
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneTypeCode()
	 */
	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	public void setEntityPhoneId(String entityPhoneId) {
		this.entityPhoneId = entityPhoneId;
	}

	public String getFormattedPhoneNumber() {
		StringBuffer sb = new StringBuffer( 30 );
		
		// TODO: get extension from country code table
		// TODO: append "+xxx" if country is not the default country
		sb.append( getPhoneNumber() );
		if ( StringUtils.isNotBlank( getExtensionNumber() ) ) {
			sb.append( " x" );
			sb.append( getExtensionNumber() );
		}
		
		return sb.toString();
	}
}
