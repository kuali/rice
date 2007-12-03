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
package org.kuali.notification.services.impl;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;
import org.kuali.notification.bo.Notification;
import org.kuali.notification.bo.NotificationRecipient;
import org.kuali.notification.bo.NotificationResponse;
import org.kuali.notification.bo.NotificationSender;
import org.kuali.notification.exception.InvalidXMLException;
import org.kuali.notification.service.NotificationMessageContentService;
import org.kuali.notification.test.NotificationTestCaseBase;
import org.kuali.notification.test.TestConstants;
import org.kuali.notification.util.NotificationConstants;
import org.kuali.notification.util.Util;

/**
 * Tests NotificationMessageContentService
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NotificationMessageContentServiceImplTest extends NotificationTestCaseBase {
    private static final String SAMPLE_EVENT_MESSAGE = "sample_message_event_type.xml";
    private static final String SAMPLE_SIMPLE_MESSAGE = "sample_message_simple_type.xml";
    private static final String SAMPLE_MALFORMED_EVENT_MESSAGE = "sample_malformed_message_event_type.xml";
    private static final String SAMPLE_MALFORMED_SIMPLE_MESSAGE = "sample_malformed_message_simple_type.xml";
    private static final String SAMPLE_BADNAMESPACE_EVENT_MESSAGE = "badnamespace_message_event_type.xml";
    private static final String SAMPLE_CHANNEL = TestConstants.VALID_CHANNEL_ONE;
    private static final String VALID_CHANNEL = TestConstants.VALID_CHANNEL_TWO;
    private static final String VALID_TYPE = NotificationConstants.DELIVERY_TYPES.FYI;
    private static final String VALID_CONTENT = "<content xmlns=\"ns:notification/ContentTypeSimple\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                                                " xsi:schemaLocation=\"ns:notification/ContentTypeSimple resource:notification/ContentTypeSimple\">\n" +
                                                "    <message>Holiday-Ho-Out Starts Next Week - 11/20/2006!</message>\n" +
                                                "</content>";
    
    private static final String sampleEdlFile = "NotificationDocumentContent.xml";

    public NotificationMessageContentServiceImplTest() {
        //setDefaultRollback(false);
    }

    private void testParseNotificationRequestMessage(String samplePath) throws Exception {
        NotificationMessageContentService impl = services.getNotificationMessageContentService();
        InputStream is = this.getClass().getResourceAsStream(samplePath);
        System.out.println(is);
        Notification notification = impl.parseNotificationRequestMessage(is);
        assertEquals(SAMPLE_CHANNEL, notification.getChannel().getName());
        System.out.println(notification.getSenders());
        System.out.println("notification id: " + notification.getId());
        List<NotificationSender> sl = notification.getSenders();
        assertTrue(sl.size() > 0);
        for (NotificationSender s :sl) {
            assertNotNull(s);
            assertNotNull(s.getSenderName());
        }
        List<NotificationRecipient> rl = notification.getRecipients();
        assertTrue(rl.size() > 0);
        for (NotificationRecipient r : rl) {
            assertNotNull(r);
            assertNotNull(r.getRecipientId());
        }
        //fail("Not yet implemented");

        notification.setCreationDateTime(new Timestamp(System.currentTimeMillis()));
        services.getBusinesObjectDao().save(notification);
        //setComplete();
    }

    @Test
    public void testParseEventNotificationRequestMessage() throws Exception {
        testParseNotificationRequestMessage(SAMPLE_EVENT_MESSAGE);
    }

    @Test
    public void testParseSimpleNotificationRequestMessage() throws Exception {
        testParseNotificationRequestMessage(SAMPLE_SIMPLE_MESSAGE);
    }
    @Test
    public void testParseMalformedEventNotificationRequestMessage() throws Exception {
        try {
            testParseNotificationRequestMessage(SAMPLE_MALFORMED_EVENT_MESSAGE);
            fail("malformed event message passed validation");
        } catch (InvalidXMLException ixe) {
            // expected
            return;
        }
    }
    @Test
    public void testParseBadNamespaceEventNotificationRequestMessage() throws Exception {
        try {
            testParseNotificationRequestMessage(SAMPLE_BADNAMESPACE_EVENT_MESSAGE);
            fail("malformed event message passed validation");
        } catch (InvalidXMLException ixe) {
            // expected
            return;
        }
    }
    @Test
    public void testParseMalformedSimpleNotificationRequestMessage() throws Exception {
        try {
            testParseNotificationRequestMessage(SAMPLE_MALFORMED_SIMPLE_MESSAGE);
            fail("malformed simple message passed validation");
        } catch (InvalidXMLException ixe) {
            // expected
        }
    }

    @Test
    public void testGenerateNotificationResponseMessage() throws Exception {
	NotificationResponse response = new NotificationResponse();
	response.setStatus("PASS");
	response.setMessage("Here is your response");
	NotificationMessageContentService impl = services.getNotificationMessageContentService();
	String xml = impl.generateNotificationResponseMessage(response);
	assertTrue(xml.length() == 89);
    }

    @Test
    public void testGenerateNotificationMessage() throws Exception {
	NotificationMessageContentService impl = services.getNotificationMessageContentService();
        InputStream is = this.getClass().getResourceAsStream(SAMPLE_SIMPLE_MESSAGE);
        System.out.println(is);
        Notification notification = impl.parseNotificationRequestMessage(is);
        String XML = impl.generateNotificationMessage(notification);
        assertTrue(XML.length()>0);
    }

    @Test
    public void testParseSerializedNotificationXml() throws Exception {
	InputStream is = this.getClass().getResourceAsStream(sampleEdlFile);
	
	byte[] bytes = Util.readFully(is);
	
	NotificationMessageContentService impl = services.getNotificationMessageContentService();
	
        Notification notification = impl.parseSerializedNotificationXml(bytes);
        
        assertNotNull(notification);
        assertEquals(VALID_CHANNEL, notification.getChannel().getName());
        
        assertEquals(VALID_TYPE, notification.getDeliveryType());
        
        assertEquals(VALID_CONTENT.replaceAll("\\s+", " "), notification.getContent().replaceAll("\\s+", " "));
    }
}