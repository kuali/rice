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
// Created on Dec 13, 2005

package edu.iu.uis.eden.test.web;

import javax.servlet.ServletContext;

import org.springframework.mock.web.MockHttpServletRequest;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * Subclass of MockHttpServletRequest that initializes the request with a user session
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkflowServletRequest extends MockHttpServletRequest {
    public WorkflowServletRequest() {
        super();
    }
    public WorkflowServletRequest(ServletContext context, String method, String requestURI) {
        super(context, method, requestURI);
    }
    public WorkflowServletRequest(ServletContext context) {
        super(context);
    }
    public WorkflowServletRequest(String method, String requestURI) {
        super(method, requestURI);
    }

    public WorkflowServletRequest(String user) throws EdenUserNotFoundException {
        setUser(user);
    }

    public void setUser(String user) throws EdenUserNotFoundException {
        WorkflowUser wfuser;
        if (user == null) {
            wfuser = null;
        } else {
            wfuser = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(user));
        }
        setWorkflowUser(wfuser);
    }

    public String getUser() {
        WorkflowUser user = getWorkflowUser();
        if (user == null) return null;
        UserId userId = user.getAuthenticationUserId();
        if (userId == null) return null;
        return userId.getId();
    }

    public void setBackdoorId(String backdoorId) throws EdenUserNotFoundException {
        UserSession session = getUserSession();
        if (session == null) {
            throw new IllegalStateException("Session must be set before backdoor id is set");
        }
        session.setBackdoorId(backdoorId);
    }

    public String getBackdoorId() {
        UserId userId = getBackdoorUserId();
        if (userId == null) return null;
        return userId.getId();
    }

    public void setWorkflowUser(WorkflowUser user) {
        if (user == null) {
            setUserSession(null);
        } else {
            setUserSession(new UserSession(user));
        }
    }

    public WorkflowUser getWorkflowUser() {
        UserSession session = getUserSession();
        if (session == null) return null;
        return session.getLoggedInWorkflowUser();
    }

    public UserId getBackdoorUserId() {
        UserSession session = getUserSession();
        if (session == null) return null;
        return session.getBackdoorId();
    }

    public void setUserSession(UserSession userSession) {
        getSession().setAttribute(EdenConstants.USER_SESSION_KEY, userSession);
    }

    public UserSession getUserSession() {
        return (UserSession) getSession().getAttribute(EdenConstants.USER_SESSION_KEY);
    }
}