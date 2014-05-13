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

import java.lang.reflect.Field;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.eclipse.persistence.indirection.ValueHolder;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;
import org.kuali.rice.core.framework.persistence.platform.DatabasePlatform;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.InactivatableFromTo;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.dao.MaintenanceDocumentDao;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.SupportAttributeDefinition;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DataObjectMetaDataService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.service.ViewDictionaryService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.LegacyUtils;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.factory.annotation.Required;

/**
*
 */
@Deprecated
public class KNSLegacyDataAdapterImpl implements LegacyDataAdapter {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            KNSLegacyDataAdapterImpl.class);

    private static final Pattern VALUE_HOLDER_FIELD_PATTERN = Pattern.compile("^_persistence_(.*)_vh$");

    private final ConcurrentMap<Class<?>, List<ValueHolderFieldPair>> valueHolderFieldCache =
            new ConcurrentHashMap<Class<?>, List<ValueHolderFieldPair>>(8, 0.9f, 1);

    private BusinessObjectService businessObjectService;
    private PersistenceService persistenceService;
    private LookupDao lookupDao;
    private LookupCriteriaGenerator lookupCriteriaGenerator;
    private DateTimeService dateTimeService;
    private DatabasePlatform databasePlatform;

    private DocumentDao documentDao;
    private MaintenanceDocumentDao maintenanceDocumentDaoOjb;

    private PersistenceStructureService persistenceStructureService;
    private DataObjectMetaDataService dataObjectMetaDataService;
    private ConfigurationService kualiConfigurationService;
    private KualiModuleService kualiModuleService;
    private DataDictionaryService dataDictionaryService;
    private DataObjectService dataObjectService;
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
           return (T) businessObjectService.save((PersistableBusinessObject) dataObject);
        }
    }

    @Override
    public <T> T linkAndSave(T dataObject) {
       return (T) businessObjectService.linkAndSave((PersistableBusinessObject) dataObject);
    }

    @Override
    public <T> T saveDocument(T document) {
        return (T) documentDao.save((Document) document);
    }

    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
        return (T) businessObjectService.findByPrimaryKey((Class<BusinessObject>) clazz, primaryKeys);
    }

    @Override
    public <T> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
        return (T) businessObjectService.findBySinglePrimaryKey((Class<BusinessObject>) clazz, primaryKey);
    }

    @Override
    public void delete(Object dataObject) {
        if (dataObject instanceof Collection) {
            for (Object dobj : (Collection) dataObject) {
                delete(dobj);
            }
        } else {
            businessObjectService.delete(dataObject);
        }
    }

    @Override
    public void deleteMatching(Class<?> type, Map<String, ?> fieldValues) {
        businessObjectService.deleteMatching(type, fieldValues);
    }

    @Override
    public <T> T retrieve(T dataObject) {
        return (T) businessObjectService.retrieve(dataObject);
    }

    @Override
    public <T> Collection<T> findAll(Class<T> clazz) {
        // just find all objects of given type without any attribute criteria
        return findMatching(clazz, Collections.<String, Object>emptyMap());
    }

    @Override
    public <T> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
        return (Collection<T>) businessObjectService.findMatching((Class<BusinessObject>) clazz, fieldValues);
    }

    @Override
    public <T> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField,
            boolean sortAscending) {
        return (Collection<T>) businessObjectService.findMatchingOrderBy((Class<BusinessObject>) clazz, fieldValues,
                    sortField, sortAscending);
    }

    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        return persistenceService.getPrimaryKeyFieldValues(dataObject);
    }

    @Override
    public void retrieveNonKeyFields(Object persistableObject) {
        persistenceService.retrieveNonKeyFields(persistableObject);
        synchronizeEclipseLinkWeavings(persistableObject);
    }

    @Override
    public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
        persistenceService.retrieveReferenceObject(persistableObject, referenceObjectName);
        synchronizeEclipseLinkWeavings(persistableObject, referenceObjectName);
    }

    /**
     * @see ValueHolderFieldPair for information on why we need to do field sychronization related to weaving
     */
    protected void synchronizeEclipseLinkWeavings(Object persistableObject, String propertyName) {
        if (LegacyUtils.isKradDataManaged(persistableObject.getClass())) {
            List<ValueHolderFieldPair> fieldPairs = loadValueHolderFieldPairs(persistableObject.getClass());
            for (ValueHolderFieldPair fieldPair : fieldPairs) {
                if (fieldPair.field.getName().equals(propertyName)) {
                    fieldPair.synchronizeValueHolder(persistableObject);
                }
            }
        }
    }

    /**
     * @see ValueHolderFieldPair for information on why we need to do field sychronization related to weaving
     */
    protected void synchronizeEclipseLinkWeavings(Object persistableObject) {
        if (LegacyUtils.isKradDataManaged(persistableObject.getClass())) {
            List<ValueHolderFieldPair> fieldPairs = loadValueHolderFieldPairs(persistableObject.getClass());
            for (ValueHolderFieldPair fieldPair : fieldPairs) {
                fieldPair.synchronizeValueHolder(persistableObject);
            }
        }
    }

    /**
     * @see ValueHolderFieldPair for information on why we need to do field sychronization related to weaving
     */
    private List<ValueHolderFieldPair> loadValueHolderFieldPairs(Class<?> type) {
        if (valueHolderFieldCache.get(type) == null) {
            List<ValueHolderFieldPair> pairs = new ArrayList<ValueHolderFieldPair>();
            searchValueHolderFieldPairs(type, pairs);
            valueHolderFieldCache.putIfAbsent(type, pairs);
        }
        return valueHolderFieldCache.get(type);
    }

    /**
     * @see ValueHolderFieldPair for information on why we need to do field sychronization related to weaving
     */
    private void searchValueHolderFieldPairs(Class<?> type, List<ValueHolderFieldPair> pairs) {
        if (type.equals(Object.class)) {
            return;
        }
        for (Field valueHolderField : type.getDeclaredFields()) {
            Matcher matcher = VALUE_HOLDER_FIELD_PATTERN.matcher(valueHolderField.getName());
            if (matcher.matches()) {
                valueHolderField.setAccessible(true);
                String fieldName = matcher.group(1);
                Field valueField = FieldUtils.getDeclaredField(type, fieldName, true);
                if (valueField != null) {
                    pairs.add(new ValueHolderFieldPair(valueField, valueHolderField));
                }
            }
        }
        searchValueHolderFieldPairs(type.getSuperclass(), pairs);
    }

    @Override
    public void refreshAllNonUpdatingReferences(Object persistableObject) {
        persistenceService.refreshAllNonUpdatingReferences((PersistableBusinessObject) persistableObject);
        synchronizeEclipseLinkWeavings(persistableObject);
    }

    @Override
    public boolean isProxied(Object object) {
    	if ( object == null || !(object instanceof BusinessObject) ) {
    		return false;
    	}
		return persistenceService.isProxied(object);
    }

    @Override
    public Object resolveProxy(Object o) {
        return persistenceService.resolveProxy(o);
    }

    // Lookup methods

    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> dataObjectClass, Map<String, String> formProperties,
            boolean unbounded, boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
            return lookupDao.findCollectionBySearchHelper(dataObjectClass, formProperties, unbounded,
                    allPrimaryKeyValuesPresentAndNotWildcard, searchResultsLimit);
    }

    //TODO: implement. currently is same implementation as above
    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> dataObjectClass, Map<String, String> formProperties,
            List<String> wildcardAsLiteralPropertyNames, boolean unbounded,
            boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        return lookupDao.findCollectionBySearchHelper(dataObjectClass, formProperties, unbounded,
                allPrimaryKeyValuesPresentAndNotWildcard, searchResultsLimit);
    }

    /**
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
        return lookupDao.findObjectByMap(type, formProps);
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

    @SuppressWarnings("unchecked")
	@Override
    public List<String> listPrimaryKeyFieldNames(Class<?> type) {
        List<String> keys = new ArrayList<String>();
        if ( type == null ) {
        	return keys;
        }
        if (isPersistable(type)) {
            keys = persistenceStructureService.listPrimaryKeyFieldNames(type);
        } else {
            ModuleService responsibleModuleService = kualiModuleService.getResponsibleModuleService(type);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(type)) {
                keys = responsibleModuleService.listPrimaryKeyFieldNames(type);
            } else {
                // check the Data Dictionary for PK's of non PBO/EBO
            	DataObjectEntry dataObjectEntry = dataDictionaryService.getDataDictionary().getDataObjectEntry(type.getName());
            	if ( dataObjectEntry != null ) {
	                List<String> pks = dataObjectEntry.getPrimaryKeys();
	                if (pks != null ) {
	                    keys = pks;
	                }
            	} else {
            		LOG.warn( "Unable to retrieve data object entry for non-persistable KNS-managed class: " + type.getName() );
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
     *         datadictionary
     */
    protected List<String> listPrimaryKeyFieldNamesConsultingAllServices(Class<?> type) {
        return dataObjectMetaDataService.listPrimaryKeyFieldNames(type);
    }

    @Override
    public Class<?> determineCollectionObjectType(Class<?> containingType, String collectionPropertyName) {
        final Class<?> collectionObjectType;
        if (isPersistable(containingType)) {
            Map<String, Class> collectionClasses = new HashMap<String, Class>();
            collectionClasses = persistenceStructureService.listCollectionObjectTypes(containingType);
            collectionObjectType = collectionClasses.get(collectionPropertyName);
        } else {
            throw new RuntimeException(
                    "Can't determine the Class of Collection elements because persistenceStructureService.isPersistable("
                            + containingType.getName()
                            + ") returns false.");
        }
        return collectionObjectType;

    }

    @Override
    public boolean hasReference(Class<?> boClass, String referenceName) {
        return persistenceStructureService.hasReference(boClass, referenceName);
    }

    @Override
    public boolean hasCollection(Class<?> boClass, String collectionName) {
        return persistenceStructureService.hasCollection(boClass, collectionName);
    }

    @Override
    public boolean isExtensionAttribute(Class<?> boClass, String attributePropertyName, Class<?> propertyType) {
        return propertyType.equals(PersistableBusinessObjectExtension.class);
    }

    @Override
    public Class<?> getExtensionAttributeClass(Class<?> boClass, String attributePropertyName) {
        return persistenceStructureService.getBusinessObjectAttributeClass((Class<? extends PersistableBusinessObject>)boClass, attributePropertyName);
    }


    @Override
    public Map<String, ?> getPrimaryKeyFieldValuesDOMDS(Object dataObject) {
        return dataObjectMetaDataService.getPrimaryKeyFieldValues(dataObject);
    }

    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        return dataObjectMetaDataService.equalsByPrimaryKeys(do1, do2);
    }

    @Override
    public void materializeAllSubObjects(Object object) {
        ObjectUtils.materializeAllSubObjects((PersistableBusinessObject) object);
    }

    @Override
    public Class<?> getPropertyType(Object object, String propertyName) {
        return ObjectUtils.getPropertyType(object, propertyName, persistenceStructureService);
    }

    @Override
    public Object getExtension(
            Class<?> businessObjectClass) throws InstantiationException, IllegalAccessException {
        Class<? extends PersistableBusinessObjectExtension> extensionClass =
                persistenceStructureService.getBusinessObjectAttributeClass((Class<? extends PersistableBusinessObject>) businessObjectClass, "extension");
        if (extensionClass != null) {
            return extensionClass.newInstance();
        }
        return null;
    }

    @Override
    public void refreshReferenceObject(Object businessObject, String referenceObjectName) {
        if (StringUtils.isNotBlank(referenceObjectName) && !StringUtils.equals(referenceObjectName, "extension")) {
            if (persistenceStructureService.hasReference(businessObject.getClass(), referenceObjectName)
                    || persistenceStructureService.hasCollection(businessObject.getClass(), referenceObjectName)) {
                retrieveReferenceObject(businessObject, referenceObjectName);
            }
        }
    }

    @Override
    public boolean isLockable(Object object) {
        return isPersistable(object.getClass());
    }

    @Override
    public void verifyVersionNumber(Object dataObject) {
        if (isPersistable(dataObject.getClass())) {
            Object pbObject = businessObjectService.retrieve(dataObject);
            if ( dataObject instanceof Versioned ) {
	            Long pbObjectVerNbr = KRADUtils.isNull(pbObject) ? null : ((Versioned) pbObject).getVersionNumber();
	            Long newObjectVerNbr = ((Versioned) dataObject).getVersionNumber();
	            if (pbObjectVerNbr != null && !(pbObjectVerNbr.equals(newObjectVerNbr))) {
	                GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS,
	                        RiceKeyConstants.ERROR_VERSION_MISMATCH);
	                throw new ValidationException(
	                        "Version mismatch between the local business object and the database business object");
	            }
            }
        }
    }

    @Override
    public RemotableQuickFinder.Builder createQuickFinder(Class<?> containingClass, String attributeName) {
        return createQuickFinderLegacy(containingClass, attributeName);
    }

    /**
     * Legacy implementation of createQuickFinder. Uses the legacy DataObjectMetadataService.
     */
    protected RemotableQuickFinder.Builder createQuickFinderLegacy(Class<?> containingClass, String attributeName) {
        Object sampleComponent;
        try {
            sampleComponent = containingClass.newInstance();
        } catch (InstantiationException e) {
            throw new RiceRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RiceRuntimeException(e);
        }

        String lookupClassName = null;
        Map<String, String> fieldConversions = new HashMap<String, String>();
        Map<String, String> lookupParameters = new HashMap<String, String>();

        org.kuali.rice.krad.bo.DataObjectRelationship relationship =
                getDataObjectRelationship(sampleComponent, containingClass, attributeName, "",
                        true, true, false);
        if (relationship != null) {
            lookupClassName = relationship.getRelatedClass().getName();

            for (Map.Entry<String, String> entry : relationship.getParentToChildReferences().entrySet()) {
                String fromField = entry.getValue();
                String toField = entry.getKey();
                fieldConversions.put(fromField, toField);
            }

            for (Map.Entry<String, String> entry : relationship.getParentToChildReferences().entrySet()) {
                String fromField = entry.getKey();
                String toField = entry.getValue();

                if (relationship.getUserVisibleIdentifierKey() == null || relationship.getUserVisibleIdentifierKey()
                        .equals(fromField)) {
                    lookupParameters.put(fromField, toField);
                }
            }
        } else {
            // check for title attribute and if match build lookup to component class using pk fields
            String titleAttribute = dataObjectMetaDataService.getTitleAttribute(containingClass);
            if (StringUtils.equals(titleAttribute, attributeName)) {
                lookupClassName = containingClass.getName();

                List<String> pkAttributes = dataObjectMetaDataService.listPrimaryKeyFieldNames(containingClass);
                for (String pkAttribute : pkAttributes) {
                    fieldConversions.put(pkAttribute, pkAttribute);
                    if (!StringUtils.equals(pkAttribute, attributeName)) {
                        lookupParameters.put(pkAttribute, pkAttribute);
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

        return null;
    }


    @Override
    public boolean isReferenceUpdatable(Class<?> type, String referenceName) {
        return persistenceStructureService.isReferenceUpdatable(type, referenceName);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Class> listReferenceObjectFields(Class<?> type) {
        return persistenceStructureService.listReferenceObjectFields(type);
    }

    @Override
    public boolean isCollectionUpdatable(Class<?> type, String collectionName) {
        return persistenceStructureService.isCollectionUpdatable(type, collectionName);
    }

    @Override
    public Map<String, Class> listCollectionObjectTypes(Class<?> type) {
        return persistenceStructureService.listCollectionObjectTypes(type);
    }

    @Override
    public BusinessObject getReferenceIfExists(Object bo, String referenceName) {
        if (!(bo instanceof BusinessObject)) {
            throw new UnsupportedOperationException("getReferenceIfExists only supports BusinessObject in KNS");
        }

        return businessObjectService.getReferenceIfExists((BusinessObject) bo, referenceName);
    }

    @Override
    public boolean allForeignKeyValuesPopulatedForReference(Object bo, String referenceName) {
        if (!(bo instanceof PersistableBusinessObject)) {
            throw new UnsupportedOperationException(
                    "getReferenceIfExists only supports PersistableBusinessObject in KNS");
        }

        return persistenceService.allForeignKeyValuesPopulatedForReference((PersistableBusinessObject) bo,
                referenceName);
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
     *         there is none
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
     * @see org.kuali.rice.krad.service.LegacyDataAdapter#areNotesSupported(Class)
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
        // if Legacy and a PersistableBusinessObject or if not Legacy and implement GlobalLyUnique use the object id field
        if ((PersistableBusinessObject.class.isAssignableFrom(
                dataObjectClass)) || (!LegacyUtils.useLegacyForObject(dataObject) && GloballyUnique.class
                .isAssignableFrom(dataObjectClass))) {
            String objectId = ObjectPropertyUtils.getPropertyValue(dataObject, UifPropertyPaths.OBJECT_ID);
            if (StringUtils.isBlank(objectId)) {
                objectId = UUID.randomUUID().toString();
                ObjectPropertyUtils.setPropertyValue(dataObject, UifPropertyPaths.OBJECT_ID, objectId);
            }
        }
        return identifierString;
    }

    @Override
    public Class<?> getInquiryObjectClassIfNotTitle(Object dataObject, String propertyName) {
        Class<?> objectClass = ObjectUtils.materializeClassForProxiedObject(dataObject);
        org.kuali.rice.krad.bo.DataObjectRelationship relationship =
                dataObjectMetaDataService.getDataObjectRelationship(dataObject, objectClass, propertyName, "", true,
                        false, true);
        if (relationship != null) {
            return relationship.getRelatedClass();
        }
        return null;
    }

    @Override
    public Map<String, String> getInquiryParameters(Object dataObject, List<String> keys, String propertyName) {
        Map<String, String> inquiryParameters = new HashMap<String, String>();
        Class<?> objectClass = ObjectUtils.materializeClassForProxiedObject(dataObject);
        org.kuali.rice.krad.bo.DataObjectRelationship relationship =
                dataObjectMetaDataService.getDataObjectRelationship(dataObject, objectClass, propertyName, "", true,
                        false, true);
        for (String keyName : keys) {
            String keyConversion = keyName;
            if (relationship != null) {
                keyConversion = relationship.getParentAttributeForChildAttribute(keyName);
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
        return dataObjectMetaDataService.hasLocalLookup(dataObjectClass);
    }

    @Override
    public boolean hasLocalInquiry(Class<?> dataObjectClass) {
        return dataObjectMetaDataService.hasLocalInquiry(dataObjectClass);
    }

    @Override
    public org.kuali.rice.krad.bo.DataObjectRelationship getDataObjectRelationship(Object dataObject,
            Class<?> dataObjectClass, String attributeName, String attributePrefix, boolean keysOnly,
            boolean supportsLookup, boolean supportsInquiry) {
        RelationshipDefinition ddReference = getDictionaryRelationship(dataObjectClass, attributeName);
        return dataObjectMetaDataService.getDataObjectRelationship(dataObject, dataObjectClass, attributeName,
                    attributePrefix, keysOnly, supportsLookup, supportsInquiry);

    }



    protected org.kuali.rice.krad.bo.DataObjectRelationship populateRelationshipFromDictionaryReference(Class<?> dataObjectClass,
            RelationshipDefinition ddReference, String attributePrefix, boolean keysOnly) {
        org.kuali.rice.krad.bo.DataObjectRelationship relationship = new org.kuali.rice.krad.bo.DataObjectRelationship(dataObjectClass,
                ddReference.getObjectAttributeName(), ddReference.getTargetClass());

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
        return persistenceStructureService.isPersistable(dataObjectClass);
    }

    @Override
    public void setObjectPropertyDeep(Object bo, String propertyName, Class type,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ObjectUtils.setObjectPropertyDeep(bo,propertyName,type,propertyValue);
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
    public boolean isNull(Object object){
         return ObjectUtils.isNull(object);
    }

    @Override
    public void setObjectProperty(Object bo, String propertyName, Class propertyType,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
        ObjectUtils.setObjectProperty(bo,propertyName,propertyType,propertyValue);
    }

    @Override
	public Class materializeClassForProxiedObject(Object object){
        return ObjectUtils.materializeClassForProxiedObject(object);
    }

    @Override
	public Object getNestedValue(Object bo, String fieldName){
        return ObjectUtils.getNestedValue(bo,fieldName);
    }

    @Override
	public Object createNewObjectFromClass(Class clazz){
        return ObjectUtils.createNewObjectFromClass(clazz);
    }

    @Override
    public ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(Object dataObject, String referenceName) {
        return persistenceStructureService.getForeignKeyFieldsPopulationState(
                    (PersistableBusinessObject) dataObject, referenceName);
    }

    @Override
    public Map<String, String> getForeignKeysForReference(Class<?> clazz, String attributeName) {
        return persistenceStructureService.getForeignKeysForReference(clazz, attributeName);
    }

    @Override
    public boolean hasPrimaryKeyFieldValues(Object dataObject) {
        return persistenceStructureService.hasPrimaryKeyFieldValues(dataObject);
    }

    @Override
    public <T extends Document> T findByDocumentHeaderId(Class<T> documentClass, String id) {
        return documentDao.findByDocumentHeaderId(documentClass, id);
    }

    @Override
    public <T extends Document> List<T> findByDocumentHeaderIds(Class<T> documentClass, List<String> ids) {
        return documentDao.findByDocumentHeaderIds(documentClass, ids);
    }


        @Required
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    @Required
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Required
    public void setLookupDao(LookupDao lookupDao) {
        this.lookupDao = lookupDao;
    }

    @Required
    public void setLookupCriteriaGenerator(LookupCriteriaGenerator lookupCriteriaGenerator) {
        this.lookupCriteriaGenerator = lookupCriteriaGenerator;
    }

    @Required
    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }

    @Required
    public void setDatabasePlatform(DatabasePlatform databasePlatform) {
        this.databasePlatform = databasePlatform;
    }

    @Required
    public void setMaintenanceDocumentDaoOjb(MaintenanceDocumentDao maintenanceDocumentDaoOjb) {
        this.maintenanceDocumentDaoOjb = maintenanceDocumentDaoOjb;
    }

    //@Required
    //public void setNoteDaoOjb(NoteDao noteDaoOjb) {
    //    this.noteDaoOjb = noteDaoOjb;
    //}

    @Required
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    @Required
    public void setDataObjectMetaDataService(DataObjectMetaDataService dataObjectMetaDataService) {
        this.dataObjectMetaDataService = dataObjectMetaDataService;
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

    @Required
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public ViewDictionaryService getViewDictionaryService() {
        return viewDictionaryService;
    }

    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }

    /**
     * Holds a reference to a property Field on a JPA entity and the associated EclipseLink {@link org.eclipse.persistence.indirection.ValueHolder} field.
     *
     * <p>This exists to support the issue of using a data object with both OJB and JPA. EclipseLink will "instrument"
     * the entity class using load-time or static weaving (depending on what's been configured). For fields which are
     * FetchType.LAZY, EclipseLink will weave a private internal field with a name like "_persistence_propertyName_vh"
     * where "propertyName" is the name of the property which is lazy. This type of this field is {@link org.eclipse.persistence.indirection.ValueHolder}.
     * In this case, if you call getPropertyName for the property, the internal code will pull the value from the
     * ValueHolder instead of the actual property field. Oftentimes this will be ok because the two fields will be in
     * sync, but when using methods like OJB's retrieveReference and retrieveAllReferences, after the "retrieve" these
     * values will be out of sync. So the {@link #synchronizeValueHolder(Object)} method on this class can be used to
     * synchronize these values and ensure that the local field has a value which matches the ValueHolder.</p>
     */
    private static final class ValueHolderFieldPair {

        final Field field;
        final Field valueHolderField;

        ValueHolderFieldPair(Field field, Field valueHolderField) {
            this.field = field;
            this.valueHolderField = valueHolderField;
        }

        void synchronizeValueHolder(Object object) {
            try {
                ValueHolder valueHolder = (ValueHolder)valueHolderField.get(object);
                if(valueHolder != null){
                    Object value = field.get(object);
                    valueHolder.setValue(value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
