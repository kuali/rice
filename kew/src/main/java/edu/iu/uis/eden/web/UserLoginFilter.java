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
package edu.iu.uis.eden.web;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

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

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * A filter for processing user logins and creating a {@link UserSession}.
 *
 * @see UserSession
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class UserLoginFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(UserLoginFilter.class);

    public void init(FilterConfig config) throws ServletException {
    }

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
                request.getSession().setAttribute(EdenConstants.USER_SESSION_KEY, userSession);
            }
        } else {
            userSession = (UserSession) request.getSession().getAttribute(EdenConstants.USER_SESSION_KEY);
        }

        if (userSession != null) {
        	// callback to the authentication service to update the user session if necessary
        	KEWServiceLocator.getWebAuthenticationService().updateUserSession(userSession, request);
            if (userSession.isBackdoorInUse()) {
                //a bad backdoorId is passed in all bets off
                if (userSession.getWorkflowUser() == null) {
                    try {
                        userSession.setBackdoorId(userSession.getLoggedInWorkflowUser().getAuthenticationUserId().getAuthenticationId());
                    } catch (EdenUserNotFoundException e) {
                        //realistically if we can't find the logged in user we're done.
                        LOG.error("Error setting backdoor id", e);
                    }
                }
            }

            // Override the HttpServletRequest with one that provides
            // our logged-in user.  This allows any engine-agnostic webapp code
            // that may be living in the context to obtain remote user traditionally
            LOG.debug("Wrapping servlet request: " + userSession.getNetworkId());
            request = new HttpServletRequestWrapper(request) {
                public String getRemoteUser() {
                    return userSession.getNetworkId();
                }
            };

            MDC.put("user", userSession.getNetworkId());
        }

        // set up the thread local reference to the current authenticated user
        // and then forward to next filter in the chain
        try {
			UserSession.setAuthenticatedUser(userSession);
			if (isAuthorizedToViewResource(userSession, request)) {
			        LOG.debug("...end UserLoginFilter.");
				chain.doFilter(request, response);
			} else {
				request.getRequestDispatcher("/WEB-INF/jsp/NotAuthorized.jsp").forward(request, response);
			}
		} finally {
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
        return (request.getSession(false) != null && request.getSession(false).getAttribute(EdenConstants.USER_SESSION_KEY) != null);
    }

    /**
     * create a UserSession object for the workflow user
     *
     * @param request the servlet request
     * @return UserSession object if authentication was successful, null otherwise
     */
    private UserSession login(HttpServletRequest request) {
        LOG.info("performing user login: ");

        WorkflowUser workflowUser = null;
        try {
            WebAuthenticationService webAuthenticationService = KEWServiceLocator.getWebAuthenticationService();
            UserId id = webAuthenticationService.getUserId(request);
            if (id == null || StringUtils.isBlank(id.getId())) {
                LOG.error("WebAuthenticationService did not derive a network id from incoming request");
                return null;
            }
            LOG.debug("Looking up user: " + id);
            workflowUser = ((UserService) KEWServiceLocator.getUserService()).getWorkflowUser(id);
            LOG.debug("ending user lookup: " + workflowUser);
            UserSession userSession = new UserSession(workflowUser);
            //load the users preferences.  The preferences action will update them if necessary
            userSession.setPreferences(KEWServiceLocator.getPreferencesService().getPreferences(workflowUser));
            userSession.setGroups(KEWServiceLocator.getWorkgroupService().getUsersGroupNames(workflowUser));
            webAuthenticationService.establishInitialUserSession(userSession, request);
            return userSession;
        } catch (Exception e) {
            LOG.error("Error in user login", e);
        } finally {
            LOG.info("...finished performing user login.");
        }
        return null;
    }

    public static UserSession getUserSession(HttpServletRequest request) {
        return (UserSession) request.getSession().getAttribute(EdenConstants.USER_SESSION_KEY);
    }

    private static String currentRestrictionSet = "";
    private static Set restrictedResources = new HashSet();

    private static boolean isAuthorizedToViewResource(UserSession userSession, HttpServletRequest request) {
	LOG.debug("Checking authorization to view resources...");
	try {
	    String restrictedResourceTokens = Utilities.getApplicationConstant(EdenConstants.WORKFLOW_ADMIN_URL_KEY);
	    if (restrictedResourceTokens == null) {
		restrictedResourceTokens = "";
	    }
	    synchronized (restrictedResources) {
		if (!currentRestrictionSet.equals(restrictedResourceTokens)) {
		    currentRestrictionSet = restrictedResourceTokens;
		    restrictedResources = new HashSet();
		    StringTokenizer tokenizer = new StringTokenizer(currentRestrictionSet, " ");
		    while (tokenizer.hasMoreTokens()) {
			restrictedResources.add(tokenizer.nextElement());
		    }
		}
	    }
	    String requestedResource = request.getServletPath();
	    if (restrictedResources.contains(requestedResource)) {
		return userSession.isAdmin();
	    } else {
		return true;
	    }
	} finally {
	    LOG.debug("...finished checking authorization to view resources.");
	}
    }

    public void destroy() {
    }
}