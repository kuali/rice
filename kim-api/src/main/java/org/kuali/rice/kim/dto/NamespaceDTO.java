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
 * This class represents a single Namespace entity instance in the System. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class NamespaceDTO implements Serializable {
	private static final long serialVersionUID = 2733821068998561691L;
	
    private Long id;
	private String name;
	private String description;

    private HashMap<String,NamespaceDefaultAttributeDTO> namespaceDefaultAttributes;

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
	 * @return HashMap - the key is the name of the attribute, the value is the DTO
	 */
    public HashMap<String,NamespaceDefaultAttributeDTO> getNamespaceDefaultAttributes() {
        return this.namespaceDefaultAttributes;
    }

    /**
     * @param namespaceDefaultAttributes - HashMap - the key is the name of the attribute, the value is the DTO
     */
    public void setNamespaceDefaultAttributes(HashMap<String,NamespaceDefaultAttributeDTO> namespaceDefaultAttributes) {
        this.namespaceDefaultAttributes = namespaceDefaultAttributes;
    }
}
