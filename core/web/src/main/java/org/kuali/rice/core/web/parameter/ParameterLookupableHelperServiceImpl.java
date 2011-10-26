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
package org.kuali.rice.core.web.parameter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.impl.component.ComponentBo;
import org.kuali.rice.core.impl.component.DerivedComponentBo;
import org.kuali.rice.core.impl.parameter.ParameterBo;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ParameterLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final long serialVersionUID = 4381873774407301041L;

    private static final Log LOG = LogFactory.getLog(ParameterLookupableHelperServiceImpl.class);
    private static final String COMPONENT_NAME = "component.name";
    private static final String DERIVED_COMPONENT_NAME = "derivedComponent.name";
    private static final String NAMESPACE_CODE = "namespaceCode";

    @Override
    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
    	
        ParameterBo parm = (ParameterBo)businessObject;
        
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, parm.getNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, parm.getComponentCode());
        permissionDetails.put(KimConstants.AttributeConstants.PARAMETER_NAME, parm.getName());
        return KimApiServiceLocator.getPermissionService().isAuthorizedByTemplateName(
        		GlobalVariables.getUserSession().getPerson().getPrincipalId(),
				KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.MAINTAIN_SYSTEM_PARAMETER,
				permissionDetails, Collections.<String, String>emptyMap());
    }
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {

        List<ParameterBo> parametersWithDerivedComponents = null;

        if (fieldValues.containsKey(COMPONENT_NAME) && StringUtils.isNotBlank(fieldValues.get(COMPONENT_NAME))) {
            // also search based on derived component name
            Map<String, String> derivedComponentFieldValues = new HashMap<String, String>(fieldValues);
            String componentName = derivedComponentFieldValues.remove(COMPONENT_NAME);
            derivedComponentFieldValues.put(DERIVED_COMPONENT_NAME, componentName);
            parametersWithDerivedComponents = (List<ParameterBo>)super.getSearchResultsUnbounded(derivedComponentFieldValues);
        }

        List<ParameterBo> results = (List<ParameterBo>)super.getSearchResultsUnbounded(fieldValues);
        if (parametersWithDerivedComponents != null) {
            results.addAll(parametersWithDerivedComponents);
        }
        normalizeParameterComponents(results);
        return results;
    }

	private void normalizeParameterComponents(List<ParameterBo> parameters) {
		// attach the derived components where needed
        for (ParameterBo parameterBo : parameters) {
            if (parameterBo.getComponent() == null) {
                parameterBo.setComponent(DerivedComponentBo.toComponentBo(parameterBo.getDerivedComponent()));
            }
        }
	}

}

