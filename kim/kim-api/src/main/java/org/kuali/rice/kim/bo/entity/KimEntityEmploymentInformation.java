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

import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.Inactivatable;

/**
 * employment information for a KIM identity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface KimEntityEmploymentInformation extends Inactivatable {

    /**
     * Gets this {@link KimEntityEmploymentInformation}'s id.
     * @return the id for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	String getEntityEmploymentId();
	
	/**
     * Gets this {@link KimEntityEmploymentInformation}'s identity affiliation id.
     * @return the identity affiliation id for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	String getEntityAffiliationId();
	
	/**
     * Gets this {@link KimEntityEmploymentInformation}'s employee status code.
     * @return the employee status code for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	String getEmployeeStatusCode();
	
	/**
     * Gets this {@link KimEntityEmploymentInformation}'s employee type code.
     * @return the employee type code for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	String getEmployeeTypeCode();
	
	/**
     * Gets this {@link KimEntityEmploymentInformation}'s primary department code.
     * @return the primary department code for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	String getPrimaryDepartmentCode();
	
	/**
     * Gets this {@link KimEntityEmploymentInformation}'s employee id.
     * @return the employee id for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	String getEmployeeId();
	
	/**
     * Gets this {@link KimEntityEmploymentInformation}'s employment record id.
     * @return the employment record id for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	String getEmploymentRecordId();
	
	/**
     * Gets this {@link KimEntityEmploymentInformation}'s base salary amount.
     * @return the base salary amount for this {@link KimEntityEmploymentInformation}, or null if none has been assigned.
     */
	KualiDecimal getBaseSalaryAmount();
	
	boolean isPrimary();

}
