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

import static org.kuali.rice.kim.bo.entity.dto.DtoUtils.unNullify;

import org.kuali.rice.kim.bo.entity.KimEntityAddress;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityAddressInfo extends KimDefaultableInfo implements KimEntityAddress {

	private static final long serialVersionUID = 1L;

	protected String entityAddressId = "";
	protected String addressTypeCode = "";
	protected String entityTypeCode = "";
	protected String cityName = "";
	protected String stateCode = "";
	protected String postalCode = "";
	protected String countryCode = "";
	protected String line1 = "";
	protected String line2 = "";
	protected String line3 = "";

	protected boolean suppressAddress = false;
	/**
	 * 
	 */
	public KimEntityAddressInfo() {
	}
	/**
	 * 
	 */
	public KimEntityAddressInfo( KimEntityAddress addr ) {
		if ( addr != null ) {
		    this.entityAddressId = unNullify( addr.getEntityAddressId() );
			this.entityTypeCode = unNullify( addr.getEntityTypeCode() );
			this.addressTypeCode = unNullify( addr.getAddressTypeCode() );
			this.cityName = unNullify( addr.getCityNameUnmasked() );
			this.stateCode = unNullify( addr.getStateCodeUnmasked() );
			this.postalCode = unNullify( addr.getPostalCodeUnmasked() );
			this.countryCode = unNullify( addr.getCountryCodeUnmasked() );
			this.line1 = unNullify( addr.getLine1Unmasked() );
			this.line2 = unNullify( addr.getLine2Unmasked() );
			this.line3 = unNullify( addr.getLine3Unmasked() );
			this.dflt = addr.isDefault();
			this.active = addr.isActive();
			this.suppressAddress = addr.isSuppressAddress();
		}
	}
	   
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

	public void setAddressTypeCode(String addressTypeCode) {
		this.addressTypeCode = addressTypeCode;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public void setEntityAddressId(String entityAddressId) {
		this.entityAddressId = entityAddressId;
	}
	public String getEntityTypeCode() {
		return this.entityTypeCode;
	}
	public void setEntityTypeCode(String entityTypeCode) {
		this.entityTypeCode = entityTypeCode;
	}
	
    public boolean isSuppressAddress() {
        return this.suppressAddress;
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
