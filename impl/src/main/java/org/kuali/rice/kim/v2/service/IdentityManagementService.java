package org.kuali.rice.kim.v2.service;

import java.util.List;

import org.kuali.rice.kim.v2.bo.Entity;
import org.kuali.rice.kim.v2.bo.Group;
import org.kuali.rice.kim.v2.bo.Permission;
import org.kuali.rice.kim.v2.bo.Principal;
import org.kuali.rice.kim.v2.bo.Role;

// THIS DEFERS TO THE OTHER SERVICES AND ADDS CACHING - THIS IS WHAT CLIENTS SHOULD ACTUALLY USE - HOW TO MAKE THAT CLEAR?
public interface IdentityManagementService extends IdentityManagementServiceBase {
    // AuthenticationService...

	
    // AuthorizationService...

	// CLIENT API
	
    // EXTENDED CLIENT API
    
    public List<Role> getRolesForPrincipal(String principalId);
    
    public List<Permission> getPermissionsForRole(String roleId);

    // KIM INTERNAL METHODS

    public Role getRole(String roleId);

    public Permission getPermission(String permissionId);

    
    // GroupService...

	// CLIENT API	

    // EXTENDED CLIENT API
    
    public List<Group> getGroupsForPrincipal(String principalId);

    public List<Group> getMemberGroups(String groupId);

    public List<Group> getDirectMemberGroups(String groupId);

    // KIM INTERNAL METHODS

    public Group getGroup(String groupId);

    public List<Group> getParentGroups(String groupId);

    public List<Group> getDirectParentGroups(String groupId);


    // IdentityService

    public Entity getEntityByPrincipalName(String principalName);

    // CLIENT API	

	// EXTENDED CLIENT API
    
	// KIM INTERNAL METHODS
	public Principal getPrincipal(String principalId);
	
	public Entity getEntity(String entityId);
}
