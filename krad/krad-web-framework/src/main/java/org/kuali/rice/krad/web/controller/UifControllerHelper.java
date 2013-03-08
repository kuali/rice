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
import org.apache.log4j.Logger;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.view.History;
import org.kuali.rice.krad.uif.view.HistoryEntry;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.form.UifFormManager;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides helper methods that will be used during the request lifecycle
 *
 * <p>
 * Created to avoid duplication of the methods used by the UifHandlerExceptionResolver
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifControllerHelper {
    private static final Logger LOG = Logger.getLogger(UifControllerHelper.class);

    /**
     * Configures the <code>ModelAndView</code> instance containing the form
     * data and pointing to the UIF generic spring view
     *
     * @param form - Form instance containing the model data
     * @param pageId - Id of the page within the view that should be rendered, can
     * be left blank in which the current or default page is rendered
     * @return ModelAndView object with the contained form
     */
    public static ModelAndView getUIFModelAndView(UifFormBase form, String pageId) {
        if (StringUtils.isNotBlank(pageId)) {
            form.setPageId(pageId);
        }

        // create the spring return object pointing to View.jsp
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(UifConstants.DEFAULT_MODEL_NAME, form);
        modelAndView.setViewName(UifConstants.SPRING_VIEW_ID);

        return modelAndView;
    }

    /**
     * After the controller logic is executed, the form is placed into session
     * and the corresponding view is prepared for rendering
     */
    public static void postControllerHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (!(handler instanceof UifControllerBase) || (modelAndView == null)) {
            return;
        }
        UifControllerBase controller = (UifControllerBase) handler;

        Object model = modelAndView.getModelMap().get(UifConstants.DEFAULT_MODEL_NAME);
        if (!(model instanceof UifFormBase)) {
            return;
        }
        UifFormBase form = (UifFormBase) model;

        // handle view building if not a redirect
        if (!form.isRequestRedirected()) {
            // prepare view instance
            prepareViewForRendering(request, form);

            // for component, page refresh and dialog update need to export the component as a model
            Component component = null;
            if (form.isUpdateComponentRequest() || form.isUpdateDialogRequest()) {
                component = form.getPostedView().getViewIndex().getComponentById(form.getUpdateComponentId());
            } else if (form.isUpdatePageRequest()) {
                component = form.getView().getCurrentPage();
            }

            if (component != null) {
                modelAndView.addObject(UifConstants.COMPONENT_MODEL_NAME, component);
            }

            // update history for view
            prepareHistory(request, form);
        }

        // expose additional objects to the templates
        modelAndView.addObject(UifParameters.REQUEST, request);
        modelAndView.addObject(KRADConstants.USER_SESSION_KEY, request.getSession().getAttribute(
                KRADConstants.USER_SESSION_KEY));

        Map<String, String> properties = KRADServiceLocator.getKualiConfigurationService().getAllProperties();
        modelAndView.addObject(UifParameters.CONFIG_PROPERTIES, properties);
    }

    /**
     * Updates the history object (or constructs a new History) for the view we are getting ready
     * to render
     *
     * @param request - Http request object containing the request parameters
     * @param form - object containing the view data
     */
    public static void prepareHistory(HttpServletRequest request, UifFormBase form) {
        View view = form.getView();

        // main history/breadcrumb tracking support
        History history = form.getFormHistory();
        if (history == null || request.getMethod().equals("GET")) {
            history = new History();

            processReturnLocationOverride(form);

/*            history.setHomewardPath(view.getBreadcrumbs().getHomewardPathList());
            history.setAppendHomewardPath(view.getBreadcrumbs().isDisplayHomewardPath());*/

            // passed settings ALWAYS override the defaults
            if (StringUtils.isNotBlank(request.getParameter(UifConstants.UrlParams.SHOW_HOME))) {
                history.setAppendHomewardPath(Boolean.parseBoolean(request.getParameter(
                        UifConstants.UrlParams.SHOW_HOME)));
            }

            // do not append the history for dialog boxes
            if (form.getDialogManager().getDialogs().size() > 0) {
                history.setAppendPassedHistory(false);
            } else if (StringUtils.isNotBlank(request.getParameter(UifConstants.UrlParams.SHOW_HISTORY))) {
                history.setAppendPassedHistory(Boolean.parseBoolean(request.getParameter(
                        UifConstants.UrlParams.SHOW_HISTORY)));
            } else {
                /*history.setAppendPassedHistory(view.getBreadcrumbs().isDisplayPassedHistory());*/
            }

            history.buildCurrentEntryFromRequest(form, request);
            history.buildHistoryFromParameterString(request.getParameter(UifConstants.UrlParams.HISTORY));

            form.setFormHistory(history);
        }
    }

    /**
     * Checks for the return location parameter and overrides the homeward path if necesssary
     *
     * @param form form instance that contains the view and return location
     */
    protected static void processReturnLocationOverride(UifFormBase form) {
        View view = form.getView();

        if (form.getReturnLocation() == null) {
            return;
        }

/*        List<HistoryEntry> homewardPathList = new ArrayList<HistoryEntry>();
        if ((view != null) && (view.getBreadcrumbs() != null) &&
                (view.getBreadcrumbs().getHomewardPathList() != null)) {
            homewardPathList = view.getBreadcrumbs().getHomewardPathList();
        }

        HistoryEntry historyEntry = new HistoryEntry("", "", "Home", form.getReturnLocation(), "");
        if (homewardPathList.isEmpty()) {
            homewardPathList.add(historyEntry);
        } else if (StringUtils.equals(homewardPathList.get(0).getTitle(), "Home")) {
            homewardPathList.set(0, historyEntry);
        } else {
            homewardPathList.add(0, historyEntry);
        }*/
    }

    /**
     * Prepares the <code>View</code> instance contained on the form for rendering
     *
     * @param request - request object
     * @param form - form instance containing the data and view instance
     */
    public static void prepareViewForRendering(HttpServletRequest request, UifFormBase form) {
        // for component refreshes only lifecycle for component is performed
        if (form.isUpdateComponentRequest() || form.isUpdateDialogRequest()) {
            String refreshComponentId = form.getUpdateComponentId();

            View postedView = form.getPostedView();

            // check if the component is nested in a box layout in order to reapply the layout item style
            boolean boxLayoutHorizontalItem = false;
            boolean boxLayoutVerticalItem = false;

            if (form.isUpdateComponentRequest()) {
                Component postedComponent = ComponentUtils.findNestedComponentById(postedView, refreshComponentId);
                if (postedComponent != null && postedComponent.getCssClasses() != null &&
                        postedComponent.getCssClasses().contains("uif-boxLayoutHorizontalItem")) {
                    boxLayoutHorizontalItem = true;
                } else if (postedComponent != null && postedComponent.getCssClasses() != null &&
                        postedComponent.getCssClasses().contains("uif-boxLayoutVerticalItem")) {
                    boxLayoutVerticalItem = true;
                }
            }

            // get a new instance of the component
            Component comp = ComponentFactory.getNewInstanceForRefresh(form.getPostedView(), refreshComponentId);

            // run lifecycle and update in view
            postedView.getViewHelperService().performComponentLifecycle(postedView, form, comp, refreshComponentId);

            // add the layout item style that should happen in the parent BoxLayoutManager
            // and is skipped in a child component refresh
            if (boxLayoutHorizontalItem) {
                comp.addStyleClass("uif-boxLayoutHorizontalItem");
            } else if (boxLayoutVerticalItem) {
                comp.addStyleClass("uif-boxLayoutVerticalItem");
            }

            // regenerate server message content for page
            postedView.getCurrentPage().getValidationMessages().generateMessages(false, postedView, form,
                    postedView.getCurrentPage());
        } else {
            // full view build
            View view = form.getView();

            // set view page to page requested on form
            if (StringUtils.isNotBlank(form.getPageId())) {
                view.setCurrentPageId(form.getPageId());
            }

            Map<String, String> parameterMap = KRADUtils.translateRequestParameterMap(request.getParameterMap());
            parameterMap.putAll(form.getViewRequestParameters());

            // build view which will prepare for rendering
            getViewService().buildView(view, form, parameterMap);
        }
    }

    /**
     * Remove unused forms from breadcrumb history
     *
     * <p>
     * When going back in the breadcrumb history some forms become unused in the breadcrumb history.  Here the unused
     * forms are being determine and removed from the server to free memory.
     * </p>
     *
     * @param uifFormManager
     * @param formKey of the current form
     * @param lastFormKey of the last form
     */
    public static void removeUnusedBreadcrumbs(UifFormManager uifFormManager, String formKey, String lastFormKey) {
        if (StringUtils.isBlank(formKey) || StringUtils.isBlank(lastFormKey) || StringUtils.equals(formKey,
                lastFormKey)) {
            return;
        }

        UifFormBase previousForm = uifFormManager.getSessionForm(lastFormKey);
        if (previousForm == null) {
            return;
        }

        boolean cleanUpRemainingForms = false;
        for (HistoryEntry historyEntry : previousForm.getFormHistory().getHistoryEntries()) {
            if (cleanUpRemainingForms) {
                uifFormManager.removeSessionFormByKey(historyEntry.getFormKey());
            } else {
                if (StringUtils.equals(formKey, historyEntry.getFormKey())) {
                    cleanUpRemainingForms = true;
                }
            }
        }

        uifFormManager.removeSessionFormByKey(lastFormKey);
    }

    protected static ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }
}
