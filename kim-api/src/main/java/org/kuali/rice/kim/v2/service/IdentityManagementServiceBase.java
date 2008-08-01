package org.kuali.rice.kim.v2.service;

import java.util.List;
import java.util.Map;

// THIS DEFERS TO THE OTHER SERVICES AND ADDS CACHING - THIS IS WHAT CLIENTS SHOULD ACTUALLY USE - HOW TO MAKE THAT CLEAR?
public interface IdentityManagementServiceBase {
    // AuthenticationService.getPrincipalName
	public String getAuthenticatedPrincipalName();

	// AuthenticationService.getPrincipalName    
    public boolean authenticationServiceValidatesPassword();
    
    // AuthorizationService...

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

    
    // GroupService...

	// CLIENT API	

	public boolean isMemberOfGroup(String principalId, String groupId);

    // EXTENDED CLIENT API
    
	public List<String> getMemberPrincipalIds(String groupId);

	public List<String> getDirectMemberPrincipalIds(String groupId);

    // KIM INTERNAL METHODS


    // IdentityService

    // CLIENT API	

	// EXTENDED CLIENT API
    
	// KIM INTERNAL METHODS
}
