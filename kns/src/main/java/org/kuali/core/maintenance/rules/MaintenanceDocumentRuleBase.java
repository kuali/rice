/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.maintenance.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.authorization.FieldAuthorization;
import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.core.exceptions.UnknownDocumentIdException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.rule.AddCollectionLineRule;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.DocumentRuleBase;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.ForeignKeyFieldsPopulationState;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class contains all of the business rules that are common to all maintenance documents.
 * 
 * 
 */
public class MaintenanceDocumentRuleBase extends DocumentRuleBase implements MaintenanceDocumentRule, AddCollectionLineRule {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceDocumentRuleBase.class);

    // public static final String CHART_MAINTENANCE_EDOC = "ChartMaintenanceEDoc";

    // these two constants are used to correctly prefix errors added to
    // the global errors
    public static final String MAINTAINABLE_ERROR_PREFIX = RiceConstants.MAINTENANCE_NEW_MAINTAINABLE;
    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String MAINTAINABLE_ERROR_PATH = DOCUMENT_ERROR_PREFIX + "newMaintainableObject";

    protected PersistenceStructureService persistenceStructureService;
    protected PersistenceService persistenceService;
    protected DataDictionaryService ddService;
    protected BusinessObjectService boService;
    protected BusinessObjectDictionaryService boDictionaryService;
    protected DictionaryValidationService dictionaryValidationService;
    protected KualiConfigurationService configService;
    protected DocumentAuthorizationService documentAuthorizationService;
    protected MaintenanceDocumentDictionaryService maintDocDictionaryService;
    protected WorkflowDocumentService workflowDocumentService;
    protected UniversalUserService universalUserService;

    private PersistableBusinessObject oldBo;
    private PersistableBusinessObject newBo;
    private Class boClass;

    private List priorErrorPath;

    /**
     * 
     * Default constructor a MaintenanceDocumentRuleBase.java.
     * 
     */
    public MaintenanceDocumentRuleBase() {

        priorErrorPath = new ArrayList();

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringServiceLocator, and configure the bean defs for spring.
        try {
            this.setPersistenceStructureService(KNSServiceLocator.getPersistenceStructureService());
            this.setDdService(KNSServiceLocator.getDataDictionaryService());
            this.setPersistenceService(KNSServiceLocator.getPersistenceService());
            this.setBoService(KNSServiceLocator.getBusinessObjectService());
            this.setBoDictionaryService(KNSServiceLocator.getBusinessObjectDictionaryService());
            this.setDictionaryValidationService(KNSServiceLocator.getDictionaryValidationService());
            this.setConfigService(KNSServiceLocator.getKualiConfigurationService());
            this.setDocumentAuthorizationService(KNSServiceLocator.getDocumentAuthorizationService());
            this.setMaintDocDictionaryService(KNSServiceLocator.getMaintenanceDocumentDictionaryService());
            this.setWorkflowDocumentService(KNSServiceLocator.getWorkflowDocumentService());
            this.setUniversalUserService( KNSServiceLocator.getUniversalUserService() );
        } catch ( Exception ex ) {
            // do nothing, avoid blowing up if called prior to spring initialization
        }
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#processSaveDocument(org.kuali.core.document.Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        // remove all items from the errorPath temporarily (because it may not
        // be what we expect, or what we need)
        clearErrorPath();

        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(maintenanceDocument);

        // the document must be in a valid state for saving. this does not include business
        // rules, but just enough testing that the document is populated and in a valid state
        // to not cause exceptions when saved. if this passes, then the save will always occur,
        // regardless of business rules.
        if (!isDocumentValidForSave(maintenanceDocument)) {
            resumeErrorPath();
            return false;
        }

        // apply rules that are specific to the class of the maintenance document
        // (if implemented). this will always succeed if not overloaded by the
        // subclass
        processCustomSaveDocumentBusinessRules(maintenanceDocument);

        // return the original set of items to the errorPath
        resumeErrorPath();

        // return the original set of items to the errorPath, to ensure no impact
        // on other upstream or downstream items that rely on the errorPath
        return true;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#processRouteDocument(org.kuali.core.document.Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        LOG.info("processRouteDocument called");

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        // remove all items from the errorPath temporarily (because it may not
        // be what we expect, or what we need)
        clearErrorPath();

        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(maintenanceDocument);

        // apply rules that are common across all maintenance documents, regardless of class
        processGlobalSaveDocumentBusinessRules(maintenanceDocument);

        // from here on, it is in a default-success mode, and will route unless one of the
        // business rules stop it.
        boolean success = true;

        // apply rules that are common across all maintenance documents, regardless of class
        success &= processGlobalRouteDocumentBusinessRules(maintenanceDocument);

        // apply rules that are specific to the class of the maintenance document
        // (if implemented). this will always succeed if not overloaded by the
        // subclass
        success &= processCustomRouteDocumentBusinessRules(maintenanceDocument);

        // return the original set of items to the errorPath, to ensure no impact
        // on other upstream or downstream items that rely on the errorPath
        resumeErrorPath();

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#processApproveDocument(ApproveDocumentEvent)
     */
    @Override
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) approveEvent.getDocument();

        // remove all items from the errorPath temporarily (because it may not
        // be what we expect, or what we need)
        clearErrorPath();

        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(maintenanceDocument);

        // apply rules that are common across all maintenance documents, regardless of class
        processGlobalSaveDocumentBusinessRules(maintenanceDocument);

        // from here on, it is in a default-success mode, and will approve unless one of the
        // business rules stop it.
        boolean success = true;

        // apply rules that are common across all maintenance documents, regardless of class
        success &= processGlobalApproveDocumentBusinessRules(maintenanceDocument);

        // apply rules that are specific to the class of the maintenance document
        // (if implemented). this will always succeed if not overloaded by the
        // subclass
        success &= processCustomApproveDocumentBusinessRules(maintenanceDocument);

        // return the original set of items to the errorPath, to ensure no impact
        // on other upstream or downstream items that rely on the errorPath
        resumeErrorPath();

        return success;
    }

    /**
     * 
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
     * applicable to the whole document).
     * 
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * 
     */
    protected void putGlobalError(String errorConstant) {
        if (!errorAlreadyExists(RiceConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(RiceConstants.DOCUMENT_ERRORS, errorConstant);
        }
    }

    /**
     * 
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
     * applicable to the whole document).
     * 
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Replacement value for part of the error message.
     * 
     */
    protected void putGlobalError(String errorConstant, String parameter) {
        if (!errorAlreadyExists(RiceConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(RiceConstants.DOCUMENT_ERRORS, errorConstant, parameter);
        }
    }

    /**
     * 
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field, but
     * applicable to the whole document).
     * 
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of replacement values for part of the error message.
     * 
     */
    protected void putGlobalError(String errorConstant, String[] parameters) {
        if (!errorAlreadyExists(RiceConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(RiceConstants.DOCUMENT_ERRORS, errorConstant, parameters);
        }
    }

    /**
     * 
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     * 
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * 
     */
    protected void putFieldError(String propertyName, String errorConstant) {
        if (!errorAlreadyExists(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant);
        }
    }

    /**
     * 
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     * 
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Single parameter value that can be used in the message so that you can display specific values to the
     *        user.
     * 
     */
    protected void putFieldError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant, parameter);
        }
    }

    /**
     * 
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     * 
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of strings holding values that can be used in the message so that you can display specific values
     *        to the user.
     * 
     */
    protected void putFieldError(String propertyName, String errorConstant, String[] parameters) {
        if (!errorAlreadyExists(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant, parameters);
        }
    }

    /**
     * Adds a property-specific error to the global errors list, with the DD short label as the single argument.
     * 
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     */
    protected void putFieldErrorWithShortLabel(String propertyName, String errorConstant) {
        String shortLabel = ddService.getAttributeShortLabel(boClass, propertyName);
        putFieldError(propertyName, errorConstant, shortLabel);
    }

    /**
     * 
     * This method is a convenience method to add a property-specific document error to the global errors list. This method makes
     * sure that the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     * 
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Single parameter value that can be used in the message so that you can display specific values to the
     *        user.
     * 
     */
    protected void putDocumentError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(DOCUMENT_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameter);
        }
    }

    /**
     * 
     * This method is a convenience method to add a property-specific document error to the global errors list. This method makes
     * sure that the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     * 
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as errored in
     *        the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of String parameters that can be used in the message so that you can display specific values to the
     *        user.
     * 
     */
    protected void putDocumentError(String propertyName, String errorConstant, String[] parameters) {
        GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameters);
    }

    /**
     * 
     * Convenience method to determine whether the field already has the message indicated.
     * 
     * This is useful if you want to suppress duplicate error messages on the same field.
     * 
     * @param propertyName - propertyName you want to test on
     * @param errorConstant - errorConstant you want to test
     * @return returns True if the propertyName indicated already has the errorConstant indicated, false otherwise
     * 
     */
    private boolean errorAlreadyExists(String propertyName, String errorConstant) {

        if (GlobalVariables.getErrorMap().fieldHasMessage(propertyName, errorConstant)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 
     * This method specifically doesn't put any prefixes before the error so that the developer can do things specific to the
     * globals errors (like newDelegateChangeDocument errors)
     * 
     * @param propertyName
     * @param errorConstant
     */
    protected void putGlobalsError(String propertyName, String errorConstant) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(propertyName, errorConstant);
        }
    }

    /**
     * 
     * This method specifically doesn't put any prefixes before the error so that the developer can do things specific to the
     * globals errors (like newDelegateChangeDocument errors)
     * 
     * @param propertyName
     * @param errorConstant
     * @param parameter
     */
    protected void putGlobalsError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(propertyName, errorConstant, parameter);
        }
    }

    /**
     * 
     * This method is used to deal with error paths that are not what we expect them to be. This method, along with
     * resumeErrorPath() are used to temporarily clear the errorPath, and then return it to the original state after the rule is
     * executed.
     * 
     * This method is called at the very beginning of rule enforcement and pulls a copy of the contents of the errorPath ArrayList
     * to a local arrayList for temporary storage.
     * 
     */
    protected void clearErrorPath() {

        // add all the items from the global list to the local list
        priorErrorPath.addAll(GlobalVariables.getErrorMap().getErrorPath());

        // clear the global list
        GlobalVariables.getErrorMap().getErrorPath().clear();

    }

    /**
     * 
     * This method is used to deal with error paths that are not what we expect them to be. This method, along with clearErrorPath()
     * are used to temporarily clear the errorPath, and then return it to the original state after the rule is executed.
     * 
     * This method is called at the very end of the rule enforcement, and returns the temporarily stored copy of the errorPath to
     * the global errorPath, so that no other classes are interrupted.
     * 
     */
    protected void resumeErrorPath() {
        // revert the global errorPath back to what it was when we entered this
        // class
        GlobalVariables.getErrorMap().getErrorPath().addAll(priorErrorPath);
    }

    /**
     * 
     * This method executes the DataDictionary Validation against the document.
     * 
     * @param document
     * @return true if it passes DD validation, false otherwise
     */
    protected boolean dataDictionaryValidate(MaintenanceDocument document) {

        LOG.debug("MaintenanceDocument validation beginning");

        // explicitly put the errorPath that the dictionaryValidationService requires
        GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");

        // document must have a newMaintainable object
        Maintainable newMaintainable = document.getNewMaintainableObject();
        if (newMaintainable == null) {
            GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");
            throw new ValidationException("Maintainable object from Maintenance Document '" + document.getDocumentTitle() + "' is null, unable to proceed.");
        }

        // document's newMaintainable must contain an object (ie, not null)
        PersistableBusinessObject businessObject = newMaintainable.getBusinessObject();
        if (businessObject == null) {
            GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject.");
            throw new ValidationException("Maintainable's component business object is null.");
        }
        
        // run required check from maintenance data dictionary
        maintDocDictionaryService.validateMaintenanceRequiredFields(document);
        
        //check for duplicate entries in collections if necessary
        maintDocDictionaryService.validateMaintainableCollectionsForDuplicateEntries(document);

        // run the DD DictionaryValidation (non-recursive)
        dictionaryValidationService.validateBusinessObject(businessObject,false);

        // do default (ie, mandatory) existence checks
        dictionaryValidationService.validateDefaultExistenceChecks(businessObject);

        // do apc checks
        dictionaryValidationService.validateApcRules(businessObject);
        

        // explicitly remove the errorPath we've added
        GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject");

        LOG.debug("MaintenanceDocument validation ending");
        return true;
    }

    /**
     * 
     * This method checks the two major cases that may violate primary key integrity.
     * 
     * 1. Disallow changing of the primary keys on an EDIT maintenance document. Other fields can be changed, but once the primary
     * keys have been set, they are permanent.
     * 
     * 2. Disallow creating a new object whose primary key values are already present in the system on a CREATE NEW maintenance
     * document.
     * 
     * This method also will add new Errors to the Global Error Map.
     * 
     * @param document - The Maintenance Document being tested.
     * @return Returns false if either test failed, otherwise returns true.
     * 
     */
    private boolean primaryKeyCheck(MaintenanceDocument document) {

        // default to success if no failures
        boolean success = true;
        Class boClass = document.getNewMaintainableObject().getBoClass();

        PersistableBusinessObject oldBo = document.getOldMaintainableObject().getBusinessObject();
        PersistableBusinessObject newBo = document.getNewMaintainableObject().getBusinessObject();

        // We dont do primaryKeyChecks on Global Business Object maintenance documents. This is
        // because it doesnt really make any sense to do so, given the behavior of Globals. When a
        // Global Document completes, it will update or create a new record for each BO in the list.
        // As a result, there's no problem with having existing BO records in the system, they will
        // simply get updated.
        if (newBo instanceof GlobalBusinessObject) {
            return success;
        }

        // fail and complain if the person has changed the primary keys on
        // an EDIT maintenance document.
        if (document.isEdit()) {
            if (!ObjectUtils.equalByKeys(oldBo, newBo)) { // this is a very handy utility on our ObjectUtils

                // add a complaint to the errors
                putDocumentError(RiceConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_PRIMARY_KEYS_CHANGED_ON_EDIT, getHumanReadablePrimaryKeyFieldNames(boClass));
                success &= false;
            }
        }

        // fail and complain if the person has selected a new object with keys that already exist
        // in the DB.
        else if (document.isNew()) {

            // get a map of the pk field names and values
            Map newPkFields = persistenceService.getPrimaryKeyFieldValues(newBo);

            // TODO: Good suggestion from Aaron, dont bother checking the DB, if all of the
            // objects PK fields dont have values. If any are null or empty, then
            // we're done. The current way wont fail, but it will make a wasteful
            // DB call that may not be necessary, and we want to minimize these.

            // attempt to do a lookup, see if this object already exists by these Primary Keys
            PersistableBusinessObject testBo = boService.findByPrimaryKey(boClass, newPkFields);

            // if the retrieve was successful, then this object already exists, and we need
            // to complain
            if (testBo != null) {
                putDocumentError(RiceConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_KEYS_ALREADY_EXIST_ON_CREATE_NEW, getHumanReadablePrimaryKeyFieldNames(boClass));
                success &= false;
            }
        }
        return success;
    }

    /**
     * 
     * This method creates a human-readable string of the class' primary key field names, as designated by the DataDictionary.
     * 
     * @param boClass
     * @return
     */
    private String getHumanReadablePrimaryKeyFieldNames(Class boClass) {

        String delim = "";
        StringBuffer pkFieldNames = new StringBuffer();

        // get a list of all the primary key field names, walk through them
        List pkFields = persistenceStructureService.getPrimaryKeys(boClass);
        for (Iterator iter = pkFields.iterator(); iter.hasNext();) {
            String pkFieldName = (String) iter.next();

            // use the DataDictionary service to translate field name into human-readable label
            String humanReadableFieldName = ddService.getAttributeLabel(boClass, pkFieldName);

            // append the next field
            pkFieldNames.append(delim + humanReadableFieldName);

            // separate names with commas after the first one
            if (delim.equalsIgnoreCase("")) {
                delim = ", ";
            }
        }

        return pkFieldNames.toString();
    }

    /**
     * This method enforces all business rules that are common to all maintenance documents which must be tested before doing an
     * approval.
     * 
     * It can be overloaded in special cases where a MaintenanceDocument has very special needs that would be contrary to what is
     * enforced here.
     * 
     * @param document - a populated MaintenanceDocument instance
     * @return true if the document can be approved, false if not
     */
    protected boolean processGlobalApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;

        // enforce authorization restrictions on fields
        success &= checkAuthorizationRestrictions(document);
        return success;
    }

    /**
     * 
     * This method enforces all business rules that are common to all maintenance documents which must be tested before doing a
     * route.
     * 
     * It can be overloaded in special cases where a MaintenanceDocument has very special needs that would be contrary to what is
     * enforced here.
     * 
     * @param document - a populated MaintenanceDocument instance
     * @return true if the document can be routed, false if not
     */
    protected boolean processGlobalRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        // require a document description field
        success &= checkEmptyDocumentField("documentHeader.financialDocumentDescription", document.getDocumentHeader().getFinancialDocumentDescription(), "Description");

        // enforce authorization restrictions on fields
        success &= checkAuthorizationRestrictions(document);
        return success;
    }

    /**
     * 
     * This method enforces all business rules that are common to all maintenance documents which must be tested before doing a
     * save.
     * 
     * It can be overloaded in special cases where a MaintenanceDocument has very special needs that would be contrary to what is
     * enforced here.
     * 
     * Note that although this method returns a true or false to indicate whether the save should happen or not, this result may not
     * be followed by the calling method. In other words, the boolean result will likely be ignored, and the document saved,
     * regardless.
     * 
     * @param document - a populated MaintenanceDocument instance
     * @return true if all business rules succeed, false if not
     */
    protected boolean processGlobalSaveDocumentBusinessRules(MaintenanceDocument document) {

        // default to success
        boolean success = true;

        // do generic checks that impact primary key violations
        primaryKeyCheck(document);

        // this is happening only on the processSave, since a Save happens in both the
        // Route and Save events.
        this.dataDictionaryValidate(document);

        // enforce authorization restrictions on fields
        checkAuthorizationRestrictions(document);

        return success;
    }

    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * 
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * This method should be overridden to provide custom rules for processing document approval.
     * 
     * @param document
     * @return booelan
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    // Document Validation Helper Methods

    /**
     * 
     * This method checks to see if the document is in a state that it can be saved without causing exceptions.
     * 
     * Note that Business Rules are NOT enforced here, only validity checks.
     * 
     * This method will only return false if the document is in such a state that routing it will cause RunTimeExceptions.
     * 
     * @param maintenanceDocument - a populated MaintenaceDocument instance.
     * @return boolean - returns true unless the object is in an invalid state.
     * 
     */
    protected boolean isDocumentValidForSave(MaintenanceDocument maintenanceDocument) {

        boolean success = true;
        
        success &= super.isDocumentOverviewValid(maintenanceDocument);
        success &= validateDocumentStructure((Document) maintenanceDocument);
        success &= validateMaintenanceDocument(maintenanceDocument);
        success &= validateGlobalBusinessObjectPersistable(maintenanceDocument);
        return success;
    }

    /**
     * 
     * This method makes sure the document itself is valid, and has the necessary fields populated to be routable.
     * 
     * This is not a business rules test, rather its a structure test to make sure that the document will not cause exceptions
     * before routing.
     * 
     * @param document - document to be tested
     * @return false if the document is missing key values, true otherwise
     */
    protected boolean validateDocumentStructure(Document document) {
        boolean success = true;

        // document must have a populated documentNumber
        String documentHeaderId = document.getDocumentNumber();
        if (documentHeaderId == null || StringUtils.isEmpty(documentHeaderId)) {
            throw new ValidationException("Document has no document number, unable to proceed.");
        }

        return success;
    }

    /**
     * 
     * This method checks to make sure the document is a valid maintenanceDocument, and has the necessary values populated such that
     * it will not cause exceptions in later routing or business rules testing.
     * 
     * This is not a business rules test.
     * 
     * @param maintenanceDocument - document to be tested
     * @return whether maintenance doc passes
     * @throws ValidationException
     */
    protected boolean validateMaintenanceDocument(MaintenanceDocument maintenanceDocument) {
        boolean success = true;
        Maintainable newMaintainable = maintenanceDocument.getNewMaintainableObject();

        // document must have a newMaintainable object
        if (newMaintainable == null) {
            throw new ValidationException("Maintainable object from Maintenance Document '" + maintenanceDocument.getDocumentTitle() + "' is null, unable to proceed.");
        }

        // document's newMaintainable must contain an object (ie, not null)
        if (newMaintainable.getBusinessObject() == null) {
            throw new ValidationException("Maintainable's component business object is null.");
        }

        // document's newMaintainable must contain a valid BusinessObject descendent
        if (!PersistableBusinessObject.class.isAssignableFrom(newMaintainable.getBoClass())) {
            throw new ValidationException("Maintainable's component object is not descended from BusinessObject.");
        }
        return success;
    }

    /**
     * 
     * This method checks whether this maint doc contains Global Business Objects, and if so, whether the GBOs are in a persistable
     * state. This will return false if this method determines that the GBO will cause a SQL Exception when the document is
     * persisted.
     * 
     * @param document
     * @return False when the method determines that the contained Global Business Object will cause a SQL Exception, and the
     *         document should not be saved. It will return True otherwise.
     * 
     */
    protected boolean validateGlobalBusinessObjectPersistable(MaintenanceDocument document) {
        boolean success = true;

        if (document.getNewMaintainableObject() == null) {
            return success;
        }
        if (document.getNewMaintainableObject().getBusinessObject() == null) {
            return success;
        }
        if (!(document.getNewMaintainableObject().getBusinessObject() instanceof GlobalBusinessObject)) {
            return success;
        }

        PersistableBusinessObject bo = (PersistableBusinessObject) document.getNewMaintainableObject().getBusinessObject();
        GlobalBusinessObject gbo = (GlobalBusinessObject) bo;
        return gbo.isPersistable();
    }

    /**
     * 
     * This method tests to make sure the MaintenanceDocument passed in is based on the class you are expecting.
     * 
     * It does this based on the NewMaintainableObject of the MaintenanceDocument.
     * 
     * @param document - MaintenanceDocument instance you want to test
     * @param clazz - class you are expecting the MaintenanceDocument to be based on
     * @return true if they match, false if not
     * 
     */
    protected boolean isCorrectMaintenanceClass(MaintenanceDocument document, Class clazz) {

        // disallow null arguments
        if (document == null || clazz == null) {
            throw new IllegalArgumentException("Null arguments were passed in.");
        }

        // compare the class names
        if (clazz.toString().equals(document.getNewMaintainableObject().getBoClass().toString())) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 
     * This method accepts an object, and attempts to determine whether it is empty by this method's definition.
     * 
     * OBJECT RESULT null false empty-string false whitespace false otherwise true
     * 
     * If the result is false, it will add an object field error to the Global Errors.
     * 
     * @param valueToTest - any object to test, usually a String
     * @param propertyName - the name of the property being tested
     * @return true or false, by the description above
     * 
     */
    protected boolean checkEmptyBOField(String propertyName, Object valueToTest, String parameter) {

        boolean success = true;

        success = checkEmptyValue(valueToTest);

        // if failed, then add a field error
        if (!success) {
            putFieldError(propertyName, RiceKeyConstants.ERROR_REQUIRED, parameter);
        }

        return success;
    }

    /**
     * 
     * This method accepts document field (such as , and attempts to determine whether it is empty by this method's definition.
     * 
     * OBJECT RESULT null false empty-string false whitespace false otherwise true
     * 
     * If the result is false, it will add document field error to the Global Errors.
     * 
     * @param valueToTest - any object to test, usually a String
     * @param propertyName - the name of the property being tested
     * @return true or false, by the description above
     * 
     */
    protected boolean checkEmptyDocumentField(String propertyName, Object valueToTest, String parameter) {
        boolean success = true;
        success = checkEmptyValue(valueToTest);
        if (!success) {
            putDocumentError(propertyName, RiceKeyConstants.ERROR_REQUIRED, parameter);
        }
        return success;
    }

    /**
     * 
     * This method accepts document field (such as , and attempts to determine whether it is empty by this method's definition.
     * 
     * OBJECT RESULT null false empty-string false whitespace false otherwise true
     * 
     * It will the result as a boolean
     * 
     * @param valueToTest - any object to test, usually a String
     * 
     */
    protected boolean checkEmptyValue(Object valueToTest) {
        boolean success = true;

        // if its not a string, only fail if its a null object
        if (valueToTest == null) {
            success = false;
        }
        else {
            // test for null, empty-string, or whitespace if its a string
            if (valueToTest instanceof String) {
                if (StringUtils.isBlank((String) valueToTest)) {
                    success = false;
                }
            }
        }

        return success;
    }

    /**
     * 
     * This method is used during debugging to dump the contents of the error map, including the key names. It is not used by the
     * application in normal circumstances at all.
     * 
     */
    private void showErrorMap() {

        if (GlobalVariables.getErrorMap().isEmpty()) {
            return;
        }

        for (Iterator i = GlobalVariables.getErrorMap().entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            TypedArrayList errorList = (TypedArrayList) e.getValue();
            for (Iterator j = errorList.iterator(); j.hasNext();) {
                ErrorMessage em = (ErrorMessage) j.next();

                if (em.getMessageParameters() == null) {
                    LOG.error(e.getKey().toString() + " = " + em.getErrorKey());
                }
                else {
                    LOG.error(e.getKey().toString() + " = " + em.getErrorKey() + " : " + em.getMessageParameters().toString());
                }
            }
        }

    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#setupBaseConvenienceObjects(MaintenanceDocument)
     */
    public void setupBaseConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldBo = (PersistableBusinessObject) document.getOldMaintainableObject().getBusinessObject();
        if (oldBo != null) {
            oldBo.refreshNonUpdateableReferences();
        }

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newBo = (PersistableBusinessObject) document.getNewMaintainableObject().getBusinessObject();
        newBo.refreshNonUpdateableReferences();

        boClass = document.getNewMaintainableObject().getBoClass();

        // call the setupConvenienceObjects in the subclass, if a subclass exists
        setupConvenienceObjects();
    }

    public void setupConvenienceObjects() {
        // should always be overriden by subclass
    }

    /**
     * 
     * This method ensures that any fields that are restricted by the Authorization system in the system are also enforced on the
     * back-end, otherwise form manipulation could bypass authorization rules.
     * 
     * This method will add errors to the Global ErrorMap if any problems are encountered.
     * 
     * @param document - the maintenance document being evaluated
     * @return true if no failures occurred, false otherwise
     */
    protected boolean checkAuthorizationRestrictions(MaintenanceDocument document) {

        // Note that this method does what may at first appear to be a highly efficient loading
        // of the document that is already loaded, and compares against that. This is done to handle
        // situations where someone along the chain of approvers has rights to modify some fields, but
        // later approvers do not have similar rights. This is how we've made sure that this person,
        // only during this modification of the document, has not changed any fields on the newBo side,
        // without wiring something into the Struts that somehow tells us that a field was modified.
        // There may indeed be better ways to do this, but make sure you're solving all the problems
        // here, including ones like this: KULCOA-924, KULCOA-884, etc

        boolean success = true;
        boolean changed = false;

        boolean isInitiator = false;
        boolean isApprover = false;

        Object oldValue = null;
        Object newValue = null;
        Object savedValue = null;

        KualiWorkflowDocument workflowDocument = null;
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        try {
                workflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(document.getDocumentNumber()), user);
        }
        catch (WorkflowException e) {
                throw new UnknownDocumentIdException("no document found for documentHeaderId '" + document.getDocumentNumber() + "'", e);
        }
        if (user.getPersonUserIdentifier().equalsIgnoreCase(workflowDocument.getInitiatorNetworkId())) {
            // if these are the same person then we know it is the initiator
            isInitiator = true;
        }
        else if (workflowDocument.isApprovalRequested()) {
            isApprover = true;
        }

        // get the correct documentAuthorizer for this document
        MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) documentAuthorizationService.getDocumentAuthorizer(document);

        // get a new instance of MaintenanceDocumentAuthorizations for this context
        MaintenanceDocumentAuthorizations auths = documentAuthorizer.getFieldAuthorizations(document, user);

        // load a temp copy of the document from the DB to compare to for changes
        MaintenanceDocument savedDoc = null;
        Maintainable savedNewMaintainable = null;
        PersistableBusinessObject savedNewBo = null;

        if (isApprover) {
            try {
                DocumentService docService = KNSServiceLocator.getDocumentService();
                    savedDoc = (MaintenanceDocument) docService.getByDocumentHeaderId(document.getDocumentNumber());
            }
            catch (WorkflowException e) {
                    throw new RuntimeException("A WorkflowException was thrown which prevented the loading of " + "the comparison document (" + document.getDocumentNumber() + ")", e);
            }

            // attempt to retrieve the BO, but leave it blank if it or any of the objects on the path
            // to it are blank
            if (savedDoc != null) {
                savedNewMaintainable = savedDoc.getNewMaintainableObject();
                if (savedNewMaintainable != null) {
                    savedNewBo = savedNewMaintainable.getBusinessObject();
                }
            }
        }

        // setup in-loop members
        FieldAuthorization fieldAuthorization = null;

        // walk through all the restrictions
        Collection restrictedFields = auths.getAuthFieldNames();
        for (Iterator iter = restrictedFields.iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();

            // get the specific field authorization structure
            fieldAuthorization = auths.getAuthFieldAuthorization(fieldName);

            // if there are any restrictions, then enforce them
            if (fieldAuthorization.isRestricted()) {
                // reset the changed flag
                changed = false;

                // new value should always be the same regardles of who is
                // making the request
                newValue = ObjectUtils.getNestedValue(newBo, fieldName);

                // first we need to handle the case of edit doc && initiator
                if (isInitiator && document.isEdit()) {
                    // old value must equal new value
                    oldValue = ObjectUtils.getNestedValue(oldBo, fieldName);
                }
                else if (isApprover && savedNewBo != null) {
                    oldValue = ObjectUtils.getNestedValue(savedNewBo, fieldName);
                }

                // check to make sure nothing has changed
                if (oldValue == null && newValue == null) {
                    changed = false;
                }
                else if ((oldValue == null && newValue != null) || (oldValue != null && newValue == null)) {
                    changed = true;
                }
                else if (oldValue != null && newValue != null) {
                    if (!oldValue.equals(newValue)) {
                        changed = true;
                    }
                }

                // if changed and a NEW doc, but the new value is the default value, then let it go
                // we dont allow changing to default values for EDIT docs though, only NEW
                if (changed && document.isNew()) {
                    String defaultValue = maintDocDictionaryService.getFieldDefaultValue(document.getNewMaintainableObject().getBoClass(), fieldName);

                    // get the string value of newValue
                    String newStringValue = newValue.toString();

                    // if the newValue is the default value, then ignore
                    if (newStringValue.equalsIgnoreCase(defaultValue)) {
                        changed = false;
                    }
                }

                // if anything has changed, complain
                if (changed) {
                    String humanReadableFieldName = ddService.getAttributeLabel(document.getNewMaintainableObject().getBoClass(), fieldName);
                    putFieldError(fieldName, RiceKeyConstants.ERROR_DOCUMENT_AUTHORIZATION_RESTRICTED_FIELD_CHANGED, humanReadableFieldName);
                    success &= false;
                }
            }
        }
        return success;
    }

    /**
     * 
     * This method checks to make sure that if the foreign-key fields for the given reference attributes have any fields filled out,
     * that all fields are filled out.
     * 
     * If any are filled out, but all are not, it will return false and add a global error message about the problem.
     * 
     * @param referenceName - The name of the reference object, whose foreign-key fields must be all-or-none filled out.
     * 
     * @return true if this is the case, false if not
     * 
     */
    protected boolean checkForPartiallyFilledOutReferenceForeignKeys(String referenceName) {

        boolean success;

        ForeignKeyFieldsPopulationState fkFieldsState;
        fkFieldsState = persistenceStructureService.getForeignKeyFieldsPopulationState(newBo, referenceName);

        // determine result
        if (fkFieldsState.isAnyFieldsPopulated() && !fkFieldsState.isAllFieldsPopulated()) {
            success = false;
        }
        else {
            success = true;
        }

        // add errors if appropriate
        if (!success) {

            // get the full set of foreign-keys
            List fKeys = new ArrayList(persistenceStructureService.getForeignKeysForReference(newBo.getClass(), referenceName).keySet());
            String fKeysReadable = consolidateFieldNames(fKeys, ", ").toString();

            // walk through the missing fields
            for (Iterator iter = fkFieldsState.getUnpopulatedFieldNames().iterator(); iter.hasNext();) {
                String fieldName = (String) iter.next();

                // get the human-readable name
                String fieldNameReadable = ddService.getAttributeLabel(newBo.getClass(), fieldName);

                // add a field error
                putFieldError(fieldName, RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_PARTIALLY_FILLED_OUT_REF_FKEYS, new String[] { fieldNameReadable, fKeysReadable });
            }
        }

        return success;
    }

    /**
     * 
     * This method is a shallow inverse wrapper around applyApcRule, it simply reverses the return value, for better readability in
     * an if test.
     * 
     * This method applies an APC rule based on the values provided.
     * 
     * It will throw an ApplicationParameterException if the APC Group and Parm do not exist in the system.
     * 
     * @param apcGroupName - The script or group name in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param parameterName - The name of the parm/rule in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param valueToTest - The String value to test against the APC rule. The value may be null or blank without throwing an error,
     *        but the rule will likely fail if null or blank.
     * @return True if the rule fails, False if the rule passes.
     * 
     */
    protected boolean apcRuleFails(String parameterNamespace, String parameterDetailTypeCode, String parameterName, String valueToTest) {
        if (applyApcRule(parameterNamespace, parameterDetailTypeCode, parameterName, valueToTest) == false) {
            return true;
        }
        return false;
    }

    /**
     * 
     * This method applies an APC rule based on the values provided.
     * 
     * It will throw an ApplicationParameterException if the APC Group and Parm do not exist in the system.
     * 
     * @param apcGroupName - The script or group name in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param parameterName - The name of the parm/rule in the APC system. If the value is null or blank, an IllegalArgumentException
     *        will be thrown.
     * @param valueToTest - The String value to test against the APC rule. The value may be null or blank without throwing an error,
     *        but the rule will likely fail if null or blank.
     * @return True if the rule passes, False if the rule fails.
     * 
     */
    protected boolean applyApcRule(String parameterNamespace, String parameterDetailTypeCode, String parameterName, String valueToTest) {

        // default to success
        boolean success = true;

        // apply the rule, see if it fails
        if (configService.failsRule(parameterNamespace, parameterDetailTypeCode, parameterName, valueToTest)) {
            success = false;
        }

        return success;
    }

    /**
     * 
     * This method turns a list of field property names, into a delimited string of the human-readable names.
     * 
     * @param fieldNames - List of fieldNames
     * @return A filled StringBuffer ready to go in an error message
     * 
     */
    private StringBuffer consolidateFieldNames(List fieldNames, String delimiter) {

        StringBuffer sb = new StringBuffer();

        // setup some vars
        boolean firstPass = true;
        String delim = "";

        // walk through the list
        for (Iterator iter = fieldNames.iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();

            // get the human-readable name
            // add the new one, with the appropriate delimiter
            sb.append(delim + ddService.getAttributeLabel(newBo.getClass(), fieldName));

            // after the first item, start using a delimiter
            if (firstPass) {
                delim = delimiter;
                firstPass = false;
            }
        }

        return sb;
    }

    /**
     * 
     * This method translates the passed in field name into a human-readable attribute label.
     * 
     * It assumes the existing newBO's class as the class to examine the fieldName for.
     * 
     * @param fieldName The fieldName you want a human-readable label for.
     * @return A human-readable label, pulled from the DataDictionary.
     * 
     */
    protected String getFieldLabel(String fieldName) {
        return ddService.getAttributeLabel(newBo.getClass(), fieldName) + "(" + ddService.getAttributeShortLabel(newBo.getClass(), fieldName) + ")";
    }

    /**
     * 
     * This method translates the passed in field name into a human-readable attribute label.
     * 
     * It assumes the existing newBO's class as the class to examine the fieldName for.
     * 
     * @param boClass The class to use in combination with the fieldName.
     * @param fieldName The fieldName you want a human-readable label for.
     * @return A human-readable label, pulled from the DataDictionary.
     * 
     */
    protected String getFieldLabel(Class boClass, String fieldName) {
        return ddService.getAttributeLabel(boClass, fieldName) + "(" + ddService.getAttributeShortLabel(boClass, fieldName) + ")";
    }

    /**
     * Gets the boDictionaryService attribute.
     * 
     * @return Returns the boDictionaryService.
     */
    protected final BusinessObjectDictionaryService getBoDictionaryService() {
        return boDictionaryService;
    }

    /**
     * Sets the boDictionaryService attribute value.
     * 
     * @param boDictionaryService The boDictionaryService to set.
     */
    public final void setBoDictionaryService(BusinessObjectDictionaryService boDictionaryService) {
        this.boDictionaryService = boDictionaryService;
    }

    /**
     * Gets the boService attribute.
     * 
     * @return Returns the boService.
     */
    protected final BusinessObjectService getBoService() {
        return boService;
    }

    /**
     * Sets the boService attribute value.
     * 
     * @param boService The boService to set.
     */
    public final void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    /**
     * Gets the configService attribute.
     * 
     * @return Returns the configService.
     */
    protected final KualiConfigurationService getConfigService() {
        return configService;
    }

    /**
     * Sets the configService attribute value.
     * 
     * @param configService The configService to set.
     */
    public final void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * Gets the ddService attribute.
     * 
     * @return Returns the ddService.
     */
    protected final DataDictionaryService getDdService() {
        return ddService;
    }

    /**
     * Sets the ddService attribute value.
     * 
     * @param ddService The ddService to set.
     */
    public final void setDdService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }

    /**
     * Gets the dictionaryValidationService attribute.
     * 
     * @return Returns the dictionaryValidationService.
     */
    protected final DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     * 
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public final void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Gets the documentAuthorizationService attribute.
     * 
     * @return Returns the documentAuthorizationService.
     */
    protected final DocumentAuthorizationService getDocumentAuthorizationService() {
        return documentAuthorizationService;
    }

    /**
     * Sets the documentAuthorizationService attribute value.
     * 
     * @param documentAuthorizationService The documentAuthorizationService to set.
     */
    public final void setDocumentAuthorizationService(DocumentAuthorizationService documentAuthorizationService) {
        this.documentAuthorizationService = documentAuthorizationService;
    }

    /**
     * Gets the maintDocDictionaryService attribute.
     * 
     * @return Returns the maintDocDictionaryService.
     */
    protected final MaintenanceDocumentDictionaryService getMaintDocDictionaryService() {
        return maintDocDictionaryService;
    }

    /**
     * Sets the maintDocDictionaryService attribute value.
     * 
     * @param maintDocDictionaryService The maintDocDictionaryService to set.
     */
    public final void setMaintDocDictionaryService(MaintenanceDocumentDictionaryService maintDocDictionaryService) {
        this.maintDocDictionaryService = maintDocDictionaryService;
    }

    /**
     * Gets the newBo attribute.
     * 
     * @return Returns the newBo.
     */
    protected final PersistableBusinessObject getNewBo() {
        return newBo;
    }

    protected void setNewBo(PersistableBusinessObject newBo) {
        this.newBo = newBo;
    }

    /**
     * Gets the oldBo attribute.
     * 
     * @return Returns the oldBo.
     */
    protected final PersistableBusinessObject getOldBo() {
        return oldBo;
    }

    /**
     * Gets the persistenceService attribute.
     * 
     * @return Returns the persistenceService.
     */
    protected final PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    public final void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Gets the persistenceStructureService attribute.
     * 
     * @return Returns the persistenceStructureService.
     */
    protected final PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public final void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Gets the workflowDocumentService attribute.
     * 
     * @return Returns the workflowDocumentService.
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * 
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        LOG.debug("processAddCollectionLineBusinessRules");
        
        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(document);
        
        // enforce authorization restrictions on fields
        checkAuthorizationRestrictions(document);
       
        // sanity check on the document object
        this.validateMaintenanceDocument( document );
        
        boolean success = true;
        ErrorMap map = GlobalVariables.getErrorMap();
        int errorCount = map.getErrorCount();
        map.addToErrorPath( MAINTAINABLE_ERROR_PATH );
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "processAddCollectionLineBusinessRules - BO: " + bo );
            LOG.debug( "Before Validate: " + map );
        }
        getBoDictionaryService().performForceUppercase(bo);
        getMaintDocDictionaryService().validateMaintainableCollectionsAddLineRequiredFields( document, document.getNewMaintainableObject().getBusinessObject(), collectionName );
        String errorPath = RiceConstants.MAINTENANCE_ADD_PREFIX + collectionName;
        map.addToErrorPath( errorPath );
        getDictionaryValidationService().validateBusinessObject( bo, false );
        success &= map.getErrorCount() == errorCount; 
        success &= validateDuplicateIdentifierInDataDictionary(document, collectionName, bo);
        success &= processCustomAddCollectionLineBusinessRules( document, collectionName, bo );
        map.removeFromErrorPath( errorPath );
        map.removeFromErrorPath( MAINTAINABLE_ERROR_PATH );
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "After Validate: " + map );
            LOG.debug( "processAddCollectionLineBusinessRules returning: " + success );
        }
        
        return success;
    }
    
    /**
     * This method validates that there should only exist one entry in the collection whose
     * fields match the fields specified within the duplicateIdentificationFields in the 
     * maintenance document data dictionary.
     * If the duplicateIdentificationFields is not specified in the DD, by default it would
     * allow the addition to happen and return true.
     * It will return false if it fails the uniqueness validation.
     * @param document
     * @param collectionName
     * @param bo
     * @return
     */
    private boolean validateDuplicateIdentifierInDataDictionary(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
    	boolean valid = true;
    	PersistableBusinessObject maintBo = document.getNewMaintainableObject().getBusinessObject();
        Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(maintBo, collectionName);
        List<String> duplicateIdentifier = document.getNewMaintainableObject().getDuplicateIdentifierFieldsFromDataDictionary(document.getDocumentHeader().getWorkflowDocument().getDocumentType(), collectionName);
    	if (duplicateIdentifier.size()>0) {
            List<String> existingIdentifierString = document.getNewMaintainableObject().getMultiValueIdentifierList(maintCollection, duplicateIdentifier);
            if (document.getNewMaintainableObject().hasBusinessObjectExisted(bo, existingIdentifierString, duplicateIdentifier)) {
    		    valid = false;
    		    GlobalVariables.getErrorMap().putError(duplicateIdentifier.get(0), RiceKeyConstants.ERROR_DUPLICATE_ELEMENT, "entries in ", document.getDocumentHeader().getWorkflowDocument().getDocumentType());
    	    }
    	}
    	return valid;
    }
    
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        return true;
    }

    public UniversalUserService getUniversalUserService() {
        return universalUserService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }
    
    public DateTimeService getDateTimeService() {
        return KNSServiceLocator.getDateTimeService();
    }
    
}
