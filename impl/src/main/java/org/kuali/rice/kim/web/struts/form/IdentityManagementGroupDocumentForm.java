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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.bo.options.MemberTypeValuesFinder;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.GroupDocumentMember;
import org.kuali.rice.kim.document.IdentityManagementGroupDocument;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementGroupDocumentForm extends KualiTransactionalDocumentFormBase {
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
        this.setDocument(new IdentityManagementRoleDocument());
    }

    /*
     * Reset method - reset attributes of form retrieved from session otherwise
     * we will always call docHandler action
     * @param mapping
     * @param request
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
    	super.reset(mapping, request);
    	this.setMethodToCall(null);
        this.setRefreshCaller(null);
        this.setAnchor(null);
        this.setCurrentTabIndex(0);
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
		if(MemberTypeValuesFinder.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
			return "principalId:member.memberId,principalName:member.memberName";
		else if(MemberTypeValuesFinder.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
			return "roleId:member.memberId,roleName:member.memberName";
		else if(MemberTypeValuesFinder.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
			return "groupId:member.memberId,groupName:member.memberName";
		return "";
	}

	private String getMemberBusinessObjectName(String memberTypeCode){
		if(MemberTypeValuesFinder.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
			return PersonImpl.class.getName();
		else if(MemberTypeValuesFinder.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
			return KimRoleImpl.class.getName();
		else if(MemberTypeValuesFinder.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
			return KimGroupImpl.class.getName();
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