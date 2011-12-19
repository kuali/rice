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
package org.kuali.rice.krad.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.doctype.ProcessDefinition;
import org.kuali.rice.kew.api.doctype.RoutePath;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewAuthorizerBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.web.form.DocumentFormBase;

import java.util.Collections;
import java.util.HashMap;
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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentViewAuthorizerBase extends ViewAuthorizerBase implements DocumentAuthorizer {
    private static final long serialVersionUID = 3800780934223224565L;

    protected static Log LOG = LogFactory.getLog(DocumentViewAuthorizerBase.class);

    public static final String PRE_ROUTING_ROUTE_NAME = "PreRoute";

    private DocumentAuthorizer documentAuthorizer;

    /**
     * @see org.kuali.rice.krad.uif.view.ViewAuthorizer#getActionFlags(org.kuali.rice.krad.uif.view.View,
     *      org.kuali.rice.krad.uif.view.ViewModel, org.kuali.rice.kim.api.identity.Person,
     *      java.util.Set<java.lang.String>)
     */
    @Override
    public Set<String> getActionFlags(View view, ViewModel model, Person user, Set<String> actions) {
        Document document = ((DocumentFormBase) model).getDocument();

        if (LOG.isDebugEnabled()) {
            LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '"
                    + document.getDocumentNumber()
                    + "'. user '"
                    + user.getPrincipalName()
                    + "'");
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && !isAuthorizedByTemplate(document,
                KRADConstants.KRAD_NAMESPACE, KimConstants.PermissionTemplateNames.EDIT_DOCUMENT,
                user.getPrincipalId())) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_COPY) && !isAuthorizedByTemplate(document,
                KRADConstants.KRAD_NAMESPACE, KimConstants.PermissionTemplateNames.COPY_DOCUMENT,
                user.getPrincipalId())) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_COPY);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE) && !isAuthorizedByTemplate(document,
                KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                KimConstants.PermissionTemplateNames.BLANKET_APPROVE_DOCUMENT, user.getPrincipalId())) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_CANCEL) && !isAuthorizedByTemplate(document,
                KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PermissionTemplateNames.CANCEL_DOCUMENT,
                user.getPrincipalId())) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_SAVE) && !isAuthorizedByTemplate(document,
                KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PermissionTemplateNames.SAVE_DOCUMENT,
                user.getPrincipalId())) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ROUTE) && !isAuthorizedByTemplate(document,
                KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KimConstants.PermissionTemplateNames.ROUTE_DOCUMENT,
                user.getPrincipalId())) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE) && !canTakeRequestedAction(document,
                KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_FYI) && !canTakeRequestedAction(document,
                KewApiConstants.ACTION_REQUEST_FYI_REQ, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_APPROVE) || actions.contains(
                KRADConstants.KUALI_ACTION_CAN_DISAPPROVE)) {
            if (!canTakeRequestedAction(document, KewApiConstants.ACTION_REQUEST_APPROVE_REQ, user)) {
                actions.remove(KRADConstants.KUALI_ACTION_CAN_APPROVE);
                actions.remove(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
            }
        }

        if (!canSendAnyTypeAdHocRequests(document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI) && !canSendAdHocRequests(document,
                KewApiConstants.ACTION_REQUEST_FYI_REQ, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_ANNOTATE) && !actions.contains(
                KRADConstants.KUALI_ACTION_CAN_EDIT)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
        }

        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT__DOCUMENT_OVERVIEW) && !canEditDocumentOverview(
                document, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT__DOCUMENT_OVERVIEW);
        }

        return actions;
    }

    public final boolean canInitiate(String documentTypeName, Person user) {
        return getDocumentAuthorizer().canInitiate(documentTypeName, user);
    }

    public final boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode) {
        return getDocumentAuthorizer().canReceiveAdHoc(document, user, actionRequestCode);
    }

    public final boolean canOpen(Document document, Person user) {
        return getDocumentAuthorizer().canOpen(document, user);
    }

    public final boolean canAddNoteAttachment(Document document, String attachmentTypeCode, Person user) {
        return getDocumentAuthorizer().canAddNoteAttachment(document, attachmentTypeCode, user);
    }

    public final boolean canDeleteNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user) {
        return getDocumentAuthorizer().canDeleteNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier,
                user);
    }

    public final boolean canViewNoteAttachment(Document document, String attachmentTypeCode,
            String authorUniversalIdentifier, Person user) {
        return getDocumentAuthorizer().canViewNoteAttachment(document, attachmentTypeCode, authorUniversalIdentifier,
                user);
    }

    public final boolean canSendAdHocRequests(Document document, String actionRequestCd, Person user) {
        return getDocumentAuthorizer().canSendAdHocRequests(document, actionRequestCd, user);
    }

    public boolean canEditDocumentOverview(Document document, Person user) {
        return getDocumentAuthorizer().canEditDocumentOverview(document, user);
    }

    public boolean canSendAnyTypeAdHocRequests(Document document, Person user) {
        return getDocumentAuthorizer().canSendAnyTypeAdHocRequests(document, user);
    }

    public boolean canTakeRequestedAction(Document document, String actionRequestCode, Person user) {
        return getDocumentAuthorizer().canTakeRequestedAction(document, actionRequestCode, user);
    }

    @Override
    protected void addPermissionDetails(Object dataObject, Map<String, String> attributes) {
        super.addPermissionDetails(dataObject, attributes);

        if (dataObject instanceof Document) {
            addStandardAttributes((Document) dataObject, attributes);
        }
    }

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

    protected DocumentAuthorizer getDocumentAuthorizer() {
        if (documentAuthorizer == null) {
            documentAuthorizer = new DocumentAuthorizerBase();
        }
        return documentAuthorizer;
    }

    public void setDocumentAuthorizer(DocumentAuthorizer documentAuthorizer) {
        this.documentAuthorizer = documentAuthorizer;
    }

    public void setDocumentAuthorizerClass(Class<? extends DocumentAuthorizer> documentAuthorizerClass) {
        this.documentAuthorizer = ObjectUtils.newInstance(documentAuthorizerClass);
    }
}
