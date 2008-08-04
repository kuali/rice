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
package org.kuali.notification.service.ws;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Web service interface to NotificationService - allows remote sending of notifications from other systems via Web Service interaction.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface NotificationWebService extends Remote {
    /**
     * This method allows consumers to send notification messages.  This particular service 
     * accepts the XML format of the notification and then marshals it out into the actual 
     * business object construct, for further processing.  The response is also sent back as 
     * a String of XML.
     * @param notificationMessageAsXml
     * @return String
     */
    public String sendNotification(String notificationMessageAsXml) throws RemoteException;
}