/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service;

import java.util.List;

import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface PermissionService {

    // --------------------
    // Permission Data
    // --------------------

    /**
     *  Return the list of details that the principal has been assigned for the given permission.
     */
    List<AttributeSet> getPermissionDetails( String principalId, String permissionId, AttributeSet qualification );
            
    // --------------------
    // Authorization Checks
    // --------------------

    /**
     * Checks whether the principal has been granted a permission matching the given details
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
    boolean hasPermission( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails );

    /**
     * Checks whether the given qualified permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermission(String, String, AttributeSet)}.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     * Each permission is checked against the permissionDetails.  The KimPermissionTypeService
     * is called for each permission with the given permissionName to see if the 
     * permissionDetails matches its details.
     */
    boolean isAuthorized( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification  );

    /**
     * Checks whether the principal has been granted a permission matching the given details
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
    boolean hasPermissionByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails );

    /**
     * Checks whether the given qualified permission is granted to the principal given
     * the passed roleQualification.  If no roleQualification is passed (null or empty)
     * then this method behaves the same as {@link #hasPermission(String, String, AttributeSet)}.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     * Each permission is checked against the permissionDetails.  The KimPermissionTypeService
     * is called for each permission with the given permissionName to see if the 
     * permissionDetails matches its details.
     */
    boolean isAuthorizedByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification  );
    
    
    /**
     * Get the list of principals/groups who have a given permission.  This also returns delegates
     * for the given principals/groups who also have this permission given the context in the
     * qualification parameter.
     * 
     * Each role assigned to the principal is checked for qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked first through
     * the role's type service.  Once it is determined that the principal has the role
     * in the given context (qualification), the permissions are examined.
     * 
     */
    List<PermissionAssigneeInfo> getPermissionAssignees( String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification );
    
    /**
     * Returns true if the given permission is defined on any Roles.
     */
    boolean isPermissionDefined( String namespaceCode, String permissionName, AttributeSet permissionDetails );
    
    /**
     * Returns true if the given permission template is defined on any Roles.
     */
    boolean isPermissionDefinedForTemplateName( String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails );
    
    /**
     * Returns permissions (with their details) that are granted to the principal given
     * the passed qualification.  If no qualification is passed (null or empty)
     * then this method does not check any qualifications on the roles.
     * 
     * All permissions with the given name are checked against the permissionDetails.  
     * The KimPermissionTypeService is called for each permission to see if the 
     * permissionDetails matches its details.
     * 
     * An asterisk (*) as a value in any permissionDetails key-value pair will match any value.
     * This forms a way to provide a wildcard to obtain multiple permissions in one call.
     * 
     * After the permissions are determined, the roles that hold those permissions are determined.
     * Each role that matches between the principal and the permission objects is checked for 
     * qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked through
     * the role's type service. 
     * 
     */
    List<KimPermissionInfo> getAuthorizedPermissions( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification );

    /**
     * Returns permissions (with their details) that are granted to the principal given
     * the passed qualification.  If no qualification is passed (null or empty)
     * then this method does not check any qualifications on the roles.
     * 
     * All permissions with the given name are checked against the permissionDetails.  
     * The KimPermissionTypeService is called for each permission to see if the 
     * permissionDetails matches its details.
     * 
     * An asterisk (*) as a value in any permissionDetails key-value pair will match any value.
     * This forms a way to provide a wildcard to obtain multiple permissions in one call.
     * 
     * After the permissions are determined, the roles that hold those permissions are determined.
     * Each role that matches between the principal and the permission objects is checked for 
     * qualifications.  If a qualifier 
     * exists on the principal's membership in that role, that is checked through
     * the role's type service. 
     * 
     */
    List<KimPermissionInfo> getAuthorizedPermissionsByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification );

    // --------------------
    // Permission Data
    // --------------------

    /**
     * Get the permission object with the given ID.
     */
    KimPermissionInfo getPermission(String permissionId);
   
	/** 
	 * Return the permission object for the given unique combination of namespace,
	 * component and permission template name.
	 */
    List<KimPermissionInfo> getPermissionsByTemplateName( String namespaceCode, String permissionTemplateName );

	/** 
	 * Return the permission object for the given unique combination of namespace,
	 * component and permission name.
	 */
    List<KimPermissionInfo> getPermissionsByName( String namespaceCode, String permissionName );
    
    /**
     * Search for permissions using arbitrary search criteria.  JavaBeans property syntax 
     * should be used to reference the properties.
     * 
     * If the searchCriteria parameter is null or empty, an empty list will be returned.
     */
    List<KimPermissionInfo> lookupPermissions(AttributeSet searchCriteria);
   
    void savePermission(KimPermissionInfo permission);
   
    void assignQualifiedPermissionToRole(String roleId, String permissionId, AttributeSet qualifier);    
 
}
