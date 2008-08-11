package org.kuali.rice.kew.webservice;

/**
 * Response object used for deleteNote method
 */
public class ErrorResponse {
    protected String errorMessage;
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}