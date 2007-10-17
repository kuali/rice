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

import javax.servlet.Servlet;

import org.springframework.mock.web.MockHttpServletRequest;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.test.web.WorkflowServletRequest;

/**
 * LocalInteractionController subclass that supplies a WorkflowServletRequest initialized
 * with user session
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class LocalWorkflowInteractionController extends LocalInteractionController {
    public LocalWorkflowInteractionController(Servlet servlet) {
        super(servlet);
    }

    protected MockHttpServletRequest createServletRequest(String method, String uri, Script script) throws EdenUserNotFoundException {
        WorkflowServletRequest request = new WorkflowServletRequest(method, uri);
        String user = script.getState().getUser();
        if (user != null) {
            request.setUser(user);
            String backdoorid = script.getState().getBackdoorId();
            if (backdoorid != null) {
                request.setBackdoorId(backdoorid);
            }
        }
        return request;
    }
}