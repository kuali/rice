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
package org.kuali.rice.krad.demo.uif.form;

import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.impl.group.GroupBo;
import org.kuali.rice.kim.impl.identity.principal.PrincipalBo;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.krad.bo.DataObjectBase;

import java.io.Serializable;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UIRoleMember extends DataObjectBase implements Serializable {

    private String typeCode;

    private String principalName;

    private String name;

    private String description;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public void setType(MemberType type) {
        typeCode = type.getCode();
    }

    public MemberType getType() {
        return MemberType.fromCode(typeCode);
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
