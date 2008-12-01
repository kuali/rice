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
package org.kuali.rice.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentTypeAndNodeOrStatePermissionTypeService extends KimPermissionTypeServiceBase {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#doesPermissionDetailMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, KimPermission)
	 */
	@Override
	public boolean doesPermissionDetailMatch(AttributeSet requestedDetails, KimPermission permission) {
		/*if (!routeNodeMatches(requestedDetails, permissionDetails) || !routeStatusMatches(requestedDetails, permissionDetails)) {
			return false;
		}*/
		
		String requestedDocumentType = requestedDetails.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL);
		String permissionDetailDocumentType = permission.getDetails().get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL);
		if (StringUtils.isBlank(requestedDocumentType) || StringUtils.isBlank(permissionDetailDocumentType)) {
			throw new RuntimeException("Blank doc type found for DocumentTypeRouteNodeRouteStatusPermissionTypeServiceImpl");
		}
		if (!documentTypeMatches(requestedDocumentType, permissionDetailDocumentType)) {
			return false;
		}
		
		return true;
	}

	/**
	 * Returns whether the requestedDocumentType is a sub-document type of or equal to the permissionDetailDocumentType.  This
	 * method will also return true if permissionDetailDocumentType is "*".
	 * 
	 * @param requestedDocumentType the document type for which the system is trying to gain authorization for
	 * @param permissionDetailDocumentType the document type used to set up the permission in the KIM DB, can be "*" to signify
	 * that any document type will match.
	 * @return whether the two document types match, based
	 */
	protected boolean documentTypeMatches(String requestedDocumentType, String permissionDetailDocumentType) {
		if ("*".equals(permissionDetailDocumentType)) {
			return true;
		}
		DocumentType currentDocType = KEWServiceLocator.getDocumentTypeService().findByName(requestedDocumentType);
		return checkPermissionDetailMatch(currentDocType, permissionDetailDocumentType);
	}

	protected boolean checkPermissionDetailMatch(DocumentType currentDocType, String permissionDetailDocumentType) {
		if (currentDocType != null) {
			if (permissionDetailDocumentType.equalsIgnoreCase(currentDocType.getName())) {
				return true;
			} else if (currentDocType.getDocTypeParentId() != null && 
					!currentDocType.getDocumentTypeId().equals(currentDocType.getDocTypeParentId())) {
				return checkPermissionDetailMatch(currentDocType.getParentDocType(),
						permissionDetailDocumentType);
			}
		}
		return false;
	}
	
	protected boolean routeNodeMatches(AttributeSet requestedDetails, AttributeSet permissionDetails) {
		if (!requestedDetails.containsKey(KEWConstants.ROUTE_NODE_NAME_DETAIL)) {
			return true;
		}
		return StringUtils.equals(requestedDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL),
				permissionDetails.get(KEWConstants.ROUTE_NODE_NAME_DETAIL));
	}
	
	protected boolean routeStatusMatches(AttributeSet requestedDetails, AttributeSet permissionDetails) {
		if (!requestedDetails.containsKey(KEWConstants.DOCUMENT_STATUS_DETAIL)) {
			return true;
		}
		return StringUtils.equals(requestedDetails.get(KEWConstants.DOCUMENT_STATUS_DETAIL),
				permissionDetails.get(KEWConstants.DOCUMENT_STATUS_DETAIL));
	}
}
