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
package org.kuali.rice.kew.routeheader.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actions.AcknowledgeAction;
import org.kuali.rice.kew.actions.ActionTakenEvent;
import org.kuali.rice.kew.actions.AdHocAction;
import org.kuali.rice.kew.actions.AdHocRevoke;
import org.kuali.rice.kew.actions.ApproveAction;
import org.kuali.rice.kew.actions.BlanketApproveAction;
import org.kuali.rice.kew.actions.CancelAction;
import org.kuali.rice.kew.actions.ClearFYIAction;
import org.kuali.rice.kew.actions.CompleteAction;
import org.kuali.rice.kew.actions.DisapproveAction;
import org.kuali.rice.kew.actions.LogDocumentActionAction;
import org.kuali.rice.kew.actions.MoveDocumentAction;
import org.kuali.rice.kew.actions.MovePoint;
import org.kuali.rice.kew.actions.ReleaseWorkgroupAuthority;
import org.kuali.rice.kew.actions.ReturnToPreviousNodeAction;
import org.kuali.rice.kew.actions.RevokeAdHocAction;
import org.kuali.rice.kew.actions.RouteDocumentAction;
import org.kuali.rice.kew.actions.SaveActionEvent;
import org.kuali.rice.kew.actions.SuperUserActionRequestApproveEvent;
import org.kuali.rice.kew.actions.SuperUserApproveEvent;
import org.kuali.rice.kew.actions.SuperUserCancelEvent;
import org.kuali.rice.kew.actions.SuperUserDisapproveEvent;
import org.kuali.rice.kew.actions.SuperUserNodeApproveEvent;
import org.kuali.rice.kew.actions.SuperUserReturnToPreviousNodeAction;
import org.kuali.rice.kew.actions.TakeWorkgroupAuthority;
import org.kuali.rice.kew.actions.asyncservices.ActionInvocation;
import org.kuali.rice.kew.actions.asyncservices.ActionInvocationService;
import org.kuali.rice.kew.docsearch.service.SearchableAttributeProcessingService;
import org.kuali.rice.kew.engine.CompatUtils;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.exception.DocumentTypeNotFoundException;
import org.kuali.rice.kew.exception.InvalidActionTakenException;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.WorkflowDocumentService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.group.KimGroup;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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

	public DocumentRouteHeaderValue acknowledgeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		AcknowledgeAction action = new AcknowledgeAction(routeHeader, user, annotation);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue releaseGroupAuthority(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String groupId, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		ReleaseWorkgroupAuthority action = new ReleaseWorkgroupAuthority(routeHeader, user, annotation, groupId);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue takeGroupAuthority(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String groupId, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		TakeWorkgroupAuthority action = new TakeWorkgroupAuthority(routeHeader, user, annotation, groupId);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue approveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		ApproveAction action = new ApproveAction(routeHeader, user, annotation);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue adHocRouteDocumentToPrincipal(WorkflowUser user, DocumentRouteHeaderValue document, String actionRequested, String nodeName, String annotation, String principalId,
			String responsibilityDesc, Boolean ignorePrevious) throws WorkflowException {
		Recipient recipient = KEWServiceLocator.getIdentityHelperService().getPrincipalRecipient(principalId);
		AdHocAction action = new AdHocAction(document, user, annotation, actionRequested, nodeName, recipient, responsibilityDesc, ignorePrevious);
		action.performAction();
		return finish(document);
	}

	public DocumentRouteHeaderValue adHocRouteDocumentToGroup(WorkflowUser user, DocumentRouteHeaderValue document, String actionRequested, String nodeName, String annotation, String groupId,
			String responsibilityDesc, Boolean ignorePrevious) throws WorkflowException {
		Recipient recipient = KEWServiceLocator.getIdentityHelperService().getGroupRecipient(groupId);
		AdHocAction action = new AdHocAction(document, user, annotation, actionRequested, nodeName, recipient, responsibilityDesc, ignorePrevious);
		action.performAction();
		return finish(document);
	}
	
	public DocumentRouteHeaderValue blanketApproval(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, Integer routeLevel) throws InvalidActionTakenException, KEWUserNotFoundException {
		RouteNode node = (routeLevel == null ? null : CompatUtils.getNodeForLevel(routeHeader.getDocumentType(), routeLevel));
		if (node == null && routeLevel != null) {
			throw new InvalidActionTakenException("Could not locate node for route level " + routeLevel);
		}
		Set<String> nodeNames = new HashSet<String>();
		if (node != null) {
			nodeNames = Utilities.asSet(node.getRouteNodeName());
		}
		ActionTakenEvent action = new BlanketApproveAction(routeHeader, user, annotation, nodeNames);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue blanketApproval(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, Set nodeNames) throws InvalidActionTakenException, KEWUserNotFoundException {
		BlanketApproveAction action = new BlanketApproveAction(routeHeader, user, annotation, nodeNames);
		action.recordAction();

		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue cancelDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		// init(routeHeader);
		CancelAction action = new CancelAction(routeHeader, user, annotation);
		action.recordAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue clearFYIDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws InvalidActionTakenException, KEWUserNotFoundException {
		// init(routeHeader);
		ClearFYIAction action = new ClearFYIAction(routeHeader, user, "");
		action.recordAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue completeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		CompleteAction action = new CompleteAction(routeHeader, user, annotation);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue createDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws DocumentTypeNotFoundException, WorkflowException {

		LOG.debug("Rmi createDocument() with "); // add some sort of a
													// routeheader logger

		if (routeHeader.getRouteHeaderId() != null) { // this is a debateable
														// check - means the
														// client is off
			throw new InvalidActionTakenException("Document already has a Document id");
		}

		boolean canInitiate = KEWServiceLocator.getDocumentTypePermissionService().canInitiate(user.getWorkflowId(), routeHeader.getDocumentType());

		if (!canInitiate) {
			throw new InvalidActionTakenException("User '" + user.getAuthenticationUserId().getId() + "' is not authorized to initiate documents of type '" + routeHeader.getDocumentType().getName());
		}

        if (!routeHeader.getDocumentType().isDocTypeActive()) {
            // don't allow creation if document type is inactive
            throw new InvalidActionTakenException("Document type '" + routeHeader.getDocumentType().getName() + "' is inactive");
        }

		routeHeader.setInitiatorWorkflowId(user.getWorkflowUserId().getWorkflowId());
		if (routeHeader.getDocRouteStatus() == null) {
			routeHeader.setDocRouteStatus(KEWConstants.ROUTE_HEADER_INITIATED_CD);
		}
		if (routeHeader.getDocRouteLevel() == null) {
			routeHeader.setDocRouteLevel(new Integer(KEWConstants.ADHOC_ROUTE_LEVEL));
		}
		if (routeHeader.getCreateDate() == null) {
			routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
		}
		if (routeHeader.getDocVersion() == null) {
			routeHeader.setDocVersion(new Integer(KEWConstants.CURRENT_DOCUMENT_VERSION));
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

	public DocumentRouteHeaderValue disapproveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		DisapproveAction action = new DisapproveAction(routeHeader, user, annotation);
		action.recordAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue returnDocumentToPreviousRouteLevel(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Integer destRouteLevel, String annotation)
			throws InvalidActionTakenException, KEWUserNotFoundException {
		RouteNode node = (destRouteLevel == null ? null : CompatUtils.getNodeForLevel(routeHeader.getDocumentType(), destRouteLevel));
		if (node == null && destRouteLevel != null) {
			throw new InvalidActionTakenException("Could not locate node for route level " + destRouteLevel);
		}
		ReturnToPreviousNodeAction action = new ReturnToPreviousNodeAction(routeHeader, user, annotation, node.getRouteNodeName(), true);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue returnDocumentToPreviousNode(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String destinationNodeName, String annotation)
			throws InvalidActionTakenException, KEWUserNotFoundException {
		ReturnToPreviousNodeAction action = new ReturnToPreviousNodeAction(routeHeader, user, annotation, destinationNodeName, true);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue routeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws WorkflowException,
			InvalidActionTakenException, KEWUserNotFoundException {
		RouteDocumentAction actionEvent = new RouteDocumentAction(routeHeader, user, annotation);
		actionEvent.performAction();
        LOG.info("routeDocument: " + routeHeader);
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue saveRoutingData(WorkflowUser user, DocumentRouteHeaderValue routeHeader) {
		KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
		if (routeHeader.getDocumentType().hasSearchableAttributes()) {
			SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(routeHeader);
			searchableAttService.indexDocument(routeHeader.getRouteHeaderId());
		}
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue saveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		SaveActionEvent action = new SaveActionEvent(routeHeader, user, annotation);
		action.performAction();
		return finish(routeHeader);
	}

	public void deleteDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws WorkflowException {
		if (routeHeader.getRouteHeaderId() == null) {
			LOG.info("Null Document id passed.");
			throw new WorkflowException("Document id must not be null.");
		}
		KEWServiceLocator.getRouteHeaderService().deleteRouteHeader(routeHeader);
	}

	public void logDocumentAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws KEWUserNotFoundException, InvalidActionTakenException {
		LogDocumentActionAction action = new LogDocumentActionAction(routeHeader, user, annotation);
		action.recordAction();
	}

	public DocumentRouteHeaderValue moveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, MovePoint movePoint, String annotation) throws InvalidActionTakenException, KEWUserNotFoundException {
		MoveDocumentAction action = new MoveDocumentAction(routeHeader, user, annotation, movePoint);
		action.performAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserActionRequestApproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Long actionRequestId, String annotation, boolean runPostProcessor)
			throws InvalidActionTakenException, KEWUserNotFoundException {
		init(routeHeader);
		SuperUserActionRequestApproveEvent suActionRequestApprove = new SuperUserActionRequestApproveEvent(routeHeader, user, actionRequestId, annotation, runPostProcessor);
		suActionRequestApprove.recordAction();
		// suActionRequestApprove.queueDocument();
		return finish(routeHeader);
	}

    /**
     * TODO As with superUserReturnDocumentToPreviousNode, we allow for the passing in of a document ID here to allow for
     * the document load inside the current running transaction.  Otherwise we get an optimistic lock exception
     * when attempting to save the branch after the transition to the 'A' status.
     */
    public DocumentRouteHeaderValue superUserActionRequestApproveAction(WorkflowUser user, Long documentId, Long actionRequestId, String annotation, boolean runPostProcessor)
        throws InvalidActionTakenException, KEWUserNotFoundException {
        return superUserActionRequestApproveAction(user, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), actionRequestId, annotation, runPostProcessor);
    }

	public DocumentRouteHeaderValue superUserApprove(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		init(routeHeader);
		new SuperUserApproveEvent(routeHeader, user, annotation, runPostProcessor).recordAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserCancelAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException,
			KEWUserNotFoundException {
		init(routeHeader);
		new SuperUserCancelEvent(routeHeader, user, annotation, runPostProcessor).recordAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserDisapproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException, KEWUserNotFoundException {
		init(routeHeader);
		new SuperUserDisapproveEvent(routeHeader, user, annotation, runPostProcessor).recordAction();
		return finish(routeHeader);
	}

	public DocumentRouteHeaderValue superUserNodeApproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation, boolean runPostProcessor) throws InvalidActionTakenException, KEWUserNotFoundException {
		init(routeHeader);
		new SuperUserNodeApproveEvent(routeHeader, user, annotation, runPostProcessor, nodeName).recordAction();
		return finish(routeHeader);
	}

	/**
	 * TODO As with superUserReturnDocumentToPreviousNode, we allow for the passing in of a document ID here to allow for
	 * the document load inside the current running transaction.  Otherwise we get an optimistic lock exception
	 * when attempting to save the branch after the transition to the 'A' status.
	 */
	public DocumentRouteHeaderValue superUserNodeApproveAction(WorkflowUser user, Long documentId, String nodeName, String annotation, boolean runPostProcessor) throws InvalidActionTakenException,
		KEWUserNotFoundException {
		return superUserNodeApproveAction(user, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), nodeName, annotation, runPostProcessor);
	}

	/**
	 * TODO remove this implementation in favor of having the SuperUserAction call through the WorkflowDocument object.  This
	 * method is here to resolve KULWF-727 where we were getting an optimistic lock exception from the super user screen on
	 * return to previous node.  This allows us to load the DocumentRouteHeaderValue inside of the transaction interceptor
	 * so that we can stay within the same PersistenceBroker cache.
	 */
	public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(WorkflowUser user, Long documentId, String nodeName, String annotation, boolean runPostProcessor)
		throws InvalidActionTakenException, KEWUserNotFoundException {
		return superUserReturnDocumentToPreviousNode(user, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), nodeName, annotation, runPostProcessor);
	}

	public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation, boolean runPostProcessor)
			throws InvalidActionTakenException, KEWUserNotFoundException {
		init(routeHeader);
		SuperUserReturnToPreviousNodeAction action = new SuperUserReturnToPreviousNodeAction(routeHeader, user, annotation, runPostProcessor, nodeName);
		action.recordAction();
		// action.queueDocument();

		return finish(routeHeader);
	}

	public void takeMassActions(WorkflowUser user, List actionInvocations) {
		for (Iterator iterator = actionInvocations.iterator(); iterator.hasNext();) {
			ActionInvocation invocation = (ActionInvocation) iterator.next();
			ActionItem actionItem = KEWServiceLocator.getActionListService().findByActionItemId(invocation.getActionItemId());
			if (actionItem == null) {
				LOG.warn("Could not locate action item for the given action item id [" + invocation.getActionItemId() + "], not taking mass action on it.");
				continue;
			}
			KEWServiceLocator.getActionListService().deleteActionItem(actionItem);
			ActionInvocationService actionInvocService = MessageServiceNames.getActionInvocationProcessorService(actionItem.getRouteHeader());
			actionInvocService.invokeAction(user, actionItem.getRouteHeaderId(), invocation);
//			ActionInvocationProcessor.queueActionInvocation(user, actionItem.getRouteHeaderId(), invocation);
		}
	}

	public DocumentRouteHeaderValue revokeAdHocRequests(WorkflowUser user, DocumentRouteHeaderValue document, AdHocRevoke revoke, String annotation) throws InvalidActionTakenException, KEWUserNotFoundException {
		RevokeAdHocAction action = new RevokeAdHocAction(document, user, revoke, annotation);
		action.performAction();
		return finish(document);
	}

}