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
package org.kuali.rice.kew.service;

import org.kuali.rice.kew.dto.AdHocRevokeDTO;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.MovePointDTO;
import org.kuali.rice.kew.dto.ReturnPointDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;

/**
 * A remotable service which provides an API for actions on documents.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkflowDocumentActions {

    public RouteHeaderDTO acknowledgeDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO approveDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO adHocRouteDocumentToPrincipal(UserIdDTO userId, RouteHeaderDTO routeHeader, String actionRequested, String nodeName, String annotation, String principalId, String responsibilityDesc, boolean ignorePrevActions) throws WorkflowException;
    public RouteHeaderDTO adHocRouteDocumentToGroup(UserIdDTO userId, RouteHeaderDTO routeHeader, String actionRequested, String nodeName, String annotation, String groupId, String responsibilityDesc, boolean ignorePrevActions) throws WorkflowException;
    public RouteHeaderDTO cancelDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO clearFYIDocument(UserIdDTO userId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public RouteHeaderDTO completeDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO createDocument(UserIdDTO userId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public RouteHeaderDTO disapproveDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;

    public RouteHeaderDTO routeDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO saveRoutingData(UserIdDTO userId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public RouteHeaderDTO saveDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public void deleteDocument(UserIdDTO userId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public void logDocumentAction(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserApprove(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserActionRequestApprove(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, Long actionRequestId, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserDisapprove(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserCancel(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;

    public DocumentContentDTO saveDocumentContent(DocumentContentDTO documentContent) throws WorkflowException;

    // Deprecated as of 2.1 //

    /**
     * @deprecated use blanketApproveToNodes instead
     */
    public RouteHeaderDTO blanketApproval(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation, Integer routeLevel) throws WorkflowException;

    /**
     * @deprecated use returnDocumentToPreviousNode instead
     */
    public RouteHeaderDTO returnDocumentToPreviousRouteLevel(UserIdDTO userId, RouteHeaderDTO routeHeader, Integer destRouteLevel, String annotation) throws WorkflowException;

    // Introduced in 2.1 //

    public RouteHeaderDTO blanketApprovalToNodes(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation, String[] nodeNames) throws WorkflowException;
    public RouteHeaderDTO returnDocumentToPreviousNode(UserIdDTO userId, RouteHeaderDTO routeHeader, ReturnPointDTO returnPoint, String annotation) throws WorkflowException;
    public RouteHeaderDTO takeGroupAuthority(UserIdDTO userId, RouteHeaderDTO routeHeader, String groupId, String annotation) throws WorkflowException;
    public RouteHeaderDTO releaseGroupAuthority(UserIdDTO userId, RouteHeaderDTO routeHeader, String groupId, String annotation) throws WorkflowException;
    public RouteHeaderDTO moveDocument(UserIdDTO userId, RouteHeaderDTO routeHeader, MovePointDTO movePoint, String annotation) throws WorkflowException;

    // Introduced in 2.2.2 //

    /**
     * Revokes AdHoc request(s) according to the given AppSpecificRevokeVO which is passed in.
     */
    public RouteHeaderDTO revokeAdHocRequests(UserIdDTO userId, RouteHeaderDTO routeHeader, AdHocRevokeDTO revoke, String annotation) throws WorkflowException;
    
    /**
     * @since 0.9.1.3
     */
    public void superUserNodeApproveAction(UserIdDTO userId, Long documentId, String nodeName, String annotation) throws WorkflowException;

    /**
     * @since 0.9.1.3
     */
    public void superUserReturnToPreviousNode(UserIdDTO userId, Long documentId, String destinationNodeName, String annotation) throws WorkflowException;

    /**
     * @since 0.9.1.3
     */
    public void superUserActionRequestApproveAction(UserIdDTO userId, Long documentId, Long actionRequestId, String annotation) throws WorkflowException;
    
    /**
     * @since 0.9.3
     */
    public void superUserNodeApproveAction(UserIdDTO userId, Long documentId, String nodeName, String annotation, boolean runPostProcessor) throws WorkflowException;
    
    /**
     * @since 0.9.3
     */
    public RouteHeaderDTO superUserApprove(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation, boolean runPostProcessor) throws WorkflowException;
    
    /**
     * @since 0.9.3
     */
    public RouteHeaderDTO superUserDisapprove(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation, boolean runPostProcessor) throws WorkflowException;

    /**
     * @since 0.9.3
     */
    public RouteHeaderDTO superUserCancel(UserIdDTO userId, RouteHeaderDTO routeHeader, String annotation, boolean runPostProcessor) throws WorkflowException;
    
    /**
     * @since 0.9.3
     */
    public void superUserReturnToPreviousNode(UserIdDTO userId, Long documentId, String destinationNodeName, String annotation, boolean runPostProcessor) throws WorkflowException;
    
    /**
     * @since 0.9.3
     */
    public void superUserActionRequestApproveAction(UserIdDTO userId, Long documentId, Long actionRequestId, String annotation, boolean runPostProcessor) throws WorkflowException;
    
}