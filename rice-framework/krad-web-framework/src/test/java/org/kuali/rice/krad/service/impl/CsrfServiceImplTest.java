/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.KRADConstants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CsrfServiceImplTest {

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private ParameterService parameterService;

    @InjectMocks
    private CsrfServiceImpl csrfService;

    @Before
    public void setUp() {
        setExemptPathsConfig(null);
        setExemptPathsParam(null);
        setCsrfEnabledConfig(true);
        setCsrfEnabledParam(null);
    }

    @Test
    public void testIsExemptPath_NoExemptPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost/myurl");
        assertFalse(csrfService.isExemptPath(request));
    }

    @Test
    public void testIsExemptPath_OneExemptPath_Config() {
        setExemptPathsConfig("myurl");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost/myurl");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/myurl2");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/myotherurl");
        assertFalse(csrfService.isExemptPath(request));
    }

    @Test
    public void testIsExemptPath_MultipleExemptPaths_Config() {
        setExemptPathsConfig("one,two,http://localhost/three");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost/one");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/two");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/three");
        assertTrue(csrfService.isExemptPath(request));
    }

    @Test
    public void testIsExemptPath_OneExemptPath_Param() {
        setExemptPathsParam("myurl");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost/myurl");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/myurl2");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/myotherurl");
        assertFalse(csrfService.isExemptPath(request));
    }

    @Test
    public void testIsExemptPath_MultipleExemptPaths_Param() {
        setExemptPathsParam("one,two,http://localhost/three");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost/one");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/two");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/three");
        assertTrue(csrfService.isExemptPath(request));
    }

    @Test
    public void testIsExemptPath_Param_Overrides_Config() {
        setExemptPathsConfig("two");
        setExemptPathsParam("one,http://localhost/three");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost/one");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("http://localhost/three");
        assertTrue(csrfService.isExemptPath(request));

        request.setRequestURI("four");
        assertFalse(csrfService.isExemptPath(request));

        // parameter should override config
        request.setRequestURI("http://localhost/two");
        assertFalse(csrfService.isExemptPath(request));

    }

    @Test
    public void testIsEnabled_Default() {
        assertTrue(csrfService.isEnabled());
    }

    @Test
    public void testIsEnabled_Config() {
        setCsrfEnabledConfig(false);
        assertFalse(csrfService.isEnabled());

        setCsrfEnabledConfig(true);
        assertTrue(csrfService.isEnabled());
    }

    @Test
    public void testIsEnabled_Param() {
        setCsrfEnabledParam(false);
        assertFalse(csrfService.isEnabled());

        setCsrfEnabledParam(true);
        assertTrue(csrfService.isEnabled());
    }

    @Test
    public void testIsEnabled_Param_Overrides_Config() {
        setCsrfEnabledConfig(true);
        setCsrfEnabledParam(false);
        assertFalse(csrfService.isEnabled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateCsrfIfNecessary_NullRequest() {
        csrfService.validateCsrfIfNecessary(null, new MockHttpServletResponse());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateCsrfIfNecessary_NullResponse() {
        csrfService.validateCsrfIfNecessary(new MockHttpServletRequest(), null);
    }

    @Test
    public void testValidateCsrfIfNecessary() {
        setCsrfEnabledConfig(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(csrfService.validateCsrfIfNecessary(request, response));

        // enable csrf protection

        setCsrfEnabledConfig(true);
        assertFalse(csrfService.validateCsrfIfNecessary(request, response));

        // now set an exempt path

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        setExemptPathsConfig("a");
        request.setRequestURI("http://a");
        assertTrue(csrfService.validateCsrfIfNecessary(request, response));

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.setRequestURI("http://b");
        assertFalse(csrfService.validateCsrfIfNecessary(request, response));
        assertEquals(403, response.getStatus());

    }

    @Test
    public void testGetSessionToken() {
        setCsrfEnabledConfig(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();
        csrfService.validateCsrfIfNecessary(request, response);
        assertNotNull(csrfService.getSessionToken(request));
    }

    private void setCsrfEnabledParam(Boolean value) {
        when(parameterService.getParameterValueAsBoolean(
                KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE,
                ParameterConstants.ALL_COMPONENT,
                KRADConstants.ParameterNames.CSRF_ENABLED_IND)).thenReturn(value);
    }

    private void setCsrfEnabledConfig(boolean value) {
        when(configurationService.getPropertyValueAsBoolean(KRADConstants.Config.CSRF_ENABLED, true)).thenReturn(value);
    }

    private void setExemptPathsParam(String exemptPaths) {
        when(parameterService.getParameterValueAsFilteredString(
                KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE,
                ParameterConstants.ALL_COMPONENT,
                KRADConstants.ParameterNames.CSRF_EXEMPT_PATHS)).thenReturn(exemptPaths);
    }

    private void setExemptPathsConfig(String exemptPaths) {
        when(configurationService.getPropertyValueAsString(KRADConstants.Config.CSRF_EXEMPT_PATHS)).thenReturn(exemptPaths);
    }



}

