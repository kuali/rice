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
package org.kuali.notification.deliverer.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.core.GlobalNotificationServiceLocator;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;
import org.kuali.notification.document.kew.NotificationWorkflowDocument;
import org.kuali.notification.exception.ErrorList;
import org.kuali.notification.exception.NotificationAutoRemoveException;
import org.kuali.notification.exception.NotificationMessageDeliveryException;
import org.kuali.notification.service.NotificationWorkflowDocumentService;
import org.kuali.notification.service.impl.NotificationMessageDeliveryDispatchServiceImpl;
import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is responsible for describing the default delivery mechanism for
 * the system - the KEW Action List.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KEWActionListMessageDeliverer implements NotificationMessageDeliverer {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(NotificationMessageDeliveryDispatchServiceImpl.class);

    private NotificationWorkflowDocumentService notificationWorkflowDocumentService;

    /**
     * Constructs a KEWActionListMessageDeliverer.java.
     */
    public KEWActionListMessageDeliverer() {
	this.notificationWorkflowDocumentService = GlobalNotificationServiceLocator.getInstance().getNotificationWorkflowDocumentService();
    }

    /**
     * This implementation leverages the workflow integration services to push this notification into the KEW action list.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#deliverMessage(org.kuali.notification.bo.NotificationMessageDelivery)
     */
    public void deliverMessage(NotificationMessageDelivery messageDelivery)
	    throws NotificationMessageDeliveryException {
	try {
	    // make the call to actually generate and ad-hoc route a workflow document
	    Long workflowDocId = notificationWorkflowDocumentService.createAndAdHocRouteNotificationWorkflowDocument(
			    	messageDelivery,
			    	NotificationConstants.KEW_CONSTANTS.NOTIFICATION_SYSTEM_USER,
			    	messageDelivery.getUserRecipientId(),
			    	NotificationConstants.KEW_CONSTANTS.GENERIC_DELIVERY_ANNOTATION);
	    
	    // now prepare and set the workflow doc id into the message delivery's delivery system id
	    String deliverySystemId = null;
	    if(workflowDocId != null) {
		deliverySystemId = workflowDocId.toString();
	    }
	    messageDelivery.setDeliverySystemId(deliverySystemId);
	    LOG.debug("Message Delivery: " + messageDelivery.toString());
	} catch (WorkflowException we) {
	    LOG.error(we.getStackTrace());
	    throw new NotificationMessageDeliveryException("Workflow exception delivering message", we);
	}
    }
    
    /**
     * This implementation does an auto-remove by "canceling" the workflow document associated with the message delivery record.  This 
     * prevents the user from seeing the item in their list anymore.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#autoRemoveMessageDelivery(org.kuali.notification.bo.NotificationMessageDelivery)
     */
    public void autoRemoveMessageDelivery(NotificationMessageDelivery messageDelivery) throws NotificationAutoRemoveException {
	// first retrieve the appropriate notification workflow document to "auto-remove" and proxy as the recipient
	NotificationWorkflowDocument workflowDoc = null;
	try {
	    workflowDoc = notificationWorkflowDocumentService.getNotificationWorkflowDocumentByDocumentId(messageDelivery.getUserRecipientId(), new Long(messageDelivery.getDeliverySystemId()));
	} catch(WorkflowException we) {
	    throw new NotificationAutoRemoveException(we);
	}
	
        try {
            notificationWorkflowDocumentService.clearAllFyisAndAcknowledgeNotificationWorkflowDocument(messageDelivery.getUserRecipientId(), workflowDoc, 
        	    NotificationConstants.KEW_CONSTANTS.GENERIC_AUTO_REMOVE_ANNOTATION);
        } catch(WorkflowException we) {
    		throw new NotificationAutoRemoveException(we);
        }
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#dismissMessageDelivery(org.kuali.notification.bo.NotificationMessageDelivery, java.lang.String, java.lang.String)
     */
    public void dismissMessageDelivery(NotificationMessageDelivery messageDelivery, String user, String cause) {
        // TODO: move hardcoded web controller actions here...
        LOG.info("Dismissing as user '" + user + "' workflow document '" + messageDelivery.getDeliverySystemId() + "' corresponding to message delivery #" + messageDelivery.getId() + " due to cause: " + cause);
        if (NotificationConstants.AUTO_REMOVE_CAUSE.equals(cause)) {
            // perform an auto-remove
        } else if (NotificationConstants.ACK_CAUSE.equals(cause)) {
            // moved from NotificationController, ack command
            /*
             * acknowledge using workflow docId
             */
            NotificationWorkflowDocument nwd;
            try {
                nwd = notificationWorkflowDocumentService.getNotificationWorkflowDocumentByDocumentId(user, Long.decode(messageDelivery.getDeliverySystemId()));
                if (nwd.isAcknowledgeRequested()) {
                    nwd.acknowledge(new String("This notification has been acknowledged."));
                    LOG.debug("acknowledged "+nwd.getTitle());                      
                    LOG.debug("status display value: "+nwd.getStatusDisplayValue());
                } else {
                    LOG.debug("Acknowledgement was not needed for document " + nwd.getRouteHeaderId());
                }
            } catch (WorkflowException we) {
                LOG.error("Could not get workflow document with docId");
                throw new RuntimeException(we);
            }
        } else if (NotificationConstants.FYI_CAUSE.equals(cause)) {
            // moved from NotificationController, fyi command
            /*
             * FYI using workflow docId
             */
            NotificationWorkflowDocument nwd;
            try {
                nwd = notificationWorkflowDocumentService.getNotificationWorkflowDocumentByDocumentId(user, Long.decode(messageDelivery.getDeliverySystemId()));
                if (nwd.isFYIRequested()) {
                    nwd.fyi();
                    LOG.debug("fyi "+nwd.getTitle());                      
                    LOG.debug("status display value: "+nwd.getStatusDisplayValue());
                } else {
                    LOG.debug("FYI was not needed for document " + nwd.getRouteHeaderId());
                }
            } catch (WorkflowException we) {
                LOG.error("Could not get workflow document with docId");
                throw new RuntimeException(we);
            }
        }
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getDescription()
     */
    public String getDescription() {
	return "The default message delivery type is the Action List.";
    }

    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getName()
     */
    public String getName() {
	return "KEWActionList";
    }
    
    /**
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getTitle()
     */
    public String getTitle() {
	return "KEW Action List";
    }

    /**
     * This implementation returns an empty String array b/c this deliverer doesn't make use of this method.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#getPreferenceKeys()
     */
    public LinkedHashMap<String, String> getPreferenceKeys() {
	return new LinkedHashMap<String, String>();
    }

    /**
     * This method does nothing in this implementation b/c this deliverer impl doesn't make use of this feature.
     * @see org.kuali.notification.deliverer.NotificationMessageDeliverer#validatePreferenceValues()
     */
    public void validatePreferenceValues(HashMap prefs) throws ErrorList {
    }
}