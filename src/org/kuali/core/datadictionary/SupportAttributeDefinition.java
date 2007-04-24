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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

public class SupportAttributeDefinition extends PrimitiveAttributeDefinition {
    // logger
    private static Log LOG = LogFactory.getLog(SupportAttributeDefinition.class);

    private boolean identifier;
    
    public SupportAttributeDefinition() {
        LOG.debug("creating new SupportAttributeDefinition");
    }

    public boolean isIdentifier() {
        return identifier;
    }
    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        String sourceClassName = rootBusinessObjectClass.getName();
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, getSourceName())) {
            throw new AttributeValidationException("unable to find attribute '" + getSourceName() + "' in relationship class '" + rootBusinessObjectClass + "' (" + getParseLocation() + ")");
        }
        String targetClassName = otherBusinessObjectClass.getName();
        if (!validationCompletionUtils.isPropertyOf(otherBusinessObjectClass, getTargetName())) {
            throw new AttributeValidationException("unable to find attribute '" + getTargetName() + "' in related class '" + otherBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

//        Class sourceClass = validationCompletionUtils.getAttributeClass(rootBusinessObjectClass, getSourceName());
//        Class targetClass = validationCompletionUtils.getAttributeClass(otherBusinessObjectClass, getTargetName());
//        if (!StringUtils.equals(sourceClass.getName(), targetClass.getName())) {
//            String sourcePath = sourceClassName + "." + getSourceName();
//            String targetPath = targetClassName + "." + getTargetName();
//
//            throw new AttributeValidationException("source attribute '" + sourcePath + "' and target attribute '" + targetPath + "' are of differing types (" + getParseLocation() + ")");
//        }
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SupportAttributeDefinition (" + getSourceName()+","+getTargetName()+","+identifier+")";
    }
    
}
