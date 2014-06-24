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
package org.kuali.rice.krad.labs;

import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.provider.annotation.Relationship;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ROLE_MBR_T")
public class LabsRoleMember extends DataObjectBase {

    @Id
    @GeneratedValue(generator = "KRIM_ROLE_MBR_ID_S")
    @PortableSequenceGenerator(name = "KRIM_ROLE_MBR_ID_S")
    @Column(name = "ROLE_MBR_ID")
    private String id;

    private RoleMemberBo roleMember;

    @Column(name = "ROLE_ID")
    private String roleId;

    @Relationship(foreignKeyFields="roleId")
    @Transient
    private RoleBo role;

    private String roleNamespaceCode;

    private String roleName;

    private String roleDescription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RoleMemberBo getRoleMember() {
        return roleMember;
    }

    public void setRoleMember(RoleMemberBo roleMember) {
        this.roleMember = roleMember;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public RoleBo getRole() {
        return role;
    }

    public void setRole(RoleBo role) {
        this.role = role;
    }

    public String getRoleNamespaceCode() {
        if (getRole() != null) {
            roleNamespaceCode = getRole().getNamespaceCode();
        }

        return roleNamespaceCode;
    }

    public void setRoleNamespaceCode(String roleNamespaceCode) {
        this.roleNamespaceCode = roleNamespaceCode;
    }

    public String getRoleName() {
        if (getRole() != null) {
            roleName = getRole().getName();
        }

        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        if (getRole() != null) {
            roleDescription = getRole().getDescription();
        }

        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
