/*
 * Copyright 2007-2009 The Kuali Foundation
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

import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.core.util.jaxb.SqlDateAdapter;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Date;
import java.util.Map;

/**
 * This service provides operations for creating and updating roles. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@WebService(name = KIMWebServiceConstants.RoleUpdateService.WEB_SERVICE_NAME, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface RoleUpdateService {
   
	/**
	 * Assigns the principal with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    void assignPrincipalToRole(@WebParam(name="principalId") String principalId, 
    		@WebParam(name="namespaceCode") String namespaceCode, 
    		@WebParam(name="roleName") String roleName, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws UnsupportedOperationException;
    
	/**
	 * Assigns the group with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    void assignGroupToRole(@WebParam(name="groupId") String groupId, 
    		@WebParam(name="namespaceCode") String namespaceCode, 
    		@WebParam(name="roleName") String roleName, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws UnsupportedOperationException;

	/**
	 * Assigns the role with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    void assignRoleToRole(@WebParam(name="roleId") String roleId, 
    		@WebParam(name="namespaceCode") String namespaceCode, 
    		@WebParam(name="roleName") String roleName, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws UnsupportedOperationException;

	/**
	 * Assigns the role with the given id to the role with the specified
	 * namespace code and name with the supplied set of qualifications.
	 */
    RoleMember saveRoleMemberForRole(@WebParam(name="roleMemberId") String roleMemberId,
    		@WebParam(name="memberId") String memberId,
    		@WebParam(name="memberTypeCode") String memberTypeCode, 
    		@WebParam(name="roleId") String roleId, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications,
    		@XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name="activeFromDate") Date activeFromDate, 
    		@XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name="activeToDate") Date activeToDate) throws UnsupportedOperationException;

    /**
     * @param roleResponsibilityId
     * @param roleMemberId
     * @param actionTypeCode
     * @param actionPolicyCode
     * @param priorityNumber
     * @param forceAction
     */
    void saveRoleRspActions(@WebParam(name="roleResponsibilityActionId") String roleResponsibilityActionId,
    		@WebParam(name="roleId") String roleId, 
    		@WebParam(name="roleResponsibilityId") String roleResponsibilityId, 
    		@WebParam(name="roleMemberId") String roleMemberId, 
    		@WebParam(name="actionTypeCode") String actionTypeCode, 
    		@WebParam(name="actionPolicyCode") String actionPolicyCode, 
    		@WebParam(name="priorityNumber") Integer priorityNumber, 
    		@WebParam(name="forceAction") Boolean forceAction);
    
	/**
	 * Assigns the member with the given id as a delegation member to the role 
	 * with the specified namespace code and name with the supplied set of qualifications.
	 */
    public void saveDelegationMemberForRole(@WebParam(name="delegationMemberId") String delegationMemberId,
    		@WebParam(name="roleMemberId") String roleMemberId, 
    		@WebParam(name="memberId") String memberId, 
    		@WebParam(name="memberTypeCode") String memberTypeCode, 
    		@WebParam(name="delegationTypeCode") String delegationTypeCode, 
    		@WebParam(name="roleId") String roleId, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications,
    		@XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name="activeFromDate") Date activeFromDate, 
    		@XmlJavaTypeAdapter(value = SqlDateAdapter.class) @WebParam(name="activeToDate") Date activeToDate) throws UnsupportedOperationException;

    /**
     * Remove the principal with the given id and qualifications from the role
     * with the specified namespace code and role name.
     */
    void removePrincipalFromRole(@WebParam(name="principalId") String principalId, 
    		@WebParam(name="namespaceCode") String namespaceCode, 
    		@WebParam(name="roleName") String roleName, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws UnsupportedOperationException;
    
    /**
     * Remove the group with the given id and qualifications from the role
     * with the specified namespace code and role name.
     */
    void removeGroupFromRole(@WebParam(name="groupId") String groupId, 
    		@WebParam(name="namespaceCode") String namespaceCode, 
    		@WebParam(name="roleName") String roleName, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws UnsupportedOperationException;

    /**
     * Remove the group with the given id and qualifications from the role
     * with the specified namespace code and role name.
     */
    void removeRoleFromRole(@WebParam(name="roleId") String roleId, 
    		@WebParam(name="namespaceCode") String namespaceCode, 
    		@WebParam(name="roleName") String roleName, 
    		@WebParam(name="qualifications") @XmlJavaTypeAdapter(value = MapStringStringAdapter.class) Map<String, String> qualifications) throws UnsupportedOperationException;

    /**
     * Creates or updates role with given attributes
     */
    void saveRole(@WebParam(name = "roleId") String roleId, @WebParam(name = "roleName") String roleName, @WebParam(name = "roleDescription") String roleDescription, @WebParam(name = "active") boolean active, @WebParam(name = "kimTypeId") String kimTypeId,
            @WebParam(name = "namespaceCode") String namespaceCode) throws UnsupportedOperationException;

    /**
     * Returns id available for a new role
     */
    String getNextAvailableRoleId() throws UnsupportedOperationException;

    /**
     * Assigns the given permission to the given role
     */
    void assignPermissionToRole(String permissionId, String roleId) throws UnsupportedOperationException;
}
