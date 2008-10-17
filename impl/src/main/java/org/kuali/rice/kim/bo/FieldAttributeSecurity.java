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

import org.kuali.rice.kns.datadictionary.AttributeSecurity;

/**
 * This is a description of what this class does - mpham don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class FieldAttributeSecurity {
	
	private String attributeName;
	private AttributeSecurity maintainableFieldAttributeSecurity;
	private AttributeSecurity businessObjectAttributeSecurity;
	private Class businessObjectClass;
	private String documentTypeName;
	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return this.attributeName;
	}
	/**
	 * @return the businessObjectClass
	 */
	public Class getBusinessObjectClass() {
		return this.businessObjectClass;
	}
	/**
	 * @return the documentTypeName
	 */
	public String getDocumentTypeName() {
		return this.documentTypeName;
	}
	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/**
	 * @param businessObjectClass the businessObjectClass to set
	 */
	public void setBusinessObjectClass(Class businessObjectClass) {
		this.businessObjectClass = businessObjectClass;
	}
	/**
	 * @param documentTypeName the documentTypeName to set
	 */
	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}
	/**
	 * @return the maintainableFieldAttributeSecurity
	 */
	public AttributeSecurity getMaintainableFieldAttributeSecurity() {
		return this.maintainableFieldAttributeSecurity;
	}
	/**
	 * @return the businessObjectAttributeSecurity
	 */
	public AttributeSecurity getBusinessObjectAttributeSecurity() {
		return this.businessObjectAttributeSecurity;
	}
	/**
	 * @param maintainableFieldAttributeSecurity the maintainableFieldAttributeSecurity to set
	 */
	public void setMaintainableFieldAttributeSecurity(
			AttributeSecurity maintainableFieldAttributeSecurity) {
		this.maintainableFieldAttributeSecurity = maintainableFieldAttributeSecurity;
	}
	/**
	 * @param businessObjectAttributeSecurity the businessObjectAttributeSecurity to set
	 */
	public void setBusinessObjectAttributeSecurity(
			AttributeSecurity businessObjectAttributeSecurity) {
		this.businessObjectAttributeSecurity = businessObjectAttributeSecurity;
	}
}
