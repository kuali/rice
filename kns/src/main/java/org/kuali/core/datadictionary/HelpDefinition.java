/*
 * Copyright 2006 The Kuali Foundation.
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

/**
 */
public class HelpDefinition extends DataDictionaryDefinitionBase {

    private String parameterNamespace;
    private String parameterDetailType;
    private String parameterName;

    private static Log LOG = LogFactory.getLog(HelpDefinition.class);

    /**
     * Constructs a HelpDefinition.
     */
    public HelpDefinition() {
        super();
        LOG.debug("creating new HelpDefinition");
    }

    /**
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        // No real validation to be done here other than perhaps checking to be
        // sure that the security workgroup is a valid workgroup.
    }

    /**
     * @return
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @param parameterName
     */
    public void setParameterName(String parameterName) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("invalid (blank) parameterName");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling setParameterName '" + parameterName + "'");
        }
        this.parameterName = parameterName;
    }

    /**
     * @return
     */
    public String getParameterNamespace() {
        return parameterNamespace;
    }

    /**
     * @param parameterNamespace
     */
    public void setParameterNamespace(String parameterNamespace) {
        if (StringUtils.isBlank(parameterNamespace)) {
            throw new IllegalArgumentException("invalid (blank) parameterNamespace");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("calling setParameterNamespace '" + parameterNamespace + "'");
        }
        this.parameterNamespace = parameterNamespace;
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
