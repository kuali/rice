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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.CompletionException;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.core.datadictionary.exception.OverrideEntryException;
import org.kuali.core.exceptions.ValidationException;

/**
 * Contains common properties and methods for data dictionary entries.
 * 
 * 
 */
abstract public class DataDictionaryEntryBase implements DataDictionaryEntry {
    // logger
    private static Log LOG = LogFactory.getLog(DataDictionaryEntryBase.class);

    private Map<String, AttributeDefinition> attributes;
    private Map<String, CollectionDefinition> collections;
    private Map<String, RelationshipDefinition> relationships;
    
    public DataDictionaryEntryBase() {
        this.attributes = new LinkedHashMap();
        this.collections = new LinkedHashMap();
        this.relationships = new LinkedHashMap();
    }
    
    /* Returns the given entry class (bo class or document class) */
    public abstract Class getEntryClass();
    
    /**
     * Adds the given AttributeDefinition to the collection of AttributeDefinitions associated with this BusinessObjectEntry.
     * 
     * @param attributeDefinition
     */
    public void addAttributeDefinition(AttributeDefinition attributeDefinition) {
        if (attributeDefinition == null) {
            throw new IllegalArgumentException("invalid (null) attributeDefinition");
        }
        LOG.debug("calling addAttributeDefinition '" + attributeDefinition.getName() + "'");

        String attributeName = attributeDefinition.getName();
        if (StringUtils.isBlank(attributeName)) {
            throw new ValidationException("invalid (blank) attributeName");
        }

        if (collections.containsKey(attributeName)) {
            throw new DuplicateEntryException("attribute '" + attributeName + "' already defined as a Collection for class '" + getEntryClass().getName() + "'");
        }
        else {
            if (Boolean.TRUE.equals(attributeDefinition.getOverride())) {
                if (!attributes.containsKey(attributeName)) {
                    throw new OverrideEntryException("overriding attribute '" + attributeName + "' doesn't have an attribute to override for class '" + getEntryClass().getName() + "'");
                }
            }
            else {
                if (attributes.containsKey(attributeName)) {
                    throw new DuplicateEntryException("attribute '" + attributeName + "' already defined for class '" + getEntryClass().getName() + "'");
                }
            }
        }

        this.attributes.put(attributeName, attributeDefinition);
    }

    /**
     * @param attributeName
     * @return AttributeDefinition with the given name, or null if none with that name exists
     */
    final public AttributeDefinition getAttributeDefinition(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        LOG.debug("calling getAttributeDefinition '" + attributeName + "'");

        return (AttributeDefinition) attributes.get(attributeName);
    }

    /**
     * @return a Map containing all AttributeDefinitions associated with this BusinessObjectEntry, indexed by attributeName
     */
    public Map getAttributes() {
        LOG.debug("calling getAttributeDefinitions");

        return Collections.unmodifiableMap(this.attributes);
    }


    /**
     * Adds the given CollectionDefinition to the collection of CollectionDefinitions associated with this BusinessObjectEntry.
     * 
     * @param collectionEntry
     */
    public void addCollectionDefinition(CollectionDefinition collectionDefinition) {
        if (collectionDefinition == null) {
            throw new IllegalArgumentException("invalid (null) collectionDefinition");
        }
        LOG.debug("calling addCollectionDefinition '" + collectionDefinition.getName() + "'");

        String collectionName = collectionDefinition.getName();
        if (StringUtils.isBlank(collectionName)) {
            throw new ValidationException("invalid (blank) collectionName");
        }

        if (collections.containsKey(collectionName)) {
            throw new DuplicateEntryException("collection '" + collectionName + "' already defined for class '" + getEntryClass().getName() + "'");
        }
        else if (attributes.containsKey(collectionName)) {
            throw new DuplicateEntryException("collection '" + collectionName + "' already defined as an Attribute for class '" + getEntryClass().getName() + "'");
        }

        this.collections.put(collectionName, collectionDefinition);
    }


    /**
     * @param collectionName
     * @return CollectionDefinition with the given name, or null if none with that name exists
     */
    public CollectionDefinition getCollectionDefinition(String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }
        LOG.debug("calling getCollectionDefinition '" + collectionName + "'");

        return (CollectionDefinition) collections.get(collectionName);
    }

    /**
     * @return a Map containing all CollectionDefinitions associated with this BusinessObjectEntry, indexed by collectionName
     */
    public Map getCollections() {
        return Collections.unmodifiableMap(this.collections);
    }

    /**
     * Locates the delegates putatively associated with all component AttributeReferences, if any, or dies trying.
     * 
     * @param dataDictionary
     * @throws CompletionException if an AttributeReference can't be expanded
     */
    public void expandAttributeReferences(DataDictionary dataDictionary, ValidationCompletionUtils validationCompletionUtils) {
        for (Iterator i = attributes.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            AttributeDefinition attributeDefinition = (AttributeDefinition) e.getValue();
            if (attributeDefinition instanceof AttributeReferenceDefinition) {
                AttributeReferenceDefinition attributeReferenceDefinition = (AttributeReferenceDefinition) attributeDefinition;

                attributeReferenceDefinition.assignDelegate(this, dataDictionary);
                attributeReferenceDefinition.completeDeferredValidation(getEntryClass(), validationCompletionUtils);
            }
        }
    }


    /**
     * Adds the given RelationshipDefinition to the collection of RelationshipDefinitions associated with this BusinessObjectEntry.
     * 
     * @param relationshipDefinition
     */
    public void addRelationshipDefinition(RelationshipDefinition relationshipDefinition) {
        if (relationshipDefinition == null) {
            throw new IllegalArgumentException("invalid (null) relationshipDefinition");
        }
        LOG.debug("calling addRelationshipDefinition '" + relationshipDefinition.getObjectAttributeName() + "'");

        String relationshipName = relationshipDefinition.getObjectAttributeName();
        if (StringUtils.isBlank(relationshipName)) {
            throw new ValidationException("invalid (blank) relationshipName");
        }

        this.relationships.put(relationshipName, relationshipDefinition);
    }


    /**
     * @param relationshipName
     * @return RelationshipDefinition with the given name, or null if none with that name exists
     */
    public RelationshipDefinition getRelationshipDefinition(String relationshipName) {
        if (StringUtils.isBlank(relationshipName)) {
            throw new IllegalArgumentException("invalid (blank) relationshipName");
        }
        LOG.debug("calling getRelationshipDefinition '" + relationshipName + "'");

        return (RelationshipDefinition) relationships.get(relationshipName);
    }

    /**
     * @return a Map containing all RelationshipDefinitions associated with this BusinessObjectEntry, indexed by relationshipName
     */
    public Map<String, RelationshipDefinition> getRelationships() {
        return Collections.unmodifiableMap(this.relationships);
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils) {
        
        for (Iterator i = attributes.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            AttributeDefinition attributeDefinition = (AttributeDefinition) e.getValue();
            attributeDefinition.completeValidation(getEntryClass(), null, validationCompletionUtils);
        }

        for (Iterator i = collections.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            CollectionDefinition collectionDefinition = (CollectionDefinition) e.getValue();
            collectionDefinition.completeValidation(getEntryClass(), null, validationCompletionUtils);
        }

        for (Iterator i = relationships.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            RelationshipDefinition relationshipDefinition = (RelationshipDefinition) e.getValue();
            relationshipDefinition.completeValidation(getEntryClass(), null, validationCompletionUtils);
        }
    }
}