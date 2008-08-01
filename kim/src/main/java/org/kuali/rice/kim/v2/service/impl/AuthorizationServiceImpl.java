package org.kuali.rice.kim.v2.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.v2.bo.Permission;
import org.kuali.rice.kim.v2.bo.Role;
import org.kuali.rice.kim.v2.service.AuthorizationService;

// TODO implement this class
public abstract class AuthorizationServiceImpl implements AuthorizationService {
    public boolean isPrincipalAuthorized(String principalId, String namespaceName, String permissionId) {
    	return true;
    }
    
    public boolean isPrincipalAuthorizedForQualifiedPermission(String principalId, String namespaceName,
    		String permissionId, Map<String,String> qualifications) {
    	return true;
    }
            
    public boolean principalHasRole(String principalId, String roleId) {
    	return true;
    }
    
    public boolean principalHasQualifiedRole(String principalId, String roleId, Map<String,String> qualifications) {
    	return true;
    }
    
    public List<String> getPrincipalIdsWithRole(String roleId) {
    	return null;
    }
        
    public List<String> getPrincipalIdsWithQualifiedRole(String roleId, Map<String,String> qualifications) {
    	return null;
    }
    
    public List<String> getGroupIdsWithRole(String roleId) {
    	return null;
    }

    public List<String> getGroupIdsWithQualifiedRole(String roleId, Map<String,String> qualifications) {
    	return null;
    }

    public List<Role> getRolesForPrincipal(String principalId) {
    	return null;
    }
    
    public List<Permission> getPermissionsForRole(String roleId) {
    	return null;
    }

    public Role getRole(String roleId) {
    	return null;
    }

    public Permission getPermission(String permissionId) {
    	return null;
    }

    public List<Role> lookupRoles(Map<String,String> searchCriteria, Map<String, String> roleAttributes, Map<String,String> qualifications) {
    	return null;
    }

    public List<Permission> lookupPermissions(Map<String,String> searchCriteria) {
    	return null;
    }

    public void saveRole(Role role) {
    }
    
    public void savePermission(Permission permission) {
    }

    public void assignQualifiedRoleToPrincipal(String principalId, String roleId, Map<String,String> qualifications) {
    }

    public void assignQualifiedRoleToGroup(String groupId, String roleId, Map<String,String> qualifications) {
    }
}