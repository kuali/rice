/**
 * 
 */
package org.kuali.rice.kim.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

public class PersonAttribute extends BusinessObjectBase {

	private static final long serialVersionUID = 2861440911751860350L;
	private Long id;
	private Long attributeTypeId;
	private Long applicationId;
	private String attributeName;
	private String value;

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> propMap = new LinkedHashMap<String, Object>();
        propMap.put("id", getId());
        propMap.put("attributeTypeId", getAttributeTypeId());
        propMap.put("applicationId", getApplicationId());
        propMap.put("attributeName", getAttributeName());
        propMap.put("value", getValue());
        return propMap;
	}
	
	public void refresh() {
		// not going to implement unless needed
	}

}
