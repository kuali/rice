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

import org.kuali.rice.kim.bo.entity.KimEntityAddress;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityAddressInfo extends KimDefaultableInfo implements KimEntityAddress {

	private static final long serialVersionUID = 1L;

	protected String entityAddressId = "";
	protected String addressTypeCode = "";
	protected String entityTypeCode = "";
	protected String cityName = "";
	protected String stateCode = "";
	protected String postalCode = "";
	protected String countryCode = "";
	protected String line1 = "";
	protected String line2 = "";
	protected String line3 = "";

	/**
	 * 
	 */
	public KimEntityAddressInfo() {
	}
	/**
	 * 
	 */
	public KimEntityAddressInfo( KimEntityAddress addr ) {
		if ( addr != null ) {
			entityAddressId = unNullify( addr.getEntityAddressId() );
			entityTypeCode = unNullify( addr.getEntityTypeCode() );
			addressTypeCode = unNullify( addr.getAddressTypeCode() );
			cityName = unNullify( addr.getCityName() );
			stateCode = unNullify( addr.getStateCode() );
			postalCode = unNullify( addr.getPostalCode() );
			countryCode = unNullify( addr.getCountryCode() );
			line1 = unNullify( addr.getLine1() );
			line2 = unNullify( addr.getLine2() );
			line3 = unNullify( addr.getLine3() );
			dflt = addr.isDefault();
			active = addr.isActive();
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getAddressTypeCode()
	 */
	public String getAddressTypeCode() {
		return addressTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getCityName()
	 */
	public String getCityName() {
		return cityName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getCountryCode()
	 */
	public String getCountryCode() {
		return countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getEntityAddressId()
	 */
	public String getEntityAddressId() {
		return entityAddressId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine1()
	 */
	public String getLine1() {
		return line1;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine2()
	 */
	public String getLine2() {
		return line2;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine3()
	 */
	public String getLine3() {
		return line3;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getPostalCode()
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getStateCode()
	 */
	public String getStateCode() {
		return stateCode;
	}

	public void setAddressTypeCode(String addressTypeCode) {
		this.addressTypeCode = addressTypeCode;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public void setEntityAddressId(String entityAddressId) {
		this.entityAddressId = entityAddressId;
	}
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}
}
