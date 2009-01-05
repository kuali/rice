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
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentTypeAndNodeOrStatePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

	{
		inputRequiredAttributes.add(KimAttributes.ROUTE_NODE_NAME);
		inputRequiredAttributes.add(KimAttributes.ROUTE_STATUS_CODE);
	}

	/**
	 *	Permission type service which can check the route node and status as well as the document hierarchy.
	 *	
	 *	Permission should be able to (in addition to taking the routingStatus, routingNote, and documentTypeName attributes) 
	 *	should take a documentNumber and retrieve those values from workflow before performing the comparison.
	 *
	 *	consider the document type hierarchy - check for a permission that just specifies the document type first at each level 
	 *	- then if you don't find that, check for the doc type and the node, then the doc type and the state. 
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#doesPermissionDetailMatch(AttributeSet, KimPermissionInfo)
	 */
	@Override
	public boolean performPermissionMatch(AttributeSet requestedDetails, KimPermissionInfo permission) {
		boolean documentTypeMatch = super.performPermissionMatch(requestedDetails, permission);
		if (documentTypeMatch && 
				routeNodeMatches(requestedDetails, permission.getDetails()) && 
				routeStatusMatches(requestedDetails, permission.getDetails())) {
			return true;
		}
		return false;
	}
	
	protected boolean routeNodeMatches(AttributeSet requestedDetails, AttributeSet permissionDetails) {
		if (!permissionDetails.containsKey(KimAttributes.ROUTE_NODE_NAME)) {
			return true;
		}
		return StringUtils.equals(requestedDetails.get(KimAttributes.ROUTE_NODE_NAME),
				permissionDetails.get(KimAttributes.ROUTE_NODE_NAME));
	}
	
	protected boolean routeStatusMatches(AttributeSet requestedDetails, AttributeSet permissionDetails) {
		if (!permissionDetails.containsKey(KimAttributes.ROUTE_STATUS_CODE)) {
			return true;
		}
		return StringUtils.equals(requestedDetails.get(KimAttributes.ROUTE_STATUS_CODE),
				permissionDetails.get(KimAttributes.ROUTE_STATUS_CODE));
	}

}