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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeAndRelationshipToNoteAuthorPermissionTypeService
		extends DocumentTypePermissionTypeServiceImpl {

	@Override
	public List<KimPermissionInfo> performPermissionMatches(
			AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
				
		List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
		if (requestedDetails == null) {
			return matchingPermissions; // empty list
		}
		
		// loop over the permissions, checking the non-document-related ones
		for (KimPermissionInfo kimPermissionInfo : permissionsList) {
			if (Boolean.parseBoolean(requestedDetails
					.get(KimAttributes.CREATED_BY_SELF))) {
				if(Boolean.parseBoolean(kimPermissionInfo.getDetails().get(
						KimAttributes.CREATED_BY_SELF_ONLY))){
					matchingPermissions.add(kimPermissionInfo);
				}
				
			}else{
				if (!Boolean.parseBoolean(kimPermissionInfo.getDetails().get(
						KimAttributes.CREATED_BY_SELF_ONLY))) {
					matchingPermissions.add(kimPermissionInfo);
				}
			}

		}
		
		matchingPermissions = super.performPermissionMatches(requestedDetails, matchingPermissions);
		return matchingPermissions;
	}
}