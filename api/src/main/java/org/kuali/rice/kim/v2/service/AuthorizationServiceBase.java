package org.kuali.rice.kim.v2.service;

import java.util.List;
import java.util.Map;

public interface AuthorizationServiceBase {
	// CLIENT API
	
    public boolean isPrincipalAuthorized(String principalId, String namespaceName, String permissionId);
    
    public boolean isPrincipalAuthorizedForQualifiedPermission(String principalId, String namespaceName,
    		String permissionId, Map<String,String> qualifications);
            
    // EXTENDED CLIENT API
    
    public boolean principalHasRole(String principalId, String roleId);
    
    public boolean principalHasQualifiedRole(String principalId, String roleId, Map<String,String> qualifications);
    
    public List<String> getPrincipalIdsWithRole(String roleId);
        
    public List<String> getPrincipalIdsWithQualifiedRole(String roleId, Map<String,String> qualifications);
    
    public List<String> getGroupIdsWithRole(String roleId);

    public List<String> getGroupIdsWithQualifiedRole(String roleId, Map<String,String> qualifications);

    // KIM INTERNAL METHODS
    
    public void assignQualifiedRoleToPrincipal(String principalId, String roleId, Map<String,String> qualifications);

    public void assignQualifiedRoleToGroup(String groupId, String roleId, Map<String,String> qualifications);
}