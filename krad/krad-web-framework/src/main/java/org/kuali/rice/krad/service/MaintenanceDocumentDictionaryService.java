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
package org.kuali.rice.krad.service;

import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.*;
import org.kuali.rice.krad.document.MaintenanceDocument;
import org.kuali.rice.krad.rule.BusinessRule;

import java.util.Collection;
import java.util.List;

/**
 * Defines methods that a MaintenanceDocumentDictionary Service must provide. Defines the API for the interacting
 * with Document-related entries in the data dictionary.
 * 
 *@author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MaintenanceDocumentDictionaryService {
    /**
     * Retrieves the label for a maintenance document.
     * 
     * @param docTypeName
     * @return The label as a String.
     */
    @Deprecated
    public String getMaintenanceLabel(String docTypeName);

    /**
     * Retrieves the description of the maintenance document.
     * 
     * @param docTypeName
     * @return The description as a String.
     */
    public String getMaintenanceDescription(String docTypeName);

    /**
     * Retrieves an instance of the class that represents the maintenance document. This is done by
     * 
     * @param docTypeName
     * @return A class instance.
     */
    public Class getMaintainableClass(String docTypeName);

    /**
     * The document type name for a class instance.
     * 
     * @param businessObjectClass
     * @return The document type name for the class as a String.
     */
    public String getDocumentTypeName(Class businessObjectClass);

    /**
     * The collection of ReferenceDefinition objects defined as DefaultExistenceChecks for the MaintenanceDocument.
     * 
     * @param businessObjectClass
     * @return A Collection of ReferenceDefinitions
     */
    public Collection getDefaultExistenceChecks(Class businessObjectClass);

    /**
     * The collection of ReferenceDefinition objects defined as DefaultExistenceChecks for the MaintenanceDocument.
     * 
     * @param docTypeName
     * @return A Collection of ReferenceDefinitions
     */
    public Collection getDefaultExistenceChecks(String docTypeName);

    /**
     * A List of field names used as locking keys
     * 
     * @param docTypeName
     * @return A List of strings
     */
    public List getLockingKeys(String docTypeName);

    /**
     * A List of maintainable section object instances corresponding to the document type name.
     * 
     * @param docTypeName
     * @return A List of maintable section objects.
     */
    @Deprecated
    public List<MaintainableSectionDefinition> getMaintainableSections(String docTypeName);

    /**
     * The instance of the business object class associated with this document type name.
     * 
     * @param docTypeName
     * @return The class instance corresponding to the document type name.
     */
    public Class getBusinessObjectClass(String docTypeName);


    /**
     * @param document
     * @return businessRulesClass associated with the given document's type
     */
    public Class<? extends BusinessRule> getBusinessRulesClass(MaintenanceDocument document);

    /**
     * 
     * This method returns the defaultValue as it would appear in the UI on a maintenance document.
     * 
     * If both a defaultValue and a defaultValueFinderClass is present in the MaintainableFieldDefinition instance, then the
     * defaultValue will be preferentially returned. If only one is present, then that will be returned.
     * 
     * Note that if a defaultValueFinderClass value is present, then this method will attempt to create a new instance of the
     * specified class. If this attempt to generate a new instance fails, the error will be suppressed, and an null result will be
     * returned.
     * 
     * @param boClass - the class of BO being maintained
     * @param fieldName - the fieldName of the attribute for which the default is desired
     * @return the default if one is available, null otherwise
     * 
     */
    @Deprecated
    public String getFieldDefaultValue(Class boClass, String fieldName);

    /**
     * 
     * This method returns the defaultValue as it would appear in the UI on a maintenance document.
     * 
     * If both a defaultValue and a defaultValueFinderClass is present in the MaintainableFieldDefinition instance, then the
     * defaultValue will be preferentially returned. If only one is present, then that will be returned.
     * 
     * Note that if a defaultValueFinderClass value is present, then this method will attempt to create a new instance of the
     * specified class. If this attempt to generate a new instance fails, the error will be suppressed, and an null result will be
     * returned.
     * 
     * @param docTypeName - the document type name of the maintainable
     * @param fieldName - the fieldName of the attribute for which the default is desired
     * @return the default if one is available, null otherwise
     * 
     */
    @Deprecated
    public String getFieldDefaultValue(String docTypeName, String fieldName);

    /**
     * 
     * This method returns the defaultValue as it would appear in the UI on a maintenance document for a collection.
     * 
     * If both a defaultValue and a defaultValueFinderClass is present in the MaintainableFieldDefinition instance, then the
     * defaultValue will be preferentially returned. If only one is present, then that will be returned.
     * 
     * Note that if a defaultValueFinderClass value is present, then this method will attempt to create a new instance of the
     * specified class. If this attempt to generate a new instance fails, the error will be suppressed, and an null result will be
     * returned.
     * 
     * @param docTypeName - the document type name of the maintainable
     * @param collectionName - the name attribute of the collection to which the field belongs
     * @param fieldName - the fieldName of the attribute for which the default is desired
     * @return the default if one is available, null otherwise
     */
    @Deprecated
    public String getCollectionFieldDefaultValue(String docTypeName, String collectionName, String fieldName);

    /**
     * Returns whether or not this document's data dictionary file has flagged it to allow document copies.
     * 
     * @param document
     * @return True if copies are allowed, false otherwise.
     */
    public Boolean getAllowsCopy(MaintenanceDocument document);
    
    /**
     * Returns whether or not this document's data dictionary file has flagged it to allow maintenance new
     * or copy actions.
     * 
     * @param document
     * @return True if new or copy maintenance actions are allowed
     */
    public Boolean getAllowsNewOrCopy(String docTypeName);
    
    
    /**
     * Returns the business object used to store the values for the given collection.
     * 
     * @param docTypeName
     * @param collectionName
     * @return
     */
    public Class getCollectionBusinessObjectClass( String docTypeName, String collectionName );
    
    /**
     * Returns the definition for the maintainable item identified by "itemName". 
     * 
     * @param docTypeName
     * @param itemName
     * @return The item or <b>null</b> if the item does not exist.
     */
    @Deprecated
    public MaintainableItemDefinition getMaintainableItem( String docTypeName, String itemName );

    /**
     * Returns the definition for the maintainable field identified by "fieldName". 
     * 
     * @param docTypeName
     * @param fieldName
     * @return The field or <b>null</b> if the item does not exist or is not a field.
     */
    @Deprecated
    public MaintainableFieldDefinition getMaintainableField( String docTypeName, String fieldName );

    /**
     * Returns the definition for the maintainable collection identified by "collectionName". 
     * 
     * @param docTypeName
     * @param collectionName
     * @return The collection or <b>null</b> if the item does not exist or is not a collection.
     */
    @Deprecated
    public MaintainableCollectionDefinition getMaintainableCollection( String docTypeName, String collectionName );

    /* They are returned in order of discovery (depth-first search) */
    /**
     * Gets a list of all top-level maintainable collections on the document.
     * 
     * 
     * @param docTypeName
     * @return
     */
    @Deprecated
    public List<MaintainableCollectionDefinition> getMaintainableCollections( String docTypeName );
    
    /**
     * Returns a list of all collections within the given collection
     * 
     * @param parentCollection
     * @return
     */
    @Deprecated
    public List<MaintainableCollectionDefinition> getMaintainableCollections( MaintainableCollectionDefinition parentCollection );


    /**
     * Validates the maintenance document contains values for the fields declared as required in the 
     * maintenance document data dictionary file.
     * 
     * @param document
     */
    @Deprecated
    public void validateMaintenanceRequiredFields(MaintenanceDocument document);
    
    /**
     * validates the collections of the maintenance document checking to see if duplicate entries in the collection exist
     * @param document
     */
    public void validateMaintainableCollectionsForDuplicateEntries(MaintenanceDocument document);

    @Deprecated
    public void validateMaintainableCollectionsAddLineRequiredFields(MaintenanceDocument document, PersistableBusinessObject businessObject, String collectionName );
    
    public MaintenanceDocumentEntry getMaintenanceDocumentEntry(String docTypeName);
    
    //for issue KULRice 3072, checking PK copy prop
    public boolean getPreserveLockingKeysOnCopy(Class businessObjectClass);
    
    //for issue KULRice 3070
    public Boolean getAllowsRecordDeletion(Class businessObjectClass);
    //for issue KULRice3070
    public Boolean getAllowsRecordDeletion(MaintenanceDocument document);
    
    /**
     * @param businessObjectClass - business object class for maintenance definition
     * @return Boolean indicating whether translating of codes is configured to true in maintenance definition  
     */
    @Deprecated
    public Boolean translateCodes(Class businessObjectClass);
    
}
