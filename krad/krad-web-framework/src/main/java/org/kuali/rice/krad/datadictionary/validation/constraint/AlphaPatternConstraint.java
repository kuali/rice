/*
 * Copyright 2005-2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.uif.UifConstants;


/**
 * Pattern for matching alpha characters
 * 
 * 
 */
public class AlphaPatternConstraint extends ValidCharactersPatternConstraint {
    protected boolean allowWhitespace = false;


    /**
     * @return allowWhitespace
     */
    public boolean getAllowWhitespace() {
        return allowWhitespace;
    }

    /**
     * @param allowWhitespace
     */
    public void setAllowWhitespace(boolean allowWhitespace) {
        this.allowWhitespace = allowWhitespace;
    }


    /**
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuffer regexString = new StringBuffer("[A-Za-z");

        if (allowWhitespace) {
            regexString.append("\\s");
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
		
		return UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "alphaPattern";
	}

    /**
     * Parameters to be used in the string retrieved by this constraint's labelKey
     * @return the validationMessageParams
     */
    public List<String> getValidationMessageParams() {
        if(validationMessageParams == null){
            validationMessageParams = new ArrayList<String>();
            ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
            StringBuilder paramString = new StringBuilder("");
            if (getAllowWhitespace()) {
                paramString.append(", " + configService
                        .getPropertyValueAsString(UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "whitespace"));
            }
            validationMessageParams.add(paramString.toString());
        }
        return this.validationMessageParams;
    }

}
