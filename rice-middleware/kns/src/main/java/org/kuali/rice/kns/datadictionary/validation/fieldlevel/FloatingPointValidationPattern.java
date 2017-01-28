/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation.fieldlevel;

import org.kuali.rice.krad.datadictionary.exporter.ExportMap;
import org.kuali.rice.krad.datadictionary.validation.FieldLevelValidationPattern;

/**
 * Validation pattern for matching floating point numbers, optionally matching negative numbers
 * 
 * @deprecated Use {@link org.kuali.rice.krad.datadictionary.validation.constraint.FloatingPointPatternConstraint}.
 */
@Deprecated
public class FloatingPointValidationPattern extends FieldLevelValidationPattern {
    protected boolean allowNegative;

    /**
     * @return allowNegative
     */
    public boolean getAllowNegative() {
        return allowNegative;
    }

    /**
     * @param allowNegative
     */
    public void setAllowNegative(boolean allowNegative) {
        this.allowNegative = allowNegative;
    }

    /**
     * Adds special handling to account for optional allowNegative
     * 
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuffer regex = new StringBuffer();

        if (allowNegative) {
            regex.append("-?");
        }
        regex.append(super.getRegexString());

        return regex.toString();
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.FieldLevelValidationPattern#getPatternTypeName()
     */
    protected String getPatternTypeName() {
        return "floatingPoint";
    }


    /**
     * @see org.kuali.rice.krad.datadictionary.validation.ValidationPattern#buildExportMap(java.lang.String)
     */
    public ExportMap buildExportMap(String exportKey) {
        ExportMap exportMap = super.buildExportMap(exportKey);

        if (allowNegative) {
            exportMap.set("allowNegative", "true");
        }

        return exportMap;
    }
    
	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.FieldLevelValidationPattern#getValidationErrorMessageKey()
	 */
	@Override
	public String getValidationErrorMessageKey() {
		StringBuilder buf = new StringBuilder();
		buf.append("error.format.").append(getClass().getName());
		if (allowNegative) {
			buf.append(".allowNegative");
		}
		return buf.toString();
	}
}
