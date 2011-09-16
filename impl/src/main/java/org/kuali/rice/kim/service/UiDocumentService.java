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

import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityActionBo;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.List;
import java.util.Map;

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
     * @param definitions
     */
    Map<String,Object> getAttributeEntries( List<KimAttributeField> definitions );
	/**
	 * 
	 * This method is to load identity to person document pending Bos when user 'initiate' a document for 'editing' identity.
	 * 
	 * @param identityManagementPersonDocument
	 * @param principalId
	 */
	void loadEntityToPersonDoc(IdentityManagementPersonDocument identityManagementPersonDocument, String principalId);

	/**
	 * 
	 * This method loads a role document
	 * 
	 * @param identityManagementRoleDocument
	 */
	public void loadRoleDoc(IdentityManagementRoleDocument identityManagementRoleDocument, Role kimRole);
	
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
	 * @param identityManagementGroupDocument
	 */
	public void loadGroupDoc(IdentityManagementGroupDocument identityManagementGroupDocument, Group kimGroup);
	
	/**
	 * 
	 * This method ...
	 * 
	 * @param identityManagementGroupDocument
	 */
	public void saveGroup(IdentityManagementGroupDocument identityManagementGroupDocument);

	public BusinessObject getMember(String memberTypeCode, String memberId);
	
	public String getMemberName(String memberTypeCode, String memberId);
	
	public String getMemberNamespaceCode(String memberTypeCode, String memberId);

	public String getMemberName(String memberTypeCode, BusinessObject member);
	
	public String getMemberNamespaceCode(String memberTypeCode, BusinessObject member);

	public List<RoleResponsibilityActionBo> getRoleMemberResponsibilityActionImpls(String roleMemberId);
	
	public List<DelegateTypeBo> getRoleDelegations(String roleId);
	
	public KimDocumentRoleMember getKimDocumentRoleMember(String memberTypeCode, String memberId, String roleId);
	
	public String getMemberIdByName(String memberTypeCode, String memberNamespaceCode, String memberName);

	public void setDelegationMembersInDocument(IdentityManagementRoleDocument identityManagementRoleDocument);
	
	public RoleMemberBo getRoleMember(String roleMemberId);
	
	public List<KimDocumentRoleMember> getRoleMembers(Map<String,String> fieldValues);
	
	public boolean canModifyEntity( String currentUserPrincipalId, String toModifyPrincipalId );
	public boolean canOverrideEntityPrivacyPreferences( String currentUserPrincipalId, String toModifyPrincipalId );

	public List<EntityEmployment> getEntityEmploymentInformationInfo(String entityId);
}
