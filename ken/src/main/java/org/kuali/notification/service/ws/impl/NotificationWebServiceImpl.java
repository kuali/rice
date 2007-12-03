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
package org.kuali.notification.service.ws.impl;

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kuali.notification.bo.NotificationResponse;
import org.kuali.notification.exception.InvalidXMLException;
import org.kuali.notification.service.NotificationMessageContentService;
import org.kuali.notification.service.NotificationService;
import org.kuali.notification.service.ws.NotificationWebService;
import org.kuali.notification.util.NotificationConstants;
import org.kuali.notification.util.PerformanceLog;
import org.kuali.notification.util.PerformanceLog.PerformanceStopWatch;

/**
 * Web service interface implementation that delegates directly to Spring service
 * Spring integration strategy taken from:
 * http://javaboutique.internet.com/tutorials/axisspring/
 * This class extends ServletEndpointSupport so that it can obtain the Notification System Spring context
 * from the ServletContext via getWebApplicationContext().  It then delegates to the NotificationService.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationWebServiceImpl /*extends ServletEndpointSupport*/ implements NotificationWebService {
    private static final Logger LOG = Logger.getLogger(NotificationWebServiceImpl.class);

    private NotificationService notificationService;
    private NotificationMessageContentService notificationMessageContentService;

    /**
     * Callback for custom initialization after the context has been set up.
     * @throws ServiceException if initialization failed
     */
    /*protected void onInit() throws ServiceException {
        // grab the spring application context from the webapp context so that we can look up our service in spring 
        NotificationServiceLocator locator = new SpringNotificationServiceLocator(getWebApplicationContext());
        this.notificationService = locator.getNotificationService();
        this.notificationMessageContentService = locator.getNotificationMessageContentService();
    }*/

    /**
     * Invokes the underlying Java API calls via Spring service invocation.
     * @see org.kuali.notification.service.ws.NotificationWebService#sendNotification(java.lang.String)
     */
    public String sendNotification(String notificationMessageAsXml) throws RemoteException {
        PerformanceStopWatch watch = PerformanceLog.startTimer("Time to send notification from web service");
        
        NotificationResponse response;
        try {
            response = notificationService.sendNotification(notificationMessageAsXml);

        } catch(IOException ioe) {
            response = new NotificationResponse();
            response.setStatus(NotificationConstants.RESPONSE_STATUSES.FAILURE);
            response.setMessage("Failed to process the message content: " + ioe.getMessage());
            LOG.error("Failed to process the message content", ioe);
        } catch(InvalidXMLException ixe) {
            response = new NotificationResponse();
            response.setStatus(NotificationConstants.RESPONSE_STATUSES.FAILURE);
            response.setMessage("Failed to process the message content because the XML message provided to the system was invalid: " + ixe.getMessage());
            LOG.error("Failed to process the message content because the XML message provided to the system was invalid", ixe);
        }

        String responseAsXml = notificationMessageContentService.generateNotificationResponseMessage(response);

        watch.recordDuration();
        
        LOG.info("Notification response: " + response);
        
        return responseAsXml;
    }
}