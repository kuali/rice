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

import org.kuali.notification.bo.NotificationResponse;
import org.kuali.notification.service.NotificationService;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.KEWXMLService;

/**
 * This class allows the NotificationService.sendNotification(XML) service 
 * to be invoked as a web service generically from the bus.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SendNotificationServiceKewXmlImpl implements KEWXMLService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
	.getLogger(SendNotificationServiceKewXmlImpl.class);
    
    private NotificationService notificationService;

    /**
     * Constructs a SendNotificationServiceKewXmlImpl instance.
     * @param notificationService
     */
    public SendNotificationServiceKewXmlImpl(NotificationService notificationService) {
	this.notificationService = notificationService;
    }

    /**
     * Actually invokes the sendNotification() service method.  The KSB calls 
     * this.
     * @see edu.iu.uis.eden.messaging.KEWXMLService#invoke(java.lang.String)
     */
    public void invoke(String xml) {
    	try {
    	   NotificationResponse response = notificationService.sendNotification(xml);
    	   LOG.info(response);
    	} catch (Exception e) {
    	    throw new WorkflowRuntimeException(e);
    	}
    }
}