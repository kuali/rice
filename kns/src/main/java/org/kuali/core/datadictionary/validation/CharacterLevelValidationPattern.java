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
package org.kuali.core.datadictionary.validation;

import java.util.regex.Pattern;

import org.kuali.core.datadictionary.exporter.ExportMap;

/**
 * Abstraction of the regular expressions used to validate attribute values.
 * 
 * 
 */
abstract public class CharacterLevelValidationPattern extends ValidationPattern {
    private int maxLength = -1;
    private int exactLength = -1;

    /**
     * Sets maxLength parameter for the associated regex.
     * 
     * @param maxLength
     */
    public void setMaxLength(int maxLength) {
        if (this.exactLength != -1) {
            throw new IllegalStateException("illegal attempt to set maxLength after mutually-exclusive exactLength has been set");
        }

        this.maxLength = maxLength;
    }

    /**
     * @return current maxLength, or -1 if none has been set
     */
    public int getMaxLength() {
        return maxLength;
    }


    /**
     * Sets exactLength parameter for the associated regex.
     * 
     * @param exactLength
     */
    public void setExactLength(int exactLength) {
        if (this.maxLength != -1) {
            throw new IllegalStateException("illegal attempt to set exactLength after mutually-exclusive maxLength has been set");
        }

        this.exactLength = exactLength;
    }

    /**
     * @return current exactLength, or -1 if none has been set
     */
    public int getExactLength() {
        return exactLength;
    }


    /**
     * @return regular expression Pattern generated using the individual ValidationPattern subclass
     */
    final public Pattern getRegexPattern() {
        String regexString = getRegexString();

        StringBuffer completeRegex = new StringBuffer("^");
        completeRegex.append(getRegexString());

        if (maxLength != -1) {
            completeRegex.append("{0," + maxLength + "}");
        }
        else if (exactLength != -1) {
            completeRegex.append("{" + exactLength + "}");
        }
        else {
            completeRegex.append("*");
        }

        completeRegex.append("$");

        Pattern pattern = Pattern.compile(completeRegex.toString());
        return pattern;
    }


    /**
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#buildExportMap(java.lang.String)
     */
    final public ExportMap buildExportMap(String exportKey) {
        ExportMap exportMap = new ExportMap(exportKey);

        if (getMaxLength() != -1) {
            exportMap.set("maxLength", Integer.toString(getMaxLength()));
        }
        else if (getExactLength() != -1) {
            exportMap.set("exactLength", Integer.toString(getExactLength()));
        }

        extendExportMap(exportMap);

        return exportMap;
    }

    /**
     * Extends the given (parent class) exportMap as needed to represent subclass instances
     * 
     * @param exportMap
     */
    abstract public void extendExportMap(ExportMap exportMap);
}
