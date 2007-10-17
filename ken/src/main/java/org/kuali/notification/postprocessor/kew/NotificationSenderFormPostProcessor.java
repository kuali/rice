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
import org.kuali.notification.bo.Notification;
import org.kuali.notification.core.GlobalNotificationServiceLocator;
import org.kuali.notification.dao.BusinessObjectDao;
import org.kuali.notification.document.kew.NotificationWorkflowDocument;
import org.kuali.notification.service.NotificationMessageContentService;
import org.kuali.notification.service.NotificationService;
import org.kuali.notification.util.NotificationConstants;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;

/**
 * This class is the post processor that gets run when the general notification 
 * message sending form is approved by its reviewers.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationSenderFormPostProcessor implements PostProcessorRemote {
    private static final Logger LOG = Logger.getLogger(NotificationSenderFormPostProcessor.class);
    
    NotificationService notificationService;
    BusinessObjectDao businessObjectDao;
    NotificationMessageContentService messageContentService;
    
    /**
     * Constructs a NotificationSenderFormPostProcessor instance.
     */
    public NotificationSenderFormPostProcessor() {
	this.notificationService = GlobalNotificationServiceLocator.getInstance().getNotificationService();
	this.businessObjectDao = GlobalNotificationServiceLocator.getInstance().getBusinesObjectDao();
	this.messageContentService = GlobalNotificationServiceLocator.getInstance().getNotificationMessageContentService();
    }

    /**
     * Constructs a NotificationSenderFormPostProcessor instance.
     * @param notificationService
     * @param businessObjectDao
     */
    public NotificationSenderFormPostProcessor(NotificationService notificationService, BusinessObjectDao businessObjectDao) {
	this.notificationService = notificationService;
	this.businessObjectDao = businessObjectDao;
    }
    
    /**
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doActionTaken(edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO)
     */
    public boolean doActionTaken(ActionTakenEventVO arg0) throws RemoteException {
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
     * When the EDL simple message sending form is submitted, it is routed straight to FINAL and at that time (when RESOLVED), we 
     * actually send the notification.
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doRouteStatusChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeVO arg0) throws RemoteException {
	LOG.debug("ENTERING NotificationSenderFormPostProcessor.doRouteStatusChange() for Notification Sender Form with route header ID: " + arg0.getRouteHeaderId());
	
	if(arg0.getNewRouteStatus().equals(EdenConstants.ROUTE_HEADER_PROCESSED_CD)) {
	    LOG.debug("Workflow status has changed to RESOLVED for Notification Sender Form with route header ID: " + arg0.getRouteHeaderId() + 
		    ".  We are now calling the NotificationService.sendNotification() service.");
	    
	    // obtain a workflow user object first
	    WorkflowIdVO proxyUser = new WorkflowIdVO(NotificationConstants.KEW_CONSTANTS.NOTIFICATION_SYSTEM_USER);
	        
	    // now construct the workflow document, which will interact with workflow
	    NotificationWorkflowDocument document;
	    try {	
		document = new NotificationWorkflowDocument(proxyUser, arg0.getRouteHeaderId());
		
		LOG.debug("XML:" + document.getApplicationContent());
		
		//parse out the application content into a Notification BO
                Notification notification = messageContentService.parseSerializedNotificationXml(document.getApplicationContent().getBytes());
                
                LOG.debug("Notification Content: " + notification.getContent());
                
                // send the notification
                notificationService.sendNotification(notification);
                
                LOG.debug("NotificationService.sendNotification() was successfully called for Notification Sender Form with route header ID: " + arg0.getRouteHeaderId());
	    } catch(Exception e) {
		throw new RuntimeException(e);
	    }
	}
	
	LOG.debug("LEAVING NotificationSenderFormPostProcessor.doRouteStatusChange() for Notification Sender Form with route header ID: " + arg0.getRouteHeaderId());
	return true;
    }
}
