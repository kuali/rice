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

import java.util.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.bo.entity.KimEntityBioDemographics;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

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

//	@Column(name = "DECEASED_DT")
//	protected Date deceasedDate;
//	
//	@Column(name = "MARITAL_STATUS")
//	protected String maritalStatusCode;
//	
//	@Column(name = "PRIM_LANG_CD")
//	protected String primaryLanguageCode;
//	
//	@Column(name = "SEC_LANG_CD")
//	protected String secondaryLanguageCode;
//	
//	@Column(name = "BIRTH_COUNTRY_CD")
//	protected String countryOfBirthCode;
//	
//	@Column(name = "BIRTH_STATE")
//	protected String stateOfBirth;
//	
//	@Column(name = "BIRTH_CITY")
//	protected String cityOfBirth;
//	
//	@Column(name = "GEO_ORIGIN")
//	protected String geographicOrigin;

	// TODO delyea - what else needs to be suppressed?
	@Transient
    protected Boolean suppressPersonal;
	
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
	 * @return the deceasedDate
	 */
	public Date getDeceasedDate() {
		return null;
		//return this.deceasedDate;
	}

	/**
	 * @param deceasedDate the deceasedDate to set
	 */
	public void setDeceasedDate(Date deceasedDate) {
//		this.deceasedDate = deceasedDate;
	}

	/**
	 * @return the maritalStatusCode
	 */
	public String getMaritalStatusCode() {
		return null;
		//		return this.maritalStatusCode;
	}

	/**
	 * @param maritalStatusCode the maritalStatusCode to set
	 */
	public void setMaritalStatusCode(String maritalStatusCode) {
//		this.maritalStatusCode = maritalStatusCode;
	}

	/**
	 * @return the primaryLanguageCode
	 */
	public String getPrimaryLanguageCode() {
		return null;
		//		return this.primaryLanguageCode;
	}

	/**
	 * @param primaryLanguageCode the primaryLanguageCode to set
	 */
	public void setPrimaryLanguageCode(String primaryLanguageCode) {
//		this.primaryLanguageCode = primaryLanguageCode;
	}

	/**
	 * @return the secondaryLanguageCode
	 */
	public String getSecondaryLanguageCode() {
		return null;
		//		return this.secondaryLanguageCode;
	}

	/**
	 * @param secondaryLanguageCode the secondaryLanguageCode to set
	 */
	public void setSecondaryLanguageCode(String secondaryLanguageCode) {
//		this.secondaryLanguageCode = secondaryLanguageCode;
	}

	/**
	 * @return the countryOfBirthCode
	 */
	public String getCountryOfBirthCode() {
		return null;
		//		return this.countryOfBirthCode;
	}

	/**
	 * @param countryOfBirthCode the countryOfBirthCode to set
	 */
	public void setCountryOfBirthCode(String countryOfBirthCode) {
//		this.countryOfBirthCode = countryOfBirthCode;
	}

	/**
	 * @return the stateOfBirth
	 */
	public String getStateOfBirth() {
		return null;
		//		return this.stateOfBirth;
	}

	/**
	 * @param stateOfBirth the stateOfBirth to set
	 */
	public void setStateOfBirth(String stateOfBirth) {
//		this.stateOfBirth = stateOfBirth;
	}

	/**
	 * @return the cityOfBirth
	 */
	public String getCityOfBirth() {
		return null;
		//		return this.cityOfBirth;
	}

	/**
	 * @param cityOfBirth the cityOfBirth to set
	 */
	public void setCityOfBirth(String cityOfBirth) {
//		this.cityOfBirth = cityOfBirth;
	}

	/**
	 * @return the geographicOrigin
	 */
	public String getGeographicOrigin() {
		return null;
		//		return this.geographicOrigin;
	}

	/**
	 * @param geographicOrigin the geographicOrigin to set
	 */
	public void setGeographicOrigin(String geographicOrigin) {
//		this.geographicOrigin = geographicOrigin;
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
//		this.suppressPersonal = suppressPersonal;
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
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put("entityId", entityId);
		m.put("birthDate", getBirthDate());
		m.put("genderCode", getGenderCode());
//		m.put("deceasedDate", getDeceasedDate());
//		m.put("maritalStatusCode", getMaritalStatusCode());
//		m.put("primaryLanguageCode", getPrimaryLanguageCode());
//		m.put("secondaryLanguageCode", getSecondaryLanguageCode());
//		m.put("countryOfBirthCode", getCountryOfBirthCode());
//		m.put("stateOfBirth", getStateOfBirth());
//		m.put("cityOfBirth", getCityOfBirth());
//		m.put("geographicOrigin", getGeographicOrigin());
		m.put("ethnicityCode", getEthnicityCode());
		return m;
	}

    public boolean isSuppressPersonal() {
        if (suppressPersonal != null) {
            return suppressPersonal.booleanValue();
        }
        KimEntityPrivacyPreferences privacy = KIMServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());

        suppressPersonal = false;
        if (privacy != null) {
            suppressPersonal = privacy.isSuppressPersonal();
        } 
        return suppressPersonal.booleanValue();
    }

/*
 *  ALL OF BELOW SHOULD BE REMOVED ONCE KimEntityEthnicity IS APPROVED
 * 
 */
    
	@Column(name = "ETHNCTY_CD")
	protected String ethnicityCode;

	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getEthnicityCode()
	 */
	public String getEthnicityCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return ethnicityCode;
	}

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getEthnicityCodeUnmasked()
     */
    public String getEthnicityCodeUnmasked() {
        return this.ethnicityCode;
    }

}
