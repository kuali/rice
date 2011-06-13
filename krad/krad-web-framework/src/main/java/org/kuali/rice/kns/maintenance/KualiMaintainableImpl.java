/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.maintenance;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.services.CoreApiServiceLocator;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.FieldRestriction;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.exception.UnknownBusinessClassAttributeException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationController;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.exception.PessimisticLockingException;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.valuefinder.ValueFinder;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocatorWeb;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.InactiveRecordsHidingUtils;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.spring.form.MaintenanceForm;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.kns.web.ui.SectionBridge;

/**
 * Base Maintainable class to hold things common to all maintainables.
 */
public class KualiMaintainableImpl extends ViewHelperServiceImpl implements Maintainable {
	private static final long serialVersionUID = 4814145799502207182L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiMaintainableImpl.class);

	protected String documentNumber;
	protected Object dataObject;
	protected PersistableBusinessObject businessObject;
	protected Class boClass;
	protected String maintenanceAction;

	protected Map<String, PersistableBusinessObject> newCollectionLines = new HashMap<String, PersistableBusinessObject>();
	protected Map<String, Boolean> inactiveRecordDisplay = new HashMap<String, Boolean>();

	protected String docTypeName;

    // TODO: rename once 'newCollectionLines' is removed
    protected Set<String> newCollectionLineNames = new HashSet<String>();

	protected transient PersistenceStructureService persistenceStructureService;
	protected transient MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
	protected transient BusinessObjectService businessObjectService;
	protected transient BusinessObjectDictionaryService businessObjectDictionaryService;
	protected transient EncryptionService encryptionService;
	protected transient org.kuali.rice.kim.service.PersonService personService;
	protected transient BusinessObjectMetaDataService businessObjectMetaDataService;
	protected transient BusinessObjectAuthorizationService businessObjectAuthorizationService;
	protected transient MaintenanceDocumentService maintenanceDocumentService;
	protected transient DocumentHelperService documentHelperService;
	protected transient LookupService lookupService;

	/**
	 * Default empty constructor
	 */
	public KualiMaintainableImpl() {
        super();
	}

	/**
	 * Constructor which initializes the business object to be maintained.
	 * 
	 * @param businessObject
	 */
	public KualiMaintainableImpl(PersistableBusinessObject businessObject) {
		super();
		this.businessObject = businessObject;
		this.dataObject = businessObject;
	}

	/**
	 * This methods will do the setting of some attributes that might be
	 * necessary if we're creating a new business object using on an existing
	 * business object. For example, create a division Vendor based on an
	 * existing parent Vendor. (Please see VendorMaintainableImpl.java)
	 */
	public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {

	}

	public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {

	}

	/**
	 * This is a hook to allow the document to override the generic document
	 * title.
	 * 
	 * @return String document title
	 */
	public String getDocumentTitle(MaintenanceDocument document) {
		// default implementation is to allow MaintenanceDocumentBase to
		// generate the doc title
		return "";
	}

	/**
	 * Note: as currently implemented, every key field for a given
	 * BusinessObject subclass must have a visible getter.
	 * 
	 * @return String containing the business object class and key value pairs
	 *         of the current instance that can be used as a unique locking
	 *         representation.
	 */
	public List<MaintenanceLock> generateMaintenanceLocks() {

		// NOTE: KualiGlobalMaintainableImpl overrides this method and forces
		// all globals to override that, so they each do their own thing

		List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
		StringBuffer lockRepresentation = new StringBuffer(boClass.getName());
		lockRepresentation.append(KNSConstants.Maintenance.AFTER_CLASS_DELIM);

		Object bo = getDataObject();
		List keyFieldNames = getMaintenanceDocumentDictionaryService().getLockingKeys(getDocumentTypeName());

		for (Iterator i = keyFieldNames.iterator(); i.hasNext();) {
			String fieldName = (String) i.next();
			Object fieldValue = ObjectUtils.getPropertyValue(bo, fieldName);
			if (fieldValue == null) {
				fieldValue = "";
			}

			// check if field is a secure
			if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(boClass,
					fieldName)) {
				try {
					fieldValue = getEncryptionService().encrypt(fieldValue);
				}
				catch (GeneralSecurityException e) {
					LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
					throw new RuntimeException("Unable to encrypt secure field for locking representation "
							+ e.getMessage());
				}
			}

			lockRepresentation.append(fieldName);
			lockRepresentation.append(KNSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
			lockRepresentation.append(String.valueOf(fieldValue));
			if (i.hasNext()) {
				lockRepresentation.append(KNSConstants.Maintenance.AFTER_VALUE_DELIM);
			}
		}

		MaintenanceLock maintenanceLock = new MaintenanceLock();
		maintenanceLock.setDocumentNumber(documentNumber);
		maintenanceLock.setLockingRepresentation(lockRepresentation.toString());
		maintenanceLocks.add(maintenanceLock);
		return maintenanceLocks;
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#populateBusinessObject(java.util.Map,
	 *      org.kuali.rice.kns.document.MaintenanceDocument, String)
	 */
	@SuppressWarnings("unchecked")
	public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument,
			String methodToCall) {
		fieldValues = decryptEncryptedData(fieldValues, maintenanceDocument, methodToCall);
		Map newFieldValues = null;
		newFieldValues = getPersonService().resolvePrincipalNamesToPrincipalIds(getBusinessObject(), fieldValues);

		Map cachedValues = FieldUtils.populateBusinessObjectFromMap(getBusinessObject(), newFieldValues);
		performForceUpperCase(newFieldValues);

		return cachedValues;
	}

	/**
	 * Special hidden parameters are set on the maintenance jsp starting with a
	 * prefix that tells us which fields have been encrypted. This field finds
	 * the those parameters in the map, whose value gives us the property name
	 * that has an encrypted value. We then need to decrypt the value in the Map
	 * before the business object is populated.
	 * 
	 * @param fieldValues
	 *            - possibly with encrypted values
	 * @return Map fieldValues - with no encrypted values
	 */
	protected Map<String, String> decryptEncryptedData(Map<String, String> fieldValues,
			MaintenanceDocument maintenanceDocument, String methodToCall) {
		try {
			MaintenanceDocumentRestrictions auths = KNSServiceLocatorWeb.getBusinessObjectAuthorizationService()
					.getMaintenanceDocumentRestrictions(maintenanceDocument,
							GlobalVariables.getUserSession().getPerson());
			for (Iterator<String> iter = fieldValues.keySet().iterator(); iter.hasNext();) {
				String fieldName = iter.next();
				String fieldValue = (String) fieldValues.get(fieldName);

				if (fieldValue != null && !"".equals(fieldValue)
						&& fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
					if (shouldFieldBeEncrypted(maintenanceDocument, fieldName, auths, methodToCall)) {
						String encryptedValue = fieldValue;

						// take off the postfix
						encryptedValue = StringUtils.stripEnd(encryptedValue, EncryptionService.ENCRYPTION_POST_PREFIX);
						String decryptedValue = getEncryptionService().decrypt(encryptedValue);

						fieldValues.put(fieldName, decryptedValue);
					}
					else
						throw new RuntimeException("The field value for field name " + fieldName
								+ " should not be encrypted.");
				}
				else if (fieldValue != null && !"".equals(fieldValue)
						&& shouldFieldBeEncrypted(maintenanceDocument, fieldName, auths, methodToCall))
					throw new RuntimeException("The field value for field name " + fieldName + " should be encrypted.");
			}
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException("Unable to decrypt secure data: " + e.getMessage());
		}

		return fieldValues;
	}

	/**
	 * Determines whether the field in a request should be encrypted. This base
	 * implementation does not work for properties of collection elements.
	 * 
	 * This base implementation will only return true if the maintenance
	 * document is being refreshed after a lookup (i.e. methodToCall is
	 * "refresh") and the data dictionary-based attribute security definition
	 * has any restriction defined, whether the user would be authorized to view
	 * the field. This assumes that only fields returned from a lookup should be
	 * encrypted in a request. If the user otherwise has no permissions to
	 * view/edit the field, then a request parameter will not be sent back to
	 * the server for population.
	 * 
	 * @param maintenanceDocument
	 * @param fieldName
	 * @param auths
	 * @param methodToCall
	 * @return
	 */
	protected boolean shouldFieldBeEncrypted(MaintenanceDocument maintenanceDocument, String fieldName,
			MaintenanceDocumentRestrictions auths, String methodToCall) {
		if ("refresh".equals(methodToCall) && fieldName != null) {
			fieldName = fieldName.replaceAll("\\[[0-9]*+\\]", "");
			fieldName = fieldName.replaceAll("^add\\.", "");
			Map<String, AttributeSecurity> fieldNameToAttributeSecurityMap = MaintenanceUtils
					.retrievePropertyPathToAttributeSecurityMappings(getDocumentTypeName());
			AttributeSecurity attributeSecurity = fieldNameToAttributeSecurityMap.get(fieldName);
			return attributeSecurity != null && attributeSecurity.hasRestrictionThatRemovesValueFromUI();
		}
		else {
			return false;
		}
	}

	/**
	 * Calls method to get all the core sections for the business object defined
	 * in the data dictionary. Then determines if the bo has custom attributes,
	 * if so builds a custom attribute section and adds to the section list.
	 * 
	 * @return List of org.kuali.ui.Section objects
	 */
	public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
		List<Section> sections = new ArrayList<Section>();
		sections.addAll(getCoreSections(document, oldMaintainable));

		return sections;
	}

	/**
	 * Gets list of maintenance sections built from the data dictionary. If the
	 * section contains maintenance fields, construct Row/Field UI objects and
	 * place under Section UI. If section contains a maintenance collection,
	 * call method to build a Section UI which contains rows of Container
	 * Fields.
	 * 
	 * @return List of org.kuali.ui.Section objects
	 */
	public List<Section> getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {
		List<Section> sections = new ArrayList<Section>();
		MaintenanceDocumentRestrictions maintenanceRestrictions = KNSServiceLocatorWeb
				.getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document,
						GlobalVariables.getUserSession().getPerson());

		MaintenanceDocumentPresentationController maintenanceDocumentPresentationController = (MaintenanceDocumentPresentationController) getDocumentHelperService()
				.getDocumentPresentationController(document);
		Set<String> conditionallyRequiredFields = maintenanceDocumentPresentationController
				.getConditionallyRequiredPropertyNames(document);

		List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService()
				.getMaintainableSections(docTypeName);
		try {
			// iterate through section definitions and create Section UI object
			for (Iterator iter = sectionDefinitions.iterator(); iter.hasNext();) {
				MaintainableSectionDefinition maintSectionDef = (MaintainableSectionDefinition) iter.next();

				List<String> displayedFieldNames = new ArrayList<String>();
				if (!maintenanceRestrictions.isHiddenSectionId(maintSectionDef.getId())) {

					for (Iterator iter2 = maintSectionDef.getMaintainableItems().iterator(); iter2.hasNext();) {
						MaintainableItemDefinition item = (MaintainableItemDefinition) iter2.next();
						if (item instanceof MaintainableFieldDefinition) {
							displayedFieldNames.add(((MaintainableFieldDefinition) item).getName());
						}
					}

					Section section = SectionBridge.toSection(maintSectionDef, getBusinessObject(), this,
							oldMaintainable, getMaintenanceAction(), displayedFieldNames, conditionallyRequiredFields);
					if (maintenanceRestrictions.isReadOnlySectionId(maintSectionDef.getId())) {
						section.setReadOnly(true);
					}

					// add to section list
					sections.add(section);
				}

			}

		}
		catch (InstantiationException e) {
			LOG.error("Unable to create instance of object class" + e.getMessage());
			throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
		}
		catch (IllegalAccessException e) {
			LOG.error("Unable to create instance of object class" + e.getMessage());
			throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
		}

		return sections;
	}

	/**
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#saveBusinessObject()
	 */
	public void saveBusinessObject() {
		getBusinessObjectService().linkAndSave(businessObject);
	}

	/**
	 * Retrieves title for maintenance document from data dictionary
	 */
	public String getMaintainableTitle() {
		return getMaintenanceDocumentDictionaryService().getMaintenanceLabel(getDocumentTypeName());
	}

	/**
	 * Retrieves the status of the boNotesEnabled
	 */
	public boolean isBoNotesEnabled() {
		if(BusinessObject.class.isAssignableFrom(boClass)) {
			return getBusinessObjectDictionaryService().areNotesSupported(this.boClass);
		}
		
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#refresh(java.lang.String,
	 *      java.util.Map) Impls will be needed if custom action is needed on
	 *      refresh.
	 */
	public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
		String referencesToRefresh = (String) fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH);
		refreshReferences(referencesToRefresh);
	}

	protected void refreshReferences(String referencesToRefresh) {
		PersistenceStructureService persistenceStructureService = getPersistenceStructureService();
		if (StringUtils.isNotBlank(referencesToRefresh)) {
			String[] references = StringUtils.split(referencesToRefresh, KNSConstants.REFERENCES_TO_REFRESH_SEPARATOR);
			for (String reference : references) {
				if (StringUtils.isNotBlank(reference)) {
					if (reference.startsWith(KNSConstants.ADD_PREFIX + ".")) {
						// add one for the period
						reference = reference.substring(KNSConstants.ADD_PREFIX.length() + 1);

						String boToRefreshName = StringUtils.substringBeforeLast(reference, ".");
						String propertyToRefresh = StringUtils.substringAfterLast(reference, ".");
						if (StringUtils.isNotBlank(propertyToRefresh)) {
							PersistableBusinessObject addlineBO = getNewCollectionLine(boToRefreshName);
							Class addlineBOClass = addlineBO.getClass();
							if (LOG.isDebugEnabled()) {
								LOG.debug("Refresh this \"new\"/add object for the collections:  " + reference);
							}
							if (persistenceStructureService.hasReference(addlineBOClass, propertyToRefresh)
									|| persistenceStructureService.hasCollection(addlineBOClass, propertyToRefresh)) {
								addlineBO.refreshReferenceObject(propertyToRefresh);
							}
							else {
								if (getDataDictionaryService().hasRelationship(addlineBOClass.getName(),
										propertyToRefresh)) {
									// a DD mapping, try to go straight to the
									// object and refresh it there
									Object possibleBO = ObjectUtils.getPropertyValue(addlineBO, propertyToRefresh);
									if (possibleBO != null && possibleBO instanceof PersistableBusinessObject) {
										((PersistableBusinessObject) possibleBO).refresh();
									}
								}
							}
						}
						else {
							LOG.error("Error: unable to refresh this \"new\"/add object for the collections:  "
									+ reference);
						}
					}
					else if (ObjectUtils.isNestedAttribute(reference)) {
						Object nestedObject = ObjectUtils.getNestedValue(getBusinessObject(),
								ObjectUtils.getNestedAttributePrefix(reference));
						if (nestedObject instanceof Collection) {
							// do nothing, probably because it's not really a
							// collection reference but a relationship defined
							// in the DD for a collections lookup
							// this part will need to be rewritten when the DD
							// supports true collection references
						}
						else if (nestedObject instanceof PersistableBusinessObject) {
							String propertyToRefresh = ObjectUtils.getNestedAttributePrimitive(reference);
							if (persistenceStructureService.hasReference(nestedObject.getClass(), propertyToRefresh)
									|| persistenceStructureService.hasCollection(nestedObject.getClass(),
											propertyToRefresh)) {
								if (LOG.isDebugEnabled()) {
									LOG.debug("Refeshing " + ObjectUtils.getNestedAttributePrefix(reference) + " "
											+ ObjectUtils.getNestedAttributePrimitive(reference));
								}
								((PersistableBusinessObject) nestedObject).refreshReferenceObject(propertyToRefresh);
							}
							else {
								// a DD mapping, try to go straight to the
								// object and refresh it there
								Object possibleBO = ObjectUtils.getPropertyValue(nestedObject, propertyToRefresh);
								if (possibleBO != null && possibleBO instanceof PersistableBusinessObject) {
									if (getDataDictionaryService().hasRelationship(possibleBO.getClass().getName(),
											propertyToRefresh)) {
										((PersistableBusinessObject) possibleBO).refresh();
									}
								}
							}
						}
						else {
							LOG.warn("Expected that a referenceToRefresh ("
									+ reference
									+ ")  would be a PersistableBusinessObject or Collection, but instead, it was of class "
									+ nestedObject.getClass().getName());
						}
					}
					else {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Refreshing " + reference);
						}
						if (persistenceStructureService.hasReference(boClass, reference)
								|| persistenceStructureService.hasCollection(boClass, reference)) {
							getBusinessObject().refreshReferenceObject(reference);
						}
						else {
							if (getDataDictionaryService().hasRelationship(getBusinessObject().getClass().getName(),
									reference)) {
								// a DD mapping, try to go straight to the
								// object and refresh it there
								Object possibleRelationship = ObjectUtils.getPropertyValue(getBusinessObject(),
										reference);
								if (possibleRelationship != null) {
									if (possibleRelationship instanceof PersistableBusinessObject) {
										((PersistableBusinessObject) possibleRelationship).refresh();
									}
									else if (possibleRelationship instanceof Collection) {
										// do nothing, probably because it's not
										// really a collection reference but a
										// relationship defined in the DD for a
										// collections lookup
										// this part will need to be rewritten
										// when the DD supports true collection
										// references
									}
									else {
										LOG.warn("Expected that a referenceToRefresh ("
												+ reference
												+ ")  would be a PersistableBusinessObject or Collection, but instead, it was of class "
												+ possibleRelationship.getClass().getName());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName,
			Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {
		Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(bo, collectionName);
		String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();

		List<String> duplicateIdentifierFieldsFromDataDictionary = getDuplicateIdentifierFieldsFromDataDictionary(
				docTypeName, collectionName);

		List<String> existingIdentifierList = getMultiValueIdentifierList(maintCollection,
				duplicateIdentifierFieldsFromDataDictionary);

		Class collectionClass = getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass(docTypeName,
				collectionName);

		List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService()
				.getMaintainableSections(docTypeName);
		Map<String, String> template = MaintenanceUtils.generateMultipleValueLookupBOTemplate(sections, collectionName);
		try {
			for (PersistableBusinessObject nextBo : rawValues) {
				PersistableBusinessObject templatedBo;
				if (needsBlank) {
					templatedBo = (PersistableBusinessObject) collectionClass.newInstance();
				}
				else {
					// templatedBo = (PersistableBusinessObject)
					// ObjectUtils.createHybridBusinessObject(collectionClass,
					// nextBo, template);
					try {
						ModuleService moduleService = KNSServiceLocatorWeb.getKualiModuleService()
								.getResponsibleModuleService(collectionClass);
						if (moduleService != null && moduleService.isExternalizable(collectionClass))
							templatedBo = (PersistableBusinessObject) moduleService
									.createNewObjectFromExternalizableClass(collectionClass);
						else
							templatedBo = (PersistableBusinessObject) collectionClass.newInstance();
					}
					catch (Exception e) {
						throw new RuntimeException("Cannot instantiate " + collectionClass.getName(), e);
					}
					// first set the default values specified in the DD
					setNewCollectionLineDefaultValues(collectionName, templatedBo);
					// then set the values from the multiple value lookup result
					ObjectUtils.createHybridBusinessObject(templatedBo, nextBo, template);

					prepareBusinessObjectForAdditionFromMultipleValueLookup(collectionName, templatedBo);
				}
				templatedBo.setNewCollectionRecord(true);

				if (!hasBusinessObjectExisted(templatedBo, existingIdentifierList,
						duplicateIdentifierFieldsFromDataDictionary)) {
					maintCollection.add(templatedBo);

				}
			}
		}
		catch (Exception e) {
			LOG.error("Unable to add multiple value lookup results " + e.getMessage());
			throw new RuntimeException("Unable to add multiple value lookup results " + e.getMessage());
		}
	}

	/**
	 * This method is to retrieve a List of fields which are specified in the
	 * maintenance document data dictionary as the
	 * duplicateIdentificationFields. This List is used to determine whether the
	 * new entry being added to the collection is a duplicate entry and if so,
	 * we should not add the new entry to the existing collection
	 * 
	 * @param docTypeName
	 * @param collectionName
	 */
	public List<String> getDuplicateIdentifierFieldsFromDataDictionary(String docTypeName, String collectionName) {
		List<String> duplicateIdentifierFieldNames = new ArrayList<String>();
		MaintainableCollectionDefinition collDef = getMaintenanceDocumentDictionaryService().getMaintainableCollection(
				docTypeName, collectionName);
		Collection<MaintainableFieldDefinition> fieldDef = collDef.getDuplicateIdentificationFields();
		for (MaintainableFieldDefinition eachFieldDef : fieldDef) {
			duplicateIdentifierFieldNames.add(eachFieldDef.getName());
		}
		return duplicateIdentifierFieldNames;
	}

	public List<String> getMultiValueIdentifierList(Collection maintCollection, List<String> duplicateIdentifierFields) {
		List<String> identifierList = new ArrayList<String>();
		for (PersistableBusinessObject bo : (Collection<PersistableBusinessObject>) maintCollection) {
			String uniqueIdentifier = new String();
			for (String identifierField : duplicateIdentifierFields) {
				uniqueIdentifier = uniqueIdentifier + identifierField + "-"
						+ ObjectUtils.getPropertyValue(bo, identifierField);
			}
			if (StringUtils.isNotEmpty(uniqueIdentifier)) {
				identifierList.add(uniqueIdentifier);
			}
		}
		return identifierList;
	}

	public boolean hasBusinessObjectExisted(BusinessObject bo, List<String> existingIdentifierList,
			List<String> duplicateIdentifierFields) {
		String uniqueIdentifier = new String();
		for (String identifierField : duplicateIdentifierFields) {
			uniqueIdentifier = uniqueIdentifier + identifierField + "-"
					+ ObjectUtils.getPropertyValue(bo, identifierField);
		}
		if (existingIdentifierList.contains(uniqueIdentifier)) {
			return true;
		}
		else {
			return false;
		}
	}

	public void prepareBusinessObjectForAdditionFromMultipleValueLookup(String collectionName, BusinessObject bo) {
		// default implementation does nothing
	}

	/**
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#prepareForSave()
	 */
	public void prepareForSave() {

	}

	/**
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterRetrieve()
	 */
	public void processAfterRetrieve() {
	}

	/**
	 * Set the new collection records back to true so they can be deleted (copy
	 * should act like new)
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
	 */
	public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
		try {
			ObjectUtils.setObjectPropertyDeep(businessObject, KNSPropertyConstants.NEW_COLLECTION_RECORD,
					boolean.class, true, 2);
		}
		catch (Exception e) {
			LOG.error("unable to set newCollectionRecord property: " + e.getMessage(), e);
			throw new RuntimeException("unable to set newCollectionRecord property: " + e.getMessage(), e);
		}
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterEdit()
	 */
	public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterNew()
	 */
	public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
	}

	/**
	 * Retrieves the document type name from the data dictionary based on
	 * business object class
	 */
	protected String getDocumentTypeName() {
		return getMaintenanceDocumentDictionaryService().getDocumentTypeName(boClass);
	}

	@Override
    public Object getDataObject() {
        return dataObject;
    }

    @Override
    public void setDataObject(Object object) {
        this.dataObject = object;
        
        if(object instanceof PersistableBusinessObject) {
            this.businessObject = (PersistableBusinessObject)object;
        }
    }

    /**
     * @return Returns the instance of the business object being maintained.
     */
    public PersistableBusinessObject getBusinessObject() {
        return businessObject;
    }

    /**
     * @param businessObject
     *            Sets the instance of a business object that will be
     *            maintained.
     */
    public void setBusinessObject(PersistableBusinessObject businessObject) {
        this.businessObject = businessObject;
        this.dataObject = businessObject;
    }

	/**
	 * @return Returns the boClass.
	 */
	public Class getBoClass() {
		return boClass;
	}

	/**
	 * @param boClass
	 *            The boClass to set.
	 */
	public void setBoClass(Class boClass) {
		this.boClass = boClass;
		this.docTypeName = getDocumentTypeName();
	}

	/**
	 * @return Returns the maintenanceAction.
	 */
	public String getMaintenanceAction() {
		return maintenanceAction;
	}

	/**
	 * @param maintenanceAction
	 *            The maintenanceAction to set.
	 */
	public void setMaintenanceAction(String maintenanceAction) {
		this.maintenanceAction = maintenanceAction;
	}

	/**
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#setGenerateDefaultValues()
	 */
	public void setGenerateDefaultValues(String docTypeName) {
		List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService()
				.getMaintainableSections(docTypeName);
		Map defaultValues = new HashMap();

		try {
			// iterate through section definitions
			for (Iterator iter = sectionDefinitions.iterator(); iter.hasNext();) {

				MaintainableSectionDefinition maintSectionDef = (MaintainableSectionDefinition) iter.next();
				Collection maintItems = maintSectionDef.getMaintainableItems();
				for (Iterator iterator = maintItems.iterator(); iterator.hasNext();) {
					MaintainableItemDefinition item = (MaintainableItemDefinition) iterator.next();

					if (item instanceof MaintainableFieldDefinition) {
						MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) item;

						String defaultValue = maintainableFieldDefinition.getDefaultValue();
						if (defaultValue != null) {
							if (defaultValue.equals("true")) {
								defaultValue = "Yes";
							}
							else if (defaultValue.equals("false")) {
								defaultValue = "No";
							}
						}

						Class defaultValueFinderClass = maintainableFieldDefinition.getDefaultValueFinderClass();
						if (defaultValueFinderClass != null) {
							defaultValue = ((ValueFinder) defaultValueFinderClass.newInstance()).getValue();

						}
						if (defaultValue != null) {
							defaultValues.put(item.getName(), defaultValue);
						}
					}
				}
			}
			Map cachedValues = FieldUtils.populateBusinessObjectFromMap(getBusinessObject(), defaultValues);
		}
		catch (Exception e) {
			LOG.error("Unable to set default value " + e.getMessage(), e);
			throw new RuntimeException("Unable to set default value" + e.getMessage(), e);
		}

	}

	/**
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#setGenerateBlankRequiredValues()
	 */
	public void setGenerateBlankRequiredValues(String docTypeName) {
		try {
			List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService()
					.getMaintainableSections(docTypeName);
			Map<String, String> defaultValues = new HashMap<String, String>();

			for (MaintainableSectionDefinition maintSectionDef : sectionDefinitions) {
				for (MaintainableItemDefinition item : maintSectionDef.getMaintainableItems()) {
					if (item instanceof MaintainableFieldDefinition) {
						MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) item;
						if (maintainableFieldDefinition.isRequired()
								&& maintainableFieldDefinition.isUnconditionallyReadOnly()) {
							Object currPropVal = ObjectUtils.getPropertyValue(this.getBusinessObject(), item.getName());
							if (currPropVal == null
									|| (currPropVal instanceof String && StringUtils.isBlank((String) currPropVal))) {
								Class<? extends ValueFinder> defaultValueFinderClass = maintainableFieldDefinition
										.getDefaultValueFinderClass();
								if (defaultValueFinderClass != null) {
									String defaultValue = defaultValueFinderClass.newInstance().getValue();
									if (defaultValue != null) {
										defaultValues.put(item.getName(), defaultValue);
									}
								}
							}
						}
					}
				}
			}
			FieldUtils.populateBusinessObjectFromMap(getBusinessObject(), defaultValues);
		}
		catch (Exception e) {
			LOG.error("Unable to set blank required value " + e.getMessage(), e);
			throw new RuntimeException("Unable to set blank required value" + e.getMessage(), e);
		}
	}

	/**
	 * Sets the documentNumber attribute value.
	 * 
	 * @param documentNumber
	 *            The documentNumber to set.
	 */
	public final void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	@Deprecated
	public void processAfterAddLine(String colName, Class colClass) {
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#processBeforeAddLine(java.lang.String,
	 *      java.lang.Class, org.kuali.rice.kns.bo.BusinessObject)
	 */
	public void processBeforeAddLine(String colName, Class colClass, BusinessObject addBO) {
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#getShowInactiveRecords(java.lang.String)
	 */
	public boolean getShowInactiveRecords(String collectionName) {
		return InactiveRecordsHidingUtils.getShowInactiveRecords(inactiveRecordDisplay, collectionName);
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#setShowInactiveRecords(java.lang.String,
	 *      boolean)
	 */
	public void setShowInactiveRecords(String collectionName, boolean showInactive) {
		InactiveRecordsHidingUtils.setShowInactiveRecords(inactiveRecordDisplay, collectionName, showInactive);
	}

	/**
	 * @return the inactiveRecordDisplay
	 */
	public Map<String, Boolean> getInactiveRecordDisplay() {
		return inactiveRecordDisplay;
	}

	public void addNewLineToCollection(String collectionName) {

		if (LOG.isDebugEnabled()) {
			LOG.debug("addNewLineToCollection( " + collectionName + " )");
		}
		// get the new line from the map
		PersistableBusinessObject addLine = newCollectionLines.get(collectionName);
		if (addLine != null) {
			// mark the isNewCollectionRecord so the option to delete this line
			// will be presented
			addLine.setNewCollectionRecord(true);

			// if we add back add button on sub collection of an "add line" we
			// may need extra logic here

			// get the collection from the business object
			Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(getBusinessObject(), collectionName);
			// add the line to the collection
			maintCollection.add(addLine);
			// refresh parent object since attributes could of changed prior to
			// user clicking add

			String referencesToRefresh = LookupUtils
					.convertReferencesToSelectCollectionToString(getAllRefreshableReferences(getBusinessObject()
							.getClass()));
			if (LOG.isInfoEnabled()) {
				LOG.info("References to refresh for adding line to collection " + collectionName + ": "
						+ referencesToRefresh);
			}
			refreshReferences(referencesToRefresh);
		}

		initNewCollectionLine(collectionName);

	}

	public PersistableBusinessObject getNewCollectionLine(String collectionName) {
		if (LOG.isDebugEnabled()) {
			// LOG.debug( this + ") getNewCollectionLine( " + collectionName +
			// ")", new Exception( "tracing exception") );
			LOG.debug("newCollectionLines: " + newCollectionLines);
		}
		PersistableBusinessObject addLine = newCollectionLines.get(collectionName);
		if (addLine == null) {
			addLine = initNewCollectionLine(collectionName);
		}
		return addLine;
	}

	public PersistableBusinessObject initNewCollectionLine(String collectionName) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("initNewCollectionLine( " + collectionName + " )");
		}
		// try to get the object from the map
		// BusinessObject addLine = newCollectionLines.get( collectionName );
		// if ( addLine == null ) {
		// if not there, instantiate a new one
		PersistableBusinessObject addLine;
		try {
			addLine = (PersistableBusinessObject) getMaintenanceDocumentDictionaryService()
					.getCollectionBusinessObjectClass(docTypeName, collectionName).newInstance();
		}
		catch (Exception ex) {
			LOG.error("unable to instantiate new collection line", ex);
			throw new RuntimeException("unable to instantiate new collection line", ex);
		}
		// and add it to the map
		newCollectionLines.put(collectionName, addLine);
		// }
		// set its values to the defaults
		setNewCollectionLineDefaultValues(collectionName, addLine);
		return addLine;
	}

	/**
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#populateNewCollectionLines(java.util.Map)
	 */
	public Map<String, String> populateNewCollectionLines(Map<String, String> fieldValues,
			MaintenanceDocument maintenanceDocument, String methodToCall) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("populateNewCollectionLines: " + fieldValues);
		}
		fieldValues = decryptEncryptedData(fieldValues, maintenanceDocument, methodToCall);

		Map<String, String> cachedValues = new HashMap<String, String>();

		// loop over all collections with an enabled add line
		List<MaintainableCollectionDefinition> collections = getMaintenanceDocumentDictionaryService()
				.getMaintainableCollections(docTypeName);

		for (MaintainableCollectionDefinition coll : collections) {
			// get the collection name
			String collName = coll.getName();
			if (LOG.isDebugEnabled()) {
				LOG.debug("checking for collection: " + collName);
			}
			// build a map for that collection
			Map<String, String> collectionValues = new HashMap<String, String>();
			Map<String, String> subCollectionValues = new HashMap<String, String>();
			// loop over the collection, extracting entries with a matching
			// prefix
			for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
				String key = entry.getKey();
				if (key.startsWith(collName)) {
					String subStrKey = key.substring(collName.length() + 1);
					// check for subcoll w/ '[', set collName to propername and
					// put in correct name for collection values (i.e. strip
					// '*[x].')
					if (key.contains("[")) {

						// collName = StringUtils.substringBeforeLast(key,"[");

						// need the whole thing if subcollection
						subCollectionValues.put(key, entry.getValue());
					}
					else {
						collectionValues.put(subStrKey, entry.getValue());
					}
				}
			}
			// send those values to the business object
			if (LOG.isDebugEnabled()) {
				LOG.debug("values for collection: " + collectionValues);
			}
			cachedValues.putAll(FieldUtils.populateBusinessObjectFromMap(getNewCollectionLine(collName),
					collectionValues, KNSConstants.MAINTENANCE_ADD_PREFIX + collName + "."));
			performFieldForceUpperCase(getNewCollectionLine(collName), collectionValues);

			cachedValues.putAll(populateNewSubCollectionLines(coll, subCollectionValues));
		}

		// cachedValues.putAll( FieldUtils.populateBusinessObjectFromMap( ))
		return cachedValues;
	}

	/*
	 * Yes, I think this could be merged with the above code - I'm leaving it
	 * separate until I figure out of there are any issues which would reqire
	 * that it be separated.
	 */
	protected Map populateNewSubCollectionLines(MaintainableCollectionDefinition parentCollection, Map fieldValues) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("populateNewSubCollectionLines: " + fieldValues);
		}
		Map cachedValues = new HashMap();

		for (MaintainableCollectionDefinition coll : parentCollection.getMaintainableCollections()) {
			// get the collection name
			String collName = coll.getName();

			if (LOG.isDebugEnabled()) {
				LOG.debug("checking for sub collection: " + collName);
			}
			Map<String, String> parents = new HashMap<String, String>();
			// get parents from list
			for (Object entry : fieldValues.entrySet()) {
				String key = (String) ((Map.Entry) entry).getKey();
				if (key.contains(collName)) {
					parents.put(StringUtils.substringBefore(key, "."), "");
				}
			}

			for (String parent : parents.keySet()) {
				// build a map for that collection
				Map<String, Object> collectionValues = new HashMap<String, Object>();
				// loop over the collection, extracting entries with a matching
				// prefix
				for (Object entry : fieldValues.entrySet()) {
					String key = (String) ((Map.Entry) entry).getKey();
					if (key.contains(parent)) {
						String substr = StringUtils.substringAfterLast(key, ".");
						collectionValues.put(substr, ((Map.Entry) entry).getValue());
					}
				}
				// send those values to the business object
				if (LOG.isDebugEnabled()) {
					LOG.debug("values for sub collection: " + collectionValues);
				}
				GlobalVariables.getMessageMap().addToErrorPath(
						KNSConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName);
				cachedValues.putAll(FieldUtils.populateBusinessObjectFromMap(getNewCollectionLine(parent + "."
						+ collName), collectionValues, KNSConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName
						+ "."));
				performFieldForceUpperCase(getNewCollectionLine(parent + "." + collName), collectionValues);
				GlobalVariables.getMessageMap().removeFromErrorPath(
						KNSConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName);
			}

			cachedValues.putAll(populateNewSubCollectionLines(coll, fieldValues));
		}

		return cachedValues;
	}

	public Collection<String> getAffectedReferencesFromLookup(BusinessObject baseBO, String attributeName,
			String collectionPrefix) {
		PersistenceStructureService pss = getPersistenceStructureService();
		String nestedBOPrefix = "";
		if (ObjectUtils.isNestedAttribute(attributeName)) {
			// if we're performing a lookup on a nested attribute, we need to
			// use the nested BO all the way down the chain
			nestedBOPrefix = ObjectUtils.getNestedAttributePrefix(attributeName);

			// renormalize the base BO so that the attribute name is not nested
			// anymore
			Class reference = ObjectUtils.getPropertyType(baseBO, nestedBOPrefix, pss);
			if (!(PersistableBusinessObject.class.isAssignableFrom(reference))) {
				return new ArrayList<String>();
			}

			try {
				baseBO = (PersistableBusinessObject) reference.newInstance();
			}
			catch (InstantiationException e) {
				LOG.error(e);
			}
			catch (IllegalAccessException e) {
				LOG.error(e);
			}
			attributeName = ObjectUtils.getNestedAttributePrimitive(attributeName);
		}

		if (baseBO == null) {
			return new ArrayList<String>();
		}

		Map<String, Class> referenceNameToClassFromPSS = LookupUtils.getPrimitiveReference(baseBO, attributeName);
		if (referenceNameToClassFromPSS.size() > 1) {
			LOG.error("LookupUtils.getPrimitiveReference return results should only have at most one element");
		}

		BusinessObjectMetaDataService businessObjectMetaDataService = getBusinessObjectMetaDataService();
		BusinessObjectRelationship relationship = businessObjectMetaDataService.getBusinessObjectRelationship(baseBO,
				attributeName);
		if (relationship == null) {
			return new ArrayList<String>();
		}

		Map<String, String> fkToPkMappings = relationship.getParentToChildReferences();

		Collection<String> affectedReferences = generateAllAffectedReferences(baseBO.getClass(), fkToPkMappings,
				nestedBOPrefix, collectionPrefix);
		if (LOG.isDebugEnabled()) {
			LOG.debug("References affected by a lookup on BO attribute \"" + collectionPrefix + nestedBOPrefix + "."
					+ attributeName + ": " + affectedReferences);
		}

		return affectedReferences;
	}

	protected boolean isRelationshipRefreshable(Class boClass, String relationshipName) {
		if (getPersistenceStructureService().isPersistable(boClass)) {
			if (getPersistenceStructureService().hasCollection(boClass, relationshipName)) {
				return !getPersistenceStructureService().isCollectionUpdatable(boClass, relationshipName);
			}
			else if (getPersistenceStructureService().hasReference(boClass, relationshipName)) {
				return !getPersistenceStructureService().isReferenceUpdatable(boClass, relationshipName);
			}
			// else, assume that the relationship is defined in the DD
		}

		return true;
	}

	protected Collection<String> generateAllAffectedReferences(Class boClass, Map<String, String> fkToPkMappings,
			String nestedBOPrefix, String collectionPrefix) {
		Set<String> allAffectedReferences = new HashSet<String>();
		DataDictionaryService dataDictionaryService = getDataDictionaryService();
		PersistenceStructureService pss = getPersistenceStructureService();

		collectionPrefix = StringUtils.isBlank(collectionPrefix) ? "" : collectionPrefix;

		// retrieve the attributes that are affected by a lookup on
		// attributeName.
		Collection<String> attributeReferenceFKAttributes = fkToPkMappings.keySet();

		// a lookup on an attribute may cause other attributes to be updated
		// (e.g. account code lookup would also affect chart code)
		// build a list of all affected FK values via mapKeyFields above, and
		// for each FK, see if there are any non-updatable references with that
		// FK

		// deal with regular simple references (<reference-descriptor>s in OJB)
		for (String fkAttribute : attributeReferenceFKAttributes) {
			for (String affectedReference : pss.getReferencesForForeignKey(boClass, fkAttribute).keySet()) {
				if (isRelationshipRefreshable(boClass, affectedReference)) {
					if (StringUtils.isBlank(nestedBOPrefix)) {
						allAffectedReferences.add(collectionPrefix + affectedReference);
					}
					else {
						allAffectedReferences.add(collectionPrefix + nestedBOPrefix + "." + affectedReference);
					}
				}
			}
		}

		// now with collection references (<collection-descriptor>s in OJB)
		for (String collectionName : pss.listCollectionObjectTypes(boClass).keySet()) {
			if (isRelationshipRefreshable(boClass, collectionName)) {
				Map<String, String> keyMappingsForCollection = pss.getInverseForeignKeysForCollection(boClass,
						collectionName);
				for (String collectionForeignKey : keyMappingsForCollection.keySet()) {
					if (attributeReferenceFKAttributes.contains(collectionForeignKey)) {
						if (StringUtils.isBlank(nestedBOPrefix)) {
							allAffectedReferences.add(collectionPrefix + collectionName);
						}
						else {
							allAffectedReferences.add(collectionPrefix + nestedBOPrefix + "." + collectionName);
						}
					}
				}
			}
		}

		// now use the DD to compute more affected references
		List<String> ddDefinedRelationships = dataDictionaryService.getRelationshipNames(boClass.getName());
		for (String ddRelationship : ddDefinedRelationships) {
			// note that this map is PK (key/target) => FK (value/source)
			Map<String, String> referencePKtoFKmappings = dataDictionaryService.getRelationshipAttributeMap(
					boClass.getName(), ddRelationship);
			for (String sourceAttribute : referencePKtoFKmappings.values()) {
				// the sourceAttribute is the FK pointing to the target
				// attribute (PK)
				if (attributeReferenceFKAttributes.contains(sourceAttribute)) {
					for (String affectedReference : dataDictionaryService.getRelationshipEntriesForSourceAttribute(
							boClass.getName(), sourceAttribute)) {
						if (isRelationshipRefreshable(boClass, ddRelationship)) {
							if (StringUtils.isBlank(nestedBOPrefix)) {
								allAffectedReferences.add(affectedReference);
							}
							else {
								allAffectedReferences.add(nestedBOPrefix + "." + affectedReference);
							}
						}
					}
				}
			}
		}
		return allAffectedReferences;
	}

	protected Collection<String> getAllRefreshableReferences(Class boClass) {
		HashSet<String> references = new HashSet<String>();
		for (String referenceName : getPersistenceStructureService().listReferenceObjectFields(boClass).keySet()) {
			if (isRelationshipRefreshable(boClass, referenceName)) {
				references.add(referenceName);
			}
		}
		for (String collectionName : getPersistenceStructureService().listCollectionObjectTypes(boClass).keySet()) {
			if (isRelationshipRefreshable(boClass, collectionName)) {
				references.add(collectionName);
			}
		}
		for (String relationshipName : getDataDictionaryService().getRelationshipNames(boClass.getName())) {
			if (isRelationshipRefreshable(boClass, relationshipName)) {
				references.add(relationshipName);
			}
		}
		return references;
	}

	protected void setNewCollectionLineDefaultValues(String collectionName, PersistableBusinessObject addLine) {
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(addLine);
		for (int i = 0; i < descriptors.length; ++i) {
			PropertyDescriptor propertyDescriptor = descriptors[i];

			String fieldName = propertyDescriptor.getName();
			Class propertyType = propertyDescriptor.getPropertyType();
			String value = getMaintenanceDocumentDictionaryService().getCollectionFieldDefaultValue(docTypeName,
					collectionName, fieldName);

			if (value != null) {
				try {
					ObjectUtils.setObjectProperty(addLine, fieldName, propertyType, value);
				}
				catch (Exception ex) {
					LOG.error("Unable to set default property of collection object: " + "\nobject: " + addLine
							+ "\nfieldName=" + fieldName + "\npropertyType=" + propertyType + "\nvalue=" + value, ex);
				}
			}

		}
	}

	public void doRouteStatusChange(DocumentHeader documentHeader) {
	}

	public List<Long> getWorkflowEngineDocumentIdsToLock() {
		return null;
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#clearBusinessObjectOfRestrictedValues(org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions)
	 */
	public void clearBusinessObjectOfRestrictedValues(MaintenanceDocumentRestrictions maintenanceDocumentRestrictions) {
		List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService()
				.getMaintainableSections(docTypeName);
		for (MaintainableSectionDefinition sectionDefinition : sections) {
			for (MaintainableItemDefinition itemDefinition : sectionDefinition.getMaintainableItems()) {
				if (itemDefinition instanceof MaintainableFieldDefinition) {
					clearFieldRestrictedValues("", businessObject, (MaintainableFieldDefinition) itemDefinition,
							maintenanceDocumentRestrictions);
				}
				else if (itemDefinition instanceof MaintainableCollectionDefinition) {
					clearCollectionRestrictedValues("", businessObject,
							(MaintainableCollectionDefinition) itemDefinition, maintenanceDocumentRestrictions);
				}
			}
		}
	}

	protected void clearCollectionRestrictedValues(String fieldNamePrefix, BusinessObject businessObject,
			MaintainableCollectionDefinition collectionDefinition,
			MaintenanceDocumentRestrictions maintenanceDocumentRestrictions) {
		String collectionName = fieldNamePrefix + collectionDefinition.getName();
		Collection<BusinessObject> collection = (Collection<BusinessObject>) ObjectUtils.getPropertyValue(
				businessObject, collectionDefinition.getName());

		if (collection != null) {
			int i = 0;
			// even though it's technically a Collection, we're going to index
			// it like a list
			for (BusinessObject collectionItem : collection) {
				String collectionItemNamePrefix = collectionName + "[" + i + "].";
				for (MaintainableCollectionDefinition subCollectionDefinition : collectionDefinition
						.getMaintainableCollections()) {
					clearCollectionRestrictedValues(collectionItemNamePrefix, collectionItem, subCollectionDefinition,
							maintenanceDocumentRestrictions);
				}
				for (MaintainableFieldDefinition fieldDefinition : collectionDefinition.getMaintainableFields()) {
					clearFieldRestrictedValues(collectionItemNamePrefix, collectionItem, fieldDefinition,
							maintenanceDocumentRestrictions);
				}
				i++;
			}
		}
	}

	protected void clearFieldRestrictedValues(String fieldNamePrefix, BusinessObject businessObject,
			MaintainableFieldDefinition fieldDefinition, MaintenanceDocumentRestrictions maintenanceDocumentRestrictions) {
		String fieldName = fieldNamePrefix + fieldDefinition.getName();

		FieldRestriction fieldRestriction = maintenanceDocumentRestrictions.getFieldRestriction(fieldName);
		if (fieldRestriction.isRestricted()) {
			String defaultValue = null;
			if (StringUtils.isNotBlank(fieldDefinition.getDefaultValue())) {
				defaultValue = fieldDefinition.getDefaultValue();
			}
			else if (fieldDefinition.getDefaultValueFinderClass() != null) {
				try {
					defaultValue = ((ValueFinder) fieldDefinition.getDefaultValueFinderClass().newInstance())
							.getValue();
				}
				catch (Exception e) {
					defaultValue = null;
					LOG.error("Error trying to instantiate ValueFinder or to determine ValueFinder for doc type: "
							+ docTypeName + " field name " + fieldDefinition.getName() + " with field prefix: "
							+ fieldNamePrefix, e);
				}
			}
			try {
				ObjectUtils.setObjectProperty(businessObject, fieldDefinition.getName(), defaultValue);
			}
			catch (Exception e) {
				// throw an exception, because we don't want users to be able to
				// see the restricted value
				LOG.error("Unable to clear maintenance document values for field name: " + fieldName
						+ " default value: " + defaultValue, e);
				throw new RuntimeException("Unable to clear maintenance document values for field name: " + fieldName,
						e);
			}
		}
	}

	protected void performForceUpperCase(Map fieldValues) {
		List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService()
				.getMaintainableSections(docTypeName);
		for (MaintainableSectionDefinition sectionDefinition : sections) {
			for (MaintainableItemDefinition itemDefinition : sectionDefinition.getMaintainableItems()) {
				if (itemDefinition instanceof MaintainableFieldDefinition) {
					performFieldForceUpperCase("", businessObject, (MaintainableFieldDefinition) itemDefinition,
							fieldValues);
				}
				else if (itemDefinition instanceof MaintainableCollectionDefinition) {
					performCollectionForceUpperCase("", businessObject,
							(MaintainableCollectionDefinition) itemDefinition, fieldValues);

				}
			}
		}
	}

	protected void performFieldForceUpperCase(String fieldNamePrefix, BusinessObject bo,
			MaintainableFieldDefinition fieldDefinition, Map fieldValues) {
		MessageMap errorMap = GlobalVariables.getMessageMap();
		String fieldName = fieldDefinition.getName();
		String mapKey = fieldNamePrefix + fieldName;
		if (fieldValues != null && fieldValues.get(mapKey) != null) {
			if (PropertyUtils.isWriteable(bo, fieldName) && ObjectUtils.getNestedValue(bo, fieldName) != null) {

				try {
					Class type = ObjectUtils.easyGetPropertyType(bo, fieldName);
					// convert to upperCase based on data dictionary
					Class businessObjectClass = bo.getClass();
					boolean upperCase = false;
					try {
						upperCase = getDataDictionaryService().getAttributeForceUppercase(businessObjectClass,
								fieldName);
					}
					catch (UnknownBusinessClassAttributeException t) {
						boolean catchme = true;
						// throw t;
					}

					Object fieldValue = ObjectUtils.getNestedValue(bo, fieldName);

					if (upperCase && fieldValue instanceof String) {
						fieldValue = ((String) fieldValue).toUpperCase();
					}
					ObjectUtils.setObjectProperty(bo, fieldName, type, fieldValue);
				}
				catch (FormatException e) {
					errorMap.putError(fieldName, e.getErrorKey(), e.getErrorArgs());
				}
				catch (IllegalAccessException e) {
					LOG.error("unable to populate business object" + e.getMessage());
					throw new RuntimeException(e.getMessage(), e);
				}
				catch (InvocationTargetException e) {
					LOG.error("unable to populate business object" + e.getMessage());
					throw new RuntimeException(e.getMessage(), e);
				}
				catch (NoSuchMethodException e) {
					LOG.error("unable to populate business object" + e.getMessage());
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	protected void performCollectionForceUpperCase(String fieldNamePrefix, BusinessObject bo,
			MaintainableCollectionDefinition collectionDefinition, Map fieldValues) {
		String collectionName = fieldNamePrefix + collectionDefinition.getName();
		Collection<BusinessObject> collection = (Collection<BusinessObject>) ObjectUtils.getPropertyValue(bo,
				collectionDefinition.getName());
		if (collection != null) {
			int i = 0;
			// even though it's technically a Collection, we're going to index
			// it like a list
			for (BusinessObject collectionItem : collection) {
				String collectionItemNamePrefix = collectionName + "[" + i + "].";
				// String collectionItemNamePrefix = "";
				for (MaintainableFieldDefinition fieldDefinition : collectionDefinition.getMaintainableFields()) {
					performFieldForceUpperCase(collectionItemNamePrefix, collectionItem, fieldDefinition, fieldValues);
				}
				for (MaintainableCollectionDefinition subCollectionDefinition : collectionDefinition
						.getMaintainableCollections()) {
					performCollectionForceUpperCase(collectionItemNamePrefix, collectionItem, subCollectionDefinition,
							fieldValues);
				}
				i++;
			}
		}
	}

	protected void performFieldForceUpperCase(BusinessObject bo, Map fieldValues) {
		MessageMap errorMap = GlobalVariables.getMessageMap();

		try {
			for (Iterator iter = fieldValues.keySet().iterator(); iter.hasNext();) {
				String propertyName = (String) iter.next();

				if (PropertyUtils.isWriteable(bo, propertyName) && fieldValues.get(propertyName) != null) {
					// if the field propertyName is a valid property on the bo
					// class
					Class type = ObjectUtils.easyGetPropertyType(bo, propertyName);
					try {
						// Keep the convert to upperCase logic here. It will be
						// used in populateNewCollectionLines,
						// populateNewSubCollectionLines
						// convert to upperCase based on data dictionary
						Class businessObjectClass = bo.getClass();
						boolean upperCase = false;
						try {
							upperCase = getDataDictionaryService().getAttributeForceUppercase(businessObjectClass,
									propertyName);
						}
						catch (UnknownBusinessClassAttributeException t) {
							boolean catchme = true;
							// throw t;
						}

						Object fieldValue = fieldValues.get(propertyName);

						if (upperCase && fieldValue instanceof String) {
							fieldValue = ((String) fieldValue).toUpperCase();
						}
						ObjectUtils.setObjectProperty(bo, propertyName, type, fieldValue);
					}
					catch (FormatException e) {
						errorMap.putError(propertyName, e.getErrorKey(), e.getErrorArgs());
					}
				}
			}
		}
		catch (IllegalAccessException e) {
			LOG.error("unable to populate business object" + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
		catch (InvocationTargetException e) {
			LOG.error("unable to populate business object" + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
		catch (NoSuchMethodException e) {
			LOG.error("unable to populate business object" + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	/**
	 * By default a maintainable is not external
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#isExternalBusinessObject()
	 */
	public boolean isExternalBusinessObject() {
		return false;
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#getExternalBusinessObject()
	 */
	public void prepareBusinessObject(BusinessObject businessObject) {
		// Do nothing by default
	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#getLockingDocumentId()
	 */
	public String getLockingDocumentId() {
		return getMaintenanceDocumentService().getLockingDocumentId(this, documentNumber);
	}

	/**
	 * This default implementation simply returns false to indicate that custom
	 * lock descriptors are not supported by KualiMaintainableImpl. If custom
	 * lock descriptors are needed, the appropriate subclasses should override
	 * this method.
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#useCustomLockDescriptors()
	 */
	public boolean useCustomLockDescriptors() {
		return false;
	}

	/**
	 * This default implementation just throws a PessimisticLockingException.
	 * Subclasses of KualiMaintainableImpl that need support for custom lock
	 * descriptors should override this method.
	 * 
	 * @see org.kuali.rice.kns.maintenance.Maintainable#getCustomLockDescriptor(org.kuali.rice.kim.bo.Person)
	 */
	public String getCustomLockDescriptor(Person user) {
		throw new PessimisticLockingException(
				"The Maintainable for document "
						+ documentNumber
						+ " is using pessimistic locking with custom lock descriptors, but the Maintainable has not overriden the getCustomLockDescriptor method");
	}

	// 3070
	public void deleteBusinessObject() {
		if (businessObject == null)
			return;

		KNSServiceLocator.getBusinessObjectService().delete(businessObject);
		businessObject = null;
	}

	public boolean isOldBusinessObjectInDocument() {
		boolean isOldBusinessObjectInExistence = false;
		if (getBusinessObject() == null) {
			isOldBusinessObjectInExistence = false;
		}
		else {
			if (KNSServiceLocator.getPersistenceStructureService().isPersistable(getBusinessObject().getClass())) {
				isOldBusinessObjectInExistence = KNSServiceLocator.getPersistenceStructureService()
						.hasPrimaryKeyFieldValues(getBusinessObject());
			}
		}
		return isOldBusinessObjectInExistence;

	}

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#setupMaintenanceObject(org.kuali.rice.kns.document.MaintenanceDocument,
	 *      java.lang.String, java.util.Map)
	 */
	public void setupMaintenanceObject(MaintenanceDocument document, String maintenanceAction,
			Map<String, String[]> requestParameters) {
		setMaintenanceAction(maintenanceAction);
		document.getOldMaintainableObject().setMaintenanceAction(maintenanceAction);

		// if action is edit or copy first need to retrieve the old record
		if (!KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)
				&& !KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
			Object oldBusinessObject = retrieveObjectForMaintenance(document, requestParameters);

			// TODO should we be using ObjectUtils? also, this needs dictionary
			// enhancement to indicate fields to/not to copy
			Object newBusinessObject = ObjectUtils.deepCopy((Serializable) oldBusinessObject);

			// process further object preparations for copy action
			if (KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
				processMaintenanceObjectForCopy(document, newBusinessObject, requestParameters);
			}
			else {
				checkMaintenanceActionAuthorization(document, oldBusinessObject, requestParameters);
			}

			// set object instance for editing
			document.getOldMaintainableObject().setDataObject(oldBusinessObject);
			document.getNewMaintainableObject().setDataObject(newBusinessObject);
		}

		// if new with existing we need to populate with passed in parameters
		if (KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
			Object newBO = document.getNewMaintainableObject().getDataObject();
			Map<String, String> parameters = buildKeyMapFromRequest(requestParameters);
			ObjectPropertyUtils.copyPropertiesToObject(parameters, newBO);
			if(newBO instanceof PersistableBusinessObject) {
			    ((PersistableBusinessObject)newBO).refresh();
			}

			setupNewFromExisting(document, requestParameters);
		}
		else if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
			processAfterNew(document, requestParameters);
		}
	}

	/**
	 * For the edit and delete maintenance actions checks with the
	 * <code>BusinessObjectAuthorizationService</code> to check whether the
	 * action is allowed for the record data. In action is allowed invokes the
	 * custom processing hook on the <code>Maintainble</code>.
	 * 
	 * @param document
	 *            - document instance for the maintenance object
	 * @param oldBusinessObject
	 *            - the old maintenance record
	 * @param requestParameters
	 *            - map of parameters from the request
	 */
	protected void checkMaintenanceActionAuthorization(MaintenanceDocument document, Object oldBusinessObject,
			Map<String, String[]> requestParameters) {
		if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {
			boolean allowsEdit = getBusinessObjectAuthorizationService().canMaintain(
					oldBusinessObject, GlobalVariables.getUserSession().getPerson(),
					document.getDocumentHeader().getWorkflowDocument().getDocumentType());
			if (!allowsEdit) {
				LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentType()
						+ " does not allow edit actions.");
				throw new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson()
						.getPrincipalId(), "edit", document.getDocumentHeader().getWorkflowDocument().getDocumentType());
			}

			// invoke custom processing method
			processAfterEdit(document, requestParameters);
		}
		// 3070
		else if (KNSConstants.MAINTENANCE_DELETE_ACTION.equals(maintenanceAction)) {
			boolean allowsDelete = getBusinessObjectAuthorizationService().canMaintain(
					(BusinessObject) oldBusinessObject, GlobalVariables.getUserSession().getPerson(),
					document.getDocumentHeader().getWorkflowDocument().getDocumentType());
			if (!allowsDelete) {
				LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentType()
						+ " does not allow delete actions.");
				throw new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson()
						.getPrincipalId(), "delete", document.getDocumentHeader().getWorkflowDocument()
						.getDocumentType());
			}
		}
	}

	/**
	 * For the edit or copy actions retrieves the record that is to be
	 * maintained
	 * 
	 * <p>
	 * Based on the persistence metadata for the maintenance object class
	 * retrieves the primary key values from the given request parameters map
	 * (if the class is persistable). With those key values attempts to find the
	 * record using the <code>LookupService</code>.
	 * </p>
	 * 
	 * @param document
	 *            - document instance for the maintenance object
	 * @param requestParameters
	 *            - Map of parameters from the request
	 * @return Object the retrieved old object
	 */
	protected Object retrieveObjectForMaintenance(MaintenanceDocument document, Map<String, String[]> requestParameters) {
	    // TODO the logic in this method is very odd, but I don't know how clients
	    // are extending this impl.  It seems like clients made non persistent objects
	    // extend PBO in order to not break the framework which requires PBO everywhere,
	    // KRAD is fixing that.
	    //
	    // Would like to see this method get passed keys already parsed from request,
	    // it seems odd to have entire http request map passed this deep

		Map<String, String> keyMap = buildKeyMapFromRequest(requestParameters);
		
		Object oldBusinessObject = retrieveObjectForEditOrCopy(document, keyMap);
		
		if (oldBusinessObject == null && !document.getOldMaintainableObject().isExternalBusinessObject()) {
			throw new RuntimeException(
					"Cannot retrieve old record for maintenance document, incorrect parameters passed on maint url: "
							+ requestParameters);
		}

		if (document.getOldMaintainableObject().isExternalBusinessObject()) {
			if (oldBusinessObject == null) {
				try {
					oldBusinessObject = document.getOldMaintainableObject().getBoClass()
							.newInstance();
				}
				catch (Exception ex) {
					throw new RuntimeException(
							"External BO maintainable was null and unable to instantiate for old maintainable object.",
							ex);
				}
			}

			populateMaintenanceObjectWithCopyKeyValues(WebUtils.translateRequestParameterMap(requestParameters),
					oldBusinessObject, document.getOldMaintainableObject());
			document.getOldMaintainableObject().prepareBusinessObject((PersistableBusinessObject)oldBusinessObject);
			oldBusinessObject = document.getOldMaintainableObject().getDataObject();
		}

		return oldBusinessObject;
	}
	

	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#retrieveObjectForEditOrCopy(java.util.Map)
	 */
	@Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {
	    Object dataObject = null;
	    
	    try {
            dataObject = getLookupService().findObjectBySearch(getBoClass(), dataObjectKeys);
        }
        catch (ClassNotPersistenceCapableException ex) {
            if (!document.getOldMaintainableObject().isExternalBusinessObject()) {
                throw new RuntimeException("BO Class: " + getBoClass()
                        + " is not persistable and is not externalizable - configuration error");
            }
            // otherwise, let fall through
        }
        
        return dataObject;
    }

    /**
	 * For the copy action clears out primary key values for the old record and
	 * does authorization checks on the remaining fields. Also invokes the
	 * custom processing method on the <code>Maintainble</code>
	 * 
	 * @param document
	 *            - document instance for the maintenance object
	 * @param maintenanceObject
	 *            - the object instance being maintained
	 * @param requestParameters
	 *            - map of parameters from the request
	 */
	protected void processMaintenanceObjectForCopy(MaintenanceDocument document, Object maintenanceObject,
			Map<String, String[]> requestParameters) {
		if (!document.isFieldsClearedOnCopy()) {
			if (!maintenanceDocumentDictionaryService.getPreserveLockingKeysOnCopy(getBoClass())) {
				clearPrimaryKeyFields(maintenanceObject);
			}

			clearUnauthorizedNewFields(document);

			Maintainable maintainable = document.getNewMaintainableObject();
			maintainable.processAfterCopy(document, requestParameters);

			// mark so that this clearing does not happen again
			document.setFieldsClearedOnCopy(true);
		}
	}

	/**
	 * Clears the value of the primary key fields on the maintenance object
	 * 
	 * @param document
	 *            - document to clear the pk fields on
	 */
	protected void clearPrimaryKeyFields(Object maintenanceObject) {
		List<String> keyFieldNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBoClass());
		for (String keyFieldName : keyFieldNames) {
			ObjectPropertyUtils.setPropertyValue(maintenanceObject, keyFieldName, null);
		}
	}

	/**
	 * Used as part of the Copy functionality, to clear any field values that
	 * the user making the copy does not have permissions to modify. This will
	 * prevent authorization errors on a copy.
	 * 
	 * @param document
	 *            - document to be adjusted
	 */
	protected void clearUnauthorizedNewFields(MaintenanceDocument document) {
		// get a reference to the current user
		Person user = GlobalVariables.getUserSession().getPerson();

		// get a new instance of MaintenanceDocumentAuthorizations for context
		MaintenanceDocumentRestrictions maintenanceDocumentRestrictions = getBusinessObjectAuthorizationService()
				.getMaintenanceDocumentRestrictions(document, user);

		clearBusinessObjectOfRestrictedValues(maintenanceDocumentRestrictions);
	}

	/**
	 * Based on the maintenance object class retrieves the key field names from
	 * the <code>BusinessObjectMetaDataService</code> (or alternatively from the
	 * request parameters), then retrieves any matching key value pairs from the
	 * request parameters
	 * 
	 * @param requestParameters
	 *            - map of parameters from the request
	 * @return Map<String, String> key value pairs
	 */
	protected Map<String, String> buildKeyMapFromRequest(Map<String, String[]> requestParameters) {
		List<String> keyFieldNames = null;

		// translate request parameters
		Map<String, String> parameters = WebUtils.translateRequestParameterMap(requestParameters);

		// are override keys listed in the request? If so, then those need to be
		// our keys, not the primary key fields for the BO
		if (!StringUtils.isBlank(parameters.get(KNSConstants.OVERRIDE_KEYS))) {
			String[] overrideKeys = parameters.get(KNSConstants.OVERRIDE_KEYS).split(
					KNSConstants.FIELD_CONVERSIONS_SEPARATOR);
			keyFieldNames = Arrays.asList(overrideKeys);
		}
		else {
			keyFieldNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBoClass());
		}

		return WebUtils.getParametersFromRequest(keyFieldNames, getBoClass(), parameters);
	}

	/**
	 * Looks for a special request parameters giving the names of the keys that
	 * should be retrieved from the request parameters and copied to the
	 * maintenance object
	 * 
	 * @param parameters
	 *            - map of parameters from the request
	 * @param oldBusinessObject
	 *            - the old maintenance object
	 * @param oldMaintainableObject
	 *            - the old maintainble object (used to get object class for
	 *            security checks)
	 */
	protected void populateMaintenanceObjectWithCopyKeyValues(Map<String, String> parameters, Object oldBusinessObject,
			Maintainable oldMaintainableObject) {
		List<String> keyFieldNamesToCopy = null;
		Map<String, String> parametersToCopy = null;

		if (!StringUtils.isBlank(parameters.get(KNSConstants.COPY_KEYS))) {
			String[] copyKeys = parameters.get(KNSConstants.COPY_KEYS).split(KNSConstants.FIELD_CONVERSIONS_SEPARATOR);
			keyFieldNamesToCopy = Arrays.asList(copyKeys);
			parametersToCopy = WebUtils.getParametersFromRequest(keyFieldNamesToCopy,
					oldMaintainableObject.getBoClass(), parameters);
		}

		if (parametersToCopy != null) {
			// TODO: make sure we are doing formatting here eventually
			ObjectPropertyUtils.copyPropertiesToObject(parametersToCopy, oldBusinessObject);
		}
	}

	/**
	 * In the case of edit maintenance adds a new blank line to the old side
	 * 
	 * TODO: should this write some sort of missing message on the old side
	 * instead?
	 * 
	 * @see org.kuali.rice.kns.uif.service.impl.ViewHelperServiceImpl#processAfterAddLine(org.kuali.rice.kns.uif.container.View,
	 *      org.kuali.rice.kns.uif.container.CollectionGroup, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	protected void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
		super.processAfterAddLine(view, collectionGroup, model, addLine);

		if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {
			MaintenanceForm maintenanceForm = (MaintenanceForm) model;
			MaintenanceDocument document = maintenanceForm.getDocument();

			// get the old object's collection
			Collection<Object> oldCollection = ObjectPropertyUtils.getPropertyValue(document.getOldMaintainableObject()
					.getBusinessObject(), collectionGroup.getPropertyName());
			try {
				Object blankLine = getBoClass().newInstance();
				oldCollection.add(blankLine);
			}
			catch (Exception e) {
				throw new RuntimeException("Unable to create new line instance for old maintenance object", e);
			}
		}
	}

	protected PersistenceStructureService getPersistenceStructureService() {
		if (persistenceStructureService == null) {
			persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
		}
		return persistenceStructureService;
	}

	protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if (maintenanceDocumentDictionaryService == null) {
			maintenanceDocumentDictionaryService = KNSServiceLocatorWeb.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}

	protected BusinessObjectService getBusinessObjectService() {
		if (businessObjectService == null) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}

	protected BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
		if (businessObjectDictionaryService == null) {
			businessObjectDictionaryService = KNSServiceLocatorWeb.getBusinessObjectDictionaryService();
		}
		return businessObjectDictionaryService;
	}

	protected EncryptionService getEncryptionService() {
		if (encryptionService == null) {
			encryptionService = CoreApiServiceLocator.getEncryptionService();
		}
		return encryptionService;
	}

	protected org.kuali.rice.kim.service.PersonService getPersonService() {
		if (personService == null) {
			personService = KimApiServiceLocator.getPersonService();
		}
		return personService;
	}

	protected BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
		if (businessObjectMetaDataService == null) {
			businessObjectMetaDataService = KNSServiceLocatorWeb.getBusinessObjectMetaDataService();
		}
		return businessObjectMetaDataService;
	}

	protected BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
		if (businessObjectAuthorizationService == null) {
			businessObjectAuthorizationService = KNSServiceLocatorWeb.getBusinessObjectAuthorizationService();
		}
		return businessObjectAuthorizationService;
	}

	protected MaintenanceDocumentService getMaintenanceDocumentService() {
		if (maintenanceDocumentService == null) {
			maintenanceDocumentService = KNSServiceLocatorWeb.getMaintenanceDocumentService();
		}
		return maintenanceDocumentService;
	}

	protected DocumentHelperService getDocumentHelperService() {
		if (documentHelperService == null) {
			documentHelperService = KNSServiceLocatorWeb.getDocumentHelperService();
		}
		return documentHelperService;
	}

	public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
		this.persistenceStructureService = persistenceStructureService;
	}

	public void setMaintenanceDocumentDictionaryService(
			MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
		this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
		this.businessObjectDictionaryService = businessObjectDictionaryService;
	}

	public void setEncryptionService(EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}

	public void setPersonService(org.kuali.rice.kim.service.PersonService personService) {
		this.personService = personService;
	}

	public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
		this.businessObjectMetaDataService = businessObjectMetaDataService;
	}

	public void setBusinessObjectAuthorizationService(
			BusinessObjectAuthorizationService businessObjectAuthorizationService) {
		this.businessObjectAuthorizationService = businessObjectAuthorizationService;
	}

	public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
		this.maintenanceDocumentService = maintenanceDocumentService;
	}

	public void setDocumentHelperService(DocumentHelperService documentHelperService) {
		this.documentHelperService = documentHelperService;
	}

	protected LookupService getLookupService() {
		if (lookupService == null) {
			lookupService = KNSServiceLocatorWeb.getLookupService();
		}
		return this.lookupService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

}
