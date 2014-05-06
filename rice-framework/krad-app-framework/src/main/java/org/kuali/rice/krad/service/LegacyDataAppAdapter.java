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
package org.kuali.rice.krad.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;

/**
 * Adapter that supports "legacy" KNS/KRAD persistence, metadata, and object utility frameworks via runtime
 * argument inspection
 *
 * @deprecated This class is deprecated by default, applications should *never*
 * use this adapter directly
 *
 * @author Kuali Rice Team (rice.collab@kuali.org).
 */
@Deprecated
public interface LegacyDataAppAdapter {

    // BusinessObjectService

    /**
     * Saves the passed in object or list of objects via the persistence layer.
     *
     * @param dataObject the data object to save
     */
    <T> T save(T dataObject);

    /**
     * Links up any contained objects, and then saves the passed in object via the persistence layer.
     *
     * @param dataObject the data object to link and save
     */
    <T> T linkAndSave(T dataObject);

    <T> T saveDocument(T document);

    /**
     * Retrieves an object instance identified by its primary key. For composite keys, use {@link #findByPrimaryKey(Class, Map)}
     *
     * @param clazz data object type class
     * @param primaryKey the primary key object
     * @return data object instance
     */
    <T> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey);

    /**
     * Retrieves an object instance identified by its primary keys and values. This can be done by constructing a map where the key
     * to the map entry is the primary key attribute and the value of the entry being the primary key value. For composite keys,
     * pass in each primaryKey attribute and its value as a map entry.
     *
     * @param clazz data object type class
     * @param primaryKeys map of String->Object key values
     * @return data object instance
     */
    <T> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys);

    /**
     * Deletes a data object from the database.
     *
     * @param dataObject the data object to delete
     */
    void delete(Object dataObject);

    /**
     * Deletes data objects from the database.
     *
     * @param clazz the entity type to delete
     * @param fieldValues map String->Object of field values to match
     */
    public void deleteMatching(Class<?> clazz, Map<String, ?> fieldValues);
    /**
     * Retrieves an object instance identified by the class of the given object and the object's primary key values.
     *
     * @param dataObject the data object
     * @return the retrieved data object or null if not found
     */
    <T> T retrieve(T dataObject);

    /**
     * This method retrieves a collection of data objects populated with data, such that each record in the database populates a
     * new object instance.This will only retrieve business objects by class type.
     *
     * @param clazz the data object type class
     * @return collection of all data objects of given type
     */
    <T> Collection<T> findAll(Class<T> clazz);

    /**
     * This method retrieves a collection of data objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve data objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name and its expected value.
     *
     * @param clazz the data object type class
     * @param fieldValues map String->Object of field values
     * @return collection of matching data objects
     */
    <T> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues);

    /**
     * This method retrieves a collection of business objects populated with data, such that each record in the database populates a
     * new object instance. This will retrieve business objects by class type and also by criteria passed in as key-value pairs,
     * specifically attribute name and its expected value. Performs an order by on sort field.
     *
     * @param clazz
     * @param fieldValues
     * @return collection of business objects
     */
    <T> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField, boolean sortAscending);
    // PersistenceService

    /**
     * This method retrieves the primary key field values for the given data object
     *
     * @param dataObject data object whose primary key field name,value pairs you want
     * @return a Map containing the names and values of primary key fields for the data object
     * @throws IllegalArgumentException if the given Object is null
     * @throws org.kuali.rice.krad.exception.ClassNotPersistableException if the given object is of a type not mapped in ORM
     */
    Map<String, ?> getPrimaryKeyFieldValues(Object dataObject);

    /**
     * @param persistableObject object whose objects need to be filled in based on primary keys
     * @throws IllegalArgumentException if the given Object is null
     * @throws org.kuali.rice.krad.exception.ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    void retrieveNonKeyFields(Object persistableObject);

    /**
     * @param persistableObject object whose specified reference object needs to be filled in based on primary keys
     * @param referenceObjectName the name of the reference object that will be filled in based on primary key values
     * @throws IllegalArgumentException if the given Object is null
     * @throws org.kuali.rice.krad.exception.ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    void retrieveReferenceObject(Object persistableObject, String referenceObjectName);

    /**
     * This method refreshes all reference objects to this main object that are 'non-updateable'. In general, this means that if a
     * reference object is configured to not be updated when the parent document is saved, then they are non-updated.
     * This will not refresh updateable objects, which can cause problems when you're creating new objects.
     * See PersistenceServiceImpl.isUpdateableReference() for the full logic.
     * @param persistableObject - the businessObject to be refreshed
     */
    void refreshAllNonUpdatingReferences(Object persistableObject);

    /**
     * Returns the object underlying any ORM proxy layers
     * @param o the object for which to determine the underlying non-ORM-proxy target
     * @return the underlying non-ORM-proxy object
     */
    Object resolveProxy(Object o);

    /**
     * Returns whether the object is an ORM proxy
     * @param object the candidate object
     * @return true if the object is an ORM proxy
     */
    boolean isProxied(Object object);

    // LookupService

    /**
     * Returns a collection of objects based on the given search parameters.
     * Search results are bounded by the KNS search results limit determined for the class.
     *
     * @param clazz the data object type
     * @param formProps field values for query
     * @param unbounded whether the search results should be bounded
     * @param allPrimaryKeyValuesPresentAndNotWildcard indicates whether or not the search only contains non-wildcarded primary key values
     * @param searchResultsLimit if the search is bounded, the search results limit, otherwise ignored. null is equivalent to KNS default for the clazz
     * @return collection of matching data objects
     * @deprecated please use {@link #findCollectionBySearchHelper(Class, java.util.Map, java.util.List, boolean, boolean, Integer)}  instead
     */
    @Deprecated
    <T> Collection<T> findCollectionBySearchHelper(Class<T> clazz, Map<String, String> formProps, boolean unbounded,
            boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit);

    /**
     * Returns a collection of objects based on the given search parameters.
     *
     * <p>
     * Search results are bounded by the KNS search results limit determined for the class.
     * This implementation further isolates the UIFramework from the LookupService and should be used
     * in place of the deprecated method.
     * </p>
     *
     * @param clazz the data object type
     * @param formProps field values for query
     * @param wildcardAsLiteralPropertyNames list of fields for query that do not allow wildcards
     * @param unbounded whether the search results should be bounded
     * @param allPrimaryKeyValuesPresentAndNotWildcard indicates whether or not the search only contains non-wildcarded primary key values
     * @param searchResultsLimit if the search is bounded, the search results limit, otherwise ignored. null is equivalent to KNS default for the clazz
     * @return collection of matching data objects
     */
    <T> Collection<T> findCollectionBySearchHelper(Class<T> clazz, Map<String, String> formProps,
            List<String> wildcardAsLiteralPropertyNames, boolean unbounded,
            boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit);

    /**
     * Retrieves a Object based on the search criteria, which should uniquely
     * identify a record.
     *
     * @param clazz the data object type
     * @param formProps field values for query
     * @return first object in returned search results, or null if none found
     */
    <T> T findObjectBySearch(Class<T> clazz, Map<String, String> formProps);

    boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps);

    // DAO interfaces are hoisted for krad or krad/kns shared services which still need to be preserved.
    // they will internally call LegacyDataAdapter

    // PersistenceStructureService

    /**
     * Returns a list of primary key field names for the given class.
     */
    List<String> listPrimaryKeyFieldNames(Class<?> clazz);

    /**
     * Determines the type of the collection object on the class with the collection with the given property name.
     *
     * @param containingClass the class of the object containing the collection
     * @param collectionPropertyName the name of the property identifying the collection
     *
     * @return the Class of elements in the identified collection
     *
     * @throws IllegalArgumentException if the given class is not available in metadata or if the given collection property name is incorrect
     */
    Class<?> determineCollectionObjectType(Class<?> containingClass, String collectionPropertyName);

    /**
     *
     * This method checks the foreign keys for a reference on a given BO, and tests that all fk fields are populated if any are
     * populated.
     *
     * In other words, for a given reference, it finds all the attributes of the BO that make up the foreign keys, and checks to see
     * if they all have values. It also keeps a list of all the fieldNames that do not have values.
     *
     * @param bo - A populated BusinessObject descendent. Must contain an attributed named referenceName.
     * @param referenceName - The name of the field that is a reference we are analyzing.
     * @return A populated ForeignKeyFieldsPopulation object which represents the state of population for the foreign key fields.
     */
    ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(Object bo, String referenceName);

    /**
     *
     * This method will return a Map of all the foreign key fields and the corresponding primary key fields for a given reference.
     *
     * The Map structure is: Key(String fkFieldName) => Value(String pkFieldName)
     *
     * @param clazz - Class that contains the named reference
     * @param attributeName - Name of the member that is the reference you want foreign keys for
     * @return returns a Map populated as described above, with one entry per foreign key field
     *
     */
    Map<String,String> getForeignKeysForReference(Class<?> clazz, String attributeName);

    /**
     * @param persistableObject
     * @return true if all primary key fields of the string have a non-null (and non-empty, for Strings) value
     * @throws IllegalArgumentException if the given Object is null
     * @throws org.kuali.rice.krad.exception.ClassNotPersistableException if the given object is of a type not described in the OJB repository
     */
    boolean hasPrimaryKeyFieldValues(Object persistableObject);

    /**
     * Returns whether there is a reference defined in the persistence layer with the given name.
     * Depending on the type of underlying persistence mechanism, this method may or may not return true
     * when the referenceName really refers to a collection type.
     *
     * To determine whether a reference is a collection, use the hasCollection method instead.
     *
     * In OJB, this method will return false for collection references.
     *
     * @param boClass
     * @param referenceName
     * @return true if the reference exists
     */
    boolean hasReference(Class<?> boClass, String referenceName);

    /**
     * Returns whether BOs of the given class have a collection defined within them with the given collection name.
     *
     * @param boClass
     * @param collectionName
     * @return true if a collection is defined
     */
    boolean hasCollection(Class<?> boClass, String collectionName);

    boolean isExtensionAttribute(Class<?> boClass, String attributePropertyName, Class<?> propertyType);

    Class<?> getExtensionAttributeClass(Class<?> boClass, String attributePropertyName);

    // DataObjectMetadataService

    /**
     * Determines the primary keys for the class of the given object, then for each
     * key field retrieves the value from the object instance and populates the return
     * map with the primary key name as the map key and the object value as the map value
     *
     * <p>Has DOMDS on the end because this is the version that delegates to DataObjectMetaDataService</p>
     *
     * @param dataObject - object whose primary key field name,value pairs you want
     * @return a Map containing the names and values of fields for the given class which
     *         are designated as key fields in the OJB repository file or DataDictionary
     * @throws IllegalArgumentException if the given Object is null
     */
    Map<String, ?> getPrimaryKeyFieldValuesDOMDS(Object dataObject);

    /**
     * Compares two dataObject instances for equality based on primary keys
     *
     * @param do1
     * @param do2
     * @return boolean indicating whether the two objects are equal.
     */
    boolean equalsByPrimaryKeys(Object do1, Object do2);

    // ObjectUtils

//    /**
//     * Casts the given object to a PersistableBusinessObject, checking first whether or not it is a valid
//     * PersistableBusinessObject.
//     *
//     * @param object the object to cast to a PersistableBusinessObject, must be non-null
//     * @return the PersistableBusinessObject for the given object, will never return null
//     * @throws IllegalArgumentException if the given object is null or does not represent a valid PersistableBusinessObject
//     */
//    PersistableBusinessObject toPersistableBusinessObject(Object object);

    /**
     * Materializes any references on the given object.
     *
     * @param object object to materialize
     */
    void materializeAllSubObjects(Object object);

    /**
     * Returns the type of the property with the given name on the supplied object. The property path may be nested.
     *
     * @param object the object against which to search
     * @param propertyName the (optionally nested) property name for which to locate the type
     * @return the type of the property
     * @throws RuntimeException if a problem occurred (i.e. bad property name), this exception will be different
     *         depending on whether the underlying implementation is calling legacy code or not
     */
    Class<?> getPropertyType(Object object, String propertyName);

    // PersistableBusinessObjectBase

    /**
     * Creates an instance of the extension for the given business object class.
     */
    Object getExtension(Class<?> businessObjectClass)
            throws InstantiationException, IllegalAccessException;

    /**
     * Refreshes the specified reference object on the given business object.
     */
    void refreshReferenceObject(Object businessObject, String referenceObjectName);

    // Misc...

    /**
     * Determines if the given ojbect is "lockable".
     *
     * @param object object for which to determine lockable
     * @return true if lockable, false otherwise
     */
    boolean isLockable(Object object);

    /**
     * Verifies that the version number of the given data object (if it has one) matches what is currently in the
     * database. If not, then the GlobalVariables message map will be updated with an error message and a
     * ValidationException will be thrown.
     *
     * <p>If this particular data object does not have versioning, this method will do nothing.</p>
     *
     * @param dataObject the data object to check the version number for
     *
     * @throws org.kuali.rice.krad.exception.ValidationException if the version number doesn't match
     */
    void verifyVersionNumber(Object dataObject);

    /**
     * Returns the builder for a remotable quick finder for the given attribute name on the given containing class.
     *
     * @param containingClass the class on which to locate the attribute
     * @param attributeName the name of the attribute for which to build the quickfinder on the specified containing class
     * @return the remotable quickfinder, or null if no such finder could be created
     */
    RemotableQuickFinder.Builder createQuickFinder(Class<?> containingClass, String attributeName);

    boolean isReferenceUpdatable(Class<?> boClass, String referenceName);

    /**
     *
     * This method uses the persistence layer to determine the list of reference objects contained within this parent object. For
     * example, an Account object contains sub-objects such as Chart, as well as the key that connects the two, String
     * chartOfAccountsCode.
     *
     * The return structure is: Map<referenceName, referenceClass>.
     *
     * As an example, an Account object passed into this would return:
     *
     * 0:['chartOfAccounts', org.kuali.module.chart.bo.Chart] 1:['organization', org.kuali.module.chart.bo.Org] etc.
     *
     * @param boClass Class that would like to be analyzed for reference names
     * @return Map containing the reference name for the key as a string, and the class of the reference as the value. If the object
     *         contains no references, then this Map will be empty.
     *
     */
    Map<String, Class> listReferenceObjectFields(Class<?> boClass);

    boolean isCollectionUpdatable(Class<?> boClass, String collectionName);

    Map<String, Class> listCollectionObjectTypes(Class<?> boClass);

    /**
     *
     * This method attempts to retrieve the reference from a BO if it exists.
     *
     * @param bo - populated BusinessObject instance that includes the referenceName property
     * @param referenceName - name of the member/property to load
     * @return A populated object from the DB, if it exists
     *
     */
    Object getReferenceIfExists(Object bo, String referenceName);

    /**
     *
     * This method examines whether all the foreign key fields for the specified reference contain values.
     *
     * @param bo
     * @param referenceName
     * @return true if they all are accessible and have values, false otherwise
     *
     */
    boolean allForeignKeyValuesPopulatedForReference(Object bo, String referenceName);

    /**
     * This method gets the title attribute from the datadictionary for the given data object class
     * @param dataObjectClass
     * @return title if available, otherwise null
     */
    String getTitleAttribute(Class<?> dataObjectClass);

    /**
     *
     * @param dataObjectClass
     * @return  true is supported, otherwise false
     */
    boolean areNotesSupported(Class<?> dataObjectClass);

    /**
     * Gets the identifier for a data object
     * @param dataObject data object
     * @return data object identifier
     */
    String getDataObjectIdentifierString(Object dataObject);

    /**
     * Get Inquiry class if not the title attribute
     * @param dataObject
     * @param propertyName
     * @return class that represents the inquiry object class, null otherwise
     */
    Class<?> getInquiryObjectClassIfNotTitle(Object dataObject, String propertyName);

    /**
     * Get Inquiry parameters for given keys for data object/property name
     * @param dataObject
     * @param keys
     * @param propertyName
     * @return map of key value pairs, empty map otherwise
     */
    Map<String,String> getInquiryParameters(Object dataObject, List<String> keys, String propertyName);


    /**
     * Determines whether the given data object class has an associated lookup in the local
     * running application
     *
     * @param dataObjectClass data object class to find lookup for
     * @return boolean true if a lookup exists for the data object class, false if not
     */
    boolean hasLocalLookup(Class<?> dataObjectClass);

    /**
     * Determines whether the given data object class has an associated inquiry in the local
     * running application
     *
     * @param dataObjectClass data object class to find inquiry for
     * @return boolean true if a inquiry exists for the data object class, false if not
     */
    boolean hasLocalInquiry(Class<?> dataObjectClass);

    /**
     * Attempts to find a relationship for the given attribute within the given
     * data object
     *
     * <p>
     * First the data dictionary is queried to find any relationship definitions
     * setup that include the attribute, if found the
     * <code>BusinessObjectRetationship</code> is build from that. If not and
     * the data object class is persistent, relationships are retrieved from the
     * persistence service. Nested attributes are handled in addition to
     * external business objects. If multiple relationships are found, the one
     * that contains the least amount of joining keys is returned
     * </p>
     *
     * @param dataObject - data object instance that contains the attribute
     * @param dataObjectClass - class for the data object that contains the attribute
     * @param attributeName - property name for the attribute
     * @param attributePrefix - property prefix for the attribute
     * @param keysOnly - indicates whether only primary key fields should be returned
     * in the relationship
     * @param supportsLookup - indicates whether the relationship should support lookup
     * @param supportsInquiry - indicates whether the relationship should support inquiry
     * @return BusinessObjectRelationship for the attribute, or null if not
     *         found
     */
    DataObjectRelationship getDataObjectRelationship(Object dataObject, Class<?> dataObjectClass,
            String attributeName, String attributePrefix, boolean keysOnly, boolean supportsLookup,
            boolean supportsInquiry);


    /**
     * Determines if a class is persistable
     *
     * @param dataObjectClass - data object instance that contains the attribute
     * @return true if the given Class is persistable (is known to OJB or JPA)
     */
    boolean isPersistable(Class<?> dataObjectClass);

    /**
     * Recursive; sets all occurences of the property in the object, its nested objects and its object lists with the
     * given value.
     *
     * @param bo
     * @param propertyName
     * @param type
     * @param propertyValue
     * @throws NoSuchMethodException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws IllegalAccessException
     */
    void setObjectPropertyDeep(Object bo, String propertyName, Class<?> type,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    /**
     * Attempts to find the Class for the given potentially proxied object
     *
     * @param object the potentially proxied object to find the Class of
     * @return the best Class which could be found for the given object
     */
    Class<?> materializeClassForProxiedObject(Object object);

    /**
     * This method safely extracts either simple values OR nested values. For example, if the bo is SubAccount, and the
     * fieldName is
     * a21SubAccount.subAccountTypeCode, this thing makes sure it gets the value off the very end attribute, no matter
     * how deeply
     * nested it is. The code would be slightly simpler if this was done recursively, but this is safer, and consumes a
     * constant
     * amount of memory, no matter how deeply nested it goes.
     *
     * @param bo
     * @param fieldName
     * @return The field value if it exists. If it doesnt, and the name is invalid, and
     */
    Object getNestedValue(Object bo, String fieldName);

    /**
     * This method safely creates a object from a class
     * Convenience method to create new object and throw a runtime exception if it cannot
     * If the class is an {@link org.kuali.rice.krad.bo.ExternalizableBusinessObject}, this method will determine the interface for the EBO and
     * query the
     * appropriate module service to create a new instance.
     *
     * @param clazz
     * @return a newInstance() of clazz
     */
    Object createNewObjectFromClass(Class clazz);

    /**
     * This method is a OJB Proxy-safe way to test for null on a proxied object that may or may not be materialized yet.
     * It is safe
     * to use on a proxy (materialized or non-materialized) or on a non-proxy (ie, regular object). Note that this will
     * force a
     * materialization of the proxy if the object is a proxy and unmaterialized.
     *
     * @param object - any object, proxied or not, materialized or not
     * @return true if the object (or underlying materialized object) is null, false otherwise
     */
    boolean isNull(Object object);

    /**
     * Sets the property of an object with the given value. Converts using the formatter of the given type if one is
     * found.
     *
     * @param bo
     * @param propertyName
     * @param propertyType
     * @param propertyValue
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    void setObjectProperty(Object bo, String propertyName, Class propertyType,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

}