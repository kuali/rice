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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.BusinessObjectAuthorizer;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictionsBase;
import org.kuali.rice.kns.authorization.InquiryOrMaintenanceDocumentAuthorizer;
import org.kuali.rice.kns.authorization.InquiryOrMaintenanceDocumentPresentationController;
import org.kuali.rice.kns.authorization.InquiryOrMaintenanceDocumentRestrictions;
import org.kuali.rice.kns.authorization.InquiryOrMaintenanceDocumentRestrictionsBase;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.InquiryCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationController;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictionsBase;
import org.kuali.rice.kns.inquiry.InquiryAuthorizer;
import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.KNSConstants;

public class BusinessObjectAuthorizationServiceImpl implements
		BusinessObjectAuthorizationService {
	private DataDictionaryService dataDictionaryService;
	private IdentityManagementService identityManagementService;
	private BusinessObjectDictionaryService businessObjectDictionaryService;
	private DocumentTypeService documentTypeService;
	private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

	public BusinessObjectRestrictions getLookupResultRestrictions(
			BusinessObject businessObject, Person user) {
		BusinessObjectRestrictions businessObjectRestrictions = new BusinessObjectRestrictionsBase();
		considerBusinessObjectFieldUnmaskAuthorization(businessObject, user,
				businessObjectRestrictions, "");
		return businessObjectRestrictions;
	}

	public InquiryRestrictions getInquiryRestrictions(
			BusinessObject businessObject, Person user) {
		InquiryRestrictions inquiryRestrictions = new InquiryOrMaintenanceDocumentRestrictionsBase();
		BusinessObjectEntry businessObjectEntry = getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						businessObject.getClass().getName());
		InquiryPresentationController inquiryPresentationController = getBusinessObjectDictionaryService()
				.getInquiryPresentationController(businessObject.getClass());
		InquiryAuthorizer inquiryAuthorizer = getBusinessObjectDictionaryService()
				.getInquiryAuthorizer(businessObject.getClass());
		considerBusinessObjectFieldUnmaskAuthorization(businessObject, user,
				inquiryRestrictions, "");
		considerBusinessObjectFieldViewAuthorization(businessObjectEntry,
				businessObject, user, inquiryAuthorizer, inquiryRestrictions,
				"");
		considerInquiryOrMaintenanceDocumentPresentationController(
				inquiryPresentationController, businessObject,
				inquiryRestrictions);
		considerInquiryOrMaintenanceDocumentAuthorizer(inquiryAuthorizer,
				businessObject, user, inquiryRestrictions);
		return inquiryRestrictions;
	}

	public MaintenanceDocumentRestrictions getMaintenanceDocumentRestrictions(
			MaintenanceDocument maintenanceDocument, Person user) {

		MaintenanceDocumentRestrictions maintenanceDocumentRestrictions = new MaintenanceDocumentRestrictionsBase();
		BusinessObjectEntry businessObjectEntry = getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						maintenanceDocument.getNewMaintainableObject()
								.getBusinessObject().getClass().getName());
		MaintenanceDocumentPresentationController maintenanceDocumentPresentationController = (MaintenanceDocumentPresentationController) getDocumentTypeService()
				.getDocumentPresentationController(maintenanceDocument);
		MaintenanceDocumentAuthorizer maintenanceDocumentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentTypeService()
				.getDocumentAuthorizer(maintenanceDocument);
		considerBusinessObjectFieldUnmaskAuthorization(maintenanceDocument
				.getNewMaintainableObject().getBusinessObject(), user,
				maintenanceDocumentRestrictions, "");
		considerBusinessObjectFieldViewAuthorization(businessObjectEntry,
				maintenanceDocument, user, maintenanceDocumentAuthorizer,
				maintenanceDocumentRestrictions, "");
		considerBusinessObjectFieldModifyAuthorization(businessObjectEntry,
				maintenanceDocument, user, maintenanceDocumentAuthorizer,
				maintenanceDocumentRestrictions, "");
		considerInquiryOrMaintenanceDocumentPresentationController(
				maintenanceDocumentPresentationController, maintenanceDocument,
				maintenanceDocumentRestrictions);
		considerInquiryOrMaintenanceDocumentAuthorizer(
				maintenanceDocumentAuthorizer, maintenanceDocument, user,
				maintenanceDocumentRestrictions);
		considerMaintenanceDocumentPresentationController(
				maintenanceDocumentPresentationController, maintenanceDocument,
				maintenanceDocumentRestrictions);
		considerMaintenanceDocumentAuthorizer(maintenanceDocumentAuthorizer,
				maintenanceDocument, user, maintenanceDocumentRestrictions);
		return maintenanceDocumentRestrictions;
	}

	protected void considerBusinessObjectFieldUnmaskAuthorization(
			BusinessObject businessObject, Person user,
			BusinessObjectRestrictions businessObjectRestrictions,
			String propertyPrefix) {
		BusinessObjectEntry businessObjectEntry = getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						businessObject.getClass().getName());
		for (String attributeName : businessObjectEntry.getAttributeNames()) {
			AttributeDefinition attributeDefinition = businessObjectEntry
					.getAttributeDefinition(attributeName);
			if (attributeDefinition.getAttributeSecurity() != null) {
				if (attributeDefinition.getAttributeSecurity().isMask()
						&& !canFullyUnmaskField(user,
								businessObject.getClass(), attributeName)) {
					businessObjectRestrictions.addFullyMaskedField(
							propertyPrefix + attributeName, attributeDefinition
									.getAttributeSecurity().getMaskFormatter());
				}
				if (attributeDefinition.getAttributeSecurity().isPartialMask()
						&& !canPartiallyUnmaskField(user, businessObject
								.getClass(), attributeName)) {
					businessObjectRestrictions.addPartiallyMaskedField(
							propertyPrefix + attributeName, attributeDefinition
									.getAttributeSecurity()
									.getPartialMaskFormatter());
				}
			}
		}
	}

	protected void considerBusinessObjectFieldViewAuthorization(
			BusinessObjectEntry businessObjectEntry,
			BusinessObject businessObject,
			Person user,
			BusinessObjectAuthorizer businessObjectAuthorizer,
			InquiryOrMaintenanceDocumentRestrictions inquiryOrMaintenanceDocumentRestrictions,
			String propertyPrefix) {
		for (String attributeName : businessObjectEntry.getAttributeNames()) {
			AttributeDefinition attributeDefinition = businessObjectEntry
					.getAttributeDefinition(attributeName);
			if (attributeDefinition.getAttributeSecurity() != null) {
				if (attributeDefinition.getAttributeSecurity().isHide()
						&& !businessObjectAuthorizer
								.isAuthorizedByTemplate(
										businessObject,
										KNSConstants.KNS_NAMESPACE,
										KimConstants.PermissionTemplateNames.VIEW_FIELD,
										user.getPrincipalId(),
										getFieldPermissionDetails(
												businessObject, attributeName),
										null)) {
					inquiryOrMaintenanceDocumentRestrictions
							.addHiddenField(propertyPrefix + attributeName);
				}
			}
		}
	}

	protected void considerBusinessObjectFieldModifyAuthorization(
			BusinessObjectEntry businessObjectEntry,
			BusinessObject businessObject, Person user,
			BusinessObjectAuthorizer businessObjectAuthorizer,
			MaintenanceDocumentRestrictions maintenanceDocumentRestrictions,
			String propertyPrefix) {
		for (String attributeName : businessObjectEntry.getAttributeNames()) {
			AttributeDefinition attributeDefinition = businessObjectEntry
					.getAttributeDefinition(attributeName);
			if (attributeDefinition.getAttributeSecurity() != null) {
				if (attributeDefinition.getAttributeSecurity().isReadOnly()
						&& !businessObjectAuthorizer
								.isAuthorizedByTemplate(
										businessObject,
										KNSConstants.KNS_NAMESPACE,
										KimConstants.PermissionTemplateNames.MODIFY_FIELD,
										user.getPrincipalId(),
										getFieldPermissionDetails(
												businessObject, attributeName),
										null)) {
					maintenanceDocumentRestrictions
							.addReadOnlyField(propertyPrefix + attributeName);
				}
			}
		}
	}

	protected void considerInquiryOrMaintenanceDocumentPresentationController(
			InquiryOrMaintenanceDocumentPresentationController businessObjectPresentationController,
			BusinessObject businessObject,
			InquiryOrMaintenanceDocumentRestrictions inquiryOrMaintenanceDocumentRestrictions) {
		for (String attributeName : businessObjectPresentationController
				.getConditionallyHiddenPropertyNames(businessObject)) {
			inquiryOrMaintenanceDocumentRestrictions
					.addHiddenField(attributeName);
		}
		for (String sectionId : businessObjectPresentationController
				.getConditionallyHiddenSectionIds(businessObject)) {
			inquiryOrMaintenanceDocumentRestrictions
					.addHiddenSectionId(sectionId);
		}
	}

	protected void considerInquiryOrMaintenanceDocumentAuthorizer(
			InquiryOrMaintenanceDocumentAuthorizer authorizer,
			BusinessObject businessObject, Person user,
			InquiryOrMaintenanceDocumentRestrictions restrictions) {
		for (String sectionId : authorizer
				.getSecurePotentiallyHiddenSectionIds()) {
			Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
			additionalPermissionDetails
					.put(KimAttributes.SECTION_ID, sectionId);
			if (!authorizer.isAuthorizedByTemplate(businessObject,
					KNSConstants.KNS_NAMESPACE,
					KimConstants.PermissionTemplateNames.VIEW_SECTION, user
							.getPrincipalId(), additionalPermissionDetails,
					null)) {
				restrictions.addHiddenSectionId(sectionId);
			}
		}
	}

	protected void considerMaintenanceDocumentPresentationController(
			MaintenanceDocumentPresentationController presentationController,
			MaintenanceDocument document,
			MaintenanceDocumentRestrictions restrictions) {
		for (String attributeName : presentationController
				.getConditionallyReadOnlyPropertyNames(document)) {
			restrictions.addReadOnlyField(attributeName);
		}
		for (String sectionId : presentationController
				.getConditionallyReadOnlySectionIds(document)) {
			restrictions.addReadOnlySectionId(sectionId);
		}
	}

	protected void considerMaintenanceDocumentAuthorizer(
			MaintenanceDocumentAuthorizer authorizer,
			MaintenanceDocument document, Person user,
			MaintenanceDocumentRestrictions restrictions) {
		for (String sectionId : authorizer
				.getSecurePotentiallyReadOnlySectionIds()) {
			Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
			additionalPermissionDetails
					.put(KimAttributes.SECTION_ID, sectionId);
			if (!authorizer.isAuthorizedByTemplate(document,
					KNSConstants.KNS_NAMESPACE,
					KimConstants.PermissionTemplateNames.MODIFY_SECTION, user
							.getPrincipalId(), additionalPermissionDetails,
					null)) {
				restrictions.addReadOnlySectionId(sectionId);
			}
		}
	}

	protected void addInquirableItemRestrictions(List itemDefinitions,
			InquiryAuthorizer authorizer, InquiryRestrictions restrictions,
			BusinessObject primaryBusinessObject,
			BusinessObject businessObject, String propertyPrefix, Person user) {
		BusinessObjectEntry businessObjectEntry = getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						businessObject.getClass().getName());
		for (Object inquirableItemDefinition : itemDefinitions) {
			if (inquirableItemDefinition instanceof InquiryCollectionDefinition) {
				InquiryCollectionDefinition inquiryCollectionDefinition = (InquiryCollectionDefinition) inquirableItemDefinition;
				try {
					Collection collection = (Collection) PropertyUtils
							.getProperty(businessObject,
									inquiryCollectionDefinition.getName());
					for (Iterator iterator = collection.iterator(); iterator
							.hasNext();) {
						BusinessObject collectionBusinessObject = (BusinessObject) iterator
								.next();
						considerBusinessObjectFieldUnmaskAuthorization(
								collectionBusinessObject, user, restrictions,
								propertyPrefix + "."
										+ inquiryCollectionDefinition.getName());
						considerBusinessObjectFieldViewAuthorization(
								businessObjectEntry, primaryBusinessObject,
								user, authorizer, restrictions, propertyPrefix
										+ "."
										+ inquiryCollectionDefinition.getName());
						addInquirableItemRestrictions(
								inquiryCollectionDefinition
										.getInquiryCollections(),
								authorizer,
								restrictions,
								primaryBusinessObject,
								collectionBusinessObject,
								propertyPrefix + "."
										+ inquiryCollectionDefinition.getName(),
								user);
					}
				} catch (Exception e) {
					throw new RuntimeException(
							"Unable to resolve collection property: "
									+ businessObject.getClass() + ":"
									+ inquiryCollectionDefinition.getName(), e);
				}
			}
		}

	}

	protected void addMaintainableItemRestrictions(List itemDefinitions,
			MaintenanceDocumentAuthorizer authorizer,
			MaintenanceDocumentRestrictions restrictions,
			MaintenanceDocument maintenanceDocument,
			BusinessObject businessObject, String propertyPrefix, Person user) {
		BusinessObjectEntry businessObjectEntry = getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						businessObject.getClass().getName());
		for (MaintainableItemDefinition maintainableItemDefinition : (List<MaintainableItemDefinition>) itemDefinitions) {
			if ((maintainableItemDefinition instanceof MaintainableFieldDefinition)
					&& ((MaintainableFieldDefinition) maintainableItemDefinition)
							.isUnconditionallyReadOnly()) {
				restrictions.addReadOnlyField(maintainableItemDefinition
						.getName());
			} else if (maintainableItemDefinition instanceof MaintainableCollectionDefinition) {
				try {
					Collection collection = (Collection) PropertyUtils
							.getProperty(businessObject,
									maintainableItemDefinition.getName());
					for (Iterator iterator = collection.iterator(); iterator
							.hasNext();) {
						BusinessObject collectionBusinessObject = (BusinessObject) iterator
								.next();
						considerBusinessObjectFieldUnmaskAuthorization(
								collectionBusinessObject, user, restrictions,
								propertyPrefix + "."
										+ maintainableItemDefinition.getName());
						considerBusinessObjectFieldViewAuthorization(
								businessObjectEntry, maintenanceDocument, user,
								authorizer, restrictions, propertyPrefix + "."
										+ maintainableItemDefinition.getName());
						considerBusinessObjectFieldModifyAuthorization(
								businessObjectEntry, maintenanceDocument, user,
								authorizer, restrictions, propertyPrefix + "."
										+ maintainableItemDefinition.getName());
						addMaintainableItemRestrictions(
								((MaintainableCollectionDefinition) maintainableItemDefinition)
										.getMaintainableCollections(),
								authorizer, restrictions, maintenanceDocument,
								collectionBusinessObject, propertyPrefix + "."
										+ maintainableItemDefinition.getName(),
								user);
						addMaintainableItemRestrictions(
								((MaintainableCollectionDefinition) maintainableItemDefinition)
										.getMaintainableFields(), authorizer,
								restrictions, maintenanceDocument,
								collectionBusinessObject, propertyPrefix + "."
										+ maintainableItemDefinition.getName(),
								user);
					}
				} catch (Exception e) {
					throw new RuntimeException(
							"Unable to resolve collection property: "
									+ businessObject.getClass() + ":"
									+ maintainableItemDefinition.getName(), e);
				}
			}
		}
	}

	public <T extends BusinessObject> boolean canFullyUnmaskField(Person user,
			Class<T> businessObjectClass, String fieldName) {
		return getIdentityManagementService().isAuthorizedByTemplateName(
				user.getPrincipalId(),
				KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD,
				new AttributeSet(getFieldPermissionDetails(businessObjectClass,
						fieldName)), null);
	}

	public <T extends BusinessObject> boolean canPartiallyUnmaskField(
			Person user, Class<T> businessObjectClass, String fieldName) {
		return getIdentityManagementService().isAuthorizedByTemplateName(
				user.getPrincipalId(),
				KNSConstants.KNS_NAMESPACE,
				KimConstants.PermissionTemplateNames.PARTIAL_UNMASK_FIELD,
				new AttributeSet(getFieldPermissionDetails(businessObjectClass,
						fieldName)), null);
	}

	protected <T extends BusinessObject> Map<String, String> getFieldPermissionDetails(
			Class<T> businessObjectClass, String attributeName) {
		try {
			return getFieldPermissionDetails(businessObjectClass.newInstance(),
					attributeName);
		} catch (Exception e) {
			throw new RuntimeException(
					"The getPermissionDetails method of BusinessObjectAuthorizationServiceImpl was unable to instantiate the businessObjectClass"
							+ businessObjectClass, e);
		}
	}

	protected Map<String, String> getFieldPermissionDetails(
			BusinessObject businessObject, String attributeName) {
		Map<String, String> permissionDetails = null;
		String namespaceCode = null;
		String componentName = null;
		String propertyName = null;
		if (attributeName.contains(".")) {
			try {
				permissionDetails = KimCommonUtils
						.getNamespaceAndComponentSimpleName(PropertyUtils
								.getPropertyType(businessObject, attributeName
										.substring(0, attributeName
												.lastIndexOf("."))));
			} catch (Exception e) {
				throw new RuntimeException(
						"Unable to discover nested business object class: "
								+ businessObject.getClass() + " : "
								+ attributeName, e);
			}
			permissionDetails.put(KimAttributes.PROPERTY_NAME, attributeName
					.substring(attributeName.indexOf(".") + 1));
		} else {
			permissionDetails = KimCommonUtils
					.getNamespaceAndComponentSimpleName(businessObject
							.getClass());
			permissionDetails.put(KimAttributes.PROPERTY_NAME, attributeName);
		}
		return permissionDetails;
	}

	private DataDictionaryService getDataDictionaryService() {
		if (dataDictionaryService == null) {
			dataDictionaryService = KNSServiceLocator
					.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

	private IdentityManagementService getIdentityManagementService() {
		if (identityManagementService == null) {
			identityManagementService = KIMServiceLocator
					.getIdentityManagementService();
		}
		return identityManagementService;
	}

	private BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
		if (businessObjectDictionaryService == null) {
			businessObjectDictionaryService = KNSServiceLocator
					.getBusinessObjectDictionaryService();
		}
		return businessObjectDictionaryService;
	}

	private DocumentTypeService getDocumentTypeService() {
		if (documentTypeService == null) {
			documentTypeService = KNSServiceLocator.getDocumentTypeService();
		}
		return documentTypeService;
	}

	private MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if (maintenanceDocumentDictionaryService == null) {
			maintenanceDocumentDictionaryService = KNSServiceLocator
					.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}
}
