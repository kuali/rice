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
package org.kuali.rice.kew.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class AdhocReviewPermissionTypeServiceImpl extends
		DocumentTypePermissionTypeServiceImpl {
	/**
	 * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet,
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
			if (StringUtils.equals(kpi.getDetails().get(
					KimAttributes.ACTION_REQUEST_CD), requestedDetails
					.get(KimAttributes.ACTION_REQUEST_CD))) {
				matchingPermissions.add(kpi);
			}
		}
		// now, filter the list to just those for the current document
		matchingPermissions = super.performPermissionMatches(requestedDetails,
				matchingPermissions);
		return matchingPermissions;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#filterRoleQualifier(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public AttributeSet filterRoleQualifier(String namespaceCode,
			String permissionTemplateName, String permissionName,
			AttributeSet roleQualifier) {
		AttributeSet filteredAttributeSet = new AttributeSet();
		// for the roles assigned to this, we don't need to pass any qualifiers
		return filteredAttributeSet;
	}
}
