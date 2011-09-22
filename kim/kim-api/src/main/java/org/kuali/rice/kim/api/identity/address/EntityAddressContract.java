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
package org.kuali.rice.kim.api.identity.address;

import org.kuali.rice.core.api.mo.common.Defaultable;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;

/**
 * address information for a KIM identity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityAddressContract extends Versioned, GloballyUnique, Defaultable, Inactivatable, Identifiable {

    /**
     * Gets this id of the parent identity object.
     * @return the identity id for this {@link EntityAddressContract}
     */
    String getEntityId();

    /**
     * Gets this entityTypeCode of the {@link EntityAddressContract}'s object.
     * @return the identity type code for this {@link EntityAddressContract}
     */
    String getEntityTypeCode();

    /**
     * Gets this {@link EntityAddressContract}'s address type code.
     * @return the address type for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	CodedAttributeContract getAddressType();

    /**
     * Gets this {@link EntityAddressContract}'s first line.
     * @return the first line for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	String getLine1();
	
	/**
	 * Gets this {@link EntityAddressContract}'s second line.
	 * @return the second line for this {@link EntityAddressContract}, or null if none has been assigned.
	 */
	String getLine2();
	
	/**
     * Gets this {@link EntityAddressContract}'s third line.
     * @return the third line for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	String getLine3();
	
	/**
     * Gets this {@link EntityAddressContract}'s city name.
     * Corresponds to PESC City.
     * @return the city name for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	String getCity();
	
	/**
     * Gets this {@link EntityAddressContract}'s state code.
     * Corresponds to PESC StateProvinceCode.
     * @return the state code for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	String getStateProvinceCode();
	
	/**
     * Gets this {@link EntityAddressContract}'s postal code.
     * @return the postal code for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	String getPostalCode();
	
	/**
     * Gets this {@link EntityAddressContract}'s country code.
     * @return the country code for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	String getCountryCode();
	
	/**
     * Gets this {@link EntityAddressContract}'s unmasked first line.
     * @return the unmasked first line for this {@link EntityAddressContract}, or null if none has been assigned.
     */
	String getLine1Unmasked();
	
	/**
     * Gets this {@link EntityAddressContract}'s unmasked second line.
     * @return the unmasked second line for this {@link EntityAddressContract}, or null if none has been assigned.
     */
    String getLine2Unmasked();
    
    /**
     * Gets this {@link EntityAddressContract}'s unmasked third line.
     * @return the unmasked third line for this {@link EntityAddressContract}, or null if none has been assigned.
     */
    String getLine3Unmasked();
    
    /**
     * Gets this {@link EntityAddressContract}'s unmasked city name.
     * @return the unmasked city name for this {@link EntityAddressContract}, or null if none has been assigned.
     */
    String getCityUnmasked();
    
    /**
     * Gets this {@link EntityAddressContract}'s unmasked state code.
     * @return the unmasked state code for this {@link EntityAddressContract}, or null if none has been assigned.
     */
    String getStateProvinceCodeUnmasked();
    
    /**
     * Gets this {@link EntityAddressContract}'s unmasked postal code.
     * @return the unmasked postal code for this {@link EntityAddressContract}, or null if none has been assigned.
     */
    String getPostalCodeUnmasked();
    
    /**
     * Gets this {@link EntityAddressContract}'s unmasked country code.
     * @return the unmasked country code for this {@link EntityAddressContract}, or null if none has been assigned.
     */
    String getCountryCodeUnmasked();

    /**
     * Returns a boolean value that determines if address fields should be suppressed.
     * @return boolean value that determines if address should be suppressed.
     */
    boolean isSuppressAddress();
}
