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

import org.kuali.rice.kim.api.entity.citizenship.EntityCitizenshipContract;
import org.kuali.rice.kim.api.entity.privacy.EntityPrivacyPreferencesContract;
import org.kuali.rice.kim.api.entity.type.EntityTypeDataContract;
import org.kuali.rice.kns.bo.Inactivateable;


/**
 * Represents an Entity (person/vendor/system) within the Rice system. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntity extends Inactivateable {
	
    /**
     * Gets this {@link KimEntity}'s entity id.
     * @return the id for this {@link KimEntity}, or null if none has been assigned.
     */
	String getEntityId();
	
	/**
	 * Gets this {@link KimEntity}'s entity types
	 * @return the List of {@link EntityTypeDataContract}S for this {@link KimEntity}.
	 * The returned List will never be null, an empty List will be assigned and returned if needed. 
	 */
	List<? extends EntityTypeDataContract> getEntityTypes();
	
    /**
     * Gets this {@link KimEntity}'s principals
     * @return the List of {@link KimPrincipal}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimPrincipal> getPrincipals();
	
    /**
     * Gets this {@link KimEntity}'s external identifiers
     * @return the List of {@link KimEntityExternalIdentifier}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimEntityExternalIdentifier> getExternalIdentifiers();

    /**
     * Gets this {@link KimEntity}'s affiliations
     * @return the List of {@link KimEntityAffiliation}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimEntityAffiliation> getAffiliations();

	/**
	 * Gets this {@link KimEntity}'s names
	 * @return the List of {@link KimEntityName}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
	 */
	List<? extends KimEntityName> getNames();
	
	
    /**
     * Gets this {@link KimEntity}'s employment information List
     * @return the List of {@link KimEntityEmploymentInformation}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimEntityEmploymentInformation> getEmploymentInformation();

    /**
     * Gets this {@link KimEntity}'s privacy preferences
     * @return the {@link org.kuali.rice.kim.api.entity.privacy.EntityPrivacyPreferences} for this {@link KimEntity},
     * or null if none has been assigned.
     */	
	EntityPrivacyPreferencesContract getPrivacyPreferences();
	
	/**
	 * Gets this {@link KimEntity}'s demographic information
	 * @return the {@link KimEntityBioDemographics} for this {@link KimEntity}, 
	 * or null if none has been assigned.
	 */
	KimEntityBioDemographics getBioDemographics();
	
    /**
     * Gets this {@link KimEntity}'s citizenship information
     * @return the List of {@link EntityCitizenshipContract}s for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityCitizenshipContract> getCitizenships();
	
	/**
	 * Gets this {@link KimEntity}'s entity type for the given type code
	 * @param entityTypeCode the type code
	 * @return the EntityEntityType object corresponding to the given code or null if this
	 * entity does not have data for that type.
	 */
	EntityTypeDataContract getEntityType( String entityTypeCode );
	
	/**
	 * Gets this {@link KimEntity}'s employment information
	 * @return the primary {@link KimEntityEmploymentInformation} for this {@link KimEntity}, 
	 * or null if none has been assigned.
	 */
	KimEntityEmploymentInformation getPrimaryEmployment();

	/**
	 * Gets this {@link KimEntity}'s default affiliation
     * @return the default {@link KimEntityAffiliation} for the entity.  If no default is defined, then
     * it returns the first one found.  If none are defined, it returns null.
     */
	KimEntityAffiliation getDefaultAffiliation();
	
	/**
	 * Gets this {@link KimEntity}'s external identifier for the given type code
	 * @param externalIdentifierTypeCode the type code
     * @return the {@link KimEntityExternalIdentifier} for this {@link KimEntity}, or null if none has been assigned.
     */
	KimEntityExternalIdentifier getEntityExternalIdentifier( String externalIdentifierTypeCode );
	
	/** 
	 * Gets this {@link KimEntity}'s default name
	 * @return the default {@link KimEntityName} record for the entity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	KimEntityName getDefaultName();

    /**
     * Gets this {@link KimEntity}'s ethnicities
     * @return the List of {@link KimEntityEthnicity}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	public List<? extends KimEntityEthnicity> getEthnicities();

    /**
     * Gets this {@link KimEntity}'s residencies
     * @return the List of {@link KimEntityResidency}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	public List<? extends KimEntityResidency> getResidencies();

    /**
     * Gets this {@link KimEntity}'s visas
     * @return the List of {@link KimEntityVisa}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	public List<? extends KimEntityVisa> getVisas();

}
