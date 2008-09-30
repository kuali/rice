/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.role.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.PermissionDetailsInfo;
import org.kuali.rice.kim.bo.role.RolePermission;
import org.kuali.rice.kim.bo.role.RolePermissionAttributeData;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KR_KIM_ROLE_PERMISSION_T")
public class RolePermissionImpl extends PersistableBusinessObjectBase implements RolePermission {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ROLE_PERM_ID")
	protected String rolePermissionId;
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="PERM_ID")
	protected String permissionId;
	
	@OneToOne(targetEntity=KimPermissionImpl.class, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "PERM_ID", insertable = false, updatable = false)
	protected KimPermission kimPermission;
	
	@OneToMany(targetEntity=RolePermissionAttributeDataImpl.class,cascade={CascadeType.ALL},fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_PERM_ID", insertable=false, updatable=false)
	protected List<RolePermissionAttributeDataImpl> details;
	
	
	public KimPermission getPermission() {
		return kimPermission;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.RolePermission#getPermissionId()
	 */
	public String getPermissionId() {
		return permissionId;
	}

	public List<? extends RolePermissionAttributeData> getDetails() {
		return details;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.RolePermission#getPermissionDetails()
	 */
	public Map<String, String> getPermissionDetails() {
		Map<String, String> map = new HashMap<String, String>();
		for (RolePermissionAttributeDataImpl data : details) {
			map.put(data.getKimAttribute().getAttributeName(), data.getAttributeValue());
		}
		return map;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.RolePermission#getRoleId()
	 */
	public String getRoleId() {
		return roleId;
	}

	/**
	 * @see org.kuali.rice.kim.bo.role.RolePermission#getRolePermissionId()
	 */
	public String getRolePermissionId() {
		return rolePermissionId;
	}

	public boolean hasDetails() {
		return !details.isEmpty();
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

	public PermissionDetailsInfo toPermissionDetail() {
		PermissionDetailsInfo info = new PermissionDetailsInfo();
		info.setPermissionId( getPermissionId() );
		info.setPermissionDetails( getPermissionDetails() );
		return info;
	}
}
