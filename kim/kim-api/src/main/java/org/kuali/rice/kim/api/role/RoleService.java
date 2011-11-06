/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kim.api.role;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * This service provides operations for querying role and role qualification
 * data.
 *
 * <p>A role is where permissions and responsibilities are granted.  Roles have
 * a membership consisting of principals, groups or even other roles.  By
 * being assigned as members of a role, the associated principals will be
 * granted all permissions and responsibilities that have been granted to the
 * role.
 *
 * <p>Each membership assignment on the role can have a qualification which
 * defines extra information about that particular member of the role.  For
 * example, one may have the role of "Dean" but that can be further qualified
 * by the school they are the dean of, such as "Dean of Computer Science".
 * Authorization checks that are then done in the permission service can pass
 * qualifiers as part of the operation if they want to restrict the subset of
 * the role against which the check is made.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = "roleService", targetNamespace = KimApiConstants.Namespaces.KIM_NAMESPACE_2_0 )
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RoleService {
    /**
     * This will create a {@link org.kuali.rice.kim.api.role.Role} exactly like the role passed in.
     *
     * @param role the role to create
     * @return the newly created object.  will never be null.
     * @throws IllegalArgumentException if the responsibility is null
     * @throws IllegalStateException if the responsibility is already existing in the system
     */
    @WebMethod(operationName="createRole")
    @WebResult(name = "role")
    @CacheEvict(value={Role.Cache.NAME}, allEntries = true)
    Role createRole(@WebParam(name = "role") Role role)
            throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * This will update a {@link Role}.
     *
     * @param role the role to update
     * @throws IllegalArgumentException if the role is null
     * @throws IllegalStateException if the role does not exist in the system
     */
    @WebMethod(operationName="updateRole")
    @WebResult(name = "role")
    @CacheEvict(value={Role.Cache.NAME}, allEntries = true)
    Role updateRole(@WebParam(name = "role") Role role)
            throws RiceIllegalArgumentException, RiceIllegalStateException;

	/**
	 * Get the KIM Role object with the given ID.
	 *
	 */
    @WebMethod(operationName = "getRole")
    @WebResult(name = "role")
    @Cacheable(value= Role.Cache.NAME, key="'id=' + #p0")
    Role getRole(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

	/**
	 * Get the KIM Role objects for the role IDs in the given List.
	 */
    @WebMethod(operationName = "getRoles")
    @XmlElementWrapper(name = "roles", required = true)
    @XmlElement(name = "role", required = false)
    @WebResult(name = "roles")
    @Cacheable(value= Role.Cache.NAME, key="'ids=' + T(org.kuali.rice.core.api.cache.CacheKeyUtils).key(#p0)")
	List<Role> getRoles( @WebParam(name="ids") List<String> ids ) throws RiceIllegalArgumentException;

	/** Get the KIM Role object with the unique combination of namespace, component,
	 * and role name.
	 *
	 */
    @WebMethod(operationName = "getRoleByNameAndNamespaceCode")
    @WebResult(name = "role")
    @Cacheable(value=Role.Cache.NAME, key="'namespaceCode=' + #p0 + '|' + 'name=' + #p1")
    Role getRoleByNameAndNamespaceCode(@WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name) throws RiceIllegalArgumentException;

	/**
	 * Return the Role ID for the given unique combination of namespace,
	 * component and role name.
	 */
    @WebMethod(operationName = "getRoleIdByNameAndNamespaceCode")
    @WebResult(name = "roleId")
    @Cacheable(value=Role.Cache.NAME, key="'{getRoleIdByNameAndNamespaceCode}' + 'namespaceCode=' + #p0 + '|' + 'name=' + #p1")
	String getRoleIdByNameAndNamespaceCode(@WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "name") String name) throws RiceIllegalArgumentException;

	/**
	 * Checks whether the role with the given role ID is active.
	 *
	 * @param id
	 * @return
	 */
    @WebMethod(operationName = "isRoleActive")
    @WebResult(name = "isRoleActive")
    @Cacheable(value=Role.Cache.NAME, key="'{isRoleActive}' + 'id=' + #p0")
    boolean isRoleActive( @WebParam(name="id") String id ) throws RiceIllegalArgumentException;

    /**
     * Returns a list of role qualifiers that the given principal has without taking into consideration
     * that the principal may be a member via an assigned group or role.  Use in situations where
     * you are only interested in the qualifiers that are directly assigned to the principal.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalByRoleIds")
    @XmlElementWrapper(name = "attributes", required = true)
    @XmlElement(name = "attribute", required = false)
    @WebResult(name = "attributes")
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    List<Map<String, String>> getRoleQualifersForPrincipalByRoleIds(@WebParam(name = "principalId") String principalId,
            @WebParam(name = "roleIds") List<String> roleIds, @WebParam(name = "qualification") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> qualification)
            throws RiceIllegalArgumentException;

    /**
     * Returns a list of role qualifiers that the given principal has without taking into consideration
     * that the principal may be a member via an assigned group or role.  Use in situations where
     * you are only interested in the qualifiers that are directly assigned to the principal.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalByNamespaceAndRolename")
    @XmlElementWrapper(name = "attributes", required = true)
    @XmlElement(name = "attribute", required = false)
    @WebResult(name = "attributes")
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    List<Map<String, String>> getRoleQualifersForPrincipalByNamespaceAndRolename(
            @WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "roleName") String roleName, @WebParam(name = "qualification") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> qualification)
            throws RiceIllegalArgumentException;

    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
    @WebMethod(operationName = "getNestedRoleQualifersForPrincipalByNamespaceAndRolename")
    @XmlElementWrapper(name = "attributes", required = true)
    @XmlElement(name = "attribute", required = false)
    @WebResult(name = "attributes")
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	List<Map<String, String>> getNestedRoleQualifersForPrincipalByNamespaceAndRolename(
            @WebParam(name = "principalId") String principalId, @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "roleName") String roleName, @WebParam(name = "qualification") @XmlJavaTypeAdapter(
            value = MapStringStringAdapter.class) Map<String, String> qualification)
            throws RiceIllegalArgumentException;

    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
    @WebMethod(operationName = "getNestedRoleQualifiersForPrincipalByRoleIds")
    @XmlElementWrapper(name = "attributes", required = true)
    @XmlElement(name = "attribute", required = false)
    @WebResult(name = "attributes")
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	List<Map<String, String>> getNestedRoleQualifiersForPrincipalByRoleIds(
            @WebParam(name = "principalId") String principalId, @WebParam(name = "roleIds") List<String> roleIds,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(
                    value = MapStringStringAdapter.class) Map<String, String> qualification)
            throws RiceIllegalArgumentException;


    // --------------------
    // Role Membership Checks
    // --------------------

    /**
     * Get all the role members (groups and principals) associated with the given list of roles
     * where their role membership/assignment matches the given qualification.  The list of RoleMemberships returned
     * will only contain group and principal members.  Any nested role members will be resolved and flattened into
     * the principals and groups that are members of that nested role (assuming qualifications match).
     *
     * The return object will have each membership relationship along with the delegations
     *
     */
    @WebMethod(operationName = "getRoleMembers")
    @XmlElementWrapper(name = "roleMemberships", required = true)
    @XmlElement(name = "roleMembership", required = false)
    @WebResult(name = "roleMemberships")
    List<RoleMembership> getRoleMembers( @WebParam(name="roleIds") List<String> roleIds,
            @WebParam(name="qualification")@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualification )
            throws RiceIllegalArgumentException;

    /**
	 * This method gets all the members, then traverses down into members of type role and group to obtain the nested principal ids
	 *
	 * @return list of member principal ids
	 */
    @WebMethod(operationName = "getRoleMemberPrincipalIds")
    @XmlElementWrapper(name = "principalIds", required = true)
    @XmlElement(name = "principalId", required = false)
    @WebResult(name = "principalIds")
    Collection<String> getRoleMemberPrincipalIds(@WebParam(name="namespaceCode") String namespaceCode,
            @WebParam(name="roleName") String roleName,
            @WebParam(name="qualification") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualification)
            throws RiceIllegalArgumentException;

    /**
     * Returns whether the given principal has any of the passed role IDs with the given qualification.
     */
    @WebMethod(operationName = "principalHasRole")
    @WebResult(name = "principalHasRole")
    boolean principalHasRole( @WebParam(name="principalId") String principalId,
            @WebParam(name="roleIds") List<String> roleIds,
            @WebParam(name="qualification") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualification )
            throws RiceIllegalArgumentException;

    /**
     * Returns the subset of the given principal ID list which has the given role and qualification.
     * This is designed to be used by lookups of people by their roles.
     */
    @WebMethod(operationName = "getPrincipalIdSubListWithRole")
    @XmlElementWrapper(name = "principalIds", required = true)
    @XmlElement(name = "principalId", required = false)
    @WebResult(name = "principalIds")
    List<String> getPrincipalIdSubListWithRole( @WebParam(name="principalIds") List<String> principalIds,
            @WebParam(name="roleNamespaceCode") String roleNamespaceCode,
            @WebParam(name="roleName") String roleName,
            @WebParam(name="qualification") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualification )
            throws RiceIllegalArgumentException;

    /**
	 *
	 * This method gets search results for role lookup
	 */
    @WebMethod(operationName = "getRolesSearchResults")
    @WebResult(name = "results")
	RoleQueryResults findRoles(@WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException;



    /**
     * Gets all direct members of the roles that have ids within the given list
     * of role ids.  This method does not recurse into any nested roles.
     *
     *  <p>The resulting List of role membership will contain membership for
     *  all the roles with the specified ids.  The list is not guaranteed to be
     *  in any particular order and may have membership info for the
     *  different roles interleaved with each other.
     */
    @WebMethod(operationName = "getFirstLevelRoleMembers")
    @XmlElementWrapper(name = "roleMemberships", required = true)
    @XmlElement(name = "roleMembership", required = false)
    @WebResult(name = "roleMemberships")
    @Cacheable(value=RoleMembership.Cache.NAME, key="'roleIds=' + T(org.kuali.rice.core.api.cache.CacheKeyUtils).key(#p0)")
	List<RoleMembership> getFirstLevelRoleMembers(@WebParam(name="roleIds") List<String> roleIds) throws RiceIllegalArgumentException;

	/**
	 * Gets role member information based on the given search criteria.  The
	 * map of criteria contains attributes of RoleMembership as it's
	 * key and the values to search on as the value.
	 */
    @WebMethod(operationName = "findRoleMemberships")
    @WebResult(name = "results")
	RoleMembershipQueryResults findRoleMemberships(@WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException;

	/**
	 * Gets a list of Roles that the given member belongs to.
	 */
    @WebMethod(operationName = "getMemberParentRoleIds")
    @XmlElementWrapper(name = "roleIds", required = true)
    @XmlElement(name = "roleId", required = false)
    @WebResult(name = "roleIds")
    @Cacheable(value=RoleMembership.Cache.NAME, key="'memberType=' + #p0 + '|' + 'memberId=' + #p1")
	List<String> getMemberParentRoleIds(String memberType, String memberId) throws RiceIllegalArgumentException;


    @WebMethod(operationName = "findRoleMembers")
    @WebResult(name = "results")
	RoleMemberQueryResults findRoleMembers(@WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getRoleTypeRoleMemberIds")
    @XmlElementWrapper(name = "memberIds", required = true)
    @XmlElement(name = "memberId", required = false)
    @WebResult(name = "memberIds")
    @Cacheable(value=RoleMember.Cache.NAME, key="'{getRoleTypeRoleMemberIds} + 'roleId=' + #p0")
    Set<String> getRoleTypeRoleMemberIds(@WebParam(name = "roleId") String roleId) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "findDelegateMembers")
    @WebResult(name = "results")
    DelegateMemberQueryResults findDelegateMembers(@WebParam(name = "query") QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException;

	/**
	 * Gets delegation member information based on the given search criteria.  The
	 * map of criteria contains attributes of Delegate as it's
	 * key and the values to search on as the value.
	 */
    @WebMethod(operationName = "getDelegationMembersByDelegationId")
    @XmlElementWrapper(name = "delegateMembers", required = true)
    @XmlElement(name = "delegateMember", required = false)
    @WebResult(name = "delegateMembers")
    @Cacheable(value=DelegateMember.Cache.NAME, key="'delegateId=' + #p0")
    List<DelegateMember> getDelegationMembersByDelegationId(
            @WebParam(name = "delegateId") String delegateId) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getDelegationMemberByDelegationAndMemberId")
    @WebResult(name = "delegateMember")
    @Cacheable(value=DelegateMember.Cache.NAME, key="'delegateId=' + #p0 + '|' + 'memberId=' + #p1")
    DelegateMember getDelegationMemberByDelegationAndMemberId(
            @WebParam(name = "delegationId") String delegationId, @WebParam(name = "memberId") String memberId) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getDelegationMemberById")
    @WebResult(name = "delegateMember")
    @Cacheable(value=DelegateMember.Cache.NAME, key="'id=' + #p0")
    DelegateMember getDelegationMemberById(@WebParam(name = "id") String id) throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getRoleResponsibilities")
    @XmlElementWrapper(name = "roleResponsibilities", required = true)
    @XmlElement(name = "roleResponsibility", required = false)
    @WebResult(name = "roleResponsibilities")
    @Cacheable(value=RoleResponsibility.Cache.NAME, key="'roleId=' + #p0")
	List<RoleResponsibility> getRoleResponsibilities(@WebParam(name="roleId") String roleId)  throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getRoleMemberResponsibilityActions")
    @XmlElementWrapper(name = "roleResponsibilityActions", required = true)
    @XmlElement(name = "roleResponsibilityAction", required = false)
    @WebResult(name = "roleResponsibilityActions")
    @Cacheable(value=RoleResponsibility.Cache.NAME, key="'roleMemberId=' + #p0")
	List<RoleResponsibilityAction> getRoleMemberResponsibilityActions(
            @WebParam(name = "roleMemberId") String roleMemberId)  throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getDelegateTypeByRoleIdAndDelegateTypeCode")
    @WebResult(name = "delegateType")
    @Cacheable(value=DelegateType.Cache.NAME, key="'roleId=' + #p0 + '|' + 'code=' + #p1")
    DelegateType getDelegateTypeByRoleIdAndDelegateTypeCode(@WebParam(name = "roleId") String roleId,
            @WebParam(name = "delegateType") DelegationType delegateType)  throws RiceIllegalArgumentException;

    @WebMethod(operationName = "getDelegateTypeByDelegationId")
    @WebResult(name = "delegateType")
    @Cacheable(value=DelegateType.Cache.NAME, key="'delegationId=' + #p0")
    DelegateType getDelegateTypeByDelegationId(@WebParam(name = "delegationId") String delegationId)  throws RiceIllegalArgumentException;

    /**
	 * Assigns the principal with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    @WebMethod(operationName = "assignPrincipalToRole")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    void assignPrincipalToRole(@WebParam(name="principalId") String principalId,
    		@WebParam(name="namespaceCode") String namespaceCode,
    		@WebParam(name="roleName") String roleName,
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws RiceIllegalArgumentException;

	/**
	 * Assigns the group with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    @WebMethod(operationName = "assignGroupToRole")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    void assignGroupToRole(@WebParam(name="groupId") String groupId,
    		@WebParam(name="namespaceCode") String namespaceCode,
    		@WebParam(name="roleName") String roleName,
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws RiceIllegalArgumentException;

	/**
	 * Assigns the role with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    @WebMethod(operationName = "assignRoleToRole")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    void assignRoleToRole(@WebParam(name="roleId") String roleId,
    		@WebParam(name="namespaceCode") String namespaceCode,
    		@WebParam(name="roleName") String roleName,
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws RiceIllegalArgumentException;

	/**
	 * Creates a new RoleMember.  Needs to be passed a valid RoleMember object that does not currently exist.
	 */
    @WebMethod(operationName = "createRoleMember")
    @WebResult(name = "roleMember")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    RoleMember createRoleMember(@WebParam(name = "roleMember") RoleMember roleMember) throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
	 * Assigns the role with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    @WebMethod(operationName = "updateRoleMember")
    @WebResult(name = "roleMember")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    RoleMember updateRoleMember(@WebParam(name = "roleMember") RoleMember roleMember) throws RiceIllegalArgumentException, RiceIllegalStateException;

    @WebMethod(operationName = "createRoleResponsibilityAction")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    RoleResponsibilityAction createRoleResponsibilityAction(@WebParam(name = "roleResponsibilityAction") RoleResponsibilityAction roleResponsibilityAction) throws RiceIllegalArgumentException;

	/**
	 * Assigns the member with the given id as a delegation member to the role
	 * with the specified namespace code and name with the supplied set of qualifications.
	 */
    @WebMethod(operationName = "createDelegateType")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    DelegateType createDelegateType(@WebParam(name="delegateType") DelegateType delegateType) throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
	 * Updates a delegation type, including attached members
	 */
    @WebMethod(operationName = "updateDelegateType")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    DelegateType updateDelegateType(@WebParam(name="delegateType") DelegateType delegateType) throws RiceIllegalArgumentException, RiceIllegalStateException;

    /**
     * Remove the principal with the given id and qualifications from the role
     * with the specified namespace code and role name.
     */
    @WebMethod(operationName = "removePrincipalFromRole")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    void removePrincipalFromRole(@WebParam(name="principalId") String principalId,
    		@WebParam(name="namespaceCode") String namespaceCode,
    		@WebParam(name="roleName") String roleName,
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws RiceIllegalArgumentException;

    /**
     * Remove the group with the given id and qualifications from the role
     * with the specified namespace code and role name.
     */
    @WebMethod(operationName = "removeGroupFromRole")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    void removeGroupFromRole(@WebParam(name="groupId") String groupId,
    		@WebParam(name="namespaceCode") String namespaceCode,
    		@WebParam(name="roleName") String roleName,
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws RiceIllegalArgumentException;

    /**
     * Remove the group with the given id and qualifications from the role
     * with the specified namespace code and role name.
     */
    @WebMethod(operationName = "removeRoleFromRole")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    void removeRoleFromRole(@WebParam(name="roleId") String roleId,
    		@WebParam(name="namespaceCode") String namespaceCode,
    		@WebParam(name="roleName") String roleName,
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws RiceIllegalArgumentException;

    /**
     * Assigns the given permission to the given role
     */
    @WebMethod(operationName = "assignPermissionToRole")
    @CacheEvict(value={Role.Cache.NAME, RoleMembership.Cache.NAME, RoleMember.Cache.NAME, DelegateMember.Cache.NAME, RoleResponsibility.Cache.NAME, DelegateType.Cache.NAME }, allEntries = true)
    void assignPermissionToRole(@WebParam(name = "permissionId") String permissionId, @WebParam(name = "roleId") String roleId) throws RiceIllegalArgumentException;
}
