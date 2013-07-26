package org.kuali.rice.kim.impl.role;

import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.role.RolePermission;
import org.kuali.rice.kim.api.role.RolePermissionContract;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "KRIM_ROLE_PERM_T")
public class RolePermissionBo extends PersistableBusinessObjectBase implements RolePermissionContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ROLE_PERM_ID")
    private String id;
    @Column(name = "ROLE_ID")
    private String roleId;
    @Column(name = "PERM_ID")
    private String permissionId;
    @Column(name = "ACTV_IND")
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    private boolean active;
    @OneToOne(targetEntity = PermissionBo.class, cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "PERM_ID", insertable = false, updatable = false)
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
