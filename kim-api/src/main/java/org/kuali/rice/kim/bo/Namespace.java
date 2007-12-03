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

public class Namespace extends PersistableBusinessObjectBase {
    	private static final long serialVersionUID = 9118112248900436184L;
	private Long id;
	private String name;
	private String description;
	private List<NamespaceDefaultAttribute> defaultAttributes;
	private List<PersonAttribute> personAttributes;
	private List<Permission> permissions;
	
	public Long getId() {
	    return this.id;
	}

	public void setId(Long id) {
	    this.id = id;
	}

	public String getName() {
	    return this.name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public String getDescription() {
	    return this.description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public List<NamespaceDefaultAttribute> getDefaultAttributes() {
	    return this.defaultAttributes;
	}

	public void setDefaultAttributes(List<NamespaceDefaultAttribute> defaultAttributes) {
	    this.defaultAttributes = defaultAttributes;
	}

	public List<PersonAttribute> getPersonAttributes() {
	    return this.personAttributes;
	}

	public void setPersonAttributes(List<PersonAttribute> personAttributes) {
	    this.personAttributes = personAttributes;
	}

	public List<Permission> getPermissions() {
	    return this.permissions;
	}

	public void setPermissions(List<Permission> permissions) {
	    this.permissions = permissions;
	}

	protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getName());
        propMap.put("description", getDescription());
        propMap.put("defaultAttributes", getDefaultAttributes());
        propMap.put("personAttributes", getPersonAttributes());
        propMap.put("permissions", getPermissions());
        return propMap;
	}

	public void refresh() {
		// not doing this unless we need it
	}
}
