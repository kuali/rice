package org.kuali.rice.kim.v2.service;


public interface AuthenticationServiceBase {
    public String getPrincipalName();
    
    public boolean validatePassword();
}
