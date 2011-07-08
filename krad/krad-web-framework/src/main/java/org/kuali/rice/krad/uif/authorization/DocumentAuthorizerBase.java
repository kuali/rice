/*
 * Copyright 2011 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.krad.uif.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.doctype.Process;
import org.kuali.rice.kew.api.doctype.RoutePath;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentAuthorizerBase extends AuthorizerBase {
    protected static Log LOG = LogFactory.getLog(DocumentAuthorizerBase.class);

    public static final String PRE_ROUTING_ROUTE_NAME = "PreRoute";
    public static final String EDIT_MODE_DEFAULT_TRUE_VALUE = "TRUE";
    public static final String USER_SESSION_METHOD_TO_CALL_OBJECT_KEY = "METHOD_TO_CALL_KEYS_METHOD_OBJECT_KEY";
    public static final String USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY = "METHOD_TO_CALL_KEYS_COMPLETE_OBJECT_KEY";

    @Override
    public Set<String> getActionFlags(UifFormBase model, Person user, Set<String> documentActions) {
        Document document = ((DocumentFormBase) model).getDocument();

        if (LOG.isDebugEnabled()) {
            LOG.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '"
                    + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT)
                && !isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
                        KimConstants.PermissionTemplateNames.EDIT_DOCUMENT, user.getPrincipalId())) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_COPY)
                && !isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
                        KimConstants.PermissionTemplateNames.COPY_DOCUMENT, user.getPrincipalId())) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_COPY);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE)
                && !isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                        KimConstants.PermissionTemplateNames.BLANKET_APPROVE_DOCUMENT, user.getPrincipalId())) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_CANCEL)
                && !isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                        KimConstants.PermissionTemplateNames.CANCEL_DOCUMENT, user.getPrincipalId())) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_CANCEL);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_SAVE)
                && !isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                        KimConstants.PermissionTemplateNames.SAVE_DOCUMENT, user.getPrincipalId())) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_ROUTE)
                && !isAuthorizedByTemplate(document, KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
                        KimConstants.PermissionTemplateNames.ROUTE_DOCUMENT, user.getPrincipalId())) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ROUTE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE)
                && !canTakeRequestedAction(document, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_FYI)
                && !canTakeRequestedAction(document, KEWConstants.ACTION_REQUEST_FYI_REQ, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_FYI);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_APPROVE)
                || documentActions.contains(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE)) {
            if (!canTakeRequestedAction(document, KEWConstants.ACTION_REQUEST_APPROVE_REQ, user)) {
                documentActions.remove(KRADConstants.KUALI_ACTION_CAN_APPROVE);
                documentActions.remove(KRADConstants.KUALI_ACTION_CAN_DISAPPROVE);
            }
        }

        if (!canSendAnyTypeAdHocRequests(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ADD_ADHOC_REQUESTS);
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI)
                && !canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_FYI_REQ, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_SEND_NOTE_FYI);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_ANNOTATE)
                && !documentActions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_ANNOTATE);
        }

        if (documentActions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT__DOCUMENT_OVERVIEW)
                && !canEditDocumentOverview(document, user)) {
            documentActions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT__DOCUMENT_OVERVIEW);
        }

        return documentActions;
    }

    public final boolean canInitiate(String documentTypeName, Person user) {
		String nameSpaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
		Map<String, String> permissionDetails = new HashMap<String, String>();
		permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
				documentTypeName);
		return getPermissionService().isAuthorizedByTemplateName(
				user.getPrincipalId(), nameSpaceCode,
				KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT,
				permissionDetails, null);
	}

	public final boolean canReceiveAdHoc(Document document, Person user,
			String actionRequestCode) {
		Map<String,String> additionalPermissionDetails = new HashMap<String, String>();
		additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_REQUEST_CD, actionRequestCode);
		return isAuthorizedByTemplate(document,
				KRADConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
				KimConstants.PermissionTemplateNames.AD_HOC_REVIEW_DOCUMENT,
				user.getPrincipalId(), additionalPermissionDetails, null );
	}

	public final boolean canOpen(Document document, Person user) {
		return isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, user
						.getPrincipalId());
	}

	public final boolean canAddNoteAttachment(Document document,
			String attachmentTypeCode, Person user) {
		Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
		if (attachmentTypeCode != null) {
			additionalPermissionDetails.put(KimConstants.AttributeConstants.ATTACHMENT_TYPE_CODE,
					attachmentTypeCode);
		}
		return isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.ADD_NOTE_ATTACHMENT, user
						.getPrincipalId(), additionalPermissionDetails, null);
	}

	public final boolean canDeleteNoteAttachment(Document document,
			String attachmentTypeCode, String createdBySelfOnly, Person user) {
		Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
		if (attachmentTypeCode != null) {
			additionalPermissionDetails.put(KimConstants.AttributeConstants.ATTACHMENT_TYPE_CODE,
					attachmentTypeCode);
		}
		additionalPermissionDetails.put(KimConstants.AttributeConstants.CREATED_BY_SELF,
				createdBySelfOnly);
		return isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.DELETE_NOTE_ATTACHMENT,
				user.getPrincipalId(), additionalPermissionDetails, null);
	}

	public final boolean canViewNoteAttachment(Document document,
			String attachmentTypeCode, Person user) {
		Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
		if (attachmentTypeCode != null) {
			additionalPermissionDetails.put(KimConstants.AttributeConstants.ATTACHMENT_TYPE_CODE,
					attachmentTypeCode);
		}
		return isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.VIEW_NOTE_ATTACHMENT, user
						.getPrincipalId(), additionalPermissionDetails, null);
	}
	
	public final boolean canSendAdHocRequests(Document document,
			String actionRequestCd, Person user) {
		Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
		if (actionRequestCd != null) {
			additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_REQUEST_CD,
					actionRequestCd);
		}
		return isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.SEND_AD_HOC_REQUEST, user
						.getPrincipalId(), additionalPermissionDetails, null);
	}
	
	public boolean canEditDocumentOverview(Document document, Person user){
		return isAuthorizedByTemplate(document,
				KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.EDIT_DOCUMENT,
				user.getPrincipalId()) && this.isDocumentInitiator(document, user);
	}
	
	protected final boolean canSendAnyTypeAdHocRequests(Document document, Person user) {
		if (canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_FYI_REQ, user)) {
		    RoutePath routePath = KewApiServiceLocator.getDocumentTypeService().getRoutePathForDocumentTypeName(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
		    Process process = routePath.getPrimaryProcess();
		    if (process != null) {
		        if (process.getInitialRouteNode() == null) {
		            return false;
		        }
		    } else {
		        return false;
		    }
		    return true;
		} else if(canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, user)){
			return true;
		}
		return canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_APPROVE_REQ, user);
	}

	protected boolean canTakeRequestedAction(Document document,
			String actionRequestCode, Person user) {
		Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
		additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_REQUEST_CD,
				actionRequestCode);
		return isAuthorizedByTemplate(document, KRADConstants.KRAD_NAMESPACE,
				KimConstants.PermissionTemplateNames.TAKE_REQUESTED_ACTION,
				user.getPrincipalId(), additionalPermissionDetails, null);
	}

	@Override
	protected void addPermissionDetails(Object dataObject,
			Map<String, String> attributes) {
		super.addPermissionDetails(dataObject, attributes);
		if (dataObject instanceof Document) {
			addStandardAttributes((Document) dataObject, attributes);
		}
	}

	@Override
	protected void addRoleQualification(Object dataObject,
			Map<String, String> attributes) {
		super.addRoleQualification(dataObject, attributes);
		if (dataObject instanceof Document) {
			addStandardAttributes((Document) dataObject, attributes);
		}
	}

	protected void addStandardAttributes(Document document,
			Map<String, String> attributes) {
		WorkflowDocument wd = document.getDocumentHeader()
				.getWorkflowDocument();
		attributes.put(KimConstants.AttributeConstants.DOCUMENT_NUMBER, document
				.getDocumentNumber());
		attributes.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, wd.getDocumentTypeName());
		if (wd.isInitiated() || wd.isSaved()) {
			attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME,
					PRE_ROUTING_ROUTE_NAME);
		} else {
			attributes.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, KRADServiceLocatorWeb.getWorkflowDocumentService().getCurrentRouteNodeNames(wd));
		}
		attributes.put(KimConstants.AttributeConstants.ROUTE_STATUS_CODE, wd.getStatus().getCode());
	}
	
	protected boolean isDocumentInitiator(Document document, Person user) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return workflowDocument.getInitiatorPrincipalId().equalsIgnoreCase(user.getPrincipalId());
    }

}
