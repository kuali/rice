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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

/**
 * A single Relationship definition in the DataDictionary, which contains information concerning which primitive attributes of this
 * class can be used to retrieve an instance of some related Object instance
 * 
 * 
 */
public class RelationshipDefinition extends DataDictionaryDefinitionBase {

    private String objectAttributeName;
    private Class<? extends BusinessObject> sourceClass;
    private Class<? extends BusinessObject> targetClass;

    private List<PrimitiveAttributeDefinition> primitiveAttributes = new ArrayList<PrimitiveAttributeDefinition>();
    private List<SupportAttributeDefinition> supportAttributes = new ArrayList<SupportAttributeDefinition>();


    public RelationshipDefinition() {}

    public String getObjectAttributeName() {
        return objectAttributeName;
    }

    public Class<? extends BusinessObject> getSourceClass() {
        return sourceClass;
    }

    public Class<? extends BusinessObject> getTargetClass() {
        return targetClass;
    }


    public void setObjectAttributeName(String objectAttributeName) {
        if (StringUtils.isBlank(objectAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) objectAttributeName");
        }

        this.objectAttributeName = objectAttributeName;
    }

    public List<PrimitiveAttributeDefinition> getPrimitiveAttributes() {
        return primitiveAttributes;
    }    

    public List<SupportAttributeDefinition> getSupportAttributes() {
        return supportAttributes;
    }

    public boolean hasIdentifier() {
        for (SupportAttributeDefinition supportAttributeDefinition : supportAttributes) {
            if ( supportAttributeDefinition.isIdentifier() ) {
                return true;
            }
        }
        return false;
    }
    
    public SupportAttributeDefinition getIdentifier() {
        for (SupportAttributeDefinition supportAttributeDefinition : supportAttributes) {
            if ( supportAttributeDefinition.isIdentifier() ) {
                return supportAttributeDefinition;
            }
        }
        return null;
    }
    
    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        String propertyName = objectAttributeName;
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, propertyName)) {
            throw new AttributeValidationException("property '" + propertyName + "' is not an attribute of class '" + rootBusinessObjectClass + "' (" + "" + ")");
        }
        Class propertyClass = DataDictionary.getAttributeClass(rootBusinessObjectClass, propertyName);
        if (!BusinessObject.class.isAssignableFrom(propertyClass)) {
            throw new AttributeValidationException("property '" + propertyName + "' is not a BusinessObject (" + "" + ")");
        }

        sourceClass = rootBusinessObjectClass;
        targetClass = propertyClass;

        for (PrimitiveAttributeDefinition primitiveAttributeDefinition : primitiveAttributes) {
            primitiveAttributeDefinition.completeValidation(rootBusinessObjectClass, propertyClass);
        }
        for (SupportAttributeDefinition supportAttributeDefinition : supportAttributes) {
            supportAttributeDefinition.completeValidation(rootBusinessObjectClass, propertyClass);
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RelationshipDefinition for relationship " + getObjectAttributeName();
    }

    public void setPrimitiveAttributes(List<PrimitiveAttributeDefinition> primitiveAttributes) {
        this.primitiveAttributes = primitiveAttributes;
    }

    public void setSupportAttributes(List<SupportAttributeDefinition> supportAttributes) {
        this.supportAttributes = supportAttributes;
    }
}
