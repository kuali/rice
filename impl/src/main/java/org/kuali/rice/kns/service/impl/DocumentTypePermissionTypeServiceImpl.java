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

import java.util.List;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - mpham don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class DocumentTypePermissionTypeServiceImpl extends KimPermissionTypeServiceBase {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#doPermissionDetailsMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
	 *      java.util.List)
	 */
	public <E extends KimPermission> boolean doPermissionDetailsMatch(AttributeSet requestedDetails,
			List<E> permissionsList) {
		for (KimPermission permission : permissionsList) {
			if (doesPermissionDetailMatch(requestedDetails, permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#doesPermissionDetailMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
	 *      KimPermission)
	 */
	public boolean doesPermissionDetailMatch(AttributeSet requestedDetails,
			KimPermission permission) {
		if (!requestedDetails.containsKey(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL)
				|| !permission.getDetails().containsKey(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL)){
			throw new RuntimeException("document type name is blank or null");
		}else if(requestedDetails.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL).equals("*")){
			return true;
		}
		DocumentType currentDocType = KEWServiceLocator
		.getDocumentTypeService().findByName(
				requestedDetails.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL));
		return checkPermissionDetailMatch(currentDocType, permission);
	}

	protected boolean checkPermissionDetailMatch(DocumentType currentDocType,
			KimPermission permissionDetails) {
		if (currentDocType != null) {
			if (permissionDetails.getDetails().get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL)
					.equalsIgnoreCase(currentDocType.getName())) {
				return true;
			} else if (currentDocType.getDocTypeParentId() != null
					&& !currentDocType.getDocumentTypeId().equals(
							currentDocType.getDocTypeParentId())) {
				return checkPermissionDetailMatch(currentDocType.getParentDocType(),
						permissionDetails);
			}
		}
		return false;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#getAllImpliedDetails(org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<AttributeSet> getAllImpliedDetails(AttributeSet requestedDetails) {
		// TODO mpham - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimPermissionTypeService#getAllImplyingDetails(org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<AttributeSet> getAllImplyingDetails(
			AttributeSet requestedDetails) {
		// TODO mpham - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getInquiryUrl(java.lang.String,
	 *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public String getInquiryUrl(String attributeName,
			AttributeSet relevantAttributeData) {
		// TODO mpham - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getLookupUrl(java.lang.String)
	 */
	public String getLookupUrl(String attributeName) {
		// TODO mpham - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#getWorkflowDocumentTypeName()
	 */
	public String getWorkflowDocumentTypeName() {
		// TODO mpham - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#validateAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public List<String> validateAttribute(String attributeName,
			String attributeValue) {
		// TODO mpham - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.support.KimTypeService#validateAttributes(org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public AttributeSet validateAttributes(AttributeSet attributes) {
		// TODO mpham - THIS METHOD NEEDS JAVADOCS
		return null;
	}

}
