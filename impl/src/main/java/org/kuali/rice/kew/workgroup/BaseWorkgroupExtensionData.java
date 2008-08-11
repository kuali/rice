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
package org.kuali.rice.kew.workgroup;

import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import org.kuali.rice.kew.attribute.ExtensionData;
import org.kuali.rice.kew.bo.BaseWorkflowPersistable;


/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_WRKGRP_EXT_DTA_T")
public class BaseWorkgroupExtensionData extends BaseWorkflowPersistable implements ExtensionData {

	private static final long serialVersionUID = 840124314333807703L;

	@Id
	@Column(name="WRKGRP_EXT_DTA_ID")
	private Long workgroupExtensionDataId;

	@Column(name="EXT_KEY")
	private String key;
	@Column(name="EXT_VAL")
	private String value;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;

	@OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="WRKGRP_EXT_ID")
	private BaseWorkgroupExtension workgroupExtension;

	public BaseWorkgroupExtension getWorkgroupExtension() {
		return workgroupExtension;
	}

	public void setWorkgroupExtension(BaseWorkgroupExtension extension) {
		this.workgroupExtension = extension;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getWorkgroupExtensionDataId() {
		return workgroupExtensionDataId;
	}

	public void setWorkgroupExtensionDataId(Long workgroupExtensionDataId) {
		this.workgroupExtensionDataId = workgroupExtensionDataId;
	}

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

}

