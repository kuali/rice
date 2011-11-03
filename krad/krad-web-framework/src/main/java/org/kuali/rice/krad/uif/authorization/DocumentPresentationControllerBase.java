/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentPresentationControllerBase extends PresentationControllerBase {

    private static transient ParameterService parameterService;

    @Override
    public Set<String> getActionFlags(UifFormBase model) {
        Set<String> documentActions = new HashSet<String>();
      
        Document document = ((DocumentFormBase) model).getDocument();

        if (canEdit(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        if (canAnnotate(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
        }

        if (canClose(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_CLOSE);
        }

        if (canSave(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }

        if (canRoute(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }

        if (canCancel(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }

        if (canReload(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_RELOAD);
        }

        if (canCopy(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_COPY);
        }

        if (canPerformRouteReport(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
        }

        if (canAddAdhocRequests(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
        }

        if (canBlanketApprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        if (canApprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_APPROVE);
        }

        if (canDisapprove(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
        }

        if (canSendAdhocRequests(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
        }

        if (canSendNoteFyi(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (this.canEditDocumentOverview(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT__DOCUMENT_OVERVIEW);
        }

        if (canFyi(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_FYI);
        }

        if (canAcknowledge(document)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        }

        return documentActions;
    }

    public boolean canInitiate(String documentTypeName) {
        return true;
    }

    /**
     * @param document
     * @return boolean (true if can edit the document)
     */
    protected boolean canEdit(Document document) {
        boolean canEdit = false;
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isEnroute()
                || workflowDocument.isException()) {
            canEdit = true;
        }

        return canEdit;
    }

    /**
     * @param document
     * @return boolean (true if can add notes to the document)
     */
    protected boolean canAnnotate(Document document) {
        return canEdit(document);
    }

    /**
     * @param document
     * @return boolean (true if can reload the document)
     */
    protected boolean canReload(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return (canEdit(document) && !workflowDocument.isInitiated());

    }

    /**
     * @param document
     * @return boolean (true if can close the document)
     */
    protected boolean canClose(Document document) {
        return true;
    }

    /**
     * @param document
     * @return boolean (true if can save the document)
     */
    protected boolean canSave(Document document) {
        return canEdit(document);
    }

    /**
     * @param document
     * @return boolean (true if can route the document)
     */
    protected boolean canRoute(Document document) {
        boolean canRoute = false;
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
            canRoute = true;
        }
        return canRoute;
    }

    /**
     * @param document
     * @return boolean (true if can cancel the document)
     */
    protected boolean canCancel(Document document) {
        return canEdit(document);
    }

    /**
     * @param document
     * @return boolean (true if can copy the document)
     */
    protected boolean canCopy(Document document) {
        boolean canCopy = false;
        if (document.getAllowsCopy()) {
            canCopy = true;
        }
        return canCopy;
    }

    /**
     * @param document
     * @return boolean (true if can perform route report)
     */
    protected boolean canPerformRouteReport(Document document) {
        return getParameterService().getParameterValueAsBoolean(KRADConstants.KRAD_NAMESPACE,
                KRADConstants.DetailTypes.DOCUMENT_DETAIL_TYPE,
                KRADConstants.SystemGroupParameterNames.DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND);
    }

    /**
     * @param document
     * @return boolean (true if can do ad hoc route)
     */
    protected boolean canAddAdhocRequests(Document document) {
        return true;
    }

    /**
     * This method ...
     * 
     * @param document
     * @return boolean (true if can blanket approve the document)
     */
    protected boolean canBlanketApprove(Document document) {
        // check system parameter - if Y, use default workflow behavior: allow a
        // user with the permission
        // to perform the blanket approve action at any time
        try {
            if (getParameterService().getParameterValueAsBoolean(KRADConstants.KRAD_NAMESPACE,
                    KRADConstants.DetailTypes.DOCUMENT_DETAIL_TYPE,
                    KRADConstants.SystemGroupParameterNames.ALLOW_ENROUTE_BLANKET_APPROVE_WITHOUT_APPROVAL_REQUEST_IND)) {
                return canEdit(document);
            }
        }
        catch (IllegalArgumentException ex) {
            // do nothing, the parameter does not exist and defaults to "N"
        }
        // otherwise, limit the display of the blanket approve button to only
        // the initiator of the document
        // (prior to routing)
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (canRoute(document)
                && StringUtils.equals(workflowDocument.getInitiatorPrincipalId(), GlobalVariables.getUserSession()
                        .getPrincipalId())) {
            return true;
        }
        // or to a user with an approval action request
        if (workflowDocument.isApprovalRequested()) {
            return true;
        }

        return false;
    }

    protected boolean canApprove(Document document) {
        return true;
    }

    protected boolean canDisapprove(Document document) {
        // most of the time, a person who can approve can disapprove
        return canApprove(document);
    }

    protected boolean canSendAdhocRequests(Document document) {
        WorkflowDocument kualiWorkflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return !(kualiWorkflowDocument.isInitiated() || kualiWorkflowDocument.isSaved());
    }

    protected boolean canSendNoteFyi(Document document) {
        return true;
    }

    protected boolean canEditDocumentOverview(Document document) {
        WorkflowDocument kualiWorkflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return (kualiWorkflowDocument.isInitiated() || kualiWorkflowDocument.isSaved());
    }

    protected boolean canFyi(Document document) {
        return true;
    }

    protected boolean canAcknowledge(Document document) {
        return true;
    }

    protected ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

}
