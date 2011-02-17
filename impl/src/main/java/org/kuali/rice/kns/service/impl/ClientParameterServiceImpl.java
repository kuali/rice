/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.parameter.Parameter;
import org.kuali.rice.core.api.parameter.ParameterKey;
import org.kuali.rice.core.api.parameter.ParameterService;
import org.kuali.rice.kns.service.ClientParameterService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KNSConstants;

import java.util.Collection;

public class ClientParameterServiceImpl implements ClientParameterService {
    private KualiModuleService kualiModuleService;
    private ParameterService parameterService;
    private String applicationCode = KNSConstants.DEFAULT_APPLICATION_CODE;

    @Override
    public void createParameter(Parameter parameter) {
        parameterService.createParameter(parameter);
    }

    @Override
    public void updateParameter(Parameter parameter) {
        parameterService.updateParameter(parameter);
    }

    @Override
    public Parameter getParameter(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Parameter>() {
            public Parameter f(ParameterKey key) {
                return parameterService.getParameter(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Parameter getParameter(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Parameter>() {
            public Parameter f(ParameterKey key) {
                return parameterService.getParameter(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public Boolean parameterExists(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Boolean>() {
            public Boolean f(ParameterKey key) {
                return parameterService.getParameter(key) != null;
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Boolean parameterExists(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Boolean>() {
            public Boolean f(ParameterKey key) {
                return parameterService.getParameter(key) != null;
            }
        }, componentClass, parameterName);
    }

    @Override
    public Boolean getParameterValueAsBoolean(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Boolean>() {
            public Boolean f(ParameterKey key) {
                return parameterService.getParameterValueAsBoolean(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Boolean getParameterValueAsBoolean(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Boolean>() {
            public Boolean f(ParameterKey key) {
                return parameterService.getParameterValueAsBoolean(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public String getParameterValueAsString(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<String>() {
            public String f(ParameterKey key) {
                return parameterService.getParameterValueAsString(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public String getParameterValueAsString(Class<?> componentClass, String parameterName) {
        return exec(new Fun<String>() {
            public String f(ParameterKey key) {
                return parameterService.getParameterValueAsString(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public Collection<String> getParameterValuesAsString(String namespaceCode, String componentCode, String parameterName) {
        return exec(new Fun<Collection<String>>() {
            public Collection<String> f(ParameterKey key) {
                return parameterService.getParameterValuesAsString(key);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Collection<String> getParameterValuesAsString(Class<?> componentClass, String parameterName) {
        return exec(new Fun<Collection<String>>() {
            public Collection<String> f(ParameterKey key) {
                return parameterService.getParameterValuesAsString(key);
            }
        }, componentClass, parameterName);
    }

    @Override
    public Collection<String> getSubParameterValuesAsString(String namespaceCode, String componentCode, String parameterName, final String constrainingValue) {
        return exec(new Fun<Collection<String>>() {
            public Collection<String> f(ParameterKey key) {
                return parameterService.getSubParameterValuesAsString(key, constrainingValue);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public Collection<String> getSubParameterValuesAsString(Class<?> componentClass, String parameterName, final String constrainingValue) {
        return exec(new Fun<Collection<String>>() {
            public Collection<String> f(ParameterKey key) {
                return parameterService.getSubParameterValuesAsString(key, constrainingValue);
            }
        }, componentClass, parameterName);
    }

    @Override
    public String getSubParameterValueAsString(String namespaceCode, String componentCode, String parameterName, final String constrainingValue) {
        return exec(new Fun<String>() {
            public String f(ParameterKey key) {
               return parameterService.getSubParameterValueAsString(key, constrainingValue);
            }
        }, namespaceCode, componentCode, parameterName);
    }

    @Override
    public String getSubParameterValueAsString(Class<?> componentClass, String parameterName, final String constrainingValue) {
        return exec(new Fun<String>() {
            public String f(ParameterKey key) {
               return parameterService.getSubParameterValueAsString(key, constrainingValue);
            }
        }, componentClass, parameterName);
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public void setParameterService(org.kuali.rice.core.api.parameter.ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    //utilities that act as a poor-man's closure - these help consolidate validation & construction of parameter keys
    private <R> R exec(Fun<R> fun, String namespaceCode, String componentCode, String parameterName) {
        if (StringUtils.isBlank(applicationCode)) {
            throw new IllegalStateException("applicationCode is blank - this service is not configured correctly");
        }

        return fun.f(ParameterKey.create(applicationCode, namespaceCode, componentCode, parameterName));
    }

    private <R> R exec(Fun<R> fun, Class<?> componentClass, String parameterName) {
        return exec(fun, kualiModuleService.getNamespaceCode(componentClass), kualiModuleService.getComponentCode(componentClass), parameterName);
    }

    private static interface Fun<R> {
        R f(ParameterKey key);
    }
}
