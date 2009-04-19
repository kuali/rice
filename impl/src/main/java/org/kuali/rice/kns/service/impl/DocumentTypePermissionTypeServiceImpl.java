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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

/**
 * This is a description of what this class does - mpham don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class DocumentTypePermissionTypeServiceImpl extends KimPermissionTypeServiceBase {
	DocumentTypeService documentTypeService;

	/**
	 * Loops over the given permissions and returns the most specific permission that matches.
	 * 
	 * That is, if a permission exists for the document type, then the permission for any 
	 * parent document will not be considered/returned.
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
	 */
	@Override
	public List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails,
			List<KimPermissionInfo> permissionsList) {
		// pull all the potential parent doc type names from the permission list
		List<String> permissionDocTypeNames = new ArrayList<String>( permissionsList.size() );
		for ( KimPermissionInfo kpi : permissionsList ) {
			String docTypeName = kpi.getDetails().get( KimAttributes.DOCUMENT_TYPE_NAME );
			if ( StringUtils.isNotBlank( docTypeName ) ) {
				permissionDocTypeNames.add( docTypeName );
			}
		}
		// find the parent documents which match
		DocumentType docType = getDocumentTypeService().findByName(requestedDetails.get(KimAttributes.DOCUMENT_TYPE_NAME));
		List<String> matchingDocTypeNames = isParentDocument( docType, permissionDocTypeNames, null );
		// re-loop over the permissions and build a new list of the ones which have the
		// matching document type names in their details
		List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>( matchingDocTypeNames.size() );
		for ( KimPermissionInfo kpi : permissionsList ) {
			String docTypeName = kpi.getDetails().get( KimAttributes.DOCUMENT_TYPE_NAME );
			// only allow a match on the "*" type if no matching document types were found
			if( (matchingDocTypeNames.isEmpty() && StringUtils.equals(docTypeName,"*")) 
					|| matchingDocTypeNames.contains( docTypeName ) ) {
				matchingPermissions.add( kpi );
			}
		}

		return matchingPermissions;
	}
	
	/**
	 * 
	 * This method traverses the document type hierarchy
	 * 
	 * @param currentDocType
	 * @param parentDocTypeName
	 * @return
	 */
	private List<String> isParentDocument(DocumentType currentDocType,
			List<String> parentDocTypeNames, List<String> matchingParentDocTypeNames ) {
		if ( matchingParentDocTypeNames == null ) {
			matchingParentDocTypeNames = new ArrayList<String>();
		}
		if (currentDocType != null) {
			if ( parentDocTypeNames.contains( currentDocType.getName() ) ) {
				matchingParentDocTypeNames.add( currentDocType.getName() );
				// a match has been found - abort - we only want to use the most specific permission
				return matchingParentDocTypeNames;
			} 
			if (currentDocType.getDocTypeParentId() != null
					&& !currentDocType.getDocumentTypeId().equals(
							currentDocType.getDocTypeParentId())) {
			    // there is a parent document type, recurse into that one
			    // above if statement prevents problems if there is a circular relationship
	            return isParentDocument(currentDocType.getParentDocType(),
	                    parentDocTypeNames, matchingParentDocTypeNames);
			}
		}
		return matchingParentDocTypeNames;
	}
	
	public DocumentTypeService getDocumentTypeService() {
		if ( documentTypeService == null ) {
			documentTypeService = KEWServiceLocator.getDocumentTypeService();
		}
		return this.documentTypeService;
	}

}
