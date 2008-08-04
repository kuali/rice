/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.dto;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This is the Data Transfer Object (DTO) that is used for our service layer.
 *
 * This class represents a single GroupType entity instance in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupTypeDTO implements Serializable {
    private static final long serialVersionUID = -4727192833249130942L;

	private Long id;
	private String name;
	private String description;
	private String workflowDocumentType;

    private HashMap<String,GroupTypeDefaultAttributeDTO> groupTypeDefaultAttributes;

	public Long getId() {
	    return this.id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public String getName() {
	    return this.name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getDescription() {
	    return this.description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	/**
	 * @return the workflowDocumentType
	 */
	public String getWorkflowDocumentType() {
		return this.workflowDocumentType;
	}

	/**
	 * @param workflowDocumentType the workflowDocumentType to set
	 */
	public void setWorkflowDocumentType(String workflowDocumentType) {
		this.workflowDocumentType = workflowDocumentType;
	}

	/**
	 * @return the groupTypeDefaultAttributes
	 */
	public HashMap<String, GroupTypeDefaultAttributeDTO> getGroupTypeDefaultAttributes() {
		return this.groupTypeDefaultAttributes;
	}

	/**
	 * @param groupTypeDefaultAttributes the groupTypeDefaultAttributes to set
	 */
	public void setGroupTypeDefaultAttributes(
			HashMap<String, GroupTypeDefaultAttributeDTO> groupTypeDefaultAttributes) {
		this.groupTypeDefaultAttributes = groupTypeDefaultAttributes;
	}
}
