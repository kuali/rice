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
package org.kuali.rice.kim.impl.permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_PERM_ATTR_DATA_T")
public class PermissionAttributeBo extends KimAttributeDataBo implements KimAttributeDataContract, BusinessObject {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ATTR_DATA_ID_S")
    @GeneratedValue(generator = "KRIM_ATTR_DATA_ID_S")
    @Id
    @Column(name = "ATTR_DATA_ID")
    private String id;

    @Column(name = "PERM_ID")
    private String assignedToId;

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

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static KimAttributeData to(PermissionAttributeBo bo) {
        if (bo == null) {
            return null;
        }
        return KimAttributeData.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    public static PermissionAttributeBo from(KimAttributeData im) {
        if (im == null) {
            return null;
        }
        PermissionAttributeBo bo = new PermissionAttributeBo();
        bo.setId(im.getId());
        bo.setAssignedToId(im.getAssignedToId());
        bo.setKimAttribute(KimAttributeBo.from(im.getKimAttribute()));
        bo.setKimAttributeId(im.getKimAttribute() != null ? im.getKimAttribute().getId() : null);
        bo.setAttributeValue(bo.getAttributeValue());
        bo.setKimTypeId(im.getKimTypeId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public void refresh() {
    }
}
