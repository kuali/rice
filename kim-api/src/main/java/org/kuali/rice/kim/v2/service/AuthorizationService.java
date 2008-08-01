package org.kuali.rice.kim.v2.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.v2.bo.Permission;
import org.kuali.rice.kim.v2.bo.Role;

public interface AuthorizationService extends AuthorizationServiceBase {
	// CLIENT API
	
    // EXTENDED CLIENT API
    
    public List<Role> getRolesForPrincipal(String principalId);
    
    public List<Permission> getPermissionsForRole(String roleId);

    // KIM INTERNAL METHODS
    
    public Role getRole(String roleId);

    public Permission getPermission(String permissionId);

    public List<Role> lookupRoles(Map<String,String> searchCriteria, Map<String, String> roleAttributes, Map<String,String> qualifications);

    public List<Permission> lookupPermissions(Map<String,String> searchCriteria);

    public void saveRole(Role role);
    
    public void savePermission(Permission permission);
}