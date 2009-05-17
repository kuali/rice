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
package org.kuali.rice.kim.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibilityAction;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMemberQualifier;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kim.web.struts.form.IdentityManagementRoleDocumentForm;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.TypedArrayList;


/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocument extends IdentityManagementTypeAttributeTransactionalDocument {

	private static final long serialVersionUID = 1L;
	// principal data
	protected String roleId;
	protected String roleTypeId;
	protected String roleTypeName;
	protected String roleNamespace = "";
	protected String roleName = "";
	protected boolean active = true;

	protected boolean editing;
	
	protected List<KimDocumentRolePermission> permissions = new TypedArrayList(KimDocumentRolePermission.class);
	protected List<KimDocumentRoleResponsibility> responsibilities = new TypedArrayList(KimDocumentRoleResponsibility.class);
	protected List<KimDocumentRoleMember> members = new TypedArrayList(KimDocumentRoleMember.class);
	private List<RoleDocumentDelegationMember> delegationMembers = new TypedArrayList(RoleDocumentDelegationMember.class);
	private List<RoleDocumentDelegation> delegations = new TypedArrayList(RoleDocumentDelegation.class);
	
	transient private ResponsibilityService responsibilityService;
	
	public IdentityManagementRoleDocument() {
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return this.roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the roleNamespace
	 */
	public String getRoleNamespace() {
		return this.roleNamespace;
	}

	/**
	 * @param roleNamespace the roleNamespace to set
	 */
	public void setRoleNamespace(String roleNamespace) {
		this.roleNamespace = roleNamespace;
	}

	/**
	 * @return the roleTypeId
	 */
	public String getRoleTypeId() {
		return this.roleTypeId;
	}

	/**
	 * @param roleTypeId the roleTypeId to set
	 */
	public void setRoleTypeId(String roleTypeId) {
		this.roleTypeId = roleTypeId;
	}

	/**
	 * @return the roleTypeName
	 */
	public String getRoleTypeName() {
		if ( roleTypeName == null ) {
			if ( kimType != null ) {
				roleTypeName = kimType.getName();
			} else if ( roleTypeId != null ) {
		        Map<String, String> criteria = new HashMap<String, String>();
		        criteria.put(KimConstants.PrimaryKeyConstants.KIM_TYPE_ID, roleTypeId);
		        setKimType((KimTypeImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimTypeImpl.class, criteria));
		        if ( kimType != null ) {
		        	roleTypeName = kimType.getName();
		        }
			}
		}
		return this.roleTypeName;
	}

	/**
	 * @param roleTypeName the roleTypeName to set
	 */
	public void setRoleTypeName(String roleTypeName) {
		this.roleTypeName = roleTypeName;
	}

	/**
	 * @return the delegationMembers
	 */
	public List<RoleDocumentDelegationMember> getDelegationMembers() {
		return this.delegationMembers;
	}

	/**
	 * @param delegationMembers the delegationMembers to set
	 */
	public void setDelegationMembers(
			List<RoleDocumentDelegationMember> delegationMembers) {
		this.delegationMembers = delegationMembers;
	}

	/**
	 * @return the permissions
	 */
	public List<KimDocumentRolePermission> getPermissions() {
		return this.permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(List<KimDocumentRolePermission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the responsibilities
	 */
	public List<KimDocumentRoleResponsibility> getResponsibilities() {
		return this.responsibilities;
	}

	/**
	 * @param responsibilities the responsibilities to set
	 */
	public void setResponsibilities(
			List<KimDocumentRoleResponsibility> responsibilities) {
		this.responsibilities = responsibilities;
	}

	/**
	 * @return the members
	 */
	public List<KimDocumentRoleMember> getMembers() {
		return this.members;
	}
	/**
	 * @return the members
	 */
	public KimDocumentRoleMember getMember(String roleMemberId) {
		if(StringUtils.isEmpty(roleMemberId)) return null;
		for(KimDocumentRoleMember roleMember: getMembers()){
			if(roleMemberId.equals(roleMember.getRoleMemberId()))
				return roleMember;
		}
		return null;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<KimDocumentRoleMember> members) {
		this.members = members;
	}

	public void addResponsibility(KimDocumentRoleResponsibility roleResponsibility){
		if(!getResponsibilityService().areActionsAtAssignmentLevelById(roleResponsibility.getResponsibilityId())) {
			roleResponsibility.getRoleRspActions().add(getNewRespAction(roleResponsibility));
		}
       	getResponsibilities().add(roleResponsibility);
	}

	protected KimDocumentRoleResponsibilityAction getNewRespAction(KimDocumentRoleResponsibility roleResponsibility){
		KimDocumentRoleResponsibilityAction roleRspAction = new KimDocumentRoleResponsibilityAction();
		roleRspAction.setKimResponsibility(roleResponsibility.getKimResponsibility());
		roleRspAction.setRoleResponsibilityId(roleResponsibility.getRoleResponsibilityId());        		
		return roleRspAction;
	}
	
	public void addDelegationMember(RoleDocumentDelegationMember newDelegationMember){
		getDelegationMembers().add(newDelegationMember);
	}

	/**
	 * @param members the members to set
	 */
	public void addMember(KimDocumentRoleMember member) {
		String roleMemberId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S).toString();
		member.setRoleMemberId(roleMemberId);
		setupMemberRspActions(member);
       	getMembers().add(member);
	}

	/**
	 * @param members the members to set
	 */
	public KimDocumentRoleMember getBlankMember() {
		KimDocumentRoleMember member = new KimDocumentRoleMember();
		KimDocumentRoleQualifier qualifier;
		for(String key : getDefinitions().keySet()) {
        	qualifier = new KimDocumentRoleQualifier();
        	qualifier.setKimAttrDefnId(getKimAttributeDefnId(getDefinitions().get(key)));
        	member.getQualifiers().add(qualifier);
        }
       	setupMemberRspActions(member);
       	return member;
	}

	/**
	 * @param members the members to set
	 */
	public RoleDocumentDelegationMember getBlankDelegationMember() {
		RoleDocumentDelegationMember member = new RoleDocumentDelegationMember();
		RoleDocumentDelegationMemberQualifier qualifier;
		for(String key : getDefinitions().keySet()) {
			qualifier = new RoleDocumentDelegationMemberQualifier();
			setAttrDefnIdForDelMemberQualifier(qualifier, getDefinitions().get(key));
        	member.getQualifiers().add(qualifier);
        }
       	return member;
	}

    public void setupMemberRspActions(KimDocumentRoleMember member) {
    	member.getRoleRspActions().clear();
        for (KimDocumentRoleResponsibility roleResp: getResponsibilities()) {
        	if (getResponsibilityService().areActionsAtAssignmentLevelById(roleResp.getResponsibilityId())) {
        		KimDocumentRoleResponsibilityAction action = new KimDocumentRoleResponsibilityAction();
        		action.setRoleResponsibilityId("*");
        		action.setRoleMemberId(member.getRoleMemberId());
        		member.getRoleRspActions().add(action);
        		break;
        	}        	
        }
    }

    public void updateMembers(IdentityManagementRoleDocumentForm roleDocumentForm){
    	for(KimDocumentRoleMember member: roleDocumentForm.getRoleDocument().getMembers()){
    		roleDocumentForm.getRoleDocument().setupMemberRspActions(member);
    	}
    }
    
    public void updateMembers(KimDocumentRoleResponsibility newResponsibility){
    	for(KimDocumentRoleMember member: getMembers()){
    		setupMemberRspActions(newResponsibility, member);
    	}
    }
    
    public void setupMemberRspActions(KimDocumentRoleResponsibility roleResp, KimDocumentRoleMember member) {
    	if (getResponsibilityService().areActionsAtAssignmentLevelById(roleResp.getResponsibilityId())) {
    		KimDocumentRoleResponsibilityAction action = new KimDocumentRoleResponsibilityAction();
    		action.setRoleResponsibilityId("*");
    		action.setRoleMemberId(member.getRoleMemberId());
    		member.getRoleRspActions().add(action);
    	}        	
    }
    
    private void setAttrDefnIdForDelMemberQualifier(RoleDocumentDelegationMemberQualifier qualifier,AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		qualifier.setKimAttrDefnId(((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		//qualifier.refreshReferenceObject("kimAttribute");
    	} else {
    		qualifier.setKimAttrDefnId(((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		//qualifier.refreshReferenceObject("kimAttribute");

    	}
    }
    
    /**
     * @see org.kuali.rice.kns.document.DocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
	public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) throws Exception {
		super.doRouteStatusChange(statusChangeEvent);
		if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
			KIMServiceLocator.getUiDocumentService().saveRole(this);
		}
	}

	public void initializeDocumentForNewRole() {
		if(StringUtils.isBlank(this.roleId)){
			this.roleId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_ID_S).toString();
		}
		if(StringUtils.isBlank(this.roleTypeId)) {
			this.roleTypeId = "1";
		}
	}
	
	public String getRoleId(){
		if(StringUtils.isBlank(this.roleId)){
			initializeDocumentForNewRole();
		}
		return roleId;
	}
	
	@Override
	public void prepareForSave(){
		String roleId;
		if(StringUtils.isBlank(getRoleId())){
			roleId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_ID_S).toString();
			setRoleId(roleId);
		} else
			roleId = getRoleId();

		if(getPermissions()!=null){
			String rolePermissionId;
			for(KimDocumentRolePermission permission: getPermissions()){
				permission.setRoleId(roleId);
				if(StringUtils.isBlank(permission.getRolePermissionId())){
					rolePermissionId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S).toString();
					permission.setRolePermissionId(rolePermissionId);
				}
			}
		}
		if(getResponsibilities()!=null){
			String roleResponsibilityId;
			for(KimDocumentRoleResponsibility responsibility: getResponsibilities()){
				if(StringUtils.isBlank(responsibility.getRoleResponsibilityId())){
					roleResponsibilityId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_RSP_ID_S).toString();
					responsibility.setRoleResponsibilityId(roleResponsibilityId);
				}
				responsibility.setRoleId(roleId);
				if(!getResponsibilityService().areActionsAtAssignmentLevelById(responsibility.getResponsibilityId())){
					if(StringUtils.isBlank(responsibility.getRoleRspActions().get(0).getRoleResponsibilityActionId())){
						String roleResponsibilityActionId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_RSP_ACTN_ID_S).toString();
						responsibility.getRoleRspActions().get(0).setRoleResponsibilityActionId(roleResponsibilityActionId);
					}
					responsibility.getRoleRspActions().get(0).setRoleMemberId("*");
					responsibility.getRoleRspActions().get(0).setDocumentNumber(getDocumentNumber());
				}
			}
		}
		if(getMembers()!=null){
			String roleMemberId;
			String roleResponsibilityActionId;
			for(KimDocumentRoleMember member: getMembers()){
				member.setRoleId(roleId);
				if(StringUtils.isBlank(member.getRoleMemberId())){
					roleMemberId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S).toString();
					member.setRoleMemberId(roleMemberId);
				}
				for(KimDocumentRoleQualifier qualifier: member.getQualifiers()){
					qualifier.setKimTypId(getKimType().getKimTypeId());
				}
				for(KimDocumentRoleResponsibilityAction roleRespAction: member.getRoleRspActions()){
					if(StringUtils.isBlank(roleRespAction.getRoleResponsibilityActionId())){
						roleResponsibilityActionId = getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_RSP_ACTN_ID_S).toString();
						roleRespAction.setRoleResponsibilityActionId(roleResponsibilityActionId);
					}
					roleRespAction.setRoleMemberId(member.getRoleMemberId());
					roleRespAction.setDocumentNumber(getDocumentNumber());
					if ( !StringUtils.equals( roleRespAction.getRoleResponsibilityId(), "*" ) ) {
						for(KimDocumentRoleResponsibility responsibility: getResponsibilities()){
							if( StringUtils.equals( roleRespAction.getKimResponsibility().getResponsibilityId(), responsibility.getResponsibilityId() ) ) {
								roleRespAction.setRoleResponsibilityId(responsibility.getRoleResponsibilityId());
							}
						}
					}
				}
			}
		}
		if(getDelegationMembers()!=null){
			for(RoleDocumentDelegationMember delegationMember: getDelegationMembers()){
				delegationMember.setDocumentNumber(getDocumentNumber());
				addDelegationMemberToDelegation(delegationMember);
			}
			for(RoleDocumentDelegation delegation: getDelegations()){
				delegation.setDocumentNumber(getDocumentNumber());
				delegation.setKimTypeId(getKimType().getKimTypeId());
				for(RoleDocumentDelegationMember member: delegation.getMembers()){
					for(RoleDocumentDelegationMemberQualifier qualifier: member.getQualifiers()){
						qualifier.setKimTypId(getKimType().getKimTypeId());
					}
				}
				delegation.setRoleId(roleId);
			}
		}
	}

	private void addDelegationMemberToDelegation(RoleDocumentDelegationMember delegationMember){
		RoleDocumentDelegation delegation;
		if(KEWConstants.DELEGATION_PRIMARY.equals(delegationMember.getDelegationTypeCode())){
			delegation = getPrimaryDelegation();
		} else{
			delegation = getSecondaryDelegation();
		}
		delegationMember.setDelegationId(delegation.getDelegationId());
		delegation.getMembers().add(delegationMember);
	}

	private RoleDocumentDelegation getPrimaryDelegation(){
		RoleDocumentDelegation primaryDelegation = null;
		for(RoleDocumentDelegation delegation: getDelegations()){
			if(delegation.isDelegationPrimary())
				primaryDelegation = delegation;
		}
		if(primaryDelegation==null){
			primaryDelegation = new RoleDocumentDelegation();
			primaryDelegation.setDelegationId(getDelegationId());
			primaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_PRIMARY);
			getDelegations().add(primaryDelegation);
		}
		return primaryDelegation;
	}

	private String getDelegationId(){
		return getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_DLGN_ID_S).toString();
	}
	
	private RoleDocumentDelegation getSecondaryDelegation(){
		RoleDocumentDelegation secondaryDelegation = null;
		for(RoleDocumentDelegation delegation: getDelegations()){
			if(delegation.isDelegationSecondary())
				secondaryDelegation = delegation;
		}
		if(secondaryDelegation==null){
			secondaryDelegation = new RoleDocumentDelegation();
			secondaryDelegation.setDelegationId(getDelegationId());
			secondaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_SECONDARY);
			getDelegations().add(secondaryDelegation);
		}
		return secondaryDelegation;
	}
	
    public ResponsibilityService getResponsibilityService() {
    	if ( responsibilityService == null ) {
    		responsibilityService = KIMServiceLocator.getResponsibilityService();
    	}
		return responsibilityService;
	}

	/**
	 * @return the editing
	 */
	public boolean isEditing() {
		return this.editing;
	}

	/**
	 * @param editing the editing to set
	 */
	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	/**
	 * @return the delegations
	 */
	public List<RoleDocumentDelegation> getDelegations() {
		return this.delegations;
	}

	/**
	 * @param delegations the delegations to set
	 */
	public void setDelegations(List<RoleDocumentDelegation> delegations) {
		this.delegations = delegations;
	}
	
	public void setKimType(KimTypeImpl kimType) {
		super.setKimType(kimType);
		if (kimType != null){
			setRoleTypeId(kimType.getKimTypeId());
			setRoleTypeName(kimType.getName());
		}
	}
}