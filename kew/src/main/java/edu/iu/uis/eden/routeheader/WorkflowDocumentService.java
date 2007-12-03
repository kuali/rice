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
package edu.iu.uis.eden.routeheader;

import java.util.List;
import java.util.Set;

import edu.iu.uis.eden.actions.ActionTakenEvent;
import edu.iu.uis.eden.actions.AdHocRevoke;
import edu.iu.uis.eden.actions.MovePoint;
import edu.iu.uis.eden.exception.DocumentTypeNotFoundException;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.server.WorkflowDocumentActions;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Service for initiating actions against documents.  Uses from the service endpoint
 * for the client API.
 *
 * @see WorkflowDocumentActions
 * @see ActionTakenEvent
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkflowDocumentService {

    public DocumentRouteHeaderValue acknowledgeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue approveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue appSpecificRouteDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String actionRequested, String routeMethodName, String annotation, Recipient rp, String responsibilityDesc, Boolean ignorePrevActions) throws WorkflowException;
    public DocumentRouteHeaderValue cancelDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue clearFYIDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue completeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue createDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws DocumentTypeNotFoundException, WorkflowException;
    public DocumentRouteHeaderValue disapproveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    public DocumentRouteHeaderValue routeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws WorkflowException, InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue saveRoutingData(WorkflowUser user, DocumentRouteHeaderValue routeHeader);
    public DocumentRouteHeaderValue saveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public void deleteDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws WorkflowException;
    public void logDocumentAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws EdenUserNotFoundException, InvalidActionTakenException;

    public DocumentRouteHeaderValue superUserActionRequestApproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Long actionRequestId, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue superUserApprove(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue superUserCancelAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;
    public DocumentRouteHeaderValue superUserDisapproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    // Introduced in 2.1 //

    /**
     * @since 2.1
     */
    public DocumentRouteHeaderValue blanketApproval(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, Set nodeNames) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @since 2.1
     */
    public DocumentRouteHeaderValue returnDocumentToPreviousNode(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String destinationNodeName, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @since 2.1
     */
    public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @since 2.1
     */
    public DocumentRouteHeaderValue takeWorkgroupAuthority(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Workgroup workgroup, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @since 2.1
     */
    public DocumentRouteHeaderValue releaseWorkgroupAuthority(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Workgroup workgroup, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @since 2.1
     */
    public DocumentRouteHeaderValue superUserNodeApproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @since 2.1
     */
    public DocumentRouteHeaderValue moveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, MovePoint movePoint, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    // Introduced in 2.2

    /**
     * Invokes a List of actions at once.  This method will remove the approriate action items from the user's action
     * list and then schedule the actual processing of the actions.
     *
     * @since 2.2
     */
    public void takeMassActions(WorkflowUser user, List actionInvocations);

    /**
     * @since 2.2
     */
    public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(WorkflowUser user, Long documentId, String nodeName, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @since 2.2.7
     */
    public DocumentRouteHeaderValue superUserNodeApproveAction(WorkflowUser user, Long documentId, String nodeName, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    // Introduced in 2.2.2

    /**
     * @since 2.2.2
     */
    public DocumentRouteHeaderValue revokeAdHocRequests(WorkflowUser user, DocumentRouteHeaderValue document, AdHocRevoke revoke, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

    // Deprecated as of 2.1 //

    /**
     * @deprecated use blanketApproval which takes a Set of nodeNames instead.
     */
    public DocumentRouteHeaderValue blanketApproval(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, Integer routeLevel) throws InvalidActionTakenException, EdenUserNotFoundException;

    /**
     * @deprecated use returnDocumentToPreviousNode instead
     */
    public DocumentRouteHeaderValue returnDocumentToPreviousRouteLevel(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Integer destRouteLevel, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException;

}
