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
package org.kuali.rice.kcb.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.deliverer.MessageDeliverer;
import org.kuali.rice.kcb.exception.MessageDeliveryException;
import org.kuali.rice.kcb.exception.MessageDismissalException;
import org.kuali.rice.kcb.service.MessageDelivererRegistryService;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.kcb.service.MessagingService;
import org.kuali.rice.kcb.service.RecipientPreferenceService;
import org.kuali.rice.kcb.vo.MessageVO;
import org.springframework.beans.factory.annotation.Required;

/**
 * MessagingService implementation 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MessagingServiceImpl implements MessagingService {
    private MessageService messageService;
    private MessageDeliveryService messageDeliveryService;
    private MessageDelivererRegistryService delivererRegistry;
    private RecipientPreferenceService recipientPrefs;
    
    /**
     * Sets the MessageService
     * @param messageService the MessageService
     */
    @Required
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Sets the MessageDeliveryService
     * @param messageDeliveryService the MessageDeliveryService
     */
    @Required
    public void setMessageDeliveryService(MessageDeliveryService messageDeliveryService) {
        this.messageDeliveryService = messageDeliveryService;
    }

    /**
     * Sets the MessageDelivererRegistryService
     * @param registry the MessageDelivererRegistryService
     */
    @Required
    public void setMessageDelivererRegistryService(MessageDelivererRegistryService registry) {
        this.delivererRegistry = registry;
    }

    /**
     * Sets the RecipientPreferencesService
     * @param prefs the RecipientPreferenceService
     */
    public void setRecipientPreferenceService(RecipientPreferenceService prefs) {
        this.recipientPrefs = prefs;
    }

    /**
     * @see org.kuali.rice.kcb.service.MessagingService#deliver(org.kuali.rice.kcb.vo.MessageVO)
     */
    public long deliver(MessageVO message) throws MessageDeliveryException {
        Message m = new Message();
        m.setTitle(message.getTitle());
        m.setDeliveryType(message.getDeliveryType());
        m.setRecipient(message.getRecipient());
        m.setContentType(message.getContentType());
        m.setContent(message.getContent());

        messageService.saveMessage(m);

        Set<String> delivererTypes = getDelivererTypesForUserAndChannel(m.getRecipient(), m.getChannel());
        for (String type: delivererTypes) {
            
            MessageDelivery delivery = new MessageDelivery();
            delivery.setDelivererTypeName(type);
            delivery.setDeliveryStatus("SeNt");
            delivery.setMessage(m);

            MessageDeliverer deliverer = delivererRegistry.getDeliverer(delivery);
            if (deliverer != null) {
                deliverer.deliverMessage(delivery);
            }
            
            messageDeliveryService.saveMessageDelivery(delivery);
        }
       
        return m.getId();
    }

    /**
     * @see org.kuali.rice.kcb.service.MessagingService#remove(int)
     */
    public void remove(long messageId) throws MessageDismissalException {
        Message m = messageService.getMessage(Long.valueOf(messageId));
        if (m == null) {
            throw new MessageDismissalException("No such message: " + messageId);
        }
        
        Collection<MessageDelivery> deliveries = messageDeliveryService.getMessageDeliveries(m);
        for (MessageDelivery delivery: deliveries) {
            delivery.setDeliveryStatus("DeLeTeD");

            MessageDeliverer deliverer = delivererRegistry.getDeliverer(delivery);
            if (deliverer != null) {
                deliverer.dismissMessageDelivery(delivery, "nobody", "no reason");
            }

            messageDeliveryService.deleteMessageDelivery(delivery);
        }
        
        messageService.deleteMessage(m);
    }

    /**
     * Determines what delivery endpoints the user has configured
     * @param userRecipientId the user
     * @return a Set of NotificationConstants.MESSAGE_DELIVERY_TYPES
     */
    private Set<String> getDelivererTypesForUserAndChannel(String userRecipientId, String channel) {
        Set<String> deliveryTypes = new HashSet<String>(1);
        
        // manually add the default one since they don't have an option on this one
        //deliveryTypes.add(NotificationConstants.MESSAGE_DELIVERY_TYPES.DEFAULT_MESSAGE_DELIVERY_TYPE);
        
        //now look for what they've configured for themselves
        Collection<String> delivererTypes= recipientPrefs.getDeliverersForRecipientAndChannel(userRecipientId, channel);
        
        // and add each config's name to the list that gets passed out, by which messages will be sent to
        deliveryTypes.addAll(delivererTypes);

        return deliveryTypes;
    }
}