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
package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

public class InquiryCollectionDefinition extends FieldDefinition implements
		CollectionDefinitionI {

	private String name;

	private Class<? extends BusinessObject> businessObjectClass;

	private Integer numberOfColumns;

	private Map<String,FieldDefinition> inquiryFieldMap = new HashMap<String, FieldDefinition>();
	private Map<String,InquiryCollectionDefinition> inquiryCollectionMap = new HashMap<String, InquiryCollectionDefinition>();
    private Map<String,FieldDefinitionI> summaryFieldMap = new HashMap<String, FieldDefinitionI>();
    private List<FieldDefinition> inquiryFields = new ArrayList<FieldDefinition>();
    private List<InquiryCollectionDefinition> inquiryCollections = new ArrayList<InquiryCollectionDefinition>();
    private List<FieldDefinition> summaryFields = new ArrayList<FieldDefinition>();

	private String summaryTitle;


	public InquiryCollectionDefinition() {
	}

	public Class getBusinessObjectClass() {
		return businessObjectClass;
	}

	public void setBusinessObjectClass(Class businessObjectClass) {
		this.businessObjectClass = businessObjectClass;
	}


	public Integer getNumberOfColumns() {
		return numberOfColumns;
	}

	public void setNumberOfColumns(Integer numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// don't like this, but need to be able get resolve the property
	// AttriubteName
	public String getAttributeName() {
		return this.name;
	}

	public void addInquiryField(FieldDefinition inquiryField) {
		if (inquiryField == null) {
			throw new IllegalArgumentException("invalid (null) inquiryField");
		}

		String itemName = inquiryField.getAttributeName();
		if (this.inquiryFieldMap.containsKey(itemName)) {
			throw new DuplicateEntryException(
					"duplicate itemName entry for item '" + itemName + "'");
		}

        this.inquiryFieldMap.put(itemName, inquiryField);
        this.inquiryFields.add(inquiryField);
	}

	public void addSummaryField(FieldDefinition inquiryField) {
		if (inquiryField == null) {
			throw new IllegalArgumentException("invalid (null) inquiryField");
		}

		String itemName = inquiryField.getAttributeName();
		if (this.summaryFieldMap.containsKey(itemName)) {
			throw new DuplicateEntryException(
					"duplicate itemName entry for item '" + itemName + "'");
		}

		this.summaryFieldMap.put(itemName, inquiryField);
		this.summaryFields.add(inquiryField);
	}

	/**
	 * @param inquiryCollection
	 * @throws IllegalArgumentException
	 *             if the given maintainableCollection is null
	 */
	public void addInquiryCollection(
			InquiryCollectionDefinition inquiryCollection) {
		if (inquiryCollection == null) {
			throw new IllegalArgumentException(
					"invalid (null) inquiryCollection");
		}

		String fieldName = inquiryCollection.getName();
		if (this.inquiryCollectionMap.containsKey(fieldName)) {
			throw new DuplicateEntryException(
					"duplicate fieldName entry for field '" + fieldName + "'");
		}

		this.inquiryCollectionMap.put(fieldName, inquiryCollection);
        this.inquiryCollections.add(inquiryCollection);
	}


	public List<? extends CollectionDefinitionI> getCollections() {
		return this.getInquiryCollections();
	}

	public List<? extends FieldDefinitionI> getFields() {
		// TODO Auto-generated method stub
		return this.getInquiryFields();
	}

	public boolean getIncludeAddLine() {
		return false;
	}


	public boolean hasSummaryField(String key) {
		return this.summaryFieldMap.containsKey(key);
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

    public void setInquiryFields(List<FieldDefinition> inquiryFields) {
        for ( FieldDefinition inquiryField : inquiryFields ) {
            if (inquiryField == null) {
                throw new IllegalArgumentException("invalid (null) inquiryField");
            }
    
            String itemName = inquiryField.getAttributeName();
            if (this.inquiryFieldMap.containsKey(itemName)) {
                throw new DuplicateEntryException(
                        "duplicate itemName entry for item '" + itemName + "'");
            }
    
            this.inquiryFieldMap.put(itemName, inquiryField);
        }

        this.inquiryFields = inquiryFields;
    }

    public List<InquiryCollectionDefinition> getInquiryCollections() {
        return this.inquiryCollections;
    }

    public void setInquiryCollections(List<InquiryCollectionDefinition> inquiryCollections) {
        for ( InquiryCollectionDefinition inquiryCollection : inquiryCollections ) {
            if (inquiryCollection == null) {
                throw new IllegalArgumentException(
                        "invalid (null) inquiryCollection");
            }

            String fieldName = inquiryCollection.getName();
            if (this.inquiryCollectionMap.containsKey(fieldName)) {
                throw new DuplicateEntryException(
                        "duplicate fieldName entry for field '" + fieldName + "'");
            }

            this.inquiryCollectionMap.put(fieldName, inquiryCollection);
        }
        this.inquiryCollections = inquiryCollections;
    }

    public List<FieldDefinition> getSummaryFields() {
        return this.summaryFields;
    }

    public void setSummaryFields(List<FieldDefinition> summaryFields) {
        for ( FieldDefinition inquiryField : summaryFields ) {
            if (inquiryField == null) {
                throw new IllegalArgumentException("invalid (null) summaryField");
            }
    
            String itemName = inquiryField.getAttributeName();
            if (this.summaryFieldMap.containsKey(itemName)) {
                throw new DuplicateEntryException(
                        "duplicate itemName entry for item '" + itemName + "'");
            }
    
            this.summaryFieldMap.put(itemName, inquiryField);
        }
        this.summaryFields = summaryFields;
    }
	
	
}
