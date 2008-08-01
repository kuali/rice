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
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.core.service.Demonstration;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.util.RiceConstants;

/**
 * This class is the base action class for all kuali actions. Overrides execute to set methodToCall for image submits. Other setup
 * for framework calls.
 *
 *
 */
/**
 * This is a description of what this class does - ctdang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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

        String methodToCall = findMethodToCall(form, request);
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getMethodToCall())) {
            if (StringUtils.isNotBlank(getImageContext(request, KNSConstants.ANCHOR))) {
                ((KualiForm) form).setAnchor(getImageContext(request, KNSConstants.ANCHOR));
            }
            else if (StringUtils.isNotBlank(request.getParameter(KNSConstants.ANCHOR))) {
                ((KualiForm) form).setAnchor(request.getParameter(KNSConstants.ANCHOR));
            }
            else {
                ((KualiForm) form).setAnchor(KNSConstants.ANCHOR_TOP_OF_FORM);
            }
        }
        // if found methodToCall, pass control to that method, else return the basic forward
        if (StringUtils.isNotBlank(methodToCall)) {
            LOG.debug("methodToCall: " + methodToCall);
            returnForward = dispatchMethod(mapping, form, request, response, methodToCall);
        }
        else {
            returnForward = mapping.findForward(RiceConstants.MAPPING_BASIC);
        }

        // make sure the user can do what they're trying to according to the module that owns the functionality
        checkAuthorization(form, methodToCall);

        // check if demonstration encryption is enabled
        if (KNSServiceLocator.getKualiConfigurationService().getIndicatorParameter(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.ALL_DETAIL_TYPE, KNSConstants.SystemGroupParameterNames.CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND) && KNSServiceLocator.getEncryptionService() instanceof Demonstration) {
            LOG.warn("WARNING: This implementation of Kuali uses the demonstration encryption framework.");
        }

        // Add the ActionForm to GlobalVariables
        // This will allow developers to retrieve both the Document and any request parameters that are not
        // part of the Form and make them available in ValueFinder classes and other places where they are needed.
        if(GlobalVariables.getKualiForm() == null) {
        GlobalVariables.setKualiForm((KualiForm)form);
        }

        return returnForward;
    }

    @Override
    protected ActionForward dispatchMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String methodToCall) throws Exception {
        GlobalVariables.getUserSession().addObject(DocumentAuthorizerBase.USER_SESSION_METHOD_TO_CALL_OBJECT_KEY, (Object)methodToCall);
        return super.dispatchMethod(mapping, form, request, response, methodToCall);
    }
    
    protected String findMethodToCall(ActionForm form, HttpServletRequest request) throws Exception {
        String methodToCall;
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getMethodToCall())) {
            methodToCall = ((KualiForm) form).getMethodToCall();
        }
        else {
            // call utility method to parse the methodToCall from the request.
            methodToCall = WebUtils.parseMethodToCall(request);
        }
        return methodToCall;
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
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
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
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
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
        String imageContext = getImageContext(request, KNSConstants.NAVIGATE_TO);
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
        String imageContext = getImageContext(request, KNSConstants.HEADER_DISPATCH);
        if (StringUtils.isNotBlank(imageContext)) {
            headerTabDispatch = imageContext;
        }
        else {
            // In some cases it might be in request params instead
            headerTabDispatch = request.getParameter(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
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
        String parameterName = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
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
        String fullParameter = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KNSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(KNSConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(KNSConstants.FIELD_CONVERSIONS_SEPERATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "lookupParams: " + lookupParams );
            }
            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(KNSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

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
        String readOnlyFields = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM8_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM8_RIGHT_DEL);
        if (StringUtils.isNotBlank(readOnlyFields)) {
            parameters.put(KNSConstants.LOOKUP_READ_ONLY_FIELDS, readOnlyFields);
        }

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "readOnlyFields: " + readOnlyFields );
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(KNSConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        // add the optional extra button source and parameters string
        String extraButtonSource = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonSource)) {
            parameters.put(KNSConstants.EXTRA_BUTTON_SOURCE, extraButtonSource);
        }
        String extraButtonParams = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM5_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM5_RIGHT_DEL);
        if (StringUtils.isNotBlank(extraButtonParams)) {
            parameters.put(KNSConstants.EXTRA_BUTTON_PARAMS, extraButtonParams);
        }

        String lookupAction = KNSConstants.LOOKUP_ACTION;

        // is this a multi-value return?
        String multipleValues = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM6_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM6_RIGHT_DEL);
        if ((new Boolean(multipleValues).booleanValue())) {
            parameters.put(KNSConstants.MULTIPLE_VALUE, multipleValues);
            lookupAction = KNSConstants.MULTIPLE_VALUE_LOOKUP_ACTION;
        }

        // the name of the collection being looked up (primarily for multivalue lookups
        String lookedUpCollectionName = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM11_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM11_RIGHT_DEL);
        if (StringUtils.isNotBlank(lookedUpCollectionName)) {
            parameters.put(KNSConstants.LOOKED_UP_COLLECTION_NAME, lookedUpCollectionName);
        }

        // grab whether or not the "supress actions" column should be hidden or not
        String supressActions = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM7_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM7_RIGHT_DEL);
        if (StringUtils.isNotBlank(supressActions)) {
            parameters.put(KNSConstants.SUPPRESS_ACTIONS, supressActions);
        }

        // grab the references that should be refreshed upon returning from the lookup
        String referencesToRefresh = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM10_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM10_RIGHT_DEL);
        if (StringUtils.isNotBlank(referencesToRefresh)) {
            parameters.put(KNSConstants.REFERENCES_TO_REFRESH, referencesToRefresh);
        }

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(KNSConstants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // now add required parameters
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "start");

        // pass value from form that shows if autoSearch is desired for lookup search
        String autoSearch = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM9_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM9_RIGHT_DEL);

        if (StringUtils.isNotBlank(autoSearch)) {
            parameters.put(KNSConstants.LOOKUP_AUTO_SEARCH, autoSearch);
            if ("YES".equalsIgnoreCase(autoSearch)){
                parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "search");
            }
        }

        parameters.put(KNSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);

        parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));
        
    	if (form instanceof KualiDocumentFormBase) {
			parameters.put(KNSConstants.DOC_NUM, ((KualiDocumentFormBase) form)
					.getDocument().getDocumentNumber());
		}else if(form instanceof LookupForm){
    		parameters.put(KNSConstants.DOC_NUM, ((LookupForm) form).getDocNum());
    	}
    	
        String lookupUrl = UrlFactory.parameterizeUrl(getBasePath(request) + "/kr/" + lookupAction, parameters);
        return new ActionForward(lookupUrl, true);
    }

    public ActionForward performInquiry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KNSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform inquiry, no business object class name specified.");
        }

        // build the parameters for the inquiry url
        Properties parameters = new Properties();
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);

        parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));

        // pass values from form that should be pre-populated on inquiry
        String parameterFields = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        if (StringUtils.isNotBlank(parameterFields)) {
            // TODO : create a method for this to be used by both lookup & inquiry ?
            String[] inquiryParams = parameterFields.split(KNSConstants.FIELD_CONVERSIONS_SEPERATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "inquiryParams: " + inquiryParams );
            }
            for (int i = 0; i < inquiryParams.length; i++) {
                String[] keyValue = inquiryParams[i].split(KNSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

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
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KNSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));

        String inquiryAction = "directInquiry.do";
        String inquiryUrl = UrlFactory.parameterizeUrl(basePath + "/kr/" + KNSConstants.DIRECT_INQUIRY_ACTION, parameters);
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

        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, "start");
        parameters.put(KNSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(KNSConstants.CALLING_METHOD, caller);
        parameters.put(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME, questionId);
        parameters.put(KNSConstants.QUESTION_IMPL_ATTRIBUTE_NAME, questionType);
        parameters.put(KNSConstants.QUESTION_TEXT_ATTRIBUTE_NAME, questionText);
        parameters.put(KNSConstants.RETURN_LOCATION_PARAMETER, getReturnLocation(request, mapping));
        parameters.put(KNSConstants.QUESTION_CONTEXT, context);
        parameters.put(KNSConstants.QUESTION_SHOW_REASON_FIELD, Boolean.toString(showReasonField));
        parameters.put(KNSConstants.QUESTION_REASON_ATTRIBUTE_NAME, reason);
        parameters.put(KNSConstants.QUESTION_ERROR_KEY, errorKey);
        parameters.put(KNSConstants.QUESTION_ERROR_PROPERTY_NAME, errorPropertyName);
        parameters.put(KNSConstants.QUESTION_ERROR_PARAMETER, errorParameter);
        parameters.put(KNSConstants.QUESTION_ANCHOR, form instanceof KualiForm ? ObjectUtils.toString(((KualiForm) form).getAnchor()) : "");
        Object methodToCallAttribute = request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (methodToCallAttribute != null) {
            parameters.put(KNSConstants.METHOD_TO_CALL_PATH, methodToCallAttribute);
        }
        
    	if (form instanceof KualiDocumentFormBase) {
			parameters.put(KNSConstants.DOC_NUM, ((KualiDocumentFormBase) form)
					.getDocument().getDocumentNumber());
		}

        String questionUrl = UrlFactory.parameterizeUrl(getBasePath(request) + "/kr/" + KNSConstants.QUESTION_ACTION, parameters);
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


        String fullParameter = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String conversionFields = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);

        String deploymentBaseUrl = KNSServiceLocator.getKualiConfigurationService().getPropertyString(KNSConstants.WORKFLOW_URL_KEY);
        String workgroupLookupUrl = deploymentBaseUrl + "/Lookup.do?lookupableImplServiceName=WorkGroupLookupableImplService&methodToCall=start&docFormKey=" + GlobalVariables.getUserSession().addObject(form);

        if (conversionFields != null) {
            workgroupLookupUrl += "&conversionFields=" + conversionFields;
        }
    	if (form instanceof KualiDocumentFormBase) {
			workgroupLookupUrl +="&docNum="+ ((KualiDocumentFormBase) form).getDocument().getDocumentNumber();
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
            ActionForward forward = dispatchMethod(mapping, form, request, response, headerTabDispatch);
            if (GlobalVariables.getErrorMap().size() > 0) {
                return mapping.findForward(RiceConstants.MAPPING_BASIC);
            }
            this.hideAllTabs(mapping, form, request, response);
            if (forward.getRedirect()) {
                return forward;
            }
        }
        return dispatchMethod(mapping, form, request, response, getHeaderTabNavigateTo(request));
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

    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaFieldName
     */
    public static final String TEXT_AREA_FIELD_NAME="textAreaFieldName";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaFieldLabel
    */
    public static final String TEXT_AREA_FIELD_LABEL="textAreaFieldLabel";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is textAreaFieldAnchor
    */
    public static final String TEXT_AREA_FIELD_ANCHOR="textAreaFieldAnchor";
    /**
     * Constant defined to match with TextArea.jsp and updateTextArea function in core.js
     * <p>Value is htmlFormAction
    */
    public static final String FORM_ACTION="htmlFormAction";
    /**
     * Constant defined to match input parameter from URL and from TextArea.jsp.
     * <p>Value is methodToCall
    */
    public static final String METHOD_TO_CALL="methodToCall";
    /**
     * Constant defined to match with global forwarding in struts-config.xml
     * for Text Area Update.
     * <p>Value is updateTextArea
    */
    public static final String FORWARD_TEXT_AREA_UPDATE="updateTextArea";
    /**
     * Constant defined to match with method to call in TextArea.jsp.
     * <p>Value is postTextAreaToParent
    */
    public static final String POST_TEXT_AREA_TO_PARENT="postTextAreaToParent";
    /**
     * Constant defined to match with local forwarding in struts-config.xml
     * for the parent of the Updated Text Area.
     * <p>Value is forwardNext
    */
    public static final String FORWARD_NEXT="forwardNext";

    /**
     * This method is invoked when Java Script is turned off from the web browser. It
     * setup the information that the update text area requires for copying current text
     * in the calling page text area and returning to the calling page. The information
     * is passed to the JSP through Http Request attributes. All other parameters are
     * forwarded 
     *  
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
    public ActionForward updateTextArea(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response)  {
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s", form.getClass().getSimpleName(),
                    request.getRequestURI());
            LOG.trace(lm);
        }
                                
        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(
                KNSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse textfieldname:htmlformaction
        String parameterFields = StringUtils.substringBetween(fullParameter,
                KNSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL,
                KNSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "fullParameter: " + fullParameter );
            LOG.debug( "parameterFields: " + parameterFields );
        }
        String[] keyValue = null;
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] textAreaParams = parameterFields.split(
                    KNSConstants.FIELD_CONVERSIONS_SEPERATOR);
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "lookupParams: " + textAreaParams );
            }
            for (int i = 0; i < textAreaParams.length; i++) {
                keyValue = textAreaParams[i].split(
                        KNSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "keyValue[0]: " + keyValue[0] );
                    LOG.debug( "keyValue[1]: " + keyValue[1] );
                }
            }
        }
        
        request.setAttribute(TEXT_AREA_FIELD_NAME, keyValue[0]);
        request.setAttribute(FORM_ACTION,keyValue[1]);
        request.setAttribute(TEXT_AREA_FIELD_LABEL,keyValue[2]);
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            request.setAttribute(TEXT_AREA_FIELD_ANCHOR,((KualiForm) form).getAnchor());
        }

        // Set document related parameter
        String docWebScope=(String)request.getAttribute(KNSConstants.DOCUMENT_WEB_SCOPE);
        if (docWebScope != null && docWebScope.trim().length() >= 0) {
            request.setAttribute(KNSConstants.DOCUMENT_WEB_SCOPE, docWebScope);
        }
        String docFormKey=(String)request.getAttribute(KNSConstants.DOC_FORM_KEY);
        if (docFormKey != null && docFormKey.trim().length() >= 0) {
            request.setAttribute(KNSConstants.DOC_FORM_KEY, docFormKey);
        }
        
        ActionForward forward=mapping.findForward(FORWARD_TEXT_AREA_UPDATE);

        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", (forward==null)?"null":forward.getPath());
            LOG.trace(lm);
        }
                        
        return forward;

    }
    /**
     * This method is invoked from the TextArea.jsp for posting its value to the parent
     * page that called the extended text area page. The invocation is done through
     * Struts action. The default forwarding id is RiceContants.MAPPING_BASIC. This
     * can be overridden using the parameter key FORWARD_NEXT.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings("unchecked")
    public ActionForward postTextAreaToParent(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        if (LOG.isTraceEnabled()) {
            String lm=String.format("ENTRY %s%n%s", form.getClass().getSimpleName(),
                    request.getRequestURI());
            LOG.trace(lm);
        }
                        
        String forwardingId=request.getParameter(FORWARD_NEXT);
        if (forwardingId == null) {
            forwardingId=RiceConstants.MAPPING_BASIC;
        }
        ActionForward forward=mapping.findForward(forwardingId);
             
        if (LOG.isTraceEnabled()) {
            String lm=String.format("EXIT %s", (forward==null)?"null":forward.getPath());
            LOG.trace(lm);
        }
                        
        return forward;
    }

}