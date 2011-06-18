package org.kuali.rice.kew.impl.action;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.api.action.AdHocRevokeFromGroup;
import org.kuali.rice.kew.api.action.AdHocRevokeFromPrincipal;
import org.kuali.rice.kew.api.action.AdHocToGroup;
import org.kuali.rice.kew.api.action.AdHocToPrincipal;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentUpdate;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;

public class WorkflowDocumentActionsServiceImpl implements WorkflowDocumentActionsService {

	private static final Logger LOG = Logger.getLogger(WorkflowDocumentActionsServiceImpl.class);
	
	private DocumentTypeService documentTypeService;
	
	@Override
	public Document create(String documentTypeName,
			String initiatorPrincipalId, DocumentUpdate documentUpdate,
			DocumentContentUpdate documentContentUpdate)
			throws RiceIllegalArgumentException {
		
		incomingParamCheck(documentTypeName, "documentTypeName");
		incomingParamCheck(initiatorPrincipalId, "initiatorPrincipalId");

		if (LOG.isDebugEnabled()) {
			LOG.debug("Create Document [documentTypeName=" + documentTypeName + ", initiatorPrincipalId=" + initiatorPrincipalId + "]");
		}
		
		String documentTypeId = documentTypeService.getDocumentTypeIdByName(documentTypeName);
		if (documentTypeId == null) {
			throw new IllegalArgumentException("Failed to locate a document type with the given name: " + documentTypeName);
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
			return DocumentRouteHeaderValue.to(documentBo);
		} catch (WorkflowException e) {
			// TODO remove this once we stop throwing WorkflowException everywhere!
			throw new RiceRuntimeException(e);
		}
	}

	@Override
	public ValidActions determineValidActions(String documentId,
			String principalId) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS
		return null;
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
	public void route(String documentId, String principalId, String annotation,
			DocumentUpdate documentUpdate,
			DocumentContentUpdate documentContentUpdate) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

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

}
