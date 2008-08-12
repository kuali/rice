package org.kuali.rice.kim.v2.service.soap.impl;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kim.v2.service.soap.AuthenticationServiceSoap;

// TODO implement this class by piggy backing on the standard implementation ("has a" not "is a") and translating from interface to DTOs
public class AuthenticationServiceSoapImpl implements AuthenticationServiceSoap {
    public String getPrincipalName(HttpServletRequest request) {
    	return null;
    }
    
    public boolean validatePassword() {
    	return true;
    }
}
