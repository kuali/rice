/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;

//@Transactional
public class KualiConfigurationServiceImpl extends AbstractStaticConfigurationServiceImpl implements
	KualiConfigurationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiConfigurationServiceImpl.class);

    public List<String> getParameterValues(Parameter parameter) {
	if (parameter == null || StringUtils.isBlank(parameter.getParameterValue())) {
	    return Collections.EMPTY_LIST;
	}
	return Arrays.asList(parameter.getParameterValue().split(";"));
    }

    public boolean constraintIsAllow(Parameter parameter) {
	return KNSConstants.APC_ALLOWED_OPERATOR.equals(parameter.getParameterConstraintCode());
    }

    public Parameter getParameter(String namespaceCode, String detailTypeCode, String parameterName) {
	if (StringUtils.isBlank(namespaceCode) || StringUtils.isBlank(detailTypeCode) || StringUtils.isBlank(parameterName)) {
	    throw new IllegalArgumentException(
		    "The getParameter method of KualiConfigurationServiceImpl requires a non-blank namespaceCode, parameterDetailTypeCode, and parameterName");
	}
	Parameter param = getParameterWithoutExceptions(namespaceCode, detailTypeCode, parameterName);
	if (param == null) {
	    throw new IllegalArgumentException(
		    "The getParameter method of KualiConfigurationServiceImpl was unable to find parameter: "
			    + namespaceCode + " / " + detailTypeCode + " / " + parameterName);
	}
	return param;
    }

    public List<Parameter> getParameters(Map<String, String> criteria) {
	ArrayList<Parameter> parameters = new ArrayList<Parameter>();
	parameters.addAll(getBusinessObjectService().findMatching(Parameter.class, criteria));
	return parameters;
    }

    public boolean evaluateConstrainedValue(String namespace, String detailType, String parameterName,
	    String constrainedValue) {
	return evaluateConstrainedValue(getParameter(namespace, detailType, parameterName), constrainedValue);
    }

    public boolean evaluateConstrainedValue(Parameter parameter, String constrainedValue) {
	checkParameterArgument(parameter, "evaluateConstrainedValue(Parameter parameter, String constrainedValue)");
	if (constraintIsAllow(parameter)) {
	    return getParameterValues(parameter).contains(constrainedValue);
	} else {
	    return !getParameterValues(parameter).contains(constrainedValue);
	}
    }

    public List<String> getParameterValues(String namespaceCode, String parameterDetailTypeCode, String parameterName) {
	return getParameterValues(getParameter(namespaceCode, parameterDetailTypeCode, parameterName));
    }

    public boolean getIndicatorParameter(String namespaceCode, String parameterDetailTypeCode, String parameterName) {
	return "Y".equals(getParameterValue(namespaceCode, parameterDetailTypeCode, parameterName));
    }

    public String getParameterValue(String namespaceCode, String parameterDetailTypeCode, String parameterName) {
	List<String> parameterValues = getParameterValues(getParameter(namespaceCode, parameterDetailTypeCode, parameterName));
	return parameterValues.isEmpty() ? "" : parameterValues.iterator().next();
    }

    public boolean parameterExists(String namespaceCode, String parameterDetailTypeCode, String parameterName) {
	return getParameterWithoutExceptions(namespaceCode, parameterDetailTypeCode, parameterName) != null;
    }

    private void checkParameterArgument(Parameter parameter, String methodName) {
	if (parameter == null) {
	    throw new IllegalArgumentException("The " + methodName
		    + " method of KualiConfigurationServiceImpl requires a non-null parameter");
	}
    }

    private Parameter getParameterWithoutExceptions(String namespaceCode, String detailTypeCode, String parameterName) {
	HashMap<String, String> crit = new HashMap<String, String>(3);
	crit.put("parameterNamespaceCode", namespaceCode);
	crit.put("parameterDetailTypeCode", detailTypeCode);
	crit.put("parameterName", parameterName);
	Parameter param = (Parameter) getBusinessObjectService().findByPrimaryKey(Parameter.class, crit);
	return param;
    }

    // using this instead of private variable with spring initialization because of recurring issues with circular
    // references
    // resulting in this error: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean
    // with
    // name 'businessObjectService': Bean with name 'businessObjectService' has been injected into other beans
    // [kualiConfigurationService]
    // in its raw version as part of a circular reference, but has eventually been wrapped (for example as part of
    // auto-proxy creation).
    // This means that said other beans do not use the final version of the bean. This is often the result of over-eager
    // type matching
    // - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.
    private BusinessObjectService getBusinessObjectService() {
	return KNSServiceLocator.getBusinessObjectService();
    }
}