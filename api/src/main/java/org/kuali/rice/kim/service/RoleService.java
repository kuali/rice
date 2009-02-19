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

import org.kuali.rice.kim.bo.role.KimRole;
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
    
    boolean isRoleActive( String roleId );

    List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, List<String> roleIds, AttributeSet qualification );

    /**
     * Returns a list of role qualifiers that the given principal has without taking into consideration
     * that the principal may be a member via an assigned group or role.  Use in situations where
     * you are only interested in the qualifiers that are directly assigned to the principal.
     */
    List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, String namespaceCode, String roleName, AttributeSet qualification );
    
    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
	List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( String principalId, String namespaceCode, String roleName, AttributeSet qualification );
	List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( String principalId, List<String> roleIds, AttributeSet qualification );

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
    List<RoleMembershipInfo> getRoleMembers( List<String> roleIds, AttributeSet qualification );

    /**
	 * This method gets all the members, then traverses down into members of type role and group to obtain the nested principal ids
	 * 
	 * @return list of member principal ids
	 */
    Collection<String> getRoleMemberPrincipalIds(String namespaceCode, String roleName, AttributeSet qualification);

    boolean principalHasRole( String principalId, List<String> roleIds, AttributeSet qualification );
    
    List<String> getPrincipalIdSubListWithRole( List<String> principalIds, String roleNamespaceCode, String roleName, AttributeSet qualification );
    
    // --------------------
    // Persistence Methods
    // --------------------
    
    void assignPrincipalToRole(String principalId, String namespaceCode, String roleName, AttributeSet qualifications);
    void assignGroupToRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifications);
    void removePrincipalFromRole(String principalId, String namespaceCode, String roleName, AttributeSet qualifications);
    void removeGroupFromRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifications);
	/**
	 * 
	 * This method get search results for role lookup
	 */
	List<? extends KimRole> getRolesSearchResults(java.util.Map<String,String> fieldValues);
}
