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
package org.kuali.rice.kim.impl.identity.address;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.joda.time.DateTime;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.address.EntityAddressContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Timestamp;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_ADDR_T")
public class EntityAddressBo extends PersistableBusinessObjectBase implements EntityAddressContract {
    private static final long serialVersionUID = 0L;
    @Id @Column(name = "ENTITY_ADDR_ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "ADDR_TYP_CD")
    private String addressTypeCode;
    @Column(name = "ENT_TYP_CD")
    private String entityTypeCode;
    @Column(name = "CITY")
    private String city;
    @Column(name = "STATE_PVC_CD")
    private String stateProvinceCode;
    @Column(name = "POSTAL_CD")
    private String postalCode;
    @Column(name = "POSTAL_CNTRY_CD")
    private String countryCode;
    @Column(name = "ATTN_LINE")
    private String attentionLine;
    @Column(name = "ADDR_LINE_1")
    private String line1;
    @Column(name = "ADDR_LINE_2")
    private String line2;
    @Column(name = "ADDR_LINE_3")
    private String line3;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;
    @ManyToOne(targetEntity = EntityAddressTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "ADDR_TYP_CD", insertable = false, updatable = false)
    private EntityAddressTypeBo addressType;
    @Column(name = "ADDR_FMT")
    private String addressFormat;
    @Column(name = "MOD_DT")
    private Timestamp modifiedDate;
    @Column(name = "VALID_DT")
    private Timestamp validatedDate;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "VALID_IND")
    private boolean validated;
    @Column(name = "NOTE_MSG")
    private String noteMessage;
    @Transient private boolean suppressAddress;

    public static EntityAddress to(EntityAddressBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityAddress.Builder.create(bo).build();
    }

    /**
     * Creates a EntityAddressBo business object from an immutable representation of a EntityAddress.
     *
     * @param immutable an immutable EntityAddress
     * @return a EntityAddressBo
     */
    public static EntityAddressBo from(EntityAddress immutable) {
        if (immutable == null) {
            return null;
        }

        EntityAddressBo bo = new EntityAddressBo();
        bo.active = immutable.isActive();
        bo.entityTypeCode = immutable.getEntityTypeCode();
        if (immutable.getAddressType() != null) {
            bo.addressTypeCode = immutable.getAddressType().getCode();
        }

        bo.addressType = EntityAddressTypeBo.from(immutable.getAddressType());
        bo.defaultValue = immutable.isDefaultValue();
        bo.attentionLine = immutable.getAttentionLineUnmasked();
        bo.line1 = immutable.getLine1Unmasked();
        bo.line2 = immutable.getLine2Unmasked();
        bo.line3 = immutable.getLine3Unmasked();
        bo.city = immutable.getCityUnmasked();
        bo.stateProvinceCode = immutable.getStateProvinceCodeUnmasked();
        bo.countryCode = immutable.getCountryCodeUnmasked();
        bo.postalCode = immutable.getPostalCodeUnmasked();
        bo.addressFormat = immutable.getAddressFormat();
        if (immutable.getModifiedDate() != null) {
            bo.modifiedDate = new Timestamp(immutable.getModifiedDate().getMillis());
        }

        if (immutable.getValidatedDate() != null) {
            bo.validatedDate = new Timestamp(immutable.getValidatedDate().getMillis());
        }

        bo.validated = immutable.isValidated();
        bo.noteMessage = immutable.getNoteMessage();
        bo.id = immutable.getId();
        bo.entityId = immutable.getEntityId();
        bo.active = immutable.isActive();
        bo.setVersionNumber(immutable.getVersionNumber());

        return bo;
    }

    @Override
    public EntityAddressTypeBo getAddressType() {
        return addressType;
    }

    public void setAddressType(EntityAddressTypeBo addressType) {
        this.addressType = addressType;
    }

    @Override
    public boolean isSuppressAddress() {
        try {
            EntityPrivacyPreferences privacy =
                KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(getEntityId());
            if (privacy != null) {
                this.suppressAddress = privacy.isSuppressAddress();
            } else {
                this.suppressAddress = false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }


        return suppressAddress;
    }

    @Override
    public String getAttentionLine() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.attentionLine;
    }

    @Override
    public String getLine1() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.line1;
    }

    @Override
    public String getLine2() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.line2;
    }

    @Override
    public String getLine3() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.line3;
    }

    @Override
    public String getCity() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.city;
    }

    @Override
    public String getStateProvinceCode() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.stateProvinceCode;
    }

    @Override
    public String getPostalCode() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_ZIP;
        }

        return this.postalCode;
    }

    @Override
    public String getCountryCode() {
        if (isSuppressAddress()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.countryCode;
    }

    @Override
    public String getAttentionLineUnmasked() {
        return attentionLine;
    }

    @Override
    public String getLine1Unmasked() {
        return line1;
    }

    @Override
    public String getLine2Unmasked() {
        return line2;
    }

    @Override
    public String getLine3Unmasked() {
        return line3;
    }

    @Override
    public String getCityUnmasked() {
        return city;
    }

    @Override
    public String getStateProvinceCodeUnmasked() {
        return stateProvinceCode;
    }

    @Override
    public String getPostalCodeUnmasked() {
        return postalCode;
    }

    @Override
    public String getCountryCodeUnmasked() {
        return countryCode;
    }

    @Override
    public DateTime getModifiedDate() {
        return modifiedDate != null ? new DateTime(modifiedDate.getTime()) : null;
    }

    @Override
    public DateTime getValidatedDate() {
        return DefaultGroovyMethods.asBoolean(validatedDate) ? new DateTime(validatedDate.getTime()) : null;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAddressTypeCode() {
        return addressTypeCode;
    }

    public void setAddressTypeCode(String addressTypeCode) {
        this.addressTypeCode = addressTypeCode;
    }

    @Override
    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStateProvinceCode(String stateProvinceCode) {
        this.stateProvinceCode = stateProvinceCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setAttentionLine(String attentionLine) {
        this.attentionLine = attentionLine;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
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
    }

    @Override
    public String getAddressFormat() {
        return addressFormat;
    }

    public void setAddressFormat(String addressFormat) {
        this.addressFormat = addressFormat;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setValidatedDate(Timestamp validatedDate) {
        this.validatedDate = validatedDate;
    }

    public boolean getValidated() {
        return validated;
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public String getNoteMessage() {
        return noteMessage;
    }

    public void setNoteMessage(String noteMessage) {
        this.noteMessage = noteMessage;
    }

    public boolean getSuppressAddress() {
        return suppressAddress;
    }

    public void setSuppressAddress(boolean suppressAddress) {
        this.suppressAddress = suppressAddress;
    }
}
