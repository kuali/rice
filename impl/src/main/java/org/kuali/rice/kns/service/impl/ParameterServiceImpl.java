/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.List;

import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterServerService;

/**
 * See ParameterService. The componentClass must be the business object, document, or step class that the parameter is associated
 * with. Implementations of this class know how to translate that to a namespace (for ParameterService Impl, determine what module
 * the Class is associated with by parsing the package) and detail type (for ParameterServiceImpl, document Class --> use simple
 * class name minus the word Document / business object Class --> use simple class name, batch step class --> use the simple class
 * name). In cases where the parameter is applicable to all documents, all lookups, all batch steps, or all components in a
 * particular module, you should pass in the appropriate constant class in KfsParameterConstants for the component Class (e.g. all
 * purchasing documents = PURCHASING_DOCUMENT.class, all purchasing lookups = PURCHASING_LOOKUP.class, all purchasing batch steps =
 * PURCHASING_BATCH.class, and all purchasing components = PURCHASING_ALL.class). In addition, certain methods take
 * constrainingValue and constrainedValue Strings. The constrainedValue is the value that you want to compare to the Parameter
 * value, and the constrainingValue is used for complex parameters that limit one field value based on the value of another field,
 * e.g VALID_OBJECT_LEVELS_BY_OBJECT_TYPE.
 */
public class ParameterServiceImpl extends ParameterServiceBase implements ParameterServerService {
	protected BusinessObjectService businessObjectService;
	
	public Parameter retrieveParameter(String namespaceCode, String detailTypeCode,
			String parameterName) {
	    HashMap<String, String> crit = new HashMap<String, String>(3);
	    crit.put("parameterNamespaceCode", namespaceCode);
	    crit.put("parameterDetailTypeCode", detailTypeCode);
	    crit.put("parameterName", parameterName);
	    return (Parameter)businessObjectService.findByPrimaryKey(Parameter.class, crit);
	}
    
	public List<ParameterDetailType> getNonDatabaseComponents() {
		return KNSServiceLocator.getRiceApplicationConfigurationMediationService().getNonDatabaseComponents();
	}
	
	public void setParameterForTesting(Class componentClass, String parameterName, String parameterText) {
	    Parameter parameter = (Parameter) getParameter(componentClass, parameterName);
	    parameter.setParameterValue(parameterText);
	    KNSServiceLocator.getBusinessObjectService().save(parameter);
	} 
	
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
	    this.businessObjectService = businessObjectService;
	}	
}
