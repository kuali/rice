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
package org.kuali.rice.kim.bo.entity.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.entity.KimEntityEthnicity;
import org.kuali.rice.kim.util.KimConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name = "KRIM_ENTITY_ETHNIC_T")
public class KimEntityEthnicityImpl extends KimEntityDataBase implements KimEntityEthnicity {

	private static final long serialVersionUID = 4870141334376945160L;

	@Id
	@GeneratedValue(generator="KRIM_ENTITY_ETHNIC_ID_S")
	@GenericGenerator(name="KRIM_ENTITY_ETHNIC_ID_S",strategy="org.kuali.rice.core.jpa.spring.RiceNumericStringSequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRIM_ENTITY_ETHNIC_ID_S"),
			@Parameter(name="value_column",value="id")
		})	
	@Column(name = "ID")
	protected String id;

	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "ETHNCTY_CD")
	protected String ethnicityCode;

	@Column(name = "SUB_ETHNCTY_CD")
	protected String subEthnicityCode;

	@Transient
    protected Boolean suppressPersonal;

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getEthnicityCode()
	 */
	@Override
	public String getEthnicityCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return ethnicityCode;
	}

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getEthnicityCodeUnmasked()
     */
    @Override
	public String getEthnicityCodeUnmasked() {
        return this.ethnicityCode;
    }

    /**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getSubEthnicityCode()
	 */
	@Override
	public String getSubEthnicityCode() {
	    if (isSuppressPersonal()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return subEthnicityCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getSubEthnicityCodeUnmasked()
	 */
	@Override
	public String getSubEthnicityCodeUnmasked() {
		return this.subEthnicityCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEthnicity#getId()
	 */
	@Override
	public String getId() {
		return entityId;
	}

	@Override
	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setEthnicityCode(String ethnicityCode) {
		this.ethnicityCode = ethnicityCode;
	}

	public void setSubEthnicityCode(String subEthnicityCode) {
		this.subEthnicityCode = subEthnicityCode;
	}

    @Override
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

}
