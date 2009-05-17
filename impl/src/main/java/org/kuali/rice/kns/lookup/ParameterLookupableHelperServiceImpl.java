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
package org.kuali.rice.kns.lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
        
        List<ParameterDetailType> ddDetailTypes = KNSServiceLocator.getParameterServerService().getNonDatabaseComponents();
        if (fieldValues.containsKey("parameterDetailType.parameterDetailTypeName") && !StringUtils.isBlank(fieldValues.get("parameterDetailType.parameterDetailTypeName"))) {
            // perform a basic database lookup for detail types codes
            Map<String, String> detailTypeCriteria = new HashMap<String, String>(2);
            String parameterNamespaceCode = fieldValues.get("parameterNamespaceCode");
            String parameterDetailTypeName = fieldValues.get("parameterDetailType.parameterDetailTypeName");
            detailTypeCriteria.put("parameterNamespaceCode", parameterNamespaceCode);
            detailTypeCriteria.put("parameterDetailTypeName", parameterDetailTypeName);
            Collection<ParameterDetailType> databaseDetailTypes = getBusinessObjectService().findMatching(ParameterDetailType.class, detailTypeCriteria);
            // get all detail types from the data dictionary and filter by their name
            StringBuffer parameterDetailTypeCodeCriteria = new StringBuffer((databaseDetailTypes.size() + ddDetailTypes.size()) * 30);

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

            // loop over matches from the DD, adding to a Lookup string
            for (ParameterDetailType detailType : ddDetailTypes) {
                if (StringUtils.isBlank(parameterNamespaceCode) || detailType.getParameterNamespaceCode().equals(parameterNamespaceCode)) {
                    if (nameRegex == null || (detailType.getParameterDetailTypeName() != null && nameRegex.matcher(detailType.getParameterDetailTypeName().toUpperCase()).matches())) {
                        if (parameterDetailTypeCodeCriteria.length() > 0) {
                            parameterDetailTypeCodeCriteria.append("|");
                        }
                        parameterDetailTypeCodeCriteria.append(detailType.getParameterDetailTypeCode());
                    }
                }
            }

            // loop over matches from the database, adding to a Lookup string
            for (ParameterDetailType detailType : databaseDetailTypes) {
                if (parameterDetailTypeCodeCriteria.length() > 0) {
                    parameterDetailTypeCodeCriteria.append("|");
                }
                parameterDetailTypeCodeCriteria.append(detailType.getParameterDetailTypeCode());
            }

            // remove the original parameter so it is not applied
            fieldValues.remove("parameterDetailType.parameterDetailTypeName");
            // add the new detail type code criteria string to the lookup
            fieldValues.put("parameterDetailTypeCode", parameterDetailTypeCodeCriteria.toString());
            results = super.getSearchResults(fieldValues);
        }
        else {
            results = super.getSearchResults(fieldValues);
        }
        // attach the non-database parameterDetailTypes
        Map<String, ParameterDetailType> ddDetailTypeMap = new HashMap<String, ParameterDetailType>(ddDetailTypes.size());
        for (ParameterDetailType detailType : ddDetailTypes) {
            ddDetailTypeMap.put(detailType.getParameterDetailTypeCode(), detailType);
        }
        for (BusinessObject obj : results) {
            if (ObjectUtils.isNull(((Parameter) obj).getParameterDetailType())) {
                ((Parameter) obj).setParameterDetailType(ddDetailTypeMap.get(((Parameter) obj).getParameterDetailTypeCode()));
            }
        }
        return results;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
}

