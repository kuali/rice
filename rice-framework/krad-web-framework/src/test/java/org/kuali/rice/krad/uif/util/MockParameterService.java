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
package org.kuali.rice.krad.uif.util;

import java.util.Collection;
import java.util.Collections;

import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * TODO mark don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MockParameterService implements ParameterService {

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#createParameter(org.kuali.rice.coreservice.api.parameter.Parameter)
     */
    @Override
    public Parameter createParameter(Parameter parameter) {
        return parameter;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#updateParameter(org.kuali.rice.coreservice.api.parameter.Parameter)
     */
    @Override
    public Parameter updateParameter(Parameter parameter) {
        return parameter;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#parameterExists(java.lang.Class, java.lang.String)
     */
    @Override
    public Boolean parameterExists(Class<?> componentClass, String parameterName) {
        return false;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#parameterExists(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Boolean parameterExists(String namespaceCode, String componentCode, String parameterName) {
        return false;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsString(java.lang.Class, java.lang.String)
     */
    @Override
    public String getParameterValueAsString(Class<?> componentClass, String parameterName) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsString(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    public String getParameterValueAsString(Class<?> componentClass, String parameterName, String defaultValue) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsString(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getParameterValueAsString(String namespaceCode, String componentCode, String parameterName) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsString(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getParameterValueAsString(String namespaceCode, String componentCode, String parameterName,
            String defaultValue) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsBoolean(java.lang.Class, java.lang.String)
     */
    @Override
    public Boolean getParameterValueAsBoolean(Class<?> componentClass, String parameterName) {
        return false;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsBoolean(java.lang.Class, java.lang.String, java.lang.Boolean)
     */
    @Override
    public Boolean getParameterValueAsBoolean(Class<?> componentClass, String parameterName, Boolean defaultValue) {
        return false;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsBoolean(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Boolean getParameterValueAsBoolean(String namespaceCode, String componentCode, String parameterName) {
        return false;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValueAsBoolean(java.lang.String, java.lang.String, java.lang.String, java.lang.Boolean)
     */
    @Override
    public Boolean getParameterValueAsBoolean(String namespaceCode, String componentCode, String parameterName,
            Boolean defaultValue) {
        return false;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameter(java.lang.Class, java.lang.String)
     */
    @Override
    public Parameter getParameter(Class<?> componentClass, String parameterName) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameter(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Parameter getParameter(String namespaceCode, String componentCode, String parameterName) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValuesAsString(java.lang.Class, java.lang.String)
     */
    @Override
    public Collection<String> getParameterValuesAsString(Class<?> componentClass, String parameterName) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getParameterValuesAsString(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Collection<String> getParameterValuesAsString(String namespaceCode, String componentCode,
            String parameterName) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getSubParameterValueAsString(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    public String getSubParameterValueAsString(Class<?> componentClass, String parameterName, String subParameterName) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getSubParameterValueAsString(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getSubParameterValueAsString(String namespaceCode, String componentCode, String parameterName,
            String subParameterName) {
        return null;
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getSubParameterValuesAsString(java.lang.Class, java.lang.String, java.lang.String)
     */
    @Override
    public Collection<String> getSubParameterValuesAsString(Class<?> componentClass, String parameterName,
            String subParameterName) {
        return Collections.emptyList();
    }

    /**
     * @see org.kuali.rice.coreservice.framework.parameter.ParameterService#getSubParameterValuesAsString(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Collection<String> getSubParameterValuesAsString(String namespaceCode, String componentCode,
            String parameterName, String subParameterName) {
        return Collections.emptyList();
    }

}
