/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.api.identity.entity;

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Historical;
import org.kuali.rice.core.api.mo.common.Identifiable;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationHistoryContract;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistoryContract;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistoryContract;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierHistoryContract;
import org.kuali.rice.kim.api.identity.name.EntityNameContract;
import org.kuali.rice.kim.api.identity.name.EntityNameHistoryContract;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographicsContract;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographicsHistoryContract;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicityHistoryContract;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.identity.principal.PrincipalHistoryContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyHistoryContract;
import org.kuali.rice.kim.api.identity.residency.EntityResidencyHistoryContract;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoHistoryContract;
import org.kuali.rice.kim.api.identity.visa.EntityVisaHistoryContract;

import java.util.List;


/**
 * Represents an Entity (person/vendor/system) within the Rice system. 
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface EntityHistoryContract extends EntityContract, Historical {

    /**
     * Gets this {@link EntityContract}'s identity types
     * @return the List of {@link org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed.
     */
    List<? extends EntityTypeContactInfoHistoryContract> getEntityTypeContactInfos();

    /**
     * Gets this {@link EntityContract}'s principals
     * @return the List of {@link PrincipalContract}s for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    List<? extends PrincipalHistoryContract> getPrincipals();


    /**
     * Gets this {@link EntityContract}'s external identifiers
     * @return the List of {@link org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    List<? extends EntityExternalIdentifierHistoryContract> getExternalIdentifiers();

    /**
     * Gets this {@link EntityContract}'s affiliations
     * @return the List of {@link EntityAffiliationContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    List<? extends EntityAffiliationHistoryContract> getAffiliations();

    /**
     * Gets this {@link EntityContract}'s names
     * @return the List of {@link EntityNameContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    List<? extends EntityNameHistoryContract> getNames();


    /**
     * Gets this {@link EntityContract}'s employment information List
     * @return the List of {@link org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    List<? extends EntityEmploymentHistoryContract> getEmploymentInformation();

    /**
     * Gets this {@link EntityContract}'s privacy preferences
     * @return the {@link org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences} for this {@link EntityContract},
     * or null if none has been assigned.
     */
    EntityPrivacyHistoryContract getPrivacyPreferences();

    /**
     * Gets this {@link EntityContract}'s demographic information
     * @return the {@link EntityBioDemographicsContract} for this {@link EntityContract},
     * or null if none has been assigned.
     */
    EntityBioDemographicsHistoryContract getBioDemographics();

    /**
     * Gets this {@link EntityContract}'s citizenship information
     * @return the List of {@link EntityCitizenshipContract}s for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    List<? extends EntityCitizenshipHistoryContract> getCitizenships();

    /**
     * Gets this {@link EntityContract}'s identity type for the given type code
     * @param entityTypeCode the type code
     * @return the EntityEntityType object corresponding to the given code or null if this
     * identity does not have data for that type.
     */
    EntityTypeContactInfoHistoryContract getEntityTypeContactInfoByTypeCode(String entityTypeCode);

    /**
     * Gets this {@link EntityContract}'s employment information
     * @return the primary {@link org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract} for this {@link EntityContract},
     * or null if none has been assigned.
     */
    EntityEmploymentHistoryContract getPrimaryEmployment();

    /**
     * Gets this {@link EntityContract}'s default affiliation
     * @return the default {@link EntityAffiliationContract} for the identity.  If no default is defined, then
     * it returns the first one found.  If none are defined, it returns null.
     */
    EntityAffiliationHistoryContract getDefaultAffiliation();

    /**
     * Gets this {@link EntityContract}'s external identifier for the given type code
     * @param externalIdentifierTypeCode the type code
     * @return the {@link org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierContract} for this {@link EntityContract}, or null if none has been assigned.
     */
    EntityExternalIdentifierHistoryContract getEntityExternalIdentifier( String externalIdentifierTypeCode );

    /**
     * Gets this {@link EntityContract}'s default name
     * @return the default {@link EntityNameContract} record for the identity.  If no default is defined, then
     * it returns the first one found.  If none are defined, it returns null.
     */
    EntityNameHistoryContract getDefaultName();

    /**
     * Gets this {@link EntityContract}'s ethnicities
     * @return the List of {@link org.kuali.rice.kim.api.identity.personal.EntityEthnicityContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<? extends EntityEthnicityHistoryContract> getEthnicities();

    /**
     * Gets this {@link EntityContract}'s residencies
     * @return the List of {@link org.kuali.rice.kim.api.identity.residency.EntityResidencyContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<? extends EntityResidencyHistoryContract> getResidencies();

    /**
     * Gets this {@link EntityContract}'s visas
     * @return the List of {@link org.kuali.rice.kim.api.identity.visa.EntityVisaContract}S for this {@link EntityContract}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<? extends EntityVisaHistoryContract> getVisas();

}
