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

import org.hibernate.annotations.Type;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This is a description of what this class does - nathanieljohnson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_ENTITY_PRIV_PREF_T")
public class KimEntityPrivacyPreferencesImpl extends KimEntityDataBase implements KimEntityPrivacyPreferences {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ID")
	protected String entityId;
	
	@Type(type="yes_no")
	@Column(name="SUPPRESS_NM_IND")
	protected boolean suppressName;
	
	@Type(type="yes_no")
	@Column(name="SUPPRESS_EMAIL_IND")
	protected boolean suppressEmail;
	
	@Type(type="yes_no")
	@Column(name="SUPPRESS_ADDR_IND")
	protected boolean suppressAddress;
	
	@Type(type="yes_no")
	@Column(name="SUPPRESS_PHONE_IND")
	protected boolean suppressPhone;
	
	@Type(type="yes_no")
	@Column(name="SUPPRESS_PRSNL_IND")
	protected boolean suppressPersonal;
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#isSuppressAddress()
	 */
	public boolean isSuppressAddress() {
		return suppressAddress;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#isSuppressEmail()
	 */
	public boolean isSuppressEmail() {
		return suppressEmail;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#isSuppressName()
	 */
	public boolean isSuppressName() {
		return suppressName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#isSuppressPersonal()
	 */
	public boolean isSuppressPersonal() {
		return suppressPersonal;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#isSuppressPhone()
	 */
	public boolean isSuppressPhone() {
		return suppressPhone;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#setSuppressAddress(boolean)
	 */
	public void setSuppressAddress(boolean suppressAddress) {
		this.suppressAddress = suppressAddress;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#setSuppressEmail(boolean)
	 */
	public void setSuppressEmail(boolean suppressEmail) {
		this.suppressEmail = suppressEmail;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#setSuppressName(boolean)
	 */
	public void setSuppressName(boolean suppressName) {
		this.suppressName = suppressName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#setSuppressPersonal(boolean)
	 */
	public void setSuppressPersonal(boolean suppressPersonal) {
		this.suppressPersonal = suppressPersonal;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences#setSuppressPhone(boolean)
	 */
	public void setSuppressPhone(boolean suppressPhone) {
		this.suppressPhone = suppressPhone;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

}
