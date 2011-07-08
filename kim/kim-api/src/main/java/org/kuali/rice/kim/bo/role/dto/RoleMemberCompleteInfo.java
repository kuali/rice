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
package org.kuali.rice.kim.bo.role.dto;

import org.kuali.rice.krad.dto.InactiveableInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleMemberCompleteInfo extends InactiveableInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String roleMemberId;
	protected String roleId;
	protected Map<String, String> qualifier;
	protected List<RoleResponsibilityActionInfo> roleRspActions;
	protected String memberName;
	protected String memberNamespaceCode;
	protected String memberId;
	protected String memberTypeCode;
	
	public RoleMemberCompleteInfo(){
		
	}
	
	public RoleMemberCompleteInfo(String roleId, String roleMemberId, String memberId, 
			String memberTypeCode, Date activeFromDate, Date activeToDate, Map<String, String> qualifier) {
		super();
		this.roleId = roleId;
		if ( memberId == null ) {
			throw new IllegalArgumentException( "memberId may not be null" );
		}
		this.memberId = memberId;
		if ( memberTypeCode == null ) {
			throw new IllegalArgumentException( "memberTypeCode may not be null" );
		}
		this.memberTypeCode = memberTypeCode;
		this.roleMemberId = roleMemberId;
		this.activeFromDate = activeFromDate;
		this.activeToDate = activeToDate;
		this.qualifier = qualifier;
	}
	
	public String getMemberId() {
		return this.memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberTypeCode() {
		return this.memberTypeCode;
	}

	public void setMemberTypeCode(String memberTypeCode) {
		this.memberTypeCode = memberTypeCode;
	}
	
	public String getRoleMemberId() {
		return this.roleMemberId;
	}
	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}
	public String getRoleId() {
		return this.roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the qualifier
	 */
	public Map<String, String> getQualifier() {
		return this.qualifier;
	}

	/**
	 * @param qualifier the qualifier to set
	 */
	public void setQualifier(Map<String, String> qualifier) {
		this.qualifier = qualifier;
	}

	/**
	 * @return the memberName
	 */
	public String getMemberName() {
		return this.memberName;
	}

	/**
	 * @param memberName the memberName to set
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	/**
	 * @return the memberNamespaceCode
	 */
	public String getMemberNamespaceCode() {
		return this.memberNamespaceCode;
	}

	/**
	 * @param memberNamespaceCode the memberNamespaceCode to set
	 */
	public void setMemberNamespaceCode(String memberNamespaceCode) {
		this.memberNamespaceCode = memberNamespaceCode;
	}

	/**
	 * @return the roleRspActions
	 */
	public List<RoleResponsibilityActionInfo> getRoleRspActions() {
		return this.roleRspActions;
	}

	/**
	 * @param roleRspActions the roleRspActions to set
	 */
	public void setRoleRspActions(List<RoleResponsibilityActionInfo> roleRspActions) {
		this.roleRspActions = roleRspActions;
	}

}
