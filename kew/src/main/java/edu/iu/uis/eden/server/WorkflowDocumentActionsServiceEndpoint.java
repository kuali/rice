/*
 * Copyright 2005-2007 The Kuali Foundation. Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.server;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.SpringLoader;
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
 * @workflow.webservice name="WorkflowDocumentActions"
 */
public class WorkflowDocumentActionsServiceEndpoint implements WorkflowDocumentActions {

    private WorkflowDocumentActions documentActions;

    public void init(Object context) {
	// fetch the service directly from the SpringLoader to gaurantee we are fetching the local, in-memory service
	this.documentActions = (WorkflowDocumentActions) SpringLoader.getInstance().getService(
		new QName(KEWServiceLocator.WORKFLOW_DOCUMENT_ACTIONS_SERVICE));
    }

    public void destroy() {
    }

    public RouteHeaderVO releaseWorkgroupAuthority(UserIdVO userId, RouteHeaderVO routeHeader, WorkgroupIdVO workgroupId,
	    String annotation) throws RemoteException, WorkflowException {
	return documentActions.takeWorkgroupAuthority(userId, routeHeader, workgroupId, annotation);
    }

    public RouteHeaderVO acknowledgeDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.acknowledgeDocument(userId, routeHeader, annotation);
    }

    public RouteHeaderVO approveDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.approveDocument(userId, routeHeader, annotation);
    }

    public RouteHeaderVO appSpecificRouteDocument(UserIdVO userId, RouteHeaderVO routeHeader, String actionRequested,
	    String nodeName, String annotation, ResponsiblePartyVO responsibleParty, String responsibilityDesc,
	    boolean ignorePrevActions) throws RemoteException, WorkflowException {
	return documentActions.appSpecificRouteDocument(userId, routeHeader, actionRequested, nodeName, annotation,
		responsibleParty, responsibilityDesc, ignorePrevActions);
    }

    public RouteHeaderVO revokeAdHocRequests(UserIdVO userId, RouteHeaderVO routeHeader, AdHocRevokeVO revoke,
	    String annotation) throws RemoteException, WorkflowException {
	return documentActions.revokeAdHocRequests(userId, routeHeader, revoke, annotation);
    }

    /**
     * @deprecated see WorkflowDocumentActions
     */
    public RouteHeaderVO blanketApproval(UserIdVO userId, RouteHeaderVO routeHeader, String annotation, Integer routeLevel)
	    throws RemoteException, WorkflowException {
	return documentActions.blanketApproval(userId, routeHeader, annotation, routeLevel);
    }

    public RouteHeaderVO blanketApprovalToNodes(UserIdVO userId, RouteHeaderVO routeHeader, String annotation,
	    String[] nodeNames) throws RemoteException, WorkflowException {
	return documentActions.blanketApprovalToNodes(userId, routeHeader, annotation, nodeNames);
    }

    public RouteHeaderVO cancelDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.cancelDocument(userId, routeHeader, annotation);
    }

    public RouteHeaderVO clearFYIDocument(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException,
	    WorkflowException {
	return documentActions.clearFYIDocument(userId, routeHeader);
    }

    public RouteHeaderVO completeDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.completeDocument(userId, routeHeader, annotation);
    }

    public RouteHeaderVO createDocument(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException,
	    WorkflowException {
	return documentActions.createDocument(userId, routeHeader);
    }

    public void deleteDocument(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException, WorkflowException {
	documentActions.deleteDocument(userId, routeHeader);
    }

    public RouteHeaderVO disapproveDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.disapproveDocument(userId, routeHeader, annotation);
    }

    public void logDocumentAction(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException,
	    WorkflowException {
	documentActions.logDocumentAction(userId, routeHeader, annotation);
    }

    /**
     * @deprecated see WorkflowDocumentActions
     */
    public RouteHeaderVO returnDocumentToPreviousRouteLevel(UserIdVO userId, RouteHeaderVO routeHeader,
	    Integer destRouteLevel, String annotation) throws RemoteException, WorkflowException {
	return documentActions.returnDocumentToPreviousRouteLevel(userId, routeHeader, destRouteLevel, annotation);
    }

    public RouteHeaderVO routeDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.routeDocument(userId, routeHeader, annotation);
    }

    public RouteHeaderVO saveDocument(UserIdVO userId, RouteHeaderVO routeHeader, String annotation) throws RemoteException,
	    WorkflowException {
	return documentActions.saveDocument(userId, routeHeader, annotation);
    }

    public RouteHeaderVO saveRoutingData(UserIdVO userId, RouteHeaderVO routeHeader) throws RemoteException,
	    WorkflowException {
	return documentActions.saveRoutingData(userId, routeHeader);
    }

    public RouteHeaderVO takeWorkgroupAuthority(UserIdVO userId, RouteHeaderVO routeHeader, WorkgroupIdVO workgroupId,
	    String annotation) throws RemoteException, WorkflowException {
	return documentActions.takeWorkgroupAuthority(userId, routeHeader, workgroupId, annotation);
    }

    public RouteHeaderVO returnDocumentToPreviousNode(UserIdVO userId, RouteHeaderVO routeHeader, ReturnPointVO returnPoint,
	    String annotation) throws RemoteException, WorkflowException {
	return documentActions.returnDocumentToPreviousNode(userId, routeHeader, returnPoint, annotation);
    }

    public RouteHeaderVO moveDocument(UserIdVO userId, RouteHeaderVO routeHeader, MovePointVO movePoint, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.moveDocument(userId, routeHeader, movePoint, annotation);
    }

    public RouteHeaderVO superUserApprove(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.superUserApprove(userId, routeHeaderVO, annotation);
    }

    public RouteHeaderVO superUserActionRequestApprove(UserIdVO userId, RouteHeaderVO routeHeaderVO, Long actionRequestId, 
	    String annotation) throws RemoteException, WorkflowException {
        return documentActions.superUserActionRequestApprove(userId, routeHeaderVO, actionRequestId, annotation);
    }
    
    public RouteHeaderVO superUserDisapprove(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.superUserDisapprove(userId, routeHeaderVO, annotation);
    }

    public RouteHeaderVO superUserCancel(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation)
	    throws RemoteException, WorkflowException {
	return documentActions.superUserCancel(userId, routeHeaderVO, annotation);
    }

    public DocumentContentVO saveDocumentContent(DocumentContentVO documentContent) throws RemoteException,
	    WorkflowException {
	return documentActions.saveDocumentContent(documentContent);
    }

}
