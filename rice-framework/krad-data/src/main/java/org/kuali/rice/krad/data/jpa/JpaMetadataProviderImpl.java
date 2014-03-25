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
package org.kuali.rice.krad.data.jpa;

import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeRelationshipImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectMetadataImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectRelationshipImpl;
import org.kuali.rice.krad.data.provider.annotation.ExtensionFor;
import org.kuali.rice.krad.data.provider.impl.MetadataProviderBase;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A superclass which handles most of the JPA metadata extraction.
 *
 * <p>
 * It handles everything which can be done via the standard javax.persistence annotations. Any implementation-specific
 * annotations must be processed in the provided abstract hook methods.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class JpaMetadataProviderImpl extends MetadataProviderBase implements JpaMetadataProvider {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JpaMetadataProviderImpl.class);

    /**
     * The entity manager used in interacting with the database.
     */
	protected EntityManager entityManager;

	/**
	 * Hook called after all "standard" annotations are processed to perform any further extraction based on the
	 * internals of the JPA implementation.
     *
     * @param metadata The metadata for the data object.
     * @param entityType The entity type of the data object.
	 */
	protected abstract void populateImplementationSpecificEntityLevelMetadata(DataObjectMetadataImpl metadata,
			EntityType<?> entityType);

	/**
	 * Hook called after all "standard" attribute-level annotations are processed to perform any further extraction
	 * based on the internals of the JPA implementation.
     *
     * @param attribute The attribute metadata for the data object.
     * @param attr The persistent single-valued property or field.
	 */
    protected abstract void populateImplementationSpecificAttributeLevelMetadata(DataObjectAttributeImpl attribute,
			SingularAttribute<?, ?> attr);

	/**
	 * Hook called after all "standard" field-level annotations are processed on attributes identified as "plural" to
	 * perform any further extraction based on the internals of the JPA implementation.
     *
     * @param collection The collection metadata for the data object.
     * @param cd The persistent collection-valued attribute.
	 */
    protected abstract void populateImplementationSpecificCollectionLevelMetadata(DataObjectCollectionImpl collection,
			PluralAttribute<?, ?, ?> cd);

	/**
	 * Hook called after all "standard" field-level annotations are processed on attributes identified as "associations"
	 * to perform any further extraction based on the internals of the JPA implementation.
     *
     * @param relationship The relationship metadata for the data object.
     * @param rd The persistent single-valued property or field.
	 */
    protected abstract void populateImplementationSpecificRelationshipLevelMetadata(
			DataObjectRelationshipImpl relationship, SingularAttribute<?, ?> rd);

    /**
     * {@inheritDoc}
     */
	@Override
	public abstract DataObjectRelationship addExtensionRelationship(Class<?> entityClass, String extensionPropertyName,
			Class<?> extensionEntity);

    /**
     * {@inheritDoc}
     */
	@Override
	protected synchronized void initializeMetadata(Collection<Class<?>> types) {
		LOG.info("Initializing JPA Metadata from " + entityManager);
		
		masterMetadataMap.clear();
		// QUESTION: When is JPA loaded so this service can initialize itself?
		// Build and store the map

		for ( IdentifiableType<?> identifiableType : entityManager.getMetamodel().getEntities() ) {
            //Only extract the metadata if EntityType and not a MappedSuperClass
            if(identifiableType instanceof EntityType<?>){
                EntityType<?> type = (EntityType<?>)identifiableType;
                try {
                    masterMetadataMap.put(type.getBindableJavaType(), getMetadataForClass(type.getBindableJavaType()));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Added Metadata For: " + type.getBindableJavaType());
                    }
                } catch (Exception ex) {
                    LOG.error("Error obtaining JPA metadata for type: " + type.getJavaType(), ex);
                }
			}
		}
	}

	/**
	 * Extracts the data from the JPA Persistence Unit. This code assumes that the given class is persistable.
	 * 
	 * @param persistableClass Class which will be looked up in OJB's static descriptor repository.
	 * @return the metadata for the class
	 */
	@SuppressWarnings("unchecked")
	public DataObjectMetadata getMetadataForClass(Class<?> persistableClass) {
        // first, let's scan for extensions
        List<DataObjectRelationship> relationships = new ArrayList<DataObjectRelationship>();
        Map<Class<?>, Class<?>> extensionMap = new HashMap<Class<?>, Class<?>>();
        for (EntityType<?> entityType : getEntityManager().getMetamodel().getEntities()) {
            if (entityType.getJavaType().isAnnotationPresent(ExtensionFor.class)) {
                ExtensionFor extensionFor = entityType.getJavaType().getAnnotation(ExtensionFor.class);
                if (extensionFor.value().equals(persistableClass)) {
                    DataObjectRelationship relationship =
                            addExtensionRelationship(persistableClass, extensionFor.extensionPropertyName(), entityType.getJavaType());
                    // have to do this because even though we've added the DatabaseMapping in EclipseLink, it will not
                    // rebuild the JPA metamodel for us
                    relationships.add(relationship);
                }
            }
        }
        // now let's build us some metadata!
		DataObjectMetadataImpl metadata = new DataObjectMetadataImpl();
		EntityType<?> entityType = entityManager.getMetamodel().entity(persistableClass);
		metadata.setProviderName(this.getClass().getSimpleName());
		metadata.setType(persistableClass);
		metadata.setName(persistableClass.getSimpleName());
		metadata.setReadOnly(false);
		
		metadata.setSupportsOptimisticLocking(entityType.hasVersionAttribute());
		populateImplementationSpecificEntityLevelMetadata(metadata, entityType);

		// PK Extraction
		try {
			metadata.setPrimaryKeyAttributeNames(getPrimaryKeyAttributeNames(entityType));
		} catch (RuntimeException ex) {
			LOG.error("Error processing PK metadata for " + entityType.getBindableJavaType().getName());
			throw new RuntimeException(
					"Error processing PK metadata for " + entityType.getBindableJavaType().getName(), ex);
		}

		// Main Attribute Extraction
		try {
			List<DataObjectAttribute> attributes = getSingularAttributes(persistableClass,
					entityType.getSingularAttributes(), metadata.getPrimaryKeyAttributeNames());
			for (DataObjectAttribute attr : attributes) {
				metadata.getOrderedAttributeList().add(attr.getName());
			}
			metadata.setAttributes(attributes);
		} catch (RuntimeException ex) {
			LOG.error("Error processing attribute metadata for " + entityType.getBindableJavaType().getName());
			throw ex;
		}

		// Collection Extraction
		try {
			metadata.setCollections(getCollectionsFromMetadata((Set) entityType.getPluralAttributes()));
		} catch (RuntimeException ex) {
			LOG.error("Error processing collection metadata for " + entityType.getBindableJavaType().getName());
			throw ex;
		}

		// Reference/Relationship Extraction
		try {
            relationships.addAll(getRelationships(entityType.getSingularAttributes()));
			metadata.setRelationships(relationships);
		} catch (RuntimeException ex) {
			LOG.error("Error processing relationship metadata for " + entityType.getBindableJavaType().getName());
			throw ex;
		}

		return metadata;
	}

    /**
     * Gets the attribute names for the primary keys from the given entity type.
     *
     * @param entityType The entity type of the data object.
     * @return A list of primary key attribute names.
     */
	protected List<String> getPrimaryKeyAttributeNames(EntityType<?> entityType) {
		List<String> primaryKeyAttributeNames = new ArrayList<String>();
		// JHK: After examining of the metadata structures of EclipseLink, I determined that there
		// was nothing in those which preserved the order of the original annotations.
		// We *need* to know the order of PK fields for KNS/KRAD functionality.
		// So, I'm falling back to checking the annotations and fields on the referenced objects.
		// Yes, the Javadoc states that the getDeclaredFields() method does not guarantee order,
		// But, it's the best we have. And, as of Java 6, it is returning them in declaration order.

		if (entityType.getIdType() instanceof EmbeddableType) {
			for (Field pkField : entityType.getIdType().getJavaType().getDeclaredFields()) {
				primaryKeyAttributeNames.add(pkField.getName());
			}
		} else {
			// First, get the ID attributes from the metadata
			List<String> unsortedPkFields = new ArrayList<String>();
			for (SingularAttribute attr : entityType.getSingularAttributes()) {
				if (attr.isId()) {
					unsortedPkFields.add(attr.getName());
				}
			}

            getPrimaryKeyNamesInOrder(primaryKeyAttributeNames, unsortedPkFields, entityType.getJavaType().getDeclaredFields(), entityType.getJavaType());
		}
		return primaryKeyAttributeNames;
	}

    /**
     * Sorts the list of primary key names.
     *
     * @param pkFieldNames The final list to which the primary key field names will be added in order.
     * @param unsortedPks The current list of unsorted primary keys.
     * @param fields The fields on the current object.
     * @param type The class of the current object.
     */
    private void getPrimaryKeyNamesInOrder(List<String> pkFieldNames, List<String> unsortedPks, Field[] fields, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (unsortedPks.contains(field.getName())) {
                pkFieldNames.add(field.getName());
            }
        }

        if (pkFieldNames.isEmpty() && type.getSuperclass() != null) {
            getPrimaryKeyNamesInOrder(pkFieldNames, unsortedPks, type.getSuperclass().getDeclaredFields(), type.getSuperclass());
        }
    }

    /**
     * Gets a list of attributes for this data object.
     *
     * @param persistableClass The class of the data object.
     * @param fields The collection of singular attributes to process.
     * @param primaryKeyAttributes The list of primary key attribute names.
     * @return The list of attributes for this data object.
     */
	protected List<DataObjectAttribute> getSingularAttributes(Class<?> persistableClass, Collection<?> fields,
			List<String> primaryKeyAttributes) {
		if (fields == null) {
			fields = Collections.emptySet();
		}
		// Put them all into a map by their property name so we can find them
		// We want to add them to the list in appearance order in the class
		Map<String, SingularAttribute> attrs = new HashMap<String, SingularAttribute>(fields.size());
		for (SingularAttribute attr : (Collection<SingularAttribute>) fields) {
			if (!attr.isAssociation()) {
				attrs.put(attr.getName(), attr);
			}
		}
		List<DataObjectAttribute> attributes = new ArrayList<DataObjectAttribute>(fields.size());
		// This will process them in appearance order
		for (Field f : persistableClass.getDeclaredFields()) {
			SingularAttribute attr = attrs.get(f.getName());
			if (attr != null) {
				attributes.add(getAttributeMetadata(persistableClass, attr, primaryKeyAttributes));
				attrs.remove(f.getName()); // to note that it's been used - see below
			}
		}
		// Just in case there are others which don't match, we don't want to miss them and will add them at the end
		for (SingularAttribute attr : attrs.values()) {
			attributes.add(getAttributeMetadata(persistableClass, attr, primaryKeyAttributes));
		}
		return attributes;
	}

	/**
	 * Gets a single field's metadata from the property descriptor.
	 * 
	 * @param persistableClass The class of the data object.
	 * @param attr The singular attribute to process.
     * @param primaryKeyAttributes The list of primary key attribute names.
	 * @return The DataObjectAttribute containing the metadata for the given attribute on the provided Class
	 */
	protected DataObjectAttribute getAttributeMetadata(Class<?> persistableClass, SingularAttribute<?, ?> attr,
			List<String> primaryKeyAttributes) {
		DataObjectAttributeImpl attribute = new DataObjectAttributeImpl();

		attribute.setOwningType(persistableClass);
		attribute.setName(attr.getName());
		Class<?> propertyType = attr.getJavaType();
		attribute.setType(propertyType);
		DataType dataType = DataType.getDataTypeFromClass(propertyType);
		if (dataType == null) {
			dataType = DataType.STRING;
		}
		attribute.setDataType(dataType);
		attribute.setRequired(!attr.isOptional() && !attr.isId() && !primaryKeyAttributes.contains(attr.getName()));

		populateImplementationSpecificAttributeLevelMetadata(attribute, attr);

		return attribute;
	}

    /**
     * Gets a collection's metadata from the property descriptor.
     *
     * @param collections The list of plural attributes to process.
     * @return The list of collections for this data object.
     */
	protected List<DataObjectCollection> getCollectionsFromMetadata(Set<PluralAttribute> collections) {
		List<DataObjectCollection> colls = new ArrayList<DataObjectCollection>(collections.size());
		for (PluralAttribute cd : collections) {
			colls.add(getCollectionMetadataFromCollectionAttribute(cd));
		}
		return colls;
	}

	/**
	 * Extracts the collection metadata from a single JPA {@link PluralAttribute} object.
     *
     * @param cd The plural attribute to process.
     * @return The collection metadata from a single JPA {@link PluralAttribute} object.
	 */
	protected DataObjectCollection getCollectionMetadataFromCollectionAttribute(PluralAttribute cd) {
		try {
			DataObjectCollectionImpl collection = new DataObjectCollectionImpl();

			// OJB stores the related class object name. We need to go into the repository and grab the table name.
			Class<?> collectionElementClass = cd.getElementType().getJavaType();
			EntityType<?> elementEntityType = entityManager.getMetamodel().entity(collectionElementClass);
			collection.setName(cd.getName());
			collection.setRelatedType(collectionElementClass);
			populateImplementationSpecificCollectionLevelMetadata(collection, cd);

			// Set to read only if store (save) operations should not be pushed through
			PersistentAttributeType persistentAttributeType = cd.getPersistentAttributeType();
			
			// default case:  Without any mapping attributes, collections are linked by their primary key
			if (persistentAttributeType == PersistentAttributeType.ONE_TO_MANY) {
				// TODO: We probably still need to handle the "mappedBy" property on the OneToMany definition
				
				// We only perform this logic here if we did not populate it in the implementation-specific call above
				if (collection.getAttributeRelationships().isEmpty()) {
					// need to obtain the keys for the relationship
					List<String> pkFields = getPrimaryKeyAttributeNames((EntityType<?>) cd.getDeclaringType());
					List<String> fkFields = getPrimaryKeyAttributeNames(elementEntityType);
					List<DataObjectAttributeRelationship> attributeRelationships = new ArrayList<DataObjectAttributeRelationship>();
					for (int i = 0; i < pkFields.size(); i++) {
						attributeRelationships.add(new DataObjectAttributeRelationshipImpl(pkFields.get(i), fkFields
								.get(i)));
					}
					collection.setAttributeRelationships(attributeRelationships);
				}
			} else if ( persistentAttributeType == PersistentAttributeType.MANY_TO_MANY ) {
				// OK, this is an assumption
				collection.setIndirectCollection( true );
				// And, since the connection is set at the *database* level through the @JoinTable anotation
				// we do not have any field names with which to make the connection
				collection.setAttributeRelationships(null);
			}
			
			return collection;
		} catch (RuntimeException ex) {
			LOG.error("Unable to process Collection metadata: " + cd);
			throw ex;
		}
	}

    /**
     * Gets the list of relationships for this data object.
     *
     * @param references The list of singular attribute references.
     * @return The list of relationships for this data object.
     */
	protected List<DataObjectRelationship> getRelationships(Set<?> references) {
		List<DataObjectRelationship> rels = new ArrayList<DataObjectRelationship>(references.size());
		for (SingularAttribute rd : (Set<SingularAttribute>) references) {
			if (rd.isAssociation()) {
				rels.add(getRelationshipMetadata(rd));
			}
		}
		return rels;
	}

    /**
     * Gets a single field's relationship metadata.
     *
     * @param rd The singular attribute to process.
     * @return The single field's relationship metadata.
     */
	protected DataObjectRelationship getRelationshipMetadata(SingularAttribute rd) {
		try {
			DataObjectRelationshipImpl relationship = new DataObjectRelationshipImpl();

			// OJB stores the related class object name. We need to go into the repository and grab the table name.
			Class<?> referencedClass = rd.getBindableJavaType();
			EntityType<?> referencedEntityType = entityManager.getMetamodel().entity(referencedClass);
			relationship.setName(rd.getName());
			relationship.setRelatedType(referencedClass);
			populateImplementationSpecificRelationshipLevelMetadata(relationship, rd);

			return relationship;
		} catch (RuntimeException ex) {
			LOG.error("Unable to process Relationship metadata: " + rd);
			throw ex;
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean isClassPersistable(Class<?> type) {
		return handles(type);
	}

    /**
     * Setter for the entity manager.
     *
     * @param entityManager The entity manager to set.
     */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

    /**
     * Gets the entity manager for interacting with the database.
     *
     * @return The entity manager for interacting with the database.
     */
	public EntityManager getEntityManager() {
		return entityManager;
	}
}
