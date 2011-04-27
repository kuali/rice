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
package org.kuali.rice.kew.routeheader.service.impl;

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actions.*;
import org.kuali.rice.kew.actions.asyncservices.ActionInvocation;
import org.kuali.rice.kew.actions.asyncservices.ActionInvocationService;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.docsearch.service.SearchableAttributeProcessingService;
import org.kuali.rice.kew.engine.CompatUtils;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.exception.DocumentTypeNotFoundException;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.postprocessor.PostProcessor;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.WorkflowDocumentService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.entity.KimPrincipal;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * this class mainly interacts with ActionEvent 'action' classes and non-vo objects.
 *
 */

public class WorkflowDocumentServiceImpl implements WorkflowDocumentService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WorkflowDocumentServiceImpl.class);

	private void init(DocumentRouteHeaderValue routeHeader) {
		KEWServiceLocator.getRouteHeaderService().lockRouteHeader(routeHeader.getRouteHeaderId(), true);
		KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
	}

    private DocumentRouteHeaderValue finish(DocumentRouteHeaderValue routeHeader) {
    	// reload the document from the database to get a "fresh and clean" copy if we aren't in the context of a
    	// document being routed
    	if (RouteContext.getCurrentRouteContext().getDocument() == null) {
    		return KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeader.getRouteHeaderId(), true);
    	} else {
    		// we could enter this case if someone calls a method on WorkflowDocument (such as app specific route)
    		// from their post processor, in that case, if we cleared the database case as above we would
    		// end up getting an optimistic lock exception when the engine attempts to save the document after
    		// the post processor call
    		return routeHeader;
    	}
    }

	public DocumentRouteHeaderValue acknowledgeDocument(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		AcknowledgeAction action = new AcknowledgeAction(routeHeader, principal, annotation);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue releaseGroupAuthority(String principalId, DocumentRouteHeaderValue routeHeader, String groupId, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		ReleaseWorkgroupAuthority action = new ReleaseWorkgroupAuthority(routeHeader, principal, annotation, groupId);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue takeGroupAuthority(String principalId, DocumentRouteHeaderValue routeHeader, String groupId, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		TakeWorkgroupAuthority action = new TakeWorkgroupAuthority(routeHeader, principal, annotation, groupId);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue approveDocument(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		ApproveAction action = new ApproveAction(routeHeader, principal, annotation);
		action.performAction();
		return finish(routeHeader);
	}
	
	public DocumentRouteHeaderValue placeInExceptionRouting(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
 	 	try {
 	 		KEWServiceLocator.getExceptionRoutingService().placeInExceptionRouting(annotation, null, routeHeader.getRouteHeaderId());
 	 	} catch (Exception e) {
 	 		throw new RiceRuntimeException("Failed to place the document into exception routing!", e);
 	 	}
 	 	return finish(routeHeader);
 	 }

	public DocumentRouteHeaderValue adHocRouteDocumentToPrincipal(String principalId, DocumentRouteHeaderValue document, String actionRequested, String nodeName, String annotation, String targetPrincipalId,
			String responsibilityDesc, Boolean forceAction, String requestLabel) throws WorkflowException {
		KimPrincipal principal = loadPrincipal(principalId);
		Recipient recipient = KEWServiceLocator.getIdentityHelperService().getPrincipalRecipient(targetPrincipalId);
		AdHocAction action = new AdHocAction(document, principal, annotation, actionRequested, nodeName, recipient, responsibilityDesc, forceAction, requestLabel);
		action.performAction();
		return finish(document);
	}

	public DocumentRouteHeaderValue adHocRouteDocumentToGroup(String principalId, DocumentRouteHeaderValue document, String actionRequested, String nodeName, String annotation, String groupId,
			String responsibilityDesc, Boolean forceAction, String requestLabel) throws WorkflowException {
		KimPrincipal principal = loadPrincipal(principalId);
		final Recipient recipient = new KimGroupRecipient(KIMServiceLocator.getIdentityManagementService().getGroup(groupId));
		AdHocAction action = new AdHocAction(document, principal, annotation, actionRequested, nodeName, recipient, responsibilityDesc, forceAction, requestLabel);
		action.performAction();
		return finish(document);
	}

	public DocumentRouteHeaderValue blanketApproval(String principalId, DocumentRouteHeaderValue routeHeader, String annotation, Integer routeLevel) throws InvalidActionTakenException {
		RouteNode node = (routeLevel == null ? null : CompatUtils.getNodeForLevel(routeHeader.getDocumentType(), routeLevel));
		if (node == null && routeLevel != null) {
			throw new InvalidActionTakenException("Could not locate node for route level " + routeLevel);
		}
		Set<String> nodeNames = new HashSet<String>();
		if (node != null) {
			nodeNames = Collections.singleton(node.getRouteNodeName());
		}
		KimPrincipal principal = loadPrincipal(principalId);
		ActionTakenEvent action = new BlanketApproveAction(routeHeader, principal, annotation, nodeNames);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue blanketApproval(String principalId, DocumentRouteHeaderValue routeHeader, String annotation, Set nodeNames) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		BlanketApproveAction action = new BlanketApproveAction(routeHeader, principal, annotation, nodeNames);
		action.recordAction();

		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue cancelDocument(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
		// init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		CancelAction action = new CancelAction(routeHeader, principal, annotation);
		action.recordAction();
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}
	
	/**
	 * Does a search index after a non-post processing action completes
	 * @param routeHeader the route header of the document just acted upon
	 */
	protected void indexForSearchAfterActionIfNecessary(DocumentRouteHeaderValue routeHeader) {
		RouteContext routeContext = RouteContext.getCurrentRouteContext();
		if (routeHeader.getDocumentType().hasSearchableAttributes() && routeContext.isSearchIndexingRequestedForContext()) {
			SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(routeHeader);
			searchableAttService.indexDocument(routeHeader.getRouteHeaderId()); 
		}
	}

	public DocumentRouteHeaderValue clearFYIDocument(String principalId, DocumentRouteHeaderValue routeHeader) throws InvalidActionTakenException {
		// init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		ClearFYIAction action = new ClearFYIAction(routeHeader, principal, "");
		action.recordAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue completeDocument(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		CompleteAction action = new CompleteAction(routeHeader, principal, annotation);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue createDocument(String principalId, DocumentRouteHeaderValue routeHeader) throws DocumentTypeNotFoundException, WorkflowException {

		LOG.debug("Rmi createDocument() with "); // add some sort of a
													// routeheader logger

		if (routeHeader.getRouteHeaderId() != null) { // this is a debateable
														// check - means the
														// client is off
			throw new InvalidActionTakenException("Document already has a Document id");
		}
		KimPrincipal principal = loadPrincipal(principalId);
		boolean canInitiate = KEWServiceLocator.getDocumentTypePermissionService().canInitiate(principalId, routeHeader.getDocumentType());

		if (!canInitiate) {
			throw new InvalidActionTakenException("Principal with name '" + principal.getPrincipalName() + "' is not authorized to initiate documents of type '" + routeHeader.getDocumentType().getName());
		}

        if (!routeHeader.getDocumentType().isDocTypeActive()) {
            // don't allow creation if document type is inactive
            throw new InvalidActionTakenException("Document type '" + routeHeader.getDocumentType().getName() + "' is inactive");
        }

		routeHeader.setInitiatorWorkflowId(principalId);
		if (routeHeader.getDocRouteStatus() == null) {
			routeHeader.setDocRouteStatus(KEWConstants.ROUTE_HEADER_INITIATED_CD);
		}
		if (routeHeader.getDocRouteLevel() == null) {
			routeHeader.setDocRouteLevel(Integer.valueOf(KEWConstants.ADHOC_ROUTE_LEVEL));
		}
		if (routeHeader.getCreateDate() == null) {
			routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
		}
		if (routeHeader.getDocVersion() == null) {
			routeHeader.setDocVersion(Integer.valueOf(KEWConstants.CURRENT_DOCUMENT_VERSION));
		}
		if (routeHeader.getDocContent() == null) {
			routeHeader.setDocContent(KEWConstants.DEFAULT_DOCUMENT_CONTENT);
		}
		routeHeader.setStatusModDate(new Timestamp(new Date().getTime()));
		KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
		KEWServiceLocator.getWorkflowEngine().initializeDocument(routeHeader);
		KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
		return routeHeader;
	}

	public DocumentRouteHeaderValue disapproveDocument(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		DisapproveAction action = new DisapproveAction(routeHeader, principal, annotation);
		action.recordAction();
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue returnDocumentToPreviousRouteLevel(String principalId, DocumentRouteHeaderValue routeHeader, Integer destRouteLevel, String annotation)
	        throws InvalidActionTakenException {
		DocumentRouteHeaderValue result = null;
		
		if (destRouteLevel != null) {
			RouteNode node = CompatUtils.getNodeForLevel(routeHeader.getDocumentType(), destRouteLevel);
			if (node == null) {
				throw new InvalidActionTakenException("Could not locate node for route level " + destRouteLevel);
			}

			KimPrincipal principal = loadPrincipal(principalId);
			ReturnToPreviousNodeAction action = new ReturnToPreviousNodeAction(routeHeader, principal, annotation, node.getRouteNodeName(), true);
			action.performAction();
			result = finish(routeHeader);
		}
		return result;
	}

	public DocumentRouteHeaderValue returnDocumentToPreviousNode(String principalId, DocumentRouteHeaderValue routeHeader, String destinationNodeName, String annotation)
			throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		ReturnToPreviousNodeAction action = new ReturnToPreviousNodeAction(routeHeader, principal, annotation, destinationNodeName, true);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue routeDocument(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws WorkflowException,
			InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		RouteDocumentAction actionEvent = new RouteDocumentAction(routeHeader, principal, annotation);
		actionEvent.performAction();
        LOG.info("routeDocument: " + routeHeader);
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue saveRoutingData(String principalId, DocumentRouteHeaderValue routeHeader) {
		KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
		
		// save routing data should invoke the post processor doActionTaken for SAVE
 	 	ActionTakenValue val = new ActionTakenValue();
 	 	val.setActionTaken(KEWConstants.ACTION_TAKEN_SAVED_CD);
 	 	val.setRouteHeaderId(routeHeader.getRouteHeaderId());
 	 	PostProcessor postProcessor = routeHeader.getDocumentType().getPostProcessor();
 	 	try {
 	 		postProcessor.doActionTaken(new org.kuali.rice.kew.postprocessor.ActionTakenEvent(routeHeader.getRouteHeaderId(), routeHeader.getAppDocId(), val));
 	 	} catch (Exception e) {
 	 		if (e instanceof RuntimeException) {
 	 			throw (RuntimeException)e;
 	 		}
 	 		throw new WorkflowRuntimeException(e);
 	 	}

 	 	RouteContext routeContext = RouteContext.getCurrentRouteContext();
 	 	if (routeHeader.getDocumentType().hasSearchableAttributes() && !routeContext.isSearchIndexingRequestedForContext()) {
 	 		routeContext.requestSearchIndexingForContext();
 	 		
			SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(routeHeader);
			searchableAttService.indexDocument(routeHeader.getRouteHeaderId());
		}
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue saveDocument(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		SaveActionEvent action = new SaveActionEvent(routeHeader, principal, annotation);
		action.performAction();
		return finish(routeHeader);
	}

	public void deleteDocument(String principalId, DocumentRouteHeaderValue routeHeader) throws WorkflowException {
		if (routeHeader.getRouteHeaderId() == null) {
			LOG.debug("Null Document id passed.");
			throw new WorkflowException("Document id must not be null.");
		}
		KEWServiceLocator.getRouteHeaderService().deleteRouteHeader(routeHeader);
	}

	public void logDocumentAction(String principalId, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		LogDocumentActionAction action = new LogDocumentActionAction(routeHeader, principal, annotation);
		action.recordAction();
	}

	public DocumentRouteHeaderValue moveDocument(String principalId, DocumentRouteHeaderValue routeHeader, MovePoint movePoint, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		MoveDocumentAction action = new MoveDocumentAction(routeHeader, principal, annotation, movePoint);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserActionRequestApproveAction(String principalId, DocumentRouteHeaderValue routeHeader, Long actionRequestId, String annotation, boolean runPostProcessor)
			throws InvalidActionTakenException {
		init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		SuperUserActionRequestApproveEvent suActionRequestApprove = new SuperUserActionRequestApproveEvent(routeHeader, principal, actionRequestId, annotation, runPostProcessor);
		suActionRequestApprove.recordAction();
		// suActionRequestApprove.queueDocument();
		RouteContext.getCurrentRouteContext().requestSearchIndexingForContext(); // make sure indexing is requested
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}

    /**
     * TODO As with superUserReturnDocumentToPreviousNode, we allow for the passing in of a document ID here to allow for
     * the document load inside the current running transaction.  Otherwise we get an optimistic lock exception
     * when attempting to save the branch after the transition to the 'A' status.
     */
    public DocumentRouteHeaderValue superUserActionRequestApproveAction(String principalId, Long documentId, Long actionRequestId, String annotation, boolean runPostProcessor)
        throws InvalidActionTakenException {
        return superUserActionRequestApproveAction(principalId, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), actionRequestId, annotation, runPostProcessor);
    }

	public DocumentRouteHeaderValue superUserApprove(String principalId, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException {
		init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		new SuperUserApproveEvent(routeHeader, principal, annotation, runPostProcessor).recordAction();
		RouteContext.getCurrentRouteContext().requestSearchIndexingForContext(); // make sure indexing is requested
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserCancelAction(String principalId, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException {
		init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		new SuperUserCancelEvent(routeHeader, principal, annotation, runPostProcessor).recordAction();
		RouteContext.getCurrentRouteContext().requestSearchIndexingForContext(); // make sure indexing is requested
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserDisapproveAction(String principalId, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException {
		init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		new SuperUserDisapproveEvent(routeHeader, principal, annotation, runPostProcessor).recordAction();
		RouteContext.getCurrentRouteContext().requestSearchIndexingForContext(); // make sure indexing is requested
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserNodeApproveAction(String principalId, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation, boolean runPostProcessor) throws InvalidActionTakenException {
		init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		new SuperUserNodeApproveEvent(routeHeader, principal, annotation, runPostProcessor, nodeName).recordAction();
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}

	/**
	 * TODO As with superUserReturnDocumentToPreviousNode, we allow for the passing in of a document ID here to allow for
	 * the document load inside the current running transaction.  Otherwise we get an optimistic lock exception
	 * when attempting to save the branch after the transition to the 'A' status.
	 */
	public DocumentRouteHeaderValue superUserNodeApproveAction(String principalId, Long documentId, String nodeName, String annotation, boolean runPostProcessor) throws InvalidActionTakenException {
		return superUserNodeApproveAction(principalId, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), nodeName, annotation, runPostProcessor);
	}

	/**
	 * TODO remove this implementation in favor of having the SuperUserAction call through the WorkflowDocument object.  This
	 * method is here to resolve KULWF-727 where we were getting an optimistic lock exception from the super user screen on
	 * return to previous node.  This allows us to load the DocumentRouteHeaderValue inside of the transaction interceptor
	 * so that we can stay within the same PersistenceBroker cache.
	 */
	public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(String principalId, Long documentId, String nodeName, String annotation, boolean runPostProcessor)
		throws InvalidActionTakenException {
		return superUserReturnDocumentToPreviousNode(principalId, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), nodeName, annotation, runPostProcessor);
	}

	public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(String principalId, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation, boolean runPostProcessor)
			throws InvalidActionTakenException {
		init(routeHeader);
		KimPrincipal principal = loadPrincipal(principalId);
		SuperUserReturnToPreviousNodeAction action = new SuperUserReturnToPreviousNodeAction(routeHeader, principal, annotation, runPostProcessor, nodeName);
		action.recordAction();
		RouteContext.getCurrentRouteContext().requestSearchIndexingForContext(); // make sure indexing is requested
		indexForSearchAfterActionIfNecessary(routeHeader);
		return finish(routeHeader);
	}

	public void takeMassActions(String principalId, List actionInvocations) {
		KimPrincipal principal = loadPrincipal(principalId);
		for (Iterator iterator = actionInvocations.iterator(); iterator.hasNext();) {
			ActionInvocation invocation = (ActionInvocation) iterator.next();
			ActionItem actionItem = KEWServiceLocator.getActionListService().findByActionItemId(invocation.getActionItemId());
			if (actionItem == null) {
				LOG.warn("Could not locate action item for the given action item id [" + invocation.getActionItemId() + "], not taking mass action on it.");
				continue;
			}
			KEWServiceLocator.getActionListService().deleteActionItem(actionItem, true);
			ActionInvocationService actionInvocService = MessageServiceNames.getActionInvocationProcessorService(
					KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionItem.getRouteHeaderId()));
			actionInvocService.invokeAction(principalId, actionItem.getRouteHeaderId(), invocation);
//			ActionInvocationProcessor.queueActionInvocation(user, actionItem.getRouteHeaderId(), invocation);
		}
	}

	public DocumentRouteHeaderValue revokeAdHocRequests(String principalId, DocumentRouteHeaderValue document, AdHocRevoke revoke, String annotation) throws InvalidActionTakenException {
		KimPrincipal principal = loadPrincipal(principalId);
		RevokeAdHocAction action = new RevokeAdHocAction(document, principal, revoke, annotation);
		action.performAction();
		return finish(document);
	}

	protected KimPrincipal loadPrincipal(String principalId) {
		return KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId);
	}

}
