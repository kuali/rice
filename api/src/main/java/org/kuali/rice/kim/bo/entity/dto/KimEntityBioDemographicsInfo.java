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

import java.io.Serializable;
import java.util.Date;

import org.kuali.rice.kim.bo.entity.KimEntityBioDemographics;

/**
 * Represents an entity's Bio Demographics info.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityBioDemographicsInfo extends KimInfoBase implements KimEntityBioDemographics, Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String entityId = "";
	protected Date birthDate;
	protected Date deceasedDate;
	protected String genderCode = "";
	protected String maritalStatusCode = "";
	protected String primaryLanguageCode = "";
	protected String secondaryLanguageCode = "";
	protected String countryOfBirthCode = "";
	protected String birthStateCode = "";
	protected String cityOfBirth = "";
	protected String geographicOrigin = "";
	protected String genderCodeUnmasked = "";
	protected String genderCodeUnmaskedUnmasked = "";
	protected String maritalStatusCodeUnmasked = "";
	protected String primaryLanguageCodeUnmasked = "";
	protected String secondaryLanguageCodeUnmasked = "";
	protected String countryOfBirthCodeUnmasked = "";
	protected String birthStateCodeUnmasked = "";
	protected String cityOfBirthUnmasked = "";
	protected String geographicOriginUnmasked = "";

	private boolean suppressPersonal = false;
	
	public KimEntityBioDemographicsInfo() {
		super();
	}

	public KimEntityBioDemographicsInfo( KimEntityBioDemographics kimEntityBioDemographics ) {
		this();
		if ( kimEntityBioDemographics != null ) {
			entityId = unNullify(kimEntityBioDemographics.getEntityId());
			birthDate = unNullify(kimEntityBioDemographics.getBirthDate());
			deceasedDate = unNullify(kimEntityBioDemographics.getDeceasedDate());
			genderCode = unNullify(kimEntityBioDemographics.getGenderCode());
			maritalStatusCode = unNullify(kimEntityBioDemographics.getMaritalStatusCode());
			primaryLanguageCode = unNullify(kimEntityBioDemographics.getPrimaryLanguageCode());
			secondaryLanguageCode = unNullify(kimEntityBioDemographics.getSecondaryLanguageCode());
			countryOfBirthCode = unNullify(kimEntityBioDemographics.getCountryOfBirthCode());
			birthStateCode = unNullify(kimEntityBioDemographics.getBirthStateCode());
			cityOfBirth = unNullify(kimEntityBioDemographics.getCityOfBirth());
			geographicOrigin = unNullify(kimEntityBioDemographics.getGeographicOrigin());

			genderCodeUnmaskedUnmasked = unNullify(kimEntityBioDemographics.getGenderCodeUnmasked());
			genderCodeUnmasked = unNullify(kimEntityBioDemographics.getGenderCodeUnmasked());
			maritalStatusCodeUnmasked = unNullify(kimEntityBioDemographics.getMaritalStatusCodeUnmasked());
			primaryLanguageCodeUnmasked = unNullify(kimEntityBioDemographics.getPrimaryLanguageCodeUnmasked());
			secondaryLanguageCodeUnmasked = unNullify(kimEntityBioDemographics.getSecondaryLanguageCodeUnmasked());
			countryOfBirthCodeUnmasked = unNullify(kimEntityBioDemographics.getCountryOfBirthCodeUnmasked());
			birthStateCodeUnmasked = unNullify(kimEntityBioDemographics.getBirthStateCodeUnmasked());
			cityOfBirthUnmasked = unNullify(kimEntityBioDemographics.getCityOfBirthUnmasked());
			geographicOriginUnmasked = unNullify(kimEntityBioDemographics.getGeographicOriginUnmasked());
			
			suppressPersonal = kimEntityBioDemographics.isSuppressPersonal();
		}
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return unNullify(this.entityId);
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return unNullify(this.birthDate);
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the deceasedDate
	 */
	public Date getDeceasedDate() {
		return unNullify(this.deceasedDate);
	}

	/**
	 * @param deceasedDate the deceasedDate to set
	 */
	public void setDeceasedDate(Date deceasedDate) {
		this.deceasedDate = deceasedDate;
	}

	/**
	 * @return the genderCode
	 */
	public String getGenderCode() {
		return unNullify(this.genderCode);
	}

	/**
	 * @param genderCode the genderCode to set
	 */
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	/**
	 * @return the maritalStatusCode
	 */
	public String getMaritalStatusCode() {
		return unNullify(this.maritalStatusCode);
	}

	/**
	 * @param maritalStatusCode the maritalStatusCode to set
	 */
	public void setMaritalStatusCode(String maritalStatusCode) {
		this.maritalStatusCode = maritalStatusCode;
	}

	/**
	 * @return the primaryLanguageCode
	 */
	public String getPrimaryLanguageCode() {
		return unNullify(this.primaryLanguageCode);
	}

	/**
	 * @param primaryLanguageCode the primaryLanguageCode to set
	 */
	public void setPrimaryLanguageCode(String primaryLanguageCode) {
		this.primaryLanguageCode = primaryLanguageCode;
	}

	/**
	 * @return the secondaryLanguageCode
	 */
	public String getSecondaryLanguageCode() {
		return unNullify(this.secondaryLanguageCode);
	}

	/**
	 * @param secondaryLanguageCode the secondaryLanguageCode to set
	 */
	public void setSecondaryLanguageCode(String secondaryLanguageCode) {
		this.secondaryLanguageCode = secondaryLanguageCode;
	}

	/**
	 * @return the countryOfBirthCode
	 */
	public String getCountryOfBirthCode() {
		return unNullify(this.countryOfBirthCode);
	}

	/**
	 * @param countryOfBirthCode the countryOfBirthCode to set
	 */
	public void setCountryOfBirthCode(String countryOfBirthCode) {
		this.countryOfBirthCode = countryOfBirthCode;
	}

	/**
	 * @return the birthStateCode
	 */
	public String getBirthStateCode() {
		return unNullify(this.birthStateCode);
	}

	/**
	 * @param birthStateCode the birthStateCode to set
	 */
	public void setBirthStateCode(String birthStateCode) {
		this.birthStateCode = birthStateCode;
	}

	/**
	 * @return the cityOfBirth
	 */
	public String getCityOfBirth() {
		return unNullify(this.cityOfBirth);
	}

	/**
	 * @param cityOfBirth the cityOfBirth to set
	 */
	public void setCityOfBirth(String cityOfBirth) {
		this.cityOfBirth = cityOfBirth;
	}

	/**
	 * @return the geographicOrigin
	 */
	public String getGeographicOrigin() {
		return unNullify(this.geographicOrigin);
	}

	/**
	 * @param geographicOrigin the geographicOrigin to set
	 */
	public void setGeographicOrigin(String geographicOrigin) {
		this.geographicOrigin = geographicOrigin;
	}

	/**
	 * @return the genderCodeUnmasked
	 */
	public String getGenderCodeUnmasked() {
		return unNullify(this.genderCodeUnmasked);
	}

	/**
	 * @param genderCodeUnmasked the genderCodeUnmasked to set
	 */
	public void setGenderCodeUnmasked(String genderCodeUnmasked) {
		this.genderCodeUnmasked = genderCodeUnmasked;
	}

	/**
	 * @return the genderCodeUnmaskedUnmasked
	 */
	public String getGenderCodeUnmaskedUnmasked() {
		return unNullify(this.genderCodeUnmaskedUnmasked);
	}

	/**
	 * @param genderCodeUnmaskedUnmasked the genderCodeUnmaskedUnmasked to set
	 */
	public void setGenderCodeUnmaskedUnmasked(String genderCodeUnmaskedUnmasked) {
		this.genderCodeUnmaskedUnmasked = genderCodeUnmaskedUnmasked;
	}

	/**
	 * @return the maritalStatusCodeUnmasked
	 */
	public String getMaritalStatusCodeUnmasked() {
		return unNullify(this.maritalStatusCodeUnmasked);
	}

	/**
	 * @param maritalStatusCodeUnmasked the maritalStatusCodeUnmasked to set
	 */
	public void setMaritalStatusCodeUnmasked(String maritalStatusCodeUnmasked) {
		this.maritalStatusCodeUnmasked = maritalStatusCodeUnmasked;
	}

	/**
	 * @return the primaryLanguageCodeUnmasked
	 */
	public String getPrimaryLanguageCodeUnmasked() {
		return unNullify(this.primaryLanguageCodeUnmasked);
	}

	/**
	 * @param primaryLanguageCodeUnmasked the primaryLanguageCodeUnmasked to set
	 */
	public void setPrimaryLanguageCodeUnmasked(String primaryLanguageCodeUnmasked) {
		this.primaryLanguageCodeUnmasked = primaryLanguageCodeUnmasked;
	}

	/**
	 * @return the secondaryLanguageCodeUnmasked
	 */
	public String getSecondaryLanguageCodeUnmasked() {
		return unNullify(this.secondaryLanguageCodeUnmasked);
	}

	/**
	 * @param secondaryLanguageCodeUnmasked the secondaryLanguageCodeUnmasked to set
	 */
	public void setSecondaryLanguageCodeUnmasked(
			String secondaryLanguageCodeUnmasked) {
		this.secondaryLanguageCodeUnmasked = secondaryLanguageCodeUnmasked;
	}

	/**
	 * @return the countryOfBirthCodeUnmasked
	 */
	public String getCountryOfBirthCodeUnmasked() {
		return unNullify(this.countryOfBirthCodeUnmasked);
	}

	/**
	 * @param countryOfBirthCodeUnmasked the countryOfBirthCodeUnmasked to set
	 */
	public void setCountryOfBirthCodeUnmasked(String countryOfBirthCodeUnmasked) {
		this.countryOfBirthCodeUnmasked = countryOfBirthCodeUnmasked;
	}

	/**
	 * @return the birthStateCodeUnmasked
	 */
	public String getBirthStateCodeUnmasked() {
		return unNullify(this.birthStateCodeUnmasked);
	}

	/**
	 * @param birthStateCodeUnmasked the birthStateCodeUnmasked to set
	 */
	public void setBirthStateCodeUnmasked(String birthStateCodeUnmasked) {
		this.birthStateCodeUnmasked = birthStateCodeUnmasked;
	}

	/**
	 * @return the cityOfBirthUnmasked
	 */
	public String getCityOfBirthUnmasked() {
		return unNullify(this.cityOfBirthUnmasked);
	}

	/**
	 * @param cityOfBirthUnmasked the cityOfBirthUnmasked to set
	 */
	public void setCityOfBirthUnmasked(String cityOfBirthUnmasked) {
		this.cityOfBirthUnmasked = cityOfBirthUnmasked;
	}

	/**
	 * @return the geographicOriginUnmasked
	 */
	public String getGeographicOriginUnmasked() {
		return unNullify(this.geographicOriginUnmasked);
	}

	/**
	 * @param geographicOriginUnmasked the geographicOriginUnmasked to set
	 */
	public void setGeographicOriginUnmasked(String geographicOriginUnmasked) {
		this.geographicOriginUnmasked = geographicOriginUnmasked;
	}

	/**
	 * @return the suppressPersonal
	 */
	public boolean isSuppressPersonal() {
		return this.suppressPersonal;
	}

	/**
	 * @param suppressPersonal the suppressPersonal to set
	 */
	public void setSuppressPersonal(boolean suppressPersonal) {
		this.suppressPersonal = suppressPersonal;
	}

}
