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
package org.kuali.core.datadictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

/**
 * MaintainableCollectionDefinition
 */
public class MaintainableCollectionDefinition extends MaintainableItemDefinition implements CollectionDefinitionI{
    // logger
    //private static Log LOG = LogFactory.getLog(MaintainableCollectionDefinition.class);

    private Class<? extends BusinessObject> businessObjectClass;

    private Class<? extends BusinessObject> sourceClassName;
    private String sourceAttributeName;
    private String summaryTitle;
    private String attributeToHighlightOnDuplicateKey;

    private boolean includeAddLine = true;
    private boolean includeMultipleLookupLine = true;
    private boolean alwaysAllowCollectionDeletion = false;

    private Map<String,MaintainableFieldDefinition> maintainableFieldMap = new HashMap<String, MaintainableFieldDefinition>();
    private Map<String,MaintainableCollectionDefinition> maintainableCollectionMap = new HashMap<String, MaintainableCollectionDefinition>();
    private Map<String,MaintainableFieldDefinition> summaryFieldMap = new HashMap<String, MaintainableFieldDefinition>();
    private Map<String,MaintainableFieldDefinition> duplicateIdentificationFieldMap = new HashMap<String, MaintainableFieldDefinition>();
    private List<MaintainableFieldDefinition> maintainableFields = new ArrayList<MaintainableFieldDefinition>();
    private List<MaintainableCollectionDefinition> maintainableCollections = new ArrayList<MaintainableCollectionDefinition>();
    private List<MaintainableFieldDefinition> summaryFields = new ArrayList<MaintainableFieldDefinition>();
    private List<MaintainableFieldDefinition> duplicateIdentificationFields = new ArrayList<MaintainableFieldDefinition>();

    public MaintainableCollectionDefinition() {}



    /**
     * @return businessObjectClass
     */
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * The BusinessObject class used for each row of this collection.
     */
    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    /**
     * @return Collection of all lookupField MaintainableFieldDefinitions associated with this MaintainableCollectionDefinition, in
     *         the order in which they were added
     */
    public List<MaintainableFieldDefinition> getMaintainableFields() {
        return maintainableFields;
    }

    public List<? extends FieldDefinitionI> getFields() {
        return maintainableFields;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isCollectionPropertyOf(rootBusinessObjectClass, getName())) {
            throw new AttributeValidationException("unable to find collection named '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (dissallowDuplicateKey()) {
            if (!DataDictionary.isPropertyOf(businessObjectClass, attributeToHighlightOnDuplicateKey)) {
                throw new AttributeValidationException("unable to find attribute named '" + attributeToHighlightOnDuplicateKey + "'in businessObjectClass '" + businessObjectClass.getName() + "' of collection '" + getName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
            }
        }
        
        for (MaintainableFieldDefinition maintainableField : maintainableFields ) {
            maintainableField.completeValidation(businessObjectClass, null);
        }

        for (MaintainableCollectionDefinition maintainableCollection : maintainableCollections ) {
            maintainableCollection.completeValidation(businessObjectClass, null);
        }

//        for (MaintainableFieldDefinition summaryField : summaryFields ) {
//            summaryField.completeValidation(businessObjectClass, null, validationCompletionUtils);
//        }
//        
//        for (MaintainableFieldDefinition identifierField : duplicateIdentificationFields) {
//            identifierField.completeValidation(businessObjectClass, null, validationCompletionUtils);
//        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "MaintainableCollectionDefinition for " + getName();
    }


    public String getSourceAttributeName() {
        return sourceAttributeName;
    }


    public void setSourceAttributeName(String sourceAttributeName) {
        this.sourceAttributeName = sourceAttributeName;
    }


    public Class<? extends BusinessObject> getSourceClassName() {
        return sourceClassName;
    }


    /** BusinessObject class which should be used for multiple value lookups for this collection.
     */
    public void setSourceClassName(Class<? extends BusinessObject> sourceClass) {
        this.sourceClassName = sourceClass;
    }

    public boolean getIncludeAddLine() {
        return includeAddLine;
    }


    /** Control whether an "add" line should be included at the top of this collection. */
    public void setIncludeAddLine(boolean includeAddLine) {
        this.includeAddLine = includeAddLine;
    }

    /**
     * @return Collection of all lookupField MaintainableCollectionDefinitions associated with this
     *         MaintainableCollectionDefinition, in the order in which they were added
     */
    public List<MaintainableCollectionDefinition> getMaintainableCollections() {
        return maintainableCollections;
    }

    public List<? extends CollectionDefinitionI> getCollections() {
        return maintainableCollections;
    }

    
    /**
     * @return Collection of all SummaryFieldDefinitions associated with this SummaryFieldDefinition, in the order in which they
     *         were added
     */
    public List<? extends FieldDefinitionI> getSummaryFields() {
        return summaryFields;
    }

    public boolean hasSummaryField(String key) {
        return summaryFieldMap.containsKey(key);
    }

    public boolean isIncludeMultipleLookupLine() {
        return includeMultipleLookupLine;
    }

    /** Set whether the multiple lookup line (and link) should appear above this collection. */
    public void setIncludeMultipleLookupLine(boolean includeMultipleLookupLine) {
        this.includeMultipleLookupLine = includeMultipleLookupLine;
    }

    public String getSummaryTitle() {
        return summaryTitle;
    }

    public void setSummaryTitle(String overrideSummaryName) {
        this.summaryTitle = overrideSummaryName;
    }


    public String getAttributeToHighlightOnDuplicateKey() {
        return attributeToHighlightOnDuplicateKey;
    }

    public void setAttributeToHighlightOnDuplicateKey(String attributeToHighlightOnDuplicate) {
        this.attributeToHighlightOnDuplicateKey = attributeToHighlightOnDuplicate;
    }

    public boolean dissallowDuplicateKey() {
        return StringUtils.isNotBlank(getAttributeToHighlightOnDuplicateKey());
    }

    public List<MaintainableFieldDefinition> getDuplicateIdentificationFields() {
        return duplicateIdentificationFields;
    }

    public void setMaintainableFields(List<MaintainableFieldDefinition> maintainableFields) {
        maintainableFieldMap.clear();
        for ( MaintainableFieldDefinition maintainableField : maintainableFields ) {
            if (maintainableField == null) {
                throw new IllegalArgumentException("invalid (null) maintainableField");
            }

            String fieldName = maintainableField.getName();
            if (maintainableFieldMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            maintainableFieldMap.put(fieldName, maintainableField);
        }
        this.maintainableFields = maintainableFields;
    }

    public void setMaintainableCollections(List<MaintainableCollectionDefinition> maintainableCollections) {
        maintainableCollectionMap.clear();
        for (MaintainableCollectionDefinition maintainableCollection : maintainableCollections ) {
            if (maintainableCollection == null) {
                throw new IllegalArgumentException("invalid (null) maintainableCollection");
            }

            String fieldName = maintainableCollection.getName();
            if (maintainableCollectionMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            maintainableCollectionMap.put(fieldName, maintainableCollection);
        }
        this.maintainableCollections = maintainableCollections;
    }

    public void setSummaryFields(List<MaintainableFieldDefinition> summaryFields) {
        summaryFieldMap.clear();
        for (MaintainableFieldDefinition summaryField : summaryFields ) {
            if (summaryField == null) {
                throw new IllegalArgumentException("invalid (null) summaryField");
            }

            String fieldName = summaryField.getName();
            if (summaryFieldMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            summaryFieldMap.put(fieldName, summaryField);
        }
        this.summaryFields = summaryFields;
    }

    /**
    The duplicateIdentificationFields element is used to define a set of
    fields that will be used to determine if two records in the collection
    are duplicates.
    */
    public void setDuplicateIdentificationFields(List<MaintainableFieldDefinition> duplicateIdentificationFields) {
        duplicateIdentificationFieldMap.clear();
        for (MaintainableFieldDefinition identifierField : duplicateIdentificationFields) {
            if (identifierField == null) {
                throw new IllegalArgumentException("invalid (null) identifierField");
            }

            String fieldName = identifierField.getName();
            if (duplicateIdentificationFieldMap.containsKey(fieldName)) {
                throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
            }

            duplicateIdentificationFieldMap.put(fieldName, identifierField);            
        }
        this.duplicateIdentificationFields = duplicateIdentificationFields;
    }



	public boolean isAlwaysAllowCollectionDeletion() {
		return this.alwaysAllowCollectionDeletion;
	}



	public void setAlwaysAllowCollectionDeletion(
			boolean alwaysAllowCollectionDeletion) {
		this.alwaysAllowCollectionDeletion = alwaysAllowCollectionDeletion;
	}
    
}