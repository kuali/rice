package org.kuali.rice.kim.v2.service.impl;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.v2.bo.Group;
import org.kuali.rice.kim.v2.bo.Permission;
import org.kuali.rice.kim.v2.bo.Principal;
import org.kuali.rice.kim.v2.bo.Role;
import org.kuali.rice.kim.v2.bo.entity.Entity;
import org.kuali.rice.kim.v2.service.IdentityManagementService;

// TODO implement this class
public class IdentityManagementServiceImpl implements IdentityManagementService {
	public String getAuthenticatedPrincipalName() {
		return null;
	}

    public boolean authenticationServiceValidatesPassword() {
    	return true;
    }
    
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

	public boolean isMemberOfGroup(String principalId, String groupId) {
		return true;
	}

	public List<String> getMemberPrincipalIds(String groupId) {
		return null;
	}

	public List<String> getDirectMemberPrincipalIds(String groupId) {
		return null;
	}

    public List<Group> getGroupsForPrincipal(String principalId) {
		return null;
	}

    public List<Group> getMemberGroups(String groupId) {
		return null;
	}

    public List<Group> getDirectMemberGroups(String groupId) {
		return null;
	}

    public Group getGroup(String groupId) {
		return null;
	}

    public List<Group> getParentGroups(String groupId) {
		return null;
	}

    public List<Group> getDirectParentGroups(String groupId) {
		return null;
	}

    public Entity getEntityByPrincipalName(String principalName) {
		return null;
	}

    public Principal getPrincipal(String principalId) {
		return null;
	}
	
	public Entity getEntity(String entityId) {
		return null;
	}
}
