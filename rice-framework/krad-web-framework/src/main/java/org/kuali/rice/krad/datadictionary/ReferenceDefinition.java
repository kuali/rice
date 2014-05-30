/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;

/**
 * The reference element specifies the name of a reference
 * object that is required to exist in order for the primary
 * business object to be created or modified on a BO.
 *
 * DD: See ReferenceDefinition.java
 *
 * JSTL: references are Maps with the following keys:
 * attributeName (String)
 * activeIndicatorAttributeName (String)
 * activeIndicatorReversed (boolean String)
 * attributeToHighlightOnFail (String)
 * displayFieldName (String)
 */
@BeanTag(name = "referenceDefinition")
public class ReferenceDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = 1737968024207302931L;

    protected String attributeName;
    protected String attributeToHighlightOnFail;
    protected String displayFieldName;
    protected String collection;
    protected Class<?> collectionBusinessObjectClass;
    protected Class<?> businessObjectClass;

    public ReferenceDefinition() {}

    /**
     * @return attributeName
     */
    @BeanTagAttribute(name = "attributeName")
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * attributeName is the name of a reference object that
     * must exist and not be null.  In the case of a collection,
     * then this is the name of a reference object within the
     * collection element.
     *
     * @throws IllegalArgumentException if the given attributeName is blank
     */
    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        this.attributeName = attributeName;
    }

    /**
     * Gets the attributeToHighlightOnFail attribute.
     *
     * @return Returns the attributeToHighlightOnFail.
     */
    @BeanTagAttribute(name = "attributeToHighlightOnFail")
    public String getAttributeToHighlightOnFail() {
        return attributeToHighlightOnFail;
    }

    /**
     * attributeToHighlightOnFail is the name of the busines
     * object attribute which will be highlighted when
     * the default existence check fails.
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
    @BeanTagAttribute(name = "displayFieldName")
    public String getDisplayFieldName() {
        return displayFieldName;
    }

    /**
     * displayFieldName is the name of the field to pull the label as it will
     * appear in an error message.  e.g. "chartOfAccountsCode".
     */
    public void setDisplayFieldName(String displayFieldName) {
        this.displayFieldName = displayFieldName;
    }

    /**
     * This method returns true if the displayFieldName is set, otherwise it returns false. Whether
     * the displayFieldName is set is defined by whether it has any non-whitespace content in it.
     * 
     * @return true if the display field name is set
     */
    public boolean isDisplayFieldNameSet() {
        return StringUtils.isNotBlank(displayFieldName);
    }

    @BeanTagAttribute(name = "collection")
    public String getCollection() {
        return collection;
    }

    /**
     * collection is the name of a collection that must exist
     */
    public void setCollection(String collection) {
        this.collection = collection;
    }

    public boolean isCollectionReference() {
        return StringUtils.isNotBlank(getCollection());
    }

    @BeanTagAttribute(name = "collectionBusinessObjectClass")
    public Class<?> getCollectionBusinessObjectClass() {
        return collectionBusinessObjectClass;
    }

    /**
     * Class that the specified collection represents.  Does not need to be set.  The DD
     * Will set this attribute through introspection.
     */
    public void setCollectionBusinessObjectClass(Class<?> collectionBusinessObjectClass) {
        this.collectionBusinessObjectClass = collectionBusinessObjectClass;
    }

    @Override
    public void dataDictionaryPostProcessing() {
        super.dataDictionaryPostProcessing();
        if (collectionBusinessObjectClass == null && isCollectionReference()) {
            collectionBusinessObjectClass = DataDictionary.getCollectionElementClass(businessObjectClass, collection);
        }
    }

    @Override
    public void completeValidation(Class<?> rootBusinessObjectClass, Class<?> otherBusinessObjectClass, ValidationTrace tracer) {
        String tmpAttributeName = isCollectionReference() ? collection : attributeName;
        // make sure the attributeName is actually a property of the BO
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, tmpAttributeName)) {
            String currentValues[] = {"rootBusinessObjectClass = " + rootBusinessObjectClass.getName(),
                    "attribute = " + tmpAttributeName};
            tracer.createError("ReferenceDefinition attribute does not exist on parent object",
                    currentValues);
        }
        // make sure the attributeToHighlightOnFail is actually a property of the BO
        if (isCollectionReference()) {
            if (collectionBusinessObjectClass == null) {
                String currentValues[] = {"rootBusinessObjectClass = " + rootBusinessObjectClass.getName(),
                        "attribute = " + tmpAttributeName};
                tracer.createError("ReferenceDefinition: Unable to determine BO class for collection",
                        currentValues);
            } else {
	            if (!DataDictionary.isPropertyOf(collectionBusinessObjectClass, attributeToHighlightOnFail)) {
                    String currentValues[] = {"collectionBusinessObjectClass = " + collectionBusinessObjectClass.getName(),
                            "attributeToHighlightOnFail = " + attributeToHighlightOnFail};
                    tracer.createError("ReferenceDefinition: attributeToHighlightOnFail does not exist on collection class",
                            currentValues);
	            }
            }
        } else {
        	if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, attributeToHighlightOnFail)) {
                String currentValues[] = {"rootBusinessObjectClass = " + rootBusinessObjectClass.getName(),
                        "attributeToHighlightOnFail = " + attributeToHighlightOnFail};
                tracer.createError("ReferenceDefinition: attributeToHighlightOnFail does not exist on parent class",
                        currentValues);
        	}
        }
    }


    @BeanTagAttribute(name = "businessObjectClass")
    public Class<?> getBusinessObjectClass() {
        return businessObjectClass;
    }

    public void setBusinessObjectClass(Class<?> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }
}
