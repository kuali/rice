package edu.iu.uis.eden.web;

import javax.servlet.http.HttpServletRequest;

import edu.iu.uis.eden.web.session.UserSession;

public class DefaultWebAuthorizationService implements WebAuthorizationService {

	public AuthorizationResult isAuthorized(UserSession userSession, HttpServletRequest request) {
		return new AuthorizationResult(true);
	}

}
