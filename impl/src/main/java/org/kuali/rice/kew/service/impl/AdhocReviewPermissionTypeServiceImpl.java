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
package org.kuali.rice.kew.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class AdhocReviewPermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl implements KimPermissionTypeService{
	
	{
		requiredAttributes.add( KimConstants.AttributeConstants.ACTION_REQUEST_CD );
	}
	
	/**
	 * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(org.kuali.rice.core.util.AttributeSet,
	 *      java.util.List)
	 */
	@Override
	public List<KimPermissionInfo> performPermissionMatches(
			AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
		if (requestedDetails == null) {
			return matchingPermissions; // empty list
		}
		// loop over the permissions, checking the non-document-related ones
		for (KimPermissionInfo kpi : permissionsList) {
			if (!kpi.getDetails().containsKey(KimConstants.AttributeConstants.ACTION_REQUEST_CD)
			  || StringUtils.equals(kpi.getDetails().
				 get(KimConstants.AttributeConstants.ACTION_REQUEST_CD), requestedDetails
					.get(KimConstants.AttributeConstants.ACTION_REQUEST_CD))) {
				matchingPermissions.add(kpi);
			}
		}
		// now, filter the list to just those for the current document
		matchingPermissions = super.performPermissionMatches(requestedDetails,
				matchingPermissions);
		return matchingPermissions;
	}
}
