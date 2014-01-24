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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.api.role.RolePermission;
import org.kuali.rice.kim.api.role.RolePermissionContract;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

@Entity
@Table(name = "KRIM_ROLE_PERM_T")
public class RolePermissionBo extends DataObjectBase implements RolePermissionContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ROLE_PERM_ID_S")
    @GeneratedValue(generator = "KRIM_ROLE_PERM_ID_S")
    @Id
    @Column(name = "ROLE_PERM_ID")
    private String id;

    @Column(name = "ROLE_ID")
    private String roleId;

    @Column(name = "PERM_ID")
    private String permissionId;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active;

    @ManyToOne(targetEntity = PermissionBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "PERM_ID", referencedColumnName = "PERM_ID", insertable = false, updatable = false)
    private PermissionBo permission;

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static RolePermission to(RolePermissionBo bo) {
        if (bo == null) {
            return null;
        }
        return RolePermission.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static RolePermissionBo from(RolePermission im) {
        if (im == null) {
            return null;
        }
        RolePermissionBo bo = new RolePermissionBo();
        bo.id = im.getId();
        bo.roleId = im.getRoleId();
        bo.permissionId = im.getPermissionId();
        bo.active = im.isActive();
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());
        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public PermissionBo getPermission() {
        return permission;
    }

    public void setPermission(PermissionBo permission) {
        this.permission = permission;
    }
}
