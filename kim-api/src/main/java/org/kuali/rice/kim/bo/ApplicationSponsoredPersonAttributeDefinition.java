/**
 * 
 */
package org.kuali.rice.kim.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

public class ApplicationSponsoredPersonAttributeDefinition extends PersistableBusinessObjectBase {

	private static final long serialVersionUID = -9056229917116128109L;
	private Long id;
	private Long applicationId;
	private String attributeName;
	private Long attributeTypeId;

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Long getAttributeTypeId() {
		return attributeTypeId;
	}

	public void setAttributeTypeId(Long attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("name", getApplicationId());
        propMap.put("discription", getAttributeName());
        propMap.put("permissions", getAttributeTypeId());
        return propMap;
    }

	public void refresh() {
	}
}
