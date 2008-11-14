/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.role.dto;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleMembershipInfo {
	protected String principalId;
	protected String groupId;
	protected String roleId;
	protected String memberRoleId;
	protected String roleMemberId;
	protected AttributeSet qualifier;
	protected List<String> delegationIds = new ArrayList<String>();
	protected List<DelegateInfo> delegates = new ArrayList<DelegateInfo>();
	
	public RoleMembershipInfo(String principalId, String groupId, String memberRoleId, String roleId, String roleMemberId,
			AttributeSet qualifier) {
		super();
		this.principalId = principalId;
		this.groupId = groupId;
		this.roleId = roleId;
		this.memberRoleId = memberRoleId;
		this.roleMemberId = roleMemberId;
		this.qualifier = qualifier;
	}
	
	
	public String getPrincipalId() {
		return this.principalId;
	}
	public void setPrincipalId(String principalId) {
		this.principalId = principalId;
	}
	public String getGroupId() {
		return this.groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public AttributeSet getQualifier() {
		return this.qualifier;
	}
	public void setQualifier(AttributeSet qualifier) {
		this.qualifier = qualifier;
	}
	public List<DelegateInfo> getDelegates() {
		return this.delegates;
	}
	public void setDelegates(List<DelegateInfo> delegates) {
		this.delegates = delegates;
	}


	public List<String> getDelegationIds() {
		return this.delegationIds;
	}


	public void setDelegationIds(List<String> delegationIds) {
		this.delegationIds = delegationIds;
	}


	public String getMemberRoleId() {
		return this.memberRoleId;
	}


	public void setMemberRoleId(String memberRoleId) {
		this.memberRoleId = memberRoleId;
	}


	public String getRoleMemberId() {
		return this.roleMemberId;
	}


	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}
	
	
}
