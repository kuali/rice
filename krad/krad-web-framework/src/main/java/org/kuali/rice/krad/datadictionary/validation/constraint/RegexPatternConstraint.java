/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.validation.constraint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern.ValidationPatternException;

/**
 * This class is used to set a regex pattern of characters to match. This string should not
 * include the start(^) and end($) symbols or any length related symbols (*, {0,}, etc)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RegexPatternConstraint extends ValidCharactersPatternConstraint {

    @XmlElement
    protected String pattern;
	
    @XmlElement
    private String validationErrorMessageKey;
	
	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the validationErrorMessageKey
	 */
	public String getValidationErrorMessageKey() {
		return this.validationErrorMessageKey;
	}

	/**
	 * @param validationErrorMessageKey the validationErrorMessageKey to set
	 */
	public void setValidationErrorMessageKey(String validationErrorMessageKey) {
		this.validationErrorMessageKey = validationErrorMessageKey;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
	 */
	@Override
	public String getLabelKey() {
		if (StringUtils.isBlank(getValidationErrorMessageKey())) {
			throw new ValidationPatternException("Regex Validation Patterns must have a validation error message key defined");
		}
		return getValidationErrorMessageKey();
	}

	/**
	 * This overridden method returns the regex pattern as set by the application
	 * 
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersPatternConstraint#getRegexString()
	 */
	@Override
	protected String getRegexString() {
		return getPattern();
	}

}
