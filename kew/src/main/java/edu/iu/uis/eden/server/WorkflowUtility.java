/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.RouteNodeInstanceVO;
import edu.iu.uis.eden.clientapp.vo.RouteTemplateEntryVO;
import edu.iu.uis.eden.clientapp.vo.RuleReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RuleVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * A remotable service which provides an API for performing various queries
 * and other utilities on KEW.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkflowUtility extends Remote {
    public RouteHeaderVO getRouteHeaderWithUser(UserIdVO userId, Long documentId) throws RemoteException, WorkflowException;
    public RouteHeaderVO getRouteHeader(Long documentId) throws RemoteException, WorkflowException;
    public DocumentDetailVO getDocumentDetail(Long documentId) throws RemoteException, WorkflowException;
    public RouteNodeInstanceVO getNodeInstance(Long nodeInstanceId) throws RemoteException, WorkflowException;
    public WorkgroupVO getWorkgroup(WorkgroupIdVO workgroupId) throws RemoteException, WorkflowException;
    public UserVO getWorkflowUser(UserIdVO userId) throws RemoteException, WorkflowException;
    public RouteTemplateEntryVO[] getDocRoute(String docName) throws RemoteException, WorkflowException;
    public DocumentTypeVO getDocumentType(Long documentTypeId) throws RemoteException, WorkflowException;
    public DocumentTypeVO getDocumentTypeByName(String documentTypeName) throws RemoteException, WorkflowException;
    public Long getNewResponsibilityId() throws RemoteException, WorkflowException;
    public WorkgroupVO[] getUserWorkgroups(UserIdVO userId) throws RemoteException, WorkflowException;
    public ActionRequestVO[] getActionRequests(Long documentId) throws RemoteException, WorkflowException;
    public ActionRequestVO[] getActionRequests(Long routeHeaderId, String nodeName, UserIdVO userId) throws RemoteException, WorkflowException;
    public ActionTakenVO[] getActionsTaken(Long documentId) throws RemoteException, WorkflowException;
    public WorkflowAttributeValidationErrorVO[] validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionVO definition) throws RemoteException, WorkflowException;
    public boolean isUserInRouteLog(Long documentId, UserIdVO userId, boolean lookFuture) throws RemoteException, WorkflowException;
    public void reResolveRole(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws RemoteException, WorkflowException;
    public void reResolveRoleByDocumentId(Long documentId, String roleName, String qualifiedRoleNameLabel) throws RemoteException, WorkflowException;
    public DocumentDetailVO routingReport(ReportCriteriaVO reportCriteria) throws RemoteException, WorkflowException;
    //public RouteHeaderDetailVO routingSimulation(RouteHeaderDetailVO detail, ActionTakenVO[] actionsToTake) throws RemoteException, WorkflowException;
    public boolean isFinalApprover(Long documentId, UserIdVO userId) throws RemoteException, WorkflowException;
    public boolean isSuperUserForDocumentType(UserIdVO userId, Long documentTypeId) throws RemoteException, WorkflowException;

    // new in 2.3

    public RuleVO[] ruleReport(RuleReportCriteriaVO ruleReportCriteria) throws RemoteException, WorkflowException;

    // deprecated as of 2.1

    /**
     * @deprecated use isLastApproverAtNode instead
     */
    public boolean isLastApproverInRouteLevel(Long routeHeaderId, UserIdVO userId, Integer routeLevel) throws RemoteException, WorkflowException;

    /**
     * @deprecated use routeNodeHasApproverActionRequest instead
     */
    public boolean routeLevelHasApproverActionRequest(String docType, String docContent, Integer routeLevel) throws RemoteException, WorkflowException;

    // new in 2.1

    public boolean isLastApproverAtNode(Long documentId, UserIdVO userId, String nodeName) throws RemoteException, WorkflowException;
    public boolean routeNodeHasApproverActionRequest(String docType, String docContent, String nodeName) throws RemoteException, WorkflowException;
    public RouteNodeInstanceVO[] getDocumentRouteNodeInstances(Long documentId) throws RemoteException, WorkflowException;
    public RouteNodeInstanceVO[] getActiveNodeInstances(Long documentId) throws RemoteException, WorkflowException;
    public RouteNodeInstanceVO[] getTerminalNodeInstances(Long documentId) throws RemoteException, WorkflowException;
    public DocumentContentVO getDocumentContent(Long routeHeaderId) throws RemoteException, WorkflowException;

    //2.2
    public String[] getPreviousRouteNodeNames(Long documentId) throws RemoteException, WorkflowException;

    // 2.4
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaVO reportCriteriaVO, String[] actionRequestedCodes) throws RemoteException;

    /**
     * @since 0.9.1
     */
    public String getDocumentStatus(Long documentId) throws RemoteException, WorkflowException;
}
