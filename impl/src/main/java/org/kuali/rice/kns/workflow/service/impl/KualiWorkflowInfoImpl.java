/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.workflow.service.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.RouteTemplateEntryDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.UserDTO;
import org.kuali.rice.kew.dto.WorkflowGroupIdDTO;
import org.kuali.rice.kew.dto.WorkgroupIdDTO;
import org.kuali.rice.kew.dto.WorkgroupNameIdDTO;
import org.kuali.rice.kew.dto.WorkgroupDTO;
import org.kuali.rice.kew.exception.InvalidWorkgroupException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.spring.Cached;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;
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

    public RouteHeaderDTO getRouteHeader(UserIdDTO userId, Long routeHeaderId) throws WorkflowException {
        return getWorkflowUtility().getRouteHeader(userId, routeHeaderId);
    }

    public RouteHeaderDTO getRouteHeader(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getRouteHeader(new NetworkIdDTO(KNSConstants.SYSTEM_USER), routeHeaderId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * @deprecated
     */
    public WorkgroupDTO getWorkgroup(String workgroupName) throws WorkflowException {
        if (StringUtils.isBlank(workgroupName)) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkgroupNameIdDTO(workgroupName));// getWorkflowUtility().getWorkgroup(new
        // WorkgroupNameIdVO(workgroupName));
    }

    /**
     * @deprecated
     */
    public WorkgroupDTO getWorkgroup(Long workgroupId) throws WorkflowException {
        if (workgroupId == null) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkflowGroupIdDTO(workgroupId));
    }

    public WorkgroupDTO getWorkgroup(WorkgroupIdDTO workgroupId) throws WorkflowException {
        try {
            return getWorkflowUtility().getWorkgroup(workgroupId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    @Cached
    public UserDTO getWorkflowUser(UserIdDTO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getWorkflowUser(userId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * @deprecated use getDocType using the name
     */
    public RouteTemplateEntryDTO[] getRoute(String documentTypeName) throws WorkflowException {
        try {
            return getWorkflowUtility().getRoute(documentTypeName);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public DocumentTypeDTO getDocType(Long documentTypeId) throws WorkflowException {
        try {
            return getWorkflowUtility().getDocType(documentTypeId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public DocumentTypeDTO getDocType(String documentTypeName) throws WorkflowException {
        try {
            // throw new WorkflowException("not supported");
            return getWorkflowUtility().getDocType(documentTypeName);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public Long getNewResponsibilityId() throws WorkflowException {
        try {
            return getWorkflowUtility().getNewResponsibilityId();
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public WorkgroupDTO[] getUserWorkgroups(UserIdDTO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getUserWorkgroups(userId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public ActionRequestDTO[] getActionRequests(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionRequests(routeHeaderId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public ActionRequestDTO[] getActionRequests(Long routeHeaderId, String nodeName, UserIdDTO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionRequests(routeHeaderId, nodeName, userId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public ActionTakenDTO[] getActionsTaken(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionsTaken(routeHeaderId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public void reResolveRole(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        try {
            getWorkflowUtility().reResolveRole(documentTypeName, roleName, qualifiedRoleNameLabel);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public void reResolveRole(Long routeHeaderId, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        try {
            getWorkflowUtility().reResolveRole(routeHeaderId, roleName, qualifiedRoleNameLabel);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * 
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowInfo#routeHeaderExists(java.lang.Long)
     */
    public boolean routeHeaderExists(Long routeHeaderId) {
        if (routeHeaderId == null) {
            throw new IllegalArgumentException("Null argument passed in for routeHeaderId.");
        }

        RouteHeaderDTO routeHeader = null;
        try {
            routeHeader = getRouteHeader(routeHeaderId);
        }
        catch (WorkflowException e) {
            LOG.error("Caught Exception from workflow", e);
            throw new WorkflowRuntimeException(e);
        }

        if (routeHeader == null) {
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * @deprecated
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowInfo#documentWillHaveAtLeastOneActionRequest(org.kuali.rice.kew.dto.ReportCriteriaDTO, java.lang.String[])
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes) throws WorkflowException {
        return documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, actionRequestedCodes, false);
    }

    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowInfo#documentWillHaveAtLeastOneActionRequest(org.kuali.rice.kew.dto.ReportCriteriaDTO, java.lang.String[], boolean)
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes, boolean ignoreCurrentlyActiveRequests) throws WorkflowException {
        try {
            return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, actionRequestedCodes, ignoreCurrentlyActiveRequests);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }
    
    /**
     * @see org.kuali.rice.kns.workflow.service.KualiWorkflowInfo#getApprovalRequestedUsers(java.lang.Long)
     */
    public List<String> getApprovalRequestedUsers(Long routeHeaderId) throws WorkflowException {
        try {
            ActionItemDTO[] actionItemVOs = getWorkflowUtility().getActionItems(routeHeaderId, new String[]{KEWConstants.ACTION_REQUEST_COMPLETE_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ});
            List users = new ArrayList();
            for (int i = 0; i < actionItemVOs.length; i++) {
                ActionItemDTO actionItemVO = actionItemVOs[i];
                users.add(actionItemVO.getUser().getUuId());
            }
            return users;
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public DocumentSearchResultDTO performDocumentSearch(DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
        try {
            return getWorkflowUtility().performDocumentSearch(criteriaVO);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public DocumentSearchResultDTO performDocumentSearch(UserIdDTO userId, DocumentSearchCriteriaDTO criteriaVO) throws RemoteException, WorkflowException {
        try {
            return getWorkflowUtility().performDocumentSearch(userId, criteriaVO);
        } catch (Exception e) {
            throw new WorkflowException(e);
        }
    }
}
