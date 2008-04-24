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

import org.kuali.rice.kim.dto.GroupAttributeDTO;

/**
 * This class represents arbitrary attributes that can be attached to groups.  For example, you could use
 * this feature to have basic key value attributes attached to a Group.  More specifically, you could create a
 * group, but then want to store a group phone number or group address.  By adding arbitrary group attributes to this,
 * you could handle all of this.
 *
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 */
public class GroupAttribute extends AbstractAttributeBase {
	private static final long serialVersionUID = 5512700461635442326L;

    private Long groupId;
	private Group group;

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

    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("groupId", getGroupId());
        propMap.put("groupName", getGroup().getName());
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
	 * This method creates a DTO of the BO
	 *
	 * @param GroupAttribute
	 * @return GroupAttributeDTO
	 */
	public static GroupAttributeDTO toDTO(final GroupAttribute ga) {
	    final GroupAttributeDTO dto = new GroupAttributeDTO();
	    AbstractAttributeBase.fillInDTO(dto, ga);
	    dto.setGroupId(ga.getGroupId());
	    return dto;
	}
}
