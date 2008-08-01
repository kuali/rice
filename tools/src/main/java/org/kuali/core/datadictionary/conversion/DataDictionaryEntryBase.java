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
package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private List<AttributeDefinition> attributes;
    private List<CollectionDefinition> collections;
    private List<RelationshipDefinition> relationships;
    private Map<String, AttributeDefinition> attributeMap;
    private Map<String, CollectionDefinition> collectionMap;
    private Map<String, RelationshipDefinition> relationshipMap;
    
    public String sourceFile = "";
    
    public DataDictionaryEntryBase() {
        this.attributes = new ArrayList<AttributeDefinition>();
        this.collections = new ArrayList<CollectionDefinition>();
        this.relationships = new ArrayList<RelationshipDefinition>();
        this.attributeMap = new LinkedHashMap<String, AttributeDefinition>();
        this.collectionMap = new LinkedHashMap<String, CollectionDefinition>();
        this.relationshipMap = new LinkedHashMap<String, RelationshipDefinition>();
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
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling addAttributeDefinition '" + attributeDefinition.getName() + "'");
        }
        String attributeName = attributeDefinition.getName();
        if (StringUtils.isBlank(attributeName)) {
            throw new ValidationException("invalid (blank) attributeName");
        }

        if (collectionMap.containsKey(attributeName)) {
            throw new DuplicateEntryException("attribute '" + attributeName + "' already defined as a Collection for class '" + getEntryClass().getName() + "'");
        }
        else {
            if (Boolean.TRUE.equals(attributeDefinition.getOverride())) {
                if (!attributeMap.containsKey(attributeName)) {
                    throw new OverrideEntryException("overriding attribute '" + attributeName + "' doesn't have an attribute to override for class '" + getEntryClass().getName() + "'");
                }
            }
            else {
                if (attributeMap.containsKey(attributeName)) {
                    throw new DuplicateEntryException("attribute '" + attributeName + "' already defined for class '" + getEntryClass().getName() + "'");
                }
            }
        }

        this.attributes.add(attributeDefinition);
        this.attributeMap.put(attributeName, attributeDefinition);
    }

    /**
     * @param attributeName
     * @return AttributeDefinition with the given name, or null if none with that name exists
     */
    final public AttributeDefinition getAttributeDefinition(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getAttributeDefinition '" + attributeName + "'");
        }
        return (AttributeDefinition) attributeMap.get(attributeName);
    }

    /**
     * @return a Map containing all AttributeDefinitions associated with this BusinessObjectEntry, indexed by attributeName
     */
    public List<AttributeDefinition> getAttributes() {
        LOG.debug("calling getAttributeDefinitions");

        return this.attributes;
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
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling addCollectionDefinition '" + collectionDefinition.getName() + "'");
        }
        String collectionName = collectionDefinition.getName();
        if (StringUtils.isBlank(collectionName)) {
            throw new ValidationException("invalid (blank) collectionName");
        }

        if (collectionMap.containsKey(collectionName)) {
            throw new DuplicateEntryException("collection '" + collectionName + "' already defined for class '" + getEntryClass().getName() + "'");
        }
        else if (attributeMap.containsKey(collectionName)) {
            throw new DuplicateEntryException("collection '" + collectionName + "' already defined as an Attribute for class '" + getEntryClass().getName() + "'");
        }

        this.collections.add(collectionDefinition);
        this.collectionMap.put(collectionName, collectionDefinition);
    }


    /**
     * @param collectionName
     * @return CollectionDefinition with the given name, or null if none with that name exists
     */
    public CollectionDefinition getCollectionDefinition(String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getCollectionDefinition '" + collectionName + "'");
        }
        return (CollectionDefinition) collectionMap.get(collectionName);
    }

    /**
     * @return a Map containing all CollectionDefinitions associated with this BusinessObjectEntry, indexed by collectionName
     */
    public List<CollectionDefinition> getCollections() {
        return this.collections;
    }

    /**
     * Locates the delegates putatively associated with all component AttributeReferences, if any, or dies trying.
     * 
     * @param dataDictionary
     * @throws CompletionException if an AttributeReference can't be expanded
     */
    public void expandAttributeReferences(DataDictionary dataDictionary) {
        for ( AttributeDefinition attributeDefinition : attributes ) {
            if (attributeDefinition instanceof AttributeReferenceDefinition) {
                AttributeReferenceDefinition attributeReferenceDefinition = (AttributeReferenceDefinition) attributeDefinition;

                attributeReferenceDefinition.assignDelegate(this, dataDictionary);
                //attributeReferenceDefinition.completeDeferredValidation(getEntryClass(), validationCompletionUtils);
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
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling addRelationshipDefinition '" + relationshipDefinition.getObjectAttributeName() + "'");
        }
        String relationshipName = relationshipDefinition.getObjectAttributeName();
        if (StringUtils.isBlank(relationshipName)) {
            throw new ValidationException("invalid (blank) relationshipName");
        }

        this.relationships.add(relationshipDefinition);
        this.relationshipMap.put(relationshipName, relationshipDefinition);
    }


    /**
     * @param relationshipName
     * @return RelationshipDefinition with the given name, or null if none with that name exists
     */
    public RelationshipDefinition getRelationshipDefinition(String relationshipName) {
        if (StringUtils.isBlank(relationshipName)) {
            throw new IllegalArgumentException("invalid (blank) relationshipName");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling getRelationshipDefinition '" + relationshipName + "'");
        }
        return (RelationshipDefinition) relationshipMap.get(relationshipName);
    }

    /**
     * @return a Map containing all RelationshipDefinitions associated with this BusinessObjectEntry, indexed by relationshipName
     */
    public List<RelationshipDefinition> getRelationships() {
        return this.relationships;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    public void completeValidation(ValidationCompletionUtils validationCompletionUtils) {
        
        for ( AttributeDefinition attributeDefinition : attributes ) {
            attributeDefinition.completeValidation(getEntryClass(), null, validationCompletionUtils);
        }

        for ( CollectionDefinition collectionDefinition : collections ) {
            collectionDefinition.completeValidation(getEntryClass(), null, validationCompletionUtils);
        }

        for ( RelationshipDefinition relationshipDefinition : relationships ) {
            relationshipDefinition.completeValidation(getEntryClass(), null, validationCompletionUtils);
        }
    }

    public void setAttributes(List<AttributeDefinition> attributes) {
        this.attributes = attributes;
    }

    public void setCollections(List<CollectionDefinition> collections) {
        this.collections = collections;
    }

    public void setRelationships(List<RelationshipDefinition> relationships) {
        this.relationships = relationships;
    }

    public Set<String> getCollectionNames() {
        return collectionMap.keySet();
    }

}