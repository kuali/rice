/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.framework.type;


import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.type.KimTypeService;

import java.util.List;
import java.util.Map;

/**
 * This is a service interface that must be used for a service related to a role type.
 * 
 * Is it used to interpret the qualifiers which may be attached.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface KimRoleTypeService extends KimTypeService {

                
    /** Return whether a role assignment with the given qualifier is applicable for the given qualification. 
     * 
     * For example, the qualifier for a role could be as follows:
     *   chartOfAccountsCode = BL
     *   organizationCode = ARSC
     *   descendsHierarchy = true
     *   
     * The qualification could be:
     *   chartOfAccountsCode = BL
     *   organizationCode = PSY    (reports to BL-ARSC)
     *   
     * This method would return true for this set of arguments.  This would require a query of 
     * the KFS organization hierarchy, so an implementation of this sort must be done by
     * a service which lives within KFS and will be called remotely by KIM.
     * 
     * The contents of the passed in attribute sets should not be modified as they may be used in future calls by
     * the role service.
     */
    boolean doesRoleQualifierMatchQualification( Map<String, String> qualification, Map<String, String> roleQualifier );

    /** Same as {@link #doesRoleQualifierMatchQualification(Map<String, String>, Map<String, String>)} except that it takes a list of qualifiers to check.
     */
    List<RoleMembership> doRoleQualifiersMatchQualification( Map<String, String> qualification, List<RoleMembership> roleMemberList );

    /**
     * Returns true if this role type represents an "application" role type.  That is, the members of the 
     * role are known to the host application, not to KIM.  This is needed for cases like the KFS
     * Fiscal Officer, where the members of the role are in the Account table in the KFS database. 
     */
    boolean isApplicationRoleType();
    
    /**
     * Returns a list of principal IDs corresponding to the given application role.  These principal IDs 
     * would be returned from the implementing application.
     * 
     * Continuing the example from {@link #isApplicationRoleType()}, the qualification in that case would be
     * a chart code and account number.  This service would use that information to retrieve the Fiscal Officer
     * from the account table.
     * 
     * The contents of the passed in attribute sets should not be modified as they may be used in future calls by
     * the role service.
     * 
     * @see #isApplicationRoleType()
     */
    List<RoleMembership> getRoleMembersFromApplicationRole( String namespaceCode, String roleName, Map<String, String> qualification );

    /**
     * This method can be used to check if the given principal has this application role.  It is designed to be used in case
     * there is a more efficient way to check for whether a principal is in a role rather than retrieving all the
     * members of the role and checking against that.
     * 
     * The groupIds parameter is intended to be the complete list of groups to which the principal belongs.  If either the
     * principalId or the groupIds parameters are blank/empty, that parameter should be ignored.
     * 
     * @see #isApplicationRoleType()
     * @see #getRoleMembersFromApplicationRole(String, String, Map<String, String>)
     */
    boolean hasApplicationRole( String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification );

    /**
     * For roles where the order of members returned may be meaningful,
     * this method provides a hook to sort the results before they
     * are returned from getRoleMembers on the RoleService.
     *
     * This method may alter the passed in list directly and return it rather than
     * allocating a new list.
     * 
     * This is also the place where the roleSortingCode property on the RoleMembershipInfo objects can be
     * populated in preparation for routing if not all members of this role should be group as separate
     * units for routing.
     */
    List<RoleMembership> sortRoleMembers( List<RoleMembership> roleMembers );
    
    /**
     * Takes the passed in qualifications and converts them, if necessary, for any downstream roles which may be present.
     */
    Map<String, String> convertQualificationForMemberRoles( String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String, String> qualification );
    
    /**
     * Called by the role service when it is notified that a principal has been inactivated.  Can be used 
     * to perform local data cleanup by application roles.
     */
    void principalInactivated( String principalId, String namespaceCode, String roleName );
    
    /**
     * Determines if the role specified by the given namespace and role name should have membership queries cached
     * 
     * @param namespaceCode the namespace code of the role to determine caching on
     * @param roleName the name of the role to determine caching on
     * @return true if the membership results of the Role should be cached, false otherwise
     */
    boolean shouldCacheRoleMembershipResults(String namespaceCode, String roleName);
    
    /** For roles whose memberships may be matched exactly by qualifiers,
     * this method returns the list of such qualifiers 
     * 
     * @return list of qualifier names that can be used for exact match
     */
    List<String> getQualifiersForExactMatch();
    
}
