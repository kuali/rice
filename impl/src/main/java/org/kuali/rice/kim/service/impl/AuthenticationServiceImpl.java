package org.kuali.rice.kim.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.service.AuthenticationService;

public class AuthenticationServiceImpl implements AuthenticationService {
	public String getPrincipalName(HttpServletRequest request) {
		return request.getRemoteUser();
	}
}
