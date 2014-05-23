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
     * {@inheritDoc}
     */
    @Override
    public Parameter createParameter(Parameter parameter) {
        return parameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parameter updateParameter(Parameter parameter) {
        return parameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parameter getParameter(String namespaceCode, String componentCode, String parameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parameter getParameter(Class<?> componentClass, String parameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean parameterExists(String namespaceCode, String componentCode, String parameterName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean parameterExists(Class<?> componentClass, String parameterName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getParameterValueAsBoolean(String namespaceCode, String componentCode, String parameterName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getParameterValueAsBoolean(String namespaceCode, String componentCode, String parameterName, Boolean defaultValue) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getParameterValueAsBoolean(Class<?> componentClass, String parameterName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getParameterValueAsBoolean(Class<?> componentClass, String parameterName, Boolean defaultValue) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsString(String namespaceCode, String componentCode, String parameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsString(String namespaceCode, String componentCode, String parameterName, String defaultValue) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsString(Class<?> componentClass, String parameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsString(Class<?> componentClass, String parameterName, String defaultValue) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsFilteredString(String namespaceCode, String componentCode, String parameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsFilteredString(String namespaceCode, String componentCode, String parameterName, String defaultValue) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsFilteredString(Class<?> componentClass, String parameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterValueAsFilteredString(Class<?> componentClass, String parameterName, String defaultValue) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getParameterValuesAsString(String namespaceCode, String componentCode, String parameterName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getParameterValuesAsString(Class<?> componentClass, String parameterName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getParameterValuesAsFilteredString(String namespaceCode, String componentCode, String parameterName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getParameterValuesAsFilteredString(Class<?> componentClass, String parameterName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubParameterValueAsString(String namespaceCode, String componentCode, String parameterName, String subParameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubParameterValueAsString(Class<?> componentClass, String parameterName, String subParameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubParameterValueAsFilteredString(String namespaceCode, String componentCode, String parameterName, String subParameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubParameterValueAsFilteredString(Class<?> componentClass, String parameterName, String subParameterName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getSubParameterValuesAsString(Class<?> componentClass, String parameterName, String subParameterName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getSubParameterValuesAsString(String namespaceCode, String componentCode, String parameterName, String subParameterName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getSubParameterValuesAsFilteredString(String namespaceCode, String componentCode, String parameterName, String subParameterName) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getSubParameterValuesAsFilteredString(Class<?> componentClass, String parameterName, String subParameterName) {
        return Collections.emptyList();
    }

}