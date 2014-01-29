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
package org.kuali.rice.kim.impl.type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeAttributeContract;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@Entity
@Table(name = "KRIM_TYP_ATTR_T")
public class KimTypeAttributeBo extends DataObjectBase implements KimTypeAttributeContract {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_TYP_ATTR_ID_S")
    @GeneratedValue(generator = "KRIM_TYP_ATTR_ID_S")
    @Id
    @Column(name = "KIM_TYP_ATTR_ID")
    private String id;

    @Column(name = "SORT_CD")
    private String sortCode;

    @Column(name = "KIM_ATTR_DEFN_ID")
    private String kimAttributeId;

    @ManyToOne(targetEntity = KimAttributeBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "KIM_ATTR_DEFN_ID", referencedColumnName = "KIM_ATTR_DEFN_ID", insertable = false, updatable = false)
    private KimAttributeBo kimAttribute;

    @Column(name = "KIM_TYP_ID")
    private String kimTypeId;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KimTypeAttribute to(KimTypeAttributeBo bo) {
        if (bo == null) {
            return null;
        }
        return KimTypeAttribute.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static KimTypeAttributeBo from(KimTypeAttribute im) {
        if (im == null) {
            return null;
        }
        KimTypeAttributeBo bo = new KimTypeAttributeBo();
        bo.setId(im.getId());
        bo.sortCode = im.getSortCode();
        final KimAttribute attribute = im.getKimAttribute();
        bo.kimAttributeId = (attribute == null ? null : attribute.getId());
        bo.kimAttribute = KimAttributeBo.from(im.getKimAttribute());
        bo.kimTypeId = im.getKimTypeId();
        bo.active = im.isActive();
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public KimAttributeBo getKimAttribute() {
        return kimAttribute;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getKimAttributeId() {
        return kimAttributeId;
    }

    public void setKimAttributeId(String kimAttributeId) {
        this.kimAttributeId = kimAttributeId;
    }

    public void setKimAttribute(KimAttributeBo kimAttribute) {
        this.kimAttribute = kimAttribute;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
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
}
