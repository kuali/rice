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
import org.kuali.rice.kim.bo.entity.KimEntityEmail;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.reference.EmailType;
import org.kuali.rice.kim.bo.reference.impl.EmailTypeImpl;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_EMAIL_T")
public class KimEntityEmailImpl extends KimDefaultableEntityDataBase implements KimEntityEmail {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_EMAIL_ID")
	protected String entityEmailId;

	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "ENT_TYP_CD")
	protected String entityTypeCode;

	@Column(name = "EMAIL_TYP_CD")
	protected String emailTypeCode;

	@Column(name = "EMAIL_ADDR")
	protected String emailAddress;

	@ManyToOne(targetEntity=EmailTypeImpl.class, fetch=FetchType.EAGER, cascade={})
	@JoinColumn(name = "EMAIL_TYP_CD", insertable = false, updatable = false)
	protected EmailType emailType;
	
	@Transient
	protected Boolean suppressEmail;

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailAddress()
	 */
	public String getEmailAddress() {
	    if (isSuppressEmail()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return emailAddress;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailTypeCode()
	 */
	public String getEmailTypeCode() {
		return emailTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEntityEmailId()
	 */
	public String getEntityEmailId() {
		return entityEmailId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#setEmailAddress(java.lang.String)
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#setEmailTypeCode(java.lang.String)
	 */
	public void setEmailTypeCode(String emailTypeCode) {
		this.emailTypeCode = emailTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData#getEntityTypeCode()
	 */
	public String getEntityTypeCode() {
		return entityTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimDefaultableEntityTypeData#setEntityTypeCode(java.lang.String)
	 */
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setEntityEmailId(String entityEmailId) {
		this.entityEmailId = entityEmailId;
	}

	public EmailType getEmailType() {
		return this.emailType;
	}

	public void setEmailType(EmailType emailType) {
		this.emailType = emailType;
	}

    public boolean isSuppressEmail() {
        if (suppressEmail != null) {
            return suppressEmail.booleanValue();
        }
        KimEntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());

        suppressEmail = false;
        if (privacy != null) {
            suppressEmail = privacy.isSuppressEmail();
        } 
        return suppressEmail.booleanValue();
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityEmail#getEmailAddressUnmasked()
     */
    public String getEmailAddressUnmasked() {
        return this.emailAddress;
    }

}
