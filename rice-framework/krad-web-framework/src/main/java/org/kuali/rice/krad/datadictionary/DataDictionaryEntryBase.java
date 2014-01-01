/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.provider.MetadataProvider;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHintType;
import org.kuali.rice.krad.datadictionary.exception.DuplicateEntryException;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.state.StateMapping;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.exception.ValidationException;
import org.springframework.beans.BeanUtils;

/**
 * Contains common properties and methods for data dictionary entries
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
abstract public class DataDictionaryEntryBase extends DictionaryBeanBase implements DataDictionaryEntry, Serializable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataDictionaryEntryBase.class);
    private static final long serialVersionUID = 5133059101016080533L;

    protected DataObjectMetadata dataObjectMetadata;

    protected List<AttributeDefinition> attributes;
    protected List<ComplexAttributeDefinition> complexAttributes;
    protected List<CollectionDefinition> collections;
    protected List<RelationshipDefinition> relationships;
    protected Map<String, AttributeDefinition> attributeMap;
    protected Map<String, ComplexAttributeDefinition> complexAttributeMap;
    protected Map<String, CollectionDefinition> collectionMap;

    protected Map<String, RelationshipDefinition> relationshipMap;

    protected StateMapping stateMapping;

    public DataDictionaryEntryBase() {
        this.attributes = new ArrayList<AttributeDefinition>();
        this.complexAttributes = new ArrayList<ComplexAttributeDefinition>();
        this.collections = new ArrayList<CollectionDefinition>();
        this.relationships = new ArrayList<RelationshipDefinition>();
        this.attributeMap = new LinkedHashMap<String, AttributeDefinition>();
        this.complexAttributeMap = new LinkedHashMap<String, ComplexAttributeDefinition>();
        this.collectionMap = new LinkedHashMap<String, CollectionDefinition>();
        this.relationshipMap = new LinkedHashMap<String, RelationshipDefinition>();
    }

    /* Returns the given entry class (bo class or document class) */
    public abstract Class<?> getEntryClass();

    /**
     * @param attributeName
     * @return AttributeDefinition with the given name, or null if none with that name exists
     */
    @Override
    public AttributeDefinition getAttributeDefinition(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        return attributeMap.get(attributeName);
    }

    /**
     * @return a Map containing all AttributeDefinitions associated with this BusinessObjectEntry, indexed by
     *         attributeName
     */
    @BeanTagAttribute(name = "attributes", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<AttributeDefinition> getAttributes() {
        return this.attributes;
    }

    /**
     * @return the complexAttributes
     */
    public List<ComplexAttributeDefinition> getComplexAttributes() {
        return this.complexAttributes;
    }

    /**
     * @param complexAttributes the complexAttributes to set
     */
    public void setComplexAttributes(List<ComplexAttributeDefinition> complexAttributes) {
        complexAttributeMap.clear();
        for (ComplexAttributeDefinition complexAttribute : complexAttributes) {
            if (complexAttribute == null) {
                throw new DataDictionaryException("invalid (null) complexAttributeDefinition on " + this);
            }
            String complexAttributeName = complexAttribute.getName();
            if (StringUtils.isBlank(complexAttributeName)) {
                throw new DataDictionaryException("invalid (blank) complexAttributeName on " + this);
            }

            if (complexAttributeMap.containsKey(complexAttribute)) {
                throw new DuplicateEntryException("complex attribute '"
                        + complexAttribute
                        + "' already defined as an complex attribute for class '"
                        + getEntryClass().getName()
                        + "'");
            } else if (collectionMap.containsKey(complexAttributeName)) {
                throw new DuplicateEntryException("complex attribute '"
                        + complexAttributeName
                        + "' already defined as a Collection for class '"
                        + getEntryClass().getName()
                        + "'");
            } else if (attributeMap.containsKey(complexAttributeName)) {
                throw new DuplicateEntryException("complex attribute '"
                        + complexAttributeName
                        + "' already defined as an Attribute for class '"
                        + getEntryClass().getName()
                        + "'");
            }

            complexAttributeMap.put(complexAttributeName, complexAttribute);

        }

        this.complexAttributes = complexAttributes;
    }

    /**
     * @param collectionName
     * @return CollectionDefinition with the given name, or null if none with that name exists
     */
    public CollectionDefinition getCollectionDefinition(String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }
        return collectionMap.get(collectionName);
    }

    /**
     * @return a Map containing all CollectionDefinitions associated with this BusinessObjectEntry, indexed by
     *         collectionName
     */
    @BeanTagAttribute(name = "collections", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<CollectionDefinition> getCollections() {
        return this.collections;
    }

    /**
     * @param relationshipName
     * @return RelationshipDefinition with the given name, or null if none with that name exists
     */
    public RelationshipDefinition getRelationshipDefinition(String relationshipName) {
        if (StringUtils.isBlank(relationshipName)) {
            throw new IllegalArgumentException("invalid (blank) relationshipName");
        }
        return getRelationshipMap().get(relationshipName);
    }

    /**
     * @return a Map containing all RelationshipDefinitions associated with this BusinessObjectEntry, indexed by
     *         relationshipName
     */
    @Override
    @BeanTagAttribute(name = "relationships", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<RelationshipDefinition> getRelationships() {
        return this.relationships;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    @Override
    public void completeValidation() {
        completeValidation( new ValidationTrace() );
    }

    protected void embedMetadata() {
        // Once we get to this point, the providers must also be loaded
        // See if this DataObjectEntry's class has associated metadata
        MetadataProvider metadataProvider = KradDataServiceLocator.getProviderRegistry().getMetadataProvider(getEntryClass());
        if ( metadataProvider != null ) {
            dataObjectMetadata = metadataProvider.getMetadataForType(getEntryClass());
            if ( dataObjectMetadata == null ) {
                LOG.warn( "No metadata defined for " + getEntryClass() + " even though provider returned." );
            } else {
                // Since we have metadata, attempt to match it up on property name with the attributes defined
                // We want to do this before calling the super.completeValidation() as it will validate that the
                // AttributeDefinition objects have certain values and we want to take advantage of defaulting from
                // the metadata model
                injectMetadataIntoAttributes(dataObjectMetadata);
                injectMetadataIntoCollections(dataObjectMetadata);
                injectMetadataIntoRelationships(dataObjectMetadata);
            }
        } else {
            LOG.info( "No metadata provider exists which handles: " + getEntryClass());
        }
    }

    /**
     * Inject the metadata into the relationship definitions.  Unlike attributes, in this case
     * we only add missing relationships.  If a relationship was defined for a given attribute
     * we leave it alone.
     *
     * @param dataObjectMetadata
     */
    protected void injectMetadataIntoRelationships(DataObjectMetadata dataObjectMetadata) {
        List<RelationshipDefinition> relationships = getRelationships();
        boolean relationshipsChanged = false;
        if ( relationships == null ) {
            relationships = new ArrayList<RelationshipDefinition>();
        }
        for ( DataObjectRelationship rel : dataObjectMetadata.getRelationships() ) {
            if ( rel.getAttributeRelationships().isEmpty() ) {
                // If we have no attributes to link with, we don't have anything to contribute
                continue;
            }
            if ( StringUtils.isNotBlank(rel.getName()) ) {
                RelationshipDefinition relationshipDefinition = getRelationshipDefinition(rel.getName());
                // no relationship defined for attribute - proceed and the given relationship parent is not
                //included in a previous relationship so as not to add duplicates
                if ( relationshipDefinition == null ){//&& !relationshipParentExists(rel.getName())) {
                    relationshipDefinition = new RelationshipDefinition();
                    relationshipDefinition.setObjectAttributeName(rel.getName());
                    relationshipDefinition.setSourceClass(getEntryClass());
                    relationshipDefinition.setTargetClass(rel.getRelatedType());
                    for ( DataObjectAttributeRelationship attrRel : rel.getAttributeRelationships() ) {
                        PrimitiveAttributeDefinition attrDef = new PrimitiveAttributeDefinition();
                        attrDef.setSourceName(attrRel.getParentAttributeName());
                        attrDef.setTargetName(attrRel.getChildAttributeName());
                        relationshipDefinition.getPrimitiveAttributes().add(attrDef);
                    }
                    relationshipDefinition.setGeneratedFromMetadata(true);
                    relationshipDefinition.setEmbeddedDataObjectMetadata(true);
                    relationships.add(relationshipDefinition);
                    relationshipsChanged = true;
                }
            } else {
                LOG.warn( "Relationship in metadata model contained blank name attribute: " + rel );
            }
        }
    }

    protected void injectMetadataIntoCollections(DataObjectMetadata dataObjectMetadata) {
        List<CollectionDefinition> collections = getCollections();
        boolean collectionsChanged = false;
        if ( collections == null ) {
            collections = new ArrayList<CollectionDefinition>();
        }
        for ( DataObjectCollection coll : dataObjectMetadata.getCollections() ) {
            if ( StringUtils.isNotBlank(coll.getName()) ) {
                // Odd special case where a list attribute has been mapped as a singular attribute in the DD.
                // Due to validation logic, a given name can not be both a collection and an attribute.
                if ( getAttributeDefinition(coll.getName()) != null ) {
                    continue;
                }
                CollectionDefinition collectionDefinition = getCollectionDefinition(coll.getName());
                // no relationship defined for attribute - proceed
                if ( collectionDefinition == null ) {
                    collectionDefinition = new CollectionDefinition();
                    collectionDefinition.setName(coll.getName());
                    collectionDefinition.setDataObjectClass(coll.getRelatedType().getName());
                    collectionDefinition.setGeneratedFromMetadata(true);
                    collections.add(collectionDefinition);
                    // only need to trigger re-indexing if we add a new collection
                    collectionsChanged = true;
                }
                collectionDefinition.setDataObjectCollection(coll);
                collectionDefinition.setEmbeddedDataObjectMetadata(true);
            } else {
                LOG.warn( "Relationship in metadata model contained blank name attribute: " + coll );
            }
        }
        // now that we are done, we need to set the resulting list back to the entry
        // This triggers the needed indexing
        if ( collectionsChanged ) {
            setCollections(collections);
        }
    }

    protected static final Set<String> EXCLUDED_PROPERTY_NAMES = new HashSet<String>();
    static {
        EXCLUDED_PROPERTY_NAMES.add("objectId");
        EXCLUDED_PROPERTY_NAMES.add("versionNumber");
    }

    protected void injectMetadataIntoAttributes( DataObjectMetadata dataObjectMetadata ) {
        List<AttributeDefinition> originalDataObjectEntryAttributes = getAttributes();
        // this should never happen, but just in case someone was pathological enough to set it to null manually, let's be prepared
        // We will use this to restore any UIF-Only attributes.
        if ( originalDataObjectEntryAttributes == null ) {
            originalDataObjectEntryAttributes = new ArrayList<AttributeDefinition>();
        }
        // This is the list we will set
        List<AttributeDefinition> dataObjectEntryAttributes = new ArrayList<AttributeDefinition>();
        // We are going to loop over the data in the metadata instead of the DD
        // because we want to add attribute definitions if they do not exist
        // and we don't care about attributes which only exist in the DD
        for ( DataObjectAttribute attr : dataObjectMetadata.getAttributes() ) {
            if ( StringUtils.isBlank(attr.getName())) {
                LOG.warn( "Attribute in metadata model contained blank name attribute: " + attr );
                continue;
            }
            // certain old properties we never want to see
            if ( EXCLUDED_PROPERTY_NAMES.contains( attr.getName() ) ) {
                continue;
            }
            // if we've been told to exclude it, just ignore
            if ( hasExcludedHint(attr) ) {
                continue;
            }

            AttributeDefinition attributeDefinition = getAttributeDefinition(attr.getName());
            originalDataObjectEntryAttributes.remove(attributeDefinition);

            if ( attributeDefinition == null ) {
                attributeDefinition = new AttributeDefinition();
                attributeDefinition.setName(attr.getName());
                attributeDefinition.setGeneratedFromMetadata(true);
            }

            attributeDefinition.setDataObjectAttribute(attr);
            attributeDefinition.setEmbeddedDataObjectMetadata(true);
            dataObjectEntryAttributes.add(attributeDefinition);
        }
        // Add any which remain in this list to the end
        dataObjectEntryAttributes.addAll(originalDataObjectEntryAttributes);
        // now that we are done, we need to set the resulting list back to the entry
        // This triggers the needed indexing
        setAttributes(dataObjectEntryAttributes);
    }

    /**
     * Check the {@link UifDisplayHint}s on an attribute, return true if any of them have the
     * EXCLUDE type.
     */
    protected boolean hasExcludedHint( DataObjectAttribute attr ) {
        if ( attr.getDisplayHints() != null ) {
            for ( UifDisplayHint hint : attr.getDisplayHints() ) {
                if ( hint.value().equals(UifDisplayHintType.EXCLUDE) ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void dataDictionaryPostProcessing() {
        super.dataDictionaryPostProcessing();
        embedMetadata();
        if (relationships != null) {
            relationshipMap.clear();
            for (RelationshipDefinition relationship : relationships) {
                if (relationship == null) {
                    LOG.warn("Skipping invalid (null) relationshipDefinition on " + this);
                    continue;
                }
                String relationshipName = relationship.getObjectAttributeName();
                if (StringUtils.isBlank(relationshipName)) {
                    LOG.warn("Skipping invalid relationshipDefinition with blank relationshipName on " + this);
                    continue;
                }
                relationship.setSourceClass(getEntryClass());
                relationshipMap.put(relationshipName, relationship);
            }
        }

        //Populate attributes with nested attribute definitions
        if (complexAttributes != null) {
            for (ComplexAttributeDefinition complexAttribute : complexAttributes) {
                if ( complexAttribute != null ) {
                    addNestedAttributes(complexAttribute, complexAttribute.getName());
                }
            }
        }
        for (AttributeDefinition definition : getAttributes()) {
            definition.dataDictionaryPostProcessing();
        }
        for (CollectionDefinition definition : getCollections()) {
            definition.dataDictionaryPostProcessing();
        }
        for (RelationshipDefinition definition : getRelationships()) {
            definition.dataDictionaryPostProcessing();
        }
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition
     * fields.
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#completeValidation(org.kuali.rice.krad.datadictionary.validator.ValidationTrace)
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        if ( getEntryClass() != null ) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Processing Validation for " + this.getClass().getSimpleName() + " for class: " + getEntryClass().getName() );
            }
            tracer.addBean(this.getClass().getSimpleName(), getEntryClass().getSimpleName());
            for (AttributeDefinition definition : getAttributes()) {
                definition.completeValidation(getEntryClass(), null, tracer.getCopy());
            }
            for (CollectionDefinition definition : getCollections()) {
                definition.completeValidation(getEntryClass(), null, tracer.getCopy());
            }
            for (RelationshipDefinition definition : getRelationships()) {
                definition.completeValidation(getEntryClass(), null, tracer.getCopy());
            }
        } else {
            tracer.addBean(this.getClass().getSimpleName(), toString() );
            tracer.createError("DataObjectClass is not set,  remaining validations aborted", null );
        }
    }

    /**
     * The attributes element contains attribute
     * elements.  These define the specifications for business object fields.
     *
     * JSTL: attributes is a Map which is accessed by a key of "attributes".
     * This map contains entries with the following keys:
     * attributeName of first attribute
     * attributeName of second attribute
     * etc.
     *
     * The corresponding value for each entry is an attribute ExportMap.
     * By the time the JSTL export happens, all attributeReferences will be
     * indistinguishable from attributes.
     *
     * See AttributesMapBuilder.java
     *
     * The attribute element specifies the way in which a business object
     * field appears on a screen for data entry or display purposes.  These
     * specifications include the following:
     * The title and formatting of the field
     * Descriptive information about the field
     * The edits used at time of data-entry
     *
     * DD: See AttributeDefinition.java
     *
     * JSTL: attribute is a Map which is accessed using a key which is the attributeName
     * of an attribute.  Each entry contains the following keys:
     * name (String)
     * forceUppercase (boolean String)
     * label (String)
     * shortLabel (String, copied from label if not present)
     * maxLength (String)
     * exclusiveMin (bigdecimal String)
     * exclusiveMax (bigdecimal String)
     * validationPattern (Map, optional)
     * required (boolean String)
     * control (Map)
     * summary (String)
     * description (String)
     * formatterClass (String, optional)
     * fullClassName (String)
     * displayWorkgroup(String, optional)
     * displayMaskClass(String, optional)
     *
     * See AttributesMapBuilder.java
     * Note: exclusiveMax is mapped from the inclusiveMax element!
     * The validation logic seems to be assuming inclusiveMax.
     */
    public void setAttributes(List<AttributeDefinition> attributes) {
        attributeMap.clear();
        for (AttributeDefinition attribute : attributes) {
            if (attribute == null) {
                throw new IllegalArgumentException("invalid (null) attributeDefinition");
            }
            String attributeName = attribute.getName();
            if (StringUtils.isBlank(attributeName)) {
                throw new ValidationException("invalid (blank) attributeName");
            }

            if (attributeMap.containsKey(attributeName)) {
                throw new DuplicateEntryException("attribute '"
                        + attributeName
                        + "' already defined as an Attribute for class '"
                        + getEntryClass().getName()
                        + "'");
            } else if (collectionMap.containsKey(attributeName)) {
                throw new DuplicateEntryException("attribute '"
                        + attributeName
                        + "' already defined as a Collection for class '"
                        + getEntryClass().getName()
                        + "'");
            } else if (complexAttributeMap.containsKey(attributeName)) {
                throw new DuplicateEntryException("attribute '"
                        + attributeName
                        + "' already defined as an Complex Attribute for class '"
                        + getEntryClass().getName()
                        + "'");
            }
            attributeMap.put(attributeName, attribute);
        }
        this.attributes = attributes;
    }

    /**
     * The collections element contains collection elements.  These define
     * the lists of other business objects which are related to and
     * defined in the business objects.
     *
     * JSTL: collections is a Map which is accessed by a key of "collections".
     * This map contains entries with the following keys:
     * name of first collection
     * name of second collection
     * etc.
     * The corresponding value for each entry is a collection ExportMap.
     *
     * The collection element defines the name and description a
     * list of objects related to the business object.
     *
     * DD: See CollectionDefinition.java.
     *
     * JSTL: collection is a Map which is accessed using a key which is the
     * name of the collection.  Each entry contains the following keys:
     * name (String)
     * label (String)
     * shortLabel (String, copied from label if missing)
     * elementLabel (String, copied from contained class if missing)
     * summary (String)
     * description (String)
     *
     * See CollectionsMapBuilder.java.
     */
    public void setCollections(List<CollectionDefinition> collections) {
        collectionMap.clear();
        for (CollectionDefinition collection : collections) {
            if (collection == null) {
                throw new IllegalArgumentException("invalid (null) collectionDefinition");
            }
            String collectionName = collection.getName();
            if (StringUtils.isBlank(collectionName)) {
                throw new ValidationException("invalid (blank) collectionName");
            }

            if (collectionMap.containsKey(collectionName)) {
                throw new DuplicateEntryException("collection '"
                        + collectionName
                        + "' already defined for class '"
                        + getEntryClass().getName()
                        + "'");
            } else if (attributeMap.containsKey(collectionName)) {
                throw new DuplicateEntryException("collection '"
                        + collectionName
                        + "' already defined as an Attribute for class '"
                        + getEntryClass().getName()
                        + "'");
            } else if (complexAttributeMap.containsKey(collectionName)) {
                throw new DuplicateEntryException("collection '"
                        + collectionName
                        + "' already defined as Complex Attribute for class '"
                        + getEntryClass().getName()
                        + "'");
            }

            collectionMap.put(collectionName, collection);

        }
        this.collections = collections;
    }

    /**
     * The relationships element contains relationship elements.
     * These are used to map attribute names to fields in a reference object.
     *
     * JSTL: relationships is a Map which is accessed by a key of "relationships".
     * This map contains entries with the following keys:
     * objectAttributeName of first relationship
     * objectAttributeName of second relationship
     * etc.
     * The corresponding value for each entry is a relationship ExportMap.
     *
     * The relationship element defines how primitive attributes of this
     * class can be used to retrieve an instance of some related Object instance
     * DD: See RelationshipDefinition.java.
     *
     * JSTL: relationship is a Map which is accessed using a key which is the
     * objectAttributeName of a relationship.  The map contains a single entry
     * with a key of "primitiveAttributes" and value which is an attributesMap ExportMap.
     *
     * The attributesMap ExportMap contains the following keys:
     * 0   (for first primitiveAttribute)
     * 1   (for second primitiveAttribute)
     * etc.
     * The corresponding value for each entry is an primitiveAttribute ExportMap
     * which contains the following keys:
     * "sourceName"
     * "targetName"
     *
     * See RelationshipsMapBuilder.java.
     */
    public void setRelationships(List<RelationshipDefinition> relationships) {
        this.relationships = relationships;
    }

    public Set<String> getCollectionNames() {
        return collectionMap.keySet();
    }

    public Set<String> getAttributeNames() {
        return attributeMap.keySet();
    }

    public Set<String> getRelationshipNames() {
        return relationshipMap.keySet();
    }

    /**
     * recursively add complex attributes
     *
     * @param complexAttribute - the complex attribute to add recursively
     * @param attrPath - a string representation of specifically which attribute (at some depth) is being accessed
     */
    private void addNestedAttributes(ComplexAttributeDefinition complexAttribute, String attrPath) {
        DataDictionaryEntryBase dataDictionaryEntry = (DataDictionaryEntryBase) complexAttribute.getDataObjectEntry();

        //Add attributes for the complex attibutes
        for (AttributeDefinition attribute : dataDictionaryEntry.getAttributes()) {
            String nestedAttributeName = attrPath + "." + attribute.getName();
            AttributeDefinition nestedAttribute = copyAttributeDefinition(attribute);
            nestedAttribute.setName(nestedAttributeName);

            if (!attributeMap.containsKey(nestedAttributeName)) {
                this.attributes.add(nestedAttribute);
                this.attributeMap.put(nestedAttributeName, nestedAttribute);
            }
        }

        //Recursively add complex attributes
        List<ComplexAttributeDefinition> nestedComplexAttributes = dataDictionaryEntry.getComplexAttributes();
        if (nestedComplexAttributes != null) {
            for (ComplexAttributeDefinition nestedComplexAttribute : nestedComplexAttributes) {
                addNestedAttributes(nestedComplexAttribute, attrPath + "." + nestedComplexAttribute.getName());
            }
        }
    }

    /**
     * copy an attribute definition
     *
     * @param attrDefToCopy - the attribute to create a copy of
     * @return a copy of the attribute
     */
    private AttributeDefinition copyAttributeDefinition(AttributeDefinition attrDefToCopy) {
        AttributeDefinition attrDefCopy = new AttributeDefinition();

        try {
            BeanUtils.copyProperties(attrDefToCopy, attrDefCopy, new String[]{"formatterClass"});

            //BeanUtils doesn't copy properties w/o "get" read methods, manually copy those here
            attrDefCopy.setRequired(attrDefToCopy.isRequired());

        } catch (Exception e) {
            LOG.warn( "Problem copying properties from attribute definition: " + attrDefToCopy, e);
        }

        return attrDefCopy;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#getStateMapping()
     */
    @Override
    @BeanTagAttribute(name = "stateMapping", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public StateMapping getStateMapping() {
        return stateMapping;
    }

    /**
     * @see DataDictionaryEntry#setStateMapping(org.kuali.rice.krad.datadictionary.state.StateMapping)
     */
    @Override
    public void setStateMapping(StateMapping stateMapping) {
        this.stateMapping = stateMapping;
    }

    public boolean hasEmbeddedDataObjectMetadata() {
        return getDataObjectMetadata() != null;
    }

    public DataObjectMetadata getDataObjectMetadata() {
        return dataObjectMetadata;
    }

    public void setDataObjectMetadata(DataObjectMetadata dataObjectMetadata) {
        this.dataObjectMetadata = dataObjectMetadata;
    }

    public Map<String, RelationshipDefinition> getRelationshipMap() {
        if(relationshipMap.isEmpty() && !getRelationships().isEmpty()){
            for(RelationshipDefinition rel : getRelationships()){
                relationshipMap.put(rel.getObjectAttributeName(),rel);
            }
        }
        return relationshipMap;
    }

    public void setRelationshipMap(Map<String, RelationshipDefinition> relationshipMap) {
        this.relationshipMap = relationshipMap;
    }
}
