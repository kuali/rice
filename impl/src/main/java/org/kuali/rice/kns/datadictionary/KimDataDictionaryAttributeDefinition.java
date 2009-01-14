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

import java.util.Map;

import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.mask.Mask;
import org.kuali.rice.kns.web.format.Formatter;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KimDataDictionaryAttributeDefinition extends AttributeDefinition {
	
	private AttributeDefinition dataDictionaryAttributeDefinition;
	private Class<? extends Formatter> formatterClass;
	private ControlDefinition controlDefinition;
	private Mask mask;
	private String sortCode;
	private String applicationUrl;
	private Map<String, String> lookupInputPropertyConversions;
	private Map<String, String> lookupReturnPropertyConversions;

	/**
	 * @see org.kuali.rice.kns.datadictionary.AttributeDefinition#getControl()
	 */
	@Override
	public ControlDefinition getControl() {
		return controlDefinition;
	}

	/**
	 * @see org.kuali.rice.kns.datadictionary.AttributeDefinition#getFormatterClass()
	 */
	@Override
	public Class<? extends Formatter> getFormatterClass() {
		return formatterClass;
	}

	/**
	 * @return the sortCode
	 */
	public String getSortCode() {
		return this.sortCode;
	}

	/**
	 * @param sortCode
	 *            the sortCode to set
	 */
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}

	/**
	 * @return the applicationUrl
	 */
	public String getApplicationUrl() {
		return this.applicationUrl;
	}

	/**
	 * @param applicationUrl
	 *            the applicationUrl to set
	 */
	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}

	/**
	 * @return the lookupInputPropertyConversions
	 */
	public Map<String, String> getLookupInputPropertyConversions() {
		return this.lookupInputPropertyConversions;
	}

	/**
	 * @param lookupInputPropertyConversions
	 *            the lookupInputPropertyConversions to set
	 */
	public void setLookupInputPropertyConversions(Map<String, String> lookupInputPropertyConversions) {
		this.lookupInputPropertyConversions = lookupInputPropertyConversions;
	}

	/**
	 * @return the lookupReturnPropertyConversions
	 */
	public Map<String, String> getLookupReturnPropertyConversions() {
		return this.lookupReturnPropertyConversions;
	}

	/**
	 * @param lookupReturnPropertyConversions
	 *            the lookupReturnPropertyConversions to set
	 */
	public void setLookupReturnPropertyConversions(Map<String, String> lookupReturnPropertyConversions) {
		this.lookupReturnPropertyConversions = lookupReturnPropertyConversions;
	}

	/**
	 * @return the dataDictionaryAttributeDefinition
	 */
	public AttributeDefinition getDataDictionaryAttributeDefinition() {
		return this.dataDictionaryAttributeDefinition;
	}

	/**
	 * @param dataDictionaryAttributeDefinition the dataDictionaryAttributeDefinition to set
	 */
	public void setDataDictionaryAttributeDefinition(
			AttributeDefinition dataDictionaryAttributeDefinition) {
		this.dataDictionaryAttributeDefinition = dataDictionaryAttributeDefinition;
	}

}
