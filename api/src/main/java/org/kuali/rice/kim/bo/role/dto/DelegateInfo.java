/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kns.dto.InactiveableInfo;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DelegateInfo extends InactiveableInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String delegationId;
	protected String delegationTypeCode;
	protected String memberId;
	protected String memberTypeCode;
	protected AttributeSet qualifier;
	protected String roleMemberId;
		
	/**
	 * 
	 */
	public DelegateInfo() {
	}
	
	public DelegateInfo(String delegationId, String delegationTypeCode, String memberId,
			String memberTypeCode, String roleMemberId, AttributeSet qualifier) {
		this.delegationId = delegationId;
		this.delegationTypeCode = delegationTypeCode;
		this.memberId = memberId;
		this.memberTypeCode = memberTypeCode;
		this.qualifier = qualifier;
		this.roleMemberId = roleMemberId;
	}
	
	public String getDelegationTypeCode() {
		return this.delegationTypeCode;
	}
	public void setDelegationTypeCode(String delegationTypeCode) {
		this.delegationTypeCode = delegationTypeCode;
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

	public AttributeSet getQualifier() {
		return this.qualifier;
	}
	public void setQualifier(AttributeSet qualifier) {
		this.qualifier = qualifier;
	}

	public String getDelegationId() {
		return this.delegationId;
	}

	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}

	/**
	 * @return the roleMemberId
	 */
	public String getRoleMemberId() {
		return this.roleMemberId;
	}

	/**
	 * @param roleMemberId the roleMemberId to set
	 */
	public void setRoleMemberId(String roleMemberId) {
		this.roleMemberId = roleMemberId;
	}
	
}
