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

import java.util.Date;

/**
 * demographic information for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntityBioDemographics {

    /**
     * Gets this {@link KimEntityBioDemographics}'s entity id.
     * @return the entity id for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getEntityId();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s deceased date.
     * @return the deceased date for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	Date getDeceasedDate();

	/**
     * Gets this {@link KimEntityBioDemographics}'s birth date.
     * @return the birth date for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	Date getBirthDate();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s gender code.
     * @return the gender code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getGenderCode();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s marital status code.
     * @return the marital status code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getMaritalStatusCode();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s primary language code.
     * @return the primary language code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getPrimaryLanguageCode();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s secondary language code.
     * @return the secondary language code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getSecondaryLanguageCode();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s country of birth code.
     * @return the country of birth code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getCountryOfBirthCode();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s birth state code.
     * @return the birth state code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getBirthStateCode();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s city of birth.
     * @return the city of birth for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getCityOfBirth();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s geographic origin.
     * @return the geographic origin for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getGeographicOrigin();

	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked birth date.
     * @return the unmasked birth date for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	Date getBirthDateUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked gender code.
     * @return the unmasked gender code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getGenderCodeUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked martial status code.
     * @return the unmasked martial status code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getMaritalStatusCodeUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked primary language code.
     * @return the unmasked primary language code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getPrimaryLanguageCodeUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked secondary language code.
     * @return the unmasked secondary language code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getSecondaryLanguageCodeUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked country of birth code.
     * @return the unmasked country of birth code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getCountryOfBirthCodeUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmaksed birth state code.
     * @return the unmaksed birth state code for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getBirthStateCodeUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked city of birth.
     * @return the unmasked city of birth for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getCityOfBirthUnmasked();
	
	/**
     * Gets this {@link KimEntityBioDemographics}'s unmasked geographic origin.
     * @return the unmasked geographic origin for this {@link KimEntityBioDemographics}, or null if none has been assigned.
     */
	String getGeographicOriginUnmasked();

	boolean isSuppressPersonal();

}
