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

import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DelegateInfo {
	protected String delegationId;
	protected String delegationTypeCode;
	protected String principalId;
	protected String groupId;
	protected AttributeSet qualifier;
	
	public DelegateInfo(String delegationId, String delegationTypeCode) {
		this.delegationId = delegationId;
		this.delegationTypeCode = delegationTypeCode;
	}
	
	public String getDelegationTypeCode() {
		return this.delegationTypeCode;
	}
	public void setDelegationTypeCode(String delegationTypeCode) {
		this.delegationTypeCode = delegationTypeCode;
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
	
	
}
