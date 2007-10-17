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
package edu.iu.uis.eden.test.web.framework;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.log4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * An InteractionController which uses Spring mock servlet objects against
 * a specified Servlet instance
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class LocalInteractionController implements InteractionController {
    private static final Logger LOG = Logger.getLogger(LocalInteractionController.class);

    private Servlet servlet;

    public LocalInteractionController(Servlet servlet) {
        this.servlet = servlet;
    }

    public String submit(String method, String uri, Script script) throws Exception {
        MockHttpServletRequest request = createServletRequest(method, uri, script);
        request.setMethod(method);
        Iterator it = script.getState().getRequest().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = entry.getKey().toString();
            Object value = entry.getValue();
            if (value instanceof List) {
                List l = (List) value;
                request.addParameter(name, (String[]) l.toArray(new String[l.size()]));
            } else if (value instanceof String) {
                request.addParameter(name, (String) value);
            } else {
                LOG.warn("Invalid parameter value type for parameter '" + name + "': " + value.getClass());
            }
        }
        MockHttpServletResponse response = createServletResponse(method, uri, script, request);

        servlet.service(request, response);

        response.flushBuffer();
        return response.getContentAsString();
    }

    /**
     * Template method that subclasses can override to return custom MockHttpServletRequest subclasses
     * @param method the method
     * @param uri the request uri
     * @param script the script
     * @return a MockHttpServletRequest
     */
    protected MockHttpServletRequest createServletRequest(String method, String uri, Script script) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        String user = script.getState().getUser();
        if (user != null) {
            request.setRemoteUser(user);
        }
        return request;
    }

    /**
     * Template method that subclasses can override to return custom MockHttpServletResponse subclasses
     * @param method the method
     * @param uri the request uri
     * @param script the script
     * @return a MockHttpServletResponse
     */
    protected MockHttpServletResponse createServletResponse(String method, String uri, Script script, MockHttpServletRequest request) throws Exception {
        return new MockHttpServletResponse();
    }
}