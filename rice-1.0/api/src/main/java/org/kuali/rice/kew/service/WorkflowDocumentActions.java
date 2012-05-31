/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.service;

import org.kuali.rice.kew.dto.AdHocRevokeDTO;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.MovePointDTO;
import org.kuali.rice.kew.dto.ReturnPointDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.exception.WorkflowException;

/**
 * A remotable service which provides an API for actions on documents.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface WorkflowDocumentActions {

    public RouteHeaderDTO acknowledgeDocument(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO approveDocument(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO adHocRouteDocumentToPrincipal(String principalId, RouteHeaderDTO routeHeader, String actionRequested, String nodeName, String annotation, String targetPrincipalId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException;
    public RouteHeaderDTO adHocRouteDocumentToGroup(String principalId, RouteHeaderDTO routeHeader, String actionRequested, String nodeName, String annotation, String groupId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException;
    public RouteHeaderDTO cancelDocument(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO clearFYIDocument(String principalId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public RouteHeaderDTO completeDocument(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO createDocument(String principalId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public RouteHeaderDTO disapproveDocument(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;

    public RouteHeaderDTO routeDocument(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO saveRoutingData(String principalId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public RouteHeaderDTO saveDocument(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public void deleteDocument(String principalId, RouteHeaderDTO routeHeader) throws WorkflowException;
    public void logDocumentAction(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserApprove(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserActionRequestApprove(String principalId, RouteHeaderDTO routeHeaderVO, Long actionRequestId, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserDisapprove(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;
    public RouteHeaderDTO superUserCancel(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;

    public DocumentContentDTO saveDocumentContent(DocumentContentDTO documentContent) throws WorkflowException;
    
    public RouteHeaderDTO placeInExceptionRouting(String principalId, RouteHeaderDTO routeHeader, String annotation) throws WorkflowException;

    // Deprecated as of 2.1 //

    /**
     * @deprecated use blanketApproveToNodes instead
     */
    public RouteHeaderDTO blanketApproval(String principalId, RouteHeaderDTO routeHeader, String annotation, Integer routeLevel) throws WorkflowException;

    /**
     * @deprecated use returnDocumentToPreviousNode instead
     */
    public RouteHeaderDTO returnDocumentToPreviousRouteLevel(String principalId, RouteHeaderDTO routeHeader, Integer destRouteLevel, String annotation) throws WorkflowException;

    // Introduced in 2.1 //

    public RouteHeaderDTO blanketApprovalToNodes(String principalId, RouteHeaderDTO routeHeader, String annotation, String[] nodeNames) throws WorkflowException;
    public RouteHeaderDTO returnDocumentToPreviousNode(String principalId, RouteHeaderDTO routeHeader, ReturnPointDTO returnPoint, String annotation) throws WorkflowException;
    public RouteHeaderDTO takeGroupAuthority(String principalId, RouteHeaderDTO routeHeader, String groupId, String annotation) throws WorkflowException;
    public RouteHeaderDTO releaseGroupAuthority(String principalId, RouteHeaderDTO routeHeader, String groupId, String annotation) throws WorkflowException;
    public RouteHeaderDTO moveDocument(String principalId, RouteHeaderDTO routeHeader, MovePointDTO movePoint, String annotation) throws WorkflowException;

    // Introduced in 2.2.2 //

    /**
     * Revokes AdHoc request(s) according to the given AppSpecificRevokeVO which is passed in.
     */
    public RouteHeaderDTO revokeAdHocRequests(String principalId, RouteHeaderDTO routeHeader, AdHocRevokeDTO revoke, String annotation) throws WorkflowException;

    /**
     * @since 0.9.1.3
     */
    public void superUserNodeApproveAction(String principalId, Long documentId, String nodeName, String annotation) throws WorkflowException;

    /**
     * @since 0.9.1.3
     */
    public void superUserReturnToPreviousNode(String principalId, Long documentId, String destinationNodeName, String annotation) throws WorkflowException;

    /**
     * @since 0.9.1.3
     */
    public void superUserActionRequestApproveAction(String principalId, Long documentId, Long actionRequestId, String annotation) throws WorkflowException;

    /**
     * @since 0.9.3
     */
    public void superUserNodeApproveAction(String principalId, Long documentId, String nodeName, String annotation, boolean runPostProcessor) throws WorkflowException;

    /**
     * @since 0.9.3
     */
    public RouteHeaderDTO superUserApprove(String principalId, RouteHeaderDTO routeHeader, String annotation, boolean runPostProcessor) throws WorkflowException;

    /**
     * @since 0.9.3
     */
    public RouteHeaderDTO superUserDisapprove(String principalId, RouteHeaderDTO routeHeader, String annotation, boolean runPostProcessor) throws WorkflowException;

    /**
     * @since 0.9.3
     */
    public RouteHeaderDTO superUserCancel(String principalId, RouteHeaderDTO routeHeader, String annotation, boolean runPostProcessor) throws WorkflowException;

    /**
     * @since 0.9.3
     */
    public void superUserReturnToPreviousNode(String principalId, Long documentId, String destinationNodeName, String annotation, boolean runPostProcessor) throws WorkflowException;

    /**
     * @since 0.9.3
     */
    public void superUserActionRequestApproveAction(String principalId, Long documentId, Long actionRequestId, String annotation, boolean runPostProcessor) throws WorkflowException;
    
    /**
     * 
     * This method This mehtod indexes a document based on the documentId.
     * 
     * @param documentId
     */
    public void indexDocument(Long documentId);
        
}
