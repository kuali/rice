/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import org.kuali.rice.krad.uif.UifConstants;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigurationBasedRegexPatternConstraint extends ValidDataPatternConstraint {
    /**
     * the key used to identify the validation pattern
     */
    protected String patternTypeKey;
    
    /**
     * the key used to identify the validation pattern
     * 
     * @return the patternTypeKey
     */
    public String getPatternTypeKey() {
        return this.patternTypeKey;
    }

    /**
     * the key used to identify the validation pattern
     * 
     * @param patternTypeKey the patternTypeKey to set
     */
    public void setPatternTypeKey(String patternTypeKey) {
        this.patternTypeKey = patternTypeKey;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
     */
    @Override
    public String getLabelKey() {
        if(StringUtils.isNotEmpty(labelKey)){
            return labelKey;
        }
        else{
            StringBuilder buf = new StringBuilder();
            buf.append(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX).append(getPatternTypeKey());
            return buf.toString();
        }
    }
    
	/**
	 * This method implementation uses the key returned by {@link #getPatternTypePropertyString()} to fetch the 
	 * validationPattern's regex string from the ConfigurationService which should not include the start(^) and end($) symbols
	 */
    protected String getRegexString() {
//        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString("validationPatternRegex." + getPatternTypeName());
        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(getPatternTypeKey());
    }

}