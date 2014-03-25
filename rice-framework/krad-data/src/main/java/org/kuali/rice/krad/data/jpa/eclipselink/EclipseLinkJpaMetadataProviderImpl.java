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
package org.kuali.rice.krad.data.jpa.eclipselink;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metamodel.EmbeddableTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.ManagedTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.PluralAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ConverterClass;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.kuali.rice.krad.data.jpa.JpaMetadataProviderImpl;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollectionSortAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataConfigurationException;
import org.kuali.rice.krad.data.metadata.SortDirection;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeRelationshipImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionSortAttributeImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectMetadataImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectRelationshipImpl;
import org.kuali.rice.krad.data.metadata.impl.MetadataChildBase;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides an EclipseLink-specific implementation for the {@link JpaMetadataProviderImpl}.
 */
public class EclipseLinkJpaMetadataProviderImpl extends JpaMetadataProviderImpl {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(EclipseLinkJpaMetadataProviderImpl.class);

    /**
     * {@inheritDoc}
     */
	@Override
	protected void populateImplementationSpecificEntityLevelMetadata(DataObjectMetadataImpl metadata,
			EntityType<?> entityType) {
		if ( entityType instanceof EntityTypeImpl ) {
			metadata.setBackingObjectName(((EntityTypeImpl<?>) entityType).getDescriptor().getTableName());
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void populateImplementationSpecificAttributeLevelMetadata(DataObjectAttributeImpl attribute,
			SingularAttribute<?, ?> attr) {

		if (attr instanceof SingularAttributeImpl) {
			DatabaseMapping mapping = ((SingularAttributeImpl<?, ?>) attr).getMapping();
			if (mapping != null && mapping.getField() != null) {
                attribute.setReadOnly(mapping.isReadOnly());
				attribute.setBackingObjectName(mapping.getField().getName());
				if (mapping.getField().getLength() != 0) {
					attribute.setMaxLength((long) mapping.getField().getLength());
				}

				// Special check on the converters to attempt to default secure attributes from being shown on the UI
				// We check for a converter which has "encrypt" in its name and auto-set the attribute security
				// to mask the attribute.
				if (mapping instanceof DirectToFieldMapping) {
					Converter converter = ((DirectToFieldMapping) mapping).getConverter();
					// ConverterClass is the internal wrapper EclipseLink uses to wrap the JPA AttributeConverter
					// classes
					// and make them conform to the EclipseLink internal API
					if (converter != null && converter instanceof ConverterClass) {
						// Unfortunately, there is no access to the actual converter class, so we have to hack it
						try {
							Field f = ConverterClass.class.getDeclaredField("attributeConverterClassName");
							f.setAccessible(true);
							String attributeConverterClassName = (String) f.get(converter);
							if (StringUtils.containsIgnoreCase(attributeConverterClassName, "encrypt")) {
								attribute.setSensitive(true);
							}
						} catch (Exception e) {
							LOG.warn("Unable to access the converter name for attribute: "
									+ attribute.getOwningType().getName() + "." + attribute.getName()
									+ "  Skipping attempt to detect converter.");
						}
					}
				}

			}
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void populateImplementationSpecificCollectionLevelMetadata(DataObjectCollectionImpl collection,
			PluralAttribute<?, ?, ?> cd) {
		// OJB stores the related class object name. We need to go into the repository and grab the table name.
		Class<?> collectionElementClass = cd.getElementType().getJavaType();
		EntityType<?> elementEntityType = entityManager.getMetamodel().entity(collectionElementClass);
		// get table name behind element
		if (elementEntityType instanceof EntityTypeImpl) {
			collection.setBackingObjectName(((EntityTypeImpl<?>) elementEntityType).getDescriptor().getTableName());
		}

		// Set to read only if store (save) operations should not be pushed through
		PersistentAttributeType persistentAttributeType = cd.getPersistentAttributeType();

		if (cd instanceof PluralAttributeImpl) {
			PluralAttributeImpl<?, ?, ?> coll = (PluralAttributeImpl<?, ?, ?>) cd;
			CollectionMapping collectionMapping = coll.getCollectionMapping();

			if (collectionMapping instanceof OneToManyMapping) {
				OneToManyMapping otm = (OneToManyMapping) collectionMapping;
                populateInverseRelationship(otm, collection);
				Map<DatabaseField, DatabaseField> keyMap = otm.getSourceKeysToTargetForeignKeys();
				List<DataObjectAttributeRelationship> attributeRelationships = new ArrayList<DataObjectAttributeRelationship>();
				for (Map.Entry<DatabaseField, DatabaseField> keyRel : keyMap.entrySet()) {
					attributeRelationships.add(new DataObjectAttributeRelationshipImpl(
							getPropertyNameFromDatabaseColumnName(cd.getDeclaringType(), keyRel.getKey().getName()),
							getPropertyNameFromDatabaseColumnName(elementEntityType, keyRel.getValue().getName())));
				}
				collection.setAttributeRelationships(attributeRelationships);
			}

			collection.setReadOnly(collectionMapping.isReadOnly());
			collection.setSavedWithParent(collectionMapping.isCascadePersist());
			collection.setDeletedWithParent(collectionMapping.isCascadeRemove());
			collection.setLoadedAtParentLoadTime(collectionMapping.isCascadeRefresh() && !collectionMapping.isLazy());
			collection.setLoadedDynamicallyUponUse(collectionMapping.isCascadeRefresh() && collectionMapping.isLazy());
		} else {
			// get what we can based on JPA values (note that we just set some to have values here)
			collection.setReadOnly(false);
			collection.setSavedWithParent(persistentAttributeType == PersistentAttributeType.ONE_TO_MANY);
			collection.setDeletedWithParent(persistentAttributeType == PersistentAttributeType.ONE_TO_MANY);
			collection.setLoadedAtParentLoadTime(true);
			collection.setLoadedDynamicallyUponUse(false);
		}

		// We need to detect the case of a intermediate mapping table. These tables are not directly mapped
		// in OJB, but are referenced by their table and column names.
		// The attributes referenced are assumed to be in the order of the PK fields of the parent and child objects
		// as there is no way to identify the attributes/columns on the linked classes.

		// Extract the default sort order for the collection
		List<DataObjectCollectionSortAttribute> sortAttributes = new ArrayList<DataObjectCollectionSortAttribute>();
		if (cd instanceof PluralAttributeImpl) {
			PluralAttributeImpl<?, ?, ?> coll = (PluralAttributeImpl<?, ?, ?>) cd;
			CollectionMapping collectionMapping = coll.getCollectionMapping();
			if (collectionMapping.getSelectionQuery() instanceof ObjectLevelReadQuery) {
				ObjectLevelReadQuery readQuery = (ObjectLevelReadQuery) collectionMapping.getSelectionQuery();
				List<Expression> orderByExpressions = readQuery.getOrderByExpressions();
				for (Expression expression : orderByExpressions) {
					if (expression instanceof FunctionExpression) {
						String attributeName = ((FunctionExpression) expression).getBaseExpression().getName();
						SortDirection direction = SortDirection.ASCENDING;
						if (expression.getOperator().isOrderOperator()) {
							if (StringUtils
									.containsIgnoreCase(expression.getOperator().getDatabaseStrings()[0], "DESC")) {
								direction = SortDirection.DESCENDING;
							}
						}
						sortAttributes.add(new DataObjectCollectionSortAttributeImpl(attributeName, direction));
					}
				}
			}

		}
		collection.setDefaultCollectionOrderingAttributeNames(sortAttributes);
	}

    /**
     * Returns the property name on the given entity type which the given database column is mapped to.
     *
     * <p>
     * If no field on the given type is mapped to this field (which is common in cases of a JPA relationship without an
     * actual {@link javax.persistence.Column} annotated field to represent the foreign key) then this method will
     * return null.
     * </p>
     *
     * @param entityType the entity type on which to search for a property that is mapped to the given column
     * @param databaseColumnName the name of the database column
     *
     * @return the name of the property on the given entity type which maps to the given column, or null if no such
     *         mapping exists
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    protected String getPropertyNameFromDatabaseColumnName(ManagedType entityType, String databaseColumnName) {
		for (SingularAttributeImpl attr : (Set<SingularAttributeImpl>) entityType.getSingularAttributes()) {
			if (!attr.isAssociation()) {
				if (!(attr.getClass().isAssignableFrom(EmbeddableTypeImpl.class)) &&
                        !(attr.getMapping().getClass().isAssignableFrom(AggregateObjectMapping.class)) &&
                        attr.getMapping().getField().getName().equals(databaseColumnName)) {
					return attr.getName();
				}
			}
		}
		return null;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	protected void populateImplementationSpecificRelationshipLevelMetadata(DataObjectRelationshipImpl relationship,
			SingularAttribute<?, ?> rd) {
		// We need to go into the repository and grab the table name.
		Class<?> referencedClass = rd.getBindableJavaType();
		EntityType<?> referencedEntityType = entityManager.getMetamodel().entity(referencedClass);
		if (referencedEntityType instanceof EntityTypeImpl) {
			relationship
					.setBackingObjectName(((EntityTypeImpl<?>) referencedEntityType).getDescriptor().getTableName());
		}
		// Set to read only if store (save) operations should not be pushed through
		PersistentAttributeType persistentAttributeType = rd.getPersistentAttributeType();

		if (rd instanceof SingularAttributeImpl) {
			SingularAttributeImpl<?, ?> rel = (SingularAttributeImpl<?, ?>) rd;

			OneToOneMapping relationshipMapping = (OneToOneMapping) rel.getMapping();
			relationship.setReadOnly(relationshipMapping.isReadOnly());
			relationship.setSavedWithParent(relationshipMapping.isCascadePersist());
			relationship.setDeletedWithParent(relationshipMapping.isCascadeRemove());
			relationship.setLoadedAtParentLoadTime(relationshipMapping.isCascadeRefresh()
					&& !relationshipMapping.isLazy());
			relationship.setLoadedDynamicallyUponUse(relationshipMapping.isCascadeRefresh()
					&& relationshipMapping.isLazy());

			List<DataObjectAttributeRelationship> attributeRelationships = new ArrayList<DataObjectAttributeRelationship>();
			for (DatabaseField parentField : relationshipMapping.getForeignKeyFields()) {
				String parentFieldName = getPropertyNameFromDatabaseColumnName(rd.getDeclaringType(),
						parentField.getName());
                if (parentFieldName != null) {
				    DatabaseField childField = relationshipMapping.getSourceToTargetKeyFields().get(parentField);
				    if (childField != null) {
					    // the target field is always done by column name. So, we need to get into the target entity and
					    // find the associated field :-(
					    // If the lookup fails, we will at least have the column name
					    String childFieldName = getPropertyNameFromDatabaseColumnName(referencedEntityType,
                                childField.getName());
                        if (childFieldName != null) {
					        attributeRelationships
                                    .add(new DataObjectAttributeRelationshipImpl(parentFieldName, childFieldName));
                        }
				    } else {
					    LOG.warn("Unable to find child field reference.  There may be a JPA mapping problem on "
						    	+ rd.getDeclaringType().getJavaType() + ": " + relationship);
				    }
                }
			}
			relationship.setAttributeRelationships(attributeRelationships);

            populateInverseRelationship(relationshipMapping, relationship);

		} else {
			// get what we can based on JPA values (note that we just set some to have values here)
			relationship.setReadOnly(persistentAttributeType == PersistentAttributeType.MANY_TO_ONE);
			relationship.setSavedWithParent(persistentAttributeType == PersistentAttributeType.ONE_TO_ONE);
			relationship.setDeletedWithParent(persistentAttributeType == PersistentAttributeType.ONE_TO_ONE);
			relationship.setLoadedAtParentLoadTime(true);
			relationship.setLoadedDynamicallyUponUse(false);
		}
	}

    /**
     * Populates the inverse relationship for a given relationship.
     *
     * @param mapping the {@link DatabaseMapping} that defines the relationship.
     * @param relationship the relationship of which to populate the other side.
     */
    protected void populateInverseRelationship(DatabaseMapping mapping, MetadataChildBase relationship) {
        DatabaseMapping relationshipPartner = findRelationshipPartner(mapping);
        if (relationshipPartner != null) {
            Class<?> partnerType = relationshipPartner.getDescriptor().getJavaClass();
            DataObjectMetadata partnerMetadata = masterMetadataMap.get(partnerType);
            // if the target metadata is not null, it means that entity has already been processed,
            // so we can go ahead and establish the inverse relationship
            if (partnerMetadata != null) {
                // first check if it's a relationship
                MetadataChildBase relationshipPartnerMetadata =
                        (MetadataChildBase)partnerMetadata.getRelationship(relationshipPartner.getAttributeName());
                if (relationshipPartnerMetadata == null) {
                    relationshipPartnerMetadata =
                            (MetadataChildBase)partnerMetadata.getCollection(relationshipPartner.getAttributeName());
                }
                if (relationshipPartnerMetadata != null) {
                    relationshipPartnerMetadata.setInverseRelationship(relationship);
                    relationship.setInverseRelationship(relationshipPartnerMetadata);
                }

            }
        }
    }

    /**
     * Gets the inverse mapping of the given {@link DatabaseMapping}.
     *
     * @param databaseMapping the {@link DatabaseMapping} of which to get the inverse.
     * @return the inverse mapping of the given {@link DatabaseMapping}.
     */
    protected DatabaseMapping findRelationshipPartner(DatabaseMapping databaseMapping) {
        if (databaseMapping instanceof OneToManyMapping) {
            OneToManyMapping mapping = (OneToManyMapping)databaseMapping;
            if (mapping.getMappedBy() != null) {
                Class<?> referenceClass = mapping.getReferenceClass();
                ClassDescriptor referenceClassDescriptor = getClassDescriptor(referenceClass);
                return referenceClassDescriptor.getMappingForAttributeName(mapping.getMappedBy());
            }
        } else if (databaseMapping instanceof ManyToOneMapping) {
            // one odd thing just to note here, for ManyToOne mappings with an inverse OneToMany, for some reason the
            // getMappedBy method still returns the mappedBy from the OneToMany side, so we can't use nullness of
            // mappedBy to infer which side of the relationship we are on, oddly enough, that's not the way it works
            // for OneToOne mappings (see below)...go figure
            //
            // I have to assume this is some sort of bug in EclipseLink metadata
            ManyToOneMapping mapping = (ManyToOneMapping)databaseMapping;
            Class<?> referenceClass = mapping.getReferenceClass();
            ClassDescriptor referenceClassDescriptor = getClassDescriptor(referenceClass);
            // find the OneToMany mapping which points back to this ManyToOne
            for (DatabaseMapping referenceMapping : referenceClassDescriptor.getMappings()) {
                if (referenceMapping instanceof OneToManyMapping) {
                    OneToManyMapping oneToManyMapping = (OneToManyMapping)referenceMapping;
                    if (mapping.getAttributeName().equals(oneToManyMapping.getMappedBy())) {
                        return oneToManyMapping;
                    }
                }
            }
        } else if (databaseMapping instanceof OneToOneMapping) {
            OneToOneMapping mapping = (OneToOneMapping)databaseMapping;
            // well for reasons I can't quite fathom, mappedBy is always null on OneToOne relationships,
            // thankfully it's OneToOne so it's pretty easy to figure out the inverse
            ClassDescriptor referenceClassDescriptor = getClassDescriptor(mapping.getReferenceClass());
            // let's check if theres a OneToOne pointing back to us
            for (DatabaseMapping referenceMapping : referenceClassDescriptor.getMappings()) {
                if (referenceMapping instanceof OneToOneMapping) {
                    OneToOneMapping oneToOneMapping = (OneToOneMapping)referenceMapping;
                    if (oneToOneMapping.getReferenceClass().equals(mapping.getDescriptor().getJavaClass())) {
                        return oneToOneMapping;
                    }
                }
            }
        }
        // TODO need to implement for bi-directional OneToOne and ManyToMany
        return null;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public DataObjectRelationship addExtensionRelationship(Class<?> entityClass, String extensionPropertyName,
			Class<?> extensionEntityClass) {
		ClassDescriptor entityDescriptor = getClassDescriptor(entityClass);
		ClassDescriptor extensionEntityDescriptor = getClassDescriptor(extensionEntityClass);

		if (LOG.isDebugEnabled()) {
			LOG.debug("About to attempt to inject a 1:1 relationship on PKs between " + entityDescriptor + " and "
					+ extensionEntityDescriptor);
		}
		OneToOneMapping dm = (OneToOneMapping) entityDescriptor.newOneToOneMapping();
		dm.setAttributeName(extensionPropertyName);
		dm.setReferenceClass(extensionEntityClass);
		dm.setDescriptor(entityDescriptor);
		dm.setIsPrivateOwned(true);
		dm.setJoinFetch(ForeignReferenceMapping.OUTER_JOIN);
		dm.setCascadeAll(true);
		dm.setIsLazy(false);
		dm.dontUseIndirection();
		dm.setIsOneToOneRelationship(true);
		dm.setRequiresTransientWeavedFields(false);

        OneToOneMapping inverse = findExtensionInverse(extensionEntityDescriptor, entityClass);
        dm.setMappedBy(inverse.getAttributeName());
        for (DatabaseField sourceField : inverse.getSourceToTargetKeyFields().keySet()) {
            DatabaseField targetField = inverse.getSourceToTargetKeyFields().get(sourceField);
            // reverse them, pass the source from the inverse as our target and the target from the inverse as our source
            dm.addTargetForeignKeyField(sourceField, targetField);
        }

        dm.preInitialize(getEclipseLinkEntityManager().getDatabaseSession());
		dm.initialize(getEclipseLinkEntityManager().getDatabaseSession());
		entityDescriptor.addMapping(dm);
		entityDescriptor.getObjectBuilder().initialize(getEclipseLinkEntityManager().getDatabaseSession());

        // build the data object relationship
        ManagedTypeImpl<?> managedType = (ManagedTypeImpl<?>)getEntityManager().getMetamodel().managedType(entityClass);
        SingularAttributeImpl<?, ?> singularAttribute = new SingularAttributeLocal(managedType, dm);
        return getRelationshipMetadata(singularAttribute);
	}

    /**
     * Provides a local implementation of {@link SingularAttributeImpl}.
     */
    class SingularAttributeLocal extends SingularAttributeImpl {

        /**
         * Creates a local implementation of {@link SingularAttributeImpl}.
         *
         * @param managedType the {@link ManagedType}.
         * @param mapping the {@link DatabaseMapping}.
         */
        SingularAttributeLocal(ManagedTypeImpl managedType, DatabaseMapping mapping) {
            super(managedType, mapping);
        }
    }

    /**
     * Gets the inverse extension of the given {@link ClassDescriptor}.
     *
     * @param extensionEntityDescriptor the {@link ClassDescriptor} of which to get the inverse.
     * @param entityType the type of the entity.
     * @return the inverse extension of the given {@link ClassDescriptor}.
     */
    protected OneToOneMapping findExtensionInverse(ClassDescriptor extensionEntityDescriptor, Class<?> entityType) {
        Collection<DatabaseMapping> derivedIdMappings = extensionEntityDescriptor.getDerivesIdMappinps();
        String extensionInfo = "(" + extensionEntityDescriptor.getJavaClass().getName() + " -> " + entityType.getName()
                + ")";
        if (derivedIdMappings == null || derivedIdMappings.isEmpty()) {
            throw new MetadataConfigurationException("Attempting to use extension framework, but extension "
                    + extensionInfo + " does not have a valid inverse OneToOne Id mapping back to the extended data "
                    + "object. Please ensure it is annotated property for use of the extension framework with JPA.");
        } else if (derivedIdMappings.size() > 1) {
            throw new MetadataConfigurationException("When attempting to determine the inverse relationship for use "
                    + "with extension framework " + extensionInfo + " encountered more than one 'derived id' mapping, "
                    + "there should be only one!");
        }
        DatabaseMapping inverseMapping = derivedIdMappings.iterator().next();
        if (!(inverseMapping instanceof OneToOneMapping)) {
            throw new MetadataConfigurationException("Identified an inverse derived id mapping for extension "
                    + "relationship " + extensionInfo + " but it was not a one-to-one mapping: " + inverseMapping);
        }
        return (OneToOneMapping)inverseMapping;
    }

    /**
     * Gets the descriptor for the entity type.
     *
     * @param entityClass the type of the enty.
     * @return the descriptor for the entity type.
     */
    protected ClassDescriptor getClassDescriptor(Class<?> entityClass) {
		return getEclipseLinkEntityManager().getDatabaseSession().getDescriptor(entityClass);
	}

    /**
     * The entity manager for interacting with the database.
     * @return the entity manager for interacting with the database.
     */
	protected JpaEntityManager getEclipseLinkEntityManager() {
		return (JpaEntityManager) entityManager;
	}
}
