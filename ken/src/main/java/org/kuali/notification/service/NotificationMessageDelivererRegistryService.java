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
package org.kuali.notification.service;

import java.util.ArrayList;

import org.kuali.notification.bo.NotificationMessageDelivery;
import org.kuali.notification.deliverer.NotificationMessageDeliverer;

/**
 * This class is responsible for providing services for Notification Message Deliverers (delivery types)
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface NotificationMessageDelivererRegistryService {
    /**
     * This service method is responsible for retrieving all 
     * NotificationMessageDeliverer Types.  
     * This service is to be run periodically in a separate thread, as a daemon process.
     * @return ArrayList of Deliverer Classes
     */
    public ArrayList getAllDelivererTypes();

    /**
     * This method returns the associated deliverer class instance for the given NotificationMessageDelivery instance.
     * @param messageDelivery
     * @return NotificationMessageDeliverer or null if not found
     */
    public NotificationMessageDeliverer getDeliverer(NotificationMessageDelivery messageDelivery);
    
    /**
     * This method returns the associated deliverer class instance for the given deliverer name.
     * @param messageDelivererName
     * @return NotificationMessageDeliverer or null if not found
     */
    public NotificationMessageDeliverer getDelivererByName(String messageDelivererName);
}
