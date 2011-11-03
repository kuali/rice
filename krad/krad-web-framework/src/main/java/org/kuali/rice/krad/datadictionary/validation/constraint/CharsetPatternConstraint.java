/**
 * Copyright 2005-2011 The Kuali Foundation
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
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.UifConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Pattern for matching any character in the given list (String)
 * 
 * 
 */
public class CharsetPatternConstraint extends ValidCharactersPatternConstraint {
    protected String validChars;

    /**
     * @return String containing all valid chars for this charset
     */
    public String getValidChars() {
        return validChars;
    }

    /**
     * @param validChars for this charset
     */
    public void setValidChars(String validChars) {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalArgumentException("invalid (empty) validChars");
        }

        this.validChars = validChars;
    }


    /**
     * Escapes every special character I could think of, to limit potential misuse of this pattern.
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        if (StringUtils.isEmpty(validChars)) {
            throw new IllegalStateException("validChars is empty");
        }

        // filter out and escape chars which would confuse the pattern-matcher
        Pattern filteringChars = Pattern.compile("([\\-\\[\\]\\{\\}\\$\\.\\^\\(\\)\\*\\&\\|])");
        String filteredChars = filteringChars.matcher(validChars).replaceAll("\\\\$1");

        StringBuffer regexString = new StringBuffer("[");
        regexString.append(filteredChars);
        if (filteredChars.endsWith("\\")) {
            regexString.append("\\");
        }
        regexString.append("]");

        return regexString.toString();
    }

	/**
	 * 
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
	 */
	@Override
	public String getLabelKey() {
		String labelKey = super.getLabelKey();
		if (StringUtils.isNotEmpty(labelKey)) {
			return labelKey;
		}
		return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "charsetPattern");
	}

    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey
     * @return the validationMessageParams
     */
    public List<String> getValidationMessageParams() {
        if(validationMessageParams == null){
            validationMessageParams = new ArrayList<String>();
            if (StringUtils.isNotBlank(validChars)) {
                validationMessageParams.add(validChars);
            }
            
        }
        return this.validationMessageParams;
    }

}
