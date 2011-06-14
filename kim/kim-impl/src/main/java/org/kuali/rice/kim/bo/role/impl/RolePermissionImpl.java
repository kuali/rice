/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.impl;

import org.hibernate.annotations.Type;
import org.kuali.rice.krad.bo.Inactivateable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KRIM_ROLE_PERM_T")
public class RolePermissionImpl extends PersistableBusinessObjectBase implements Inactivateable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ROLE_PERM_ID")
	protected String rolePermissionId;
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="PERM_ID")
	protected String permissionId;
	@Type(type="yes_no")
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToOne(targetEntity=KimPermissionImpl.class, fetch = FetchType.EAGER, cascade = { })
	@JoinColumn(name = "PERM_ID", insertable = false, updatable = false)
	protected KimPermissionImpl kimPermission;
	
	public KimPermissionImpl getPermission() {
		return kimPermission;
	}

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

	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setRolePermissionId(String rolePermissionId) {
		this.rolePermissionId = rolePermissionId;
	}

	/**
	 * @return the kimPermission
	 */
	public KimPermissionImpl getKimPermission() {
		return this.kimPermission;
	}

	/**
	 * @param kimPermission the kimPermission to set
	 */
	public void setKimPermission(KimPermissionImpl kimPermission) {
		this.kimPermission = kimPermission;
	}

}
