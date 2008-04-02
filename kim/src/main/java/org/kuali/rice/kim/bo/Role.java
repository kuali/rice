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
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.TypedArrayList;

/**
 * Roles represent an aggregation of permissions.  Authorization is given to either a principal or group by attributing a role 
 * to them.  
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class Role extends PersistableBusinessObjectBase {
	private static final long serialVersionUID = -8535955276605020423L;
	private Long id;
	private String name;
	private String description;
	private ArrayList<Group> permissions;
	private ArrayList<Group> groups;
	private ArrayList<Principal> principals;
	
	/**
	 * This constructs a Role instance, primarily constructing necessary TypeArrayLists for the 
	 * maintenance documents.
	 *
	 */
	public Role() {
	    this.permissions = new TypedArrayList(Permission.class);
	    this.groups = new TypedArrayList(Group.class);
	    this.principals = new TypedArrayList(Principal.class);
	}

	/**
	 * This method retrieves the description.
	 * 
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * This method set the description.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * This method retrieves the id for the role.
	 * 
	 * @return
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method sets the id (PK) of the role instance.
	 * 
	 * @param id Long
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method retrieves the name.
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method sets the name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
     * @return the groups
     */
    public ArrayList<Group> getGroups() {
        return this.groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }

    /**
     * @return the principals
     */
    public ArrayList<Principal> getPrincipals() {
        return this.principals;
    }

    /**
     * @param principals the principals to set
     */
    public void setPrincipals(ArrayList<Principal> principals) {
        this.principals = principals;
    }
    
    /**
     * @return the permissions
     */
    public ArrayList<Group> getPermissions() {
        return this.permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(ArrayList<Group> permissions) {
        this.permissions = permissions;
    }

    /**
	 * This overridden method retrieves a string representation of an instance of a Role.
	 * 
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getName());
        propMap.put("description", getDescription());
        return propMap;
	}
}