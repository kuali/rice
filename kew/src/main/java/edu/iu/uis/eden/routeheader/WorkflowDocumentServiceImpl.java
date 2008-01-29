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
package edu.iu.uis.eden.routeheader;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.workflow.actions.ActionHandler;
import org.kuali.workflow.actions.ActionHandlerRegistryImpl;
import org.kuali.workflow.actions.RouteActionHandler;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actions.ActionTakenEvent;
import edu.iu.uis.eden.actions.AdHocAction;
import edu.iu.uis.eden.actions.AdHocRevoke;
import edu.iu.uis.eden.actions.BlanketApproveAction;
import edu.iu.uis.eden.actions.MoveDocumentAction;
import edu.iu.uis.eden.actions.MovePoint;
import edu.iu.uis.eden.actions.ReleaseWorkgroupAuthority;
import edu.iu.uis.eden.actions.ReturnToPreviousNodeAction;
import edu.iu.uis.eden.actions.RevokeAdHocAction;
import edu.iu.uis.eden.actions.SuperUserActionRequestApproveEvent;
import edu.iu.uis.eden.actions.SuperUserApproveEvent;
import edu.iu.uis.eden.actions.SuperUserCancelEvent;
import edu.iu.uis.eden.actions.SuperUserDisapproveEvent;
import edu.iu.uis.eden.actions.SuperUserNodeApproveEvent;
import edu.iu.uis.eden.actions.SuperUserReturnToPreviousNodeAction;
import edu.iu.uis.eden.actions.TakeWorkgroupAuthority;
import edu.iu.uis.eden.actions.asyncservices.ActionInvocation;
import edu.iu.uis.eden.actions.asyncservices.ActionInvocationService;
import edu.iu.uis.eden.docsearch.SearchableAttributeProcessingService;
import edu.iu.uis.eden.engine.CompatUtils;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.exception.DocumentTypeNotFoundException;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;

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

    protected DocumentRouteHeaderValue takeAction(String action, DocumentRouteHeaderValue routeHeader, WorkflowUser user, String annotation, Object... args) throws InvalidActionTakenException, EdenUserNotFoundException {
        try {
            ActionHandler actionHandler = new ActionHandlerRegistryImpl().getActionHandler(action);
            actionHandler.invoke(routeHeader, user, annotation, args);
        } catch (ResourceUnavailableException rue) {
            throw new WorkflowRuntimeException("Could not find handler for action '" + action + "'", rue);
        }
        return finish(routeHeader);
    }

	public DocumentRouteHeaderValue acknowledgeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
	    return takeAction("acknowledge", routeHeader, user, annotation);
	}

	public DocumentRouteHeaderValue releaseWorkgroupAuthority(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Workgroup workgroup, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
	    return takeAction("releaseworkgroupauthority", routeHeader, user, annotation, workgroup);
	}

	public DocumentRouteHeaderValue takeWorkgroupAuthority(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Workgroup workgroup, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
	    return takeAction("takeworkgroupauthority", routeHeader, user, annotation, workgroup);
	}

	public DocumentRouteHeaderValue approveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
	    return takeAction("approve", routeHeader, user, annotation);
	}

	public DocumentRouteHeaderValue appSpecificRouteDocument(WorkflowUser user, DocumentRouteHeaderValue document, String actionRequested, String nodeName, String annotation, Recipient recipient,
			String responsibilityDesc, Boolean ignorePrevious) throws WorkflowException {
	    return takeAction("adhoc", document, user, annotation, actionRequested, nodeName, recipient, responsibilityDesc, ignorePrevious);
	}

	public DocumentRouteHeaderValue blanketApproval(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, Integer routeLevel) throws InvalidActionTakenException, EdenUserNotFoundException {
		RouteNode node = (routeLevel == null ? null : CompatUtils.getNodeForLevel(routeHeader.getDocumentType(), routeLevel));
		if (node == null && routeLevel != null) {
			throw new InvalidActionTakenException("Could not locate node for route level " + routeLevel);
		}
		Set<String> nodeNames = new HashSet<String>();
		if (node != null) {
			nodeNames = Utilities.asSet(node.getRouteNodeName());
		}
		return blanketApproval(user, routeHeader, annotation, nodeNames);
	}

	public DocumentRouteHeaderValue blanketApproval(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, Set nodeNames) throws InvalidActionTakenException, EdenUserNotFoundException {
	    return takeAction("blanketapprove", routeHeader, user, annotation, nodeNames);
	}

	public DocumentRouteHeaderValue cancelDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
		// init(routeHeader);
	    return takeAction("cancel", routeHeader, user, annotation);
	}

	public DocumentRouteHeaderValue clearFYIDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws InvalidActionTakenException, EdenUserNotFoundException {
		// init(routeHeader);
	    return takeAction("clearfyi", routeHeader, user, "");
	}

	public DocumentRouteHeaderValue completeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
	    return takeAction("complete", routeHeader, user, annotation);
	}


	public DocumentRouteHeaderValue disapproveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
	    return takeAction("disapprove", routeHeader, user, annotation);
	}

	public DocumentRouteHeaderValue returnDocumentToPreviousRouteLevel(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Integer destRouteLevel, String annotation)
			throws InvalidActionTakenException, EdenUserNotFoundException {
		RouteNode node = (destRouteLevel == null ? null : CompatUtils.getNodeForLevel(routeHeader.getDocumentType(), destRouteLevel));
		if (node == null && destRouteLevel != null) {
			throw new InvalidActionTakenException("Could not locate node for route level " + destRouteLevel);
		}
		return returnDocumentToPreviousNode(user, routeHeader, node.getRouteNodeName(), annotation);
	}

	public DocumentRouteHeaderValue returnDocumentToPreviousNode(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String destinationNodeName, String annotation)
			throws InvalidActionTakenException, EdenUserNotFoundException {
	    return takeAction("returntopreviousnode", routeHeader, user, annotation, destinationNodeName, true, true, null);
	}

	public DocumentRouteHeaderValue routeDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws WorkflowException,
			InvalidActionTakenException, EdenUserNotFoundException {
	    return takeAction("route", routeHeader, user, annotation);
	}

	public DocumentRouteHeaderValue saveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws InvalidActionTakenException,
			EdenUserNotFoundException {
	    return takeAction("save", routeHeader, user, annotation);
	}

	public void logDocumentAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation) throws EdenUserNotFoundException, InvalidActionTakenException {
	    takeAction("log", routeHeader, user, annotation);
	}

	public DocumentRouteHeaderValue moveDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader, MovePoint movePoint, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException {
	    return takeAction("movedocument", routeHeader, user, annotation, movePoint);
	}

	
	// ---- super user actions
	
	public DocumentRouteHeaderValue superUserActionRequestApproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, Long actionRequestId, String annotation, boolean runPostProcessor)
			throws InvalidActionTakenException, EdenUserNotFoundException {
		init(routeHeader);
		return takeAction("suactionrequestapprove", routeHeader, user, annotation, actionRequestId, runPostProcessor);
	}

    /**
     * TODO As with superUserReturnDocumentToPreviousNode, we allow for the passing in of a document ID here to allow for
     * the document load inside the current running transaction.  Otherwise we get an optimistic lock exception
     * when attempting to save the branch after the transition to the 'A' status.
     */
    public DocumentRouteHeaderValue superUserActionRequestApproveAction(WorkflowUser user, Long documentId, Long actionRequestId, String annotation, boolean runPostProcessor)
        throws InvalidActionTakenException, EdenUserNotFoundException {
        return superUserActionRequestApproveAction(user, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), actionRequestId, annotation, runPostProcessor);
    }

	public DocumentRouteHeaderValue superUserApprove(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException,
			EdenUserNotFoundException {
		init(routeHeader);
		return takeAction("suapprove", routeHeader, user, annotation, runPostProcessor);
	}

	public DocumentRouteHeaderValue superUserCancelAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException,
			EdenUserNotFoundException {
		init(routeHeader);
		return takeAction("sucancel", routeHeader, user, annotation, runPostProcessor);
	}

	public DocumentRouteHeaderValue superUserDisapproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String annotation, boolean runPostProcessor) throws InvalidActionTakenException, EdenUserNotFoundException {
		init(routeHeader);
		return takeAction("sudisapprove", routeHeader, user, annotation, runPostProcessor);
	}

	public DocumentRouteHeaderValue superUserNodeApproveAction(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation, boolean runPostProcessor) throws InvalidActionTakenException, EdenUserNotFoundException {
		init(routeHeader);
		return takeAction("sunodeapprove", routeHeader, user, annotation, nodeName, runPostProcessor);
	}

	/**
	 * TODO As with superUserReturnDocumentToPreviousNode, we allow for the passing in of a document ID here to allow for
	 * the document load inside the current running transaction.  Otherwise we get an optimistic lock exception
	 * when attempting to save the branch after the transition to the 'A' status.
	 */
	public DocumentRouteHeaderValue superUserNodeApproveAction(WorkflowUser user, Long documentId, String nodeName, String annotation, boolean runPostProcessor) throws InvalidActionTakenException,
		EdenUserNotFoundException {
		return superUserNodeApproveAction(user, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), nodeName, annotation, runPostProcessor);
	}

	/**
	 * TODO remove this implementation in favor of having the SuperUserAction call through the WorkflowDocument object.  This
	 * method is here to resolve KULWF-727 where we were getting an optimistic lock exception from the super user screen on
	 * return to previous node.  This allows us to load the DocumentRouteHeaderValue inside of the transaction interceptor
	 * so that we can stay within the same PersistenceBroker cache.
	 */
	public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(WorkflowUser user, Long documentId, String nodeName, String annotation, boolean runPostProcessor)
		throws InvalidActionTakenException, EdenUserNotFoundException {
		return superUserReturnDocumentToPreviousNode(user, KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId), nodeName, annotation, runPostProcessor);
	}

	public DocumentRouteHeaderValue superUserReturnDocumentToPreviousNode(WorkflowUser user, DocumentRouteHeaderValue routeHeader, String nodeName, String annotation, boolean runPostProcessor)
			throws InvalidActionTakenException, EdenUserNotFoundException {
		init(routeHeader);
		return takeAction("sureturntopreviousnode", routeHeader, user, annotation, nodeName, runPostProcessor);
	}
	
	public DocumentRouteHeaderValue revokeAdHocRequests(WorkflowUser user, DocumentRouteHeaderValue document, AdHocRevoke revoke, String annotation) throws InvalidActionTakenException, EdenUserNotFoundException {
	    return takeAction("revokeadhoc", document, user, annotation, revoke);
	}
	
	// ---- non-"actions"

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
//	          ActionInvocationProcessor.queueActionInvocation(user, actionItem.getRouteHeaderId(), invocation);
        }
    }

    public DocumentRouteHeaderValue saveRoutingData(WorkflowUser user, DocumentRouteHeaderValue routeHeader) {
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
        if (routeHeader.getDocumentType().hasSearchableAttributes()) {
            SearchableAttributeProcessingService searchableAttService = (SearchableAttributeProcessingService) MessageServiceNames.getSearchableAttributeService(routeHeader);
            searchableAttService.indexDocument(routeHeader.getRouteHeaderId());
        }
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

        if (!routeHeader.getDocumentType().isDocTypeActive()) {
            // don't allow creation if document type is inactive
            throw new InvalidActionTakenException("Document type '" + routeHeader.getDocumentType().getName() + "' is inactive");
        }

        routeHeader.setInitiatorWorkflowId(user.getWorkflowUserId().getWorkflowId());
        if (routeHeader.getDocRouteStatus() == null) {
            routeHeader.setDocRouteStatus(EdenConstants.ROUTE_HEADER_INITIATED_CD);
        }
        if (routeHeader.getDocRouteLevel() == null) {
            routeHeader.setDocRouteLevel(new Integer(EdenConstants.ADHOC_ROUTE_LEVEL));
        }
        if (routeHeader.getCreateDate() == null) {
            routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        }
        if (routeHeader.getDocVersion() == null) {
            routeHeader.setDocVersion(new Integer(EdenConstants.CURRENT_DOCUMENT_VERSION));
        }
        if (routeHeader.getDocContent() == null) {
            routeHeader.setDocContent(EdenConstants.DEFAULT_DOCUMENT_CONTENT);
        }
        routeHeader.setStatusModDate(new Timestamp(new Date().getTime()));
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
        KEWServiceLocator.getWorkflowEngine().initializeDocument(routeHeader);
        KEWServiceLocator.getRouteHeaderService().saveRouteHeader(routeHeader);
        return routeHeader;
    }


    public void deleteDocument(WorkflowUser user, DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        if (routeHeader.getRouteHeaderId() == null) {
            LOG.info("Null Document id passed.");
            throw new WorkflowException("Document id must not be null.");
        }
        KEWServiceLocator.getRouteHeaderService().deleteRouteHeader(routeHeader);
    }
}