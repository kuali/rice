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

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.jaxb.JaxbStringMapAdapter;
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
@WebService(name = "RoleService", targetNamespace = "http://org.kuali.rice/kim/role")
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
    List<AttributeSet> getRoleQualifiersForPrincipal( @WebParam(name="principalId") String principalId, @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") AttributeSet qualification );

    /**
     * Returns a list of role qualifiers that the given principal has without taking into consideration
     * that the principal may be a member via an assigned group or role.  Use in situations where
     * you are only interested in the qualifiers that are directly assigned to the principal.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalNamespaceRolename")
    List<AttributeSet> getRoleQualifiersForPrincipal( @WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") AttributeSet qualification );
    
    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalIncludingNestedNamespaceRolename")
	List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( @WebParam(name="principalId") String principalId, @WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") AttributeSet qualification );

    /**
     * Returns a list of role qualifiers that the given principal.  If the principal's membership
     * is via a group or role, that group or role's qualifier on the given role is returned.
     */
    @WebMethod(operationName = "getRoleQualifersForPrincipalIncludingNestedRoleIds")
	List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( @WebParam(name="principalId") String principalId, @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") AttributeSet qualification );

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
    List<RoleMembershipInfo> getRoleMembers( @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") AttributeSet qualification );

    /**
	 * This method gets all the members, then traverses down into members of type role and group to obtain the nested principal ids
	 * 
	 * @return list of member principal ids
	 */
    Collection<String> getRoleMemberPrincipalIds(@WebParam(name="namespaceCode") String namespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") AttributeSet qualification);

    /**
     * Returns whether the given principal has any of the passed role IDs with the given qualification.
     */
    boolean principalHasRole( @WebParam(name="principalId") String principalId, @WebParam(name="roleIds") List<String> roleIds, @WebParam(name="qualification") AttributeSet qualification );
    
    /**
     * Returns the subset of the given principal ID list which has the given role and qualification.
     * This is designed to be used by lookups of people by their roles.
     */
    List<String> getPrincipalIdSubListWithRole( @WebParam(name="principalIds") List<String> principalIds, @WebParam(name="roleNamespaceCode") String roleNamespaceCode, @WebParam(name="roleName") String roleName, @WebParam(name="qualification") AttributeSet qualification );

    /**
	 * 
	 * This method get search results for role lookup
	 */
	List<? extends Role> getRolesSearchResults(@XmlJavaTypeAdapter(value = JaxbStringMapAdapter.class) @WebParam(name = "fieldValues") java.util.Map<String,String> fieldValues);
	
	/**
	 * Notifies all of a principal's roles and role types that the principal has been inactivated.
	 */
	void principalInactivated( @WebParam(name="principalId") String principalId );
	
	void roleInactivated(@WebParam(name="roleId") String roleId);
	
    void groupInactivated(@WebParam(name="groupId") String groupId);
    
	List<RoleMembershipInfo> getFirstLevelRoleMembers(@WebParam(name="roleIds") List<String> roleIds);
}
