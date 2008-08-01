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

package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.inquiry.Inquirable;

/**
 * Contains inquiry-related information relating to the parent BusinessObject.
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class InquiryDefinition extends DataDictionaryDefinitionBase {

    private String title;
    private List<InquirySectionDefinition> inquirySections = new ArrayList<InquirySectionDefinition>();
    private Class<? extends Inquirable> inquirableClass;

    public InquiryDefinition() {
    }


    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title to the given value.
     * 
     * @param title
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("invalid (blank) title");
        }

        this.title = title;
    }


    /**
     * @param inquirySection
     * @throws IllegalArgumentException if the given inquirySection is null
     */
    public void addInquirySection(InquirySectionDefinition inquirySection) {
        if (inquirySection == null) {
            throw new IllegalArgumentException("invalid (null) inquirySection");
        }

        this.inquirySections.add(inquirySection);
    }

    /**
     * @return Collection of all inquiryField FieldDefinitions associated with this InquiryDefinition, in the order in which they
     *         were added
     */
    public List<InquirySectionDefinition> getInquirySections() {
        return inquirySections;
    }
   
    /**
     * Returns the FieldDefinition associated with the field attribute name
     * @param fieldName
     * @return
     */
    public FieldDefinition getFieldDefinition(String fieldName) {
        for (InquirySectionDefinition section : inquirySections ) {
            for (FieldDefinition field : section.getInquiryFields() ) {
                if (field.getAttributeName().equals(fieldName)) {
                    return field;
                }
            }
        }
        
        return null;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        for ( InquirySectionDefinition inquirySection : inquirySections ) {
            inquirySection.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "InquiryDefinition '" + getTitle() + "'";
    }


    public Class<? extends Inquirable> getInquirableClass() {
        return inquirableClass;
    }


    public void setInquirableClass(Class<? extends Inquirable> inquirableClass) {
        this.inquirableClass = inquirableClass;
    }


    public void setInquirySections(List<InquirySectionDefinition> inquirySections) {
        this.inquirySections = inquirySections;
    }
}