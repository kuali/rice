package org.kuali.rice.kew.impl.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.AdHocRevokeFromGroup;
import org.kuali.rice.kew.api.action.AdHocRevokeFromPrincipal;
import org.kuali.rice.kew.api.action.AdHocToGroup;
import org.kuali.rice.kew.api.action.AdHocToPrincipal;
import org.kuali.rice.kew.api.action.DocumentActionResult;
import org.kuali.rice.kew.api.action.InvalidActionTakenException;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.RequestedActions;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.doctype.DocumentTypeNotFoundException;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentCreationException;
import org.kuali.rice.kew.api.document.DocumentUpdate;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.principal.Principal;

public class WorkflowDocumentActionsServiceImpl implements WorkflowDocumentActionsService {

	private static final Logger LOG = Logger.getLogger(WorkflowDocumentActionsServiceImpl.class);
	
	private DocumentTypeService documentTypeService;
	
	protected DocumentRouteHeaderValue init(String documentId, String principalId, DocumentUpdate documentUpdate, DocumentContentUpdate documentContentUpdate) {
		incomingParamCheck(documentId, "documentId");
		incomingParamCheck(principalId, "principalId");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Initializing Document from incoming documentId: " + documentId);
		}
		KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
		
		// TODO update notes?
		
		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		boolean modified = false;
		if (documentUpdate != null) {
			document.applyDocumentUpdate(documentUpdate);
			modified = true;
		}
		if (documentContentUpdate != null) {
			String newDocumentContent = DTOConverter.buildUpdatedDocumentContent(document.getDocContent(), documentContentUpdate, document.getDocumentTypeName());
			document.setDocContent(newDocumentContent);
			modified = true;
		}
		
		if (modified) {
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

		}
        
        return document;
	}
	
	/**
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
	
	/**
	 * 
	 * TODO
	 * 
	 * @throws RiceIllegalArgumentException if principalId is null or blank
	 * @throws RiceIllegalArgumentException if documentTypeName is null or blank
	 * @throws DocumentTypeNotFoundException if documentTypeName does not represent a valid document type
	 * @throws DocumentCreationException if document for the given document type could not be created
	 * @throws InvalidActionTakenException if the caller is not allowed to execute this action
	 */
	@Override
	public Document create(String documentTypeName,
			String initiatorPrincipalId, DocumentUpdate documentUpdate,
			DocumentContentUpdate documentContentUpdate)
			throws RiceIllegalArgumentException, DocumentTypeNotFoundException, DocumentCreationException, InvalidActionTakenException {
		
		incomingParamCheck(documentTypeName, "documentTypeName");
		incomingParamCheck(initiatorPrincipalId, "initiatorPrincipalId");

		if (LOG.isDebugEnabled()) {
			LOG.debug("Create Document [documentTypeName=" + documentTypeName + ", initiatorPrincipalId=" + initiatorPrincipalId + "]");
		}
		
		String documentTypeId = documentTypeService.getDocumentTypeIdByName(documentTypeName);
		if (documentTypeId == null) {
			throw new DocumentTypeNotFoundException("Failed to locate a document type with the given name: " + documentTypeName);
		}
		
		DocumentRouteHeaderValue documentBo = new DocumentRouteHeaderValue();
		documentBo.setDocumentTypeId(documentTypeId);
		documentBo.setInitiatorWorkflowId(initiatorPrincipalId);
		if (documentUpdate != null) {
			documentBo.setDocTitle(documentUpdate.getTitle());
			documentBo.setAppDocId(documentUpdate.getApplicationDocumentId());
		}
		if (documentContentUpdate != null) {
			String newDocumentContent = DTOConverter.buildUpdatedDocumentContent(null, documentContentUpdate, documentTypeName);
			documentBo.setDocContent(newDocumentContent);
		}
		
		try {
			documentBo = KEWServiceLocator.getWorkflowDocumentService().createDocument(initiatorPrincipalId, documentBo);
		} catch (WorkflowException e) {
			// TODO remove this once we stop throwing WorkflowException everywhere!
			translateException(e);
		}
		return DocumentRouteHeaderValue.to(documentBo);
	}

	@Override
	public ValidActions determineValidActions(String documentId, String principalId) {
		incomingParamCheck(documentId, "documentId");
		incomingParamCheck(principalId, "principalId");
		DocumentRouteHeaderValue documentBo = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		if (documentBo == null) {
			throw new IllegalArgumentException("Failed to locate a document for document id: " + documentId);
		}
		return determineValidActionsInternal(documentBo, principalId);
	}
	
	protected ValidActions determineValidActionsInternal(DocumentRouteHeaderValue documentBo, String principalId) {
		Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId);
		return KEWServiceLocator.getActionRegistry().getNewValidActions(principal, documentBo);
	}		
	
	@Override
	public RequestedActions determineRequestedActions(String documentId, String principalId) {
		incomingParamCheck(documentId, "documentId");
		incomingParamCheck(principalId, "principalId");DocumentRouteHeaderValue documentBo = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		if (documentBo == null) {
			throw new IllegalArgumentException("Failed to locate a document for document id: " + documentId);
		}
		return determineRequestedActionsInternal(documentBo, principalId);
	}
	
	protected RequestedActions determineRequestedActionsInternal(DocumentRouteHeaderValue documentBo, String principalId) {
		AttributeSet actionsRequested = KEWServiceLocator.getActionRequestService().getActionsRequested(documentBo, principalId, true);
		boolean completeRequested = false;
		boolean approveRequested = false;
		boolean acknowledgeRequested = false;
		boolean fyiRequested = false;
        for (String actionRequestCode : actionsRequested.keySet()) {
			if (ActionRequestType.FYI.getCode().equals(actionRequestCode)) {
                fyiRequested = Boolean.parseBoolean(actionsRequested.get(actionRequestCode));					
			} else if (ActionRequestType.ACKNOWLEDGE.getCode().equals(actionRequestCode)) {
               acknowledgeRequested = Boolean.parseBoolean(actionsRequested.get(actionRequestCode));
			} else if (ActionRequestType.APPROVE.getCode().equals(actionRequestCode)) {
                approveRequested = Boolean.parseBoolean(actionsRequested.get(actionRequestCode));					
			} else if (ActionRequestType.COMPLETE.getCode().equals(actionRequestCode)) {
                completeRequested = Boolean.parseBoolean(actionsRequested.get(actionRequestCode));
			}
		}
        return RequestedActions.create(completeRequested, approveRequested, acknowledgeRequested, fyiRequested);
	}
	
	protected DocumentActionResult constructDocumentActionResponse(DocumentRouteHeaderValue documentBo, String principalId) {
		Document document = DocumentRouteHeaderValue.to(documentBo);
		ValidActions validActions = determineValidActionsInternal(documentBo, principalId);
		RequestedActions requestedActions = determineRequestedActionsInternal(documentBo, principalId);
		return DocumentActionResult.create(document, validActions, requestedActions);
	}

	@Override
	public void acknowledge(String documentId, String principalId,
			String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void approve(String documentId, String principalId, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void adHocToPrincipal(String documentId, String principalId,
			AdHocToPrincipal adHocCommand, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void adHocToGroup(String documentId, String principalId,
			AdHocToGroup adHocCommand, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void revokeAdHocRequestsFromPrincipal(String documentId,
			String principalId, AdHocRevokeFromPrincipal revoke,
			String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void revokeAdHocRequestsFromGroup(String documentId,
			String principalId, AdHocRevokeFromGroup revoke, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void cancel(String documentId, String principalId, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void clearFyi(String documentId, String principalId,
			String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void complete(String documentId, String principalId,
			String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void disapprove(String documentId, String principalId,
			String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public DocumentActionResult route(String documentId, String principalId, String annotation,
			DocumentUpdate documentUpdate,
			DocumentContentUpdate documentContentUpdate) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Route Document [principalId=" + principalId + ", documentId=" + documentId + ", annotation=" + annotation + "]");
		}
		DocumentRouteHeaderValue documentBo = init(documentId, principalId, documentUpdate, documentContentUpdate);
		try {
			documentBo = KEWServiceLocator.getWorkflowDocumentService().routeDocument(principalId, documentBo, annotation);
		} catch (WorkflowException e) {
			// TODO fix this up once the checked exception goes away
			translateException(e);
		}
		return constructDocumentActionResponse(documentBo, principalId);
	}

	@Override
	public void blanketApproveToNodes(String documentId, String principalId,
			List<String> nodeNames, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void returnToPreviousNode(String documentId, String principalId,
			ReturnPoint returnPoint, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void move(String documentId, String principalId,
			MovePoint movePoint, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void takeGroupAuthority(String documentId, String principalId,
			String groupId, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void releaseGroupAuthority(String documentId, String principalId,
			String groupId, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void save(String documentId, String principalId, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void delete(String documentId) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void logAnnotation(String documentId, String principalId,
			String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void initiateIndexing(String documentId) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void superUserFullApprove(String documentId, String principalId,
			boolean executePostProcessor, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void superUserNodeApprove(String documentId, String principalId,
			String nodeName, boolean executePostProcessor, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void superUserTakeRequestedAction(String documentId,
			String principalId, String actionRequestId,
			boolean executePostProcessor, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void superUserDisapprove(String documentId, String principalId,
			boolean executePostProcessor, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void superUserCancel(String documentId, String principalId,
			boolean executePostProcessor, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void superUserReturnToPreviousNode(String documentId,
			String principalId, ReturnPoint returnPoint,
			boolean executePostProcessor, String annotation) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public void placeInExceptionRouting(String documentId, String principalId) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	@Override
	public List<WorkflowAttributeValidationError> validateWorkflowAttributeDefinition(
			WorkflowAttributeDefinition definition) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	private void incomingParamCheck(Object object, String name) {
		if (object == null) {
			throw new RiceIllegalArgumentException(name + " was null");
		} else if (object instanceof String
				&& StringUtils.isBlank((String) object)) {
			throw new RiceIllegalArgumentException(name + " was blank");
		}
	}
	
	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}

	/**
	 * TODO - this code is temporary until we get rid of all the crazy
	 * throwing of "WorkflowException"
	 */
	private void translateException(WorkflowException e) {
		if (e instanceof org.kuali.rice.kew.exception.InvalidActionTakenException) {
			throw new InvalidActionTakenException(e.getMessage(), e);
		}
		throw new WorkflowRuntimeException(e.getMessage(), e);
	}
	
}
