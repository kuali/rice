package org.kuali.rice.kim.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.ResponsibilityResolution;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.AuthenticationService;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.ResponsibilityService;

public class IdentityManagementServiceImpl implements IdentityManagementService {
	
	protected AuthenticationService authenticationService; 
//	protected AuthorizationService authorizationService; 
	protected PermissionService permissionService; 
	protected ResponsibilityService responsibilityService;  
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
    	return getPermissionService().hasPermission( principalId, permissionId );
    }

    public boolean hasQualifiedPermission(String principalId, String permissionId, AttributeSet qualification ) {
    	return getPermissionService().hasQualifiedPermission( principalId, permissionId, qualification );
    }
    
    public boolean hasQualifiedPermissionByName(String principalId,  
    		String permissionName, AttributeSet qualification) {
    	return getPermissionService().hasQualifiedPermissionByName( principalId, permissionName, qualification );
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#hasQualifiedPermissionWithDetails(java.lang.String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean hasQualifiedPermissionWithDetails(String principalId,
    		String permissionId, AttributeSet qualification,
    		AttributeSet permissionDetails) {
    	return getPermissionService().hasQualifiedPermissionWithDetails( principalId, permissionId, qualification, permissionDetails );
    }
    

    public KimPermission getPermission(String permissionId) {
		return getPermissionService().getPermission( permissionId );
	}

    public KimPermission getPermissionByName(String permissionName ) {
		return getPermissionService().getPermissionByName( permissionName );
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

//	public AuthorizationService getAuthorizationService() {
//		if ( authorizationService == null ) {
//			authorizationService = KIMServiceLocator.getAuthorizationService();
//		}
//		return authorizationService;
//	}

	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KIMServiceLocator.getPermissionService();
		}
		return permissionService;
	}

	public ResponsibilityService getResponsibilityService() {
		if ( responsibilityService == null ) {
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}
	
    // ----------------------
    // Responsibility Methods
    // ----------------------

	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getPrincipalIdsWithResponsibility(java.lang.String, AttributeSet, AttributeSet)
	 */
	public List<String> getPrincipalIdsWithResponsibility(String responsibilityId,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getPrincipalIdsWithResponsibility( responsibilityId, qualification, responsibilityDetails );
	}
	
	public List<String> getPrincipalIdsWithResponsibilityByName( String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().getPrincipalIdsWithResponsibilityByName( responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibility(java.lang.String)
	 */
	public KimResponsibility getResponsibility(String responsibilityId) {
		return getResponsibilityService().getResponsibility( responsibilityId );
	}

	public KimResponsibility getResponsibilityByName( String responsibilityName) {
		return getResponsibilityService().getResponsibilityByName( responsibilityName );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityInfo(java.lang.String, AttributeSet, AttributeSet)
	 */
	public List<? extends ResponsibilityResolution> getResponsibilityInfo(String responsibilityId,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityInfo( responsibilityId, qualification, responsibilityDetails );
	}
	
	public List<? extends ResponsibilityResolution> getResponsibilityInfoByName( String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityInfoByName( responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasQualifiedResponsibilityWithDetails(java.lang.String, java.lang.String, AttributeSet, AttributeSet)
	 */
	public boolean hasQualifiedResponsibilityWithDetails(String principalId,
			String responsibilityId, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasQualifiedResponsibilityWithDetails( principalId, responsibilityId, qualification, responsibilityDetails );
	}
}
