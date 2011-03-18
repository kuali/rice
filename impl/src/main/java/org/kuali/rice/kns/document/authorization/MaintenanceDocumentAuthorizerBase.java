/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.document.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSUtils;

public class MaintenanceDocumentAuthorizerBase extends DocumentAuthorizerBase
		implements MaintenanceDocumentAuthorizer {
	// private static final org.apache.log4j.Logger LOG =
	// org.apache.log4j.Logger.getLogger(MaintenanceDocumentAuthorizerBase.class);

	transient protected static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

	@SuppressWarnings("unchecked")
	public final boolean canCreate(Class boClass, Person user) {
		AttributeSet permissionDetails = new AttributeSet();
		permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
				getMaintenanceDocumentDictionaryService().getDocumentTypeName(
						boClass));
		permissionDetails.put(KNSConstants.MAINTENANCE_ACTN,
				KNSConstants.MAINTENANCE_NEW_ACTION);
		return !permissionExistsByTemplate(KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
				permissionDetails)
				|| getIdentityManagementService()
						.isAuthorizedByTemplateName(
								user.getPrincipalId(),
								KNSConstants.KNS_NAMESPACE,
								KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
								permissionDetails, new AttributeSet());
	}

	@SuppressWarnings("unchecked")
	public final boolean canMaintain(BusinessObject businessObject, Person user) {
		Map<String, String> permissionDetails = new HashMap<String, String>(2);
		permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
				getMaintenanceDocumentDictionaryService().getDocumentTypeName(
						businessObject.getClass()));
		permissionDetails.put(KNSConstants.MAINTENANCE_ACTN,
				KNSConstants.MAINTENANCE_EDIT_ACTION);
		return !permissionExistsByTemplate(KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
				permissionDetails)
				|| isAuthorizedByTemplate(
						businessObject,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
						user.getPrincipalId(), permissionDetails, null);
	}

	public final boolean canCreateOrMaintain(
			MaintenanceDocument maintenanceDocument, Person user) {
		return !permissionExistsByTemplate(maintenanceDocument,
				KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS)
				|| isAuthorizedByTemplate(
						maintenanceDocument,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
						user.getPrincipalId());
	}

	public Set<String> getSecurePotentiallyHiddenSectionIds() {
		return new HashSet<String>();
	}

	public Set<String> getSecurePotentiallyReadOnlySectionIds() {
		return new HashSet<String>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
		super.addRoleQualification(businessObject, attributes);
		if (businessObject instanceof MaintenanceDocument) {
			MaintenanceDocument maintDoc = (MaintenanceDocument)businessObject;
			if ( maintDoc.getNewMaintainableObject() != null ) {			
				attributes.putAll(KNSUtils.getNamespaceAndComponentSimpleName(maintDoc.getNewMaintainableObject().getBoClass()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addPermissionDetails(BusinessObject businessObject, Map<String, String> attributes) {
		super.addPermissionDetails(businessObject, attributes);
		if (businessObject instanceof MaintenanceDocument) {
			MaintenanceDocument maintDoc = (MaintenanceDocument)businessObject;
			if ( maintDoc.getNewMaintainableObject() != null ) {			
				attributes.putAll(KNSUtils.getNamespaceAndComponentSimpleName(maintDoc.getNewMaintainableObject().getBoClass()));
				attributes.put(KNSConstants.MAINTENANCE_ACTN,maintDoc.getNewMaintainableObject().getMaintenanceAction());
			}
		}
	}

	protected final MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if (maintenanceDocumentDictionaryService == null) {
			maintenanceDocumentDictionaryService = KNSServiceLocatorWeb
					.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}

}
