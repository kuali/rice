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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentTypeAndExistingRecordsOnlyPermissionTypeService extends
		DocumentTypePermissionTypeServiceImpl {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#doesPermissionDetailMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, KimPermission)
	 */
	@Override
	public boolean doesPermissionDetailMatch(AttributeSet requestedDetails,
			KimPermission permission) {
		if (!super.doesPermissionDetailMatch(requestedDetails, permission)) {
			return false;
		}
		String documentTypeName = requestedDetails.get(KEWConstants.DOCUMENT_TYPE_NAME_DETAIL);
		if (StringUtils.isNotBlank(documentTypeName)) {
			DocumentEntry docEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry(documentTypeName);
			if (docEntry instanceof MaintenanceDocumentEntry) {
				// this is a maint doc, we need to check whether the existing records only value matches
				String permissionDetailsExistingRecordsOnly = permission.getDetails().get(KimConstants.KIM_ATTRIB_EXISTING_RECORDS_ONLY);
				if (StringUtils.isNotBlank(permissionDetailsExistingRecordsOnly)) {
					String requestedDetailsExistingRecordsOnly = requestedDetails.get(KimConstants.KIM_ATTRIB_EXISTING_RECORDS_ONLY);
					return StringUtils.equals(requestedDetailsExistingRecordsOnly, permissionDetailsExistingRecordsOnly);
				}
			}
			return true;
		}
		else {
			throw new RuntimeException("Empty document type name");
		}
	}

	protected DataDictionaryService getDataDictionaryService() {
		return KNSServiceLocator.getDataDictionaryService();
	}
}
