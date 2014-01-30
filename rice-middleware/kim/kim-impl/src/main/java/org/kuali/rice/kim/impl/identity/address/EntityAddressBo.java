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
package org.kuali.rice.kim.impl.identity.address;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ENTITY_ADDR_T")
public class EntityAddressBo extends EntityAddressBase {

    private static final long serialVersionUID = 0L;

    @PortableSequenceGenerator(name = "KRIM_ENTITY_ADDR_ID_S")
    @GeneratedValue(generator = "KRIM_ENTITY_ADDR_ID_S")
    @Id
    @Column(name = "ENTITY_ADDR_ID")
    private String id;

    @ManyToOne(targetEntity = EntityAddressTypeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ADDR_TYP_CD", referencedColumnName = "ADDR_TYP_CD", insertable = false, updatable = false)
    private EntityAddressTypeBo addressType;

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
        bo.setActive(immutable.isActive());
        bo.setEntityTypeCode(immutable.getEntityTypeCode());
        if (immutable.getAddressType() != null) {
            bo.setAddressTypeCode(immutable.getAddressType().getCode());
        }
        bo.setAddressType(EntityAddressTypeBo.from(immutable.getAddressType()));
        bo.setDefaultValue(immutable.isDefaultValue());
        bo.setAttentionLine(immutable.getAttentionLineUnmasked());
        bo.setLine1(immutable.getLine1Unmasked());
        bo.setLine2(immutable.getLine2Unmasked());
        bo.setLine3(immutable.getLine3Unmasked());
        bo.setCity(immutable.getCityUnmasked());
        bo.setStateProvinceCode(immutable.getStateProvinceCodeUnmasked());
        bo.setCountryCode(immutable.getCountryCodeUnmasked());
        bo.setPostalCode(immutable.getPostalCodeUnmasked());
        bo.setAddressFormat(immutable.getAddressFormat());
        bo.setModifiedDate(immutable.getModifiedDate());
        bo.setValidatedDate(immutable.getValidatedDate());
        bo.setValidated(immutable.isValidated());
        bo.setNoteMessage(immutable.getNoteMessage());
        bo.setId(immutable.getId());
        bo.setEntityId(immutable.getEntityId());
        bo.setActive(immutable.isActive());
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
