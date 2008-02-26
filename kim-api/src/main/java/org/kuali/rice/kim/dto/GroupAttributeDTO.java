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

/**
 * This is a Data Transfer Object (DTO) that is used by the service layer.
 * 
 * An instance of this class represents a single meta-data attribute associated with a Group in the system. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupAttributeDTO implements Serializable {
	private static final long serialVersionUID = 6461027264149246830L;
	
    private Long id;
	private Long groupId;
	private Long attributeTypeId;
	private String attributeName;
	private String value;
	
    /**
     * @return the groupId
     */
    public Long getGroupId() {
        return this.groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Long getAttributeTypeId() {
		return attributeTypeId;
	}

	public void setAttributeTypeId(Long attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}