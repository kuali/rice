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

import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.kcb.service.MessagingService;
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
     * @see org.kuali.rice.kcb.service.MessagingService#deliver(org.kuali.rice.kcb.vo.MessageVO)
     */
    public long deliver(MessageVO message) {
        Message m = new Message();
        m.setTitle(message.getTitle());
        m.setDeliveryType(message.getDeliveryType());
        m.setRecipient(message.getRecipient());
        m.setContentType(message.getContentType());
        m.setContent(message.getContent());

        messageService.saveMessage(m);

        return m.getId();
    }

    /**
     * @see org.kuali.rice.kcb.service.MessagingService#remove(int)
     */
    public void remove(long messageId) {
    // TODO arh14 - THIS METHOD NEEDS JAVADOCS

    }
}
