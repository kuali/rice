package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;
import org.kuali.rice.krad.bo.Attachment;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.InactivatableFromTo;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.lookup.LookupUtils;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.LegacyUtils;
import org.springframework.beans.factory.annotation.Required;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * LegacyDataAdapter implementation.
 *
 * @deprecated instead of using this class, where possible go directly against new KRAD code
 */
@Deprecated
public class LegacyDataAdapterImpl implements LegacyDataAdapter {
    private LegacyDataAdapter kradLegacyDataAdapter;

    private DataDictionaryService dataDictionaryService;

    @Override
    public <T> T save(T dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().save(dataObject);
        } else {
            return kradLegacyDataAdapter.save(dataObject);
        }
    }

    @Override
    public <T> T linkAndSave(T dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return (T) getKnsLegacyDataAdapter().linkAndSave((PersistableBusinessObject) dataObject);
        } else {
            // Skipping validation.  This method is only used from MaintainableImpl
            return kradLegacyDataAdapter.linkAndSave(dataObject);
        }
    }

    @Override
    public <T> T saveDocument(T document) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(document)) {
            return getKnsLegacyDataAdapter().saveDocument(document);
        } else {
            return kradLegacyDataAdapter.saveDocument(document);
        }
    }

    @Override
    public <T> T findByPrimaryKey(Class<T> clazz, Map<String, ?> primaryKeys) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(clazz)) {
            return getKnsLegacyDataAdapter().findByPrimaryKey(clazz,primaryKeys);
        } else {
            return kradLegacyDataAdapter.findByPrimaryKey(clazz,primaryKeys);
        }
    }

    @Override
    public <T> T findBySinglePrimaryKey(Class<T> clazz, Object primaryKey) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(clazz)) {
            return getKnsLegacyDataAdapter().findBySinglePrimaryKey(clazz,primaryKey);
        } else {
            return kradLegacyDataAdapter.findBySinglePrimaryKey(clazz,primaryKey);
        }
    }

    @Override
    public void delete(Object dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            getKnsLegacyDataAdapter().delete(dataObject);
        } else {
            kradLegacyDataAdapter.delete(dataObject);
        }
    }

    @Override
    public void deleteMatching(Class<?> type, Map<String, ?> fieldValues) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            getKnsLegacyDataAdapter().deleteMatching(type,fieldValues);
        } else {
            kradLegacyDataAdapter.deleteMatching(type,fieldValues);
        }
    }

    @Override
    public <T> T retrieve(T dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().retrieve(dataObject);
        } else {
            return kradLegacyDataAdapter.retrieve(dataObject);
        }
    }

    @Override
    public <T> Collection<T> findAll(Class<T> clazz) {
        // just find all objects of given type without any attribute criteria
        return findMatching(clazz, Collections.<String, Object>emptyMap());
    }

    @Override
    public <T> Collection<T> findMatching(Class<T> clazz, Map<String, ?> fieldValues) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(clazz)) {
            return getKnsLegacyDataAdapter().findMatching(clazz,fieldValues);
        } else {
            return kradLegacyDataAdapter.findMatching(clazz,fieldValues);
        }
    }

    @Override
    public <T> Collection<T> findMatchingOrderBy(Class<T> clazz, Map<String, ?> fieldValues, String sortField,
            boolean sortAscending) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(clazz)) {
            return getKnsLegacyDataAdapter().findMatchingOrderBy(clazz,fieldValues,sortField,sortAscending);
        } else {
            return kradLegacyDataAdapter.findMatchingOrderBy(clazz,fieldValues,sortField,sortAscending);
        }
    }

    @Override
    public Map<String, ?> getPrimaryKeyFieldValues(Object dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().getPrimaryKeyFieldValues(dataObject);
        } else {
            return kradLegacyDataAdapter.getPrimaryKeyFieldValues(dataObject);
        }
    }

    @Override
    public void retrieveNonKeyFields(Object persistableObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(persistableObject)) {
            getKnsLegacyDataAdapter().retrieveNonKeyFields(persistableObject);
        } else {
            throw new UnsupportedOperationException("retrieveNonKeyFields not supported in KRAD");
        }
    }

    @Override
    public void retrieveReferenceObject(Object persistableObject, String referenceObjectName) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(persistableObject)) {
            getKnsLegacyDataAdapter().retrieveReferenceObject(persistableObject,referenceObjectName);
        } else {
            throw new UnsupportedOperationException("retrieveReferenceObject not supported in KRAD");
        }
    }

    @Override
    public void refreshAllNonUpdatingReferences(Object persistableObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(persistableObject)) {
            getKnsLegacyDataAdapter().refreshAllNonUpdatingReferences(persistableObject);
        } else {
            throw new UnsupportedOperationException("refreshAllNonUpdatingReferences not supported in KRAD");
        }
    }

    @Override
    public boolean isProxied(Object object) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(object)) {
            return getKnsLegacyDataAdapter().isProxied(object);
        } else {
            return kradLegacyDataAdapter.isProxied(object);
        }
    }

    @Override
    public Object resolveProxy(Object o) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(o)) {
            return getKnsLegacyDataAdapter().resolveProxy(o);
        }
        return kradLegacyDataAdapter.resolveProxy(o);
    }

    // Lookup methods

    @Override
    public <T> Collection<T> findCollectionBySearchHelper(Class<T> dataObjectClass, Map<String, String> formProperties,
            boolean unbounded, boolean allPrimaryKeyValuesPresentAndNotWildcard, Integer searchResultsLimit) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(dataObjectClass)) {
            return getKnsLegacyDataAdapter().findCollectionBySearchHelper(dataObjectClass, formProperties, unbounded,
                    allPrimaryKeyValuesPresentAndNotWildcard, searchResultsLimit);
        } else {
            return kradLegacyDataAdapter.findCollectionBySearchHelper(dataObjectClass, formProperties, unbounded,
                    allPrimaryKeyValuesPresentAndNotWildcard, searchResultsLimit);
        }
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
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            return getKnsLegacyDataAdapter().findObjectBySearch(type,formProps);
        } else {
            return kradLegacyDataAdapter.findObjectBySearch(type,formProps);
        }
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

    @Override
    public Attachment getAttachmentByNoteId(Long noteId) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(Attachment.class)) {
            return getKnsLegacyDataAdapter().getAttachmentByNoteId(noteId);
        } else {
            // noteIdentifier is the PK of Attachment, so just look up by PK
            return kradLegacyDataAdapter.getAttachmentByNoteId(noteId);
        }
    }

    @Override
    public DocumentHeader getByDocumentHeaderId(String id) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(DocumentHeader.class)) {
            return getKnsLegacyDataAdapter().getByDocumentHeaderId(id);
        } else {
            return kradLegacyDataAdapter.getByDocumentHeaderId(id);
        }
    }

    @Override
    @Deprecated
    public Class getDocumentHeaderBaseClass() {
        if (isKNSLoaded() && LegacyUtils.useLegacy(DocumentHeader.class)) {
            return getKnsLegacyDataAdapter().getDocumentHeaderBaseClass();
        } else {
            return kradLegacyDataAdapter.getDocumentHeaderBaseClass();
        }
    }

    @Override
    public void deleteLocks(String documentNumber) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(MaintenanceLock.class)) {
            getKnsLegacyDataAdapter().deleteLocks(documentNumber);
        } else {
            kradLegacyDataAdapter.deleteLocks(documentNumber);
        }
    }

    @Override
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(MaintenanceLock.class)) {
            return getKnsLegacyDataAdapter().getLockingDocumentNumber(lockingRepresentation,documentNumber);
        } else {
            return kradLegacyDataAdapter.getLockingDocumentNumber(lockingRepresentation,documentNumber);
        }
    }

    @Override
    public void storeLocks(List<MaintenanceLock> maintenanceLocks) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(MaintenanceLock.class)) {
            getKnsLegacyDataAdapter().storeLocks(maintenanceLocks);
        } else {
            kradLegacyDataAdapter.storeLocks(maintenanceLocks);
        }
    }

    @Override
    public List<String> listPrimaryKeyFieldNames(Class<?> type) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            return getKnsLegacyDataAdapter().listPrimaryKeyFieldNames(type);
        } else {
            return kradLegacyDataAdapter.listPrimaryKeyFieldNames(type);
        }
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
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            return getKnsLegacyDataAdapter().listPrimaryKeyFieldNames(type);
        } else {
            return kradLegacyDataAdapter.listPrimaryKeyFieldNames(type);
        }
    }

    @Override
    public Class<?> determineCollectionObjectType(Class<?> containingType, String collectionPropertyName) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(containingType)) {
            return getKnsLegacyDataAdapter().determineCollectionObjectType(containingType,collectionPropertyName);
        } else {
            return kradLegacyDataAdapter.determineCollectionObjectType(containingType,collectionPropertyName);
        }
    }

    @Override
    public Map<String, ?> getPrimaryKeyFieldValuesDOMDS(Object dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().getPrimaryKeyFieldValuesDOMDS(dataObject);
        }
        return kradLegacyDataAdapter.getPrimaryKeyFieldValuesDOMDS(dataObject);
    }

    @Override
    public boolean equalsByPrimaryKeys(Object do1, Object do2) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(do1)) {
            return getKnsLegacyDataAdapter().equalsByPrimaryKeys(do1, do2);
        }
        return kradLegacyDataAdapter.equalsByPrimaryKeys(do1,do2);
    }

    @Override
    public PersistableBusinessObject toPersistableBusinessObject(Object object) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(object)) {
            return getKnsLegacyDataAdapter().toPersistableBusinessObject(object);
        }
        throw new IllegalArgumentException("Given object was not a PersistableBusinessObject");
    }

    @Override
    public void materializeAllSubObjects(Object object) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(object)) {
            getKnsLegacyDataAdapter().materializeAllSubObjects(object);
        } else {
            // for now, do nothing if this is not a legacy object, we'll eliminate the concept of materializing
            // sub objects in this fashion in the new data layer, will enter a jira to re-examine this
        }

    }

    @Override
    public Class<?> getPropertyType(Object object, String propertyName) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(object)) {
            return getKnsLegacyDataAdapter().getPropertyType(object,propertyName);
        }
        return kradLegacyDataAdapter.getPropertyType(object,propertyName);
    }

    @Override
    public boolean isLockable(Object object) {
        // only need to check if it's persistable for legacy business/data objects
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(object)) {
            return getKnsLegacyDataAdapter().isLockable(object);
        }
        return true;
    }

    @Override
    public void verifyVersionNumber(Object dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            getKnsLegacyDataAdapter().verifyVersionNumber(dataObject);
        } else {
            kradLegacyDataAdapter.verifyVersionNumber(dataObject);
        }
    }

    @Override
    public RemotableQuickFinder.Builder createQuickFinder(Class<?> containingClass, String attributeName) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(containingClass)) {
            return getKnsLegacyDataAdapter().createQuickFinder(containingClass,attributeName);
        }
        return kradLegacyDataAdapter.createQuickFinder(containingClass,attributeName);
    }

    @Override
    public boolean isReferenceUpdatable(Class<?> type, String referenceName) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            return getKnsLegacyDataAdapter().isReferenceUpdatable(type,referenceName);
        } else {
            return kradLegacyDataAdapter.isReferenceUpdatable(type,referenceName);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Class> listReferenceObjectFields(Class<?> type) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            return getKnsLegacyDataAdapter().listReferenceObjectFields(type);
        } else {
            return kradLegacyDataAdapter.listReferenceObjectFields(type);
        }
    }

    @Override
    public boolean isCollectionUpdatable(Class<?> type, String collectionName) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            return getKnsLegacyDataAdapter().isCollectionUpdatable(type,collectionName);
        } else {
            return kradLegacyDataAdapter.isCollectionUpdatable(type,collectionName);
        }

    }

    @Override
    public Map<String, Class> listCollectionObjectTypes(Class<?> type) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(type)) {
            return getKnsLegacyDataAdapter().listCollectionObjectTypes(type);
        } else {
            return kradLegacyDataAdapter.listCollectionObjectTypes(type);
        }
    }

    @Override
    public BusinessObject getReferenceIfExists(BusinessObject bo, String referenceName) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(bo)) {
            return getKnsLegacyDataAdapter().getReferenceIfExists(bo,referenceName);
        }
        // TODO: Determine if we need to implement this...
        throw new UnsupportedOperationException("This function has not been implemented for JPA/krad-data objects.");
    }

    @Override
    public boolean allForeignKeyValuesPopulatedForReference(PersistableBusinessObject bo, String referenceName) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(bo)) {
            return getKnsLegacyDataAdapter().allForeignKeyValuesPopulatedForReference(bo,referenceName);
        }
        throw new UnsupportedOperationException("This function has not been implemented for JPA/krad-data objects.");
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
        if(isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)){
            return getKnsLegacyDataAdapter().getDataObjectIdentifierString(dataObject);
        } else {
            return kradLegacyDataAdapter.getDataObjectIdentifierString(dataObject);
        }
    }

    @Override
    public Class<?> getInquiryObjectClassIfNotTitle(Object dataObject, String propertyName) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().getInquiryObjectClassIfNotTitle(dataObject,propertyName);
        } else {
            return kradLegacyDataAdapter.getInquiryObjectClassIfNotTitle(dataObject,propertyName);
        }
    }

    @Override
    public Map<String, String> getInquiryParameters(Object dataObject, List<String> keys, String propertyName) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().getInquiryParameters(dataObject,keys,propertyName);
        } else {
            return kradLegacyDataAdapter.getInquiryParameters(dataObject,keys,propertyName);
        }
    }

    @Override
    public boolean hasLocalLookup(Class<?> dataObjectClass) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(dataObjectClass)) {
            return getKnsLegacyDataAdapter().hasLocalLookup(dataObjectClass);
        }
        return kradLegacyDataAdapter.hasLocalLookup(dataObjectClass);
    }

    @Override
    public boolean hasLocalInquiry(Class<?> dataObjectClass) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(dataObjectClass)) {
            return getKnsLegacyDataAdapter().hasLocalInquiry(dataObjectClass);
        }
        return kradLegacyDataAdapter.hasLocalInquiry(dataObjectClass);
    }

    @Override
    public org.kuali.rice.krad.bo.DataObjectRelationship getDataObjectRelationship(Object dataObject,
            Class<?> dataObjectClass, String attributeName, String attributePrefix, boolean keysOnly,
            boolean supportsLookup, boolean supportsInquiry) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(dataObjectClass)) {
            return getKnsLegacyDataAdapter().getDataObjectRelationship(dataObject,dataObjectClass,
                    attributeName,attributePrefix,keysOnly,supportsLookup,supportsInquiry);
        } else {
            return kradLegacyDataAdapter.getDataObjectRelationship(dataObject,dataObjectClass,
                    attributeName,attributePrefix,keysOnly,supportsLookup,supportsInquiry);
        }

    }

    public boolean isPersistable(Class<?> dataObjectClass) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(dataObjectClass)) {
            return getKnsLegacyDataAdapter().isPersistable(dataObjectClass);
        }  else {
            return kradLegacyDataAdapter.isPersistable(dataObjectClass);
        }
    }


    @Override
    public ForeignKeyFieldsPopulationState getForeignKeyFieldsPopulationState(Object dataObject, String referenceName) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().getForeignKeyFieldsPopulationState(dataObject,referenceName);
        } else {
            return kradLegacyDataAdapter.getForeignKeyFieldsPopulationState(dataObject,referenceName);
        }
    }

    @Override
    public Map<String, String> getForeignKeysForReference(Class<?> clazz, String attributeName) {
        if (isKNSLoaded() && LegacyUtils.useLegacy(clazz)) {
            return getKnsLegacyDataAdapter().getForeignKeysForReference(clazz,attributeName);
        } else {
            return kradLegacyDataAdapter.getForeignKeysForReference(clazz,attributeName);
        }
    }

    @Override
    public boolean hasPrimaryKeyFieldValues(Object dataObject) {
        if (isKNSLoaded() && LegacyUtils.useLegacyForObject(dataObject)) {
            return getKnsLegacyDataAdapter().hasPrimaryKeyFieldValues(dataObject);
        } else {
            return kradLegacyDataAdapter.hasPrimaryKeyFieldValues(dataObject);
        }
    }

    public void setObjectPropertyDeep(Object bo, String propertyName, Class type,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
        if(isKNSLoaded() && LegacyUtils.useLegacyForObject(bo)){
            getKnsLegacyDataAdapter().setObjectPropertyDeep(bo, propertyName, type, propertyValue);
        } else {
            kradLegacyDataAdapter.setObjectPropertyDeep(bo, propertyName, type, propertyValue);
        }

    }

    public Class materializeClassForProxiedObject(Object object){
        if(isKNSLoaded() && LegacyUtils.useLegacyForObject(object)){
              return getKnsLegacyDataAdapter().materializeClassForProxiedObject(object);
        } else {
            return kradLegacyDataAdapter.materializeClassForProxiedObject(object);
        }

    }

    public Object getNestedValue(Object bo, String fieldName){
        if(isKNSLoaded() && LegacyUtils.useLegacyForObject(bo)){
            return getKnsLegacyDataAdapter().getNestedValue(bo,fieldName);
        } else {
            return kradLegacyDataAdapter.getNestedValue(bo,fieldName);
        }
    }

    public Object createNewObjectFromClass(Class clazz){
        if(isKNSLoaded() && LegacyUtils.useLegacy(clazz)){
            return getKnsLegacyDataAdapter().createNewObjectFromClass(clazz);
        } else {
            return kradLegacyDataAdapter.createNewObjectFromClass(clazz);
        }
    }

    @Override
    public boolean isNull(Object object){
        if(isKNSLoaded() && LegacyUtils.useLegacyForObject(object)){
            return getKnsLegacyDataAdapter().isNull(object);
        } else {
            return kradLegacyDataAdapter.isNull(object);
        }
    }

    public void setObjectProperty(Object bo, String propertyName, Class propertyType,
            Object propertyValue) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
        if(isKNSLoaded() && LegacyUtils.useLegacyForObject(bo)){
            getKnsLegacyDataAdapter().setObjectProperty(bo,propertyName,propertyType,propertyValue);
        } else {
            kradLegacyDataAdapter.setObjectProperty(bo,propertyName,propertyType,propertyValue);
        }
    }

    public LegacyDataAdapter getKnsLegacyDataAdapter() {
        return GlobalResourceLoader.getService("knsLegacyDataAdapter");
    }

    public LegacyDataAdapter getKradLegacyDataAdapter() {
        return kradLegacyDataAdapter;
    }

    public void setKradLegacyDataAdapter(LegacyDataAdapter kradLegacyDataAdapter) {
        this.kradLegacyDataAdapter = kradLegacyDataAdapter;
    }

    @Required
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public boolean isKNSLoaded(){
        return GlobalResourceLoader.getService("knsLegacyDataAdapter") != null;
    }
}

