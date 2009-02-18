/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kns.maintenance;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.authorization.FieldRestriction;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.BusinessObjectRelationship;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.InactiveRecordsHidingUtils;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.kns.web.ui.SectionBridge;

/**
 * Base Maintainable class to hold things common to all maintainables.
 */
public class KualiMaintainableImpl implements Maintainable, Serializable {
    private static final long serialVersionUID = 4814145799502207182L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiMaintainableImpl.class);

    protected String documentNumber;
    protected PersistableBusinessObject businessObject;
    protected Class boClass;
    protected String maintenanceAction;
   
    
    protected Map<String,PersistableBusinessObject> newCollectionLines = new HashMap<String,PersistableBusinessObject>();
    protected Map<String, Boolean> inactiveRecordDisplay = new HashMap<String, Boolean>();
    
    private String docTypeName;

    private static PersistenceStructureService persistenceStructureService;
    
    private static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private static DataDictionaryService dataDictionaryService;
    private static BusinessObjectService businessObjectService;
    private static BusinessObjectDictionaryService businessObjectDictionaryService;
    private static EncryptionService encryptionService;
    private static org.kuali.rice.kim.service.PersonService personService;
    private static BusinessObjectMetaDataService businessObjectMetaDataService;
    private static BusinessObjectAuthorizationService businessObjectAuthorizationService;
    
    /**
     * Default empty constructor
     */
    public KualiMaintainableImpl() {
    }

    /**
     * Constructor which initializes the business object to be maintained.
     * 
     * @param businessObject
     */
    public KualiMaintainableImpl(PersistableBusinessObject businessObject) {
        this();
        this.businessObject = businessObject;
    }

    public void setupNewFromExisting( MaintenanceDocument document, Map<String,String[]> parameters ) {
        
    }

    public void processAfterPost( MaintenanceDocument document, Map<String,String[]> parameters ) {
        
    }
    
    /**
     * This is a hook to allow the document to override the generic document title.
     * 
     * @return String   document title
     */
    public String getDocumentTitle(MaintenanceDocument document) {
        //default implementation is to allow MaintenanceDocumentBase to generate the doc title
        return "";
    }

    /**
     * Note: as currently implemented, every key field for a given BusinessObject subclass must have a visible getter.
     * 
     * @return String containing the business object class and key value pairs of the current instance that can be used as a unique
     *         locking representation.
     */
    public List<MaintenanceLock> generateMaintenanceLocks() {

        //NOTE: KualiGlobalMaintainableImpl overrides this method and forces all globals to override that, so they each do their own thing
        
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        StringBuffer lockRepresentation = new StringBuffer(boClass.getName());
        lockRepresentation.append(KNSConstants.Maintenance.AFTER_CLASS_DELIM);

        PersistableBusinessObject bo = getBusinessObject();
        List keyFieldNames = getMaintenanceDocumentDictionaryService().getLockingKeys(getDocumentTypeName());

        for (Iterator i = keyFieldNames.iterator(); i.hasNext();) {
            String fieldName = (String) i.next();
            Object fieldValue = ObjectUtils.getPropertyValue(bo, fieldName);
            if (fieldValue == null) {
                fieldValue = "";
            }
            
            // check if field is a secure
            if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(boClass, fieldName)) {
                try {
                    fieldValue = getEncryptionService().encrypt(fieldValue);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
                    throw new RuntimeException("Unable to encrypt secure field for locking representation " + e.getMessage());
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
     * @see org.kuali.rice.kns.maintenance.Maintainable#populateBusinessObject(java.util.Map)
     */
    public Map populateBusinessObject(Map fieldValues, MaintenanceDocument maintenanceDocument) {
        fieldValues = decryptEncryptedData(fieldValues, maintenanceDocument);
        Map newFieldValues = null;
        newFieldValues = getPersonService().resolvePrincipalNamesToPrincipalIds(getBusinessObject(), fieldValues);
   
        Map cachedValues = FieldUtils.populateBusinessObjectFromMap(getBusinessObject(), newFieldValues);
        getBusinessObjectDictionaryService().performForceUppercase(getBusinessObject());
        return cachedValues;
    }


    /**
     * Special hidden parameters are set on the maintenance jsp starting with a prefix that tells us which fields have been
     * encrypted. This field finds the those parameters in the map, whose value gives us the property name that has an encrypted
     * value. We then need to decrypt the value in the Map before the business object is populated.
     * 
     * @param fieldValues - possibly with encrypted values
     * @return Map fieldValues - with no encrypted values
     */
    private Map decryptEncryptedData(Map fieldValues, MaintenanceDocument maintenanceDocument) {
    	try {
    		MaintenanceDocumentRestrictions auths = KNSServiceLocator.getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(maintenanceDocument, GlobalVariables.getUserSession().getPerson());
	        for (Iterator iter = fieldValues.keySet().iterator(); iter.hasNext();) {
                String fieldName = (String) iter.next();
                String fieldValue = (String) fieldValues.get(fieldName);

                if (fieldValue != null &&!"".equals(fieldValue) && fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)){
                	if(shouldFieldBeEncrypted(maintenanceDocument, fieldName, auths)){
	                    String encryptedValue = fieldValue;
	
	                    // take off the postfix
	                    encryptedValue = StringUtils.stripEnd(encryptedValue, EncryptionService.ENCRYPTION_POST_PREFIX);
	                    String decryptedValue = getEncryptionService().decrypt(encryptedValue);
	
	                    fieldValues.put(fieldName, decryptedValue);
                	} else
                		throw new RuntimeException(
                			"The field value for field name "+fieldName+" should not be encrypted. Value received: "+fieldValue);
            	} else if(fieldValue != null &&!"".equals(fieldValue)  && shouldFieldBeEncrypted(maintenanceDocument, fieldName, auths))
            		throw new RuntimeException(
            			"The field value for field name "+fieldName+" should be encrypted. Value received: "+fieldValue);
	        	} 
	        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException("Unable to decrypt secure data: " + e.getMessage());
        }
       
        return fieldValues;
    }

    private boolean shouldFieldBeEncrypted(MaintenanceDocument maintenanceDocument, String fieldName, MaintenanceDocumentRestrictions auths){
    	// If the user does not have appropriate permissions, a non-blank displayEditMode implies that this field should be encrypted
    	// If the logged in user has the permission to view or edit this field, 
    	// editMap will have an entry corresponding to displayEditMode, in which case, the field value received will not be encrypted
    	// The corresponding value in editMap actually does not matter;
    	// just the presence of the displayEditMode inside that map is enough.
    	// Note: this "if" stmt is same as "${field.secure && empty KualiForm.editingMode[field.displayEditMode]}" of rowDisplay.jsp
    	if(auths!=null && auths.hasRestriction(fieldName)){
    		FieldRestriction fieldAuth = auths.getFieldRestriction(fieldName);
    		return fieldAuth.isShouldBeEncrypted();
    	}
    	return false;
    }
    
    /*private String getDisplayEditMode(Document maintenanceDocument, String fieldName){
    	String docTypeName = maintenanceDocument.getDocumentHeader().getWorkflowDocument().getDocumentType();
    	MaintainableFieldDefinition fieldDefinition =
    		KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintainableField(docTypeName, fieldName);
    	return fieldDefinition==null?null:fieldDefinition.getDisplayEditMode();
	}*/
    
    /**
     * Calls method to get all the core sections for the business object defined in the data dictionary. Then determines if the bo
     * has custom attributes, if so builds a custom attribute section and adds to the section list.
     * 
     * @return List of org.kuali.ui.Section objects
     */
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = new ArrayList<Section>();
        sections.addAll(getCoreSections(document, oldMaintainable));
        
        return sections;
    }
    

    /**
     * Gets list of maintenance sections built from the data dictionary. If the section contains maintenance fields, construct
     * Row/Field UI objects and place under Section UI. If section contains a maintenance collection, call method to build a Section
     * UI which contains rows of Container Fields.
     * 
     * @return List of org.kuali.ui.Section objects
     */
    public List<Section> getCoreSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        
        List<Section> sections = new ArrayList<Section>();
        MaintenanceDocumentRestrictions maintenanceRestrictions = KNSServiceLocator.getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document, GlobalVariables.getUserSession().getPerson());
        
        List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);

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
                	
                	Section section = SectionBridge.toSection(maintSectionDef, getBusinessObject(), this, oldMaintainable, getMaintenanceAction(), displayedFieldNames);
                	if(maintenanceRestrictions.isReadOnlySectionId(maintSectionDef.getId())){
                		section.setReadOnly(true);
                	}
                
                	// add to section list
                	sections.add(section);
                }
                
            }
            
        } catch (InstantiationException e) {
            LOG.error("Unable to create instance of object class" + e.getMessage());
            throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
        } catch (IllegalAccessException e) {
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
    	return getBusinessObjectDictionaryService().areNotesSupported(this.boClass);
    }
    
    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#refresh(java.lang.String, java.util.Map) Impls will be needed if custom action
     *      is needed on refresh.
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
                            if ( LOG.isDebugEnabled() ) {
                                LOG.debug("Refresh this \"new\"/add object for the collections:  " + reference);
                            }
                            if (persistenceStructureService.hasReference(addlineBOClass, propertyToRefresh) ||
                                    persistenceStructureService.hasCollection(addlineBOClass, propertyToRefresh)) {
                                addlineBO.refreshReferenceObject(propertyToRefresh);
                            }
                            else {
                                if (getDataDictionaryService().hasRelationship(addlineBOClass.getName(), propertyToRefresh)) {
                                    // a DD mapping, try to go straight to the object and refresh it there
                                    Object possibleBO = ObjectUtils.getPropertyValue(addlineBO, propertyToRefresh);
                                    if (possibleBO != null && possibleBO instanceof PersistableBusinessObject) {
                                        ((PersistableBusinessObject) possibleBO).refresh();
                                    }
                                }
                            }
                        }
                        else {
                            LOG.error("Error: unable to refresh this \"new\"/add object for the collections:  " + reference);
                        }
                    }
                    else if (ObjectUtils.isNestedAttribute(reference)) {
                        Object nestedObject = ObjectUtils.getNestedValue(getBusinessObject(), ObjectUtils.getNestedAttributePrefix(reference));
                        if (nestedObject instanceof Collection) {
                            // do nothing, probably because it's not really a collection reference but a relationship defined in the DD for a collections lookup
                            // this part will need to be rewritten when the DD supports true collection references   
                        }
                        else if (nestedObject instanceof PersistableBusinessObject) {
                            String propertyToRefresh = ObjectUtils.getNestedAttributePrimitive(reference);
                            if (persistenceStructureService.hasReference(nestedObject.getClass(), propertyToRefresh) ||
                                    persistenceStructureService.hasCollection(nestedObject.getClass(), propertyToRefresh)) {
                                if ( LOG.isDebugEnabled() ) {
                                    LOG.debug("Refeshing " + ObjectUtils.getNestedAttributePrefix(reference) + " " + ObjectUtils.getNestedAttributePrimitive(reference));
                                }
                                ((PersistableBusinessObject) nestedObject).refreshReferenceObject(propertyToRefresh);
                            }
                            else {
                                // a DD mapping, try to go straight to the object and refresh it there
                                Object possibleBO = ObjectUtils.getPropertyValue(nestedObject, propertyToRefresh);
                                if (possibleBO != null && possibleBO instanceof PersistableBusinessObject) {
                                    if (getDataDictionaryService().hasRelationship(possibleBO.getClass().getName(), propertyToRefresh)) {
                                        ((PersistableBusinessObject) possibleBO).refresh();
                                    }
                                }
                            }
                        }
                        else {
                            LOG.warn("Expected that a referenceToRefresh (" + reference + ")  would be a PersistableBusinessObject or Collection, but instead, it was of class " + nestedObject.getClass().getName());
                        }
                    }
                    else {
                        if ( LOG.isDebugEnabled() ) {
                            LOG.debug("Refreshing " + reference);
                        }
                        if (persistenceStructureService.hasReference(boClass, reference) ||
                                persistenceStructureService.hasCollection(boClass, reference)) {
                            getBusinessObject().refreshReferenceObject(reference);
                        }
                        else {
							if (getDataDictionaryService().hasRelationship(getBusinessObject().getClass().getName(), reference)) {
                                // a DD mapping, try to go straight to the object and refresh it there
                                Object possibleRelationship = ObjectUtils.getPropertyValue(getBusinessObject(), reference);
                                if (possibleRelationship != null) {
                                    if (possibleRelationship instanceof PersistableBusinessObject) {
                                        ((PersistableBusinessObject) possibleRelationship).refresh();
                                    }
                                    else if (possibleRelationship instanceof Collection) {
                                        // do nothing, probably because it's not really a collection reference but a relationship defined in the DD for a collections lookup
                                        // this part will need to be rewritten when the DD supports true collection references   
                                    }
                                    else {
                                        LOG.warn("Expected that a referenceToRefresh (" + reference + 
                                                ")  would be a PersistableBusinessObject or Collection, but instead, it was of class " + 
                                                possibleRelationship.getClass().getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    

    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {
        Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(bo, collectionName);
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        
        List<String> duplicateIdentifierFieldsFromDataDictionary = getDuplicateIdentifierFieldsFromDataDictionary(docTypeName, collectionName);
        
        List<String> existingIdentifierList = getMultiValueIdentifierList(maintCollection, duplicateIdentifierFieldsFromDataDictionary);
        
        Class collectionClass = getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass(docTypeName, collectionName);

        List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);
        Map<String, String> template = MaintenanceUtils.generateMultipleValueLookupBOTemplate(sections, collectionName);
        try {
            for (PersistableBusinessObject nextBo : rawValues) {
                PersistableBusinessObject templatedBo;
                if(needsBlank) {
                    templatedBo = (PersistableBusinessObject) collectionClass.newInstance();                    
                } else {
                    templatedBo = (PersistableBusinessObject) ObjectUtils.createHybridBusinessObject(collectionClass, nextBo, template);
                
                    prepareBusinessObjectForAdditionFromMultipleValueLookup(collectionName, templatedBo);
                }
                    templatedBo.setNewCollectionRecord(true);
                
                if (!hasBusinessObjectExisted(templatedBo, existingIdentifierList, duplicateIdentifierFieldsFromDataDictionary)) {   
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
     * This method is to retrieve a List of fields which are specified in the maintenance document
     * data dictionary as the duplicateIdentificationFields. This List is used to determine whether
     * the new entry being added to the collection is a duplicate entry and if so, we should not
     * add the new entry to the existing collection
     * 
     * @param docTypeName
     * @param collectionName
     */
    public List<String> getDuplicateIdentifierFieldsFromDataDictionary(String docTypeName, String collectionName) {
    	List<String> duplicateIdentifierFieldNames = new ArrayList<String>();
    	MaintainableCollectionDefinition collDef = getMaintenanceDocumentDictionaryService().getMaintainableCollection(docTypeName, collectionName);
    	Collection<MaintainableFieldDefinition> fieldDef = collDef.getDuplicateIdentificationFields();
    	for (MaintainableFieldDefinition eachFieldDef : fieldDef) {
    		duplicateIdentifierFieldNames.add(eachFieldDef.getName());
    	}
    	return duplicateIdentifierFieldNames;
    }
    

    public List<String> getMultiValueIdentifierList(Collection maintCollection, List<String> duplicateIdentifierFields) {
        List<String> identifierList = new ArrayList<String>();
        for (PersistableBusinessObject bo : (Collection<PersistableBusinessObject>)maintCollection) {
        	String uniqueIdentifier = new String();
        	for (String identifierField : duplicateIdentifierFields) {
                    uniqueIdentifier = uniqueIdentifier + identifierField + "-" + ObjectUtils.getPropertyValue(bo, identifierField);
        	}
        	if (StringUtils.isNotEmpty(uniqueIdentifier)) {
                identifierList.add(uniqueIdentifier);
        	}
        }
        return identifierList;
    } 
    
    public boolean hasBusinessObjectExisted(BusinessObject bo, List<String> existingIdentifierList, List<String> duplicateIdentifierFields) {
    	String uniqueIdentifier = new String();
        for (String identifierField : duplicateIdentifierFields) {
            uniqueIdentifier = uniqueIdentifier + identifierField + "-" + ObjectUtils.getPropertyValue(bo, identifierField);
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
	if ( businessObject != null ) {
	    businessObject.prepareForWorkflow();
	}
    }

    /**
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterRetrieve()
     */
    public void processAfterRetrieve() {
    }
    
	/** 
     * Set the new collection records back to true so they can be deleted (copy should act like new)
     * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
	 */
	public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        try {
            ObjectUtils.setObjectPropertyDeep(businessObject, KNSPropertyConstants.NEW_COLLECTION_RECORD, boolean.class, true, 2);
        }
        catch (Exception e) {
            LOG.error("unable to set newCollectionRecord property: " + e.getMessage(), e);
            throw new RuntimeException("unable to set newCollectionRecord property: " + e.getMessage(), e);
        }
	}
    
    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterEdit()
     */
    public void processAfterEdit( MaintenanceDocument document, Map<String,String[]> parameters ) {
    }

    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterNew()
     */
    public void processAfterNew( MaintenanceDocument document, Map<String,String[]> parameters ) {
    }
    
    /**
     * Retrieves the document type name from the data dictionary based on business object class
     */
    private String getDocumentTypeName() {
        return getMaintenanceDocumentDictionaryService().getDocumentTypeName(boClass);
    }

    /**
     * @return Returns the instance of the business object being maintained.
     */
    public PersistableBusinessObject getBusinessObject() {
        return businessObject;
    }

    /**
     * @param businessObject Sets the instance of a business object that will be maintained.
     */
    public void setBusinessObject(PersistableBusinessObject businessObject) {
        this.businessObject = businessObject;
    }


    /**
     * @return Returns the boClass.
     */
    public Class getBoClass() {
        return boClass;
    }


    /**
     * @param boClass The boClass to set.
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
     * @param maintenanceAction The maintenanceAction to set.
     */
    public void setMaintenanceAction(String maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }


    /**
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#setGenerateDefaultValues()
     */
    public void setGenerateDefaultValues(String docTypeName) {
     List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);

     try {
         // iterate through section definitions
         for (Iterator iter = sectionDefinitions.iterator(); iter.hasNext();) {
             
             MaintainableSectionDefinition maintSectionDef = (MaintainableSectionDefinition) iter.next();
             Collection maintItems = maintSectionDef.getMaintainableItems();
             for (Iterator iterator = maintItems.iterator(); iterator.hasNext();) {
                 MaintainableItemDefinition item = (MaintainableItemDefinition) iterator.next();

                 if (item instanceof MaintainableFieldDefinition) {
                     MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) item;
                     if (maintainableFieldDefinition.isRequired() && maintainableFieldDefinition.isUnconditionallyReadOnly() ) {
                    	
                         Object defaultValue = maintainableFieldDefinition.getDefaultValue();
                         if (defaultValue != null) {
                             if (defaultValue.toString().equals("true")) {
                                 defaultValue = "Yes";
                             }
                             else if (defaultValue.toString().equals("false")) {
                                 defaultValue = "No";
                             }
                         }

                         Class defaultValueFinderClass = maintainableFieldDefinition.getDefaultValueFinderClass();
                         if (defaultValueFinderClass != null) {
                        	 defaultValue = ((ValueFinder) defaultValueFinderClass.newInstance()).getValue();
                            
                         }
                         if (defaultValue != null) {
                        	 ObjectUtils.setObjectProperty(this.getBusinessObject(), item.getName(), defaultValue.getClass(), defaultValue);
                         }
                     }
                 }
             }   
         }
     } catch(Exception e){
    	 LOG.error("Unable to set default value " + e.getMessage(), e);
    	 throw new RuntimeException("Unable to create instance of object class" + e.getMessage(), e);
     }
     

    }


    /**
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#setGenerateBlankRequiredValues()
     */
    public void setGenerateBlankRequiredValues(String docTypeName) {
    	
    	 List<Section> sections = new ArrayList<Section>();
         
         List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);

         try {
             // iterate through section definitions
             for (Iterator iter = sectionDefinitions.iterator(); iter.hasNext();) {
                 
                 MaintainableSectionDefinition maintSectionDef = (MaintainableSectionDefinition) iter.next();
                 Collection maintItems = maintSectionDef.getMaintainableItems();
                 for (Iterator iterator = maintItems.iterator(); iterator.hasNext();) {
                     MaintainableItemDefinition item = (MaintainableItemDefinition) iterator.next();

                     if (item instanceof MaintainableFieldDefinition) {
                         MaintainableFieldDefinition maintainableFieldDefinition = (MaintainableFieldDefinition) item;
                         if (maintainableFieldDefinition.isRequired() && maintainableFieldDefinition.isUnconditionallyReadOnly() ) {
                        	
                        	 if (ObjectUtils.getPropertyValue(this.getBusinessObject(), item.getName()) != null) {
                        		 Class defaultValueFinderClass = maintainableFieldDefinition.getDefaultValueFinderClass();
                        		 if (defaultValueFinderClass != null) { 
                        			ObjectUtils.setObjectProperty(this.getBusinessObject(), item.getName(), String.class, ((ValueFinder) defaultValueFinderClass.newInstance()).getValue());
                        		 
                        		 }
                        	 }
                         }
                     }
                 }   
             }
             
         } catch (InstantiationException e) {
             LOG.error("Unable to create instance of object class" + e.getMessage());
             throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
         } catch (IllegalAccessException e) {
             LOG.error("Unable to create instance of object class" + e.getMessage());
             throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
         } catch (Exception e){
        	 throw new RuntimeException("Unable to create instance of object class" + e.getMessage());
         }

       
    }


    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public final void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public void processAfterAddLine(String colName, Class colClass) {
    }
    
    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#getShowInactiveRecords(java.lang.String)
     */
    public boolean getShowInactiveRecords(String collectionName) {
        return InactiveRecordsHidingUtils.getShowInactiveRecords(inactiveRecordDisplay, collectionName);
    }
        
    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#setShowInactiveRecords(java.lang.String, boolean)
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

    public void addNewLineToCollection( String collectionName ) {
        
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "addNewLineToCollection( " + collectionName + " )" );
        }
        // get the new line from the map
        PersistableBusinessObject addLine = newCollectionLines.get( collectionName );
        if ( addLine != null ) {
            // mark the isNewCollectionRecord so the option to delete this line will be presented
            addLine.setNewCollectionRecord(true);

            //if we add back add button on sub collection of an "add line" we may need extra logic here

            // get the collection from the business object
            Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(getBusinessObject(), collectionName);
            // add the line to the collection
            maintCollection.add( addLine );
            //refresh parent object since attributes could of changed prior to user clicking add
            
            String referencesToRefresh = LookupUtils.convertReferencesToSelectCollectionToString(getAllRefreshableReferences(getBusinessObject().getClass()));
            if (LOG.isInfoEnabled()) {
                LOG.info("References to refresh for adding line to collection " + collectionName + ": " + referencesToRefresh);
            }
            refreshReferences(referencesToRefresh);
        }
        
        initNewCollectionLine( collectionName );
        
    }
    
    public PersistableBusinessObject getNewCollectionLine( String collectionName ) {
        if ( LOG.isDebugEnabled() ) {
            //LOG.debug( this + ") getNewCollectionLine( " + collectionName + ")", new Exception( "tracing exception") );
            LOG.debug( "newCollectionLines: " + newCollectionLines );
        }
        PersistableBusinessObject addLine = newCollectionLines.get( collectionName );
        if ( addLine == null ) {
            addLine = initNewCollectionLine( collectionName );            
        }
        return addLine;
    }
    
    public PersistableBusinessObject initNewCollectionLine( String collectionName ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "initNewCollectionLine( " + collectionName + " )" );
        }
        // try to get the object from the map
        //BusinessObject addLine = newCollectionLines.get( collectionName );
        //if ( addLine == null ) {
            // if not there, instantiate a new one
        PersistableBusinessObject addLine;
        try {
            addLine = (PersistableBusinessObject)getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass( docTypeName, collectionName ).newInstance();
        } catch ( Exception ex ) {
            LOG.error( "unable to instantiate new collection line", ex );
            throw new RuntimeException( "unable to instantiate new collection line", ex );
        }
        // and add it to the map
        newCollectionLines.put( collectionName, addLine );            
        //}
        // set its values to the defaults
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors( addLine );
        for (int i = 0; i < descriptors.length; ++i) {
            PropertyDescriptor propertyDescriptor = descriptors[i];

            String fieldName = propertyDescriptor.getName();
            Class propertyType = propertyDescriptor.getPropertyType();
            String value = getMaintenanceDocumentDictionaryService().getCollectionFieldDefaultValue(docTypeName, collectionName, fieldName);
            if (value != null) {
                try {                   
                    ObjectUtils.setObjectProperty( addLine, fieldName, propertyType, value);
                } catch ( Exception ex ) {
                    LOG.error( "Unable to set default property of collection object: "
                            + "\nobject: " 
                            + addLine 
                            + "\nfieldName=" + fieldName 
                            + "\npropertyType=" + propertyType 
                            + "\nvalue=" + value, ex );
                }
            }

        }
        return addLine;
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#populateNewCollectionLines(java.util.Map)
     */
    public Map populateNewCollectionLines( Map fieldValues ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "populateNewCollectionLines: " + fieldValues );
        }
        Map cachedValues = new HashMap();
        
        // loop over all collections with an enabled add line
        List<MaintainableCollectionDefinition> collections = 
                getMaintenanceDocumentDictionaryService().getMaintainableCollections( docTypeName );
                
        for ( MaintainableCollectionDefinition coll : collections ) {
            // get the collection name
            String collName = coll.getName();
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "checking for collection: " + collName );
            }
            // build a map for that collection
            Map<String,Object> collectionValues = new HashMap<String,Object>();
            Map<String,Object> subCollectionValues = new HashMap<String,Object>();
            // loop over the collection, extracting entries with a matching prefix
            for ( Object entry : fieldValues.entrySet() ) {
                String key = (String)((Map.Entry)entry).getKey(); 
                if ( key.startsWith( collName ) ) {
                    String subStrKey = key.substring( collName.length() + 1 );
                    //check for subcoll w/ '[', set collName to propername and put in correct name for collection values (i.e. strip '*[x].')
                    if(key.contains("[")) {
                        
                        //collName = StringUtils.substringBeforeLast(key,"[");
                        
                        //need the whole thing if subcollection
                        subCollectionValues.put( key, ((Map.Entry)entry).getValue() );
                    } else {
                        collectionValues.put( subStrKey, ((Map.Entry)entry).getValue() );
                    }
                }
            }
            // send those values to the business object
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "values for collection: " + collectionValues );
            }
            cachedValues.putAll( FieldUtils.populateBusinessObjectFromMap( getNewCollectionLine( collName ), collectionValues, KNSConstants.MAINTENANCE_ADD_PREFIX + collName + "." ) );
            GlobalVariables.getErrorMap().addToErrorPath( KNSConstants.MAINTENANCE_ADD_PREFIX + collName );
            GlobalVariables.getErrorMap().removeFromErrorPath( KNSConstants.MAINTENANCE_ADD_PREFIX + collName );
            cachedValues.putAll( populateNewSubCollectionLines( coll, subCollectionValues ) );
        }
        
        //cachedValues.putAll( FieldUtils.populateBusinessObjectFromMap( ))
        return cachedValues;
    }

    /* Yes, I think this could be merged with the above code - I'm 
     * leaving it separate until I figure out of there are any issues which would reqire
     * that it be separated.
     */
    protected Map populateNewSubCollectionLines( MaintainableCollectionDefinition parentCollection, Map fieldValues) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "populateNewSubCollectionLines: " + fieldValues );
        }
        Map cachedValues = new HashMap();
        
        for ( MaintainableCollectionDefinition coll : parentCollection.getMaintainableCollections() ) {
            // get the collection name
            String collName = coll.getName();
            

            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "checking for sub collection: " + collName );
            }
            Map<String,String> parents = new HashMap<String,String>();
            //get parents from list
            for ( Object entry : fieldValues.entrySet() ) {
                String key = (String)((Map.Entry)entry).getKey(); 
                if ( key.contains( collName ) ) {
                    parents.put(StringUtils.substringBefore(key,"."),"");
                }
            }
            
            for (String parent : parents.keySet()) {
                // build a map for that collection
                Map<String,Object> collectionValues = new HashMap<String,Object>();
                // loop over the collection, extracting entries with a matching prefix
                for ( Object entry : fieldValues.entrySet() ) {
                    String key = (String)((Map.Entry)entry).getKey(); 
                    if ( key.contains( parent ) ) {
                        String substr = StringUtils.substringAfterLast(key,".");
                        collectionValues.put( substr, ((Map.Entry)entry).getValue() );                                        
                    }
                }
                // send those values to the business object
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "values for sub collection: " + collectionValues );
                }
                GlobalVariables.getErrorMap().addToErrorPath( KNSConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName );
                cachedValues.putAll( FieldUtils.populateBusinessObjectFromMap( getNewCollectionLine( parent+"."+collName ), collectionValues, KNSConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName + "." ) );
                GlobalVariables.getErrorMap().removeFromErrorPath( KNSConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName );
            }
            
            cachedValues.putAll( populateNewSubCollectionLines( coll, fieldValues ) );
        }
        
        return cachedValues;
    }

    public Collection<String> getAffectedReferencesFromLookup(BusinessObject baseBO, String attributeName, String collectionPrefix) {
        PersistenceStructureService pss = getPersistenceStructureService();
        String nestedBOPrefix = "";
        if (ObjectUtils.isNestedAttribute(attributeName)) {
            // if we're performing a lookup on a nested attribute, we need to use the nested BO all the way down the chain
            nestedBOPrefix = ObjectUtils.getNestedAttributePrefix(attributeName);
            
            // renormalize the base BO so that the attribute name is not nested anymore 
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
        BusinessObjectRelationship relationship = businessObjectMetaDataService.getBusinessObjectRelationship(baseBO, attributeName);
        if (relationship == null) {
            return new ArrayList<String>();
        }
        
        Map<String, String> fkToPkMappings = relationship.getParentToChildReferences();
        
        Collection<String> affectedReferences = generateAllAffectedReferences(baseBO.getClass(), fkToPkMappings, nestedBOPrefix, collectionPrefix);
        if (LOG.isDebugEnabled()) {
            LOG.debug("References affected by a lookup on BO attribute \"" + collectionPrefix + nestedBOPrefix + "." + attributeName + ": " + affectedReferences);
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
    
    protected Collection<String> generateAllAffectedReferences(Class boClass, 
            Map<String, String> fkToPkMappings, String nestedBOPrefix, String collectionPrefix) {
        Set<String> allAffectedReferences = new HashSet<String>();
        DataDictionaryService dataDictionaryService = getDataDictionaryService();
        PersistenceStructureService pss = getPersistenceStructureService();
        
        collectionPrefix = StringUtils.isBlank(collectionPrefix) ? "" : collectionPrefix;
        
        // retrieve the attributes that are affected by a lookup on attributeName.
        Collection<String> attributeReferenceFKAttributes = fkToPkMappings.keySet();
        
        // a lookup on an attribute may cause other attributes to be updated (e.g. account code lookup would also affect chart code)
        // build a list of all affected FK values via mapKeyFields above, and for each FK, see if there are any non-updatable references with that FK
        
        // deal with regular simple references (<reference-descriptor>s in OJB)
        for (String fkAttribute : attributeReferenceFKAttributes) {
            for (String affectedReference : pss.getReferencesForForeignKey(boClass, fkAttribute).keySet()) {
                if (isRelationshipRefreshable(boClass, affectedReference)) {
                    if (StringUtils.isBlank(nestedBOPrefix)) {
                        allAffectedReferences.add(collectionPrefix+ affectedReference);
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
                Map<String, String> keyMappingsForCollection = pss.getInverseForeignKeysForCollection(boClass, collectionName);
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
            Map<String, String> referencePKtoFKmappings = dataDictionaryService.getRelationshipAttributeMap(boClass.getName(), ddRelationship);
            for (String sourceAttribute : referencePKtoFKmappings.values()) {
                // the sourceAttribute is the FK pointing to the target attribute (PK)
                if (attributeReferenceFKAttributes.contains(sourceAttribute)) {
                    for (String affectedReference : dataDictionaryService.getRelationshipEntriesForSourceAttribute(boClass.getName(), sourceAttribute)) {
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

	public void handleRouteStatusChange(DocumentHeader documentHeader) {
	}

	public static PersistenceStructureService getPersistenceStructureService() {
	    if ( persistenceStructureService == null ) {
	        persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
	    }
	    return persistenceStructureService;
	}

    public static MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if ( maintenanceDocumentDictionaryService == null ) {
            maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }

    public static DataDictionaryService getDataDictionaryService() {
        if ( dataDictionaryService == null ) {
            dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    public static BusinessObjectService getBusinessObjectService() {
        if ( businessObjectService == null ) {
            businessObjectService = KNSServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    public static BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        if ( businessObjectDictionaryService == null ) {
            businessObjectDictionaryService = KNSServiceLocator.getBusinessObjectDictionaryService();
        }
        return businessObjectDictionaryService;
    }

    public static EncryptionService getEncryptionService() {
        if ( encryptionService == null ) {
            encryptionService = KNSServiceLocator.getEncryptionService();
        }
        return encryptionService;
    }

    public static org.kuali.rice.kim.service.PersonService getPersonService() {
        if ( personService == null ) {
            personService = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService();
        }
        return personService;
    }

    public static BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        if ( businessObjectMetaDataService == null ) {
            businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
        }
        return businessObjectMetaDataService;
    }

    public static BusinessObjectAuthorizationService getBusinessObjectAuthorizationService() {
    	if (businessObjectAuthorizationService == null) {
    		businessObjectAuthorizationService = KNSServiceLocator.getBusinessObjectAuthorizationService();
    	}
    	return businessObjectAuthorizationService;
    }
    
	/**
	 * @see org.kuali.rice.kns.maintenance.Maintainable#clearBusinessObjectOfRestrictedValues(org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions)
	 */
	public void clearBusinessObjectOfRestrictedValues(MaintenanceDocumentRestrictions maintenanceDocumentRestrictions) {
		List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);
		for (MaintainableSectionDefinition sectionDefinition : sections) {
			for (MaintainableItemDefinition itemDefinition : sectionDefinition.getMaintainableItems()) {
				if (itemDefinition instanceof MaintainableFieldDefinition) {
					clearFieldRestrictedValues("", businessObject, (MaintainableFieldDefinition) itemDefinition, maintenanceDocumentRestrictions);
				}
				else if (itemDefinition instanceof MaintainableCollectionDefinition) {
					clearCollectionRestrictedValues("", businessObject, (MaintainableCollectionDefinition) itemDefinition, maintenanceDocumentRestrictions);
				}
			}
		}
	}
	
	private void clearCollectionRestrictedValues(String fieldNamePrefix, BusinessObject businessObject, MaintainableCollectionDefinition collectionDefinition, MaintenanceDocumentRestrictions maintenanceDocumentRestrictions) {
		String collectionName = fieldNamePrefix + collectionDefinition.getName();
		Collection<BusinessObject> collection = (Collection<BusinessObject>) ObjectUtils.getPropertyValue(businessObject, collectionDefinition.getName());
		
		if (collection != null) {
			int i = 0;
			// even though it's technically a Collection, we're going to index it like a list
			for (BusinessObject collectionItem : collection) {
				String collectionItemNamePrefix = collectionName + "[" + i + "].";
				for (MaintainableCollectionDefinition subCollectionDefinition : collectionDefinition.getMaintainableCollections()) {
					clearCollectionRestrictedValues(collectionItemNamePrefix, collectionItem, subCollectionDefinition, maintenanceDocumentRestrictions);
				}
				for (MaintainableFieldDefinition fieldDefinition : collectionDefinition.getMaintainableFields()) {
					clearFieldRestrictedValues(collectionItemNamePrefix, collectionItem, fieldDefinition, maintenanceDocumentRestrictions);
				}
				i++;
			}
		}
	}
	
	private void clearFieldRestrictedValues(String fieldNamePrefix, BusinessObject businessObject, MaintainableFieldDefinition fieldDefinition, MaintenanceDocumentRestrictions maintenanceDocumentRestrictions) {
		String fieldName = fieldNamePrefix + fieldDefinition.getName();
		
		FieldRestriction fieldRestriction = maintenanceDocumentRestrictions.getFieldRestriction(fieldName);
		if (fieldRestriction.isRestricted()) {
			String defaultValue = null;
			if (StringUtils.isNotBlank(fieldDefinition.getDefaultValue())) {
				defaultValue = fieldDefinition.getDefaultValue();
			}
			else if (fieldDefinition.getDefaultValueFinderClass() != null) {
				try {
					defaultValue = ((ValueFinder) fieldDefinition.getDefaultValueFinderClass().newInstance()).getValue();
				} catch (Exception e) {
					defaultValue = null;
					LOG.error("Error trying to instantiate ValueFinder or to determine ValueFinder for doc type: " + docTypeName + " field name " + fieldDefinition.getName() + " with field prefix: " + fieldNamePrefix, e);
				}
			}
			try {
				ObjectUtils.setObjectProperty(businessObject, fieldDefinition.getName(), defaultValue);
			} catch (Exception e) {
				// throw an exception, because we don't want users to be able to see the restricted value
				LOG.error("Unable to clear maintenance document values for field name: " + fieldName + " default value: " + defaultValue, e);
				throw new RuntimeException("Unable to clear maintenance document values for field name: " + fieldName, e);
			}
		}
	}
	
	
}
