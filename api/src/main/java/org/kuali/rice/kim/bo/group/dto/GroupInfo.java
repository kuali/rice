/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.group.dto;

import java.io.Serializable;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;


/**
 * This is a description of what this class does - sgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupInfo implements Group, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String groupId;
	protected String groupName;
	protected String groupDescription;
	protected boolean active;
	protected String kimTypeId;
	protected String namespaceCode;
	protected AttributeSet attributes;
	
	
	public String getGroupDescription() {
		return this.groupDescription;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public String getKimTypeId() {
		return this.kimTypeId;
	}

	public String getNamespaceCode() {
		return this.namespaceCode;
	}

	public AttributeSet getAttributes() {
		return this.attributes;
	}
	
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

	public void setNamespaceCode(String namespaceCode) {
		this.namespaceCode = namespaceCode;
	}

	public void setAttributes(AttributeSet attributes) {
		this.attributes = attributes;
	}
	
	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

    public void refresh(){
    	
    }
    
    public void prepareForWorkflow(){
    	
    }

}
