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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BatchFeedOrJobPermissionTypeServiceImpl extends NamespaceCodePermissionTypeServiceImpl {

	/**
	 * @see org.kuali.rice.kns.service.impl.NamespaceCodePermissionTypeServiceImpl#doesPermissionDetailMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.role.KimPermission)
	 */
	@Override
	public boolean doesPermissionDetailMatch(AttributeSet requestedDetails, KimPermission permission) {
		// Checking the namespace first
		try {
			if (super.doesPermissionDetailMatch(requestedDetails, permission)) {
				return true;
			}
		} catch (Exception e) {
			// Ignoring the possible empty namespace code exception in the case that bean name was passed in.
		}
		
		// Namespace not a match. Checking bean name.
		if (StringUtils.isEmpty(requestedDetails.get(KimConstants.KIM_ATTRIB_BEAN_NAME_CODE))) {
        	throw new RuntimeException("Both " + KimConstants.KIM_ATTRIB_NAMESPACE_CODE + " and " + KimConstants.KIM_ATTRIB_BEAN_NAME_CODE + " should not be blank or null.");
		}
		
		return requestedDetails.get(KimConstants.KIM_ATTRIB_BEAN_NAME_CODE).equals(permission.getDetails().get(KimConstants.KIM_ATTRIB_BEAN_NAME_CODE));
	}
	
}
