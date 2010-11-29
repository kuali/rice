/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.krad;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.Guid;
import org.kuali.rice.kns.util.KNSConstants;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * This is a description of what this class does - swgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KradControllerHandlerInterceptor implements HandlerInterceptor {

	private static final Logger LOG = Logger.getLogger(KradControllerHandlerInterceptor.class);
	
	protected IdentityManagementService identityManagementService;
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		// do nothing
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		// do nothing
	}

	/**
	 * override of the pre process for all struts requests which will ensure
	 * that we have the appropriate state for user sessions for all of our
	 * requests, also populating the GlobalVariables class with our UserSession
	 * for convenience to the non web layer based classes and implementations
	 * 
	 * this is KualiRequestProcessor.processPreprocess
	 * 
	 * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		UserSession userSession = null;
		if (!isUserSessionEstablished(request)) {
			String principalName = getIdentityManagementService().getAuthenticatedPrincipalName(request);
			if ( StringUtils.isNotBlank(principalName) ) {
				KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName( principalName );
				if ( principal != null ) {
					AttributeSet qualification = new AttributeSet();
					qualification.put( "principalId", principal.getPrincipalId() );
					// check to see if the given principal is an active principal/entity
					if ( getIdentityManagementService().isAuthorized( 
							principal.getPrincipalId(), 
							KimConstants.KIM_TYPE_DEFAULT_NAMESPACE, 
							KimConstants.PermissionNames.LOG_IN, 
							null, 
							qualification ) ) {
					
						// This is a temp solution to show KIM AuthN checking existence of Principals.
						// We may want to move this code to the IdentityService once it is finished.
						userSession = new UserSession(principalName);
						if ( userSession.getPerson() == null ) {
							LOG.warn("Unknown User: " + principalName);
							throw new RuntimeException("Invalid User: " + principalName);
						}
						
						String kualiSessionId = this.getKualiSessionId(request, response);
						if (kualiSessionId == null) {
							kualiSessionId = new Guid().toString();
							response.addCookie(new Cookie(KNSConstants.KUALI_SESSION_ID, kualiSessionId));
						}
						userSession.setKualiSessionId(kualiSessionId);
					} /* if: principal is active */ else {
						LOG.warn("Principal is Inactive: " + principalName);
						throw new RuntimeException("You cannot log in, because you are not an active Kuali user.\nPlease ask someone to activate your account, if you need to use Kuali Systems.\nThe user id provided was: " + principalName + ".\n");
					}
				} /* if: principal is null */ else {
					LOG.warn("Principal Name not found in IdentityManagementService: " + principalName);
					throw new RuntimeException("Unknown User: " + principalName);
				}
			} /* if: principalName blank */ else {
				LOG.error( "Principal Name from the authentication service was blank!" );
				throw new RuntimeException( "Blank User from AuthenticationService - This should never happen." );
			}
		} else {
			userSession = (UserSession) request.getSession().getAttribute(KNSConstants.USER_SESSION_KEY);
		}
		
		request.getSession().setAttribute(KNSConstants.USER_SESSION_KEY, userSession);
		GlobalVariables.setUserSession(userSession);
		GlobalVariables.clear();
		if ( StringUtils.isNotBlank( request.getParameter(KNSConstants.BACKDOOR_PARAMETER) ) ) {
			if ( !KNSServiceLocator.getKualiConfigurationService().isProductionEnvironment() ) {
				if ( KNSServiceLocator.getParameterService().getIndicatorParameter(KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE, KNSConstants.DetailTypes.BACKDOOR_DETAIL_TYPE, KEWConstants.SHOW_BACK_DOOR_LOGIN_IND) ) {
	    			userSession.setBackdoorUser(request.getParameter(KNSConstants.BACKDOOR_PARAMETER));
	    			org.kuali.rice.kew.web.session.UserSession.getAuthenticatedUser().establishBackdoorWithPrincipalName(request.getParameter(KNSConstants.BACKDOOR_PARAMETER));
				}
			}
		}
		
		return true;
	}
	
	private String getKualiSessionId(HttpServletRequest request, HttpServletResponse response) {
		String kualiSessionId = null;
		Cookie[] cookies = (Cookie[]) request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (KNSConstants.KUALI_SESSION_ID.equals(cookie.getName()))
					kualiSessionId = cookie.getValue();
			}
		}
		return kualiSessionId;
	}
	
	/**
	 * Checks if the user who made the request has a UserSession established
	 * 
	 * @param request
	 *            the HTTPServletRequest object passed in
	 * @return true if the user session has been established, false otherwise
	 */
	private boolean isUserSessionEstablished(HttpServletRequest request) {
		boolean result = (request.getSession().getAttribute(KNSConstants.USER_SESSION_KEY) != null);
		return result;
	}
	
	public IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();
		}
		return this.identityManagementService;
	}

}
