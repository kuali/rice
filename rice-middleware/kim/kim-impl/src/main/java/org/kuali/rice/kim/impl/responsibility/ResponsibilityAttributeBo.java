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
package org.kuali.rice.kim.impl.responsibility;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_RSP_ATTR_DATA_T")
public class ResponsibilityAttributeBo extends KimAttributeDataBo implements KimAttributeDataContract, BusinessObject {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ATTR_DATA_ID_S")
    @GeneratedValue(generator = "KRIM_ATTR_DATA_ID_S")
    @Id
    @Column(name = "ATTR_DATA_ID")
    private String id;

    @Column(name = "RSP_ID")
    private String assignedToId;

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KimAttributeData to(ResponsibilityAttributeBo bo) {
        if (bo == null) {
            return null;
        }
        return KimAttributeData.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static ResponsibilityAttributeBo from(KimAttributeData im) {
        if (im == null) {
            return null;
        }
        ResponsibilityAttributeBo bo = new ResponsibilityAttributeBo();
        bo.setId(im.getId());
        bo.setKimAttribute(KimAttributeBo.from(im.getKimAttribute()));
        final KimAttribute attribute = im.getKimAttribute();
        bo.setKimAttributeId((attribute == null ? null : attribute.getId()));
        bo.setAttributeValue(bo.getAttributeValue());
        final KimType type = im.getKimType();
        bo.setKimTypeId((type == null ? null : type.getId()));
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getAssignedToId() {
        return assignedToId;
    }

    @Override
    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

    @Override
    public void refresh() {
    }
}
