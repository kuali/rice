/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.CsrfService;
import org.kuali.rice.krad.util.CsrfValidator;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CsrfServiceImpl implements CsrfService {

    private ConfigurationService configurationService;
    private ParameterService parameterService;

    @Override
    public boolean validateCsrfIfNecessary(HttpServletRequest request, HttpServletResponse response) {
        if (request == null || response == null) {
            throw new IllegalArgumentException("request and response must not be null");
        }
        return !isEnabled() || isExemptPath(request) || CsrfValidator.validateCsrf(request, response);
    }

    /**
     * Returns true if the given requestUri matches one of the provided exempt paths.
     */
    protected boolean isExemptPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String[] exemptPaths = exemptPaths();
        if (exemptPaths != null) {
            for (String path : exemptPaths) {
                if (requestURI.contains(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected String[] exemptPaths() {
        // check parameter first
        String exemptPaths = getParameterService().getParameterValueAsFilteredString(KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, ParameterConstants.ALL_COMPONENT, KRADConstants.ParameterNames.CSRF_EXEMPT_PATHS);
        if (exemptPaths == null) {
            // next check the config property
            exemptPaths = getConfigurationService().getPropertyValueAsString(KRADConstants.Config.CSRF_EXEMPT_PATHS);
        }
        if (StringUtils.isBlank(exemptPaths)) {
            return null;
        }
        return exemptPaths.split(",");
    }

    protected boolean isEnabled() {
        // first check the system parameter
        Boolean csrfEnabled = getParameterService().getParameterValueAsBoolean(KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE, ParameterConstants.ALL_COMPONENT, KRADConstants.ParameterNames.CSRF_ENABLED_IND);
        if (csrfEnabled == null) {
            // next check the config property
            csrfEnabled = getConfigurationService().getPropertyValueAsBoolean(KRADConstants.Config.CSRF_ENABLED, true);
        }
        return csrfEnabled;
    }

    @Override
    public String getSessionToken(HttpServletRequest request) {
        return CsrfValidator.getSessionToken(request);
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            this.configurationService = CoreApiServiceLocator.getKualiConfigurationService();
        }
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ParameterService getParameterService() {
        if (parameterService == null) {
            this.parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
