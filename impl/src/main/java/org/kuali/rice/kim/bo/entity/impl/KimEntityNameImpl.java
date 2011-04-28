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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.reference.EntityNameType;
import org.kuali.rice.kim.bo.reference.impl.EntityNameTypeImpl;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_NM_T")
public class KimEntityNameImpl extends KimDefaultableEntityDataBase implements KimEntityName {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_NM_ID")
	protected String entityNameId;

	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "NM_TYP_CD")
	protected String nameTypeCode ;

	@Column(name = "FIRST_NM")
	protected String firstName;

	@Column(name = "MIDDLE_NM")
	protected String middleName;

	@Column(name = "LAST_NM")
	protected String lastName;

	@Column(name = "TITLE_NM")
	protected String title;

	@Column(name = "SUFFIX_NM")
	protected String suffix;
	
	@ManyToOne(targetEntity=EntityNameTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "NM_TYP_CD", insertable = false, updatable = false)
	protected EntityNameType entityNameType;
	
	@Transient
	protected Boolean suppressName;
	

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getEntityNameId()
	 */
	public String getEntityNameId() {
		return entityNameId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFirstName()
	 */
	public String getFirstName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return firstName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getLastName()
	 */
	public String getLastName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return lastName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getMiddleName()
	 */
	public String getMiddleName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return middleName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getNameTypeCode()
	 */
	public String getNameTypeCode() {
		return nameTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getSuffix()
	 */
	public String getSuffix() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return suffix;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getTitle()
	 */
	public String getTitle() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return title;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setNameTypeCode(String nameTypeCode) {
		this.nameTypeCode = nameTypeCode;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * This default implementation formats the name as LAST, FIRST MIDDLE.
	 * 
	 * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFormattedName()
	 */
	public String getFormattedName() {
	    if (isSuppressName()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return getLastName() + ", " + getFirstName() + (getMiddleName()==null?"":" " + getMiddleName());
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public EntityNameType getEntityNameType() {
		return this.entityNameType;
	}

	public void setEntityNameType(EntityNameType entityNameType) {
		this.entityNameType = entityNameType;
	}

	public void setEntityNameId(String entityNameId) {
		this.entityNameId = entityNameId;
	}

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFirstNameUnmasked()
     */
    public String getFirstNameUnmasked() {
        return this.firstName;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getFormattedNameUnmasked()
     */
    public String getFormattedNameUnmasked() {
        return this.lastName + ", " + this.firstName + (this.middleName==null?"":" " + this.middleName);
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getLastNameUnmasked()
     */
    public String getLastNameUnmasked() {
        return this.lastName;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getMiddleNameUnmasked()
     */
    public String getMiddleNameUnmasked() {
        return this.middleName;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getSuffixUnmasked()
     */
    public String getSuffixUnmasked() {
        return this.suffix;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityName#getTitleUnmasked()
     */
    public String getTitleUnmasked() {
        return this.title;
    }

    public boolean isSuppressName() {
        if (suppressName != null) {
            return suppressName.booleanValue();
        }
        KimEntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());

        suppressName = false;
        if (privacy != null) {
            suppressName = privacy.isSuppressName();
        } 
        return suppressName.booleanValue();
    }
}
