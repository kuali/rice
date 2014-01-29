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
package org.kuali.rice.kim.impl.identity.privacy;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferences;
import org.kuali.rice.kim.api.identity.privacy.EntityPrivacyPreferencesContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@Entity
@Table(name = "KRIM_ENTITY_PRIV_PREF_T")
public class EntityPrivacyPreferencesBo extends DataObjectBase implements EntityPrivacyPreferencesContract {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "SUPPRESS_NM_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean suppressName;

    @Column(name = "SUPPRESS_EMAIL_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean suppressEmail;

    @Column(name = "SUPPRESS_ADDR_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean suppressAddress;

    @Column(name = "SUPPRESS_PHONE_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean suppressPhone;

    @Column(name = "SUPPRESS_PRSNL_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean suppressPersonal;

    public static EntityPrivacyPreferences to(EntityPrivacyPreferencesBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityPrivacyPreferences.Builder.create(bo).build();
    }

    /**
     * Creates a CountryBo business object from an immutable representation of a Country.
     *
     * @param immutable an immutable Country
     * @return a CountryBo
     */
    public static EntityPrivacyPreferencesBo from(EntityPrivacyPreferences immutable) {
        if (immutable == null) {
            return null;
        }
        EntityPrivacyPreferencesBo bo = new EntityPrivacyPreferencesBo();
        bo.entityId = immutable.getEntityId();
        bo.suppressAddress = immutable.isSuppressAddress();
        bo.suppressEmail = immutable.isSuppressEmail();
        bo.suppressName = immutable.isSuppressName();
        bo.suppressPersonal = immutable.isSuppressPersonal();
        bo.suppressPhone = immutable.isSuppressPhone();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public boolean getSuppressName() {
        return suppressName;
    }

    @Override
    public boolean isSuppressName() {
        return suppressName;
    }

    public void setSuppressName(boolean suppressName) {
        this.suppressName = suppressName;
    }

    public boolean getSuppressEmail() {
        return suppressEmail;
    }

    @Override
    public boolean isSuppressEmail() {
        return suppressEmail;
    }

    public void setSuppressEmail(boolean suppressEmail) {
        this.suppressEmail = suppressEmail;
    }

    public boolean getSuppressAddress() {
        return suppressAddress;
    }

    @Override
    public boolean isSuppressAddress() {
        return suppressAddress;
    }

    public void setSuppressAddress(boolean suppressAddress) {
        this.suppressAddress = suppressAddress;
    }

    public boolean getSuppressPhone() {
        return suppressPhone;
    }

    @Override
    public boolean isSuppressPhone() {
        return suppressPhone;
    }

    public void setSuppressPhone(boolean suppressPhone) {
        this.suppressPhone = suppressPhone;
    }

    public boolean getSuppressPersonal() {
        return suppressPersonal;
    }

    @Override
    public boolean isSuppressPersonal() {
        return suppressPersonal;
    }

    public void setSuppressPersonal(boolean suppressPersonal) {
        this.suppressPersonal = suppressPersonal;
    }
}
