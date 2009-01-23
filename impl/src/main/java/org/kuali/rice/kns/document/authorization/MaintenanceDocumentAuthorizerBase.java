/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.kns.document.authorization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.KNSConstants;

public class MaintenanceDocumentAuthorizerBase extends DocumentAuthorizerBase
		implements MaintenanceDocumentAuthorizer {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceDocumentAuthorizerBase.class);
	
	private static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
	private static PersistenceStructureService persistenceStructureService;

	public final boolean canCreate(Class boClass, Person user) {
		AttributeSet permissionDetails = new AttributeSet();
		permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
				getMaintenanceDocumentDictionaryService().getDocumentTypeName(
						boClass));
		permissionDetails.put(KNSConstants.MAINTENANCE_ACTN,
				KNSConstants.MAINTENANCE_NEW_ACTION);
		// check if permissions exist at all for this document and action
		// if not, then there are no restrictions and this action is allowed
		if ( !permissionExistsByTemplate(KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
				permissionDetails) ) {
			return true;
		}
		return getIdentityManagementService()
				.isAuthorizedByTemplateName(
						user.getPrincipalId(),
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
						permissionDetails, new AttributeSet());
		// FIXME: should not need to pass in an empty AttributeSet
	}

	@SuppressWarnings("unchecked")
	public final boolean canMaintain(BusinessObject businessObject, Person user) {
		Map<String, String> permissionDetails = new HashMap<String, String>( 2 );
		permissionDetails.put(KimAttributes.DOCUMENT_TYPE_NAME,
				getMaintenanceDocumentDictionaryService().getDocumentTypeName(
						businessObject.getClass()));
		permissionDetails.put(KNSConstants.MAINTENANCE_ACTN,
				KNSConstants.MAINTENANCE_EDIT_ACTION);
		// check if permissions exist at all for this document and action
		// if not, then there are no restrictions and this action is allowed
		if ( !permissionExistsByTemplate(KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
				permissionDetails) ) {
			return true;
		}
		List<String> pkFieldNames = getPersistenceStructureService().getPrimaryKeys( businessObject.getClass() );
		Map<String,String> additionalQualifiers = new HashMap<String,String>( pkFieldNames.size() );
		for ( String pkField : pkFieldNames ) {
			try {
				Object pkFieldValue = PropertyUtils.getSimpleProperty( businessObject, pkField );
				if ( pkFieldValue != null ) {
					additionalQualifiers.put( pkField, pkFieldValue.toString() );
				}
			} catch ( Exception ex ) {
				// do nothing, we don't care at this point
				if ( LOG.isDebugEnabled() ) {
					LOG.debug( "Unable to retrieve PK property (" + pkField + ") from: " + businessObject, ex );
				}
			}
		}
		return isAuthorizedByTemplate(
				businessObject,
				KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
				user.getPrincipalId(), permissionDetails, additionalQualifiers);
	}

	public final boolean canCreateOrMaintain(
			MaintenanceDocument maintenanceDocument, Person user) {
		return canCreate( maintenanceDocument.getNewMaintainableObject().getBoClass(), user )
				|| canMaintain( maintenanceDocument.getNewMaintainableObject().getBusinessObject(), user );
	}

	public Set<String> getSecurePotentiallyHiddenSectionIds() {
		return new HashSet<String>();
	}

	public Set<String> getSecurePotentiallyReadOnlySectionIds() {
		return new HashSet<String>();
	}

	@Override
	protected void addRoleQualification(BusinessObject businessObject,
			Map<String, String> attributes) {
		super.addRoleQualification(businessObject, attributes);
		if (businessObject instanceof MaintenanceDocument) {
			attributes
					.putAll(KimCommonUtils
							.getNamespaceAndComponentSimpleName(((MaintenanceDocument) businessObject)
									.getNewMaintainableObject()
									.getBusinessObject().getClass()));
		}
	}

	@Override
	protected void addPermissionDetails(BusinessObject businessObject,
			Map<String, String> attributes) {
		super.addPermissionDetails(businessObject, attributes);
		if (businessObject instanceof MaintenanceDocument) {
			attributes
					.putAll(KimCommonUtils
							.getNamespaceAndComponentSimpleName(((MaintenanceDocument) businessObject)
									.getNewMaintainableObject()
									.getBusinessObject().getClass()));
			attributes.put(KNSConstants.MAINTENANCE_ACTN,
					((MaintenanceDocument) businessObject)
							.getNewMaintainableObject().getMaintenanceAction());
		}
	}

	protected final MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if (maintenanceDocumentDictionaryService == null) {
			maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}

	protected final PersistenceStructureService getPersistenceStructureService() {
		if (persistenceStructureService == null) {
			persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
		}
		return persistenceStructureService;
	}
}