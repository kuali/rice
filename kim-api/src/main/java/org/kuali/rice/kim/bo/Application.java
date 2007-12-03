/**
 * 
 */
package org.kuali.rice.kim.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObjectBase;

public class Application extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = 7639954054609198731L;
	private Long id;
	private String name;
	private String description;
	private List<ApplicationSponsoredPersonAttributeDefinition> applicationSponsoredPersonAttributeDefinitions;
	private List<Permission> permissions;
	
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

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public List<ApplicationSponsoredPersonAttributeDefinition> getApplicationSponsoredUserAttributeDefinitions() {
		return applicationSponsoredPersonAttributeDefinitions;
	}

	public void setApplicationSponsoredUserAttributeDefinitions(List<ApplicationSponsoredPersonAttributeDefinition> applicationSponsoredPersonAttributeDefinitions) {
		this.applicationSponsoredPersonAttributeDefinitions = applicationSponsoredPersonAttributeDefinitions;
	}

	protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getName());
        propMap.put("discription", getDescription());
        propMap.put("permissions", getPermissions());
        propMap.put("applicationSponsoredPersonAttributeDefinitions", getApplicationSponsoredUserAttributeDefinitions());
        return propMap;
	}

	public void refresh() {
		// not going to do this unless needed
	}

}
