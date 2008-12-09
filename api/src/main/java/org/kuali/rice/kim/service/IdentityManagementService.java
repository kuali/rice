package org.kuali.rice.kim.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is the front end for the KIM module.  Clients of KIM should access this service from
 * their applications.  If KIM is not running on the same machine (VM) as the application
 * (as would be the case with a standalone Rice server), then this service should be implemented 
 * locally within the application and access the core KIM services 
 * (Authentication/Authorization/Identity/Group) via the service bus.
 *
 *  For efficiency, implementations of this interface should add appropriate caching of
 *  the information retrieved from the core services for load and performance reasons.
 *  
 *  Most of the methods on this interface are straight pass-thrus to methods on the four core services.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface IdentityManagementService {
	
	// *******************************
	// IdentityService
	// *******************************
	
    KimEntity getEntityByPrincipalName(String principalName);
	KimEntity getEntity(String entityId);

	KimPrincipal getPrincipal(String principalId);
	KimPrincipal getPrincipalByPrincipalName(String principalName);

	// *******************************
	// GroupService
	// *******************************

	KimGroup getGroup(String groupId);
    KimGroup getGroupByName(String namespaceCode, String groupName);   
    List<String> getParentGroupIds(String groupId);
    List<String> getDirectParentGroupIds(String groupId);

    List<String> getGroupIdsForPrincipal(String principalId);
    List<String> getGroupIdsForPrincipal(String principalId, String namespaceCode );
    List<? extends KimGroup> getGroupsForPrincipal(String principalId);
    List<? extends KimGroup> getGroupsForPrincipal(String principalId, String namespaceCode );
    List<String> getMemberGroupIds(String groupId);
    List<String> getDirectMemberGroupIds(String groupId);

	boolean isMemberOfGroup(String principalId, String groupId);
	boolean isMemberOfGroup(String principalId, String namespaceCode, String groupName);
	List<String> getGroupMemberPrincipalIds(String groupId);
	List<String> getDirectGroupMemberPrincipalIds(String groupId);
    
	// *******************************
	// AuthenticationService
	// *******************************
	
	String getAuthenticatedPrincipalName(HttpServletRequest request);
    boolean authenticationServiceValidatesPassword();
    
	// *******************************
	// AuthorizationService
	// *******************************
	   
    // --------------------
    // Permission Data
    // --------------------
    
//    KimPermission getPermission(String permissionId);
//    KimPermission getPermissionByName( String permissionName );    

    // --------------------
    // Authorization Checks
    // --------------------
    
    boolean hasPermission(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails);
    boolean isAuthorized( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification );  

    boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails);
    boolean isAuthorizedByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification );  

    /**
     * Returns the matching permission objects for a principal.
     * 
     * @see PermissionService#getAuthorizedPermissions(String, String, AttributeSet, AttributeSet)
     */
    List<? extends KimPermission> getAuthorizedPermissions( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification );
    List<? extends KimPermission> getAuthorizedPermissionsByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification);

    List<AttributeSet> getRoleQualifiersByPermissionName( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification );
    List<AttributeSet> getRoleQualifiersByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification );

    // ----------------------
    // Responsibility Methods
    // ----------------------

    /**
     * Get the responsibility object with the given ID.
     */
    KimResponsibility getResponsibility(String responsibilityId);
    
 	/** 
 	 * Return the responsibility object for the given unique combination of namespace,
 	 * component and responsibility name.
 	 */
    List<? extends KimResponsibility> getResponsibilitiesByName( String namespaceCode, String responsibilityName );
    
    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibility( String principalId, String namespaceCode, String responsibilityName, AttributeSet qualification, AttributeSet responsibilityDetails );

    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibilityByTemplateName( String principalId, String namespaceCode, String responsibilityTemplateName, AttributeSet qualification, AttributeSet responsibilityDetails );

    List<ResponsibilityActionInfo> getResponsibilityActions( String namespaceCode, String responsibilityName, AttributeSet qualification, AttributeSet responsibilityDetails);
    List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName( String namespaceCode, String responsibilityTemplateName,	AttributeSet qualification, AttributeSet responsibilityDetails);

    // ----------------------
    // Cache Flush Methods
    // ----------------------
    
    void flushAllCaches();
    void flushEntityPrincipalCaches();
	void flushGroupCaches();
	void flushPermissionCaches();
	void flushResponsibilityCaches();
 
}
