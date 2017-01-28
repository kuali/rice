/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.uif.view.RequestAuthorizationCache;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

import java.io.Serializable;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentPresentationControllerBase implements DocumentPresentationController, Serializable {
    private static final long serialVersionUID = -9181864754090276024L;

    private static transient ParameterService parameterService;

    private DocumentRequestAuthorizationCache documentRequestAuthorizationCache;

    public boolean canInitiate(String documentTypeName) {
        return true;
    }

    public boolean canEdit(Document document) {
        boolean canEdit = false;

        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        if (workflowDocumentInfo.isInitiated()
                || workflowDocumentInfo.isSaved()
                || workflowDocumentInfo.isEnroute()
                || workflowDocumentInfo.isException()) {
            canEdit = true;
        }

        return canEdit;
    }

    public boolean canAnnotate(Document document) {
        return true;
    }

    public boolean canReload(Document document) {
        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        return (canEdit(document) && !workflowDocumentInfo.isInitiated());

    }

    public boolean canClose(Document document) {
        return false;
    }

    public boolean canSave(Document document) {
        return canEdit(document);
    }

    public boolean canRoute(Document document) {
        boolean canRoute = false;

        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        if (workflowDocumentInfo.isInitiated() || workflowDocumentInfo.isSaved()) {
            canRoute = true;
        }

        return canRoute;
    }

    public boolean canCancel(Document document) {
        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        return workflowDocumentInfo.isValidAction(ActionType.CANCEL);
    }

    public boolean canRecall(Document document) {
        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        return workflowDocumentInfo.isEnroute();
    }

    public boolean canCopy(Document document) {
        boolean canCopy = false;
        if (document.getAllowsCopy()) {
            canCopy = true;
        }
        return canCopy;
    }

    @Override
    public boolean canPerformRouteReport(Document document) {
        return getParameterService().getParameterValueAsBoolean(KRADConstants.KNS_NAMESPACE,
                KRADConstants.DetailTypes.DOCUMENT_DETAIL_TYPE,
                KRADConstants.SystemGroupParameterNames.DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND);
    }

    public boolean canAddAdhocRequests(Document document) {
        return true;
    }

    public boolean canBlanketApprove(Document document) {
        // check system parameter - if Y, use default workflow behavior: allow a user with the permission
        // to perform the blanket approve action at any time
        Boolean allowBlanketApproveNoRequest = getParameterService().getParameterValueAsBoolean(
                KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_DETAIL_TYPE,
                KRADConstants.SystemGroupParameterNames.ALLOW_ENROUTE_BLANKET_APPROVE_WITHOUT_APPROVAL_REQUEST_IND);
        if (allowBlanketApproveNoRequest != null && allowBlanketApproveNoRequest.booleanValue()) {
            return canEdit(document);
        }

        // otherwise, limit the display of the blanket approve button to only the initiator of the document
        // (prior to routing)
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (canRoute(document) && StringUtils.equals(workflowDocument.getInitiatorPrincipalId(),
                GlobalVariables.getUserSession().getPrincipalId())) {
            return true;
        }

        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        // or to a user with an approval action request
        if (workflowDocumentInfo.isApprovalRequested()) {
            return true;
        }

        return false;
    }

    public boolean canApprove(Document document) {
        return !canComplete(document);
    }

    public boolean canDisapprove(Document document) {
        // most of the time, a person who can approve can disapprove
        return canApprove(document);
    }

    public boolean canSendAdhocRequests(Document document) {
        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        return !(workflowDocumentInfo.isInitiated() || workflowDocumentInfo.isSaved());
    }

    public boolean canSendNoteFyi(Document document) {
        return true;
    }

    public boolean canEditDocumentOverview(Document document) {
        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        return (workflowDocumentInfo.isInitiated() || workflowDocumentInfo.isSaved());
    }

    public boolean canFyi(Document document) {
        return true;
    }

    public boolean canAcknowledge(Document document) {
        return true;
    }

    public boolean canComplete(Document document) {
        DocumentRequestAuthorizationCache.WorkflowDocumentInfo workflowDocumentInfo =
                getDocumentRequestAuthorizationCache(document).getWorkflowDocumentInfo();

        boolean docInInit = workflowDocumentInfo.isInitiated() || workflowDocumentInfo.isSaved();
        boolean completionRequested = workflowDocumentInfo.isCompletionRequested();
        if (completionRequested && !docInInit) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSuperUserTakeAction(Document document) {
        return hasActionRequests(document) && canTakeAction(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSuperUserApprove(Document document) {
        return canApproveOrDisapprove(document);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSuperUserDisapprove(Document document) {
        return canApproveOrDisapprove(document);
    }

    /**
     * Returns whether the {@code document} has any APPROVE or COMPLETE action requests.
     *
     * @param document the document to check
     *
     * @return true if the {@code document} has any APPROVE or COMPLETE action requests, false otherwise
     */
    protected boolean hasActionRequests(Document document) {
        boolean hasActionRequests = false;

        for (ActionRequest actionRequest : document.getActionRequests()) {
            if  (StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.APPROVE.getCode())
                    || StringUtils.equals(actionRequest.getActionRequested().getCode(), ActionRequestType.COMPLETE.getCode())) {
                hasActionRequests = true;
                break;
            }
        }

        return hasActionRequests;
    }

    /**
     * Returns whether a super user action can be taken on the {@code document}.
     *
     * <p>
     * Typically, actions can only be taken on a document not in INITIATED, FINAL, or CANCELLED status.
     * </p>
     *
     * @param document the document to check
     *
     * @return true if a super user action can be taken on the {@code document}, false otherwise
     */
    protected boolean canTakeAction(Document document) {
        String documentNumber = document.getDocumentNumber();
        DocumentStatus status = KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(documentNumber);

        return !isStateInitiatedFinalCancelled(status);
    }

    /**
     * Returns whether a super user approve or disapprove action can be taken on the {@code document}.
     *
     * <p>
     * Typically, actions can only be taken on a document not in INITIATED, SAVED, PROCESSED, DISAPPROVED, FINAL, or
     * CANCELLED status.
     * </p>
     *
     * @param document the document to check
     * @return true if a super user approve or disapprove action can be taken on the {@code document}, false otherwise
     */
    protected boolean canApproveOrDisapprove(Document document) {
        boolean canComplete = canComplete(document);
        String documentNumber = document.getDocumentNumber();
        DocumentStatus status = KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatus(documentNumber);

        return !canComplete && !isStateInitiatedFinalCancelled(status) && !isStateSaved(status)
                && !isStateProcessedOrDisapproved(status);
    }

    /**
     * Returns whether the {@code document} is in a INITIATED, FINAL, or CANCELLED state.
     *
     * @param status the document status
     *
     * @return true if the {@code document} is in a INITIATED, FINAL, or CANCELLED state, false otherwise
     */
    protected boolean isStateInitiatedFinalCancelled(DocumentStatus status) {
        return (StringUtils.equals(status.getCode(), DocumentStatus.INITIATED.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.FINAL.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.CANCELED.getCode()));
    }

    /**
     * Returns whether the {@code document} is in a SAVED state.
     *
     * @param status the document status
     *
     * @return true if the {@code document} is in a SAVED state, false otherwise
     */
    protected boolean isStateSaved(DocumentStatus status) {
        return (StringUtils.equals(status.getCode(), DocumentStatus.SAVED.getCode()));
    }

    /**
     * Returns whether the {@code document} is in a PROCESSED or DISAPPROVED state.
     *
     * @param status the document status
     *
     * @return true if the {@code document} is in a PROCESSED or DISAPPROVED state, false otherwise
     */
    protected boolean isStateProcessedOrDisapproved(DocumentStatus status) {
        return (StringUtils.equals(status.getCode(), DocumentStatus.PROCESSED.getCode()) ||
                StringUtils.equals(status.getCode(), DocumentStatus.DISAPPROVED.getCode()));
    }

    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

    protected DocumentRequestAuthorizationCache getDocumentRequestAuthorizationCache(Document document) {
        if (this.documentRequestAuthorizationCache == null) {
            this.documentRequestAuthorizationCache = new DocumentRequestAuthorizationCache();
        }

        if (this.documentRequestAuthorizationCache.getWorkflowDocumentInfo() == null) {
            this.documentRequestAuthorizationCache.createWorkflowDocumentInfo(
                    document.getDocumentHeader().getWorkflowDocument());
        }

        return this.documentRequestAuthorizationCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDocumentRequestAuthorizationCache(
            DocumentRequestAuthorizationCache documentRequestAuthorizationCache) {
         this.documentRequestAuthorizationCache = documentRequestAuthorizationCache;
    }
}
