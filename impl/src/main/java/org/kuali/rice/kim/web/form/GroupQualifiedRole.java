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
package org.kuali.rice.kim.web.form;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRoleAttribute;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Primarily a helper business object that provides a list of qualified role attributes for
 * a specific group and role.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupQualifiedRole extends Group {
	private static final long serialVersionUID = 6701917498866245651L;

	private Long roleId;

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
	public void setQualifiedRoleAttributes(
			ArrayList<GroupQualifiedRoleAttribute> qualifiedRoleAttributes) {
		this.qualifiedRoleAttributes = qualifiedRoleAttributes;
	}

	/**
     * This overridden method ...
     *
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = super.toStringMapper();
        propMap.put("roleId", getRoleId());

        return propMap;
    }

}
