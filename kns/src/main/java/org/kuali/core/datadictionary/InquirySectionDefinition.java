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

package org.kuali.core.datadictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

/**
 * Contains section-related information for inquiry sections
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class InquirySectionDefinition extends DataDictionaryDefinitionBase {
    private static Log LOG = LogFactory.getLog(InquirySectionDefinition.class);

    private String title;
    private Map<String, FieldDefinition> inquiryFields;
    private Map inquiryCollections;
    
    private String numberOfColumns;

    public InquirySectionDefinition() {
        LOG.debug("creating new InquirySectionDefinition");
        this.inquiryFields = new LinkedHashMap();
        this.inquiryCollections = new LinkedHashMap();
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
        LOG.debug("calling setTitle '" + title + "'");

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
        if (this.inquiryFields.containsKey(itemName)) {
            throw new DuplicateEntryException("duplicate itemName entry for item '" + itemName + "'");
        }

        this.inquiryFields.put(itemName, inquiryField);
    }

    /**
     * @return List of attributeNames of all FieldDefinitions associated with this InquirySection, in the order in
     *         which they were added
     */
    public List getInquiryFieldNames() {
        List itemNames = new ArrayList();
        itemNames.addAll(this.inquiryFields.keySet());

        return Collections.unmodifiableList(itemNames);
    }

    /**
     * @return Collection of all FieldDefinitions associated with this InquirySection, in the order in which they
     *         were added
     */
    public Collection<FieldDefinition> getInquiryFields() {
        return Collections.unmodifiableCollection(this.inquiryFields.values());
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        for (Iterator i = inquiryFields.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            FieldDefinition inquiryField = (FieldDefinition) e.getValue();
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

    public String getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(String numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }
}