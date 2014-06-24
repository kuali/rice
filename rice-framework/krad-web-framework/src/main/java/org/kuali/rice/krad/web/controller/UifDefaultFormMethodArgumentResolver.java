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

import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.ServletRequest;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifDefaultFormMethodArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Default Constructor.
     */
    public UifDefaultFormMethodArgumentResolver() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isUifForm = false;

        if (!parameter.hasParameterAnnotation(ModelAttribute.class) && UifFormBase.class.isAssignableFrom(
                parameter.getParameterType())) {
            isUifForm = true;
        }

        return isUifForm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String name = UifConstants.DEFAULT_MODEL_NAME;

        Object attribute = null;
        if (mavContainer.containsAttribute(name)) {
            attribute = mavContainer.getModel().get(name);
        }

        WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
        if (binder.getTarget() != null) {
            ServletRequest servletRequest = webRequest.getNativeRequest(ServletRequest.class);
            ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
            servletBinder.bind(servletRequest);

            if (binder.getBindingResult().hasErrors()) {
                if (isBindExceptionRequired(parameter)) {
                    throw new BindException(binder.getBindingResult());
                }
            }
        }

        Map<String, Object> bindingResultModel = binder.getBindingResult().getModel();
        mavContainer.removeAttributes(bindingResultModel);
        mavContainer.addAllAttributes(bindingResultModel);

        return binder.getTarget();
    }

    /**
     * Whether to raise a {@link BindException} on validation errors.
     *
     * @param parameter the method argument
     * @return {@code true} if the next method argument is not of type {@link org.springframework.validation.Errors}.
     */
    protected boolean isBindExceptionRequired(MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getMethod().getParameterTypes();
        boolean hasBindingResult = (paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));

        return !hasBindingResult;
    }

}
