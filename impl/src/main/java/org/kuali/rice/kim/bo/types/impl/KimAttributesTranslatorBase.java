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
package org.kuali.rice.kim.bo.types.impl;

import java.util.List;

import org.kuali.rice.kim.bo.types.KimAttributesTranslator;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public abstract class KimAttributesTranslatorBase implements KimAttributesTranslator {

	protected List<String> supportedAttributeNames;
	protected List<String> resultAttributeNames;

	/**
	 * @see org.kuali.rice.kim.bo.types.KimAttributesTranslator#supportsTranslationOfAttributes(java.util.List)
	 */
	public boolean supportsTranslationOfAttributes(final List<String> attributeNames) {
		if(supportedAttributeNames.containsAll(attributeNames)) 
			return true;
		return false;
	}

	/**
	 * @return the resultAttributeNames
	 */
	public List<String> getResultAttributeNames() {
		return this.resultAttributeNames;
	}

	/**
	 * @param resultAttributeNames the resultAttributeNames to set
	 */
	public void setResultAttributeNames(List<String> resultAttributeNames) {
		this.resultAttributeNames = resultAttributeNames;
	}

	/**
	 * @see org.kuali.rice.kim.bo.types.KimAttributesTranslator#getSupportedAttributeNames()
	 */
	public List<String> getSupportedAttributeNames() {
		return this.supportedAttributeNames;
	}
	
	/**
	 * @param supportedAttributeNames the supportedAttributeNames to set
	 */
	public void setSupportedAttributeNames(List<String> supportedAttributeNames) {
		this.supportedAttributeNames = supportedAttributeNames;
	}

}
