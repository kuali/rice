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
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.impl.type.KimTypeServiceBase;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimPermissionTypeServiceBase extends KimTypeServiceBase implements KimPermissionTypeService {

	/**
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#getMatchingPermissions(Attributes, List)
	 */
	public final List<KimPermissionInfo> getMatchingPermissions( Attributes requestedDetails, List<KimPermissionInfo> permissionsList ) {
		requestedDetails = translateInputAttributes(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return performPermissionMatches(requestedDetails, permissionsList);
	}

	/**
	 * Internal method for matching permissions.  Override this method to customize the matching behavior.
	 * 
	 * This base implementation uses the {@link #performMatch(Attributes, Attributes)} method
	 * to perform an exact match on the permission details and return all that are equal.
	 */
	protected List<KimPermissionInfo> performPermissionMatches(Attributes requestedDetails, List<KimPermissionInfo> permissionsList) {
		List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
		for (KimPermissionInfo permission : permissionsList) {
			if ( performMatch(requestedDetails, Attributes.fromMap(permission.getDetails())) ) {
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
