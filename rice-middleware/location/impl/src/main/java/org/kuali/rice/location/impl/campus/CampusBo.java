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
import org.kuali.rice.location.framework.campus.CampusEbo;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "KRLC_CMP_T")
public class CampusBo extends PersistableBusinessObjectBase implements CampusEbo {

    private static final long serialVersionUID = 787567094298971223L;

    @Id
    @Column(name = "CAMPUS_CD")
    private String code;

    @Column(name = "CAMPUS_NM")
    private String name;

    @Column(name = "CAMPUS_SHRT_NM")
    private String shortName;

    @Column(name = "CAMPUS_TYP_CD")
    private String campusTypeCode;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CAMPUS_TYP_CD", insertable = false, updatable = false)
    private CampusTypeBo campusType;

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
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCampusTypeCode() {
        return campusTypeCode;
    }

    public void setCampusTypeCode(String campusTypeCode) {
        this.campusTypeCode = campusTypeCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public CampusTypeBo getCampusType() {
        return campusType;
    }

    public void setCampusType(CampusTypeBo campusType) {
        this.campusType = campusType;
    }

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static org.kuali.rice.location.api.campus.Campus to(CampusBo bo) {
        if (bo == null) {
            return null;
        }
        return org.kuali.rice.location.api.campus.Campus.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static CampusBo from(org.kuali.rice.location.api.campus.Campus im) {
        if (im == null) {
            return null;
        }

        CampusBo bo = new CampusBo();
        bo.code = im.getCode();
        bo.name = im.getName();
        bo.shortName = im.getShortName();
        bo.active = im.isActive();
        if (im.getCampusType() != null) {
            bo.campusTypeCode = im.getCampusType().getCode();
        }
        bo.campusType = CampusTypeBo.from(im.getCampusType());
        bo.versionNumber = im.getVersionNumber();
        bo.objectId = im.getObjectId();

        return bo;
    }

}