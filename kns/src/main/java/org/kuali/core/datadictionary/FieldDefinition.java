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
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.lookup.valueFinder.ValueFinder;

/**
 * Contains field-related information for DataDictionary entries.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 * 
 * 
 */
public class FieldDefinition extends DataDictionaryDefinitionBase implements FieldDefinitionI {
 
    private String attributeName;
    private boolean required = false;
    private boolean forceInquiry = false;
    private boolean noInquiry = false;
    private boolean forceLookup = false;
    private boolean noLookup = false;
    private String defaultValue;
    private Class<? extends ValueFinder> defaultValueFinderClass;

    private Integer maxLength = null;

    private String displayEditMode;
    private Mask displayMask;

    public FieldDefinition() {
    }


    /**
     * @return attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets attributeName to the given value.
     * 
     * @param attributeName
     * @throws IllegalArgumentException if the given attributeName is blank
     */
    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        this.attributeName = attributeName;
    }


    /**
     * @return true if this attribute is required
     */
    public boolean isRequired() {
        return required;
    }


    /**
     * Sets required to the given value.
     * 
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }


    /**
     * @return Returns the forceInquiry.
     */
    public boolean isForceInquiry() {
        return forceInquiry;
    }


    /**
     * @param forceInquiry The forceInquiry to set.
     */
    public void setForceInquiry(boolean forceInquiry) {
        this.forceInquiry = forceInquiry;
    }

    /**
     * @return Returns the forceLookup.
     */
    public boolean isForceLookup() {
        return forceLookup;
    }

    /**
     * @param forceLookup The forceLookup to set.
     */
    public void setForceLookup(boolean forceLookup) {
        this.forceLookup = forceLookup;
    }

    /**
     * @return Returns the noInquiry.
     */
    public boolean isNoInquiry() {
        return noInquiry;
    }

    /**
     * @param noInquiry The noInquiry to set.
     */
    public void setNoInquiry(boolean noInquiry) {
        this.noInquiry = noInquiry;
    }

    /**
     * @return Returns the noLookup.
     */
    public boolean isNoLookup() {
        return noLookup;
    }

    /**
     * @param noLookup The noLookup to set.
     */
    public void setNoLookup(boolean noLookup) {
        this.noLookup = noLookup;
    }


    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }


    /**
           The defaultValue element will pre-load the specified value
           into the field.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     * @return custom defaultValue class
     */
    public Class<? extends ValueFinder> getDefaultValueFinderClass() {
        return this.defaultValueFinderClass;
    }

    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getAttributeName())) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (defaultValueFinderClass != null && defaultValue != null) {
            throw new AttributeValidationException("Both defaultValue and defaultValueFinderClass can not be specified on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }

        if (forceInquiry == true && noInquiry == true) {
            throw new AttributeValidationException("Both forceInquiry and noInquiry can not be set to true on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }
        if (forceLookup == true && noLookup == true) {
            throw new AttributeValidationException("Both forceLookup and noLookup can not be set to true on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "FieldDefinition for attribute " + getAttributeName();
    }


    public String getName() {
        return attributeName;
    }


    public String getDisplayEditMode() {
        return displayEditMode;
    }


    public void setDisplayEditMode(String displayEditMode) {
        this.displayEditMode = displayEditMode;
    }


    public Mask getDisplayMask() {
        return displayMask;
    }


    public void setDisplayMask(Mask displayMask) {
        this.displayMask = displayMask;
    }


    public boolean isReadOnlyAfterAdd() {
        return false;
    }


    /**
     * Gets the maxLength attribute. 
     * @return Returns the maxLength.
     */
    public Integer getMaxLength() {
        return maxLength;
    }


    /**
     * Sets the maxLength attribute value.
     * @param maxLength The maxLength to set.
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }


    /**
                      The defaultValueFinderClass specifies the java class that will be
                      used to determine the default value of a field.  The classname
                      specified in this field must implement org.kuali.core.lookup.valueFinder.ValueFinder
     */
    public void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass) {
        if (defaultValueFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) defaultValueFinderClass");
        }
        this.defaultValueFinderClass = defaultValueFinderClass;
    }
}