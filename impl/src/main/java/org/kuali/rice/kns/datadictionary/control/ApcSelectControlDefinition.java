/*
 * Copyright 2006-2007 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.control;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.lookup.keyvalues.ApcValuesFinder;

/**
    The apcSelect element will render an HTML select control.
    The values for the select control are obtained from
    parameter table in the database.
    
    paramNamespace, parameterDetailType, and parameterName uniquely identify the parameter from which the select's
        values will be derived
 */
public class ApcSelectControlDefinition extends SelectControlDefinition {
    private static final long serialVersionUID = 7068651175290924411L;
    
	protected String parameterNamespace;
    protected String parameterDetailType;
    protected String parameterName;

    public ApcSelectControlDefinition() {
        super();
        setValuesFinderClass(ApcValuesFinder.class.getName());
    }

    public String getParameterNamespace() {
        return parameterNamespace;
    }

    /**
     * Used by the ApcSelectControlDefinition to pull the needed parameter from the ParameterService.
     */
    public void setParameterNamespace(String parameterNamespace) {
        if (StringUtils.isBlank(parameterNamespace)) {
            throw new IllegalArgumentException("invalid (blank) parameterNamespace in <apcSelect>");
        }
        this.parameterNamespace = parameterNamespace;
    }

    public String getParameterName() {
        return parameterName;
    }

    /**
     * Used by the ApcSelectControlDefinition to pull the needed parameter from the ParameterService.
     */
    public void setParameterName(String parameterName) {
        if (StringUtils.isBlank(parameterName)) {
            throw new IllegalArgumentException("invalid (blank) parameterName in <apcSelect>");
        }
        this.parameterName = parameterName;
    }

    public boolean isApcSelect() {
        return true;
    }

	public String getParameterDetailType() {
		return this.parameterDetailType;
	}

    /**
     * Used by the ApcSelectControlDefinition to pull the needed parameter from the ParameterService.
     */
	public void setParameterDetailType(String parameterDetailType) {
        if (StringUtils.isBlank(parameterDetailType)) {
            throw new IllegalArgumentException("invalid (blank) parameterDetailType in <apcSelect>");
        }
		this.parameterDetailType = parameterDetailType;
	}

}
