/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.kuali.rice.kim.api.entity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimEntityAffiliation;
import org.kuali.rice.kim.bo.entity.KimEntityCitizenship;
import org.kuali.rice.kim.bo.entity.KimEntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.KimEntityEthnicity;
import org.kuali.rice.kim.bo.entity.KimEntityExternalIdentifier;
import org.kuali.rice.kim.bo.entity.KimEntityName;
import org.kuali.rice.kim.bo.entity.KimEntityResidency;
import org.kuali.rice.kim.bo.entity.KimEntityVisa;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.api.entity.type.EntityTypeData;
import org.kuali.rice.kim.api.entity.type.EntityTypeDataContract;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a data transfer object containing all information related to a KIM
 * entity.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimEntityInfo extends KimInactivatableInfo implements KimEntity {

    private static final long serialVersionUID = 1L;

    private List<KimEntityAffiliationInfo> affiliations;
    private KimEntityBioDemographicsInfo bioDemographics;
    private List<KimEntityCitizenshipInfo> citizenships;
    private List<KimEntityEmploymentInformationInfo> employmentInformation;
    private String entityId;
    private List<EntityTypeData> entityTypes;
    private List<KimEntityExternalIdentifierInfo> externalIdentifiers;
    private List<KimEntityNameInfo> names;
    private List<KimPrincipalInfo> principals;
    private EntityPrivacyPreferences privacyPreferences;
    private List<KimEntityEthnicityInfo> ethnicities;
    private List<KimEntityResidencyInfo> residencies;
    private List<KimEntityVisaInfo> visas;

    /**
     * This constructs an empty KimEntityInfo
     */
    public KimEntityInfo() {
        super();
        active = true;
    }

    /**
     * This constructs a KimEntityInfo derived from the {@link KimEntity} passed in.
     * 
     * @param entity the {@link KimEntity} that this KimEntityInfo is derived from.  If null, then an empty 
     * KimEntityInfo will be constructed.
     */
    public KimEntityInfo(KimEntity entity) {
        this();

        if (entity != null) {

            entityId = entity.getEntityId();
            active = entity.isActive();

            // See comments by utility method deriveCollection for why this is used.  
            // Essentially, the constructor was too darned long.

            principals = deriveCollection(entity.getPrincipals(), new XForm<KimPrincipal, KimPrincipalInfo>() {
                public KimPrincipalInfo xform(KimPrincipal source) {
                    return new KimPrincipalInfo(source);
                }
            });

            if (entity.getBioDemographics() != null) {
                bioDemographics = new KimEntityBioDemographicsInfo(entity.getBioDemographics());
            }

            if (entity.getPrivacyPreferences() != null) {
                privacyPreferences = EntityPrivacyPreferences.Builder.create(entity.getPrivacyPreferences()).build();
            }

            names = deriveCollection(entity.getNames(), new XForm<KimEntityName, KimEntityNameInfo>() {
                public KimEntityNameInfo xform(KimEntityName source) {
                    return new KimEntityNameInfo(source);
                } 
            });

            entityTypes = new ArrayList<EntityTypeData>();
            for (EntityTypeDataContract contract : entity.getEntityTypes()) {
                entityTypes.add(EntityTypeData.Builder.create(contract).build());
            }

            affiliations = deriveCollection(entity.getAffiliations(), new XForm<KimEntityAffiliation, KimEntityAffiliationInfo>() {
                public KimEntityAffiliationInfo xform(KimEntityAffiliation source) {
                    return new KimEntityAffiliationInfo(source);
                }
            });

            employmentInformation = deriveCollection(entity.getEmploymentInformation(), 
                    new XForm<KimEntityEmploymentInformation, KimEntityEmploymentInformationInfo>() {
                public KimEntityEmploymentInformationInfo xform(KimEntityEmploymentInformation source) {
                    return new KimEntityEmploymentInformationInfo(source);
                }
            });

            externalIdentifiers = deriveCollection(entity.getExternalIdentifiers(), 
                    new XForm<KimEntityExternalIdentifier, KimEntityExternalIdentifierInfo>() {
                public KimEntityExternalIdentifierInfo xform(KimEntityExternalIdentifier source) {
                    return new KimEntityExternalIdentifierInfo(source); 
                }
            });        

            citizenships = deriveCollection(entity.getCitizenships(), new XForm<KimEntityCitizenship, KimEntityCitizenshipInfo>() {
                public KimEntityCitizenshipInfo xform(KimEntityCitizenship source) {
                    return new KimEntityCitizenshipInfo(source);
                }
            });

            ethnicities = deriveCollection(entity.getEthnicities(), new XForm<KimEntityEthnicity, KimEntityEthnicityInfo>() {
                public KimEntityEthnicityInfo xform(KimEntityEthnicity source) {
                    return new KimEntityEthnicityInfo(source);
                }
            });

            residencies = deriveCollection(entity.getResidencies(), new XForm<KimEntityResidency, KimEntityResidencyInfo>() {
                public KimEntityResidencyInfo xform(KimEntityResidency source) {
                    return new KimEntityResidencyInfo(source);
                }
            });

            visas = deriveCollection(entity.getVisas(), new XForm<KimEntityVisa, KimEntityVisaInfo>() {
                public KimEntityVisaInfo xform(KimEntityVisa source) {
                    return new KimEntityVisaInfo(source);
                }
            });
        }
    }

    /** 
     * {@inheritDoc}
     * @see KimEntity#getAffiliations()
     */
    public List<KimEntityAffiliationInfo> getAffiliations() {
        // If our reference is null, assign and return an empty List
        return (affiliations != null) ? affiliations : (affiliations = new ArrayList<KimEntityAffiliationInfo>());

    }

    /** 
     * Setter for this {@link KimEntityInfo}'s affiliations.  Note the behavior of {@link #getAffiliations()} if
     * this is set to null;
     */
    public void setAffiliations(List<KimEntityAffiliationInfo> affiliations) {
        this.affiliations = affiliations;
    }

    /** 
     * {@inheritDoc}
     * @see KimEntity#getDefaultAffiliation()
     */
    public KimEntityAffiliationInfo getDefaultAffiliation() {
        KimEntityAffiliationInfo result = null;
        if (affiliations != null)
            for (KimEntityAffiliationInfo affiliation : affiliations) {
                if (result == null) {
                    result = affiliation;
                }
                if (affiliation.isDefaultValue()) {
                    result = affiliation;
                    break;
                }
            }
        return result;
    }

    /** 
     * {@inheritDoc}
     * @see KimEntity#getBioDemographics()
     */
    public KimEntityBioDemographicsInfo getBioDemographics() {
        return bioDemographics;
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s demographic information.  Note the behavior of 
     * {@link #getBioDemographics()} if this is set to null;
     */
    public void setBioDemographics(KimEntityBioDemographicsInfo bioDemographics) {
        this.bioDemographics = bioDemographics;
    }

    /** 
     * {@inheritDoc}
     * @see KimEntity#getCitizenships()
     */
    public List<KimEntityCitizenshipInfo> getCitizenships() {
        // If our reference is null, assign and return an empty List
        return (citizenships != null) ? citizenships : (citizenships = new ArrayList<KimEntityCitizenshipInfo>());

    }

    /** 
     * Setter for this {@link KimEntityInfo}'s demographic information.  Note the behavior of 
     * {@link #getCitizenships()} if this is set to null;
     */
    public void setCitizenships(List<KimEntityCitizenshipInfo> citizenships) {
        this.citizenships = citizenships;
    }

    /** 
     * {@inheritDoc}
     * @see KimEntity#getEmploymentInformation()
     */
    public List<KimEntityEmploymentInformationInfo> getEmploymentInformation() {
        // If our reference is null, assign and return an empty List
        return (employmentInformation != null) ? employmentInformation
                : (employmentInformation = new ArrayList<KimEntityEmploymentInformationInfo>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s employment information.  Note the behavior of 
     * {@link #getEmploymentInformation()} if this is set to null;
     */
    public void setEmploymentInformation(List<KimEntityEmploymentInformationInfo> employmentInformation) {
        this.employmentInformation = employmentInformation;
    }

    /** 
     * {@inheritDoc}
     * @see KimEntity#getPrimaryEmployment()
     */
    public KimEntityEmploymentInformationInfo getPrimaryEmployment() {
        KimEntityEmploymentInformationInfo result = null;
        if (employmentInformation != null)
            for (KimEntityEmploymentInformationInfo employment : employmentInformation) {
                if (employment.isPrimary()) {
                    result = employment;
                }
            }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getEntityId()
     */
    public String getEntityId() {
        return entityId;
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s entity id.  Note the behavior of 
     * {@link #getEntityId()} if this is set to null;
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getEntityTypes()
     */
    public List<EntityTypeData> getEntityTypes() {
        // If our reference is null, assign and return an empty List
        return (entityTypes != null) ? entityTypes : (entityTypes = new ArrayList<EntityTypeData>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s entity types.  Note the behavior of 
     * {@link #getEntityTypes()} if this is set to null;
     */
    public void setEntityTypes(List<EntityTypeData> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getExternalIdentifiers()
     */
    public List<KimEntityExternalIdentifierInfo> getExternalIdentifiers() {
        // If our reference is null, assign and return an empty List
        return (externalIdentifiers != null) ? externalIdentifiers
                : (externalIdentifiers = new ArrayList<KimEntityExternalIdentifierInfo>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s external identifiers.  Note the behavior of 
     * {@link #getExternalIdentifiers()} if this is set to null;
     */
    public void setExternalIdentifiers(List<KimEntityExternalIdentifierInfo> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getNames()
     */
    public List<KimEntityNameInfo> getNames() {
        // If our reference is null, assign and return an empty List
        return (names != null) ? names : (names = new ArrayList<KimEntityNameInfo>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s names.  Note the behavior of 
     * {@link #getNames()} if this is set to null;
     */
    public void setNames(List<KimEntityNameInfo> names) {
        this.names = names;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getDefaultName()
     */
    public KimEntityNameInfo getDefaultName() {
        KimEntityNameInfo result = null;
        for (KimEntityNameInfo name : this.getNames()) {
            if (result == null) {
                result = name;
            }
            if (name.isDefaultValue()) {
                result = name;
                break;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getPrincipals()
     */
    public List<KimPrincipalInfo> getPrincipals() {
        // If our reference is null, assign and return an empty List
        return (principals != null) ? principals : (principals = new ArrayList<KimPrincipalInfo>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s principals.  Note the behavior of 
     * {@link #getPrincipals()} if this is set to null;
     */
    public void setPrincipals(List<KimPrincipalInfo> principals) {
        this.principals = principals;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getPrivacyPreferences()
     */
    public EntityPrivacyPreferences getPrivacyPreferences() {
        return privacyPreferences;
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s privacy preferences.  Note the behavior of 
     * {@link #getPrivacyPreferences()} if this is set to null;
     */
    public void setPrivacyPreferences(EntityPrivacyPreferences privacyPreferences) {
        this.privacyPreferences = privacyPreferences;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getEthnicities()
     */
    public List<KimEntityEthnicityInfo> getEthnicities() {
        // If our reference is null, assign and return an empty List
        return (ethnicities != null) ? ethnicities : (ethnicities = new ArrayList<KimEntityEthnicityInfo>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s ethnicities.  Note the behavior of 
     * {@link #getEthnicities()} if this is set to null;
     */
    public void setEthnicities(List<KimEntityEthnicityInfo> ethnicities) {
        this.ethnicities = ethnicities;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getResidencies()
     */
    public List<KimEntityResidencyInfo> getResidencies() {
        // If our reference is null, assign and return an empty List
        return (residencies != null) ? residencies : (residencies = new ArrayList<KimEntityResidencyInfo>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s residencies.  Note the behavior of 
     * {@link #getResidencies()} if this is set to null;
     */
    public void setResidencies(List<KimEntityResidencyInfo> residencies) {
        this.residencies = residencies;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getVisas()
     */
    public List<KimEntityVisaInfo> getVisas() {
        // If our reference is null, assign and return an empty List
        return (visas != null) ? visas : (visas = new ArrayList<KimEntityVisaInfo>());
    }

    /** 
     * Setter for this {@link KimEntityInfo}'s visas.  Note the behavior of 
     * {@link #getVisas()} if this is set to null;
     */
    public void setVisas(List<KimEntityVisaInfo> visas) {
        this.visas = visas;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getEntityExternalIdentifier(String)
     */
    public KimEntityExternalIdentifier getEntityExternalIdentifier(String externalIdentifierTypeCode) {
        KimEntityExternalIdentifier result = null;

        List<KimEntityExternalIdentifierInfo> externalIdentifiers = getExternalIdentifiers();
        if (externalIdentifiers != null)
            for (KimEntityExternalIdentifier eid : externalIdentifiers) {
                if (eid.getExternalIdentifierTypeCode().equals(externalIdentifierTypeCode)) {
                    result = eid;
                }
            }
        return result;
    }

    /**
     * {@inheritDoc}
     * @see KimEntity#getEntityType(String)
     */
    public EntityTypeData getEntityType(String entityTypeCode) {
        EntityTypeData result = null;

        if (entityTypes != null)
            for (EntityTypeData eType : entityTypes) {
                if (eType.getEntityTypeCode().equals(entityTypeCode)) {
                    result = eType;
                }
            }
        return result;
    }
    
    /*
        // utility method converts this monstrous block:
        
        if (entity.getEntityTypes() != null) {
            entityTypes = new ArrayList<KimEntityEntityTypeInfo>(entity.getEntityTypes().size());

            for (KimEntityEntityType entityEntityType : entity.getEntityTypes()) if (entityEntityType != null) {
                entityTypes.add(new KimEntityEntityTypeInfo(entityEntityType));
            }
        } else {
            entityTypes = new ArrayList<KimEntityEntityTypeInfo>();
        }
        
        // to this:
        
        entityTypes = deriveCollection(entity.getEntityTypes(), new XForm<KimEntityEntityType, KimEntityEntityTypeInfo>() {
            public KimEntityEntityTypeInfo xform(KimEntityEntityType source) {
                return new KimEntityEntityTypeInfo(source);
            }
        });
     
        // Note that generic type C is required because some of the source collections use wildcards
     */
    private static <A,B,C> List<B> deriveCollection(List<A> source, XForm<C,B> transformer) {
        List<B> result = null;
        if (source != null) {
            result = new ArrayList<B>(source.size());
            
            for (A element : source) if (element != null) {
                B mutant = transformer.xform((C)element);
                if (mutant != null) {
                    result.add(mutant);
                }
            }
        } else {
            result = new ArrayList();
        }
        return result;
    }
    
    private static interface XForm<A,B> {
        public B xform(A source);
    }

}
