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

import java.util.HashMap;

/**
 * This is the Data Transfer Object (DTO) that is used for our service layer.
 * 
 * Primarily a helper business object that provides a list of qualified role attributes for 
 * a specific group and role.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupQualifiedRoleDTO extends QualifiedRoleDTO {
	private static final long serialVersionUID = -5188574297122448851L;
	
    private Long groupId;
	
	private GroupDTO groupDto;
	
	private HashMap<String,GroupQualifiedRoleAttributeDTO> qualifiedRoleAttributeDtos;
	
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

    /**
     * This method ...
     * 
     * @return GroupDTO
     */
    public GroupDTO getGroupDto() {
	    return this.groupDto;
	}

    /**
     * This method ...
     * 
     * @param groupDto
     */
	public void setGroupDto(GroupDTO groupDto) {
	    this.groupDto = groupDto;
	}
	
    /**
     * @return the qualifiedRoleAttributeDtos
     */
    public HashMap<String,GroupQualifiedRoleAttributeDTO> getQualifiedRoleAttributeDtos() {
        return this.qualifiedRoleAttributeDtos;
    }

    /**
     * @param qualifiedRoleAttributeDtos the qualifiedRoleAttributes to set
     */
    public void setQualifiedRoleAttributes(HashMap<String,GroupQualifiedRoleAttributeDTO> qualifiedRoleAttributeDtos) {
        this.qualifiedRoleAttributeDtos = qualifiedRoleAttributeDtos;
    }
}