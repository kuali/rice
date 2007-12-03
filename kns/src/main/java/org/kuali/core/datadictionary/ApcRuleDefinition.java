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
import org.kuali.core.datadictionary.exception.ClassValidationException;

public class ApcRuleDefinition extends DataDictionaryDefinitionBase {

    private String attributeName;
    private String parameterNamespace;
    private String parameterDetailType;
    private String parameterName;
    private String errorMessage;

    // logger
    private static Log LOG = LogFactory.getLog(ReferenceDefinition.class);


    public ApcRuleDefinition() {
        LOG.debug("creating new ApcRuleDefinition");
    }

    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        
        // make sure the rootBusinessObjectClass is actually a descendent of BusinessObject
        if (!validationCompletionUtils.isBusinessObjectClass(rootBusinessObjectClass)) {
            throw new ClassValidationException("RootBusinessObject is not a descendent of BusinessObject. " + "rootBusinessObjectClass = '" + rootBusinessObjectClass.getName() + "' " + "(" + getParseLocation() + ")");
        }

        // make sure the attributeName is actually a property of the BO
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, attributeName)) {
            throw new AttributeValidationException("unable to find attribute '" + attributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        // make sure that the member referencec by attributeName is actually a string
        Class attributeClass = validationCompletionUtils.getAttributeClass(rootBusinessObjectClass, attributeName);
        if (!attributeClass.equals(String.class)) {
            throw new AttributeValidationException("the attribute '" + attributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' is of type '" + attributeClass.getName() + "'. These attributes may only be string. (" + getParseLocation() + ")");
        }


    }

    public String getParameterNamespace() {
        return parameterNamespace;
    }

    public void setParameterNamespace(String parameterNamespace) {
        if (StringUtils.isBlank(parameterNamespace)) {
            throw new IllegalArgumentException("invalid (blank) parameterNamespace");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling parameterNamespace '" + parameterNamespace + "'");
        }
        this.parameterNamespace = parameterNamespace;
    }

    public String getParameterName() {
        return parameterName;
    }
    void setParameterName(String parameterName) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("invalid (blank) parameterName");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling parameterName '" + parameterName + "'");
        }
        this.parameterName = parameterName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling setAttributeName '" + attributeName + "'");
        }
        this.attributeName = attributeName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        if (StringUtils.isBlank(errorMessage)) {
            throw new IllegalArgumentException("invalid (blank) errorMessage");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling setErrorMessage '" + errorMessage + "'");
        }
        this.errorMessage = errorMessage;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "ApcRuleDefinition for attribute " + getAttributeName();
    }

	public String getParameterDetailType() {
		return this.parameterDetailType;
	}

	public void setParameterDetailType(String parameterDetailType) {
        if (StringUtils.isBlank(parameterDetailType)) {
            throw new IllegalArgumentException("invalid (blank) parameterDetailType");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling setParameterDetailType '" + parameterDetailType + "'");
        }
		this.parameterDetailType = parameterDetailType;
	}
}
