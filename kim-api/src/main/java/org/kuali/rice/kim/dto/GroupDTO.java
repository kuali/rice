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
 * This is a Data Transfer Object (DTO) that is used by the service layer.
 * 
 * An instance of this class houses details about a single Group in the system. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupDTO implements Serializable {
	private static final long serialVersionUID = -3582175447178397223L;
	
    private Long id;
	private String name;
	private String description;
	private HashMap<String,GroupDTO> memberGroupDtos;
	private HashMap<String,GroupDTO> parentGroupDtos;
    private HashMap<String,RoleDTO> groupRoleDtos;
    private HashMap<String,GroupAttributeDTO> groupAttributeDtos;
    
	/**
     * @return the memberGroupDtos - the key to the HashMap is the name of the group, the value 
     *                            is the actual DTO object
     */
    public HashMap<String,GroupDTO> getMemberGroupDtos() {
        return this.memberGroupDtos;
    }

    /**
     * @param memberGroupDtos the memberGroupDtos to set - the key to the HashMap is the name of the group, the value 
     *                            is the actual DTO object
     */
    public void setMemberGroupDtos(HashMap<String,GroupDTO> memberGroups) {
        this.memberGroupDtos = memberGroups;
    }

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
     * @return the groupRoleDtos - the key to the HashMap is the name of the role, the value 
     *                             is the actual DTO object
     */
    public HashMap<String,RoleDTO> getGroupRoleDtos() {
        return this.groupRoleDtos;
    }
    /**
     * @param groupRoleDtos the groupRoleDtos to set - the key to the HashMap is the name of the role, the value 
     *                            is the actual DTO object
     */
    public void setGroupRoleDtos(HashMap<String,RoleDTO> groupRoles) {
        this.groupRoleDtos = groupRoles;
    }

    /**
     * @return the parentGroupDtos - the key to the HashMap is the name of the group, the value 
     *                            is the actual DTO object
     */
    public HashMap<String,GroupDTO> getParentGroupDtos() {
        return this.parentGroupDtos;
    }

    /**
     * @param parentGroupDtos the parentGroupDtos to set - the key to the HashMap is the name of the group, the value 
     *                            is the actual DTO object
     */
    public void setParentGroupDtos(HashMap<String,GroupDTO> parentGroups) {
        this.parentGroupDtos = parentGroups;
    }

    /**
     * @return the groupAttributeDtos - the key to the HashMap is the name of the group, the value 
     *                            is the actual DTO object
     */
    public HashMap<String,GroupAttributeDTO> getGroupAttributeDtos() {
        return this.groupAttributeDtos;
    }

    /**
     * @param groupAttributeDtos the groupAttributeDtos to set - the key to the HashMap is the name of the group, the value 
     *                            is the actual DTO object
     */
    public void setGroupAttributeDtos(HashMap<String,GroupAttributeDTO> groupAttributes) {
        this.groupAttributeDtos = groupAttributes;
    }
}
