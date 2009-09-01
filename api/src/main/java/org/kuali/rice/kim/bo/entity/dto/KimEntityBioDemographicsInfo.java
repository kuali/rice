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
	protected String genderCode = "";
	protected String genderCodeUnmasked = "";
	protected Date deceasedDate;
	protected String maritalStatusCode = "";
	protected String primaryLanguageCode = "";
	protected String secondaryLanguageCode = "";
	protected String countryOfBirthCode = "";
	protected String stateOfBirth = "";
	protected String cityOfBirth = "";
	protected String geographicOrigin = "";

	private String ethnicityCode = "";
	private String ethnicityCodeUnmasked = "";
	
	private boolean suppressPersonal = false;
	
	public KimEntityBioDemographicsInfo() {
		super();
	}

	public KimEntityBioDemographicsInfo( KimEntityBioDemographics kimEntityBioDemographics ) {
		this();
		if ( kimEntityBioDemographics != null ) {
			entityId = unNullify(kimEntityBioDemographics.getEntityId());
			birthDate = unNullify(kimEntityBioDemographics.getBirthDate());
			genderCode = unNullify(kimEntityBioDemographics.getGenderCode());
			genderCodeUnmasked = unNullify(kimEntityBioDemographics.getGenderCodeUnmasked());
			deceasedDate = unNullify(kimEntityBioDemographics.getDeceasedDate());
			maritalStatusCode = unNullify(kimEntityBioDemographics.getMaritalStatusCode());
			primaryLanguageCode = unNullify(kimEntityBioDemographics.getPrimaryLanguageCode());
			secondaryLanguageCode = unNullify(kimEntityBioDemographics.getSecondaryLanguageCode());
			countryOfBirthCode = unNullify(kimEntityBioDemographics.getCountryOfBirthCode());
			stateOfBirth = unNullify(kimEntityBioDemographics.getStateOfBirth());
			cityOfBirth = unNullify(kimEntityBioDemographics.getCityOfBirth());
			geographicOrigin = unNullify(kimEntityBioDemographics.getGeographicOrigin());

			ethnicityCode = unNullify(kimEntityBioDemographics.getEthnicityCode());
			ethnicityCodeUnmasked = unNullify(kimEntityBioDemographics.getEthnicityCodeUnmasked());
			
			suppressPersonal = kimEntityBioDemographics.isSuppressPersonal();
		}
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return this.entityId;
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
		return this.birthDate;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the genderCode
	 */
	public String getGenderCode() {
		return this.genderCode;
	}

	/**
	 * @param genderCode the genderCode to set
	 */
	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	/**
	 * @return the genderCodeUnmasked
	 */
	public String getGenderCodeUnmasked() {
		return this.genderCodeUnmasked;
	}

	/**
	 * @param genderCodeUnmasked the genderCodeUnmasked to set
	 */
	public void setGenderCodeUnmasked(String genderCodeUnmasked) {
		this.genderCodeUnmasked = genderCodeUnmasked;
	}

	/**
	 * @return the deceasedDate
	 */
	public Date getDeceasedDate() {
		return this.deceasedDate;
	}

	/**
	 * @param deceasedDate the deceasedDate to set
	 */
	public void setDeceasedDate(Date deceasedDate) {
		this.deceasedDate = deceasedDate;
	}

	/**
	 * @return the maritalStatusCode
	 */
	public String getMaritalStatusCode() {
		return this.maritalStatusCode;
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
		return this.primaryLanguageCode;
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
		return this.secondaryLanguageCode;
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
		return this.countryOfBirthCode;
	}

	/**
	 * @param countryOfBirthCode the countryOfBirthCode to set
	 */
	public void setCountryOfBirthCode(String countryOfBirthCode) {
		this.countryOfBirthCode = countryOfBirthCode;
	}

	/**
	 * @return the stateOfBirth
	 */
	public String getStateOfBirth() {
		return this.stateOfBirth;
	}

	/**
	 * @param stateOfBirth the stateOfBirth to set
	 */
	public void setStateOfBirth(String stateOfBirth) {
		this.stateOfBirth = stateOfBirth;
	}

	/**
	 * @return the cityOfBirth
	 */
	public String getCityOfBirth() {
		return this.cityOfBirth;
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
		return this.geographicOrigin;
	}

	/**
	 * @param geographicOrigin the geographicOrigin to set
	 */
	public void setGeographicOrigin(String geographicOrigin) {
		this.geographicOrigin = geographicOrigin;
	}

	/**
	 * @return the ethnicityCode
	 */
	public String getEthnicityCode() {
		return this.ethnicityCode;
	}

	/**
	 * @param ethnicityCode the ethnicityCode to set
	 */
	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	/**
	 * @return the ethnicityCodeUnmasked
	 */
	public String getEthnicityCodeUnmasked() {
		return this.ethnicityCodeUnmasked;
	}

	/**
	 * @param ethnicityCodeUnmasked the ethnicityCodeUnmasked to set
	 */
	public void setEthnicityCodeUnmasked(String ethnicityCodeUnmasked) {
		this.ethnicityCodeUnmasked = ethnicityCodeUnmasked;
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
