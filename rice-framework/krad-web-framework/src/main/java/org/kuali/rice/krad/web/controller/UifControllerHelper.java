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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.util.UifRenderHelperMethods;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.servlet.ModelAndView;

/**
 * Provides helper methods that will be used during the request lifecycle.
 *
 * <p>Created to avoid duplication of the methods used by the UifHandlerExceptionResolver</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifControllerHelper {
    private static final Logger LOG = Logger.getLogger(UifControllerHelper.class);

    private UifControllerHelper() {}

    /**
     * Attempts to resolve a view id from the given request
     *
     * <p>
     * First an attempt will be made to find the view id as a request parameter. If no such request parameter
     * is found, the request will be looked at for view type information and a call will be made to the
     * view service to find the view id by type
     * </p>
     *
     * <p>
     * If a view id is found it is stuck in the request as an attribute (under the key
     * {@link org.kuali.rice.krad.uif.UifParameters#VIEW_ID}) for subsequent retrieval
     * </p>
     *
     * @param request instance to resolve view id for
     * @return view id if one is found, null if not found
     */
    public static String getViewIdFromRequest(HttpServletRequest request) {
        String viewId = request.getParameter(UifParameters.VIEW_ID);

        if (StringUtils.isBlank(viewId)) {
            String viewTypeName = request.getParameter(UifParameters.VIEW_TYPE_NAME);

            UifConstants.ViewType viewType = null;
            if (StringUtils.isNotBlank(viewTypeName)) {
                viewType = UifConstants.ViewType.valueOf(viewTypeName);
            }

            if (viewType != null) {
                @SuppressWarnings("unchecked") Map<String, String> parameterMap =
                        KRADUtils.translateRequestParameterMap(request.getParameterMap());
                viewId = getViewService().getViewIdForViewType(viewType, parameterMap);
            }
        }

        if (StringUtils.isNotBlank(viewId)) {
            request.setAttribute(UifParameters.VIEW_ID, viewId);
        }

        return viewId;
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
     * After the controller logic is executed, the form is placed into session and the corresponding view
     * is prepared for rendering.
     *
     * @param request servlet request
     * @param modelAndView model and view
     */
    public static void prepareView(HttpServletRequest request, ModelAndView modelAndView) {
        if (modelAndView == null) {
            return;
        }

        Object model = modelAndView.getModelMap().get(UifConstants.DEFAULT_MODEL_NAME);
        if (!(model instanceof UifFormBase)) {
            return;
        }

        UifFormBase form = (UifFormBase) model;

        if (!form.isRequestRedirected()) {
            invokeViewLifecycle(request, form);
        }

        // expose additional objects to the templates
        modelAndView.addObject(UifParameters.REQUEST, request);
        modelAndView.addObject(KRADConstants.USER_SESSION_KEY, request.getSession().getAttribute(
                KRADConstants.USER_SESSION_KEY));

        Map<String, String> properties = CoreApiServiceLocator.getKualiConfigurationService().getAllProperties();
        modelAndView.addObject(UifParameters.CONFIG_PROPERTIES, properties);

        modelAndView.addObject(UifParameters.RENDER_HELPER_METHODS, new UifRenderHelperMethods());
    }

    /**
     * Prepares the {@link org.kuali.rice.krad.uif.view.View} instance contained on the form for rendering.
     *
     * @param request servlet request
     * @param form form instance containing the data and view instance
     */
    public static void invokeViewLifecycle(HttpServletRequest request, UifFormBase form) {
        // for component refreshes only lifecycle for component is performed
        if (form.isUpdateComponentRequest() || form.isUpdateDialogRequest() || (form.isJsonRequest() && StringUtils
                .isNotBlank(form.getUpdateComponentId()))) {
            String refreshComponentId = form.getUpdateComponentId();

            Component updateComponent = ViewLifecycle.performComponentLifecycle(form.getView(), form, request,
                    form.getViewPostMetadata(), refreshComponentId);
            form.setUpdateComponent(updateComponent);
        } else {
            // full view build
            View view = form.getView();
            if (view == null) {
                LOG.warn("View in form was null: " + form);

                return;
            }

            Map<String, String> parameterMap = KRADUtils.translateRequestParameterMap(request.getParameterMap());
            parameterMap.putAll(form.getViewRequestParameters());

            // build view which will prepare for rendering
            ViewPostMetadata postMetadata = ViewLifecycle.buildView(view, form, request, parameterMap);
            form.setViewPostMetadata(postMetadata);

            if (form.isUpdatePageRequest()) {
                Component updateComponent = form.getView().getCurrentPage();
                form.setUpdateComponent(updateComponent);
            }

            // update the page on the form to reflect the current page of the view
            form.setPageId(view.getCurrentPageId());
        }
    }

    protected static ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

}
