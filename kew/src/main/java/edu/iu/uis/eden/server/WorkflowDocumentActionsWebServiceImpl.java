/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.rmi.RemoteException;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.vo.AdHocRevokeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.MovePointVO;
import edu.iu.uis.eden.clientapp.vo.ResponsiblePartyVO;
import edu.iu.uis.eden.clientapp.vo.ReturnPointVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;

public class WorkflowDocumentActionsWebServiceImpl implements WorkflowDocumentActions {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowDocumentActionsWebServiceImpl.class);
    
    private DocumentRouteHeaderValue init(RouteHeaderVO routeHeaderVO) throws WorkflowException {
        incomingParamCheck(routeHeaderVO, "routeHeaderVO");
        Long documentId = routeHeaderVO.getRouteHeaderId();
        LOG.debug("Initializing Document from incoming RouteHeaderVO [docId=" + documentId + "]");
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
        
//      update notes database based on notes and notesToDelete arrays in routeHeaderVO
        BeanConverter.updateNotes(routeHeaderVO, routeHeaderVO.getRouteHeaderId());
        
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        document.setRouteHeaderData(routeHeaderVO);
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
        return document;
    }
    
    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            LOG.error("null " + name + " passed in.");
            throw new RuntimeException("null " + name + " passed in.");
        }
    }
    
    public RouteHeaderVO releaseWorkgroupAuthority(UserIdVO userId, RouteHeaderVO routeHeaderVO, WorkgroupIdVO workgroupId, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Releasing workgroup authority [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", workgroupId=" + workgroupId + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(workgroupId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().releaseWorkgroupAuthority(user, routeHeader, workgroup, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO takeWorkgroupAuthority(UserIdVO userId, RouteHeaderVO routeHeaderVO, WorkgroupIdVO workgroupId, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Taking workgroup authority [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", workgroupId=" + workgroupId + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(workgroupId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().takeWorkgroupAuthority(user, routeHeader, workgroup, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO acknowledgeDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Acknowledge [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().acknowledgeDocument(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO approveDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().approveDocument(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO appSpecificRouteDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String actionRequested, String nodeName, String annotation, ResponsiblePartyVO responsiblePartyVO, String responsibilityDesc, boolean ignorePrevActions) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        incomingParamCheck(actionRequested, "actionRequested");
        //incomingParamCheck(routeMethodName, "routeMethodName");
        incomingParamCheck(responsiblePartyVO, "responsiblePartyVO");
        LOG.debug("AdHoc Route [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", actionRequest=" + actionRequested + ", nodeName=" + nodeName + ", responsibleParty=" + responsiblePartyVO + ", ignorePrevious=" + ignorePrevActions + ", annotation="+annotation+"]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        
        Recipient recipient = BeanConverter.convertResponsiblePartyVOtoRecipient(responsiblePartyVO);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().appSpecificRouteDocument(user, routeHeader, actionRequested, nodeName, annotation, recipient, responsibilityDesc, new Boolean(ignorePrevActions));
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO blanketApproval(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation, Integer routeLevel) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Blanket Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", routeLevel=" + routeLevel + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().blanketApproval(user, routeHeader, annotation, routeLevel);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }

    public RouteHeaderVO blanketApprovalToNodes(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation, String[] nodeNames) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Blanket Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", nodeNames=" + ArrayUtils.toString(nodeNames) + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        Set nodeNameSet = Utilities.asSet(nodeNames);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().blanketApproval(user, routeHeader, annotation, nodeNameSet);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }

    
    public RouteHeaderVO cancelDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Cancel [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().cancelDocument(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO clearFYIDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Clear FYI [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().clearFYIDocument(user, routeHeader);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO completeDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Complete [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().completeDocument(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO createDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO) throws RemoteException, WorkflowException {
        incomingParamCheck(userId, "userId");
        incomingParamCheck(routeHeaderVO, "routeHeaderVO");
        LOG.debug("Create Document [userId=" + userId + ", docTypeName=" + routeHeaderVO.getDocTypeName() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);        
        DocumentRouteHeaderValue routeHeader = BeanConverter.convertRouteHeaderVO(routeHeaderVO);
        routeHeader.setInitiatorWorkflowId(user.getWorkflowUserId().getWorkflowId());
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().createDocument(user, routeHeader);
        
//      update notes database based on notes and notesToDelete arrays in routeHeaderVO
        BeanConverter.updateNotes(routeHeaderVO, routeHeader.getRouteHeaderId());
        
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO disapproveDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Disapprove [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().disapproveDocument(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO returnDocumentToPreviousRouteLevel(UserIdVO userId, RouteHeaderVO routeHeaderVO, Integer destRouteLevel, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        incomingParamCheck(destRouteLevel, "destRouteLevel");
        LOG.debug("Return to Previous [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", destRouteLevel=" + destRouteLevel + "]");        
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().returnDocumentToPreviousRouteLevel(user, routeHeader, destRouteLevel, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO returnDocumentToPreviousNode(UserIdVO userId, RouteHeaderVO routeHeaderVO, ReturnPointVO returnPoint, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        incomingParamCheck(returnPoint, "returnPoint");
        LOG.debug("Return to Previous [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", destNodeName=" + returnPoint.getNodeName() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().returnDocumentToPreviousNode(user, routeHeader, returnPoint.getNodeName(), annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO routeDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Route Document [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().routeDocument(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO saveRoutingData(UserIdVO userId, RouteHeaderVO routeHeaderVO) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Saving Routing Data [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().saveRoutingData(user, routeHeader);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO saveDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Save Document [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().saveDocument(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public void deleteDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Delete [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        KEWServiceLocator.getWorkflowDocumentService().deleteDocument(user, routeHeader);
    }
    
    public void logDocumentAction(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Log [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        KEWServiceLocator.getWorkflowDocumentService().logDocumentAction(user, routeHeader, annotation);
    }
    
    public RouteHeaderVO moveDocument(UserIdVO userId, RouteHeaderVO routeHeaderVO, MovePointVO movePoint, String annotation) throws RemoteException, WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Move [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", startNode=" + movePoint.getStartNodeName() + "steps=" + movePoint.getStepsToMove() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().moveDocument(user, routeHeader, BeanConverter.convertMovePointVO(movePoint), annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }

    public RouteHeaderVO revokeAdHocRequests(UserIdVO userId, RouteHeaderVO routeHeaderVO, AdHocRevokeVO revoke, String annotation) throws RemoteException, WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Revoke AdHoc [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().revokeAdHocRequests(user, routeHeader, BeanConverter.convertAdHocRevokeVO(revoke), annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO superUserApprove(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("SU Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserApprove(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderVO superUserActionRequestApprove(UserIdVO userId, RouteHeaderVO routeHeaderVO, Long actionRequestId, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
	incomingParamCheck(userId, "userId");
	LOG.debug("SU Cancel [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
	WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
	routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(user, routeHeader, actionRequestId, annotation);
	return BeanConverter.convertRouteHeader(routeHeader, user);
    }

    public RouteHeaderVO superUserDisapprove(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("SU Disapprove [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserDisapproveAction(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);    	
    }
    
    public RouteHeaderVO superUserCancel(UserIdVO userId, RouteHeaderVO routeHeaderVO, String annotation) throws RemoteException, WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("SU Cancel [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserCancelAction(user, routeHeader, annotation);
        return BeanConverter.convertRouteHeader(routeHeader, user);
    }

	public DocumentContentVO saveDocumentContent(DocumentContentVO documentContent) throws RemoteException, WorkflowException {
		incomingParamCheck(documentContent, "documentContent");
		Long documentId = documentContent.getRouteHeaderId();
		incomingParamCheck(documentId, "documentContent document ID");
		KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);        
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		LOG.debug("Saving Document Content [documentId=" + documentId + "]");
		String updatedDocumentContent = BeanConverter.buildUpdatedDocumentContent(documentContent);
    	// updatedDocumentContent will be null if the content has not changed, only update if its changed
    	if (updatedDocumentContent != null) {
    		document.setDocContent(updatedDocumentContent);
    		KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
    	}
    	return BeanConverter.convertDocumentContent(document.getDocContent(), documentId);
	}
    
    
}