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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

/**
 * MaintainableCollectionDefinition
 */
public class MaintainableCollectionDefinition extends MaintainableItemDefinition implements CollectionDefinitionI{
    // logger
    private static Log LOG = LogFactory.getLog(MaintainableCollectionDefinition.class);

    private String name;
    private Class businessObjectClass;

    private String sourceClassName;
    private String sourceAttributeName;
    private String summaryTitle;
    private String attributeToHighlightOnDuplicateKey;

    private boolean includeAddLine = true;
    private boolean includeMultipleLookupLine = true;

    private Map maintainableFields;
    private Map maintainableCollections;
    private Map summaryFields;
    private Map duplicateIdentificationFields;

    private Map<String, String> template;


    public MaintainableCollectionDefinition() {
        LOG.debug("creating new MaintainableCollectionDefinition");

        this.maintainableFields = new LinkedHashMap();
        this.maintainableCollections = new LinkedHashMap();
        this.summaryFields = new LinkedHashMap();
        this.duplicateIdentificationFields = new LinkedHashMap();
    }


    /**
     * @return attributeName
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name to the given value.
     * 
     * @param name
     * @throws IllegalArgumentException if the given name is blank
     */
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling setName '" + name + "'");
        }

        this.name = name;
    }


    /**
     * @return businessObjectClass
     */
    public Class getBusinessObjectClass() {
        return this.businessObjectClass;
    }

    /**
     * Sets businessObjectClass to the given value
     * 
     * @param businessObjectClass
     */
    public void setBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling setBusinessObjectClass '" + businessObjectClass.getName() + "'");
        }

        this.businessObjectClass = businessObjectClass;
    }


    /**
     * @param maintainableField
     * @throws IllegalArgumentException if the given maintainableField is null
     */
    public void addMaintainableField(MaintainableFieldDefinition maintainableField) {
        if (maintainableField == null) {
            throw new IllegalArgumentException("invalid (null) maintainableField");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling addMaintainableField for field '" + maintainableField.getName() + "'");
        }

        String fieldName = maintainableField.getName();
        if (this.maintainableFields.containsKey(fieldName)) {
            throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
        }

        this.maintainableFields.put(fieldName, maintainableField);
    }


    /**
     * @return Collection of all lookupField MaintainableFieldDefinitions associated with this MaintainableCollectionDefinition, in
     *         the order in which they were added
     */
    public Collection<MaintainableFieldDefinition> getMaintainableFields() {
        return Collections.unmodifiableCollection(this.maintainableFields.values());
    }

    public Collection getFields() {
        return this.getMaintainableFields();
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        if (!validationCompletionUtils.isCollectionPropertyOf(rootBusinessObjectClass, name)) {
            throw new AttributeValidationException("unable to find collection named '" + name + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        if (!validationCompletionUtils.isBusinessObjectClass(this.businessObjectClass)) {
            throw new ClassValidationException("class '" + businessObjectClass.getName() + "' is not a subclass of BusinessObject (" + getParseLocation() + ")");
        }
        if (dissalowDuplicateKey()) {
            if (!validationCompletionUtils.isPropertyOf(this.businessObjectClass, attributeToHighlightOnDuplicateKey)) {
                throw new AttributeValidationException("unable to find attribute named '" + name + "'in businessObjectClass '" + businessObjectClass.getName() + "' of collection '" + name + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
            }
        }

        for (Iterator i = maintainableFields.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            MaintainableFieldDefinition maintainableField = (MaintainableFieldDefinition) e.getValue();
            maintainableField.completeValidation(this.businessObjectClass, null, validationCompletionUtils);
        }

        for (Iterator i = maintainableCollections.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            MaintainableCollectionDefinition maintainableCollection = (MaintainableCollectionDefinition) e.getValue();
            maintainableCollection.completeValidation(this.businessObjectClass, null, validationCompletionUtils);
        }
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


    public String getSourceClassName() {
        return sourceClassName;
    }


    public void setSourceClassName(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }


    public Map<String, String> getTemplate() {
        return template;
    }


    public void setTemplate(Map<String, String> template) {
        this.template = template;
    }

    public boolean getIncludeAddLine() {
        return includeAddLine;
    }


    public void setIncludeAddLine(boolean includeAddLine) {
        this.includeAddLine = includeAddLine;
    }

    /**
     * @param maintainableCollection
     * @throws IllegalArgumentException if the given maintainableCollection is null
     */
    public void addMaintainableCollection(MaintainableCollectionDefinition maintainableCollection) {
        if (maintainableCollection == null) {
            throw new IllegalArgumentException("invalid (null) maintainableCollection");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling addMaintainableCollection for field '" + maintainableCollection.getName() + "'");
        }

        String fieldName = maintainableCollection.getName();
        if (this.maintainableCollections.containsKey(fieldName)) {
            throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
        }

        this.maintainableCollections.put(fieldName, maintainableCollection);
    }


    /**
     * @return Collection of all lookupField MaintainableCollectionDefinitions associated with this
     *         MaintainableCollectionDefinition, in the order in which they were added
     */
    public Collection<MaintainableCollectionDefinition> getMaintainableCollections() {
        return Collections.unmodifiableCollection(this.maintainableCollections.values());
    }

    public Collection getCollections() {
        return this.getMaintainableCollections();
    }
    
    /**
     * @param maintainableCollection
     * @throws IllegalArgumentException if the given maintainableCollection is null
     */
    public void addSummaryField(MaintainableFieldDefinition summaryField) {
        if (summaryField == null) {
            throw new IllegalArgumentException("invalid (null) summaryField");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling addSummaryField for field '" + summaryField.getName() + "'");
        }

        String fieldName = summaryField.getName();
        if (this.summaryFields.containsKey(fieldName)) {
            throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
        }

        this.summaryFields.put(fieldName, summaryField);
    }

    public void addDuplicateIdentificationField(MaintainableFieldDefinition identifierField) {
        if (identifierField == null) {
            throw new IllegalArgumentException("invalid (null) identifierField");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("calling addDuplicateIdentificationField for field '" + identifierField.getName() + "'");
        }

        String fieldName = identifierField.getName();
        if (this.duplicateIdentificationFields.containsKey(fieldName)) {
            throw new DuplicateEntryException("duplicate fieldName entry for field '" + fieldName + "'");
        }

        this.duplicateIdentificationFields.put(fieldName, identifierField);
    }

    
    /**
     * @return Collection of all SummaryFieldDefinitions associated with this SummaryFieldDefinition, in the order in which they
     *         were added
     */
    public Collection<MaintainableFieldDefinition> getSummaryFields() {
        return Collections.unmodifiableCollection(this.summaryFields.values());
    }

    public boolean hasSummaryField(String key) {
        return this.summaryFields.containsKey(key);
    }

    public boolean isIncludeMultipleLookupLine() {
        return includeMultipleLookupLine;
    }

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

    public boolean dissalowDuplicateKey() {
        return StringUtils.isNotBlank(getAttributeToHighlightOnDuplicateKey());
    }

    public Collection<MaintainableFieldDefinition> getDuplicateIdentificationFields() {
        return Collections.unmodifiableCollection(this.duplicateIdentificationFields.values());
    }

}