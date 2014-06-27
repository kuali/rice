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
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.krad.service.ViewValidationService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.util.UifRenderHelperMethods;
import org.kuali.rice.krad.uif.view.MessageView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.ModelAndViewService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of the model and view service.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ModelAndViewServiceImpl implements ModelAndViewService {
    private static final Logger LOG = Logger.getLogger(ModelAndViewServiceImpl.class);

    private ViewService viewService;
    private ViewValidationService viewValidationService;

    /**
     * Invokes {@link org.kuali.rice.krad.service.ViewValidationService} to validate the contents of the
     * given form instance.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView checkForm(UifFormBase form) {
        getViewValidationService().validateViewSimulation(form);

        return getModelAndView(form);
    }

    /**
     * Builds the dialog group with the given id then creates the script for showing the dialog once the
     * page reloads.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView showDialog(String dialogId, boolean confirmation, UifFormBase form) {
        if (form.isAjaxRequest()) {
            form.setAjaxReturnType(UifConstants.AjaxReturnTypes.UPDATEDIALOG.getKey());
            form.setUpdateComponentId(dialogId);
        }

        // run the lifecycle to build the dialog first
        ModelAndView modelAndView = getModelAndView(form);
        prepareView(form.getRequest(), modelAndView);

        Component updateComponent;
        if (form.isAjaxRequest()) {
            updateComponent = form.getUpdateComponent();
        } else {
            updateComponent = form.getView();
        }

        // now add the script that will show the dialog to the on ready of the document
        String showDialogScript = buildShowDialogScript(dialogId, confirmation, form);

        String onReadyScript = ScriptUtils.appendScript(updateComponent.getOnDocumentReadyScript(), showDialogScript);
        updateComponent.setOnDocumentReadyScript(onReadyScript);

        form.getRequest().setAttribute(UifParameters.Attributes.VIEW_LIFECYCLE_COMPLETE, "true");

        return modelAndView;
    }

    /**
     * Builds a JavaScript string for invoking the showDialog method with the given dialog parameters.
     *
     * @param dialogId id for the dialog group to show
     * @param confirmation whether the dialog should be shown as a confirmation
     * @param form instance containing the model data
     * @return String containing script
     */
    protected String buildShowDialogScript(String dialogId, boolean confirmation, UifFormBase form) {
        StringBuilder showDialogScript = new StringBuilder();

        showDialogScript.append(UifConstants.JsFunctions.SHOW_DIALOG);
        showDialogScript.append("('");
        showDialogScript.append(dialogId);
        showDialogScript.append("', {responseHandler: ");
        showDialogScript.append(UifConstants.JsFunctions.HANDLE_SERVER_DIALOG_RESPONSE);
        showDialogScript.append(",responseEventData:{triggerActionId:'");
        showDialogScript.append(form.getTriggerActionId());
        showDialogScript.append("',confirmation:");
        showDialogScript.append(confirmation);
        showDialogScript.append("}});");

        return showDialogScript.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView performRedirect(UifFormBase form, String baseUrl, Properties urlParameters) {
        String redirectUrl = UrlFactory.parameterizeUrl(baseUrl, urlParameters);

        return performRedirect(form, redirectUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView performRedirect(UifFormBase form, String redirectUrl) {
        // indicate a redirect is occuring to prevent view processing down the line
        form.setRequestRedirected(true);

        // set the ajaxReturnType on the form this will override the return type requested by the client
        form.setAjaxReturnType(UifConstants.AjaxReturnTypes.REDIRECT.getKey());

        ModelAndView modelAndView;
        if (form.isAjaxRequest()) {
            modelAndView = getModelAndView(form, form.getPageId());
            modelAndView.addObject("redirectUrl", redirectUrl);
        } else {
            modelAndView = new ModelAndView(UifConstants.REDIRECT_PREFIX + redirectUrl);
        }

        return modelAndView;
    }

    /**
     * Retrieves an instance of the view with id {@link org.kuali.rice.krad.uif.UifConstants#MESSAGE_VIEW_ID}
     * and sets the header and message from the given parameters.
     *
     * {@inheritDoc}
     */
    @Override
    public ModelAndView getMessageView(UifFormBase form, String headerText, String messageText) {
        MessageView messageView = (MessageView) getViewService().getViewById(UifConstants.MESSAGE_VIEW_ID);

        messageView.setHeaderText(headerText);
        messageView.setMessageText(messageText);

        form.setViewId(UifConstants.MESSAGE_VIEW_ID);
        form.setView(messageView);

        return getModelAndView(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView getModelAndView(UifFormBase form) {
        return getModelAndView(form, form.getPageId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView getModelAndView(UifFormBase form, String pageId) {
        if (StringUtils.isNotBlank(pageId)) {
            form.setPageId(pageId);
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(UifConstants.DEFAULT_MODEL_NAME, form);
        modelAndView.setViewName(UifConstants.SPRING_VIEW_ID);

        return modelAndView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView getModelAndView(UifFormBase form, Map<String, Object> additionalViewAttributes) {
        ModelAndView modelAndView = getModelAndView(form, form.getPageId());

        if (additionalViewAttributes != null) {
            for (Map.Entry<String, Object> additionalViewAttribute : additionalViewAttributes.entrySet()) {
                modelAndView.getModelMap().put(additionalViewAttribute.getKey(), additionalViewAttribute.getValue());
            }
        }

        return modelAndView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView getModelAndViewWithInit(UifFormBase form, String viewId) {
        form.setPageId(null);

        return getModelAndViewWithInit(form, viewId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView getModelAndViewWithInit(UifFormBase form, String viewId, String pageId) {
        View view = getViewService().getViewById(viewId);

        if (view == null) {
            throw new RiceRuntimeException("No view was found with view id " + viewId);
        }

        form.setView(view);
        form.setViewId(viewId);

        return getModelAndView(form, pageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareView(HttpServletRequest request, ModelAndView modelAndView) {
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
    protected void invokeViewLifecycle(HttpServletRequest request, UifFormBase form) {
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

    protected ViewService getViewService() {
        return viewService;
    }

    public void setViewService(ViewService viewService) {
        this.viewService = viewService;
    }

    public ViewValidationService getViewValidationService() {
        return viewValidationService;
    }

    public void setViewValidationService(ViewValidationService viewValidationService) {
        this.viewValidationService = viewValidationService;
    }
}
