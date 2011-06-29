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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.KRADServiceLocator;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigurationBasedRegexPatternConstraint extends ValidCharactersConstraint{

	/**
     * the key used to retrieve the validationPattern's type name from the ConfigurationService
	 */
	protected String patternTypePropertyString;

	/**
     * Warning: This value should NOT be set on ValidCharactersPatternConstraints as the value is built dynamically from the
     * flags set on the constraint - if this value IS set it will override any automatic generation and only
     * use that which was set through this method for server side validation
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint#setValue(java.lang.String)
     */
    @Override
    public void setValue(String value) {
    	super.setValue(value);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint#getValue()
     */
    @Override
    public String getValue() {
        if(StringUtils.isEmpty(value)){
    		return "^" + getRegexString() + "$";
    	}
    	return value;

    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
	 */
	@Override
	public String getLabelKey() {
		StringBuilder buf = new StringBuilder();
		buf.append("validation.format.").append(getPatternTypePropertyString());
		return buf.toString();
	}

	/**
	 * This method returns a string representing a regex with characters to match, this string should not
	 * include the start(^) and end($) symbols
	 * 
	 * The default implementation uses the key returned by getConfigurationRegexKey to fetch the 
	 * validationPattern's regex string from the ConfigurationService
	 * 
	 */
    protected String getRegexString() {
//        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyString("validationPatternRegex." + getPatternTypeName());
        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyString(getPatternTypePropertyString());
    }

	/**
     * the key used to retrieve the validationPattern's type name from the ConfigurationService
     * 
	 * @return the patternTypePropertyString
	 */
	public String getPatternTypePropertyString() {
		return this.patternTypePropertyString;
	}

	/**
     * the key used to retrieve the validationPattern's type name from the ConfigurationService
     * 
	 * @param patternTypePropertyString the patternTypePropertyString to set
	 */
	public void setPatternTypePropertyString(String patternTypePropertyString) {
		this.patternTypePropertyString = patternTypePropertyString;
	}

}