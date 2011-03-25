/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kns.datadictionary;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
            The help element provides the keys to obtain a
            help description from the database.

            On document JSP pages, a help icon may be rendered.  If this tag is specified, then
            the filename of this page will be located in the value of the parameter specified by the namespace, detail type, and name.

            The value of the parameter is relative to the value of the "externalizable.help.url" property in ConfigurationService (see KualiHelpAction).
            parameterNamespace: namespace of the parameter that has the path to the help page
            parameterName: name of the parameter that has the path to the help page
            parameterDetailType: detail type of the parameter that has the path to the help page
 */
public class HelpDefinition extends DataDictionaryDefinitionBase implements Serializable {
    private static final long serialVersionUID = -6869646654597012863L;
    
	protected String parameterNamespace;
    protected String parameterDetailType;
    protected String parameterName;

    /**
     * Constructs a HelpDefinition.
     */
    public HelpDefinition() {}

    /**
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
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
     * parameterName: name of the parameter that has the path to the help page
     */
    public void setParameterName(String parameterName) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("invalid (blank) parameterName");
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
     * parameterNamespace: namespace of the parameter that has the path to the help page
     */
    public void setParameterNamespace(String parameterNamespace) {
        if (StringUtils.isBlank(parameterNamespace)) {
            throw new IllegalArgumentException("invalid (blank) parameterNamespace");
        }
        this.parameterNamespace = parameterNamespace;
    }

	public String getParameterDetailType() {
		return this.parameterDetailType;
	}

	/**
	 * parameterDetailType: detail type of the parameter that has the path to the help page
	 */
	public void setParameterDetailType(String parameterDetailType) {
        if (StringUtils.isBlank(parameterDetailType)) {
            throw new IllegalArgumentException("invalid (blank) parameterDetailType");
        }
		this.parameterDetailType = parameterDetailType;
	}

}
