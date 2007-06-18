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

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.lookup.SelectiveReferenceRefresher;

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
     * Sets the financial document number
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
     * @param oldMaintainable - If this is the new maintainable, the old is passed in for reference
     * @return
     */
    public List getSections(Maintainable oldMaintainable);


    /**
     * Populates the business object based on key/value pairs.
     */
    public Map populateBusinessObject(Map fieldValues);


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
     * Indicates to maintainable whether or not to set default values.
     * 
     * @param generateDefault
     */
    public void setGenerateDefaultValues(boolean generateDefault);

    /**
     * 
     * Indicates whether default values are set on this maintainable.
     * 
     * @return
     */
    public boolean isGenerateDefaultValues();

    /**
     * Indicates to maintainable whether or not to set default values for blank required fields.
     * 
     * @param generateBlankRequiredValues
     */
    public void setGenerateBlankRequiredValues(boolean generateBlankRequiredValues);

    /**
     * 
     * Indicates whether default values should be generated for blank required fields on this maintainable.
     * 
     * @return
     */
    public boolean isGenerateBlankRequiredValues();

    public Class getBoClass();

    public void setBoClass(Class boClass);

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
    public void processAfterCopy();
    
    /**
     * This method is a hook to do any necessary post-edit processing, which is to say that it is called when a document is about to be edited;
     * this is therefore a hook to write any code to modify the business object before it is displayed to the end user to edit.
     */
    public void processAfterEdit();


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
    public Map populateNewCollectionLines( Map fieldValues );
    
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
    public void setupNewFromExisting();
    
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
    
    public void overrideDataDictionarySectionConfiguration(DataDictionaryDefinitionBase definition);
    public void overrideDataDictionaryFieldConfiguration(DataDictionaryDefinitionBase definition);
    
    
}