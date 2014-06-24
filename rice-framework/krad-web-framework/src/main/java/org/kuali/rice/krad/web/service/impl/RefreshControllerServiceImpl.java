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
package org.kuali.rice.krad.web.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.kuali.rice.krad.web.service.RefreshControllerService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Default implementation of the refresh controller service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RefreshControllerServiceImpl implements RefreshControllerService {

    private ModelAndViewService modelAndViewService;

    /**
     * Handles the refresh call by checking the request parameters and delegating out to helper methods.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView refresh(UifFormBase form) {
        HttpServletRequest request = form.getRequest();

        if (request.getParameterMap().containsKey(UifParameters.MESSAGE_TO_DISPLAY)) {
            String messageToDisplay = request.getParameter(UifParameters.MESSAGE_TO_DISPLAY);

            if (StringUtils.isNotBlank(messageToDisplay)) {
                GlobalVariables.getMessageMap().putErrorForSectionId(KRADConstants.GLOBAL_ERRORS, messageToDisplay);
            }
        }

        if (request.getParameterMap().containsKey(UifParameters.REFRESH_STATUS)) {
            String refreshStatus = request.getParameter(UifParameters.REFRESH_STATUS);

            // if the return URL reported an error, do not continue with the refresh call
            if (UifConstants.RefreshStatus.ERROR.equals(refreshStatus)) {
                return getModelAndViewService().getModelAndView(form);
            }
        }

        String refreshCallerType = "";
        if (request.getParameterMap().containsKey(KRADConstants.REFRESH_CALLER_TYPE)) {
            refreshCallerType = request.getParameter(KRADConstants.REFRESH_CALLER_TYPE);
        }

        if (StringUtils.equals(refreshCallerType, UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP)) {
            processMultiValueReturn(form, request);
        }

        if (request.getParameterMap().containsKey(KRADConstants.REFERENCES_TO_REFRESH)) {
            final String referencesToRefresh = request.getParameter(KRADConstants.REFERENCES_TO_REFRESH);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ViewLifecycle.getHelper().refreshReferences(referencesToRefresh);
                }
            };

            ViewLifecycle.encapsulateLifecycle(form.getView(), form, form.getViewPostMetadata(), null, request,
                    runnable);
        }

        if (request.getParameterMap().containsKey(UifParameters.QUICKFINDER_ID)) {
            String quickfinderId = request.getParameter(UifParameters.QUICKFINDER_ID);

            setFocusJumpFromQuickfinder(form, quickfinderId);

            invokeQuickfinderCallback(form, request, quickfinderId);
        }

        return getModelAndViewService().getModelAndView(form);
    }

    /**
     * Handles the return from a multi-value lookup, processing any select line values and invoking the
     * configured view helper service to create the lines for those values in the model collection.
     *
     * <p>There are two supported strategies for returning the selected lines. One, if the lookup view
     * and the caller are within the same application container, Springs input flash map is used. If however,
     * the lookup view is outside the caller, then just a standard request parameter is used.</p>
     *
     * @param form form instance containing the model data
     * @param request http request object being handled
     */
    protected void processMultiValueReturn(final UifFormBase form, HttpServletRequest request) {
        final String lookupCollectionId = request.getParameter(UifParameters.LOOKUP_COLLECTION_ID);

        final String lookupCollectionName = request.getParameter(UifParameters.LOOKUP_COLLECTION_NAME);
        if (StringUtils.isBlank(lookupCollectionName)) {
            throw new RuntimeException("Lookup collection name is required for processing multi-value lookup results");
        }

        final String multiValueReturnFields = request.getParameter(UifParameters.MULIT_VALUE_RETURN_FILEDS);
        String selectedLineValuesParam = request.getParameter(UifParameters.SELECTED_LINE_VALUES);

        String flashMapSelectedLineValues = "";
        if (RequestContextUtils.getInputFlashMap(request) != null) {
            flashMapSelectedLineValues = (String) RequestContextUtils.getInputFlashMap(request).get(
                    UifParameters.SELECTED_LINE_VALUES);
        }

        if (!StringUtils.isBlank(flashMapSelectedLineValues)) {
            selectedLineValuesParam = flashMapSelectedLineValues;
        }

        final String selectedLineValues = selectedLineValuesParam;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // invoked view helper to populate the collection from lookup results
                ViewLifecycle.getHelper().processMultipleValueLookupResults(form, lookupCollectionId,
                        lookupCollectionName, multiValueReturnFields, selectedLineValues);
            }
        };

        ViewLifecycle.encapsulateLifecycle(form.getView(), form, form.getViewPostMetadata(), null, request, runnable);
    }

    /**
     * Retrieves the configured focus id and jump id for the quickfinder from the post metadata, and sets
     * those values onto the form for the view rendering.
     *
     * @param form form instance containing the model data
     * @param quickfinderId id for the quickfinder component that triggered the lookup we are
     * returning from
     */
    protected void setFocusJumpFromQuickfinder(UifFormBase form, String quickfinderId) {
        String focusId = (String) form.getViewPostMetadata().getComponentPostData(quickfinderId,
                UifConstants.PostMetadata.QUICKFINDER_FOCUS_ID);
        if (StringUtils.isNotBlank(focusId)) {
            form.setFocusId(focusId);
        }

        String jumpToId = (String) form.getViewPostMetadata().getComponentPostData(quickfinderId,
                UifConstants.PostMetadata.QUICKFINDER_JUMP_TO_ID);
        if (StringUtils.isNotBlank(jumpToId)) {
            form.setJumpToId(jumpToId);
        }
    }

    /**
     * Retrieves post metadata for the quickfinder component with the given id and if a callback method
     * has been configured, invokes that callback method.
     *
     * @param form form instance containing the model data
     * @param request http request object being handled
     * @param quickfinderId id for the quickfinder component that triggered the lookup we are
     * returning from
     */
    protected void invokeQuickfinderCallback(UifFormBase form, final HttpServletRequest request,
            final String quickfinderId) {
        String callbackMethodToCall = (String) form.getViewPostMetadata().getComponentPostData(quickfinderId,
                UifConstants.PostMetadata.QUICKFINDER_CALLBACK_METHOD_TO_CALL);
        MethodInvokerConfig callbackMethod = (MethodInvokerConfig) form.getViewPostMetadata().
                getComponentPostData(quickfinderId, UifConstants.PostMetadata.QUICKFINDER_CALLBACK_METHOD);

        if (StringUtils.isBlank(callbackMethodToCall) && (callbackMethod == null)) {
            return;
        }

        if (callbackMethod == null) {
            callbackMethod = new MethodInvokerConfig();
        }

        // get additional parameters to be passed to the callback method
        Map<String, String> callbackContext = (Map<String, String>) form.getViewPostMetadata().
                getComponentPostData(quickfinderId, UifConstants.PostMetadata.QUICKFINDER_CALLBACK_CONTEXT);

        // if target class or object not set, use view helper service
        if ((callbackMethod.getTargetClass() == null) && (callbackMethod.getTargetObject() == null)) {
            callbackMethod.setTargetObject(form.getViewHelperService());
        }

        callbackMethod.setTargetMethod(callbackMethodToCall);

        Object[] arguments = new Object[3];
        arguments[0] = form;
        arguments[1] = quickfinderId;
        arguments[2] = callbackContext;
        callbackMethod.setArguments(arguments);

        final MethodInvokerConfig methodToInvoke = callbackMethod;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    methodToInvoke.prepare();
                    methodToInvoke.invoke();
                } catch (Exception e) {
                    throw new RuntimeException("Error invoking callback method for quickfinder: " + quickfinderId, e);
                }
            }
        };

        ViewLifecycle.encapsulateLifecycle(form.getView(), form, form.getViewPostMetadata(), null, request, runnable);
    }

    /**
     * Instance of model and view service to use within the collection service.
     *
     * @return ModelAndViewService instance
     */
    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    /**
     * @see RefreshControllerServiceImpl#getModelAndViewService()
     */
    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
