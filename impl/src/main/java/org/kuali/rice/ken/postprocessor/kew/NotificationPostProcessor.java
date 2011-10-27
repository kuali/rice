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
package org.kuali.rice.ken.postprocessor.kew;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.core.GlobalNotificationServiceLocator;
import org.kuali.rice.ken.deliverer.impl.KEWActionListMessageDeliverer;
import org.kuali.rice.ken.service.NotificationMessageDeliveryService;
import org.kuali.rice.ken.service.NotificationService;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.ken.util.Util;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.AfterProcessEventDTO;
import org.kuali.rice.kew.dto.BeforeProcessEventDTO;
import org.kuali.rice.kew.dto.DeleteEventDTO;
import org.kuali.rice.kew.dto.DocumentLockingEventDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.postprocessor.PostProcessorRemote;
import org.kuali.rice.kew.api.KewApiConstants;


/**
 * This class is the post processor that gets run when workflow state changes occur for the 
 * underlying core NotificationDocumentType that all notifications go into KEW as.  This class is responsible for changing 
 * the state of the associated notification message delivery record after someone FYIs or ACKs their notification 
 * in the KEW Action List.
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#doActionTaken(org.kuali.rice.kew.dto.ActionTakenEventDTO)
     */
    public boolean doActionTaken(ActionTakenEventDTO event) throws RemoteException {
        LOG.debug("ENTERING NotificationPostProcessor.doActionTaken() for Notification action item with document ID: " + event.getDocumentId());

        // NOTE: this action could be happening because the user initiated it via KEW, OR because a dismiss or autoremove action
        // has been invoked programmatically and the KEWActionListMessageDeliverer is taking an action...so there is a risk of being
        // invoked recursively (which will lead to locking issues and other problems).  We therefore mark the document in the KEWActionList
        // MessageDeliverer before performing an action, so that we can detect this scenario here, and avoid invoking KEN again.

        LOG.debug("ACTION TAKEN=" + event.getActionTaken().getActionTaken());

        String actionTakenCode = event.getActionTaken().getActionTaken().getCode();

        Properties p = new Properties();
        WorkflowDocument doc = WorkflowDocumentFactory.loadDocument(event.getActionTaken().getPrincipalId(), event.getDocumentId());
        try {
            p.load(new ByteArrayInputStream(doc.getAttributeContent().getBytes()));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        String internalCommand = p.getProperty(KEWActionListMessageDeliverer.INTERNAL_COMMAND_FLAG);

        if (Boolean.valueOf(internalCommand)) {
            LOG.info("Internal command detected by NotificationPostProcessor - will not invoke KEN");
            return true;
        }
        
        LOG.info("NotificationPostProcessor detected end-user action " + event.getActionTaken().getActionTaken() + " on document " + event.getActionTaken().getDocumentId());

        if(actionTakenCode.equals(KewApiConstants.ACTION_TAKEN_ACKNOWLEDGED_CD) || actionTakenCode.equals(KewApiConstants.ACTION_TAKEN_FYI_CD)) {
            LOG.debug("User has taken either acknowledge or fy action (action code=" + actionTakenCode + 
                    ") for Notification action item with document ID: " + event.getDocumentId() + 
            ".  We are now changing the status of the associated NotificationMessageDelivery to REMOVED.");

            try {
                NotificationMessageDelivery nmd = msgDeliverySvc.getNotificationMessageDeliveryByDelivererId(event.getDocumentId());

                if (nmd == null) {
                    throw new RuntimeException("Could not find message delivery from workflow document " + event.getDocumentId() + " to dismiss");
                }

                //get the id of the associated notification message delivery record
                String cause;
                if (KewApiConstants.ACTION_TAKEN_ACKNOWLEDGED_CD.equals(actionTakenCode)) {
                    cause = NotificationConstants.ACK_CAUSE;
                } else if (KewApiConstants.ACTION_TAKEN_FYI_CD.equals(actionTakenCode)) {
                    cause = NotificationConstants.FYI_CAUSE;
                } else {
                    cause = "unknown";
                }

                LOG.info("Dismissing message id " + nmd.getId() + " due to cause: " + cause);
                notificationService.dismissNotificationMessageDelivery(nmd.getId(),
                        Util.getNotificationSystemUser(),
                        cause);
            } catch(Exception e) {
                throw new RuntimeException("Error dismissing message", e);
            }
        }

        LOG.debug("LEAVING NotificationPostProcessor.doActionTaken() for Notification action item with document ID: " + event.getDocumentId());
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
     * @see org.kuali.rice.kew.postprocessor.PostProcessorRemote#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeDTO arg0) throws RemoteException {
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
	public String[] getDocumentIdsToLock(DocumentLockingEventDTO documentLockingEvent) throws Exception {
		return null;
	}
    
    
    
    
}
