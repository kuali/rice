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
package org.kuali.rice.kim.document;

import java.util.List;

import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;


/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocument extends IdentityManagementTypeAttributeTransactionalDocument {

	// principal data
	protected String roleId;
	protected String roleTypeId;
	protected String roleTypeName;
	protected String roleNamespace;
	protected String roleName;
	protected boolean active;

	protected List<RolePermissionImpl> permissions;
	protected List<RoleResponsibilityImpl> responsibilities;
	protected List<RoleDocumentDelegation> delegations; 
	
	public IdentityManagementRoleDocument() {
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the roleId
	 */
	public String getRoleId() {
		return this.roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return this.roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the roleNamespace
	 */
	public String getRoleNamespace() {
		return this.roleNamespace;
	}

	/**
	 * @param roleNamespace the roleNamespace to set
	 */
	public void setRoleNamespace(String roleNamespace) {
		this.roleNamespace = roleNamespace;
	}

	/**
	 * @return the roleTypeId
	 */
	public String getRoleTypeId() {
		return this.roleTypeId;
	}

	/**
	 * @param roleTypeId the roleTypeId to set
	 */
	public void setRoleTypeId(String roleTypeId) {
		this.roleTypeId = roleTypeId;
	}

	/**
	 * @return the roleTypeName
	 */
	public String getRoleTypeName() {
		return this.roleTypeName;
	}

	/**
	 * @param roleTypeName the roleTypeName to set
	 */
	public void setRoleTypeName(String roleTypeName) {
		this.roleTypeName = roleTypeName;
	}

	/**
	 * @return the delegations
	 */
	public List<RoleDocumentDelegation> getDelegations() {
		return this.delegations;
	}

	/**
	 * @param delegations the delegations to set
	 */
	public void setDelegations(List<RoleDocumentDelegation> delegations) {
		this.delegations = delegations;
	}

	/**
	 * @return the permissions
	 */
	public List<RolePermissionImpl> getPermissions() {
		return this.permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(List<RolePermissionImpl> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the responsibilities
	 */
	public List<RoleResponsibilityImpl> getResponsibilities() {
		return this.responsibilities;
	}

	/**
	 * @param responsibilities the responsibilities to set
	 */
	public void setResponsibilities(List<RoleResponsibilityImpl> responsibilities) {
		this.responsibilities = responsibilities;
	}

}