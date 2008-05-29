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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_QUAL_ROLE_T")
public class QualifiedRole implements java.io.Serializable {

	@Id
	@Column(name="QUAL_ROLE_ID")
	private Long qualifiedRoleId;

	@Column(name="ROLE_ID")
	private Long roleId;
	@Column(name="ACTV_IND")
	private boolean active;
	@Column(name="QUAL_ROLE_DESC")
	private String description;
	@Column(name="DOC_HDR_ID")
	private Long documentId;
	//@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FRM_DT")
	private Timestamp fromDate;
	//@Temporal(TemporalType.TIMESTAMP)
	@Column(name="TO_DT")
	private Timestamp toDate;
	//@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ACTVN_DT")
	private Timestamp activationDate;
	//@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DACTVN_DT")
	private Timestamp deactivationDate;
	@Column(name="VER_NBR")
	private Integer versionNumber;
	@Column(name="CUR_IND")
	private boolean current;
	@Column(name="PREV_VER_ID")
	private Long previousVersionId;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="ROLE_ID", insertable=false, updatable=false)
	private Role role;
    @Transient
	private List<QualifiedRoleMember> members = new ArrayList<QualifiedRoleMember>();
    @Transient
	private List<QualifiedRoleExtension> extensions = new ArrayList<QualifiedRoleExtension>();

	public Timestamp getActivationDate() {
		return activationDate;
	}
	public void setActivationDate(Timestamp activationDate) {
		this.activationDate = activationDate;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isCurrent() {
		return current;
	}
	public void setCurrent(boolean current) {
		this.current = current;
	}
	public Timestamp getDeactivationDate() {
		return deactivationDate;
	}
	public void setDeactivationDate(Timestamp deactivationDate) {
		this.deactivationDate = deactivationDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public List<QualifiedRoleExtension> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<QualifiedRoleExtension> extensions) {
		this.extensions = extensions;
	}
	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public List<QualifiedRoleMember> getMembers() {
		return members;
	}
	public void setMembers(List<QualifiedRoleMember> members) {
		this.members = members;
	}
	public Long getPreviousVersionId() {
		return previousVersionId;
	}
	public void setPreviousVersionId(Long previousVersionId) {
		this.previousVersionId = previousVersionId;
	}
	public Long getQualifiedRoleId() {
		return qualifiedRoleId;
	}
	public void setQualifiedRoleId(Long qualifiedRoleId) {
		this.qualifiedRoleId = qualifiedRoleId;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	public Timestamp getToDate() {
		return toDate;
	}
	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}
	public Integer getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

}

