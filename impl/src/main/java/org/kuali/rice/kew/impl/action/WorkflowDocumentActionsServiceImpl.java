package org.kuali.rice.kew.impl.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.action.AdHocToGroup;
import org.kuali.rice.kew.api.action.AdHocToPrincipal;
import org.kuali.rice.kew.api.action.DocumentActionParameters;
import org.kuali.rice.kew.api.action.DocumentActionResult;
import org.kuali.rice.kew.api.action.InvalidActionTakenException;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.action.RequestedActions;
import org.kuali.rice.kew.api.action.ReturnPoint;
import org.kuali.rice.kew.api.action.ValidActions;
import org.kuali.rice.kew.api.action.WorkflowDocumentActionsService;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.doctype.IllegalDocumentTypeException;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentUpdate;
import org.kuali.rice.kew.api.document.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.WorkflowAttributeValidationError;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.principal.Principal;

/**
 * Reference implementation of the {@link WorkflowDocumentActionsService} api.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class WorkflowDocumentActionsServiceImpl implements WorkflowDocumentActionsService {

    private static final Logger LOG = Logger.getLogger(WorkflowDocumentActionsServiceImpl.class);

    private DocumentTypeService documentTypeService;

    private static final DocumentActionCallback ACKNOWLEDGE_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().acknowledgeDocument(principalId, documentBo,
                    annotation);
        }

        public String getActionName() {
            return ActionType.ACKNOWLEDGE.getLabel();
        }
    };

    private static final DocumentActionCallback APPROVE_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().approveDocument(principalId, documentBo, annotation);
        }

        public String getActionName() {
            return ActionType.APPROVE.getLabel();
        }
    };

    private static final DocumentActionCallback CANCEL_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().cancelDocument(principalId, documentBo, annotation);
        }

        public String getActionName() {
            return ActionType.CANCEL.getLabel();
        }
    };

    private static final DocumentActionCallback FYI_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().clearFYIDocument(principalId, documentBo, annotation);
        }

        public String getActionName() {
            return ActionType.FYI.getLabel();
        }
    };

    private static final DocumentActionCallback COMPLETE_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().completeDocument(principalId, documentBo, annotation);
        }

        public String getActionName() {
            return ActionType.COMPLETE.getLabel();
        }
    };

    private static final DocumentActionCallback DISAPPROVE_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().disapproveDocument(principalId, documentBo,
                    annotation);
        }

        public String getActionName() {
            return ActionType.DISAPPROVE.getLabel();
        }
    };

    private static final DocumentActionCallback ROUTE_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().routeDocument(principalId, documentBo, annotation);
        }

        public String getActionName() {
            return ActionType.ROUTE.getLabel();
        }
    };

    private static final DocumentActionCallback BLANKET_APPROVE_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().blanketApproval(principalId, documentBo, annotation,
                    new HashSet<String>());
        }

        public String getActionName() {
            return ActionType.BLANKET_APPROVE.getLabel();
        }
    };

    private static final DocumentActionCallback SAVE_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().saveDocument(principalId, documentBo, annotation);
        }

        public String getActionName() {
            return ActionType.SAVE.getLabel();
        }
    };

    private static final DocumentActionCallback PLACE_IN_EXCEPTION_CALLBACK = new StandardDocumentActionCallback() {
        public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException {
            return KEWServiceLocator.getWorkflowDocumentService().placeInExceptionRouting(principalId, documentBo,
                    annotation);
        }

        public String getActionName() {
            return "Place In Exception";
        }
    };

    protected DocumentRouteHeaderValue init(DocumentActionParameters parameters) {
        String documentId = parameters.getDocumentId();
        String principalId = parameters.getPrincipalId();
        DocumentUpdate documentUpdate = parameters.getDocumentUpdate();
        DocumentContentUpdate documentContentUpdate = parameters.getDocumentContentUpdate();
        incomingParamCheck(documentId, "documentId");
        incomingParamCheck(principalId, "principalId");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing Document from incoming documentId: " + documentId);
        }
        KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);

        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        if (document == null) {
            throw new RiceIllegalArgumentException("Failed to locate a document for document id: " + documentId);
        }
        boolean modified = false;
        if (documentUpdate != null) {
            document.applyDocumentUpdate(documentUpdate);
            modified = true;
        }
        if (documentContentUpdate != null) {
            String newDocumentContent = DTOConverter.buildUpdatedDocumentContent(document.getDocContent(),
                    documentContentUpdate, document.getDocumentTypeName());
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
    private void saveRouteNodeInstances(DocumentRouteHeaderValue routeHeader) {

        List<RouteNodeInstance> routeNodes = routeHeader.getInitialRouteNodeInstances();
        if (routeNodes != null && !routeNodes.isEmpty()) {
            for (RouteNodeInstance rni : routeNodes) {
                KEWServiceLocator.getRouteNodeService().save(rni);
            }
        }

    }

    @Override
    public Document create(String documentTypeName,
            String initiatorPrincipalId, DocumentUpdate documentUpdate,
            DocumentContentUpdate documentContentUpdate)
            throws RiceIllegalArgumentException, IllegalDocumentTypeException, InvalidActionTakenException {

        incomingParamCheck(documentTypeName, "documentTypeName");
        incomingParamCheck(initiatorPrincipalId, "initiatorPrincipalId");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Create Document [documentTypeName=" + documentTypeName + ", initiatorPrincipalId="
                    + initiatorPrincipalId + "]");
        }

        String documentTypeId = documentTypeService.getIdByName(documentTypeName);
        if (documentTypeId == null) {
            throw new RiceIllegalArgumentException("Failed to locate a document type with the given name: "
                    + documentTypeName);
        }

        DocumentRouteHeaderValue documentBo = new DocumentRouteHeaderValue();
        documentBo.setDocumentTypeId(documentTypeId);
        documentBo.setInitiatorWorkflowId(initiatorPrincipalId);
        if (documentUpdate != null) {
            documentBo.setDocTitle(documentUpdate.getTitle());
            documentBo.setAppDocId(documentUpdate.getApplicationDocumentId());
        }
        if (documentContentUpdate != null) {
            String newDocumentContent = DTOConverter.buildUpdatedDocumentContent(null, documentContentUpdate,
                    documentTypeName);
            documentBo.setDocContent(newDocumentContent);
        }

        try {
            documentBo = KEWServiceLocator.getWorkflowDocumentService()
                    .createDocument(initiatorPrincipalId, documentBo);
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
            throw new RiceIllegalArgumentException("Failed to locate a document for document id: " + documentId);
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
        incomingParamCheck(principalId, "principalId");
        DocumentRouteHeaderValue documentBo = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        if (documentBo == null) {
            throw new RiceIllegalArgumentException("Failed to locate a document for document id: " + documentId);
        }
        KEWServiceLocator.getIdentityHelperService().validatePrincipalId(principalId);
        return determineRequestedActionsInternal(documentBo, principalId);
    }

    protected RequestedActions determineRequestedActionsInternal(DocumentRouteHeaderValue documentBo, String principalId) {
        AttributeSet actionsRequested = KEWServiceLocator.getActionRequestService().getActionsRequested(documentBo,
                principalId, true);
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

    protected DocumentActionResult constructDocumentActionResult(DocumentRouteHeaderValue documentBo, String principalId) {
        Document document = DocumentRouteHeaderValue.to(documentBo);
        ValidActions validActions = determineValidActionsInternal(documentBo, principalId);
        RequestedActions requestedActions = determineRequestedActionsInternal(documentBo, principalId);
        return DocumentActionResult.create(document, validActions, requestedActions);
    }

    @Override
    public DocumentActionResult acknowledge(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, ACKNOWLEDGE_CALLBACK);
    }

    @Override
    public DocumentActionResult approve(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, APPROVE_CALLBACK);
    }

    @Override
    public DocumentActionResult adHocToPrincipal(DocumentActionParameters parameters,
            final AdHocToPrincipal adHocToPrincipal) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    @Override
                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "AdHoc Route To Principal [principalId=" + principalId +
                                ", docId=" + documentId +
                                ", actionRequest=" + adHocToPrincipal.getActionRequested() +
                                ", nodeName=" + adHocToPrincipal.getNodeName() +
                                ", targetPrincipalId=" + adHocToPrincipal.getTargetPrincipalId() +
                                ", forceAction=" + adHocToPrincipal.isForceAction() +
                                ", annotation=" + annotation +
                                ", requestLabel=" + adHocToPrincipal.getRequestLabel() + "]";
                    }

                    @Override
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().adHocRouteDocumentToPrincipal(
                                principalId,
                                    documentBo,
                                    adHocToPrincipal.getActionRequested().getCode(),
                                    adHocToPrincipal.getNodeName(),
                                    annotation,
                                    adHocToPrincipal.getTargetPrincipalId(),
                                    adHocToPrincipal.getResponsibilityDescription(),
                                    adHocToPrincipal.isForceAction(),
                                    adHocToPrincipal.getRequestLabel());
                    }
                });
    }

    @Override
    public DocumentActionResult adHocToGroup(DocumentActionParameters parameters,
            final AdHocToGroup adHocToGroup) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    @Override
                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "AdHoc Route To Group [principalId=" + principalId +
                                ", docId=" + documentId +
                                ", actionRequest=" + adHocToGroup.getActionRequested() +
                                ", nodeName=" + adHocToGroup.getNodeName() +
                                ", targetGroupId=" + adHocToGroup.getTargetGroupId() +
                                ", forceAction=" + adHocToGroup.isForceAction() +
                                ", annotation=" + annotation +
                                ", requestLabel=" + adHocToGroup.getRequestLabel() + "]";
                    }

                    @Override
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().adHocRouteDocumentToGroup(principalId,
                                    documentBo,
                                    adHocToGroup.getActionRequested().getCode(),
                                    adHocToGroup.getNodeName(),
                                    annotation,
                                    adHocToGroup.getTargetGroupId(),
                                    adHocToGroup.getResponsibilityDescription(),
                                    adHocToGroup.isForceAction(),
                                    adHocToGroup.getRequestLabel());
                    }
                });
    }

    @Override
    public DocumentActionResult revokeAdHocRequestById(DocumentActionParameters parameters,
            final String actionRequestId) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    @Override
                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "Revoke AdHoc from Principal [principalId=" + principalId +
                                ", documentId=" + documentId +
                                ", annotation=" + annotation +
                                ", actionRequestId=" + actionRequestId + "]";
                    }

                    @Override
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().revokeAdHocRequests(principalId,
                                documentBo, actionRequestId, annotation);
                    }
                });
    }

    @Override
    public DocumentActionResult revokeAdHocRequests(DocumentActionParameters parameters,
            final AdHocRevoke revoke) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    @Override
                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "Revoke AdHoc Requests [principalId=" + principalId +
                                ", docId=" + documentId +
                                ", annotation=" + annotation +
                                ", revoke=" + revoke.toString() + "]";
                    }

                    @Override
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().revokeAdHocRequests(principalId,
                                documentBo, revoke, annotation);
                    }
                });
    }

    @Override
    public DocumentActionResult revokeAllAdHocRequests(DocumentActionParameters parameters) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    @Override
                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "Revoke All AdHoc Requests [principalId=" + principalId +
                                ", docId=" + documentId +
                                ", annotation=" + annotation + "]";
                    }

                    @Override
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().revokeAdHocRequests(principalId,
                                documentBo, (AdHocRevoke) null, annotation);
                    }
                });
    }

    @Override
    public DocumentActionResult cancel(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, CANCEL_CALLBACK);
    }

    public DocumentActionResult clearFyi(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, FYI_CALLBACK);
    }

    @Override
    public DocumentActionResult complete(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, COMPLETE_CALLBACK);
    }

    @Override
    public DocumentActionResult disapprove(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, DISAPPROVE_CALLBACK);
    }

    @Override
    public DocumentActionResult route(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, ROUTE_CALLBACK);
    }

    @Override
    public DocumentActionResult blanketApprove(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, BLANKET_APPROVE_CALLBACK);
    }

    @Override
    public DocumentActionResult blanketApproveToNodes(DocumentActionParameters parameters,
            final Set<String> nodeNames) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().blanketApproval(principalId, documentBo,
                                annotation, nodeNames);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "Blanket Approve [principalId=" + principalId + ", documentId=" + documentId
                                + ", annotation=" + annotation + ", nodeNames=" + nodeNames + "]";
                    }
                });
    }

    @Override
    public DocumentActionResult returnToPreviousNode(DocumentActionParameters parameters,
            final ReturnPoint returnPoint) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().returnDocumentToPreviousNode(principalId,
                                documentBo, returnPoint.getNodeName(), annotation);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "Return to Previous [principalId=" + principalId + ", documentId=" + documentId
                                + ", annotation=" + annotation + ", destNodeName=" + returnPoint.getNodeName() + "]";
                    }
                });
    }

    @Override
    public DocumentActionResult move(DocumentActionParameters parameters,
            final MovePoint movePoint) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().moveDocument(principalId, documentBo,
                                movePoint, annotation);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "Move Document [principalId=" + principalId + ", documentId=" + documentId
                                + ", annotation=" + annotation + ", movePoint=" + movePoint + "]";
                    }
                });
    }

    @Override
    public DocumentActionResult takeGroupAuthority(DocumentActionParameters parameters,
            final String groupId) {
        return executeActionInternal(parameters,
                new StandardDocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().takeGroupAuthority(principalId,
                                documentBo, groupId, annotation);
                    }

                    public String getActionName() {
                        return ActionType.TAKE_GROUP_AUTHORITY.getLabel();
                    }
                });
    }

    @Override
    public DocumentActionResult releaseGroupAuthority(DocumentActionParameters parameters,
            final String groupId) {
        return executeActionInternal(parameters,
                new StandardDocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().releaseGroupAuthority(principalId,
                                documentBo, groupId, annotation);
                    }

                    public String getActionName() {
                        return ActionType.RELEASE_GROUP_AUTHORITY.getLabel();
                    }
                });

    }

    @Override
    public DocumentActionResult save(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, SAVE_CALLBACK);
    }

    @Override
    public DocumentActionResult saveDocumentData(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, new DocumentActionCallback() {

            @Override
            public String getLogMessage(String documentId, String principalId, String annotation) {
                return "Saving Routing Data [principalId=" + principalId + ", docId=" + documentId + "]";
            }

            @Override
            public DocumentRouteHeaderValue doInDocumentBo(
                    DocumentRouteHeaderValue documentBo, String principalId,
                    String annotation) throws WorkflowException {
                return KEWServiceLocator.getWorkflowDocumentService().saveRoutingData(principalId, documentBo);
            }
        });
    }

    @Override
    public Document delete(String documentId, String principalId) {
        DocumentRouteHeaderValue documentBo = init(DocumentActionParameters.create(documentId, principalId, null));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Delete [principalId=" + principalId + ", documentId=" + documentId + "]");
        }
        Document document = null;
        try {
            document = DocumentRouteHeaderValue.to(documentBo);
            KEWServiceLocator.getWorkflowDocumentService().deleteDocument(principalId, documentBo);
            
        } catch (WorkflowException e) {
            translateException(e);
        }
        return document;
    }

    @Override
    public void logAnnotation(String documentId, String principalId, String annotation) {
        DocumentRouteHeaderValue documentBo = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        try {
            KEWServiceLocator.getWorkflowDocumentService().logDocumentAction(principalId, documentBo, annotation);
        } catch (WorkflowException e) {
            translateException(e);
        }
    }

    @Override
    public void initiateIndexing(String documentId) {
        // TODO ewestfal - THIS METHOD NEEDS JAVADOCS
        throw new UnsupportedOperationException("implement me!!!");
    }

    @Override
    public DocumentActionResult superUserBlanketApprove(DocumentActionParameters parameters,
            final boolean executePostProcessor) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().superUserApprove(principalId, documentBo,
                                annotation, executePostProcessor);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "SU Blanket Approve [principalId=" + principalId + ", documentId=" + documentId
                                + ", annotation=" + annotation + "]";
                    }
                });
    }

    @Override
    public DocumentActionResult superUserNodeApprove(DocumentActionParameters parameters,
            final boolean executePostProcessor, final String nodeName) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().superUserNodeApproveAction(principalId,
                                documentBo, nodeName, annotation, executePostProcessor);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "SU Node Approve Action [principalId=" + principalId + ", documentId=" + documentId
                                + ", nodeName=" + nodeName + ", annotation=" + annotation + "]";
                    }
                });

    }

    @Override
    public DocumentActionResult superUserTakeRequestedAction(DocumentActionParameters parameters,
            final boolean executePostProcessor, final String actionRequestId) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(
                                principalId, documentBo, Long.valueOf(actionRequestId), annotation,
                                executePostProcessor);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "SU Take Requested Action [principalId=" + principalId + ", docume tId=" + documentId
                                + ", actionRequestId=" + actionRequestId + ", annotation=" + annotation + "]";
                    }
                });
    }

    @Override
    public DocumentActionResult superUserDisapprove(DocumentActionParameters parameters,
            final boolean executePostProcessor) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().superUserDisapproveAction(principalId,
                                documentBo, annotation, executePostProcessor);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "SU Disapprove [principalId=" + principalId + ", documentId=" + documentId
                                + ", annotation=" + annotation + "]";
                    }
                });
    }

    @Override
    public DocumentActionResult superUserCancel(DocumentActionParameters parameters, final boolean executePostProcessor) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().superUserCancelAction(principalId,
                                documentBo, annotation, executePostProcessor);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "SU Cancel [principalId=" + principalId + ", documentId=" + documentId + ", annotation="
                                + annotation + "]";
                    }
                });
    }

    @Override
    public DocumentActionResult superUserReturnToPreviousNode(DocumentActionParameters parameters,
            final boolean executePostProcessor, final ReturnPoint returnPoint) {
        return executeActionInternal(parameters,
                new DocumentActionCallback() {
                    public DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo,
                            String principalId, String annotation) throws WorkflowException {
                        return KEWServiceLocator.getWorkflowDocumentService().superUserReturnDocumentToPreviousNode(
                                principalId, documentBo, returnPoint.getNodeName(), annotation, executePostProcessor);
                    }

                    public String getLogMessage(String documentId, String principalId, String annotation) {
                        return "SU Return to Previous Node [principalId=" + principalId + ", documentId=" + documentId
                                + ", annotation=" + annotation + ", returnPoint=" + returnPoint + "]";
                    }
                });

    }

    @Override
    public DocumentActionResult placeInExceptionRouting(DocumentActionParameters parameters) {
        return executeActionInternal(parameters, PLACE_IN_EXCEPTION_CALLBACK);
    }

    @Override
    public List<WorkflowAttributeValidationError> validateWorkflowAttributeDefinition(
            WorkflowAttributeDefinition definition) {
        // TODO ewestfal - THIS METHOD NEEDS JAVADOCS
        throw new UnsupportedOperationException("implement me!!!");
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
     * TODO - this code is temporary until we get rid of all the crazy throwing of
     * "WorkflowException"
     */
    private void translateException(WorkflowException e) {
        if (e instanceof org.kuali.rice.kew.exception.InvalidActionTakenException) {
            throw new InvalidActionTakenException(e.getMessage(), e);
        }
        throw new WorkflowRuntimeException(e.getMessage(), e);
    }

    protected DocumentActionResult executeActionInternal(DocumentActionParameters parameters,
            DocumentActionCallback callback) {
        if (parameters == null) {
            throw new RiceIllegalArgumentException("Document action parameters was null.");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(callback.getLogMessage(parameters.getDocumentId(), parameters.getPrincipalId(),
                    parameters.getAnnotation()));
        }
        DocumentRouteHeaderValue documentBo = init(parameters);
        try {
            documentBo = callback.doInDocumentBo(documentBo, parameters.getPrincipalId(), parameters.getAnnotation());
        } catch (WorkflowException e) {
            // TODO fix this up once the checked exception goes away
            translateException(e);
        }
        return constructDocumentActionResult(documentBo, parameters.getPrincipalId());
    }

    protected static interface DocumentActionCallback {

        DocumentRouteHeaderValue doInDocumentBo(DocumentRouteHeaderValue documentBo, String principalId,
                String annotation) throws WorkflowException;

        String getLogMessage(String documentId, String principalId, String annotation);

    }

    protected static abstract class StandardDocumentActionCallback implements DocumentActionCallback {

        public final String getLogMessage(String documentId, String principalId, String annotation) {
            return getActionName() + " [principalId=" + principalId + ", documentId=" + documentId + ", annotation="
                    + annotation + "]";
        }

        protected abstract String getActionName();

    }

}
