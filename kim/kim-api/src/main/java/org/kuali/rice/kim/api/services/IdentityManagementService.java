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

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.Type;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliationType;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.entity.EntityDefaultQueryResults;
import org.kuali.rice.kim.api.identity.entity.EntityQueryResults;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifierType;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityAction;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;

import javax.jws.WebParam;
import java.util.List;

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
public interface IdentityManagementService {

	// *******************************
	// IdentityService
	// *******************************

	Principal getPrincipal( String principalId);
	Principal getPrincipalByPrincipalName( String principalName);

	Principal getPrincipalByPrincipalNameAndPassword(
             String principalName,
             String password
    );

	EntityDefault getEntityDefaultInfo( String entityId);
	EntityDefault getEntityDefaultInfoByPrincipalId( String principalId);
	EntityDefault getEntityDefaultInfoByPrincipalName( String principalName);

    EntityDefaultQueryResults findEntityDefaults(@WebParam(name = "query") QueryByCriteria queryByCriteria);

	//KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences(String entityId);

	Entity getEntity( String entityId);
	Entity getEntityByPrincipalId( String principalId);
	Entity getEntityByPrincipalName( String principalName);

	EntityQueryResults findEntities(@WebParam(name = "query") QueryByCriteria queryByCriteria);

	Type getAddressType( String code);
	EntityAffiliationType getAffiliationType( String code);
	Type getCitizenshipStatus( String code);
	Type getEmailType( String code);
	Type getEmploymentStatus( String code);
	Type getEmploymentType( String code);
	Type getEntityNameType( String code);
	Type getEntityType( String code);
	EntityExternalIdentifierType getExternalIdentifierType( String code);
	Type getPhoneType( String code);

	// *******************************
	// GroupService
	// *******************************

	Group getGroup( String groupId);

    Group getGroupByName(
             String namespaceCode,
             String groupName
    );

    List<String> getParentGroupIds( String groupId);
    List<String> getDirectParentGroupIds( String groupId);

    
    List<String> getGroupIdsForPrincipal( String principalId);

    
    List<String> getGroupIdsForPrincipal(
             String principalId,
             String namespaceCode
    );

    
    List<? extends Group> getGroupsForPrincipal( String principalId);

    
    List<? extends Group> getGroupsForPrincipal(
             String principalId,
             String namespaceCode
    );

    List<String> getMemberGroupIds( String groupId);
    List<String> getDirectMemberGroupIds( String groupId);

    
	boolean isMemberOfGroup(
             String principalId,
             String groupId
    );

    
	boolean isMemberOfGroup(
             String principalId,
             String namespaceCode,
             String groupName
    );

	boolean isGroupMemberOfGroup(
             String potentialMemberGroupId,
             String potentialParentId
    );

	List<String> getGroupMemberPrincipalIds( String groupId);
	List<String> getDirectGroupMemberPrincipalIds( String groupId);

    boolean addGroupToGroup(
             String childId,
             String parentId
    );

    boolean removeGroupFromGroup(
             String childId,
             String parentId
    );

    boolean addPrincipalToGroup(
             String principalId,
             String groupId
    );

    boolean removePrincipalFromGroup(
             String principalId,
             String groupId
    );

    Group createGroup( Group group);
    void removeAllMembers( String groupId);

    Group updateGroup(
             String groupId,
             Group group
    );

    // --------------------
    // Authorization Checks
    // --------------------

    boolean hasPermission(
             String principalId,
             String namespaceCode,
             String permissionName,
              AttributeSet permissionDetails
    );

    boolean isAuthorized(
             String principalId,
             String namespaceCode,
             String permissionName,
              AttributeSet permissionDetails,
              AttributeSet qualification
    );

    boolean hasPermissionByTemplateName(
             String principalId,
             String namespaceCode,
             String permissionTemplateName,
              AttributeSet permissionDetails
    );

    boolean isAuthorizedByTemplateName(
             String principalId,
             String namespaceCode,
             String permissionTemplateName,
             Attributes permissionDetails,
             Attributes qualification
    );

    /**
     * Returns the matching permission objects for a principal.
     */
    List<Permission> getAuthorizedPermissions(
             String principalId,
             String namespaceCode,
             String permissionName,
              AttributeSet permissionDetails,
              AttributeSet qualification
    );

    List<Permission> getAuthorizedPermissionsByTemplateName(
             String principalId,
             String namespaceCode,
             String permissionTemplateName,
              AttributeSet permissionDetails,
              AttributeSet qualification
    );

    List<PermissionAssigneeInfo> getPermissionAssignees(
             String namespaceCode,
             String permissionName,
              AttributeSet permissionDetails,
              AttributeSet qualification
    );

    List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName(
             String namespaceCode,
             String permissionTemplateName,
             Attributes permissionDetails,
             Attributes qualification
    );

    // ----------------------
    // Responsibility Methods
    // ----------------------

    /**
     * Get the responsibility object with the given ID.
     */
    Responsibility getResponsibility( String responsibilityId);

 	/**
 	 * Return the responsibility object for the given unique combination of namespace,
 	 * component and responsibility name.
 	 */
    Responsibility getResponsibilityByName(
             String namespaceCode,
             String responsibilityName
    );

    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibility(
             String principalId,
             String namespaceCode,
             String responsibilityName,
              AttributeSet qualification,
              AttributeSet responsibilityDetails
    );

    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibilityByTemplateName(
             String principalId,
             String namespaceCode,
             String responsibilityTemplateName,
              AttributeSet qualification,
              AttributeSet responsibilityDetails
    );

    List<ResponsibilityAction> getResponsibilityActions(
             String namespaceCode,
             String responsibilityName,
              AttributeSet qualification,
              AttributeSet responsibilityDetails
    );

    List<ResponsibilityAction> getResponsibilityActionsByTemplateName(
             String namespaceCode,
             String responsibilityTemplateName,
              AttributeSet qualification,
              AttributeSet responsibilityDetails
    );

    /**
     * Returns true if there are any assigned permissions with the given template.
     */
    boolean isPermissionDefinedForTemplateName(
             String namespaceCode,
             String permissionTemplateName,
              AttributeSet permissionDetails
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
