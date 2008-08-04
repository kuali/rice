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
 * This class represents a Role instance in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleDTO implements Serializable {
    private static final long serialVersionUID = -3710722671286677124L;

    private Long id;
	private String name;
	private String description;

	private HashMap<String, PermissionDTO> permissions;
	private HashMap<String, GroupDTO> groups = new HashMap<String, GroupDTO>();
	private HashMap<String, PrincipalDTO> principals = new HashMap<String, PrincipalDTO>();
	private HashMap<String, RoleAttributeDTO> roles = new HashMap<String, RoleAttributeDTO>();

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
     * @return the permissions
     */
    public HashMap<String, PermissionDTO> getPermissions() {
        return this.permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(HashMap<String, PermissionDTO> permissions) {
        this.permissions = permissions;
    }

    /**
     * @return the groups
     */
    public HashMap<String, GroupDTO> getGroups() {
        return this.groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(HashMap<String, GroupDTO> groups) {
        this.groups = groups;
    }

    /**
     * @return the principals
     */
    public HashMap<String, PrincipalDTO> getPrincipals() {
        return this.principals;
    }

    /**
     * @param principals the principals to set
     */
    public void setPrincipals(HashMap<String, PrincipalDTO> principals) {
        this.principals = principals;
    }

    /**
     * @return the roles
     */
    public HashMap<String, RoleAttributeDTO> getRoles() {
        return this.roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(HashMap<String, RoleAttributeDTO> roles) {
        this.roles = roles;
    }
}
