/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationContract;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationHistory;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationHistoryContract;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenship;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipContract;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistory;
import org.kuali.rice.kim.api.identity.citizenship.EntityCitizenshipHistoryContract;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentContract;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistory;
import org.kuali.rice.kim.api.identity.employment.EntityEmploymentHistoryContract;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierContract;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierHistory;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierHistoryContract;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.name.EntityNameContract;
import org.kuali.rice.kim.api.identity.name.EntityNameHistory;
import org.kuali.rice.kim.api.identity.name.EntityNameHistoryContract;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographics;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographicsHistory;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicity;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicityContract;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicityHistory;
import org.kuali.rice.kim.api.identity.personal.EntityEthnicityHistoryContract;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.identity.principal.PrincipalHistory;
import org.kuali.rice.kim.api.identity.principal.PrincipalHistoryContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyHistory;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.residency.EntityResidency;
import org.kuali.rice.kim.api.identity.residency.EntityResidencyContract;
import org.kuali.rice.kim.api.identity.residency.EntityResidencyHistory;
import org.kuali.rice.kim.api.identity.residency.EntityResidencyHistoryContract;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoContract;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoHistory;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoHistoryContract;
import org.kuali.rice.kim.api.identity.visa.EntityVisa;
import org.kuali.rice.kim.api.identity.visa.EntityVisaContract;
import org.kuali.rice.kim.api.identity.visa.EntityVisaHistory;
import org.kuali.rice.kim.api.identity.visa.EntityVisaHistoryContract;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = EntityHistory.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = EntityHistory.Constants.TYPE_NAME, propOrder = {
    EntityHistory.Elements.ID,
    EntityHistory.Elements.PRINCIPALS,
    EntityHistory.Elements.ENTITY_TYPE_CONTACT_INFOS,
    EntityHistory.Elements.EXTERNAL_IDENTIFIERS,
    EntityHistory.Elements.AFFILIATIONS,
    EntityHistory.Elements.NAMES,
    EntityHistory.Elements.EMPLOYMENT_INFORMATION,
    EntityHistory.Elements.PRIVACY_PREFERENCES,
    EntityHistory.Elements.BIO_DEMOGRAPHICS,
    EntityHistory.Elements.CITIZENSHIPS,
    EntityHistory.Elements.PRIMARY_EMPLOYMENT,
    EntityHistory.Elements.DEFAULT_AFFILIATION,
    EntityHistory.Elements.DEFAULT_NAME,
    EntityHistory.Elements.ETHNICITIES,
    EntityHistory.Elements.RESIDENCIES,
    EntityHistory.Elements.VISAS,
    CoreConstants.CommonElements.VERSION_NUMBER,
    CoreConstants.CommonElements.OBJECT_ID,
    EntityHistory.Elements.ACTIVE,
    CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class EntityHistory extends AbstractDataTransferObject
    implements EntityContract
{
    private static final long serialVersionUID = 6506056736804182758L;
    @XmlElement(name = Elements.ID, required = false)
    private final String id;

    @XmlElementWrapper(name = Elements.PRINCIPALS, required = false)
    @XmlElement(name = Elements.PRINCIPAL, required = false)
    private final List<PrincipalHistory> principals;

    @XmlElementWrapper(name = Elements.ENTITY_TYPE_CONTACT_INFOS, required = false)
    @XmlElement(name = Elements.ENTITY_TYPE_CONTACT_INFO, required = false)
    private final List<EntityTypeContactInfoHistory> entityTypeContactInfos;

    @XmlElementWrapper(name = Elements.EXTERNAL_IDENTIFIERS, required = false)
    @XmlElement(name = Elements.EXTERNAL_IDENTIFIER, required = false)
    private final List<EntityExternalIdentifierHistory> externalIdentifiers;

    @XmlElementWrapper(name = Elements.AFFILIATIONS, required = false)
    @XmlElement(name = Elements.AFFILIATION, required = false)
    private final List<EntityAffiliationHistory> affiliations;

    @XmlElementWrapper(name = Elements.NAMES, required = false)
    @XmlElement(name = Elements.NAME, required = false)
    private final List<EntityNameHistory> names;

    @XmlElementWrapper(name = Elements.EMPLOYMENT_INFORMATION, required = false)
    @XmlElement(name = Elements.EMPLOYMENT, required = false)
    private final List<EntityEmploymentHistory> employmentInformation;

    @XmlElement(name = Elements.PRIVACY_PREFERENCES, required = false)
    private final EntityPrivacyHistory privacyPreferences;

    @XmlElement(name = Elements.BIO_DEMOGRAPHICS, required = false)
    private final EntityBioDemographicsHistory bioDemographics;

    @XmlElementWrapper(name = Elements.CITIZENSHIPS, required = false)
    @XmlElement(name = Elements.CITIZENSHIP, required = false)
    private final List<EntityCitizenshipHistory> citizenships;

    @XmlElement(name = Elements.PRIMARY_EMPLOYMENT, required = false)
    private final EntityEmploymentHistory primaryEmployment;

    @XmlElement(name = Elements.DEFAULT_AFFILIATION, required = false)
    private final EntityAffiliationHistory defaultAffiliation;

    @XmlElement(name = Elements.DEFAULT_NAME, required = false)
    private final EntityNameHistory defaultName;

    @XmlElementWrapper(name = Elements.ETHNICITIES, required = false)
    @XmlElement(name = Elements.ETHNICITY, required = false)
    private final List<EntityEthnicityHistory> ethnicities;

    @XmlElementWrapper(name = Elements.RESIDENCIES, required = false)
    @XmlElement(name = Elements.RESIDENCY, required = false)
    private final List<EntityResidencyHistory> residencies;

    @XmlElementWrapper(name = Elements.VISAS, required = false)
    @XmlElement(name = Elements.VISA, required = false)
    private final List<EntityVisaHistory> visas;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER, required = false)
    private final Long versionNumber;

    @XmlElement(name = CoreConstants.CommonElements.OBJECT_ID, required = false)
    private final String objectId;

    @XmlElement(name = Elements.ACTIVE, required = false)
    private final boolean active;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     *
     */
    private EntityHistory() {
        this.principals = null;
        this.entityTypeContactInfos = null;
        this.externalIdentifiers = null;
        this.affiliations = null;
        this.names = null;
        this.employmentInformation = null;
        this.privacyPreferences = null;
        this.bioDemographics = null;
        this.citizenships = null;
        this.primaryEmployment = null;
        this.defaultAffiliation = null;
        this.defaultName = null;
        this.ethnicities = null;
        this.residencies = null;
        this.visas = null;
        this.versionNumber = null;
        this.objectId = null;
        this.active = false;
        this.id = null;
    }

    private EntityHistory(Builder builder) {
        this.principals = ModelObjectUtils.buildImmutableCopy(builder.getPrincipals());
        this.entityTypeContactInfos = ModelObjectUtils.buildImmutableCopy(builder.getEntityTypeContactInfos());
        this.externalIdentifiers = ModelObjectUtils.buildImmutableCopy(builder.getExternalIdentifiers());
        this.affiliations = ModelObjectUtils.buildImmutableCopy(builder.getAffiliations());
        this.names = ModelObjectUtils.buildImmutableCopy(builder.getNames());
        this.employmentInformation = ModelObjectUtils.buildImmutableCopy(builder.getEmploymentInformation());
        this.privacyPreferences = ModelObjectUtils.buildImmutable(builder.getPrivacyPreferences());
        this.bioDemographics = ModelObjectUtils.buildImmutable(builder.getBioDemographics());
        this.citizenships = ModelObjectUtils.buildImmutableCopy(builder.getCitizenships());
        this.primaryEmployment = ModelObjectUtils.buildImmutable(builder.getPrimaryEmployment());
        this.defaultAffiliation = ModelObjectUtils.buildImmutable(builder.getDefaultAffiliation());
        this.defaultName = ModelObjectUtils.buildImmutable(builder.getDefaultName());
        this.ethnicities = ModelObjectUtils.buildImmutableCopy(builder.getEthnicities());
        this.residencies = ModelObjectUtils.buildImmutableCopy(builder.getResidencies());
        this.visas = ModelObjectUtils.buildImmutableCopy(builder.getVisas());
        this.versionNumber = builder.getVersionNumber();
        this.objectId = builder.getObjectId();
        this.active = builder.isActive();
        this.id = builder.getId();
    }

    @Override
    public List<PrincipalHistory> getPrincipals() {
        return this.principals;
    }

    @Override
    public List<EntityTypeContactInfoHistory> getEntityTypeContactInfos() {
        return this.entityTypeContactInfos;
    }

    @Override
    public List<EntityExternalIdentifierHistory> getExternalIdentifiers() {
        return this.externalIdentifiers;
    }

    @Override
    public List<EntityAffiliationHistory> getAffiliations() {
        return this.affiliations;
    }

    @Override
    public List<EntityNameHistory> getNames() {
        return this.names;
    }

    @Override
    public List<EntityEmploymentHistory> getEmploymentInformation() {
        return this.employmentInformation;
    }

    @Override
    public EntityPrivacyHistory getPrivacyPreferences() {
        return this.privacyPreferences;
    }

    @Override
    public EntityBioDemographicsHistory getBioDemographics() {
        return this.bioDemographics;
    }

    @Override
    public List<EntityCitizenshipHistory> getCitizenships() {
        return this.citizenships;
    }

    @Override
    public EntityEmploymentHistory getPrimaryEmployment() {
        return this.primaryEmployment;
    }

    @Override
    public EntityAffiliationHistory getDefaultAffiliation() {
        return this.defaultAffiliation;
    }

    @Override
    public EntityExternalIdentifierHistory getEntityExternalIdentifier(String externalIdentifierTypeCode) {
        if (externalIdentifiers == null) {
            return null;
        }
        for (EntityExternalIdentifierHistory externalId : externalIdentifiers) {
            if (externalId.getExternalIdentifierTypeCode().equals(externalIdentifierTypeCode)) {
                return externalId;
            }
        }
        return null;
    }

    @Override
    public EntityNameHistory getDefaultName() {
        return this.defaultName;
    }

    @Override
    public List<EntityEthnicityHistory> getEthnicities() {
        return this.ethnicities;
    }

    @Override
    public List<EntityResidencyHistory> getResidencies() {
        return this.residencies;
    }

    @Override
    public List<EntityVisaHistory> getVisas() {
        return this.visas;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    @Override
    public String getObjectId() {
        return this.objectId;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public EntityTypeContactInfoHistory getEntityTypeContactInfoByTypeCode(String entityTypeCode) {
        if (entityTypeContactInfos == null) {
            return null;
        }
        for (EntityTypeContactInfoHistory entType : entityTypeContactInfos) {
            if (entType.getEntityTypeCode().equals(entityTypeCode)) {
                return entType;
            }
        }
        return null;
    }

    /**
     * A builder which can be used to construct {@link org.kuali.rice.kim.api.identity.entity.EntityHistory} instances.  Enforces the constraints of the {@link org.kuali.rice.kim.api.identity.entity.EntityContract}.
     *
     */
    public final static class Builder
        implements Serializable, ModelBuilder, EntityContract
    {

        private List<PrincipalHistory.Builder> principals;
        private List<EntityTypeContactInfoHistory.Builder> entityTypeContactInfos;
        private List<EntityExternalIdentifierHistory.Builder> externalIdentifiers;
        private List<EntityAffiliationHistory.Builder> affiliations;
        private List<EntityNameHistory.Builder> names;
        private List<EntityEmploymentHistory.Builder> employmentInformation;
        private EntityPrivacyHistory.Builder privacyPreferences;
        private EntityBioDemographicsHistory.Builder bioDemographics;
        private List<EntityCitizenshipHistory.Builder> citizenships;
        private List<EntityEthnicityHistory.Builder> ethnicities;
        private List<EntityResidencyHistory.Builder> residencies;
        private List<EntityVisaHistory.Builder> visas;
        private Long versionNumber;
        private String objectId;
        private boolean active;
        private String id;

        private Builder() { }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(EntityHistoryContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            if (contract.getPrincipals() != null) {
                List<PrincipalHistory.Builder> tempPrincipals = new ArrayList<PrincipalHistory.Builder>();
                for (PrincipalHistoryContract principal : contract.getPrincipals()) {
                    tempPrincipals.add(PrincipalHistory.Builder.create(principal));
                }
                builder.setPrincipals(tempPrincipals);
            }
            if (contract.getEntityTypeContactInfos() != null) {
                List<EntityTypeContactInfoHistory.Builder> tempTypeData = new ArrayList<EntityTypeContactInfoHistory.Builder>();
                for (EntityTypeContactInfoHistoryContract typeData : contract.getEntityTypeContactInfos()) {
                    tempTypeData.add(EntityTypeContactInfoHistory.Builder.create(typeData));
                }
                builder.setEntityTypes(tempTypeData);
            }
            if (contract.getExternalIdentifiers() != null) {
                List<EntityExternalIdentifierHistory.Builder> externalIds = new ArrayList<EntityExternalIdentifierHistory.Builder>();
                for (EntityExternalIdentifierHistoryContract externalId : contract.getExternalIdentifiers()) {
                    externalIds.add(EntityExternalIdentifierHistory.Builder.create(externalId));
                }
                builder.setExternalIdentifiers(externalIds);
            }
            if (contract.getAffiliations() != null) {
                List<EntityAffiliationHistory.Builder> affils = new ArrayList<EntityAffiliationHistory.Builder>();
                for (EntityAffiliationHistoryContract affil : contract.getAffiliations()) {
                    affils.add(EntityAffiliationHistory.Builder.create(affil));
                }
                builder.setAffiliations(affils);
            }
            if (contract.getNames() != null) {
                List<EntityNameHistory.Builder> nms = new ArrayList<EntityNameHistory.Builder>();
                for (EntityNameHistoryContract nm : contract.getNames()) {
                    nms.add(EntityNameHistory.Builder.create(nm));
                }
                builder.setNames(nms);
            }
            if (contract.getEmploymentInformation() != null) {
                List<EntityEmploymentHistory.Builder> emps = new ArrayList<EntityEmploymentHistory.Builder>();
                for (EntityEmploymentHistoryContract emp : contract.getEmploymentInformation()) {
                    emps.add(EntityEmploymentHistory.Builder.create(emp));
                }
                builder.setEmploymentInformation(emps);
            }
            builder.setPrivacyPreferences(contract.getPrivacyPreferences() == null ? null : EntityPrivacyHistory.Builder.create(contract.getPrivacyPreferences()));
            builder.setBioDemographics(contract.getBioDemographics() == null ? null : EntityBioDemographicsHistory.Builder.create(contract.getBioDemographics()));
            if (contract.getCitizenships() != null) {
                List<EntityCitizenshipHistory.Builder> cits = new ArrayList<EntityCitizenshipHistory.Builder>();
                for (EntityCitizenshipHistoryContract cit : contract.getCitizenships()) {
                    cits.add(EntityCitizenshipHistory.Builder.create(cit));
                }
                builder.setCitizenships(cits);
            }
            if (contract.getEthnicities() != null) {
                List<EntityEthnicityHistory.Builder> ethnctys = new ArrayList<EntityEthnicityHistory.Builder>();
                for (EntityEthnicityHistoryContract ethncty : contract.getEthnicities()) {
                    ethnctys.add(EntityEthnicityHistory.Builder.create(ethncty));
                }
                builder.setEthnicities(ethnctys);
            }
            if (contract.getResidencies() != null) {
                List<EntityResidencyHistory.Builder> residencyBuilders = new ArrayList<EntityResidencyHistory.Builder>();
                for (EntityResidencyHistoryContract residency : contract.getResidencies()) {
                    residencyBuilders.add(EntityResidencyHistory.Builder.create(residency));
                }
                builder.setResidencies(residencyBuilders);
            }
            if (contract.getVisas() != null) {
                List<EntityVisaHistory.Builder> visaBuilders = new ArrayList<EntityVisaHistory.Builder>();
                for (EntityVisaHistoryContract visa : contract.getVisas()) {
                    visaBuilders.add(EntityVisaHistory.Builder.create(visa));
                }
                builder.setVisas(visaBuilders);
            }
            builder.setVersionNumber(contract.getVersionNumber());
            builder.setObjectId(contract.getObjectId());
            builder.setActive(contract.isActive());
            builder.setId(contract.getId());
            return builder;
        }

        public EntityHistory build() {
            return new EntityHistory(this);
        }

        @Override
        public List<PrincipalHistory.Builder> getPrincipals() {
            return this.principals;
        }

        @Override
        public List<EntityTypeContactInfoHistory.Builder> getEntityTypeContactInfos() {
            return this.entityTypeContactInfos;
        }

        @Override
        public List<EntityExternalIdentifierHistory.Builder> getExternalIdentifiers() {
            return this.externalIdentifiers;
        }

        @Override
        public List<EntityAffiliationHistory.Builder> getAffiliations() {
            return this.affiliations;
        }

        @Override
        public List<EntityNameHistory.Builder> getNames() {
            return this.names;
        }

        @Override
        public List<EntityEmploymentHistory.Builder> getEmploymentInformation() {
            return this.employmentInformation;
        }

        @Override
        public EntityPrivacyHistory.Builder getPrivacyPreferences() {
            return this.privacyPreferences;
        }

        @Override
        public EntityBioDemographicsHistory.Builder getBioDemographics() {
            return this.bioDemographics;
        }

        @Override
        public List<EntityCitizenshipHistory.Builder> getCitizenships() {
            return this.citizenships;
        }

        @Override
        public EntityTypeContactInfoHistory.Builder getEntityTypeContactInfoByTypeCode(String entityTypeCode) {
            if (CollectionUtils.isEmpty(this.entityTypeContactInfos)) {
                return null;
            }
            for (EntityTypeContactInfoHistory.Builder builder : this.entityTypeContactInfos) {
                if (builder.getEntityTypeCode().equals(entityTypeCode) && builder.isActive()) {
                    return builder;
                }
            }
            return null;
        }

        @Override
        public EntityEmploymentHistory.Builder getPrimaryEmployment() {
            if (CollectionUtils.isEmpty(this.employmentInformation)) {
                return null;
            }
            for (EntityEmploymentHistory.Builder builder : this.employmentInformation) {
                if (builder.isPrimary()
                        && builder.isActive()) {
                    return builder;
                }
            }
            return null;
        }

        @Override
        public EntityAffiliationHistory.Builder getDefaultAffiliation() {
            if (CollectionUtils.isEmpty(this.affiliations)) {
                return null;
            }
            for (EntityAffiliationHistory.Builder builder : this.affiliations) {
                if (builder.isDefaultValue()
                        && builder.isActive()) {
                    return builder;
                }
            }
            return null;
        }

        @Override
        public EntityExternalIdentifierHistory.Builder getEntityExternalIdentifier(String externalIdentifierTypeCode) {
            if (CollectionUtils.isEmpty(this.externalIdentifiers)) {
                return null;
            }
            for (EntityExternalIdentifierHistory.Builder builder : this.externalIdentifiers) {
                if (builder.getExternalIdentifierTypeCode().equals(externalIdentifierTypeCode)) {
                    return builder;
                }
            }
            return null;
        }

        @Override
        public EntityNameHistory.Builder getDefaultName() {
            if (CollectionUtils.isEmpty(this.names)) {
                return null;
            }
            for (EntityNameHistory.Builder builder : this.names) {
                if (builder.isDefaultValue()
                        && builder.isActive()) {
                    return builder;
                }
            }
            return null;
        }

        @Override
        public List<EntityEthnicityHistory.Builder> getEthnicities() {
            return this.ethnicities;
        }

        @Override
        public List<EntityResidencyHistory.Builder> getResidencies() {
            return this.residencies;
        }

        @Override
        public List<EntityVisaHistory.Builder> getVisas() {
            return this.visas;
        }

        @Override
        public Long getVersionNumber() {
            return this.versionNumber;
        }

        @Override
        public String getObjectId() {
            return this.objectId;
        }

        @Override
        public boolean isActive() {
            return this.active;
        }

        @Override
        public String getId() {
            return this.id;
        }

        public void setPrincipals(List<PrincipalHistory.Builder> principals) {
            this.principals = principals;
        }

        public void setEntityTypes(List<EntityTypeContactInfoHistory.Builder> entityTypeContactInfos) {
            this.entityTypeContactInfos = entityTypeContactInfos;
        }

        public void setExternalIdentifiers(List<EntityExternalIdentifierHistory.Builder> externalIdentifiers) {
            this.externalIdentifiers = externalIdentifiers;
        }

        public void setAffiliations(List<EntityAffiliationHistory.Builder> affiliations) {
            this.affiliations = affiliations;
        }

        public void setNames(List<EntityNameHistory.Builder> names) {
            this.names = names;
        }

        public void setEmploymentInformation(List<EntityEmploymentHistory.Builder> employmentInformation) {
            this.employmentInformation = employmentInformation;
        }

        public void setPrivacyPreferences(EntityPrivacyHistory.Builder privacyPreferences) {
            this.privacyPreferences = privacyPreferences;
        }

        public void setBioDemographics(EntityBioDemographicsHistory.Builder bioDemographics) {
            this.bioDemographics = bioDemographics;
        }

        public void setCitizenships(List<EntityCitizenshipHistory.Builder> citizenships) {
            this.citizenships = citizenships;
        }

        public void setEthnicities(List<EntityEthnicityHistory.Builder> ethnicities) {
            this.ethnicities = ethnicities;
        }

        public void setResidencies(List<EntityResidencyHistory.Builder> residencies) {
            this.residencies = residencies;
        }

        public void setVisas(List<EntityVisaHistory.Builder> visas) {
            this.visas = visas;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void setId(String id) {
            if (StringUtils.isWhitespace(id)) {
                throw new IllegalArgumentException("id is blank");
            }
            this.id = id;
        }

    }


    /**
     * Defines some internal constants used on this class.
     *
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "entity";
        final static String TYPE_NAME = "EntityType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = new String[] {CoreConstants.CommonElements.FUTURE_ELEMENTS };

    }


    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     *
     */
    static class Elements {

        final static String PRINCIPALS = "principals";
        final static String PRINCIPAL = "principal";
        final static String ENTITY_TYPE_CONTACT_INFOS = "entityTypeContactInfos";
        final static String ENTITY_TYPE_CONTACT_INFO = "entityTypeContactInfo";
        final static String EXTERNAL_IDENTIFIERS = "externalIdentifiers";
        final static String EXTERNAL_IDENTIFIER = "externalIdentifier";
        final static String AFFILIATIONS = "affiliations";
        final static String AFFILIATION = "affiliation";
        final static String NAMES = "names";
        final static String NAME = "name";
        final static String EMPLOYMENT_INFORMATION = "employmentInformation";
        final static String EMPLOYMENT = "employment";
        final static String PRIVACY_PREFERENCES = "privacyPreferences";
        final static String BIO_DEMOGRAPHICS = "bioDemographics";
        final static String CITIZENSHIPS = "citizenships";
        final static String CITIZENSHIP = "citizenship";
        final static String PRIMARY_EMPLOYMENT = "primaryEmployment";
        final static String DEFAULT_AFFILIATION = "defaultAffiliation";
        final static String DEFAULT_NAME = "defaultName";
        final static String ETHNICITIES = "ethnicities";
        final static String ETHNICITY = "ethnicity";
        final static String RESIDENCIES = "residencies";
        final static String RESIDENCY = "residency";
        final static String VISAS = "visas";
        final static String VISA = "visa";
        final static String ACTIVE = "active";
        final static String ID = "id";

    }

    public static class Cache {
        public static final String NAME = KimConstants.Namespaces.KIM_NAMESPACE_2_0 + "/" + EntityHistory.Constants.TYPE_NAME;
    }
}