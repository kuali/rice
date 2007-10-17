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

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QualifiedRoleExtension {

	private Long qualifiedRoleExtensionId;

	private Long qualifiedRoleId;
	private Long roleAttributeId;
	private Integer lockVerNbr;

	private QualifiedRole qualifiedRole;
	private RoleAttribute roleAttribute;
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
