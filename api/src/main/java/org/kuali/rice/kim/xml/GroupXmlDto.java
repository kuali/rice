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
package org.kuali.rice.kim.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.jaxb.MapStringStringAdapter;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.xml.GroupMembershipXmlDto;



/**
 * This is a description of what this class does - sgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */

@XmlRootElement(name = "group", namespace = "http://rice.kuali.org/xsd/kim/group")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Group", namespace = "http://rice.kuali.org/xsd/kim/group", propOrder = {
    "groupId", "groupName", "groupDescription", "active", "kimTypeId", "namespaceCode",
    "attributes", "members"
})

public class GroupXmlDto implements Group, Serializable {
	
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "groupId", namespace = "http://rice.kuali.org/xsd/kim/group")	
	protected String groupId;

	@XmlElement(name = "groupName", namespace = "http://rice.kuali.org/xsd/kim/group")	
	protected String groupName;

	@XmlElement(name = "groupDescription", namespace = "http://rice.kuali.org/xsd/kim/group")	
	protected String groupDescription;

	@XmlElement(name = "active", namespace = "http://rice.kuali.org/xsd/kim/group")	
	protected boolean active;

	@XmlElement(name = "kimTypeId", namespace = "http://rice.kuali.org/xsd/kim/group")	
	protected String kimTypeId;

	@XmlElement(name = "namespaceCode", namespace = "http://rice.kuali.org/xsd/kim/group")	
	protected String namespaceCode;

	@XmlElement(name = "attributes", namespace = "http://rice.kuali.org/xsd/kim/group")	
	@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
	protected AttributeSet attributes;
	
	@XmlElement(name = "members", namespace = "http://rice.kuali.org/xsd/kim/group")	
	protected List<GroupMembershipXmlDto> members;
		
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
	
	public List<GroupMembershipXmlDto> getMembers() {
		return this.members;
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
	
	public void setMembers(List<GroupMembershipXmlDto> members) {
		this.members = members;
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
