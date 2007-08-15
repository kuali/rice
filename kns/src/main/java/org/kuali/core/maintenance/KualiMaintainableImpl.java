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
package org.kuali.core.maintenance;

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
import org.kuali.RiceConstants;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.BusinessObjectRelationship;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableItemDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.BusinessObjectMetaDataService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.MaintenanceUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.Section;
import org.kuali.core.web.ui.SectionBridge;
import org.kuali.rice.KNSServiceLocator;

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
    protected boolean generateDefaultValues;
    protected boolean generateBlankRequiredValues;
    
    protected Map<String,PersistableBusinessObject> newCollectionLines = new HashMap<String,PersistableBusinessObject>();
    protected Map<String, Boolean> inactiveRecordDisplay = new HashMap<String, Boolean>();
    
    private String docTypeName;

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

    public void setupNewFromExisting() {
        
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
        lockRepresentation.append(RiceConstants.Maintenance.AFTER_CLASS_DELIM);

        PersistableBusinessObject bo = getBusinessObject();
        List keyFieldNames = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getLockingKeys(getDocumentTypeName());

        for (Iterator i = keyFieldNames.iterator(); i.hasNext();) {
            String fieldName = (String) i.next();
            Object fieldValue = ObjectUtils.getPropertyValue(bo, fieldName);
            if (fieldValue == null) {
                fieldValue = "";
            }
            
            // check if field is a secure
            String displayWorkgroup = KNSServiceLocator.getDataDictionaryService().getAttributeDisplayWorkgroup(getBoClass(), fieldName);
            if (StringUtils.isNotBlank(displayWorkgroup)) {
                try {
                    fieldValue = KNSServiceLocator.getEncryptionService().encrypt(fieldValue);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
                    throw new RuntimeException("Unable to encrypt secure field for locking representation " + e.getMessage());
                }
            }

            lockRepresentation.append(fieldName);
            lockRepresentation.append(RiceConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(String.valueOf(fieldValue));
            if (i.hasNext()) {
                lockRepresentation.append(RiceConstants.Maintenance.AFTER_VALUE_DELIM);
            }
        }

        MaintenanceLock maintenanceLock = new MaintenanceLock();
        maintenanceLock.setDocumentNumber(documentNumber);
        maintenanceLock.setLockingRepresentation(lockRepresentation.toString());
        maintenanceLocks.add(maintenanceLock);
        return maintenanceLocks;
    }

    /**
     * @see org.kuali.core.maintenance.Maintainable#populateBusinessObject(java.util.Map)
     */
    public Map populateBusinessObject(Map fieldValues) {
        fieldValues = decryptEncryptedData(fieldValues);
        Map newFieldValues = null;
        newFieldValues = KNSServiceLocator.getUniversalUserService().resolveUserIdentifiersToUniversalIdentifiers(getBusinessObject(), fieldValues);
   
        Map cachedValues = FieldUtils.populateBusinessObjectFromMap(getBusinessObject(), newFieldValues);
        KNSServiceLocator.getBusinessObjectDictionaryService().performForceUppercase(getBusinessObject());
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
    private Map decryptEncryptedData(Map fieldValues) {
        try {
        for (Iterator iter = fieldValues.keySet().iterator(); iter.hasNext();) {
                String fieldName = (String) iter.next();
                String fieldValue = (String) fieldValues.get(fieldName);
                if (fieldValue != null && fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
                    String encryptedValue = fieldValue;

                    // take of the postfix
                    encryptedValue = StringUtils.stripEnd(encryptedValue, EncryptionService.ENCRYPTION_POST_PREFIX);
                    String decryptedValue = KNSServiceLocator.getEncryptionService().decrypt(encryptedValue);

                    fieldValues.put(fieldName, decryptedValue);
                }
            }
        }
                catch (GeneralSecurityException e) {
                    throw new RuntimeException("Unable to decrypt secure data: " + e.getMessage());
                }
        
        return fieldValues;
    }

    /**
     * Calls method to get all the core sections for the business object defined in the data dictionary. Then determines if the bo
     * has custom attributes, if so builds a custom attribute section and adds to the section list.
     * 
     * @return List of org.kuali.ui.Section objects
     */
    public List getSections(Maintainable oldMaintainable) {
        List<Section> sections = new ArrayList<Section>();
        sections.addAll(getCoreSections(oldMaintainable));

        return sections;
    }

    /**
     * Gets list of maintenance sections built from the data dictionary. If the section contains maintenance fields, construct
     * Row/Field UI objects and place under Section UI. If section contains a maintenance collection, call method to build a Section
     * UI which contains rows of Container Fields.
     * 
     * @return List of org.kuali.ui.Section objects
     */
    public List<Section> getCoreSections(Maintainable oldMaintainable) {
        
        List<Section> sections = new ArrayList<Section>();

        List<MaintainableSectionDefinition> sectionDefinitions = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);

        try {
            // iterate through section definitions and create Section UI object
            for (Iterator iter = sectionDefinitions.iterator(); iter.hasNext();) {
                
                MaintainableSectionDefinition maintSectionDef = (MaintainableSectionDefinition) iter.next();

                List<String> displayedFieldNames = new ArrayList<String>();
                
                for (Iterator iter2 = maintSectionDef.getMaintainableItems().iterator(); iter2.hasNext();) {
                    
                    MaintainableItemDefinition item = (MaintainableItemDefinition) iter2.next();
                    if (item instanceof MaintainableFieldDefinition) {
                        displayedFieldNames.add(((MaintainableFieldDefinition) item).getName());
                    }
                }
                
                Section section = SectionBridge.toSection(maintSectionDef, getBusinessObject(), this, oldMaintainable, getMaintenanceAction(), isGenerateDefaultValues(), isGenerateBlankRequiredValues(), displayedFieldNames);
                
                // add to section list
                sections.add(section);
                
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
     * @see org.kuali.core.maintenance.Maintainable#saveBusinessObject()
     */
    public void saveBusinessObject() {
        KNSServiceLocator.getBusinessObjectService().linkAndSave(businessObject);
    }
    
    /**
     * Retrieves title for maintenance document from data dictionary
     */
    public String getMaintainableTitle() {        
        return KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintenanceLabel(getDocumentTypeName());
    }

    /**
     * Retrieves the status of the boNotesEnabled
     */
    public boolean isBoNotesEnabled() {
    	return KNSServiceLocator.getBusinessObjectDictionaryService().areNotesSupported(this.boClass);
    }
    
    /**
     * @see org.kuali.core.maintenance.Maintainable#refresh(java.lang.String, java.util.Map) Impls will be needed if custom action
     *      is needed on refresh.
     */
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        String referencesToRefresh = (String) fieldValues.get(RiceConstants.REFERENCES_TO_REFRESH);
        refreshReferences(referencesToRefresh);
    }

    
    protected void refreshReferences(String referencesToRefresh) {
        PersistenceStructureService persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
        if (StringUtils.isNotBlank(referencesToRefresh)) {
            String[] references = StringUtils.split(referencesToRefresh, RiceConstants.REFERENCES_TO_REFRESH_SEPARATOR);
            for (String reference : references) {
                if (StringUtils.isNotBlank(reference)) {
                    if (reference.startsWith(RiceConstants.ADD_PREFIX + ".")) {
                        // add one for the period
                        reference = reference.substring(RiceConstants.ADD_PREFIX.length() + 1);
                        
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
                                if (KNSServiceLocator.getDataDictionaryService().hasRelationship(addlineBOClass.getName(), propertyToRefresh)) {
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
                                    if (KNSServiceLocator.getDataDictionaryService().hasRelationship(possibleBO.getClass().getName(), propertyToRefresh)) {
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
							if (KNSServiceLocator.getDataDictionaryService().hasRelationship(getBusinessObject().getClass().getName(), reference)) {
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

    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues) {
        PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();
        Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(bo, collectionName);
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        
        List<String> duplicateIdentifierFieldsFromDataDictionary = getDuplicateIdentifierFieldsFromDataDictionary(docTypeName, collectionName);
        
        List<String> existingIdentifierList = getMultiValueIdentifierList(maintCollection, duplicateIdentifierFieldsFromDataDictionary);
        
        Class collectionClass = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass(docTypeName, collectionName);

        List<MaintainableSectionDefinition> sections = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);
        Map<String, String> template = MaintenanceUtils.generateMultipleValueLookupBOTemplate(sections, collectionName);
        try {
            for (PersistableBusinessObject nextBo : rawValues) {
                PersistableBusinessObject templatedBo = (PersistableBusinessObject) ObjectUtils.createHybridBusinessObject(collectionClass, nextBo, template);
                templatedBo.setNewCollectionRecord(true);
                prepareBusinessObjectForAdditionFromMultipleValueLookup(collectionName, templatedBo);
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
    	MaintainableCollectionDefinition collDef = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintainableCollection(docTypeName, collectionName);
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
     * @see org.kuali.core.maintenance.Maintainable#prepareForSave()
     */
    public void prepareForSave() {
    }

    /**
     * 
     * @see org.kuali.core.maintenance.Maintainable#processAfterRetrieve()
     */
    public void processAfterRetrieve() {
    }
    
	/** 
     * Set the new collection records back to true so they can be deleted (copy should act like new)
     * 
	 * @see org.kuali.core.maintenance.KualiMaintainableImpl#processAfterCopy()
	 */
	public void processAfterCopy() {
        try {
            ObjectUtils.setObjectPropertyDeep(businessObject, RicePropertyConstants.NEW_COLLECTION_RECORD, boolean.class, true, 2);
        }
        catch (Exception e) {
            LOG.error("unable to set newCollectionRecord property: " + e.getMessage(), e);
            throw new RuntimeException("unable to set newCollectionRecord property: " + e.getMessage(), e);
        }
	}
    
    /**
     * @see org.kuali.core.maintenance.Maintainable#processAfterEdit()
     */
    public void processAfterEdit() {
    }
    
    /**
     * Retrieves the document type name from the data dictionary based on business object class
     */
    private String getDocumentTypeName() {
        return KNSServiceLocator.getMaintenanceDocumentDictionaryService().getDocumentTypeName(boClass);
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
     * @return Returns the generateDefaultValues.
     */
    public boolean isGenerateDefaultValues() {
        return generateDefaultValues;
    }

    /**
     * @param generateDefaultValues The generateDefaultValues to set.
     */
    public void setGenerateDefaultValues(boolean generateDefaultValues) {
        this.generateDefaultValues = generateDefaultValues;
    }

    /**
     * @return Returns the generateDefaultValues.
     */
    public boolean isGenerateBlankRequiredValues() {
        return generateBlankRequiredValues;
    }

    /**
     * @param generateDefaultValues The generateDefaultValues to set.
     */
    public void setGenerateBlankRequiredValues(boolean generateBlankRequiredValues) {
        this.generateBlankRequiredValues = generateBlankRequiredValues;
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
     * @see org.kuali.core.maintenance.Maintainable#getShowInactiveRecords(java.lang.String)
     */
    public boolean getShowInactiveRecords(String collectionName) {
        boolean showInactive = false;
        
        if (collectionName == null) {
            throw new IllegalArgumentException("collection name cannot be null");
        }
        // remove periods from the collection name due to parsing limitation in Apache beanutils 
        collectionName = collectionName.replace( '.', '_' );
        
        if (inactiveRecordDisplay.containsKey(collectionName)) {
            Object inactiveSetting = inactiveRecordDisplay.get(collectionName);
            if (inactiveSetting instanceof Boolean) {
                showInactive = ((Boolean) inactiveSetting).booleanValue();
            } else {
                showInactive = Boolean.parseBoolean(((String[]) inactiveSetting)[0]);
            }
        }
        
        return showInactive;
    }

    /**
     * @see org.kuali.core.maintenance.Maintainable#setShowInactiveRecords(java.lang.String, boolean)
     */
    public void setShowInactiveRecords(String collectionName, boolean showInactive) {
        if (collectionName == null) {
            throw new IllegalArgumentException("collection name cannot be null");
        }
        
        // remove periods from the collection name due to parsing limitation in Apache beanutils 
        collectionName = collectionName.replace( '.', '_' );

        inactiveRecordDisplay.put(collectionName, new Boolean(showInactive));
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
            addLine = (PersistableBusinessObject)KNSServiceLocator.getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass( docTypeName, collectionName ).newInstance();
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
            String value = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getCollectionFieldDefaultValue(docTypeName, collectionName, fieldName);
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
     * @see org.kuali.core.maintenance.Maintainable#populateNewCollectionLines(java.util.Map)
     */
    public Map populateNewCollectionLines( Map fieldValues ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "populateNewCollectionLines: " + fieldValues );
        }
        Map cachedValues = new HashMap();
        
        // loop over all collections with an enabled add line
        List<MaintainableCollectionDefinition> collections = 
            KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintainableCollections( docTypeName );
                
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
            GlobalVariables.getErrorMap().addToErrorPath( RiceConstants.MAINTENANCE_ADD_PREFIX + collName );
            cachedValues.putAll( FieldUtils.populateBusinessObjectFromMap( getNewCollectionLine( collName ), collectionValues, RiceConstants.MAINTENANCE_ADD_PREFIX + collName + "." ) );
            GlobalVariables.getErrorMap().removeFromErrorPath( RiceConstants.MAINTENANCE_ADD_PREFIX + collName );
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
                GlobalVariables.getErrorMap().addToErrorPath( RiceConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName );
                cachedValues.putAll( FieldUtils.populateBusinessObjectFromMap( getNewCollectionLine( parent+"."+collName ), collectionValues, RiceConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName + "." ) );
                GlobalVariables.getErrorMap().removeFromErrorPath( RiceConstants.MAINTENANCE_ADD_PREFIX + parent + "." + collName );
            }
            
            cachedValues.putAll( populateNewSubCollectionLines( coll, fieldValues ) );
        }
        
        return cachedValues;
    }

    public void overrideDataDictionaryFieldConfiguration(DataDictionaryDefinitionBase definition) {
        // Default no-op implemenation
    }

    public void overrideDataDictionarySectionConfiguration(DataDictionaryDefinitionBase definition) {
        // Default no-op implemenation
    }

    public Collection<String> getAffectedReferencesFromLookup(BusinessObject baseBO, String attributeName, String collectionPrefix) {
        PersistenceStructureService pss = KNSServiceLocator.getPersistenceStructureService();
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        
        BusinessObjectMetaDataService businessObjectMetaDataService = KNSServiceLocator.getBusinessObjectMetaDataService();
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
        if (KNSServiceLocator.getPersistenceStructureService().isPersistable(boClass)) {
            if (KNSServiceLocator.getPersistenceStructureService().hasCollection(boClass, relationshipName)) {
                return !KNSServiceLocator.getPersistenceStructureService().isCollectionUpdatable(boClass, relationshipName);
            }
            else if (KNSServiceLocator.getPersistenceStructureService().hasReference(boClass, relationshipName)) {
                return !KNSServiceLocator.getPersistenceStructureService().isReferenceUpdatable(boClass, relationshipName);
            }
            // else, assume that the relationship is defined in the DD
        }

        return true;
    }
    
    protected Collection<String> generateAllAffectedReferences(Class boClass, 
            Map<String, String> fkToPkMappings, String nestedBOPrefix, String collectionPrefix) {
        Set<String> allAffectedReferences = new HashSet<String>();
        DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        PersistenceStructureService pss = KNSServiceLocator.getPersistenceStructureService();
        
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
        for (String referenceName : KNSServiceLocator.getPersistenceStructureService().listReferenceObjectFields(boClass).keySet()) {
            if (isRelationshipRefreshable(boClass, referenceName)) {
                references.add(referenceName);
            }
        }
        for (String collectionName : KNSServiceLocator.getPersistenceStructureService().listCollectionObjectTypes(boClass).keySet()) {
            if (isRelationshipRefreshable(boClass, collectionName)) {
                references.add(collectionName);
            }
        }
        for (String relationshipName : KNSServiceLocator.getDataDictionaryService().getRelationshipNames(boClass.getName())) {
            if (isRelationshipRefreshable(boClass, relationshipName)) {
                references.add(relationshipName);
            }
        }
        return references;
    }

	public void handleRouteStatusChange(DocumentHeader documentHeader) {
	}
}