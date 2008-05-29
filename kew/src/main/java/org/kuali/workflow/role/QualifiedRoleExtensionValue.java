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

import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_QUAL_ROLE_EXT_VAL_T")
public class QualifiedRoleExtensionValue {

	@Id
	@Column(name="QUAL_ROLE_EXT_VAL_ID")
	private Long qualifiedRoleExtensionValueId;

	@Column(name="QUAL_ROLE_EXT_ID")
	private Long qualifiedRoleExtensionId;
	@Column(name="EXT_KEY")
	private String key;
	@Column(name="EXT_VAL")
	private String value;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="QUAL_ROLE_EXT_ID", insertable=false, updatable=false)
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

