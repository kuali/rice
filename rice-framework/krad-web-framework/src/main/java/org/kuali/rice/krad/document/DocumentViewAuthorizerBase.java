/**
 * Copyright 2005-2014 The Kuali Foundation
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.UserSessionUtils;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizerBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.DocumentFormBase;

import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link org.kuali.rice.krad.uif.view.ViewAuthorizer} for
 * {@link org.kuali.rice.krad.uif.view.DocumentView} instances
 *
 * <p>
 * Performs KIM permission checks for the various document actions such as save, approve, cancel
 * </p>
 *
 * <p>
 * By default delegates to the {@link DocumentAuthorizer} configured for the document in the data dictionary
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentViewAuthorizerBase extends ViewAuthorizerBase implements DocumentAuthorizer {
    private static final long serialVersionUID = 3800780934223224565L;
    protected static Log LOG = LogFactory.getLog(DocumentViewAuthorizerBase.class);

    public static final String PRE_ROUTING_ROUTE_NAME = "PreRoute";

    private DocumentAuthorizer documentAuthorizer;

    private DocumentDictionaryService documentDictionaryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getActionFlags(View view, ViewModel model, Person user, Set<String> actions) {
        Document document = ((DocumentFormBase) model).getDocument();

        if (LOG.isDebugEnabled()) {
            LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '" + document
                    .getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && !canEdit(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_COPY) && !canCopy(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_COPY);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_CLOSE) && !canClose(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_CLOSE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_RELOAD) && !canReload(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_RELOAD);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE) && !canBlanketApprove(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_CANCEL) && !canCancel(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_RECALL) && !canRecall(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_RECALL);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_SAVE) && !canSave(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ROUTE) && !canRoute(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE) && !canAcknowledge(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_FYI) && !canFyi(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_APPROVE) && !canApprove(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_APPROVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE) && !canDisapprove(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
        }

        if (!canSendAnyTypeAdHocRequests(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI) && !canSendNoteFyi(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ANNOTATE) && !canAnnotate(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW) && !canEditDocumentOverview(
                document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT_DOCUMENT_OVERVIEW);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT) && !canPerformRouteReport(document,
                user)) {
            actions.remove(KRADConstants.KUALI_ACTION_PERFORM_ROUTE_REPORT);
        }

        return actions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canInitiate(String documentTypeName, Person user) {
        initializeDocumentAuthorizerIfNecessary(documentTypeName);

        return getDocumentAuthorizer().canInitiate(documentTypeName, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canOpen(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canOpen(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canOpenView(View view, ViewModel model, Person user) {
        DocumentFormBase documentForm = (DocumentFormBase) model;

        return super.canOpenView(view, model, user) && canOpen(documentForm.getDocument(), user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEdit(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canEdit(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEditView(View view, ViewModel model, Person user) {
        DocumentFormBase documentForm = (DocumentFormBase) model;

        return super.canEditView(view, model, user) && canEdit(documentForm.getDocument(), user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canUnmaskField(View view, ViewModel model, DataField field, String propertyName, Person user) {
        if (field.getDataFieldSecurity() == null) {
            return true;
        }

        // check mask authz flag is set
        AttributeSecurity attributeSecurity = field.getDataFieldSecurity().getAttributeSecurity();
        if (attributeSecurity == null || !attributeSecurity.isMask()) {
            return true;
        }

        // don't mask empty fields when user is the initiator (allows document creation when masked field exists)
        String fieldValue = ObjectPropertyUtils.getPropertyValue(model, field.getBindingInfo().getBindingPath());
        if (StringUtils.isBlank(fieldValue) && isInitiator(model, user)) {
            return true;
        }

        return super.canUnmaskField(view, model, field, propertyName, user);
    }

    /**
     * Checks if the user is the initiator for the current document
     *
     * @param model object containing the view data
     * @param user user we are authorizing
     * @return true if user is the initiator, false otherwise
     */
    protected boolean isInitiator(ViewModel model, Person user) {
        WorkflowDocument workflowDocument = UserSessionUtils.getWorkflowDocument(GlobalVariables.getUserSession(),
                ((DocumentFormBase) model).getDocument().getDocumentNumber());
        return StringUtils.equals(user.getPrincipalId(), workflowDocument.getInitiatorPrincipalId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAnnotate(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canAnnotate(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canReload(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canReload(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canClose(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canClose(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSave(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canSave(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRoute(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canRoute(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCancel(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canCancel(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canRecall(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canRecall(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canCopy(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canCopy(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canPerformRouteReport(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canPerformRouteReport(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBlanketApprove(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canBlanketApprove(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canApprove(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canApprove(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canDisapprove(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canDisapprove(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSendNoteFyi(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canSendNoteFyi(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canFyi(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canFyi(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAcknowledge(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canAcknowledge(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canReceiveAdHoc(document, user, actionRequestCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canAddNoteAttachment(document, attachmentTypeCode, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canDeleteNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier,
                user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canViewNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canViewNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier,
                user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canSendAdHocRequests(document, actionRequestCd, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEditDocumentOverview(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canEditDocumentOverview(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canSendAnyTypeAdHocRequests(Document document, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canSendAnyTypeAdHocRequests(document, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user) {
        initializeDocumentAuthorizerIfNecessary(document);

        return getDocumentAuthorizer().canTakeRequestedAction(document, actionRequestCode, user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addPermissionDetails(Object dataObject, Map<String, String> attributes) {
        super.addPermissionDetails(dataObject, attributes);

        if (dataObject instanceof Document) {
            addStandardAttributes((Document) dataObject, attributes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);

        if (dataObject instanceof Document) {
            addStandardAttributes((Document) dataObject, attributes);
        }
    }

    protected void addStandardAttributes(Document document, Map<String, String> attributes) {
        WorkflowDocument wd = document.getDocumentHeader().getWorkflowDocument();
        attributes.put(KimConstants.AttributeConstants.DOCUMENT_NUMBER, document.getDocumentNumber());
        attributes.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, wd.getDocumentTypeName());

        if (wd.isInitiated() || wd.isSaved()) {
            attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, PRE_ROUTING_ROUTE_NAME);
        } else {
            attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME,
                    KRADServiceLocatorWeb.getWorkflowDocumentService().getCurrentRouteNodeNames(wd));
        }

        attributes.put(KimConstants.AttributeConstants.ROUTE_STATUS_CODE, wd.getStatus().getCode());
    }

    protected boolean isDocumentInitiator(Document document, Person user) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return workflowDocument.getInitiatorPrincipalId().equalsIgnoreCase(user.getPrincipalId());
    }

    /**
     * If the document authorizer is null, gets the authorizer from the document dictionary service for the given
     * document's class.
     *
     * @param document document instance to get authorizer for
     */
    public void initializeDocumentAuthorizerIfNecessary(Document document) {
        if (documentAuthorizer == null) {
            DocumentEntry documentEntry = getDocumentDictionaryService().getDocumentEntryByClass(document.getClass());

            if (documentEntry == null) {
                throw new RuntimeException(
                        "Unable to find document entry for document class: " + document.getClass().getName());
            }

            setDocumentAuthorizerClass(documentEntry.getDocumentAuthorizerClass());
        }
    }

    /**
     * If the document authorizer is null, gets the authorizer from the document dictionary service for the given
     * document type name.
     *
     * @param documentTypeName document type to get authorizer for
     */
    public void initializeDocumentAuthorizerIfNecessary(String documentTypeName) {
        if (documentAuthorizer == null) {
            DocumentEntry documentEntry = getDocumentDictionaryService().getDocumentEntry(documentTypeName);

            if (documentEntry == null) {
                throw new RuntimeException(
                        "Unable to find document entry for document class: " + documentTypeName);
            }

            setDocumentAuthorizerClass(documentEntry.getDocumentAuthorizerClass());
        }
    }

    public DocumentAuthorizer getDocumentAuthorizer() {
        return documentAuthorizer;
    }

    public void setDocumentAuthorizer(DocumentAuthorizer documentAuthorizer) {
        this.documentAuthorizer = documentAuthorizer;
    }

    public void setDocumentAuthorizerClass(Class<? extends DocumentAuthorizer> documentAuthorizerClass) {
        this.documentAuthorizer = KRADUtils.createNewObjectFromClass(documentAuthorizerClass);
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }

        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
