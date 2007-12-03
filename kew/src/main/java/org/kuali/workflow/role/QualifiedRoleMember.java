/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package org.kuali.workflow.role;

import org.kuali.workflow.identity.IdentityType;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QualifiedRoleMember {

	private Long qualifiedRoleMemberId;

	private Long qualifiedRoleId;
	private Long responsibilityId;
	private String memberId;
	private IdentityType memberType;
	private Integer lockVerNbr;

	private QualifiedRole qualifiedRole;

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public IdentityType getMemberType() {
		return memberType;
	}

	public void setMemberType(IdentityType memberType) {
		this.memberType = memberType;
	}

	public QualifiedRole getQualifiedRole() {
		return qualifiedRole;
	}

	public void setQualifiedRole(QualifiedRole qualifiedRole) {
		this.qualifiedRole = qualifiedRole;
	}

	public Long getQualifiedRoleId() {
		return qualifiedRoleId;
	}

	public void setQualifiedRoleId(Long qualifiedRoleId) {
		this.qualifiedRoleId = qualifiedRoleId;
	}

	public Long getQualifiedRoleMemberId() {
		return qualifiedRoleMemberId;
	}

	public void setQualifiedRoleMemberId(Long qualifiedRoleMemberId) {
		this.qualifiedRoleMemberId = qualifiedRoleMemberId;
	}

	public Long getResponsibilityId() {
		return responsibilityId;
	}

	public void setResponsibilityId(Long responsibilityId) {
		this.responsibilityId = responsibilityId;
	}

}

