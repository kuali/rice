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

import java.util.ArrayList;
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
 * Contains inquiry-related information relating to the parent BusinessObject.
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 */
public class InquiryDefinition extends DataDictionaryDefinitionBase {
    private static Log LOG = LogFactory.getLog(InquiryDefinition.class);

    private String title;
    private Map inquirySections;
    private Class inquirableClass;

    public InquiryDefinition() {
        LOG.debug("creating new InquiryDefinition");
        this.inquirySections = new LinkedHashMap();
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
     * @param inquirySection
     * @throws IllegalArgumentException if the given inquirySection is null
     */
    public void addInquirySection(InquirySectionDefinition inquirySection) {
        if (inquirySection == null) {
            throw new IllegalArgumentException("invalid (null) inquirySection");
        }
        LOG.debug("calling addInquirySection for section '" + inquirySection.getTitle() + "'");

        String sectionTitle = inquirySection.getTitle();
        if (this.inquirySections.containsKey(sectionTitle)) {
            throw new DuplicateEntryException("duplicate inquirySection entry for attribute '" + sectionTitle + "'");
        }

        this.inquirySections.put(sectionTitle, inquirySection);
    }

    /**
     * @return Collection of all inquiryField FieldDefinitions associated with this InquiryDefinition, in the order in which they
     *         were added
     */
    public List getInquirySections() {
        List sectionList = new ArrayList();

        sectionList.addAll(this.inquirySections.values());

        return Collections.unmodifiableList(sectionList);
    }

    /**
     * @return InquirySectionDefinition for the inquiry section associated with the given section title, or null if there is none
     */
    public InquirySectionDefinition getInquirySection(String sectionTitle) {
        return (InquirySectionDefinition) inquirySections.get(sectionTitle);
    }
     
    /**
     * Returns the FieldDefinition associated with the field attribute name
     * @param fieldName
     * @return
     */
    public FieldDefinition getFieldDefinition(String fieldName) {
        for (Iterator iter = inquirySections.values().iterator(); iter.hasNext();) {
            InquirySectionDefinition section = (InquirySectionDefinition) iter.next();
            for (Iterator iterator = section.getInquiryFields().iterator(); iterator.hasNext();) {
                FieldDefinition field = (FieldDefinition) iterator.next();
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
        for (Iterator i = inquirySections.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            InquirySectionDefinition inquirySection = (InquirySectionDefinition) e.getValue();
            inquirySection.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "InquiryDefinition '" + getTitle() + "'";
    }


    public Class getInquirableClass() {
        return inquirableClass;
    }


    public void setInquirableClass(Class inquirableClass) {
        this.inquirableClass = inquirableClass;
    }
}