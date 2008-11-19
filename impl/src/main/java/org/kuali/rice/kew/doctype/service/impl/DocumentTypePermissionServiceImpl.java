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

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypePermissionService;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * Implementation of the DocumentTypePermissionService. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentTypePermissionServiceImpl implements DocumentTypePermissionService {

	private static final String PERMISSION_NAMESPACE = "KR-WKFLW";
	
	private static final String DOCUMENT_TYPE_NAME_DETAIL = "name";
	
	private static final String BLANKET_APPROVE_PERMISSION = "Blanket Approve Document";
	
	public boolean isBlanketApprover(DocumentType documentType,	String principalId) {
		if (documentType.hasBlanketApproveDefined()) {
			return documentType.isBlanketApprover(principalId);
		}
		AttributeSet permissionDetails = buildDocumentTypePermissionDetails(documentType);
		return KIMServiceLocator.getIdentityManagementService().isAuthorized(principalId, PERMISSION_NAMESPACE, BLANKET_APPROVE_PERMISSION, permissionDetails, new AttributeSet());
	}
	
	protected AttributeSet buildDocumentTypePermissionDetails(DocumentType documentType) {
		AttributeSet details = new AttributeSet();
		details.put(DOCUMENT_TYPE_NAME_DETAIL, documentType.getName());
		return details;
	}

}
