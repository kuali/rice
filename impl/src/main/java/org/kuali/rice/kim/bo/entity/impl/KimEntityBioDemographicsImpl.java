/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.impl;

import org.kuali.rice.kim.api.entity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.entity.KimEntityBioDemographics;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.util.KualiDateMask;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_BIO_T")
public class KimEntityBioDemographicsImpl extends KimEntityDataBase implements KimEntityBioDemographics {

	private static final long serialVersionUID = 6317317790920881093L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "BIRTH_DT")
	protected Date birthDate;

	@Column(name = "GNDR_CD")
	protected String genderCode;

	@Column(name = "DECEASED_DT")
	protected Date deceasedDate;

	@Column(name = "MARITAL_STATUS")
	protected String maritalStatusCode;

	@Column(name = "PRIM_LANG_CD")
	protected String primaryLanguageCode;

	@Column(name = "SEC_LANG_CD")
	protected String secondaryLanguageCode;

	@Column(name = "BIRTH_CNTRY_CD")
	protected String countryOfBirthCode;

	@Column(name = "BIRTH_STATE_CD")
	protected String birthStateCode;

	@Column(name = "BIRTH_CITY")
	protected String cityOfBirth;

	@Column(name = "GEO_ORIGIN")
	protected String geographicOrigin;

	@Transient
	protected Boolean suppressPersonal;
	
	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		
	    if (isSuppressPersonal()) {
            return KualiDateMask.getInstance();
        }
		return this.birthDate;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getBirthDateUnmasked()
	 */
	public Date getBirthDateUnmasked() {
		return this.birthDate;
	}

	/**
	 * @return the deceasedDate
	 */
	public Date getDeceasedDate() {
		return this.deceasedDate;
	}

	/**
	 * @return the maritalStatusCode
	 */
	public String getMaritalStatusCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.maritalStatusCode;
	}

	/**
	 * @return the primaryLanguageCode
	 */
	public String getPrimaryLanguageCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.primaryLanguageCode;
	}

	/**
	 * @return the secondaryLanguageCode
	 */
	public String getSecondaryLanguageCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.secondaryLanguageCode;
	}

	/**
	 * @return the countryOfBirthCode
	 */
	public String getCountryOfBirthCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.countryOfBirthCode;
	}

	/**
	 * @return the birthStateCode
	 */
	public String getBirthStateCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.birthStateCode;
	}

	/**
	 * @return the cityOfBirth
	 */
	public String getCityOfBirth() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.cityOfBirth;
	}

	/**
	 * @return the geographicOrigin
	 */
	public String getGeographicOrigin() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return this.geographicOrigin;
	}

	/**
	 * @return the maritalStatusCode
	 */
	public String getMaritalStatusCodeUnmasked() {
		return this.maritalStatusCode;
	}

	/**
	 * @return the primaryLanguageCode
	 */
	public String getPrimaryLanguageCodeUnmasked() {
		return this.primaryLanguageCode;
	}

	/**
	 * @return the secondaryLanguageCode
	 */
	public String getSecondaryLanguageCodeUnmasked() {
		return this.secondaryLanguageCode;
	}

	/**
	 * @return the countryOfBirthCode
	 */
	public String getCountryOfBirthCodeUnmasked() {
		return this.countryOfBirthCode;
	}

	/**
	 * @return the birthStateCode
	 */
	public String getBirthStateCodeUnmasked() {
		return this.birthStateCode;
	}

	/**
	 * @return the cityOfBirth
	 */
	public String getCityOfBirthUnmasked() {
		return this.cityOfBirth;
	}

	/**
	 * @return the geographicOrigin
	 */
	public String getGeographicOriginUnmasked() {
		return this.geographicOrigin;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @param deceasedDate the deceasedDate to set
	 */
	public void setDeceasedDate(Date deceasedDate) {
		this.deceasedDate = deceasedDate;
	}

	/**
	 * @param maritalStatusCode the maritalStatusCode to set
	 */
	public void setMaritalStatusCode(String maritalStatusCode) {
		this.maritalStatusCode = maritalStatusCode;
	}

	/**
	 * @param primaryLanguageCode the primaryLanguageCode to set
	 */
	public void setPrimaryLanguageCode(String primaryLanguageCode) {
		this.primaryLanguageCode = primaryLanguageCode;
	}

	/**
	 * @param secondaryLanguageCode the secondaryLanguageCode to set
	 */
	public void setSecondaryLanguageCode(String secondaryLanguageCode) {
		this.secondaryLanguageCode = secondaryLanguageCode;
	}

	/**
	 * @param countryOfBirthCode the countryOfBirthCode to set
	 */
	public void setCountryOfBirthCode(String countryOfBirthCode) {
		this.countryOfBirthCode = countryOfBirthCode;
	}

	/**
	 * @param birthStateCode the birthStateCode to set
	 */
	public void setBirthStateCode(String birthStateCode) {
		this.birthStateCode = birthStateCode;
	}

	/**
	 * @param cityOfBirth the cityOfBirth to set
	 */
	public void setCityOfBirth(String cityOfBirth) {
		this.cityOfBirth = cityOfBirth;
	}

	/**
	 * @param geographicOrigin the geographicOrigin to set
	 */
	public void setGeographicOrigin(String geographicOrigin) {
		this.geographicOrigin = geographicOrigin;
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
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGenderCode()
	 */
	public String getGenderCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return genderCode;
	}

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGenderCodeUnmasked()
     */
    public String getGenderCodeUnmasked() {
        return this.genderCode;
    }

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	/**
	 * @return the suppressPersonal
	 */
	public Boolean getSuppressPersonal() {
		return this.suppressPersonal;
	}

	/**
	 * @param suppressPersonal the suppressPersonal to set
	 */
	public void setSuppressPersonal(Boolean suppressPersonal) {
		this.suppressPersonal = suppressPersonal;
	}

    public boolean isSuppressPersonal() {
        if (suppressPersonal != null) {
            return suppressPersonal.booleanValue();
        }
        EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());

        suppressPersonal = false;
        if (privacy != null) {
            suppressPersonal = privacy.isSuppressPersonal();
        } 
        return suppressPersonal.booleanValue();
    }

    //mask date to "0001-01-01"
    private Date dateMask(){

    	Calendar calendar = Calendar.getInstance();
    	calendar.set(calendar.getMinimum(Calendar.YEAR), 
    			calendar.getMinimum(Calendar.MONTH ), 
    			calendar.getMinimum(Calendar.DATE));
	
    	return (calendar != null ? new java.sql.Date(calendar.getTime().getTime()) : null);
    }
}
