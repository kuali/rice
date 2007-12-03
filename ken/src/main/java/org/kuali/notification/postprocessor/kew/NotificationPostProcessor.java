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
import org.kuali.notification.service.NotificationMessageDeliveryService;
import org.kuali.notification.service.NotificationService;
import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;

/**
 * This class is the post processor that gets run when workflow state changes occur for the 
 * underlying core NotificationDocumentType that all notifications go into KEW as.  This class is responsible for changing 
 * the state of the associated notification message delivery record after someone FYIs or ACKs their notification 
 * in the KEW Action List.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationPostProcessor implements PostProcessorRemote {
    private static final Logger LOG = Logger.getLogger(NotificationPostProcessor.class);
    
    NotificationService notificationService;
    NotificationMessageDeliveryService msgDeliverySvc;
    
    /**
     * Constructs a NotificationPostProcessor instance.
     */
    public NotificationPostProcessor() {
	this.msgDeliverySvc = GlobalNotificationServiceLocator.getInstance().getNotificationMessageDeliveryService();
	this.notificationService = GlobalNotificationServiceLocator.getInstance().getNotificationService();
    }

    /**
     * Need to intercept ACKNOWLEDGE or FYI actions taken on notification workflow documents and set the local state of the 
     * Notification to REMOVED as well.
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doActionTaken(edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO)
     */
    public boolean doActionTaken(ActionTakenEventVO event) throws RemoteException {
	LOG.debug("ENTERING NotificationPostProcessor.doActionTaken() for Notification action item with route header ID: " + event.getRouteHeaderId());
	
	// XXX: this action could be happening because the user initiated it via KEW, OR because a dismiss has been invoked
	// programmatically and the KEWActionListMessageDeliverer is taking an action...so there is a risk of dismiss being
	// invoked recursively and causing an exception (because the doc has already been transitioned).  We make the decision
	// of avoiding in the KEWActionListeMissageDeliverer, which should only attempt to take actions which are legal at
	// the time it is called

	LOG.debug("ACTION TAKEN=" + event.getActionTaken().getActionTaken());
	
	String actionTakenCode = event.getActionTaken().getActionTaken();
	
	if(actionTakenCode.equals(EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD) || actionTakenCode.equals(EdenConstants.ACTION_TAKEN_FYI_CD)) {
	    LOG.debug("User has taken either acknowledge or fy action (action code=" + actionTakenCode + 
		    ") for Notification action item with route header ID: " + event.getRouteHeaderId() + 
		    ".  We are now changing the status of the associated NotificationMessageDelivery to REMOVED.");
	    
	    try {
		NotificationMessageDelivery nmd = msgDeliverySvc.getNotificationMessageDeliveryByDelivererId(event.getRouteHeaderId());
		
		if (nmd == null) {
		    throw new RuntimeException("Could not find message delivery from workflow document " + event.getRouteHeaderId() + " to dismiss");
		}

		//get the id of the associated notification message delivery record
		String cause;
		if (EdenConstants.ACTION_TAKEN_ACKNOWLEDGED_CD.equals(actionTakenCode)) {
		    cause = NotificationConstants.ACK_CAUSE;
		} else if (EdenConstants.ACTION_TAKEN_FYI_CD.equals(actionTakenCode)) {
		    cause = NotificationConstants.FYI_CAUSE;
		} else {
		    cause = "unknown";
		}
		
		LOG.info("Dismissing message id " + nmd.getId() + " due to cause: " + cause);
		notificationService.dismissNotificationMessageDelivery(nmd.getId(),
		                                                       NotificationConstants.KEW_CONSTANTS.NOTIFICATION_SYSTEM_USER,
		                                                       cause);
	    } catch(Exception e) {
		throw new RuntimeException("Error dismissing message", e);
	    }
	}

	LOG.debug("LEAVING NotificationPostProcessor.doActionTaken() for Notification action item with route header ID: " + event.getRouteHeaderId());
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
