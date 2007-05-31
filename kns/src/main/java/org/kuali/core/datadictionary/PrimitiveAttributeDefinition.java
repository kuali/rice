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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

/**
 * Contains field-related information for DataDictionary entries.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 * 
 * 
 */
public class PrimitiveAttributeDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(PrimitiveAttributeDefinition.class);

    private String sourceName;
    private String targetName;

    public PrimitiveAttributeDefinition() {
        LOG.debug("creating new PrimitiveAttributeDefinition");
    }


    /**
     * @return sourceName
     */
    public String getSourceName() {
        return sourceName;
    }

    /**
     * Sets sourceName to the given value.
     * 
     * @param sourceName
     * @throws IllegalArgumentException if the given sourceName is blank
     */
    public void setSourceName(String sourceName) {
        if (StringUtils.isBlank(sourceName)) {
            throw new IllegalArgumentException("invalid (blank) sourceName");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setSourceName '" + sourceName + "'");
        }

        this.sourceName = sourceName;
    }


    /**
     * @return targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Sets targetName to the given value.
     * 
     * @param targetName
     * @throws IllegalArgumentException if the given targetName is blank
     */
    public void setTargetName(String targetName) {
        if (StringUtils.isBlank(targetName)) {
            throw new IllegalArgumentException("invalid (blank) targetName");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setTargetName '" + targetName + "'");
        }

        this.targetName = targetName;
    }


    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        String sourceClassName = rootBusinessObjectClass.getName();
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, sourceName)) {
            throw new AttributeValidationException("unable to find attribute '" + sourceName + "' in relationship class '" + rootBusinessObjectClass + "' (" + getParseLocation() + ")");
        }
        String targetClassName = otherBusinessObjectClass.getName();
        if (!validationCompletionUtils.isPropertyOf(otherBusinessObjectClass, targetName)) {
            throw new AttributeValidationException("unable to find attribute '" + targetName + "' in related class '" + otherBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        Class sourceClass = validationCompletionUtils.getAttributeClass(rootBusinessObjectClass, sourceName);
        Class targetClass = validationCompletionUtils.getAttributeClass(otherBusinessObjectClass, targetName);
        if ((null == sourceClass && null != targetClass) || (null != sourceClass && null == targetClass) || !StringUtils.equals(sourceClass.getName(), targetClass.getName())) {
            String sourcePath = sourceClassName + "." + sourceName;
            String targetPath = targetClassName + "." + targetName;

            throw new AttributeValidationException("source attribute '" + sourcePath + "' (" + sourceClass + ") and target attribute '" + targetPath + "' (" + targetClass + ") are of differing types (" + getParseLocation() + ")");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PrimitiveAttributeDefinition (" + getSourceName()+","+getTargetName()+")";
    }
}