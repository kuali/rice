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

import java.sql.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.util.jaxb.SqlDateAdapter;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DelegateMemberCompleteInfo extends DelegateInfo {
	
	private static final long serialVersionUID = 1L;
	
	protected String roleId;
	protected String delegationMemberId;
	protected String memberName;
	protected String memberNamespaceCode;
	
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
	 * @return the delegationMemberId
	 */
	public String getDelegationMemberId() {
		return this.delegationMemberId;
	}

	/**
	 * @param delegationMemberId the delegationMemberId to set
	 */
	public void setDelegationMemberId(String delegationMemberId) {
		this.delegationMemberId = delegationMemberId;
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

}
