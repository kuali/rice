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

import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.BaseWorkflowPersistable;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupType extends BaseWorkflowPersistable {

	private static final long serialVersionUID = 7394951204472441349L;

	private Long workgroupTypeId;
	private String name;
	private String label;
	private String description;
	private String documentTypeName;
	private Boolean active = true;
	private Integer lockVerNbr;

	private List<WorkgroupTypeAttribute> attributes = new ArrayList<WorkgroupTypeAttribute>();

	public String getDocumentTypeName() {
		return documentTypeName;
	}

	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<WorkgroupTypeAttribute> getActiveAttributes() {
		List<WorkgroupTypeAttribute> activeAttributes = new ArrayList<WorkgroupTypeAttribute>();
		for (WorkgroupTypeAttribute attribute : getAttributes()) {
			if (attribute.getActive()) {
				activeAttributes.add(attribute);
			}
		}
		return activeAttributes;
	}

	public List<WorkgroupTypeAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<WorkgroupTypeAttribute> attributes) {
		this.attributes = attributes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getLockVerNbr() {
		return lockVerNbr;
	}

	public void setLockVerNbr(Integer lockVerNbr) {
		this.lockVerNbr = lockVerNbr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getWorkgroupTypeId() {
		return workgroupTypeId;
	}

	public void setWorkgroupTypeId(Long workgroupTypeId) {
		this.workgroupTypeId = workgroupTypeId;
	}

}
