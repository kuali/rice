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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

public class InquiryCollectionDefinition extends FieldDefinition implements
		CollectionDefinitionI {

	private Class<? extends BusinessObject> businessObjectClass;

	private Integer numberOfColumns;

	private Map<String,FieldDefinition> inquiryFieldMap = new HashMap<String, FieldDefinition>();
	private Map<String,InquiryCollectionDefinition> inquiryCollectionMap = new HashMap<String, InquiryCollectionDefinition>();
    private Map<String,FieldDefinitionI> summaryFieldMap = new HashMap<String, FieldDefinitionI>();
    private List<FieldDefinition> inquiryFields = new ArrayList<FieldDefinition>();
    private List<InquiryCollectionDefinition> inquiryCollections = new ArrayList<InquiryCollectionDefinition>();
    private List<FieldDefinition> summaryFields = new ArrayList<FieldDefinition>();

	private String summaryTitle;


	public InquiryCollectionDefinition() {}

	public Class getBusinessObjectClass() {
		return businessObjectClass;
	}

	public void setBusinessObjectClass(Class businessObjectClass) {
		this.businessObjectClass = businessObjectClass;
	}

	public Integer getNumberOfColumns() {
		return numberOfColumns;
	}

	/**
                numberOfColumns = the number of fields to be displayed in each row of the inquiry section.
                For example, numberOfColumns = 2 indicates that the label and values for two fields will be
                displayed in each row as follows:
                    field1label field1value  |   field2label field2value
                    field3label field3value  |   field4label field4value
                etc.
	 */
	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public String getName() {
		return getAttributeName();
	}
	
	public List<? extends CollectionDefinitionI> getCollections() {
		return inquiryCollections;
	}

	public List<? extends FieldDefinitionI> getFields() {
		return inquiryFields;
	}

	public boolean getIncludeAddLine() {
		return false;
	}

	public boolean hasSummaryField(String key) {
		return summaryFieldMap.containsKey(key);
	}

	public void setSummaryTitle(String summaryTitle) {
		this.summaryTitle = summaryTitle;
	}

	public String getSummaryTitle() {
		return this.summaryTitle;
	}

    public List<FieldDefinition> getInquiryFields() {
        return this.inquiryFields;
    }

    /**
                JSTL: inquiryFields is a Map which is accessed using a
                key of "inquiryFields".  This map contains the following types
                of elements:
                    * inquirySubSectionHeader
                    * field
                    * inquiryCollection
                Each of these entries are keyed by "attributeName".
                The associated value is the attributeName of the
                mapped element.

                  The inquirySubSectionHeader allows a separator containing text to
                  separate groups of fields.  The name attribute is the displayed text.

                  DD:   See InquirySubSectionHeaderDefinition.
                  JSTL: inquirySubSectionHeader appears in the inquiryFields map as:
                      * key = "attributeName"
                      * value = name of inquirySubSectionHeader


                    The field element defines the attributes of a single data field.

                    DD:  See FieldDefinition.java
                    JSTL: The field element is a Map which is accessed using
                    a key of the attributeName.  This map contains the following keys:
                        * attributeName (String)
                        * forceInquiry (boolean String)
                        * noInquiry (boolean String)
                        * maxLength (String)

                forceInquiry = true means that the displayed field value will
                always be made inquirable (this attribute is not used within the code).

                noInquiry = true means that the displayed field will never be made inquirable.

                maxLength = the maximum allowable length of the field in the lookup result fields.  In other contexts,
                like inquiries, this field has no effect.

     */
    public void setInquiryFields(List<FieldDefinition> inquiryFields) {
        inquiryFieldMap.clear();
        for ( FieldDefinition inquiryField : inquiryFields ) {
            if (inquiryField == null) {
                throw new IllegalArgumentException("invalid (null) inquiryField");
            }
    
            String itemName = inquiryField.getAttributeName();
            if (inquiryFieldMap.containsKey(itemName)) {
                throw new DuplicateEntryException(
                        "duplicate itemName entry for item '" + itemName + "'");
            }
    
            inquiryFieldMap.put(itemName, inquiryField);
        }

        this.inquiryFields = inquiryFields;
    }

    public List<InquiryCollectionDefinition> getInquiryCollections() {
        return inquiryCollections;
    }

    public void setInquiryCollections(List<InquiryCollectionDefinition> inquiryCollections) {
        inquiryCollectionMap.clear();
        for ( InquiryCollectionDefinition inquiryCollection : inquiryCollections ) {
            if (inquiryCollection == null) {
                throw new IllegalArgumentException(
                        "invalid (null) inquiryCollection");
            }

            String fieldName = inquiryCollection.getName();
            if (inquiryCollectionMap.containsKey(fieldName)) {
                throw new DuplicateEntryException(
                        "duplicate fieldName entry for field '" + fieldName + "'");
            }

            inquiryCollectionMap.put(fieldName, inquiryCollection);
        }
        this.inquiryCollections = inquiryCollections;
    }

    public List<FieldDefinition> getSummaryFields() {
        return this.summaryFields;
    }

    /**
                    The inquirySummaryField indicates which fields are to appear in
                    the header line of each record in a collection.  For example,
                    the header of an address record may contain something like:
                        "Address ( Purchase_Order - San Francisco )"
                    where the two summary fields are:
                        Vendor Type = "Purchase_Order"
                        Vendor City = "San Francisco"

                    DD:  See FieldDefinition.java
     */
    public void setSummaryFields(List<FieldDefinition> summaryFields) {
        summaryFieldMap.clear();
        for ( FieldDefinition inquiryField : summaryFields ) {
            if (inquiryField == null) {
                throw new IllegalArgumentException("invalid (null) summaryField");
            }
    
            String itemName = inquiryField.getAttributeName();
            if (summaryFieldMap.containsKey(itemName)) {
                throw new DuplicateEntryException(
                        "duplicate itemName entry for item '" + itemName + "'");
            }
    
            summaryFieldMap.put(itemName, inquiryField);
        }
        this.summaryFields = summaryFields;
    }
	
	
}
