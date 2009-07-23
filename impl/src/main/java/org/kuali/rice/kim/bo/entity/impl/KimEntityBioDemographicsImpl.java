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
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KRIM_ENTITY_BIO_T")
public class KimEntityBioDemographicsImpl extends KimEntityDataBase implements KimEntityBioDemographics {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "BIRTH_DT")
	protected Date birthDate;

	@Column(name = "ETHNCTY_CD")
	protected String ethnicityCode;

	@Column(name = "GNDR_CD")
	protected String genderCode;

	@Transient
    protected Boolean suppressPersonal;
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getBirthDate()
	 */
	public Date getBirthDate() {
		return birthDate;
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
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGenderCode()
	 */
	public String getGenderCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return genderCode;
	}

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put("entityId", entityId);
		m.put("birthDate", getBirthDate());
		m.put("ethnicityCode", getEthnicityCode());
		m.put("genderCode", getGenderCode());
		return m;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
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

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getEthnicityCodeUnmasked()
     */
    public String getEthnicityCodeUnmasked() {
        return this.ethnicityCode;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getGenderCodeUnmasked()
     */
    public String getGenderCodeUnmasked() {
        return this.genderCode;
    }

}
