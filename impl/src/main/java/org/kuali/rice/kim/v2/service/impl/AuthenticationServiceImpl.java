package org.kuali.rice.kim.v2.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.v2.service.AuthenticationService;
import org.kuali.rice.kns.web.filter.KualiCasFilter;

public class AuthenticationServiceImpl implements AuthenticationService {
	private boolean validatePassword = true;
	
	// TODO: Remove request and get this from Acegi
	public String getPrincipalName(HttpServletRequest request) {
		// TODO: Fix this
		// A bit of a hack as this could be broken down into a CAS and Remote User implementation
		return (request.getRemoteUser() == null) ? KualiCasFilter.getRemoteUser(request) : request.getRemoteUser();
    }
    
    public boolean validatePassword() {
    	return validatePassword;
    }

	public void setValidatePassword(boolean validatePassword) {
		this.validatePassword = validatePassword;
	}    
}
