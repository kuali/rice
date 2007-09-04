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
package org.kuali.notification.postprocessor.kew;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.core.GlobalNotificationServiceLocator;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.document.kew.NotificationWorkflowDocument;
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;

/**
 * This class is the post processor that gets run when workflow state changes occur for the 
 * underlying core NotificationDocumentType that all notifications go into KEW as.  This class is responsible for changing 
 * the state of the associated notification message delivery record after someone FYIs or ACKs their notification 
 * in the KEW Action List.
 * @author Aaron Godert (ag266 at cornell dot edu)
 */
public class NotificationPostProcessor implements PostProcessorRemote {
    private static final Logger LOG = Logger.getLogger(NotificationPostProcessor.class);
    
    NotificationMessageDeliveryService msgDeliverySvc;
    BusinessObjectDao businessObjectDao;
    
    /**
     * Constructs a NotificationPostProcessor instance.
     */
    public NotificationPostProcessor() {
	this.msgDeliverySvc = GlobalNotificationServiceLocator.getInstance().getNotificationMessageDeliveryService();
	this.businessObjectDao = GlobalNotificationServiceLocator.getInstance().getBusinesObjectDao();
    }

    /**
     * Need to intercept ACKNOWLEDGE or FYI actions taken on notification workflow documents and set the local state of the 
     * Notification to REMOVED as well.
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doActionTaken(edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO)
     */
    public boolean doActionTaken(ActionTakenEventVO arg0) throws RemoteException {
	LOG.debug("ENTERING NotificationPostProcessor.doActionTaken() for Notification action item with route header ID: " + arg0.getRouteHeaderId());
	
	LOG.debug("ACTION TAKEN=" + arg0.getActionTaken().getActionTaken());
	
	String actionTakenCode = arg0.getActionTaken().getActionTaken();
	
	if(actionTakenCode.equals(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD) || actionTakenCode.equals(EdenConstants.ACTION_TAKEN_FYI_CD)) {
	    LOG.debug("User has taken either acknowledge or fy action (action code=" + actionTakenCode + 
		    ") for Notification action item with route header ID: " + arg0.getRouteHeaderId() + 
		    ".  We are now changing the status of the associated NotificationMessageDelivery to REMOVED.");
	    
	    // obtain a workflow user object first
	    WorkflowIdVO proxyUser = new WorkflowIdVO(NotificationConstants.KEW_CONSTANTS.NOTIFICATION_SYSTEM_USER);
	        
	    // now construct the workflow document, which will interact with workflow
	    NotificationWorkflowDocument document;
	    try {	
		document = new NotificationWorkflowDocument(proxyUser, arg0.getRouteHeaderId());
		
		//get the id of the associated notification message delivery record
                String notificationMsgDeliveryId = document.getAppDocId();
                NotificationMessageDelivery notificationMessageDelivery = msgDeliverySvc.getNotificationMessageDelivery(new Long(notificationMsgDeliveryId));

                // avoid conflicts during an auto remove and make sure it wasn't already auto removed
                // if we did this during an auto remove, then we'd not only be overwriting the AUTO_REMOVE status, but we'd also get an OJB optimistic lock exception
                // if we did this after an auto remove, then we'd be overwriting the appropriate AUTO_REMOVE status
                if(notificationMessageDelivery.getLockedDate() == null && !notificationMessageDelivery.getMessageDeliveryStatus().equals(NotificationConstants.MESSAGE_DELIVERY_STATUS.AUTO_REMOVED)) { 
                    //change the state to REMOVED and persist
                    assert(NotificationConstants.MESSAGE_DELIVERY_STATUS.REMOVED.equals(notificationMessageDelivery.getMessageDeliveryStatus())) : "MessageDelivery should already have been dismissed/removed by Notification System!";
                    //notificationMessageDelivery.setMessageDeliveryStatus(NotificationConstants.MESSAGE_DELIVERY_STATUS.REMOVED);
                    //businessObjectDao.save(notificationMessageDelivery);
                }
                
                LOG.debug("Status of NotificationMessageDelivery with ID of " + notificationMsgDeliveryId + " was set to 'REMOVED'.");
	    } catch(Exception e) {
		throw new RuntimeException(e);
	    }
	}

	LOG.debug("LEAVING NotificationPostProcessor.doActionTaken() for Notification action item with route header ID: " + arg0.getRouteHeaderId());
	return true;
    }

    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doDeleteRouteHeader(edu.iu.uis.eden.clientapp.vo.DeleteEventVO)
     */
    public boolean doDeleteRouteHeader(DeleteEventVO arg0) throws RemoteException {
	return true;
    }

    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    public boolean doRouteLevelChange(DocumentRouteLevelChangeVO arg0) throws RemoteException {
	return true;
    }

    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doRouteStatusChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeVO arg0) throws RemoteException {
	return true;
    }
}
