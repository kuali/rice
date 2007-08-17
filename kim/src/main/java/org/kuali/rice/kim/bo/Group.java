package org.kuali.rice.kim.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

public class Group extends BusinessObjectBase {

	private static final long serialVersionUID = 4974576362491778342L;

	private Long id;
	private String name;
	private String description;
	private List<User> users;
	private List<GroupAttribute> groupAttributes;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<GroupAttribute> getGroupAttributes() {
		return groupAttributes;
	}

	public void setGroupAttributes(List<GroupAttribute> groupAttributes) {
		this.groupAttributes = groupAttributes;
	}
	
	protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getName());
        propMap.put("discription", getDescription());
        propMap.put("permissions", getGroupAttributes());
        propMap.put("applicationSponsoredUserAttributes", getUsers());
        return propMap;
	}

	public void refresh() {
		// not implemented unless needed
	}
}
