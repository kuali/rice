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
package org.kuali.rice.kim.document.rule;

import java.util.HashMap;

import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.util.KIMConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class handles document business rules for the Role maintenance document. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleMaintenanceDocumentRule extends MaintenanceDocumentRuleBase {
	private static final String ROLE_NAME_SAME_ON_COPY = "error.document.maintenance.role.nameTheSameOnCopy";
	private static final String PERMISSION_MORE_THAN_ONCE = "error.document.maintenance.role.permissionMoreThanOnce";

	/**
	 * This overridden method checks to make sure that a number of business rules are enforced upon submit of the document.
	 * 
	 * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomRouteDocumentBusinessRules(
			MaintenanceDocument document) {
		
		checkForDuplicateRoleNamesOnCopy(document);
		checkForDuplicatePermissionAssociations(document);
		
		return super.processCustomRouteDocumentBusinessRules(document);
	}

	/**
	 * This method will check to make sure that the new Role record being created as a copy, has a different Role name 
	 * since those must be unique.
	 * 
	 * @param document
	 */
	private void checkForDuplicateRoleNamesOnCopy(MaintenanceDocument document) {
		String maintAction = document.getNewMaintainableObject().getMaintenanceAction();
		if(maintAction != null && maintAction.equals(KNSConstants.MAINTENANCE_COPY_ACTION)) {  // check for copy action
			Role oldRole = (Role) document.getOldMaintainableObject().getBusinessObject();
			Role newRole = (Role) document.getNewMaintainableObject().getBusinessObject();
			
			if(oldRole != null && newRole != null) {
				String oldRoleName = oldRole.getName();
				String newRoleName = newRole.getName();
				
				if(oldRoleName.equalsIgnoreCase(newRoleName)) {
					putFieldError(KIMConstants.BO_PROPERTY_NAMES.NAME, ROLE_NAME_SAME_ON_COPY, "");
				}
			}
		}
	}

	/**
	 * This overridden method checks to make sure that the same permission isn't included more than once.
	 */
	public void checkForDuplicatePermissionAssociations(MaintenanceDocument document) {
		Role newRole = (Role) document.getNewMaintainableObject().getBusinessObject();
		
		HashMap<Long, Permission> checkList = new HashMap<Long, Permission>();
		
		for(Permission permission : newRole.getPermissions()) {
			checkList.put(permission.getId(), permission);
		}
		
		if(checkList.size() != newRole.getPermissions().size()) {  // if the sizes don't match, then the same permission was used more than once
			putFieldError(KIMConstants.BO_PROPERTY_NAMES.PERMISSIONS, PERMISSION_MORE_THAN_ONCE);
		}
	}
	
	
	
}
