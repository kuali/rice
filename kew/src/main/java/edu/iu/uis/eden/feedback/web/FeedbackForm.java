/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.feedback.web;

import org.apache.struts.action.ActionForm;

/**
 * Struts ActionForm for {@link FeedbackAction}.
 * 
 * @see FeedbackAction
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
/* Action Form for Feedback and Support Web form */
public class FeedbackForm extends ActionForm {

	private static final long serialVersionUID = -6881094307991817497L;
	/* Form elements */
    private String userName;
    private String userEmail;
    private String networkId;
    private String documentType;
    private String pageUrl;
    private String exception;
    private String timeDate;
    private String comments;
    private String routeHeaderId;
    private String firstName;
    private String lastName;
    private String phone;
    private String edenCategory;
    private String methodToCall = "";

    /**
     * @return Returns the edenCategory.
     */
    public String getEdenCategory() {
        return edenCategory;
    }

    /**
     * @param edenCategory
     *            The edenCategory to set.
     */
    public void setEdenCategory(String edenCategory) {
        this.edenCategory = edenCategory;
    }

    /* getters and setters */
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setRouteHeaderId(String routeHeaderId) {
        this.routeHeaderId = routeHeaderId;
    }

    public String getRouteHeaderId() {
        return routeHeaderId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * @return Returns the methodToCall.
     */
    public String getMethodToCall() {
        return methodToCall;
    }

    /**
     * @param methodToCall
     *            The methodToCall to set.
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

    
    
}
