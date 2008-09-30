package org.kuali.rice.kim.service.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.PrincipalResponsibility;
import org.kuali.rice.kim.service.AuthenticationService;
import org.kuali.rice.kim.service.AuthorizationService;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;

public class IdentityManagementServiceImpl implements IdentityManagementService {
	
	protected AuthenticationService authenticationService; 
	protected AuthorizationService authorizationService; 
	protected IdentityService identityService;
	protected GroupService groupService;
	
	// AUTHENTICATION SERVICE
	
	public String getAuthenticatedPrincipalName(HttpServletRequest request) {
		return getAuthenticationService().getPrincipalName(request);
	}

    public boolean authenticationServiceValidatesPassword() {
    	return getAuthenticationService().validatePassword();
    }
    
    // AUTHORIZATION SERVICE
    
    public boolean hasPermission(String principalId, String permissionId) {
    	return getAuthorizationService().hasPermission( principalId, permissionId );
    }

    public boolean hasQualifiedPermission(String principalId, String permissionId, Map<String,String> qualification ) {
    	return getAuthorizationService().hasQualifiedPermission( principalId, permissionId, qualification );
    }
    
    public boolean hasQualifiedPermissionByName(String principalId, String namespaceCode, 
    		String permissionName, Map<String,String> qualification) {
    	return getAuthorizationService().hasQualifiedPermissionByName( principalId, namespaceCode, permissionName, qualification );
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#hasQualifiedPermissionWithDetails(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
     */
    public boolean hasQualifiedPermissionWithDetails(String principalId,
    		String permissionId, Map<String,String> qualification,
    		Map<String,String> permissionDetails) {
    	return getAuthorizationService().hasQualifiedPermissionWithDetails( principalId, permissionId, qualification, permissionDetails );
    }
    

    public KimPermission getPermission(String permissionId) {
		return getAuthorizationService().getPermission( permissionId );
	}

    public KimPermission getPermissionByName(String namespaceCode, String permissionName ) {
		return getAuthorizationService().getPermissionByName( namespaceCode, permissionName );
	}
    
    // GROUP SERVICE
    
    
	public boolean isMemberOfGroup(String principalId, String groupId) {
		return getGroupService().isMemberOfGroup(principalId, groupId);
	}

	public List<String> getGroupMemberPrincipalIds(String groupId) {
		return getGroupService().getMemberPrincipalIds(groupId);
	}

	public List<String> getDirectGroupMemberPrincipalIds(String groupId) {
		return getGroupService().getDirectMemberPrincipalIds(groupId);
	}

    public List<String> getGroupIdsForPrincipal(String principalId) {
		return getGroupService().getGroupIdsForPrincipal(principalId);
	}

    public List<String> getGroupIdsForPrincipal(String principalId, String namespaceCode ) {
		return getGroupService().getGroupIdsForPrincipalByNamespace(principalId, namespaceCode );
	}

    public List<? extends KimGroup> getGroupsForPrincipal(String principalId) {
		return getGroupService().getGroupsForPrincipal(principalId);
	}

    public List<? extends KimGroup> getGroupsForPrincipal(String principalId, String namespaceCode ) {
		return getGroupService().getGroupsForPrincipalByNamespace(principalId, namespaceCode );
	}
    
    public List<String> getMemberGroupIds(String groupId) {
		return getGroupService().getMemberGroupIds(groupId);
	}

    public List<String> getDirectMemberGroupIds(String groupId) {
		return getGroupService().getDirectMemberGroupIds(groupId);
	}

    public KimGroup getGroup(String groupId) {
		return getGroupService().getGroupInfo(groupId);
	}

//    public boolean groupExistsByName( String namespaceCode, String groupName ) {
//    	return getGroupService().groupExistsByName(namespaceCode, groupName);
//    }

    public KimGroup getGroupByName(String namespaceCode, String groupName) {
    	return getGroupService().getGroupInfoByName( namespaceCode, groupName );
    }
    
    public List<String> getParentGroupIds(String groupId) {
		return getGroupService().getParentGroupIds( groupId );
	}

    public List<String> getDirectParentGroupIds(String groupId) {
		return getGroupService().getDirectParentGroupIds( groupId );
	}

    
    // IDENTITY SERVICE
    
    
    public KimEntity getEntityByPrincipalName(String principalName) {
		return getIdentityService().getEntityByPrincipalName(principalName);
	}

    public KimPrincipal getPrincipal(String principalId) {
		return getIdentityService().getPrincipal(principalId);
	}
    
    public KimPrincipal getPrincipalByPrincipalName(String principalName) {
    	return getIdentityService().getPrincipalByPrincipalName(principalName);
    }
	
	public KimEntity getEntity(String entityId) {
		return getIdentityService().getEntity(entityId);
	}

	
	
	// OTHER METHODS
	
	
	
	public AuthenticationService getAuthenticationService() {
		if ( authenticationService == null ) {
			authenticationService = KIMServiceLocator.getAuthenticationService();
		}
		return authenticationService;
	}
	public IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KIMServiceLocator.getIdentityService();
		}
		return identityService;
	}

	public GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();
		}
		return groupService;
	}

	public AuthorizationService getAuthorizationService() {
		if ( authorizationService == null ) {
			authorizationService = KIMServiceLocator.getAuthorizationService();
		}
		return authorizationService;
	}

    // ----------------------
    // Responsibility Methods
    // ----------------------

	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getPrincipalIdsWithResponsibility(java.lang.String, java.util.Map, java.util.Map)
	 */
	public List<String> getPrincipalIdsWithResponsibility(String responsibilityId,
			Map<String,String> qualification, Map<String,String> responsibilityDetails) {
		return getAuthorizationService().getPrincipalIdsWithResponsibility( responsibilityId, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getPrincipalIdsWithResponsibilityByName(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
	 */
	public List<String> getPrincipalIdsWithResponsibilityByName(String namespaceCode,
			String responsibilityName, Map<String,String> qualification,
			Map<String,String> responsibilityDetails) {
		return getAuthorizationService().getPrincipalIdsWithResponsibilityByName( namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibility(java.lang.String)
	 */
	public KimResponsibility getResponsibility(String responsibilityId) {
		return getAuthorizationService().getResponsibility( responsibilityId );
	}
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityByName(java.lang.String, java.lang.String)
	 */
	public KimResponsibility getResponsibilityByName(String namespaceCode,
			String responsibilityName) {
		return getAuthorizationService().getResponsibilityByName( namespaceCode, responsibilityName );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityInfo(java.lang.String, java.util.Map, java.util.Map)
	 */
	public List<? extends PrincipalResponsibility> getResponsibilityInfo(String responsibilityId,
			Map<String,String> qualification, Map<String,String> responsibilityDetails) {
		return getAuthorizationService().getResponsibilityInfo( responsibilityId, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityInfoByName(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
	 */
	public List<? extends PrincipalResponsibility> getResponsibilityInfoByName(String namespaceCode,
			String responsibilityName, Map<String,String> qualification,
			Map<String,String> responsibilityDetails) {
		return getAuthorizationService().getResponsibilityInfoByName( namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasQualifiedResponsibilityWithDetails(java.lang.String, java.lang.String, java.util.Map, java.util.Map)
	 */
	public boolean hasQualifiedResponsibilityWithDetails(String principalId,
			String responsibilityId, Map<String,String> qualification,
			Map<String,String> responsibilityDetails) {
		return getAuthorizationService().hasQualifiedResponsibilityWithDetails( principalId, responsibilityId, qualification, responsibilityDetails );
	}
}
