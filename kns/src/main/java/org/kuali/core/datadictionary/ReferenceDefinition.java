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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

public class ReferenceDefinition extends DataDictionaryDefinitionBase {


    private String attributeName;
    private String activeIndicatorAttributeName;
    private boolean activeIndicatorReversed = false;
    private String attributeToHighlightOnFail;
    private String displayFieldName;
    private String collection;
    private Class<? extends BusinessObject> collectionBusinessObjectClass;
    
    public ReferenceDefinition() {}

    /**
     * @return attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Sets attributeName to the given value.
     * 
     * @param attributeName
     * @throws IllegalArgumentException if the given attributeName is blank
     */
    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        this.attributeName = attributeName;
    }

    /**
     * @return activeIndicatorAttributeName
     */
    public String getActiveIndicatorAttributeName() {
        return activeIndicatorAttributeName;
    }

    /**
     * Sets activeIndicatorAttributeName to the given value.
     * 
     * @param attributeName
     * @throws IllegalArgumentException if the given activeIndicatorAttributeName is blank
     */
    public void setActiveIndicatorAttributeName(String activeIndicatorAttributeName) {
        this.activeIndicatorAttributeName = activeIndicatorAttributeName;
    }

    /**
     * Gets the activeIndicatorReversed attribute.
     * 
     * @return Returns the activeIndicatorReversed.
     */
    public boolean isActiveIndicatorReversed() {
        return activeIndicatorReversed;
    }

    /**
     * Sets the activeIndicatorReversed attribute value.
     * 
     * @param activeIndicatorReversed The activeIndicatorReversed to set.
     */
    public void setActiveIndicatorReversed(boolean activeIndicatorReversed) {
        this.activeIndicatorReversed = activeIndicatorReversed;
    }

    /**
     * Gets the attributeToHighlightOnFail attribute.
     * 
     * @return Returns the attributeToHighlightOnFail.
     */
    public String getAttributeToHighlightOnFail() {
        return attributeToHighlightOnFail;
    }

    /**
     * Sets the attributeToHighlightOnFail attribute value.
     * 
     * @param attributeToHighlightOnFail The attributeToHighlightOnFail to set.
     */
    public void setAttributeToHighlightOnFail(String attributeToHighlightOnFail) {
        if (StringUtils.isBlank(attributeToHighlightOnFail)) {
            throw new IllegalArgumentException("invalid (blank) attributeToHighlightOnFail");
        }
        this.attributeToHighlightOnFail = attributeToHighlightOnFail;
    }

    /**
     * Gets the displayFieldName attribute.
     * 
     * @return Returns the displayFieldName.
     */
    public String getDisplayFieldName() {
        return displayFieldName;
    }

    /**
     * Sets the displayFieldName attribute value.
     * 
     * @param displayFieldName The displayFieldName to set.
     */
    public void setDisplayFieldName(String displayFieldName) {
        this.displayFieldName = displayFieldName;
    }

    /**
     * This method returns true if the displayFieldName is set, otherwise it returns false. Whether the displayFieldName is set is
     * defined by whether it has any non-whitespace content in it.
     * 
     * @return
     */
    public boolean isDisplayFieldNameSet() {
        return StringUtils.isNotBlank(displayFieldName);
    }

    /**
     * @return Returns true if there is an ActiveIndicatorAttributeName set, false if not.
     */
    public boolean isActiveIndicatorSet() {
        return StringUtils.isNotBlank(activeIndicatorAttributeName);
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public boolean isCollectionReference() {
        return StringUtils.isNotBlank(getCollection());
    }

    public Class<? extends BusinessObject> getCollectionBusinessObjectClass() {
        return collectionBusinessObjectClass;
    }

    public void setCollectionBusinessObjectClass(Class<? extends BusinessObject> collectionBusinessObjectClass) {
        this.collectionBusinessObjectClass = collectionBusinessObjectClass;
    }

    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {


        // make sure the attributeName is actually a property of the BO
        String tmpAttributeName = isCollectionReference() ? collection : attributeName;
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, tmpAttributeName)) {
            throw new AttributeValidationException("unable to find attribute '" + tmpAttributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }
        if(isCollectionReference()){
            collectionBusinessObjectClass=DataDictionary.getCollectionElementClass(rootBusinessObjectClass, collection);
        }
        // if there's an activeIndicator set, then validate it
        if (isActiveIndicatorSet()) {

            // make sure named activeIndicator field exists in the reference class

            Class referenceClass = isCollectionReference() ? DataDictionary.getAttributeClass(collectionBusinessObjectClass, attributeName) : DataDictionary.getAttributeClass(rootBusinessObjectClass, attributeName);

            if (!DataDictionary.isPropertyOf(referenceClass, activeIndicatorAttributeName)) {
                throw new AttributeValidationException("unable to find attribute '" + activeIndicatorAttributeName + "' in reference class '" + referenceClass.getName() + "' (" + "" + ")");
            }

            // make sure named activeIndicator field is a boolean in the reference class
            Class activeIndicatorClass = DataDictionary.getAttributeClass(referenceClass, activeIndicatorAttributeName);
            if (!activeIndicatorClass.equals(boolean.class)) {
                throw new AttributeValidationException("Active Indicator Attribute Name '" + activeIndicatorAttributeName + "' in reference class '" + referenceClass.getName() + "' is not a boolean, it is a '" + activeIndicatorClass.getName() + "' " + " (" + "" + ")");
            }

        }

        // make sure the attributeToHighlightOnFail is actually a property of the BO
        if (isCollectionReference()) {

            if (!DataDictionary.isPropertyOf(collectionBusinessObjectClass, attributeToHighlightOnFail)) {
                throw new AttributeValidationException("unable to find attribute '" + attributeToHighlightOnFail + "' in collectionBusinessObjectClass '" + collectionBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }
        else {
            if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, attributeToHighlightOnFail)) {
                throw new AttributeValidationException("unable to find attribute '" + attributeToHighlightOnFail + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }

    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ReferenceDefinition for attribute " + getAttributeName();
    }
}
