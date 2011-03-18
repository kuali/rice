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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.bo.entity.KimEntityPrivacyPreferences;
import org.kuali.rice.kim.bo.reference.AddressType;
import org.kuali.rice.kim.bo.reference.impl.AddressTypeImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_ADDR_T")
public class KimEntityAddressImpl extends KimDefaultableEntityDataBase implements KimEntityAddress {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ENTITY_ADDR_ID")
	protected String entityAddressId;

	@Column(name = "ENTITY_ID")
	protected String entityId;

	@Column(name = "ADDR_TYP_CD")
	protected String addressTypeCode;

	@Column(name = "ENT_TYP_CD")
	protected String entityTypeCode;

	@Column(name = "CITY_NM")
	protected String cityName;

	@Column(name = "POSTAL_STATE_CD")
	protected String stateCode;

	@Column(name = "POSTAL_CD")
	protected String postalCode;

	@Column(name = "POSTAL_CNTRY_CD")
	protected String countryCode;

	@Column(name = "ADDR_LINE_1")
	protected String line1;

	@Column(name = "ADDR_LINE_2")
	protected String line2;

	@Column(name = "ADDR_LINE_3")
	protected String line3;

	@ManyToOne(targetEntity=AddressTypeImpl.class, fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinColumn(name = "ADDR_TYP_CD", insertable = false, updatable = false)
	protected AddressType addressType;
	
	@Transient
    protected Boolean suppressAddress;

	// Waiting until we pull in from KFS
	// protected State state;
	// protected PostalCode postalCode;
	// protected Country country;

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getAddressTypeCode()
	 */
	public String getAddressTypeCode() {
		return addressTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getCityName()
	 */
	public String getCityName() {
	    if (isSuppressAddress()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return cityName;
	}
	

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getCountryCode()
	 */
	public String getCountryCode() {
	    if (isSuppressAddress()) {
            return "XX";
        }
		return countryCode;
	}
	
	

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getEntityAddressId()
	 */
	public String getEntityAddressId() {
		return entityAddressId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine1()
	 */
	public String getLine1() {
	    if (isSuppressAddress()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return line1;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine2()
	 */
	public String getLine2() {
	    if (isSuppressAddress()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return line2;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine3()
	 */
	public String getLine3() {
	    if (isSuppressAddress()) {
            return KimConstants.RESTRICTED_DATA_MASK;
        }
		return line3;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getPostalCode()
	 */
	public String getPostalCode() {
	    if (isSuppressAddress()) {
            return "00000";
        }
		return postalCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getStateCode()
	 */
	public String getStateCode() {
	    if (isSuppressAddress()) {
            return "XX";
        }
		return stateCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setAddressTypeCode(java.lang.String)
	 */
	public void setAddressTypeCode(String addressTypeCode) {
		this.addressTypeCode = addressTypeCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setCityName(java.lang.String)
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setCountryCode(java.lang.String)
	 */
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setLine1(java.lang.String)
	 */
	public void setLine1(String line1) {
		this.line1 = line1;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setLine2(java.lang.String)
	 */
	public void setLine2(String line2) {
		this.line2 = line2;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setLine3(java.lang.String)
	 */
	public void setLine3(String line3) {
		this.line3 = line3;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setPostalCode(java.lang.String)
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#setStateCode(java.lang.String)
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
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

	public void setEntityAddressId(String entityAddressId) {
		this.entityAddressId = entityAddressId;
	}

	public AddressType getAddressType() {
		return this.addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#isSuppressAddress()
     */
    public boolean isSuppressAddress() {
        if (suppressAddress != null) {
            return suppressAddress.booleanValue();
        }
        KimEntityPrivacyPreferences privacy = KIMServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());

        suppressAddress = false;
        if (privacy != null) {
            suppressAddress = privacy.isSuppressAddress();
        } 
        return suppressAddress.booleanValue();
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getCityNameUnmasked()
     */
    public String getCityNameUnmasked() {
        return this.cityName;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getCountryCodeUnmasked()
     */
    public String getCountryCodeUnmasked() {
        return this.countryCode;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine1Unmasked()
     */
    public String getLine1Unmasked() {
        return this.line1;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine2Unmasked()
     */
    public String getLine2Unmasked() {
        return this.line2;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getLine3Unmasked()
     */
    public String getLine3Unmasked() {
        return this.line3;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getPostalCodeUnmasked()
     */
    public String getPostalCodeUnmasked() {
        return this.postalCode;
    }

    /**
     * @see org.kuali.rice.kim.bo.entity.KimEntityAddress#getStateCodeUnmasked()
     */
    public String getStateCodeUnmasked() {
        return this.stateCode;
    }
}
