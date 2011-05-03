/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.doctype.service;

import java.util.List;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;

/**
 * Implements permission checks related to Document Type.  In general,
 * these permission checks are invoked from the various actions
 * which require authorization.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentTypePermissionService {

	public static final String DOC_TYPE_PERM_CACHE_GROUP = "DocumentTypePerm";
	
	/**
	 * Determines if the given principal is authorized to receive ad hoc requests
	 * for the given DocumentType and action request type.
	 */
	public boolean canReceiveAdHocRequest(String principalId, DocumentType documentType, String actionRequestCode);

	/**
	 * Determines if the given group is authorized to receive ad hoc requests of the
	 * specified action request code for the given DocumentType and action request type.  
	 * A group is considered to be authorized to receive an ad hoc request if all
	 * of it's members can receive the request.
	 */
	public boolean canGroupReceiveAdHocRequest(String groupId, DocumentType documentType, String actionRequestCode);
	
	/**
	 * Determines if the given principal can administer routing for the given
	 * DocumentType.  Having this permission gives them "super user" capabilities.
	 */
	public boolean canAdministerRouting(String principalId, DocumentType documentType);

	/**
	 * Determines if the given principal can initiate documents of the given DocumentType.
	 */
	public boolean canInitiate(String principalId, DocumentType documentType);
	
	/**
	 * Determines if the given principal can route documents of the given DocumentRouteHeaderValue.  The permission check
	 * also considers the document status and initiator of the document.
	 */
	public boolean canRoute(String principalId, DocumentRouteHeaderValue documentRouteHeaderValue);
	
	/**
	 * Determines if the given principal can save documents of the given DocumentType.  The permission check
	 * also considers the document's current route nodes, document status, and initiator of the document.
	 * 
	 * <p>It is intended the only one of the given route nodes will need to satisfy the permission check.
	 * For example, if the save permission is defined for node 1 but not for node 2, then a document which
	 * is at both node 1 and node 2 should satisfy the permission check.
	 */
	public boolean canSave(String principalId, String documentId, DocumentType documentType, List<String> routeNodeNames, String documentStatus, String initiatorPrincipalId);

	/**
	 * Determines if the given principal can blanket approve documents of the given DocumentType.  The permission check
	 * also considers the document status and the initiator of the document.
	 */
	public boolean canBlanketApprove(String principalId, DocumentType documentType, String documentStatus, String initiatorPrincipalId);

	/**
	 * Determines if the given principal can cancel documents of the given DocumentType.  The permission check
	 * also considers the document's current route nodes, document status, and initiator of the document.
	 * 
	 * <p>It is intended the only one of the given route nodes will need to satisfy the permission check.
	 * For example, if the cancel permission is defined for node 1 but not for node 2, then a document which
	 * is at both node 1 and node 2 should satisfy the permission check.
	 */
	public boolean canCancel(String principalId, String documentId, DocumentType documentType, List<String> routeNodeNames, String documentStatus, String initiatorPrincipalId);
	
	/**
	 * Determines if the given principal can add route log messages for documents of the given DocumentRouteHeaderValue.  The permission check
	 * also considers the document status and initiator of the document.
	 */
	public boolean canAddRouteLogMessage(String principalId, DocumentRouteHeaderValue documentRouteHeaderValue);
	
}
