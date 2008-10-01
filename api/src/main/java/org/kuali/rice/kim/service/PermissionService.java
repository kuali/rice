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
import java.util.Map;

import org.kuali.rice.kim.bo.role.KimPermissionInfo;

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
	 * Return the permission ID for the given unique combination of namespace and permission name.
	 */
	String getPermissionIdByName( String permissionName );
	


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
    boolean hasQualifiedPermissionByName(String principalId, String permissionName, Map<String,String> qualification);
    

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
    KimPermissionInfo getPermissionByName( String permissionName );

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
