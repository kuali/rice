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

import java.util.List;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.Inactivateable;


/**
 * Represents an Entity (person/vendor/system) within the Rice system. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntity extends Inactivateable, ExternalizableBusinessObject {
	
	String getEntityId();
	
	List<? extends KimEntityEntityType> getEntityTypes();
	List<? extends KimPrincipal> getPrincipals();
	
	List<? extends KimEntityExternalIdentifier> getExternalIdentifiers();
	List<? extends KimEntityAffiliation> getAffiliations();

	/**
	 * Return the list of EntityName objects associated with this EntityType.
	 * 
	 * The returned list will never be null, the implementation will generate an
	 * empty list as needed.
	 */
	List<? extends KimEntityName> getNames();
	
	
	/**
	 * Returns the employment information object for this Entity.
	 * 
	 * Implementations will create an object if necessary before returning.  
	 * This method shall not return null.
	 * 
	 */
	List<? extends KimEntityEmploymentInformation> getEmploymentInformation();

	/**
	 * Returns the privacy preferences object for this Entity.
	 * 
	 * Implementations will create an object if necessary before returning.  
	 * This method shall not return null.
	 * 
	 */
	KimEntityPrivacyPreferences getPrivacyPreferences();
	
	/**
	 * Returns the demographic information for this Entity.
	 * 
	 * Implementations will create an object if necessary before returning.  
	 * This method shall not return null.
	 * 
	 */
	KimEntityBioDemographics getBioDemographics();
	
	List<? extends KimEntityCitizenship> getCitizenships();
	
	/**
	 * Returns the EntityEntityType object corresponding to the given code or null if this
	 * entity does not have data for that type.
	 */
	KimEntityEntityType getEntityType( String entityTypeCode );
	
	KimEntityEmploymentInformation getPrimaryEmployment();
	KimEntityAffiliation getDefaultAffiliation();
	KimEntityExternalIdentifier getEntityExternalIdentifier( String externalIdentifierTypeCode );
	
	/** Returns the default name record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityName getDefaultName();

	public List<? extends KimEntityEthnicity> getEthnicities();

	public List<? extends KimEntityResidency> getResidencies();

	public List<? extends KimEntityVisa> getVisas();

}
