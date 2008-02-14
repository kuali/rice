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
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.kcb.test.BusinessObjectTestCase;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.AssertThrows;


/**
 * Tests MessageService 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class MessageServiceTest extends BusinessObjectTestCase {
    private MessageService messageService;
    private Message message;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    
        messageService = GlobalKCBServiceLocator.getInstance().getMessageService();

        message = new Message();
        message.setContent("test content 1");
        message.setContentType("test content type 1");
        message.setDeliveryType("test delivery type 1");
        message.setRecipient("test recipient 1");
        message.setTitle("test title 1");

        messageService.saveMessage(message);
    }

    @Test
    @Override
    public void testCreate() {
        Message m = new Message();
        m.setContent("test content 2");
        m.setContentType("test content type 2");
        m.setDeliveryType("test delivery type 2");
        m.setRecipient("test recipient 2");
        m.setTitle("test title 2");

        messageService.saveMessage(m);
        assertNotNull(m.getId());

        Collection<Message> ms = messageService.getAllMessages();
        assertNotNull(ms);
        assertEquals(2, ms.size());
        
        Message m2 = messageService.getMessage(m.getId());
        assertNotNull(m2);
        assertNotNull(m2.getId());
        assertNotNull(m2.getCreationDateTime());
        assertEquals(m.getContent(), m2.getContent());
        assertEquals(m.getContentType(), m2.getContentType());
        assertEquals(m.getDeliveryType(), m2.getDeliveryType());
        assertEquals(m.getRecipient(), m2.getRecipient());
        assertEquals(m.getTitle(), m2.getTitle());
    }

    @Test
    @Override
    public void testDelete() {
        messageService.deleteMessage(message);
        
        Collection<Message> ms = messageService.getAllMessages();
        assertNotNull(ms);
        assertEquals(0, ms.size());
        
        assertNull(messageService.getMessage(message.getId()));
    }

    /* since OJB treats creates and updates the same, this test doesn't really test anything under OJB */
    @Test
    @Override
    public void testDuplicateCreate() {
        Message m = new Message();
        m.setId(message.getId());
        m.setContent(message.getContent());
        m.setContentType(message.getContentType());
        m.setCreationDateTime(message.getCreationDateTime());
        m.setDeliveryType(message.getDeliveryType());
        m.setRecipient(message.getRecipient());
        m.setTitle(message.getTitle());
        m.setLockVerNbr(message.getLockVerNbr());
        messageService.saveMessage(m);
    }

    @Test
    @Override
    public void testInvalidCreate() {
        final Message m = new Message();
        new AssertThrows(DataIntegrityViolationException.class) {
            @Override
            public void test() throws Exception {
                messageService.saveMessage(m);
            }
            
        }.runTest();
    }

    @Test
    @Override
    public void testInvalidDelete() {
        final Message m = new Message();
        m.setId(new Long(-1));
        // OJB yields an org.springmodules.orm.ojb.OjbOperationException/OptimisticLockException and claims the object
        // may have been deleted by somebody else
        new AssertThrows(DataAccessException.class) {
            public void test() {
                messageService.deleteMessage(m);        
            }
        }.runTest();
    }

    @Test
    @Override
    public void testInvalidRead() {
        super.testInvalidRead();Message m = messageService.getMessage(Long.valueOf(-1));
        assertNull(m);
    }

    @Test
    @Override
    public void testInvalidUpdate() {
        final Message m = messageService.getMessage(message.getId());
        m.setContentType(null);
        new AssertThrows(DataAccessException.class) {
            @Override
            public void test() throws Exception {
                messageService.saveMessage(m);
            }
            
        }.runTest();
        
    }

    @Test
    @Override
    public void testReadById() {
        Message m = messageService.getMessage(message.getId());

        assertEquals(message.getId(), m.getId());
        assertEquals(message.getCreationDateTime(), m.getCreationDateTime());
        assertEquals(message.getContent(), m.getContent());
        assertEquals(message.getContentType(), m.getContentType());
        assertEquals(message.getDeliveryType(), m.getDeliveryType());
        assertEquals(message.getRecipient(), m.getRecipient());
        assertEquals(message.getTitle(), m.getTitle());
    }

    @Test
    @Override
    public void testUpdate() {
        Message m = messageService.getMessage(message.getId());
        m.setTitle("A better title");
        m.setContent("different content");
        messageService.saveMessage(m);
        
        Message m2 = messageService.getMessage(m.getId());
        assertNotNull(m2);
        
        assertEquals(m.getId(), m2.getId());
        assertEquals(m.getCreationDateTime(), m2.getCreationDateTime());
        assertEquals(m.getContent(), m2.getContent());
        assertEquals(m.getContentType(), m2.getContentType());
        assertEquals(m.getDeliveryType(), m2.getDeliveryType());
        assertEquals(m.getRecipient(), m2.getRecipient());
        assertEquals(m.getTitle(), m2.getTitle());
    }
}
