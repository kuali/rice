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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

/**
 * A single Relationship definition in the DataDictionary, which contains information concerning which primitive attributes of this
 * class can be used to retrieve an instance of some related Object instance
 * 
 * 
 */
public class RelationshipDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(RelationshipDefinition.class);

    private String objectAttributeName;
    private Class sourceClass;
    private Class targetClass;

    private List<PrimitiveAttributeDefinition> primitiveAttributes;
    private List<SupportAttributeDefinition> supportAttributes;


    public RelationshipDefinition() {
        LOG.debug("creating new RelationshipDefinition");

        primitiveAttributes = new ArrayList<PrimitiveAttributeDefinition>();
        supportAttributes = new ArrayList<SupportAttributeDefinition>();
    }

    public String getObjectAttributeName() {
        return objectAttributeName;
    }

    public Class getSourceClass() {
        return sourceClass;
    }

    public Class getTargetClass() {
        return targetClass;
    }


    public void setObjectAttributeName(String objectAttributeName) {
        if (StringUtils.isBlank(objectAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) objectAttributeName");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setObjectAttributeName '" + objectAttributeName + "'");
        }

        this.objectAttributeName = objectAttributeName;
    }


    public void addPrimitiveAttributeDefinition(PrimitiveAttributeDefinition primitiveAttributeDefinition) {
        if (primitiveAttributeDefinition == null) {
            throw new IllegalArgumentException("invalid (null) primitiveAttributeDefinition");
        }

        primitiveAttributes.add(primitiveAttributeDefinition);
    }

    public void addSupportAttributeDefinition(SupportAttributeDefinition supportAttributeDefinition) {
        if (supportAttributeDefinition == null) {
            throw new IllegalArgumentException("invalid (null) supportAttributeDefinition");
        }

        supportAttributes.add(supportAttributeDefinition);
    }

    public List<PrimitiveAttributeDefinition> getPrimitiveAttributes() {
        return Collections.unmodifiableList(primitiveAttributes);
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
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        String propertyName = objectAttributeName;
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, propertyName)) {
            throw new AttributeValidationException("property '" + propertyName + "' is not an attribute of class '" + rootBusinessObjectClass + "' (" + getParseLocation() + ")");
        }
        Class propertyClass = validationCompletionUtils.getAttributeClass(rootBusinessObjectClass, propertyName);
        if (!validationCompletionUtils.isDescendentClass(propertyClass, BusinessObject.class)) {
            throw new AttributeValidationException("property '" + propertyName + "' is not a BusinessObject (" + getParseLocation() + ")");
        }

        sourceClass = rootBusinessObjectClass;
        targetClass = propertyClass;

        for (PrimitiveAttributeDefinition primitiveAttributeDefinition : primitiveAttributes) {
            primitiveAttributeDefinition.completeValidation(rootBusinessObjectClass, propertyClass, validationCompletionUtils);
        }
        for (SupportAttributeDefinition supportAttributeDefinition : supportAttributes) {
            supportAttributeDefinition.completeValidation(rootBusinessObjectClass, propertyClass, validationCompletionUtils);
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RelationshipDefinition for relationship " + getObjectAttributeName();
    }
}
