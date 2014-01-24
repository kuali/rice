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
package org.kuali.rice.kim.impl.group;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.attribute.KimAttributeData;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

@Entity
@Table(name = "KRIM_GRP_ATTR_DATA_T")
public class GroupAttributeBo extends KimAttributeDataBo {

    private static final long serialVersionUID = 6380313567330578976L;

    @PortableSequenceGenerator(name = "KRIM_GRP_ATTR_DATA_ID_S")
    @GeneratedValue(generator = "KRIM_GRP_ATTR_DATA_ID_S")
    @Id
    @Column(name = "ATTR_DATA_ID")
    private String id;

    @Column(name = "GRP_ID")
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

    public static KimAttributeData to(GroupAttributeBo bo) {
        if (bo == null) {
            return null;
        }
        return KimAttributeData.Builder.create(bo).build();
    }

    public static GroupAttributeBo from(KimAttributeData im) {
        if (im == null) {
            return null;
        }
        GroupAttributeBo bo = new GroupAttributeBo();
        bo.setId(im.getId());
        bo.setAssignedToId(im.getAssignedToId());
        bo.setKimAttribute(KimAttributeBo.from(im.getKimAttribute()));
        final KimAttribute attribute = im.getKimAttribute();
        bo.setKimAttributeId((attribute == null ? null : attribute.getId()));
        bo.setAttributeValue(bo.getAttributeValue());
        bo.setKimTypeId(im.getKimTypeId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }
}
