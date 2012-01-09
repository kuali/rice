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
package org.kuali.rice.krad.web.bind;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.service.ViewService;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.validation.AbstractPropertyBindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Override of ServletRequestDataBinder in order to hook in the UifBeanPropertyBindingResult
 * which instantiates a custom BeanWrapperImpl.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifServletRequestDataBinder extends ServletRequestDataBinder {
    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            UifServletRequestDataBinder.class);

    private UifBeanPropertyBindingResult bindingResult;
    private ConversionService conversionService;

    public UifServletRequestDataBinder(Object target) {
        super(target);
        setBindingErrorProcessor(new UifBindingErrorProcessor());
    }

    public UifServletRequestDataBinder(Object target, String name) {
        super(target, name);
        setBindingErrorProcessor(new UifBindingErrorProcessor());
    }

    /**
     * Allows for a custom binding result class.
     *
     * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
     */
    @Override
    public void initBeanPropertyAccess() {
        Assert.state(this.bindingResult == null,
                "DataBinder is already initialized - call initBeanPropertyAccess before other configuration methods");
        this.bindingResult = new UifBeanPropertyBindingResult(getTarget(), getObjectName(), isAutoGrowNestedPaths(),
                getAutoGrowCollectionLimit());
        if (this.conversionService != null) {
            this.bindingResult.initConversion(this.conversionService);
        }
    }

    /**
     * Allows for the setting attributes to use to find the data dictionary data from Kuali
     *
     * @see org.springframework.validation.DataBinder#getInternalBindingResult()
     */
    @Override
    protected AbstractPropertyBindingResult getInternalBindingResult() {
        if (this.bindingResult == null) {
            initBeanPropertyAccess();
        }
        return this.bindingResult;
    }

    /**
     * Disallows direct field access for Kuali
     *
     * @see org.springframework.validation.DataBinder#initDirectFieldAccess()
     */
    @Override
    public void initDirectFieldAccess() {
        LOG.error("Direct Field access is not allowed in UifServletRequestDataBinder.");
        throw new RuntimeException("Direct Field access is not allowed in Kuali");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bind(ServletRequest request) {
        super.bind(request);

        UifFormBase form = (UifFormBase) this.getTarget();

        // back up previous view instance
        View previousView = form.getView();
        form.setPreviousView(previousView);

        // check for request param that indicates to skip view initialize
        Boolean skipViewInit = KRADUtils.getRequestParameterAsBoolean(request, UifParameters.SKIP_VIEW_INIT);
        if ((skipViewInit == null) || !skipViewInit.booleanValue()) {
            // initialize new view for request
            View view = null;

            String viewId = request.getParameter(UifParameters.VIEW_ID);
            if (viewId != null) {
                view = getViewService().getViewById(viewId);
            } else {
                // attempt to get view instance by type parameters
                ViewType viewType = null;

                String viewTypeName = request.getParameter(UifParameters.VIEW_TYPE_NAME);
                viewType = StringUtils.isBlank(viewTypeName) ? form.getViewTypeName() : ViewType.valueOf(viewTypeName);

                if (viewType == null) {
                    view = getViewFromPreviousModel(form);
                    if (view == null) {
                        throw new RuntimeException("Could not find enough information to fetch the required view. "
                                + " Checked the model retrieved from session for both viewTypeName and viewId");
                    }
                } else {
                    Map<String, String> parameterMap = KRADUtils.translateRequestParameterMap(
                            request.getParameterMap());
                    try {
                        view = getViewService().getViewByType(viewType, parameterMap);
                    } catch (RuntimeException rtex) {
                        view = getViewFromPreviousModel(form);
                        // if we didn't find one, just re-throw
                        if (view == null) {
                            throw rtex;
                        }
                        LOG.warn("Obtained viewId from cached form, this may not be safe!");
                    }
                }
            }

            form.setViewId(view.getId());
            form.setView(view);
        }

        form.postBind((HttpServletRequest) request);

        // set form key as request attribute so form can be pulled from request
        request.setAttribute(UifParameters.FORM_KEY, form.getFormKey());

        // set form in session
        ((HttpServletRequest) request).getSession().setAttribute(form.getFormKey(), form);
    }

    protected View getViewFromPreviousModel(UifFormBase form) {
        // maybe we have a view id from the session form
        if (form.getViewId() != null) {
            return getViewService().getViewById(form.getViewId());
        }

        return null;
    }

    public ViewService getViewService() {
        return KRADServiceLocatorWeb.getViewService();
    }

}
