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
package org.kuali.rice.kim.impl.identity.external;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierContract;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_ENTITY_EXT_ID_T")
public class EntityExternalIdentifierBo extends DataObjectBase implements EntityExternalIdentifierContract {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(EntityExternalIdentifierBo.class);
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ENTITY_EXT_ID_ID")
    @PortableSequenceGenerator(name = "KRIM_ENTITY_EXT_ID_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_EXT_ID_ID_S")
    private String id;

    @Column(name = "ENTITY_ID")
    private String entityId;

    @Column(name = "EXT_ID_TYP_CD")
    private String externalIdentifierTypeCode;

    @Column(name = "EXT_ID")
    private String externalId;

    @ManyToOne(targetEntity = EntityExternalIdentifierTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "EXT_ID_TYP_CD", referencedColumnName = "EXT_ID_TYP_CD", insertable = false, updatable = false)
    private EntityExternalIdentifierTypeBo externalIdentifierType;

    @Transient
    private EntityExternalIdentifierType cachedExtIdType = null;

    @Transient
    private boolean encryptionRequired = false;

    @Transient
    private boolean decryptionNeeded = false;

    public static EntityExternalIdentifier to(EntityExternalIdentifierBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityExternalIdentifier.Builder.create(bo).build();
    }

    /**
     * Creates a EntityExternalIdentifierBo business object from an immutable representation of a
     * EntityExternalIdentifier.
     *
     * @param immutable immutable EntityExternalIdentifier
     * @return a EntityExternalIdentifierBo
     */
    public static EntityExternalIdentifierBo from(EntityExternalIdentifier immutable) {
        if (immutable == null) {
            return null;
        }
        EntityExternalIdentifierBo bo = new EntityExternalIdentifierBo();
        bo.id = immutable.getId();
        bo.externalId = immutable.getExternalId();
        bo.entityId = immutable.getEntityId();
        bo.externalIdentifierTypeCode = immutable.getExternalIdentifierTypeCode();
        bo.externalIdentifierType = immutable.getExternalIdentifierType() != null ? EntityExternalIdentifierTypeBo.from(immutable.getExternalIdentifierType()) : null;
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    @PrePersist
    protected void prePersist() {
        super.prePersist();
        encryptExternalId();
    }

    @Override
    @PreUpdate
    protected void preUpdate() {
        super.preUpdate();
        if (!this.decryptionNeeded) {
            encryptExternalId();
        }
    }

    protected void encryptExternalId() {
        evaluateExternalIdentifierType();
        if (encryptionRequired && StringUtils.isNotEmpty(this.externalId)) {
            try {
                if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                    this.externalId = CoreApiServiceLocator.getEncryptionService().encrypt(this.externalId);
                    this.decryptionNeeded = true;
                }
            } catch (Exception e) {
                LOG.info("Unable to encrypt value : " + e.getMessage() + " or it is already encrypted");
            }
        }
    }

    protected void decryptExternalId() {
        evaluateExternalIdentifierType();
        if (encryptionRequired && StringUtils.isNotEmpty(externalId)) {
            try {
                if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                    this.externalId = CoreApiServiceLocator.getEncryptionService().decrypt(this.externalId);
                }
            } catch (Exception e) {
                LOG.info("Unable to decrypt value : " + e.getMessage() + " or it is already decrypted");
            }
        }
    }

    protected void evaluateExternalIdentifierType() {
        if (cachedExtIdType == null) {
            cachedExtIdType = KimApiServiceLocator.getIdentityService().getExternalIdentifierType(externalIdentifierTypeCode);
            encryptionRequired = cachedExtIdType != null && cachedExtIdType.isEncryptionRequired();
        }
    }

    protected String decryptedExternalId() {
        evaluateExternalIdentifierType();
        if (encryptionRequired && StringUtils.isNotEmpty(externalId)) {
            try {
                if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                    return CoreApiServiceLocator.getEncryptionService().decrypt(this.externalId);
                }
            } catch (Exception e) {
                LOG.info("Unable to decrypt value : " + e.getMessage() + " or it is already decrypted");
            }
        }
        return "";
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
        this.decryptionNeeded = false;
    }

    public void setExternalIdentifierTypeCode(String externalIdentifierTypeCode) {
        this.externalIdentifierTypeCode = externalIdentifierTypeCode;
        cachedExtIdType = null;
    }

    public void setExternalIdentifierType(EntityExternalIdentifierTypeBo externalIdentifierType) {
        this.externalIdentifierType = externalIdentifierType;
        cachedExtIdType = null;
    }

    @Override
    public EntityExternalIdentifierTypeBo getExternalIdentifierType() {
        return this.externalIdentifierType;
    }

    @Override
    public String getExternalId() {
        if (this.decryptionNeeded) {
            return decryptedExternalId();
        }
        return externalId;
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
    public String getExternalIdentifierTypeCode() {
        return externalIdentifierTypeCode;
    }
}
