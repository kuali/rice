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
package org.kuali.rice.kns.datadictionary;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Defines a list of property paths (relative to a Document object) that contain a particular type of value (e.g.
 * all campus codes of a document).  This information is used to help workflow perform document searches and routing
 * based on values on the document.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WorkflowAttributeDefinition extends DataDictionaryDefinitionBase {
    private String referenceBusinessObjectAttributeName;
    private Class referenceBusinessObjectClass;
    private List<String> documentValuesPropertyPaths;
    
	public String getReferenceBusinessObjectAttributeName() {
		return this.referenceBusinessObjectAttributeName;
	}

	public void setReferenceBusinessObjectAttributeName(
			String referenceBusinessObjectPropertyName) {
		this.referenceBusinessObjectAttributeName = referenceBusinessObjectPropertyName;
	}

	public Class getreferenceBusinessObjectClass() {
		return this.referenceBusinessObjectClass;
	}

	public void setreferenceBusinessObjectClass(
			Class referenceBusinessObjectClass) {
		this.referenceBusinessObjectClass = referenceBusinessObjectClass;
	}

	public List<String> getDocumentValuesPropertyPaths() {
		return this.documentValuesPropertyPaths;
	}

	public void setDocumentValuesPropertyPaths(
			List<String> documentValuesPropertyPaths) {
		this.documentValuesPropertyPaths = documentValuesPropertyPaths;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
	 */
	public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
		if ((referenceBusinessObjectClass != null) != (StringUtils.isNotBlank(referenceBusinessObjectAttributeName))) {
			// both must be blank, or both must be filled in
			throw new IllegalArgumentException("referenceBusinessObjectClass and referenceBusinessObjectPropertyName must both be filled in or both not filled in");
		}
	}
}
