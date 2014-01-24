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
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@MappedSuperclass
public abstract class CodedAttributeBo extends DataObjectBase implements CodedAttributeContract {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CodedAttributeBo.class);
    private static final long serialVersionUID = -5023039880648352464L;
    @Id
    @Column(name = "CD")
    private String code;
    @Column(name = "NM")
    private String name;
    @javax.persistence.Convert(converter=BooleanYNConverter.class)
    @Column(name = "ACTV_IND")
    private boolean active;
    @Column(name = "DISPLAY_SORT_CD")
    private String sortCode;

    /**
     * Converts a mutable extension CodedAttributeBo to an immutable CodedAttribute representation.
     *
     * @param bo
     * @return an immutable EntityCitizenshipChangeType
     */
    public static <T extends CodedAttributeBo>CodedAttribute to(T bo) {
        if (bo == null) {
            return null;
        }

        return CodedAttribute.Builder.create(bo).build();
    }

    /**
     * Creates a EntityCitizenshipChangeTypeBo business object from an immutable representation of a
     * EntityCitizenshipChangeType.
     *
     * @param immutable an immutable CodedAttribute
     * @return an object extending from CodedAttributeBo
     */
    public static <T extends CodedAttributeBo> T from(Class<T> type, CodedAttribute immutable) {
        if (immutable == null) {
            return null;
        }
        T bo = null;
        try {
            bo = type.newInstance();

            bo.setCode(immutable.getCode());
            bo.setName(immutable.getName());
            bo.setSortCode(immutable.getSortCode());
            bo.setActive(immutable.isActive());
            bo.setVersionNumber(immutable.getVersionNumber());
            bo.setObjectId(immutable.getObjectId());
        } catch (InstantiationException e) {
            LOG.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }
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

    public void refresh() {
    }

}
