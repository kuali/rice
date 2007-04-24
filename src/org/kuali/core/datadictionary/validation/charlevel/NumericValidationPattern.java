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

import org.kuali.core.datadictionary.exporter.ExportMap;
import org.kuali.core.datadictionary.validation.CharacterLevelValidationPattern;

/**
 * Pattern for matching numeric characters
 * 
 * 
 */
public class NumericValidationPattern extends CharacterLevelValidationPattern {
    /**
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuffer regexString = new StringBuffer("[0-9]");

        return regexString.toString();
    }


    /**
     * @see org.kuali.core.datadictionary.validation.CharacterLevelValidationPattern#extendExportMap(org.kuali.bo.datadictionary.exporter.ExportMap)
     */
    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "numeric");
    }


    /**
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#getPatternXml()
     */
    public String getPatternXml() {
        return "<numeric />";
    }
}
