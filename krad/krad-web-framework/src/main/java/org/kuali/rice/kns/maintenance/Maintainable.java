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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.lookup.SelectiveReferenceRefresher;

/**
 * This interface defines basic methods that all maintainable objects must provide.
 */
public interface Maintainable extends java.io.Serializable, SelectiveReferenceRefresher {

    /**
     * This is a hook to allow the document to override the generic document title.
     * 
     * @return String   document title
     */
    public String getDocumentTitle(MaintenanceDocument document);

    /**
     * Sets the document number
     * 
     * @param documentNumber - the Document's documentNumber
     * 
     */
    public void setDocumentNumber(String documentNumber);

    /**
     * Returns instance of the business object that is being maintained.
     */
    public PersistableBusinessObject getBusinessObject();


    /**
     * @return the locking representation(s) of this document, which are reproducible given the same keys and the same maintainable object
     */
    public List<MaintenanceLock> generateMaintenanceLocks();


    /**
     * Returns a string that will be displayed as title on the maintenance screen.
     */
    public String getMaintainableTitle();

    /**
     * Retrieves the status of the boNotesEnabled
     */
    public boolean isBoNotesEnabled();

    /**
     * Returns a list of Section objects that specify how to render the view for the maintenance object.
     * 
     * @param oldMaintainable - If this is the new maintainable, the old is passed in for reference.  If it is the old maintainable, then null will be passed in
     * @return
     */
    public List getSections(MaintenanceDocument maintenanceDocument, Maintainable oldMaintainable);


    /**
     * This method populates the business object based on key/value pairs.
     * 
     * @param fieldValues
     * @param maintenanceDocument
     * @return
     */
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall);


    /**
     * Called from a lookup return by the maintenance action.
     */
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document);


    /**
     * Sets an instance of a business object to be maintained.
     */
    public void setBusinessObject(PersistableBusinessObject object);

    /**
     * Sets the maintenance action - new, edit, or copy
     */
    public void setMaintenanceAction(String maintenanceAction);

    /**
     * Returns the maintenance action - new, edit, or copy
     */
    public String getMaintenanceAction();

    /**
     * Set default values.
     * @param docTypeName
     *
     */
    public void setGenerateDefaultValues(String docTypeName);


    /**
     * Set default values for blank required fields.
     * @param docTypeName
     * 
     */
    public void setGenerateBlankRequiredValues(String docTypeName);


    public Class<? extends PersistableBusinessObject> getBoClass();

    public void setBoClass(Class<? extends PersistableBusinessObject> boClass);

    /**
     * 
     * This method is a hook to do any necessary pre-save processing.
     * 
     */
    public void prepareForSave();

    /**
     * 
     * This method is a hook to do any necessary post-load processing.
     * 
     */
    public void processAfterRetrieve();

    /**
     * 
     * This method is a hook to do any necessary post-copy processing.
     * 
     */
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters );
    
    /**
     * This method is a hook to do any necessary post-edit processing, which is to say that it is called when a document is about to be edited;
     * this is therefore a hook to write any code to modify the business object before it is displayed to the end user to edit.
     */
    public void processAfterEdit( MaintenanceDocument document, Map<String,String[]> parameters );

    /**
     * 
     * This method is a hook to do any necessary post-copy processing.
     * 
     */
    public void processAfterNew( MaintenanceDocument document, Map<String,String[]> parameters );

    /**
     * 
     * KULRICE-4264 - a hook to change the state of the business object, which is the "new line" of a collection, before it is validated
     * 
     * @param colName
     * @param colClass
     * @param addBO
     */
    public void processBeforeAddLine(String colName, Class colClass, BusinessObject addBO);
    
    /**
     * 
     * This method is a hook to do any necessary post-post processing.
     * 
     */
    public void processAfterPost( MaintenanceDocument document, Map<String,String[]> parameters );
    
    /**
     * 
     * This method will cause the Maintainable implementation to save/store the relevant business object(s). This typically is
     * called only after the maint document has gone through state to final.
     * 
     */
    public void saveBusinessObject();

    /**
     * Populates the new collection lines based on key/value pairs.
     * 
     * @param fieldValues
     * @return
     */
    public Map<String, String> populateNewCollectionLines( Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall );
    
    /**
     * Gets the holder for the "add line" for a collection on the business object 
     * 
     * @param collectionName
     * @return
     */
    public PersistableBusinessObject getNewCollectionLine( String collectionName );

    /**
     * Adds the new line for the given collection to the business object's collection.
     * 
     * @param collectionName
     */
    public void addNewLineToCollection( String collectionName );
    
    /**
     * 
     * This methods will do the setting of some attributes that might be necessary
     * if we're creating a new business object using on an existing business object.
     * For example, create a division Vendor based on an existing parent Vendor.
     * (Please see VendorMaintainableImpl.java)
     */
    public void setupNewFromExisting( MaintenanceDocument document, Map<String,String[]> parameters );
    
    /**
     * Indicates whether inactive records for the given collection should be display.
     * 
     * @param collectionName - name of the collection (or sub-collection) to check inactive record display setting
     * @return true if inactive records should be displayed, false otherwise
     */
    public boolean getShowInactiveRecords(String collectionName);
    
    /**
     * Returns the Map used to control the state of inactive record collection display. Exposed for setting from the
     * maintenance jsp.
     */
    public Map<String, Boolean> getInactiveRecordDisplay();
    
    /**
     * Indicates to maintainble whether or not inactive records should be displayed for the given collection name.
     * 
     * @param collectionName - name of the collection (or sub-collection) to set inactive record display setting
     * @param showInactive - true to display inactive, false to not display inactive records
     */
    public void setShowInactiveRecords(String collectionName, boolean showInactive);
    
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo);
    
    /**
     * method to integrate with workflow, where we will actually handle the transitions of status for documents
     */
    public void doRouteStatusChange(DocumentHeader documentHeader);

    public List<String> getDuplicateIdentifierFieldsFromDataDictionary(String docTypeName, String collectionName);
    
    public List<String> getMultiValueIdentifierList(Collection maintCollection, List<String> duplicateIdentifierFields);
    
    public boolean hasBusinessObjectExisted(BusinessObject bo, List<String> existingIdentifierList, List<String> duplicateIdentifierFields);

    /**
     * Blanks out or sets the default of any value specified as restricted within the {@link MaintenanceDocumentRestrictions} instance.
     * 
     * This method should only be called if this maintainable represents the new maintainable of the maintenance document.
     * 
     * @param maintenanceDocumentRestrictions
     */
    public void clearBusinessObjectOfRestrictedValues(MaintenanceDocumentRestrictions maintenanceDocumentRestrictions);
    
    /**
     * For the case when we want to maintain a business object that doesn't necessarily map to 
     * a single table in the database or may doesn't map to a database at all  
     * 
     * @return
     */
    public boolean isExternalBusinessObject();
 
    /**
     * Gives chance to a maintainable object to prepare and return a maintainable object which 
     * might be external to the system 
     * 
     * @return
     */
    public void prepareBusinessObject(BusinessObject businessObject);
    
    /**
     * Searches for any relevant locking documents.
     * 
     * @return
     */
    public String getLockingDocumentId();
    
    /**
     * Return an array of document ids to lock prior to processing this document in the workflow engine. 
     */
    public List<Long> getWorkflowEngineDocumentIdsToLock();
    
    //3070
    public void deleteBusinessObject(); 

    /**
     * This method returns whether or not this maintainable supports custom lock descriptors for pessimistic locking.
     * 
     * @return True if the maintainable can generate custom lock descriptors, false otherwise.
     * @see #getCustomLockDescriptor(Map, Person)
     */
    public boolean useCustomLockDescriptors();
    
    /**
     * Generates a custom lock descriptor for pessimistic locking. This method should not be called unless {@link #useCustomLockDescriptors()} returns true.
     * 
     * @param user The user trying to establish the lock.
     * @return A String representing the lock descriptor.
     * @see #useCustomLockDescriptors()
     * @see org.kuali.rice.kns.service.PessimisticLockService
     * @see org.kuali.rice.kns.service.impl.PessimisticLockServiceImpl
     */
    public String getCustomLockDescriptor(Person user);
    
    boolean isOldBusinessObjectInDocument();
}
