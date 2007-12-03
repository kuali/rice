/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.RicePropertyConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.rule.AddAdHocRoutePersonRule;
import org.kuali.core.rule.AddAdHocRouteWorkgroupRule;
import org.kuali.core.rule.AddNoteRule;
import org.kuali.core.rule.ApproveDocumentRule;
import org.kuali.core.rule.RouteDocumentRule;
import org.kuali.core.rule.SaveDocumentRule;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowInfo;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class contains all of the business rules that are common to all documents.
 */
public abstract class DocumentRuleBase implements SaveDocumentRule, RouteDocumentRule, ApproveDocumentRule, AddNoteRule, AddAdHocRoutePersonRule, AddAdHocRouteWorkgroupRule {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentRuleBase.class);

    private static UniversalUserService universalUserService;
    private static KualiModuleService kualiModuleService;
    private static DictionaryValidationService dictionaryValidationService;
    private static KualiWorkflowInfo workflowInfoService;
    private static KualiConfigurationService kualiConfigurationService;
    
    private void initStatics() {
        if ( universalUserService == null ) {
            workflowInfoService = KNSServiceLocator.getWorkflowInfoService();
            kualiConfigurationService = KNSServiceLocator.getKualiConfigurationService();
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
            dictionaryValidationService = KNSServiceLocator.getDictionaryValidationService();
            universalUserService = KNSServiceLocator.getUniversalUserService();
        }
    }
    
    protected UniversalUserService getUniversalUserService() {
        initStatics();
        return universalUserService;
    }

    protected KualiModuleService getKualiModuleService() {
        initStatics();
        return kualiModuleService;
    }

    protected DictionaryValidationService getDictionaryValidationService() {
        initStatics();
        return dictionaryValidationService;
    }

    protected KualiWorkflowInfo getWorkflowInfoService() {
        initStatics();
        return workflowInfoService;
    }

    protected KualiConfigurationService getKualiConfigurationService() {
        initStatics();
        return kualiConfigurationService;
    }
    
    /**
     * Verifies that the document's overview fields are valid - it does required and format checks.
     * 
     * @param document
     * @return boolean True if the document description is valid, false otherwise.
     */
    public boolean isDocumentOverviewValid(Document document) {
        LOG.debug("isDocumentOverviewValid(Document) - start");

        // add in the documentHeader path
        GlobalVariables.getErrorMap().addToErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);
        GlobalVariables.getErrorMap().addToErrorPath(RiceConstants.DOCUMENT_HEADER_PROPERTY_NAME);

        // check the document header for fields like the description
        getDictionaryValidationService().validateBusinessObject(document.getDocumentHeader());

        // drop the error path keys off now
        GlobalVariables.getErrorMap().removeFromErrorPath(RiceConstants.DOCUMENT_HEADER_PROPERTY_NAME);
        GlobalVariables.getErrorMap().removeFromErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);

        boolean returnboolean = GlobalVariables.getErrorMap().isEmpty();
        LOG.debug("isDocumentOverviewValid(Document) - end");
        return returnboolean;
    }

    /**
     * Validates the document attributes against the data dictionary.
     * 
     * @param document
     * @return True if the document attributes are valid, false otherwise.
     */
    public boolean isDocumentAttributesValid(Document document) {
        LOG.debug("isDocumentAttributesValid(Document) - start");

        // start updating the error path name
        GlobalVariables.getErrorMap().addToErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);

        // check the document for fields like explanation and org doc #
        getDictionaryValidationService().validateDocument(document);

        // drop the error path keys off now
        GlobalVariables.getErrorMap().removeFromErrorPath(RiceConstants.DOCUMENT_PROPERTY_NAME);

        boolean returnboolean = GlobalVariables.getErrorMap().isEmpty();
        LOG.debug("isDocumentAttributesValid(Document) - end");
        return returnboolean;
    }

    /**
     * Runs all business rules needed prior to saving. This includes both common rules for all documents, plus class-specific
     * business rules.
     * 
     * This method will only return false if it fails the isValidForSave() test. Otherwise, it will always return positive
     * regardless of the outcome of the business rules. However, any error messages resulting from the business rules will still be
     * populated, for display to the consumer of this service.
     * 
     * @see org.kuali.core.rule.SaveDocumentRule#processSaveDocument(org.kuali.core.document.Document)
     * 
     */
    public boolean processSaveDocument(Document document) {
        LOG.debug("processSaveDocument(Document) - start");

        boolean isValid = true;

        isValid &= isDocumentOverviewValid(document);

        if (isValid) {
            isValid &= processCustomSaveDocumentBusinessRules(document);
        }

        LOG.debug("processSaveDocument(Document) - end");
        return isValid;
    }

    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the "save document" event.
     * 
     * @param document
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        LOG.debug("processCustomSaveDocumentBusinessRules(Document) - start");

        LOG.debug("processCustomSaveDocumentBusinessRules(Document) - end");
        return true;
    }

    /**
     * Runs all business rules needed prior to routing. This includes both common rules for all maintenance documents, plus
     * class-specific business rules.
     * 
     * This method will return false if any business rule fails, or if the document is in an invalid state, and not routable (see
     * isDocumentValidForRouting()).
     * 
     * @see org.kuali.core.rule.RouteDocumentRule#processRouteDocument(org.kuali.core.document.Document)
     */
    public boolean processRouteDocument(Document document) {
        LOG.debug("processRouteDocument(Document) - start");

        boolean isValid = true;

        isValid &= isDocumentAttributesValid(document);

        if (isValid) {
            isValid &= processCustomRouteDocumentBusinessRules(document);
        }

        LOG.debug("processRouteDocument(Document) - end");
        return isValid;
    }

    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the "route document" event.
     * 
     * @param document
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.debug("processCustomRouteDocumentBusinessRules(Document) - start");

        LOG.debug("processCustomRouteDocumentBusinessRules(Document) - end");
        return true;
    }

    /**
     * Runs all business rules needed prior to approving. This includes both common rules for all documents, plus class-specific
     * business rules.
     * 
     * This method will return false if any business rule fails, or if the document is in an invalid state, and not approveble.
     * 
     * @see org.kuali.core.rule.ApproveDocumentRule#processApproveDocument(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {
        LOG.debug("processApproveDocument(ApproveDocumentEvent) - start");

        boolean isValid = true;

        isValid &= processCustomApproveDocumentBusinessRules(approveEvent);

        LOG.debug("processApproveDocument(ApproveDocumentEvent) - end");
        return isValid;
    }

    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the "approve document" event.
     * 
     * @param document
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        LOG.debug("processCustomApproveDocumentBusinessRules(ApproveDocumentEvent) - start");

        LOG.debug("processCustomApproveDocumentBusinessRules(ApproveDocumentEvent) - end");
        return true;
    }
    
    /**
     * Runs all business rules needed prior to adding a document note.
     * 
     * This method will return false if any business rule fails.
     * 
     * @see org.kuali.core.rule.AddDocumentNoteRule#processAddDocumentNote(org.kuali.core.document.Document,
     *      org.kuali.core.document.DocumentNote)
     */
    public boolean processAddNote(Document document, Note note) {
        LOG.debug("processAddNote(Document, Note) - start");

        boolean isValid = true;

        isValid &= isNoteValid(note);

        isValid &= processCustomAddNoteBusinessRules(document, note);

        LOG.debug("processAdfNote(Document, Note) - end");
        return isValid;
    }
    
    /**
     * Verifies that the note's fields are valid - it does required and format checks.
     * 
     * @param note
     * @return boolean True if the document description is valid, false otherwise.
     */
    public boolean isNoteValid(Note note) {
        //TODO: Chris change these constants!
        
        // add the error path keys on the stack
        GlobalVariables.getErrorMap().addToErrorPath(RiceConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME);

        // check the document header for fields like the description
        KNSServiceLocator.getDictionaryValidationService().validateBusinessObject(note);

        // drop the error path keys off now
        GlobalVariables.getErrorMap().removeFromErrorPath(RiceConstants.NEW_DOCUMENT_NOTE_PROPERTY_NAME);

        return GlobalVariables.getErrorMap().isEmpty();
    }
    
    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the "add document note" event.
     * 
     * @param document
     * @param note
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomAddNoteBusinessRules(Document document, Note note) {
        return true;
    }

    /**
     * @see org.kuali.core.rule.AddAdHocRoutePersonRule#processAddAdHocRoutePerson(org.kuali.core.document.Document,
     *      org.kuali.core.bo.AdHocRoutePerson)
     */
    public boolean processAddAdHocRoutePerson(Document document, AdHocRoutePerson adHocRoutePerson) {
        LOG.debug("processAddAdHocRoutePerson(Document, AdHocRoutePerson) - start");

        boolean isValid = true;

        isValid &= isAddHocRoutePersonValid(document, adHocRoutePerson);

        isValid &= processCustomAddAdHocRoutePersonBusinessRules(document, adHocRoutePerson);

        LOG.debug("processAddAdHocRoutePerson(Document, AdHocRoutePerson) - end");
        return isValid;
    }

    /**
     * Verifies that the adHocRoutePerson's fields are valid - it does required and format checks.
     * 
     * @param person
     * @return boolean True if valid, false otherwise.
     */
    public boolean isAddHocRoutePersonValid(Document document, AdHocRoutePerson person) {
        LOG.debug("isAddHocRoutePersonValid(AdHocRoutePerson) - start");

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        // new recipients are not embedded in the error path; existing lines should be
        if (errorMap.getErrorPath().size() == 0) {
            // add the error path keys on the stack
            errorMap.addToErrorPath(RiceConstants.NEW_AD_HOC_ROUTE_PERSON_PROPERTY_NAME);
        }
        
        if (StringUtils.isNotBlank(person.getId())) {
            UniversalUser user = null;
            // validate that they are a user from the user service by looking them up
            try {
                user = getUniversalUserService().getUniversalUser(new AuthenticationUserId(person.getId()));
            }
            catch (UserNotFoundException userNotFoundException) {
                LOG.warn("isAddHocRoutePersonValid(AdHocRoutePerson) - exception ignored", userNotFoundException);
            }
            if ( user == null || !user.isActiveForAnyModule() ) {
                GlobalVariables.getErrorMap().putError(RicePropertyConstants.ID, RiceKeyConstants.ERROR_INVALID_ADHOC_PERSON_ID);
            } else {
                // determine the module for the document
                KualiModule module = null;
                Class docOrBoClass = null;
                if ( document instanceof MaintenanceDocument ) {
                    docOrBoClass = ((MaintenanceDocument)document).getNewMaintainableObject().getBoClass();
                } else {
                    docOrBoClass = document.getClass();
                }
                if ( !getKualiModuleService().isAuthorized( user, new AuthorizationType.AdHocRequest(docOrBoClass, person.getActionRequested()) ) ) {
                    GlobalVariables.getErrorMap().putError(RicePropertyConstants.ID, RiceKeyConstants.ERROR_UNAUTHORIZED_ADHOC_PERSON_ID);
                }
            }
        } else {
        	GlobalVariables.getErrorMap().putError(RicePropertyConstants.ID, RiceKeyConstants.ERROR_MISSING_ADHOC_PERSON_ID);
        }

        // drop the error path keys off now
        errorMap.removeFromErrorPath(RiceConstants.NEW_AD_HOC_ROUTE_PERSON_PROPERTY_NAME);

        boolean returnboolean = GlobalVariables.getErrorMap().isEmpty();
        LOG.debug("isAddHocRoutePersonValid(AdHocRoutePerson) - end");
        return returnboolean;
    }

    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the "add ad hoc route person" event.
     * 
     * @param document
     * @param person
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomAddAdHocRoutePersonBusinessRules(Document document, AdHocRoutePerson person) {
        LOG.debug("processCustomAddAdHocRoutePersonBusinessRules(Document, AdHocRoutePerson) - start");

        LOG.debug("processCustomAddAdHocRoutePersonBusinessRules(Document, AdHocRoutePerson) - end");
        return true;
    }

    /**
     * @see org.kuali.core.rule.AddAdHocRouteWorkgroupRule#processAddAdHocRouteWorkgroup(org.kuali.core.document.Document,
     *      org.kuali.core.bo.AdHocRouteWorkgroup)
     */
    public boolean processAddAdHocRouteWorkgroup(Document document, AdHocRouteWorkgroup adHocRouteWorkgroup) {
        LOG.debug("processAddAdHocRouteWorkgroup(Document, AdHocRouteWorkgroup) - start");

        boolean isValid = true;

        isValid &= isAddHocRouteWorkgroupValid(adHocRouteWorkgroup);

        isValid &= processCustomAddAdHocRouteWorkgroupBusinessRules(document, adHocRouteWorkgroup);

        LOG.debug("processAddAdHocRouteWorkgroup(Document, AdHocRouteWorkgroup) - end");
        return isValid;
    }

    /**
     * Verifies that the adHocRouteWorkgroup's fields are valid - it does required and format checks.
     * 
     * @param workgroup
     * @return boolean True if valid, false otherwise.
     */
    public boolean isAddHocRouteWorkgroupValid(AdHocRouteWorkgroup workgroup) {
        LOG.debug("isAddHocRouteWorkgroupValid(AdHocRouteWorkgroup) - start");

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        // new recipients are not embedded in the error path; existing lines should be
        if (errorMap.getErrorPath().size() == 0) {
            // add the error path keys on the stack
            GlobalVariables.getErrorMap().addToErrorPath(RiceConstants.NEW_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME);
        }

        if (StringUtils.isNotBlank(workgroup.getId())) {
            // validate that they are a workgroup from the workgroup service by looking them up
            try {
                WorkgroupVO workgroupVo = getWorkflowInfoService().getWorkgroup(new WorkgroupNameIdVO(workgroup.getId()));
                if (workgroupVo == null || !workgroupVo.isActiveInd()) {
                    GlobalVariables.getErrorMap().putError(RicePropertyConstants.ID, RiceKeyConstants.ERROR_INVALID_ADHOC_WORKGROUP_ID);
                }
            }
            catch (WorkflowException e) {
                LOG.error("isAddHocRouteWorkgroupValid(AdHocRouteWorkgroup)", e);

                GlobalVariables.getErrorMap().putError(RicePropertyConstants.ID, RiceKeyConstants.ERROR_INVALID_ADHOC_WORKGROUP_ID);
            }
        } else {
        	GlobalVariables.getErrorMap().putError(RicePropertyConstants.ID, RiceKeyConstants.ERROR_MISSING_ADHOC_WORKGROUP_ID);
        }

        // drop the error path keys off now
        GlobalVariables.getErrorMap().removeFromErrorPath(RiceConstants.NEW_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME);

        boolean returnboolean = GlobalVariables.getErrorMap().isEmpty();
        LOG.debug("isAddHocRouteWorkgroupValid(AdHocRouteWorkgroup) - end");
        return returnboolean;
    }

    /**
     * This method should be overridden by children rule classes as a hook to implement document specific business rule checks for
     * the "add ad hoc route workgroup" event.
     * 
     * @param document
     * @param workgroup
     * @return boolean True if the rules checks passed, false otherwise.
     */
    protected boolean processCustomAddAdHocRouteWorkgroupBusinessRules(Document document, AdHocRouteWorkgroup workgroup) {
        LOG.debug("processCustomAddAdHocRouteWorkgroupBusinessRules(Document, AdHocRouteWorkgroup) - start");

        LOG.debug("processCustomAddAdHocRouteWorkgroupBusinessRules(Document, AdHocRouteWorkgroup) - end");
        return true;
    }

    /**
     * Helper method to determine if the given <code>value</code> fails a rule.
     * 
     * @param parameterName
     * @param value
     * @return boolean
     */
    protected boolean failsRule(String parameterName, String value) {
        LOG.debug("failsRule(String, String) - start");
        Parameter rule = getParameterRule(parameterName);
        boolean returnboolean = true;

        if ( rule != null ) {
        	returnboolean = getKualiConfigurationService().failsRule(rule,value);
        }

        LOG.debug("failsRule(String, String) - end");
        return returnboolean;
    }

    /**
     * Helper method to determine if the given <code>value</code> succeeds a rule.
     * 
     * @param parameterName
     * @param value
     * @return boolean
     */
    protected boolean succeedsRule(String parameterName, String value) {
        LOG.debug("succeedsRule(String, String) - start");
        Parameter rule = getParameterRule(parameterName);
        boolean returnboolean = false;
        if ( rule != null ) {
        	returnboolean = getKualiConfigurationService().succeedsRule(rule,value);
        }
        LOG.debug("succeedsRule(String, String) - end");
        return returnboolean;
    }

    /**
     * Convenience method for accessing the most-likely requested security grouping
     * 
     * @return String
     */
    protected String getDefaultParameterNamespace() {
        return null;
    }

    protected String getDefaultParameterDetailTypeCode() {
    	return null;
    }

    /**
     * Uses default security grouping specified with the <code>getDefaultSecurityGrouping()</code> method to obtain a
     * <code>{@link Parameter}</code> instance for the given <code>parameterName</code>
     * 
     * @param parameterName
     * @return Parameter
     * @see #getDefaultSecurityGrouping()
     */
    protected Parameter getParameterRule(String parameterName) {
        LOG.debug("getParameterRule(String) - start");

        if (StringUtils.isBlank(getDefaultParameterNamespace())) {
            throw new IllegalArgumentException("No Namespace Supplied for ParameterRule " + parameterName);
        }
        if (StringUtils.isBlank(getDefaultParameterDetailTypeCode())) {
            throw new IllegalArgumentException("No Detail Type Code Supplied for ParameterRule " + parameterName);
        }
        Parameter returnKualiParameterRule = getParameterRule(getDefaultParameterNamespace(), getDefaultParameterDetailTypeCode(), parameterName);
        LOG.debug("getParameterRule(String) - end");
        return returnKualiParameterRule;
    }

    /**
     * Obtain access to an instance of <code>{@link Parameter}</code> for the given <code>securityGrouping</code> and
     * <code>parameterName</code>
     * 
     * @param securityGrouping
     * @param parameterName
     * @return KualiDocumentRule
     */
    protected Parameter getParameterRule(String parameterNamespace, String parameterDetailTypeCode, String parameterName) {
        LOG.debug("getParameterRule(String, String) - start");

        Parameter returnKualiParameterRule = getKualiConfigurationService().getParameter(parameterNamespace, parameterDetailTypeCode, parameterName);
        LOG.debug("getParameterRule(String, String) - end");
        return returnKualiParameterRule;
    }

    /**
     * Checks the given field value against a restriction defined in the application parameters table. If the rule fails, an error
     * is added to the global error map.
     * 
     * @param parameterGroupName - Security Group name
     * @param parameterName - Parameter Name
     * @param restrictedFieldValue - Value to check
     * @param errorField - Key to associate error with in error map
     * @param errorParameter - String parameter for the restriction error message
     * @return boolean indicating whether or not the rule passed
     */
    protected boolean executeParameterRestriction(String parameterNamespace, String parameterDetailTypeCode, String parameterName, String restrictedFieldValue, String errorField, String errorParameter) {
        LOG.debug("executeParameterRestriction(String, String, String, String, String) - start");

        boolean rulePassed = true;

        Parameter rule = getKualiConfigurationService().getParameter(parameterNamespace, parameterDetailTypeCode, parameterName);
        if ( rule != null ) {        	
            if (getKualiConfigurationService().failsRule(rule, restrictedFieldValue)) {
                GlobalVariables.getErrorMap().putError(errorField, getKualiConfigurationService().getErrorMessageKey( rule ), new String[] { errorParameter, restrictedFieldValue, parameterName, parameterNamespace, rule.getParameterValue() });
                rulePassed = false;
            }
        }

        LOG.debug("executeParameterRestriction(String, String, String, String, String) - end");
        return rulePassed;
        }

    /**
     * Checks the given field value against a restriction defined in the application parameters table. If the rule fails, an error
     * is added to the global error map.
     * 
     * @param parameterGroupName - Security Group name
     * @param parameterName - Parameter Name
     * @param restrictedFieldValue - Value to check
     * @param errorField - Key to associate error with in error map
     * @param errorParameter - String parameter for the restriction error message
     * @return boolean indicating whether or not the rule passed
     */
    protected boolean executeConstrainedParameterRestriction(String parameterNamespace, String parameterDetailTypeCode, String allowedParameterName, String disallowedParameterName, String constrainingValue, String restrictedFieldValue, String errorField, String errorParameter) {
        LOG.debug("executeConstrainedParameterRestriction(String, String, String, String, String, String) - start");

        boolean rulePassed = true;

        Parameter allowedRule = getKualiConfigurationService().getParameter(parameterNamespace, parameterDetailTypeCode, allowedParameterName);
        Parameter disallowedRule = getKualiConfigurationService().getParameter(parameterNamespace, parameterDetailTypeCode, disallowedParameterName);
        if ( allowedRule != null || disallowedRule != null ) {           
            rulePassed = getKualiConfigurationService().evaluateConstrainedParameter(allowedRule, disallowedRule, constrainingValue, restrictedFieldValue);
            if ( !rulePassed ) {
                Parameter ruleToUseInError = (allowedRule !=null)?allowedRule:disallowedRule;
                GlobalVariables.getErrorMap().putError(errorField, getKualiConfigurationService().getErrorMessageKey( ruleToUseInError ), new String[] { errorParameter, restrictedFieldValue, ruleToUseInError.getParameterName()+"/"+constrainingValue, parameterNamespace+"/"+parameterDetailTypeCode, ruleToUseInError.getParameterValue() });
                rulePassed = false;
            }
        }

        LOG.debug("executeConstrainedParameterRestriction(String, String, String, String, String, String) - end");
        return rulePassed;
    }
}
