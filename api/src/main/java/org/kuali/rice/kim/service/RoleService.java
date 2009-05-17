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

import org.kuali.rice.kim.bo.Role;
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
	
	List<KimRoleInfo> getRoles( List<String> roleIds );

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
	 * Checks whether the role with the given role ID is active.
	 * 
	 * @param roleId
	 * @return
	 */
    boolean isRoleActive( String roleId );

    /**
     * Returns a list of role qualifiers that the given principal has without taking into consideration
     * that the principal may be a member via an assigned group or role.  Use in situations where
     * you are only interested in the qualifiers that are directly assigned to the principal.
     */
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

    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
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

    /**
     * Returns whether the given principal has any of the passed role IDs with the given qualification.
     */
    boolean principalHasRole( String principalId, List<String> roleIds, AttributeSet qualification );
    
    /**
     * Returns the subset of the given principal ID list which has the given role and qualification.
     * This is designed to be used by lookups of people by their roles.
     */
    List<String> getPrincipalIdSubListWithRole( List<String> principalIds, String roleNamespaceCode, String roleName, AttributeSet qualification );

    /**
	 * 
	 * This method get search results for role lookup
	 */
	List<? extends Role> getRolesSearchResults(java.util.Map<String,String> fieldValues);
	
	/**
	 * Notifies all of a principal's roles and role types that the principal has been inactivated.
	 */
	void principalInactivated( String principalId );
	
	void roleInactivated(String roleId);
	
	List<RoleMembershipInfo> getFirstLevelRoleMembers(List<String> roleIds);
}
