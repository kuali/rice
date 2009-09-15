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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeAndAttachmentTypePermissionTypeService extends
		DocumentTypePermissionTypeServiceImpl {

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
			if (!kimPermissionInfo.getDetails().containsKey(
						KimAttributes.ATTACHMENT_TYPE_CODE)
			  || kimPermissionInfo.getDetails().get(KimAttributes.ATTACHMENT_TYPE_CODE)
				 .equals(requestedDetails.get(KimAttributes.ATTACHMENT_TYPE_CODE))) 		
			{
				matchingPermissions.add(kimPermissionInfo);
			}

		}
		// now, filter the list to just those for the current document
		matchingPermissions = super.performPermissionMatches(requestedDetails,
				matchingPermissions);
		return matchingPermissions;
	}
}
