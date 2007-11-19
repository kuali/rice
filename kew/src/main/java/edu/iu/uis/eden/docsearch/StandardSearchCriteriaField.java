/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.docsearch;



/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class StandardSearchCriteriaField implements java.io.Serializable {
	
	private static final long serialVersionUID = -8499396401490695140L;
	
    public static final String TEXT = "text";
    public static final String DROPDOWN = "dropdown";

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

    /**
     * @return the tEXT
     */
    public String getTEXT() {
        return TEXT;
    }

}
