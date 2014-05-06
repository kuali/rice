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
package org.kuali.rice.krad.uif.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.uif.RemotableQuickFinder.Builder;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;

/**
 * Mock implementation of {@link LegacyDataAdapter} for supporting UIF unit tests.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockLegacyDataAdapter implements LegacyDataAdapter {

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#save(java.lang.Object)
     */
    @Override
    public <T> T save(T dataObject) {
        return dataObject;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#linkAndSave(java.lang.Object)
     */
    @Override
    public <T> T linkAndSave(T dataObject) {
        return dataObject;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#saveDocument(java.lang.Object)
     */
    @Override
    public <T> T saveDocument(T document) {
        return document;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findBySinglePrimaryKey(java.lang.Class, java.lang.Object)
     */
    @Override
    public <T> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findByPrimaryKey(java.lang.Class, java.util.Map)
     */
    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#delete(java.lang.Object)
     */
    @Override
    public void delete(Object dataObject) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#deleteMatching(java.lang.Class, java.util.Map)
     */
    @Override
    public void deleteMatching(Class<?> clazz, Map<String, ?> fieldValues) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#retrieve(java.lang.Object)
     */
    @Override
    public <T> T retrieve(T dataObject) {
        return dataObject;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findAll(java.lang.Class)
     */
    @Override
    public <T> Collection<T> findAll(Class<T> clazz) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findMatching(java.lang.Class, java.util.Map)
     */
    @Override
    public <T> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findMatchingOrderBy(java.lang.Class, java.util.Map,
     * java.lang.String, boolean)
     */
    @Override
    public <T> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField,
            boolean sortAscending) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getPrimaryKeyFieldValues(java.lang.Object)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        return Collections.emptyMap();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#retrieveNonKeyFields(java.lang.Object)
     */
    @Override
    public void retrieveNonKeyFields(Object persistableObject) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#retrieveReferenceObject(java.lang.Object, java.lang.String)
     */
    @Override
    public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#refreshAllNonUpdatingReferences(java.lang.Object)
     */
    @Override
    public void refreshAllNonUpdatingReferences(Object persistableObject) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#resolveProxy(java.lang.Object)
     */
    @Override
    public Object resolveProxy(Object o) {
        return o;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#isProxied(java.lang.Object)
     */
    @Override
    public boolean isProxied(Object object) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findCollectionBySearchHelper(java.lang.Class, java.util.Map,
     * boolean, boolean, java.lang.Integer)
     */
    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> clazz, Map<String, String> formProps,
            boolean unbounded, boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findCollectionBySearchHelper(java.lang.Class, java.util.Map,
     * java.util.List, boolean, boolean, java.lang.Integer)
     */
    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> clazz, Map<String, String> formProps,
            List<String> wildcardAsLiteralPropertyNames, boolean unbounded,
            boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findObjectBySearch(java.lang.Class, java.util.Map)
     */
    @Override
    public <T> T findObjectBySearch(Class<T> clazz, Map<String, String> formProps) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#allPrimaryKeyValuesPresentAndNotWildcard(java.lang.Class,
     * java.util.Map)
     */
    @Override
    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#listPrimaryKeyFieldNames(java.lang.Class)
     */
    @Override
    public List<String> listPrimaryKeyFieldNames(Class<?> clazz) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#determineCollectionObjectType(java.lang.Class,
     * java.lang.String)
     */
    @Override
    public Class<?> determineCollectionObjectType(Class<?> containingClass, String collectionPropertyName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getForeignKeyFieldsPopulationState(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(Object bo, String referenceName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getForeignKeysForReference(java.lang.Class, java.lang.String)
     */
    @Override
    public Map<String, String> getForeignKeysForReference(Class<?> clazz, String attributeName) {
        return Collections.emptyMap();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#hasPrimaryKeyFieldValues(java.lang.Object)
     */
    @Override
    public boolean hasPrimaryKeyFieldValues(Object persistableObject) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#hasReference(java.lang.Class, java.lang.String)
     */
    @Override
    public boolean hasReference(Class<?> boClass, String referenceName) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#hasCollection(java.lang.Class, java.lang.String)
     */
    @Override
    public boolean hasCollection(Class<?> boClass, String collectionName) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#isExtensionAttribute(java.lang.Class, java.lang.String,
     * java.lang.Class)
     */
    @Override
    public boolean isExtensionAttribute(Class<?> boClass, String attributePropertyName, Class<?> propertyType) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getExtensionAttributeClass(java.lang.Class, java.lang.String)
     */
    @Override
    public Class<?> getExtensionAttributeClass(Class<?> boClass, String attributePropertyName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getPrimaryKeyFieldValuesDOMDS(java.lang.Object)
     */
    @Override
    public Map<String, ?> getPrimaryKeyFieldValuesDOMDS(Object dataObject) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#equalsByPrimaryKeys(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        return false;
    }

//    /**
//     * @see org.kuali.rice.krad.service.LegacyDataAdapter#toPersistableBusinessObject(java.lang.Object)
//     */
//    @Override
//    public PersistableBusinessObject toPersistableBusinessObject(Object object) {
//        return null;
//    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#materializeAllSubObjects(java.lang.Object)
     */
    @Override
    public void materializeAllSubObjects(Object object) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getPropertyType(java.lang.Object, java.lang.String)
     */
    @Override
    public Class<?> getPropertyType(Object object, String propertyName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getExtension(java.lang.Class)
     */
    @Override
    public Object getExtension(
            Class<?> businessObjectClass) throws InstantiationException, IllegalAccessException {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#refreshReferenceObject(org.kuali.rice.krad.bo.PersistableBusinessObject,
     * java.lang.String)
     */
    @Override
    public void refreshReferenceObject(Object businessObject, String referenceObjectName) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#isLockable(java.lang.Object)
     */
    @Override
    public boolean isLockable(Object object) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#verifyVersionNumber(java.lang.Object)
     */
    @Override
    public void verifyVersionNumber(Object dataObject) {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#createQuickFinder(java.lang.Class, java.lang.String)
     */
    @Override
    public Builder createQuickFinder(Class<?> containingClass, String attributeName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#isReferenceUpdatable(java.lang.Class, java.lang.String)
     */
    @Override
    public boolean isReferenceUpdatable(Class<?> boClass, String referenceName) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#listReferenceObjectFields(java.lang.Class)
     */
    @Override
    public Map<String, Class> listReferenceObjectFields(Class<?> boClass) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#isCollectionUpdatable(java.lang.Class, java.lang.String)
     */
    @Override
    public boolean isCollectionUpdatable(Class<?> boClass, String collectionName) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#listCollectionObjectTypes(java.lang.Class)
     */
    @Override
    public Map<String, Class> listCollectionObjectTypes(Class<?> boClass) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getReferenceIfExists(java.lang.Object, java.lang.String)
     */
    @Override
    public BusinessObject getReferenceIfExists(Object bo, String referenceName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#allForeignKeyValuesPopulatedForReference(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public boolean allForeignKeyValuesPopulatedForReference(Object bo, String referenceName) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getDictionaryRelationship(java.lang.Class, java.lang.String)
     */
    @Override
    public RelationshipDefinition getDictionaryRelationship(Class<?> c, String attributeName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getTitleAttribute(java.lang.Class)
     */
    @Override
    public String getTitleAttribute(Class<?> dataObjectClass) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#areNotesSupported(java.lang.Class)
     */
    @Override
    public boolean areNotesSupported(Class<?> dataObjectClass) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getDataObjectIdentifierString(java.lang.Object)
     */
    @Override
    public String getDataObjectIdentifierString(Object dataObject) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getInquiryObjectClassIfNotTitle(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Class<?> getInquiryObjectClassIfNotTitle(Object dataObject, String propertyName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getInquiryParameters(java.lang.Object, java.util.List,
     * java.lang.String)
     */
    @Override
    public Map<String, String> getInquiryParameters(Object dataObject, List<String> keys, String propertyName) {
        return Collections.emptyMap();
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#hasLocalLookup(java.lang.Class)
     */
    @Override
    public boolean hasLocalLookup(Class<?> dataObjectClass) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#hasLocalInquiry(java.lang.Class)
     */
    @Override
    public boolean hasLocalInquiry(Class<?> dataObjectClass) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getDataObjectRelationship(java.lang.Object, java.lang.Class,
     * java.lang.String, java.lang.String, boolean, boolean, boolean)
     */
    @Override
    public DataObjectRelationship getDataObjectRelationship(Object dataObject, Class<?> dataObjectClass,
            String attributeName, String attributePrefix, boolean keysOnly, boolean supportsLookup,
            boolean supportsInquiry) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#isPersistable(java.lang.Class)
     */
    @Override
    public boolean isPersistable(Class<?> dataObjectClass) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#setObjectPropertyDeep(java.lang.Object, java.lang.String,
     * java.lang.Class, java.lang.Object)
     */
    @Override
    public void setObjectPropertyDeep(Object bo, String propertyName, Class<?> type,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#materializeClassForProxiedObject(java.lang.Object)
     */
    @Override
    public Class<?> materializeClassForProxiedObject(Object object) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#getNestedValue(java.lang.Object, java.lang.String)
     */
    @Override
    public Object getNestedValue(Object bo, String fieldName) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#createNewObjectFromClass(java.lang.Class)
     */
    @Override
    public Object createNewObjectFromClass(Class clazz) {
        Object object = null;

        try {
            object = clazz.newInstance();
        } catch (InstantiationException e) {
            // do nothing
        } catch (IllegalAccessException e) {
            // do nothing
        }

        return object;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#isNull(java.lang.Object)
     */
    @Override
    public boolean isNull(Object object) {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#setObjectProperty(java.lang.Object, java.lang.String,
     * java.lang.Class, java.lang.Object)
     */
    @Override
    public void setObjectProperty(Object bo, String propertyName, Class propertyType,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findByDocumentHeaderId(java.lang.Class, java.lang.String)
     */
    @Override
    public <T extends Document> T findByDocumentHeaderId(Class<T> documentClass, String id) {
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#findByDocumentHeaderIds(java.lang.Class, java.util.List)
     */
    @Override
    public <T extends Document> List<T> findByDocumentHeaderIds(Class<T> documentClass, List<String> ids) {
        return Collections.emptyList();
    }

}
