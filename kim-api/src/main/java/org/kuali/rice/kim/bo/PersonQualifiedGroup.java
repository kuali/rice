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
 *  Joins all group attributes assigned to a person and group. 
 * 
 * @author Phillip Berres (pberres at usc dot edu)
 *
 */
public class PersonQualifiedGroup extends PersistableBusinessObjectBase {
    
    	private static final long serialVersionUID = -701762127134964503L;
	private Person person;
	private Long id;
	private Group group;
	private List<GroupAttribute> groupAttributes;

	public Person getPerson() {
	    return this.person;
	}

	public void setPerson(Person person) {
	    this.person = person;
	}

	public Group getGroup() {
	    return this.group;
	}

	public void setGroup(Group group) {
	    this.group = group;
	}

	public List<GroupAttribute> getGroupAttributes() {
	    return this.groupAttributes;
	}

	public void setGroupAttributes(List<GroupAttribute> groupAttributes) {
	    this.groupAttributes = groupAttributes;
	}
    
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
	    propMap.put("id", getId());
	    propMap.put("person", getPerson());
	    propMap.put("group", getGroup());
	    propMap.put("groupAttributes", getGroupAttributes());
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