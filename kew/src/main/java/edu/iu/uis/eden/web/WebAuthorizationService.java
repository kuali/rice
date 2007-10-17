
package edu.iu.uis.eden.web;

import javax.servlet.http.HttpServletRequest;

import edu.iu.uis.eden.web.session.UserSession;

/**
 * A service which is used to authorize access to certain screens in the application
 * for certain users.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface WebAuthorizationService {

	/**
	 * Determines if the user identified by the given UserSession is permitted to access the resource
	 * being requested in the HttpServletRequest.
	 *
	 * @return an AuthorizationResult which indicates whether or not the user is authorized and may
	 * optionally include messages to display.
	 */
	public AuthorizationResult isAuthorized(UserSession userSession, HttpServletRequest request);

}
