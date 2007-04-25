/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.validation.fieldlevel;

import org.kuali.core.datadictionary.exporter.ExportMap;
import org.kuali.core.datadictionary.validation.FieldLevelValidationPattern;

/**
 * Validation pattern for matching fixed point numbers, optionally matching negative numbers
 * 
 * 
 */
public class FixedPointValidationPattern extends FieldLevelValidationPattern {
    public static final String PATTERN_TYPE_PRECISION = "fixedPoint.precision";
    public static final String PATTERN_TYPE_SCALE = "fixedPoint.scale";

    private boolean allowNegative;
    private int precision;
    private int scale;

    /**
     * @return Returns the precision.
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision The precision to set.
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * @return Returns the scale.
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param scale The scale to set.
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

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
     * Adds special handling to account for optional allowNegative and dynamic precision, scale
     * 
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#getRegexString()
     */
    protected String getRegexString() {
        StringBuffer regex = new StringBuffer();

        if (allowNegative) {
            regex.append("-?");
        }
        // final patter will be: -?([0-9]{0,p-s}\.[0-9]{1,s}|[0-9]{1,p-s}) where p = precision, s=scale
        regex.append("(");
        regex.append("[0-9]{0," + (getPrecision() - getScale()) + "}");
        regex.append("\\.");
        regex.append("[0-9]{1," + getScale() + "}");
        regex.append("|[0-9]{1," + (getPrecision() - getScale()) + "}");
        regex.append(")");
        return regex.toString();
    }

    /**
     * @see org.kuali.core.datadictionary.validation.FieldLevelValidationPattern#getPatternTypeName()
     */
    protected String getPatternTypeName() {
        return "fixedPoint";
    }


    /**
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#buildExportMap(java.lang.String)
     */
    public ExportMap buildExportMap(String exportKey) {
        ExportMap exportMap = super.buildExportMap(exportKey);

        if (allowNegative) {
            exportMap.set("allowNegative", "true");
        }
        exportMap.set("precision", Integer.toString(precision));
        exportMap.set("scale", Integer.toString(scale));

        return exportMap;
    }


    /**
     * @see org.kuali.core.datadictionary.validation.ValidationPattern#getPatternXml()
     */
    public String getPatternXml() {
        StringBuffer xml = new StringBuffer("<fixedPoint ");
        if (allowNegative) {
            xml.append("allowNegative=\"true\" ");
        }
        xml.append("precision=\"" + precision + "\" ");
        xml.append("scale=\"" + scale + "\" ");
        xml.append("/>");

        return xml.toString();
    }
}
