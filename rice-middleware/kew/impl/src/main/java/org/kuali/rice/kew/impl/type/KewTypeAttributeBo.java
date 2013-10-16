/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kew.impl.type;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kew.api.repository.type.KewTypeAttribute;
import org.kuali.rice.kew.api.repository.type.KewTypeAttributeContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *  Kuali workflow type attribute business object.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_TYP_ATTR_T")
public class KewTypeAttributeBo implements KewTypeAttributeContract, MutableInactivatable {

    @Id
    @Column(name="TYP_ATTR_ID")
    private String id;

    @Column(name="SEQ_NO")
    private Integer sequenceNumber;

    @Column(name="TYP_ID")
    private String typeId;

    @Column(name="ACTV")
    private boolean active;

    @Column(name="VER_NBR")
    private Long versionNumber;

    @ManyToOne
    @JoinColumn(name="ATTR_DEFN_ID")
    private KewAttributeDefinitionBo attributeDefinition;

    /**
     * Converts a mutable bo to it's immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KewTypeAttribute to(KewTypeAttributeBo bo) {
        if (null == bo) {
            return null;
        } else {
            return org.kuali.rice.kew.api.repository.type.KewTypeAttribute.Builder.create(bo).build();
        }
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static KewTypeAttributeBo from(KewTypeAttribute im) {
        if (null == im) {
            return null;
        } else {
            KewTypeAttributeBo bo = new KewTypeAttributeBo();
            bo.setId(im.getId());
            bo.setTypeId(im.getTypeId());
            bo.setSequenceNumber(im.getSequenceNumber());
            bo.setActive(im.isActive());
            bo.setAttributeDefinition(KewAttributeDefinitionBo.from(im.getAttributeDefinition()));
            return bo;
        }
    }

    /**
     * Default constructor
     */
    public KewTypeAttributeBo() { }

    /**
     * Returns the type attribute id.
     * @return the type attribute id
     */
    public String getId() {
        return id;
    }

    /**
     * @see #getId()
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the sequence number.
     * @return the sequence number
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @see #getSequenceNumber()
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Returns the type id.
     * @return the type id
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * @see #getTypeId()
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /**
     * Returns the status of the KEW type attribute.
     * @return TRUE if the KEW type attribute is active, FALSE otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see #isActive()
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the version number.
     * @return the version number
     */
    public Long getVersionNumber() {
        return versionNumber;
    }

    /**
     * @see #getVersionNumber()
     */
    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Returns the {@link KewAttributeDefinitionBo}.
     * @return a {@link KewAttributeDefinitionBo}
     */
    public KewAttributeDefinitionBo getAttributeDefinition() {
        return attributeDefinition;
    }

    /**
     * @see #getAttributeDefinition()
     */
    public void setAttributeDefinition(KewAttributeDefinitionBo attributeDefinition) {
        this.attributeDefinition = attributeDefinition;
    }

    /**
     * Returns the attribute definition id from the internal attribute definition parameter.
     * @return the attribute definition id, or null if the attribute definition is not set.
     */
    public String getAttributeDefinitionId() {

        if (null != this.attributeDefinition) {
            return this.attributeDefinition.getId();
        } else {
            return null;
        }
    }
}
