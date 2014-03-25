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
package org.kuali.rice.krad.data.provider.annotation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectCollectionSortAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataConfigurationException;
import org.kuali.rice.krad.data.metadata.MetadataMergeAction;
import org.kuali.rice.krad.data.metadata.MetadataRepository;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectAttributeRelationshipImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectCollectionSortAttributeImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectMetadataImpl;
import org.kuali.rice.krad.data.metadata.impl.DataObjectRelationshipImpl;
import org.kuali.rice.krad.data.metadata.impl.MetadataCommonBase;
import org.kuali.rice.krad.data.provider.annotation.AttributeRelationship;
import org.kuali.rice.krad.data.provider.annotation.BusinessKey;
import org.kuali.rice.krad.data.provider.annotation.CollectionRelationship;
import org.kuali.rice.krad.data.provider.annotation.CollectionSortAttribute;
import org.kuali.rice.krad.data.provider.annotation.Description;
import org.kuali.rice.krad.data.provider.annotation.ForceUppercase;
import org.kuali.rice.krad.data.provider.annotation.InheritProperties;
import org.kuali.rice.krad.data.provider.annotation.InheritProperty;
import org.kuali.rice.krad.data.provider.annotation.KeyValuesFinderClass;
import org.kuali.rice.krad.data.provider.annotation.Label;
import org.kuali.rice.krad.data.provider.annotation.MergeAction;
import org.kuali.rice.krad.data.provider.annotation.NonPersistentProperty;
import org.kuali.rice.krad.data.provider.annotation.PropertyEditorClass;
import org.kuali.rice.krad.data.provider.annotation.ReadOnly;
import org.kuali.rice.krad.data.provider.annotation.Relationship;
import org.kuali.rice.krad.data.provider.annotation.Sensitive;
import org.kuali.rice.krad.data.provider.annotation.ShortLabel;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViews;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHints;
import org.kuali.rice.krad.data.provider.annotation.UifValidCharactersConstraintBeanName;
import org.kuali.rice.krad.data.provider.impl.MetadataProviderBase;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Parses custom krad-data annotations for additional metadata to layer on top of that provided by the persistence
 * metadata provider which should have run before this one.
 *
 * <p>
 * At the moment, it will only process classes which were previously identified by the JPA implementation.
 * </p>
 *
 * <p>
 * TODO: Addition of a new Annotation which will need to be scanned for in order to process non-persistent classes as data objects.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AnnotationMetadataProviderImpl extends MetadataProviderBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(AnnotationMetadataProviderImpl.class);

	private boolean initializationAttempted = false;
    private DataObjectService dataObjectService;

    /**
     * {@inheritDoc}
     */
	@Override
	protected void initializeMetadata(Collection<Class<?>> types) {
		if (initializationAttempted) {
			return;
		}
        initializationAttempted = true;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing annotations for the given list of data objects: " + types);
		}
		if (types == null || types.isEmpty()) {
            LOG.warn(getClass().getSimpleName() + " was passed an empty list of types to initialize, doing nothing");
            return;
		}
		LOG.info("Started Scanning For Metadata Annotations");
		for (Class<?> type : types) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Processing Annotations on : " + type);
			}
			boolean annotationsFound = false;
			DataObjectMetadataImpl metadata = new DataObjectMetadataImpl();
			metadata.setProviderName(this.getClass().getSimpleName());
			metadata.setType(type);
			// check for class level annotations
			annotationsFound |= processClassLevelAnnotations(type, metadata);
			// check for field level annotations
			annotationsFound |= processFieldLevelAnnotations(type, metadata);
			// check for method (getter) level annotations
			annotationsFound |= processMethodLevelAnnotations(type, metadata);
			// Look for inherited properties
			annotationsFound |= processInheritedAttributes(type, metadata);
			if (annotationsFound) {
				masterMetadataMap.put(type, metadata);
			}
		}
		LOG.info("Completed Scanning For Metadata Annotations");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Annotation Metadata: " + masterMetadataMap);
		}
	}

	/**
	 * Handle annotations made at the class level and add their data to the given metadata object.
     *
     * @param clazz the class to process.
	 * @param metadata the metadata for the class.
	 * @return <b>true</b> if any annotations are found.
	 */
	protected boolean processClassLevelAnnotations(Class<?> clazz, DataObjectMetadataImpl metadata) {
		boolean classAnnotationFound = false;
		boolean fieldAnnotationsFound = false;
		// get the class annotations
		List<DataObjectAttribute> attributes = new ArrayList<DataObjectAttribute>(metadata.getAttributes());
		Annotation[] classAnnotations = clazz.getAnnotations();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Class-level annotations: " + Arrays.asList(classAnnotations));
		}
		for (Annotation a : classAnnotations) {
			// check if it's one we can handle
			// do something with it
			if (processAnnotationsforCommonMetadata(a, metadata)) {
				classAnnotationFound = true;
				continue;
			}
			if (a instanceof MergeAction) {
				MetadataMergeAction mma = ((MergeAction) a).value();
				if (!(mma == MetadataMergeAction.MERGE || mma == MetadataMergeAction.REMOVE)) {
					throw new MetadataConfigurationException(
							"Only the MERGE and REMOVE merge actions are supported since the annotation metadata provider can not specify all required properties and may only be used as an overlay.");
				}
				metadata.setMergeAction(mma);
				classAnnotationFound = true;
				continue;
			}
			if (a instanceof UifAutoCreateViews) {
				metadata.setAutoCreateUifViewTypes(Arrays.asList(((UifAutoCreateViews) a).value()));
				classAnnotationFound = true;
			}
		}
		if (fieldAnnotationsFound) {
			metadata.setAttributes(attributes);
		}
		return classAnnotationFound;
	}

	/**
	 * Handle annotations made at the field level and add their data to the given metadata object.
     *
	 * @param clazz the class to process.
     * @param metadata the metadata for the class.
	 * @return <b>true</b> if any annotations are found.
	 */
	protected boolean processFieldLevelAnnotations(Class<?> clazz, DataObjectMetadataImpl metadata) {
		boolean fieldAnnotationsFound = false;
		boolean additionalClassAnnotationsFound = false;
		List<DataObjectAttribute> attributes = new ArrayList<DataObjectAttribute>();
		for (Field f : clazz.getDeclaredFields()) {
			boolean fieldAnnotationFound = false;
			String propertyName = f.getName();
			DataObjectAttributeImpl attr = (DataObjectAttributeImpl) metadata.getAttribute(propertyName);
			boolean existingAttribute = attr != null;
			if (!existingAttribute) {
				attr = new DataObjectAttributeImpl();
				attr.setName(propertyName);
				attr.setType(f.getType());
				DataType dataType = DataType.getDataTypeFromClass(f.getType());
				if (dataType == null) {
					dataType = DataType.STRING;
				}
				attr.setDataType(dataType);
				attr.setOwningType(metadata.getType());
			}
			Annotation[] fieldAnnotations = f.getDeclaredAnnotations();
			if (LOG.isDebugEnabled()) {
				LOG.debug(f.getDeclaringClass() + "." + f.getName() + " Field-level annotations: "
						+ Arrays.asList(fieldAnnotations));
			}
			for (Annotation a : fieldAnnotations) {
				// check if it's one we can handle then do something with it
				fieldAnnotationFound |= processAnnotationForAttribute(a, attr, metadata);
				if (!fieldAnnotationFound) {
					if (a instanceof BusinessKey) {
						ArrayList<String> businessKeys = new ArrayList<String>(metadata.getBusinessKeyAttributeNames());
						businessKeys.add(f.getName());
						metadata.setBusinessKeyAttributeNames(businessKeys);
						// We are not altering the field definition, so dont set the flag
						// fieldAnnotationFound = true;
						additionalClassAnnotationsFound = true;
						continue;
					}
					if (a instanceof Relationship) {
						addDataObjectRelationship(metadata, f, (Relationship) a);

						additionalClassAnnotationsFound = true;
						continue;
					}
					if (a instanceof CollectionRelationship) {
						addDataObjectCollection(metadata, f, (CollectionRelationship) a);

						additionalClassAnnotationsFound = true;
						continue;
					}
				}
			}
			if (fieldAnnotationFound) {
				attributes.add(attr);
				fieldAnnotationsFound = true;
			}
		}
		if (fieldAnnotationsFound) {
			metadata.setAttributes(attributes);
		}
		return fieldAnnotationsFound || additionalClassAnnotationsFound;
	}

	/**
	 * Helper method to process the annotations which can be present on attributes or classes.
     *
     * @param a the annotation to process.
     * @param metadata the metadata for the class.
	 * @return <b>true</b> if a valid annotation is found
	 */
	protected boolean processAnnotationsforCommonMetadata(Annotation a, MetadataCommonBase metadata) {
		if (a instanceof Label) {
			if (StringUtils.isNotBlank(((Label) a).value())) {
				metadata.setLabel(((Label) a).value());
				return true;
			}
		}
		if (a instanceof ShortLabel) {
			metadata.setShortLabel(((ShortLabel) a).value());
			return true;
		}
		if (a instanceof Description) {
			metadata.setDescription(((Description) a).value());
			return true;
		}
		return false;
	}

	/**
	 * Helper method to process the annotations which can be present on attributes.
     *
     * <p>Used to abstract the logic so it can be applied to both field and method-level annotations.</p>
     *
     * @param a the annotation to process.
     * @param attr the attribute for the field.
     * @param metadata the metadata for the class.
	 * 
	 * @return true if any annotations were processed, false if not
	 */
	protected boolean processAnnotationForAttribute(Annotation a, DataObjectAttributeImpl attr,
			DataObjectMetadataImpl metadata) {
		if (a == null) {
			return false;
		}
		if (a instanceof NonPersistentProperty) {
			attr.setPersisted(false);
			return true;
		}
		if (processAnnotationsforCommonMetadata(a, attr)) {
			return true;
		}
		if (a instanceof ReadOnly) {
			attr.setReadOnly(true);
			return true;
		}
		if (a instanceof UifValidCharactersConstraintBeanName) {
			attr.setValidCharactersConstraintBeanName(((UifValidCharactersConstraintBeanName) a).value());
			return true;
		}
		if (a instanceof KeyValuesFinderClass) {
			try {
				attr.setValidValues(((KeyValuesFinderClass) a).value().newInstance());
				return true;
			} catch (Exception ex) {
				LOG.error("Unable to instantiate options finder: " + ((KeyValuesFinderClass) a).value(), ex);
			}
		}
		if (a instanceof NotNull) {
			attr.setRequired(true);
			return true;
		}
		if (a instanceof ForceUppercase) {
			attr.setForceUppercase(true);
			return true;
		}
		if (a instanceof PropertyEditorClass) {
			try {
				attr.setPropertyEditor(((PropertyEditorClass) a).value().newInstance());
				return true;
			} catch (Exception ex) {
				LOG.warn("Unable to instantiate property editor class for " + metadata.getTypeClassName()
						+ "." + attr.getName() + " : " + ((PropertyEditorClass) a).value());
			}
		}
		if (a instanceof Size) {
			// We only process it at the moment if the max length has been set
			// Otherwise, we want the JPA value (max column length) to pass through
			if (((Size) a).max() != Integer.MAX_VALUE) {
				attr.setMaxLength((long) ((Size) a).max());
				return true;
			}
		}
		if (a instanceof Sensitive) {
			attr.setSensitive(true);
			return true;
		}
		if (a instanceof UifDisplayHints) {
			attr.setDisplayHints(new HashSet<UifDisplayHint>(Arrays.asList(((UifDisplayHints) a).value())));
			return true;
		}
		if (a instanceof MergeAction) {
			MetadataMergeAction mma = ((MergeAction) a).value();
			if (!(mma == MetadataMergeAction.MERGE || mma == MetadataMergeAction.REMOVE)) {
				throw new MetadataConfigurationException(
						"Only the MERGE and REMOVE merge actions are supported since the annotation metadata provider can not specify all required properties and may only be used as an overlay.");
			}
			attr.setMergeAction(mma);
			return true;
		}
		return false;
	}

	/**
	 * Used to find the property name from a getter method.
	 * 
	 * <p>(Not using PropertyUtils since it required an instance of the class.)</p>
     *
     * @param m the method from which to get the property name.
     * @return the property name.
	 */
	protected String getPropertyNameFromGetterMethod(Method m) {
		String propertyName = "";
		if (m.getName().startsWith("get")) {
			propertyName = StringUtils.uncapitalize(StringUtils.removeStart(m.getName(), "get"));
		} else { // must be "is"
			propertyName = StringUtils.uncapitalize(StringUtils.removeStart(m.getName(), "is"));
		}
		return propertyName;
	}

	/**
	 * Handle annotations made at the method level and add their data to the given metadata object.
     *
     * @param clazz the class to process.
     * @param metadata the metadata for the class.
	 * 
	 * @return <b>true</b> if any annotations are found.
	 */
	protected boolean processMethodLevelAnnotations(Class<?> clazz, DataObjectMetadataImpl metadata) {
		boolean fieldAnnotationsFound = false;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing Method Annotations on " + clazz);
		}
		List<DataObjectAttribute> attributes = new ArrayList<DataObjectAttribute>(metadata.getAttributes());
		for (Method m : clazz.getDeclaredMethods()) {
			// we only care about properties which are designated as non-persistent
			// we don't want to load metadata about everything just because it's there
			// (E.g., we don't know how expensive all method calls are)
			if (!m.isAnnotationPresent(NonPersistentProperty.class)) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Rejecting method " + m.getName()
							+ " because does not have NonPersistentProperty annotation");
				}
				continue;
			}
			// we only care about getters
			if (!m.getName().startsWith("get") && !m.getName().startsWith("is")) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Rejecting method " + m.getName() + " because name does not match getter pattern");
				}
				continue;
			}
			// we also need it to return a value and have no arguments to be a proper getter
			if (m.getReturnType() == null || m.getParameterTypes().length > 0) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Rejecting method " + m.getName() + " because has no return type or has arguments");
				}
				continue;
			}
			String propertyName = getPropertyNameFromGetterMethod(m);
			boolean fieldAnnotationFound = false;
			boolean existingAttribute = true;
			DataObjectAttributeImpl attr = (DataObjectAttributeImpl) metadata.getAttribute(propertyName);
			if (attr == null) {
				existingAttribute = false;
				attr = new DataObjectAttributeImpl();
				attr.setName(propertyName);
				attr.setType(m.getReturnType());
				DataType dataType = DataType.getDataTypeFromClass(m.getReturnType());
				if (dataType == null) {
					dataType = DataType.STRING;
				}
				attr.setDataType(dataType);
				attr.setOwningType(metadata.getType());
			}
			Annotation[] methodAnnotations = m.getDeclaredAnnotations();
			if (LOG.isDebugEnabled()) {
				LOG.debug(m.getDeclaringClass() + "." + m.getName() + " Method-level annotations: "
						+ Arrays.asList(methodAnnotations));
			}
			for (Annotation a : methodAnnotations) {
				fieldAnnotationFound |= processAnnotationForAttribute(a, attr, metadata);
			}
			if (fieldAnnotationFound) {
				if (!existingAttribute) {
					attributes.add(attr);
				}
				fieldAnnotationsFound = true;
			}
		}
		if (fieldAnnotationsFound) {
			metadata.setAttributes(attributes);
		}

		return fieldAnnotationsFound;
	}

    /**
     * Adds a relationship for a field to the metadata object.
     *
     * @param metadata the metadata for the class.
     * @param f the field to process.
     * @param a the relationship to add.
     */
	protected void addDataObjectRelationship(DataObjectMetadataImpl metadata, Field f, Relationship a) {
		List<DataObjectRelationship> relationships = new ArrayList<DataObjectRelationship>(metadata.getRelationships());
		DataObjectRelationshipImpl relationship = new DataObjectRelationshipImpl();
		relationship.setName(f.getName());
		Class<?> childType = f.getType();
		relationship.setRelatedType(childType);
		relationship.setReadOnly(true);
		relationship.setSavedWithParent(false);
		relationship.setDeletedWithParent(false);
		relationship.setLoadedAtParentLoadTime(false);
		relationship.setLoadedDynamicallyUponUse(true);

		List<DataObjectAttributeRelationship> attributeRelationships = new ArrayList<DataObjectAttributeRelationship>();
		List<String> referencePkFields = Collections.emptyList();
        MetadataRepository metadataRepository = getDataObjectService().getMetadataRepository();
		if (metadataRepository.contains(childType)) {
            DataObjectMetadata childMetadata = metadataRepository.getMetadata(childType);
			referencePkFields = childMetadata.getPrimaryKeyAttributeNames();
		} else {
			// HACK ALERT!!!!!!!! FIXME: can be removed once Person is annotated for JPA
			if (f.getType().getName().equals("org.kuali.rice.kim.api.identity.Person")) {
				referencePkFields = Collections.singletonList("principalId");
			}
		}
		int index = 0;
		for (String pkField : a.foreignKeyFields()) {
			attributeRelationships.add(new DataObjectAttributeRelationshipImpl(pkField, referencePkFields.get(index)));
			index++;
		}
		relationship.setAttributeRelationships(attributeRelationships);

		relationships.add(relationship);
		metadata.setRelationships(relationships);
	}

    /**
     * Adds a collection relationship for a field to the metadata object.
     *
     * @param metadata the metadata for the class.
     * @param f the field to process.
     * @param a the collection relationship to add.
     */
	protected void addDataObjectCollection(DataObjectMetadataImpl metadata, Field f, CollectionRelationship a) {
		List<DataObjectCollection> collections = new ArrayList<DataObjectCollection>(metadata.getCollections());
		DataObjectCollectionImpl collection = new DataObjectCollectionImpl();
		collection.setName(f.getName());
		
		if ( !Collection.class.isAssignableFrom(f.getType()) ) {
			throw new IllegalArgumentException(
					"@CollectionRelationship annotations can only be on attributes of Collection type.  Field: "
							+ f.getDeclaringClass().getName() + "." + f.getName() + " (" + f.getType() + ")");
		}
		
		if (a.collectionElementClass().equals(Object.class)) { // Object is the default (and meaningless anyway)
			Type[] genericArgs = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
			if (genericArgs.length == 0) {
				throw new IllegalArgumentException(
						"You can only leave off the collectionElementClass annotation on a @CollectionRelationship when the Collection type has been <typed>.  Field: "
								+ f.getDeclaringClass().getName() + "." + f.getName() + " (" + f.getType() + ")");
			}
			collection.setRelatedType((Class<?>) genericArgs[0]);
		} else {
			collection.setRelatedType(a.collectionElementClass());
		}
		
		List<DataObjectAttributeRelationship> attributeRelationships = new ArrayList<DataObjectAttributeRelationship>(
				a.attributeRelationships().length);
		for (AttributeRelationship rel : a.attributeRelationships()) {
			attributeRelationships.add(new DataObjectAttributeRelationshipImpl(rel.parentAttributeName(), rel
					.childAttributeName()));
		}
		collection.setAttributeRelationships(attributeRelationships);

		collection.setReadOnly(false);
		collection.setSavedWithParent(false);
		collection.setDeletedWithParent(false);
		collection.setLoadedAtParentLoadTime(true);
		collection.setLoadedDynamicallyUponUse(false);
		List<DataObjectCollectionSortAttribute> sortAttributes = new ArrayList<DataObjectCollectionSortAttribute>(
				a.sortAttributes().length);
		for (CollectionSortAttribute csa : a.sortAttributes()) {
			sortAttributes.add(new DataObjectCollectionSortAttributeImpl(csa.value(), csa.sortDirection()));
		}
		collection.setDefaultCollectionOrderingAttributeNames(sortAttributes);

		collection.setIndirectCollection(a.indirectCollection());
		collection.setMinItemsInCollection(a.minItemsInCollection());
		collection.setMaxItemsInCollection(a.maxItemsInCollection());
		if (StringUtils.isNotBlank(a.label())) {
			collection.setLabel(a.label());
		}
		if (StringUtils.isNotBlank(a.elementLabel())) {
			collection.setLabel(a.elementLabel());
		}

		collections.add(collection);
		metadata.setCollections(collections);
	}

    /**
     * Handle inherited properties and add their data to the given metadata object.
     *
     * @param clazz the class to process.
     * @param metadata the metadata for the class.
     *
     * @return <b>true</b> if any annotations are found.
     */
	protected boolean processInheritedAttributes(Class<?> clazz, DataObjectMetadataImpl metadata) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Processing InheritProperties field Annotations on " + clazz);
		}
		List<DataObjectAttribute> attributes = new ArrayList<DataObjectAttribute>(metadata.getAttributes());
		boolean fieldAnnotationsFound = false;
		for (Field f : clazz.getDeclaredFields()) {
			boolean fieldAnnotationFound = false;
			String propertyName = f.getName();

			if (!f.isAnnotationPresent(InheritProperties.class) && !f.isAnnotationPresent(InheritProperty.class)) {
				continue;
			}
			fieldAnnotationFound = true;
			// Get the list of inherited properties, either from a single annotation or the "plural" version
			InheritProperty[] propertyList = null;
			InheritProperties a = f.getAnnotation(InheritProperties.class);
			if (a != null) {
				propertyList = a.value();
			} else {
				// if the above is not present, then there must be an @InheritProperty annotation
				InheritProperty ip = f.getAnnotation(InheritProperty.class);
				propertyList = new InheritProperty[] { ip };
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("InheritProperties found on " + clazz + "." + f.getName() + " : "
						+ Arrays.toString(propertyList));
			}
			for (InheritProperty inheritedProperty : propertyList) {
				String inheritedPropertyName = inheritedProperty.name();
				String extendedPropertyName = propertyName + "." + inheritedPropertyName;
				DataObjectAttributeImpl attr = (DataObjectAttributeImpl) metadata.getAttribute(extendedPropertyName);
				boolean existingAttribute = attr != null;
				if (!existingAttribute) {
					// NOTE: dropping to reflection here as the related metadata may not be loaded yet...
					// TODO: this may need to be reworked to allow for "real-time" inheritance
					// since the values seen here should reflect overrides performed later in the chain
					// (e.g., by the MessageServiceMetadataProvider)
					attr = new DataObjectAttributeImpl();
					attr.setName(extendedPropertyName);
					Class<?> relatedClass = f.getType();
					try {
						attr.setType(getTypeOfProperty(relatedClass, inheritedPropertyName));
						DataType dataType = DataType.getDataTypeFromClass(attr.getType());
						if (dataType == null) {
							dataType = DataType.STRING;
						}
						attr.setDataType(dataType);
					} catch (Exception e) {
						throw new IllegalArgumentException("no field with name " + inheritedPropertyName
								+ " exists on " + relatedClass, e);
					}
					// Since this attribute is really part of another object, we want to indicate that it's not
					// persistent (as far as this object is concerned)
					attr.setPersisted(false);
					attr.setOwningType(metadata.getType());
					attr.setInheritedFromType(relatedClass);
					attr.setInheritedFromAttributeName(inheritedPropertyName);
					attr.setInheritedFromParentAttributeName(propertyName);

					// Handle the label override, if present
					processAnnotationForAttribute(inheritedProperty.label(), attr, metadata);
					// Handle the UIF displayoverride, if present
					processAnnotationForAttribute(inheritedProperty.displayHints(), attr, metadata);

					attributes.add(attr);
				}
			}

			fieldAnnotationsFound |= fieldAnnotationFound;
		}
		if (fieldAnnotationsFound) {
			metadata.setAttributes(attributes);
		}
		return fieldAnnotationsFound;
	}

	/**
	 * Used to find the property type of a given attribute regardless of whether the attribute exists as a field or only
	 * as a getter method.
	 * 
	 * <p>(Not using PropertyUtils since it required an instance of the class.)</p>
     *
     * @param clazz the class that contains the property.
     * @param propertyName the name of the property.
     * @return the type of the property.
	 */
	protected Class<?> getTypeOfProperty(Class<?> clazz, String propertyName) {
		try {
			Field f = clazz.getField(propertyName);
			return f.getType();
		} catch (Exception e) {
			// Do nothing = field does not exist
		}
		try {
			Method m = clazz.getMethod("get" + StringUtils.capitalize(propertyName));
			return m.getReturnType();
		} catch (Exception e) {
			// Do nothing = method does not exist
		}
		try {
			Method m = clazz.getMethod("is" + StringUtils.capitalize(propertyName));
			return m.getReturnType();
		} catch (Exception e) {
			// Do nothing = method does not exist
		}
		return null;
	}

	/**
	 * {@inheritDoc}
     *
     * Returns true in this implementation. This tells the composite metadata provider to pass in all known metadata to
	 * the initializeMetadata method.
	 */
	@Override
	public boolean requiresListOfExistingTypes() {
		return true;
	}

    /**
     * Gets whether initialization was attempted.
     *
     * @return whether initialization was attempted.
     */
    public boolean isInitializationAttempted() {
        return initializationAttempted;
    }

    /**
     * Gets the {@link DataObjectService}.
     * @return the {@link DataObjectService}.
     */
    public DataObjectService getDataObjectService() {
        if (dataObjectService == null) {
            dataObjectService = KradDataServiceLocator.getDataObjectService();
        }
        return dataObjectService;
    }

    /**
     * Setter for the the {@link DataObjectService}.
     *
     * @param dataObjectService the the {@link DataObjectService} to set.
     */
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
