package org.kuali.rice.kim.service;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    public String getPrincipalName(HttpServletRequest request);
    
    public boolean validatePassword();
}
