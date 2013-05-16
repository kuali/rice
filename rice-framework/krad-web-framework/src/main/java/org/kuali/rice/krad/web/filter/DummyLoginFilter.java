/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.web.filter;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.exception.AuthenticationException;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

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
        loginPath = ConfigContext.getCurrentContextConfig().getProperty("loginPath");
        showPassword = Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty("showPassword")).booleanValue();
        if (loginPath == null) {
            // TODO not really sure of where to put this as a constant?
            loginPath = "/kr-login/login?methodToCall=start&viewId=DummyLoginView&dataObjectClassName=org.kuali.rice.krad.web.login.DummyLoginForm";
        }
    }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		this.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}
    
	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final UserSession session = KRADUtils.getUserSessionFromRequest(request);

        if (session == null) {
            IdentityService auth = KimApiServiceLocator.getIdentityService();
            final String user = request.getParameter("__login_user");
            final String password = request.getParameter("__login_pw");

            if (user != null) {

                // if passwords are used, they cannot be blank
                if (showPassword && StringUtils.isBlank(password)) {
                    handleInvalidLogin(request, response);
                    return;
                }

                // Very simple password checking. Nothing hashed or encrypted. This is strictly for demonstration purposes only.
                //    password must have non null value on krim_prncpl_t record
                final Principal principal = showPassword ? auth.getPrincipalByPrincipalNameAndPassword(user, password) : auth.getPrincipalByPrincipalName(user);

                if (principal == null) {
                    handleInvalidLogin(request, response);
                    return;
                }

                UserSession userSession = new UserSession(user);

                if ( userSession.getPerson() == null ) {
                    throw new AuthenticationException("Invalid User: " + user  );
                }

                request.getSession().setAttribute(KRADConstants.USER_SESSION_KEY, userSession);

                // wrap the request with the remote user
                // UserLoginFilter and WebAuthenticationService will create the session
                request = new HttpServletRequestWrapper(request) {
                    @Override
					public String getRemoteUser() {
                        return user;
                    }
                };

                StringBuffer redirectUrl = new StringBuffer(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY));
                redirectUrl.append(findTargetUrl(request));

                response.sendRedirect(redirectUrl.toString());
                return;

            } else {
                // allow ajax calls from login screen
                if (ObjectUtils.isNotNull(request.getPathInfo())&&request.getPathInfo().equals("/listener")){
                    chain.doFilter(request, response);
                    return;
                }

                // process login request
                if (ObjectUtils.isNotNull(request.getPathInfo())&&request.getPathInfo().equals("/login")){
                    chain.doFilter(request, response);
                    return;
                }

                // no session has been established and this is not a login form submission, so redirect to login page
                response.sendRedirect(getLoginRedirectUrl(request));
                return;

                }
        } else {
            request = new HttpServletRequestWrapper(request) {
                @Override
                public String getRemoteUser() {
                    return session.getPrincipalName();
                }
            };
        }
        chain.doFilter(request, response);
    }
	
	/**
	 * Handles and invalid login attempt.
     *
     * Sets error message and redirects to login screen
	 *  
	 * @param request the incoming request
	 * @param response the outgoing response
	 * @throws javax.servlet.ServletException if unable to handle the invalid login
	 * @throws java.io.IOException if unable to handle the invalid login
	 */
	private void handleInvalidLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuffer redirectUrl = new StringBuffer(getLoginRedirectUrl(request));
        redirectUrl.append("&login_message=Invalid Login");

        response.sendRedirect(redirectUrl.toString());
        return;
    }

    @Override
	public void destroy() {
    	loginPath = null;
    }

    /**
     * Construct Url to login screen with original target Url in returnLocation property
     *
     * @param request
     * @return Url string
     * @throws IOException
     */
    private String getLoginRedirectUrl(HttpServletRequest request) throws IOException {
        String targetUrl = findTargetUrl(request);

        StringBuffer redirectUrl = new StringBuffer(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.APPLICATION_URL_KEY));
        redirectUrl.append(loginPath);
        redirectUrl.append("&returnLocation=");
        redirectUrl.append(URLEncoder.encode(targetUrl,"UTF-8"));

        return redirectUrl.toString();
    }

    /**
     * Construct a url from a HttpServletRequest & removing login properties
     *
     * @param request
     * @return Url string
     */
    private String findTargetUrl(HttpServletRequest request) {
        StringBuffer targetUrl = new StringBuffer();
        targetUrl.append(request.getServletPath());

        if (StringUtils.isNotBlank(request.getPathInfo())) {
            targetUrl.append(request.getPathInfo());
        }

        // clean login params from query string
        if (StringUtils.isNotBlank(request.getQueryString())) {
            targetUrl.append("?");

            for (String pair : request.getQueryString().split("&")) {
                int eq = pair.indexOf("=");

                if (eq < 0) {
                    // key with no value
                    targetUrl.append("&").append(pair);
                } else {
                    // key=value
                    String key = pair.substring(0, eq);
                    if (!key.equals("__login_pw")
                             && !key.equals("__login_user")
                             && !key.equals("login_message")
                             && !key.equals("returnLocation")) {
                        targetUrl.append("&").append(pair);
                    }
                }

            }

        }

        return targetUrl.toString().replace("&&","&").replace("?&","?");
    }
}
