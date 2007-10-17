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

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.mail.EmailBody;
import edu.iu.uis.eden.mail.EmailContent;
import edu.iu.uis.eden.mail.EmailContentService;
import edu.iu.uis.eden.mail.EmailFrom;
import edu.iu.uis.eden.mail.EmailService;
import edu.iu.uis.eden.mail.EmailSubject;
import edu.iu.uis.eden.mail.EmailTo;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * Struts action which handles the Feedback screen.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class FeedbackAction extends WorkflowAction {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FeedbackAction.class);

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // load fixed properties in Form elements
        FeedbackForm feedbackForm = (FeedbackForm) form;
        feedbackForm.setTimeDate(new Date().toString());

        // load EDEN User properties in Form elements
        feedbackForm.setComments("");

        // load application properties from Request in Form elements
        String documentType = request.getParameter("docType");
        if (documentType == null) {
            documentType = "";
        }
        feedbackForm.setDocumentType(documentType);

        String pageUrl = request.getParameter("pageUrl");
        if (pageUrl == null) {
            pageUrl = "";
        }
        feedbackForm.setPageUrl(pageUrl);

        String routeHeaderId = request.getParameter("routeHeaderId");
        if (routeHeaderId == null) {
            routeHeaderId = "";
        }
        feedbackForm.setRouteHeaderId(routeHeaderId);

        String exception = request.getParameter("exception");
        if (exception == null) {
            feedbackForm.setException("");
            feedbackForm.setEdenCategory("");
        } else {
            feedbackForm.setEdenCategory("problem");
            feedbackForm.setException(exception);
        }

        UserSession uSession = getUserSession(request);
        WorkflowUser workflowUser = uSession.getWorkflowUser();
        if (workflowUser != null) {
            feedbackForm.setNetworkId(workflowUser.getAuthenticationUserId().getAuthenticationId());
            feedbackForm.setUserEmail(workflowUser.getEmailAddress());
            String name = workflowUser.getDisplayName().trim();
            feedbackForm.setUserName(name);
            String firstName = name.substring(0, name.indexOf(" "));
            String lastName = name.substring(name.lastIndexOf(" ") + 1, name.length());
            feedbackForm.setFirstName(firstName);
            feedbackForm.setLastName(lastName);
        } else {
            feedbackForm.setNetworkId("");
            feedbackForm.setUserEmail("");
            feedbackForm.setUserName("");
            feedbackForm.setFirstName("");
            feedbackForm.setLastName("");
        }

        return mapping.findForward("start");
    }

    public ActionForward sendFeedback(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	FeedbackForm feedbackForm = (FeedbackForm)form;
    	EmailService emailService = KEWServiceLocator.getEmailService();
        EmailContentService emailContentService = KEWServiceLocator.getEmailContentService();
        String fromAddress = determineFromAddress(emailContentService, feedbackForm);
    	String toAddress = emailContentService.getApplicationEmailAddress();
        EmailContent content = emailContentService.generateFeedback(feedbackForm);
    	emailService.sendEmail(new EmailFrom(fromAddress), new EmailTo(toAddress), new EmailSubject(content.getSubject()), new EmailBody(content.getBody()), content.isHtml());
    	return mapping.findForward("sent");
    }

    private String determineFromAddress(EmailContentService emailContentService, FeedbackForm form) {
    	DocumentType docType = null;
    	if (!StringUtils.isEmpty(form.getDocumentType())) {
    		docType = KEWServiceLocator.getDocumentTypeService().findByName(form.getDocumentType());
    		if (docType == null) {
    			LOG.warn("Couldn't locate document type for the given name to determine feedback from address! " + form.getDocumentType());
    		}
    	}
    	// if we pass null to this method it will return us the application email address
    	return emailContentService.getDocumentTypeEmailAddress(docType);
    }

    private String constructSubject(FeedbackForm form) {
    	String subject = "Feedback from " + form.getNetworkId();
    	if (form.getRouteHeaderId() != null) {
    		subject += (" for document " + form.getRouteHeaderId());
    	}
    	return subject;
    }

    private String constructEmailBody(FeedbackForm form) {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("\n");
    	buffer.append("Network ID: " + form.getNetworkId()).append("\n");
    	buffer.append("Name: " + form.getUserName()).append("\n");
    	buffer.append("Email: " + form.getUserEmail()).append("\n");
    	buffer.append("Phone: " + form.getPhone()).append("\n");
    	buffer.append("Time: " + form.getTimeDate()).append("\n");
    	buffer.append("Environment: " + Core.getCurrentContextConfig().getEnvironment()).append("\n\n");

    	buffer.append("Document type: " + form.getDocumentType()).append("\n");
    	buffer.append("Document id: " + form.getRouteHeaderId()).append("\n\n");

    	buffer.append("Category: " + form.getEdenCategory()).append("\n");
    	buffer.append("Comments: \n" + form.getComments()).append("\n\n");

    	buffer.append("Exception: \n" + form.getException());
    	return buffer.toString();
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        return null;
    }

}

