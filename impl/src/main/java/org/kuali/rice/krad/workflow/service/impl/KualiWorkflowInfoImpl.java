/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.workflow.service.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.krad.workflow.service.KualiWorkflowInfo;
import org.springframework.transaction.annotation.Transactional;


@SuppressWarnings("deprecation")
@Transactional
public class KualiWorkflowInfoImpl implements KualiWorkflowInfo {

    private static final Logger LOG = Logger.getLogger(KualiWorkflowInfoImpl.class);
    
    private WorkflowInfo workflowInfo;

    public KualiWorkflowInfoImpl() {
        workflowInfo = new WorkflowInfo();
    }

    private WorkflowInfo getWorkflowUtility() {
        return workflowInfo;
    }

    public Long getNewResponsibilityId() throws WorkflowException {
    	return getWorkflowUtility().getNewResponsibilityId();
    }

    public ActionRequestDTO[] getActionRequests(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getActionRequests(documentId);
    }

    public ActionRequestDTO[] getActionRequests(String documentId, String nodeName, String principalId) throws WorkflowException {
    	return getWorkflowUtility().getActionRequests(documentId, nodeName, principalId);
    }

    public ActionTakenDTO[] getActionsTaken(String documentId) throws WorkflowException {
    	return getWorkflowUtility().getActionsTaken(documentId);
    }
  
    public void reResolveRoleByDocTypeName(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
    	getWorkflowUtility().reResolveRoleByDocTypeName(documentTypeName, roleName, qualifiedRoleNameLabel);
    }

    public void reResolveRoleByDocumentId(String documentId, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
    	getWorkflowUtility().reResolveRoleByDocumentId(documentId, roleName, qualifiedRoleNameLabel);
    }
        
    /**
     * @deprecated
     * @see org.kuali.rice.krad.workflow.service.KualiWorkflowInfo#documentWillHaveAtLeastOneActionRequest(org.kuali.rice.kew.dto.ReportCriteriaDTO, java.lang.String[])
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes) throws WorkflowException {
        return documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, actionRequestedCodes, false);
    }

    /**
     * @see org.kuali.rice.krad.workflow.service.KualiWorkflowInfo#documentWillHaveAtLeastOneActionRequest(org.kuali.rice.kew.dto.ReportCriteriaDTO, java.lang.String[], boolean)
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes, boolean ignoreCurrentlyActiveRequests) throws WorkflowException {
    	return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, actionRequestedCodes, ignoreCurrentlyActiveRequests);
    }
    
    /**
     * @see org.kuali.rice.krad.workflow.service.KualiWorkflowInfo#getApprovalRequestedUsers(java.lang.String)
     */
    public List<String> getApprovalRequestedUsers(String documentId) throws WorkflowException {
    	ActionItemDTO[] actionItemVOs = getWorkflowUtility().getActionItems(documentId, new String[]{KEWConstants.ACTION_REQUEST_COMPLETE_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ});
    	List<String> users = new ArrayList<String>();
    	for (int i = 0; i < actionItemVOs.length; i++) {
    		ActionItemDTO actionItemVO = actionItemVOs[i];
    		users.add(actionItemVO.getPrincipalId());
    	}
    	return users;
    }

    public DocumentSearchResultDTO performDocumentSearch(DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
    	return getWorkflowUtility().performDocumentSearch(criteriaVO);
    }

    public DocumentSearchResultDTO performDocumentSearch(String principalId, DocumentSearchCriteriaDTO criteriaVO) throws RemoteException, WorkflowException {
    	return getWorkflowUtility().performDocumentSearch(principalId, criteriaVO);
    }
    
}
