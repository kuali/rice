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
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;

/**
 * ConfigurationBasedRegexPatternConstraint uses a patternTypeKey to get the regex used for validation by key from
 * the KualiConfigurationService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "configurationBasedRegexPatternConstraint"),
        @BeanTag(name = "phoneNumberPatternConstraint", parent = "PhoneNumberPatternConstraint"),
        @BeanTag(name = "timePatternConstraint", parent = "TimePatternConstraint"),
        @BeanTag(name = "time24HPatternConstraint", parent = "Time24HPatternConstraint"),
        @BeanTag(name = "urlPatternConstraint", parent = "UrlPatternConstraint"),
        @BeanTag(name = "noWhitespacePatternConstraint", parent = "NoWhitespacePatternConstraint"),
        @BeanTag(name = "javaClassPatternConstraint", parent = "JavaClassPatternConstraint"),
        @BeanTag(name = "emailAddressPatternConstraint", parent = "EmailAddressPatternConstraint"),
        @BeanTag(name = "timestampPatternConstraint", parent = "TimestampPatternConstraint"),
        @BeanTag(name = "yearPatternConstraint", parent = "YearPatternConstraint"),
        @BeanTag(name = "monthPatternConstraint", parent = "MonthPatternConstraint"),
        @BeanTag(name = "zipcodePatternConstraint", parent = "ZipcodePatternConstraint")})
public class ConfigurationBasedRegexPatternConstraint extends ValidDataPatternConstraint {
    protected String patternTypeKey;

    /**
     * Message key used to identify the validation pattern
     *
     * @return the patternTypeKey
     */
    @BeanTagAttribute(name = "patternTypeKey")
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
     * MessageKey in used in this class have the patternTypeKey appended to the VALIDATION_MSG_KEY_PREFIX by default,
     * if it is not explicitly set to something else
     *
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
    @Override
    protected String getRegexString() {
        if ( StringUtils.isBlank(getPatternTypeKey())) {
            throw new IllegalArgumentException("patternTypeKey is null, configuration of " + this.getClass().getName() + " is incomplete" );
        }
        return CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(getPatternTypeKey());
    }

}