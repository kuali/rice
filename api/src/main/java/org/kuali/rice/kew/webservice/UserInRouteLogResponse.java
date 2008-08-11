package org.kuali.rice.kew.webservice;

/**
 * Response object used for isUserInRouteLog method
 */
public class UserInRouteLogResponse extends ErrorResponse {
    protected String isUserInRouteLog;
    public String getIsUserInRouteLog() {
        return isUserInRouteLog;
    }
    public void setIsUserInRouteLog(String isUserInRouteLog) {
        this.isUserInRouteLog = isUserInRouteLog;
    }
}