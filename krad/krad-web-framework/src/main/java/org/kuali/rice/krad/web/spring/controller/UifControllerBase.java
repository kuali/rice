/*
 * Copyright 2007 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.web.spring.controller;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LookupInquiryUtils;
import org.kuali.rice.krad.uif.util.UifWebUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.util.WebUtils;
import org.kuali.rice.krad.web.spring.form.UifFormBase;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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

    protected static final String REDIRECT_PREFIX = "redirect:";
    private SessionDocumentService sessionDocumentService;

    /**
     * Create/obtain the model(form) object before it is passed
     * to the Binder/BeanWrapper. This method is not intended to be overridden
     * by client applications as it handles framework setup and session
     * maintenance. Clients should override createIntialForm() instead when they
     * need custom form initialization.
     */
    @ModelAttribute(value = "KualiForm")
    public UifFormBase initForm(HttpServletRequest request) {
        UifFormBase form = null;
        String formKeyParam = request.getParameter(UifParameters.FORM_KEY);
        String documentNumber = request.getParameter(KRADConstants.DOCUMENT_DOCUMENT_NUMBER);

        if (StringUtils.isNotBlank(formKeyParam)) {
            form = (UifFormBase) request.getSession().getAttribute(formKeyParam);

            // retreive from db if form not in session
            if (form == null) {
                UserSession userSession = (UserSession) request.getSession()
                        .getAttribute(KRADConstants.USER_SESSION_KEY);
                form = getSessionDocumentService().getUifDocumentForm(documentNumber, formKeyParam, userSession,
                        request.getRemoteAddr());
            }
        } else {
            form = createInitialForm(request);
        }

        return form;
    }

    /**
     * Called to create a new model(form) object when
     * necessary. This usually occurs on the initial request in a conversation
     * (when the model is not present in the session). This method must be
     * overridden when extending a controller and using a different form type
     * than the superclass.
     */
    protected abstract UifFormBase createInitialForm(HttpServletRequest request);

    private Set<String> methodToCallsToNotCheckAuthorization = new HashSet<String>();
    {
        methodToCallsToNotCheckAuthorization.add("performLookup");
        methodToCallsToNotCheckAuthorization.add("performQuestion");
        methodToCallsToNotCheckAuthorization.add("performQuestionWithInput");
        methodToCallsToNotCheckAuthorization.add("performQuestionWithInputAgainBecauseOfErrors");
        methodToCallsToNotCheckAuthorization.add("performQuestionWithoutInput");
        methodToCallsToNotCheckAuthorization.add("performWorkgroupLookup");
    }

    /**
     * Use to add a methodToCall to the a list which will not have authorization
     * checks. This assumes that the call will be redirected (as in the case of
     * a lookup) that will perform the authorization.
     */
    protected final void addMethodToCallToUncheckedList(String methodToCall) {
        methodToCallsToNotCheckAuthorization.add(methodToCall);
    }

    /**
     * Returns an immutable Set of methodToCall parameters that should not be
     * checked for authorization.
     */
    public Set<String> getMethodToCallsToNotCheckAuthorization() {
        return Collections.unmodifiableSet(methodToCallsToNotCheckAuthorization);
    }

    /**
     * Override this method to provide controller class-level access controls to
     * the application.
     */
    public void checkAuthorization(UifFormBase form, String methodToCall) throws AuthorizationException {
        String principalId = GlobalVariables.getUserSession().getPrincipalId();
        AttributeSet roleQualifier = new AttributeSet(getRoleQualification(form, methodToCall));
        AttributeSet permissionDetails = KRADUtils.getNamespaceAndActionClass(this.getClass());

        if (!KimApiServiceLocator.getIdentityManagementService().isAuthorizedByTemplateName(principalId,
                KRADConstants.KRAD_NAMESPACE, KimConstants.PermissionTemplateNames.USE_SCREEN, permissionDetails,
                roleQualifier)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(),
                    methodToCall, this.getClass().getSimpleName());
        }
    }

    /**
     * Override this method to add data from the form for role qualification in
     * the authorization check
     */
    protected Map<String, String> getRoleQualification(UifFormBase form, String methodToCall) {
        return new HashMap<String, String>();
    }

    /**
     * Initial method called when requesting a new view instance which forwards
     * the view for rendering
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=start")
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {

        return getUIFModelAndView(form);
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

        View view = uifForm.getPreviousView();
        view.getViewHelperService().processCollectionAddLine(view, uifForm, selectedCollectionPath);

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

        View view = uifForm.getPreviousView();
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

        CollectionGroup collectionGroup = (CollectionGroup) ComponentFactory.getComponentById(collectionGroupId);

        // update inactive flag on group
        collectionGroup.setShowInactive(showInactive);

        // run lifecycle and update in view
        uifForm.getView().getViewHelperService().performComponentLifecycle(uifForm, collectionGroup, collectionGroupId);
        uifForm.getView().getViewIndex().indexComponent(collectionGroup);

        return UifWebUtils.getComponentModelAndView(collectionGroup, uifForm);
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

        return performRedirect(form, returnUrl, props);
    }

    /**
     * Handles menu navigation between view pages
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=navigate")
    public ModelAndView navigate(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String pageId = form.getActionParamaterValue(UifParameters.NAVIGATE_TO_PAGE_ID);

        // only refreshing page
        form.setRenderFullView(false);

        return getUIFModelAndView(form, form.getViewId(), pageId);
    }

    @RequestMapping(params = "methodToCall=refresh")
    public ModelAndView refresh(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO: this code still needs ported with whatever we are supposed
        // to do on refresh
        return getUIFModelAndView(form);
    }

    /**
     * Updates the current component by retrieving a fresh copy from the dictionary,
     * running its component lifecycle, and returning it
     *
     * @param request - the request must contain reqComponentId that specifies the component to retrieve
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=updateComponent")
    public ModelAndView updateComponent(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        String requestedComponentId = request.getParameter(UifParameters.REQUESTED_COMPONENT_ID);
        if (StringUtils.isBlank(requestedComponentId)) {
            throw new RuntimeException("Requested component id for update not found in request");
        }

        Component comp = ComponentFactory.getComponentByIdWithLifecycle(form, requestedComponentId);

        return UifWebUtils.getComponentModelAndView(comp, form);
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
            Map<String, String> lookupParameterFields = WebUtils.getMapFromParameterString(lookupParameterString);
            for (Entry<String, String> lookupParameter : lookupParameterFields.entrySet()) {
                String lookupParameterValue = LookupInquiryUtils.retrieveLookupParameterValue(form, request,
                        lookupObjectClass, lookupParameter.getValue(), lookupParameter.getKey());
                if (StringUtils.isNotBlank(lookupParameterValue)) {
                    lookupParameters.put(lookupParameter.getValue(), lookupParameterValue);
                }
            }
        }

        // TODO: lookup anchors and doc number?

        // TODO: multi-value lookup requests

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
            ModuleService responsibleModuleService = KRADServiceLocatorWeb.getKualiModuleService()
                    .getResponsibleModuleService(lookupObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(lookupObjectClass)) {
                Map<String, String> parameterMap = new HashMap<String, String>();
                Enumeration<Object> e = lookupParameters.keys();
                while (e.hasMoreElements()) {
                    String paramName = (String) e.nextElement();
                    parameterMap.put(paramName, lookupParameters.getProperty(paramName));
                }

                String lookupUrl = responsibleModuleService.getExternalizableBusinessObjectLookupUrl(lookupObjectClass,
                        parameterMap);
                return performRedirect(form, lookupUrl, new Properties());
            }
        }

        return performRedirect(form, baseLookupUrl, lookupParameters);
    }

    /**
     * Invoked to provide the options for a suggest widget. The valid options are retrieved by the associated
     * <code>AttributeQuery</code> for the field containing the suggest widget. The controller method picks
     * out the query parameters from the request and calls <code>AttributeQueryService</code> to perform the
     * suggest query and prepare the result object that will be exposed with JSON
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=performFieldSuggest")
    public @ResponseBody AttributeQueryResult performFieldSuggest(@ModelAttribute("KualiForm") UifFormBase form,
            BindingResult result, HttpServletRequest request, HttpServletResponse response) {

        // retrieve query fields from request
        Map<String, String> queryParameters = new HashMap<String, String>();
        for (Object parameterName : request.getParameterMap().keySet()) {
            if (parameterName.toString().startsWith(UifParameters.QUERY_PARAMETER + ".")) {
                String fieldName =
                        StringUtils.substringAfter(parameterName.toString(), UifParameters.QUERY_PARAMETER + ".");
                String fieldValue = request.getParameter(parameterName.toString());
                queryParameters.put(fieldName, fieldValue);
            }
        }

        // retrieve id for field to perform query for
        String queryFieldId = request.getParameter(UifParameters.QUERY_FIELD_ID);
        if (StringUtils.isBlank(queryFieldId)) {
            throw new RuntimeException(
                    "Unable to find id for field to perform query on under request parameter name: " +
                            UifParameters.QUERY_FIELD_ID);
        }

        // get the field term to match
        String queryTerm = request.getParameter(UifParameters.QUERY_TERM);
        if (StringUtils.isBlank(queryTerm)) {
            throw new RuntimeException(
                    "Unable to find id for query term value for attribute query on under request parameter name: " +
                            UifParameters.QUERY_TERM);
        }

        // invoke attribute query service to perform the query
        AttributeQueryResult queryResult = KRADServiceLocatorWeb.getAttributeQueryService()
                .performFieldSuggestQuery(form.getView(), queryFieldId, queryTerm, queryParameters);

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
    public @ResponseBody AttributeQueryResult performFieldQuery(@ModelAttribute("KualiForm") UifFormBase form,
            BindingResult result, HttpServletRequest request, HttpServletResponse response) {

        // retrieve query fields from request
        Map<String, String> queryParameters = new HashMap<String, String>();
        for (Object parameterName : request.getParameterMap().keySet()) {
            if (parameterName.toString().startsWith(UifParameters.QUERY_PARAMETER + ".")) {
                String fieldName =
                        StringUtils.substringAfter(parameterName.toString(), UifParameters.QUERY_PARAMETER + ".");
                String fieldValue = request.getParameter(parameterName.toString());
                queryParameters.put(fieldName, fieldValue);
            }
        }

        // retrieve id for field to perform query for
        String queryFieldId = request.getParameter(UifParameters.QUERY_FIELD_ID);
        if (StringUtils.isBlank(queryFieldId)) {
            throw new RuntimeException(
                    "Unable to find id for field to perform query on under request parameter name: " +
                            UifParameters.QUERY_FIELD_ID);
        }

        // invoke attribute query service to perform the query
        AttributeQueryResult queryResult = KRADServiceLocatorWeb.getAttributeQueryService()
                .performFieldQuery(form.getView(), queryFieldId, queryParameters);

        return queryResult;
    }

    /**
     * Builds a <code>ModelAndView</code> instance configured to redirect to the
     * URL formed by joining the base URL with the given URL parameters
     *
     * @param form
     *            - current form instance
     * @param baseUrl
     *            - base url to redirect to
     * @param urlParameters
     *            - properties containing key/value pairs for the url parameters
     * @return ModelAndView configured to redirect to the given URL
     */
    protected ModelAndView performRedirect(UifFormBase form, String baseUrl, Properties urlParameters) {
        // On post redirects we need to make sure we are sending the history
        // forward:
        urlParameters.setProperty(UifConstants.UrlParams.HISTORY, form.getFormHistory().getHistoryParameterString());

        // If this is an Ajax call only return the redirectURL view with the URL
        // set this is to avoid automatic redirect when using light boxes
        if (urlParameters.get("ajaxCall") != null && urlParameters.get("ajaxCall").equals("true")) {
            urlParameters.remove("ajaxCall");
            String redirectUrl = UrlFactory.parameterizeUrl(baseUrl, urlParameters);

            ModelAndView modelAndView = new ModelAndView("redirectURL");
            modelAndView.addObject("redirectUrl", redirectUrl);
            return modelAndView;
        }

        String redirectUrl = UrlFactory.parameterizeUrl(baseUrl, urlParameters);
        ModelAndView modelAndView = new ModelAndView(REDIRECT_PREFIX + redirectUrl);

        return modelAndView;
    }

    protected ModelAndView getUIFModelAndView(UifFormBase form) {
        return getUIFModelAndView(form, form.getViewId(), form.getPageId());
    }

    protected ModelAndView getUIFModelAndView(UifFormBase form, String viewId) {
        return getUIFModelAndView(form, viewId, "");
    }

    /**
     * Configures the <code>ModelAndView</code> instance containing the form
     * data and pointing to the UIF generic spring view
     *
     * @param form
     *            - Form instance containing the model data
     * @param viewId
     *            - Id of the View to return
     * @param pageId
     *            - Id of the page within the view that should be rendered, can
     *            be left blank in which the current or default page is rendered
     * @return ModelAndView object with the contained form
     */
    protected ModelAndView getUIFModelAndView(UifFormBase form, String viewId, String pageId) {
        return UifWebUtils.getUIFModelAndView(form, viewId, pageId);
    }

    protected ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

    public SessionDocumentService getSessionDocumentService() {
        return KRADServiceLocatorWeb.getSessionDocumentService();
    }

}
