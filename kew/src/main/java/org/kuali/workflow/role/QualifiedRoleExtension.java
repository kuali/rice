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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_QUAL_ROLE_EXT_T")
public class QualifiedRoleExtension {

	@Id
	@Column(name="QUAL_ROLE_EXT_ID")
	private Long qualifiedRoleExtensionId;

	@Column(name="QUAL_ROLE_ID")
	private Long qualifiedRoleId;
	@Column(name="ROLE_ATTRIB_ID")
	private Long roleAttributeId;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="QUAL_ROLE_ID", insertable=false, updatable=false)
	private QualifiedRole qualifiedRole;
	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="ROLE_ATTRIB_ID", insertable=false, updatable=false)
	private RoleAttribute roleAttribute;
    @Transient
	private List<QualifiedRoleExtensionValue> extensionValues = new ArrayList<QualifiedRoleExtensionValue>();

	public List<QualifiedRoleExtensionValue> getExtensionValues() {
		return extensionValues;
	}
	public void setExtensionValues(List<QualifiedRoleExtensionValue> extensionValues) {
		this.extensionValues = extensionValues;
	}
	public Integer getLockVerNbr() {
		return lockVerNbr;
	}
	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}
	public QualifiedRole getQualifiedRole() {
		return qualifiedRole;
	}
	public void setQualifiedRole(QualifiedRole qualifiedRole) {
		this.qualifiedRole = qualifiedRole;
	}
	public Long getQualifiedRoleExtensionId() {
		return qualifiedRoleExtensionId;
	}
	public void setQualifiedRoleExtensionId(Long qualifiedRoleExtensionId) {
		this.qualifiedRoleExtensionId = qualifiedRoleExtensionId;
	}
	public Long getQualifiedRoleId() {
		return qualifiedRoleId;
	}
	public void setQualifiedRoleId(Long qualifiedRoleId) {
		this.qualifiedRoleId = qualifiedRoleId;
	}
	public RoleAttribute getRoleAttribute() {
		return roleAttribute;
	}
	public void setRoleAttribute(RoleAttribute roleAttribute) {
		this.roleAttribute = roleAttribute;
	}
	public Long getRoleAttributeId() {
		return roleAttributeId;
	}
	public void setRoleAttributeId(Long roleAttributeId) {
		this.roleAttributeId = roleAttributeId;
	}

}

