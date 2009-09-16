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

import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;

/**
 * This is a description of what this class does - nathanieljohnson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimEntityPrivacyPreferencesInfo implements KimEntityPrivacyPreferences, Serializable {

	private static final long serialVersionUID = 1L;

	protected boolean suppressName = false;
	protected boolean suppressEmail = false;
	protected boolean suppressAddress = false;
	protected boolean suppressPhone = false;
	protected boolean suppressPersonal = false;

	/**
	 * 
	 */
	public KimEntityPrivacyPreferencesInfo() {
	}
	
	/**
	 * 
	 */
	public KimEntityPrivacyPreferencesInfo( KimEntityPrivacyPreferences prefs ) {
		if ( prefs != null ) {
			suppressName = prefs.isSuppressName();
			suppressEmail = prefs.isSuppressEmail();
			suppressAddress = prefs.isSuppressAddress();
			suppressPhone = prefs.isSuppressPhone();
			suppressPersonal = prefs.isSuppressPersonal();
		}
	}
	
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

	public void setSuppressAddress(boolean suppressAddress) {
		this.suppressAddress = suppressAddress;
	}

	public void setSuppressEmail(boolean suppressEmail) {
		this.suppressEmail = suppressEmail;
	}

	public void setSuppressName(boolean suppressName) {
		this.suppressName = suppressName;
	}

	public void setSuppressPersonal(boolean suppressPersonal) {
		this.suppressPersonal = suppressPersonal;
	}

	public void setSuppressPhone(boolean suppressPhone) {
		this.suppressPhone = suppressPhone;
	}

	/** {@inheritDoc} */
    public void refresh(){
    	
    }
    
    /** {@inheritDoc} */
    public void prepareForWorkflow(){
    	
    }
}
