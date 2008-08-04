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
package edu.iu.uis.eden.clientapp;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.web.UserLoginFilter;
import edu.iu.uis.eden.web.WebAuthenticationService;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * Class that extracts the networkid of the user from request using the request attribute
 * "_currentWorkflowUser".  This class is used so that applications using embedded workflow
 * can easily tell the embedded workflow web layer which user is logged in when using the
 * {@link UserLoginFilter} in the application web app to construct the workflow session object.
 *
 * The {@link UserLoginFilter} constructs the {@link UserSession} object used by workflow and
 * contacts the implemented {@link WebAuthenticationService} to get the currently logged in
 * user.
 *
 * This simple implementation could be replaced by another auth service that went against
 * an institution specific implementation.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SimpleWebAuthenticationService implements WebAuthenticationService {

	public static String LOGGED_IN_USER_REQUEST_ATT_KEY = "__CURRENTLY_LOGGED_IN_USER";

	public UserId getUserId(HttpServletRequest request) {
		String id = (String)request.getAttribute(LOGGED_IN_USER_REQUEST_ATT_KEY);
		if (StringUtils.isBlank(id)) {
			return null;
		}
		return new AuthenticationUserId(id);
	}

	public UserSession updateUserSession(UserSession userSession, HttpServletRequest request) {
		return userSession;
	}

	public UserSession establishInitialUserSession(UserSession userSession, HttpServletRequest request) {
		return userSession;
	}
}