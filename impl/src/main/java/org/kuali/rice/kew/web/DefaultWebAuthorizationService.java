package org.kuali.rice.kew.web;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kew.web.session.UserSession;


public class DefaultWebAuthorizationService implements WebAuthorizationService {

	public AuthorizationResult isAuthorized(UserSession userSession, HttpServletRequest request) {
		return new AuthorizationResult(true);
	}

}
