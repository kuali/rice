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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.io.SerializationUtils;
import org.kuali.rice.core.framework.persistence.jta.TransactionalNoValidationExceptionRollback;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.DataObjectAuthorizationService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * Service implementation for the MaintenanceDocument structure. This is the
 * default implementation, that is delivered with Kuali
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@TransactionalNoValidationExceptionRollback
public class MaintenanceDocumentServiceImpl implements MaintenanceDocumentService {
    private static final Logger LOG = Logger.getLogger(MaintenanceDocumentServiceImpl.class);

    protected LegacyDataAdapter legacyDataAdapter;
    protected DataObjectService dataObjectService;
    protected DataObjectAuthorizationService dataObjectAuthorizationService;
    protected DocumentService documentService;
    protected DocumentDictionaryService documentDictionaryService;

    /**
     * @see org.kuali.rice.krad.service.MaintenanceDocumentService#setupNewMaintenanceDocument(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
	@SuppressWarnings("unchecked")
    public MaintenanceDocument setupNewMaintenanceDocument(String objectClassName, String documentTypeName,
            String maintenanceAction) {
        if (StringUtils.isEmpty(objectClassName) && StringUtils.isEmpty(documentTypeName)) {
            throw new IllegalArgumentException("Document type name or bo class not given!");
        }

        // get document type if not passed
        if (StringUtils.isEmpty(documentTypeName)) {
            try {
                documentTypeName =
                        getDocumentDictionaryService().getMaintenanceDocumentTypeName(Class.forName(objectClassName));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (StringUtils.isEmpty(documentTypeName)) {
                throw new RuntimeException(
                        "documentTypeName is empty; does this Business Object have a maintenance document definition? " +
                                objectClassName);
            }
        }

        // check doc type allows new or copy if that action was requested
        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) ||
                KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
            Class<?> boClass =
                    getDocumentDictionaryService().getMaintenanceDataObjectClass(documentTypeName);
            boolean allowsNewOrCopy = getDataObjectAuthorizationService()
                    .canCreate(boClass, GlobalVariables.getUserSession().getPerson(), documentTypeName);
            if (!allowsNewOrCopy) {
                LOG.error("Document type " + documentTypeName + " does not allow new or copy actions.");
                throw new DocumentTypeAuthorizationException(
                        GlobalVariables.getUserSession().getPerson().getPrincipalId(), "newOrCopy", documentTypeName);
            }
        }

        // get new document from service
        try {
            return (MaintenanceDocument) getDocumentService().getNewDocument(documentTypeName);
        } catch (WorkflowException e) {
            LOG.error("Cannot get new maintenance document instance for doc type: " + documentTypeName, e);
            throw new RuntimeException("Cannot get new maintenance document instance for doc type: " + documentTypeName,
                    e);
        }
    }

    /**
     * @see org.kuali.rice.krad.service.impl.MaintenanceDocumentServiceImpl#setupMaintenanceObject
     */
    @Override
    public void setupMaintenanceObject(MaintenanceDocument document, String maintenanceAction,
            Map<String, String[]> requestParameters) {
        document.getNewMaintainableObject().setMaintenanceAction(maintenanceAction);
        document.getOldMaintainableObject().setMaintenanceAction(maintenanceAction);

        // if action is delete, check that object can be deleted
        if (KRADConstants.MAINTENANCE_DELETE_ACTION.equals(maintenanceAction))
        {
            checkMaintenanceActionAuthorization(document, document.getOldMaintainableObject(),
                    maintenanceAction, requestParameters);
        }

        // if action is edit or copy first need to retrieve the old record
        if (!KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) &&
                !KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
            Object oldDataObject = retrieveObjectForMaintenance(document, requestParameters);

            Object newDataObject = null;

            // TODO should we be using ObjectUtils?
            if (dataObjectService.supports(oldDataObject.getClass())) {
                newDataObject = dataObjectService.copyInstance(oldDataObject);
            } else {
                newDataObject = SerializationUtils.deepCopy((Serializable) oldDataObject);
            }

            // set object instance for editing
            document.getOldMaintainableObject().setDataObject(oldDataObject);
            document.getNewMaintainableObject().setDataObject(newDataObject);

            if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction) && !document.isFieldsClearedOnCopy()) {
                Maintainable maintainable = document.getNewMaintainableObject();

                // Since this will be a new object, we also need to blank out the object ID and version number fields
                // (if they exist).  If the object uses a different locking key or unique ID field, the blanking of
                // these will need to be done in the Maintainable.processAfterCopy() method.
                if ( maintainable.getDataObject() instanceof DataObjectBase ) {
                    ((DataObjectBase) maintainable.getDataObject()).setObjectId(null);
                    ((DataObjectBase) maintainable.getDataObject()).setVersionNumber(null);
                } else if ( maintainable.getDataObject() instanceof PersistableBusinessObject ) {
                    // Legacy KNS Support - since they don't use DataObjectBase
                    ((PersistableBusinessObject) maintainable.getDataObject()).setObjectId(null);
                    ((PersistableBusinessObject) maintainable.getDataObject()).setVersionNumber(null);
                } else {
                    // If neither then use reflection to see if the object has setVersionNumber and setObjectId methods
                   if(ObjectPropertyUtils.getWriteMethod(maintainable.getDataObject().getClass(), "versionNumber") != null) {
                        ObjectPropertyUtils.setPropertyValue(maintainable.getDataObject(), "versionNumber", null);
                   }

                   if(ObjectPropertyUtils.getWriteMethod(maintainable.getDataObject().getClass(), "objectId") != null) {
                        ObjectPropertyUtils.setPropertyValue(maintainable.getDataObject(), "objectId", null);
                   }
                }

                clearValuesForPropertyNames(newDataObject, maintainable.getDataObjectClass());

                if (!getDocumentDictionaryService().getPreserveLockingKeysOnCopy(maintainable.getDataObjectClass())) {
                    clearPrimaryKeyFields(newDataObject, maintainable.getDataObjectClass());
                }
            }

            checkMaintenanceActionAuthorization(document, oldDataObject, maintenanceAction, requestParameters);
        }

        // if new with existing we need to populate with passed in parameters
        if (KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
            Object newBO = document.getNewMaintainableObject().getDataObject();
            Map<String, String> parameters =
                    buildKeyMapFromRequest(requestParameters, document.getNewMaintainableObject().getDataObjectClass());
            ObjectPropertyUtils.copyPropertiesToObject(parameters, newBO);
            if (newBO instanceof PersistableBusinessObject) {
                ((PersistableBusinessObject) newBO).refresh();
            }

            document.getNewMaintainableObject().setupNewFromExisting(document, requestParameters);
        } else if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
            document.getNewMaintainableObject().processAfterNew(document, requestParameters);
        }
    }

    /**
     * For the edit and delete maintenance actions checks with the
     * <code>BusinessObjectAuthorizationService</code> to check whether the
     * action is allowed for the record data. In action is allowed invokes the
     * custom processing hook on the <code>Maintainble</code>.
     *
     * @param document - document instance for the maintenance object
     * @param oldBusinessObject - the old maintenance record
     * @param maintenanceAction - type of maintenance action requested
     * @param requestParameters - map of parameters from the request
     */
    protected void checkMaintenanceActionAuthorization(MaintenanceDocument document, Object oldBusinessObject,
            String maintenanceAction, Map<String, String[]> requestParameters) {
        if (KRADConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {
            boolean allowsEdit = getDataObjectAuthorizationService()
                    .canMaintain(oldBusinessObject, GlobalVariables.getUserSession().getPerson(),
                            document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            if (!allowsEdit) {
                LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() +
                        " does not allow edit actions.");
                throw new DocumentTypeAuthorizationException(
                        GlobalVariables.getUserSession().getPerson().getPrincipalId(), "edit",
                        document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            }

            // invoke custom processing method
            document.getNewMaintainableObject().processAfterEdit(document, requestParameters);
        } else if (KRADConstants.MAINTENANCE_DELETE_ACTION.equals(maintenanceAction)) {
            boolean allowsDelete = getDataObjectAuthorizationService()
                    .canMaintain(oldBusinessObject, GlobalVariables.getUserSession().getPerson(),
                            document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

            if (!allowsDelete) {
                LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() +
                        " does not allow delete actions.");
                throw new DocumentTypeAuthorizationException(
                        GlobalVariables.getUserSession().getPerson().getPrincipalId(), "delete",
                        document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            }

            boolean dataObjectAllowsDelete = getDocumentDictionaryService().getAllowsRecordDeletion(
                    document.getOldMaintainableObject().getDataObject().getClass());

            if (!dataObjectAllowsDelete) {
                LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName() +
                        " does not allow delete actions.");
                GlobalVariables.getMessageMap().removeAllWarningMessagesForProperty(KRADConstants.GLOBAL_MESSAGES);
                GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS,
                        RiceKeyConstants.MESSAGE_DELETE_ACTION_NOT_SUPPORTED);

            }

        }
    }

    /**
     * For the edit or copy actions retrieves the record that is to be
     * maintained
     *
     * <p>
     * Based on the persistence metadata for the maintenance object class
     * retrieves the primary key values from the given request parameters map
     * (if the class is persistable). With those key values attempts to find the
     * record using the <code>LookupService</code>.
     * </p>
     *
     * @param document - document instance for the maintenance object
     * @param requestParameters - Map of parameters from the request
     * @return Object the retrieved old object
     */
    protected Object retrieveObjectForMaintenance(MaintenanceDocument document,
            Map<String, String[]> requestParameters) {
        Map<String, String> keyMap =
                buildKeyMapFromRequest(requestParameters, document.getNewMaintainableObject().getDataObjectClass());

        Object oldDataObject = document.getNewMaintainableObject().retrieveObjectForEditOrCopy(document, keyMap);

        if (oldDataObject == null && !document.getOldMaintainableObject().isExternalBusinessObject()) {
            throw new RuntimeException(
                    "Cannot retrieve old record for maintenance document, incorrect parameters passed on maint url: " +
                            requestParameters);
        }

        if (document.getOldMaintainableObject().isExternalBusinessObject()) {
            if (oldDataObject == null) {
                try {
                    oldDataObject = document.getOldMaintainableObject().getDataObjectClass().newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException(
                            "External BO maintainable was null and unable to instantiate for old maintainable object.",
                            ex);
                }
            }

            populateMaintenanceObjectWithCopyKeyValues(KRADUtils.translateRequestParameterMap(requestParameters),
                    oldDataObject, document.getOldMaintainableObject());
            document.getOldMaintainableObject().prepareExternalBusinessObject((PersistableBusinessObject) oldDataObject);
            oldDataObject = document.getOldMaintainableObject().getDataObject();
        }

        return oldDataObject;
    }

    /**
     * Clears the value of the primary key fields on the maintenance object
     *
     * @param maintenanceObject - document to clear the pk fields on
     * @param dataObjectClass - class to use for retrieving primary key metadata
     */
    protected void clearPrimaryKeyFields(Object maintenanceObject, Class<?> dataObjectClass) {
        List<String> keyFieldNames = legacyDataAdapter.listPrimaryKeyFieldNames(dataObjectClass);
        for (String keyFieldName : keyFieldNames) {
            ObjectPropertyUtils.setPropertyValue(maintenanceObject, keyFieldName, null);
        }
    }

    /**
     * Clears the value of the particular fields on the maintenance object
     *
     * @param maintenanceObject - document to clear the fields on
     * @param dataObjectClass - class to use for retrieving list of fields to clear
     */
    protected void clearValuesForPropertyNames(Object maintenanceObject, Class<?> dataObjectClass) {
        List<String> clearValueOnCopyPropertyNames = getDocumentDictionaryService().getClearValueOnCopyPropertyNames(
                dataObjectClass);

        for (String clearValueOnCopyPropertyName : clearValueOnCopyPropertyNames) {
            if (!StringUtils.contains(clearValueOnCopyPropertyName, ".")) {
                if (ObjectPropertyUtils.isWritableProperty(maintenanceObject, clearValueOnCopyPropertyName)) {
                    ObjectPropertyUtils.setPropertyValue(maintenanceObject, clearValueOnCopyPropertyName, null);
                }
            } else {
                String objectName = StringUtils.substringBeforeLast(clearValueOnCopyPropertyName, ".");
                String objectToClear = StringUtils.substringAfterLast(clearValueOnCopyPropertyName, ".");

                clearValuesInNestedObjects(objectName, maintenanceObject, objectToClear);
            }
        }
    }

    /**
     * Clears the value of objects in nested objects on the maintenance object
     *
     * @param objectName -  name of the object which contains the field to be cleared
     * @param maintenanceObject - Object to clear the fields on
     * @param objectToClear - the object to be cleared on the nested object
     */
    private void clearValuesInNestedObjects(String objectName, Object maintenanceObject, String objectToClear) {
        if (objectName.contains(".")) {
            String newObjectName = StringUtils.substringAfter(objectName, ".");
            objectName = StringUtils.substringBefore(objectName, ".");

            if (ObjectPropertyUtils.getPropertyValue(maintenanceObject, objectName) instanceof Collection<?>) {
                Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(maintenanceObject, objectName);

                for (Object object : collection) {
                    clearValuesInNestedObjects(newObjectName, object, objectToClear);
                }
            } else {
                Object object = ObjectPropertyUtils.getPropertyValue(maintenanceObject, objectName);
                clearValuesInNestedObjects(newObjectName, object, objectToClear);
            }
        } else {
            if (ObjectPropertyUtils.getPropertyValue(maintenanceObject, objectName) instanceof Collection<?>) {
                Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(maintenanceObject, objectName);

                for (Object object : collection) {
                    if (ObjectPropertyUtils.isWritableProperty(object, objectToClear)) {
                        ObjectPropertyUtils.setPropertyValue(object, objectToClear, null);
                    }
                }
            } else {
                Object object = ObjectPropertyUtils.getPropertyValue(maintenanceObject, objectName);

                if (ObjectPropertyUtils.isWritableProperty(object, objectToClear)) {
                    ObjectPropertyUtils.setPropertyValue(object, objectToClear, null);
                }
            }
        }
    }

    /**
     * Based on the maintenance object class retrieves the key field names from
     * the <code>BusinessObjectMetaDataService</code> (or alternatively from the
     * request parameters), then retrieves any matching key value pairs from the
     * request parameters
     *
     * @param requestParameters - map of parameters from the request
     * @param dataObjectClass - class to use for checking security parameter restrictions
     * @return Map<String, String> key value pairs
     */
    protected Map<String, String> buildKeyMapFromRequest(Map<String, String[]> requestParameters,
            Class<?> dataObjectClass) {
        List<String> keyFieldNames = null;

        // translate request parameters
        Map<String, String> parameters = KRADUtils.translateRequestParameterMap(requestParameters);

        // are override keys listed in the request? If so, then those need to be
        // our keys, not the primary key fields for the BO
        if (!StringUtils.isBlank(parameters.get(KRADConstants.OVERRIDE_KEYS))) {
            String[] overrideKeys =
                    parameters.get(KRADConstants.OVERRIDE_KEYS).split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            keyFieldNames = Arrays.asList(overrideKeys);
        } else {
            keyFieldNames = legacyDataAdapter.listPrimaryKeyFieldNames(dataObjectClass);
        }

        return KRADUtils.getParametersFromRequest(keyFieldNames, dataObjectClass, parameters);
    }

    /**
     * Looks for a special request parameters giving the names of the keys that
     * should be retrieved from the request parameters and copied to the
     * maintenance object
     *
     * @param parameters - map of parameters from the request
     * @param oldBusinessObject - the old maintenance object
     * @param oldMaintainableObject - the old maintainble object (used to get object class for
     * security checks)
     */
    protected void populateMaintenanceObjectWithCopyKeyValues(Map<String, String> parameters, Object oldBusinessObject,
            Maintainable oldMaintainableObject) {
        List<String> keyFieldNamesToCopy = null;
        Map<String, String> parametersToCopy = null;

        if (!StringUtils.isBlank(parameters.get(KRADConstants.COPY_KEYS))) {
            String[] copyKeys =
                    parameters.get(KRADConstants.COPY_KEYS).split(KRADConstants.FIELD_CONVERSIONS_SEPARATOR);
            keyFieldNamesToCopy = Arrays.asList(copyKeys);
            parametersToCopy = KRADUtils
                    .getParametersFromRequest(keyFieldNamesToCopy, oldMaintainableObject.getDataObjectClass(),
                            parameters);
        }

        if (parametersToCopy != null) {
            // TODO: make sure we are doing formatting here eventually
            ObjectPropertyUtils.copyPropertiesToObject(parametersToCopy, oldBusinessObject);
        }
    }

    /**
     * @see org.kuali.rice.krad.service.MaintenanceDocumentService#getLockingDocumentId(org.kuali.rice.krad.maintenance.MaintenanceDocument)
     */
    @Override
	public String getLockingDocumentId(MaintenanceDocument document) {
        return getLockingDocumentId(document.getNewMaintainableObject(), document.getDocumentNumber());
    }

    /**
     * @see org.kuali.rice.krad.service.MaintenanceDocumentService#getLockingDocumentId(org.kuali.rice.krad.maintenance.Maintainable,
     *      java.lang.String)
     */
    @Override
	public String getLockingDocumentId(Maintainable maintainable, final String documentNumber) {
        final List<MaintenanceLock> maintenanceLocks = maintainable.generateMaintenanceLocks();
        String lockingDocId = null;
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(),
                    documentNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }

        return lockingDocId;
    }

    protected String getLockingDocumentNumber(String lockingRepresentation, String documentNumber) {
        String lockingDocNumber = "";

        // build the query criteria
        List<Predicate> predicates = new ArrayList<Predicate>();
        predicates.add(PredicateFactory.equal("lockingRepresentation", lockingRepresentation));

        // if a docHeaderId is specified, then it will be excluded from the
        // locking representation test.
        if (StringUtils.isNotBlank(documentNumber)) {
            predicates.add(PredicateFactory.notEqual(KRADPropertyConstants.DOCUMENT_NUMBER, documentNumber));
        }

        QueryByCriteria.Builder qbc = QueryByCriteria.Builder.create();
        qbc.setPredicates(PredicateFactory.and(predicates.toArray(new Predicate[predicates.size()])));

        // attempt to retrieve a document based off this criteria
        List<MaintenanceLock> results = KradDataServiceLocator.getDataObjectService().findMatching(MaintenanceLock.class, qbc.build())
                .getResults();
        if (results.size() > 1) {
            throw new IllegalStateException(
                    "Expected single result querying for MaintenanceLock. LockRep: " + lockingRepresentation);
        }

        // if a document was found, then there's already one out there pending,
        // and we consider it 'locked' and we return the docnumber.
        if (!results.isEmpty()) {
            lockingDocNumber = results.get(0).getDocumentNumber();
        }
        return lockingDocNumber;
    }

    /**
     * @see org.kuali.rice.krad.service.MaintenanceDocumentService#deleteLocks(String)
     */
    @Override
	public void deleteLocks(String documentNumber) {
        dataObjectService.deleteMatching(MaintenanceLock.class, QueryByCriteria.Builder.forAttribute(
                "documentNumber", documentNumber).build());
    }

    /**
     * @see org.kuali.rice.krad.service.MaintenanceDocumentService#storeLocks(java.util.List)
     */
    @Override
	public void storeLocks(List<MaintenanceLock> maintenanceLocks) {
        if (maintenanceLocks == null) {
            return;
        }
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            dataObjectService.save(maintenanceLock);
        }
    }

    protected DataObjectAuthorizationService getDataObjectAuthorizationService() {
        return dataObjectAuthorizationService;
    }

    @Required
    public void setDataObjectAuthorizationService(DataObjectAuthorizationService dataObjectAuthorizationService) {
        this.dataObjectAuthorizationService = dataObjectAuthorizationService;
    }

    protected DocumentService getDocumentService() {
        return this.documentService;
    }

    @Required
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        return documentDictionaryService;
    }

    @Required
    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
		this.dataObjectService = dataObjectService;
	}

	@Required
    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

}
