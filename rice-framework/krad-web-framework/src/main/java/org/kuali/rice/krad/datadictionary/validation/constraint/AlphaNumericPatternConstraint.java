/**
 * Copyright 2005-2018 The Kuali Foundation
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
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;

/**
 * A ValidCharactersConstraint based on AlphaNumericValidationPattern.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "alphaNumericPatternConstraint", parent="AlphaNumericPatternConstraint"),
        @BeanTag(name = "alphaNumericWithBasicPunc", parent="AlphaNumericWithBasicPunc")})
public class AlphaNumericPatternConstraint extends AllowCharacterConstraint {
    protected boolean lowerCase = false;
    protected boolean upperCase = false;

    /**
     * A label key is auto generated for this bean if none is set. This generated message can be
     * overridden through setMessageKey, but the generated message should cover most cases.
     *
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.BaseConstraint#getMessageKey()
     */
    @Override
    public String getMessageKey() {
        if (StringUtils.isEmpty(messageKey)) {
            StringBuilder key = new StringBuilder("");
            if (lowerCase) {
                return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "alphanumericPatternLowerCase");
            } else if (upperCase) {
                return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "alphanumericPatternUpperCase");
            } else {
                return (UifConstants.Messages.VALIDATION_MSG_KEY_PREFIX + "alphanumericPattern");
            }
        }

        return messageKey;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        StringBuilder regexString = new StringBuilder("[A-Za-z0-9");
        /*
         * This check must be first because we are removing the base 'A-Z' if lowerCase == true
         */
        if (lowerCase) {
            regexString = new StringBuilder("[a-z0-9");
        } else if (upperCase) {
            regexString = new StringBuilder("[A-Z0-9");
        }

        regexString.append(this.getAllowedCharacterRegex());

        regexString.append("]");

        return regexString.toString();
    }

    /**
     * @return the lowerCase
     */
    @BeanTagAttribute(name = "lowerCase")
    public boolean isLowerCase() {
        return this.lowerCase;
    }

    /**
     * Only allow lowerCase characters. DO NOT use with upperCase option, no flags set for case
     * means both upper and lower case are allowed.
     *
     * @param lowerCase the lowerCase to set
     */
    public void setLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
    }

    @BeanTagAttribute(name = "upperCase")
    public boolean isUpperCase() {
        return upperCase;
    }

    /**
     * Only allow upperCase characters.  DO NOT use with lowerCase option, no flags set for case
     * means both upper and lower case are allowed.
     *
     * @param upperCase the lowerCase to set
     */
    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }
}
