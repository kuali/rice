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
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - jonathan don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class AdhocReviewPermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {
	/**
	 * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet,
	 *      java.util.List)
	 */
	@Override
	public List<KimPermissionInfo> performPermissionMatches(
			AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		List<KimPermissionInfo> matchingPermissions = super.performPermissionMatches(requestedDetails, permissionsList);
		List<KimPermissionInfo> returnPermissions = new ArrayList<KimPermissionInfo>();
		
		
		for (KimPermissionInfo kimPermissionInfo : matchingPermissions) {
			//If a note without an attachment, byPass attachment type code check. (Fix for KFSMI-2849)
			if(KNSConstants.BY_PASS_ACTION_REQUEST_CD_INDICATOR.equals(requestedDetails.get(KimAttributes.ACTION_REQUEST_CD))){
				returnPermissions.add(kimPermissionInfo);
			}else{
				if (StringUtils.equals(kimPermissionInfo.getDetails().get(
						KimAttributes.ACTION_REQUEST_CD), requestedDetails
						.get(KimAttributes.ACTION_REQUEST_CD))) {
					returnPermissions.add(kimPermissionInfo);
				}
			}
		}
		
		return returnPermissions;
	}
}
