package org.kuali.rice.kim.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
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
    
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails) {
    	return getPermissionService().hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
    }
    
    public boolean isAuthorized(String principalId, String namespaceCode, 
    		String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    }

    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
    	return getPermissionService().hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
    }
    
    public boolean isAuthorizedByTemplateName(String principalId,
    		String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification );
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#getAuthorizedPermissions(java.lang.String, String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public List<? extends KimPermission> getAuthorizedPermissions(String principalId,
    		String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissions( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    }

    public List<? extends KimPermission> getAuthorizedPermissionsByTemplateName(String principalId,
    		String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissionsByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
    }
    
    @Deprecated
    public boolean hasPermission(String principalId, String permissionName, AttributeSet permissionDetails) {
    	return getPermissionService().hasPermission( principalId, permissionName, permissionDetails );
    }
    
    @Deprecated
    public boolean isAuthorized(String principalId,
    		String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().isAuthorized( principalId, permissionName, permissionDetails, qualification );
    }

    @Deprecated
    public boolean hasPermissionByTemplateName(String principalId, String permissionTemplateName, AttributeSet permissionDetails) {
    	return getPermissionService().hasPermissionByTemplateName( principalId, permissionTemplateName, permissionDetails );
    }
    
    @Deprecated
    public boolean isAuthorizedByTemplateName(String principalId,
    		String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().isAuthorizedByTemplateName( principalId, permissionTemplateName, permissionDetails, qualification );
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#getAuthorizedPermissions(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Deprecated
    public List<? extends KimPermission> getAuthorizedPermissions(String principalId,
    		String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissions( principalId, permissionName, permissionDetails, qualification );
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
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibility(java.lang.String)
	 */
	public KimResponsibility getResponsibility(String responsibilityId) {
		return getResponsibilityService().getResponsibility( responsibilityId );
	}

	@Deprecated
	public List<? extends KimResponsibility> getResponsibilitiesByName( String responsibilityName) {
		return getResponsibilityService().getResponsibilitiesByName( responsibilityName );
	}
	
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibility(java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
	 */
	public boolean hasResponsibility(String principalId, String namespaceCode,
			String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibility( principalId, namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}

	public List<? extends KimResponsibility> getResponsibilitiesByName( String namespaceCode, String responsibilityName) {
		return getResponsibilityService().getResponsibilitiesByName( namespaceCode, responsibilityName );
	}
	
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibility(java.lang.String, java.lang.String, AttributeSet, AttributeSet)
	 */
	@Deprecated
	public boolean hasResponsibility(String principalId,
			String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibility( principalId, responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityActions(java.lang.String, AttributeSet, AttributeSet)
	 */
	@Deprecated
	public List<ResponsibilityActionInfo> getResponsibilityActions(String responsibilityName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActions( responsibilityName, qualification, responsibilityDetails );
	}
	
	public List<ResponsibilityActionInfo> getResponsibilityActions( String namespaceCode, String responsibilityName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActions( namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityActionsByTemplateName(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName(
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActionsByTemplateName(namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibilityByTemplateName(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public boolean hasResponsibilityByTemplateName(String principalId,
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibilityByTemplateName(principalId, namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}
}
