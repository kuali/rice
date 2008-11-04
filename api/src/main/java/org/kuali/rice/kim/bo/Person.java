/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Person object for use by the KNS and KNS-based applications.  This provides an abstraction layer
 * between application code and the KIM objects to simplify use. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public interface Person extends ExternalizableBusinessObject {
	
	String getPrincipalId();
	String getPrincipalName();
	String getEntityId();
	String getEntityTypeCode();
	
	/**
	 * The first name from the default name record for the entity.
	 */
	String getFirstName();
	String getMiddleName();
	String getLastName();
	
	/*
	 * Method which composites the first, middle and last names.
	 */
	String getName();

	String getEmailAddress();
	
	/**
	 * Returns line1 of the default address for the Person.  Will lazy-load the information from the
	 * IdentityManagementService if requested. 
	 */
	String getAddressLine1();
	/**
	 * Returns line2 of the default address for the Person.  Will lazy-load the information from the
	 * IdentityManagementService if requested. 
	 */
	String getAddressLine2();
	/**
	 * Returns line3 of the default address for the Person.  Will lazy-load the information from the
	 * IdentityManagementService if requested. 
	 */
	String getAddressLine3();
	/**
	 * Returns the city name from the default address for the Person.  Will lazy-load the information from the
	 * IdentityManagementService if requested. 
	 */
	String getAddressCityName();
	/**
	 * Returns the state code from the default address for the Person.  Will lazy-load the information from the
	 * IdentityManagementService if requested. 
	 */
	String getAddressStateCode();
	/**
	 * Returns the postal code from the default address for the Person.  Will lazy-load the information from the
	 * IdentityManagementService if requested. 
	 */
	String getAddressPostalCode();
	/**
	 * Returns the country code from the default address for the Person.  Will lazy-load the information from the
	 * IdentityManagementService if requested. 
	 */
	String getAddressCountryCode();
	
	/** Returns the default phone number for the entity.
	 */
	String getPhoneNumber();

	String getCampusCode();
		
	Map<String,String> getExternalIdentifiers();
	
	/** Checks whether the person has an affiliation of a particular type: staff/faculty/student/etc... */
	boolean hasAffiliationOfType( String affiliationTypeCode );
	
	List<String> getCampusCodesForAffiliationOfType(String affiliationTypeCode);
	
	String getEmployeeStatusCode();
	String getEmployeeTypeCode();
	KualiDecimal getBaseSalaryAmount();
	
	String getExternalId( String externalIdentifierTypeCode );
	
	String getPrimaryDepartmentCode();
	
	String getEmployeeId();
	
}
