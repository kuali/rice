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

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.user.BaseWorkflowUser;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ActionRequestDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
	private static final String ACTION_REQUEST_RECIPIENT_ROLE_NAME = "Action Request Recipient";
	
	protected List<String> requiredAttributes = new ArrayList<String>();
	{
		requiredAttributes.add(KimAttributes.DOCUMENT_NUMBER);
		requiredAttributes.add(KimAttributes.ACTION_REQUEST_CD);
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

    	String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
		String actionRequestCode = qualification.get(KimAttributes.ACTION_REQUEST_CD);
		List<String> principalIds = new ArrayList<String>();
		if(ACTION_REQUEST_RECIPIENT_ROLE_NAME.equals(roleName)){
			try{
				WorkflowInfo workflowInfo = new WorkflowInfo();
				principalIds.addAll(
						workflowInfo.getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(
								actionRequestCode, Long.parseLong(documentNumber)));
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid (non-numeric) document number: "+documentNumber);
			} catch(WorkflowException wex){
				throw new RuntimeException("Unable to get principal Ids with pending request " +
						"for actionRequestCode: "+actionRequestCode+" and document number: "+documentNumber);
			}
		}
		return principalIds;
    }
    
    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
	public boolean hasApplicationRole(
			String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){
		validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
		String documentNumber = qualification.get(KimAttributes.DOCUMENT_NUMBER);
		return KEWServiceLocator.getActionRequestService().doesPrincipalHaveRequest(principalId, Long.parseLong(documentNumber));
	}
	
}