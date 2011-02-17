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
package org.kuali.rice.kns.inquiry;

import java.util.List;
import java.util.Map;

import org.kuali.rice.core.impl.component.ComponentBo;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.DataDictionaryException;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;

/**
 * Since ParameterDetailType can be either DataDictionary or DB based, we need a custom {@link Inquirable} to
 * check both.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ParameterComponentInquirableImpl extends KualiInquirableImpl {

	private static final String PARAMETER_DETAIL_TYPE_CODE = "componentCode";
	private static final String PARAMETER_NAMESPACE_CODE = "namespaceCode";
	
	/**
	 * This overridden method gets the BO for inquiries on {@link org.kuali.rice.core.impl.component.ComponentBo}
	 * 
	 * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
	 */
	@Override
	public BusinessObject getBusinessObject(Map fieldValues) {
		BusinessObject result = super.getBusinessObject(fieldValues);

		if (result == null) {

			String parameterDetailTypeCode = (String)fieldValues.get(PARAMETER_DETAIL_TYPE_CODE);
	        String parameterNamespaceCode = (String)fieldValues.get(PARAMETER_NAMESPACE_CODE);
			
	        if (parameterDetailTypeCode == null) throw new RuntimeException(PARAMETER_DETAIL_TYPE_CODE + 
	        		" is a required key for this inquiry");
	        if (parameterNamespaceCode == null) throw new RuntimeException(PARAMETER_NAMESPACE_CODE + 
	        		" is a required key for this inquiry");

			List<ComponentBo> components;
	        try {
	        	components = KNSServiceLocatorWeb.getRiceApplicationConfigurationMediationService().getNonDatabaseComponents();
	        } catch (DataDictionaryException ex) {
	            throw new RuntimeException(
	            		"Problem parsing data dictionary during full load required for inquiry to function: " + 
	            		ex.getMessage(), ex);
	        }
	        
	        for (ComponentBo pdt : components) {
	        	if (parameterDetailTypeCode.equals(pdt.getCode()) &&
	        			parameterNamespaceCode.equals(pdt.getNamespaceCode())) {
	        		result = pdt;
	        		break;
	        	}
	        }
		}
		
		return result; 
	}
	
}
