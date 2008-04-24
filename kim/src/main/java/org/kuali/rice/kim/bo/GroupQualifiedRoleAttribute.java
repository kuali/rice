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

/**
 * Business object that represents a single qualified role attribute record associated with a group.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupQualifiedRoleAttribute extends AbstractQualifiedRoleAttribute {
    private static final long serialVersionUID = 6701917498866245651L;

    private Long groupId;

    private Group group;

    /**
     * @return the groupId
     */
    public Long getGroupId() {
        return this.groupId;
    }

    /**
     * @param groupId
     *            the groupId to set
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * 
     * This method ...
     * 
     * @return Group
     */
    public Group getGroup() {
        return this.group;
    }

    /**
     * 
     * This method ...
     * 
     * @param group
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.bo.AbstractQualifiedRole#toStringMapper()
     */
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = super.toStringMapper();
        propMap.put("group", getGroup().toStringMapper());
        return propMap;
    }
}