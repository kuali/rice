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
package org.kuali.rice.kns.datadictionary.validation.charlevel;

import org.kuali.rice.kns.datadictionary.exporter.ExportMap;
import org.kuali.rice.kns.datadictionary.validation.CharacterLevelValidationPattern;

/**
 * Pattern for matching alphanumeric characters
 * 
 * Also, allows conditionally whitespace, underscore, or period.
 */
public class AlphaNumericValidationPattern extends CharacterLevelValidationPattern {
    protected boolean allowWhitespace = false;
    protected boolean allowUnderscore = false;
    protected boolean allowPeriod = false;

    /**
     * @return allowPeriod
     */
    public boolean getAllowPeriod() {
        return allowPeriod;
    }

    /**
     * @param allowPeriod
     */
    public void setAllowPeriod(boolean allowPeriod) {
        this.allowPeriod = allowPeriod;
    }
    
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
     * @return allowUnderscore
     */
    public boolean getAllowUnderscore() {
        return allowUnderscore;
    }

    /**
     * @param allowWhitespace
     */
    public void setAllowUnderscore(boolean allowUnderscore) {
        this.allowUnderscore = allowUnderscore;
    }


    /**
     * @see org.kuali.rice.kns.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
    	StringBuilder regexString = new StringBuilder("[A-Za-z0-9");

        if (allowWhitespace) {
            regexString.append("\\s");
        }
        if (allowUnderscore) {
            regexString.append("_");
        }
        if (allowPeriod) {
            regexString.append(".");
        }
        regexString.append("]");

        return regexString.toString();
    }


    /**
     * @see org.kuali.rice.kns.datadictionary.validation.CharacterLevelValidationPattern#extendExportMap(org.kuali.bo.datadictionary.exporter.ExportMap)
     */
    public void extendExportMap(ExportMap exportMap) {
        exportMap.set("type", "alphaNumeric");

        if (allowWhitespace) {
            exportMap.set("allowWhitespace", "true");
        }
        if (allowUnderscore) {
            exportMap.set("allowUnderscore", "true");
        }
        if (allowPeriod) {
        	exportMap.set("allowPeriod", "true");
        }
    }

	@Override
	protected String getValidationErrorMessageKeyOptions() {
		final StringBuilder opts = new StringBuilder();

		if (allowWhitespace) {
			opts.append(".allowWhitespace");
		}
		if (allowUnderscore) {
			opts.append(".allowUnderscore");
		}
		if (allowPeriod) {
			opts.append(".allowPeriod");
		}

		return opts.toString();
	}
}
