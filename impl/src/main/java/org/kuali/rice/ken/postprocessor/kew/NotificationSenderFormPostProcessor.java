/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.ken.postprocessor.kew;

import org.apache.log4j.Logger;
import org.kuali.rice.core.framework.dao.GenericDao;
import org.kuali.rice.ken.bo.Notification;
import org.kuali.rice.ken.core.GlobalNotificationServiceLocator;
import org.kuali.rice.ken.document.kew.NotificationWorkflowDocument;
import org.kuali.rice.ken.service.NotificationMessageContentService;
import org.kuali.rice.ken.service.NotificationService;
import org.kuali.rice.ken.util.Util;
import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.AfterProcessEventDTO;
import org.kuali.rice.kew.dto.BeforeProcessEventDTO;
import org.kuali.rice.kew.dto.DeleteEventDTO;
import org.kuali.rice.kew.dto.DocumentLockingEventDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.postprocessor.PostProcessorRemote;
import org.kuali.rice.kew.util.KEWConstants;

import java.rmi.RemoteException;


/**
 * This class is the post processor that gets run when the general notification 
 * message sending form is approved by its reviewers.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class NotificationSenderFormPostProcessor implements PostProcessorRemote {
    private static final Logger LOG = Logger.getLogger(NotificationSenderFormPostProcessor.class);
    
    NotificationService notificationService;
    GenericDao businessObjectDao;
    NotificationMessageContentService messageContentService;
    
    /**
     * Constructs a NotificationSenderFormPostProcessor instance.
     */
    public NotificationSenderFormPostProcessor() {
	this.notificationService = GlobalNotificationServiceLocator.getInstance().getNotificationService();
	this.businessObjectDao = GlobalNotificationServiceLocator.getInstance().getGenericDao();
	this.messageContentService = GlobalNotificationServiceLocator.getInstance().getNotificationMessageContentService();
    }

    /**
     * Constructs a NotificationSenderFormPostProcessor instance.
     * @param notificationService
     * @param businessObjectDao
     */
    public NotificationSenderFormPostProcessor(NotificationService notificationService, GenericDao businessObjectDao) {
	this.notificationService = notificationService;
	this.businessObjectDao = businessObjectDao;
    }
    
    /**
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#doActionTaken(org.kuali.rice.kew.dto.ActionTakenEventDTO)
     */
    public boolean doActionTaken(ActionTakenEventDTO arg0) throws RemoteException {
	return true;
    }

    /**
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#doDeleteRouteHeader(org.kuali.rice.kew.dto.DeleteEventDTO)
     */
    public boolean doDeleteRouteHeader(DeleteEventDTO arg0) throws RemoteException {
	return true;
    }

    /**
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#doRouteLevelChange(org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO)
     */
    public boolean doRouteLevelChange(DocumentRouteLevelChangeDTO arg0) throws RemoteException {
	return true;
    }

    /**
     * When the EDL simple message sending form is submitted, it is routed straight to FINAL and at that time (when RESOLVED), we 
     * actually send the notification.
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeDTO arg0) throws RemoteException {
	LOG.debug("ENTERING NotificationSenderFormPostProcessor.doRouteStatusChange() for Notification Sender Form with route header ID: " + arg0.getRouteHeaderId());
	
	if(arg0.getNewRouteStatus().equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD)) {
	    LOG.debug("Workflow status has changed to RESOLVED for Notification Sender Form with route header ID: " + arg0.getRouteHeaderId() + 
		    ".  We are now calling the NotificationService.sendNotification() service.");
	    
	    // obtain a workflow user object first
	    NetworkIdDTO proxyUser = new NetworkIdDTO(Util.getNotificationSystemUser());
	        
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

    /**
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#beforeProcess(org.kuali.rice.kew.dto.BeforeProcessEventDTO)
     */
    public boolean beforeProcess(BeforeProcessEventDTO beforeProcessEvent) throws Exception {
        return true;
    }

    /**
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#afterProcess(org.kuali.rice.kew.dto.AfterProcessEventDTO)
     */
    public boolean afterProcess(AfterProcessEventDTO afterProcessEvent) throws Exception {
        return true;
    }
    
    /**
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#getDocumentIdsToLock(org.kuali.rice.kew.dto.DocumentLockingEventDTO)
     */
	public Long[] getDocumentIdsToLock(DocumentLockingEventDTO documentLockingEvent) throws Exception {
		return null;
	}
	
}
