/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.BusinessObjectAuthorizerBase;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.KualiWorkflowInfo;

/**
 * DocumentAuthorizer containing common, reusable document-level authorization code.
 */
/**
 * This is a description of what this class does - zjzhou don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class DocumentAuthorizerBase extends BusinessObjectAuthorizerBase
		implements DocumentAuthorizer {
	private static Log LOG = LogFactory.getLog(DocumentAuthorizerBase.class);
	private static final String PRE_ROUTING_ROUTE_NAME = "PreRoute";
	public static final String EDIT_MODE_DEFAULT_TRUE_VALUE = "TRUE";
	public static final String USER_SESSION_METHOD_TO_CALL_OBJECT_KEY = "METHOD_TO_CALL_KEYS_METHOD_OBJECT_KEY";
	public static final String USER_SESSION_METHOD_TO_CALL_COMPLETE_OBJECT_KEY = "METHOD_TO_CALL_KEYS_COMPLETE_OBJECT_KEY";

	private static KualiWorkflowInfo kualiWorkflowInfo;
	private static KualiConfigurationService kualiConfigurationService;

	/**
	 * Individual document families will need to reimplement this according to
	 * their own needs; this version should be good enough to be usable during
	 * initial development.
	 * 
	 * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.rice.kns.document.Document,
	 *      org.kuali.rice.kns.bo.user.KualiUser)
	 */
	public Set getDocumentActions(Document document, Person user,
			Set<String> documentActions) {
		if (LOG.isDebugEnabled()) {
			LOG
					.debug("calling DocumentAuthorizerBase.getDocumentActionFlags for document '"
							+ document.getDocumentNumber()
							+ "'. user '"
							+ user.getPrincipalName() + "'");
		}
		if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_EDIT)
				&& !isAuthorizedByTemplate(document,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PermissionTemplateNames.EDIT_DOCUMENT,
						user.getPrincipalId())) {
			documentActions.remove(KNSConstants.KUALI_ACTION_CAN_EDIT);
		}

		if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_COPY)
				&& !isAuthorizedByTemplate(document,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PermissionTemplateNames.COPY_DOCUMENT,
						user.getPrincipalId())) {
			documentActions.remove(KNSConstants.KUALI_ACTION_CAN_COPY);
		}

		if (documentActions
				.contains(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE)
				&& !isAuthorizedByTemplate(
						document,
						KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
						KimConstants.PermissionTemplateNames.BLANKET_APPROVE_DOCUMENT,
						user.getPrincipalId())) {
			documentActions
					.remove(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
		}

		if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_CANCEL)
				&& !isAuthorizedByTemplate(document,
						KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
						KimConstants.PermissionTemplateNames.CANCEL_DOCUMENT,
						user.getPrincipalId())) {
			documentActions.remove(KNSConstants.KUALI_ACTION_CAN_CANCEL);
		}

		if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_SAVE)
				&& !isAuthorizedByTemplate(document,
						KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
						KimConstants.PermissionTemplateNames.SAVE_DOCUMENT,
						user.getPrincipalId())) {
			documentActions.remove(KNSConstants.KUALI_ACTION_CAN_SAVE);
		}

		if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_ROUTE)
				&& !isAuthorizedByTemplate(document,
						KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
						KimConstants.PermissionTemplateNames.ROUTE_DOCUMENT,
						user.getPrincipalId())) {
			documentActions.remove(KNSConstants.KUALI_ACTION_CAN_ROUTE);
		}

		if (canAcknowledge(document, user)) {
			documentActions.add(KNSConstants.KUALI_ACTION_CAN_ACKNOWLEDGE);
		}

		if (canClearFYI(document, user)) {
			documentActions.add(KNSConstants.KUALI_ACTION_CAN_FYI);
		}

		if (documentActions
				.contains(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE)
				&& !documentActions
						.contains(KNSConstants.KUALI_ACTION_CAN_EDIT)) {
			documentActions.remove(KNSConstants.KUALI_ACTION_CAN_AD_HOC_ROUTE);
		}

		if (canApprove(document, user)) {
			documentActions.add(KNSConstants.KUALI_ACTION_CAN_APPROVE);
		} else {
			if (documentActions
					.contains(KNSConstants.KUALI_ACTION_CAN_DISAPPROVE)) {
				documentActions
						.remove(KNSConstants.KUALI_ACTION_CAN_DISAPPROVE);
			}
		}
		if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_ANNOTATE)
				&& !canAnnotate(document, user)) {
			documentActions.remove(KNSConstants.KUALI_ACTION_CAN_ANNOTATE);
		}
		return documentActions;
	}

	/**
	 * DocumentTypeAuthorizationException can be extended to customize the
	 * initiate error message
	 * 
	 * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canInitiate(java.lang.String,
	 *      org.kuali.rice.kns.bo.user.KualiUser)
	 */
	public final boolean canInitiate(String documentTypeName, Person user) {
		String nameSpaceCode = KNSConstants.KUALI_RICE_SYSTEM_NAMESPACE;
		AttributeSet permissionDetails = new AttributeSet();
		permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
				documentTypeName);
		return getIdentityManagementService().isAuthorizedByTemplateName(
				user.getPrincipalId(), nameSpaceCode,
				KimConstants.PermissionTemplateNames.INITIATE_DOCUMENT,
				permissionDetails, null);
	}

	public final boolean canReceiveAdHoc(Document document, Person user,
			String actionRequestCode) {
		return isAuthorizedByTemplate(document,
				KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE,
				KimConstants.PermissionTemplateNames.AD_HOC_REVIEW_DOCUMENT,
				user.getPrincipalId());
	}

	public final boolean canOpen(Document document, Person user) {
		return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, user
						.getPrincipalId());
	}

	private boolean canApprove(Document document, Person user) {
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if (permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD)
				&& KEWConstants.ACTION_REQUEST_APPROVE_REQ
						.equals(permissionDetails
								.get(KimAttributes.ACTION_REQUEST_CD))) {
			return canTakeRequestedAction(document, user);
		} else {
			return false;
		}
	}

	private boolean canClearFYI(Document document, Person user) {
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if (permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD)
				&& KEWConstants.ACTION_REQUEST_FYI_REQ.equals(permissionDetails
						.get(KimAttributes.ACTION_REQUEST_CD))) {
			return canTakeRequestedAction(document, user);
		} else {
			return false;
		}
	}

	private boolean canAcknowledge(Document document, Person user) {
		AttributeSet permissionDetails = getPermissionDetailValues(document);
		if (permissionDetails.containsKey(KimAttributes.ACTION_REQUEST_CD)
				&& KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ
						.equals(permissionDetails
								.get(KimAttributes.ACTION_REQUEST_CD))) {
			return canTakeRequestedAction(document, user);
		} else {
			return false;
		}
	}

	// TODO pretty sure this is wrong - annotate is a workflow thing
	private boolean canAnnotate(Document document, Person user) {
		return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.ADD_NOTE, user
						.getPrincipalId());
	}

	private boolean canTakeRequestedAction(Document document, Person user) {
		return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.TAKE_REQUESTED_ACTION,
				user.getPrincipalId());
	}
	
	@Override
	protected void addPermissionDetails(BusinessObject businessObject,
			Map<String, String> attributes) {
		super.addPermissionDetails(businessObject, attributes);
		if (businessObject instanceof Document) {
			addStandardAttributes((Document)businessObject, attributes);
		}
	}

	@Override
	protected void addRoleQualification(BusinessObject businessObject,
			Map<String, String> attributes) {
		super.addRoleQualification(businessObject, attributes);
		if (businessObject instanceof Document) {
			addStandardAttributes((Document)businessObject, attributes);
		}
	}

	private void addStandardAttributes(Document document,
			Map<String, String> attributes) {
		KualiWorkflowDocument wd = document.getDocumentHeader()
				.getWorkflowDocument();
		attributes.put(KimAttributes.DOCUMENT_NUMBER, document
				.getDocumentNumber());
		attributes.put(KimAttributes.DOCUMENT_TYPE_NAME, wd.getDocumentType());
		if (wd.stateIsInitiated() || wd.stateIsSaved()) {
			attributes.put(KimAttributes.ROUTE_NODE_NAME,
					PRE_ROUTING_ROUTE_NAME);
		} else {
			attributes.put(KimAttributes.ROUTE_NODE_NAME, wd
					.getCurrentRouteNodeNames());
		}
		attributes.put(KimAttributes.ROUTE_STATUS_CODE, wd.getRouteHeader()
				.getDocRouteStatus());

		if (wd.isApprovalRequested()) {
			attributes.put(KimAttributes.ACTION_REQUEST_CD,
					KEWConstants.ACTION_REQUEST_APPROVE_REQ);
		} else if (wd.isAcknowledgeRequested()) {
			attributes.put(KimAttributes.ACTION_REQUEST_CD,
					KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
		} else if (wd.isFYIRequested()) {
			attributes.put(KimAttributes.ACTION_REQUEST_CD,
					KEWConstants.ACTION_REQUEST_FYI_REQ);
		}
	}
}