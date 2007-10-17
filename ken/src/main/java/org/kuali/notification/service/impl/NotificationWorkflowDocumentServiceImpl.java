/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.notification.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.document.kew.NotificationWorkflowDocument;
import org.kuali.notification.service.NotificationMessageContentService;
import org.kuali.notification.service.NotificationWorkflowDocumentService;
import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is responsible for interacting with KEW - this is the default implementation that leverages the KEW client API.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationWorkflowDocumentServiceImpl implements NotificationWorkflowDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(NotificationWorkflowDocumentServiceImpl.class);
    
    private NotificationMessageContentService messageContentService;
    
    /**
     * Constructs a NotificationWorkflowDocumentServiceImpl instance.
     * @param messageContentService
     */
    public NotificationWorkflowDocumentServiceImpl(NotificationMessageContentService messageContentService) {
	this.messageContentService = messageContentService;
    }
    
    /**
     * Implements by instantiating a NotificationWorkflowDocument, which in turn interacts with Workflow to set it up with an initiator of the 
     * passed in user id.
     * @see org.kuali.notification.service.NotificationWorkflowDocumentService#createAndAdHocRouteNotificationWorkflowDocument(org.kuali.notification.bo.NotificationMessageDelivery, java.lang.String, java.lang.String, java.lang.String)
     */
    public Long createAndAdHocRouteNotificationWorkflowDocument(NotificationMessageDelivery messageDelivery, String initiatorUserId, 
	    String recipientUserId, String annotation) throws WorkflowException {
	// obtain a workflow user object first
	WorkflowIdVO initiator = new WorkflowIdVO(initiatorUserId);
        
	// now construct the workflow document, which will interact with workflow
	NotificationWorkflowDocument document = new NotificationWorkflowDocument(initiator);
	
	// this is our loose foreign key to our message delivery record in notification
	document.getRouteHeader().setAppDocId(messageDelivery.getId().toString());
	//document.setAppDocId(messageDelivery.getId().toString());
	
	// now add the content of the notification as XML to the document
	document.setApplicationContent(messageContentService.generateNotificationMessage(messageDelivery.getNotification(), messageDelivery.getUserRecipientId()));

        if (!StringUtils.isBlank(messageDelivery.getNotification().getTitle())) {
            document.setTitle(messageDelivery.getNotification().getTitle());
        } else {
            LOG.error("Encountered notification with no title set: Message Delivery #" + messageDelivery.getId() + ", Notification #" + messageDelivery.getNotification().getId());
        }
	
	// now set up the ad hoc route
	String actionRequested;
	if(NotificationConstants.DELIVERY_TYPES.ACK.equals(messageDelivery.getNotification().getDeliveryType())) {
	    actionRequested = NotificationConstants.KEW_CONSTANTS.ACK_AD_HOC_ROUTE;
	} else {
	    actionRequested = NotificationConstants.KEW_CONSTANTS.FYI_AD_HOC_ROUTE;
	}
	
	// construct the recipient object in KEW terms
	NetworkIdVO recipient = new NetworkIdVO(recipientUserId);
	
	// Clarification of ad hoc route call
	// param 1 - actionRequested will be either ACK or FYI
	// param 2 - annotation is whatever text we pass in to describe the transaction - this will be system generated
	// param 3 - recipient is the person who will receive this request
	// param 4 - this is the responsibilityParty (a.k.a the system that produced this request), so we'll put the producer name in there
	// param 5 - this is the "ignore previous" requests - if set to true, this will be delivered to the recipients list regardless of 
	//           whether the recipient has already taken action on this request; in our case, this doesn't really apply at this point in time, 
	//           so we'll set to true just to be safe
	document.appSpecificRouteDocumentToUser(actionRequested, annotation, recipient, 
		messageDelivery.getNotification().getProducer().getName(), true);
	
	// now actually route it along its way
	document.routeDocument(annotation);
	
	return document.getRouteHeaderId();
    }

    /**
     * This service method is implemented by constructing a NotificationWorkflowDocument using the pre-existing document Id 
     * that is passed in.
     * @see org.kuali.notification.service.NotificationWorkflowDocumentService#findNotificationWorkflowDocumentByDocumentId(java.lang.String, java.lang.Long)
     */
    public NotificationWorkflowDocument getNotificationWorkflowDocumentByDocumentId(String initiatorUserId, Long workflowDocumentId) throws WorkflowException {
	// construct the workflow id value object
	WorkflowIdVO initiator = new WorkflowIdVO(initiatorUserId);
	
	// now return the actual document instance
	// this handles going out and getting the workflow document
	return new NotificationWorkflowDocument(initiator, workflowDocumentId);
    }

    /**
     * @see org.kuali.notification.service.NotificationWorkflowDocumentService#clearAllFyisAndAcknowledgeNotificationWorkflowDocument(java.lang.String, org.kuali.notification.document.kew.NotificationWorkflowDocument, java.lang.String)
     */
    public void clearAllFyisAndAcknowledgeNotificationWorkflowDocument(String initiatorUserId, NotificationWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
	ActionRequestVO[] reqs = workflowDocument.getActionRequests();
        for(int i = 0; i < reqs.length; i++) {
            LOG.info("Action Request[" + i + "] = " + reqs[i].getActionRequested());
            if(reqs[i].getActionRequested().equals(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ)) {
                workflowDocument.acknowledge(annotation);
            } else if(reqs[i].getActionRequested().equals(EdenConstants.ACTION_REQUEST_FYI_REQ)) {
                workflowDocument.logDocumentAction(annotation);
                workflowDocument.clearFYI();
            } else {
                throw new WorkflowException("Invalid notification action request in workflow document (" + workflowDocument.getRouteHeaderId().toString() + ") was encountered.  Should be either an acknowledge or fyi and was not.");
            }
        }
    }
    
    /**
     * @see org.kuali.notification.service.NotificationWorkflowDocumentService#terminateWorkflowDocument(edu.iu.uis.eden.clientapp.WorkflowDocument)
     */
    public void terminateWorkflowDocument(WorkflowDocument document) throws WorkflowException {
        document.superUserCancel("terminating document: routeHeaderId=" + document.getRouteHeaderId() + ", appDocId=" + document.getAppDocId());
    }
}