package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.PrincipalResponsibility;

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
	List<String> getGroupMemberPrincipalIds(String groupId);
	List<String> getDirectGroupMemberPrincipalIds(String groupId);
	
//    boolean groupExistsByName( String namespaceCode, String groupName );
    
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
    
    KimPermission getPermission(String permissionId);
    KimPermission getPermissionByName( String permissionName );    

    // --------------------
    // Authorization Checks
    // --------------------
    
    /**
     * @see AuthorizationService#hasPermission(java.lang.String, java.lang.String)
     */
    boolean hasPermission(String principalId, String permissionId);
    boolean hasQualifiedPermission(String principalId, String permissionId, Map<String,String> qualification );
    boolean hasQualifiedPermissionWithDetails( String principalId, String permissionId, Map<String,String> qualification, Map<String,String> permissionDetails );  
    boolean hasQualifiedPermissionByName( String principalId, String permissionName, Map<String,String> qualification );
    
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
    KimResponsibility getResponsibilityByName( String responsibilityName );
    
    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasQualifiedResponsibilityWithDetails( String principalId, String responsibilityId, Map<String,String> qualification, Map<String,String> responsibilityDetails );

    /**
     * Get a list of the principals who have this responsibility given the qualifications.
     */
    List<String> getPrincipalIdsWithResponsibility( String responsibilityId, Map<String,String> qualification, Map<String,String> responsibilityDetails );

    /**
     * Get a list of the principals who have this responsibility given the qualifications.
     */
    List<String> getPrincipalIdsWithResponsibilityByName( String responsibilityName, Map<String,String> qualification, Map<String,String> responsibilityDetails );
    
    /**
     * Obtain a list of the principal/responsibility relationships given the qualifier and responsibility details.
     */
    List<? extends PrincipalResponsibility> getResponsibilityInfo( String responsibilityId, Map<String,String> qualification, Map<String,String> responsibilityDetails );
    /**
     * Obtain a list of the principal/responsibility relationships given the qualifier and responsibility details.
     */
    List<? extends PrincipalResponsibility> getResponsibilityInfoByName( String responsibilityName, Map<String,String> qualification, Map<String,String> responsibilityDetails );
    
}
