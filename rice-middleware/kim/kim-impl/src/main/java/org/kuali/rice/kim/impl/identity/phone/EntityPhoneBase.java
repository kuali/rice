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
package org.kuali.rice.kim.impl.identity.phone;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.phone.EntityPhoneContract;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@MappedSuperclass
public abstract class EntityPhoneBase extends DataObjectBase implements EntityPhoneContract {

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "ENT_TYP_CD")
    private String entityTypeCode;

    @Column(name = "PHONE_TYP_CD")
    private String phoneTypeCode;

    @Column(name = "PHONE_NBR")
    private String phoneNumber;

    @Column(name = "PHONE_EXTN_NBR")
    private String extensionNumber;

    @Column(name = "POSTAL_CNTRY_CD")
    private String countryCode;

    @Transient
    private boolean suppressPhone;

    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;

    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;

    public String getPhoneTypeCode() {
        return this.phoneTypeCode;
    }

    public static EntityPhone to(EntityPhoneBase bo) {
        if (bo == null) {
            return null;
        }

        return EntityPhone.Builder.create(bo).build();
    }

    @Override
    public boolean isSuppressPhone() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(
                    getEntityId());
            if (privacy != null) {
                this.suppressPhone = privacy.isSuppressPhone();
            } else {
                this.suppressPhone = false;
            }

        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }
        return this.suppressPhone;
    }

    @Override
    public String getFormattedPhoneNumber() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return getFormattedPhoneNumberUnmasked();
    }

    @Override
    public String getPhoneNumberUnmasked() {
        return this.phoneNumber;
    }

    @Override
    public String getExtensionNumberUnmasked() {
        return this.extensionNumber;
    }

    @Override
    public String getCountryCodeUnmasked() {
        return this.countryCode;
    }

    @Override
    public String getFormattedPhoneNumberUnmasked() {
        StringBuffer sb = new StringBuffer(30);

        // TODO: get extension from country code table
        // TODO: append "+xxx" if country is not the default country
        sb.append(this.phoneNumber);
        if (StringUtils.isNotBlank(this.extensionNumber)) {
            sb.append(" x");
            sb.append(this.extensionNumber);
        }

        return sb.toString();
    }

    @Override
    public String getPhoneNumber() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_PHONE;
        }

        return this.phoneNumber;
    }

    @Override
    public String getCountryCode() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK_CODE;
        }

        return this.countryCode;
    }

    @Override
    public String getExtensionNumber() {
        if (isSuppressPhone()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.extensionNumber;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    @Override
    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public void setPhoneTypeCode(String phoneTypeCode) {
        this.phoneTypeCode = phoneTypeCode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean getSuppressPhone() {
        return suppressPhone;
    }

    public void setSuppressPhone(boolean suppressPhone) {
        this.suppressPhone = suppressPhone;
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

    private static final long serialVersionUID = 1L;

}
