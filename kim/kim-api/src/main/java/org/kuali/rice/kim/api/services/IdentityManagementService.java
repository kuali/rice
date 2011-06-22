/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.api.services;

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.jaxb.AttributeSetAdapter;
import org.kuali.rice.core.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Type;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityAction;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.util.KIMWebServiceConstants;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

/**
 * This is the front end for the KIM module.  Clients of KIM should access this service from
 * their applications.  If KIM is not running on the same machine (VM) as the application
 * (as would be the case with a standalone Rice server), then this service should be implemented
 * locally within the application and access the core KIM services
 * (Authentication/Authorization/Identity/Group) via the service bus.
 *
 *  For efficiency, implementations of this interface should add appropriate caching of
 *  the information retrieved from the core services for load and performance reasons.
 *
 *  Most of the methods on this interface are straight pass-thrus to methods on the four core services.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(name = KIMWebServiceConstants.IdentityManagementService.WEB_SERVICE_NAME, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface IdentityManagementService {

	// *******************************
	// IdentityService
	// *******************************

	Principal getPrincipal(@WebParam(name = "principalId") String principalId);
	Principal getPrincipalByPrincipalName(@WebParam(name = "principalName") String principalName);

	Principal getPrincipalByPrincipalNameAndPassword(
            @WebParam(name = "principalName") String principalName,
            @WebParam(name = "password") String password
    );

	KimEntityDefaultInfo getEntityDefaultInfo(@WebParam(name = "entityId") String entityId);
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId(@WebParam(name = "principalId") String principalId);
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(@WebParam(name = "principalName") String principalName);

	List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo(
            @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
            @WebParam(name = "searchCriteria") Map<String, String> searchCriteria,
            @WebParam(name = "unbounded") boolean unbounded
    );

	int getMatchingEntityCount(@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
                               @WebParam(name = "searchCriteria") Map<String, String> searchCriteria);

	//KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences(String entityId);

	KimEntityInfo getEntityInfo(@WebParam(name = "entityId") String entityId);
	KimEntityInfo getEntityInfoByPrincipalId(@WebParam(name = "principalId") String principalId);
	KimEntityInfo getEntityInfoByPrincipalName(@WebParam(name = "principalName") String principalName);

	List<KimEntityInfo> lookupEntityInfo(
            @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
            @WebParam(name = "searchCriteria") Map<String, String> searchCriteria,
            @WebParam(name = "unbounded") boolean unbounded
    );

	Type getAddressType(@WebParam(name = "code") String code);
	EntityAffiliationType getAffiliationType(@WebParam(name = "code") String code);
	Type getCitizenshipStatus(@WebParam(name = "code") String code);
	Type getEmailType(@WebParam(name = "code") String code);
	Type getEmploymentStatus(@WebParam(name = "code") String code);
	Type getEmploymentType(@WebParam(name = "code") String code);
	Type getEntityNameType(@WebParam(name = "code") String code);
	Type getEntityType(@WebParam(name = "code") String code);
	EntityExternalIdentifierType getExternalIdentifierType(@WebParam(name = "code") String code);
	Type getPhoneType(@WebParam(name = "code") String code);

	// *******************************
	// GroupService
	// *******************************

	Group getGroup(@WebParam(name = "groupId") String groupId);

    Group getGroupByName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "groupName") String groupName
    );

    List<String> getParentGroupIds(@WebParam(name = "groupId") String groupId);
    List<String> getDirectParentGroupIds(@WebParam(name = "groupId") String groupId);

    @WebMethod(operationName="getGroupIdsForPrincipal")
    List<String> getGroupIdsForPrincipal(@WebParam(name = "principalId") String principalId);

    @WebMethod(operationName="getGroupIdsForPrincipalByNamespace")
    List<String> getGroupIdsForPrincipal(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode
    );

    @WebMethod(operationName="getGroupsForPrincipal")
    List<? extends Group> getGroupsForPrincipal(@WebParam(name = "principalId") String principalId);

    @WebMethod(operationName="getGroupsForPrincipalByNamespace")
    List<? extends Group> getGroupsForPrincipal(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode
    );

    List<String> getMemberGroupIds(@WebParam(name = "groupId") String groupId);
    List<String> getDirectMemberGroupIds(@WebParam(name = "groupId") String groupId);

    @WebMethod(operationName="isMemberOfGroup")
	boolean isMemberOfGroup(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "groupId") String groupId
    );

    @WebMethod(operationName="isMemberOfGroupByNamespace")
	boolean isMemberOfGroup(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "groupName") String groupName
    );

	boolean isGroupMemberOfGroup(
            @WebParam(name = "potentialMemberGroupId") String potentialMemberGroupId,
            @WebParam(name = "potentialParentId") String potentialParentId
    );

	List<String> getGroupMemberPrincipalIds(@WebParam(name = "groupId") String groupId);
	List<String> getDirectGroupMemberPrincipalIds(@WebParam(name = "groupId") String groupId);

    boolean addGroupToGroup(
            @WebParam(name = "childId") String childId,
            @WebParam(name = "parentId") String parentId
    );

    boolean removeGroupFromGroup(
            @WebParam(name = "childId") String childId,
            @WebParam(name = "parentId") String parentId
    );

    boolean addPrincipalToGroup(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "groupId") String groupId
    );

    boolean removePrincipalFromGroup(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "groupId") String groupId
    );

    Group createGroup(@WebParam(name = "group") Group group);
    void removeAllMembers(@WebParam(name = "groupId") String groupId);

    Group updateGroup(
            @WebParam(name = "groupId") String groupId,
            @WebParam(name = "group") Group group
    );

	// *******************************
	// AuthenticationService
	// *******************************
    //@WebMethod(exclude=true)
	//String getAuthenticatedPrincipalName( @WebParam(name="request") HttpServletRequest request);

	// *******************************
	// AuthorizationService
	// *******************************

    // --------------------
    // Authorization Checks
    // --------------------

    boolean hasPermission(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionName") String permissionName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails
    );

    boolean isAuthorized(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionName") String permissionName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification
    );

    boolean hasPermissionByTemplateName(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionTemplateName") String permissionTemplateName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails
    );

    boolean isAuthorizedByTemplateName(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionTemplateName") String permissionTemplateName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification
    );

    /**
     * Returns the matching permission objects for a principal.
     */
    List<? extends KimPermissionInfo> getAuthorizedPermissions(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionName") String permissionName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification
    );

    List<? extends KimPermissionInfo> getAuthorizedPermissionsByTemplateName(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionTemplateName") String permissionTemplateName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification
    );

    List<PermissionAssigneeInfo> getPermissionAssignees(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionName") String permissionName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification
    );

    List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionTemplateName") String permissionTemplateName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification
    );

    // ----------------------
    // Responsibility Methods
    // ----------------------

    /**
     * Get the responsibility object with the given ID.
     */
    Responsibility getResponsibility(@WebParam(name = "responsibilityId") String responsibilityId);

 	/**
 	 * Return the responsibility object for the given unique combination of namespace,
 	 * component and responsibility name.
 	 */
    List<? extends Responsibility> getResponsibilitiesByName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "responsibilityName") String responsibilityName
    );

    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibility(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "responsibilityName") String responsibilityName,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification,
            @WebParam(name = "responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails
    );

    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibilityByTemplateName(
            @WebParam(name = "principalId") String principalId,
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "responsibilityTemplateName") String responsibilityTemplateName,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification,
            @WebParam(name = "responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails
    );

    List<ResponsibilityAction> getResponsibilityActions(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "responsibilityName") String responsibilityName,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification,
            @WebParam(name = "responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails
    );

    List<ResponsibilityAction> getResponsibilityActionsByTemplateName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "responsibilityTemplateName") String responsibilityTemplateName,
            @WebParam(name = "qualification") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet qualification,
            @WebParam(name = "responsibilityDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet responsibilityDetails
    );

    /**
     * Returns true if there are any assigned permissions with the given template.
     */
    boolean isPermissionDefinedForTemplateName(
            @WebParam(name = "namespaceCode") String namespaceCode,
            @WebParam(name = "permissionTemplateName") String permissionTemplateName,
            @WebParam(name = "permissionDetails") @XmlJavaTypeAdapter(value = AttributeSetAdapter.class) AttributeSet permissionDetails
    );


    // ----------------------
    // Cache Flush Methods
    // ----------------------

    void flushAllCaches();
    void flushEntityPrincipalCaches();
	void flushGroupCaches();
	void flushPermissionCaches();
	void flushResponsibilityCaches();

}
