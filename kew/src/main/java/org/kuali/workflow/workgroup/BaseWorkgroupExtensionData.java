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
package org.kuali.workflow.workgroup;

import org.kuali.workflow.attribute.ExtensionData;

import edu.iu.uis.eden.BaseWorkflowPersistable;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseWorkgroupExtensionData extends BaseWorkflowPersistable implements ExtensionData {

	private static final long serialVersionUID = 840124314333807703L;

	private Long workgroupExtensionDataId;

	private String key;
	private String value;
	private Integer lockVerNbr;

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
