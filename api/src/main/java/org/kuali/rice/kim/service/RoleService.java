/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.AttributeSetAdapter;
import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateTypeInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityInfo;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

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
 * <p>This service provides read-only operations.  For write operations, see
 * {@link RoleUpdateService}.
 * 
 * @see RoleUpdateService
 * @see PermissionService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = KIMWebServiceConstants.RoleService.WEB_SERVICE_NAME, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RoleService {
    // --------------------
    // Role Data
    // --------------------

	/**
	 * Get the KIM Role object with the given ID.
	 * 
	 * If the roleId is blank, this method returns <code>null</code>.
	 */
	KimRoleInfo getRole( @WebParam(name="roleId") String roleId );
	
	/**
	 * Get the KIM Role objects for the role IDs in the given List.
	 */
	List<KimRoleInfo> getRoles( @WebParam(name="roleIds") List<String> roleIds );

	/** Get the KIM Role object with the unique combination of namespace, component,
	 * and role name.
	 * 
	 * If any parameter is blank, this method returns <code>null</code>.
	 */
    KimRoleInfo getRoleByName( @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName );
	
	/** 
	 * Return the Role ID for the given unique combination of namespace,
	 * component and role name.
	 */
	String getRoleIdByName( @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName );
    
	/**
	 * Checks whether the role with the given role ID is active.
	 * 
	 * @param roleId
	 * @return
	 */
    boolean isRoleActive( @WebParam(name="roleId") String roleId );

    /**
     * Returns a list of role qualifiers that the given principal has without taking into consideration
     * that the principal may be a member via an assigned group or role.  Use in situations where
     * you are only interested in the qualifiers that are directly assigned to the principal.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalRoleIds")
    List<AttributeSet> getRoleQualifiersForPrincipal( @WebParam(name="principalId") String principalId, @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

    /**
     * Returns a list of role qualifiers that the given principal has without taking into consideration
     * that the principal may be a member via an assigned group or role.  Use in situations where
     * you are only interested in the qualifiers that are directly assigned to the principal.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalNamespaceRolename")
    List<AttributeSet> getRoleQualifiersForPrincipal( @WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );
    
    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalIncludingNestedNamespaceRolename")
	List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( @WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalIncludingNestedRoleIds")
	List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( @WebParam(name="principalId") String principalId, @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

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
    List<RoleMembershipInfo> getRoleMembers( @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

    /**
	 * This method gets all the members, then traverses down into members of type role and group to obtain the nested principal ids
	 * 
	 * @return list of member principal ids
	 */
    Collection<String> getRoleMemberPrincipalIds(@WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification);

    /**
     * Returns whether the given principal has any of the passed role IDs with the given qualification.
     */
    boolean principalHasRole( @WebParam(name="principalId") String principalId, @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );
    
    /**
     * Returns the subset of the given principal ID list which has the given role and qualification.
     * This is designed to be used by lookups of people by their roles.
     */
    List<String> getPrincipalIdSubListWithRole( @WebParam(name="principalIds") List<String> principalIds, @WebParam(name="roleNamespaceCode") String roleNamespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification );

    /**
	 * 
	 * This method get search results for role lookup
	 */
	List<? extends Role> getRolesSearchResults(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name = "fieldValues") java.util.Map<String,String> fieldValues);
	
	/**
	 * Notifies all of a principal's roles and role types that the principal has been inactivated.
	 */
	void principalInactivated( @WebParam(name="principalId") String principalId );
	
	/**
	 * Notifies the role service that the role with the given id has been inactivated. 
	 */
	void roleInactivated(@WebParam(name="roleId") String roleId);

	/**
	 * Notifies the role service that the group with the given id has been inactivated. 
	 */
    void groupInactivated(@WebParam(name="groupId") String groupId);
    
    /**
     * Gets all direct members of the roles that have ids within the given list
     * of role ids.  This method does not recurse into any nested roles.
     * 
     *  <p>The resulting List of role membership will contain membership for
     *  all the roles with the specified ids.  The list is not guaranteed to be
     *  in any particular order and may have membership info for the
     *  different roles interleaved with each other.
     */
	List<RoleMembershipInfo> getFirstLevelRoleMembers(@WebParam(name="roleIds") List<String> roleIds);
	
	/**
	 * Gets role member information based on the given search criteria.  The
	 * map of criteria contains attributes of RoleMembershipInfo as it's
	 * key and the values to search on as the value.
	 */
	List<RoleMembershipInfo> findRoleMembers(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name="fieldValues") java.util.Map<String, String> fieldValues);

	/**
	 * 
	 * Gets a list of Roles that the given member belongs to.  
	 * 
	 */
	List<String> getMemberParentRoleIds(String memberType, String memberId);
	
	List<RoleMemberCompleteInfo> findRoleMembersCompleteInfo(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name="fieldValues") java.util.Map<String, String> fieldValues);

	List<DelegateMemberCompleteInfo> findDelegateMembersCompleteInfo(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class) @WebParam(name="fieldValues") java.util.Map<String, String> fieldValues);
	
	/**
	 * Gets delegation member information based on the given search criteria.  The
	 * map of criteria contains attributes of DelegateInfo as it's
	 * key and the values to search on as the value.
	 */
	List<DelegateMemberCompleteInfo> getDelegationMembersByDelegationId(@WebParam(name="delegationId") String delegationId);
		
	DelegateMemberCompleteInfo getDelegationMemberByDelegationAndMemberId(@WebParam(name="delegationId") String delegationId, @WebParam(name="memberId") String memberId);
	
	DelegateMemberCompleteInfo getDelegationMemberById(@WebParam(name="delegationMemberId") String delegationMemberId);

	List<RoleResponsibilityInfo> getRoleResponsibilities(@WebParam(name="roleId") String roleId);
	
	List<RoleResponsibilityActionInfo> getRoleMemberResponsibilityActionInfo( @WebParam(name="roleMemberId") String roleMemberId);

	DelegateTypeInfo getDelegateTypeInfo( @WebParam(name="roleId") String roleId, @WebParam(name="delegationTypeCode") String delegationTypeCode);

	DelegateTypeInfo getDelegateTypeInfoById( @WebParam(name="delegationId") String delegationId);
	
	void applicationRoleMembershipChanged( @WebParam(name="roleId") String roleId );
	
	List<KimRoleInfo> lookupRoles(@WebParam(name="searchCriteria") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> searchCriteria);

	/**
	 * Flushes an internal role cache used by the base implementation to prevent repeated database I/O.
	 */
	void flushInternalRoleCache();
	
	/**
	 * Flushes an internal role member cache used by the base implementation to prevent repeated database I/O.
	 */
	void flushInternalRoleMemberCache();
	
	/**
	 * Flushes an internal delegation cache used by the base implementation to prevent repeated database I/O.
	 */
	void flushInternalDelegationCache();
	
	/**
	 * Flushes an internal delegation member cache used by the base implementation to prevent repeated database I/O.
	 */
	void flushInternalDelegationMemberCache();
}
