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
package org.kuali.rice.krad.filter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * Filter for establishing login for load testing.
 *
 * <p>Note this should only be used for load testing!</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TestingLoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    /**
     * Looks for a login user request parameter and establishs a user session for that user, then simply
     * returns a login message.
     *
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (StringUtils.isBlank(request.getParameter("login_user"))) {
            return;
        }

        final String user = request.getParameter("login_user");

        UserSession userSession = new UserSession(user);
        httpServletRequest.getSession().setAttribute(KRADConstants.USER_SESSION_KEY, userSession);

        // wrap the request with the signed in user
        // UserLoginFilter and WebAuthenticationService will build the session
        request = new HttpServletRequestWrapper(httpServletRequest) {
            @Override
            public String getRemoteUser() {
                return user;
            }
        };

        response.getWriter().print("Login Successful.");
    }

    @Override
    public void destroy() {

    }
}
