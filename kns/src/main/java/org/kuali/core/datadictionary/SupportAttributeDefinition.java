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

import org.kuali.core.datadictionary.exception.AttributeValidationException;

public class SupportAttributeDefinition extends PrimitiveAttributeDefinition {

    private boolean identifier;
    
    public SupportAttributeDefinition() {}

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
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getSourceName())) {
            throw new AttributeValidationException("unable to find attribute '" + getSourceName() + "' in relationship class '" + rootBusinessObjectClass + "' (" + "" + ")");
        }
        if (!DataDictionary.isPropertyOf(otherBusinessObjectClass, getTargetName())) {
            throw new AttributeValidationException("unable to find attribute '" + getTargetName() + "' in related class '" + otherBusinessObjectClass.getName() + "' (" + "" + ")");
        }
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SupportAttributeDefinition (" + getSourceName()+","+getTargetName()+","+identifier+")";
    }
    
}
