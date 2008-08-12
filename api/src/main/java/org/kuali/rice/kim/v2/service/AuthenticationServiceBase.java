package org.kuali.rice.kim.v2.service;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationServiceBase {
    public String getPrincipalName(HttpServletRequest request);
    
    public boolean validatePassword();
}
