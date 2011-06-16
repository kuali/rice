/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.ken.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.document.kew.NotificationWorkflowDocument;
import org.kuali.rice.ken.service.NotificationMessageContentService;
import org.kuali.rice.ken.service.NotificationWorkflowDocumentService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;



/**
 * This class is responsible for interacting with KEW - this is the default implementation that leverages the KEW client API.
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
     * @see org.kuali.rice.ken.service.NotificationWorkflowDocumentService#createAndAdHocRouteNotificationWorkflowDocument(org.kuali.rice.ken.bo.NotificationMessageDelivery, java.lang.String, java.lang.String, java.lang.String)
     */
    public String createAndAdHocRouteNotificationWorkflowDocument(NotificationMessageDelivery messageDelivery, String initiatorUserId,
	    String recipientUserId, String annotation) throws WorkflowException {
	// obtain a workflow user object first
	//WorkflowIdDTO initiator = new WorkflowIdDTO(initiatorUserId);

	// now construct the workflow document, which will interact with workflow
	NotificationWorkflowDocument document = NotificationWorkflowDocument.createNotificationDocument(initiatorUserId);

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

	// Clarification of ad hoc route call
	// param 1 - actionRequested will be either ACK or FYI
	// param 2 - annotation is whatever text we pass in to describe the transaction - this will be system generated
	// param 3 - recipient is the person who will receive this request
	// param 4 - this is the responsibilityParty (a.k.a the system that produced this request), so we'll put the producer name in there
	// param 5 - this is the "force action" requests - if set to true, this will be delivered to the recipients list regardless of
	//           whether the recipient has already taken action on this request; in our case, this doesn't really apply at this point in time,
	//           so we'll set to true just to be safe
	Principal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(recipientUserId);
	document.adHocRouteDocumentToPrincipal(actionRequested, annotation, principal.getPrincipalId(),
		messageDelivery.getNotification().getProducer().getName(), true);

	// now actually route it along its way
	document.routeDocument(annotation);

	return document.getDocumentId();
    }

    /**
     * This service method is implemented by constructing a NotificationWorkflowDocument using the pre-existing document Id
     * that is passed in.
     * @see org.kuali.rice.ken.service.NotificationWorkflowDocumentService#findNotificationWorkflowDocumentByDocumentId(java.lang.String, java.lang.String)
     */
    public NotificationWorkflowDocument getNotificationWorkflowDocumentByDocumentId(String initiatorUserId, String workflowDocumentId) throws WorkflowException {
	// construct the workflow id value object
	//WorkflowIdDTO initiator = new WorkflowIdDTO(initiatorUserId);


	// now return the actual document instance
	// this handles going out and getting the workflow document
	return NotificationWorkflowDocument.loadNotificationDocument(initiatorUserId, workflowDocumentId);
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationWorkflowDocumentService#clearAllFyisAndAcknowledgeNotificationWorkflowDocument(java.lang.String, org.kuali.rice.ken.document.kew.NotificationWorkflowDocument, java.lang.String)
     */
    public void clearAllFyisAndAcknowledgeNotificationWorkflowDocument(String initiatorUserId, NotificationWorkflowDocument workflowDocument, String annotation) throws WorkflowException {
	ActionRequestDTO[] reqs = workflowDocument.getActionRequests();
        for(int i = 0; i < reqs.length; i++) {
            LOG.info("Action Request[" + i + "] = " + reqs[i].getActionRequested());
            if(reqs[i].getActionRequested().equals(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ)) {
                workflowDocument.acknowledge(annotation);
            } else if(reqs[i].getActionRequested().equals(KEWConstants.ACTION_REQUEST_FYI_REQ)) {
                workflowDocument.logDocumentAction(annotation);
                workflowDocument.clearFYI();
            } else {
                throw new WorkflowException("Invalid notification action request in workflow document (" + workflowDocument.getDocumentId() + ") was encountered.  Should be either an acknowledge or fyi and was not.");
            }
        }
    }

    /**
     * @see org.kuali.rice.ken.service.NotificationWorkflowDocumentService#terminateWorkflowDocument(org.kuali.rice.kew.service.WorkflowDocument)
     */
    public void terminateWorkflowDocument(WorkflowDocument document) throws WorkflowException {
        document.superUserCancel("terminating document: documentId=" + document.getDocumentId() + ", appDocId=" + document.getAppDocId());
    }
}
