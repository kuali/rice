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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.kuali.core.util.TypedArrayList;
import org.kuali.rice.kim.dto.GroupDTO;
import org.kuali.rice.kim.dto.GroupQualifiedRoleAttributeDTO;
import org.kuali.rice.kim.dto.GroupQualifiedRoleDTO;
import org.kuali.rice.kim.dto.RoleDTO;

/**
 * Primarily a helper business object that provides a list of qualified role attributes for
 * a specific group and role.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupQualifiedRole extends Group {
	private static final long serialVersionUID = 6701917498866245651L;

	private Long groupId;
	private Long roleId;

	private Group group;
	private Role role;

	private ArrayList<GroupQualifiedRoleAttribute> qualifiedRoleAttributes;

    public GroupQualifiedRole() {
        super();
        this.qualifiedRoleAttributes = new TypedArrayList(GroupQualifiedRoleAttribute.class);
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

    /**
     * @return the qualifiedRoleAttributes
     */
    public ArrayList<GroupQualifiedRoleAttribute> getQualifiedRoleAttributes() {
        return this.qualifiedRoleAttributes;
    }

    /**
     * @param qualifiedRoleAttributes the qualifiedRoleAttributes to set
     */
    public void setQualifiedRoleAttributes(ArrayList<GroupQualifiedRoleAttribute> qualifiedRoleAttributes) {
        this.qualifiedRoleAttributes = qualifiedRoleAttributes;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = super.toStringMapper();
        propMap.put("roleId", getRoleId());

        return propMap;
    }

    public static GroupQualifiedRoleDTO toDTO(final GroupQualifiedRole groupQualifiedRole) {
        final GroupQualifiedRoleDTO dto = new GroupQualifiedRoleDTO();

        final GroupDTO groupDto = new GroupDTO();
        Group.fillInDTO((Group)groupQualifiedRole, groupDto, true);
        dto.setGroupDto(groupDto);
        dto.setGroupId(groupQualifiedRole.getGroupId());

        final RoleDTO roleDto = new RoleDTO();
        Role.fillInDTO(groupQualifiedRole.getRole(), roleDto, true);
        dto.setRoleDto(roleDto);
        dto.setRoleId(groupQualifiedRole.getRoleId());


        final HashMap<String, GroupQualifiedRoleAttributeDTO> qualifiedRoleAttributeDtos = new HashMap<String, GroupQualifiedRoleAttributeDTO>();
        for (GroupQualifiedRoleAttribute attr : groupQualifiedRole.getQualifiedRoleAttributes()) {
        	qualifiedRoleAttributeDtos.put(attr.getAttributeName(), GroupQualifiedRoleAttribute.toDTO(attr));
        }
        dto.setQualifiedRoleAttributes(qualifiedRoleAttributeDtos);
        return dto;
    }

    public static GroupQualifiedRoleDTO toDTO(final GroupQualifiedRoleAttribute groupQualifiedRoleAttribute) {
        final GroupQualifiedRoleDTO dto = new GroupQualifiedRoleDTO();
        dto.setRoleId(groupQualifiedRoleAttribute.getRoleId());
        final RoleDTO roleDto = new RoleDTO();
        Role.fillInDTO(groupQualifiedRoleAttribute.getRole(), roleDto, true);
        dto.setRoleDto(roleDto);

        dto.setGroupId(groupQualifiedRoleAttribute.getGroupId());
        final GroupDTO groupDto = new GroupDTO();
        Group.fillInDTO(groupQualifiedRoleAttribute.getGroup(), groupDto, true);
        dto.setGroupDto(groupDto);

        final HashMap<String, GroupQualifiedRoleAttributeDTO> gqra = new HashMap<String, GroupQualifiedRoleAttributeDTO>();


        // TODO GNL fix up
        return dto;
    }

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
	 * @return the group
	 */
	public Group getGroup() {
		return this.group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

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
}
