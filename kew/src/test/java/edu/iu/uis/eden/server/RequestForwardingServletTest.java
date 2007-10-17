/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Created on Dec 6, 2006

package edu.iu.uis.eden.server;

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;

/**
 * Tests the RequestForwardingServlet used to remap WSDL location
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RequestForwardingServletTest extends TestCase {

    protected static void testForward(String sourcePattern, String targetPath, String sourcePath, String forwardedPath) throws ServletException, IOException {
        MockServletConfig cfg = new MockServletConfig(new MockServletContext());
        cfg.addInitParameter(sourcePattern, targetPath);
        RequestForwardingServlet servlet = new RequestForwardingServlet();
        servlet.init(cfg);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", sourcePath);
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        assertEquals(response.getForwardedUrl(), forwardedPath);
    }

    public void test() throws Exception {
        testForward(".*/(\\w+)\\.wsdl", "/services/{0}?wsdl", "http://bogushost/wsdl/WorkflowDocumentActionsService.wsdl", "/services/WorkflowDocumentActionsService?wsdl");

        testForward(".*/(\\w+)\\.wsdl", "/services/{0}?wsdl", "http://bogushost/any/path/really/WorkflowDocumentActionsService.wsdl", "/services/WorkflowDocumentActionsService?wsdl");

        testForward(".*/(\\w+)\\.wsdl", "/services/{0}?wsdl", "http://bogushost/any/path/really/Invalid Service Name.wsdl", null);
    }
}