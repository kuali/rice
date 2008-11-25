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
package org.kuali.rice.kew.service.impl;

import java.rmi.RemoteException;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.rice.kew.dto.AdHocRevokeDTO;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.MovePointDTO;
import org.kuali.rice.kew.dto.ResponsiblePartyDTO;
import org.kuali.rice.kew.dto.ReturnPointDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.WorkgroupIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocumentActions;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;


public class WorkflowDocumentActionsWebServiceImpl implements WorkflowDocumentActions {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowDocumentActionsWebServiceImpl.class);
    
    private DocumentRouteHeaderValue init(RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        incomingParamCheck(routeHeaderVO, "routeHeaderVO");
        Long documentId = routeHeaderVO.getRouteHeaderId();
        LOG.debug("Initializing Document from incoming RouteHeaderVO [docId=" + documentId + "]");
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
        
//      update notes database based on notes and notesToDelete arrays in routeHeaderVO
        DTOConverter.updateNotes(routeHeaderVO, routeHeaderVO.getRouteHeaderId());
        
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        document.setRouteHeaderData(routeHeaderVO);
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
        return document;
    }
    
    private void init(Long documentId) throws WorkflowException {
        incomingParamCheck(documentId, "documentId");
        LOG.debug("Initializing Document from incoming Document ID [docId=" + documentId + "]");
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
    }
    
    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            LOG.error("null " + name + " passed in.");
            throw new RuntimeException("null " + name + " passed in.");
        }
    }
    
    public RouteHeaderDTO releaseGroupAuthority(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, GroupInfo groupInfo, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Releasing group authority [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", groupInfo=" + groupInfo.getGroupName() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup(groupInfo.getGroupId());
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().releaseGroupAuthority(user, routeHeader, group, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO takeGroupAuthority(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, GroupInfo groupInfo, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Taking workgroup authority [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", groupInfo=" + groupInfo + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        KimGroup group = KIMServiceLocator.getIdentityManagementService().getGroup(groupInfo.getGroupId());
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().takeGroupAuthority(user, routeHeader, group, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO acknowledgeDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Acknowledge [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().acknowledgeDocument(user, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO approveDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().approveDocument(user, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO appSpecificRouteDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String actionRequested, String nodeName, String annotation, ResponsiblePartyDTO responsiblePartyVO, String responsibilityDesc, boolean ignorePrevActions) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        incomingParamCheck(actionRequested, "actionRequested");
        //incomingParamCheck(routeMethodName, "routeMethodName");
        incomingParamCheck(responsiblePartyVO, "responsiblePartyVO");
        LOG.debug("AdHoc Route [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", actionRequest=" + actionRequested + ", nodeName=" + nodeName + ", responsibleParty=" + responsiblePartyVO + ", ignorePrevious=" + ignorePrevActions + ", annotation="+annotation+"]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        
        Recipient recipient = DTOConverter.convertResponsiblePartyVOtoRecipient(responsiblePartyVO);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().appSpecificRouteDocument(user, routeHeader, actionRequested, nodeName, annotation, recipient, responsibilityDesc, new Boolean(ignorePrevActions));
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO blanketApproval(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation, Integer routeLevel) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Blanket Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", routeLevel=" + routeLevel + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().blanketApproval(user, routeHeader, annotation, routeLevel);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }

    public RouteHeaderDTO blanketApprovalToNodes(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation, String[] nodeNames) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Blanket Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", nodeNames=" + ArrayUtils.toString(nodeNames) + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        Set nodeNameSet = Utilities.asSet(nodeNames);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().blanketApproval(user, routeHeader, annotation, nodeNameSet);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }

    
    public RouteHeaderDTO cancelDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Cancel [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().cancelDocument(user, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO clearFYIDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Clear FYI [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().clearFYIDocument(user, routeHeader);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO completeDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Complete [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().completeDocument(user, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO createDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        incomingParamCheck(userId, "userId");
        incomingParamCheck(routeHeaderVO, "routeHeaderVO");
        LOG.debug("Create Document [userId=" + userId + ", docTypeName=" + routeHeaderVO.getDocTypeName() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);        
        DocumentRouteHeaderValue routeHeader = DTOConverter.convertRouteHeaderVO(routeHeaderVO);
        routeHeader.setInitiatorWorkflowId(user.getWorkflowUserId().getWorkflowId());
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().createDocument(user, routeHeader);
        
//      update notes database based on notes and notesToDelete arrays in routeHeaderVO
        DTOConverter.updateNotes(routeHeaderVO, routeHeader.getRouteHeaderId());
        
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO disapproveDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Disapprove [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().disapproveDocument(user, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO returnDocumentToPreviousRouteLevel(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, Integer destRouteLevel, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        incomingParamCheck(destRouteLevel, "destRouteLevel");
        LOG.debug("Return to Previous [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", destRouteLevel=" + destRouteLevel + "]");        
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().returnDocumentToPreviousRouteLevel(user, routeHeader, destRouteLevel, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO returnDocumentToPreviousNode(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, ReturnPointDTO returnPoint, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        incomingParamCheck(returnPoint, "returnPoint");
        LOG.debug("Return to Previous [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", destNodeName=" + returnPoint.getNodeName() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().returnDocumentToPreviousNode(user, routeHeader, returnPoint.getNodeName(), annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO routeDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Route Document [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().routeDocument(user, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO saveRoutingData(UserIdDTO userId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Saving Routing Data [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().saveRoutingData(user, routeHeader);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO saveDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Save Document [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().saveDocument(user, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public void deleteDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Delete [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        KEWServiceLocator.getWorkflowDocumentService().deleteDocument(user, routeHeader);
    }
    
    public void logDocumentAction(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Log [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        KEWServiceLocator.getWorkflowDocumentService().logDocumentAction(user, routeHeader, annotation);
    }
    
    public RouteHeaderDTO moveDocument(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, MovePointDTO movePoint, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Move [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", startNode=" + movePoint.getStartNodeName() + "steps=" + movePoint.getStepsToMove() + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().moveDocument(user, routeHeader, DTOConverter.convertMovePointVO(movePoint), annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }

    public RouteHeaderDTO revokeAdHocRequests(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, AdHocRevokeDTO revoke, String annotation) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("Revoke AdHoc [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().revokeAdHocRequests(user, routeHeader, DTOConverter.convertAdHocRevokeVO(revoke), annotation);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO superUserApprove(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation, boolean runPostProcessor) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("SU Approve [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserApprove(user, routeHeader, annotation, runPostProcessor);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO superUserApprove(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
    	return superUserApprove(userId, routeHeaderVO, annotation, true);
    }
    
    public RouteHeaderDTO superUserActionRequestApprove(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, Long actionRequestId, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
	incomingParamCheck(userId, "userId");
	LOG.debug("SU Cancel [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
	WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
	routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(user, routeHeader, actionRequestId, annotation, true);
	return DTOConverter.convertRouteHeader(routeHeader, user);
    }

    public RouteHeaderDTO superUserDisapprove(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation, boolean runPostProcessor) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("SU Disapprove [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserDisapproveAction(user, routeHeader, annotation, runPostProcessor);
        return DTOConverter.convertRouteHeader(routeHeader, user);    	
    }
    
    public RouteHeaderDTO superUserDisapprove(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
    	return superUserDisapprove(userId, routeHeaderVO, annotation, true);
    }
    
    public RouteHeaderDTO superUserCancel(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation, boolean runPostProcessor) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(userId, "userId");
        LOG.debug("SU Cancel [userId=" + userId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserCancelAction(user, routeHeader, annotation, runPostProcessor);
        return DTOConverter.convertRouteHeader(routeHeader, user);
    }
    
    public RouteHeaderDTO superUserCancel(UserIdDTO userId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
    	return superUserCancel(userId, routeHeaderVO, annotation, true);
    }

	public DocumentContentDTO saveDocumentContent(DocumentContentDTO documentContent) throws WorkflowException {
		incomingParamCheck(documentContent, "documentContent");
		Long documentId = documentContent.getRouteHeaderId();
		incomingParamCheck(documentId, "documentContent document ID");
		KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);        
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		LOG.debug("Saving Document Content [documentId=" + documentId + "]");
		String updatedDocumentContent = DTOConverter.buildUpdatedDocumentContent(documentContent);
    	// updatedDocumentContent will be null if the content has not changed, only update if its changed
    	if (updatedDocumentContent != null) {
    		document.setDocContent(updatedDocumentContent);
    		KEWServiceLocator.getRouteHeaderService().saveRouteHeader(document);
    	}
    	return DTOConverter.convertDocumentContent(document.getDocContent(), documentId);
	}
    	
	public void superUserNodeApproveAction(UserIdDTO userId, Long documentId, String nodeName, String annotation, boolean runPostProcessor) throws WorkflowException {
	    init(documentId);
	    incomingParamCheck(userId, "userId");
	    LOG.debug("SU Node Approve Action [userId=" + userId + ", docId=" + documentId + ", nodeName=" + nodeName + ", annotation=" + annotation + "]");
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
	    KEWServiceLocator.getWorkflowDocumentService().superUserNodeApproveAction(user, documentId, nodeName, annotation, runPostProcessor);	        
	}
	
	public void superUserNodeApproveAction(UserIdDTO userId, Long documentId, String nodeName, String annotation) throws WorkflowException {
		superUserNodeApproveAction(userId, documentId, nodeName, annotation, true);
	}

	public void superUserActionRequestApproveAction(UserIdDTO userId, Long documentId, Long actionRequestId, String annotation, boolean runPostProcessor) throws WorkflowException {
	    init(documentId);
	    incomingParamCheck(userId, "userId");
	    LOG.debug("SU Action Request Approve [userId=" + userId + ", docId=" + documentId + ", actionRequestId=" + actionRequestId + ", annotation=" + annotation + "]");
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
	    DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
	    document = KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(user, document, actionRequestId, annotation, runPostProcessor);
	}
	
	public void superUserActionRequestApproveAction(UserIdDTO userId, Long documentId, Long actionRequestId, String annotation) throws WorkflowException {
		superUserActionRequestApproveAction(userId, documentId, actionRequestId, annotation, true);
	}

	public void superUserReturnToPreviousNode(UserIdDTO userId, Long documentId, String destinationNodeName, String annotation, boolean runPostProcessor) throws WorkflowException {
	    init(documentId);
	    incomingParamCheck(userId, "userId");
	    LOG.debug("SU Cancel [userId=" + userId + ", docId=" + documentId + ", destinationNodeName=" + destinationNodeName + ", annotation=" + annotation + "]");
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
	    KEWServiceLocator.getWorkflowDocumentService().superUserReturnDocumentToPreviousNode(user, documentId, destinationNodeName, annotation, runPostProcessor);
	}
	
	public void superUserReturnToPreviousNode(UserIdDTO userId, Long documentId, String destinationNodeName, String annotation) throws WorkflowException {
		superUserReturnToPreviousNode(userId, documentId, destinationNodeName, annotation, true);
	}
    
}