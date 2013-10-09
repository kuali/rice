/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metamodel.EmbeddableTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.EntityTypeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.PluralAttributeImpl;
import org.eclipse.persistence.internal.jpa.metamodel.SingularAttributeImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.converters.ConverterClass;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.kuali.rice.krad.data.jpa.JpaMetadataProviderImpl;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollectionSortAttribute;
import org.kuali.rice.krad.data.metadata.SortDirection;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeRelationshipImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionSortAttributeImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectMetadataImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectRelationshipImpl;

public class EclipseLinkJpaMetadataProviderImpl extends JpaMetadataProviderImpl {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(EclipseLinkJpaMetadataProviderImpl.class);

	@Override
	protected void populateImplementationSpecificEntityLevelMetadata(DataObjectMetadataImpl metadata,
			EntityType<?> entityType) {
		if ( entityType instanceof EntityTypeImpl ) {
			metadata.setBackingObjectName(((EntityTypeImpl<?>) entityType).getDescriptor().getTableName());
		}
	}
	
	@Override
	protected void populateImplementationSpecificAttributeLevelMetadata(DataObjectAttributeImpl attribute,
			SingularAttribute<?, ?> attr) {

		if (attr instanceof SingularAttributeImpl) {
			DatabaseMapping mapping = ((SingularAttributeImpl<?, ?>) attr).getMapping();
			if (mapping != null && mapping.getField() != null) {
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

	@Override
	protected void populateImplementationSpecificCollectionLevelMetadata(DataObjectCollectionImpl collection,
			PluralAttribute<?, ?, ?> cd) {
		// TODO Auto-generated method stub
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
     * Returns the property name on the given entity type which the given database column is mapped to. If no field on
     * the given type is mapped to this field (which is common in cases of a JPA relationship without an actual @Column
     * annotated field to represent the foreign key) then this method will return null.
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

		} else {
			// get what we can based on JPA values (note that we just set some to have values here)
			relationship.setReadOnly(persistentAttributeType == PersistentAttributeType.MANY_TO_ONE);
			relationship.setSavedWithParent(persistentAttributeType == PersistentAttributeType.ONE_TO_ONE);
			relationship.setDeletedWithParent(persistentAttributeType == PersistentAttributeType.ONE_TO_ONE);
			relationship.setLoadedAtParentLoadTime(true);
			relationship.setLoadedDynamicallyUponUse(false);
		}
	}

	@Override
	public void addExtensionRelationship(Class<?> entityClass, String extensionPropertyName,
			Class<?> extensionEntityClass) {
		ClassDescriptor entityDescriptor = getClassDescriptor(entityClass);
		ClassDescriptor extensionEntityDescriptor = getClassDescriptor(extensionEntityClass);

		if (LOG.isDebugEnabled()) {
			LOG.debug("About to attempt to inject a 1:1 relationship on PKs between " + entityDescriptor + " and "
					+ extensionEntityDescriptor);
		}
		// ((EntityTypeImpl<?>)entityType).
		// TODO: verify that PK fields align
		OneToOneMapping dm = (OneToOneMapping) entityDescriptor.newOneToOneMapping();
		dm.setAttributeName(extensionPropertyName);
		dm.setReferenceClass(extensionEntityClass);
		dm.setDescriptor(entityDescriptor);
		dm.setIsPrivateOwned(true);
		dm.setCascadeRefresh(true);
		dm.setJoinFetch(ForeignReferenceMapping.OUTER_JOIN);
		dm.setCascadeAll(true);
		dm.setIsLazy(false);
		dm.setIsOptional(false);
		dm.dontUseIndirection();
		dm.setIsOneToOneRelationship(true);
		dm.setIsOneToOnePrimaryKeyRelationship(true);
		dm.setRequiresTransientWeavedFields(false);

		List<String> extensionPkFields = extensionEntityDescriptor.getPrimaryKeyFieldNames();
		int index = 0;
		for (String pkFieldName : entityDescriptor.getPrimaryKeyFieldNames()) {
			dm.addForeignKeyFieldName(pkFieldName, extensionPkFields.get(index));
			index++;
		}
		dm.preInitialize(getEclipseLinkEntityManager().getDatabaseSession());
		dm.initialize(getEclipseLinkEntityManager().getDatabaseSession());
		entityDescriptor.addMapping(dm);
		entityDescriptor.getObjectBuilder().initialize(getEclipseLinkEntityManager().getDatabaseSession());
	}

	protected ClassDescriptor getClassDescriptor(Class<?> entityClass) {
		return getEclipseLinkEntityManager().getDatabaseSession().getDescriptor(entityClass);
	}

	protected JpaEntityManager getEclipseLinkEntityManager() {
		return (JpaEntityManager) entityManager;
	}
}
