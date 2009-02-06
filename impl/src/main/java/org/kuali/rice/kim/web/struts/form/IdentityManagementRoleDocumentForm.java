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
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleQualifier;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegation;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementRoleDocumentForm extends KualiTransactionalDocumentFormBase {
	{
		requiredNonEditableProperties.add("methodToCall");
	}
	
	private KimDocumentRoleMember member;
	{
		member = new KimDocumentRoleMember();
		member.getQualifiers().add(new KimDocumentRoleQualifier());
	}
	private KimDocumentRolePermission permission;
	private KimDocumentRoleResponsibility responsibility;
	private RoleDocumentDelegation delegation; 

    
    public IdentityManagementRoleDocumentForm() {
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

    @Override
	public void populate(HttpServletRequest request) {
		super.populate(request);
	}

	public IdentityManagementRoleDocument getRoleDocument() {
        return (IdentityManagementRoleDocument) this.getDocument();
    }

	/**
	 * @return the delegation
	 */
	public RoleDocumentDelegation getDelegation() {
		return this.delegation;
	}

	/**
	 * @param delegation the delegation to set
	 */
	public void setDelegation(RoleDocumentDelegation delegation) {
		this.delegation = delegation;
	}

	/**
	 * @return the member
	 */
	public KimDocumentRoleMember getMember() {
		return this.member;
	}

	/**
	 * @param member the member to set
	 */
	public void setMember(KimDocumentRoleMember member) {
		this.member = member;
	}

	/**
	 * @return the permission
	 */
	public KimDocumentRolePermission getPermission() {
		return this.permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(KimDocumentRolePermission permission) {
		this.permission = permission;
	}

	/**
	 * @return the responsibility
	 */
	public KimDocumentRoleResponsibility getResponsibility() {
		return this.responsibility;
	}

	/**
	 * @param responsibility the responsibility to set
	 */
	public void setResponsibility(KimDocumentRoleResponsibility responsibility) {
		this.responsibility = responsibility;
	}

	public String getMemberFieldConversions(){
		if(member==null)
			return "";
		String memberTypeCode = member.getMemberTypeCode();
		if(MemberTypeValuesFinder.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
			return "principalId:member.memberId,principalName:member.memberName";
		else if(MemberTypeValuesFinder.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
			return "roleId:member.memberId,roleName:member.memberName";
		else if(MemberTypeValuesFinder.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
			return "groupId:member.memberId,groupName:member.memberName";
		return "";
	}
	
	public String getMemberBusinessObjectName(){
		if(member==null)
			return "";
		String memberTypeCode = member.getMemberTypeCode();
		if(MemberTypeValuesFinder.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode))
			return PersonImpl.class.getName();
		else if(MemberTypeValuesFinder.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode))
			return KimRoleImpl.class.getName();
		else if(MemberTypeValuesFinder.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode))
			return KimGroupImpl.class.getName();
		return "";
	}

}
