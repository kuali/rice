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

import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationContract;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract;
import org.kuali.rice.kim.api.identity.name.EntityNameContract;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographicsContract;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicityContract;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferencesContract;
import org.kuali.rice.kim.api.identity.residency.EntityResidencyContract;
import org.kuali.rice.kim.api.identity.type.EntityTypeDataContract;
import org.kuali.rice.kim.api.identity.visa.EntityVisaContract;

import java.util.List;


/**
 * Represents an Entity (person/vendor/system) within the Rice system. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimEntity extends Inactivatable {
	
    /**
     * Gets this {@link KimEntity}'s identity id.
     * @return the id for this {@link KimEntity}, or null if none has been assigned.
     */
	String getEntityId();
	
	/**
	 * Gets this {@link KimEntity}'s identity types
	 * @return the List of {@link EntityTypeDataContract}S for this {@link KimEntity}.
	 * The returned List will never be null, an empty List will be assigned and returned if needed. 
	 */
	List<? extends EntityTypeDataContract> getEntityTypes();
	
    /**
     * Gets this {@link KimEntity}'s principals
     * @return the List of {@link PrincipalContract}s for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends PrincipalContract> getPrincipals();

	
    /**
     * Gets this {@link KimEntity}'s external identifiers
     * @return the List of {@link KimEntityExternalIdentifier}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends KimEntityExternalIdentifier> getExternalIdentifiers();

    /**
     * Gets this {@link KimEntity}'s affiliations
     * @return the List of {@link EntityAffiliationContract}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityAffiliationContract> getAffiliations();

	/**
	 * Gets this {@link KimEntity}'s names
	 * @return the List of {@link EntityNameContract}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
	 */
	List<? extends EntityNameContract> getNames();
	
	
    /**
     * Gets this {@link KimEntity}'s employment information List
     * @return the List of {@link org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityEmploymentContract> getEmploymentInformation();

    /**
     * Gets this {@link KimEntity}'s privacy preferences
     * @return the {@link org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences} for this {@link KimEntity},
     * or null if none has been assigned.
     */	
	EntityPrivacyPreferencesContract getPrivacyPreferences();
	
	/**
	 * Gets this {@link KimEntity}'s demographic information
	 * @return the {@link EntityBioDemographicsContract} for this {@link KimEntity},
	 * or null if none has been assigned.
	 */
	EntityBioDemographicsContract getBioDemographics();
	
    /**
     * Gets this {@link KimEntity}'s citizenship information
     * @return the List of {@link EntityCitizenshipContract}s for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	List<? extends EntityCitizenshipContract> getCitizenships();
	
	/**
	 * Gets this {@link KimEntity}'s identity type for the given type code
	 * @param entityTypeCode the type code
	 * @return the EntityEntityType object corresponding to the given code or null if this
	 * identity does not have data for that type.
	 */
	EntityTypeDataContract getEntityType( String entityTypeCode );
	
	/**
	 * Gets this {@link KimEntity}'s employment information
	 * @return the primary {@link org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract} for this {@link KimEntity},
	 * or null if none has been assigned.
	 */
	EntityEmploymentContract getPrimaryEmployment();

	/**
	 * Gets this {@link KimEntity}'s default affiliation
     * @return the default {@link EntityAffiliationContract} for the identity.  If no default is defined, then
     * it returns the first one found.  If none are defined, it returns null.
     */
	EntityAffiliationContract getDefaultAffiliation();
	
	/**
	 * Gets this {@link KimEntity}'s external identifier for the given type code
	 * @param externalIdentifierTypeCode the type code
     * @return the {@link KimEntityExternalIdentifier} for this {@link KimEntity}, or null if none has been assigned.
     */
	KimEntityExternalIdentifier getEntityExternalIdentifier( String externalIdentifierTypeCode );
	
	/** 
	 * Gets this {@link KimEntity}'s default name
	 * @return the default {@link EntityNameContract} record for the identity.  If no default is defined, then
	 * it returns the first one found.  If none are defined, it returns null.
	 */
	EntityNameContract getDefaultName();

    /**
     * Gets this {@link KimEntity}'s ethnicities
     * @return the List of {@link org.kuali.rice.kim.api.identity.personal.EntityEthnicityContract}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	public List<? extends EntityEthnicityContract> getEthnicities();

    /**
     * Gets this {@link KimEntity}'s residencies
     * @return the List of {@link org.kuali.rice.kim.api.identity.residency.EntityResidencyContract}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	public List<? extends EntityResidencyContract> getResidencies();

    /**
     * Gets this {@link KimEntity}'s visas
     * @return the List of {@link org.kuali.rice.kim.api.identity.visa.EntityVisaContract}S for this {@link KimEntity}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
	public List<? extends EntityVisaContract> getVisas();

}
