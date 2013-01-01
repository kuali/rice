/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.web.controller;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LookupInquiryUtils;
import org.kuali.rice.krad.uif.view.MessageView;
import org.kuali.rice.krad.web.form.UifFormManager;
import org.kuali.rice.krad.uif.view.DialogManager;
import org.kuali.rice.krad.uif.view.History;
import org.kuali.rice.krad.uif.view.HistoryEntry;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Base controller class for views within the KRAD User Interface Framework
 *
 * Provides common methods such as:
 *
 * <ul>
 * <li>Authorization methods such as method to call check</li>
 * <li>Preparing the View instance and setup in the returned
 * <code>ModelAndView</code></li>
 * </ul>
 *
 * All subclass controller methods after processing should call one of the
 * #getUIFModelAndView methods to setup the <code>View</code> and return the
 * <code>ModelAndView</code> instance.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UifControllerBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UifControllerBase.class);

    private UrlBasedViewResolver viewResolver;

    /**
     * Create/obtain the model(form) object before it is passed to the Binder/BeanWrapper. This method
     * is not intended to be overridden by client applications as it handles framework setup and session
     * maintenance. Clients should override createInitialForm() instead when they need custom form initialization.
     *
     * @param request - the http request that was made
     */
    @ModelAttribute(value = "KualiForm")
    public UifFormBase initForm(HttpServletRequest request) {
        UifFormBase requestForm = null;

        // get Uif form manager from session if exists or setup a new one for the session
        UifFormManager uifFormManager = (UifFormManager) request.getSession().getAttribute(UifParameters.FORM_MANAGER);
        if (uifFormManager == null) {
            uifFormManager = new UifFormManager();
            request.getSession().setAttribute(UifParameters.FORM_MANAGER, uifFormManager);
        }

        // add form manager to GlobalVariables for easy reference by other controller methods
        GlobalVariables.setUifFormManager(uifFormManager);

        // create a new form for every request
        requestForm = createInitialForm(request);

        String formKeyParam = request.getParameter(UifParameters.FORM_KEY);
        if (StringUtils.isNotBlank(formKeyParam)) {
            // retrieves the session form and updates the request from with the session transient attributes
            uifFormManager.updateFormWithSession(requestForm, formKeyParam);
        }

        // if form exist, remove unused forms from breadcrumb history
        if (requestForm != null) {
            UifControllerHelper.removeUnusedBreadcrumbs(uifFormManager, requestForm.getFormKey(), request.getParameter(
                    UifConstants.UrlParams.LAST_FORM_KEY));
        }

        // sets the request form in the request for later retrieval
        request.setAttribute(UifConstants.REQUEST_FORM, requestForm);

        return requestForm;
    }

    /**
     * Called to create a new model(form) object when necessary. This usually occurs on the initial request
     * in a conversation (when the model is not present in the session). This method must be
     * overridden when extending a controller and using a different form type than the superclass.
     *
     * @param request - the http request that was made
     */
    protected abstract UifFormBase createInitialForm(HttpServletRequest request);

    /**
     * Initial method called when requesting a new view instance which checks authorization and forwards
     * the view for rendering
     */
    @RequestMapping(params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        // check view authorization
        // TODO: this needs to be invoked for each request
        if (form.getView() != null) {
            String methodToCall = request.getParameter(KRADConstants.DISPATCH_REQUEST_PARAMETER);
            checkViewAuthorization(form, methodToCall);
        }

        return getUIFModelAndView(form);
    }

    /**
     * Invokes the configured {@link org.kuali.rice.krad.uif.view.ViewAuthorizer} to verify the user has access to
     * open the view. An exception is thrown if access has not been granted
     *
     * <p>
     * Note this method is invoked automatically by the controller interceptor for each request
     * </p>
     *
     * @param form - form instance containing the request data
     * @param methodToCall - the request parameter 'methodToCall' which is used to determine the controller
     * method invoked
     */
    public void checkViewAuthorization(UifFormBase form, String methodToCall) throws AuthorizationException {
        Person user = GlobalVariables.getUserSession().getPerson();

        boolean canOpenView = form.getView().getAuthorizer().canOpenView(form.getView(), form, user);
        if (!canOpenView) {
            throw new AuthorizationException(user.getPrincipalName(), "open", form.getView().getId(),
                    "User '" + user.getPrincipalName() + "' is not authorized to open view ID: " + form.getView()
                            .getId(), null);
        }
    }

    /**
     * Called by the add line action for a new collection line. Method
     * determines which collection the add action was selected for and invokes
     * the view helper service to add the line
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addLine")
    public ModelAndView addLine(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        String selectedCollectionPath = uifForm.getActionParamaterValue(UifParameters.SELLECTED_COLLECTION_PATH);
        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection was not set for add line action, cannot add new line");
        }

        View view = uifForm.getPostedView();
        view.getViewHelperService().processCollectionAddLine(view, uifForm, selectedCollectionPath);

        return getUIFModelAndView(uifForm);
    }

    /**
     * Called by the add blank line action for a new collection line
     *
     * <p>
     * Method determines which collection the add action was selected for and invokes the view helper service to
     * add the blank line.
     * </p>
     *
     * @param uifForm - form instance containing the request data
     * @return the  ModelAndView object
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=addBlankLine")
    public ModelAndView addBlankLine(@ModelAttribute("KualiForm") UifFormBase uifForm) {

        String selectedCollectionPath = uifForm.getActionParamaterValue(UifParameters.SELLECTED_COLLECTION_PATH);
        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection was not set for add line action, cannot add new line");
        }

        View view = uifForm.getPostedView();
        view.getViewHelperService().processCollectionAddBlankLine(view, uifForm, selectedCollectionPath);

        return getUIFModelAndView(uifForm);
    }

    /**
     * Called by the save line action for a new collection line. Does server side validation and provides hook
     * for client application to persist specific data.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveLine")
    public ModelAndView saveLine(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        String selectedCollectionPath = uifForm.getActionParamaterValue(UifParameters.SELLECTED_COLLECTION_PATH);
        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection was not set for add line action, cannot add new line");
        }

        int selectedLineIndex = -1;
        String selectedLine = uifForm.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
        if (StringUtils.isNotBlank(selectedLine)) {
            selectedLineIndex = Integer.parseInt(selectedLine);
        }

        if (selectedLineIndex == -1) {
            throw new RuntimeException("Selected line index was not set for delete line action, cannot delete line");
        }

        View view = uifForm.getPostedView();
        view.getViewHelperService().processCollectionSaveLine(view, uifForm, selectedCollectionPath, selectedLineIndex);

        return getUIFModelAndView(uifForm);
    }

    /**
     * Called by the delete line action for a model collection. Method
     * determines which collection the action was selected for and the line
     * index that should be removed, then invokes the view helper service to
     * process the action
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=deleteLine")
    public ModelAndView deleteLine(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        String selectedCollectionPath = uifForm.getActionParamaterValue(UifParameters.SELLECTED_COLLECTION_PATH);
        if (StringUtils.isBlank(selectedCollectionPath)) {
            throw new RuntimeException("Selected collection was not set for delete line action, cannot delete line");
        }

        int selectedLineIndex = -1;
        String selectedLine = uifForm.getActionParamaterValue(UifParameters.SELECTED_LINE_INDEX);
        if (StringUtils.isNotBlank(selectedLine)) {
            selectedLineIndex = Integer.parseInt(selectedLine);
        }

        if (selectedLineIndex == -1) {
            throw new RuntimeException("Selected line index was not set for delete line action, cannot delete line");
        }

        View view = uifForm.getPostedView();
        view.getViewHelperService().processCollectionDeleteLine(view, uifForm, selectedCollectionPath,
                selectedLineIndex);

        return getUIFModelAndView(uifForm);
    }

    /**
     * Invoked to toggle the show inactive indicator on the selected collection group and then
     * rerun the component lifecycle and rendering based on the updated indicator and form data
     *
     * @param request - request object that should contain the request component id (for the collection group)
     * and the show inactive indicator value
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=toggleInactiveRecordDisplay")
    public ModelAndView toggleInactiveRecordDisplay(@ModelAttribute("KualiForm") UifFormBase uifForm,
            BindingResult result, HttpServletRequest request, HttpServletResponse response) {
        String collectionGroupId = request.getParameter(UifParameters.REQUESTED_COMPONENT_ID);
        if (StringUtils.isBlank(collectionGroupId)) {
            throw new RuntimeException(
                    "Collection group id to update for inactive record display not found in request");
        }

        String showInactiveStr = request.getParameter(UifParameters.SHOW_INACTIVE_RECORDS);
        Boolean showInactive = false;
        if (StringUtils.isNotBlank(showInactiveStr)) {
            // TODO: should use property editors once we have util class
            showInactive = (Boolean) (new BooleanFormatter()).convertFromPresentationFormat(showInactiveStr);
        } else {
            throw new RuntimeException("Show inactive records flag not found in request");
        }

        CollectionGroup collectionGroup = (CollectionGroup) ComponentFactory.getNewInstanceForRefresh(
                uifForm.getPostedView(), collectionGroupId);

        // update inactive flag on group
        collectionGroup.setShowInactiveLines(showInactive);

        // run lifecycle and update in view
        uifForm.getPostedView().getViewHelperService().performComponentLifecycle(uifForm.getPostedView(), uifForm,
                collectionGroup, collectionGroupId);

        return getUIFModelAndView(uifForm);
    }

    /**
     * Just returns as if return with no value was selected.
     */
    @RequestMapping(params = "methodToCall=cancel")
    public ModelAndView cancel(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        return close(form, result, request, response);
    }

    /**
     * Just returns as if return with no value was selected.
     */
    @RequestMapping(params = "methodToCall=close")
    public ModelAndView close(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        Properties props = new Properties();
        props.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.REFRESH);
        if (StringUtils.isNotBlank(form.getReturnFormKey())) {
            props.put(UifParameters.FORM_KEY, form.getReturnFormKey());
        }

        // TODO this needs setup for lightbox and possible home location
        // property
        String returnUrl = form.getReturnLocation();
        if (StringUtils.isBlank(returnUrl)) {
            returnUrl = ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY);
        }

        // clear current form from session
        GlobalVariables.getUifFormManager().removeSessionForm(form);

        return performRedirect(form, returnUrl, props);
    }

    /**
     * Invoked to navigate back one page in history..
     *
     * @param form - form object that should contain the history object
     */
    @RequestMapping(params = "methodToCall=returnToPrevious")
    public ModelAndView returnToPrevious(@ModelAttribute("KualiForm") UifFormBase form) {

        return returnToHistory(form, false);
    }

    /**
     * Invoked to navigate back to the first page in history.
     *
     * @param form - form object that should contain the history object
     */
    @RequestMapping(params = "methodToCall=returnToHub")
    public ModelAndView returnToHub(@ModelAttribute("KualiForm") UifFormBase form) {

        return returnToHistory(form, true);
    }

    /**
     * Invoked to navigate back to a history entry. The homeFlag will determine whether navigation
     * will be back to the first or last history entry.
     *
     * @param form - form object that should contain the history object
     * @param homeFlag - if true will navigate back to first entry else will navigate to last entry
     * in the history
     */
    public ModelAndView returnToHistory(UifFormBase form, boolean homeFlag) {
        // Get the history from the form
        History hist = form.getFormHistory();
        List<HistoryEntry> histEntries = hist.getHistoryEntries();

        // Get the history page url. Default to the application url if there is no history.
        String histUrl = null;
        if (histEntries.isEmpty()) {
            // TODO: use configuration service here
            histUrl = ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY);
        } else {
            // For home get the first entry, for previous get the last entry.
            // Remove history up to where page is opened
            if (homeFlag) {
                histUrl = histEntries.get(0).getUrl();
                histEntries.clear();
            } else {
                histUrl = histEntries.get(histEntries.size() - 1).getUrl();
                histEntries.remove(histEntries.size() - 1);
                hist.setCurrent(null);
            }
        }

        // Add the refresh call
        Properties props = new Properties();
        props.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.REFRESH);

        // clear current form from session
        GlobalVariables.getUifFormManager().removeSessionForm(form);

        return performRedirect(form, histUrl, props);
    }

    /**
     * Handles menu navigation between view pages
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=navigate")
    public ModelAndView navigate(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String pageId = form.getActionParamaterValue(UifParameters.NAVIGATE_TO_PAGE_ID);

        return getUIFModelAndView(form, pageId);
    }

    /**
     * handles an ajax refresh
     *
     * <p>The query form plugin  activates this request via a form post, where on the JS side,
     * {@code org.kuali.rice.krad.uif.UifParameters#RENDER_FULL_VIEW} is set to false</p>
     *
     * @param form -  Holds properties necessary to determine the <code>View</code> instance that will be used to
     * render
     * the UI
     * @param result -   represents binding results
     * @param request - http servlet request data
     * @param response - http servlet response object
     * @return the  ModelAndView object
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=refresh")
    public ModelAndView refresh(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO: this code still needs to handle reference refreshes
        String refreshCallerType = "";
        if (request.getParameterMap().containsKey(KRADConstants.REFRESH_CALLER_TYPE)) {
            refreshCallerType = request.getParameter(KRADConstants.REFRESH_CALLER_TYPE);
        }

        // process multi-value lookup returns
        if (StringUtils.equals(refreshCallerType, UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP)) {
            String lookupCollectionName = "";
            if (request.getParameterMap().containsKey(UifParameters.LOOKUP_COLLECTION_NAME)) {
                lookupCollectionName = request.getParameter(UifParameters.LOOKUP_COLLECTION_NAME);
            }

            if (StringUtils.isBlank(lookupCollectionName)) {
                throw new RuntimeException(
                        "Lookup collection name is required for processing multi-value lookup results");
            }

            String selectedLineValues = "";
            if (request.getParameterMap().containsKey(UifParameters.SELECTED_LINE_VALUES)) {
                selectedLineValues = request.getParameter(UifParameters.SELECTED_LINE_VALUES);
            }

            // invoked view helper to populate the collection from lookup results
            form.getPostedView().getViewHelperService().processMultipleValueLookupResults(form.getPostedView(), form,
                    lookupCollectionName, selectedLineValues);
        }

        return getUIFModelAndView(form);
    }

    /**
     * Builds up a URL to the lookup view based on the given post action
     * parameters and redirects
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=performLookup")
    public ModelAndView performLookup(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        Properties lookupParameters = form.getActionParametersAsProperties();

        String lookupObjectClassName = (String) lookupParameters.get(UifParameters.DATA_OBJECT_CLASS_NAME);
        Class<?> lookupObjectClass = null;
        try {
            lookupObjectClass = Class.forName(lookupObjectClassName);
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to get class for name: " + lookupObjectClassName);
            throw new RuntimeException("Unable to get class for name: " + lookupObjectClassName, e);
        }

        // get form values for the lookup parameter fields
        String lookupParameterString = (String) lookupParameters.get(UifParameters.LOOKUP_PARAMETERS);
        if (lookupParameterString != null) {
            Map<String, String> lookupParameterFields = KRADUtils.getMapFromParameterString(lookupParameterString);
            for (Entry<String, String> lookupParameter : lookupParameterFields.entrySet()) {
                String lookupParameterValue = LookupInquiryUtils.retrieveLookupParameterValue(form, request,
                        lookupObjectClass, lookupParameter.getValue(), lookupParameter.getKey());

                if (StringUtils.isNotBlank(lookupParameterValue)) {
                    lookupParameters.put(UifPropertyPaths.LOOKUP_CRITERIA + "['" + lookupParameter.getValue() + "']",
                            lookupParameterValue);
                }
            }

            lookupParameters.remove(UifParameters.LOOKUP_PARAMETERS);
        }

        // TODO: lookup anchors and doc number?

        String baseLookupUrl = (String) lookupParameters.get(UifParameters.BASE_LOOKUP_URL);
        lookupParameters.remove(UifParameters.BASE_LOOKUP_URL);

        // set lookup method to call
        lookupParameters.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);
        String autoSearchString = (String) lookupParameters.get(UifParameters.AUTO_SEARCH);
        if (Boolean.parseBoolean(autoSearchString)) {
            lookupParameters.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.SEARCH);
        }

        lookupParameters.put(UifParameters.RETURN_LOCATION, form.getFormPostUrl());
        lookupParameters.put(UifParameters.RETURN_FORM_KEY, form.getFormKey());

        // special check for external object classes
        if (lookupObjectClass != null) {
            ModuleService responsibleModuleService =
                    KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(lookupObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(lookupObjectClass)) {
                String lookupUrl = responsibleModuleService.getExternalizableDataObjectLookupUrl(lookupObjectClass,
                        lookupParameters);

                Properties externalInquiryProperties = new Properties();
                if (lookupParameters.containsKey(UifParameters.LIGHTBOX_CALL)) {
                    externalInquiryProperties.put(UifParameters.LIGHTBOX_CALL, lookupParameters.get(
                            UifParameters.LIGHTBOX_CALL));
                }

                return performRedirect(form, lookupUrl, externalInquiryProperties);
            }
        }

        return performRedirect(form, baseLookupUrl, lookupParameters);
    }

    /**
     * Checks the form/view against all current and future validations and returns warnings for any validations
     * that fail
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=checkForm")
    public ModelAndView checkForm(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        KRADServiceLocatorWeb.getViewValidationService().validateViewSimulation(form.getPostedView(), form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked to provide the options for a suggest widget. The valid options are retrieved by the associated
     * <code>AttributeQuery</code> for the field containing the suggest widget. The controller method picks
     * out the query parameters from the request and calls <code>AttributeQueryService</code> to perform the
     * suggest query and prepare the result object that will be exposed with JSON
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=performFieldSuggest")
    public
    @ResponseBody
    AttributeQueryResult performFieldSuggest(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        // retrieve query fields from request
        Map<String, String> queryParameters = new HashMap<String, String>();
        for (Object parameterName : request.getParameterMap().keySet()) {
            if (parameterName.toString().startsWith(UifParameters.QUERY_PARAMETER + ".")) {
                String fieldName = StringUtils.substringAfter(parameterName.toString(),
                        UifParameters.QUERY_PARAMETER + ".");
                String fieldValue = request.getParameter(parameterName.toString());
                queryParameters.put(fieldName, fieldValue);
            }
        }

        // retrieve id for field to perform query for
        String queryFieldId = request.getParameter(UifParameters.QUERY_FIELD_ID);
        if (StringUtils.isBlank(queryFieldId)) {
            throw new RuntimeException("Unable to find id for field to perform query on under request parameter name: "
                    + UifParameters.QUERY_FIELD_ID);
        }

        // get the field term to match
        String queryTerm = request.getParameter(UifParameters.QUERY_TERM);
        if (StringUtils.isBlank(queryTerm)) {
            throw new RuntimeException(
                    "Unable to find id for query term value for attribute query on under request parameter name: "
                            + UifParameters.QUERY_TERM);
        }

        // invoke attribute query service to perform the query
        AttributeQueryResult queryResult = KRADServiceLocatorWeb.getAttributeQueryService().performFieldSuggestQuery(
                form.getPostedView(), queryFieldId, queryTerm, queryParameters);

        return queryResult;
    }

    /**
     * Invoked to execute the <code>AttributeQuery</code> associated with a field given the query parameters
     * found in the request. This controller method picks out the query parameters from the request and calls
     * <code>AttributeQueryService</code> to perform the field query and prepare the result object
     * that will be exposed with JSON. The result is then used to update field values in the UI with client
     * script.
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=performFieldQuery")
    public
    @ResponseBody
    AttributeQueryResult performFieldQuery(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        // retrieve query fields from request
        Map<String, String> queryParameters = new HashMap<String, String>();
        for (Object parameterName : request.getParameterMap().keySet()) {
            if (parameterName.toString().startsWith(UifParameters.QUERY_PARAMETER + ".")) {
                String fieldName = StringUtils.substringAfter(parameterName.toString(),
                        UifParameters.QUERY_PARAMETER + ".");
                String fieldValue = request.getParameter(parameterName.toString());
                queryParameters.put(fieldName, fieldValue);
            }
        }

        // retrieve id for field to perform query for
        String queryFieldId = request.getParameter(UifParameters.QUERY_FIELD_ID);
        if (StringUtils.isBlank(queryFieldId)) {
            throw new RuntimeException("Unable to find id for field to perform query on under request parameter name: "
                    + UifParameters.QUERY_FIELD_ID);
        }

        // invoke attribute query service to perform the query
        AttributeQueryResult queryResult = KRADServiceLocatorWeb.getAttributeQueryService().performFieldQuery(
                form.getPostedView(), queryFieldId, queryParameters);

        return queryResult;
    }

    /**
     * returns whether this dialog has been displayed on the client
     *
     * @param dialogId - the id of the dialog
     * @param form - form instance containing the request data
     * @return boolean - true if dialog has been displayed, false if not
     */
    protected boolean hasDialogBeenDisplayed(String dialogId, UifFormBase form) {
        return (form.getDialogManager().hasDialogBeenDisplayed(dialogId));
    }

    /**
     * returns whether the dialog has already been answered by the user
     *
     * @param dialogId - identifier for the dialog group
     * @param form - form instance containing the request data
     * @return boolean - true if client has already responded to the dialog, false otherwise
     */
    protected boolean hasDialogBeenAnswered(String dialogId, UifFormBase form) {
        return (form.getDialogManager().hasDialogBeenAnswered(dialogId));
    }

    /**
     * Handles modal dialog interactions for a view controller When a controller method wishes to prompt the user
     * for additional information before continuing to process the request.
     *
     * <p>
     * If this modal dialog has not yet been presented to the user, a redirect back to the client
     * is performed to display the modal dialog as a Lightbox. The DialogGroup identified by the
     * dialogId is used as the Lightbox content.
     * </p>
     *
     * <p>
     * If the dialog has already been answered by the user.  The boolean value representing the
     * option chosen by the user is returned back to the calling controller
     * </p>
     *
     * @param dialogId - identifier of the dialog group
     * @param form - form instance containing the request data
     * @param request - the http request
     * @param response - the http response
     * @return boolean - true if user chose affirmative response, false if negative response was chosen
     */
    protected boolean getBooleanDialogResponse(String dialogId, UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DialogManager dm = form.getDialogManager();
        if (!dm.hasDialogBeenAnswered(dialogId)) {
            showDialog(dialogId, form, request, response);
            
            // throw an exception until showDialog is able to complete request.
            // until then, programmers should check hasDialogBeenAnswered
            throw new RiceRuntimeException("Dialog has not yet been answered by client. "
                    + "Check that hasDialogBeenAnswered(id) returns true.");
        }
        
        return dm.wasDialogAnswerAffirmative(dialogId);
    }

    /**
     * Handles a modal dialog interaction with the client user when a String response is desired
     *
     * <p>
     * Similar to askYesOrNoQuestion() but returns a string instead of a boolean.  The string value is the key
     * string of the key/value pair assigned to the button that the user chose.
     * </p>
     *
     * @param dialogId - identifier of the dialog group
     * @param form - form instance containing the request data
     * @param request - the http request
     * @param response - the http response
     * @return
     * @throws Exception
     */
    protected String getStringDialogResponse(String dialogId, UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DialogManager dm = form.getDialogManager();
        if (!dm.hasDialogBeenAnswered(dialogId)) {
            showDialog(dialogId, form, request, response);
            
            // throw an exception until showDialog is able to complete request.
            // until then, programmers should check hasDialogBeenAnswered
            throw new RiceRuntimeException("Dialog has not yet been answered by client. "
                    + "Check that hasDialogBeenAnswered(id) returns true.");
        }
        
        return dm.getDialogAnswer(dialogId);
    }

    /**
     * Complete the response directly and launch lightbox with dialog content upon returning back to the client. If it
     * is an ajax request then set the ajaxReturnType and set the updateComponentId to the dialogId.
     *
     * <p>
     * Need to build up the view/component properly as we would if we returned normally back to the DispatcherServlet
     * from the controller method.
     * </p>
     *
     * @param dialogId - id of the dialog or group to use as content in the lightbox.
     * @param form - the form associated with the view
     * @param request - the http request
     * @param response - the http response
     * @return will return void.  actually, won't return at all.
     * @throws Exception
     */
    protected ModelAndView showDialog(String dialogId, UifFormBase form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // js script to invoke lightbox: runs onDocumentReady
        form.setLightboxScript("openLightboxOnLoad('" + dialogId + "');");
        form.getDialogManager().addDialog(dialogId, form.getMethodToCall());

        // if the dialog is being invoked sever side via ajax set the ajaxReturnType to update-dialog
        // and set the updateComponentId to the dialogId
        if (form.isAjaxRequest()) {
            form.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATEDIALOG.getKey());
            form.setUpdateComponentId(dialogId);
        }

        return getUIFModelAndView(form);
    }

    /**
     * Common return point for dialogs
     *
     * <p>
     * Determines the user responses to the dialog. Performs dialog management and then redirects to the
     * original contoller method.
     * </p>
     *
     * @param form - current form
     * @param result - binding result
     * @param request - http request
     * @param response - http response
     * @return ModelAndView setup for redirect to original controller methodToCall
     * @throws Exception
     */
    @RequestMapping(params = "methodToCall=returnFromLightbox")
    public ModelAndView returnFromLightbox(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        String newMethodToCall = "";
        
        // Save user responses from dialog
        DialogManager dm = form.getDialogManager();
        String dialogId = dm.getCurrentDialogId();
        if (dialogId == null){
            // may have been invoked by client.
            // TODO:  handle this case (scheduled for 2.2-m3)
            // for now, log WARNING and default to start, can we add a growl?
            newMethodToCall = "start";
        } else {
            dm.setDialogAnswer(dialogId, form.getDialogResponse());
            dm.setDialogExplanation(dialogId, form.getDialogExplanation());
            newMethodToCall = dm.getDialogReturnMethod(dialogId);
            dm.setCurrentDialogId(null);
        }

        // call intended controller method
        Properties props = new Properties();
        props.put(UifParameters.METHOD_TO_CALL, newMethodToCall);
        props.put(UifParameters.VIEW_ID, form.getViewId());
        props.put(UifParameters.FORM_KEY, form.getFormKey());
        props.put(UifParameters.AJAX_REQUEST,"false");

        return performRedirect(form, form.getFormPostUrl(), props);
    }

    /**
     * Builds a <code>ModelAndView</code> instance configured to redirect to the
     * URL formed by joining the base URL with the given URL parameters
     *
     * @param form - current form instance
     * @param baseUrl - base url to redirect to
     * @param urlParameters - properties containing key/value pairs for the url parameters, if null or empty,
     * the baseUrl will be used as the full URL
     * @return ModelAndView configured to redirect to the given URL
     */
    protected ModelAndView performRedirect(UifFormBase form, String baseUrl, Properties urlParameters) {
        // indicate a redirect is occuring to prevent view processing down the line
        form.setRequestRedirected(true);

        //set the ajaxReturnType on the form this will override the return type requested by the client
        form.setAjaxReturnType(UifConstants.AjaxReturnTypes.REDIRECT.getKey());

        if(urlParameters != null){
            // On post redirects we need to make sure we are sending the history forward:
            if(form.getFormHistory() != null)  {
                urlParameters.setProperty(UifConstants.UrlParams.HISTORY, form.getFormHistory().getHistoryParameterString());
            }
            // If this is an Light Box call only return the redirectURL view with the URL
            // set this is to avoid automatic redirect when using light boxes
            if (urlParameters.get(UifParameters.LIGHTBOX_CALL) != null && urlParameters.get(UifParameters.LIGHTBOX_CALL)
                    .equals("true")) {
                urlParameters.remove(UifParameters.LIGHTBOX_CALL);
                String redirectUrl = UrlFactory.parameterizeUrl(baseUrl, urlParameters);

                ModelAndView modelAndView = new ModelAndView(UifConstants.SPRING_REDIRECT_ID);
                modelAndView.addObject("redirectUrl", redirectUrl);
                return modelAndView;
            }
        }
        String redirectUrl = UrlFactory.parameterizeUrl(baseUrl, urlParameters);

        //If this is an ajax redirect get the model and view from the form
        if(form.isAjaxRequest()) {
            ModelAndView modelAndView = getUIFModelAndView(form, form.getPageId());
            modelAndView.addObject("redirectUrl", redirectUrl);
            return modelAndView ;
        }  else {
            ModelAndView modelAndView = new ModelAndView(UifConstants.REDIRECT_PREFIX + redirectUrl);
            return modelAndView;
        }

    }

    /**
     * Builds a message view from the given header and message text then forwards the UIF model and view
     *
     * <p>
     * If an error or other type of interruption occurs during the request processing the controller can
     * invoke this message to display the message to the user. This will abandon the view that was requested
     * and display a view with just the message
     * </p>
     *
     * @param form UIF form instance
     * @param headerText header text for the message view (can be blank)
     * @param messageText text for the message to display
     * @return ModelAndView
     */
    protected ModelAndView getMessageView(UifFormBase form, String headerText, String messageText) {
        // get a new message view
        MessageView messageView = (MessageView) getViewService().getViewById(UifConstants.MESSAGE_VIEW_ID);

        messageView.setHeaderText(headerText);
        messageView.setMessageText(messageText);

        form.setViewId(UifConstants.MESSAGE_VIEW_ID);
        form.setView(messageView);

        return getUIFModelAndView(form);
    }

    protected ModelAndView getUIFModelAndView(UifFormBase form) {
        return getUIFModelAndView(form, form.getPageId());
    }

    /**
     * Configures the <code>ModelAndView</code> instance containing the form
     * data and pointing to the UIF generic spring view
     *
     * @param form - Form instance containing the model data
     * @param pageId - Id of the page within the view that should be rendered, can
     * be left blank in which the current or default page is rendered
     * @return ModelAndView object with the contained form
     */
    protected ModelAndView getUIFModelAndView(UifFormBase form, String pageId) {
        return UifControllerHelper.getUIFModelAndView(form, pageId);
    }

    // TODO: add getUIFModelAndView that takes in a view id and can perform view switching

    protected ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

}
