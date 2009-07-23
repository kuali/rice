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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.ParameterServerService;
import org.kuali.rice.kns.util.KNSConstants;

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
	protected LookupService lookupService;
	
	public Parameter retrieveParameter(String namespaceCode, String detailTypeCode, String parameterName) {
	    String applicationNamespace = KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.APPLICATION_CODE);
	    if (StringUtils.isEmpty(applicationNamespace)) {
	        applicationNamespace = KNSConstants.DEFAULT_APPLICATION_CODE;
	    }
	    Parameter parameter = fetchFromCache(namespaceCode, detailTypeCode, parameterName);
        if (parameter != null) {
            return parameter;
        }
	    HashMap<String, String> crit = new HashMap<String, String>(3);
	    crit.put("parameterNamespaceCode", namespaceCode);
	    crit.put("parameterDetailTypeCode", detailTypeCode);
	    crit.put("parameterName", parameterName);
	    //crit.put("parameterApplicationNamespaceCode", applicationNamespace);
	    
	    List<Parameter> parameters = (List<Parameter>)getBusinessObjectService().findMatching(Parameter.class, crit);
	    Parameter parameterDefault = null;
	    for (Parameter parm : parameters) {
	        if (StringUtils.equals(applicationNamespace, parm.getParameterApplicationNamespaceCode())) {
	            parameter = parm;
	            break;
	        } else if (StringUtils.equals(KNSConstants.DEFAULT_APPLICATION_CODE, parm.getParameterApplicationNamespaceCode())) {
	            parameterDefault = parm;
	        }
	    }

	    if (parameter == null) {
	        parameter = parameterDefault;
	    }
	    
	    insertIntoCache(parameter); 
	    //if (parameter != null 
	    //        && StringUtils.equals(KNSConstants.DEFAULT_APPLICATION_CODE, parameter.getParameterApplicationNamespaceCode())
	    //        && !StringUtils.equals(KNSConstants.DEFAULT_APPLICATION_CODE, applicationNamespace)) {
	    //    insertIntoCache(parameter, applicationNamespace); 
	    //}
	    return parameter;
	}
    
   /**
    * This method can be used to retrieve a list of parameters that
    * match the given fieldValues criteria. You could also specify the "like"
    * criteria in the Map.
    * 
    * @param   fieldValues The Map containing the key value pairs to be used 
    *                      to build the criteria.
    * @return  List of Parameters that match the criteria.
    */
	@SuppressWarnings("unchecked")
	public List<Parameter> retrieveParametersGivenLookupCriteria(Map<String, String> fieldValues) {
		Collection<Parameter> results = getLookupService().findCollectionBySearch(Parameter.class, fieldValues);
		return new ArrayList<Parameter>( results );
    }    
	
	public List<ParameterDetailType> getNonDatabaseComponents() {
		return KNSServiceLocator.getRiceApplicationConfigurationMediationService().getNonDatabaseComponents();
	}
	
	@SuppressWarnings("unchecked")
	public void setParameterForTesting(Class componentClass, String parameterName, String parameterText) {
	    Parameter parameter = (Parameter) getParameter(componentClass, parameterName);
	    parameter.setParameterValue(parameterText);
	    getBusinessObjectService().save(parameter);
	} 
	
	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
	    this.businessObjectService = businessObjectService;
	}

	protected LookupService getLookupService() {
		if ( lookupService == null ) {
			lookupService = KNSServiceLocator.getLookupService();
		}
		return lookupService;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return this.businessObjectService;
	}	
}
