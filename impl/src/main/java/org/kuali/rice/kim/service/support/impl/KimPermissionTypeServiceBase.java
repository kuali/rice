/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.service.support.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.type.KimTypeServiceBase;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimPermissionTypeServiceBase extends KimTypeServiceBase implements KimPermissionTypeService {

	@Override
	public final List<Permission> getMatchingPermissions(Map<String, String> requestedDetails, List<Permission> permissionsList) {
		requestedDetails = translateInputAttributes(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return performPermissionMatches(requestedDetails, permissionsList);
	}

	/**
	 * Internal method for matching permissions.  Override this method to customize the matching behavior.
	 * 
	 * This base implementation uses the {@link #performMatch(Map<String, String>, Map<String, String>)} method
	 * to perform an exact match on the permission details and return all that are equal.
	 */
	protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
		List<Permission> matchingPermissions = new ArrayList<Permission>();
		for (Permission permission : permissionsList) {
            PermissionBo bo = PermissionBo.from(permission);
			if ( performMatch(requestedDetails, bo.getDetails()) ) {
				matchingPermissions.add( permission );
			}
		}
		return matchingPermissions;
	}
	
	/**
	 * 
	 * Internal method for checking if property name matches
	 * 
	 * @param requestedDetailsPropertyName name of requested details property
	 * @param permissionDetailsPropertyName name of permission details property
	 * @return boolean 
	 */
	protected boolean doesPropertyNameMatch(
			String requestedDetailsPropertyName,
			String permissionDetailsPropertyName) {
		if (StringUtils.isBlank(permissionDetailsPropertyName)) {
			return true;
		}
		if ( requestedDetailsPropertyName == null ) {
		    requestedDetailsPropertyName = ""; // prevent NPE
		}
		return StringUtils.equals(requestedDetailsPropertyName, permissionDetailsPropertyName)
				|| (requestedDetailsPropertyName.startsWith(permissionDetailsPropertyName+"."));
	}
}
