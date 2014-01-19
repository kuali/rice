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
package org.kuali.rice.location.impl.campus;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.location.api.campus.CampusType;
import org.kuali.rice.location.framework.campus.CampusTypeEbo;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "KRLC_CMP_TYP_T")
public class CampusTypeBo extends PersistableBusinessObjectBase implements CampusTypeEbo {

    private static final long serialVersionUID = 7644401997723042733L;

    @Id
    @Column(name = "CAMPUS_TYP_CD")
    private String code;

    @Column(name = "CMP_TYP_NM")
    private String name;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

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

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Converts a mutable CountryBo to an immutable Country representation.
     * @param bo
     * @return an immutable Country
     */
    public static CampusType to(CampusTypeBo bo) {
        if (bo == null) {
            return null;
        }
        return CampusType.Builder.create(bo).build();
    }

    /**
     * Creates a CountryBo business object from an immutable representation of a Country.
     * @param im immutable Country
     * @return a CountryBo
     */
    static CampusTypeBo from(CampusType im) {
        if (im == null) {
            return null;
        }

        CampusTypeBo bo = new CampusTypeBo();
        bo.code = im.getCode();
        bo.name = im.getName();
        bo.active = im.isActive();
        bo.versionNumber = im.getVersionNumber();
        bo.objectId = im.getObjectId();

        return bo;
    }
}