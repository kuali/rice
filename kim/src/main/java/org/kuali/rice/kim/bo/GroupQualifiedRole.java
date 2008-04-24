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

import java.util.List;

/**
 * Primarily a helper business object that provides a list of qualified role attributes for 
 * a specific group and role.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupQualifiedRole extends AbstractQualifiedRole {
	private static final long serialVersionUID = 6701917498866245651L;
	
	private Long groupId;
	
	private Group group;
	
	private List<GroupQualifiedRoleAttribute> qualifiedRoleAttributes;
	
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
     * @return Group
     */
    public Group getGroup() {
	    return this.group;
	}

    /**
     * This method ...
     * 
     * @param group
     */
	public void setGroup(Group group) {
	    this.group = group;
	}
	
    /**
     * @return the qualifiedRoleAttributes
     */
    public List<GroupQualifiedRoleAttribute> getQualifiedRoleAttributes() {
        return this.qualifiedRoleAttributes;
    }

    /**
     * @param qualifiedRoleAttributes the qualifiedRoleAttributes to set
     */
    public void setqualifiedRoleAttributes(List<GroupQualifiedRoleAttribute> qualifiedRoleAttributes) {
        this.qualifiedRoleAttributes = qualifiedRoleAttributes;
    }
}