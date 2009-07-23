/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;


/**
 * A filter for processing user logins and creating a {@link UserSession}.
 *
 * @see UserSession
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserLoginFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(UserLoginFilter.class);

    public void init(FilterConfig config) throws ServletException {}

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!(req instanceof HttpServletRequest && res instanceof HttpServletResponse)) {
            chain.doFilter(req, res);
            return;
        }

        LOG.debug("Begin UserLoginFilter...");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        final UserSession userSession;
        if (!isUserSessionEstablished(request)) {
            userSession = login(request);
            if (userSession != null) {
                request.getSession().setAttribute(KEWConstants.USER_SESSION_KEY, userSession);
            }
        } else {
            userSession = (UserSession) request.getSession().getAttribute(KEWConstants.USER_SESSION_KEY);
        }

        if (userSession != null) {
            // Override the HttpServletRequest with one that provides
            // our logged-in user. This allows any engine-agnostic webapp code
            // that may be living in the context to obtain remote user traditionally
            LOG.debug("Wrapping servlet request: " + userSession.getPrincipalName());
            request = new HttpServletRequestWrapper(request) {
                public String getRemoteUser() {
                    return userSession.getPrincipalName();
                }
            };
        }

        // set up the thread local reference to the current authenticated user
        // and then forward to next filter in the chain
        MDC.put("user", userSession.getPrincipalName());
        try {
            UserSession.setAuthenticatedUser(userSession);
            LOG.debug("...end UserLoginFilter.");
            chain.doFilter(request, response);
        } finally {
        	MDC.remove("user");
            UserSession.setAuthenticatedUser(null);
        }

    }

    /**
     * Checks if the user who made the request has a UserSession established
     *
     * @param request
     *            the HTTPServletRequest object passed in
     * @return true if the user session has been established, false otherwise
     */
    public static boolean isUserSessionEstablished(HttpServletRequest request) {
        return (request.getSession(false) != null && request.getSession(false).getAttribute(KEWConstants.USER_SESSION_KEY) != null);
    }

    /**
     * create a UserSession object for the workflow user
     *
     * @param request
     *            the servlet request
     * @return UserSession object if authentication was successful, null otherwise
     */
    private UserSession login(HttpServletRequest request) {
        LOG.info("performing user login: ");

        String principalName = null;
        KimPrincipal principal = null;

        IdentityManagementService idmService = KIMServiceLocator.getIdentityManagementService();
        principalName = idmService.getAuthenticatedPrincipalName(request);
        	
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Looking up principal by name: " + principalName);
        }
        
        principal = idmService.getPrincipalByPrincipalName(principalName);

        if (StringUtils.isBlank(principalName) || principal == null) {
        	throw new RiceRuntimeException("KIM could not identify an authenticated principal from incoming request.  The principal name was " + principalName);
        }
        
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("ending user lookup: " + principal);
        }

        UserSession userSession = new UserSession(principal);
        LOG.info("...finished performing user login.");
        return userSession;
    }

    public static UserSession getUserSession(HttpServletRequest request) {
        return (UserSession) request.getSession().getAttribute(KEWConstants.USER_SESSION_KEY);
    }

    public void destroy() {}
}
