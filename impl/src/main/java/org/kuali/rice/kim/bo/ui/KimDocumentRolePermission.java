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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@IdClass(KimDocumentRolePermissionId.class)
@Entity
@Table(name="KRIM_PND_ROLE_PERM_T")
public class KimDocumentRolePermission extends KimDocumentBoActivatableBase {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator="KRIM_ROLE_PERM_ID_S")
	@GenericGenerator(name="KRIM_ROLE_PERM_ID_S",strategy="org.kuali.rice.core.jpa.spring.RiceNumericStringSequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KRIM_ROLE_PERM_ID_S"),
			@Parameter(name="value_column",value="id")
		})
	@Column(name="ROLE_PERM_ID")
	protected String rolePermissionId;
	@Column(name="ROLE_ID")
	protected String roleId;
	@Column(name="PERM_ID")
	protected String permissionId;
	@Transient
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
	
}
