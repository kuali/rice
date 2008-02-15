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
package org.kuali.rice.kcb.services.impl;

import java.util.Collection;

import org.junit.Test;
import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.service.MessagingService;
import org.kuali.rice.kcb.test.KCBTestCase;
import org.kuali.rice.kcb.vo.MessageVO;

/**
 * Tests MessagingService 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class MessagingServiceTest extends KCBTestCase {
    private MessagingService messagingService;
    private MessageDeliveryService messageDeliveryService;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    
        messagingService = GlobalKCBServiceLocator.getInstance().getMessagingService();
        messageDeliveryService = GlobalKCBServiceLocator.getInstance().getMessageDeliveryService();
    }

    protected long deliver() throws Exception {
        MessageVO message = new MessageVO();
        message.setContent("test content 1");
        message.setContentType("test content type 1");
        message.setDeliveryType("test delivery type 1");
        message.setRecipient("test recipient 1");
        message.setTitle("test title 1");

        long id = messagingService.deliver(message);

        Collection<MessageDelivery> deliveries = messageDeliveryService.getAllMessageDeliveries();
        assertNotNull(deliveries);
        // HACK: for now our impl just creates deliveries for all deliverer types because we don't have preferences yet
        // so there should be exactly as many deliveries as deliverer types
        assertEquals(GlobalKCBServiceLocator.getInstance().getMessageDelivererRegistryService().getAllDelivererTypes().size(),
                     deliveries.size());
        
        return id;
    }

    @Test
    public void testDeliver() throws Exception {
        deliver();
    }
    
    @Test
    public void testDismiss() throws Exception {
        long id = deliver();

        messagingService.remove(id);

        Collection<MessageDelivery> deliveries = messageDeliveryService.getAllMessageDeliveries();
        assertNotNull(deliveries);
        // should be all gone
        assertEquals(0, deliveries.size());
    }
}