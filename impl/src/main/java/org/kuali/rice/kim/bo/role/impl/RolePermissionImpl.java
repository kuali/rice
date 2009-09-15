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

import java.util.LinkedHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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
	
	@Column(name="ACTV_IND")
	protected boolean active;

	@OneToOne(targetEntity=KimPermissionImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
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

	/**
	 * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected LinkedHashMap toStringMapper() {
		LinkedHashMap m = new LinkedHashMap();
		m.put( "rolePermissionId", rolePermissionId );
		m.put( "roleId", roleId );
		m.put( "permissionId", permissionId );
		return m;
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
