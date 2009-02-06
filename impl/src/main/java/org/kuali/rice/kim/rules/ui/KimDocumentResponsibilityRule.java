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
 * See the License for the specific language governing responsibilitys and
 * limitations under the License.
 */
package org.kuali.rice.kim.rules.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleResponsibility;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.rule.event.ui.AddResponsibilityEvent;
import org.kuali.rice.kim.rule.ui.AddResponsibilityRule;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimDocumentResponsibilityRule extends DocumentRuleBase implements AddResponsibilityRule {

	private static final String ERROR_PATH = "responsibility.responsibilityId";

	public boolean processAddResponsibility(AddResponsibilityEvent addResponsibilityEvent) {
		KimDocumentRoleResponsibility newResponsibility = addResponsibilityEvent.getResponsibility();
		if(newResponsibility==null){
			GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Responsibility"});
			return false;
		}

		KimResponsibilityImpl kimResponsibilityImpl = newResponsibility.getKimResponsibility();
		if(kimResponsibilityImpl==null){
			GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Responsibility"});
			return false;
		}

		IdentityManagementRoleDocument document = (IdentityManagementRoleDocument)addResponsibilityEvent.getDocument();		
	    boolean rulePassed = true;
		Map<String,String> responsibilityDetails = new HashMap<String,String>();
		responsibilityDetails.put(KimAttributes.NAMESPACE_CODE, kimResponsibilityImpl.getNamespaceCode());
		responsibilityDetails.put(KimAttributes.PERMISSION_NAME, kimResponsibilityImpl.getTemplate().getName());
		if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document, 
				KimConstants.NAMESPACE_CODE, 
				KimConstants.PermissionTemplateNames.GRANT_RESPONSIBILITY, 
				GlobalVariables.getUserSession().getPerson().getPrincipalId(), 
				responsibilityDetails, null)) {
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_ASSIGN_RESPONSIBILITY, 
            		new String[] {kimResponsibilityImpl.getNamespaceCode(), kimResponsibilityImpl.getTemplate().getName()});
            return false;
		}
		
		if (newResponsibility == null || StringUtils.isBlank(newResponsibility.getResponsibilityId())) {
            rulePassed = false;
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Responsibility"});
        } else {
		    for (KimDocumentRoleResponsibility responsibility: document.getResponsibilities()) {
		    	if (responsibility.getResponsibilityId().equals(newResponsibility.getResponsibilityId())) {
		            rulePassed = false;
		            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Responsibility"});
		    	}
		    }
        }
		return rulePassed;
	} 

}
