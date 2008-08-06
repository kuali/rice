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
package org.kuali.rice.kew.role;

import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import org.kuali.rice.kew.identity.IdentityType;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_QUAL_ROLE_MBR_T")
public class QualifiedRoleMember {

	@Id
	@Column(name="QUAL_ROLE_MBR_ID")
	private Long qualifiedRoleMemberId;

	@Column(name="QUAL_ROLE_ID")
	private Long qualifiedRoleId;
	@Column(name="RSP_ID")
	private Long responsibilityId;
	@Column(name="MBR_ID")
	private String memberId;
	@Column(name="MBR_TYP")
	private IdentityType memberType;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="QUAL_ROLE_ID", insertable=false, updatable=false)
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


