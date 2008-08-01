package org.kuali.rice.kim.v2.service.soap.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.dto.PermissionDTO;
import org.kuali.rice.kim.dto.RoleDTO;
import org.kuali.rice.kim.v2.service.soap.AuthorizationServiceSoap;

// TODO implement this class by piggy backing on the standard implementation ("has a" not "is a") and translating from interface to DTOs
public class AuthorizationServiceSoapImpl implements AuthorizationServiceSoap {
	public List<PermissionDTO> getWebServiceSafePermissionsForRole(String roleId) {
		return null;
	}

	public List<RoleDTO> getWebServiceSafeRolesForPrincipal(String principalId) {
		return null;
	}

	public void assignQualifiedRoleToGroup(String groupId, String roleId,
			Map<String, String> qualifications) {
	}

	public void assignQualifiedRoleToPrincipal(String principalId,
			String roleId, Map<String, String> qualifications) {
	}

	public List<String> getGroupIdsWithQualifiedRole(String roleId,
			Map<String, String> qualifications) {
		return null;
	}

	public List<String> getGroupIdsWithRole(String roleId) {
		return null;
	}

	public List<String> getPrincipalIdsWithQualifiedRole(String roleId,
			Map<String, String> qualifications) {
		return null;
	}

	public List<String> getPrincipalIdsWithRole(String roleId) {
		return null;
	}

	public boolean isPrincipalAuthorized(String principalId,
			String namespaceName, String permissionId) {
		return false;
	}

	public boolean isPrincipalAuthorizedForQualifiedPermission(
			String principalId, String namespaceName, String permissionId,
			Map<String, String> qualifications) {
		return false;
	}

	public boolean principalHasQualifiedRole(String principalId, String roleId,
			Map<String, String> qualifications) {
		return false;
	}

	public boolean principalHasRole(String principalId, String roleId) {
		return false;
	}
}