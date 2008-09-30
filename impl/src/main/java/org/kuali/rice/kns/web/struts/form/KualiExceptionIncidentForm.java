/*
 * Copyright 2005-2007 The Kuali Foundation.
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

package org.kuali.rice.kns.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kns.exception.ExceptionIncident;

/**
 * This class is the action form for all Question Prompts.
 * 
 * 
 */
public class KualiExceptionIncidentForm extends KualiForm {
    private static final long serialVersionUID = 831951332440283401L;
    private static Logger LOG=Logger.getLogger(KualiExceptionIncidentForm.class); 
    /**
     * Flag to determine whether it's cancel action
     */
    private boolean cancel=false;
    /**
     * Object containing exception information
     */
//    private KualiExceptionIncident exceptionIncident;
    /**
     * The error subject created from current settings and thrown exception
     */
     private String exceptionReportSubject;
    /**
     * The error message
     */
     private String exceptionMessage;
     /**
      * The error message to be displayed
      */
      private String displayMessage;
     /**
     * Additional message from user
     */
     private String description;
     /**
      * Document id. it's blank if not a document process
      */
     private String documentId=""; 
     /**
      * Session user email address
      */
     private String userEmail="";
     /**
      * Session user name
      */
     private String personUserIdentifier="";
     /**
      * Session user name
      */
     private String userName="";
     /**
      * Detail message not for displaying
      */
     private String stackTrace;
     /**
     * Form that threw the exception
     */
    private String componentName;
     /**
     * A custom contextual information obtained from an attribute of GlobalVariables
     */
    private String customContextualInfo;

    /**
     * @see org.kuali.rice.kns.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        
        super.populate(request);
        
    }

    /*
     * Reset method - reset attributes of form retrieved from session otherwise
     * we will always call docHandler action
     * @param mapping
     * @param request
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        
        this.setMethodToCall(null);
        this.setRefreshCaller(null);
        this.setAnchor(null);
        this.setCurrentTabIndex(0);
        
        this.cancel=false;
        this.documentId=null;
        this.componentName=null;
        this.customContextualInfo=null;
        this.description=null;
        this.displayMessage=null;
        this.exceptionMessage=null;
        this.stackTrace=null;
        this.userEmail=null;
        this.userName=null;
        this.personUserIdentifier=null;

    }
    
    /**
     * This method return list of required information contained by the jsp in both
     * display and hidden properties.
     * 
     * @return
     * <p>Example:
     * <code>
     * documentId, 2942084
     * userEmail, someone@somewhere
     * userName, some name
     * componentFormName, Form that threw exception name
     * exceptionMessage, Error message from exception
     * displayMessage, Either exception error message or generic exception error message
     * stackTrace, Exception stack trace here
     * customContextualInfo, ?
     * </code>
     * 
     */
    public Map<String, String> toMap() {
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY");
            LOG.trace(message);
        }
        
        Map<String, String> map=new HashMap<String, String>();
        map.put(ExceptionIncident.DOCUMENT_ID, this.documentId);
        map.put(ExceptionIncident.USER_EMAIL, this.userEmail);
        map.put(ExceptionIncident.USER_NAME, this.userName);
        map.put(ExceptionIncident.UUID, this.personUserIdentifier);
        map.put(ExceptionIncident.COMPONENT_NAME, this.componentName);
        map.put(ExceptionIncident.DESCRIPTION, this.description);
        map.put(ExceptionIncident.EXCEPTION_REPORT_SUBJECT, this.exceptionReportSubject);
        map.put(ExceptionIncident.EXCEPTION_MESSAGE, this.exceptionMessage);
        map.put(ExceptionIncident.DISPLAY_MESSAGE, this.displayMessage);
        map.put(ExceptionIncident.STACK_TRACE, this.stackTrace);
        map.put(ExceptionIncident.CUSTOM_CONTEXTUAL_INFO, customContextualInfo);
        
        if (LOG.isTraceEnabled()) {
            String message=String.format("ENTRY %s", map.toString());
            LOG.trace(message);
        }
        
        return map;
    }

    /**
     * @return the cancel
     */
    public final boolean isCancel() {
        return this.cancel;
    }

    /**
     * @param cancel the cancel to set
     */
    public final void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * @return the exceptionIncident
     */
//    public final KualiExceptionIncident getExceptionIncident() {
//        return this.exceptionIncident;
//    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the exceptionMessage
     */
    public final String getExceptionMessage() {
        return this.exceptionMessage;
    }

    /**
     * @param exceptionMessage the exceptionMessage to set
     */
    public final void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * @return the displayMessage
     */
    public final String getDisplayMessage() {
        return this.displayMessage;
    }

    /**
     * @param displayMessage the displayMessage to set
     */
    public final void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    /**
     * @return the documentId
     */
    public final String getDocumentId() {
        return this.documentId;
    }

    /**
     * @param documentId the documentId to set
     */
    public final void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * @return the userEmail
     */
    public final String getUserEmail() {
        return this.userEmail;
    }

    /**
     * @param userEmail the userEmail to set
     */
    public final void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
	 * @return the personUserIdentifier
	 */
	public String getPersonUserIdentifier() {
		return this.personUserIdentifier;
	}

	/**
	 * @param personUserIdentifier the personUserIdentifier to set
	 */
	public void setPersonUserIdentifier(String personUserIdentifier) {
		this.personUserIdentifier = personUserIdentifier;
	}

	/**
     * @return the userName
     */
    public final String getUserName() {
        return this.userName;
    }

    /**
     * @param userName the userName to set
     */
    public final void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param stackTrace the stackTrace to set
     */
    public final void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    /**
     * @return the stackTrace
     */
    public final String getStackTrace() {
        return this.stackTrace;
    }

    /**
     * @return the customContextualInfo
     */
    public final String getCustomContextualInfo() {
        return this.customContextualInfo;
    }

    /**
     * @param customContextualInfo the customContextualInfo to set
     */
    public final void setCustomContextualInfo(String customContextualInfo) {
        this.customContextualInfo = customContextualInfo;
    }

    /**
     * @return the exceptionReportSubject
     */
    public final String getExceptionReportSubject() {
        return this.exceptionReportSubject;
    }

    /**
     * @param exceptionReportSubject the exceptionReportSubject to set
     */
    public final void setExceptionReportSubject(String exceptionReportSubject) {
        this.exceptionReportSubject = exceptionReportSubject;
    }

    /**
     * @return the componentName
     */
    public final String getComponentName() {
        return this.componentName;
    }

    /**
     * @param componentName the componentName to set
     */
    public final void setComponentName(String componentName) {
        this.componentName = componentName;
    }

}
