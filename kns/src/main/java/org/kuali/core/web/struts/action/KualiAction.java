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
package org.kuali.core.web.struts.action;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.Demonstration;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TabState;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class is the base action class for all kuali actions. Overrides execute to set methodToCall for image submits. Other setup
 * for framework calls.
 *
 *  
 */
public abstract class KualiAction extends DispatchAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAction.class);

    private static KualiModuleService kualiModuleService;
    
    /**
     * Entry point to all actions.
     * 
     * NOTE: No need to hook into execute for handling framwork setup anymore. Just implement the methodToCall for the framework
     * setup, Constants.METHOD_REQUEST_PARAMETER will contain the full parameter, which can be sub stringed for getting framework
     * parameters.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward returnForward = null;

        String methodToCall;
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getMethodToCall())) {
            methodToCall = ((KualiForm) form).getMethodToCall();

            if (StringUtils.isNotBlank(getImageContext(request, Constants.ANCHOR))) {
                ((KualiForm) form).setAnchor(getImageContext(request, Constants.ANCHOR));
            }
            else if (StringUtils.isNotBlank(request.getParameter(Constants.ANCHOR))) {
                ((KualiForm) form).setAnchor(request.getParameter(Constants.ANCHOR));
            } 
            else {
                ((KualiForm) form).setAnchor(Constants.ANCHOR_TOP_OF_FORM);
            }
        }
        else {
            // call utility method to parse the methodToCall from the request.
            methodToCall = WebUtils.parseMethodToCall(request);
        }
        
        // if found methodToCall, pass control to that method, else return the basic forward
        if (StringUtils.isNotBlank(methodToCall)) {
            LOG.debug("methodToCall: " + methodToCall);
            returnForward = this.dispatchMethod(mapping, form, request, response, methodToCall);
        }
        else {
            returnForward = mapping.findForward(Constants.MAPPING_BASIC);
        }
        
        // make sure the user can do what they're trying to according to the module that owns the functionality
        checkAuthorization(form, methodToCall);

        // check if demonstration encryption is enabled
        if (KNSServiceLocator.getKualiConfigurationService().getApplicationParameterIndicator("SYSTEM", "demonstrationEncryptionCheck_FLAG") && KNSServiceLocator.getEncryptionService() instanceof Demonstration) {
            LOG.warn("WARNING: This implementation of Kuali uses the demonstration encryption framework.");
        }

        return returnForward;
    }

    /**
     * Toggles the tab state in the ui
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward toggleTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiForm kualiForm = (KualiForm) form;
        if (getTabToToggle(request) >= 0) {
            kualiForm.getTabState(getTabToToggle(request)).setOpen(!kualiForm.getTabState(getTabToToggle(request)).isOpen());
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Toggles all tabs to open
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward showAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiForm kualiForm = (KualiForm) form;
        List tabStates = kualiForm.getTabStates();
        for (Iterator iter = tabStates.iterator(); iter.hasNext();) {
            TabState state = (TabState) iter.next();
            state.setOpen(true);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Toggles all tabs to closed
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward hideAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiForm kualiForm = (KualiForm) form;
        List tabStates = kualiForm.getTabStates();
        for (Iterator iter = tabStates.iterator(); iter.hasNext();) {
            TabState state = (TabState) iter.next();
            state.setOpen(false);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Default refresh method. Called from returning frameworks.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    /**
     * Parses the method to call attribute to pick off the line number which should be deleted.
     * 
     * @param request
     * @return
     */
    protected int getLineToDelete(HttpServletRequest request) {
        return getSelectedLine(request);
    }

    /**
     * Parses the method to call attribute to pick off the line number which should have an action performed on it.
     * 
     * @param request
     * @return
     */
    protected int getSelectedLine(HttpServletRequest request) {
        int selectedLine = -1;
        String parameterName = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lineNumber = StringUtils.substringBetween(parameterName, ".line", ".");
            selectedLine = Integer.parseInt(lineNumber);
        }

        return selectedLine;
    }

    /**
     * Determines which tab was requested to be toggled
     * 
     * @param request
     * @return
     */
    protected int getTabToToggle(HttpServletRequest request) {
        int tabToToggle = -1;
        String parameterName = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            String lineNumber = StringUtils.substringBetween(parameterName, ".tab", ".");
            tabToToggle = Integer.parseInt(lineNumber);
        }
        return tabToToggle;
    }

    /**
     * Retrieves the header tab to navigate to.
     * 
     * @param request
     * @return
     */
    protected String getHeaderTabNavigateTo(HttpServletRequest request) {
        String headerTabNavigateTo = Constants.MAPPING_BASIC;
        String imageContext = getImageContext(request, Constants.NAVIGATE_TO);
        if (StringUtils.isNotBlank(imageContext)) {
            headerTabNavigateTo = imageContext;
        }
        return headerTabNavigateTo;
    }

    /**
     * Retrieves the header tab dispatch.
     * 
     * @param request
     * @return
     */
    protected String getHeaderTabDispatch(HttpServletRequest request) {
        String headerTabDispatch = null;
        String imageContext = getImageContext(request, Constants.HEADER_DISPATCH);
        if (StringUtils.isNotBlank(imageContext)) {
            headerTabDispatch = imageContext;
        }
        else {
            // In some cases it might be in request params instead
            headerTabDispatch = request.getParameter(Constants.METHOD_TO_CALL_ATTRIBUTE);
        }
        return headerTabDispatch;
    }

    /**
     * Retrieves the image context
     * 
     * @param request
     * @param contextKey
     * @return
     */
    protected String getImageContext(HttpServletRequest request, String contextKey) {
        String imageContext = "";
        String parameterName = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isBlank(parameterName)) {
            parameterName = request.getParameter("methodToCallPath");
        }
        if (StringUtils.isNotBlank(parameterName)) {
            imageContext = StringUtils.substringBetween(parameterName, contextKey, ".");
        }
        return imageContext;
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the lookup action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_BOPARM_LEFT_DEL, Constants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM1_LEFT_DEL, Constants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(Constants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM2_LEFT_DEL, Constants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(Constants.FIELD_CONVERSIONS_SEPERATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "lookupParams: " + lookupParams );
            }
            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(Constants.FIELD_CONVERSION_PAIR_SEPERATOR);

                // hard-coded passed value
                if (StringUtils.contains(keyValue[0], "'")) {
                    parameters.put(keyValue[1], StringUtils.replace(keyValue[0], "'", ""));
                }
                // passed value should come from property
                else if (StringUtils.isNotBlank(request.getParameter(keyValue[0]))) {
                    parameters.put(keyValue[1], request.getParameter(keyValue[0]));
                }
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "keyValue[0]: " + keyValue[0] );
                    LOG.debug( "keyValue[1]: " + keyValue[1] );
                }
            }
        }
        
        // pass values from form that should be read-Only on lookup search
        String readOnlyFields = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM8_LEFT_DEL, Constants.METHOD_TO_CALL_PARM8_RIGHT_DEL);
        if (StringUtils.isNotBlank(readOnlyFields)) {
            parameters.put(Constants.LOOKUP_READ_ONLY_FIELDS, readOnlyFields);
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "readOnlyFields: " + readOnlyFields );
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM3_LEFT_DEL, Constants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(Constants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // add the optional extra button source and parameters string
        String extraButtonSource = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM4_LEFT_DEL, Constants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonSource)) {
            parameters.put(Constants.EXTRA_BUTTON_SOURCE, extraButtonSource);
        }
        String extraButtonParams = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM5_LEFT_DEL, Constants.METHOD_TO_CALL_PARM5_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonParams)) {
            parameters.put(Constants.EXTRA_BUTTON_PARAMS, extraButtonParams);
        }

        String lookupAction = Constants.LOOKUP_ACTION;
        
        // is this a multi-value return?
        String multipleValues = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM6_LEFT_DEL, Constants.METHOD_TO_CALL_PARM6_RIGHT_DEL);
        if ((new Boolean(multipleValues).booleanValue())) {
            parameters.put(Constants.MULTIPLE_VALUE, multipleValues);
            lookupAction = Constants.MULTIPLE_VALUE_LOOKUP_ACTION;
        }
        
        // the name of the collection being looked up (primarily for multivalue lookups
        String lookedUpCollectionName = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM11_LEFT_DEL, Constants.METHOD_TO_CALL_PARM11_RIGHT_DEL);
        if (StringUtils.isNotBlank(lookedUpCollectionName)) {
            parameters.put(Constants.LOOKED_UP_COLLECTION_NAME, lookedUpCollectionName);
        }
        
        // grab whether or not the "supress actions" column should be hidden or not
        String supressActions = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM7_LEFT_DEL, Constants.METHOD_TO_CALL_PARM7_RIGHT_DEL);
        if (StringUtils.isNotBlank(supressActions)) {
            parameters.put(Constants.SUPPRESS_ACTIONS, supressActions);
        }
        
        // grab the references that should be refreshed upon returning from the lookup
        String referencesToRefresh = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM10_LEFT_DEL, Constants.METHOD_TO_CALL_PARM10_RIGHT_DEL);
        if (StringUtils.isNotBlank(referencesToRefresh)) {
            parameters.put(Constants.REFERENCES_TO_REFRESH, referencesToRefresh);
        }
        
        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(Constants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // now add required parameters
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, "start");
        
        // pass value from form that shows if autoSearch is desired for lookup search
        String autoSearch = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM9_LEFT_DEL, Constants.METHOD_TO_CALL_PARM9_RIGHT_DEL);
        
        if (StringUtils.isNotBlank(autoSearch)) {
            parameters.put(Constants.LOOKUP_AUTO_SEARCH, autoSearch);
            if ("YES".equalsIgnoreCase(autoSearch)){
                parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, "search");
            }
        }
        
        parameters.put(Constants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);               
        
        parameters.put(Constants.RETURN_LOCATION_PARAMETER, basePath + ("/lookup".equals(mapping.getPath()) || "/maintenance".equals(mapping.getPath()) ? "/kr" : "") + mapping.getPath() + ".do");
        
        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/kr/" + lookupAction, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * This method handles rendering the question component, but without any of the extra error fields
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @return ActionForward
     * @throws Exception
     */
    protected ActionForward performQuestionWithoutInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context) throws Exception {
        return performQuestion(mapping, form, request, response, questionId, questionText, questionType, caller, context, false, "", "", "", "");
    }

    /**
     * Handles rendering a question prompt - without a specified context.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @return ActionForward
     * @throws Exception
     */
    protected ActionForward performQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context) throws Exception {
        return performQuestion(mapping, form, request, response, questionId, questionText, questionType, caller, context, true, "", "", "", "");
    }

    /**
     * Handles re-rendering a question prompt because of an error on what was submitted.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @param reason
     * @param errorKey
     * @param errorPropertyName
     * @param errorParameter
     * @return ActionForward
     * @throws Exception
     */
    protected ActionForward performQuestionWithInputAgainBecauseOfErrors(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context, String reason, String errorKey, String errorPropertyName, String errorParameter) throws Exception {
        return performQuestion(mapping, form, request, response, questionId, questionText, questionType, caller, context, true, reason, errorKey, errorPropertyName, errorParameter);
    }

    /**
     * Handles rendering a question prompt - with a specified context.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionId
     * @param questionText
     * @param questionType
     * @param caller
     * @param context
     * @param showReasonField
     * @param reason
     * @param errorKey
     * @param errorPropertyName
     * @param errorParameter
     * @return ActionForward
     * @throws Exception
     */
    private ActionForward performQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionId, String questionText, String questionType, String caller, String context, boolean showReasonField, String reason, String errorKey, String errorPropertyName, String errorParameter) throws Exception {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        Properties parameters = new Properties();

        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(Constants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(Constants.CALLING_METHOD, caller);
        parameters.put(Constants.QUESTION_INST_ATTRIBUTE_NAME, questionId);
        parameters.put(Constants.QUESTION_IMPL_ATTRIBUTE_NAME, questionType);
        parameters.put(Constants.QUESTION_TEXT_ATTRIBUTE_NAME, questionText);
        parameters.put(Constants.RETURN_LOCATION_PARAMETER, basePath + ("/lookup".equals(mapping.getPath()) || "/maintenance".equals(mapping.getPath()) ? "/kr" : "") + mapping.getPath() + ".do");
        parameters.put(Constants.QUESTION_CONTEXT, context);
        parameters.put(Constants.QUESTION_SHOW_REASON_FIELD, Boolean.toString(showReasonField));
        parameters.put(Constants.QUESTION_REASON_ATTRIBUTE_NAME, reason);
        parameters.put(Constants.QUESTION_ERROR_KEY, errorKey);
        parameters.put(Constants.QUESTION_ERROR_PROPERTY_NAME, errorPropertyName);
        parameters.put(Constants.QUESTION_ERROR_PARAMETER, errorParameter);
        parameters.put(Constants.QUESTION_ANCHOR, form instanceof KualiForm ? ObjectUtils.toString(((KualiForm) form).getAnchor()) : "");
        Object methodToCallAttribute = request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        if (methodToCallAttribute != null) {
            parameters.put(Constants.METHOD_TO_CALL_PATH, methodToCallAttribute);
        }

        String questionUrl = UrlFactory.parameterizeUrl(basePath + "/kr/" + Constants.QUESTION_ACTION, parameters);
        return new ActionForward(questionUrl, true);
    }


    /**
     * Takes care of storing the action form in the User session and forwarding to the workflow workgroup lookup action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performWorkgroupLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getPath() + ".do";

        String fullParameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        String conversionFields = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM1_LEFT_DEL, Constants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

        String deploymentBaseUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(Constants.WORKFLOW_URL_KEY);
        String workgroupLookupUrl = deploymentBaseUrl + "/Lookup.do?lookupableImplServiceName=WorkGroupLookupableImplService&methodToCall=start&docFormKey=" + GlobalVariables.getUserSession().addObject(form);

        if (conversionFields != null) {
            workgroupLookupUrl += "&conversionFields=" + conversionFields;
        }
        workgroupLookupUrl += "&returnLocation=" + returnUrl;

        return new ActionForward(workgroupLookupUrl, true);
    }

    /**
     * Handles requests that originate via Header Tabs.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward headerTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // header tab actions can do two things - 1, call into an action and perform what needs to happen in there and 2, forward to
        // a new location.
        String headerTabDispatch = getHeaderTabDispatch(request);
        if (StringUtils.isNotEmpty(headerTabDispatch)) {
            ActionForward forward = this.dispatchMethod(mapping, form, request, response, headerTabDispatch);
            if (GlobalVariables.getErrorMap().size() > 0) {
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
            this.hideAllTabs(mapping, form, request, response);
            if (forward.getRedirect()) {
                return forward;
            }
        }

        return this.dispatchMethod(mapping, form, request, response, getHeaderTabNavigateTo(request));
    }
    
    /**
     * Override this method to provide action-level access controls to the application.
     * 
     * @param form
     * @throws AuthorizationException
     */
    protected void checkAuthorization( ActionForm form, String methodToCall) throws AuthorizationException {
        try {
            LOG.warn(new StringBuffer("KualiAction.checkAuthorization was deferred to.  Caller is ").append(Thread.currentThread().getStackTrace()[3].toString()).append(" and methodToCall is ").append(methodToCall));
        } catch (Exception e) {
            LOG.warn("KualiAction.checkAuthorization was deferred to.  Caller is unknown and methodToCall is " + methodToCall);
        }
        AuthorizationType defaultAuthorizationType = new AuthorizationType.Default(this.getClass());
        if ( !KNSServiceLocator.getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), defaultAuthorizationType ) ) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName() );
            throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), defaultAuthorizationType, getKualiModuleService().getResponsibleModule(((KualiDocumentFormBase)form).getDocument().getClass()) );
        }
    }

    protected static KualiModuleService getKualiModuleService() {
        if ( kualiModuleService == null ) {
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        }
        return kualiModuleService;
    }

}