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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigurationBasedRegexPatternConstraint extends ValidDataPatternConstraint {
    protected String patternTypeKey;
    
    /**
     * Message key used to identify the validation pattern
     * 
     * @return the patternTypeKey
     */
    public String getPatternTypeKey() {
        return this.patternTypeKey;
    }

    /**
     * Setter for the pattern message key
     * 
     * @param patternTypeKey the patternTypeKey to set
     */
    public void setPatternTypeKey(String patternTypeKey) {
        this.patternTypeKey = patternTypeKey;
    }
    
    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getMessageKey()
     */
    @Override
    public String getMessageKey() {
        if (StringUtils.isNotEmpty(messageKey)) {
            return messageKey;
        }

        StringBuilder buf = new StringBuilder();
        buf.append(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX).append(getPatternTypeKey());
        return buf.toString();
    }
    
	/**
	 * Uses the key returned by {@link #getPatternTypeKey()} to fetch the
	 * validationPattern's regex string from the ConfigurationService which should not include
     * the start(^) and end($) symbols
     *
     * @return String regex validation string
	 */
    protected String getRegexString() {
        return (String) KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(getPatternTypeKey());
    }

}