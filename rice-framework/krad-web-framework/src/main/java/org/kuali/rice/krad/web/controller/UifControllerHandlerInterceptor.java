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
package org.kuali.rice.krad.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.util.ProcessLogger;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.HistoryManager;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.form.UifFormManager;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring controller intercepter for KRAD controllers.
 *
 * <p>Provides infrastructure for preparing the form and view before and after the controller is invoked.
 * Included in this is form session management and preparation of the view for rendering</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifControllerHandlerInterceptor implements HandlerInterceptor {
    private static final Logger LOG = Logger.getLogger(UifControllerHandlerInterceptor.class);

    @Autowired
    private ModelAndViewService modelAndViewService;

    /**
     * Before the controller executes the user session is set on GlobalVariables
     * and messages are cleared, in addition setup for the history manager and a check on missing session
     * forms is performed.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        checkHandlerMethodAccess(request, handler);

        final UserSession session = KRADUtils.getUserSessionFromRequest(request);

        GlobalVariables.setUserSession(session);
        GlobalVariables.clear();

        createUifFormManagerIfNecessary(request);

        // add the HistoryManager for storing HistoryFlows to the session
        if (request.getSession().getAttribute(UifConstants.HistoryFlow.HISTORY_MANAGER) == null) {
            request.getSession().setAttribute(UifConstants.HistoryFlow.HISTORY_MANAGER, new HistoryManager());
        }

        ProcessLogger.trace("pre-handle");

        return true;
    }

    /**
     * Checks whether access is allowed for the requested controller method.
     *
     * <p>First a check is done on the method to determine whether it contains the annotation
     * {@link org.kuali.rice.krad.web.controller.MethodAccessible}. If so, access is allowed. If the
     * annotation is not present, data from the posted view (if any) is referenced to determine
     * whether the method was configured as an accessible method to call.</p>
     *
     * <p>If method access is not allowed, a {@link org.kuali.rice.krad.web.controller.MethodAccessException}
     * is thrown.</p>
     *
     * @param request HTTP request (used to retrieve parameters)
     * @param handler handler method that was determined based on the request and mappings
     * @throws Exception
     */
    protected void checkHandlerMethodAccess(HttpServletRequest request, Object handler) throws Exception {
        String requestMethod = request.getMethod();

        // if it is a GET request then we allow without any check
        if(requestMethod.equalsIgnoreCase(RequestMethod.GET.name())) {
            return;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        MethodAccessible methodAccessible = handlerMethod.getMethodAnnotation(MethodAccessible.class);

        // if accessible by annotation then return, otherwise go on to check view configuration
        if (methodAccessible != null) {
            return;
        }

        boolean isMethodAccessible = checkForMethodAccess(request);

        if (!isMethodAccessible) {
            throw new MethodAccessException(handlerMethod.getBeanType(), handlerMethod.getMethod().getName());
        }
    }

    /**
     * Checks whether access to the handler method is allowed based available methods or accessible methods
     * on view configuration.
     *
     * <p>Since this method is invoked before the request form is setup, we need to retrieve the session form
     * form the form manager. In the case of missing post data (GET requests), view method access is not
     * granted.</p>
     *
     * @param request HTTP request to retrieve parameters from
     * @return boolean true if method access is allowed based on the view, false if not
     */
    protected boolean checkForMethodAccess(HttpServletRequest request) {
        String methodToCall = request.getParameter(UifParameters.METHOD_TO_CALL);

        // if method to call is blank, we will assume they are using other strategies to map controller
        // methods, and therefore using custom access management
        if (StringUtils.isBlank(methodToCall)) {
            return true;
        }

        UifFormManager uifFormManager = (UifFormManager) request.getSession().getAttribute(UifParameters.FORM_MANAGER);
        UifFormBase form = null;

        String formKeyParam = request.getParameter(UifParameters.FORM_KEY);
        if (StringUtils.isNotBlank(formKeyParam) && (uifFormManager != null)) {
            form = uifFormManager.getSessionForm(formKeyParam);
        }

        // if we don't have the view post data, access cannot be granted based on the view
        if ((form == null) || (form.getViewPostMetadata() == null) || (form.getViewPostMetadata()
                .getAccessibleMethodToCalls() == null)) {
            return false;
        }

        // if the method to call is listed as a method in either the available methods to call or the
        // view's accessible methods to call, then return true
        return !form.getViewPostMetadata().getAvailableMethodToCalls().contains(methodToCall) ||
                form.getViewPostMetadata().getAccessibleMethodToCalls().contains(methodToCall);
    }

    /**
     * Checks if a form manager is present in the session, and if not creates a form manager and adds to the
     * session and global variables.
     *
     * @param request http request being handled
     */
    protected void createUifFormManagerIfNecessary(HttpServletRequest request) {
        UifFormManager uifFormManager = (UifFormManager) request.getSession().getAttribute(UifParameters.FORM_MANAGER);
        if (uifFormManager == null) {
            uifFormManager = new UifFormManager();
            request.getSession().setAttribute(UifParameters.FORM_MANAGER, uifFormManager);
        }

        // add form manager to GlobalVariables for easy reference by other controller methods
        GlobalVariables.setUifFormManager(uifFormManager);
    }

    /**
     * After the controller logic is executed, the form is placed into session and the corresponding view
     * is prepared for rendering.
     *
     * {@inheritDoc}
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (request.getAttribute(UifParameters.Attributes.VIEW_LIFECYCLE_COMPLETE) == null) {
            getModelAndViewService().prepareView(request, modelAndView);
        }

        if ((modelAndView != null) && (modelAndView.getModelMap() != null)) {
            Object model = modelAndView.getModelMap().get(UifConstants.DEFAULT_MODEL_NAME);
            if ((model != null) && (model instanceof ViewModel)) {
                ((ViewModel) model).preRender(request);
            }
        }

        ProcessLogger.trace("post-handle");
    }

    /**
     * After the view is rendered remove the view to reduce the size of the form storage in memory.
     *
     * {@inheritDoc}
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
        ProcessLogger.trace("after-completion");

        UifFormManager uifFormManager = (UifFormManager) request.getSession().getAttribute(UifParameters.FORM_MANAGER);
        UifFormBase uifForm = (UifFormBase) request.getAttribute(UifConstants.REQUEST_FORM);

        if ((uifForm == null) || (uifForm.getView() == null)) {
            return;
        }

        // remove the session transient variables from the request form before adding it to the list of
        // Uif session forms
        boolean persistFormToSession = uifForm.getView().isPersistFormToSession();
        if (persistFormToSession && (uifFormManager != null)) {
            uifFormManager.purgeForm(uifForm);
            uifFormManager.addSessionForm(uifForm);
        }

        uifForm.setView(null);

        ProcessLogger.trace("after-completion-end");
    }

    protected ModelAndViewService getModelAndViewService() {
        return modelAndViewService;
    }

    public void setModelAndViewService(ModelAndViewService modelAndViewService) {
        this.modelAndViewService = modelAndViewService;
    }
}
