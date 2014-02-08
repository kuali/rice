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
package org.kuali.rice.krms.impl.repository;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttributeContract;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Table(name = "KRMS_TYP_ATTR_T")
public class KrmsTypeAttributeBo implements MutableInactivatable, KrmsTypeAttributeContract, Serializable {

    private static final long serialVersionUID = 1l;

    @PortableSequenceGenerator(name = "KRMS_TYP_ATTR_S")
    @GeneratedValue(generator = "KRMS_TYP_ATTR_S")
    @Id
    @Column(name = "TYP_ATTR_ID")
    private String id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "TYP_ID", referencedColumnName = "TYP_ID")
    private KrmsTypeBo type;

    @Column(name = "SEQ_NO")
    private Integer sequenceNumber;

    @Column(name = "ACTV")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    @Column(name = "ATTR_DEFN_ID")
    private String attributeDefinitionId;

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KrmsTypeAttribute to(KrmsTypeAttributeBo bo) {
        if (bo == null) {
            return null;
        }

        return KrmsTypeAttribute.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static KrmsTypeAttributeBo from(KrmsTypeAttribute im) {
        if (im == null) {
            return null;
        }

        KrmsTypeAttributeBo bo = new KrmsTypeAttributeBo();
        bo.id = im.getId();

        // not setting type, it will be set within KrmsTypeBo.from

        bo.attributeDefinitionId = im.getAttributeDefinitionId();
        bo.sequenceNumber = im.getSequenceNumber();
        bo.active = im.isActive();
        bo.setVersionNumber(im.getVersionNumber());

        return bo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        if (type != null) {
            return type.getId();
        }

        return null;
    }

    public KrmsTypeBo getType() {
        return type;
    }

    public void setType(KrmsTypeBo type) {
        this.type = type;
    }

    public String getAttributeDefinitionId() {
        return attributeDefinitionId;
    }

    public void setAttributeDefinitionId(String attributeDefinitionId) {
        this.attributeDefinitionId = attributeDefinitionId;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean getActive() {
        return active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }
}
