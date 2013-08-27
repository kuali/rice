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
package org.kuali.rice.kim.impl.identity.personal;

import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographics;
import org.kuali.rice.kim.api.identity.personal.EntityBioDemographicsContract;
import org.kuali.rice.kim.api.identity.personal.EntityDisability;
import org.kuali.rice.kim.api.identity.personal.EntityMilitary;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Entity
@Table(name = "KRIM_ENTITY_BIO_T")
public class EntityBioDemographicsBo extends PersistableBusinessObjectBase implements EntityBioDemographicsContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "BIRTH_DT")
    @Temporal(TemporalType.DATE)
    private java.util.Date birthDateValue;
    @Column(name = "GNDR_CD")
    private String genderCode;
    @Column(name = "GNDR_CHNG_CD")
    private String genderChangeCode;
    @Column(name = "DECEASED_DT")
    @Temporal(TemporalType.DATE)
    private java.util.Date deceasedDateValue;
    @Column(name = "MARITAL_STATUS")
    private String maritalStatusCode;
    @Column(name = "PRIM_LANG_CD")
    private String primaryLanguageCode;
    @Column(name = "SEC_LANG_CD")
    private String secondaryLanguageCode;
    @Column(name = "BIRTH_CNTRY_CD")
    private String birthCountry;
    @Column(name = "BIRTH_STATE_PVC_CD")
    private String birthStateProvinceCode;
    @Column(name = "BIRTH_CITY")
    private String birthCity;
    @Column(name = "GEO_ORIGIN")
    private String geographicOrigin;
    @Column(name = "NOTE_MSG")
    private String noteMessage;
    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ENTITY_ID", insertable = true, updatable = true)
    private List<EntityMilitaryBo> militaryRecords;
    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ENTITY_ID", insertable = true, updatable = true)
    private List<EntityDisabilityBo> disabilities;
    @Transient
    private boolean suppressPersonal;

    public static EntityBioDemographics to(EntityBioDemographicsBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityBioDemographics.Builder.create(bo).build();
    }

    /**
     * Creates a EntityBioDemographicsBo business object from an immutable representation of a EntityBioDemographics.
     *
     * @param immutable an immutable EntityBioDemographics
     * @return a EntityBioDemographicsBo
     */
    public static EntityBioDemographicsBo from(EntityBioDemographics immutable) {
        if (immutable == null) {
            return null;
        }
        EntityBioDemographicsBo bo = new EntityBioDemographicsBo();
        bo.entityId = immutable.getEntityId();
        if (immutable.getBirthDateUnmasked() != null) {
            bo.birthDateValue = DateTimeFormat.forPattern(EntityBioDemographicsContract.BIRTH_DATE_FORMAT)
                                    .parseDateTime(immutable.getBirthDateUnmasked()).toDate();
        }

        bo.birthStateProvinceCode = immutable.getBirthStateProvinceCodeUnmasked();
        bo.birthCity = immutable.getBirthCityUnmasked();
        bo.birthCountry = immutable.getBirthCountryUnmasked();
        if (immutable.getDeceasedDate() != null) {
            bo.deceasedDateValue = DateTimeFormat.forPattern(EntityBioDemographicsContract.DECEASED_DATE_FORMAT)
                    .parseDateTime(immutable.getDeceasedDate()).toDate();
        }

        bo.genderCode = immutable.getGenderCodeUnmasked();
        bo.geographicOrigin = immutable.getGeographicOriginUnmasked();
        bo.maritalStatusCode = immutable.getMaritalStatusCodeUnmasked();
        bo.primaryLanguageCode = immutable.getPrimaryLanguageCodeUnmasked();
        bo.secondaryLanguageCode = immutable.getSecondaryLanguageCodeUnmasked();
        bo.noteMessage = immutable.getNoteMessage();
        bo.suppressPersonal = immutable.isSuppressPersonal();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public String getBirthDate() {
        if (this.birthDateValue != null) {
            if (isSuppressPersonal()) {
                return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
            }

            return new SimpleDateFormat(BIRTH_DATE_FORMAT).format(this.birthDateValue);
        }

        return null;
    }

    @Override
    public Integer getAge() {
        if (this.birthDateValue != null && !isSuppressPersonal()) {
            DateTime endDate;
            if (this.deceasedDateValue != null) {
                endDate = new DateTime(this.deceasedDateValue);
            } else {
                endDate = new DateTime();
            }

            return Years.yearsBetween(new DateTime(this.birthDateValue), endDate).getYears();
        }

        return null;
    }

    @Override
    public String getDeceasedDate() {
        if (this.deceasedDateValue != null) {
            return new SimpleDateFormat(DECEASED_DATE_FORMAT).format(this.deceasedDateValue);
        }

        return null;
    }

    @Override
    public String getBirthDateUnmasked() {
        if (this.birthDateValue != null) {
            return new SimpleDateFormat(BIRTH_DATE_FORMAT).format(this.birthDateValue);
        }

        return null;
    }

    @Override
    public boolean isSuppressPersonal() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(
                    getEntityId());
            if (privacy != null) {
                this.suppressPersonal = privacy.isSuppressPersonal();
            } else {
                this.suppressPersonal = false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }
        return suppressPersonal;
    }

    @Override
    public String getGenderCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.genderCode;
    }

    @Override
    public String getGenderChangeCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.genderChangeCode;
    }

    @Override
    public String getMaritalStatusCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.maritalStatusCode;
    }

    @Override
    public String getPrimaryLanguageCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.primaryLanguageCode;
    }

    @Override
    public String getSecondaryLanguageCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.secondaryLanguageCode;
    }

    @Override
    public String getBirthCountry() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.birthCountry;
    }

    @Override
    public String getBirthStateProvinceCode() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.birthStateProvinceCode;
    }

    @Override
    public String getBirthCity() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.birthCity;
    }

    @Override
    public String getGeographicOrigin() {
        if (isSuppressPersonal()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.geographicOrigin;
    }

    @Override
    public String getGenderCodeUnmasked() {
        return this.genderCode;
    }

    @Override
    public String getGenderChangeCodeUnmasked() {
        return this.genderChangeCode;
    }

    @Override
    public String getMaritalStatusCodeUnmasked() {
        return this.maritalStatusCode;
    }

    @Override
    public String getPrimaryLanguageCodeUnmasked() {
        return this.primaryLanguageCode;
    }

    @Override
    public String getSecondaryLanguageCodeUnmasked() {
        return this.secondaryLanguageCode;
    }

    @Override
    public String getBirthCountryUnmasked() {
        return this.birthCountry;
    }

    @Override
    public String getBirthStateProvinceCodeUnmasked() {
        return this.birthStateProvinceCode;
    }

    @Override
    public String getBirthCityUnmasked() {
        return this.birthCity;
    }

    @Override
    public String getGeographicOriginUnmasked() {
        return this.geographicOrigin;
    }

    @Override
    public boolean isVeteran() {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public java.util.Date getBirthDateValue() {
        return birthDateValue;
    }

    public void setBirthDateValue(java.util.Date birthDateValue) {
        this.birthDateValue = birthDateValue;
    }

    public void setGenderCode(String genderCode) {
        this.genderCode = genderCode;
    }

    public void setGenderChangeCode(String genderChangeCode) {
        this.genderChangeCode = genderChangeCode;
    }

    public java.util.Date getDeceasedDateValue() {
        return deceasedDateValue;
    }

    public void setDeceasedDateValue(java.util.Date deceasedDateValue) {
        this.deceasedDateValue = deceasedDateValue;
    }

    public void setMaritalStatusCode(String maritalStatusCode) {
        this.maritalStatusCode = maritalStatusCode;
    }

    public void setPrimaryLanguageCode(String primaryLanguageCode) {
        this.primaryLanguageCode = primaryLanguageCode;
    }

    public void setSecondaryLanguageCode(String secondaryLanguageCode) {
        this.secondaryLanguageCode = secondaryLanguageCode;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public void setBirthStateProvinceCode(String birthStateProvinceCode) {
        this.birthStateProvinceCode = birthStateProvinceCode;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public void setGeographicOrigin(String geographicOrigin) {
        this.geographicOrigin = geographicOrigin;
    }

    @Override
    public String getNoteMessage() {
        return noteMessage;
    }

    public void setNoteMessage(String noteMessage) {
        this.noteMessage = noteMessage;
    }

    @Override
    public List<EntityMilitaryBo> getMilitaryRecords() {
        return militaryRecords;
    }

    public void setMilitaryRecords(List<EntityMilitaryBo> militaryRecords) {
        this.militaryRecords = militaryRecords;
    }

    @Override
    public List<EntityDisabilityBo> getDisabilities() {
        return disabilities;
    }

    public void setDisabilities(List<EntityDisabilityBo> disabilities) {
        this.disabilities = disabilities;
    }

    public void setSuppressPersonal(boolean suppressPersonal) {
        this.suppressPersonal = suppressPersonal;
    }

}
