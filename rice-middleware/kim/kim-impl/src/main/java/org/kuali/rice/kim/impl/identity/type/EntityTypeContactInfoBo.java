
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
package org.kuali.rice.kim.impl.identity.type;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.kuali.rice.kim.api.identity.EntityUtils;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.kim.api.identity.email.EntityEmail;
import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoContract;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoDefault;
import org.kuali.rice.kim.impl.identity.EntityTypeBo;
import org.kuali.rice.kim.impl.identity.address.EntityAddressBo;
import org.kuali.rice.kim.impl.identity.email.EntityEmailBo;
import org.kuali.rice.kim.impl.identity.phone.EntityPhoneBo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.IdClassBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@Entity
@Table(name = "KRIM_ENTITY_ENT_TYP_T")
@IdClass(EntityTypeContactInfoBo.EntityTypeContactInfoBoId.class)
public class EntityTypeContactInfoBo extends DataObjectBase implements EntityTypeContactInfoContract {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ENTITY_ID")
    private String entityId;

    @Id
    @Column(name = "ENT_TYP_CD")
    private String entityTypeCode;

    @ManyToOne(targetEntity = EntityTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ENT_TYP_CD", referencedColumnName = "ENT_TYP_CD", insertable = false, updatable = false)
    private EntityTypeBo entityType;

    @OneToMany(targetEntity = EntityEmailBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumns({ @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ENTITY_ID", insertable = false, updatable = false), @JoinColumn(name = "ENT_TYP_CD", referencedColumnName = "ENT_TYP_CD", insertable = false, updatable = false) })
    private List<EntityEmailBo> emailAddresses;

    @OneToMany(targetEntity = EntityPhoneBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumns({ @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ENTITY_ID", insertable = false, updatable = false), @JoinColumn(name = "ENT_TYP_CD", referencedColumnName = "ENT_TYP_CD", insertable = false, updatable = false) })
    private List<EntityPhoneBo> phoneNumbers;

    @OneToMany(targetEntity = EntityAddressBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumns({ @JoinColumn(name = "ENTITY_ID", referencedColumnName = "ENTITY_ID", insertable = false, updatable = false), @JoinColumn(name = "ENT_TYP_CD", referencedColumnName = "ENT_TYP_CD", insertable = false, updatable = false) })
    private List<EntityAddressBo> addresses;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    public static EntityTypeContactInfo to(EntityTypeContactInfoBo bo) {
        if (bo == null) {
            return null;
        }
        return EntityTypeContactInfo.Builder.create(bo).build();
    }

    public static EntityTypeContactInfoDefault toDefault(EntityTypeContactInfoBo bo) {
        if (bo == null) {
            return null;
        }
        return new EntityTypeContactInfoDefault(bo.getEntityTypeCode(), EntityAddressBo.to(bo.getDefaultAddress()), EntityEmailBo.to(bo.getDefaultEmailAddress()), EntityPhoneBo.to(bo.getDefaultPhoneNumber()));
    }

    /**
     * Creates a EntityTypeDataBo business object from an immutable representation of a EntityTypeData.
     *
     * @param an immutable EntityTypeData
     * @return a EntityTypeDataBo
     */
    public static EntityTypeContactInfoBo from(EntityTypeContactInfo immutable) {
        if (immutable == null) {
            return null;
        }
        EntityTypeContactInfoBo bo = new EntityTypeContactInfoBo();
        bo.active = immutable.isActive();
        bo.entityId = immutable.getEntityId();
        bo.entityTypeCode = immutable.getEntityTypeCode();
        bo.addresses = new ArrayList<EntityAddressBo>();
        if (CollectionUtils.isNotEmpty(immutable.getAddresses())) {
            for (EntityAddress address : immutable.getAddresses()) {
                bo.addresses.add(EntityAddressBo.from(address));
            }
        }
        bo.phoneNumbers = new ArrayList<EntityPhoneBo>();
        if (CollectionUtils.isNotEmpty(immutable.getPhoneNumbers())) {
            for (EntityPhone phone : immutable.getPhoneNumbers()) {
                bo.phoneNumbers.add(EntityPhoneBo.from(phone));
            }
        }
        bo.emailAddresses = new ArrayList<EntityEmailBo>();
        if (CollectionUtils.isNotEmpty(immutable.getEmailAddresses())) {
            for (EntityEmail email : immutable.getEmailAddresses()) {
                bo.emailAddresses.add(EntityEmailBo.from(email));
            }
        }
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    public EntityAddressBo getDefaultAddress() {
        return EntityUtils.getDefaultItem(this.addresses);
    }

    @Override
    public EntityEmailBo getDefaultEmailAddress() {
        return EntityUtils.getDefaultItem(this.emailAddresses);
    }

    @Override
    public EntityPhoneBo getDefaultPhoneNumber() {
        return EntityUtils.getDefaultItem(this.phoneNumbers);
    }

    @Override
    public EntityTypeBo getEntityType() {
        return this.entityType;
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

    public void setEntityType(EntityTypeBo entityType) {
        this.entityType = entityType;
    }

    @Override
    public List<EntityEmailBo> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<EntityEmailBo> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }

    @Override
    public List<EntityPhoneBo> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<EntityPhoneBo> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public List<EntityAddressBo> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<EntityAddressBo> addresses) {
        this.addresses = addresses;
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

    public static class EntityTypeContactInfoBoId extends IdClassBase implements Comparable<EntityTypeContactInfoBoId> {

        private static final long serialVersionUID = -6087504648008003050L;

        private String entityId;
        private String entityTypeCode;

        public EntityTypeContactInfoBoId() {}

        public EntityTypeContactInfoBoId(String entityId, String entityTypeCode) {
            this.entityId = entityId;
            this.entityTypeCode = entityTypeCode;
        }

        public String getEntityId() {
            return this.entityId;
        }
        public String getEntityTypeCode() {
            return this.entityTypeCode;
        }

        @Override
        public int compareTo(EntityTypeContactInfoBoId other) {
            return new CompareToBuilder().append(this.entityId, other.entityId).append(this.entityTypeCode, other.entityTypeCode).toComparison();
        }
    }
}
