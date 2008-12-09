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

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.KimPermission;
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

	protected List<String> requiredAttributes = new ArrayList<String>();
	{
		requiredAttributes.add(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL);
	}

	/***
	 * 
	 * This overridden method checks the document type hierarchy to match the permission details.
	 * 
	 * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.role.KimPermission)
	 */
	@Override
	protected boolean performPermissionMatch(AttributeSet requestedDetails, KimPermission permission) {
		validateRequiredAttributesAgainstReceived(requiredAttributes, requestedDetails);
		validateRequiredAttributesAgainstReceived(requiredAttributes, permission.getDetails());

		if(requestedDetails.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL).equals("*")){
			return true;
		}
		DocumentType currentDocType = KEWServiceLocator.getDocumentTypeService().findByName(
				requestedDetails.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL));
		return KimCommonUtils.checkPermissionDetailMatch(currentDocType, permission.getDetails());
	}

}
