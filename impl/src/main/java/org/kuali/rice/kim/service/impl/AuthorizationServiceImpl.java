/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.util.List;

import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.AuthorizationService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleMembershipInfo;
import org.kuali.rice.kim.service.RoleService;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AuthorizationServiceImpl implements AuthorizationService {

	protected PermissionService permissionService = new PermissionServiceImpl();
	protected RoleService roleService;
	
	public void assignQualifiedPermissionToRole(String roleId, String permissionId,
			AttributeSet qualifier) {
		getPermissionService().assignQualifiedPermissionToRole( roleId, permissionId, qualifier );
	}
	public KimPermissionInfo getPermission(String permissionId) {
		return getPermissionService().getPermission( permissionId );
	}
	public KimPermissionInfo getPermissionByName(String permissionName) {
		return getPermissionService().getPermissionByName( permissionName );
	}
	public List<AttributeSet> getPermissionDetails(String principalId, String permissionId,
			AttributeSet roleQualification) {
		return getPermissionService().getPermissionDetails( principalId, permissionId,
				roleQualification );
	}
	public String getPermissionIdByName(String permissionName) {
		return getPermissionService().getPermissionIdByName( permissionName );
	}
	public boolean hasPermission(String principalId, String permissionId) {
		return getPermissionService().hasPermission( principalId, permissionId );
	}
	public boolean hasPermissionWithDetails(String principalId, String permissionId,
			AttributeSet permissionDetails) {
		return getPermissionService().hasPermissionWithDetails( principalId, permissionId,
				permissionDetails );
	}
	public boolean hasQualifiedPermission(String principalId, String permissionId,
			AttributeSet qualification) {
		return getPermissionService().hasQualifiedPermission( principalId, permissionId,
				qualification );
	}
	public boolean hasQualifiedPermissionByName(String principalId, String permissionName,
			AttributeSet qualification) {
		return getPermissionService().hasQualifiedPermissionByName( principalId, permissionName,
				qualification );
	}
	public boolean hasQualifiedPermissionWithDetails(String principalId, String permissionId,
			AttributeSet qualification, AttributeSet permissionDetails) {
		return getPermissionService().hasQualifiedPermissionWithDetails( principalId, permissionId,
				qualification, permissionDetails );
	}
	public List<KimPermissionInfo> lookupPermissions(AttributeSet searchCriteria) {
		return getPermissionService().lookupPermissions( searchCriteria );
	}
	public void savePermission(KimPermissionInfo permission) {
		getPermissionService().savePermission( permission );
	}
	public void assignQualifiedRoleToGroup(String groupId, String roleId,
			AttributeSet qualifier) {
		getRoleService().assignQualifiedRoleToGroup( groupId, roleId, qualifier );
	}
	public void assignQualifiedRoleToPrincipal(String principalId, String roleId,
			AttributeSet qualifier) {
		getRoleService().assignQualifiedRoleToPrincipal( principalId, roleId, qualifier );
	}
	public List<String> getImpliedRoleIds(String roleId) {
		return getRoleService().getImpliedRoleIds( roleId );
	}
	public List<String> getImplyingRoleIds(String roleId) {
		return getRoleService().getImplyingRoleIds( roleId );
	}
	public List<String> getPrincipalIdsWithQualifiedRole(String roleId,
			AttributeSet qualification) {
		return getRoleService().getPrincipalIdsWithQualifiedRole( roleId, qualification );
	}
	public List<String> getPrincipalIdsWithRole(String roleId) {
		return getRoleService().getPrincipalIdsWithRole( roleId );
	}
	public KimRoleInfo getRole(String roleId) {
		return getRoleService().getRole( roleId );
	}
	public KimRoleInfo getRoleByName(String namespaceCode, String roleName) {
		return getRoleService().getRoleByName( namespaceCode, roleName );
	}
	public String getRoleIdByName(String namespaceCode, String roleName) {
		return getRoleService().getRoleIdByName( namespaceCode, roleName );
	}
	public List<String> getRoleIdsForPrincipal(String principalId) {
		return getRoleService().getRoleIdsForPrincipal( principalId );
	}
	public List<String> getRoleIdsInNamespaceForPrincipal(String principalId, String namespaceCode) {
		return getRoleService().getRoleIdsInNamespaceForPrincipal( principalId, namespaceCode );
	}
	public List<String> getRoleIdsMatchingQualification(String principalId,
			AttributeSet qualification) {
		return getRoleService().getRoleIdsMatchingQualification( principalId, qualification );
	}
	public boolean isRoleActive(String roleId) {
		return getRoleService().isRoleActive( roleId );
	}
	public boolean principalHasQualifiedRole(String principalId, String roleId,
			AttributeSet qualification) {
		return getRoleService().principalHasQualifiedRole( principalId, roleId, qualification );
	}
	public boolean principalHasRole(String principalId, String roleId) {
		return getRoleService().principalHasRole( principalId, roleId );
	}
	public void saveRole(KimRoleInfo role) {
		getRoleService().saveRole( role );
	}
	
	
	public List<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
		return getRoleService().getRoleMembers( roleIds, qualification );
	}
	public boolean principalHasRole(String principalId, List<String> roleIds,
			AttributeSet qualification) {
		return getRoleService().principalHasRole( principalId, roleIds, qualification );
	}
	
	
	
	public PermissionService getPermissionService() {
		return permissionService;
	}
	protected RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleService();		
		}

		return roleService;
	}
		
}
