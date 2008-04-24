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
package org.kuali.rice.kim.bo;

import java.util.LinkedHashMap;

import org.kuali.rice.kim.dto.RoleAttributeDTO;

/**
 * This class represents arbitrary attributes that can be attached to roles.  For example, you could use
 * this feature to have basic key value attributes attached to a Role.
 *
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 */
public class RoleAttribute extends AbstractAttributeBase {

	private static final long serialVersionUID = -2255690191635455239L;
	private Long roleId;

	private Role role;

	/**
     * @return the role
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

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

	protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("roleId", getRoleId());
        propMap.put("roleName", getRole().getName());
        propMap.put("attributeTypeId", getAttributeTypeId());
        propMap.put("attributeName", getAttributeName());
        propMap.put("value", getValue());
        return propMap;
	}

	public void refresh() {
		// not going to add unless needed
	}

	/**
	 *
	 * This method creates a DTO from a BO
	 *
	 * @param role
	 * @return RoleDTO
	 */
	public static RoleAttributeDTO toDTO(final RoleAttribute role) {
	    final RoleAttributeDTO dto = new RoleAttributeDTO();

	    return dto;
	}
}
