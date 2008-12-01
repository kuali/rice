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
package org.kuali.rice.kew.role.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RouteLogDerivedRoleTypeServiceImpl extends KimRoleTypeServiceBase {
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getGroupIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<String> getGroupIdsFromApplicationRole(String namespaceCode,
			String roleName, AttributeSet qualification) {
		// A group is never the initiator of the document
		return new ArrayList<String>();
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<String> getPrincipalIdsFromApplicationRole(
			String namespaceCode, String roleName, AttributeSet qualification) {
		String documentNumber = qualification.get(KimConstants.KIM_ATTRIB_DOCUMENT_NUMBER);
		List<String> principalIds = new ArrayList<String>();
		
		if (StringUtils.isNotBlank(documentNumber)) {
			if ("Initiator".equals(roleName)) {
				Long documentNumberLong = Long.parseLong(documentNumber);
				DocumentRouteHeaderValue documentRouteHeaderValue = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentNumberLong);
				principalIds.add(documentRouteHeaderValue.getInitiatorWorkflowId());
			}
		}
		return principalIds;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#isApplicationRoleType()
	 */
	@Override
	public boolean isApplicationRoleType() {
		return true;
	}
}
