/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

/**
 * Contains section-related information for inquiry sections
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class InquirySectionDefinition extends DataDictionaryDefinitionBase {

    private String title;
    private List<FieldDefinition> inquiryFields = new ArrayList<FieldDefinition>();
    private Map<String, FieldDefinition> inquiryFieldMap = new HashMap<String, FieldDefinition>();
    private Map inquiryCollections;
    
    private Integer numberOfColumns;

    public InquirySectionDefinition() {
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
     * @param FieldDefinition
     * @throws IllegalArgumentException if the given FieldDefinition is null
     */
    public void addInquiryField(FieldDefinition inquiryField) {
        if (inquiryField == null) {
            throw new IllegalArgumentException("invalid (null) inquiryField");
        }

        String itemName = inquiryField.getAttributeName();
        if (inquiryFieldMap.containsKey(itemName)) {
            throw new DuplicateEntryException("duplicate itemName entry for item '" + itemName + "'");
        }

        inquiryFields.add(inquiryField);
        inquiryFieldMap.put(itemName, inquiryField);
    }

    /**
     * @return List of attributeNames of all FieldDefinitions associated with this InquirySection, in the order in
     *         which they were added
     */
    public List<String> getInquiryFieldNames() {
        List<String> itemNames = new ArrayList<String>();
        itemNames.addAll(this.inquiryFieldMap.keySet());

        return itemNames;
    }

    /**
     * @return Collection of all FieldDefinitions associated with this InquirySection, in the order in which they
     *         were added
     */
    public List<FieldDefinition> getInquiryFields() {
        return inquiryFields;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        for (FieldDefinition inquiryField : inquiryFields ) {
            inquiryField.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
        }
    }

    public String toString() {
        return "InquirySectionDefinition '" + getTitle() + "'";
    }

    public Map getInquiryCollections() {
        return inquiryCollections;
    }

    public void setInquiryCollections(Map inquiryCollections) {
        this.inquiryCollections = inquiryCollections;
    }

    public Integer getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(Integer numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }


    /**
     * @param inquiryFields the inquiryFields to set
     */
    public void setInquiryFields(List<FieldDefinition> inquiryFields) {
        this.inquiryFields = inquiryFields;
    }

}