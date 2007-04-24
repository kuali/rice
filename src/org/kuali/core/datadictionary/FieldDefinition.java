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
import org.kuali.Constants;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.ClassValidationException;
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
    // logger
    private static Log LOG = LogFactory.getLog(FieldDefinition.class);

    private String attributeName;
    private boolean required;
    private boolean forceInquiry;
    private boolean noInquiry;
    private boolean forceLookup;
    private boolean noLookup;
    private String defaultValue;
    private Class defaultValueFinderClass;
    /**
     * This field is stored as a String because apache digester does not make it
     * easy to detect number format exceptions because it swallows parsing exceptions.
     */
    private String maxLength = String.valueOf(Constants.LOOKUP_RESULT_FIELD_MAX_LENGTH_NOT_DEFINED);

    private String displayEditMode;
    private Mask displayMask;

    public FieldDefinition() {
        LOG.debug("creating new FieldDefinition");

        this.required = false;
        this.forceInquiry = false;
        this.noInquiry = false;
        this.forceLookup = false;
        this.noLookup = false;
        this.maxLength = String.valueOf(Constants.LOOKUP_RESULT_FIELD_MAX_LENGTH_NOT_DEFINED);
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
        LOG.debug("calling setAttributeName '" + attributeName + "'");

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
        LOG.debug("calling setRequired '" + required + "'");

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
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, getAttributeName())) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        if (defaultValueFinderClass != null && defaultValue != null) {
            throw new AttributeValidationException("Both defaultValue and defaultValueFinderClass can not be specified on attribute " + getAttributeName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }

        if (defaultValueFinderClass != null) {
            if (!ValueFinder.class.isAssignableFrom(defaultValueFinderClass)) {
                throw new ClassValidationException("defaultValueFinderClass '" + defaultValueFinderClass + "' is not a subclasss of ValueFinder (" + getParseLocation() + ")");
            }
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
        return this.getAttributeName();
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
    public String getMaxLength() {
        return maxLength;
    }


    /**
     * Sets the maxLength attribute value.
     * @param maxLength The maxLength to set.
     */
    public void setMaxLength(String maxLength) {
        int maxLengthInt = Integer.parseInt(maxLength);
        if (maxLengthInt < 0) {
            throw new AttributeValidationException("Cannot have maxLength < 0");
        }
        this.maxLength = maxLength;
    }
}