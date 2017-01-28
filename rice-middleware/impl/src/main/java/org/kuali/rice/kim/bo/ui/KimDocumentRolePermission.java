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
package org.kuali.rice.kim.bo.ui;

import java.util.Iterator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_PND_ROLE_PERM_T")
public class KimDocumentRolePermission extends KimDocumentBoActivatableBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ROLE_PERM_ID_S")
    @GeneratedValue(generator = "KRIM_ROLE_PERM_ID_S")
    @Id
    @Column(name = "ROLE_PERM_ID")
    protected String rolePermissionId;

    @Column(name = "ROLE_ID")
    protected String roleId;

    @Column(name = "PERM_ID")
    protected String permissionId;

    @Transient
    protected Permission permission;

    @Transient
    protected String name;

    @Transient
    protected String namespaceCode;

    public String getPermissionId() {
        return permissionId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getRolePermissionId() {
        return rolePermissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setRolePermissionId(String rolePermissionId) {
        this.rolePermissionId = rolePermissionId;
    }

    public Permission getPermission() {
        if (null != permissionId) {
            if (permission == null || !StringUtils.equals(permission.getId(), permissionId)) {
                permission = KimApiServiceLocator.getPermissionService().getPermission(permissionId);
            }
        }
        return permission;
    }

    public String getPermissionDetailValues() {
        Permission perm = getPermission();
        StringBuilder sb = new StringBuilder();
        if (perm.getAttributes() != null) {
            Iterator<String> keyIter = perm.getAttributes().keySet().iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                sb.append(key).append('=').append(perm.getAttributes().get(key));
                if (keyIter.hasNext()) {
                    sb.append(KimConstants.KimUIConstants.COMMA_SEPARATOR).append(" ");
                }
            }
        }
        return sb.toString();
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getName() {
        if (null == permission) {
            getPermission();
        }
        if (null == permission) {
            return "";
        }
        return permission.getName();
    }

    public String getNamespaceCode() {
        if (null == permission) {
            getPermission();
        }
        if (null == permission) {
            return "";
        }
        return permission.getNamespaceCode();
    }
}
