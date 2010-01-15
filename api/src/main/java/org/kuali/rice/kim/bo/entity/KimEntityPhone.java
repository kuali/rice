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
 * phone information for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityPhone extends KimDefaultableEntityTypeData {

    /**
     * Gets this {@link KimEntityPhone}'s id.
     * @return the id for this {@link KimEntityPhone}, or null if none has been assigned.
     */
	String getEntityPhoneId();
	
	/**
     * Gets this {@link KimEntityPhone}'s type code.
     * @return the type code for this {@link KimEntityPhone}, or null if none has been assigned.
     */
	String getPhoneTypeCode();
	
	/**
     * Gets this {@link KimEntityPhone}'s phone number.
     * @return the phone number for this {@link KimEntityPhone}, or null if none has been assigned.
     */
	String getPhoneNumber();
	
	/**
     * Gets this {@link KimEntityPhone}'s extension number.
     * @return the extension number for this {@link KimEntityPhone}, or null if none has been assigned.
     */
	String getExtensionNumber();
	
	/**
     * Gets this {@link KimEntityPhone}'s country code.
     * @return the country code for this {@link KimEntityPhone}, or null if none has been assigned.
     */
	String getCountryCode();
	
	/**
     * Gets this {@link KimEntityPhone}'s unmasked phone number.
     * @return the unmasked phone number for this {@link KimEntityPhone}, or null if none has been assigned.
     */
	String getPhoneNumberUnmasked();
	
	/**
     * Gets this {@link KimEntityPhone}'s unmasked extension number.
     * @return the unmasked extension number for this {@link KimEntityPhone}, or null if none has been assigned.
     */
    String getExtensionNumberUnmasked();
    
    /**
     * Gets this {@link KimEntityPhone}'s unmasked country code.
     * @return the unmasked country code for this {@link KimEntityPhone}, or null if none has been assigned.
     */
    String getCountryCodeUnmasked();
    
    /**
     * Gets this {@link KimEntityPhone}'s formatted phone number.
     * @return the formatted phone number for this {@link KimEntityPhone}, or null if none has been assigned.
     */
    String getFormattedPhoneNumber();
    
    /**
     * Gets this {@link KimEntityPhone}'s unmasked formatted phone number.
     * @return the unmasked formatted phone number for this {@link KimEntityPhone}, or null if none has been assigned.
     */
	String getFormattedPhoneNumberUnmasked();

	boolean isSuppressPhone();
}
