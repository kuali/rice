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
package org.kuali.rice.kim.impl.identity.email;

import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.email.EntityEmailContract;
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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_EMAIL_T")
public class EntityEmailBo extends PersistableBusinessObjectBase implements EntityEmailContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ENTITY_EMAIL_ID")
    private String id;
    @Column(name = "ENTITY_ID")
    private String entityId;
    @Column(name = "ENT_TYP_CD")
    private String entityTypeCode;
    @Column(name = "EMAIL_TYP_CD")
    private String emailTypeCode;
    @Column(name = "EMAIL_ADDR")
    private String emailAddress;
    @ManyToOne(targetEntity = EntityEmailTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(
            name = "PHONE_TYP_CD", insertable = false, updatable = false)
    private EntityEmailTypeBo emailType;
    @Transient
    private boolean suppressEmail;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "DFLT_IND")
    private boolean defaultValue;

    public static EntityEmail to(EntityEmailBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityEmail.Builder.create(bo).build();
    }

    /**
     * Creates a EntityEmailBo business object from an immutable representation of a EntityEmail.
     *
     * @param an immutable EntityEmail
     * @return a EntityEmailBo
     */
    public static EntityEmailBo from(EntityEmail immutable) {
        if (immutable == null) {
            return null;
        }

        EntityEmailBo bo = new EntityEmailBo();
        bo.id = immutable.getId();
        bo.active = immutable.isActive();

        bo.entityId = immutable.getEntityId();
        bo.entityTypeCode = immutable.getEntityTypeCode();
        if (immutable.getEmailType() != null) {
            bo.emailTypeCode = immutable.getEmailType().getCode();
        }

        bo.emailAddress = immutable.getEmailAddressUnmasked();
        bo.emailType = EntityEmailTypeBo.from(immutable.getEmailType());
        bo.defaultValue = immutable.isDefaultValue();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public EntityEmailTypeBo getEmailType() {
        return this.emailType;
    }

    public void setEmailType(EntityEmailTypeBo emailType) {
        this.emailType = emailType;
    }

    @Override
    public boolean isSuppressEmail() {
        try {
            EntityPrivacyPreferences privacy = KimApiServiceLocator.getIdentityService().getEntityPrivacyPreferences(
                    getEntityId());
            if (privacy != null) {
                this.suppressEmail = privacy.isSuppressEmail();
            } else {
                this.suppressEmail = false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (ClassCastException c) {
            return false;
        }
        return this.suppressEmail;
    }

    @Override
    public String getEmailAddressUnmasked() {
        return this.emailAddress;
    }

    @Override
    public String getEmailAddress() {
        if (isSuppressEmail()) {
            return KimApiConstants.RestrictedMasks.RESTRICTED_DATA_MASK;
        }

        return this.emailAddress;
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

    @Override
    public String getEntityTypeCode() {
        return entityTypeCode;
    }

    public void setEntityTypeCode(String entityTypeCode) {
        this.entityTypeCode = entityTypeCode;
    }

    public String getEmailTypeCode() {
        return emailTypeCode;
    }

    public void setEmailTypeCode(String emailTypeCode) {
        this.emailTypeCode = emailTypeCode;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean getSuppressEmail() {
        return suppressEmail;
    }

    public void setSuppressEmail(boolean suppressEmail) {
        this.suppressEmail = suppressEmail;
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

}
