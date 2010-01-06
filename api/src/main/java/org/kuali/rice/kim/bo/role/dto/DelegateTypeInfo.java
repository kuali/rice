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

import java.io.Serializable;
import java.util.List;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DelegateTypeInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String roleId;
	protected String delegationId;
	protected String delegationTypeCode;
	protected String kimTypeId;
	protected List<DelegateMemberCompleteInfo> members;
	protected Boolean active;
	//protected KimTypeInfo kimType;
	
	/**
	 * @return the kimTypeId
	 */
	public String getKimTypeId() {
		return this.kimTypeId;
	}

	/**
	 * @param kimTypeId the kimTypeId to set
	 */
	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return this.active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * 
	 */
	public DelegateTypeInfo() {
	}
	
	public DelegateTypeInfo(String roleId, String delegationId, String delegationTypeCode, List<DelegateMemberCompleteInfo> members) {
		this.roleId = roleId;
		this.delegationId = delegationId;
		this.delegationTypeCode = delegationTypeCode;
		this.members = members;
	}
	
	public String getDelegationTypeCode() {
		return this.delegationTypeCode;
	}
	public void setDelegationTypeCode(String delegationTypeCode) {
		this.delegationTypeCode = delegationTypeCode;
	}
	public String getDelegationId() {
		return this.delegationId;
	}

	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}

	/**
	 * @return the roleId
	 */
	public String getRoleId() {
		return this.roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the members
	 */
	public List<DelegateMemberCompleteInfo> getMembers() {
		return this.members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(List<DelegateMemberCompleteInfo> members) {
		this.members = members;
	}
	
	
}
