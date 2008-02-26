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
 * Abstract DTO class from which GroupQualifiedRoleDTO and PrincipalQualifiedRoleDTO extend.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class QualifiedRoleDTO implements Serializable {
	private Long roleId;
	
	private RoleDTO roleDto;
	
	/**
     * @return the roleId
     */
    public Long getRoleId() {
        return this.roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * This method ...
     * 
     * @return Role
     */
    public RoleDTO getRoleDto() {
	    return this.roleDto;
	}

    /**
     * This method ...
     * 
     * @param roleDto
     */
	public void setRoleDto(RoleDTO role) {
	    this.roleDto = role;
	}
}