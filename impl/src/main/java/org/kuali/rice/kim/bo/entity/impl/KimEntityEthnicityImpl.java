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
package org.kuali.rice.kim.bo.entity.impl;

import java.util.LinkedHashMap;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.bo.entity.KimEntityEthnicity;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KRIM_ENTITY_ETHNIC_T")
public class KimEntityEthnicityImpl extends KimEntityDataBase implements KimEntityEthnicity {

	private static final long serialVersionUID = 4870141334376945160L;

//	@Id
//	@Column(name = "ENTITY_ID")
//	protected String entityId;
//
//	@Column(name = "ETHNCTY_CD")
//	protected String ethnicityCode;
//
//	@Column(name = "SUBETHNCTY_CD")
//	protected String subEthnicityCode;

	@Transient
    protected Boolean suppressPersonal;

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getEthnicityCode()
	 */
	public String getEthnicityCode() {
		return null;
		//		return ethnicityCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityBioDemographics#getSubEthnicityCode()
	 */
	public String getSubEthnicityCode() {
		return null;
		//		return subEthnicityCode;
	}
	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@Override
	protected LinkedHashMap<String, String> toStringMapper() {
		LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
//		m.put("entityId", entityId);
//		m.put("ethnicityCode", ethnicityCode);
//		m.put("subEthnicityCode", subEthnicityCode);
		return m;
	}

	public String getEntityId() {
		return null;
		//		return this.entityId;
	}

	public void setEntityId(String entityId) {
//		this.entityId = entityId;
	}

	public void setEthnicityCode(String ethnicityCode) {
//		this.ethnicityCode = ethnicityCode;
	}

	public void setSubEthnicityCode(String subEthnicityCode) {
//		this.subEthnicityCode = subEthnicityCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getId()
	 */
	public String getId() {
		return null;
		//		return entityId;
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

}
