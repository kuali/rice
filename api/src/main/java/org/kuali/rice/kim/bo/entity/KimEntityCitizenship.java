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

import org.kuali.rice.kns.bo.Inactivateable;

/**
 * citizenship information for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface KimEntityCitizenship extends Inactivateable {

    /**
     * Gets this {@link KimEntityCitizenship}'s id.
     * @return the id for this {@link KimEntityCitizenship}, or null if none has been assigned.
     */
	String getEntityCitizenshipId();
	
	/**
     * Gets this {@link KimEntityCitizenship}'s citizenship status code.
     * @return the citizenship status code for this {@link KimEntityCitizenship}, or null if none has been assigned.
     */
	String getCitizenshipStatusCode();
	
	/**
     * Gets this {@link KimEntityCitizenship}'s country code.
     * @return the country code for this {@link KimEntityCitizenship}, or null if none has been assigned.
     */
	String getCountryCode();
	
	/**
     * Gets this {@link KimEntityCitizenship}'s start date.
     * @return the start date for this {@link KimEntityCitizenship}, or null if none has been assigned.
     */
	Date getStartDate();
	
	/**
     * Gets this {@link KimEntityCitizenship}'s end date.
     * @return the end date for this {@link KimEntityCitizenship}, or null if none has been assigned.
     */
	Date getEndDate();
}
