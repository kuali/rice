/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ParameterLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ParameterLookupableHelperServiceImpl.class);
	
    private ParameterService parameterService;

    @Override
    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
    	
        boolean allowsEdit = false;
        Parameter parm = (Parameter)businessObject;
        
        AttributeSet permissionDetails = new AttributeSet();
        permissionDetails.put(KimAttributes.NAMESPACE_CODE, parm.getParameterNamespaceCode());
        permissionDetails.put(KimAttributes.COMPONENT_NAME, parm.getParameterDetailTypeCode());
        permissionDetails.put(KimAttributes.PARAMETER_NAME, parm.getParameterName());
        allowsEdit = KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(
        		GlobalVariables.getUserSession().getPerson().getPrincipalId(),
				KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.MAINTAIN_SYSTEM_PARAMETER,
				permissionDetails, null);
        
        return allowsEdit;
    }
    
    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        List<? extends BusinessObject> results;
        
        // get the DD detail types
        List<ParameterDetailType> ddDetailTypes = KNSServiceLocator.getParameterServerService().getNonDatabaseComponents();
        if (fieldValues.containsKey("parameterDetailType.parameterDetailTypeName") && !StringUtils.isBlank(fieldValues.get("parameterDetailType.parameterDetailTypeName"))) {
        	final Set<ParameterDetailType> matchingDetailTypes = new HashSet<ParameterDetailType>();
            // perform a basic database lookup for detail types codes
            String parameterNamespaceCode = fieldValues.get("parameterNamespaceCode");
            String parameterDetailTypeName = fieldValues.get("parameterDetailType.parameterDetailTypeName");

            List<ParameterDetailType> dbDetailTypes = 
            	(List<ParameterDetailType>)getBusinessObjectService().findAll(ParameterDetailType.class);
            List<ParameterDetailType> allDetailTypes = new ArrayList<ParameterDetailType>(ddDetailTypes.size() + dbDetailTypes.size());
            allDetailTypes.addAll(dbDetailTypes);
            allDetailTypes.addAll(ddDetailTypes);
            
            // add some error logging if there are duplicates
            reportDuplicateDetailTypes(allDetailTypes);
            
            // filter all detail types by their name
            Pattern nameRegex = getParameterDetailTypeNameRegex(parameterDetailTypeName);
            for (ParameterDetailType detailType : allDetailTypes) {
                if (StringUtils.isBlank(parameterNamespaceCode) || detailType.getParameterNamespaceCode().equals(parameterNamespaceCode)) {
                    if (nameRegex == null || (detailType.getParameterDetailTypeName() != null && nameRegex.matcher(detailType.getParameterDetailTypeName().toUpperCase()).matches())) {
                    	matchingDetailTypes.add(detailType);
                    }
                }
            }
            // we're filtering in memory, so remove this criteria
            fieldValues.remove("parameterDetailType.parameterDetailTypeName");
            
            results = super.getSearchResults(fieldValues);
            // attach the DD detail types to your results before we filter (else filtering won't work correctly)
            attachDataDictionaryDetailTypes(results, ddDetailTypes);
            // filter down to just results with matching parameter component (ParameterDetailType)
            CollectionUtils.filter(results, new Predicate() {
            	public boolean evaluate(Object object) {
            		return matchingDetailTypes.contains(((Parameter)object).getParameterDetailType());
            	}
            });
        }
        else {
            results = super.getSearchResults(fieldValues);
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
			List<ParameterDetailType> allDetailTypes) {
		// check for duplicates between DD and DB 
		Set<ParameterDetailType> dupCheck = new HashSet<ParameterDetailType>();
		for (ParameterDetailType detailType : allDetailTypes) {
			if (dupCheck.contains(detailType)) {
				ParameterDetailType duplicate = null;
				for (ParameterDetailType d : dupCheck) if (d.equals(detailType)) {
					duplicate = d;
					break;
				}
				LOG.error(ParameterDetailType.class.getSimpleName() + "found with duplicate keys: " + detailType + " and " + duplicate);
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
			List<ParameterDetailType> ddDetailTypes) {
		// attach the non-database parameterDetailTypes
        Map<String, ParameterDetailType> ddDetailTypeMap = new HashMap<String, ParameterDetailType>(ddDetailTypes.size());
        for (ParameterDetailType detailType : ddDetailTypes) {
            ddDetailTypeMap.put(detailType.getParameterDetailTypeCode(), detailType);
        }
        for (BusinessObject obj : parameters) {
            if (ObjectUtils.isNull(((Parameter) obj).getParameterDetailType())) {
                ((Parameter) obj).setParameterDetailType(ddDetailTypeMap.get(((Parameter) obj).getParameterDetailTypeCode()));
            }
        }
	}

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
}

