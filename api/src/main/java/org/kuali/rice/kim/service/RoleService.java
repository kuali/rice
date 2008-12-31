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
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
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
    
    public void assignPrincipalToRole(String principalId, String namespaceCode, String roleName, AttributeSet qualifications);
    public void assignGroupToRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifications);
    public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName, AttributeSet qualifications);
    public void removeGroupFromRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifications);
	/**
	 * 
	 * This method get search results for role lookup
	 * 
	 * @param fieldValues
	 * @return
	 */
	public List<KimRoleImpl> getRolesSearchResults(java.util.Map<String,String> fieldValues);

}
