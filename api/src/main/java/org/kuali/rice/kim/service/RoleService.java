/*
 * Copyright 2007 The Kuali Foundation
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

import java.util.Collection;
import java.util.List;

import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public interface RoleService {
    // --------------------
    // Role Data
    // --------------------

	/**
	 * Get the KIM Role object with the given ID.
	 * 
	 * If the roleId is blank, this method returns <code>null</code>.
	 */
	KimRoleInfo getRole( String roleId );

	/** Get the KIM Role object with the unique combination of namespace, component,
	 * and role name.
	 * 
	 * If any parameter is blank, this method returns <code>null</code>.
	 */
    KimRoleInfo getRoleByName( String namespaceCode, String roleName );
	
	/** 
	 * Return the Role ID for the given unique combination of namespace,
	 * component and role name.
	 */
	String getRoleIdByName( String namespaceCode, String roleName );
	

	/**
	 * Get the complete list of all roles which imply the given role.  This includes the role passed in.
	 * 
	 * I.e., where the given role is contained within a higher level role.
	 */
    List<String> getImplyingRoleIds( String roleId );    
    
	/**
	 * Get all the roles implied by the given role.  This method will recurse down
	 * through all role relationships to return a complete list of all roles that
	 * a principal would have if they have the given role.
	 * 
	 * An empty list will be returned if the given role is invalid.
	 * If the role has no contained roles, then this method will return a 
	 * one-element list containing the given role.
	 */
    List<String> getImpliedRoleIds( String roleId );
    
    boolean isRoleActive( String roleId );

    List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, List<String> roleIds, AttributeSet qualification );
    List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, String namespaceCode, String roleName, AttributeSet qualification );
    // --------------------
    // Role Membership Checks
    // --------------------

//	/**
//	 * Return a list of all the principal IDs who have the passed role.  This will include
//	 * all principals assigned to roles which are contained within the given role.  It will
//	 * also have those principals who belong to groups (and their sub-groups)
//	 * which are assigned to this role. 
//	 */
//    List<String> getPrincipalIdsWithRole(String roleId);       
//    
//	/**
//	 * Return a list of all the principal IDs who have the passed role where the 
//	 * qualification matches the qualifier on their assignment to this role.  
//	 * 
//	 * The qualification is only matched against the role listed.  So, only principals
//	 * assigned to this role or in groups assigned to this role which match the
//	 * qualification will be returned.  
//	 */
//    List<String> getPrincipalIdsWithQualifiedRole(String roleId, AttributeSet qualification);
//    
//    // TODO: leave commented out unless we have a use case for checking groups in this way
////    List<String> getGroupIdsWithRole(String roleId);
////    List<String> getGroupIdsWithQualifiedRole(String roleId, AttributeSet qualification);
//
//    /**
//     * Check whether the principal has the given unqualified role.
//     */
//    boolean principalHasRole(String principalId, String roleId);
//
//    /** 
//     *  Check whether the principal has the given role within the context identified
//     *  by the passed qualification.
//     */
//    boolean principalHasQualifiedRole(String principalId, String roleId, AttributeSet qualification);
//    
//    
//    /**
//     * Return the list of all roles that are assigned to a given principal.
//     * 
//     * This list will include the roles which are directly assigned to the
//     * principal or to a group to which they belong and all contained roles
//     * under those roles.
//     */
//    List<String> getRoleIdsForPrincipal(String principalId);
//    List<String> getRoleIdsInNamespaceForPrincipal(String principalId, String namespaceCode);
//    List<String> getRoleIdsMatchingQualification( String principalId, AttributeSet qualification );
    
    /**
     * Get all the role members (groups and principals) associated with the given list of roles
     * where their role membership/assignment matches the given qualification.
     *
     * The return object will have each membership relationship along with the delegations
     * 
     */
    Collection<RoleMembershipInfo> getRoleMembers( List<String> roleIds, AttributeSet qualification );

    boolean principalHasRole( String principalId, List<String> roleIds, AttributeSet qualification );
    
    // --------------------
    // Persistence Methods
    // --------------------
        
    void saveRole(KimRoleInfo role);   

    void assignQualifiedRoleToPrincipal(String principalId, String roleId, AttributeSet qualifier);
    void assignQualifiedRoleToGroup(String groupId, String roleId, AttributeSet qualifier);
    
}
