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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class EntityPermissionTypeServiceImpl extends KimPermissionTypeServiceBase {

	{
		requiredAttributes.add(KimAttributes.ENTITY_TYPE_CODE);
	}

	/**
	 * 
	 * Attributes:
	 *	Entity Type Code
	 *	Property Name
	 *	
	 * Requirements:
	 *	- Both Entity Type Code and Property Name should be passed in the requested details. If not, throw exception.
	 *	- If only Entity Type Code is passed in the permission details, match that.
	 *	- If both Entity Type Code and Property Name are passed in the permission details check if the value passed in starts with the value in the db. 
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(AttributeSet, AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet requestedDetails, AttributeSet permissionDetails) {
		return doesEntityTypeCodeMatch(requestedDetails.get(KimAttributes.ENTITY_TYPE_CODE), 
						permissionDetails.get(KimAttributes.ENTITY_TYPE_CODE)) 
				&&
				KimCommonUtils.doesPropertyNameMatch(requestedDetails.get(KimAttributes.PROPERTY_NAME), 
						permissionDetails.get(KimAttributes.PROPERTY_NAME));
	}

	protected boolean doesEntityTypeCodeMatch(String requestedDetailsEntityTypeCode, String permissionDetailsEntityTypeCode){
		if(StringUtils.isBlank(permissionDetailsEntityTypeCode)) {
			return true;
		}
		return StringUtils.equals(requestedDetailsEntityTypeCode, permissionDetailsEntityTypeCode);
	}
}