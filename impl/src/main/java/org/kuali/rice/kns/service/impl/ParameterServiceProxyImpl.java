/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.util.List;

import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;

/**
 * ParameterServiceProxyImpl is an implementation of ParameterServiceProxy
 * that performs simple proxying of storage/retrieval calls to a remoted
 * ParameterService implementation.
 * 
 * TODO this class still needs caching implemented!!!
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ParameterServiceProxyImpl implements ParameterService {
	
    private ParameterService parameterService;    

	public Parameter retrieveParameter(String namespaceCode, String detailTypeCode,
			String parameterName) {
		return getParameterService().retrieveParameter(namespaceCode, detailTypeCode, parameterName);
	}
    
	public void setParameterForTesting(Class componentClass, String parameterName, String parameterText) {
		getParameterService().setParameterForTesting(componentClass, parameterName, parameterText);
	}
	
	

	public void clearCache() {
		getParameterService().clearCache();
	}

	public String getDetailType(Class<? extends Object> documentOrStepClass) {
		return getParameterService().getDetailType(documentOrStepClass);
	}

	public boolean getIndicatorParameter(
			Class<? extends Object> componentClass, String parameterName) {
		return getParameterService().getIndicatorParameter(componentClass,
				parameterName);
	}

	public String getNamespace(Class<? extends Object> documentOrStepClass) {
		return getParameterService().getNamespace(documentOrStepClass);
	}

	public ParameterEvaluator getParameterEvaluator(
			Class<? extends Object> componentClass, String allowParameterName,
			String denyParameterName, String constrainingValue,
			String constrainedValue) {
		return getParameterService().getParameterEvaluator(componentClass,
				allowParameterName, denyParameterName, constrainingValue,
				constrainedValue);
	}

	public ParameterEvaluator getParameterEvaluator(
			Class<? extends Object> componentClass, String parameterName,
			String constrainingValue, String constrainedValue) {
		return getParameterService().getParameterEvaluator(componentClass,
				parameterName, constrainingValue, constrainedValue);
	}

	public ParameterEvaluator getParameterEvaluator(
			Class<? extends Object> componentClass, String parameterName,
			String constrainedValue) {
		return getParameterService().getParameterEvaluator(componentClass,
				parameterName, constrainedValue);
	}

	public ParameterEvaluator getParameterEvaluator(
			Class<? extends Object> componentClass, String parameterName) {
		return getParameterService().getParameterEvaluator(componentClass,
				parameterName);
	}

	public String getParameterValue(Class<? extends Object> componentClass,
			String parameterName, String constrainingValue) {
		return getParameterService().getParameterValue(componentClass,
				parameterName, constrainingValue);
	}

	public String getParameterValue(Class<? extends Object> componentClass,
			String parameterName) {
		return getParameterService().getParameterValue(componentClass,
				parameterName);
	}

	public List<String> getParameterValues(
			Class<? extends Object> componentClass, String parameterName,
			String constrainingValue) {
		return getParameterService().getParameterValues(componentClass,
				parameterName, constrainingValue);
	}

	public List<String> getParameterValues(
			Class<? extends Object> componentClass, String parameterName) {
		return getParameterService().getParameterValues(componentClass,
				parameterName);
	}

	public boolean parameterExists(Class<? extends Object> componentClass,
			String parameterName) {
		return getParameterService().parameterExists(componentClass,
				parameterName);
	}

	public ParameterService getParameterService() {
		if ( parameterService == null ) {
			parameterService = KNSServiceLocator.getParameterServerService();
		}
		return parameterService;
	}
	
}
