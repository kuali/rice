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
package org.kuali.rice.kns.datadictionary.validation.constraint;

import org.apache.commons.lang.StringUtils;


/**
 * Pattern for matching any printable character
 * 
 * 
 */
public class AnyCharacterPatternConstraint extends ValidCharactersPatternConstraint {
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
     * @see org.kuali.rice.kns.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuffer regexString = new StringBuffer("[");


        regexString.append("\\x21-\\x7E");
        if (allowWhitespace) {
            regexString.append("\\t\\r\\n\\v\\f");
        }
        regexString.append("]");

        return regexString.toString();
    }

	/**
	 * 
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.BaseConstraint#getLabelKey()
	 */
	@Override
	public String getLabelKey() {
		String labelKey = super.getLabelKey();
		if (StringUtils.isNotEmpty(labelKey)) {
			return labelKey;
		}
		StringBuilder key = new StringBuilder("");
		key.append("anyCharacterPattern,");
		if (getAllowWhitespace()) {
			key.append("whitespace");
		}
		return key.toString();
	}

//	@Override
//	protected String getValidationErrorMessageKeyOptions() {
//		if (getAllowWhitespace()) {
//			return ".allowWhitespace";
//		}
//		return KNSConstants.EMPTY_STRING;
//	}

}
