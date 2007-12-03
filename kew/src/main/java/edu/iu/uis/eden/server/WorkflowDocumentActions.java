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

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.AdHocRevokeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.MovePointVO;
import edu.iu.uis.eden.clientapp.vo.ResponsiblePartyVO;
import edu.iu.uis.eden.clientapp.vo.ReturnPointVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * A remotable service which provides an API for actions on documents.
 *
 * @see WorkflowDocument
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WorkflowDocumentActions extends Remote {

    public RouteHeaderVO acknowledgeDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO approveDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO appSpecificRouteDocument(UserIdVO userId, RouteHeaderVO routeHeader, String actionRequested, String nodeName, String annotation, ResponsiblePartyVO responsibleParty, String responsibilityDesc, boolean ignorePrevActions) throws RemoteException, WorkflowException;
    public RouteHeaderVO cancelDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO clearFYIDocument(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException, WorkflowException;
    public RouteHeaderVO completeDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO createDocument(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException, WorkflowException;
    public RouteHeaderVO disapproveDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;

    public RouteHeaderVO routeDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO saveRoutingData(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException, WorkflowException;
    public RouteHeaderVO saveDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public void deleteDocument(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException, WorkflowException;
    public void logDocumentAction(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO superUserApprove(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO superUserActionRequestApprove(UserIdVO userId, RouteHeaderVO routeHeaderVO, Long actionRequestId, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO superUserDisapprove(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO superUserCancel(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException, WorkflowException;

    public DocumentContentVO saveDocumentContent(DocumentContentVO documentContent) throws RemoteException, WorkflowException;

    // Deprecated as of 2.1 //

    /**
     * @deprecated use blanketApproveToNodes instead
     */
    public RouteHeaderVO blanketApproval(UserIdVO userId, RouteHeaderVO routeHeader, String annotation, Integer routeLevel) throws RemoteException, WorkflowException;

    /**
     * @deprecated use returnDocumentToPreviousNode instead
     */
    public RouteHeaderVO returnDocumentToPreviousRouteLevel(UserIdVO userId, RouteHeaderVO routeHeader, Integer destRouteLevel, String annotation) throws RemoteException, WorkflowException;

    // Introduced in 2.1 //

    public RouteHeaderVO blanketApprovalToNodes(UserIdVO userId, RouteHeaderVO routeHeader, String annotation, String[] nodeNames) throws RemoteException, WorkflowException;
    public RouteHeaderVO returnDocumentToPreviousNode(UserIdVO userId, RouteHeaderVO routeHeader, ReturnPointVO returnPoint, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO takeWorkgroupAuthority(UserIdVO userId, RouteHeaderVO routeHeader, WorkgroupIdVO workgroupId, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO releaseWorkgroupAuthority(UserIdVO userId, RouteHeaderVO routeHeader, WorkgroupIdVO workgroupId, String annotation) throws RemoteException, WorkflowException;
    public RouteHeaderVO moveDocument(UserIdVO userId, RouteHeaderVO routeHeader, MovePointVO movePoint, String annotation) throws RemoteException, WorkflowException;

    // Introduced in 2.2.2 //

    /**
     * Revokes AdHoc request(s) according to the given AppSpecificRevokeVO which is passed in.
     */
    public RouteHeaderVO revokeAdHocRequests(UserIdVO userId, RouteHeaderVO routeHeader, AdHocRevokeVO revoke, String annotation) throws RemoteException, WorkflowException;

}