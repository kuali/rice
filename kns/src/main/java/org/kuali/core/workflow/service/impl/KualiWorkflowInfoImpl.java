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
package org.kuali.core.workflow.service.impl;

import org.apache.log4j.Logger;
import org.kuali.RiceConstants;
import org.kuali.core.util.spring.Cached;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.RouteTemplateEntryVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.InvalidWorkgroupException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.util.Utilities;

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

    public RouteHeaderVO getRouteHeader(UserIdVO userId, Long routeHeaderId) throws WorkflowException {
        return getWorkflowUtility().getRouteHeader(userId, routeHeaderId);
    }

    public RouteHeaderVO getRouteHeader(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getRouteHeader(new NetworkIdVO(RiceConstants.SYSTEM_USER), routeHeaderId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    /**
     * @deprecated
     */
    public WorkgroupVO getWorkgroup(String workgroupName) throws WorkflowException {
        if (Utilities.isEmpty(workgroupName)) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkgroupNameIdVO(workgroupName));// getWorkflowUtility().getWorkgroup(new
        // WorkgroupNameIdVO(workgroupName));
    }

    /**
     * @deprecated
     */
    public WorkgroupVO getWorkgroup(Long workgroupId) throws WorkflowException {
        if (workgroupId == null) {
            throw new InvalidWorkgroupException("Workgroup name cannot be empty");
        }
        return getWorkgroup(new WorkflowGroupIdVO(workgroupId));
    }

    public WorkgroupVO getWorkgroup(WorkgroupIdVO workgroupId) throws WorkflowException {
        try {
            return getWorkflowUtility().getWorkgroup(workgroupId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    @Cached
    public UserVO getWorkflowUser(UserIdVO userId) throws WorkflowException {
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
    public RouteTemplateEntryVO[] getRoute(String documentTypeName) throws WorkflowException {
        try {
            return getWorkflowUtility().getRoute(documentTypeName);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public DocumentTypeVO getDocType(Long documentTypeId) throws WorkflowException {
        try {
            return getWorkflowUtility().getDocType(documentTypeId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public DocumentTypeVO getDocType(String documentTypeName) throws WorkflowException {
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

    public WorkgroupVO[] getUserWorkgroups(UserIdVO userId) throws WorkflowException {
        try {
            return getWorkflowUtility().getUserWorkgroups(userId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public ActionRequestVO[] getActionRequests(Long routeHeaderId) throws WorkflowException {
        try {
            return getWorkflowUtility().getActionRequests(routeHeaderId);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }

    public ActionTakenVO[] getActionsTaken(Long routeHeaderId) throws WorkflowException {
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
     * @see org.kuali.core.workflow.service.KualiWorkflowInfo#routeHeaderExists(java.lang.Long)
     */
    public boolean routeHeaderExists(Long routeHeaderId) {
        if (routeHeaderId == null) {
            throw new IllegalArgumentException("Null argument passed in for routeHeaderId.");
        }

        RouteHeaderVO routeHeader = null;
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
     *  @see org.kuali.core.workflow.service.KualiWorkflowInfo#documentWillHaveAtLeastOneActionRequest(java.lang.Long, java.lang.String, java.lang.String[])
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaVO reportCriteriaVO, String[] actionRequestedCodes) throws WorkflowException {
        try {
            return getWorkflowUtility().documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, actionRequestedCodes);
        }
        catch (Exception e) {
            throw new WorkflowException(e);
        }
    }
}
