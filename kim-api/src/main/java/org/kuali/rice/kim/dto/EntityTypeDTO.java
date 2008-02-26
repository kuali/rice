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
 * This class defines the concept of an entity type.  Person, process, system, or company 
 * could be an entity type in the system.  This is a generic organizing classification.  
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EntityTypeDTO implements Serializable {
    private static final long serialVersionUID = 4985356085446767955L;
    
    private Long id;
	private String name;
	private String description;

	/**
	 * This method retrieves the entity type name.
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the entity type name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * This method retrieves the unique id (primary key) for an entity type instance.
	 * 
	 * @return Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method sets the unique id (primary key) for an entity type instance.
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
	    return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
	    this.description = description;
	}
}