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

    /**
     * Performs data binding from servlet request parameters to the form, initializes view object, then calls
     * {@link org.kuali.rice.krad.web.form.UifFormBase#postBind(javax.servlet.http.HttpServletRequest)}
     *
     * <p>
     * The view is initialized by first looking for the {@code viewId} parameter in the request. If found, the view is
     * retrieved based on this id. If the id is not present, then an attempt is made to find a view by type. In order
     * to retrieve a view based on type, the view request parameter {@code viewTypeName} must be present. If all else
     * fails and the viewId is populated on the form (could be populated from a previous request), this is used to
     * retrieve the view.
     * </p>
     *
     * <p>
     * Note the view is not initialized for Ajax requests that perform partial page updates or no
     * updates at all
     * </p>
     *
     * @param request - HTTP Servlet Request instance
     */
    @Override
    public void bind(ServletRequest request) {
        super.bind(request);

        UifFormBase form = (UifFormBase) this.getTarget();

        // if doing a partial page update or ajax request with no updating, do not initialize view
        if (!form.isUpdateComponentRequest() && !form.isUpdateNoneRequest() && !form.isUpdateDialogRequest()) {
            View view = null;

            // attempt to retrieve a view by unique identifier first
            String viewId = request.getParameter(UifParameters.VIEW_ID);
            if (viewId != null) {
                view = getViewService().getViewById(viewId);
            } else {
                // attempt to get view instance by type parameters
                String viewTypeName = request.getParameter(UifParameters.VIEW_TYPE_NAME);
                ViewType viewType = StringUtils.isBlank(viewTypeName) ? form.getViewTypeName() : ViewType.valueOf(
                        viewTypeName);

                if (viewType != null) {
                    Map<String, String> parameterMap = KRADUtils.translateRequestParameterMap(
                            request.getParameterMap());
                    view = getViewService().getViewByType(viewType, parameterMap);
                }

                // if view not found attempt to find one based on the cached form
                if (view == null) {
                    view = getViewFromPreviousModel(form);

                    if (view != null) {
                        LOG.warn("Obtained viewId from cached form, this may not be safe!");
                    }
                }
            }

            if (view != null) {
                form.setViewId(view.getId());
                form.setView(view);
            } else {
                form.setViewId(null);
                form.setView(null);
            }
        }

        form.postBind((HttpServletRequest) request);
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


