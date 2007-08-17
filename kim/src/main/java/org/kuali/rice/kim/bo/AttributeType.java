/**
 * 
 */
package org.kuali.rice.kim.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

public class AttributeType extends BusinessObjectBase {

	private static final long serialVersionUID = -3856630570406063764L;
	private Long id;
	private String attributeTypeName;

	public String getAttributeTypeName() {
		return attributeTypeName;
	}

	public void setAttributeTypeName(String attributeTypeName) {
		this.attributeTypeName = attributeTypeName;
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
        propMap.put("attributeTypeName", getAttributeTypeName());
        return propMap;
	}

	public void refresh() {
		// not doing this unless needed
	}

}
