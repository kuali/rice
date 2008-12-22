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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeAndActionRequestTypePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

	/**
	 * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.role.KimPermission)
	 */
	@Override
	protected boolean performPermissionMatch(AttributeSet requestedDetails, KimPermissionInfo permission) {
		if (!super.performPermissionMatch(requestedDetails, permission)) {
			return false;
		}
		
		if (StringUtils.isEmpty(requestedDetails.get(KimAttributes.DOCUMENT_TYPE_NAME)) || StringUtils.isEmpty(requestedDetails.get(KimAttributes.ACTION_REQUEST_CD))) {
        	throw new RuntimeException("Both " + KimAttributes.DOCUMENT_TYPE_NAME + " and " + KimAttributes.ACTION_REQUEST_CD + " should not be blank or null.");
		}	
		
		if (!permission.getDetails().get(KimAttributes.DOCUMENT_TYPE_NAME).equals(requestedDetails.get(KimAttributes.DOCUMENT_TYPE_NAME))) {
			return false;
		}
		
		if (StringUtils.isNotEmpty(permission.getDetails().get(KimAttributes.ACTION_REQUEST_CD)) && 
				!permission.getDetails().get(KimAttributes.ACTION_REQUEST_CD).equals(requestedDetails.get(KimAttributes.ACTION_REQUEST_CD))) {
			return false;
		}
		
		return true;
	}
	
}
