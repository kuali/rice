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

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class QualifiedRoleExtensionValue {

	private Long qualifiedRoleExtensionValueId;

	private Long qualifiedRoleExtensionId;
	private String key;
	private String value;
	private Integer lockVerNbr;

	private QualifiedRoleExtension extension;

	public QualifiedRoleExtension getExtension() {
		return extension;
	}

	public void setExtension(QualifiedRoleExtension extension) {
		this.extension = extension;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

	public Long getQualifiedRoleExtensionValueId() {
		return qualifiedRoleExtensionValueId;
	}

	public void setQualifiedRoleExtensionValueId(Long qualifiedRoleExtensionValueId) {
		this.qualifiedRoleExtensionValueId = qualifiedRoleExtensionValueId;
	}

	public Long getQualifiedRoleExtensionId() {
		return qualifiedRoleExtensionId;
	}

	public void setQualifiedRoleExtensionId(Long qualifiedRoleExtentionId) {
		this.qualifiedRoleExtensionId = qualifiedRoleExtentionId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
