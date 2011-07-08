/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.doctype.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.doctype.DocumentTypePolicyEnum;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypePermissionService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.cache.RiceCacheAdministrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the DocumentTypePermissionService. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentTypePermissionServiceImpl implements DocumentTypePermissionService {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentTypePermissionServiceImpl.class);
	
	public static final String DOC_TYPE_PERM_CACHE_PREFIX = DOC_TYPE_PERM_CACHE_GROUP + ":";
	public static final String BLANKET_APPROVE_CACHE_PREFIX = DOC_TYPE_PERM_CACHE_PREFIX + "BlanketApprove:";
	public static final String PRINCIPAL_ADHOC_CACHE_PREFIX = DOC_TYPE_PERM_CACHE_PREFIX + "PrincipalAdhoc:";
	public static final String GROUP_ADHOC_CACHE_PREFIX = DOC_TYPE_PERM_CACHE_PREFIX + "GroupAdhoc:";
	public static final String ADMIN_ROUTING_CACHE_PREFIX = DOC_TYPE_PERM_CACHE_PREFIX + "AdminRouting:";
	public static final String CANCEL_CACHE_PREFIX = DOC_TYPE_PERM_CACHE_PREFIX + "Cancel:";
	
	private RiceCacheAdministrator cacheAdministrator;
	
	protected RiceCacheAdministrator getCacheAdministrator() {
		if ( cacheAdministrator == null ) {
			cacheAdministrator = KsbApiServiceLocator.getCacheAdministrator();
		}
		return cacheAdministrator;
	}
	
	public boolean canBlanketApprove(String principalId, DocumentType documentType, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);
		
		String cacheKey = buildBlanketApproveCacheKey(principalId, documentType, documentStatus, initiatorPrincipalId);
		Boolean result = (Boolean)getCacheAdministrator().getFromCache(cacheKey);
		if ( result == null ) {
			if (documentType.isBlanketApproveGroupDefined()) {
				boolean initiatorAuthorized = true;
				if (documentType.getInitiatorMustBlanketApprovePolicy().getPolicyValue()) {
					initiatorAuthorized = executeInitiatorPolicyCheck(principalId, initiatorPrincipalId, documentStatus);
				}
				result = initiatorAuthorized && documentType.isBlanketApprover(principalId);
			} else {		
				Map<String, String> permissionDetails = buildDocumentTypePermissionDetails(documentType);
				result = getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE, KEWConstants.BLANKET_APPROVE_PERMISSION, permissionDetails, new HashMap<String, String>());
			}
			getCacheAdministrator().putInCache(cacheKey, result, DOC_TYPE_PERM_CACHE_GROUP);
		}
		return result;
	}
	
	protected String buildBlanketApproveCacheKey( String principalId, DocumentType documentType, String documentStatus, String initiatorPrincipalId ) {
		return BLANKET_APPROVE_CACHE_PREFIX + documentType.getName() + "/" + documentStatus + "/" + principalId + "/" + initiatorPrincipalId;
	}
	
	public boolean canReceiveAdHocRequest(String principalId, DocumentType documentType, String actionRequestType) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateActionRequestType(actionRequestType);
		
		String cacheKey = buildPrincipalAdhocCacheKey(principalId, documentType, actionRequestType );
		Boolean result = (Boolean)getCacheAdministrator().getFromCache(cacheKey);
		
		if ( result == null ) {
			Map<String, String> permissionDetails = buildDocumentTypeActionRequestPermissionDetails(documentType, actionRequestType);
			if (useKimPermission(KEWConstants.KEW_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails)) {
				result = getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails, new HashMap<String, String>());
			} else {
				result = Boolean.TRUE;
			}
			getCacheAdministrator().putInCache(cacheKey, result, DOC_TYPE_PERM_CACHE_GROUP);
		}
		return result;
	}

	protected String buildPrincipalAdhocCacheKey( String principalId, DocumentType documentType, String actionRequestType ) {
		return PRINCIPAL_ADHOC_CACHE_PREFIX + documentType.getName() + "/" + actionRequestType + "/" + principalId;
	}
	
	public boolean canGroupReceiveAdHocRequest(String groupId, DocumentType documentType, String actionRequestType) {
		validateGroupId(groupId);
		validateDocumentType(documentType);
		validateActionRequestType(actionRequestType);
		
		String cacheKey = buildGroupAdhocCacheKey(groupId, documentType, actionRequestType );
		Boolean result = (Boolean)getCacheAdministrator().getFromCache(cacheKey);
		
		if ( result == null ) {
			result = Boolean.TRUE;
			Map<String, String> permissionDetails = buildDocumentTypeActionRequestPermissionDetails(documentType, actionRequestType);
			if (useKimPermission(KEWConstants.KEW_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails)) {
				List<String> principalIds = getGroupService().getMemberPrincipalIds(groupId);
				// if any member of the group is not allowed to receive the request, then the group may not receive it
				for (String principalId : principalIds) {
					if (!getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE, KEWConstants.AD_HOC_REVIEW_PERMISSION, permissionDetails, new HashMap<String, String>())) {
						result = Boolean.FALSE;
						break;
					}
				}
			}
			getCacheAdministrator().putInCache(cacheKey, result, DOC_TYPE_PERM_CACHE_GROUP);
		}
		return result;
	}

	protected String buildGroupAdhocCacheKey( String groupId, DocumentType documentType, String actionRequestType ) {
		return GROUP_ADHOC_CACHE_PREFIX + documentType.getName() + "/" + actionRequestType + "/" + groupId;
	}
	
	public boolean canAdministerRouting(String principalId, DocumentType documentType) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);

		String cacheKey = buildAdminRoutingCacheKey(principalId, documentType );
		Boolean result = (Boolean)getCacheAdministrator().getFromCache(cacheKey);
		
		if ( result == null ) {
			if (documentType.isSuperUserGroupDefined()) {
				result = documentType.isSuperUser(principalId);
			} else {			
				Map<String, String> permissionDetails = buildDocumentTypePermissionDetails(documentType);
				result = getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE, KEWConstants.ADMINISTER_ROUTING_PERMISSION, permissionDetails, new HashMap<String, String>());
			}
			getCacheAdministrator().putInCache(cacheKey, result, DOC_TYPE_PERM_CACHE_GROUP);
		}
		
		return result;
	}

	protected String buildAdminRoutingCacheKey( String principalId, DocumentType documentType ) {
		return ADMIN_ROUTING_CACHE_PREFIX + documentType.getName() + "/" + principalId;
	}
	
	public boolean canCancel(String principalId, String documentId, DocumentType documentType, List<String> routeNodeNames, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateRouteNodeNames(routeNodeNames);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);

			if (!documentType.isPolicyDefined(DocumentTypePolicyEnum.INITIATOR_MUST_CANCEL)) {
				List<Map<String, String>> permissionDetailList = buildDocumentTypePermissionDetails(documentType, routeNodeNames, documentStatus);

                boolean foundAtLeastOnePermission = false;
                // loop over permission details, only one of them needs to be authorized
                for (Map<String, String> permissionDetails : permissionDetailList) {
                    Map<String, String> roleQualifiers = buildDocumentIdRoleDocumentTypeDocumentStatusQualifiers(documentType, documentStatus, documentId, permissionDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL));
                    if (useKimPermission(KEWConstants.KEW_NAMESPACE, KEWConstants.CANCEL_PERMISSION, permissionDetails)) {
						foundAtLeastOnePermission = true;
					if (getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE, KEWConstants.CANCEL_PERMISSION, permissionDetails, roleQualifiers)) {
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
			return executeInitiatorPolicyCheck(principalId, initiatorPrincipalId, documentStatus);
			} else {
			return true;
			}			
	}
	
	public boolean canInitiate(String principalId, DocumentType documentType) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		
		Map<String, String> permissionDetails = buildDocumentTypePermissionDetails(documentType);
		if (useKimPermission(KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KEWConstants.INITIATE_PERMISSION, permissionDetails)) {
			return getPermissionService().isAuthorizedByTemplateName(principalId, KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, KEWConstants.INITIATE_PERMISSION, permissionDetails, new HashMap<String, String>());
    }
		return true;
	}

	public boolean canRoute(String principalId, DocumentRouteHeaderValue documentRouteHeaderValue) {
		return canRoute(principalId, documentRouteHeaderValue.getDocumentId(), documentRouteHeaderValue.getDocumentType(),
				documentRouteHeaderValue.getDocRouteStatus(), documentRouteHeaderValue.getInitiatorWorkflowId());
	}
	
	public boolean canRoute(String principalId,	String documentId, DocumentType documentType, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);

		if (!documentType.isPolicyDefined(DocumentTypePolicyEnum.INITIATOR_MUST_ROUTE)) {
			Map<String, String> permissionDetails = buildDocumentTypeDocumentStatusPermissionDetails(documentType, documentStatus);
			Map<String, String> roleQualifiers = buildDocumentIdRoleDocumentTypeDocumentStatusQualifiers(documentType, documentStatus, documentId, permissionDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL));
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Permission details values: " + permissionDetails);
				LOG.debug("Role qualifiers values: " + roleQualifiers);
			}
			if (useKimPermission(KEWConstants.KEW_NAMESPACE, KEWConstants.ROUTE_PERMISSION, permissionDetails)) {
				return getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE, KEWConstants.ROUTE_PERMISSION, permissionDetails, roleQualifiers);
			}
		}

		if (documentType.getInitiatorMustRoutePolicy().getPolicyValue()) {
			return executeInitiatorPolicyCheck(principalId, initiatorPrincipalId, documentStatus);
    }
		return true;
	}

	public boolean canAddRouteLogMessage(String principalId, DocumentRouteHeaderValue documentRouteHeaderValue) {
		return canAddRouteLogMessage(principalId, documentRouteHeaderValue.getDocumentId(),
				documentRouteHeaderValue.getDocumentType(), documentRouteHeaderValue.getDocRouteStatus(),
				documentRouteHeaderValue.getInitiatorWorkflowId());
	}

	public boolean canAddRouteLogMessage(String principalId, String documentId, DocumentType documentType,
			String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);

		Map<String, String> permissionDetails = buildDocumentTypeDocumentStatusPermissionDetails(documentType, documentStatus);
		Map<String, String> roleQualifiers = buildDocumentIdRoleDocumentTypeDocumentStatusQualifiers(documentType,
				documentStatus, documentId, permissionDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL));

		if (LOG.isDebugEnabled()) {
			LOG.debug("Permission details values: " + permissionDetails);
			LOG.debug("Role qualifiers values: " + roleQualifiers);
		}

		if (useKimPermission(KEWConstants.KEW_NAMESPACE, KEWConstants.ADD_MESSAGE_TO_ROUTE_LOG, permissionDetails)) {
			return getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE,
					KEWConstants.ADD_MESSAGE_TO_ROUTE_LOG, permissionDetails, roleQualifiers);
		}

		return false;
	}

	public boolean canSave(String principalId, String documentId, DocumentType documentType, List<String> routeNodeNames, String documentStatus, String initiatorPrincipalId) {
		validatePrincipalId(principalId);
		validateDocumentType(documentType);
		validateRouteNodeNames(routeNodeNames);
		validateDocumentStatus(documentStatus);
		validatePrincipalId(initiatorPrincipalId);

		if (!documentType.isPolicyDefined(DocumentTypePolicyEnum.INITIATOR_MUST_SAVE)) {
			List<Map<String, String>> permissionDetailList = buildDocumentTypePermissionDetails(documentType, routeNodeNames, documentStatus);

            boolean foundAtLeastOnePermission = false;
            // loop over permission details, only one of them needs to be authorized
            for (Map<String, String> permissionDetails : permissionDetailList) {
                Map<String, String> roleQualifiers = buildDocumentIdRoleDocumentTypeDocumentStatusQualifiers(documentType, documentStatus, documentId, permissionDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL));
                if (useKimPermission(KEWConstants.KEW_NAMESPACE, KEWConstants.SAVE_PERMISSION, permissionDetails)) {
					foundAtLeastOnePermission = true;
					if (getPermissionService().isAuthorizedByTemplateName(principalId, KEWConstants.KEW_NAMESPACE, KEWConstants.SAVE_PERMISSION, permissionDetails, roleQualifiers)) {
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

	protected Map<String, String> buildDocumentTypePermissionDetails(DocumentType documentType) {
		Map<String, String> details = new HashMap<String, String>();
		details.put(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL, documentType.getName());
		return details;
	}
	
	protected Map<String, String> buildDocumentTypeActionRequestPermissionDetails(DocumentType documentType, String actionRequestCode) {
		Map<String, String> details = buildDocumentTypePermissionDetails(documentType);
		if (!StringUtils.isBlank(actionRequestCode)) {
			details.put(KEWConstants.ACTION_REQUEST_CD_DETAIL, actionRequestCode);
		}
		return details;
	}
	
	protected Map<String, String> buildDocumentTypeDocumentStatusPermissionDetails(DocumentType documentType, String documentStatus) {
		Map<String, String> details = buildDocumentTypePermissionDetails(documentType);
		if (!StringUtils.isBlank(documentStatus)) {
			details.put(KEWConstants.DOCUMENT_STATUS_DETAIL, documentStatus);
		}
		return details;
	}
	
	protected Map<String, String> buildDocumentIdRoleDocumentTypeDocumentStatusQualifiers(DocumentType documentType, String documentStatus, String documentId, String routeNodeName) {
		Map<String, String> qualifiers = new HashMap<String, String>();
		qualifiers.put(KimConstants.AttributeConstants.DOCUMENT_NUMBER, documentId);
		if (!StringUtils.isBlank(documentStatus)) {
			qualifiers.put(KEWConstants.DOCUMENT_STATUS_DETAIL, documentStatus);
			if (KEWConstants.ROUTE_HEADER_INITIATED_CD.equals(documentStatus) || KEWConstants.ROUTE_HEADER_SAVED_CD.equals(documentStatus)) {
				qualifiers.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, DocumentAuthorizerBase.PRE_ROUTING_ROUTE_NAME);
			}
			else {
				qualifiers.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, routeNodeName);
			}
		}
		qualifiers.put(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL, documentType.getName());
		
		DocumentEntry documentEntry = KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDocumentEntry(documentType.getName());
		if (documentEntry != null) {
			Class<? extends Document> documentClass = documentEntry.getDocumentClass();
			String namespaceCode;
			if (MaintenanceDocument.class.isAssignableFrom(documentClass)) {
				MaintenanceDocumentEntry maintenanceDocumentEntry = (MaintenanceDocumentEntry) documentEntry;
				namespaceCode = KRADUtils.getNamespaceCode(maintenanceDocumentEntry.getDataObjectClass());
			}
			else {
				namespaceCode = KRADUtils.getNamespaceCode(documentClass);
			}
			qualifiers.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
		}
		
		return qualifiers;
	}
	
	/**
	 * This method generates the permission details for the given document.  Note that this has to match the required
	 * data defined in krim_typ_attr_t for the krim_typ_t with 
	 * srvc_nm='documentTypeAndNodeOrStatePermissionTypeService'.  
     * TODO: See KULRICE-3490, make assembly of permission details dynamic based on db config
	 * 
	 * @param documentType
	 * @param routeNodeNames
	 * @param documentStatus
	 * @return
	 */
	protected List<Map<String, String>> buildDocumentTypePermissionDetails(DocumentType documentType,
			List<String> routeNodeNames, String documentStatus) {
		List<Map<String, String>> detailList = new ArrayList<Map<String, String>>();

		for (String routeNodeName : routeNodeNames) {
			Map<String, String> details = buildDocumentTypePermissionDetails(documentType);
			if (KEWConstants.ROUTE_HEADER_INITIATED_CD.equals(documentStatus) || 
					KEWConstants.ROUTE_HEADER_SAVED_CD.equals(documentStatus)) {
				details.put(KEWConstants.ROUTE_NODE_NAME_DETAIL, DocumentAuthorizerBase.PRE_ROUTING_ROUTE_NAME);
			} else if (!StringUtils.isBlank(routeNodeName)) {
				details.put(KEWConstants.ROUTE_NODE_NAME_DETAIL, routeNodeName);
			}
			if (!StringUtils.isBlank(documentStatus)) {
				details.put(KEWConstants.DOCUMENT_STATUS_DETAIL, documentStatus);
			}
			if (null != documentType) {
				details.put(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL, documentType.getName());
			}
			detailList.add(details);
		}
		return detailList;
	}

	
	protected boolean useKimPermission(String namespace, String permissionTemplateName, Map<String, String> permissionDetails) {
		Boolean b =  CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.ALL_DETAIL_TYPE, KEWConstants.KIM_PRIORITY_ON_DOC_TYP_PERMS_IND);
		if (b == null || b) {
			return getPermissionService().isPermissionDefinedForTemplateName(namespace, permissionTemplateName, permissionDetails);
		}
		return false;
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
		    return;
			//throw new IllegalArgumentException("List of route node names was empty.");
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
	
	protected GroupService getGroupService() {
		return KimApiServiceLocator.getGroupService();
	}
	
	protected PermissionService getPermissionService() {
		return KimApiServiceLocator.getPermissionService();
	}

}
