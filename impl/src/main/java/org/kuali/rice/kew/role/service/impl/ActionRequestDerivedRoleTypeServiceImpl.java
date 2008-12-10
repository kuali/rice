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

import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ActionRequestDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

	protected List<String> requiredAttributes = new ArrayList<String>();
	{
		requiredAttributes.add(KimConstants.KIM_ATTRIB_DOCUMENT_NUMBER);
		requiredAttributes.add(KimConstants.KIM_ATTRIB_ACTION_REQUEST_CODE);
	}
	
    /**
	 *	Attributes:
	 *	Document Id
	 *	Action Request Code
	 *	
	 *	Requirements:
	 *	- the only role that will be of this type is KR-WKFLW Action Request Recipient
	 *	- users who have a pending action request of the given type for the document in question should be considered to be in this role
     *   
     *  Action Requests - Approve, Complete, Clear FYI, Acknowledge
     *  
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification){
    	validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);

    	String documentNumber = qualification.get(KimConstants.KIM_ATTRIB_DOCUMENT_NUMBER);
		String actionRequestCode = qualification.get(KimConstants.KIM_ATTRIB_ACTION_REQUEST_CODE);
		List<String> principalIds = new ArrayList<String>();
		if(KimConstants.KIM_ROLE_NAME_ACTION_REQUEST_RECIPIENT.equals(roleName)){
			Long documentNumberLong = Long.parseLong(documentNumber);
			DocumentRouteHeaderValue documentRouteHeaderValue = 
				KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentNumberLong);
			principalIds.addAll(getPrincipalIdsForActionRequest(documentRouteHeaderValue.getActionRequests(), actionRequestCode));
		}
		return principalIds;
    }
	
    protected List<String> getPrincipalIdsForActionRequest(List<ActionRequestValue> actionRequests, String actionRequestValue){
    	List<String> principalIds = new ArrayList<String>();
    	if(KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestValue)){
    		for(ActionRequestValue actionRequest: actionRequests){
    			if(actionRequest.isApproveRequest())
    				fillPrincipalIdsList(principalIds, actionRequest);
    		}
    	} else if(KEWConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestValue)){
    		for(ActionRequestValue actionRequest: actionRequests){
    			if(actionRequest.isFYIRequest())
    				fillPrincipalIdsList(principalIds, actionRequest);
    		}
    	} else if(KEWConstants.ACTION_REQUEST_COMPLETE_REQ.equals(actionRequestValue)){
    		for(ActionRequestValue actionRequest: actionRequests){
    			if(actionRequest.isCompleteRequst())
    				fillPrincipalIdsList(principalIds, actionRequest);
    		}
    	} else if(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestValue)){
    		for(ActionRequestValue actionRequest: actionRequests){
    			if(actionRequest.isAcknowledgeRequest())
    				fillPrincipalIdsList(principalIds, actionRequest);
    		}
    	}
    	return principalIds;
    }
    
    private void fillPrincipalIdsList(List<String> principalIds, ActionRequestValue actionRequest){
		try{
			principalIds.add(actionRequest.getWorkflowUser().getWorkflowUserId().getId());
		} catch(KEWUserNotFoundException uex){
			
		}
    }

}