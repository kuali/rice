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

import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * WebAuthenticationService implementation that just propagates the Remote User
 * from the web environment into the network id
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RemoteUserWebAuthenticationService implements WebAuthenticationService {
    public UserId getUserId(HttpServletRequest request) {
        return new AuthenticationUserId(request.getRemoteUser());
    }
    public UserSession updateUserSession(UserSession userSession, HttpServletRequest request) {
  	  return userSession;
    }
    public UserSession establishInitialUserSession(UserSession userSession, HttpServletRequest request) {
    	return userSession;
    }
}