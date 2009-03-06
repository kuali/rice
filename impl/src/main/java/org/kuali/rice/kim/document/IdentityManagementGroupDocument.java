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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.bo.ui.GroupDocumentQualifier;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.util.TypedArrayList;


/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementGroupDocument extends IdentityManagementTypeAttributeTransactionalDocument {

	// principal data
	protected String groupId;
	protected String groupTypeId;
	protected String groupTypeName;
	protected String groupNamespace;
	protected String groupName;
	protected boolean active = true;

	protected boolean editing;
	
	private List<GroupDocumentMember> members = new TypedArrayList(GroupDocumentMember.class);
	private List<GroupDocumentQualifier> qualifiers = new TypedArrayList(GroupDocumentQualifier.class);
	
	public IdentityManagementGroupDocument() {
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
	 * @param groupId the groupId to set
	 */
	public void setRoleId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @param members the members to set
	 */
	public void addMember(GroupDocumentMember member) {
       	getMembers().add(member);
	}

	/**
	 * @param members the members to set
	 */
	public GroupDocumentMember getBlankMember() {
		GroupDocumentMember member = new GroupDocumentMember();
       	return member;
	}
    
    private void setAttrDefnIdForDelMemberQualifier(GroupDocumentQualifier qualifier,AttributeDefinition definition) {
    	if (definition instanceof KimDataDictionaryAttributeDefinition) {
    		qualifier.setKimAttributeId(((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		//qualifier.refreshReferenceObject("kimAttribute");
    	} else {
    		qualifier.setKimAttributeId(((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId());
    		//qualifier.refreshReferenceObject("kimAttribute");

    	}
    }
    
	/**
	 * @see org.kuali.rice.kns.document.DocumentBase#handleRouteStatusChange()
	 */
	@Override
	public void handleRouteStatusChange() {
		super.handleRouteStatusChange();
		if (getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
			//KIMServiceLocator.getUiDocumentService().saveGroup(this);
		}
	}
	
	@Override
	public void prepareForSave(){
		String groupId;
		if(StringUtils.isBlank(getGroupId())){
			groupId = getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_ID_S").toString();
			setGroupId(groupId);
		} else
			groupId = getGroupId();
		if(getMembers()!=null){
			String groupMemberId;
			for(GroupDocumentMember member: getMembers()){
				member.setGroupId(groupId);
				if(StringUtils.isBlank(member.getGroupMemberId())){
					groupMemberId = getSequenceAccessorService().getNextAvailableSequenceNumber("KRIM_ROLE_MBR_ID_S").toString();
					member.setGroupMemberId(groupMemberId);
				}
				/*for(KimDocumentGroupQualifier qualifier: member.getQualifiers()){
					qualifier.setKimTypId(getKimType().getKimTypeId());
				}*/
			}
		}
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return this.groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return this.groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the groupNamespace
	 */
	public String getGroupNamespace() {
		return this.groupNamespace;
	}

	/**
	 * @param groupNamespace the groupNamespace to set
	 */
	public void setGroupNamespace(String groupNamespace) {
		this.groupNamespace = groupNamespace;
	}

	/**
	 * @return the groupTypeId
	 */
	public String getGroupTypeId() {
		return this.groupTypeId;
	}

	/**
	 * @param groupTypeId the groupTypeId to set
	 */
	public void setGroupTypeId(String groupTypeId) {
		this.groupTypeId = groupTypeId;
	}

	/**
	 * @return the groupTypeName
	 */
	public String getGroupTypeName() {
		return this.groupTypeName;
	}

	/**
	 * @param groupTypeName the groupTypeName to set
	 */
	public void setGroupTypeName(String groupTypeName) {
		this.groupTypeName = groupTypeName;
	}

	/**
	 * @return the members
	 */
	public List<GroupDocumentMember> getMembers() {
		return this.members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<GroupDocumentMember> members) {
		this.members = members;
	}

	/**
	 * @return the qualifiers
	 */
	public List<GroupDocumentQualifier> getQualifiers() {
		return this.qualifiers;
	}

	/**
	 * @param qualifiers the qualifiers to set
	 */
	public void setQualifiers(List<GroupDocumentQualifier> qualifiers) {
		this.qualifiers = qualifiers;
	}

}