/*
 * Copyright 2005-2008 The Kuali Foundation
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

import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * A login filter which forwards to a login page that allows for the desired
 * authentication ID to be entered without the need for a password.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DummyLoginFilter implements Filter {
    private String loginPath;
    private boolean showPassword = false;
    @Override
	public void init(FilterConfig config) throws ServletException {
        loginPath = config.getInitParameter("loginPath");
        showPassword = Boolean.valueOf(config.getInitParameter("showPassword")).booleanValue();
        if (loginPath == null) {
            loginPath = "/WEB-INF/jsp/dummy_login.jsp";
        }
    }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}
    
	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final UserSession session = KRADUtils.getUserSessionFromRequest(request);
        
        if (session == null) {
        	IdentityManagementService auth = KimApiServiceLocator.getIdentityManagementService();
       		request.setAttribute("showPasswordField", Boolean.valueOf(showPassword));
            final String user = request.getParameter("__login_user");
            final String password = request.getParameter("__login_pw");
            if (user != null) {
            	// Very simple password checking. Nothing hashed or encrypted. This is strictly for demonstration purposes only.
            	final Principal principal = showPassword ? auth.getPrincipalByPrincipalNameAndPassword(user, password) : auth.getPrincipalByPrincipalName(user);
            	if (principal == null) {
            		handleInvalidLogin(request, response);	
            		return;
            	}
            	
                // wrap the request with the remote user
                // UserLoginFilter and WebAuthenticationService will create the session
                request = new HttpServletRequestWrapper(request) {
                    @Override
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
        chain.doFilter(request, response);
    }
	
	/**
	 * Handles and invalid login attempt.
	 *  
	 * @param request the incoming request
	 * @param response the outgoing response
	 * @throws ServletException if unable to handle the invalid login
	 * @throws IOException if unable to handle the invalid login
	 */
	private void handleInvalidLogin(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		request.setAttribute("invalidAuth", Boolean.TRUE);
		request.getRequestDispatcher(loginPath).forward(request, response);
	}

    @Override
	public void destroy() {
    	loginPath = null;
    }
}
