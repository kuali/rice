/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.docsearch;



/**
 * This is a container object used by KEW Document Search 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class StandardSearchCriteriaField implements java.io.Serializable {
	
	private static final long serialVersionUID = -8499396401490695140L;
	
    public static final String TEXT = "text";
    public static final String DROPDOWN = "dropdown";
    public static final String DROPDOWN_HIDE_EMPTY = "dropdown_hide_empty";

    private String key;
	private String property;
	private String fieldType;
	private boolean hidden = false;
	private String displayOnlyPropertyName = null;
	private String datePickerKey = null;
	private String lookupableImplServiceName = null;
	private boolean lookupTypeRequired = false;
	private String labelMessageKey;
	private String helpMessageKeyArgument;
	
	// values for field types with multiple options
	private String optionsCollectionProperty;
	private String collectionLabelProperty;
	private String collectionKeyProperty;
	private String emptyCollectionMessage;

	public StandardSearchCriteriaField() {}
	
    public StandardSearchCriteriaField(String fieldKey, String propertyName, String fieldType, String datePickerKey, String labelMessageKey, String helpMessageKeyArgument, boolean hidden, String displayOnlyPropertyName, String lookupableImplServiceName, boolean lookupTypeRequired) {
    	setupField(fieldKey, propertyName, fieldType, datePickerKey, labelMessageKey, helpMessageKeyArgument, hidden, displayOnlyPropertyName, lookupableImplServiceName, lookupTypeRequired);
    }

    private void setupField(String fieldKey, String propertyName, String fieldType, String datePickerKey, String labelMessageKey, String helpMessageKeyArgument, boolean hidden, String displayOnlyPropertyName, String lookupableImplServiceName, boolean lookupTypeRequired) {
    	this.key = fieldKey;
    	this.property = propertyName;
    	this.fieldType = fieldType;
    	this.datePickerKey = datePickerKey;
    	this.labelMessageKey = labelMessageKey;
    	this.helpMessageKeyArgument = helpMessageKeyArgument;
    	this.hidden = hidden;
    	this.displayOnlyPropertyName = displayOnlyPropertyName;
    	this.lookupableImplServiceName = lookupableImplServiceName;
    	this.lookupTypeRequired = lookupTypeRequired;
    }

    public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getProperty() {
		return this.property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getFieldType() {
		return this.fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public boolean isHidden() {
		return this.hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public String getDisplayOnlyPropertyName() {
		return this.displayOnlyPropertyName;
	}
	public void setDisplayOnlyPropertyName(String displayOnlyPropertyName) {
		this.displayOnlyPropertyName = displayOnlyPropertyName;
	}
	public String getDatePickerKey() {
		return this.datePickerKey;
	}
	public void setDatePickerKey(String datePickerKey) {
		this.datePickerKey = datePickerKey;
	}
	public String getLookupableImplServiceName() {
		return this.lookupableImplServiceName;
	}
	public void setLookupableImplServiceName(String lookupableImplServiceName) {
		this.lookupableImplServiceName = lookupableImplServiceName;
	}
	public boolean isLookupTypeRequired() {
		return this.lookupTypeRequired;
	}
	public void setLookupTypeRequired(boolean lookupTypeRequired) {
		this.lookupTypeRequired = lookupTypeRequired;
	}
	public String getLabelMessageKey() {
		return this.labelMessageKey;
	}
	public void setLabelMessageKey(String labelMessageKey) {
		this.labelMessageKey = labelMessageKey;
	}
	public String getHelpMessageKeyArgument() {
		return this.helpMessageKeyArgument;
	}
	public void setHelpMessageKeyArgument(String helpMessageKeyArgument) {
		this.helpMessageKeyArgument = helpMessageKeyArgument;
	}

    public String getOptionsCollectionProperty() {
		return this.optionsCollectionProperty;
	}

	public void setOptionsCollectionProperty(String optionsCollectionProperty) {
		this.optionsCollectionProperty = optionsCollectionProperty;
	}

	public String getCollectionLabelProperty() {
		return this.collectionLabelProperty;
	}

	public void setCollectionLabelProperty(String collectionLabelProperty) {
		this.collectionLabelProperty = collectionLabelProperty;
	}

	public String getCollectionKeyProperty() {
		return this.collectionKeyProperty;
	}

	public void setCollectionKeyProperty(String collectionKeyProperty) {
		this.collectionKeyProperty = collectionKeyProperty;
	}

	public String getEmptyCollectionMessage() {
		return this.emptyCollectionMessage;
	}

	public void setEmptyCollectionMessage(String emptyCollectionMessage) {
		this.emptyCollectionMessage = emptyCollectionMessage;
	}

	/**
     * @return the tEXT
     */
    public String getTEXT() {
        return TEXT;
    }
    
    /**
     * @return the dROPDOWN
     */
    public String getDROPDOWN() {
        return DROPDOWN;
    }

    /**
     * @return the dROPDOWN
     */
    public String getDROPDOWN_HIDE_EMPTY() {
        return DROPDOWN_HIDE_EMPTY;
    }

}
