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
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.MaintainableCollectionDefinition;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableItemDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.datadictionary.MaintenanceDocumentEntry;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;

/**
 * This class is the service implementation for the MaintenanceDocumentDictionary structure. Defines the API for the interacting
 * with Document-related entries in the data dictionary. This is the default implementation, that is delivered with Kuali.
 */
public class MaintenanceDocumentDictionaryServiceImpl implements MaintenanceDocumentDictionaryService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceDocumentDictionaryServiceImpl.class);

    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getMaintenanceLabel(java.lang.String)
     */
    public String getMaintenanceLabel(String docTypeName) {
        String label = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            label = entry.getLabel();
        }

        return label;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getMaintenanceDescription(java.lang.String)
     */
    public String getMaintenanceDescription(String docTypeName) {
        String description = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            description = entry.getDescription();
        }

        return description;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getMaintainableClass(java.lang.String)
     */
    public Class getMaintainableClass(String docTypeName) {
        Class maintainableClass = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            LOG.debug("suppling a generic Rule to insure basic validation");
            maintainableClass = entry.getMaintainableClass();
        }

        return maintainableClass;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getBusinessObjectClass(java.lang.String)
     */
    public Class getBusinessObjectClass(String docTypeName) {
        Class businessObjectClass = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            businessObjectClass = entry.getBusinessObjectClass();
        }

        return businessObjectClass;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getDocumentTypeName(java.lang.Class)
     */
    public String getDocumentTypeName(Class businessObjectClass) {
        String documentTypeName = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(businessObjectClass);
        if (entry != null) {
            documentTypeName = entry.getDocumentTypeName();
        }

        return documentTypeName;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getMaintainableSections(java.lang.String)
     */
    public List getMaintainableSections(String docTypeName) {
        List sections = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            sections = entry.getMaintainableSections();
        }

        return sections;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getBusinessRulesClass(MaintenanceDocument)
     */
    public Class getBusinessRulesClass(MaintenanceDocument document) {
        Maintainable maintainable = document.getOldMaintainableObject();
        if (maintainable == null) {
            throw new IllegalArgumentException("unable to determine documentType for maintenanceDocument with no oldMaintainableObject");
        }

        Class businessRulesClass = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(maintainable.getBoClass());
        if (entry != null) {
            businessRulesClass = entry.getBusinessRulesClass();
        }

        if (businessRulesClass == null) {
            return MaintenanceDocumentRuleBase.class; // default to a generic rule that will enforce Required fields
        }

        LOG.info("return class: " + businessRulesClass.getName());

        return businessRulesClass;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getDefaultExistenceChecks(java.lang.Class)
     */
    public Collection getDefaultExistenceChecks(Class businessObjectClass) {
        return getDefaultExistenceChecks(getDocumentTypeName(businessObjectClass));
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getDefaultExistenceChecks(java.lang.String)
     */
    public Collection getDefaultExistenceChecks(String docTypeName) {

        Collection defaultExistenceChecks = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            defaultExistenceChecks = entry.getDefaultExistenceChecks();
        }

        return defaultExistenceChecks;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getApplyApcRules(java.lang.Class)
     */
    public Collection getApplyApcRules(Class businessObjectClass) {
        return getApplyApcRules(getDocumentTypeName(businessObjectClass));
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getApplyApcRules(java.lang.String)
     */
    public Collection getApplyApcRules(String docTypeName) {

        Collection apcRules = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            apcRules = entry.getApcRules();
        }

        return apcRules;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getLockingKeys(java.lang.String)
     */
    public List getLockingKeys(String docTypeName) {
        List lockingKeys = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            lockingKeys = entry.getLockingKeyFieldnames();
        }

        return lockingKeys;
    }

    /**
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * @return
     */
    public DataDictionary getDataDictionary() {
        return this.dataDictionaryService.getDataDictionary();
    }

    /**
     * @param docTypeName
     * @return
     */
    public MaintenanceDocumentEntry getMaintenanceDocumentEntry(String docTypeName) {
        if (StringUtils.isBlank(docTypeName)) {
            throw new IllegalArgumentException("invalid (blank) docTypeName");
        }

        MaintenanceDocumentEntry entry = (MaintenanceDocumentEntry)getDataDictionary().getDocumentEntry(docTypeName);
        return entry;
    }

    private MaintenanceDocumentEntry getMaintenanceDocumentEntry(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (blank) businessObjectClass");
        }

        MaintenanceDocumentEntry entry = getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(businessObjectClass);
        return entry;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getFieldDefaultValue(java.lang.Class, java.lang.String)
     */
    public String getFieldDefaultValue(Class boClass, String fieldName) {

        // input parameter validation
        if (boClass == null) {
            throw new IllegalArgumentException("The boClass parameter value specified was " + "null.  A valid class representing the boClass must " + "be specified.");
        }

        // call the twin
        return getFieldDefaultValue(getDocumentTypeName(boClass), fieldName);
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getFieldDefaultValue(java.lang.String, java.lang.String)
     */
    public String getFieldDefaultValue(String docTypeName, String fieldName) {

        // input parameter validation
        if (StringUtils.isBlank(docTypeName)) {
            throw new IllegalArgumentException("The docTypeName parameter value specified was  " + "blank, whitespace, or null.  A valid string representing the docTypeName must " + "be specified.");
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("The fieldName parameter value specified was  " + "blank, whitespace, or null.  A valid string representing the fieldName must " + "be specified.");
        }

        // walk through the sections
        List sections = getMaintainableSections(docTypeName);
        for (Iterator sectionIterator = sections.iterator(); sectionIterator.hasNext();) {
            MaintainableSectionDefinition section = (MaintainableSectionDefinition) sectionIterator.next();

            // walk through the fields
            Collection fields = section.getMaintainableItems();
            String defaultValue = getFieldDefaultValue(fields, fieldName);
            // need to keep trying sections until a match is found
            if (defaultValue != null) {
                return defaultValue;
            }
        }
        return null;
    }

    private String getFieldDefaultValue(Collection maintainableFields, String fieldName) {
        for (Iterator iterator = maintainableFields.iterator(); iterator.hasNext();) {
            MaintainableItemDefinition item = (MaintainableItemDefinition) iterator.next();
            // only check fields...skip subcollections
            if (item instanceof MaintainableFieldDefinition) {

                MaintainableFieldDefinition field = (MaintainableFieldDefinition) item;

                // if the field name matches
                if (field.getName().endsWith(fieldName)) {

                    // preferentially take the raw default value
                    if (StringUtils.isNotBlank(field.getDefaultValue())) {
                        return field.getDefaultValue();
                    }

                    // take the valuefinder
                    else if (field.getDefaultValueFinderClass() != null) {

                        // attempt to get an instance of the defaultValueFinderClass
                        ValueFinder valueFinder = null;
                        try {
                            valueFinder = (ValueFinder) field.getDefaultValueFinderClass().newInstance();
                        }
                        catch (Exception e) {
                            LOG.info("Exception obtaining valueFinder for collection field default value", e);
                            valueFinder = null;
                        }

                        // get the value
                        if (valueFinder != null) {
                            return valueFinder.getValue();
                        }
                    }
                    // if we found the field, but no default anything, then we're done
                    else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getCollectionFieldDefaultValue(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public String getCollectionFieldDefaultValue(String docTypeName, String collectionName, String fieldName) {
        // input parameter validation
        if (StringUtils.isBlank(docTypeName)) {
            throw new IllegalArgumentException("The docTypeName parameter value specified was blank, whitespace, or null.  A valid string representing the docTypeName must be specified.");
        }
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("The fieldName parameter value specified was blank, whitespace, or null.  A valid string representing the fieldName must be specified.");
        }
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("The collectionName parameter value specified was null.  A valid string representing the collectionName must be specified.");
        }

        MaintainableCollectionDefinition coll = getMaintainableCollection(docTypeName, collectionName);
        if (coll != null) {
            Collection collectionFields = coll.getMaintainableFields();
            return getFieldDefaultValue(collectionFields, fieldName);
        }
        return null;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getAllowsCopy(MaintenanceDocument)
     */
    public Boolean getAllowsCopy(MaintenanceDocument document) {
        Boolean allowsCopy = null;
        if (document != null) {
            MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(document.getNewMaintainableObject().getBoClass());
            if (entry != null) {
                allowsCopy = Boolean.valueOf(entry.getAllowsCopy());
            }
        }

        return allowsCopy;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#getAllowsNewOrCopy(java.lang.String)
     */
    public Boolean getAllowsNewOrCopy(String docTypeName) {
        Boolean allowsNewOrCopy = null;

        if (docTypeName != null) {
            MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
            if (entry != null) {
                allowsNewOrCopy = Boolean.valueOf(entry.getAllowsNewOrCopy());
            }
        }

        return allowsNewOrCopy;
    }

    public MaintainableItemDefinition getMaintainableItem(String docTypeName, String itemName) {
        // input parameter validation
        if (StringUtils.isBlank(docTypeName)) {
            throw new IllegalArgumentException("The docTypeName parameter value specified was  " + "blank, whitespace, or null.  A valid string representing the docTypeName must " + "be specified.");
        }
        if (StringUtils.isBlank(itemName)) {
            throw new IllegalArgumentException("The itemName parameter value specified was  " + "blank, whitespace, or null.  A valid string representing the itemName must " + "be specified.");
        }

        // split name for subcollections
        String[] subItems = {};
        subItems = StringUtils.split(itemName, ".");


        // walk through the sections
        List sections = getMaintainableSections(docTypeName);
        for (Iterator sectionIterator = sections.iterator(); sectionIterator.hasNext();) {
            MaintainableSectionDefinition section = (MaintainableSectionDefinition) sectionIterator.next();

            // walk through the fields
            Collection fields = section.getMaintainableItems();
            for (Iterator fieldIterator = fields.iterator(); fieldIterator.hasNext();) {
                MaintainableItemDefinition item = (MaintainableItemDefinition) fieldIterator.next();

                if (item.getName().equals(itemName)) {
                    return item;
                }
                // if collection check to see if it has sub collections
                // for now this only allows 1 level (i.e. a.b) it should be expanded at some point
                if (item instanceof MaintainableCollectionDefinition) {
                    MaintainableCollectionDefinition col = (MaintainableCollectionDefinition) item;
                    if ((subItems.length > 1) && (StringUtils.equals(col.getName(), subItems[0]))) {
                        for (Iterator<MaintainableCollectionDefinition> colIterator = col.getMaintainableCollections().iterator(); colIterator.hasNext();) {
                            MaintainableCollectionDefinition subCol = (MaintainableCollectionDefinition) colIterator.next();
                            if (subCol.getName().equals(subItems[1])) {
                                return subCol;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public MaintainableFieldDefinition getMaintainableField(String docTypeName, String fieldName) {
        MaintainableItemDefinition item = getMaintainableItem(docTypeName, fieldName);
        if (item != null && item instanceof MaintainableFieldDefinition) {
            return (MaintainableFieldDefinition) item;
        }
        return null;
    }

    public MaintainableCollectionDefinition getMaintainableCollection(String docTypeName, String collectionName) {
        // strip brackets as they are not needed to get to collection class
        // Like the other subcollections changes this currently only supports one sub level
        if (StringUtils.contains(collectionName, "[")) {
            collectionName = StringUtils.substringBefore(collectionName, "[") + StringUtils.substringAfter(collectionName, "]");
        }
        MaintainableItemDefinition item = getMaintainableItem(docTypeName, collectionName);
        if (item != null && item instanceof MaintainableCollectionDefinition) {
            return (MaintainableCollectionDefinition) item;
        }
        return null;
    }

    public Class getCollectionBusinessObjectClass(String docTypeName, String collectionName) {
        MaintainableCollectionDefinition coll = getMaintainableCollection(docTypeName, collectionName);
        if (coll != null) {
            return coll.getBusinessObjectClass();
        }
        return null;
    }

    public List<MaintainableCollectionDefinition> getMaintainableCollections(String docTypeName) {
        ArrayList<MaintainableCollectionDefinition> collections = new ArrayList<MaintainableCollectionDefinition>();

        // walk through the sections
        List sections = getMaintainableSections(docTypeName);
        for (Iterator sectionIterator = sections.iterator(); sectionIterator.hasNext();) {
            MaintainableSectionDefinition section = (MaintainableSectionDefinition) sectionIterator.next();

            // walk through the fields
            Collection fields = section.getMaintainableItems();
            for (Iterator fieldIterator = fields.iterator(); fieldIterator.hasNext();) {
                MaintainableItemDefinition item = (MaintainableItemDefinition) fieldIterator.next();

                if (item instanceof MaintainableCollectionDefinition) {
                    collections.add((MaintainableCollectionDefinition) item);
                    // collections.addAll( getMaintainableCollections( (MaintainableCollectionDefinition)item ) );
                }
            }
        }

        return collections;
    }

    public List<MaintainableCollectionDefinition> getMaintainableCollections(MaintainableCollectionDefinition parentCollection) {
        ArrayList<MaintainableCollectionDefinition> collections = new ArrayList<MaintainableCollectionDefinition>();

        // walk through the sections
        Collection<MaintainableCollectionDefinition> colls = parentCollection.getMaintainableCollections();
        for (MaintainableCollectionDefinition coll : colls) {
            collections.add(coll);
            collections.addAll(getMaintainableCollections(coll));
        }

        return collections;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#validateMaintenanceRequiredFields(org.kuali.core.document.MaintenanceDocument)
     */
    public void validateMaintenanceRequiredFields(MaintenanceDocument document) {
        Maintainable newMaintainableObject = document.getNewMaintainableObject();
        if (newMaintainableObject == null) {
            LOG.error("New maintainable is null");
            throw new RuntimeException("New maintainable is null");
        }

        List<MaintainableSectionDefinition> maintainableSectionDefinitions = getMaintainableSections(getDocumentTypeName(newMaintainableObject.getBoClass()));
        for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSectionDefinitions) {
            for (MaintainableItemDefinition maintainableItemDefinition : maintainableSectionDefinition.getMaintainableItems()) {
                // validate fields
                if (maintainableItemDefinition instanceof MaintainableFieldDefinition) {
                    validateMaintainableFieldRequiredFields((MaintainableFieldDefinition) maintainableItemDefinition, newMaintainableObject.getBusinessObject(), maintainableItemDefinition.getName());
                }
                // validate collections
                else if (maintainableItemDefinition instanceof MaintainableCollectionDefinition) {
                    validateMaintainableCollectionsRequiredFields(newMaintainableObject.getBusinessObject(), (MaintainableCollectionDefinition) maintainableItemDefinition);
                }
            }
        }
    }

    /**
     * generates error message if a field is marked as required but is not filled in
     * 
     * @param maintainableFieldDefinition
     * @param businessObject
     * @param fieldName
     */
    private void validateMaintainableFieldRequiredFields(MaintainableFieldDefinition maintainableFieldDefinition, PersistableBusinessObject businessObject, String fieldName) {

        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("invalid fieldName parameter.");
        }
        // if required check we have a value for this field
        if (maintainableFieldDefinition.isRequired() && !maintainableFieldDefinition.isReadOnly() ) {
            try {
                Object obj = ObjectUtils.getNestedValue(businessObject, fieldName);

                if (obj == null || StringUtils.isBlank(obj.toString())) {
                    String attributeLabel = dataDictionaryService.getAttributeLabel(businessObject.getClass(), fieldName);
                    String shortLabel = dataDictionaryService.getAttributeShortLabel(businessObject.getClass(), fieldName);
                    GlobalVariables.getErrorMap().putError(fieldName, KeyConstants.ERROR_REQUIRED, attributeLabel + " (" + shortLabel + ")" );
                }
            } catch( Exception ex ) {
                LOG.error( "unable to read property during doc required field checks", ex );
            }
        }
    }

    
    private MaintainableCollectionDefinition getCollectionDefinition( String docTypeName, String collectionName ) {
        String currentCollection = StringUtils.substringBefore( collectionName, "." );
        String nestedCollections = StringUtils.substringAfter( collectionName, "." );
        // strip off any array indexes
        currentCollection = StringUtils.substringBefore( collectionName, "[" );
        // loop over all sections to find this collection
        List<MaintainableSectionDefinition> maintainableSectionDefinitions = getMaintainableSections( docTypeName );
        for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSectionDefinitions) {
            for (MaintainableItemDefinition maintainableItemDefinition : maintainableSectionDefinition.getMaintainableItems()) {
                if (maintainableItemDefinition instanceof MaintainableCollectionDefinition && maintainableItemDefinition.getName().equals( currentCollection ) ) {
                    if ( StringUtils.isBlank( nestedCollections ) ) {
                        return (MaintainableCollectionDefinition) maintainableItemDefinition;
                    } else {
                        return getCollectionDefinition( (MaintainableCollectionDefinition)maintainableItemDefinition, nestedCollections );
                    }
                }
            }
        }
        return null;
    }

    private MaintainableCollectionDefinition getCollectionDefinition( MaintainableCollectionDefinition collectionDef, String collectionName ) {
        String currentCollection = StringUtils.substringBefore( collectionName, "." );
        String nestedCollections = StringUtils.substringAfter( collectionName, "." );
        // strip off any array indexes
        currentCollection = StringUtils.substringBefore( collectionName, "[" );
        // loop over all nested collections
        for (MaintainableCollectionDefinition maintainableCollectionDefinition : collectionDef.getMaintainableCollections()) {
            if ( maintainableCollectionDefinition.getName().equals( collectionName ) ) {
                if ( StringUtils.isBlank( nestedCollections ) ) {
                    return maintainableCollectionDefinition;
                } else {
                    return getCollectionDefinition( maintainableCollectionDefinition, nestedCollections );
                }
            }
        }
        return null;
    }
    
    public void validateMaintainableCollectionsAddLineRequiredFields(MaintenanceDocument document, PersistableBusinessObject businessObject, String collectionName ) {
        MaintainableCollectionDefinition def = getCollectionDefinition( getDocumentTypeName(businessObject.getClass()), collectionName );
        if ( def != null ) {
            validateMaintainableCollectionsAddLineRequiredFields( document, businessObject, collectionName, def, 0);
        }
    }
    /**
     * calls code to generate error messages if maintainableFields within any collections or sub-collections are marked as required
     * 
     * @param document
     * @param businessObject
     * @param collectionName
     * @param maintainableCollectionDefinition
     * @param depth
     */
    private void validateMaintainableCollectionsAddLineRequiredFields(MaintenanceDocument document, PersistableBusinessObject businessObject, String collectionName, MaintainableCollectionDefinition maintainableCollectionDefinition, int depth) {
        if ( depth == 0 ) {
            GlobalVariables.getErrorMap().addToErrorPath("add");
        }
        // validate required fields on fields withing collection definition
        PersistableBusinessObject element = document.getNewMaintainableObject().getNewCollectionLine( collectionName );
        GlobalVariables.getErrorMap().addToErrorPath(collectionName);
        for (MaintainableFieldDefinition maintainableFieldDefinition : maintainableCollectionDefinition.getMaintainableFields()) {
            final String fieldName = maintainableFieldDefinition.getName();
            validateMaintainableFieldRequiredFields(maintainableFieldDefinition, element, fieldName);
            
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(collectionName);
        if ( depth == 0 ) {
            GlobalVariables.getErrorMap().removeFromErrorPath("add");
        }
    }

    /**
     * calls code to generate error messages if maintainableFields within any collections or sub-collections are marked as required
     * 
     * @param businessObject
     * @param maintainableCollectionDefinition
     */
    private void validateMaintainableCollectionsRequiredFields(PersistableBusinessObject businessObject, MaintainableCollectionDefinition maintainableCollectionDefinition) {
        final String collectionName = maintainableCollectionDefinition.getName();

        // validate required fields on fields withing collection definition
        Collection<PersistableBusinessObject> collection = (Collection) ObjectUtils.getPropertyValue(businessObject, collectionName);
        if (collection != null && !collection.isEmpty()) {
            for (MaintainableFieldDefinition maintainableFieldDefinition : maintainableCollectionDefinition.getMaintainableFields()) {
                int pos = 0;
                final String fieldName = maintainableFieldDefinition.getName();
                for (PersistableBusinessObject element : collection) {
                    String parentName = collectionName + "[" + (pos++) + "]";
                    GlobalVariables.getErrorMap().addToErrorPath(parentName);
                    validateMaintainableFieldRequiredFields(maintainableFieldDefinition, element, fieldName);
                    GlobalVariables.getErrorMap().removeFromErrorPath(parentName);
                }
            }

            // recursivley validate required fields on subcollections
            GlobalVariables.getErrorMap().addToErrorPath(collectionName);
            for (MaintainableCollectionDefinition nestedMaintainableCollectionDefinition : maintainableCollectionDefinition.getMaintainableCollections()) {
                for (PersistableBusinessObject element : collection) {
                    validateMaintainableCollectionsRequiredFields(element, nestedMaintainableCollectionDefinition);
                }
            }
            GlobalVariables.getErrorMap().removeFromErrorPath(collectionName);
        }
    }
    
    /**
     * default implementation checks for duplicats based on keys of objects only
     * 
     * @see org.kuali.core.service.MaintenanceDocumentDictionaryService#validateMaintainableCollectionsForDuplicateEntries(org.kuali.core.document.MaintenanceDocument)
     */
    public void validateMaintainableCollectionsForDuplicateEntries(MaintenanceDocument document) {
        Maintainable newMaintainableObject = document.getNewMaintainableObject();
        if (newMaintainableObject == null) {
            LOG.error("New maintainable is null");
            throw new RuntimeException("New maintainable is null");
        }

        List<MaintainableSectionDefinition> maintainableSectionDefinitions = getMaintainableSections(getDocumentTypeName(newMaintainableObject.getBoClass()));
        for (MaintainableSectionDefinition maintainableSectionDefinition : maintainableSectionDefinitions) {
            for (MaintainableItemDefinition maintainableItemDefinition : maintainableSectionDefinition.getMaintainableItems()) {
                // validate collections
                if (maintainableItemDefinition instanceof MaintainableCollectionDefinition) {
                    validateMaintainableCollectionsForDuplicateEntries(newMaintainableObject.getBusinessObject(), (MaintainableCollectionDefinition) maintainableItemDefinition);
                }
            }
        }
    }

    /**
     * recursivly checks collections for duplicate entries based on key valuse
     * 
     * @param businessObject
     * @param maintainableCollectionDefinition
     */
    private void validateMaintainableCollectionsForDuplicateEntries(PersistableBusinessObject businessObject, MaintainableCollectionDefinition maintainableCollectionDefinition) {
        final String collectionName = maintainableCollectionDefinition.getName();

        if (maintainableCollectionDefinition.dissalowDuplicateKey()) {
            final Class maintainableBusinessObjectClass = businessObject.getClass();
            // validate that no duplicates based on keys exist
            Collection<PersistableBusinessObject> collection = (Collection) ObjectUtils.getPropertyValue(businessObject, collectionName);
            if (collection != null && !collection.isEmpty()) {
                final String propertyName = maintainableCollectionDefinition.getAttributeToHighlightOnDuplicateKey();
                // get collection label for dd
                final String label = dataDictionaryService.getCollectionLabel(maintainableBusinessObjectClass, collectionName);
                final String shortLabel = dataDictionaryService.getCollectionShortLabel(maintainableBusinessObjectClass, collectionName);
                int pos = 0;
                for (PersistableBusinessObject element : collection) {
                    String pathToElement = collectionName + "[" + (pos++) + "]";
                    if (ObjectUtils.countObjectsWithIdentitcalKey(collection, element) > 1) {
                        GlobalVariables.getErrorMap().addToErrorPath(pathToElement);
                        GlobalVariables.getErrorMap().putError(propertyName, KeyConstants.ERROR_DUPLICATE_ELEMENT, new String[] { label, shortLabel });
                        GlobalVariables.getErrorMap().removeFromErrorPath(pathToElement);
                    }
                }

                // recursivley check for duplicate entries on subcollections
                GlobalVariables.getErrorMap().addToErrorPath(collectionName);
                for (MaintainableCollectionDefinition nestedMaintainableCollectionDefinition : maintainableCollectionDefinition.getMaintainableCollections()) {
                    for (PersistableBusinessObject element : collection) {
                        validateMaintainableCollectionsForDuplicateEntries(element, nestedMaintainableCollectionDefinition);
                    }
                }
                GlobalVariables.getErrorMap().removeFromErrorPath(collectionName);

            }
        }
    }

}