/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

/**
 * Contains a pair of attributeNames, accessible as fieldTo and fieldFrom.
 * 
 * 
 */
public class FieldPairDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log log = LogFactory.getLog(FieldPairDefinition.class);

    private String fieldTo;
    private String fieldFrom;

    public FieldPairDefinition() {
        log.debug("creating new FieldPairDefinition");
    }


    public String getFieldFrom() {
        return fieldFrom;
    }

    public void setFieldFrom(String fieldFrom) {
        if (StringUtils.isBlank(fieldFrom)) {
            throw new IllegalArgumentException("invalid (blank) fieldFrom");
        }

        this.fieldFrom = fieldFrom;
    }

    public String getFieldTo() {
        return fieldTo;
    }

    public void setFieldTo(String fieldTo) {
        if (StringUtils.isBlank(fieldTo)) {
            throw new IllegalArgumentException("invalid (blank) fieldTo");
        }

        this.fieldTo = fieldTo;
    }


    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, fieldTo)) {
            throw new AttributeValidationException("unable to find attribute '" + fieldTo + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        if (!validationCompletionUtils.isPropertyOf(otherBusinessObjectClass, fieldFrom)) {
            throw new AttributeValidationException("unable to find attribute '" + fieldFrom + "' in otherBusinessObjectClass '" + otherBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "FieldPairDefinition for (to,from) => (" + getFieldTo() + "," + getFieldFrom() + ")";
    }
}