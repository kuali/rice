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
package org.kuali.rice.kim.web.struts.form;

import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementGroupDocumentForm extends KualiTransactionalDocumentFormBase {
	private static final long serialVersionUID = -107836689162363400L;
	{
		requiredNonEditableProperties.add("methodToCall");
	}
	
	private boolean canAssignGroup = true;
	private KimTypeImpl kimType;
	private GroupDocumentMember member;
	{
		member = new GroupDocumentMember();
	}
    
	public IdentityManagementGroupDocumentForm() {
        super();
        this.setDocument(new IdentityManagementGroupDocument());
    }

	public IdentityManagementGroupDocument getGroupDocument() {
        return (IdentityManagementGroupDocument) this.getDocument();
    }

	public String getMemberFieldConversions(){
		if(member==null)
			return "";
		return getMemberFieldConversions(member.getMemberTypeCode());
	}

	public String getMemberBusinessObjectName(){
		if(member==null)
			return "";
		return getMemberBusinessObjectName(member.getMemberTypeCode());
	}

	private String getMemberFieldConversions(String memberTypeCode){
		if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
			return "principalId:member.memberId,principalName:member.memberName";
		else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
			return "roleId:member.memberId,roleName:member.memberName";
		else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
			return "groupId:member.memberId,groupName:member.memberName";
		return "";
	}

	private String getMemberBusinessObjectName(String memberTypeCode){
		if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
			return PersonImpl.class.getName();
		else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
			return RoleImpl.class.getName();
		else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
			return GroupImpl.class.getName();
		return "";
	}

	/**
	 * @return the kimType
	 */
	public KimTypeImpl getKimType() {
		return this.kimType;
	}

	/**
	 * @param kimType the kimType to set
	 */
	public void setKimType(KimTypeImpl kimType) {
		this.kimType = kimType;
	}

	/**
	 * @return the canAssignGroup
	 */
	public boolean isCanAssignGroup() {
		return this.canAssignGroup;
	}

	/**
	 * @param canAssignGroup the canAssignGroup to set
	 */
	public void setCanAssignGroup(boolean canAssignGroup) {
		this.canAssignGroup = canAssignGroup;
	}

	/**
	 * @return the member
	 */
	public GroupDocumentMember getMember() {
		return this.member;
	}

	/**
	 * @param member the member to set
	 */
	public void setMember(GroupDocumentMember member) {
		this.member = member;
	}

}