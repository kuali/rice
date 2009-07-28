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
package org.kuali.rice.kns.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.datadictionary.DataDictionaryException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill
 * this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ParameterRule extends MaintenanceDocumentRuleBase {

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		boolean result = super.processCustomRouteDocumentBusinessRules( document );

		result &= checkAllowsMaintenanceEdit( document.getDocumentHeader().getWorkflowDocument()
				.getRouteHeader().getInitiatorPrincipalId(), (Parameter)getNewBo() );

		result &= checkComponent((Parameter) getNewBo());
		
		return result;
	}

	protected boolean checkAllowsMaintenanceEdit(String initiatorPrincipalId, Parameter newBO) {

		 boolean allowsEdit = false;
	        Parameter parm = (Parameter)newBO;
	        
	        AttributeSet permissionDetails = new AttributeSet();
	        permissionDetails.put(KimAttributes.NAMESPACE_CODE, parm.getParameterNamespaceCode());
	        permissionDetails.put(KimAttributes.COMPONENT_NAME, parm.getParameterDetailTypeCode());
	        permissionDetails.put(KimAttributes.PARAMETER_NAME, parm.getParameterName());
	        allowsEdit = KIMServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(
	        				GlobalVariables.getUserSession().getPerson().getPrincipalId(),
	        				KNSConstants.KNS_NAMESPACE,
	        				KimConstants.PermissionTemplateNames.MAINTAIN_SYSTEM_PARAMETER,
	        				permissionDetails, null);
	        if(!allowsEdit){
	        	putGlobalError(RiceKeyConstants.AUTHORIZATION_ERROR_PARAMETER);
	        }
	        return allowsEdit;
	}

    public boolean checkComponent(Parameter param) {
        String component = param.getParameterDetailTypeCode();
        String namespace = param.getParameterNamespaceCode();
        boolean result = false;

        try {
            List<ParameterDetailType> dataDictionaryAndSpringComponents = KNSServiceLocator.getParameterServerService().getNonDatabaseComponents();
            for (ParameterDetailType pdt : dataDictionaryAndSpringComponents) {
                if (pdt.getParameterNamespaceCode().equals(namespace) && pdt.getParameterDetailTypeCode().equals(component)) {
                    result = true;
                    break;
                }
            }

            if (!result) {
                Map<String, String> primaryKeys = new HashMap<String, String>(2);
                primaryKeys.put("parameterNamespaceCode", namespace);
                primaryKeys.put("parameterDetailTypeCode", component);
                result = ObjectUtils.isNotNull(getBoService().findByPrimaryKey(ParameterDetailType.class, primaryKeys));
            }

            if (!result) {
                putFieldError("parameterDetailTypeCode", "error.document.parameter.detailType.invalid", component);
            }

            return result;
        }
        catch (DataDictionaryException ex) {
            throw new RuntimeException("Problem parsing data dictionary during full load required for rule validation: " + ex.getMessage(), ex);
        }
    }
}

