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
package org.kuali.rice.kim.impl.identity.entity;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.eclipselink.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@javax.persistence.Entity
@Table(name = "KRIM_HIST_ENTITY_T")
public class EntityHistoryBo extends PersistableBusinessObjectBase /*implements EntityHistoryContract*/ {
    private static final long serialVersionUID = -2448541334029932773L;

    @Id
    @GeneratedValue(generator = "KRIM_HIST_ENTITY_ID_S")
    @PortableSequenceGenerator(name = "KRIM_HIST_ENTITY_ID_S")
    @Column(name = "HIST_ID")
    private Long historyId;

    @Column(name = "ENTITY_ID")
    private String id;

    @Column(name = "ACTV_FRM_DT")
    private Timestamp activeFromDateValue;

    @Column(name = "ACTV_TO_DT")
    private Timestamp activeToDateValue;

    /*@Transient
    private List<EntityNameHistoryBo> names = new ArrayList<EntityNameHistoryBo>();
    @Transient
    private List<PrincipalHistoryBo> principals = new ArrayList<PrincipalHistoryBo>();
    @Transient
    private List<EntityExternalIdentifierHistoryBo> externalIdentifiers = new ArrayList<EntityExternalIdentifierHistoryBo>();
    @Transient
    private List<EntityAffiliationHistoryBo> affiliations = new ArrayList<EntityAffiliationHistoryBo>();
    @Transient
    private List<EntityEmploymentHistoryBo> employmentInformation = new ArrayList<EntityEmploymentHistoryBo>();
    @Transient
    private List<EntityTypeContactInfoHistoryBo> entityTypeContactInfos = new ArrayList<EntityTypeContactInfoHistoryBo>();
    @Transient
    private EntityPrivacyPreferencesHistoryBo privacyPreferences;
    @Transient
    private EntityBioDemographicsHistoryBo bioDemographics;
    @Transient
    private List<EntityCitizenshipHistoryBo> citizenships = new ArrayList<EntityCitizenshipHistoryBo>();
    @Transient
    private List<EntityEthnicityHistoryBo> ethnicities = new ArrayList<EntityEthnicityHistoryBo>();
    @Transient
    private List<EntityResidencyHistoryBo> residencies = new ArrayList<EntityResidencyHistoryBo>();
    @Transient
    private List<EntityVisaHistoryBo> visas = new ArrayList<EntityVisaHistoryBo>();*/

    @Column(name = "ACTV_IND")
    private boolean active;

    /*public static Entity to(EntityHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return Entity.Builder.create(bo).build();
    }

    public static EntityDefault toDefault(EntityHistoryBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityDefault.Builder.create(bo).build();
    }

    public static EntityHistoryBo from(Entity immutable) {
        return fromAndUpdate(immutable, null);
    }
*/
    /**
     * Creates a EntityBo business object from an immutable representation of a Entity.
     *
     * @param immutable an immutable Entity
     * @return a EntityBo
     */
/*    public static EntityHistoryBo fromAndUpdate(Entity immutable, EntityHistoryBo toUpdate) {
        if (immutable == null) {
            return null;
        }

        EntityHistoryBo bo = toUpdate;
        if (toUpdate == null) {
            bo = new EntityHistoryBo();
        }

        bo.active = immutable.isActive();
        bo.id = immutable.getId();

        bo.names = new ArrayList<EntityNameBo>();
        if (CollectionUtils.isNotEmpty(immutable.getNames())) {
            for (EntityName name : immutable.getNames()) {
                bo.names.add(EntityNameBo.from(name));
            }

        }

        bo.principals = new ArrayList<PrincipalBo>();
        if (CollectionUtils.isNotEmpty(immutable.getPrincipals())) {
            for (Principal principal : immutable.getPrincipals()) {
                bo.principals.add(PrincipalBo.from(principal));
            }

        }

        bo.externalIdentifiers = new ArrayList<EntityExternalIdentifierBo>();
        if (CollectionUtils.isNotEmpty(immutable.getExternalIdentifiers())) {
            for (EntityExternalIdentifier externalId : immutable.getExternalIdentifiers()) {
                bo.externalIdentifiers.add(EntityExternalIdentifierBo.from(externalId));
            }

        }

        bo.affiliations = new ArrayList<EntityAffiliationBo>();
        if (CollectionUtils.isNotEmpty(immutable.getAffiliations())) {
            for (EntityAffiliation affiliation : immutable.getAffiliations()) {
                bo.affiliations.add(EntityAffiliationBo.from(affiliation));
            }

        }

        bo.employmentInformation = new ArrayList<EntityEmploymentBo>();
        if (CollectionUtils.isNotEmpty(immutable.getEmploymentInformation())) {
            for (EntityEmployment employment : immutable.getEmploymentInformation()) {
                bo.employmentInformation.add(EntityEmploymentBo.from(employment));
            }

        }

        bo.entityTypeContactInfos = new ArrayList<EntityTypeContactInfoBo>();
        if (CollectionUtils.isNotEmpty(immutable.getEntityTypeContactInfos())) {
            for (EntityTypeContactInfo entityType : immutable.getEntityTypeContactInfos()) {
                bo.entityTypeContactInfos.add(EntityTypeContactInfoBo.from(entityType));
            }

        }

        if (immutable.getPrivacyPreferences() != null) {
            bo.privacyPreferences = EntityPrivacyPreferencesBo.from(immutable.getPrivacyPreferences());
        }

        if (immutable.getBioDemographics() != null) {
            bo.bioDemographics = EntityBioDemographicsBo.from(immutable.getBioDemographics());
        }

        bo.citizenships = new ArrayList<EntityCitizenshipBo>();
        if (CollectionUtils.isNotEmpty(immutable.getCitizenships())) {
            for (EntityCitizenship citizenship : immutable.getCitizenships()) {
                bo.citizenships.add(EntityCitizenshipBo.from(citizenship));
            }

        }

        bo.ethnicities = new ArrayList<EntityEthnicityBo>();
        if (CollectionUtils.isNotEmpty(immutable.getEthnicities())) {
            for (EntityEthnicity ethnicity : immutable.getEthnicities()) {
                bo.ethnicities.add(EntityEthnicityBo.from(ethnicity));
            }

        }

        bo.residencies = new ArrayList<EntityResidencyBo>();
        if (CollectionUtils.isNotEmpty(immutable.getResidencies())) {
            for (EntityResidency residency : immutable.getResidencies()) {
                bo.residencies.add(EntityResidencyBo.from(residency));
            }

        }

        bo.visas = new ArrayList<EntityVisaBo>();
        if (CollectionUtils.isNotEmpty(immutable.getVisas())) {
            for (EntityVisa visa : immutable.getVisas()) {
                bo.visas.add(EntityVisaBo.from(visa));
            }

        }

        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public EntityTypeContactInfoBo getEntityTypeContactInfoByTypeCode(String entityTypeCode) {
        if (CollectionUtils.isEmpty(this.entityTypeContactInfos)) {
            return null;
        }

        for (EntityTypeContactInfoBo entType : this.entityTypeContactInfos) {
            if (entType.getEntityTypeCode().equals(entityTypeCode)) {
                return entType;
            }

        }

        return null;
    }

    @Override
    public EntityEmploymentBo getPrimaryEmployment() {
        if (CollectionUtils.isEmpty(this.employmentInformation)) {
            return null;
        }

        for (EntityEmploymentBo employment : this.employmentInformation) {
            if (employment.isPrimary() && employment.isActive()) {
                return employment;
            }

        }

        return null;
    }

    @Override
    public EntityAffiliationBo getDefaultAffiliation() {
        return EntityUtils.getDefaultItem(this.affiliations);
    }

    @Override
    public EntityExternalIdentifierBo getEntityExternalIdentifier(String externalIdentifierTypeCode) {
        if (CollectionUtils.isEmpty(this.externalIdentifiers)) {
            return null;
        }

        for (EntityExternalIdentifierBo externalId : this.externalIdentifiers) {
            if (externalId.getExternalIdentifierTypeCode().equals(externalIdentifierTypeCode)) {
                return externalId;
            }

        }

        return null;
    }

    @Override
    public EntityNameContract getDefaultName() {
        return EntityUtils.getDefaultItem(this.names);
    }

    @Override
    public EntityPrivacyPreferencesBo getPrivacyPreferences() {
        return this.privacyPreferences;
    }

    @Override
    public EntityBioDemographicsBo getBioDemographics() {
        return this.bioDemographics;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<EntityNameBo> getNames() {
        return names;
    }

    public void setNames(List<EntityNameBo> names) {
        this.names = names;
    }

    @Override
    public List<PrincipalBo> getPrincipals() {
        return principals;
    }

    public void setPrincipals(List<PrincipalBo> principals) {
        this.principals = principals;
    }

    @Override
    public List<EntityExternalIdentifierBo> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<EntityExternalIdentifierBo> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    @Override
    public List<EntityAffiliationBo> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<EntityAffiliationBo> affiliations) {
        this.affiliations = affiliations;
    }

    @Override
    public List<EntityEmploymentBo> getEmploymentInformation() {
        return employmentInformation;
    }

    public void setEmploymentInformation(List<EntityEmploymentBo> employmentInformation) {
        this.employmentInformation = employmentInformation;
    }

    @Override
    public List<EntityTypeContactInfoBo> getEntityTypeContactInfos() {
        return entityTypeContactInfos;
    }

    public void setEntityTypeContactInfos(List<EntityTypeContactInfoBo> entityTypeContactInfos) {
        this.entityTypeContactInfos = entityTypeContactInfos;
    }

    public void setPrivacyPreferences(EntityPrivacyPreferencesBo privacyPreferences) {
        this.privacyPreferences = privacyPreferences;
    }

    public void setBioDemographics(EntityBioDemographicsBo bioDemographics) {
        this.bioDemographics = bioDemographics;
    }

    @Override
    public List<EntityCitizenshipBo> getCitizenships() {
        return citizenships;
    }

    public void setCitizenships(List<EntityCitizenshipBo> citizenships) {
        this.citizenships = citizenships;
    }

    @Override
    public List<EntityEthnicityBo> getEthnicities() {
        return ethnicities;
    }

    public void setEthnicities(List<EntityEthnicityBo> ethnicities) {
        this.ethnicities = ethnicities;
    }

    @Override
    public List<EntityResidencyBo> getResidencies() {
        return residencies;
    }

    public void setResidencies(List<EntityResidencyBo> residencies) {
        this.residencies = residencies;
    }

    @Override
    public List<EntityVisaBo> getVisas() {
        return visas;
    }

    public void setVisas(List<EntityVisaBo> visas) {
        this.visas = visas;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }*/

}
