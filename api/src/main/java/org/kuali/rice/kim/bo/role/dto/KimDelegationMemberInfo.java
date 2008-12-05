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

import java.io.Serializable;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimDelegationMemberInfo implements Serializable {
	
	protected String memberId;
	protected String memberTypeCode;
	protected String delegationMemberId;
	protected String delegationId;	
	protected AttributeSet qualifier;
	
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
	public String getDelegationMemberId() {
		return this.delegationMemberId;
	}
	public void setDelegationMemberId(String delegationMemberId) {
		this.delegationMemberId = delegationMemberId;
	}
	public String getDelegationId() {
		return this.delegationId;
	}
	public void setDelegationId(String delegationId) {
		this.delegationId = delegationId;
	}
	public AttributeSet getQualifier() {
		return this.qualifier;
	}
	public void setQualifier(AttributeSet qualifier) {
		this.qualifier = qualifier;
	}
	
	
}
