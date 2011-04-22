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
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This is a description of what this class does - mpham don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class DocumentTypePermissionTypeServiceImpl extends KimPermissionTypeServiceBase {
	protected transient DocumentTypeService documentTypeService;
	
	{
		requiredAttributes.add( KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME );
		checkRequiredAttributes = true;
	}
	/**
	 * Loops over the given permissions and returns the most specific permission that matches.
	 * 
	 * That is, if a permission exists for the document type, then the permission for any 
	 * parent document will not be considered/returned.
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.core.util.AttributeSet, java.util.List)
	 */
	@Override
	protected List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		// pull all the potential parent doc type names from the permission list
		Set<String> permissionDocTypeNames = new HashSet<String>( permissionsList.size() );
		for ( KimPermissionInfo kpi : permissionsList ) {
			String docTypeName = kpi.getDetails().get( KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME );
			if ( StringUtils.isNotBlank( docTypeName ) ) {
				permissionDocTypeNames.add( docTypeName );
			}
		}
		// find the parent documents which match
		DocumentType docType = getDocumentTypeService().findByName(requestedDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
		String matchingDocTypeName = getClosestParentDocumentTypeName(docType, permissionDocTypeNames);
		// re-loop over the permissions and build a new list of the ones which have the
		// matching document type names in their details
		List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
		for ( KimPermissionInfo kpi : permissionsList ) {
			String docTypeName = kpi.getDetails().get( KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME );
			// only allow a match on the "*" type if no matching document types were found
			if((StringUtils.isEmpty(matchingDocTypeName) && StringUtils.equals(docTypeName,"*")) 
				|| (StringUtils.isNotEmpty(matchingDocTypeName) && matchingDocTypeName.equals(docTypeName))) {
				matchingPermissions.add( kpi );
			}
		}

		return matchingPermissions;
	}
	
	protected DocumentTypeService getDocumentTypeService() {
		if ( documentTypeService == null ) {
			documentTypeService = KEWServiceLocator.getDocumentTypeService();
		}
		return this.documentTypeService;
	}

}
