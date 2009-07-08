package org.kuali.rice.kim.service;

import javax.servlet.http.HttpServletRequest;

/**
 * This service is used to extract the name of the authenticated principal from
 * an incoming http request.  Depending on the implementation of this service,
 * it may extract information from a request which has already been authenticated
 * (i.e. via another service like CAS or Shiboleth).  Alternatively, this
 * implementation might actually perform the authentication itself based on
 * information available on the http request.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public interface AuthenticationService {
	
	/**
	 * Returns the principalName of the principal that has authenticated with
	 * the incoming HttpServletRequest.  Implementations of this method might
	 * perform actual authentication or merely extract the existing
	 * authenticated principal's name off of the incoming request.
	 * 
	 * @param request the incoming HttpServletRequest
	 * @return the principalName of the authenticated principal, or null if
	 * the principal could not be authenticated
	 */
    public String getPrincipalName(HttpServletRequest request);
    
}
