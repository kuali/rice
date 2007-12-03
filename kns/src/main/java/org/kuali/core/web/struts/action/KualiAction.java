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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.kuali.RiceConstants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.Demonstration;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.GlobalVariables;
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

            if (StringUtils.isNotBlank(getImageContext(request, RiceConstants.ANCHOR))) {
                ((KualiForm) form).setAnchor(getImageContext(request, RiceConstants.ANCHOR));
            }
            else if (StringUtils.isNotBlank(request.getParameter(RiceConstants.ANCHOR))) {
                ((KualiForm) form).setAnchor(request.getParameter(RiceConstants.ANCHOR));
            } 
            else {
                ((KualiForm) form).setAnchor(RiceConstants.ANCHOR_TOP_OF_FORM);
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
            returnForward = mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
        
        // make sure the user can do what they're trying to according to the module that owns the functionality
        checkAuthorization(form, methodToCall);

        // check if demonstration encryption is enabled
        if (KNSServiceLocator.getKualiConfigurationService().getIndicatorParameter(RiceConstants.KNS_NAMESPACE, RiceConstants.DetailTypes.ALL_DETAIL_TYPE, RiceConstants.SystemGroupParameterNames.CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND) && KNSServiceLocator.getEncryptionService() instanceof Demonstration) {
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
        String tabToToggle = getTabToToggle(request);
        if (StringUtils.isNotBlank(tabToToggle)) {
            if (kualiForm.getTabState(tabToToggle).equals("OPEN")) {
            	kualiForm.getTabStates().remove(tabToToggle);
            	kualiForm.getTabStates().put(tabToToggle, "CLOSE");
            }
            else {
            	kualiForm.getTabStates().remove(tabToToggle);
            	kualiForm.getTabStates().put(tabToToggle, "OPEN");
            }
        }
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
        
        Map<String, String> tabStates = kualiForm.getTabStates();
        Map<String, String> newTabStates = new HashMap<String, String>();
        for (String tabKey: tabStates.keySet()) {
            newTabStates.put(tabKey, "OPEN");
        }
        kualiForm.setTabStates(newTabStates);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
        
        Map<String, String> tabStates = kualiForm.getTabStates();
        Map<String, String> newTabStates = new HashMap<String, String>();
        for (String tabKey: tabStates.keySet()) {
        	newTabStates.put(tabKey, "CLOSE");
        }
        kualiForm.setTabStates(newTabStates);
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
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
        String parameterName = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
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
    protected String getTabToToggle(HttpServletRequest request) {
        String tabToToggle = "";
        String parameterName = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            tabToToggle = StringUtils.substringBetween(parameterName, ".tab", ".");
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
        String headerTabNavigateTo = RiceConstants.MAPPING_BASIC;
        String imageContext = getImageContext(request, RiceConstants.NAVIGATE_TO);
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
        String imageContext = getImageContext(request, RiceConstants.HEADER_DISPATCH);
        if (StringUtils.isNotBlank(imageContext)) {
            headerTabDispatch = imageContext;
        }
        else {
            // In some cases it might be in request params instead
            headerTabDispatch = request.getParameter(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
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
        String parameterName = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isBlank(parameterName)) {
            parameterName = request.getParameter("methodToCallPath");
        }
        if (StringUtils.isNotBlank(parameterName)) {
            imageContext = StringUtils.substringBetween(parameterName, contextKey, ".");
        }
        return imageContext;
    }
    
    protected String getBasePath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
    
    protected String getReturnLocation(HttpServletRequest request, ActionMapping mapping) {
        return getBasePath(request) + ("/lookup".equals(mapping.getPath()) || "/maintenance".equals(mapping.getPath()) || "/multipleValueLookup".equals(mapping.getPath()) ? "/kr" : "") + mapping.getPath() + ".do";
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
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, RiceConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(RiceConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(RiceConstants.FIELD_CONVERSIONS_SEPERATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "lookupParams: " + lookupParams );
            }
            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(RiceConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

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
        String readOnlyFields = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM8_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM8_RIGHT_DEL);
        if (StringUtils.isNotBlank(readOnlyFields)) {
            parameters.put(RiceConstants.LOOKUP_READ_ONLY_FIELDS, readOnlyFields);
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "readOnlyFields: " + readOnlyFields );
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(RiceConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // add the optional extra button source and parameters string
        String extraButtonSource = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonSource)) {
            parameters.put(RiceConstants.EXTRA_BUTTON_SOURCE, extraButtonSource);
        }
        String extraButtonParams = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM5_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM5_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonParams)) {
            parameters.put(RiceConstants.EXTRA_BUTTON_PARAMS, extraButtonParams);
        }

        String lookupAction = RiceConstants.LOOKUP_ACTION;
        
        // is this a multi-value return?
        String multipleValues = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM6_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM6_RIGHT_DEL);
        if ((new Boolean(multipleValues).booleanValue())) {
            parameters.put(RiceConstants.MULTIPLE_VALUE, multipleValues);
            lookupAction = RiceConstants.MULTIPLE_VALUE_LOOKUP_ACTION;
        }
        
        // the name of the collection being looked up (primarily for multivalue lookups
        String lookedUpCollectionName = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM11_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM11_RIGHT_DEL);
        if (StringUtils.isNotBlank(lookedUpCollectionName)) {
            parameters.put(RiceConstants.LOOKED_UP_COLLECTION_NAME, lookedUpCollectionName);
        }
        
        // grab whether or not the "supress actions" column should be hidden or not
        String supressActions = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM7_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM7_RIGHT_DEL);
        if (StringUtils.isNotBlank(supressActions)) {
            parameters.put(RiceConstants.SUPPRESS_ACTIONS, supressActions);
        }
        
        // grab the references that should be refreshed upon returning from the lookup
        String referencesToRefresh = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM10_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM10_RIGHT_DEL);
        if (StringUtils.isNotBlank(referencesToRefresh)) {
            parameters.put(RiceConstants.REFERENCES_TO_REFRESH, referencesToRefresh);
        }
        
        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(RiceConstants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // now add required parameters
        parameters.put(RiceConstants.DISPATCH_REQUEST_PARAMETER, "start");
        
        // pass value from form that shows if autoSearch is desired for lookup search
        String autoSearch = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM9_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM9_RIGHT_DEL);
        
        if (StringUtils.isNotBlank(autoSearch)) {
            parameters.put(RiceConstants.LOOKUP_AUTO_SEARCH, autoSearch);
            if ("YES".equalsIgnoreCase(autoSearch)){
                parameters.put(RiceConstants.DISPATCH_REQUEST_PARAMETER, "search");
            }
        }
        
        parameters.put(RiceConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(RiceConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);               
        
        parameters.put(RiceConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));
        
        String lookupUrl = UrlFactory.parameterizeUrl(getBasePath(request) + "/kr/" + lookupAction, parameters);
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performInquiry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, RiceConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform inquiry, no business object class name specified.");
        }

        // build the parameters for the inquiry url
        Properties parameters = new Properties();
        parameters.put(RiceConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);               
        
        parameters.put(RiceConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));

        // pass values from form that should be pre-populated on inquiry
        String parameterFields = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        if (StringUtils.isNotBlank(parameterFields)) {
            // TODO : create a method for this to be used by both lookup & inquiry ?
            String[] inquiryParams = parameterFields.split(RiceConstants.FIELD_CONVERSIONS_SEPERATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "inquiryParams: " + inquiryParams );
            }
            for (int i = 0; i < inquiryParams.length; i++) {
                String[] keyValue = inquiryParams[i].split(RiceConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

                // hard-coded passed value
                if (StringUtils.contains(keyValue[0], "'")) {
                    parameters.put(keyValue[1], StringUtils.replace(keyValue[0], "'", ""));
                }
                // passed value should come from property
                else if (StringUtils.isNotBlank(request.getParameter(keyValue[0]))) {
                    parameters.put(keyValue[1], request.getParameter(keyValue[0]));
                } else {
                    parameters.put(keyValue[1], "directInquiryKeyNotSpecified");                    
                }
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "keyValue[0]: " + keyValue[0] );
                    LOG.debug( "keyValue[1]: " + keyValue[1] );
                }
            }
        }
        parameters.put(RiceConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(RiceConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));

        String inquiryAction = "directInquiry.do";
        String inquiryUrl = UrlFactory.parameterizeUrl(basePath + "/kr/" + RiceConstants.DIRECT_INQUIRY_ACTION, parameters);
        return new ActionForward(inquiryUrl, true);

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
        Properties parameters = new Properties();

        parameters.put(RiceConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(RiceConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(RiceConstants.CALLING_METHOD, caller);
        parameters.put(RiceConstants.QUESTION_INST_ATTRIBUTE_NAME, questionId);
        parameters.put(RiceConstants.QUESTION_IMPL_ATTRIBUTE_NAME, questionType);
        parameters.put(RiceConstants.QUESTION_TEXT_ATTRIBUTE_NAME, questionText);
        parameters.put(RiceConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));
        parameters.put(RiceConstants.QUESTION_CONTEXT, context);
        parameters.put(RiceConstants.QUESTION_SHOW_REASON_FIELD, Boolean.toString(showReasonField));
        parameters.put(RiceConstants.QUESTION_REASON_ATTRIBUTE_NAME, reason);
        parameters.put(RiceConstants.QUESTION_ERROR_KEY, errorKey);
        parameters.put(RiceConstants.QUESTION_ERROR_PROPERTY_NAME, errorPropertyName);
        parameters.put(RiceConstants.QUESTION_ERROR_PARAMETER, errorParameter);
        parameters.put(RiceConstants.QUESTION_ANCHOR, form instanceof KualiForm ? ObjectUtils.toString(((KualiForm) form).getAnchor()) : "");
        Object methodToCallAttribute = request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (methodToCallAttribute != null) {
            parameters.put(RiceConstants.METHOD_TO_CALL_PATH, methodToCallAttribute);
        }

        String questionUrl = UrlFactory.parameterizeUrl(getBasePath(request) + "/kr/" + RiceConstants.QUESTION_ACTION, parameters);
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
    	String returnUrl = null;
    	if ("/kr".equals(mapping.getModuleConfig().getPrefix())) {
    		returnUrl = getBasePath(request) + mapping.getModuleConfig().getPrefix() + mapping.getPath() + ".do";
    	} else {
    		returnUrl = getBasePath(request) + mapping.getPath() + ".do";
    	}
        

        String fullParameter = (String) request.getAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE);
        String conversionFields = StringUtils.substringBetween(fullParameter, RiceConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, RiceConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

        String deploymentBaseUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(RiceConstants.WORKFLOW_URL_KEY);
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
                return mapping.findForward(RiceConstants.MAPPING_BASIC);
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