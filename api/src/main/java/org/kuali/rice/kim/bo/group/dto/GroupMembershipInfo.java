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
package org.kuali.rice.kim.bo.group.dto;


/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupMembershipInfo {
	protected String groupId;
	protected String groupMemberId;
	protected String memberId;
	protected String memberTypeCode;
	protected Long versionNumber;
	
	public GroupMembershipInfo(String groupId, String groupMemberId, String memberId, String memberTypeCode) {
		super();
		this.groupId = groupId;
		this.memberId = memberId;
		this.memberTypeCode = memberTypeCode;
		this.groupMemberId = groupMemberId;
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


	public String getGroupId() {
		return this.groupId;
	}


	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}


	public String getGroupMemberId() {
		return this.groupMemberId;
	}


	public void setGroupMemberId(String groupMemberId) {
		this.groupMemberId = groupMemberId;
	}


	public Long getVersionNumber() {
		return this.versionNumber;
	}


	public void setVersionNumber(Long versionNumber) {
		this.versionNumber = versionNumber;
	}


}
