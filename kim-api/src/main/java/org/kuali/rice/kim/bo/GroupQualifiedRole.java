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
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Business object that expresses the join of a given KIM Role and Group. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupQualifiedRole extends QualifiedRole {
	private static final long serialVersionUID = 6701917498866245651L;
	private Long id;
	private Long roleId;
	private Long groupId;
	private String attributeName;
	private String attributeValue;
	public Long getId() {
	    return this.id;
	}
	public void setId(Long id) {
	    this.id = id;
	}
	public Long getRoleId() {
	    return this.roleId;
	}
	public void setRoleId(Long roleId) {
	    this.roleId = roleId;
	}
	public Long getGroupId() {
	    return this.groupId;
	}
	public void setGroupId(Long groupId) {
	    this.groupId = groupId;
	}
	public String getAttributeName() {
	    return this.attributeName;
	}
	public void setAttributeName(String attributeName) {
	    this.attributeName = attributeName;
	}
	public String getAttributeValue() {
	    return this.attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
	    this.attributeValue = attributeValue;
	}
	protected LinkedHashMap toStringMapper() {
	        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
	        propMap.put("id", getId());
	        propMap.put("roleId", getRoleId());
	        propMap.put("groupId", getGroupId());
	        propMap.put("attributeName", getAttributeName());
	        propMap.put("attributeValue", getAttributeValue());
	        return propMap;
		}

}
