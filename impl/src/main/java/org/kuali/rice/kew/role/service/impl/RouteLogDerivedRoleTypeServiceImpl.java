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
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RouteLogDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
	
	/**
	 *	- qualifier is document number
	 *	- the roles that will be of this type are KR-WKFLW Initiator and KR-WKFLW Initiator or Reviewer, KR-WKFLW Router
	 *	- only the initiator of the document in question gets the KR-WKFLW Initiator role
	 *	- user who routed the document according to the route log should get the KR-WKFLW Router role
	 *	- users who are authorized by the route log, i.e. initiators, people who have taken action, people with a pending action request
	 *		, or people who will receive an action request for the document in question get the KR-WKFLW Initiator or Reviewer Role 
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	public List<String> getPrincipalIdsFromApplicationRole(
			String namespaceCode, String roleName, AttributeSet qualification) {
		String documentNumber = qualification.get(KimConstants.KIM_ATTRIB_DOCUMENT_NUMBER);
		List<String> principalIds = new ArrayList<String>();
		
		if (StringUtils.isNotBlank(documentNumber)) {
			Long documentNumberLong = Long.parseLong(documentNumber);
			DocumentRouteHeaderValue documentRouteHeaderValue = 
				KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentNumberLong);
			if (KimConstants.KIM_ROLE_NAME_INITIATOR.equals(roleName))
				fillPrincipalIdsForInitiator(principalIds, documentRouteHeaderValue);
			else if(KimConstants.KIM_ROLE_NAME_INITIATOR_OR_REVIEWER.equals(roleName))
				fillPrincipalIdsForInitiatorOrReviewer(principalIds, documentRouteHeaderValue);
			else if(KimConstants.KIM_ROLE_NAME_ROUTER.equals(roleName))
				fillPrincipalIdsForRouter(principalIds, documentRouteHeaderValue);
		}
		return principalIds;
	}

	protected void fillPrincipalIdsForInitiator(List<String> principalIds, DocumentRouteHeaderValue documentRouteHeaderValue){
		principalIds.add(documentRouteHeaderValue.getInitiatorWorkflowId());
	}

	protected void fillPrincipalIdsForInitiatorOrReviewer(List<String> principalIds, DocumentRouteHeaderValue documentRouteHeaderValue){
		for(ActionTakenValue actionTaken: documentRouteHeaderValue.getActionsTaken()){
			try{
				principalIds.add(actionTaken.getWorkflowUser().getWorkflowUserId().getId());
			} catch(KEWUserNotFoundException uex){
				//ignore if the user cannot be found by kew
			}
		}
		for(ActionRequestValue actionRequest: documentRouteHeaderValue.getActionRequests()){
			try{
				principalIds.add(actionRequest.getWorkflowUser().getWorkflowUserId().getId());
			} catch(KEWUserNotFoundException uex){
				//ignore if the user cannot be found by kew
			}
		}
	}

	protected void fillPrincipalIdsForRouter(List<String> principalIds, DocumentRouteHeaderValue documentRouteHeaderValue){
		principalIds.add(documentRouteHeaderValue.getRoutedByUserWorkflowId());
	}

}
