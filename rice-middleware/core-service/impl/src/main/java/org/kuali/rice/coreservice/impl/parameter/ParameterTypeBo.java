/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.coreservice.impl.parameter;

import org.kuali.rice.coreservice.api.parameter.ParameterType;
import org.kuali.rice.coreservice.framework.parameter.ParameterTypeEbo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="KRCR_PARM_TYP_T")
public class ParameterTypeBo extends PersistableBusinessObjectBase implements ParameterTypeEbo {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="PARM_TYP_CD")
    private String code;

    @Column(name="NM")
    private String name;

    @Column(name="ACTV_IND")
    @Convert(converter=BooleanYNConverter.class)
    private boolean active = true;

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

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static ParameterType to(ParameterTypeBo bo) {
        if (bo == null) {
            return null;
        }

        return ParameterType.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static ParameterTypeBo from(ParameterType im) {
        if (im == null) {
            return null;
        }

        ParameterTypeBo bo = new ParameterTypeBo();
        bo.active = im.isActive();
        bo.code = im.getCode();
        bo.name = im.getName();
        bo.versionNumber = im.getVersionNumber();
        bo.objectId = im.getObjectId();
        return bo;
    }
}