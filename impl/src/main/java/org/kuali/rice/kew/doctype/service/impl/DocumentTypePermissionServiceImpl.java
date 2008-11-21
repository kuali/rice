/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.doctype.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.DocumentTypePolicyEnum;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypePermissionService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;

/**
 * Implementation of the DocumentTypePermissionService. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentTypePermissionServiceImpl implements DocumentTypePermissionService {
	
	public boolean canBlanketApprove(String principalId, DocumentType documentType, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);
		
		if (documentType.isBlanketApproveGroupDefined()) {
			boolean initiatorAuthorized = true;
			if (documentType.getInitiatorMustBlanketApprovePolicy().getPolicyValue()) {
				initiatorAuthorized = executeInitiatorPolicyCheck(principalId, initiatorPrincipalId, documentStatus);
			}
			return initiatorAuthorized && documentType.isBlanketApprover(principalId);
		}
		
		AttributeSet permissionDetails = buildDocumentTypePermissionDetails(documentType);
		return getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.BLANKET_APPROVE_PERMISSION, permissionDetails, new AttributeSet());
	}
	
	public boolean canReceiveAdHocRequest(String principalId, DocumentType documentType, String actionRequestType) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateActionRequestType(actionRequestType);
		
		AttributeSet permissionDetails = buildDocumentTypeActionRequestPermissionDetails(documentType, actionRequestType);
		if (getPermissionService().isPermissionAssignedForTemplateName(KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails)) {
			return getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails, new AttributeSet());
		}
		return true;
	}
	
	public boolean canGroupReceiveAdHocRequest(String groupId, DocumentType documentType, String actionRequestType) {
		validateGroupId(groupId);
		validateDocumentType(documentType);
		validateActionRequestType(actionRequestType);
		
		AttributeSet permissionDetails = buildDocumentTypeActionRequestPermissionDetails(documentType, actionRequestType);
		if (getPermissionService().isPermissionAssignedForTemplateName(KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails)) {
			List<String> principalIds = getIdentityManagementService().getGroupMemberPrincipalIds(groupId);
			for (String principalId : principalIds) {
				if (!getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails, new AttributeSet())) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean canAdministerRouting(String principalId, DocumentType documentType) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		
		if (documentType.isSuperUserGroupDefined()) {
			return documentType.isSuperUser(principalId);
		}
		
		AttributeSet permissionDetails = buildDocumentTypePermissionDetails(documentType);
		return getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.ADMINISTER_ROUTING_PERMISSION, permissionDetails, new AttributeSet());
	}
	
	public boolean canCancel(String principalId, DocumentType documentType, List<String> routeNodeNames, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateRouteNodeNames(routeNodeNames);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);

		if (!documentType.isPolicyDefined(DocumentTypePolicyEnum.INITIATOR_MUST_CANCEL)) {
			List<AttributeSet> permissionDetailList = buildDocumentTypePermissionDetails(documentType, routeNodeNames, documentStatus);
			boolean foundAtLeastOnePermission = false;
			// loop over permission details, only one of them needs to be authorized
			for (AttributeSet permissionDetails : permissionDetailList) {
				if (getPermissionService().isPermissionAssignedForTemplateName(KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.CANCEL_PERMISSION, permissionDetails)) {
					foundAtLeastOnePermission = true;
					if (getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.CANCEL_PERMISSION, permissionDetails, new AttributeSet())) {
						return true;
					}
				}
			}
			// if we found defined KIM permissions, but not of them have authorized this user, return false
			if (foundAtLeastOnePermission) {
				return false;
			}
		}
		
		if (documentType.getInitiatorMustCancelPolicy().getPolicyValue()) {
			executeInitiatorPolicyCheck(principalId, initiatorPrincipalId, documentStatus);
		}
		return true;
	}

	public boolean canInitiate(String principalId, DocumentType documentType) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		
		AttributeSet permissionDetails = buildDocumentTypePermissionDetails(documentType);
		if (getPermissionService().isPermissionAssignedForTemplateName(KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.INITIATE_PERMISSION, permissionDetails)) {
			return getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.INITIATE_PERMISSION, permissionDetails, new AttributeSet());
		}
		return true;
	}

	public boolean canRoute(String principalId,	DocumentType documentType, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);

		if (!documentType.isPolicyDefined(DocumentTypePolicyEnum.INITIATOR_MUST_ROUTE)) {
			AttributeSet permissionDetails = buildDocumentTypeDocumentStatusPermissionDetails(documentType, documentStatus);
			if (getPermissionService().isPermissionAssignedForTemplateName(KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.ROUTE_PERMISSION, permissionDetails)) {
				return getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.ROUTE_PERMISSION, permissionDetails, new AttributeSet());
			}
		}
			
		if (documentType.getInitiatorMustRoutePolicy().getPolicyValue()) {
			return executeInitiatorPolicyCheck(principalId, initiatorPrincipalId, documentStatus);
		}
		return true;
	}

	public boolean canSave(String principalId, DocumentType documentType, List<String> routeNodeNames, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateRouteNodeNames(routeNodeNames);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);

		if (!documentType.isPolicyDefined(DocumentTypePolicyEnum.INITIATOR_MUST_SAVE)) {
			List<AttributeSet> permissionDetailList = buildDocumentTypePermissionDetails(documentType, routeNodeNames, documentStatus);
			boolean foundAtLeastOnePermission = false;
			// loop over permission details, only one of them needs to be authorized
			for (AttributeSet permissionDetails : permissionDetailList) {
				if (getPermissionService().isPermissionAssignedForTemplateName(KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.SAVE_PERMISSION, permissionDetails)) {
					foundAtLeastOnePermission = true;
					if (getIdentityManagementService().isAuthorizedByTemplateName(principalId, KEWConstants.DEFAULT_KIM_NAMESPACE, KEWConstants.SAVE_PERMISSION, permissionDetails, new AttributeSet())) {
						return true;
					}
				}
			}
			// if we found defined KIM permissions, but not of them have authorized this user, return false
			if (foundAtLeastOnePermission) {
				return false;
			}
		}
		
		if (documentType.getInitiatorMustSavePolicy().getPolicyValue()) {
			return executeInitiatorPolicyCheck(principalId, initiatorPrincipalId, documentStatus);
		}
		return true;
	}

	protected AttributeSet buildDocumentTypePermissionDetails(DocumentType documentType) {
		AttributeSet details = new AttributeSet();
		details.put(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL, documentType.getName());
		return details;
	}
	
	protected AttributeSet buildDocumentTypeActionRequestPermissionDetails(DocumentType documentType, String actionRequestCode) {
		AttributeSet details = buildDocumentTypePermissionDetails(documentType);
		if (!StringUtils.isBlank(actionRequestCode)) {
			details.put(KEWConstants.ACTION_REQUEST_CD_DETAIL, actionRequestCode);
		}
		return details;
	}
	
	protected AttributeSet buildDocumentTypeDocumentStatusPermissionDetails(DocumentType documentType, String documentStatus) {
		AttributeSet details = buildDocumentTypePermissionDetails(documentType);
		if (!StringUtils.isBlank(documentStatus)) {
			details.put(KEWConstants.DOCUMENT_STATUS_DETAIL, documentStatus);
		}
		return details;
	}
	
	protected List<AttributeSet> buildDocumentTypePermissionDetails(DocumentType documentType, List<String> routeNodeNames, String documentStatus) {
		List<AttributeSet> detailList = new ArrayList<AttributeSet>();
		for (String routeNodeName : routeNodeNames) {
			AttributeSet details = buildDocumentTypePermissionDetails(documentType);
			if (!StringUtils.isBlank(routeNodeName)) {
				details.put(KEWConstants.ROUTE_NODE_NAME_DETAIL, routeNodeName);
			}
			if (!StringUtils.isBlank(documentStatus)) {
				details.put(KEWConstants.DOCUMENT_STATUS_DETAIL, documentStatus);
			}
			detailList.add(details);
		}
		return detailList;
	}
	
	private boolean executeInitiatorPolicyCheck(String principalId, String initiatorPrincipalId, String documentStatus) {
		return principalId.equals(initiatorPrincipalId) || !(KEWConstants.ROUTE_HEADER_SAVED_CD.equals(documentStatus) || KEWConstants.ROUTE_HEADER_INITIATED_CD.equals(documentStatus));
	}
	
	private void validatePrincipalId(String principalId) {
		if (StringUtils.isBlank(principalId)) {
			throw new IllegalArgumentException("Invalid principal ID, value was empty");
		}
	}
	
	private void validateGroupId(String groupId) {
		if (StringUtils.isBlank(groupId)) {
			throw new IllegalArgumentException("Invalid group ID, value was empty");
		}
	}
	
	private void validateDocumentType(DocumentType documentType) {
		if (documentType == null) {
			throw new IllegalArgumentException("DocumentType cannot be null");
		}
	}
	
	private void validateActionRequestType(String actionRequestType) {
		if (StringUtils.isBlank(actionRequestType)) {
			throw new IllegalArgumentException("Invalid action request type, value was empty");
		}
		if (!KEWConstants.ACTION_REQUEST_CODES.containsKey(actionRequestType)) {
			throw new IllegalArgumentException("Invalid action request type was given, value was: " + actionRequestType);
		}
	}
	
	private void validateRouteNodeNames(List<String> routeNodeNames) {
		if (routeNodeNames.isEmpty()) {
			throw new IllegalArgumentException("List of route node names was empty.");
		}
		for (String routeNodeName : routeNodeNames) {
			if (StringUtils.isBlank(routeNodeName)) {
				throw new IllegalArgumentException("List of route node names contained an invalid route node name, value was empty");
			}
		}
	}
	
	private void validateDocumentStatus(String documentStatus) {
		if (StringUtils.isBlank(documentStatus)) {
			throw new IllegalArgumentException("Invalid document status, value was empty");
		}
		if (!KEWConstants.DOCUMENT_STATUSES.containsKey(documentStatus)) {
			throw new IllegalArgumentException("Invalid document status was given, value was: " + documentStatus);
		}
	}
	
	protected IdentityManagementService getIdentityManagementService() {
		return KIMServiceLocator.getIdentityManagementService();
	}
	
	protected PermissionService getPermissionService() {
		return KIMServiceLocator.getPermissionService();
	}

}
