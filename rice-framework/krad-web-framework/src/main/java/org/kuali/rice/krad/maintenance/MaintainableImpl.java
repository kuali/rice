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
package org.kuali.rice.krad.maintenance;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.data.CompoundKey;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.DataObjectWrapper;
import org.kuali.rice.krad.exception.PessimisticLockingException;
import org.kuali.rice.krad.rules.rule.event.AddCollectionLineEvent;
import org.kuali.rice.krad.service.DataObjectAuthorizationService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.service.ViewHelperService;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;

/**
 * Default implementation of the <code>Maintainable</code> interface
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@SuppressWarnings("deprecation")
public class MaintainableImpl extends ViewHelperServiceImpl implements Maintainable {
    private static final long serialVersionUID = 9125271369161634992L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintainableImpl.class);

    private String documentNumber;
    private Object dataObject;
    private Class<?> dataObjectClass;
    private String maintenanceAction;

    private transient LegacyDataAdapter legacyDataAdapter;
    private transient DataObjectAuthorizationService dataObjectAuthorizationService;
    private transient DocumentDictionaryService documentDictionaryService;
    private transient EncryptionService encryptionService;
    private transient DataObjectService dataObjectService;
    private transient MaintenanceDocumentService maintenanceDocumentService;
    private transient KualiRuleService kualiRuleService;

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#retrieveObjectForEditOrCopy(MaintenanceDocument, java.util.Map)
     */
    @Override
    public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {
        Object dataObject = null;
        if ( getDataObjectService().supports(getDataObjectClass())) {
            dataObject = getDataObjectService().find(getDataObjectClass(), new CompoundKey(dataObjectKeys));
        } else {
            try {
                dataObject = getLegacyDataAdapter().findObjectBySearch(getDataObjectClass(), dataObjectKeys);
            } catch (ClassNotPersistenceCapableException ex) {
                if (!document.getOldMaintainableObject().isExternalBusinessObject()) {
                    throw new RuntimeException("Data Object Class: "
                            + getDataObjectClass()
                            + " is not persistable and is not externalizable - configuration error");
                }
                // otherwise, let fall through
            }
        }

        return dataObject;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#setDocumentNumber
     */
    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#getDocumentTitle
     */
    @Override
    public String getDocumentTitle(MaintenanceDocument document) {
        // default implementation is to allow MaintenanceDocumentBase to
        // generate the doc title
        return "";
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#getDataObject
     */
    @Override
    public Object getDataObject() {
        return dataObject;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#setDataObject
     */
    @Override
    public void setDataObject(Object object) {
        this.dataObject = object;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#getDataObjectClass
     */
    @Override
    public Class<?> getDataObjectClass() {
        return dataObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#setDataObjectClass
     */
    @Override
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * Persistable business objects are lockable
     *
     * @deprecated note used by Rice framework
     */
    @Override
    @Deprecated
    public boolean isLockable() {
        return KRADServiceLocatorWeb.getLegacyDataAdapter().isLockable(getDataObject());
    }

    /**
     * Returns the data object if its persistable, null otherwise.
     *
     * @deprecated this method has been left for compatibility reasons, use getDataObject instead.
     */
    @Override
    @Deprecated // Uses KNS Classes
    public PersistableBusinessObject getPersistableBusinessObject() {
        return KRADServiceLocatorWeb.getLegacyDataAdapter().toPersistableBusinessObject(getDataObject());
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#getMaintenanceAction
     */
    @Override
    public String getMaintenanceAction() {
        return maintenanceAction;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#setMaintenanceAction
     */
    @Override
    public void setMaintenanceAction(String maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }

    /**
     * Note: as currently implemented, every key field for a given
     * data object class must have a visible getter
     *
     * @see org.kuali.rice.krad.maintenance.Maintainable#generateMaintenanceLocks
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        StringBuffer lockRepresentation = new StringBuffer(dataObjectClass.getName());
        lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_CLASS_DELIM);

        Object bo = getDataObject();
        DataObjectWrapper<Object> wrapper = getDataObjectService().wrap(bo);

        List<String> keyFieldNames = getDocumentDictionaryService().getLockingKeys(getDocumentTypeName());

        for (Iterator<?> i = keyFieldNames.iterator(); i.hasNext(); ) {
            String fieldName = (String) i.next();

            Object fieldValue = wrapper.getPropertyValueNullSafe(fieldName);
            if (fieldValue == null) {
                fieldValue = "";
            }

            // check if field is a secure
            if (getDataObjectAuthorizationService()
                    .attributeValueNeedsToBeEncryptedOnFormsAndLinks(dataObjectClass, fieldName)) {
                try {
                    if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                        fieldValue = getEncryptionService().encrypt(fieldValue);
                    }
                } catch (GeneralSecurityException e) {
                    LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
                    throw new RuntimeException(
                            "Unable to encrypt secure field for locking representation " + e.getMessage());
                }
            }

            lockRepresentation.append(fieldName);
            lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(String.valueOf(fieldValue));
            if (i.hasNext()) {
                lockRepresentation.append(KRADConstants.Maintenance.LOCK_AFTER_VALUE_DELIM);
            }
        }

        MaintenanceLock maintenanceLock = new MaintenanceLock();
        maintenanceLock.setDocumentNumber(documentNumber);
        maintenanceLock.setLockingRepresentation(lockRepresentation.toString());
        maintenanceLocks.add(maintenanceLock);

        return maintenanceLocks;
    }

    /**
     * Retrieves the document type name from the data dictionary based on
     * business object class
     */
    protected String getDocumentTypeName() {
        return getDocumentDictionaryService().getMaintenanceDocumentTypeName(dataObjectClass);
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#saveDataObject
     */
    @Override
    public void saveDataObject() {
        if ( dataObject == null ) {
            LOG.warn( "dataObject in maintainable was null - this should not be the case.  Skipping saveDataObject()");
            return;
        }
        dataObject = getLegacyDataAdapter().linkAndSave((Serializable)dataObject);
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#deleteDataObject
     */
    @Override
    public void deleteDataObject() {
        if (dataObject == null) {
            return;
        }
        getLegacyDataAdapter().delete(dataObject);
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#doRouteStatusChange
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        // no default implementation
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#getLockingDocumentId
     */
    @Override
    public String getLockingDocumentId() {
        return getMaintenanceDocumentService().getLockingDocumentId(this, documentNumber);
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#getWorkflowEngineDocumentIdsToLock
     */
    @Override
    public List<String> getWorkflowEngineDocumentIdsToLock() {
        return null;
    }

    /**
     * Default implementation simply returns false to indicate that custom
     * lock descriptors are not supported by MaintainableImpl. If custom
     * lock descriptors are needed, the appropriate subclasses should override
     * this method
     *
     * @see org.kuali.rice.krad.maintenance.Maintainable#useCustomLockDescriptors
     */
    @Override
    public boolean useCustomLockDescriptors() {
        return false;
    }

    /**
     * Default implementation just throws a PessimisticLockingException.
     * Subclasses of MaintainableImpl that need support for custom lock
     * descriptors should override this method
     *
     * @see org.kuali.rice.krad.maintenance.Maintainable#getCustomLockDescriptor
     */
    @Override
    public String getCustomLockDescriptor(Person user) {
        throw new PessimisticLockingException("The Maintainable for document " + documentNumber +
                " is using pessimistic locking with custom lock descriptors, but the Maintainable has not overridden the getCustomLockDescriptor method");
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#isNotesEnabled
     */
    @Override
    public boolean isNotesEnabled() {
        return getLegacyDataAdapter().areNotesSupported(dataObjectClass);
    }

    /**
     * @see org.kuali.rice.krad.maintenance.MaintainableImpl#isExternalBusinessObject
     */
    @Override
    public boolean isExternalBusinessObject() {
        return false;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.MaintainableImpl#prepareExternalBusinessObject
     */
    @Override
    @Deprecated
    public void prepareExternalBusinessObject(BusinessObject businessObject) {
        // by default do nothing
    }

    /**
     * Checks whether the data object is not null and has its primary key values populated
     *
     * @see org.kuali.rice.krad.maintenance.MaintainableImpl#isOldDataObjectInDocument
     */
    @Override
    public boolean isOldDataObjectInDocument() {
        boolean isOldDataObjectInExistence = true;

        if (getDataObject() == null) {
            isOldDataObjectInExistence = false;
        } else {
            Map<String, ?> keyFieldValues = getLegacyDataAdapter().getPrimaryKeyFieldValuesDOMDS(getDataObject());
            for (Object keyValue : keyFieldValues.values()) {
                if (keyValue == null) {
                    isOldDataObjectInExistence = false;
                } else if ((keyValue instanceof String) && StringUtils.isBlank((String) keyValue)) {
                    isOldDataObjectInExistence = false;
                }

                if (!isOldDataObjectInExistence) {
                    break;
                }
            }
        }

        return isOldDataObjectInExistence;
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#prepareForSave
     */
    @Override
    public void prepareForSave() {
        // by default do nothing
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#processAfterRetrieve
     */
    @Override
    public void processAfterRetrieve() {
        // by default do nothing
    }

    /**
     * @see org.kuali.rice.krad.maintenance.MaintainableImpl#setupNewFromExisting
     */
    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        // by default do nothing
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#processAfterCopy
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#processAfterEdit
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#processAfterNew
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * @see org.kuali.rice.krad.maintenance.Maintainable#processAfterPost
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        // by default do nothing
    }

    /**
     * In the case of edit maintenance adds a new blank line to the old side
     *
     * TODO: should this write some sort of missing message on the old side
     * instead?
     *
     */
    @Override
    public void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine,
            boolean isValidLine) {
        super.processAfterAddLine(view, collectionGroup, model, addLine, isValidLine);

        // Check for maintenance documents in edit but exclude notes and ad hoc recipients
        if (model instanceof MaintenanceDocumentForm
                && KRADConstants.MAINTENANCE_EDIT_ACTION.equals(
                ((MaintenanceDocumentForm) model).getMaintenanceAction())
                && !(addLine instanceof Note)
                && !(addLine instanceof AdHocRoutePerson)
                && !(addLine instanceof AdHocRouteWorkgroup)) {
            MaintenanceDocumentForm maintenanceForm = (MaintenanceDocumentForm) model;
            MaintenanceDocument document = maintenanceForm.getDocument();

            // get the old object's collection
            //KULRICE-7970 support multiple level objects
            String bindingPrefix = collectionGroup.getBindingInfo().getBindByNamePrefix();
            String propertyPath = collectionGroup.getPropertyName();
            if (bindingPrefix != "" && bindingPrefix != null) {
                propertyPath = bindingPrefix + "." + propertyPath;
            }

            Collection<Object> oldCollection = ObjectPropertyUtils.getPropertyValue(
                    document.getOldMaintainableObject().getDataObject(), propertyPath);

            try {
                Object blankLine = collectionGroup.getCollectionObjectClass().newInstance();
                //Add a blank line to the top of the collection
                if (oldCollection instanceof List) {
                    ((List<Object>) oldCollection).add(0, blankLine);
                } else {
                    oldCollection.add(blankLine);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to create new line instance for old maintenance object", e);
            }
        }
    }

    /**
     * In the case of edit maintenance deleted the item on the old side
     *
     *
     * @see org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl#processAfterDeleteLine(View,
     *      org.kuali.rice.krad.uif.container.CollectionGroup, java.lang.Object,  int)
     */
    @Override
    public void processAfterDeleteLine(View view, CollectionGroup collectionGroup, Object model, int lineIndex) {
        super.processAfterDeleteLine(view, collectionGroup, model, lineIndex);

        // Check for maintenance documents in edit but exclude notes and ad hoc recipients
        if (model instanceof MaintenanceDocumentForm
                && KRADConstants.MAINTENANCE_EDIT_ACTION.equals(((MaintenanceDocumentForm)model).getMaintenanceAction())
                && !collectionGroup.getCollectionObjectClass().getName().equals(Note.class.getName())
                && !collectionGroup.getCollectionObjectClass().getName().equals(AdHocRoutePerson.class.getName())
                && !collectionGroup.getCollectionObjectClass().getName().equals(AdHocRouteWorkgroup.class.getName())) {
            MaintenanceDocumentForm maintenanceForm = (MaintenanceDocumentForm) model;
            MaintenanceDocument document = maintenanceForm.getDocument();

            // get the old object's collection
            Collection<Object> oldCollection = ObjectPropertyUtils
                    .getPropertyValue(document.getOldMaintainableObject().getDataObject(),
                            collectionGroup.getPropertyName());
            try {
                // Remove the object at lineIndex from the collection
                oldCollection.remove(oldCollection.toArray()[lineIndex]);
            } catch (Exception e) {
                throw new RuntimeException("Unable to delete line instance for old maintenance object", e);
            }
        }
    }

    @Override
    protected boolean performAddLineValidation(View view, CollectionGroup collectionGroup, Object model, Object addLine) {
        boolean isValidLine = super.performAddLineValidation(view, collectionGroup, model, addLine);

        if (model instanceof MaintenanceDocumentForm) {
            MaintenanceDocumentForm form = ((MaintenanceDocumentForm) model);
            isValidLine &= getKualiRuleService()
                    .applyRules(new AddCollectionLineEvent(form.getDocument(), collectionGroup.getPropertyName(), addLine));
        }

        return isValidLine;
    }

    /**
     * Retrieves the document number configured on this maintainable
     *
     * @return String document number
     */
    protected String getDocumentNumber() {
        return this.documentNumber;
    }

    /**
     * For the copy action, clears out primary key values and replaces any new fields that the current user is
     * unauthorized for with default values in the old record.
     *
     * {@inheritDoc}
     */
    @Override
    public void performCustomFinalize(Component component, Object model, Component parent) {
        if (!(model instanceof MaintenanceDocumentForm)) {
            return;
        }

        MaintenanceDocumentForm form = (MaintenanceDocumentForm) model;

        if (form.getDocument().isFieldsClearedOnCopy()) {
            return;
        }

        if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(form.getMaintenanceAction())) {
            View view = ViewLifecycle.getView();

            if (component instanceof DataField) {
                DataField field = (DataField) component;

                clearUnauthorizedField(view, form, field);
            } else if (component instanceof CollectionGroup) {
                CollectionGroup group = (CollectionGroup) component;

                clearUnauthorizedLine(view, form, group);
            }
        }
    }

    /**
     * For the copy action, runs the custom processing after the copy and sets the indicator that fields have been
     * copied as true.
     *
     * {@inheritDoc}
     */
    @Override
    public void performCustomViewFinalize(Object model) {
        if (!(model instanceof MaintenanceDocumentForm)) {
            return;
        }

        MaintenanceDocumentForm form = (MaintenanceDocumentForm) model;

        if (KRADConstants.MAINTENANCE_COPY_ACTION.equals(form.getMaintenanceAction())) {
            processAfterCopy(form.getDocument(), form.getInitialRequestParameters());

            form.getDocument().setFieldsClearedOnCopy(true);
        }
    }

    /**
     * Determines if the current field is restricted and replaces its value with a default value if so.
     *
     * @param view view instance that contains the fields being checked
     * @param model model instance that contains the fields being checked
     * @param field field being checked for restrictions
     */
    private void clearUnauthorizedField(View view, ViewModel model, DataField field) {
        ViewHelperService helper = ViewLifecycle.getHelper();
        String bindingPath = field.getBindingInfo().getBindingPath();

        if (StringUtils.contains(bindingPath, KRADConstants.MAINTENANCE_NEW_MAINTAINABLE)) {
            // The field is restricted if it is hidden or read only
            boolean isRestricted = field.isHidden() || field.isReadOnly() || field.isApplyMask();

            // If just the field (not its containing line) is restricted, clear it out and apply default values
            if (isRestricted && !isLineRestricted(field)) {
                if (ObjectPropertyUtils.isWritableProperty(model, bindingPath)) {
                    ObjectPropertyUtils.setPropertyValue(model, bindingPath, null);
                }

                helper.populateDefaultValueForField(model, field, bindingPath);
            }
        }
    }

    /**
     * Returns whether a line that contains a field is restricted; that is, if the field is part of a group and that
     * group has some unauthorized binding information.
     *
     * @param field field being checked for restrictions
     *
     * @return true if the field is in a line with restrictions, false otherwise
     */
    private boolean isLineRestricted(DataField field) {
        CollectionGroup group = (CollectionGroup) MapUtils.getObject(field.getContext(),
                UifConstants.ContextVariableNames.COLLECTION_GROUP);

        return group != null && CollectionUtils.isNotEmpty(group.getUnauthorizedLineBindingInfos());
    }

    /**
     * Determines if the current group contains restricted lines and clears them if so.
     *
     * @param view view instance that contains the group being checked
     * @param model model instance that contains the group being checked
     * @param group group being checked for restrictions
     */
    private void clearUnauthorizedLine(View view, ViewModel model, CollectionGroup group) {
        String bindingPath = group.getBindingInfo().getBindingPath();

        if (StringUtils.contains(bindingPath, KRADConstants.MAINTENANCE_NEW_MAINTAINABLE)) {
            // A line is restricted if it is hidden or read only
            if (group.getUnauthorizedLineBindingInfos() != null) {
                Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, bindingPath);

                // If any lines are restricted, clear them out
                for (BindingInfo bindingInfo : group.getUnauthorizedLineBindingInfos()) {
                    String lineBindingPath = bindingInfo.getBindingPath();
                    Object line = ObjectPropertyUtils.getPropertyValue(model, lineBindingPath);

                    collection.remove(line);
                }
            }
        }
    }

    @Deprecated // KNS Service
    protected LegacyDataAdapter getLegacyDataAdapter() {
        if (legacyDataAdapter == null) {
            legacyDataAdapter = KRADServiceLocatorWeb.getLegacyDataAdapter();
        }
        return this.legacyDataAdapter;
    }

    @Deprecated // KNS Service
    public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
        this.legacyDataAdapter = legacyDataAdapter;
    }

    protected DataObjectAuthorizationService getDataObjectAuthorizationService() {
        if (dataObjectAuthorizationService == null) {
            this.dataObjectAuthorizationService = KRADServiceLocatorWeb.getDataObjectAuthorizationService();
        }
        return dataObjectAuthorizationService;
    }

    public void setDataObjectAuthorizationService(DataObjectAuthorizationService dataObjectAuthorizationService) {
        this.dataObjectAuthorizationService = dataObjectAuthorizationService;
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            this.documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    protected EncryptionService getEncryptionService() {
        if (encryptionService == null) {
            encryptionService = CoreApiServiceLocator.getEncryptionService();
        }
        return encryptionService;
    }

    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    protected DataObjectService getDataObjectService() {
        if (dataObjectService == null) {
            dataObjectService = KRADServiceLocator.getDataObjectService();
        }
        return dataObjectService;
    }

    protected MaintenanceDocumentService getMaintenanceDocumentService() {
        if (maintenanceDocumentService == null) {
            maintenanceDocumentService = KRADServiceLocatorWeb.getMaintenanceDocumentService();
        }
        return maintenanceDocumentService;
    }

    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }

    public KualiRuleService getKualiRuleService() {
        if (kualiRuleService == null) {
            kualiRuleService = KRADServiceLocatorWeb.getKualiRuleService();
        }
        return kualiRuleService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }
}
