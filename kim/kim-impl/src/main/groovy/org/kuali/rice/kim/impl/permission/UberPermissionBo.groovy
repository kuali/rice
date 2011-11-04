/**
 * Copyright 2005-2011 The Kuali Foundation
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



import org.apache.commons.lang.StringUtils
import org.kuali.rice.kim.api.KimConstants
import org.kuali.rice.kim.api.identity.Person
import org.kuali.rice.kim.impl.group.GroupBo
import org.kuali.rice.kim.impl.role.RoleBo
import org.springframework.util.AutoPopulatingList
import org.kuali.rice.kim.api.permission.Permission

//strange - hacky non-PBO
class UberPermissionBo extends PermissionBo {

    private static final long serialVersionUID = 1L

    List<RoleBo> assignedToRoles = new AutoPopulatingList(RoleBo.class)
    String assignedToRoleNamespaceForLookup
    String assignedToRoleNameForLookup
    RoleBo assignedToRole
    String assignedToPrincipalNameForLookup
    Person assignedToPrincipal
    String assignedToGroupNamespaceForLookup
    String assignedToGroupNameForLookup
    GroupBo assignedToGroup
    String attributeName
    String attributeValue
    String detailCriteria

    public static UberPermissionBo from(PermissionBo bo) {
        if (bo == null) {
            return null
        }
        Permission.Builder builder =  Permission.Builder.create(bo)
        return (UberPermissionBo) PermissionBo.from(builder.build())

    }
    public String getAssignedToRolesToDisplay() {
        StringBuffer assignedToRolesText = new StringBuffer()
        for (RoleBo roleImpl: assignedToRoles) {
            assignedToRolesText.append(roleImpl.namespaceCode.trim()
                    + " "
                    + roleImpl.name.trim()
                    + KimConstants.KimUIConstants.COMMA_SEPARATOR)
        }
        return StringUtils.chomp(assignedToRolesText.toString(), KimConstants.KimUIConstants.COMMA_SEPARATOR)
    }

    /*String getRoleDetailsToDisplay(RoleBo roleImpl) {
        String value = roleImpl.namespaceCode.trim() + " " + roleImpl.name.trim() + KimConstants.KimUIConstants.COMMA_SEPARATOR
        return value;
    }*/
}

