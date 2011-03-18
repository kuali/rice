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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentTypeAndEditModePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {
	
	{
		requiredAttributes.add(KimConstants.AttributeConstants.EDIT_MODE);
	}
	
	@Override
	protected List<KimPermissionInfo> performPermissionMatches(
			AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
		for (KimPermissionInfo kimPermissionInfo : permissionsList) {
			if (requestedDetails.get(KimConstants.AttributeConstants.EDIT_MODE).equals(kimPermissionInfo.getDetails().get(KimConstants.AttributeConstants.EDIT_MODE))) {
				matchingPermissions.add(kimPermissionInfo);
			}
		}
		return super.performPermissionMatches(requestedDetails, matchingPermissions);
	}
}
