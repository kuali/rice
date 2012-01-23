/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.view.History;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.DocumentFormBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class UifWebUtils {
    private static final Logger LOG = Logger.getLogger(UifWebUtils.class);

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

    public static ModelAndView getComponentModelAndView(Component component, Object model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(UifConstants.DEFAULT_MODEL_NAME, model);
        modelAndView.addObject("Component", component);
        modelAndView.setViewName("ComponentUpdate");

        return modelAndView;
    }

    /**
     * After the controller logic is executed, the form is placed into session
     * and the corresponding view is prepared for rendering
     */
    public static void postControllerHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        if (handler instanceof UifControllerBase && (modelAndView != null)) {
            UifControllerBase controller = (UifControllerBase) handler;
            UifFormBase form = null;

            // check to see if this is a full view request
            if (modelAndView.getViewName().equals(UifConstants.SPRING_VIEW_ID)) {
                Object model = modelAndView.getModelMap().get(UifConstants.DEFAULT_MODEL_NAME);
                if (model instanceof UifFormBase) {
                    form = (UifFormBase) model;

                    form.setPreviousView(null);

                    // persist document form to db
                    if (form instanceof DocumentFormBase) {
                        UserSession userSession = (UserSession) request.getSession().getAttribute(
                                KRADConstants.USER_SESSION_KEY);
                        getSessionDocumentService().setDocumentForm((DocumentFormBase) form, userSession,
                                request.getRemoteAddr());
                    }

                    // prepare view instance
                    prepareViewForRendering(request, form);

                    // update history for view
                    prepareHistory(request, form);
                }
            }
        }
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
            history.setHomewardPath(view.getBreadcrumbs().getHomewardPathList());
            history.setAppendHomewardPath(view.getBreadcrumbs().isDisplayHomewardPath());
            history.setAppendPassedHistory(view.getBreadcrumbs().isDisplayPassedHistory());

            // passed settings ALWAYS override the defaults
            if (StringUtils.isNotBlank(request.getParameter(UifConstants.UrlParams.SHOW_HOME))) {
                history.setAppendHomewardPath(Boolean.parseBoolean(request.getParameter(
                        UifConstants.UrlParams.SHOW_HOME)));
            }

            if (StringUtils.isNotBlank(request.getParameter(UifConstants.UrlParams.SHOW_HISTORY))) {
                history.setAppendPassedHistory(Boolean.parseBoolean(request.getParameter(
                        UifConstants.UrlParams.SHOW_HISTORY)));
            }

            history.setCurrent(form, request);
            history.buildHistoryFromParameterString(request.getParameter(UifConstants.UrlParams.HISTORY));
            form.setFormHistory(history);
        }
    }

    /**
     * Prepares the <code>View</code> instance contained on the form for
     * rendering
     *
     * @param request - request object
     * @param form - form instance containing the data and view instance
     */
    public static void prepareViewForRendering(HttpServletRequest request, UifFormBase form) {
        View view = form.getView();

        // set view page to page requested on form
        if (StringUtils.isNotBlank(form.getPageId())) {
            view.setCurrentPageId(form.getPageId());
        }

        Map<String, String> parameterMap = KRADUtils.translateRequestParameterMap(request.getParameterMap());
        parameterMap.putAll(form.getViewRequestParameters());

        // build view which will prepare for rendering
        getViewService().buildView(view, form, parameterMap);

        // set dirty flag
        form.setValidateDirty(view.isValidateDirty());
    }

    protected static SessionDocumentService getSessionDocumentService() {
        return KRADServiceLocatorWeb.getSessionDocumentService();
    }

    protected static ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }
}
