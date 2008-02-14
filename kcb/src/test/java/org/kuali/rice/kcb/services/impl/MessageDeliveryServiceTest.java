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
import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.bo.MessageDelivery;
import org.kuali.rice.kcb.service.MessageDeliveryService;
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.kcb.test.BusinessObjectTestCase;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.AssertThrows;


/**
 * Tests MessageDeliveryService
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MessageDeliveryServiceTest extends BusinessObjectTestCase {
    private MessageService messageService;
    private MessageDeliveryService messageDeliveryService;
    private Message message;
    private MessageDelivery messageDelivery;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    
        messageService = GlobalKCBServiceLocator.getInstance().getMessageService();
        messageDeliveryService = GlobalKCBServiceLocator.getInstance().getMessageDeliveryService();

        message = new Message();
        message.setContent("test content 1");
        message.setContentType("test content type 1");
        message.setDeliveryType("test delivery type 1");
        message.setRecipient("test recipient 1");
        message.setTitle("test title 1");

        messageService.saveMessage(message);
        
        messageDelivery = new MessageDelivery();
        messageDelivery.setDelivererTypeName("email");
        messageDelivery.setDeliveryStatus("abcd");
        messageDelivery.setMessage(message);
        
        messageDeliveryService.saveMessageDelivery(messageDelivery);
    }

    @Test
    @Override
    public void testCreate() {
        MessageDelivery md = new MessageDelivery();
        md.setDelivererTypeName("pigeon");
        md.setDeliveryStatus("in flight");
        md.setMessage(message);
        
        messageDeliveryService.saveMessageDelivery(md);

        assertNotNull(md.getId());
        Collection<MessageDelivery> ms = messageDeliveryService.getAllMessageDeliveries();
        assertNotNull(ms);
        assertEquals(2, ms.size());
        
        MessageDelivery md2 = messageDeliveryService.getMessageDelivery(md.getId());
        assertNotNull(md2);
        assertNotNull(md2.getId());
        assertNotNull(md2.getMessage());
        assertEquals(md.getDelivererSystemId(), md2.getDelivererSystemId());
        assertEquals(md.getDelivererTypeName(), md2.getDelivererTypeName());
        assertEquals(md.getDeliveryStatus(), md2.getDeliveryStatus());
        assertEquals(md.getMessage().getId(), md2.getMessage().getId());
        
    }

    @Test
    @Override
    public void testDelete() {
        messageDeliveryService.deleteMessageDelivery(messageDelivery);

        Collection<MessageDelivery> ms = messageDeliveryService.getAllMessageDeliveries();
        assertNotNull(ms);
        assertEquals(0, ms.size());
        
        assertNull(messageDeliveryService.getMessageDelivery(messageDelivery.getId()));
    }

    /* since OJB treats creates and updates the same, this test doesn't really test anything under OJB */ 
    @Test
    @Override
    public void testDuplicateCreate() {
        MessageDelivery md = new MessageDelivery();
        md.setId(messageDelivery.getId());
        md.setDelivererSystemId(messageDelivery.getDelivererSystemId());
        md.setDelivererTypeName(messageDelivery.getDelivererTypeName());
        md.setDeliveryStatus(messageDelivery.getDeliveryStatus());
        md.setLockVerNbr(messageDelivery.getLockVerNbr());
        md.setMessage(message);
        messageDeliveryService.saveMessageDelivery(md);
    }

    @Test
    @Override
    public void testInvalidCreate() {
        final MessageDelivery m = new MessageDelivery();
        new AssertThrows(DataIntegrityViolationException.class) {
            @Override
            public void test() throws Exception {
                messageDeliveryService.saveMessageDelivery(m);
            }
            
        }.runTest();
    }

    @Test
    @Override
    public void testInvalidDelete() {
        final MessageDelivery m = new MessageDelivery();
        m.setId(new Long(-1));
        // OJB yields an org.springmodules.orm.ojb.OjbOperationException/OptimisticLockException and claims the object
        // may have been deleted by somebody else
        new AssertThrows(DataAccessException.class) {
            public void test() {
                messageDeliveryService.deleteMessageDelivery(m);        
            }
        }.runTest();
    }

    @Test
    @Override
    public void testInvalidRead() {
        MessageDelivery m = messageDeliveryService.getMessageDelivery(Long.valueOf(-1));
        assertNull(m);
    }

    @Test
    @Override
    public void testInvalidUpdate() {
        final MessageDelivery m = messageDeliveryService.getMessageDelivery(messageDelivery.getId());
        m.setDeliveryStatus(null);
        new AssertThrows(DataAccessException.class) {
            @Override
            public void test() throws Exception {
                messageDeliveryService.saveMessageDelivery(m);
            }
            
        }.runTest();
    }

    @Test
    @Override
    public void testReadById() {
        MessageDelivery m = messageDeliveryService.getMessageDelivery(messageDelivery.getId());

        assertEquals(messageDelivery.getId(), m.getId());
        assertEquals(messageDelivery.getDelivererSystemId(), m.getDelivererSystemId());
        assertEquals(messageDelivery.getDelivererTypeName(), m.getDelivererTypeName());
        assertEquals(messageDelivery.getDeliveryStatus(), m.getDeliveryStatus());
        assertEquals(messageDelivery.getMessage().getId(), m.getMessage().getId());
    }

    @Test
    @Override
    public void testUpdate() {
        MessageDelivery m = messageDeliveryService.getMessageDelivery(messageDelivery.getId());
        m.setDelivererTypeName("eagle");
        m.setDeliveryStatus("soaring");
        m.setDelivererSystemId("1234");
        messageDeliveryService.saveMessageDelivery(m);
        
        MessageDelivery m2 = messageDeliveryService.getMessageDelivery(m.getId());
        assertNotNull(m2);
        
        assertEquals(m.getId(), m2.getId());
        assertEquals(m.getDelivererSystemId(), m2.getDelivererSystemId());
        assertEquals(m.getDelivererTypeName(), m2.getDelivererTypeName());
        assertEquals(m.getDeliveryStatus(), m2.getDeliveryStatus());
        assertEquals(m.getMessage().getId(), m2.getMessage().getId());
    }
}