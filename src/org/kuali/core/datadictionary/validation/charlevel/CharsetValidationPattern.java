/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.validation.charlevel;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.exporter.ExportMap;
import org.kuali.core.datadictionary.validation.CharacterLevelValidationPattern;

/**
 * Pattern for matching any character in the given list (String)
 * 
 * 
 */
public class CharsetValidationPattern extends CharacterLevelValidationPattern {
    private String validChars;

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
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#getRegexString()
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
     * @see org.kuali.core.datadictionary.validation.CharacterLevelValidationPattern#extendExportMap(org.kuali.bo.datadictionary.exporter.ExportMap)
     */
    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "charset");

        exportMap.set("validChars", getValidChars());
    }


    /**
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#getPatternXml()
     */
    public String getPatternXml() {
        StringBuffer xml = new StringBuffer("<charSet ");
        xml.append(validChars);
        xml.append("/>");

        return xml.toString();
    }
}
