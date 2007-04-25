/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.core.datadictionary;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.datadictionary.mask.MaskFormatter;
import org.kuali.core.datadictionary.validation.ValidationPattern;
import org.kuali.core.web.format.Formatter;


/**
 * A single attribute definition in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a specific attribute of an entry.
 * 
 * 
 */
public class AttributeDefinition extends DataDictionaryDefinitionBase {
    protected final static Pattern MATCH_ANYTHING = Pattern.compile(".*");

    // logger
    private static Log LOG = LogFactory.getLog(AttributeDefinition.class);

    private Boolean override = Boolean.FALSE;

    private Boolean forceUppercase;

    private String name;
    private String label;
    private String shortLabel;
    private String displayLabelAttribute;

    private Integer maxLength;

    private BigDecimal exclusiveMin;
    private BigDecimal inclusiveMax;

    private ValidationPattern validationPattern;
    private Boolean required;

    private ControlDefinition control;

    private String displayWorkgroup;
    private Mask displayMask;

    private String summary;
    private String description;

    private Class formatterClass;
    private Class attributeClass;

    public AttributeDefinition() {
        super();
        
        LOG.debug("creating new AttributeDefinition");

        this.displayWorkgroup = "";
    }


    public void setForceUppercase(Boolean forceUppercase) {
        this.forceUppercase = forceUppercase;
    }

    public Boolean getForceUppercase() {
        return this.forceUppercase;
    }


    public void setOverride(Boolean override) {
        this.override = override;
    }

    public Boolean getOverride() {
        return this.override;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        LOG.debug("calling setName '" + name + "'");

        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if (StringUtils.isBlank(label)) {
            throw new IllegalArgumentException("invalid (blank) label");
        }
        LOG.debug("calling setLabel '" + label + "'");

        this.label = label;
    }

    /**
     * @return the shortLabel, or the label if no shortLabel has been set
     */
    public String getShortLabel() {
        return (shortLabel != null) ? shortLabel : getLabel();
    }

    /**
     * @return the shortLabel directly, without substituting in the label
     */
    protected String getDirectShortLabel() {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel) {
        if (StringUtils.isBlank(shortLabel)) {
            throw new IllegalArgumentException("invalid (blank) shortLabel");
        }
        LOG.debug("calling setShortLabel '" + shortLabel + "'");

        this.shortLabel = shortLabel;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        LOG.debug("calling setMaxLength " + maxLength + "");

        this.maxLength = maxLength;
    }

    public BigDecimal getExclusiveMin() {
        return exclusiveMin;
    }

    public void setExclusiveMin(BigDecimal exclusiveMin) {
        this.exclusiveMin = exclusiveMin;
    }

    public BigDecimal getInclusiveMax() {
        return inclusiveMax;
    }

    public void setInclusiveMax(BigDecimal inclusiveMax) {
        this.inclusiveMax = inclusiveMax;
    }

    /**
     * @return true if a validationPattern has been set
     */
    public boolean hasValidationPattern() {
        return (validationPattern != null);
    }

    public ValidationPattern getValidationPattern() {
        return this.validationPattern;
    }

    public void setValidationPattern(ValidationPattern validationPattern) {
        this.validationPattern = validationPattern;
    }


    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean isRequired() {
        return this.required;
    }


    /**
     * @return control
     */
    public ControlDefinition getControl() {
        return control;
    }

    /**
     * @param control
     * @throws IllegalArgumentException if the given control is null
     */
    public void setControl(ControlDefinition control) {
        if (control == null) {
            throw new IllegalArgumentException("invalid (null) control");
        }
        LOG.debug("calling setControl '" + control + "'");

        this.control = control;
    }

    /**
     * Gets the displayMask attribute.
     * 
     * @return Returns the displayMask.
     */
    public Mask getDisplayMask() {
        return displayMask;
    }


    /**
     * Sets the displayMask attribute value.
     * 
     * @param displayMask The displayMask to set.
     */
    public void setDisplayMask(Mask displayMask) {
        this.displayMask = displayMask;
    }


    /**
     * Gets the displayWorkgroup attribute.
     * 
     * @return Returns the displayWorkgroup.
     */
    public String getDisplayWorkgroup() {
        return displayWorkgroup;
    }


    /**
     * Sets the displayWorkgroup attribute value.
     * 
     * @param displayWorkgroup The displayWorkgroup to set.
     */
    public void setDisplayWorkgroup(String displayWorkgroup) {
        this.displayWorkgroup = displayWorkgroup;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        if (StringUtils.isBlank(summary)) {
            throw new IllegalArgumentException("invalid (blank) summary");
        }
        LOG.debug("calling setSummary '" + summary + "'");

        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("invalid (blank) description");
        }
        LOG.debug("calling setDescription '" + description + "'");

        this.description = description;
    }

    public boolean hasFormatterClass() {
        return (formatterClass != null);
    }

    public boolean hasDisplayMask() {
        return (displayMask != null);
    }

    public Class getFormatterClass() {
        return formatterClass;
    }

    public void setFormatterClass(Class formatterClass) {
        if (formatterClass == null) {
            throw new IllegalArgumentException("invalid (null) formatterClass");
        }
        LOG.debug("calling setFormatterClass '" + formatterClass.getName() + "'");

        this.formatterClass = formatterClass;
    }

    public Class getAttributeClass() {
        return attributeClass;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootObjectClass, Class otherObjectClass, ValidationCompletionUtils validationCompletionUtils) {

        if (!validationCompletionUtils.isPropertyOf(rootObjectClass, getName())) {
            throw new AttributeValidationException("property '" + getName() + "' is not a property of class '" + rootObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        if (hasFormatterClass()) {
            if (!Formatter.class.isAssignableFrom(getFormatterClass())) {
                throw new ClassValidationException("formatterClass '" + getFormatterClass().getName() + "' for attribute '" + getName() + "' is not a subclass of pojo.format.Formatter (" + getParseLocation() + ")");
            }
        }

        getControl().completeValidation(rootObjectClass, otherObjectClass, validationCompletionUtils);

        if (StringUtils.isNotBlank(getDisplayWorkgroup()) && !hasDisplayMask()) {
            throw new AttributeValidationException("property '" + getName() + "' has a display workgroup defined but not a valid display mask '" + rootObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        if (hasDisplayMask()) {
            if (getDisplayMask().getMaskFormatter() == null && getDisplayMask().getMaskFormatterClass() == null) {
                throw new AttributeValidationException("No mask formatter or formatter class specified for secure attribute " + getName() + "' (" + getParseLocation() + ")");
            }

            if (getDisplayMask().getMaskFormatterClass() != null) {
                if (!MaskFormatter.class.isAssignableFrom(getDisplayMask().getMaskFormatterClass())) {
                    throw new ClassValidationException("Class '" + getDisplayMask().getMaskFormatterClass().getName() + "' for secure attribute '" + getName() + "' does not implement org.kuali.core.datadictionary.mask.MaskFormatter (" + getParseLocation() + ")");
                }
            }
        }


        // set default values
        if (getForceUppercase() == null) {
            setForceUppercase(Boolean.FALSE);
        }
        if (isRequired() == null) {
            setRequired(Boolean.FALSE);

        }

        // lookup and set attribute class
        attributeClass = rootObjectClass;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AttributeDefinition for attribute " + getName();
    }


    public String getDisplayLabelAttribute() {
        return displayLabelAttribute;
    }


    public void setDisplayLabelAttribute(String displayLabelAttribute) {
        this.displayLabelAttribute = displayLabelAttribute;
    }
}