/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;


public class InquiryCollectionDefinition extends FieldDefinition implements CollectionDefinitionI {
    private static Log LOG = LogFactory.getLog(InquiryCollectionDefinition.class);
    
    private String name;
    private Class businessObjectClass;
    private String numberOfColumns;

    private Map inquiryFields;
    private Map inquiryCollections;
    
    private String summaryTitle;
    private Map summaryFields;

    public InquiryCollectionDefinition() {
        this.inquiryFields = new LinkedHashMap();
        this.inquiryCollections = new LinkedHashMap();
        this.summaryFields = new LinkedHashMap();
    }

    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    public void setBusinessObjectClass(Class businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    public Collection getInquiryFields() {
        return Collections.unmodifiableCollection(this.inquiryFields.values());
    }

    public void setInquiryFields(Map inquiryFields) {
        this.inquiryFields = inquiryFields;
    }

    public String getNumberOfColumns() {
        return numberOfColumns;
    }
    
    public void setNumberOfColumns(String numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    //don't like this, but need to be able get resolve the property AttriubteName
    public String getAttributeName() {
        return this.name;
    }

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
    

    public void addSummaryField(FieldDefinition inquiryField) {
        if (inquiryField == null) {
            throw new IllegalArgumentException("invalid (null) inquiryField");
        }

        String itemName = inquiryField.getAttributeName();
        if (this.summaryFields.containsKey(itemName)) {
            throw new DuplicateEntryException("duplicate itemName entry for item '" + itemName + "'");
        }

        this.summaryFields.put(itemName, inquiryField);
    }
    
    /**
     * @param inquiryCollection
     * @throws IllegalArgumentException if the given maintainableCollection is null
     */
    public void addInquiryCollection(InquiryCollectionDefinition inquiryCollection) {
        if (inquiryCollection == null) {
            throw new IllegalArgumentException("invalid (null) inquiryCollection");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling addinquiryCollection for field '" + inquiryCollection.getName() + "'");
        }

        String fieldName = inquiryCollection.getName();
        if (this.inquiryCollections.containsKey(fieldName)) {
            throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
        }

        this.inquiryCollections.put(fieldName, inquiryCollection);
    }


    /**
     * @return Collection of all lookupField MaintainableCollectionDefinitions associated with this
     *         MaintainableCollectionDefinition, in the order in which they were added
     */
    public Collection<InquiryCollectionDefinition> getInquiryCollections() {
        return Collections.unmodifiableCollection(this.inquiryCollections.values());
    }
    
    public Collection<? extends CollectionDefinitionI> getCollections() {
        return this.getInquiryCollections();
    }

    public Collection getFields() {
        // TODO Auto-generated method stub
        return this.getInquiryFields();
    }

    public boolean getIncludeAddLine() {
        return false;
    }

    /**
    * @return Collection of all SummaryFieldDefinitions associated with this SummaryFieldDefinition, in the order in which they
    *         were added
    */
   public Collection<? extends FieldDefinitionI> getSummaryFields() {
       return Collections.unmodifiableCollection(this.summaryFields.values());
   }

   public boolean hasSummaryField(String key) {
       return this.summaryFields.containsKey(key);
   }

    public String getSummaryTitle() {
        return this.summaryTitle;
    }
}
