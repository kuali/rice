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
package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.reference.dto.AddressTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.AffiliationTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.CitizenshipStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmailTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityNameTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.PhoneTypeInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

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
	
	KimPrincipalInfo getPrincipal(String principalId);
	KimPrincipalInfo getPrincipalByPrincipalName(String principalName);
	KimPrincipalInfo getPrincipalByPrincipalNameAndPassword(String principalName, String password);

	KimEntityDefaultInfo getEntityDefaultInfo( String entityId );
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId( String principalId );
	KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName( String principalName );
	List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo( Map<String,String> searchCriteria, boolean unbounded );
	int getMatchingEntityCount( Map<String,String> searchCriteria );
	//KimEntityPrivacyPreferencesInfo getEntityPrivacyPreferences(String entityId);

	KimEntityInfo getEntityInfo( String entityId );
	KimEntityInfo getEntityInfoByPrincipalId( String principalId );
	KimEntityInfo getEntityInfoByPrincipalName( String principalName );
	List<KimEntityInfo> lookupEntityInfo( Map<String,String> searchCriteria, boolean unbounded );
	
	AddressTypeInfo getAddressType( String code );
	AffiliationTypeInfo getAffiliationType( String code );
	CitizenshipStatusInfo getCitizenshipStatus( String code );
	EmailTypeInfo getEmailType( String code );
	EmploymentStatusInfo getEmploymentStatus( String code );
	EmploymentTypeInfo getEmploymentType( String code );
	EntityNameTypeInfo getEntityNameType( String code );
	EntityTypeInfo getEntityType( String code );
	ExternalIdentifierTypeInfo getExternalIdentifierType( String code );
	PhoneTypeInfo getPhoneType( String code );

	// *******************************
	// GroupService
	// *******************************

	GroupInfo getGroup(String groupId);
    GroupInfo getGroupByName(String namespaceCode, String groupName);   
    List<String> getParentGroupIds(String groupId);
    List<String> getDirectParentGroupIds(String groupId);

    List<String> getGroupIdsForPrincipal(String principalId);
    List<String> getGroupIdsForPrincipal(String principalId, String namespaceCode );
    List<? extends GroupInfo> getGroupsForPrincipal(String principalId);
    List<? extends GroupInfo> getGroupsForPrincipal(String principalId, String namespaceCode );
    List<String> getMemberGroupIds(String groupId);
    List<String> getDirectMemberGroupIds(String groupId);

	boolean isMemberOfGroup(String principalId, String groupId);
	boolean isMemberOfGroup(String principalId, String namespaceCode, String groupName);
	boolean isGroupMemberOfGroup(String potentialMemberGroupId, String potentialParentId);
	List<String> getGroupMemberPrincipalIds(String groupId);
	List<String> getDirectGroupMemberPrincipalIds(String groupId);
    
    boolean addGroupToGroup(String childId, String parentId);    
    boolean removeGroupFromGroup(String childId, String parentId);  
    boolean addPrincipalToGroup(String principalId, String groupId);   
    boolean removePrincipalFromGroup(String principalId, String groupId);
    GroupInfo createGroup(GroupInfo groupInfo);
    void removeAllGroupMembers(String groupId);
    GroupInfo updateGroup(String groupId, GroupInfo groupInfo);
    
	// *******************************
	// AuthenticationService
	// *******************************
	
	String getAuthenticatedPrincipalName(HttpServletRequest request);
    
	// *******************************
	// AuthorizationService
	// *******************************
	   
    // --------------------
    // Authorization Checks
    // --------------------
    
    boolean hasPermission(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails);
    boolean isAuthorized( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification );  

    boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails);
    boolean isAuthorizedByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification );  

    /**
     * Returns the matching permission objects for a principal.
     */
    List<? extends KimPermissionInfo> getAuthorizedPermissions( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification );
    List<? extends KimPermissionInfo> getAuthorizedPermissionsByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification);

    List<PermissionAssigneeInfo> getPermissionAssignees(String namespaceCode,
			String permissionName, AttributeSet permissionDetails,
			AttributeSet qualification);
    List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName(String namespaceCode,
			String permissionTemplateName, AttributeSet permissionDetails,
			AttributeSet qualification);
    // ----------------------
    // Responsibility Methods
    // ----------------------

    /**
     * Get the responsibility object with the given ID.
     */
    KimResponsibilityInfo getResponsibility(String responsibilityId);
    
 	/** 
 	 * Return the responsibility object for the given unique combination of namespace,
 	 * component and responsibility name.
 	 */
    List<? extends KimResponsibilityInfo> getResponsibilitiesByName( String namespaceCode, String responsibilityName );
    
    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibility( String principalId, String namespaceCode, String responsibilityName, AttributeSet qualification, AttributeSet responsibilityDetails );

    /**
     * Check whether the principal has the given responsibility within the passed qualifier.
     */
    boolean hasResponsibilityByTemplateName( String principalId, String namespaceCode, String responsibilityTemplateName, AttributeSet qualification, AttributeSet responsibilityDetails );

    List<ResponsibilityActionInfo> getResponsibilityActions( String namespaceCode, String responsibilityName, AttributeSet qualification, AttributeSet responsibilityDetails);
    List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName( String namespaceCode, String responsibilityTemplateName,	AttributeSet qualification, AttributeSet responsibilityDetails);

    /**
     * Returns true if there are any assigned permissions with the given template.
     */
    boolean isPermissionDefinedForTemplateName( String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails );

    
    // ----------------------
    // Cache Flush Methods
    // ----------------------
    
    void flushAllCaches();
    void flushEntityPrincipalCaches();
	void flushGroupCaches();
	void flushPermissionCaches();
	void flushResponsibilityCaches();
 
}
