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
package org.kuali.rice.kim.framework.role;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.framework.type.KimTypeService;

import java.util.List;
import java.util.Map;

/**
 * A {@link KimTypeService} with specific methods for Roles.
 */
public interface RoleTypeService extends KimTypeService {

    /**
     * Gets whether a role assignment with the given qualifier is applicable for the given qualification.
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
     *
     * @param qualification the qualification.  cannot be null.
     * @param roleQualifier the role qualifier. cannot be null.
     * @return true if the qualifications match
     * @throws IllegalArgumentException if the qualification or roleQualifier is null
     */
    boolean doesRoleQualifierMatchQualification( Map<String, String> qualification, Map<String, String> roleQualifier ) throws RiceIllegalArgumentException;

    /**
     * Gets whether a role membership with the given details is applicable for the given qualification.
     *
     * @param qualification the qualification.  cannot be null.
     * @param roleMemberships the list of roleMemberships to check for matches. cannot be null.
     * @return an immutable list of matched roleMemberships.  will not return null.
     * @throws IllegalArgumentException if the qualification or roleMemberships is null.
     */
    List<RoleMembership> getMatchingRoleMemberships(Map<String, String> qualification, List<RoleMembership> roleMemberships) throws RiceIllegalArgumentException;

    /**
     * Returns true if this role type represents an "application" role type.  That is, the members of the 
     * role are known to the host application, not to KIM.  This is needed for cases like the KFS
     * Fiscal Officer, where the members of the role are in the Account table in the KFS database.
     *
     * @return true if application type
     */
    boolean isApplicationRoleType();

    /**
     * This method can be used to check if the given principal has this application role.  It is designed to be used in case
     * there is a more efficient way to check for whether a principal is in a role rather than retrieving all the
     * members of the role and checking against that.
     * 
     * The groupIds parameter is intended to be the complete list of groups to which the principal belongs.  If either the
     * principalId or the groupIds parameters are blank/empty, that parameter should be ignored.
     *
     * @param principalId the principalId. cannot be null or blank.
     * @param groupIds the groupIds the principal is a member of. cannot be null.
     * @param namespaceCode the namespace code the role is in. cannot be blank or null.
     * @param roleName the name of the role.  cannot be blank or null.
     * @param qualification the qualification.  cannot be null.
     * @return if the principal has an application role.
     * @throws IllegalArgumentException if the principalId, namespaceCode, roleName is blank or null.
     * @throws IllegalArgumentException if the groupIds, qualification is null.
     */
    boolean hasApplicationRole( String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification ) throws RiceIllegalArgumentException;
    
    /**
     * Takes the passed in qualifications and converts them, if necessary, for any downstream roles which may be present.
     *
     * @param namespaceCode the namespace code the role is in. cannot be blank or null.
     * @param roleName the name of the role.  cannot be blank or null.
     * @param memberRoleNamespaceCode the namespace code the member role is in. cannot be blank or null.
     * @param memberRoleName the name of the member role.  cannot be blank or null.
     * @param qualification the qualification.  cannot be null.
     * @return an immutable map of qualifiers. Will never return null.
     * @throws IllegalArgumentException if the namespaceCode, roleName, memberRoleNamespaceCode, memberRoleName, is blank or null.
     * @throws IllegalArgumentException if the qualification is null.
     */
    Map<String, String> convertQualificationForMemberRoles( String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String, String> qualification ) throws RiceIllegalArgumentException;
    
    /**
     * Determines if the role specified by the given namespace and role name has a dynamic role membership.
     *
     * A dynamic role membership means that a role membership may be changed over time and cannot be safely cached.
     * 
     * @param namespaceCode the namespace code of the role. cannot be null or blank
     * @param roleName the name of the role. cannot be null or blank.
     * @return true if the membership results of the Role are dynamic, false otherwise
     * @throws IllegalArgumentException if the namespaceCode, roleName is blank or null.
     */
    boolean dynamicRoleMembership(String namespaceCode, String roleName) throws RiceIllegalArgumentException;
    
    /**
     * Roles whose memberships may be matched exactly by qualifiers,
     * this method returns the list of such qualifier names.
     * 
     * @return immutable list of qualifier names that can be used for exact match.  Will never return null.
     */
    List<String> getQualifiersForExactMatch();
    
}
