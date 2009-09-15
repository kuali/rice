/*
 * Copyright 2007-2009 The Kuali Foundation
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

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KRIM_PND_ROLE_PERM_T")
public class KimDocumentRolePermission extends KimDocumentBoBase {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="ROLE_PERM_ID")
	protected String rolePermissionId;
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="PERM_ID")
	protected String permissionId;
	
	protected KimPermissionInfo kimPermission;
	
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

	public void setRolePermissionId(String rolePermissionId) {
		this.rolePermissionId = rolePermissionId;
	}

	/**
	 * @return the kimPermission
	 */
	public KimPermissionInfo getKimPermission() {
		if ( kimPermission == null || !StringUtils.equals( kimPermission.getPermissionId(), permissionId ) ) {
			kimPermission = KIMServiceLocator.getPermissionService().getPermission(permissionId);
		}
		return kimPermission;
	}

	/**
	 * @param kimPermission the kimPermission to set
	 */
	public void setKimPermission(KimPermissionInfo kimPermission) {
		this.kimPermission = kimPermission;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}
	
}
