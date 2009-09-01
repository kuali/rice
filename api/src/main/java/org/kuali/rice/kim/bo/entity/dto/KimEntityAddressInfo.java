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

import org.kuali.rice.kim.bo.entity.KimEntityAddress;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KimEntityAddressInfo extends KimDefaultableInfo implements KimEntityAddress {

	private static final long serialVersionUID = 1L;

	protected String entityAddressId = "";
	protected String addressTypeCode = "";
	protected String entityTypeCode = "";
	protected String cityName = "";
	protected String cityNameUnmasked = "";
	protected String stateCode = "";
	protected String stateCodeUnmasked = "";
	protected String postalCode = "";
	protected String postalCodeUnmasked = "";
	protected String countryCode = "";
	protected String countryCodeUnmasked = "";
	protected String line1 = "";
	protected String line1Unmasked = "";
	protected String line2 = "";
	protected String line2Unmasked = "";
	protected String line3 = "";
	protected String line3Unmasked = "";

	protected boolean suppressAddress = false;
	/**
	 * 
	 */
	public KimEntityAddressInfo() {
		super();
		active = true;
	}
	/**
	 * 
	 */
	public KimEntityAddressInfo( KimEntityAddress addr ) {
		this();
		if ( addr != null ) {
		    this.entityAddressId = unNullify( addr.getEntityAddressId() );
			this.entityTypeCode = unNullify( addr.getEntityTypeCode() );
			this.addressTypeCode = unNullify( addr.getAddressTypeCode() );
			this.cityName = unNullify( addr.getCityName() );
			this.cityNameUnmasked = unNullify( addr.getCityNameUnmasked() );
			this.stateCode = unNullify( addr.getStateCode() );
			this.stateCodeUnmasked = unNullify( addr.getStateCodeUnmasked() );
			this.postalCode = unNullify( addr.getPostalCode() );
			this.postalCodeUnmasked = unNullify( addr.getPostalCodeUnmasked() );
			this.countryCode = unNullify( addr.getCountryCode() );
			this.countryCodeUnmasked = unNullify( addr.getCountryCodeUnmasked() );
			this.line1 = unNullify( addr.getLine1() );
			this.line1Unmasked = unNullify( addr.getLine1Unmasked() );
			this.line2 = unNullify( addr.getLine2() );
			this.line2Unmasked = unNullify( addr.getLine2Unmasked() );
			this.line3 = unNullify( addr.getLine3() );
			this.line3Unmasked = unNullify( addr.getLine3Unmasked() );
			this.dflt = addr.isDefault();
			this.active = addr.isActive();
			this.suppressAddress = addr.isSuppressAddress();
		}
	}
	/**
	 * @return the entityAddressId
	 */
	public String getEntityAddressId() {
		return this.entityAddressId;
	}
	/**
	 * @param entityAddressId the entityAddressId to set
	 */
	public void setEntityAddressId(String entityAddressId) {
		this.entityAddressId = entityAddressId;
	}
	/**
	 * @return the addressTypeCode
	 */
	public String getAddressTypeCode() {
		return this.addressTypeCode;
	}
	/**
	 * @param addressTypeCode the addressTypeCode to set
	 */
	public void setAddressTypeCode(String addressTypeCode) {
		this.addressTypeCode = addressTypeCode;
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
	 * @return the cityName
	 */
	public String getCityName() {
		return this.cityName;
	}
	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	/**
	 * @return the cityNameUnmasked
	 */
	public String getCityNameUnmasked() {
		return this.cityNameUnmasked;
	}
	/**
	 * @param cityNameUnmasked the cityNameUnmasked to set
	 */
	public void setCityNameUnmasked(String cityNameUnmasked) {
		this.cityNameUnmasked = cityNameUnmasked;
	}
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return this.stateCode;
	}
	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	/**
	 * @return the stateCodeUnmasked
	 */
	public String getStateCodeUnmasked() {
		return this.stateCodeUnmasked;
	}
	/**
	 * @param stateCodeUnmasked the stateCodeUnmasked to set
	 */
	public void setStateCodeUnmasked(String stateCodeUnmasked) {
		this.stateCodeUnmasked = stateCodeUnmasked;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return this.postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * @return the postalCodeUnmasked
	 */
	public String getPostalCodeUnmasked() {
		return this.postalCodeUnmasked;
	}
	/**
	 * @param postalCodeUnmasked the postalCodeUnmasked to set
	 */
	public void setPostalCodeUnmasked(String postalCodeUnmasked) {
		this.postalCodeUnmasked = postalCodeUnmasked;
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
	 * @return the line1
	 */
	public String getLine1() {
		return this.line1;
	}
	/**
	 * @param line1 the line1 to set
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	/**
	 * @return the line1Unmasked
	 */
	public String getLine1Unmasked() {
		return this.line1Unmasked;
	}
	/**
	 * @param line1Unmasked the line1Unmasked to set
	 */
	public void setLine1Unmasked(String line1Unmasked) {
		this.line1Unmasked = line1Unmasked;
	}
	/**
	 * @return the line2
	 */
	public String getLine2() {
		return this.line2;
	}
	/**
	 * @param line2 the line2 to set
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}
	/**
	 * @return the line2Unmasked
	 */
	public String getLine2Unmasked() {
		return this.line2Unmasked;
	}
	/**
	 * @param line2Unmasked the line2Unmasked to set
	 */
	public void setLine2Unmasked(String line2Unmasked) {
		this.line2Unmasked = line2Unmasked;
	}
	/**
	 * @return the line3
	 */
	public String getLine3() {
		return this.line3;
	}
	/**
	 * @param line3 the line3 to set
	 */
	public void setLine3(String line3) {
		this.line3 = line3;
	}
	/**
	 * @return the line3Unmasked
	 */
	public String getLine3Unmasked() {
		return this.line3Unmasked;
	}
	/**
	 * @param line3Unmasked the line3Unmasked to set
	 */
	public void setLine3Unmasked(String line3Unmasked) {
		this.line3Unmasked = line3Unmasked;
	}
	/**
	 * @return the suppressAddress
	 */
	public boolean isSuppressAddress() {
		return this.suppressAddress;
	}
	/**
	 * @param suppressAddress the suppressAddress to set
	 */
	public void setSuppressAddress(boolean suppressAddress) {
		this.suppressAddress = suppressAddress;
	}

}
