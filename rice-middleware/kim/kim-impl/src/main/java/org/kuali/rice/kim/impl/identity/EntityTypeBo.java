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
package org.kuali.rice.kim.impl.identity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.framework.identity.EntityTypeEbo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@Entity
@Table(name = "KRIM_ENT_TYP_T")
public class EntityTypeBo extends DataObjectBase implements EntityTypeEbo {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ENT_TYP_CD")
    private String code;

    @Column(name = "NM")
    private String name;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @Column(name = "DISPLAY_SORT_CD")
    private String sortCode;

    /**
     * Converts a mutable AddressTypeBo to an immutable AddressType representation.
     *
     * @param bo
     * @return an immutable AddressType
     */
    public static CodedAttribute to(EntityTypeBo bo) {
        if (bo == null) {
            return null;
        }
        return CodedAttribute.Builder.create(bo).build();
    }

    /**
     * Creates a AddressType business object from an immutable representation of a AddressType.
     *
     * @param an immutable AddressType
     * @return a AddressTypeBo
     */
    public static EntityTypeBo from(CodedAttribute immutable) {
        if (immutable == null) {
            return null;
        }
        EntityTypeBo bo = new EntityTypeBo();
        bo.code = immutable.getCode();
        bo.name = immutable.getName();
        bo.sortCode = immutable.getSortCode();
        bo.active = immutable.isActive();
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setObjectId(immutable.getObjectId());
        return bo;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    @Override
    public void refresh() {
    }
}
