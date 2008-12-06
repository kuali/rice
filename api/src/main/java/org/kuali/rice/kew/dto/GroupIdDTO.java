/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package org.kuali.rice.kew.dto;

/**
 * A data transport object for specifying a group id.
 * Either namespace and group name should be defined or just groupId.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class GroupIdDTO implements java.io.Serializable {
	
	private String namespace;
	private String groupName;
	private String groupId;

	public GroupIdDTO() {}

	public String getNamespace() {
		return this.namespace;
	}
	
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getGroupName() {
		return this.groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getGroupId() {
		return this.groupId;
	}
	
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String toString() {
		return "[namespace = " + namespace + ", " +
			"groupName = " + groupName + ", " +
			"groupId = " + groupId + "]";
	}
	
}
