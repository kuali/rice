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
package org.kuali.rice.kim.service;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEmploymentInformationInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface UiDocumentService {
	/**
	 * 
	 * This method to populate Entity tables from person document pending tables when it is approved.
	 * 	  
	 * @param identityManagementPersonDocument
	 */
    void saveEntityPerson(IdentityManagementPersonDocument identityManagementPersonDocument);
    
    /**
     * 
     * This method is to set up the DD attribute entry map for role qualifiers, so it can be rendered.
     * 
     * @param personDocRole
     */
    Map<String,Object> getAttributeEntries( AttributeDefinitionMap definitions );
	/**
	 * 
	 * This method is to load identity to person document pending Bos when user 'initiate' a document for 'editing' identity.
	 * 
	 * @param identityManagementPersonDocument
	 * @param kimEntity
	 */
	void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId);

	/**
	 * 
	 * This method loads a role document
	 * 
	 * @param identityManagementRoleDocument
	 */
	public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, KimRoleInfo kimRole);
	
	/**
	 * 
	 * This method ...
	 * 
	 * @param identityManagementRoleDocument
	 */
	public void saveRole(IdentityManagementRoleDocument identityManagementRoleDocument);


	/**
	 * 
	 * This method loads a role document
	 * 
	 * @param identityManagementRoleDocument
	 */
	public void loadGroupDoc(IdentityManagementGroupDocument identityManagementGroupDocument, Group kimGroup);
	
	/**
	 * 
	 * This method ...
	 * 
	 * @param identityManagementRoleDocument
	 */
	public void saveGroup(IdentityManagementGroupDocument identityManagementGroupDocument);

	public BusinessObject getMember(String memberTypeCode, String memberId);
	
	public String getMemberName(String memberTypeCode, String memberId);
	
	public String getMemberNamespaceCode(String memberTypeCode, String memberId);

	public String getMemberName(String memberTypeCode, BusinessObject member);
	
	public String getMemberNamespaceCode(String memberTypeCode, BusinessObject member);

	public List<RoleResponsibilityActionImpl> getRoleMemberResponsibilityActionImpls(String roleMemberId);
	
	public List<KimDelegationImpl> getRoleDelegations(String roleId);
	
	public KimDocumentRoleMember getKimDocumentRoleMember(String memberTypeCode, String memberId, String roleId);
	
	public String getMemberIdByName(String memberTypeCode, String memberNamespaceCode, String memberName);

	public void setDelegationMembersInDocument(IdentityManagementRoleDocument identityManagementRoleDocument);
	
	public RoleMemberImpl getRoleMember(String roleMemberId);
	
	public List<KimDocumentRoleMember> getRoleMembers(Map<String,String> fieldValues);
	
	public boolean canModifyEntity( String currentUserPrincipalId, String toModifyPrincipalId );
	public boolean canOverrideEntityPrivacyPreferences( String currentUserPrincipalId, String toModifyPrincipalId );

	public List<KimEntityEmploymentInformationInfo> getEntityEmploymentInformationInfo(String entityId);
}
