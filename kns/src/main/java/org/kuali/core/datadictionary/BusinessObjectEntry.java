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
import org.kuali.core.datadictionary.exception.ClassValidationException;

/**
 * A single BusinessObject entry in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a BusinessObject and its attributes.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class BusinessObjectEntry extends DataDictionaryEntryBase {
    // logger
    private static Log LOG = LogFactory.getLog(BusinessObjectEntry.class);

    private Class businessObjectClass;

    private boolean boNotesEnabled;
    
    private InquiryDefinition inquiryDefinition;
    private LookupDefinition lookupDefinition;

    private String titleAttribute;
    private String objectLabel;
    private String objectDescription;

    private HelpDefinition helpDefinition;

    public BusinessObjectEntry() {
        super();
        LOG.debug("creating new BusinessObjectEntry");
    }

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#getJstlKey()
     */
    public String getJstlKey() {
        if (this.businessObjectClass == null) {
            throw new IllegalStateException("cannot generate JSTL key: businessObjectClass is null");
        }

        String jstlKey = StringUtils.substringAfterLast(this.businessObjectClass.getName(), ".");
        return jstlKey;
    }


    public void setBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    public boolean isBoNotesEnabled() {
        return boNotesEnabled;
    }

    public void setBoNotesEnabled(boolean boNotesEnabled) {
        this.boNotesEnabled = boNotesEnabled;
    }

    /**
     * @return true if this instance has an inquiryDefinition
     */
    public boolean hasInquiryDefinition() {
        return (inquiryDefinition != null);
    }

    /**
     * @return current inquiryDefinition for this BusinessObjectEntry, or null if there is none
     */
    public InquiryDefinition getInquiryDefinition() {
        return inquiryDefinition;
    }

    /**
     * Sets the inquiryDefinition for this BusinessObjectEntry to the given inquiryDefinition.
     * 
     * @param inquiryDefinition
     */
    public void setInquiryDefinition(InquiryDefinition inquiryDefinition) {
        if (inquiryDefinition == null) {
            throw new IllegalArgumentException("invalid (null) inquiryDefinition");
        }
        LOG.debug("calling setInquiryDefinition '" + inquiryDefinition.getTitle() + "'");

        this.inquiryDefinition = inquiryDefinition;
    }

    /**
     * @return true if this instance has a lookupDefinition
     */
    public boolean hasLookupDefinition() {
        return (lookupDefinition != null);
    }

    /**
     * @return current lookupDefinition for this BusinessObjectEntry, or null if there is none
     */
    public LookupDefinition getLookupDefinition() {
        return lookupDefinition;
    }

    /**
     * Sets the lookupDefinition for this BusinessObjectEntry to the given lookupDefinition.
     * 
     * @param lookupDefinition
     */
    public void setLookupDefinition(LookupDefinition lookupDefinition) {
        if (lookupDefinition == null) {
            throw new IllegalArgumentException("invalid (null) lookupDefinition");
        }
        LOG.debug("calling setLookupDefinition '" + lookupDefinition.getTitle() + "'");

        this.lookupDefinition = lookupDefinition;
    }

    /**
     * @return Returns the titleAttribute.
     */
    public String getTitleAttribute() {
        return titleAttribute;
    }


    /**
     * @param titleAttribute The titleAttribute to set.
     */
    public void setTitleAttribute(String titleAttribute) {
        this.titleAttribute = titleAttribute;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils) {
        super.completeValidation(validationCompletionUtils);

        if (!validationCompletionUtils.isBusinessObjectClass(businessObjectClass)) {
            throw new ClassValidationException("businessObjectClass '" + businessObjectClass.getName() + "' is not a BusinessObject class");
        }

        if (hasInquiryDefinition()) {
            inquiryDefinition.completeValidation(businessObjectClass, null, validationCompletionUtils);
        }

        if (hasLookupDefinition()) {
            lookupDefinition.completeValidation(businessObjectClass, null, validationCompletionUtils);
        }

    }

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntryBase#getEntryClass()
     */
    public Class getEntryClass() {
        return getBusinessObjectClass();
    }


    /**
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#getFullClassName()
     */
    public String getFullClassName() {
        return getBusinessObjectClass().getName();
    }

    /**
     * @return Returns the objectLabel.
     */
    public String getObjectLabel() {
        return objectLabel;
    }

    /**
     * @param objectLabel The objectLabel to set.
     */
    public void setObjectLabel(String objectLabel) {
        this.objectLabel = objectLabel;
    }

    /**
     * @return Returns the description.
     */
    public String getObjectDescription() {
        return objectDescription;
    }

    /**
     * @param description The description to set.
     */
    public void setObjectDescription(String objectDescription) {
        if (StringUtils.isBlank(objectDescription)) {
            throw new IllegalArgumentException("invalid (blank) objectDescription");
        }
        LOG.debug("calling setObjectDescription '" + objectDescription + "'");

        this.objectDescription = objectDescription;
    }

    /**
     * Gets the helpDefinition attribute.
     * 
     * @return Returns the helpDefinition.
     */
    public HelpDefinition getHelpDefinition() {
        return helpDefinition;
    }

    /**
     * Sets the helpDefinition attribute value.
     * 
     * @param helpDefinition The helpDefinition to set.
     */
    public void setHelpDefinition(HelpDefinition helpDefinition) {
        this.helpDefinition = helpDefinition;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String className = null;
        if (getBusinessObjectClass() != null) {
            className = getBusinessObjectClass().getName();
        }

        return "BusinessObjectEntry for class " + className;
    }


}