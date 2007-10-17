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


/**
 * This class is responsible for the job that will actually deliver a notification message to its recipients.  It's responsible 
 * for loading and processing the proper NotificationMessageDelivery records.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface NotificationMessageDeliveryDispatchService {
    /**
     * This service method is responsible for retrieving all undelivered notifications that have sendDateTimes either equal to or before the 
     * current time, and delivering them.  This service is to be run periodically in a separate thread, as a daemon process.
     * @throw ErrorList
     */
    public ProcessingResult processUndeliveredNotificationMessageDeliveries();
}
