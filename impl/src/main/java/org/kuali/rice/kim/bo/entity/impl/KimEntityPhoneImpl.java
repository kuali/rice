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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.entity.KimEntityPhone;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.reference.PhoneType;
import org.kuali.rice.kim.bo.reference.impl.PhoneTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_PHONE_T")
public class KimEntityPhoneImpl extends KimDefaultableEntityDataBase implements KimEntityPhone {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_PHONE_ID")
	protected String entityPhoneId;
	
	@Column(name = "ENTITY_ID")
	protected String entityId;
	
	@Column(name = "ENT_TYP_CD")
	protected String entityTypeCode;
	
	@Column(name = "PHONE_TYP_CD")
	protected String phoneTypeCode;
	
	@Column(name = "PHONE_NBR")
	protected String phoneNumber;
	
	@Column(name = "PHONE_EXTN_NBR")
	protected String extensionNumber;
	
	@Column(name = "POSTAL_CNTRY_CD")
	protected String countryCode;
	
	@ManyToOne(targetEntity=PhoneTypeImpl.class, fetch = FetchType.EAGER, cascade = {})
	@JoinColumn(name = "PHONE_TYP_CD", insertable = false, updatable = false)
	protected PhoneType phoneType;
	
	@Transient
    protected Boolean suppressPhone;

	// Waiting until we pull in from KFS
	// protected Country country;
	
	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getCountryCode()
	 */
	public String getCountryCode() {
	    if (isSuppressPhone()) {
            return "XX";
        }
		return countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getEntityPhoneId()
	 */
	public String getEntityPhoneId() {
		return entityPhoneId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getExtensionNumber()
	 */
	public String getExtensionNumber() {
	    if (isSuppressPhone()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return extensionNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneNumber()
	 */
	public String getPhoneNumber() {
	    if (isSuppressPhone()) {
            return "000-000-0000";
        }
		return phoneNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneTypeCode()
	 */
	public String getPhoneTypeCode() {
		return phoneTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setCountryCode(java.lang.String)
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setExtensionNumber(java.lang.String)
	 */
	public void setExtensionNumber(String extensionNumber) {
		this.extensionNumber = extensionNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setPhoneNumber(java.lang.String)
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#setPhoneTypeCode(java.lang.String)
	 */
	public void setPhoneTypeCode(String phoneTypeCode) {
		this.phoneTypeCode = phoneTypeCode;
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

	public PhoneType getPhoneType() {
		return this.phoneType;
	}

	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public void setEntityPhoneId(String entityPhoneId) {
		this.entityPhoneId = entityPhoneId;
	}

	public String getFormattedPhoneNumber() {
	    if (isSuppressPhone()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		StringBuffer sb = new StringBuffer( 30 );
		
		// TODO: get extension from country code table
		// TODO: append "+xxx" if country is not the default country
		sb.append( getPhoneNumber() );
		if ( StringUtils.isNotBlank( getExtensionNumber() ) ) {
			sb.append( " x" );
			sb.append( getExtensionNumber() );
		}
		
		return sb.toString();
	}

    public boolean isSuppressPhone() {
        if (suppressPhone != null) {
            return suppressPhone.booleanValue();
        }
        KimEntityPrivacyPreferences privacy = KIMServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());

        suppressPhone = false;
        if (privacy != null) {
            suppressPhone = privacy.isSuppressPhone();
        } 
        return suppressPhone.booleanValue();
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getCountryCodeUnmasked()
     */
    public String getCountryCodeUnmasked() {
        return this.countryCode;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getExtensionNumberUnmasked()
     */
    public String getExtensionNumberUnmasked() {
        return this.extensionNumber;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getFormattedPhoneNumberUnmasked()
     */
    public String getFormattedPhoneNumberUnmasked() {
        StringBuffer sb = new StringBuffer( 30 );
        
        // TODO: get extension from country code table
        // TODO: append "+xxx" if country is not the default country
        sb.append( this.phoneNumber );
        if ( StringUtils.isNotBlank( this.extensionNumber ) ) {
            sb.append( " x" );
            sb.append( this.extensionNumber );
        }
        
        return sb.toString();
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityPhone#getPhoneNumberUnmasked()
     */
    public String getPhoneNumberUnmasked() {
        return this.phoneNumber;
    }
}
