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
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimCommonUtils;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentTypeAndNodeAndFieldsPermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

	{
		inputRequiredAttributes.add(KEWConstants.ROUTE_NODE_NAME_DETAIL);
		inputRequiredAttributes.add(KimAttributes.PROPERTY_NAME);
	}

	/**
	 * 
	 *	consider the document type hierarchy - check for a permission that just specifies the document type first at each level 
	 *	- then if you don't find that, check for the doc type and the node, then the doc type and the field.
	 *
	 *	- if the field value passed in starts with the value on the permission detail it is a match.  so...
	 *	permision detail sourceAccountingLines will match passed in value of sourceAccountingLines.amount and sourceAccountingLines 
	 *	permission detail sourceAccountingLines.objectCode will match sourceAccountingLines.objectCode but not sourceAccountingLines
	 *
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#doesPermissionDetailMatch(AttributeSet, KimPermissionInfo)
	 */
	@Override
	public boolean performPermissionMatch(AttributeSet requestedDetails, KimPermissionInfo permission) {
		boolean documentTypeMatch = super.performPermissionMatch(requestedDetails, permission);
		if (documentTypeMatch && 
				routeNodeMatches(requestedDetails, permission.getDetails()) && 
				KimCommonUtils.doesPropertyNameMatch(requestedDetails.get(KimAttributes.PROPERTY_NAME), permission.getDetails().get(KimAttributes.PROPERTY_NAME))) {
			return true;
		}
		return false;
	}
	
	protected boolean routeNodeMatches(AttributeSet requestedDetails, AttributeSet permissionDetails) {
		if (!permissionDetails.containsKey(KEWConstants.ROUTE_NODE_NAME_DETAIL)) {
			return true;
		}
		return StringUtils.equals(requestedDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL),
				permissionDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL));
	}
}