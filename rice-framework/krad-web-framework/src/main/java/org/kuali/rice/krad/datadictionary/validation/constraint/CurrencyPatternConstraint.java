/**
 * Copyright 2005-2014 The Kuali Foundation
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
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.UifConstants;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;

/**
 * Validation pattern for matching currency type. Extends the FloatingPointPatternConstraint and
 * adds the currency prefix/suffix to the regex string for validation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class CurrencyPatternConstraint extends FloatingPointPatternConstraint {

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersPatternConstraint#getRegexString()
     */
    @Override
    protected String getRegexString() {
        StringBuilder regexString = new StringBuilder(super.getRegexString());

        NumberFormat formatter = getCurrencyInstanceUsingParseBigDecimal();

        if (!(formatter instanceof DecimalFormat)) {
            return regexString.toString();
        }

        String prefix = ((DecimalFormat) formatter).getPositivePrefix();
        String suffix = ((DecimalFormat) formatter).getPositiveSuffix();

        // Regex special characters need to be escaped if they are part of the prefix/suffix
        if (prefix != null) {
            StringBuilder escapedPrefix = new StringBuilder();
            for (char c : prefix.toCharArray()) {
                if (UifConstants.JS_REGEX_SPECIAL_CHARS.indexOf(c) != -1) {
                    escapedPrefix.append("\\");
                }
                escapedPrefix.append(c);
            }

            regexString.insert(0, "(" + escapedPrefix + ")?");
        }

        if (suffix != null) {
            StringBuilder escapedSuffix = new StringBuilder();
            for (char c : suffix.toCharArray()) {
                if (UifConstants.JS_REGEX_SPECIAL_CHARS.indexOf(c) != -1) {
                    escapedSuffix.append("\\");
                }
                escapedSuffix.append(c);
            }

            regexString.append("(" + escapedSuffix + ")?");
        }

        return regexString.toString();
    }

    /**
     * retrieves a currency formatter instance and sets ParseBigDecimal to true
     * to fix [KULEDOCS-742]
     *
     * @return CurrencyInstance
     */
    private NumberFormat getCurrencyInstanceUsingParseBigDecimal() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        if (formatter instanceof DecimalFormat) {
            ((DecimalFormat) formatter).setParseBigDecimal(true);
        }
        return formatter;
    }

}
