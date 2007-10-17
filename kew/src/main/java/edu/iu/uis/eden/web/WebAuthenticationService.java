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

import javax.servlet.http.HttpServletRequest;

import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * A service which is used to authenticate web users from an Http request.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WebAuthenticationService {
	
	/**
	 * Determines who the authenticated user is by examining the given HttpServiceRequest.
	 * 
	 * @param request the HttpServletRequest which should contain user authentication information.
	 * @return the id of the authenticated user, this id should corresond to a user with
	 * the give UserId in the UserService
	 */
	public UserId getUserId(HttpServletRequest request);
	
	/**
	 * Establishes the initial user session.  Can be used to lookup and cache user authentications,
	 * roles, etc.  Called only once when the UserSession is initially established.
	 */
	public UserSession establishInitialUserSession(UserSession userSession, HttpServletRequest request);

	/**
	 * Update the UserSession with any new information.  This method is invoked on entry into every page in the system 
	 * after the UserSession has been established and can be used to set up initial or subsequent authentications
	 * on the UserSession object with additional information from the request if necessary.  It can also be used
	 * to swap out implementations of the UserSession if the implementing application should deem it necessary.
	 * 
	 * @return the updated UserSession
	 */
	public UserSession updateUserSession(UserSession userSession, HttpServletRequest request);
	
}
