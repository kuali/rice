/*
 * Copyright 2006-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.core.api.parameter;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ClientParameterService;

public class ApcValuesFinder extends KeyValuesBase {

    private String parameterName;
    private String parameterDetailType;
    private String parameterNamespace;

    public String getParameterNamespace() {
        return this.parameterNamespace;
    }

    public void setParameterNamespace(String parameterNamespace) {
        this.parameterNamespace = parameterNamespace;
    }

    public ApcValuesFinder() {
        super();
    }

    public ApcValuesFinder(String parameterNamesapce, String parameterDetailType, String parameterName) {
    	super();
    	this.parameterNamespace = parameterNamespace;
    	this.parameterDetailType = parameterDetailType;
    	this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    @Override
	public List<KeyValue> getKeyValues() {
    	ClientParameterService parameterService = KNSServiceLocator.getClientParameterService();
    	List<KeyValue> activeLabels = new ArrayList<KeyValue>();
    	activeLabels.add(new ConcreteKeyValue("", ""));
    	for (String parm : parameterService.getParameterValuesAsString(parameterNamespace, parameterDetailType, parameterName)) {
    	    activeLabels.add(new ConcreteKeyValue(parm, parm));
    	}
    	return activeLabels;
    }

    public String getParameterDetailType() {
        return this.parameterDetailType;
    }

    public void setParameterDetailType(String parameterDetailType) {
        this.parameterDetailType = parameterDetailType;
    }

}
