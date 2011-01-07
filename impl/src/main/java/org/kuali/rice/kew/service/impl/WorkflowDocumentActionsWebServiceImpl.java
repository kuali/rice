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
package org.kuali.rice.kew.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.kuali.rice.kew.docsearch.service.SearchableAttributeProcessingService;
import org.kuali.rice.kew.dto.*;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocumentActions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        
        /* 
         * Branch data is not persisted when we call saveRouteHeader so we must Explicitly
         * save the branch.  Noticed issue in: KULRICE-4074 when the future action request info,
         * which is stored in the branch, was not being persisted.
         * 
         * The call to setRouteHeaderData will ensure that the variable data is in the branch, but we have
         * to persist the route header before we can save the branch info.
         * 
         * Placing here to minimize system impact.  We should investigate placing this logic into 
         * saveRouteHeader... but at that point we should just turn auto-update = true on the branch relationship
         * 
         */
        this.saveRouteNodeInstances(document);
        
        return document;
    }

    /**
     * 
     * This method explicitly saves the branch data if it exists in the routeHeaderValue
     * 
     * @param routeHeader
     */
    private void saveRouteNodeInstances(DocumentRouteHeaderValue routeHeader){
    
    	List<RouteNodeInstance> routeNodes = routeHeader.getInitialRouteNodeInstances();               
        if(routeNodes != null && !routeNodes.isEmpty()){        	        	
        	for(RouteNodeInstance rni: routeNodes){
        		KEWServiceLocator.getRouteNodeService().save(rni);        		
        	}
        }
    	
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

    public RouteHeaderDTO releaseGroupAuthority(String principalId, RouteHeaderDTO routeHeaderVO, String groupId, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Releasing group authority [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", groupId=" + groupId + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().releaseGroupAuthority(principalId, routeHeader, groupId, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO takeGroupAuthority(String principalId, RouteHeaderDTO routeHeaderVO, String groupId, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Taking workgroup authority [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", groupInfo=" + groupId + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().takeGroupAuthority(principalId, routeHeader, groupId, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO acknowledgeDocument(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Acknowledge [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().acknowledgeDocument(principalId, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO approveDocument(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Approve [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().approveDocument(principalId, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO adHocRouteDocumentToPrincipal(String principalId, RouteHeaderDTO routeHeaderVO, String actionRequested, String nodeName, String annotation, String recipientPrincipalId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(actionRequested, "actionRequested");
        //incomingParamCheck(routeMethodName, "routeMethodName");
        incomingParamCheck(recipientPrincipalId, "recipientPrincipalId");
        LOG.debug("AdHoc Route [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", actionRequest=" + actionRequested + ", nodeName=" + nodeName + ", recipientPrincipalId=" + recipientPrincipalId + ", forceAction=" + forceAction + ", annotation="+annotation + ", requestLabel="+requestLabel+"]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().adHocRouteDocumentToPrincipal(principalId, routeHeader, actionRequested, nodeName, annotation, recipientPrincipalId, responsibilityDesc, new Boolean(forceAction), requestLabel);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO adHocRouteDocumentToGroup(String principalId, RouteHeaderDTO routeHeaderVO, String actionRequested, String nodeName, String annotation, String recipientGroupId, String responsibilityDesc, boolean forceAction, String requestLabel) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(actionRequested, "actionRequested");
        //incomingParamCheck(routeMethodName, "routeMethodName");
        incomingParamCheck(recipientGroupId, "recipientGroupId");
        LOG.debug("AdHoc Route [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", actionRequest=" + actionRequested + ", nodeName=" + nodeName + ", recipientGroupId=" + recipientGroupId + ", forceAction=" + forceAction + ", annotation="+annotation+"]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().adHocRouteDocumentToGroup(principalId, routeHeader, actionRequested, nodeName, annotation, recipientGroupId, responsibilityDesc, new Boolean(forceAction), requestLabel);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO blanketApproval(String principalId, RouteHeaderDTO routeHeaderVO, String annotation, Integer routeLevel) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Blanket Approve [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", routeLevel=" + routeLevel + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().blanketApproval(principalId, routeHeader, annotation, routeLevel);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO blanketApprovalToNodes(String principalId, RouteHeaderDTO routeHeaderVO, String annotation, String[] nodeNames) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Blanket Approve [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", nodeNames=" + ArrayUtils.toString(nodeNames) + "]");
        Set<String> nodeNameSet = new HashSet<String>();
        CollectionUtils.addAll(nodeNameSet, nodeNames);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().blanketApproval(principalId, routeHeader, annotation, nodeNameSet);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }


    public RouteHeaderDTO cancelDocument(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Cancel [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().cancelDocument(principalId, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO clearFYIDocument(String principalId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Clear FYI [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().clearFYIDocument(principalId, routeHeader);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO completeDocument(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Complete [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().completeDocument(principalId, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO createDocument(String principalId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(routeHeaderVO, "routeHeaderVO");
        LOG.debug("Create Document [principalId=" + principalId + ", docTypeName=" + routeHeaderVO.getDocTypeName() + "]");
        DocumentRouteHeaderValue routeHeader = DTOConverter.convertRouteHeaderVO(routeHeaderVO);
        routeHeader.setInitiatorWorkflowId(principalId);
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().createDocument(principalId, routeHeader);

//      update notes database based on notes and notesToDelete arrays in routeHeaderVO
        DTOConverter.updateNotes(routeHeaderVO, routeHeader.getRouteHeaderId());

        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO disapproveDocument(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Disapprove [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().disapproveDocument(principalId, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO returnDocumentToPreviousRouteLevel(String principalId, RouteHeaderDTO routeHeaderVO, Integer destRouteLevel, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(destRouteLevel, "destRouteLevel");
        LOG.debug("Return to Previous [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", destRouteLevel=" + destRouteLevel + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().returnDocumentToPreviousRouteLevel(principalId, routeHeader, destRouteLevel, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO returnDocumentToPreviousNode(String principalId, RouteHeaderDTO routeHeaderVO, ReturnPointDTO returnPoint, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(returnPoint, "returnPoint");
        LOG.debug("Return to Previous [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", destNodeName=" + returnPoint.getNodeName() + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().returnDocumentToPreviousNode(principalId, routeHeader, returnPoint.getNodeName(), annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO routeDocument(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Route Document [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().routeDocument(principalId, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO saveRoutingData(String principalId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Saving Routing Data [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().saveRoutingData(principalId, routeHeader);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO saveDocument(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Save Document [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().saveDocument(principalId, routeHeader, annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public void deleteDocument(String principalId, RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Delete [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + "]");
        KEWServiceLocator.getWorkflowDocumentService().deleteDocument(principalId, routeHeader);
    }

    public void logDocumentAction(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Log [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        KEWServiceLocator.getWorkflowDocumentService().logDocumentAction(principalId, routeHeader, annotation);
    }

    public RouteHeaderDTO moveDocument(String principalId, RouteHeaderDTO routeHeaderVO, MovePointDTO movePoint, String annotation) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Move [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + ", startNode=" + movePoint.getStartNodeName() + "steps=" + movePoint.getStepsToMove() + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().moveDocument(principalId, routeHeader, DTOConverter.convertMovePointVO(movePoint), annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO revokeAdHocRequests(String principalId, RouteHeaderDTO routeHeaderVO, AdHocRevokeDTO revoke, String annotation) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("Revoke AdHoc [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().revokeAdHocRequests(principalId, routeHeader, DTOConverter.convertAdHocRevokeVO(revoke), annotation);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO superUserApprove(String principalId, RouteHeaderDTO routeHeaderVO, String annotation, boolean runPostProcessor) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("SU Approve [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserApprove(principalId, routeHeader, annotation, runPostProcessor);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO superUserApprove(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
    	return superUserApprove(principalId, routeHeaderVO, annotation, true);
    }

    public RouteHeaderDTO superUserActionRequestApprove(String principalId, RouteHeaderDTO routeHeaderVO, Long actionRequestId, String annotation) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
    	incomingParamCheck(principalId, "principalId");
    	LOG.debug("SU Cancel [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
    	routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(principalId, routeHeader, actionRequestId, annotation, true);
		return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO superUserDisapprove(String principalId, RouteHeaderDTO routeHeaderVO, String annotation, boolean runPostProcessor) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("SU Disapprove [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserDisapproveAction(principalId, routeHeader, annotation, runPostProcessor);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO superUserDisapprove(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
    	return superUserDisapprove(principalId, routeHeaderVO, annotation, true);
    }

    public RouteHeaderDTO superUserCancel(String principalId, RouteHeaderDTO routeHeaderVO, String annotation, boolean runPostProcessor) throws WorkflowException {
    	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
        incomingParamCheck(principalId, "principalId");
        LOG.debug("SU Cancel [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
        routeHeader = KEWServiceLocator.getWorkflowDocumentService().superUserCancelAction(principalId, routeHeader, annotation, runPostProcessor);
        return DTOConverter.convertRouteHeader(routeHeader, principalId);
    }

    public RouteHeaderDTO superUserCancel(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
    	return superUserCancel(principalId, routeHeaderVO, annotation, true);
    }
    
    public RouteHeaderDTO placeInExceptionRouting(String principalId, RouteHeaderDTO routeHeaderVO, String annotation) throws WorkflowException {
 	 	DocumentRouteHeaderValue routeHeader = init(routeHeaderVO);
 	 	incomingParamCheck(principalId, "principalId");
 	 	LOG.debug("placeInExceptionRouting [principalId=" + principalId + ", docId=" + routeHeaderVO.getRouteHeaderId() + ", annotation=" + annotation + "]");
 	 	routeHeader = KEWServiceLocator.getWorkflowDocumentService().placeInExceptionRouting(principalId, routeHeader, annotation);
 	 	return DTOConverter.convertRouteHeader(routeHeader, principalId);
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

	public void superUserNodeApproveAction(String principalId, Long documentId, String nodeName, String annotation, boolean runPostProcessor) throws WorkflowException {
	    init(documentId);
	    incomingParamCheck(principalId, "principalId");
	    LOG.debug("SU Node Approve Action [principalId=" + principalId + ", docId=" + documentId + ", nodeName=" + nodeName + ", annotation=" + annotation + "]");
	    KEWServiceLocator.getWorkflowDocumentService().superUserNodeApproveAction(principalId, documentId, nodeName, annotation, runPostProcessor);
	}

	public void superUserNodeApproveAction(String principalId, Long documentId, String nodeName, String annotation) throws WorkflowException {
		superUserNodeApproveAction(principalId, documentId, nodeName, annotation, true);
	}

	public void superUserActionRequestApproveAction(String principalId, Long documentId, Long actionRequestId, String annotation, boolean runPostProcessor) throws WorkflowException {
	    init(documentId);
	    incomingParamCheck(principalId, "principalId");
	    LOG.debug("SU Action Request Approve [principalId=" + principalId + ", docId=" + documentId + ", actionRequestId=" + actionRequestId + ", annotation=" + annotation + "]");
	    DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
	    document = KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(principalId, document, actionRequestId, annotation, runPostProcessor);
	}

	public void superUserActionRequestApproveAction(String principalId, Long documentId, Long actionRequestId, String annotation) throws WorkflowException {
		superUserActionRequestApproveAction(principalId, documentId, actionRequestId, annotation, true);
	}

	public void superUserReturnToPreviousNode(String principalId, Long documentId, String destinationNodeName, String annotation, boolean runPostProcessor) throws WorkflowException {
	    init(documentId);
	    incomingParamCheck(principalId, "principalId");
	    LOG.debug("SU Cancel [principalId=" + principalId + ", docId=" + documentId + ", destinationNodeName=" + destinationNodeName + ", annotation=" + annotation + "]");
	    KEWServiceLocator.getWorkflowDocumentService().superUserReturnDocumentToPreviousNode(principalId, documentId, destinationNodeName, annotation, runPostProcessor);
	}

	public void superUserReturnToPreviousNode(String principalId, Long documentId, String destinationNodeName, String annotation) throws WorkflowException {
		superUserReturnToPreviousNode(principalId, documentId, destinationNodeName, annotation, true);
	}

	/**
	 * This mehtod indexes a document based on the documentId
	 * 
	 * @see org.kuali.rice.kew.service.WorkflowDocumentActions#indexDocument(java.lang.Long)
	 */	
	public void indexDocument(Long documentId) {
		SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId));
		searchableAttService.indexDocument(documentId);		
	}

}
