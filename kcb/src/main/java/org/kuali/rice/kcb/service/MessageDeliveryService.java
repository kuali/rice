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
package org.kuali.rice.kcb.service;

import java.util.Collection;

import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.bo.MessageDelivery;

/**
 * The MessageDeliveryService class is responsible various functions regarding the 
 * MessageDelivery records that exist within the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface MessageDeliveryService {
    /**
     * Saves a MessageDelivery
     * @param delivery the MessageDelivery to save
     */
    public void saveMessageDelivery(MessageDelivery delivery);

    /**
     * Deletes a MessageDelivery
     * @param delivery the MessageDelivery to delete 
     */
    public void deleteMessageDelivery(MessageDelivery delivery);

    /**
     * This method will retrieve a MessageDelivery object from the system, given the id of the 
     * actual record.
     * @param id
     * @return MessageDelivery
     */
    public MessageDelivery getMessageDelivery(Long id);

    /**
     * This method will retrieve a MessageDelivery object from the system, given the external deliverer system id
     * registered with the MessageDelivery.
     * @param id the external deliverer system id
     * @return MessageDelivery
     */
    public MessageDelivery getMessageDeliveryByDelivererSystemId(Long id);

    /**
     * This method will return all MessageDelivery objects in the system 
     * @return Collection<MessageDelivery> list of MessageDelivery objects in the system
     */
    public Collection<MessageDelivery> getAllMessageDeliveries();
    
    /**
     * This method will return all MessageDelievery objects generated for the given Message
     * @param message the message which generated the message deliveries
     * @return collection of NotificationMessageDelivery objects generated for the given Notification for the given user
     */
    public Collection<MessageDelivery> getMessageDeliveries(Message message);
}