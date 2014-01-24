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
package org.kuali.rice.kim.impl.role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * /**
 * The column names have been used in a native query in RoleDaoOjb and will need to be modified if any changes to the
 * column names are made here.
 */
@Entity
@Table(name = "KRIM_ROLE_MBR_ATTR_DATA_T")
public class RoleMemberAttributeDataBo extends KimAttributeDataBo {

    @PortableSequenceGenerator(name = "KRIM_ATTR_DATA_ID_S")
    @GeneratedValue(generator = "KRIM_ATTR_DATA_ID_S")
    @Id
    @Column(name = "ATTR_DATA_ID")
    private String id;

    @Column(name = "ROLE_MBR_ID")
    protected String assignedToId;

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
        return this.assignedToId;
    }

    @Override
    public void setAssignedToId(String roleMemberId) {
        this.assignedToId = roleMemberId;
    }

    public boolean equals(RoleMemberAttributeDataBo roleMemberAttributeDataBo) {
        if (!StringUtils.equals(roleMemberAttributeDataBo.getKimTypeId(), getKimTypeId())) {
            return false;
        }
        if (!StringUtils.equals(roleMemberAttributeDataBo.getKimAttributeId(), getKimAttributeId())) {
            return false;
        }
        if (!StringUtils.equals(roleMemberAttributeDataBo.getAttributeValue(), getAttributeValue())) {
            return false;
        }
        return true;
    }
}
