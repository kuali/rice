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
package org.kuali.rice.krad.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;
import org.kuali.rice.krad.util.LegacyUtils;

/**
 * LegacyDataAdapter implementation.
 *
 * @deprecated instead of using this class, where possible go directly against new KRAD code
 */
@Deprecated
public class LegacyDataAdapterImpl implements LegacyDataAdapter {

    private LegacyDataAdapter kradLegacyDataAdapter;
    private LegacyDataAdapter knsLegacyDataAdapter;

    @Override
    public <T> T save(T dataObject) {
        return selectAdapter(dataObject).save(dataObject);
    }

    @Override
    public <T> T linkAndSave(T dataObject) {
        return selectAdapter(dataObject).linkAndSave(dataObject);
    }

    @Override
    public <T> T saveDocument(T document) {
        return selectAdapter(document).saveDocument(document);
    }

    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
        return selectAdapter(clazz).findByPrimaryKey(clazz, primaryKeys);
    }

    @Override
    public <T> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
        return selectAdapter(clazz).findBySinglePrimaryKey(clazz, primaryKey);
    }

    @Override
    public void delete(Object dataObject) {
        selectAdapter(dataObject).delete(dataObject);
    }

    @Override
    public void deleteMatching(Class<?> type, Map<String, ?> fieldValues) {
        selectAdapter(type).deleteMatching(type, fieldValues);
    }

    @Override
    public <T> T retrieve(T dataObject) {
        return selectAdapter(dataObject).retrieve(dataObject);
    }

    @Override
    public <T> Collection<T> findAll(Class<T> clazz) {
        return selectAdapter(clazz).findAll(clazz);
    }

    @Override
    public <T> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
        return selectAdapter(clazz).findMatching(clazz, fieldValues);
    }

    @Override
    public <T> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField,
            boolean sortAscending) {
        return selectAdapter(clazz).findMatchingOrderBy(clazz, fieldValues, sortField, sortAscending);
    }

    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        return selectAdapter(dataObject).getPrimaryKeyFieldValues(dataObject);
    }

    @Override
    public void retrieveNonKeyFields(Object persistableObject) {
        selectAdapter(persistableObject).retrieveNonKeyFields(persistableObject);
    }

    @Override
    public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
        selectAdapter(persistableObject).retrieveReferenceObject(persistableObject, referenceObjectName);
    }

    @Override
    public void refreshAllNonUpdatingReferences(Object persistableObject) {
        selectAdapter(persistableObject).refreshAllNonUpdatingReferences(persistableObject);
    }

    @Override
    public boolean isProxied(Object object) {
        return selectAdapter(object).isProxied(object);
    }

    @Override
    public Object resolveProxy(Object object) {
        return selectAdapter(object).resolveProxy(object);
    }

    // Lookup methods

    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> dataObjectClass, Map<String, String> formProperties,
            boolean unbounded, boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        return selectAdapter(dataObjectClass).findCollectionBySearchHelper(dataObjectClass, formProperties, unbounded,
                allPrimaryKeyValuesPresentAndNotWildcard, searchResultsLimit);
    }

    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> dataObjectClass, Map<String, String> formProperties,
            List<String> wildcardAsLiteralPropertyNames, boolean unbounded,
            boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        return selectAdapter(dataObjectClass).findCollectionBySearchHelper(dataObjectClass, formProperties,
                wildcardAsLiteralPropertyNames, unbounded, allPrimaryKeyValuesPresentAndNotWildcard,
                searchResultsLimit);
    }

    @Override
    public <T> T findObjectBySearch(Class<T> type, Map<String, String> formProps) {
        return selectAdapter(type).findObjectBySearch(type, formProps);
    }

    @Override
    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps) {
        return selectAdapter(boClass).allPrimaryKeyValuesPresentAndNotWildcard(boClass, formProps);
    }

    @Override
    public List<String> listPrimaryKeyFieldNames(Class<?> type) {
        return selectAdapter(type).listPrimaryKeyFieldNames(type);
    }

    @Override
    public Class<?> determineCollectionObjectType(Class<?> containingType, String collectionPropertyName) {
        return selectAdapter(containingType).determineCollectionObjectType(containingType, collectionPropertyName);
    }

    @Override
    public boolean hasReference(Class<?> boClass, String referenceName) {
        return selectAdapter(boClass).hasReference(boClass, referenceName);
    }

    @Override
    public boolean hasCollection(Class<?> boClass, String collectionName) {
        return selectAdapter(boClass).hasCollection(boClass, collectionName);
    }

    @Override
    public boolean isExtensionAttribute(Class<?> boClass, String attributePropertyName, Class<?> propertyType) {
        return selectAdapter(boClass).isExtensionAttribute(boClass, attributePropertyName, propertyType);
    }

    @Override
    public Class<?> getExtensionAttributeClass(Class<?> boClass, String attributePropertyName) {
        return selectAdapter(boClass).getExtensionAttributeClass(boClass, attributePropertyName);
    }

    @Override
    public Map<String, ?> getPrimaryKeyFieldValuesDOMDS(Object dataObject) {
        return selectAdapter(dataObject).getPrimaryKeyFieldValuesDOMDS(dataObject);
    }

    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        return selectAdapter(do1).equalsByPrimaryKeys(do1, do2);
    }

    @Override
    public void materializeAllSubObjects(Object object) {
        selectAdapter(object).materializeAllSubObjects(object);
    }

    @Override
    public Class<?> getPropertyType(Object object, String propertyName) {
        return selectAdapter(object).getPropertyType(object, propertyName);
    }

    @Override
    public Object getExtension(
            Class<?> businessObjectClass) throws InstantiationException, IllegalAccessException {
        return selectAdapter(businessObjectClass).getExtension(businessObjectClass);
    }

    @Override
    public void refreshReferenceObject(Object businessObject, String referenceObjectName) {
        selectAdapter(businessObject).refreshReferenceObject(businessObject, referenceObjectName);
    }

    @Override
    public boolean isLockable(Object object) {
        return selectAdapter(object).isLockable(object);
    }

    @Override
    public void verifyVersionNumber(Object dataObject) {
        selectAdapter(dataObject).verifyVersionNumber(dataObject);
    }

    @Override
    public RemotableQuickFinder.Builder createQuickFinder(Class<?> containingClass, String attributeName) {
        return selectAdapter(containingClass).createQuickFinder(containingClass, attributeName);
    }

    @Override
    public boolean isReferenceUpdatable(Class<?> type, String referenceName) {
        return selectAdapter(type).isReferenceUpdatable(type, referenceName);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Class> listReferenceObjectFields(Class<?> type) {
        return selectAdapter(type).listReferenceObjectFields(type);
    }

    @Override
    public boolean isCollectionUpdatable(Class<?> type, String collectionName) {
        return selectAdapter(type).isCollectionUpdatable(type, collectionName);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map<String, Class> listCollectionObjectTypes(Class<?> type) {
        return selectAdapter(type).listCollectionObjectTypes(type);
    }

    @Override
    public Object getReferenceIfExists(Object bo, String referenceName) {
        return selectAdapter(bo).getReferenceIfExists(bo, referenceName);
    }

    @Override
    public boolean allForeignKeyValuesPopulatedForReference(Object bo, String referenceName) {
        return selectAdapter(bo).allForeignKeyValuesPopulatedForReference(bo, referenceName);
    }

    @Override
    public RelationshipDefinition getDictionaryRelationship(Class<?> c, String attributeName) {
        return selectAdapter(c).getDictionaryRelationship(c, attributeName);
    }

    @Override
    public String getTitleAttribute(Class<?> dataObjectClass) {
        return selectAdapter(dataObjectClass).getTitleAttribute(dataObjectClass);
    }

    @Override
    public boolean areNotesSupported(Class<?> dataObjectClass) {
        return selectAdapter(dataObjectClass).areNotesSupported(dataObjectClass);
    }

    @Override
    public String getDataObjectIdentifierString(Object dataObject) {
        return selectAdapter(dataObject).getDataObjectIdentifierString(dataObject);
    }

    @Override
    public Class<?> getInquiryObjectClassIfNotTitle(Object dataObject, String propertyName) {
        return selectAdapter(dataObject).getInquiryObjectClassIfNotTitle(dataObject, propertyName);
    }

    @Override
    public Map<String, String> getInquiryParameters(Object dataObject, List<String> keys, String propertyName) {
        return selectAdapter(dataObject).getInquiryParameters(dataObject, keys, propertyName);
    }

    @Override
    public boolean hasLocalLookup(Class<?> dataObjectClass) {
        return selectAdapter(dataObjectClass).hasLocalLookup(dataObjectClass);
    }

    @Override
    public boolean hasLocalInquiry(Class<?> dataObjectClass) {
        return selectAdapter(dataObjectClass).hasLocalInquiry(dataObjectClass);
    }

    @Override
    public org.kuali.rice.krad.bo.DataObjectRelationship getDataObjectRelationship(Object dataObject,
            Class<?> dataObjectClass, String attributeName, String attributePrefix, boolean keysOnly,
            boolean supportsLookup, boolean supportsInquiry) {
        return selectAdapter(dataObjectClass).getDataObjectRelationship(dataObject, dataObjectClass, attributeName,
                attributePrefix, keysOnly, supportsLookup, supportsInquiry);
    }

    @Override
    public boolean isPersistable(Class<?> dataObjectClass) {
        return selectAdapter(dataObjectClass).isPersistable(dataObjectClass);
    }

    @Override
    public ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(Object dataObject, String referenceName) {
        return selectAdapter(dataObject).getForeignKeyFieldsPopulationState(dataObject, referenceName);
    }

    @Override
    public Map<String, String> getForeignKeysForReference(Class<?> clazz, String attributeName) {
        return selectAdapter(clazz).getForeignKeysForReference(clazz, attributeName);
    }

    @Override
    public boolean hasPrimaryKeyFieldValues(Object dataObject) {
        return selectAdapter(dataObject).hasPrimaryKeyFieldValues(dataObject);
    }

    @Override
    public void setObjectPropertyDeep(Object bo, String propertyName, Class type,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        selectAdapter(bo).setObjectPropertyDeep(bo, propertyName, type, propertyValue);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class materializeClassForProxiedObject(Object object) {
        return selectAdapter(object).materializeClassForProxiedObject(object);
    }

    @Override
    public Object getNestedValue(Object bo, String fieldName) {
        return selectAdapter(bo).getNestedValue(bo, fieldName);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object createNewObjectFromClass(Class clazz) {
        return selectAdapter(clazz).createNewObjectFromClass(clazz);
    }

    @Override
    public boolean isNull(Object object) {
        return selectAdapter(object).isNull(object);
    }

    @Override
    public void setObjectProperty(Object bo, String propertyName, Class propertyType,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        selectAdapter(bo).setObjectProperty(bo, propertyName, propertyType, propertyValue);
    }

    @Override
    public <T extends Document> T findByDocumentHeaderId(Class<T> documentClass, String id) {
        return selectAdapter(documentClass).findByDocumentHeaderId(documentClass, id);
    }

    @Override
    public <T extends Document> List<T> findByDocumentHeaderIds(Class<T> documentClass, List<String> ids) {
        return selectAdapter(documentClass).findByDocumentHeaderIds(documentClass, ids);
    }

    protected LegacyDataAdapter selectAdapter(Object dataObject) {
        if (LegacyUtils.isKnsEnabled() && (dataObject instanceof Class) && LegacyUtils.useLegacy(
                (Class<?>) dataObject)) {
            return getKnsLegacyDataAdapter();
        } else if (LegacyUtils.isKnsEnabled() && !(dataObject instanceof Class) && LegacyUtils.useLegacyForObject(
                dataObject)) {
            return getKnsLegacyDataAdapter();
        } else {
            return getKradLegacyDataAdapter();
        }

    }

    public LegacyDataAdapter getKradLegacyDataAdapter() {
        return kradLegacyDataAdapter;
    }

    public void setKradLegacyDataAdapter(LegacyDataAdapter kradLegacyDataAdapter) {
        this.kradLegacyDataAdapter = kradLegacyDataAdapter;
    }

    public LegacyDataAdapter getKnsLegacyDataAdapter() {
        return knsLegacyDataAdapter;
    }

    public void setKnsLegacyDataAdapter(LegacyDataAdapter knsLegacyDataAdapter) {
        this.knsLegacyDataAdapter = knsLegacyDataAdapter;
    }

}

