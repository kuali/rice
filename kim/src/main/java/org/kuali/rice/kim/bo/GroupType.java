/*
 * Copyright 2008 The Kuali Foundation
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
 * A GroupType represents a type of Group in the system.  This can be used to drive 
 * different workflows based on different types of group setup.  By default, the system
 * comes with a "Default" group type that has no special workflow or business meaning attached 
 * to it. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupType extends PersistableBusinessObjectBase {
    private static final long serialVersionUID = 9118112248900436184L;
	private Long id;
	private String name;
	private String description;
	private String workflowDocumentType;

    private ArrayList<GroupTypeDefaultAttribute> groupTypeDefaultAttributes;

    /**
     * This constructs a GroupType business object instance.
     *
     */
    public GroupType() {
        this.groupTypeDefaultAttributes = new TypedArrayList(GroupTypeDefaultAttribute.class);
    }
    
    /**
     * Retrieves the id (PK) of the group type. 
     * 
     * @return Long
     */
    public Long getId() {
	    return this.id;
	}

    /**
     * This method sets the id (PK) of the group type.
     * 
     * @param id
     */
	public void setId(Long id) {
	    this.id = id;
	}

	/**
	 * This method retrieves the name of the group type - a unique identifier.
	 * 
	 * @return String
	 */
	public String getName() {
	    return this.name;
	}

	/**
	 * This method sets the name of the group type - a unique identifier.
	 * 
	 * @param name
	 */
	public void setName(String name) {
	    this.name = name;
	}

	/**
	 * This method retrieves the description of the group type.
	 * 
	 * @return String
	 */
	public String getDescription() {
	    return this.description;
	}

	/**
	 * This method sets the description.
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
	    this.description = description;
	}

	/**
     * @return the workflowDocumentType
     */
    public String getWorkflowDocumentType() {
        return this.workflowDocumentType;
    }

    /**
     * @param workflowDocumentType the workflowDocumentType to set
     */
    public void setWorkflowDocumentType(String workflowDocumentType) {
        this.workflowDocumentType = workflowDocumentType;
    }

    /**
     * @return the groupTypeDefaultAttributes
     */
    public ArrayList<GroupTypeDefaultAttribute> getGroupTypeDefaultAttributes() {
        return this.groupTypeDefaultAttributes;
    }

    /**
     * @param groupTypeDefaultAttributes the groupTypeDefaultAttributes to set
     */
    public void setGroupTypeDefaultAttributes(ArrayList<GroupTypeDefaultAttribute> groupTypeDefaultAttributes) {
        this.groupTypeDefaultAttributes = groupTypeDefaultAttributes;
    }

    /**
	 * This method returns a string representation of a group type instance and is used for logging.
	 * 
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getName());
        propMap.put("description", getDescription());
        propMap.put("workflowDocumentType", getWorkflowDocumentType());
        return propMap;
	}
}
