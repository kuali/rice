/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * ParameterServiceProxyImpl is an implementation of ParameterServiceProxy
 * that performs simple proxying of storage/retrieval calls to a remoted
 * ParameterService implementation.
 * 
 * TODO this class needs improved caching!!!
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ParameterServiceProxyImpl implements ParameterService {
    private ParameterService parameterService;
    public static final String PARAMETER_CACHE_PREFIX = "ParameterProxy:";
    public static final String PARAMETER_CACHE_GROUP_NAME = "SystemParameterProxy";
    
	public Parameter retrieveParameter(String namespaceCode, String detailTypeCode,
			String parameterName) {
	    Parameter parameter = fetchFromCache(namespaceCode, detailTypeCode, parameterName);
        if (parameter != null) {
            return parameter;
        }
        parameter = getParameterService().retrieveParameter(namespaceCode, detailTypeCode, parameterName);
        insertIntoCache(parameter); 
        return parameter; 
	}
    
	@SuppressWarnings("unchecked")
	public void setParameterForTesting(Class componentClass, String parameterName, String parameterText) {
		getParameterService().setParameterForTesting(componentClass, parameterName, parameterText);
		
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

	public List<Parameter> retrieveParametersGivenLookupCriteria(
			Map<String, String> fieldValues) {
		return getParameterService().retrieveParametersGivenLookupCriteria(fieldValues);
	}	

    public String getParameterValue(String namespaceCode, String detailTypeCode, String parameterName) {
        return getParameterService().getParameterValue(namespaceCode, detailTypeCode, parameterName);
    }

    public List<String> getParameterValues(String namespaceCode, String detailTypeCode, String parameterName) {
        return getParameterService().getParameterValues(namespaceCode, detailTypeCode, parameterName);
        
    }

    public boolean getIndicatorParameter(String namespaceCode, String detailTypeCode, String parameterName) {
        return getParameterService().getIndicatorParameter(namespaceCode, detailTypeCode, parameterName);
    }

    public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName) {
        return getParameterService().getParameterEvaluator(namespaceCode, detailTypeCode, parameterName);
    }

    public ParameterEvaluator getParameterEvaluator(String namespaceCode, String detailTypeCode, String parameterName, String constrainedValue) {
        return getParameterService().getParameterEvaluator(namespaceCode, detailTypeCode, parameterName, constrainedValue);
    }
    

    public void clearCache() {
        getParameterService().clearCache();
        KEWServiceLocator.getCacheAdministrator().flushGroup(PARAMETER_CACHE_GROUP_NAME);
    }
    
    protected Parameter fetchFromCache(String namespaceCode, String detailTypeCode, String name) {
        return (Parameter)KEWServiceLocator.getCacheAdministrator().getFromCache(getParameterCacheKey(namespaceCode, detailTypeCode, name));
    }
    
    protected void insertIntoCache(Parameter parameter) {
        if (parameter == null) {
            return;
        }
        KEWServiceLocator.getCacheAdministrator().putInCache(getParameterCacheKey(parameter.getParameterNamespaceCode(), parameter.getParameterDetailTypeCode(), parameter.getParameterName()), parameter, PARAMETER_CACHE_GROUP_NAME);
    }
    
    protected void flushParameterFromCache(String namespaceCode, String detailTypeCode, String name) {
        KEWServiceLocator.getCacheAdministrator().flushEntry(getParameterCacheKey(namespaceCode, detailTypeCode, name));
    }
    
    protected String getParameterCacheKey(String namespaceCode, String detailTypeCode, String name) {
        return PARAMETER_CACHE_PREFIX + namespaceCode + "-" + detailTypeCode + "-" + name;
    }
}
