package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.role.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.PrincipalResponsibilityInfo;

public interface AuthorizationService {


    // --------------------
    // Permission Data
    // --------------------

	/** 
	 * Return the permission ID for the given unique combination of namespace and permission name.
	 */
	String getPermissionIdByName(String namespaceCode, String permissionName );
	


    /**
     *  Return the list of details that the principal has been assigned for the given permission.
     */
    List<Map<String,String>> getPermissionDetails( String principalId, String permissionId, Map<String,String> roleQualification );
            
    // --------------------
    // Authorization Checks
    // --------------------
	
	/** 
	 * Check whether the principal is granted this permission without taking 
	 * qualifiers into account.
	 * 
	 * This method should not be used for true authorization checks since a principal
	 * may only have this permission within a given context.  It could be used to
	 * identify that the user would have some permissions within a certain area.
	 * Later checks would identify exactly what permissions were granted.
	 * 
	 * It can also be used when the client application KNOWS that this is a role which
	 * is never qualified.
	 */
    boolean hasPermission(String principalId, String permissionId);

    /**
     * Checks whether the principal has been granted a permission matching the given qualification
     * without taking role qualifiers into account.
     * 
	 * This method should not be used for true authorization checks since a principal
	 * may only have this permission within a given context.  It could be used to
	 * identify that the user would have some permissions within a certain area.
	 * Later checks would identify exactly what permissions were granted.
	 * 
	 * It can also be used when the client application KNOWS that this is a role which
	 * is never qualified.
     */
    boolean hasPermissionWithDetails( String principalId, String permissionId, Map<String,String> permissionDetails );
    
    /**
     * Checks whether the given permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermission(String, String)}.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     */
    boolean hasQualifiedPermission(String principalId, String permissionId, Map<String,String> qualification);

    /**
     * Checks whether the given qualified permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermissionWithDetails(String, String, Map)}.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     * Each permission is checked against the permissionQualification.  The KimPermissionTypeService
     * is called for each permission with the given permissionId to see if the 
     * permissionQualification matches its qualifier.
     */
    boolean hasQualifiedPermissionWithDetails( String principalId, String permissionId, Map<String,String> qualification, Map<String,String> permissionDetails );
    
    /**
     * Same as {@link #hasQualifiedPermission(String, String, Map)} except that it looks up the
     * permission by its namespace/name first.
     */
    boolean hasQualifiedPermissionByName(String principalId, String namespaceCode, String permissionName, Map<String,String> qualification);
    
    // --------------------
    // Responsibility Methods
    // --------------------

    /**
     * Get the responsibility object with the given ID.
     */
    KimResponsibilityInfo getResponsibility(String responsibilityId);
    
 	/** 
 	 * Return the responsibility object for the given unique combination of namespace,
 	 * component and responsibility name.
 	 */
    KimResponsibilityInfo getResponsibilityByName(String namespaceCode, String responsibilityName );
    
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
    List<String> getPrincipalIdsWithResponsibilityByName( String namespaceCode, String responsibilityName, Map<String,String> qualification, Map<String,String> responsibilityDetails );
    
    /**
     * Obtain a list of the principal/responsibility relationships given the qualifier and responsibility details.
     */
    List<PrincipalResponsibilityInfo> getResponsibilityInfo( String responsibilityId, Map<String,String> qualification, Map<String,String> responsibilityDetails );
    /**
     * Obtain a list of the principal/responsibility relationships given the qualifier and responsibility details.
     */
    List<PrincipalResponsibilityInfo> getResponsibilityInfoByName( String namespaceCode, String responsibilityName, Map<String,String> qualification, Map<String,String> responsibilityDetails );
    
   
    // --------------------
    // Permission Data
    // --------------------

    /**
     * Get the permission object with the given ID.
     */
    KimPermissionInfo getPermission(String permissionId);
   
	/** 
	 * Return the permission object for the given unique combination of namespace,
	 * component and permission name.
	 */
    KimPermissionInfo getPermissionByName(String namespaceCode, String permissionName );

    /**
     * Search for permissions using arbitrary search criteria.  JavaBeans property syntax 
     * should be used to reference the properties.
     * 
     * If the searchCriteria parameter is null or empty, an empty list will be returned.
     */
    List<KimPermissionInfo> lookupPermissions(Map<String,String> searchCriteria);
   
    void savePermission(KimPermissionInfo permission);
   
    void assignQualifiedPermissionToRole(String roleId, String permissionId, Map<String,String> qualifier);    
    
}