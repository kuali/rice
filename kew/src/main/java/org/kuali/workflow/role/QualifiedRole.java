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

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QualifiedRole implements java.io.Serializable {

	private Long qualifiedRoleId;

	private Long roleId;
	private boolean active;
	private String description;
	private Long documentId;
	private Timestamp fromDate;
	private Timestamp toDate;
	private Timestamp activationDate;
	private Timestamp deactivationDate;
	private Integer versionNumber;
	private boolean current;
	private Long previousVersionId;
	private Integer lockVerNbr;

	private Role role;
	private List<QualifiedRoleMember> members = new ArrayList<QualifiedRoleMember>();
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
