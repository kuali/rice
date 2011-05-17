/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kim.impl.permission

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import org.kuali.rice.kim.api.common.attribute.KimAttributeData
import org.kuali.rice.kim.api.common.attribute.KimAttributeDataContract
import org.kuali.rice.kim.impl.common.attribute.AttributeData
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo
import org.kuali.rice.kim.impl.type.KimTypeBo

@Entity
@Table(name="KRIM_PERM_ATTR_DATA_T")
public class PermissionAttributeBo extends AttributeData implements KimAttributeDataContract {
   
    @Column(name="PERM_ID")
    String assignedToId;

    /**
     * Converts a mutable bo to its immutable counterpart
     * @param bo the mutable business object
     * @return the immutable object
     */
    static KimAttributeData to(PermissionAttributeBo bo) {
        if (bo == null) {
            return null
        }

        return KimAttributeData.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     * @param im immutable object
     * @return the mutable bo
     */
    static PermissionAttributeBo from(KimAttributeData im) {
        if (im == null) {
            return null
        }

        PermissionAttributeBo bo = new PermissionAttributeBo()
        bo.id = im.id
        bo.assignedToId = im.assignedToId
        bo.kimAttribute = KimAttributeBo.from(im.kimAttribute)
        bo.kimAttributeId = im.kimAttribute?.id
        bo.attributeValue = bo.attributeValue
        bo.kimType = KimTypeBo.from(im.kimType)
        bo.kimTypeId = im.kimType?.id
        bo.versionNumber = im.versionNumber
		bo.objectId = im.objectId;

        return bo
    }
}
