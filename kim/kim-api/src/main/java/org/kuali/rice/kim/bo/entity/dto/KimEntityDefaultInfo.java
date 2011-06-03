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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.api.entity.type.EntityTypeDataDefault;

/**
 * default information for a KIM entity
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KimEntityDefaultInfo extends KimInactivatableInfo {

    private static final long serialVersionUID = 7930630152792502380L;
    protected String entityId;
    protected KimEntityNameInfo defaultName;
    protected List<KimPrincipalInfo> principals;
    protected List<EntityTypeDataDefault> entityTypes;
    protected List<KimEntityAffiliationInfo> affiliations;
    protected KimEntityAffiliationInfo defaultAffiliation;
    protected KimEntityEmploymentInformationInfo primaryEmployment;
    protected List<KimEntityExternalIdentifierInfo> externalIdentifiers;
    protected KimEntityPrivacyPreferencesInfo privacyPreferences;

    /**
     * Gets this {@link KimEntityDefaultInfo}'s entity id.
     * @return the entity id for this {@link KimEntityDefaultInfo}, or null if none has been assigned.
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
    public KimEntityNameInfo getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(KimEntityName defaultName) {
        if (defaultName instanceof KimEntityNameInfo) {
            this.defaultName = (KimEntityNameInfo) defaultName;
        }
        else {
            this.defaultName = new KimEntityNameInfo(defaultName);
        }
    }

    public void setDefaultName(KimEntityNameInfo defaultName) {
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
     * Gets this {@link KimEntityDefaultInfo}'s List of {@link KimEntityAffiliationInfo}S.
     * @return the List of {@link KimEntityAffiliationInfo}S for this {@link KimEntityDefaultInfo}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<KimEntityAffiliationInfo> getAffiliations() {
        // If our reference is null, assign and return an empty List
        return (affiliations != null) ? affiliations : (affiliations = new ArrayList<KimEntityAffiliationInfo>());
    }

    public void setAffiliations(List<KimEntityAffiliationInfo> affiliations) {
        this.affiliations = affiliations;
    }

    /**
     * Gets this {@link KimEntityDefaultInfo}'s affiliation info.
     * @return the affiliation info for this {@link KimEntityDefaultInfo}, or null if none has been assigned.
     */
    public KimEntityAffiliationInfo getDefaultAffiliation() {
        return defaultAffiliation;
    }

    public void setDefaultAffiliation(KimEntityAffiliation defaultAffiliation) {
        if (defaultAffiliation != null) {
            if (defaultAffiliation instanceof KimEntityAffiliationInfo) {
                this.defaultAffiliation = (KimEntityAffiliationInfo) defaultAffiliation;
            }
            else {
                this.defaultAffiliation = new KimEntityAffiliationInfo(defaultAffiliation);
            }
        }
    }

    public void setDefaultAffiliation(KimEntityAffiliationInfo defaultAffiliation) {
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
     * Gets this {@link KimEntityDefaultInfo}'s List of {@link KimPrincipalInfo}S.
     * @return the List of {@link KimPrincipalInfo}S for this {@link KimEntityDefaultInfo}.
     * The returned List will never be null, an empty List will be assigned and returned if needed. 
     */
    public List<KimPrincipalInfo> getPrincipals() {
        // If our reference is null, assign and return an empty List
        return (principals != null) ? principals : (principals = new ArrayList<KimPrincipalInfo>());
    }

    public void setPrincipals(List<KimPrincipalInfo> principals) {
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
    public KimEntityPrivacyPreferencesInfo getPrivacyPreferences() {
        return privacyPreferences;
    }

    public void setPrivacyPreferences(KimEntityPrivacyPreferencesInfo privacyPreferences) {
        this.privacyPreferences = privacyPreferences;
    }

}
