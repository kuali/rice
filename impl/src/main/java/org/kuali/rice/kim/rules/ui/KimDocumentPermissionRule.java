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
package org.kuali.rice.kim.rules.ui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRolePermission;
import org.kuali.rice.kim.document.IdentityManagementRoleDocument;
import org.kuali.rice.kim.rule.event.ui.AddPermissionEvent;
import org.kuali.rice.kim.rule.ui.AddPermissionRule;
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
public class KimDocumentPermissionRule extends DocumentRuleBase implements AddPermissionRule {

	private static final String ERROR_PATH = "document.permission.permissionId";
	
	public boolean processAddPermission(AddPermissionEvent addPermissionEvent) {
		KimDocumentRolePermission newPermission = addPermissionEvent.getPermission();
		if(newPermission==null){
			GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Permission"});
			return false;
		}

		KimPermissionImpl kimPermissionImpl = newPermission.getKimPermission();
		if(kimPermissionImpl==null){
			GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Permission"});
			return false;
		}
	    boolean rulePassed = true;
		IdentityManagementRoleDocument document = (IdentityManagementRoleDocument)addPermissionEvent.getDocument();
		Map<String,String> permissionDetails = new HashMap<String,String>();
		permissionDetails.put(KimAttributes.NAMESPACE_CODE, kimPermissionImpl.getNamespaceCode());
		permissionDetails.put(KimAttributes.PERMISSION_NAME, kimPermissionImpl.getTemplate().getName());
		if (!getDocumentHelperService().getDocumentAuthorizer(document).isAuthorizedByTemplate(
				document, 
				KimConstants.NAMESPACE_CODE, 
				KimConstants.PermissionTemplateNames.ASSIGN_ROLE, 
				GlobalVariables.getUserSession().getPerson().getPrincipalId(), 
				permissionDetails, null)) {
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_ASSIGN_PERMISSION, 
            		new String[] {kimPermissionImpl.getNamespaceCode(), kimPermissionImpl.getTemplate().getName()});
            return false;
		}

		if (newPermission == null || StringUtils.isBlank(newPermission.getPermissionId())) {
            rulePassed = false;
            GlobalVariables.getErrorMap().putError(ERROR_PATH, RiceKeyConstants.ERROR_EMPTY_ENTRY, new String[] {"Permission"});
        } else {
		    int i = 0;
        	for (KimDocumentRolePermission permission: document.getPermissions()) {
		    	if (permission.getPermissionId().equals(newPermission.getPermissionId())) {
		            rulePassed = false;
		            GlobalVariables.getErrorMap().putError("document.permissions["+i+"].permissionId", RiceKeyConstants.ERROR_DUPLICATE_ENTRY, new String[] {"Permission"});
		    	}
		    	i++;
		    }
        }
		return rulePassed;
	} 

}
