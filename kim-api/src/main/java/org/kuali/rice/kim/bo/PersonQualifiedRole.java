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
 *  Joins all role attributes assigned to a person and role. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonQualifiedRole extends QualifiedRole {

    	private static final long serialVersionUID = -3834313283054550673L;
    	private Long id;
	private Person person;
	private Role role;
	private List<RoleAttribute> roleAttributes;

	public Person getPerson() {
	    return this.person;
	}

	public void setPerson(Person person) {
	    this.person = person;
	}

	public Role getRole() {
	    return this.role;
	}

	public void setRole(Role role) {
	    this.role = role;
	}

	public List<RoleAttribute> getRoleAttributes() {
	    return this.roleAttributes;
	}

	public void setRoleAttributes(List<RoleAttribute> roleAttributes) {
	    this.roleAttributes = roleAttributes;
	}

	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
	    propMap.put("id", getId());
	    propMap.put("person", getPerson());
	    propMap.put("role", getRole());
	    propMap.put("roleAttributes", getRoleAttributes());
	    return propMap;
	}

	public void refresh() {
		// not going to do this unless needed
	}

	public Long getId() {
	    return this.id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

}
