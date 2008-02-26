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
 * This is the Data Transfer Object (DTO) that is used for our service layer.
 * 
 * Each Entity can have one to many meta-data attributes associated with them.  This DTO represents 
 * one of those. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityAttributeDTO implements Serializable {
	private static final long serialVersionUID = -445101091769342601L;
	
    private Long id;
	private Long attributeTypeId;
	private Long namespaceId;
	private String attributeName;
	private String value;
	private Long entityId;

	public Long getNamespaceId() {
		return namespaceId;
	}

	public void setNamespaceId(Long namespaceId) {
		this.namespaceId = namespaceId;
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

    /**
     * @return the entityId
     */
    public Long getEntityId() {
        return this.entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}