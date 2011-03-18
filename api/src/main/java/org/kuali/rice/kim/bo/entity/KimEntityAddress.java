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
package org.kuali.rice.kim.bo.entity;

/**
 * address information for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityAddress extends KimDefaultableEntityTypeData {
	
    /**
     * Gets this {@link KimEntityAddress}'s address id.
     * @return the address for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getEntityAddressId();
	
    /**
     * Gets this {@link KimEntityAddress}'s address type code.
     * @return the address type code for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getAddressTypeCode();

    /**
     * Gets this {@link KimEntityAddress}'s first line.
     * @return the first line for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getLine1();
	
	/**
	 * Gets this {@link KimEntityAddress}'s second line.
	 * @return the second line for this {@link KimEntityAddress}, or null if none has been assigned.
	 */
	String getLine2();
	
	/**
     * Gets this {@link KimEntityAddress}'s third line.
     * @return the third line for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getLine3();
	
	/**
     * Gets this {@link KimEntityAddress}'s city name.
     * @return the city name for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getCityName();
	
	/**
     * Gets this {@link KimEntityAddress}'s state code.
     * @return the state code for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getStateCode();
	
	/**
     * Gets this {@link KimEntityAddress}'s postal code.
     * @return the postal code for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getPostalCode();
	
	/**
     * Gets this {@link KimEntityAddress}'s country code.
     * @return the country code for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getCountryCode();
	
	/**
     * Gets this {@link KimEntityAddress}'s unmasked first line.
     * @return the unmasked first line for this {@link KimEntityAddress}, or null if none has been assigned.
     */
	String getLine1Unmasked();
	
	/**
     * Gets this {@link KimEntityAddress}'s unmasked second line.
     * @return the unmasked second line for this {@link KimEntityAddress}, or null if none has been assigned.
     */
    String getLine2Unmasked();
    
    /**
     * Gets this {@link KimEntityAddress}'s unmasked third line.
     * @return the unmasked third line for this {@link KimEntityAddress}, or null if none has been assigned.
     */
    String getLine3Unmasked();
    
    /**
     * Gets this {@link KimEntityAddress}'s unmasked city name.
     * @return the unmasked city name for this {@link KimEntityAddress}, or null if none has been assigned.
     */
    String getCityNameUnmasked();
    
    /**
     * Gets this {@link KimEntityAddress}'s unmasked state code.
     * @return the unmasked state code for this {@link KimEntityAddress}, or null if none has been assigned.
     */
    String getStateCodeUnmasked();
    
    /**
     * Gets this {@link KimEntityAddress}'s unmasked postal code.
     * @return the unmasked postal code for this {@link KimEntityAddress}, or null if none has been assigned.
     */
    String getPostalCodeUnmasked();
    
    /**
     * Gets this {@link KimEntityAddress}'s unmasked country code.
     * @return the unmasked country code for this {@link KimEntityAddress}, or null if none has been assigned.
     */
    String getCountryCodeUnmasked();
    
    boolean isSuppressAddress();
}
