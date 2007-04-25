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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.datadictionary.mask.MaskFormatter;
import org.kuali.core.lookup.valueFinder.ValueFinder;

/**
 * MaintainableFieldDefinition
 * 
 * 
 */
public class MaintainableFieldDefinition extends MaintainableItemDefinition implements FieldDefinitionI{
    // logger
    private static Log LOG = LogFactory.getLog(MaintainableFieldDefinition.class);

    private String name;
    private boolean required;
    private boolean readOnly;
    private boolean readOnlyAfterAdd = false; 

    private String defaultValue;
    private String template;
    private Class defaultValueFinderClass;

    private String displayEditMode;
    private Mask displayMask;

    private String webUILeaveFieldFunction = "";
    private String webUILeaveFieldCallbackFunction = "";
    
    private Class overrideLookupClass;
    private String overrideFieldConversions;
    
    public MaintainableFieldDefinition() {
        LOG.debug("creating new MaintainableFieldDefinition");

        this.required = false;
    }


    /**
     * @return attributeName
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name to the given value.
     * 
     * @param name
     * @throws IllegalArgumentException if the given name is blank
     */
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        LOG.debug("calling setName '" + name + "'");

        this.name = name;
    }


    /**
     * @return true if this attribute is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets isRequired to the given value
     * 
     * @param isRequired
     */
    public void setRequired(boolean required) {
        LOG.debug("calling setRequired '" + required + "'");

        this.required = required;
    }

    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }


    /**
     * @param defaultValue The defaultValue to set.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * @param defaultValueFinderClass
     */
    public void setDefaultValueFinderClass(Class defaultValueFinderClass) {
        if (defaultValueFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) defaultValueFinderClass");
        }

        this.defaultValueFinderClass = defaultValueFinderClass;
    }

    /**
     * @return custom defaultValue class
     */
    public Class getDefaultValueFinderClass() {
        return this.defaultValueFinderClass;
    }

    /**
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly The readOnly to set.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }


    /**
     * Gets the displayEditMode attribute.
     * 
     * @return Returns the displayEditMode.
     */
    public String getDisplayEditMode() {
        return displayEditMode;
    }


    /**
     * Sets the displayEditMode attribute value.
     * 
     * @param displayEditMode The displayEditMode to set.
     */
    public void setDisplayEditMode(String displayEditMode) {
        this.displayEditMode = displayEditMode;
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
     * Gets the overrideFieldConversions attribute. 
     * @return Returns the overrideFieldConversions.
     */
    public String getOverrideFieldConversions() {
        return overrideFieldConversions;
    }


    /**
     * Sets the overrideFieldConversions attribute value.
     * @param overrideFieldConversions The overrideFieldConversions to set.
     */
    public void setOverrideFieldConversions(String overrideFieldConversions) {
        this.overrideFieldConversions = overrideFieldConversions;
    }


    /**
     * Gets the overrideLookupClass attribute. 
     * @return Returns the overrideLookupClass.
     */
    public Class getOverrideLookupClass() {
        return overrideLookupClass;
    }


    /**
     * Sets the overrideLookupClass attribute value.
     * @param overrideLookupClass The overrideLookupClass to set.
     */
    public void setOverrideLookupClass(Class overrideLookupClass) {
        this.overrideLookupClass = overrideLookupClass;
    }


    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, name)) {
            throw new AttributeValidationException("unable to find attribute or collection named '" + name + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        if (defaultValueFinderClass != null && defaultValue != null) {
            throw new AttributeValidationException("Both defaultValue and defaultValueFinderClass can not be specified on attribute " + name + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }

        if (defaultValueFinderClass != null) {
            if (!ValueFinder.class.isAssignableFrom(defaultValueFinderClass)) {
                throw new ClassValidationException("defaultValueFinderClass '" + defaultValueFinderClass + "' is not a subclasss of ValueFinder (" + getParseLocation() + ")");
            }
        }

        if (StringUtils.isNotBlank(displayEditMode) && displayMask == null) {
            throw new AttributeValidationException("property '" + getName() + "' has a display edit mode defined but not a valid display mask '" + "' (" + getParseLocation() + ")");
        }

        if (displayMask != null) {
            if (getDisplayMask().getMaskFormatter() == null && getDisplayMask().getMaskFormatterClass() == null) {
                throw new AttributeValidationException("No mask formatter or formatter class specified for secure attribute " + getName() + "' (" + getParseLocation() + ")");
            }

            if (getDisplayMask().getMaskFormatterClass() != null) {
                if (!MaskFormatter.class.isAssignableFrom(getDisplayMask().getMaskFormatterClass())) {
                    throw new ClassValidationException("Class '" + getDisplayMask().getMaskFormatterClass().getName() + "' for secure attribute '" + getName() + "' does not implement org.kuali.core.datadictionary.mask.MaskFormatter (" + getParseLocation() + ")");
                }
            }
        }
        
        if (overrideLookupClass != null) {
            if (!BusinessObject.class.isAssignableFrom(overrideLookupClass)) {
                throw new ClassValidationException("overrideLookupClass '" + overrideLookupClass + "' is not a subclasss of BusinessObject (" + getParseLocation() + ")");
            }
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintainableFieldDefinition for field " + getName();
    }


    public String getTemplate() {
        return template;
    }


    public void setTemplate(String template) {
        this.template = template;
    }


    public String getWebUILeaveFieldCallbackFunction() {
        return webUILeaveFieldCallbackFunction;
    }


    public void setWebUILeaveFieldCallbackFunction(String webUILeaveFieldCallbackFunction) {
        this.webUILeaveFieldCallbackFunction = webUILeaveFieldCallbackFunction;
    }


    public String getWebUILeaveFieldFunction() {
        return webUILeaveFieldFunction;
    }


    public void setWebUILeaveFieldFunction(String webUILeaveFieldFunction) {
        this.webUILeaveFieldFunction = webUILeaveFieldFunction;
    }


    public boolean isReadOnlyAfterAdd() {
        return readOnlyAfterAdd;
    }


    public void setReadOnlyAfterAdd(boolean readOnlyAfterAdd) {
        this.readOnlyAfterAdd = readOnlyAfterAdd;
    }
}