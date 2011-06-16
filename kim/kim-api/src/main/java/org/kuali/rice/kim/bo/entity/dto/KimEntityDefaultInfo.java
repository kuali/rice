/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.entity.dto;

import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.type.EntityTypeDataDefault;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * default information for a KIM identity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KimEntityDefaultInfo extends KimInactivatableInfo {

    private static final long serialVersionUID = 7930630152792502380L;
    protected String entityId;
    protected EntityName defaultName;
    protected List<Principal> principals;
    protected List<EntityTypeDataDefault> entityTypes;
    protected List<EntityAffiliation> affiliations;
    protected EntityAffiliation defaultAffiliation;
    protected KimEntityEmploymentInformationInfo primaryEmployment;
    protected List<KimEntityExternalIdentifierInfo> externalIdentifiers;
    protected EntityPrivacyPreferences privacyPreferences;

    /**
     * Gets this {@link KimEntityDefaultInfo}'s identity id.
     * @return the identity id for this {@link KimEntityDefaultInfo}, or null if none has been assigned.
     */
    public String getEntityId() {
        return this.entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s default name.
     * @return the default name for this {@link KimEntityDefaultInfo}, or null if none has been assigned.
     */
    public EntityName getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(EntityName defaultName) {
        this.defaultName = defaultName;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s List of {@link KimEntityEntityTypeDefaultInfo}S.
     * @return the List of {@link KimEntityEntityTypeDefaultInfo}S for this {@link KimEntityDefaultInfo}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<EntityTypeDataDefault> getEntityTypes() {
        // If our reference is null, assign and return an empty List
        return (entityTypes != null) ? entityTypes : (entityTypes = new ArrayList<EntityTypeDataDefault>());
    }

    public void setEntityTypes(List<EntityTypeDataDefault> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s List of {@link EntityAffiliation}S.
     * @return the List of {@link EntityAffiliation}S for this {@link KimEntityDefaultInfo}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<EntityAffiliation> getAffiliations() {
        // If our reference is null, assign and return an empty List
        return (affiliations != null) ? affiliations : (affiliations = new ArrayList<EntityAffiliation>());
    }

    public void setAffiliations(List<EntityAffiliation> affiliations) {
        this.affiliations = affiliations;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s affiliation info.
     * @return the affiliation info for this {@link KimEntityDefaultInfo}, or null if none has been assigned.
     */
    public EntityAffiliation getDefaultAffiliation() {
        return defaultAffiliation;
    }

    public void setDefaultAffiliation(EntityAffiliation defaultAffiliation) {
        this.defaultAffiliation = defaultAffiliation;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s primary employment info.
     * @return the primary employment info for this {@link KimEntityDefaultInfo}, or null if none has been assigned.
     */
    public KimEntityEmploymentInformationInfo getPrimaryEmployment() {
        return primaryEmployment;
    }

    public void setPrimaryEmployment(KimEntityEmploymentInformation primaryEmployment) {
        if (primaryEmployment != null) {
            if (primaryEmployment instanceof KimEntityEmploymentInformationInfo) {
                this.primaryEmployment = (KimEntityEmploymentInformationInfo) primaryEmployment;
            }
            else {
                this.primaryEmployment = new KimEntityEmploymentInformationInfo(primaryEmployment);
            }
        }
    }

    public void setPrimaryEmployment(KimEntityEmploymentInformationInfo primaryEmployment) {
        this.primaryEmployment = primaryEmployment;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s List of {@link KimEntityExternalIdentifierInfo}S.
     * @return the List of {@link KimEntityExternalIdentifierInfo}S for this {@link KimEntityDefaultInfo}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<KimEntityExternalIdentifierInfo> getExternalIdentifiers() {
        // If our reference is null, assign and return an empty List
        return (externalIdentifiers != null) ? 
                externalIdentifiers : (externalIdentifiers = new ArrayList<KimEntityExternalIdentifierInfo>());
    }

    public void setExternalIdentifiers(
            List<KimEntityExternalIdentifierInfo> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s List of {@link Principal}s.
     * @return the List of {@link Principal}S for this {@link KimEntityDefaultInfo}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<Principal> getPrincipals() {
        // If our reference is null, assign and return an empty List
        return (principals != null) ? principals : (principals = new ArrayList<Principal>());
    }

    public void setPrincipals(List<Principal> principals) {
        this.principals = principals;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s {@link KimEntityEntityTypeDefaultInfo} for the given type code.
     * @return the {@link KimEntityEntityTypeDefaultInfo} for the given type code for this {@link KimEntityDefaultInfo}, 
     * or null if none has been assigned.
     */
    public EntityTypeDataDefault getEntityType(String entityTypeCode) {
        EntityTypeDataDefault result = null;
        if (entityTypes == null) {
            entityTypes = new ArrayList<EntityTypeDataDefault>();
        }
        for (EntityTypeDataDefault entType : entityTypes) {
            if (entType.getEntityTypeCode().equals(entityTypeCode)) {
                result = entType;
            }
        }
        return result;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s privacy preferences.
     * @return the privacy preferences for this {@link KimEntityDefaultInfo}, or null if none has been assigned.
     */
    public EntityPrivacyPreferences getPrivacyPreferences() {
        return privacyPreferences;
    }

    public void setPrivacyPreferences(EntityPrivacyPreferences privacyPreferences) {
        this.privacyPreferences = privacyPreferences;
    }

}
