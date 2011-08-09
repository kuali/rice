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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.impl.component.ComponentBo;
import org.kuali.rice.core.impl.parameter.ParameterBo;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ParameterLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(ParameterLookupableHelperServiceImpl.class);
    private static final String COMPONENT_NAME = "component.name";
    private static final String NAMESPACE_CODE = "namespaceCode";

    private ParameterService parameterService;

    @Override
    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
    	
        boolean allowsEdit = false;
        ParameterBo parm = (ParameterBo)businessObject;
        
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, parm.getNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, parm.getComponentCode());
        permissionDetails.put(KimConstants.AttributeConstants.PARAMETER_NAME, parm.getName());
        allowsEdit = KimApiServiceLocator.getPermissionService().isAuthorizedByTemplateName(
        		GlobalVariables.getUserSession().getPerson().getPrincipalId(),
				KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.MAINTAIN_SYSTEM_PARAMETER,
				permissionDetails, Collections.<String, String>emptyMap());
        
        return allowsEdit;
    }
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        List<? extends BusinessObject> results;
        
        // get the DD detail types
        List<Component> ddDetailTypes = KRADServiceLocatorWeb.getRiceApplicationConfigurationMediationService().getNonDatabaseComponents();
        if (fieldValues.containsKey(COMPONENT_NAME) && !StringUtils.isBlank(fieldValues.get(COMPONENT_NAME))) {
        	final Set<ComponentBo> matchingDetailTypes = new HashSet<ComponentBo>();
            // perform a basic database lookup for detail types codes
            String namespaceCode = fieldValues.get(NAMESPACE_CODE);
            String parameterDetailTypeName = fieldValues.get(COMPONENT_NAME);

            List<ComponentBo> dbDetailTypes =
            	(List<ComponentBo>)getBusinessObjectService().findAll(ComponentBo.class);
            List<ComponentBo> allDetailTypes = new ArrayList<ComponentBo>(ddDetailTypes.size() + dbDetailTypes.size());
            allDetailTypes.addAll(dbDetailTypes);
            for (Component fromDD : ddDetailTypes) {
                allDetailTypes.add(ComponentBo.from(fromDD));
            }
            
            // add some error logging if there are duplicates
            reportDuplicateDetailTypes(allDetailTypes);
            
            // filter all detail types by their name
            Pattern nameRegex = getParameterDetailTypeNameRegex(parameterDetailTypeName);
            for (ComponentBo detailType : allDetailTypes) {
                if (StringUtils.isBlank(namespaceCode) || detailType.getNamespaceCode().equals(namespaceCode)) {
                    if (nameRegex == null || (detailType.getCode() != null && nameRegex.matcher(detailType.getCode().toUpperCase()).matches())) {
                    	matchingDetailTypes.add(detailType);
                    }
                }
            }
            // we're filtering in memory, so remove this criteria
            fieldValues.remove(COMPONENT_NAME);
            
            results = super.getSearchResultsUnbounded(fieldValues);
            // attach the DD detail types to your results before we filter (else filtering won't work correctly)
            attachDataDictionaryDetailTypes(results, ddDetailTypes);
            // filter down to just results with matching parameter component (ParameterDetailType)
            CollectionUtils.filter(results, new Predicate() {
            	public boolean evaluate(Object object) {
            		return matchingDetailTypes.contains(((ParameterBo)object).getComponentCode());
            	}
            });
        }
        else {
            results = super.getSearchResultsUnbounded(fieldValues);
            attachDataDictionaryDetailTypes(results, ddDetailTypes);
        }
        return results;
    }

	/**
	 * This method ...
	 * 
	 * @param allDetailTypes
	 */
	private void reportDuplicateDetailTypes(
			List<ComponentBo> allDetailTypes) {
		// check for duplicates between DD and DB 
		Set<ComponentBo> dupCheck = new HashSet<ComponentBo>();
		for (ComponentBo detailType : allDetailTypes) {
			if (dupCheck.contains(detailType)) {
				ComponentBo duplicate = null;
				for (ComponentBo d : dupCheck) {
                    if (d.equals(detailType)) {
					    duplicate = d;
					    break;
                    }
				}
				LOG.error(ComponentBo.class.getSimpleName() + "found with duplicate keys: " + detailType + " and " + duplicate);
			} else {
				dupCheck.add(detailType);
			}
		}
	}

	/**
	 * This method ...
	 * 
	 * @param parameterDetailTypeName
	 * @return
	 */
	private Pattern getParameterDetailTypeNameRegex(
			String parameterDetailTypeName) {
		Pattern nameRegex = null;
		if (StringUtils.isNotBlank(parameterDetailTypeName)) {
		    String patternStr = parameterDetailTypeName.replace("*", ".*").toUpperCase();
		    try {
		        nameRegex = Pattern.compile(patternStr);
		    }
		    catch (PatternSyntaxException ex) {
		        LOG.error("Unable to parse parameterDetailTypeName pattern, ignoring.", ex);
		    }
		}
		return nameRegex;
	}

	/**
	 * This method ...
	 * 
	 * @param parameters
	 * @param ddDetailTypes
	 */
	private void attachDataDictionaryDetailTypes(
			List<? extends BusinessObject> parameters,
			List<Component> ddDetailTypes) {
		// attach the non-database parameterDetailTypes
        Map<String, ComponentBo> ddDetailTypeMap = new HashMap<String, ComponentBo>(ddDetailTypes.size());
        for (Component detailType : ddDetailTypes) {
            ddDetailTypeMap.put(detailType.getCode(), ComponentBo.from(detailType));
        }
	}

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
}

