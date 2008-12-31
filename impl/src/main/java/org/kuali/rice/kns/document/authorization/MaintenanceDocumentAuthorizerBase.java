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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.bo.FieldAttributeSecurity;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.DocumentAttributeSecurityUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.datadictionary.mask.MaskFormatter;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class MaintenanceDocumentAuthorizerBase extends DocumentAuthorizerBase
		implements MaintenanceDocumentAuthorizer {

	private static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

	public final void addMaintenanceDocumentRestrictions(
			MaintenanceDocumentAuthorizations auths,
			MaintenanceDocument document, Person user) {
		String documentType = document.getDocumentHeader()
				.getWorkflowDocument().getDocumentType();

		MaintenanceDocumentEntry objectEntry = getMaintenanceDocumentDictionaryService()
				.getMaintenanceDocumentEntry(documentType);
		Map<String, FieldAttributeSecurity> restrictionFields = DocumentAttributeSecurityUtils
				.getRestrictionMaintainableFields(objectEntry);

		Set<String> keys = restrictionFields.keySet();
		Iterator<String> keyIter = keys.iterator();

		while (keyIter.hasNext()) {
			String fullFieldName = keyIter.next();
			FieldAttributeSecurity fieldAttributeSecurity = restrictionFields
					.get(fullFieldName);
			String fieldName = fieldAttributeSecurity.getAttributeName();

			AttributeSecurity maintainableFieldAttributeSecurity = (AttributeSecurity) fieldAttributeSecurity
					.getMaintainableFieldAttributeSecurity();
			AttributeSecurity businessObjectAttributeSecurity = (AttributeSecurity) fieldAttributeSecurity
					.getBusinessObjectAttributeSecurity();

			Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
			additionalPermissionDetails.put(KimAttributes.PROPERTY_NAME,
					fieldName);

			if ((businessObjectAttributeSecurity != null && businessObjectAttributeSecurity
					.isReadOnly())
					|| (maintainableFieldAttributeSecurity != null && maintainableFieldAttributeSecurity
							.isReadOnly())) {
				if (!isAuthorizedByTemplate(document,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PERMISSION_EDIT_PROPERTY, user
								.getPrincipalId(), additionalPermissionDetails,
						null)) {
					auths.addReadonlyAuthField(fullFieldName);
				}
			}
			if ((businessObjectAttributeSecurity != null && businessObjectAttributeSecurity
					.isPartialMask())
					|| (maintainableFieldAttributeSecurity != null && maintainableFieldAttributeSecurity
							.isPartialMask())) {
				if (!isAuthorizedByTemplate(document,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PERMISSION_PARTIALLY_UNMASK_PROPERTY, user
								.getPrincipalId(), additionalPermissionDetails,
						null)) {
					MaskFormatter partialMaskFormatter = businessObjectAttributeSecurity
							.getPartialMaskFormatter();
					auths.addPartiallyMaskedAuthField(fullFieldName,
							partialMaskFormatter);
				}
			}
			if ((businessObjectAttributeSecurity != null && businessObjectAttributeSecurity
					.isMask())
					|| (maintainableFieldAttributeSecurity != null && maintainableFieldAttributeSecurity
							.isMask())) {
				if (!isAuthorizedByTemplate(document,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PERMISSION_UNMASK_PROPERTY, user
								.getPrincipalId(), additionalPermissionDetails,
						null)) {
					MaskFormatter maskFormatter = businessObjectAttributeSecurity
							.getMaskFormatter();
					auths.addMaskedAuthField(fullFieldName, maskFormatter);
				}
			}
			if ((businessObjectAttributeSecurity != null && businessObjectAttributeSecurity
					.isHide())
					|| (maintainableFieldAttributeSecurity != null && maintainableFieldAttributeSecurity
							.isPartialMask())) {
				if (!isAuthorizedByTemplate(document,
						KNSConstants.KNS_NAMESPACE,
						KimConstants.PERMISSION_VIEW_PROPERTY, user
								.getPrincipalId(), additionalPermissionDetails,
						null)) {
					auths.addHiddenAuthField(fullFieldName);
				}
			}
		}

		Set<String> hiddenSectionIds = getSecurePotentiallyHiddenSectionIds();
		for (String hiddenSectionId : hiddenSectionIds) {
			Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
			additionalPermissionDetails.put(KimAttributes.SECTION_ID,
					hiddenSectionId);
			if (!isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE,
					KimConstants.PERMISSION_VIEW_SECTION,
					user.getPrincipalId(), additionalPermissionDetails, null)) {
				auths.addHiddenSectionId(hiddenSectionId);
			}
		}
		Set<String> readOnlySectionIds = getSecurePotentiallyHiddenSectionIds();
		for (String readOnlySectionId : readOnlySectionIds) {
			Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
			additionalPermissionDetails.put(KimAttributes.SECTION_ID,
					readOnlySectionId);
			if (!isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE,
					KimConstants.PERMISSION_EDIT_SECTION,
					user.getPrincipalId(), additionalPermissionDetails, null)) {
				auths.addReadOnlySectionId(readOnlySectionId);
			}
		}
	}

	public Set<String> getDocumentActions(Document document, Person user,
			Set<String> documentActions) {
		Set docActions = super.getDocumentActions(document, user,
				documentActions);
		MaintenanceDocument maintDoc = (MaintenanceDocument) document;
		MaintenanceDocumentAuthorizations docAuths = KNSServiceLocator
				.getMaintenanceDocumentAuthorizationService()
				.generateMaintenanceDocumentAuthorizations(maintDoc,
						GlobalVariables.getUserSession().getPerson());
		if (docActions.contains(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE)
				&& docAuths.hasAnyFieldRestrictions()) {
			docActions.remove(KNSConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
		}
		return docActions;
	}

	public final boolean canCreate(Class boClass, Person user) {
		AttributeSet permissionDetails = new AttributeSet();
		permissionDetails.put(KimAttributes.DOCUMENT_TYPE_CODE,
				getMaintenanceDocumentDictionaryService().getDocumentTypeName(
						boClass));
		permissionDetails.put(KNSConstants.MAINTENANCE_ACTN,
				KNSConstants.MAINTENANCE_NEW_ACTION);
		return getIdentityManagementService().isAuthorizedByTemplateName(
				user.getPrincipalId(), KNSConstants.KNS_NAMESPACE,
				KimConstants.PERMISSION_CREATE_MAINTAIN_RECORDS,
				permissionDetails, null);
	}

	public final boolean canMaintain(Class boClass, Map primaryKeys, Person user) {
		Map<String, String> permissionDetails = new HashMap<String, String>();
		permissionDetails.put(KimAttributes.DOCUMENT_TYPE_CODE,
				getMaintenanceDocumentDictionaryService().getDocumentTypeName(
						boClass));
		permissionDetails.put(KNSConstants.MAINTENANCE_ACTN,
				KNSConstants.MAINTENANCE_EDIT_ACTION);
		return getIdentityManagementService().isAuthorizedByTemplateName(
				user.getPrincipalId(),
				KNSConstants.KNS_NAMESPACE,
				KimConstants.PERMISSION_CREATE_MAINTAIN_RECORDS,
				new AttributeSet(permissionDetails),
				new AttributeSet(
						getPreInitiationRoleQualificationForEditAction(boClass,
								primaryKeys)));
	}

	/**
	 * Override this method to retrieve the business object and obtain
	 * additional role qualification values to be used in the canMaintain check
	 * from it
	 */
	protected Map<String, String> getPreInitiationRoleQualificationForEditAction(
			Class boClass, Map primaryKeys) {
		return new HashMap<String, String>();
	}

	public final boolean canCreateOrMaintain(
			MaintenanceDocument maintenanceDocument, Person user) {
		return isAuthorizedByTemplate(maintenanceDocument, KNSConstants.KNS_NAMESPACE, KimConstants.PERMISSION_CREATE_MAINTAIN_RECORDS, user.getPrincipalId());
	}
	
	@Override
	protected void addPermissionDetails(BusinessObject businessObject,
			Map<String, String> attributes) {
		super.addPermissionDetails(businessObject, attributes);
		attributes.put(KNSConstants.MAINTENANCE_ACTN, ((MaintenanceDocument)businessObject).getNewMaintainableObject().getMaintenanceAction());
	}

	@Override
	protected String getComponentName(BusinessObject businessObject) {
		return super.getComponentName(((MaintenanceDocument) businessObject)
				.getNewMaintainableObject().getBusinessObject());
	}

	/**
	 * This method should indicate which sections of the document may need to be
	 * hidden based on the user. The framework will use this list to perform the
	 * permission checks.
	 * 
	 * @return Set of section ids that can be used to identify Sections that may
	 *         need to be hidden based on user permissions
	 */
	protected Set<String> getSecurePotentiallyHiddenSectionIds() {
		return new HashSet<String>();
	}

	/**
	 * This method should indicate which sections of the document may need to be
	 * unmodifiable based on the user. The framework will use this list to
	 * perform the permission checks.
	 * 
	 * @return Set of section ids that can be used to identify Sections that may
	 *         need to be read only based on user permissions
	 */
	protected Set<String> getSecurePotentiallyReadOnlySectionIds() {
		return new HashSet<String>();
	}

	protected static final MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if (maintenanceDocumentDictionaryService == null) {
			maintenanceDocumentDictionaryService = KNSServiceLocator
					.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}
}