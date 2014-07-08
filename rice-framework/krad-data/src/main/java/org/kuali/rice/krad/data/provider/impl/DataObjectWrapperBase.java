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
package org.kuali.rice.krad.data.provider.impl;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.metadata.DataObjectAttribute;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.metadata.MetadataChild;
import org.kuali.rice.krad.data.util.ReferenceLinker;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The base implementation of {@link DataObjectWrapper}.
 *
 * @param <T> the type of the data object to wrap.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DataObjectWrapperBase<T> implements DataObjectWrapper<T> {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DataObjectWrapperBase.class);

    private final T dataObject;
    private final DataObjectMetadata metadata;
    private final BeanWrapper wrapper;
    private final DataObjectService dataObjectService;
    private final ReferenceLinker referenceLinker;

    /**
     * Creates a data object wrapper.
     *
     * @param dataObject the data object to wrap.
     * @param metadata the metadata of the data object.
     * @param dataObjectService the data object service to use.
     * @param referenceLinker the reference linker implementation.
     */
    protected DataObjectWrapperBase(T dataObject, DataObjectMetadata metadata, DataObjectService dataObjectService,
            ReferenceLinker referenceLinker) {
        this.dataObject = dataObject;
        this.metadata = metadata;
        this.dataObjectService = dataObjectService;
        this.referenceLinker = referenceLinker;
        this.wrapper = PropertyAccessorFactory.forBeanPropertyAccess(dataObject);
        // note that we do *not* want to set auto grow to be true here since we are using this primarily for
        // access to the data, we will expose getPropertyValueNullSafe instead because it prevents a a call to
        // getPropertyValue from modifying the internal state of the object by growing intermediate nested paths
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataObjectMetadata getMetadata() {
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getWrappedInstance() {
        return dataObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValueNullSafe(String propertyName) throws BeansException {
        try {
            return getPropertyValue(propertyName);
        } catch (NullValueInNestedPathException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
	@SuppressWarnings("unchecked")
	@Override
    public Class<T> getWrappedClass() {
        return (Class<T>) wrapper.getWrappedClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        return wrapper.getPropertyDescriptors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
        return wrapper.getPropertyDescriptor(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
        wrapper.setAutoGrowNestedPaths(autoGrowNestedPaths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAutoGrowNestedPaths() {
        return wrapper.isAutoGrowNestedPaths();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
        wrapper.setAutoGrowCollectionLimit(autoGrowCollectionLimit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAutoGrowCollectionLimit() {
        return wrapper.getAutoGrowCollectionLimit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConversionService(ConversionService conversionService) {
        wrapper.setConversionService(conversionService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConversionService getConversionService() {
        return wrapper.getConversionService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
        wrapper.setExtractOldValueForEditor(extractOldValueForEditor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExtractOldValueForEditor() {
        return wrapper.isExtractOldValueForEditor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadableProperty(String propertyName) {
        return wrapper.isReadableProperty(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWritableProperty(String propertyName) {
        return wrapper.isWritableProperty(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getPropertyType(String propertyName) throws BeansException {
        return wrapper.getPropertyType(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
        return wrapper.getPropertyTypeDescriptor(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPropertyValue(String propertyName) throws BeansException {
        return wrapper.getPropertyValue(propertyName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(String propertyName, Object value) throws BeansException {
        wrapper.setPropertyValue(propertyName, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValue(PropertyValue pv) throws BeansException {
        wrapper.setPropertyValue(pv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValues(Map<?, ?> map) throws BeansException {
        wrapper.setPropertyValues(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValues(PropertyValues pvs) throws BeansException {
        wrapper.setPropertyValues(pvs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown) throws BeansException {
        wrapper.setPropertyValues(pvs, ignoreUnknown);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown,
            boolean ignoreInvalid) throws BeansException {
        wrapper.setPropertyValues(pvs, ignoreUnknown, ignoreInvalid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        wrapper.registerCustomEditor(requiredType, propertyEditor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor) {
        wrapper.registerCustomEditor(requiredType, propertyPath, propertyEditor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath) {
        return wrapper.findCustomEditor(requiredType, propertyPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <Y> Y convertIfNecessary(Object value, Class<Y> requiredType) throws TypeMismatchException {
        return wrapper.convertIfNecessary(value, requiredType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <Y> Y convertIfNecessary(Object value, Class<Y> requiredType,
            MethodParameter methodParam) throws TypeMismatchException {
        return wrapper.convertIfNecessary(value, requiredType, methodParam);
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public <Y> Y convertIfNecessary(Object value, Class<Y> requiredType, Field field) throws TypeMismatchException {
        return wrapper.convertIfNecessary(value, requiredType, field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getPrimaryKeyValues() {
        Map<String, Object> primaryKeyValues = new HashMap<String, Object>();
		if (metadata != null) {
			List<String> primaryKeyAttributeNames = metadata.getPrimaryKeyAttributeNames();
			if (primaryKeyAttributeNames != null) {
				for (String primaryKeyAttributeName : primaryKeyAttributeNames) {
					primaryKeyValues.put(primaryKeyAttributeName, getPropertyValue(primaryKeyAttributeName));
				}
			}
		} else {
			LOG.warn("Attempt to retrieve PK fields on object with no metadata: " + dataObject.getClass().getName());
        }
        return primaryKeyValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPrimaryKeyValue() {
        if (!areAllPrimaryKeyAttributesPopulated()) {
            return null;
        }
        Map<String, Object> primaryKeyValues = getPrimaryKeyValues();
        if (getPrimaryKeyValues().size() == 1) {
            return primaryKeyValues.values().iterator().next();
        } else {
            return new CompoundKey(primaryKeyValues);
        }
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean areAllPrimaryKeyAttributesPopulated() {
		if (metadata != null) {
			List<String> primaryKeyAttributeNames = metadata.getPrimaryKeyAttributeNames();
			if (primaryKeyAttributeNames != null) {
				for (String primaryKeyAttributeName : primaryKeyAttributeNames) {
					Object propValue = getPropertyValue(primaryKeyAttributeName);
					if (propValue == null || (propValue instanceof String && StringUtils.isBlank((String) propValue))) {
						return false;
					}
				}
			}
			return true;
		} else {
			LOG.warn("Attempt to check areAllPrimaryKeyAttributesPopulated on object with no metadata: "
					+ dataObject.getClass().getName());
			return true;
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean areAnyPrimaryKeyAttributesPopulated() {
		if (metadata != null) {
			List<String> primaryKeyAttributeNames = metadata.getPrimaryKeyAttributeNames();
			if (primaryKeyAttributeNames != null) {
				for (String primaryKeyAttributeName : primaryKeyAttributeNames) {
					Object propValue = getPropertyValue(primaryKeyAttributeName);
					if (propValue instanceof String && StringUtils.isNotBlank((String) propValue)) {
						return true;
					} else if (propValue != null) {
						return true;
					}
				}
			}
			return false;
		} else {
			LOG.warn("Attempt to check areAnyPrimaryKeyAttributesPopulated on object with no metadata: "
					+ dataObject.getClass().getName());
			return true;
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public List<String> getUnpopulatedPrimaryKeyAttributeNames() {
		List<String> emptyKeys = new ArrayList<String>();
		if (metadata != null) {
			List<String> primaryKeyAttributeNames = metadata.getPrimaryKeyAttributeNames();
			if (primaryKeyAttributeNames != null) {
				for (String primaryKeyAttributeName : primaryKeyAttributeNames) {
					Object propValue = getPropertyValue(primaryKeyAttributeName);
					if (propValue == null || (propValue instanceof String && StringUtils.isBlank((String) propValue))) {
						emptyKeys.add(primaryKeyAttributeName);
					}
				}
			}
		} else {
			LOG.warn("Attempt to check getUnpopulatedPrimaryKeyAttributeNames on object with no metadata: "
					+ dataObject.getClass().getName());
		}
		return emptyKeys;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equalsByPrimaryKey(T object) {
        if (object == null) {
            return false;
        }
        DataObjectWrapper<T> wrap = dataObjectService.wrap(object);
        if (!getWrappedClass().isAssignableFrom(wrap.getWrappedClass())) {
            throw new IllegalArgumentException("The type of the given data object does not match the type of this " +
                    "data object. Given: " + wrap.getWrappedClass() + ", but expected: " + getWrappedClass());
        }
        // since they are the same type, we know they must have the same number of primary keys,
        Map<String, Object> localPks = getPrimaryKeyValues();
        Map<String, Object> givenPks = wrap.getPrimaryKeyValues();
        for (String localPk : localPks.keySet()) {
            Object localPkValue = localPks.get(localPk);
            if (localPkValue == null || !localPkValue.equals(givenPks.get(localPk))) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getForeignKeyValue(String relationshipName) {
        Object foreignKeyAttributeValue = getForeignKeyAttributeValue(relationshipName);
        if (foreignKeyAttributeValue != null) {
            return foreignKeyAttributeValue;
        }
        // if there are no attribute relationships, or the attribute relationships are not fully populated, fall
        // back to the actual relationship object
        Object relationshipObject = getPropertyValue(relationshipName);
        if (relationshipObject == null) {
            return null;
        }
        return dataObjectService.wrap(relationshipObject).getPrimaryKeyValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getForeignKeyAttributeValue(String relationshipName) {
		Map<String, Object> attributeMap = getForeignKeyAttributeMap(relationshipName);
		if (attributeMap == null) {
			return null;
		}
		return asSingleKey(attributeMap);
	}

    /**
     * Gets the map of child attribute names to the parent attribute values.
     *
     * @param relationshipName the name of the relationship for which to get the map.
     * @return the map of child attribute names to the parent attribute values.
     */
	public Map<String, Object> getForeignKeyAttributeMap(String relationshipName) {
		MetadataChild relationship = findAndValidateRelationship(relationshipName);
        List<DataObjectAttributeRelationship> attributeRelationships = relationship.getAttributeRelationships();

        if (!attributeRelationships.isEmpty()) {
            Map<String, Object> attributeMap = new LinkedHashMap<String, Object>();

            for (DataObjectAttributeRelationship attributeRelationship : attributeRelationships) {
                // obtain the property value on the current parent object
                String parentAttributeName = attributeRelationship.getParentAttributeName();
                Object parentAttributeValue = null;

                try {
                    parentAttributeValue = getPropertyValue(parentAttributeName);
                } catch (BeansException be) {
                    // exception thrown may be a db property which may not be defined on class (JPA foreign keys)
                    // use null value for parentAttributeValue
                }

                // not all of our relationships are populated, so we cannot obtain a valid foreign key
                if (parentAttributeValue == null) {
                    return null;
                }

                // store the mapping with the child attribute name to fetch on the referenced child object
                String childAttributeName = attributeRelationship.getChildAttributeName();
                attributeMap.put(childAttributeName, parentAttributeValue);
            }

            return attributeMap;
        }

        return null;
    }

    /**
     * Gets a single key from a map of keys, either by grabbing the first value from a map size of 1 or by creating a
     * {@link CompoundKey}.
     *
     * @param keyValues the map of keys to process.
     * @return a single key from a set map of keys.
     */
    private Object asSingleKey(Map<String, Object> keyValues) {
        if (keyValues.size() == 1) {
            return keyValues.values().iterator().next();
        }
        return new CompoundKey(keyValues);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getPropertyTypeNullSafe(Class<?> objectType, String propertyName) {
        DataObjectMetadata objectMetadata = dataObjectService.getMetadataRepository().getMetadata(objectType);
        return getPropertyTypeChild(objectMetadata,propertyName);
    }

    /**
     * Gets the property type for a property name.
     *
     * @param objectMetadata the metadata object.
     * @param propertyName the name of the property.
     * @return the property type for a property name.
     */
    private Class<?> getPropertyTypeChild(DataObjectMetadata objectMetadata, String propertyName){
        if(PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName)){
            String attributePrefix = StringUtils.substringBefore(propertyName,".");
            String attributeName = StringUtils.substringAfter(propertyName,".");

            if(StringUtils.isNotBlank(attributePrefix) && StringUtils.isNotBlank(attributeName) &&
                    objectMetadata!= null){
                Class<?> propertyType = traverseRelationship(objectMetadata,attributePrefix,attributeName);
                if(propertyType != null){
                    return propertyType;
                }
            }
        }
        return getPropertyType(propertyName);
    }

    /**
     * Gets the property type for a property name in a relationship.
     *
     * @param objectMetadata the metadata object.
     * @param attributePrefix the prefix of the property that indicated it was in a relationship.
     * @param attributeName the name of the property.
     * @return the property type for a property name.
     */
    private Class<?> traverseRelationship(DataObjectMetadata objectMetadata,String attributePrefix,
                                          String attributeName){
        DataObjectRelationship rd = objectMetadata.getRelationship(attributePrefix);
        if(rd != null){
            DataObjectMetadata relatedObjectMetadata =
                    dataObjectService.getMetadataRepository().getMetadata(rd.getRelatedType());
            if(relatedObjectMetadata != null){
                if(PropertyAccessorUtils.isNestedOrIndexedProperty(attributeName)){
                    return getPropertyTypeChild(relatedObjectMetadata,attributeName);
                } else{
                    if(relatedObjectMetadata.getAttribute(attributeName) == null &&
                            relatedObjectMetadata.getRelationship(attributeName)!=null){
                        DataObjectRelationship relationship = relatedObjectMetadata.getRelationship(attributeName);
                        return relationship.getRelatedType();
                    }
                    return relatedObjectMetadata.getAttribute(attributeName).getDataType().getType();
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void linkChanges(Set<String> changedPropertyPaths) {
        referenceLinker.linkChanges(getWrappedInstance(), changedPropertyPaths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void linkForeignKeys(boolean onlyLinkReadOnly) {
        linkForeignKeysInternalWrapped(this, onlyLinkReadOnly, Sets.newHashSet());
    }

    /**
     * Links all foreign keys on the data object.
     *
     * @param object the object to link.
     * @param onlyLinkReadOnly whether to only link read-only objects.
     * @param linked the set of currently linked objects, used as a base case to exit out of recursion.
     */
    protected void linkForeignKeysInternal(Object object, boolean onlyLinkReadOnly, Set<Object> linked) {
        if (object == null || linked.contains(object) || !dataObjectService.supports(object.getClass())) {
            return;
        }
        linked.add(object);
        DataObjectWrapper<?> wrapped = dataObjectService.wrap(object);
        linkForeignKeysInternalWrapped(wrapped, onlyLinkReadOnly, linked);
    }

    /**
     * Links all foreign keys on the wrapped data object.
     *
     * @param wrapped the wrapped object to link.
     * @param onlyLinkReadOnly whether to only link read-only objects.
     * @param linked the set of currently linked objects, used as a base case to exit out of recursion.
     */
    protected void linkForeignKeysInternalWrapped(DataObjectWrapper<?> wrapped, boolean onlyLinkReadOnly, Set<Object> linked) {
        List<DataObjectRelationship> relationships = wrapped.getMetadata().getRelationships();
        for (DataObjectRelationship relationship : relationships) {
            String relationshipName = relationship.getName();
            Object relationshipValue = wrapped.getPropertyValue(relationshipName);

            // let's get the current value and recurse down if it's a relationship that is cascaded on save
            if (relationship.isSavedWithParent()) {

                linkForeignKeysInternal(relationshipValue, onlyLinkReadOnly, linked);
            }

            // next, if we have related attributes, we need to link our keys
            linkForeignKeysInternal(wrapped, relationship, relationshipValue, onlyLinkReadOnly);
        }
        List<DataObjectCollection> collections = wrapped.getMetadata().getCollections();
        for (DataObjectCollection collection : collections) {
            String relationshipName = collection.getName();

            // let's get the current value and recurse down for each element if it's a collection that is cascaded on save
            if (collection.isSavedWithParent()) {
                Collection<?> collectionValue = (Collection<?>)wrapped.getPropertyValue(relationshipName);
                if (collectionValue != null) {
                    for (Object object : collectionValue) {
                        linkForeignKeysInternal(object, onlyLinkReadOnly, linked);
                    }
                }
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fetchRelationship(String relationshipName) {
        fetchRelationship(relationshipName, true, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fetchRelationship(String relationshipName, boolean useForeignKeyAttribute, boolean nullifyDanglingRelationship) {
        fetchRelationship(findAndValidateRelationship(relationshipName), useForeignKeyAttribute,
                nullifyDanglingRelationship);
    }
    /**
     * Fetches and populates the value for the relationship with the given name on the wrapped object.
     *
     * @param relationship the relationship on the wrapped data object to refresh
     * @param useForeignKeyAttribute whether to use the foreign key attribute to fetch the relationship
     * @param nullifyDanglingRelationship whether to set the related object to null if no relationship value is found
     */
	protected void fetchRelationship(MetadataChild relationship, boolean useForeignKeyAttribute, boolean nullifyDanglingRelationship) {
        Class<?> relatedType = relationship.getRelatedType();
        if (!dataObjectService.supports(relatedType)) {
            LOG.warn("Encountered a related type that is not supported by DataObjectService, fetch "
                    + "relationship will do nothing: " + relatedType);
            return;
        }
        // if we have at least one attribute relationships here, then we are set to proceed
        if (useForeignKeyAttribute) {
            fetchRelationshipUsingAttributes(relationship, nullifyDanglingRelationship);
        } else {
            fetchRelationshipUsingIdentity(relationship, nullifyDanglingRelationship);
        }
    }

    /**
     * Fetches the relationship using the foreign key attributes.
     *
     * @param relationship the relationship on the wrapped data object to refresh
     * @param nullifyDanglingRelationship whether to set the related object to null if no relationship value is found
     */
    protected void fetchRelationshipUsingAttributes(MetadataChild relationship, boolean nullifyDanglingRelationship) {
        Class<?> relatedType = relationship.getRelatedType();
        if (relationship.getAttributeRelationships().isEmpty()) {
            LOG.warn("Attempted to fetch a relationship using a foreign key attribute "
                    + "when one does not exist: "
                    + relationship.getName());
        } else {
            Object fetchedValue = null;
            if (relationship instanceof DataObjectRelationship) {
                Object foreignKey = getForeignKeyAttributeValue(relationship.getName());
                if (foreignKey != null) {
                    fetchedValue = dataObjectService.find(relatedType, foreignKey);
                }
            } else if (relationship instanceof DataObjectCollection) {
                Map<String, Object> foreignKeyAttributeMap = getForeignKeyAttributeMap(relationship.getName());
                fetchedValue = dataObjectService.findMatching(relatedType,
                        QueryByCriteria.Builder.andAttributes(foreignKeyAttributeMap).build()).getResults();
            }
            if (fetchedValue != null || nullifyDanglingRelationship) {
                setPropertyValue(relationship.getName(), fetchedValue);
            }
        }
    }

    /**
     * Fetches the relationship using the primary key attributes.
     *
     * @param relationship the relationship on the wrapped data object to refresh
     * @param nullifyDanglingRelationship whether to set the related object to null if no relationship value is found
     */
    protected void fetchRelationshipUsingIdentity(MetadataChild relationship, boolean nullifyDanglingRelationship) {
        Object propertyValue = getPropertyValue(relationship.getName());
        if (propertyValue != null) {
            if (!dataObjectService.supports(propertyValue.getClass())) {
                throw new IllegalArgumentException("Attempting to fetch an invalid relationship, must be a"
                        + "DataObjectRelationship when fetching without a foreign key");
            }
            DataObjectWrapper<?> wrappedRelationship = dataObjectService.wrap(propertyValue);
            Map<String, Object> primaryKeyValues = wrappedRelationship.getPrimaryKeyValues();
            Object newPropertyValue = dataObjectService.find(wrappedRelationship.getWrappedClass(),
                    new CompoundKey(primaryKeyValues));
            if (newPropertyValue != null || nullifyDanglingRelationship) {
                propertyValue = newPropertyValue;
                setPropertyValue(relationship.getName(), propertyValue);
            }
        }
        // now copy pk values back to the foreign key, because we are being explicity asked to fetch the relationship
        // using the identity and not the FK, we don't care about whether the FK field is read only or not so pass
        // "false" for onlyLinkReadOnly argument to linkForeignKeys
        linkForeignKeysInternal(this, relationship, propertyValue, false);
        populateInverseRelationship(relationship, propertyValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void linkForeignKeys(String relationshipName, boolean onlyLinkReadOnly) {
        MetadataChild relationship = findAndValidateRelationship(relationshipName);
        Object propertyValue = getPropertyValue(relationshipName);
        linkForeignKeysInternal(this, relationship, propertyValue, onlyLinkReadOnly);
    }

    /**
     * Links foreign keys non-recursively using the relationship with the given name on the wrapped data object.
     *
     * @param wrapped the wrapped object to link.
     * @param relationship the relationship on the wrapped data object for which to link foreign keys.
     * @param relationshipValue the value of the relationship.
     * @param onlyLinkReadOnly indicates whether or not only read-only foreign keys should be linked.
     */
    protected void linkForeignKeysInternal(DataObjectWrapper<?> wrapped, MetadataChild relationship,
            Object relationshipValue, boolean onlyLinkReadOnly) {
        if (!relationship.getAttributeRelationships().isEmpty()) {
            // this means there's a foreign key so we need to copy values back
            DataObjectWrapper<?> wrappedRelationship = null;
            if (relationshipValue != null) {
                wrappedRelationship = dataObjectService.wrap(relationshipValue);
            }
            for (DataObjectAttributeRelationship attributeRelationship : relationship.getAttributeRelationships()) {
                String parentAttributeName = attributeRelationship.getParentAttributeName();
                // if the property value is null, we need to copy null back to all parent foreign keys,
                // otherwise we copy back the actual value
                Object childAttributeValue = null;
                if (wrappedRelationship != null) {
                    childAttributeValue =
                            wrappedRelationship.getPropertyValue(attributeRelationship.getChildAttributeName());
                }
                if (onlyLinkReadOnly) {
                    DataObjectAttribute attribute = wrapped.getMetadata().getAttribute(parentAttributeName);
                    if (attribute.isReadOnly()) {
                        wrapped.setPropertyValue(parentAttributeName, childAttributeValue);
                    }
                } else {
                    wrapped.setPropertyValue(parentAttributeName, childAttributeValue);
                }
            }
        }
    }

    /**
     * Populates the property on the other side of the relationship.
     *
     * @param relationship the relationship on the wrapped data object for which to populate the inverse relationship.
     * @param propertyValue the value of the property.
     */
    protected void populateInverseRelationship(MetadataChild relationship, Object propertyValue) {
        if (propertyValue != null) {
            MetadataChild inverseRelationship = relationship.getInverseRelationship();
            if (inverseRelationship != null) {
                DataObjectWrapper<?> wrappedRelationship = dataObjectService.wrap(propertyValue);
                if (inverseRelationship instanceof DataObjectCollection) {
                    DataObjectCollection collectionRelationship = (DataObjectCollection)inverseRelationship;
                    String colRelName = inverseRelationship.getName();
                    Collection<Object> collection =
                            (Collection<Object>)wrappedRelationship.getPropertyValue(colRelName);
                    if (collection == null) {
                        // if the collection is null, let's instantiate an empty one
                        collection =
                                CollectionFactory.createCollection(wrappedRelationship.getPropertyType(colRelName), 1);
                        wrappedRelationship.setPropertyValue(colRelName, collection);
                    }
                    collection.add(getWrappedInstance());
                }
            }
        }
    }

    /**
     * Finds and validates the relationship specified by the given name.
     *
     * @param relationshipName the name of the relationship to find.
     * @return the found relationship.
     */
	private MetadataChild findAndValidateRelationship(String relationshipName) {
        if (StringUtils.isBlank(relationshipName)) {
            throw new IllegalArgumentException("The relationshipName must not be null or blank");
        }
        // validate the relationship exists
		MetadataChild relationship = getMetadata().getRelationship(relationshipName);
        if (relationship == null) {
			relationship = getMetadata().getCollection(relationshipName);
			if (relationship == null) {
				throw new IllegalArgumentException("Failed to locate a valid relationship from " + getWrappedClass()
						+ " with the given relationship name '" + relationshipName + "'");
			}
        }
        return relationship;
    }

}
