/*
 * Copyright 2005-2006 The Kuali Foundation.
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
import javax.xml.namespace.QName;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * A login filter which forwards to a login page that allows for the desired
 * authentication ID to be entered without the need for a password.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DummyLoginFilter implements Filter {
    private String loginPath;
    public void init(FilterConfig config) throws ServletException {
        loginPath = config.getInitParameter("loginPath");
        if (loginPath == null) {
            loginPath = "/WEB-INF/jsp/dummy_login.jsp";
        }
    }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest hsreq = (HttpServletRequest) request;
            UserSession session = null;
            if (UserLoginFilter.isUserSessionEstablished(hsreq)) {
            	session = UserLoginFilter.getUserSession(hsreq);	
            }
            if (session == null) {
            	IdentityManagementService auth = KIMServiceLocator.getIdentityManagementService();
           		request.setAttribute("showPasswordField", auth.authenticationServiceValidatesPassword());
                final String user = request.getParameter("__login_user");
                final String password = request.getParameter("__login_pw");
                if (user != null) {
                	// Very simple password checking. Nothing hashed or encrypted. This is strictly for demonstration purposes only.
                    if (auth.authenticationServiceValidatesPassword()) {
        				KimPrincipal principal = auth.getPrincipalByPrincipalNameAndPassword( user, password );
        				if (principal == null ) {
        					request.setAttribute("invalidPassword", Boolean.TRUE);
        					request.getRequestDispatcher(loginPath).forward(request, response);
                        	return;
        				}
                    }
                    // wrap the request with the remote user
                    // UserLoginFilter and WebAuthenticationService will create the session
                    request = new HttpServletRequestWrapper(hsreq) {
                        public String getRemoteUser() {
                            return user;
                        }
                    };
                } else {
                    // no session has been established and this is not a login form submission, so forward to login page
                    request.getRequestDispatcher(loginPath).forward(request, response);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}
