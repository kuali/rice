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

import org.kuali.rice.kim.api.identity.phone.EntityPhone;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "KRIM_ENTITY_PHONE_T")
public class EntityPhoneBo extends EntityPhoneBase {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "KRIM_ENTITY_PHONE_ID_S")
    @PortableSequenceGenerator(name = "KRIM_ENTITY_PHONE_ID_S")
    @Column(name = "ENTITY_PHONE_ID")
    private String id;

    @ManyToOne(targetEntity = EntityPhoneTypeBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "PHONE_TYP_CD", insertable = false, updatable = false)
    private EntityPhoneTypeBo phoneType;

    public static EntityPhone to(EntityPhoneBo bo) {
        if (bo == null) {
            return null;
        }

        return EntityPhone.Builder.create(bo).build();
    }

    /**
     * Creates a CountryBo business object from an immutable representation of a Country.
     *
     * @param immutable immutable Country
     * @return a CountryBo
     */
    public static EntityPhoneBo from(EntityPhone immutable) {
        if (immutable == null) {
            return null;
        }

        EntityPhoneBo bo = new EntityPhoneBo();
        bo.setId(immutable.getId());
        bo.setActive(immutable.isActive());

        bo.setEntityId(immutable.getEntityId());
        bo.setEntityTypeCode(immutable.getEntityTypeCode());
        if (immutable.getPhoneType() != null) {
            bo.setPhoneTypeCode(immutable.getPhoneType().getCode());
        }

        bo.setPhoneType(EntityPhoneTypeBo.from(immutable.getPhoneType()));
        bo.setDefaultValue(immutable.isDefaultValue());
        bo.setCountryCode(immutable.getCountryCodeUnmasked());
        bo.setPhoneNumber(immutable.getPhoneNumberUnmasked());
        bo.setExtensionNumber(immutable.getExtensionNumberUnmasked());
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());

        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public EntityPhoneTypeBo getPhoneType() {
        return this.phoneType;
    }

    public void setPhoneType(EntityPhoneTypeBo phoneType) {
        this.phoneType = phoneType;
    }


}
