/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.web.spring.controller;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.ClassNotPersistenceCapableException;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.exception.DocumentTypeAuthorizationException;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.MaintenanceUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.spring.form.MaintenanceForm;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
@Controller
@RequestMapping(value = "/maintenance")
public class MaintenanceDocumentController extends DocumentControllerBase {
    protected static final Logger LOG = Logger.getLogger(MaintenanceDocumentController.class);

    protected EncryptionService encryptionService;
    protected LookupService lookupService;
    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    
    public MaintenanceDocumentController() {
        super();
        encryptionService = KNSServiceLocator.getEncryptionService();
        lookupService = KNSServiceLocator.getLookupService();
        maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
    }
    
    @Override
    public MaintenanceForm createInitialForm(HttpServletRequest request) {
        return new MaintenanceForm();
    }
    
    
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute("KualiForm") MaintenanceForm form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        setupMaintenance(form, request, KNSConstants.MAINTENANCE_NEW_ACTION);
        
        return getUIFModelAndView(form);
    }
    
    protected void setupMaintenance(MaintenanceForm form, HttpServletRequest request, String maintenanceAction) throws Exception {
        MaintenanceDocument document = form.getDocument();
        
        // create a new document object, if required (on NEW object, or other reasons)
        if (document == null) {
            if (StringUtils.isEmpty(form.getBusinessObjectClassName()) && StringUtils.isEmpty(form.getDocTypeName())) {
                throw new IllegalArgumentException("Document type name or bo class not given!");
            }

            String documentTypeName = form.getDocTypeName();
            // get document type if not passed
            if (StringUtils.isEmpty(documentTypeName)) {
                documentTypeName = maintenanceDocumentDictionaryService.getDocumentTypeName(Class.forName(form.getBusinessObjectClassName()));
                form.setDocTypeName(documentTypeName);
            }

            if (StringUtils.isEmpty(documentTypeName)) {
                throw new RuntimeException("documentTypeName is empty; does this Business Object have a maintenance document definition? " + form.getBusinessObjectClassName());
            }

            // check doc type allows new or copy if that action was requested
            if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) || KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                Class<? extends BusinessObject> boClass = maintenanceDocumentDictionaryService.getBusinessObjectClass(documentTypeName);
                boolean allowsNewOrCopy = getBusinessObjectAuthorizationService().canCreate(boClass, GlobalVariables.getUserSession().getPerson(), documentTypeName);
                if (!allowsNewOrCopy) {
                    LOG.error("Document type " + documentTypeName + " does not allow new or copy actions.");
                    throw new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalId(), "newOrCopy", documentTypeName);
                }
            }

            // get new document from service
            document = (MaintenanceDocument) getDocumentService().getNewDocument(form.getDocTypeName());
            form.setDocument(document);
        }

        // retrieve business object from request parameters
        if (!(KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) && !(KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction))) {
            Map<String, String> requestParameters = buildKeyMapFromRequest(document.getNewMaintainableObject(), request);
            PersistableBusinessObject oldBusinessObject = null;
            try {
                oldBusinessObject = (PersistableBusinessObject) lookupService.findObjectBySearch(Class.forName(form.getBusinessObjectClassName()), requestParameters);
            } catch (ClassNotPersistenceCapableException ex) {
                if (!document.getOldMaintainableObject().isExternalBusinessObject()) {
                    throw new RuntimeException("BO Class: " + form.getBusinessObjectClassName() + " is not persistable and is not externalizable - configuration error");
                }
                // otherwise, let fall through
            }
            if (oldBusinessObject == null && !document.getOldMaintainableObject().isExternalBusinessObject()) {
                throw new RuntimeException("Cannot retrieve old record for maintenance document, incorrect parameters passed on maint url: " + requestParameters);
            }

            if (document.getOldMaintainableObject().isExternalBusinessObject()) {
                if (oldBusinessObject == null) {
                    try {
                        oldBusinessObject = (PersistableBusinessObject) document.getOldMaintainableObject().getBoClass().newInstance();
                    } catch (Exception ex) {
                        throw new RuntimeException("External BO maintainable was null and unable to instantiate for old maintainable object.", ex);
                    }
                }
                populateBOWithCopyKeyValues(request, oldBusinessObject, document.getOldMaintainableObject());
                document.getOldMaintainableObject().prepareBusinessObject(oldBusinessObject);
                oldBusinessObject = document.getOldMaintainableObject().getBusinessObject();
            }
            
            // removed some code here during port to krad that loaded jpa extensions

            // TODO should we be using ObjectUtils?  also, this needs dictionary enhancement to indicate fields to/not to copy
            PersistableBusinessObject newBusinessObject = (PersistableBusinessObject) ObjectUtils.deepCopy(oldBusinessObject);

            // set business object instance for editing
            document.getOldMaintainableObject().setBusinessObject(oldBusinessObject);
            document.getOldMaintainableObject().setBoClass(Class.forName(form.getBusinessObjectClassName()));
            document.getNewMaintainableObject().setBusinessObject(newBusinessObject);
            document.getNewMaintainableObject().setBoClass(Class.forName(form.getBusinessObjectClassName()));

            // on a COPY, clear any fields that this user isnt authorized for, and also
            // clear the primary key fields
            if (KNSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {
                if (!document.isFieldsClearedOnCopy()) {
                    // for issue KULRice 3072
                    Class<?> boClass = maintenanceDocumentDictionaryService.getBusinessObjectClass(form.getDocTypeName());
                    if (!maintenanceDocumentDictionaryService.getPreserveLockingKeysOnCopy(boClass)) {
                        clearPrimaryKeyFields(document);
                    }

                    clearUnauthorizedNewFields(document);

                    Maintainable maintainable = document.getNewMaintainableObject();

                    maintainable.processAfterCopy(document, request.getParameterMap());

                    // mark so that this clearing doesnt happen again
                    document.setFieldsClearedOnCopy(true);

                    // mark so that blank required fields will be populated with default values
                    maintainable.setGenerateBlankRequiredValues(form.getDocTypeName());
                }
            } else if (KNSConstants.MAINTENANCE_EDIT_ACTION.equals(maintenanceAction)) {
                boolean allowsEdit = getBusinessObjectAuthorizationService().canMaintain(oldBusinessObject, GlobalVariables.getUserSession().getPerson(), document.getDocumentHeader().getWorkflowDocument().getDocumentType());
                if (!allowsEdit) {
                    LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentType() + " does not allow edit actions.");
                    throw new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalId(), "edit", document.getDocumentHeader().getWorkflowDocument().getDocumentType());
                }
                document.getNewMaintainableObject().processAfterEdit(document, request.getParameterMap());
            }
            // 3070
            else if (KNSConstants.MAINTENANCE_DELETE_ACTION.equals(maintenanceAction)) {
                boolean allowsDelete = getBusinessObjectAuthorizationService().canMaintain(oldBusinessObject, GlobalVariables.getUserSession().getPerson(), document.getDocumentHeader().getWorkflowDocument().getDocumentType());
                if (!allowsDelete) {
                    LOG.error("Document type " + document.getDocumentHeader().getWorkflowDocument().getDocumentType() + " does not allow delete actions.");
                    throw new DocumentTypeAuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalId(), "delete", document.getDocumentHeader().getWorkflowDocument().getDocumentType());
                }
                // document.getNewMaintainableObject().processAfterEdit( document, request.getParameterMap() );
            }
        }
        
        // if new with existing we need to populate we need to populate with passed in parameters
        if (KNSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION.equals(maintenanceAction)) {
            // TODO: this code should be abstracted out into a helper
            // also is it a problem that we're not calling setGenerateDefaultValues? it blanked out the below values when I
            // did maybe we need a new generateDefaultValues that doesn't overwrite?
            PersistableBusinessObject newBO = document.getNewMaintainableObject().getBusinessObject();
            Map<String, String> parameters = buildKeyMapFromRequest(document.getNewMaintainableObject(), request);
            copyParametersToBO(parameters, newBO);
            newBO.refresh();
            document.getNewMaintainableObject().setupNewFromExisting(document, request.getParameterMap());
        }

        // for new maintainable need to pick up default values
        if (KNSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
            document.getNewMaintainableObject().setGenerateDefaultValues(form.getDocTypeName());
            document.getNewMaintainableObject().processAfterNew(document, request.getParameterMap());

            // If a maintenance lock exists, warn the user.
            MaintenanceUtils.checkForLockingDocument(document.getNewMaintainableObject(), false);
        }

        // set maintenance action state
        document.getNewMaintainableObject().setMaintenanceAction(maintenanceAction);
        form.setMaintenanceAction(maintenanceAction);

        // attach any extra JS from the data dictionary
        if (LOG.isDebugEnabled()) {
            LOG.debug("maintenanceForm.getAdditionalScriptFiles(): " + form.getAdditionalScriptFiles());
        }
        if (form.getAdditionalScriptFiles().isEmpty()) {
            DocumentEntry docEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
            form.getAdditionalScriptFiles().addAll(docEntry.getWebScriptFiles());
        }

        // Retrieve notes topic display flag from data dictionary and add to document
        //      
        DocumentEntry entry = maintenanceDocumentDictionaryService.getMaintenanceDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentType());
        document.setDisplayTopicFieldInNotes(entry.getDisplayTopicFieldInNotes());

        // TODO should this return anything?
    }
    
    /**
     * This method clears the value of the primary key fields on a Business Object.
     * 
     * @param document - document to clear the pk fields on
     */
    protected void clearPrimaryKeyFields(MaintenanceDocument document) {
        // get business object being maintained and its keys
        PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();
        List<String> keyFieldNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(bo.getClass());

        // TODO anything in the new ModelUtils for this?
        BeanWrapper wrapper = new BeanWrapperImpl(bo);
        for (String keyFieldName : keyFieldNames) {
            try {
                // TODO are there formatters that set other values for null?
                wrapper.setPropertyValue(keyFieldName, null);
            }
            catch (Exception e) {
                LOG.error("Unable to clear primary key field: " + e.getMessage());
                throw new RuntimeException("Unable to clear primary key field: " + e.getMessage());
            }
        }
    }
    /**
     * This method is used as part of the Copy functionality, to clear any field values that the user making the copy does not have
     * permissions to modify. This will prevent authorization errors on a copy.
     * 
     * @param document - document to be adjusted
     */
    protected void clearUnauthorizedNewFields(MaintenanceDocument document) {
        // get a reference to the current user
        Person user = GlobalVariables.getUserSession().getPerson();

        // get a new instance of MaintenanceDocumentAuthorizations for this context
        MaintenanceDocumentRestrictions maintenanceDocumentRestrictions = getBusinessObjectAuthorizationService().getMaintenanceDocumentRestrictions(document, user);

        // get a reference to the newBo
        PersistableBusinessObject newBo = document.getNewMaintainableObject().getBusinessObject();

        document.getNewMaintainableObject().clearBusinessObjectOfRestrictedValues(maintenanceDocumentRestrictions);
    }
    
    protected void populateBOWithCopyKeyValues(HttpServletRequest request, PersistableBusinessObject oldBusinessObject, Maintainable oldMaintainableObject) throws Exception{
        List<String> keyFieldNamesToCopy = null;
        Map<String, String> parametersToCopy = null;
        
        if (!StringUtils.isBlank(request.getParameter(KNSConstants.COPY_KEYS))) {
            String[] copyKeys = request.getParameter(KNSConstants.COPY_KEYS).split(KNSConstants.FIELD_CONVERSIONS_SEPARATOR);
            keyFieldNamesToCopy = Arrays.asList(copyKeys);
            parametersToCopy = getRequestParameters(keyFieldNamesToCopy, oldMaintainableObject, request);
        }
        
        if(parametersToCopy != null && parametersToCopy.size()>0){
            copyParametersToBO(parametersToCopy, oldBusinessObject);
        }
    }
    
    protected void copyParametersToBO(Map<String, String> parameters, PersistableBusinessObject newBO) throws Exception{
        for (String parmName : parameters.keySet()) {
            String propertyValue = parameters.get(parmName);

            if (StringUtils.isNotBlank(propertyValue)) {
                String propertyName = parmName;
//*******************************************************************
// TODO need to interact with beanWrapper/dictionary/viewService here
//*******************************************************************
                // set value of property in bo
                if (PropertyUtils.isWriteable(newBO, propertyName)) {
                    Class<?> type = ObjectUtils.easyGetPropertyType(newBO, propertyName);
                    if (type != null && Formatter.getFormatter(type) != null) {
                        Formatter formatter = Formatter.getFormatter(type);
                        Object obj = formatter.convertFromPresentationFormat(propertyValue);
                        ObjectUtils.setObjectProperty(newBO, propertyName, obj.getClass(), obj);
                    }
                    else {
                        ObjectUtils.setObjectProperty(newBO, propertyName, String.class, propertyValue);
                    }
                }
            }
        }
    }
    
    /**
     * Gets keys for the maintainable business object from the persistence metadata explorer. Checks for existence of key property
     * names as request parameters, if found adds them to the returned hash map.
     */
    protected Map<String, String> buildKeyMapFromRequest(Maintainable maintainable, HttpServletRequest request) {
        List<String> keyFieldNames = null;
        
        // are override keys listed in the request? If so, then those need to be our keys,
        // not the primary key fields for the BO
        if (!StringUtils.isBlank(request.getParameter(KNSConstants.OVERRIDE_KEYS))) {
            String[] overrideKeys = request.getParameter(KNSConstants.OVERRIDE_KEYS).split(KNSConstants.FIELD_CONVERSIONS_SEPARATOR);
            keyFieldNames = Arrays.asList(overrideKeys);
        }
        else {
            keyFieldNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(maintainable.getBusinessObject().getClass());
        }
        return getRequestParameters(keyFieldNames, maintainable, request);
    }
    
    // TODO we should make this more general for use by all controllers
    protected Map<String, String> getRequestParameters(List<String> keyFieldNames, Maintainable maintainable, HttpServletRequest request){

        Map<String, String> requestParameters = new HashMap<String, String>();

        for (Iterator<String> iter = keyFieldNames.iterator(); iter.hasNext();) {
            String keyPropertyName = iter.next();

            if (request.getParameter(keyPropertyName) != null) {
                String keyValue = request.getParameter(keyPropertyName);

                // Check if this element was encrypted, if it was decrypt it
                if (getBusinessObjectAuthorizationService().attributeValueNeedsToBeEncryptedOnFormsAndLinks(maintainable.getBoClass(), keyPropertyName)) {
                    try {
                        keyValue = StringUtils.removeEnd(keyValue, EncryptionService.ENCRYPTION_POST_PREFIX);
                        keyValue = encryptionService.decrypt(keyValue);
                    }
                    catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                }

                requestParameters.put(keyPropertyName, keyValue);
            }
        }

        return requestParameters;
    }

}
