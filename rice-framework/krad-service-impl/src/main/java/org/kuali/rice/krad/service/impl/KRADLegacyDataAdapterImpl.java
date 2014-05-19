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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.OrderByField;
import org.kuali.rice.core.api.criteria.OrderDirection;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.InactivatableFromTo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;
import org.kuali.rice.krad.data.metadata.DataObjectCollection;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.provider.annotation.ExtensionFor;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.SupportAttributeDefinition;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentAdHocService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.service.ViewDictionaryService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.LegacyUtils;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

/**
 *
 */
public class KRADLegacyDataAdapterImpl implements LegacyDataAdapter {
    private DataObjectService dataObjectService;
    private LookupCriteriaGenerator lookupCriteriaGenerator;

    private ConfigurationService kualiConfigurationService;
    private KualiModuleService kualiModuleService;
    private DataDictionaryService dataDictionaryService;
    private ViewDictionaryService viewDictionaryService;

    @Override
    public <T> T save(T dataObject) {
        if (dataObject instanceof Collection) {
            Collection<Object> newList = new ArrayList<Object>(((Collection) dataObject).size());
            for (Object obj : (Collection<?>) dataObject) {
                newList.add(save(obj));
            }
            return (T) newList;
        } else {
            return dataObjectService.save(dataObject);
        }
    }

    @Override
    public <T> T linkAndSave(T dataObject) {
        // This method is only used from MaintainableImpl
        return dataObjectService.save(dataObject, PersistenceOption.LINK_KEYS);
    }

    @Override
    public <T> T saveDocument(T document) {
        return dataObjectService.save(document, PersistenceOption.LINK_KEYS, PersistenceOption.FLUSH);
    }

    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
        return dataObjectService.find(clazz, new CompoundKey(primaryKeys));
    }

    @Override
    public <T> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
        return dataObjectService.find(clazz, primaryKey);
    }

    @Override
    public void delete(Object dataObject) {
        if (dataObject instanceof Collection) {
            for (Object dobj : (Collection) dataObject) {
                delete(dobj);
            }
        } else {
            dataObjectService.delete(dataObject);
        }
    }

    @Override
    public void deleteMatching(Class<?> type, Map<String, ?> fieldValues) {
        dataObjectService.deleteMatching(type, QueryByCriteria.Builder.andAttributes(fieldValues).build());
    }

    @Override
    public <T> T retrieve(T dataObject) {
        Object id = null;
        Map<String, Object> primaryKeyValues = dataObjectService.wrap(dataObject).getPrimaryKeyValues();
        if (primaryKeyValues.isEmpty()) {
            throw new IllegalArgumentException("Given data object has no primary key!");
        }
        if (primaryKeyValues.size() == 1) {
            id = primaryKeyValues.values().iterator().next();
        } else {
            id = new CompoundKey(primaryKeyValues);
        }
        return dataObjectService.find((Class<T>) dataObject.getClass(), id);
    }

    @Override
    public <T> Collection<T> findAll(Class<T> clazz) {
        // just find all objects of given type without any attribute criteria
        return findMatching(clazz, Collections.<String, Object>emptyMap());
    }

    @Override
    public <T> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
        QueryResults<T> result = dataObjectService.findMatching(clazz, QueryByCriteria.Builder.andAttributes(
                fieldValues).build());
        return result.getResults();
    }

    @Override
    public <T> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField,
            boolean sortAscending) {
        OrderDirection direction = sortAscending ? OrderDirection.ASCENDING : OrderDirection.DESCENDING;
        OrderByField orderBy = OrderByField.Builder.create(sortField, direction).build();
        QueryResults<T> result = dataObjectService.findMatching(clazz, QueryByCriteria.Builder.andAttributes(
                fieldValues).setOrderByFields(orderBy).build());
        return result.getResults();
    }

    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        return dataObjectService.wrap(dataObject).getPrimaryKeyValues();
    }

    @Override
    public void retrieveNonKeyFields(Object persistableObject) {
        List<DataObjectRelationship> relationships = dataObjectService.getMetadataRepository().getMetadata(
                persistableObject.getClass()).getRelationships();
        for (DataObjectRelationship relationship : relationships) {
            retrieveReferenceObject(persistableObject, relationship.getName());
        }
    }

    @Override
    public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
        dataObjectService.wrap(persistableObject).fetchRelationship(referenceObjectName);
    }

    @Override
    public void refreshAllNonUpdatingReferences(Object persistableObject) {
        List<DataObjectRelationship> nonUpdateableRelationships = findNonUpdateableRelationships(persistableObject);
        for (DataObjectRelationship relationship : nonUpdateableRelationships) {
            retrieveReferenceObject(persistableObject, relationship.getName());
        }
    }

    protected List<DataObjectRelationship> findNonUpdateableRelationships(Object persistableObject) {
        List<DataObjectRelationship> nonUpdateableRelationships = new ArrayList<DataObjectRelationship>();
        DataObjectMetadata dataObjectMetadata = dataObjectService.getMetadataRepository().
                getMetadata(persistableObject.getClass());
        if (dataObjectMetadata != null) {
            List<DataObjectRelationship> relationships = dataObjectMetadata.getRelationships();
            for (DataObjectRelationship relationship : relationships) {
                if (!relationship.isSavedWithParent()) {
                    nonUpdateableRelationships.add(relationship);
                }
            }
        }
        return nonUpdateableRelationships;
    }

    @Override
    public boolean isProxied(Object object) {
        // KRAD data adapter does nothing
        return false;
    }

    @Override
    public Object resolveProxy(Object o) {
        // KRAD data adapter does nothing
        return o;
    }

    // Lookup methods

    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> dataObjectClass, Map<String, String> formProperties,
            boolean unbounded, boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        return performDataObjectServiceLookup(dataObjectClass, formProperties, unbounded,
                allPrimaryKeyValuesPresentAndNotWildcard, searchResultsLimit);
    }

    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> dataObjectClass, Map<String, String> formProperties,
            List<String> wildcardAsLiteralPropertyNames, boolean unbounded,
            boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        return performDataObjectServiceLookup(dataObjectClass, formProperties, wildcardAsLiteralPropertyNames,
                unbounded, allPrimaryKeyValuesPresentAndNotWildcard, searchResultsLimit);
    }

    /**
     * Our new DataObjectService-based lookup implementation
     *
     * @param dataObjectClass the dataobject class
     * @param formProperties the incoming lookup form properties
     * @param unbounded whether the search is unbounded
     * @param searchResultsLimit the searchResultsLimit; null implies use of default KNS value if set for the class
     * @param <T> the data object type
     * @return collection of lookup results
     */
    protected <T> Collection<T> performDataObjectServiceLookup(Class<T> dataObjectClass,
            Map<String, String> formProperties, boolean unbounded, boolean allPrimaryKeyValuesPresentAndNotWildcard,
            Integer searchResultsLimit) {
        if (!unbounded && searchResultsLimit == null) {
            // use KRAD LookupUtils.getSearchResultsLimit instead of KNS version. we have no LookupForm, so pass null, only the class will be used
            //searchResultsLimit = LookupUtils.getSearchResultsLimit(example, null);
            searchResultsLimit = org.kuali.rice.kns.lookup.LookupUtils.getSearchResultsLimit(dataObjectClass);
        }
        QueryByCriteria.Builder query = lookupCriteriaGenerator.generateCriteria(dataObjectClass, formProperties,
                allPrimaryKeyValuesPresentAndNotWildcard);
        if (!unbounded && searchResultsLimit != null) {
            query.setMaxResults(searchResultsLimit);
        }

        Collection<T> results = dataObjectService.findMatching(dataObjectClass, query.build()).getResults();
        return filterCurrentDataObjects(dataObjectClass, results, formProperties);
    }

    /**
     * Our newer DataObjectService-based lookup implementation
     *
     * @param dataObjectClass the dataobject class
     * @param formProperties the incoming lookup form properties
     * @param wildcardAsLiteralPropertyNames list of the lookup properties with wildcard characters disabled
     * @param unbounded whether the search is unbounded
     * @param searchResultsLimit the searchResultsLimit; null implies use of default KNS value if set for the class
     * @param <T> the data object type
     * @return collection of lookup results
     */
    protected <T> Collection<T> performDataObjectServiceLookup(Class<T> dataObjectClass,
            Map<String, String> formProperties, List<String> wildcardAsLiteralPropertyNames, boolean unbounded,
            boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        if (!unbounded && searchResultsLimit == null) {
            // use KRAD LookupUtils.getSearchResultsLimit instead of KNS version. we have no LookupForm, so pass null, only the class will be used
            //searchResultsLimit = LookupUtils.getSearchResultsLimit(example, null);
            searchResultsLimit = org.kuali.rice.kns.lookup.LookupUtils.getSearchResultsLimit(dataObjectClass);
        }

        QueryByCriteria.Builder query = lookupCriteriaGenerator.generateCriteria(dataObjectClass, formProperties,
                wildcardAsLiteralPropertyNames, allPrimaryKeyValuesPresentAndNotWildcard);
        if (!unbounded && searchResultsLimit != null) {
            query.setMaxResults(searchResultsLimit);
        }

        Collection<T> results = dataObjectService.findMatching(dataObjectClass, query.build()).getResults();
        return filterCurrentDataObjects(dataObjectClass, results, formProperties);
    }

    protected <T> Collection<T> filterCurrentDataObjects(Class<T> dataObjectClass, Collection<T> unfiltered,
            Map<String, String> formProps) {
        if (InactivatableFromTo.class.isAssignableFrom(dataObjectClass)) {
            Boolean currentSpecifier = lookupCriteriaCurrentSpecifier(formProps);
            if (currentSpecifier != null) {
                List<InactivatableFromTo> onlyCurrent =
                        KRADServiceLocator.getInactivateableFromToService().filterOutNonCurrent(new ArrayList(
                                unfiltered), new Date(LookupUtils.getActiveDateTimestampForCriteria(formProps)
                                .getTime()));
                if (currentSpecifier) {
                    return (Collection<T>) onlyCurrent;
                } else {
                    unfiltered.removeAll(onlyCurrent);
                    return unfiltered;
                }
            }
        }
        return unfiltered;
    }

    protected Boolean lookupCriteriaCurrentSpecifier(Map<String, String> formProps) {
        String value = formProps.get(KRADPropertyConstants.CURRENT);
        if (StringUtils.isNotBlank(value)) {
            // FIXME: use something more portable than this direct OJB converter
            String currentBooleanStr = (String) new OjbCharBooleanConversion().javaToSql(value);
            if (OjbCharBooleanConversion.DATABASE_BOOLEAN_TRUE_STRING_REPRESENTATION.equals(currentBooleanStr)) {
                return Boolean.TRUE;
            } else if (OjbCharBooleanConversion.DATABASE_BOOLEAN_FALSE_STRING_REPRESENTATION.equals(
                    currentBooleanStr)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }

    @Override
    public <T> T findObjectBySearch(Class<T> type, Map<String, String> formProps) {
        // This is the strictly Lookup-compatible way of constructing the criteria
        // from a map of properties, which performs some minor logic such as only including
        // non-blank and "writable" properties, as well as minor type coercions of string values
        QueryByCriteria.Builder queryByCriteria = lookupCriteriaGenerator.createObjectCriteriaFromMap(type, formProps);
        List<T> results = dataObjectService.findMatching(type, queryByCriteria.build()).getResults();
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() != 1) {
            // this behavior is different from the legacy OJB behavior in that it throws an exception if more than
            // one result from such a single object query
            throw new IncorrectResultSizeDataAccessException("Incorrect number of results returned when finding object",
                    1, results.size());
        }
        return results.get(0);
    }

    /**
     * Returns whether all primary key values are specified in the lookup  map and do not contain any wildcards
     *
     * @param boClass the bo class to lookup
     * @param formProps the incoming form/lookup properties
     * @return whether all primary key values are specified in the lookup  map and do not contain any wildcards
     */
    @Override
    public boolean allPrimaryKeyValuesPresentAndNotWildcard(Class<?> boClass, Map<String, String> formProps) {
        List<String> pkFields = listPrimaryKeyFieldNames(boClass);
        Iterator<String> pkIter = pkFields.iterator();
        boolean returnVal = true;
        while (returnVal && pkIter.hasNext()) {
            String pkName = pkIter.next();
            String pkValue = formProps.get(pkName);

            if (StringUtils.isBlank(pkValue)) {
                returnVal = false;
            } else {
                for (SearchOperator op : SearchOperator.QUERY_CHARACTERS) {
                    if (pkValue.contains(op.op())) {
                        returnVal = false;
                        break;
                    }
                }
            }
        }
        return returnVal;
    }

    //    @Override
    //    public Attachment getAttachmentByNoteId(Long noteId) {
    //        // noteIdentifier is the PK of Attachment, so just look up by PK
    //        return dataObjectService.find(Attachment.class, noteId);
    //    }

    @Override
    public List<String> listPrimaryKeyFieldNames(Class<?> type) {
        List<String> keys = Collections.emptyList();
        if (dataObjectService.getMetadataRepository().contains(type)) {
            keys = dataObjectService.getMetadataRepository().getMetadata(type).getPrimaryKeyAttributeNames();
        } else {
            // check the Data Dictionary for PK's of non-persisted objects
            DataObjectEntry dataObjectEntry = dataDictionaryService.getDataDictionary().getDataObjectEntry(
                    type.getName());
            if (dataObjectEntry != null) {
                List<String> pks = dataObjectEntry.getPrimaryKeys();
                if (pks != null) {
                    keys = pks;
                }
            } else {
                ModuleService responsibleModuleService = kualiModuleService.getResponsibleModuleService(type);
                if (responsibleModuleService != null && responsibleModuleService.isExternalizable(type)) {
                    keys = responsibleModuleService.listPrimaryKeyFieldNames(type);
                }
            }
        }
        return keys;
    }

    /**
     * LookupServiceImpl calls BusinessObjectMetaDataService to listPrimaryKeyFieldNames.
     * The BusinessObjectMetaDataService goes beyond the PersistenceStructureService to consult
     * the associated ModuleService in determining the primary key field names.
     * TODO: Do we need both listPrimaryKeyFieldNames/persistenceStructureService and
     * listPrimaryKeyFieldNamesConsultingAllServices/businesObjectMetaDataService or
     * can the latter superset be used for the former?
     *
     * @param type the data object class
     * @return list of primary key field names, consulting persistence structure service, module service and
     * datadictionary
     */
    protected List<String> listPrimaryKeyFieldNamesConsultingAllServices(Class<?> type) {
        List<String> keys = new ArrayList<String>();
        if (dataObjectService.getMetadataRepository().contains(type)) {
            keys = dataObjectService.getMetadataRepository().getMetadata(type).getPrimaryKeyAttributeNames();
        }
        return keys;
    }

    @Override
    public Class<?> determineCollectionObjectType(Class<?> containingType, String collectionPropertyName) {
        final Class<?> collectionObjectType;
        if (dataObjectService.getMetadataRepository().contains(containingType)) {
            DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(containingType);
            DataObjectCollection collection = metadata.getCollection(collectionPropertyName);
            if (collection == null) {
                throw new IllegalArgumentException(
                        "Failed to locate a collection property with the given name: " + collectionPropertyName);
            }
            collectionObjectType = collection.getRelatedType();
        } else {
            throw new IllegalArgumentException(
                    "Given containing class is not a valid data object, no metadata could be located for "
                            + containingType.getName());
        }
        return collectionObjectType;

    }

    @Override
    public boolean hasReference(Class<?> boClass, String referenceName) {
        throw new UnsupportedOperationException("hasReference not valid for KRAD data operation");
    }

    @Override
    public boolean hasCollection(Class<?> boClass, String collectionName) {
        throw new UnsupportedOperationException("hasCollection not valid for KRAD data operation");
    }

    @Override
    public boolean isExtensionAttribute(Class<?> boClass, String attributePropertyName, Class<?> propertyType) {
        DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(boClass);
        if (metadata != null) {
            DataObjectRelationship relationship = metadata.getRelationship(attributePropertyName);
            if (relationship != null) {
                Class<?> relatedType = relationship.getRelatedType();
                // right now, the only way to tell if an attribute is an extension is to check this annotation, the
                // metadata repository does not currently store any such info that we can glom onto
                ExtensionFor annotation = relatedType.getAnnotation(ExtensionFor.class);
                if (annotation != null) {
                    return annotation.value().equals(boClass);
                }
            }
        }
        return false;
    }

    @Override
    public Class<?> getExtensionAttributeClass(Class<?> boClass, String attributePropertyName) {
        DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(boClass);
        if (metadata != null) {
            DataObjectRelationship relationship = metadata.getRelationship(attributePropertyName);
            if (relationship != null) {
                return relationship.getRelatedType();
            }
        }
        return null;
    }

    @Override
    public Map<String, ?> getPrimaryKeyFieldValuesDOMDS(Object dataObject) {
        return dataObjectService.wrap(dataObject).getPrimaryKeyValues();
    }

    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        return dataObjectService.wrap(do1).equalsByPrimaryKey(do2);
    }

//    @Override
//    public PersistableBusinessObject toPersistableBusinessObject(Object object) {
//        throw new UnsupportedOperationException("toPersistableBusinessObject not valid for KRAD data operation");
//    }

    @Override
    public void materializeAllSubObjects(Object object) {
        // for now, do nothing if this is not a legacy object, we'll eliminate the concept of materializing
        // sub objects in this fashion in the new data layer, will enter a jira to re-examine this
    }

    @Override
    /**
     * Recursively calls getPropertyTypeChild if nested property to allow it to properly look it up
     */
    public Class<?> getPropertyType(Object object, String propertyName) {
        DataObjectWrapper wrappedObject = dataObjectService.wrap(object);
        if (PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName)) {
            return wrappedObject.getPropertyTypeNullSafe(wrappedObject.getWrappedClass(), propertyName);
        }
        return wrappedObject.getPropertyType(propertyName);
    }

    @Override
    public Object getExtension(
            Class<?> businessObjectClass) throws InstantiationException, IllegalAccessException {
        DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(businessObjectClass);
        DataObjectRelationship extensionRelationship = metadata.getRelationship("extension");
        if (extensionRelationship != null) {
            Class<?> extensionType = extensionRelationship.getRelatedType();
            return (PersistableBusinessObjectExtension) extensionType.newInstance();
        }
        return null;
    }

    @Override
    public void refreshReferenceObject(Object businessObject, String referenceObjectName) {
        dataObjectService.wrap(businessObject).fetchRelationship(referenceObjectName);
    }

    @Override
    public boolean isLockable(Object object) {
        return isPersistable(object.getClass());
    }

    @Override
    public void verifyVersionNumber(Object dataObject) {
        DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(dataObject.getClass());
        if (metadata == null) {
            return;
        }

        if (metadata.isSupportsOptimisticLocking()) {
            if (dataObject instanceof Versioned) {
                Map<String, ?> keyPropertyValues = dataObjectService.wrap(dataObject).getPrimaryKeyValues();
                CompoundKey key = new CompoundKey(keyPropertyValues);
                Object persistableDataObject = null;
                if (!key.hasNullKeyValues()) {
                    persistableDataObject = dataObjectService.find(dataObject.getClass(), key);
                }
                // if it's null that means that this is an insert, not an update
                if (persistableDataObject != null) {
                    Long databaseVersionNumber = ((Versioned) persistableDataObject).getVersionNumber();
                    Long documentVersionNumber = ((Versioned) dataObject).getVersionNumber();
                    if (databaseVersionNumber != null && !(databaseVersionNumber.equals(documentVersionNumber))) {
                        GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS,
                                RiceKeyConstants.ERROR_VERSION_MISMATCH);
                        throw new ValidationException(
                                "Version mismatch between the local business object and the database business object");
                    }
                }
            }
        }
    }

    @Override
    public RemotableQuickFinder.Builder createQuickFinder(Class<?> containingClass, String attributeName) {
        return createQuickFinderNew(containingClass, attributeName);
    }

    /**
     * New implementation of createQuickFinder which uses the new dataObjectService.getMetadataRepository().
     */
    protected RemotableQuickFinder.Builder createQuickFinderNew(Class<?> containingClass, String attributeName) {
        if (dataObjectService.getMetadataRepository().contains(containingClass)) {

            String lookupClassName = null;
            Map<String, String> fieldConversions = new HashMap<String, String>();
            Map<String, String> lookupParameters = new HashMap<String, String>();

            DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(containingClass);
            DataObjectRelationship relationship = metadata.getRelationshipByLastAttributeInRelationship(attributeName);
            if (relationship != null) {
                DataObjectMetadata lookupClassMetadata = dataObjectService.getMetadataRepository().getMetadata(
                        relationship.getRelatedType());
                lookupClassName = lookupClassMetadata.getClass().getName();
                for (DataObjectAttributeRelationship attributeRelationship : relationship.getAttributeRelationships()) {

                    // for field conversions, we map from the child attribute name to the parent attribute name because
                    // whenever the value is returned from the object being looked up (child in this case) we want to
                    // map the result back to the corresponding attributes on the "parent" object
                    fieldConversions.put(attributeRelationship.getChildAttributeName(),
                            attributeRelationship.getParentAttributeName());

                    // for lookup parameters, we need to map the other direction since we are passing parameters *from* our parent
                    // object *to* the child object
                    lookupParameters.put(attributeRelationship.getParentAttributeName(),
                            attributeRelationship.getChildAttributeName());
                }
                // in the legacy implementation of this, if there was a "userVisibleIdentifierKey" defined on
                // the relationship, it would only add the lookup parameter for that key
                //
                // In krad-data, we recognize that related objects have business keys and we use that information
                // to alter the lookup parameters (only) to pass the business key field(s) to the lookup
                if (lookupClassMetadata.hasDistinctBusinessKey()) {
                    lookupParameters.clear();
                    for (String businessKeyAttributeName : lookupClassMetadata.getBusinessKeyAttributeNames()) {
                        lookupParameters.put(relationship.getName() + "." + businessKeyAttributeName,
                                businessKeyAttributeName);
                    }
                }
            } else {
                // check for primary display attribute attribute and if match build lookup to target class using primary key fields
                String primaryDisplayAttributeName = metadata.getPrimaryDisplayAttributeName();
                if (StringUtils.equals(primaryDisplayAttributeName, attributeName)) {
                    lookupClassName = containingClass.getName();
                    List<String> primaryKeyAttributes = metadata.getPrimaryKeyAttributeNames();
                    for (String primaryKeyAttribute : primaryKeyAttributes) {
                        fieldConversions.put(primaryKeyAttribute, primaryKeyAttribute);
                        if (!StringUtils.equals(primaryKeyAttribute, attributeName)) {
                            lookupParameters.put(primaryKeyAttribute, primaryKeyAttribute);
                        }
                    }
                }
            }

            if (StringUtils.isNotBlank(lookupClassName)) {
                String baseUrl = kualiConfigurationService.getPropertyValueAsString(KRADConstants.KRAD_LOOKUP_URL_KEY);
                RemotableQuickFinder.Builder builder = RemotableQuickFinder.Builder.create(baseUrl, lookupClassName);
                builder.setLookupParameters(lookupParameters);
                builder.setFieldConversions(fieldConversions);
                return builder;
            }

        }
        return null;
    }

    @Override
    public boolean isReferenceUpdatable(Class<?> type, String referenceName) {
        if (dataObjectService.getMetadataRepository().contains(type)) {
            DataObjectRelationship relationship = dataObjectService.getMetadataRepository().getMetadata(type)
                    .getRelationship(referenceName);
            if (relationship != null) {
                return relationship.isSavedWithParent();
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Class> listReferenceObjectFields(Class<?> type) {
        Map<String, Class> referenceNameToTypeMap = new HashMap<String, Class>();
        if (dataObjectService.getMetadataRepository().contains(type)) {
            List<DataObjectRelationship> relationships = dataObjectService.getMetadataRepository().getMetadata(type)
                    .getRelationships();
            for (DataObjectRelationship rel : relationships) {
                referenceNameToTypeMap.put(rel.getName(), rel.getRelatedType());
            }
        }
        return referenceNameToTypeMap;
    }

    @Override
    public boolean isCollectionUpdatable(Class<?> type, String collectionName) {
        if (dataObjectService.getMetadataRepository().contains(type)) {
            DataObjectCollection collection = dataObjectService.getMetadataRepository().getMetadata(type).getCollection(
                    collectionName);
            if (collection != null) {
                return collection.isSavedWithParent();
            }
        }
        return false;
    }

    @Override
    public Map<String, Class> listCollectionObjectTypes(Class<?> type) {
        Map<String, Class> collectionNameToTypeMap = new HashMap<String, Class>();
        if (dataObjectService.getMetadataRepository().contains(type)) {
            List<DataObjectCollection> collections = dataObjectService.getMetadataRepository().getMetadata(type)
                    .getCollections();
            for (DataObjectCollection coll : collections) {
                collectionNameToTypeMap.put(coll.getName(), coll.getRelatedType());
            }
        }
        return collectionNameToTypeMap;
    }

    @Override
    public Object getReferenceIfExists(Object bo, String referenceName) {
        // fetches relationship if key is set and return populated value or null
        DataObjectWrapper<Object> dataObjectWrapper = dataObjectService.wrap(bo);
        dataObjectWrapper.fetchRelationship(referenceName);
        return dataObjectWrapper.getPropertyValueNullSafe(referenceName);
    }

    @Override
    public boolean allForeignKeyValuesPopulatedForReference(Object bo, String referenceName) {
        Map<String, String> fkReferences = getForeignKeysForReference(bo.getClass(), referenceName);
        if (fkReferences.size() > 0) {
            DataObjectWrapper<Object> dataObjectWrapper = dataObjectService.wrap(bo);

            for (String fkFieldName : fkReferences.keySet()) {
                Object fkFieldValue = dataObjectWrapper.getForeignKeyAttributeValue(fkFieldName);
                if (fkFieldValue == null) {
                    return false;
                } else if (fkFieldValue instanceof CompoundKey) {
                    return !((CompoundKey) fkFieldValue).hasNullKeyValues();
                } else if (String.class.isAssignableFrom(fkFieldValue.getClass())) {
                    if (StringUtils.isBlank((String) fkFieldValue)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * gets the relationship that the attribute represents on the class
     *
     * @param c - the class to which the attribute belongs
     * @param attributeName - property name for the attribute
     * @return a relationship definition for the attribute
     */
    @Override
    public RelationshipDefinition getDictionaryRelationship(Class<?> c, String attributeName) {
        DataDictionaryEntry entryBase =
                KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(
                        c.getName());
        if (entryBase == null) {
            return null;
        }

        RelationshipDefinition relationship = null;

        List<RelationshipDefinition> ddRelationships = entryBase.getRelationships();

        int minKeys = Integer.MAX_VALUE;
        for (RelationshipDefinition def : ddRelationships) {
            // favor key sizes of 1 first
            if (def.getPrimitiveAttributes().size() == 1) {
                for (PrimitiveAttributeDefinition primitive : def.getPrimitiveAttributes()) {
                    if (primitive.getSourceName().equals(attributeName) || def.getObjectAttributeName().equals(
                            attributeName)) {
                        relationship = def;
                        minKeys = 1;
                        break;
                    }
                }
            } else if (def.getPrimitiveAttributes().size() < minKeys) {
                for (PrimitiveAttributeDefinition primitive : def.getPrimitiveAttributes()) {
                    if (primitive.getSourceName().equals(attributeName) || def.getObjectAttributeName().equals(
                            attributeName)) {
                        relationship = def;
                        minKeys = def.getPrimitiveAttributes().size();
                        break;
                    }
                }
            }
        }

        // check the support attributes
        if (relationship == null) {
            for (RelationshipDefinition def : ddRelationships) {
                if (def.hasIdentifier()) {
                    if (def.getIdentifier().getSourceName().equals(attributeName)) {
                        relationship = def;
                    }
                }
            }
        }

        return relationship;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter
     */
    @Override
    public String getTitleAttribute(Class<?> dataObjectClass) {
        String titleAttribute = null;
        DataObjectEntry entry = getDataObjectEntry(dataObjectClass);
        if (entry != null) {
            titleAttribute = entry.getTitleAttribute();
        }
        return titleAttribute;
    }

    /**
     * @param dataObjectClass
     * @return DataObjectEntry for the given dataObjectClass, or null if
     * there is none
     * @throws IllegalArgumentException if the given Class is null
     */
    protected DataObjectEntry getDataObjectEntry(Class<?> dataObjectClass) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        DataObjectEntry entry = dataDictionaryService.getDataDictionary().getDataObjectEntry(dataObjectClass.getName());

        return entry;
    }

    /**
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#areNotesSupported(java.lang.Class)
     */
    @Override
    public boolean areNotesSupported(Class<?> dataObjectClass) {
        boolean hasNotesSupport = false;

        DataObjectEntry entry = getDataObjectEntry(dataObjectClass);
        if (entry != null) {
            hasNotesSupport = entry.isBoNotesEnabled();
        }

        return hasNotesSupport;
    }

    /**
     * Grabs primary key fields and sorts them if sort field names is true
     *
     * @param dataObject
     * @param sortFieldNames
     * @return Map of sorted primary key field values
     */
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject, boolean sortFieldNames) {
        Map<String, Object> keyFieldValues = (Map<String, Object>) getPrimaryKeyFieldValues(dataObject);
        if (sortFieldNames) {
            Map<String, Object> sortedKeyFieldValues = new TreeMap<String, Object>();
            sortedKeyFieldValues.putAll(keyFieldValues);
            return sortedKeyFieldValues;
        }
        return keyFieldValues;
    }

    /**
     * @see org.kuali.rice.krad.service.DataObjectMetaDataService#getDataObjectIdentifierString
     */
    @Override
    public String getDataObjectIdentifierString(Object dataObject) {
        String identifierString = "";

        if (dataObject == null) {
            identifierString = "Null";
            return identifierString;
        }

        Class<?> dataObjectClass = dataObject.getClass();
        // build identifier string from primary key values
        Map<String, ?> primaryKeyFieldValues = getPrimaryKeyFieldValues(dataObject, true);
        for (Map.Entry<String, ?> primaryKeyValue : primaryKeyFieldValues.entrySet()) {
            if (primaryKeyValue.getValue() == null) {
                identifierString += "Null";
            } else {
                identifierString += primaryKeyValue.getValue();
            }
            identifierString += ":";
        }
        return StringUtils.removeEnd(identifierString, ":");
    }

    @Override
    public Class<?> getInquiryObjectClassIfNotTitle(Object dataObject, String propertyName) {
        DataObjectMetadata objectMetadata =
                KRADServiceLocator.getDataObjectService().getMetadataRepository().getMetadata(dataObject.getClass());
        if (objectMetadata != null) {
            org.kuali.rice.krad.data.metadata.DataObjectRelationship dataObjectRelationship =
                    objectMetadata.getRelationship(propertyName);
            if (dataObjectRelationship != null) {
                return dataObjectRelationship.getRelatedType();
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getInquiryParameters(Object dataObject, List<String> keys, String propertyName) {
        Map<String, String> inquiryParameters = new HashMap<String, String>();
        org.kuali.rice.krad.data.metadata.DataObjectRelationship dataObjectRelationship = null;

        DataObjectMetadata objectMetadata =
                KRADServiceLocator.getDataObjectService().getMetadataRepository().getMetadata(dataObject.getClass());

        if (objectMetadata != null) {
            dataObjectRelationship = objectMetadata.getRelationshipByLastAttributeInRelationship(propertyName);
        }

        for (String keyName : keys) {
            String keyConversion = keyName;
            if (dataObjectRelationship != null) {
                keyConversion = dataObjectRelationship.getParentAttributeNameRelatedToChildAttributeName(keyName);
            } else if (PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName)) {
                String nestedAttributePrefix = KRADUtils.getNestedAttributePrefix(propertyName);
                keyConversion = nestedAttributePrefix + "." + keyName;
            }
            inquiryParameters.put(keyConversion, keyName);
        }
        return inquiryParameters;
    }

    @Override
    public boolean hasLocalLookup(Class<?> dataObjectClass) {
        return viewDictionaryService.isLookupable(dataObjectClass);
    }

    @Override
    public boolean hasLocalInquiry(Class<?> dataObjectClass) {
        return viewDictionaryService.isInquirable(dataObjectClass);
    }

    @Override
    public org.kuali.rice.krad.bo.DataObjectRelationship getDataObjectRelationship(Object dataObject,
            Class<?> dataObjectClass, String attributeName, String attributePrefix, boolean keysOnly,
            boolean supportsLookup, boolean supportsInquiry) {
        RelationshipDefinition ddReference = getDictionaryRelationship(dataObjectClass, attributeName);

        org.kuali.rice.krad.bo.DataObjectRelationship relationship = null;
        DataObjectAttributeRelationship rel = null;
        if (PropertyAccessorUtils.isNestedOrIndexedProperty(attributeName)) {
            if (ddReference != null) {
                if (classHasSupportedFeatures(ddReference.getTargetClass(), supportsLookup, supportsInquiry)) {
                    relationship = populateRelationshipFromDictionaryReference(dataObjectClass, ddReference,
                            attributePrefix, keysOnly);

                    return relationship;
                }
            }

            if (dataObject == null) {
                try {
                    dataObject = KRADUtils.createNewObjectFromClass(dataObjectClass);
                } catch (RuntimeException e) {
                    // found interface or abstract class, just swallow exception and return a null relationship
                    return null;
                }
            }

            // recurse down to the next object to find the relationship
            int nextObjectIndex = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(attributeName);
            if (nextObjectIndex == StringUtils.INDEX_NOT_FOUND) {
                nextObjectIndex = attributeName.length();
            }
            String localPrefix = StringUtils.substring(attributeName, 0, nextObjectIndex);
            String localAttributeName = StringUtils.substring(attributeName, nextObjectIndex + 1);
            Object nestedObject = ObjectPropertyUtils.getPropertyValue(dataObject, localPrefix);
            Class<?> nestedClass = null;
            if (nestedObject == null) {
                nestedClass = ObjectPropertyUtils.getPropertyType(dataObject, localPrefix);
            } else {
                nestedClass = nestedObject.getClass();
            }

            String fullPrefix = localPrefix;
            if (StringUtils.isNotBlank(attributePrefix)) {
                fullPrefix = attributePrefix + "." + localPrefix;
            }

            relationship = getDataObjectRelationship(nestedObject, nestedClass, localAttributeName, fullPrefix,
                    keysOnly, supportsLookup, supportsInquiry);

            return relationship;
        }

        // non-nested reference, get persistence relationships first
        int maxSize = Integer.MAX_VALUE;

        if (isPersistable(dataObjectClass)) {
            DataObjectMetadata metadata = dataObjectService.getMetadataRepository().getMetadata(dataObjectClass);
            DataObjectRelationship dataObjectRelationship = metadata.getRelationship(attributeName);

            if (dataObjectRelationship != null) {
                List<DataObjectAttributeRelationship> attributeRelationships =
                        dataObjectRelationship.getAttributeRelationships();
                for (DataObjectAttributeRelationship dataObjectAttributeRelationship : attributeRelationships) {
                    if (classHasSupportedFeatures(dataObjectRelationship.getRelatedType(), supportsLookup,
                            supportsInquiry)) {
                        maxSize = attributeRelationships.size();
                        relationship = transformToDeprecatedDataObjectRelationship(dataObjectClass, attributeName,
                                attributePrefix, dataObjectRelationship.getRelatedType(),
                                dataObjectAttributeRelationship);

                        break;
                    }
                }
            }

        } else {
            ModuleService moduleService = kualiModuleService.getResponsibleModuleService(dataObjectClass);
            if (moduleService != null && moduleService.isExternalizable(dataObjectClass)) {
                relationship = getRelationshipMetadata(dataObjectClass, attributeName, attributePrefix);
                if ((relationship != null) && classHasSupportedFeatures(relationship.getRelatedClass(), supportsLookup,
                        supportsInquiry)) {
                    return relationship;
                } else {
                    return null;
                }
            }
        }

        if (ddReference != null && ddReference.getPrimitiveAttributes().size() < maxSize) {
            if (classHasSupportedFeatures(ddReference.getTargetClass(), supportsLookup, supportsInquiry)) {
                relationship = populateRelationshipFromDictionaryReference(dataObjectClass, ddReference, null,
                        keysOnly);
            }
        }
        return relationship;
    }

    protected org.kuali.rice.krad.bo.DataObjectRelationship transformToDeprecatedDataObjectRelationship(
            Class<?> dataObjectClass, String attributeName, String attributePrefix, Class<?> relatedObjectClass,
            DataObjectAttributeRelationship relationship) {
        org.kuali.rice.krad.bo.DataObjectRelationship rel = new org.kuali.rice.krad.bo.DataObjectRelationship(
                dataObjectClass, attributeName, relatedObjectClass);
        if (StringUtils.isBlank(attributePrefix)) {
            rel.getParentToChildReferences().put(relationship.getParentAttributeName(),
                    relationship.getChildAttributeName());
        } else {
            rel.getParentToChildReferences().put(attributePrefix + "." + relationship.getParentAttributeName(),
                    relationship.getChildAttributeName());
        }

        return rel;
    }

    protected org.kuali.rice.krad.bo.DataObjectRelationship populateRelationshipFromDictionaryReference(
            Class<?> dataObjectClass, RelationshipDefinition ddReference, String attributePrefix, boolean keysOnly) {
        org.kuali.rice.krad.bo.DataObjectRelationship relationship = new org.kuali.rice.krad.bo.DataObjectRelationship(
                dataObjectClass, ddReference.getObjectAttributeName(), ddReference.getTargetClass());

        for (PrimitiveAttributeDefinition def : ddReference.getPrimitiveAttributes()) {
            if (StringUtils.isNotBlank(attributePrefix)) {
                relationship.getParentToChildReferences().put(attributePrefix + "." + def.getSourceName(),
                        def.getTargetName());
            } else {
                relationship.getParentToChildReferences().put(def.getSourceName(), def.getTargetName());
            }
        }

        if (!keysOnly) {
            for (SupportAttributeDefinition def : ddReference.getSupportAttributes()) {
                if (StringUtils.isNotBlank(attributePrefix)) {
                    relationship.getParentToChildReferences().put(attributePrefix + "." + def.getSourceName(),
                            def.getTargetName());
                    if (def.isIdentifier()) {
                        relationship.setUserVisibleIdentifierKey(attributePrefix + "." + def.getSourceName());
                    }
                } else {
                    relationship.getParentToChildReferences().put(def.getSourceName(), def.getTargetName());
                    if (def.isIdentifier()) {
                        relationship.setUserVisibleIdentifierKey(def.getSourceName());
                    }
                }
            }
        }

        return relationship;
    }

    @Override
    public boolean isPersistable(Class<?> dataObjectClass) {
        return dataObjectService.getMetadataRepository().contains(dataObjectClass);
    }

    protected org.kuali.rice.krad.bo.DataObjectRelationship getRelationshipMetadata(Class<?> dataObjectClass,
            String attributeName, String attributePrefix) {

        RelationshipDefinition relationshipDefinition = getDictionaryRelationship(dataObjectClass, attributeName);
        if (relationshipDefinition == null) {
            return null;
        }

        org.kuali.rice.krad.bo.DataObjectRelationship dataObjectRelationship =
                new org.kuali.rice.krad.bo.DataObjectRelationship(relationshipDefinition.getSourceClass(),
                        relationshipDefinition.getObjectAttributeName(), relationshipDefinition.getTargetClass());

        if (!StringUtils.isEmpty(attributePrefix)) {
            attributePrefix += ".";
        }

        List<PrimitiveAttributeDefinition> primitives = relationshipDefinition.getPrimitiveAttributes();
        for (PrimitiveAttributeDefinition primitiveAttributeDefinition : primitives) {
            dataObjectRelationship.getParentToChildReferences().put(
                    attributePrefix + primitiveAttributeDefinition.getSourceName(),
                    primitiveAttributeDefinition.getTargetName());
        }

        return dataObjectRelationship;
    }

    protected boolean classHasSupportedFeatures(Class relationshipClass, boolean supportsLookup,
            boolean supportsInquiry) {
        boolean hasSupportedFeatures = true;
        if (supportsLookup && !getViewDictionaryService().isLookupable(relationshipClass)) {
            hasSupportedFeatures = false;
        }
        if (supportsInquiry && !getViewDictionaryService().isInquirable(relationshipClass)) {
            hasSupportedFeatures = false;
        }

        return hasSupportedFeatures;
    }

    @Override
    public ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(Object dataObject, String referenceName) {
        DataObjectWrapper<Object> dataObjectWrapper = dataObjectService.wrap(dataObject);
        return new ForeignKeyFieldsPopulationState(dataObjectWrapper.areAllPrimaryKeyAttributesPopulated(),
                dataObjectWrapper.areAnyPrimaryKeyAttributesPopulated(),
                dataObjectWrapper.getUnpopulatedPrimaryKeyAttributeNames());
    }

    @Override
    public Map<String, String> getForeignKeysForReference(Class<?> clazz, String attributeName) {
        if (dataObjectService.getMetadataRepository().contains(clazz)) {
            DataObjectRelationship relationship = dataObjectService.getMetadataRepository().getMetadata(clazz)
                    .getRelationship(attributeName);
            List<DataObjectAttributeRelationship> attributeRelationships = relationship.getAttributeRelationships();
            Map<String, String> parentChildKeyRelationships = new HashMap<String, String>(
                    attributeRelationships.size());
            for (DataObjectAttributeRelationship doar : attributeRelationships) {
                parentChildKeyRelationships.put(doar.getParentAttributeName(), doar.getChildAttributeName());
            }
            return parentChildKeyRelationships;
        }
        return Collections.emptyMap();
    }

    @Override
    public void setObjectPropertyDeep(Object bo, String propertyName, Class type,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        DataObjectWrapper<Object> dataObjectWrapper = dataObjectService.wrap(bo);
        // Base return cases to avoid null pointers & infinite loops
        if (KRADUtils.isNull(bo) || !PropertyUtils.isReadable(bo, propertyName) || (propertyValue != null
                && propertyValue.equals(dataObjectWrapper.getPropertyValueNullSafe(propertyName))) || (type != null
                && !type.equals(KRADUtils.easyGetPropertyType(bo, propertyName)))) {
            return;
        }
        // Set the property in the BO
        KRADUtils.setObjectProperty(bo, propertyName, type, propertyValue);

        // Now drill down and check nested BOs and BO lists
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(bo.getClass());
        for (int i = 0; i < propertyDescriptors.length; i++) {

            PropertyDescriptor propertyDescriptor = propertyDescriptors[i];

            // Business Objects
            if (propertyDescriptor.getPropertyType() != null && (BusinessObject.class).isAssignableFrom(
                    propertyDescriptor.getPropertyType()) && PropertyUtils.isReadable(bo,
                    propertyDescriptor.getName())) {
                Object nestedBo = dataObjectWrapper.getPropertyValueNullSafe(propertyDescriptor.getName());
                if (nestedBo instanceof BusinessObject) {
                    setObjectPropertyDeep(nestedBo, propertyName, type, propertyValue);
                }
            }

            // Lists
            else if (propertyDescriptor.getPropertyType() != null && (List.class).isAssignableFrom(
                    propertyDescriptor.getPropertyType()) && dataObjectWrapper.getPropertyValueNullSafe(
                    propertyDescriptor.getName()) != null) {

                List propertyList = (List) dataObjectWrapper.getPropertyValueNullSafe(propertyDescriptor.getName());
                for (Object listedBo : propertyList) {
                    if (listedBo != null && listedBo instanceof BusinessObject) {
                        setObjectPropertyDeep(listedBo, propertyName, type, propertyValue);
                    }
                } // end for
            }
        } // end for
    }

    @Override
    public boolean hasPrimaryKeyFieldValues(Object dataObject) {
        DataObjectWrapper<Object> dataObjectWrapper = dataObjectService.wrap(dataObject);
        return dataObjectWrapper.areAllPrimaryKeyAttributesPopulated();
    }

    @Override
    public Class materializeClassForProxiedObject(Object object) {
        if (object == null) {
            return null;
        }
        if (LegacyUtils.isKradDataManaged(object.getClass())) {
            Object o = resolveProxy(object);
            if (o != null) {
                return o.getClass();
            }
        }
        return object.getClass();
    }

    @Override
    public Object getNestedValue(Object bo, String fieldName) {
        return KradDataServiceLocator.getDataObjectService().wrap(bo).getPropertyValueNullSafe(fieldName);
    }

    @Override
    public Object createNewObjectFromClass(Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class was passed in as null");
        }

        Object object = null;

        try {
            object = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return object;
    }

    @Override
    public boolean isNull(Object object) {
        return object == null;
    }

    @Override
    public void setObjectProperty(Object bo, String propertyName, Class propertyType,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PropertyUtils.setNestedProperty(bo, propertyName, propertyValue);
    }

    @Override
    public <T extends Document> T findByDocumentHeaderId(Class<T> documentClass, String id) {
        T document = KRADServiceLocator.getDataObjectService().find(documentClass, id);
        // original KNS code always did this addAdHocs nonsense, so we'll do the same to preserve behavior
        ((DocumentAdHocService) KRADServiceLocatorWeb.getService("documentAdHocService")).addAdHocs(document);
        return document;
    }

    @Override
    public <T extends Document> List<T> findByDocumentHeaderIds(Class<T> documentClass, List<String> ids) {
        List<T> documents = new ArrayList<T>();
        for (String id : ids) {
            documents.add(findByDocumentHeaderId(documentClass, id));
        }
        return documents;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    @Required
    public void setLookupCriteriaGenerator(LookupCriteriaGenerator lookupCriteriaGenerator) {
        this.lookupCriteriaGenerator = lookupCriteriaGenerator;
    }

    @Required
    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    @Required
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    @Required
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public ViewDictionaryService getViewDictionaryService() {
        return viewDictionaryService;
    }

    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }

}
