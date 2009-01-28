/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.rules;

import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
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
	        				KimConstants.PermissionTemplateNames.MAINAIN_SYSTEM_PARAMETER,
	        				permissionDetails, null);
	        if(!allowsEdit){
	        	putGlobalError(RiceKeyConstants.AUTHORIZATION_ERROR_PARAMETER);
	        }
	        return allowsEdit;
	}

}

