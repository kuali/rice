/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.exporter;

import java.util.Iterator;

import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.FieldDefinition;
import org.kuali.core.datadictionary.InquiryDefinition;
import org.kuali.core.datadictionary.InquirySectionDefinition;

/**
 * InquiryMapBuilder
 * 
 * 
 */
public class InquiryMapBuilder {

    /**
     * Default constructor
     */
    public InquiryMapBuilder() {
    }


    /**
     * @param inquiry
     * @return ExportMap containing the standard entries for the entry's InquiryDefinition, or null if the given entry has no
     *         inquiryDefinition
     */
    public ExportMap buildInquiryMap(BusinessObjectEntry entry) {
        ExportMap inquiryMap = null;

        if (entry.hasInquiryDefinition()) {
            InquiryDefinition inquiryDefinition = entry.getInquiryDefinition();
            inquiryMap = new ExportMap("inquiry");

            inquiryMap.set("title", inquiryDefinition.getTitle());

            inquiryMap.set(buildInquiryFieldsMap(inquiryDefinition));
        }

        return inquiryMap;
    }

    private ExportMap buildInquiryFieldsMap(InquiryDefinition inquiryDefinition) {
        ExportMap inquiryFieldsMap = new ExportMap("inquiryFields");

        for (Iterator i = inquiryDefinition.getInquirySections().iterator(); i.hasNext();) {
            InquirySectionDefinition inquirySection = (InquirySectionDefinition) i.next();
            for (Iterator iter = inquirySection.getInquiryFields().iterator(); iter.hasNext();) {
                FieldDefinition FieldDefinition = (FieldDefinition) iter.next();
                inquiryFieldsMap.set(MapperUtils.buildFieldMap(FieldDefinition));
            }
        }

        return inquiryFieldsMap;
    }
}