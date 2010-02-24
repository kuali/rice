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
	
	protected String entityId;
	protected Date birthDate;
	protected Date deceasedDate;
	protected String genderCode;
	protected String maritalStatusCode;
	protected String primaryLanguageCode;
	protected String secondaryLanguageCode;
	protected String countryOfBirthCode;
	protected String birthStateCode;
	protected String cityOfBirth;
	protected String geographicOrigin;
	protected Date birthDateUnmasked;
	protected String genderCodeUnmasked;
	protected String genderCodeUnmaskedUnmasked;
	protected String maritalStatusCodeUnmasked;
	protected String primaryLanguageCodeUnmasked;
	protected String secondaryLanguageCodeUnmasked;
	protected String countryOfBirthCodeUnmasked;
	protected String birthStateCodeUnmasked;
	protected String cityOfBirthUnmasked;
	protected String geographicOriginUnmasked;

	private boolean suppressPersonal = false;
	
	public KimEntityBioDemographicsInfo() {
		super();
	}

	public KimEntityBioDemographicsInfo( KimEntityBioDemographics kimEntityBioDemographics ) {
		this();
		if ( kimEntityBioDemographics != null ) {
			entityId = kimEntityBioDemographics.getEntityId();
			birthDate = kimEntityBioDemographics.getBirthDate();
			deceasedDate = kimEntityBioDemographics.getDeceasedDate();
			genderCode = kimEntityBioDemographics.getGenderCode();
			maritalStatusCode = kimEntityBioDemographics.getMaritalStatusCode();
			primaryLanguageCode = kimEntityBioDemographics.getPrimaryLanguageCode();
			secondaryLanguageCode = kimEntityBioDemographics.getSecondaryLanguageCode();
			countryOfBirthCode = kimEntityBioDemographics.getCountryOfBirthCode();
			birthStateCode = kimEntityBioDemographics.getBirthStateCode();
			cityOfBirth = kimEntityBioDemographics.getCityOfBirth();
			geographicOrigin = kimEntityBioDemographics.getGeographicOrigin();

			birthDateUnmasked = kimEntityBioDemographics.getBirthDateUnmasked();
			genderCodeUnmaskedUnmasked = kimEntityBioDemographics.getGenderCodeUnmasked();
			genderCodeUnmasked = kimEntityBioDemographics.getGenderCodeUnmasked();
			maritalStatusCodeUnmasked = kimEntityBioDemographics.getMaritalStatusCodeUnmasked();
			primaryLanguageCodeUnmasked = kimEntityBioDemographics.getPrimaryLanguageCodeUnmasked();
			secondaryLanguageCodeUnmasked = kimEntityBioDemographics.getSecondaryLanguageCodeUnmasked();
			countryOfBirthCodeUnmasked = kimEntityBioDemographics.getCountryOfBirthCodeUnmasked();
			birthStateCodeUnmasked = kimEntityBioDemographics.getBirthStateCodeUnmasked();
			cityOfBirthUnmasked = kimEntityBioDemographics.getCityOfBirthUnmasked();
			geographicOriginUnmasked = kimEntityBioDemographics.getGeographicOriginUnmasked();
			
			suppressPersonal = kimEntityBioDemographics.isSuppressPersonal();
		}
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getEntityId()
	 */
	public String getEntityId() {
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getBirthDate()
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
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getDeceasedDate()
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
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGenderCode()
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
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getMaritalStatusCode()
	 */
	public String getMaritalStatusCode() {
		return maritalStatusCode;
	}

	/**
	 * @param maritalStatusCode the maritalStatusCode to set
	 */
	public void setMaritalStatusCode(String maritalStatusCode) {
		this.maritalStatusCode = maritalStatusCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getPrimaryLanguageCode()
	 */
	public String getPrimaryLanguageCode() {
		return primaryLanguageCode;
	}

	/**
	 * @param primaryLanguageCode the primaryLanguageCode to set
	 */
	public void setPrimaryLanguageCode(String primaryLanguageCode) {
		this.primaryLanguageCode = primaryLanguageCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getSecondaryLanguageCode()
	 */
	public String getSecondaryLanguageCode() {
		return secondaryLanguageCode;
	}

	/**
	 * @param secondaryLanguageCode the secondaryLanguageCode to set
	 */
	public void setSecondaryLanguageCode(String secondaryLanguageCode) {
		this.secondaryLanguageCode = secondaryLanguageCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getCountryOfBirthCode()
	 */
	public String getCountryOfBirthCode() {
		return countryOfBirthCode;
	}

	/**
	 * @param countryOfBirthCode the countryOfBirthCode to set
	 */
	public void setCountryOfBirthCode(String countryOfBirthCode) {
		this.countryOfBirthCode = countryOfBirthCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getBirthStateCode()
	 */
	public String getBirthStateCode() {
		return birthStateCode;
	}

	/**
	 * @param birthStateCode the birthStateCode to set
	 */
	public void setBirthStateCode(String birthStateCode) {
		this.birthStateCode = birthStateCode;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getCityOfBirth()
	 */
	public String getCityOfBirth() {
		return cityOfBirth;
	}

	/**
	 * @param cityOfBirth the cityOfBirth to set
	 */
	public void setCityOfBirth(String cityOfBirth) {
		this.cityOfBirth = cityOfBirth;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGeographicOrigin()
	 */
	public String getGeographicOrigin() {
		return geographicOrigin;
	}

	/**
	 * @param geographicOrigin the geographicOrigin to set
	 */
	public void setGeographicOrigin(String geographicOrigin) {
		this.geographicOrigin = geographicOrigin;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGenderCodeUnmasked()
	 */
	public String getGenderCodeUnmasked() {
		return genderCodeUnmasked;
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
		return genderCodeUnmaskedUnmasked;
	}

	/**
	 * @param genderCodeUnmaskedUnmasked the genderCodeUnmaskedUnmasked to set
	 */
	public void setGenderCodeUnmaskedUnmasked(String genderCodeUnmaskedUnmasked) {
		this.genderCodeUnmaskedUnmasked = genderCodeUnmaskedUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getMaritalStatusCodeUnmasked()
	 */
	public String getMaritalStatusCodeUnmasked() {
		return maritalStatusCodeUnmasked;
	}

	/**
	 * @param maritalStatusCodeUnmasked the maritalStatusCodeUnmasked to set
	 */
	public void setMaritalStatusCodeUnmasked(String maritalStatusCodeUnmasked) {
		this.maritalStatusCodeUnmasked = maritalStatusCodeUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getPrimaryLanguageCodeUnmasked()
	 */
	public String getPrimaryLanguageCodeUnmasked() {
		return primaryLanguageCodeUnmasked;
	}

	/**
	 * @param primaryLanguageCodeUnmasked the primaryLanguageCodeUnmasked to set
	 */
	public void setPrimaryLanguageCodeUnmasked(String primaryLanguageCodeUnmasked) {
		this.primaryLanguageCodeUnmasked = primaryLanguageCodeUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getSecondaryLanguageCodeUnmasked()
	 */
	public String getSecondaryLanguageCodeUnmasked() {
		return secondaryLanguageCodeUnmasked;
	}

	/**
	 * @param secondaryLanguageCodeUnmasked the secondaryLanguageCodeUnmasked to set
	 */
	public void setSecondaryLanguageCodeUnmasked(
			String secondaryLanguageCodeUnmasked) {
		this.secondaryLanguageCodeUnmasked = secondaryLanguageCodeUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getCountryOfBirthCodeUnmasked()
	 */
	public String getCountryOfBirthCodeUnmasked() {
		return countryOfBirthCodeUnmasked;
	}

	/**
	 * @param countryOfBirthCodeUnmasked the countryOfBirthCodeUnmasked to set
	 */
	public void setCountryOfBirthCodeUnmasked(String countryOfBirthCodeUnmasked) {
		this.countryOfBirthCodeUnmasked = countryOfBirthCodeUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getBirthStateCodeUnmasked()
	 */
	public String getBirthStateCodeUnmasked() {
		return birthStateCodeUnmasked;
	}

	/**
	 * @param birthStateCodeUnmasked the birthStateCodeUnmasked to set
	 */
	public void setBirthStateCodeUnmasked(String birthStateCodeUnmasked) {
		this.birthStateCodeUnmasked = birthStateCodeUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getCityOfBirthUnmasked()
	 */
	public String getCityOfBirthUnmasked() {
		return cityOfBirthUnmasked;
	}

	/**
	 * @param cityOfBirthUnmasked the cityOfBirthUnmasked to set
	 */
	public void setCityOfBirthUnmasked(String cityOfBirthUnmasked) {
		this.cityOfBirthUnmasked = cityOfBirthUnmasked;
	}

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGeographicOriginUnmasked()
	 */
	public String getGeographicOriginUnmasked() {
		return geographicOriginUnmasked;
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

	/**
	 * {@inheritDoc} 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getBirthDateUnmasked()
	 */
    public Date getBirthDateUnmasked() {
		return this.birthDateUnmasked;
	}
	
	public void setBirthDateUnmasked(Date birthDateUnmasked){
		this.birthDateUnmasked = birthDateUnmasked;
	}

}
