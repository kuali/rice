/**
 * Copyright 2005-2012 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.messages;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Test cases for {@link MessageServiceImpl}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageServiceImplTest {

    /**
     * Test that list of messages are being correctly merged, messages of same
     * type should not be overridden
     */
    @Test
    public void testMergeMessages() {
        Collection<Message> messages1 = new ArrayList<Message>();
        Collection<Message> messages2 = new ArrayList<Message>();
        
        MessageBuilder builder = MessageBuilder.create("Default", "All", "Test Message");

        Message message1 = builder.build();

        builder.setName("Test Message 2");
        Message message2 = builder.build();

        builder.setComponentCode("TestView");
        Message message3 = builder.build();

        builder.setNamespaceCode("KR-SAP");
        Message message4 = builder.build();

        Message message5 = builder.build();

        messages1.add(message1);
        messages1.add(message2);
        messages1.add(message4);

        messages2.add(message1);
        messages2.add(message3);
        messages2.add(message4);
        messages2.add(message5);

        MessageServiceImpl messageServiceImpl = new MessageServiceImpl();
        messageServiceImpl.mergeMessages(messages1, messages2);

        assertEquals("Merged map is not correct size", 5, messages1.size());

    }

    public static class MessageBuilder {
        private String namespaceCode;
        private String componentCode;
        private String name;
        private String text;
        private String messageTypeCode;
        private String locale;

        public MessageBuilder(String namespaceCode, String componentCode, String name) {
            setNamespaceCode(namespaceCode);
            setComponentCode(componentCode);
            setName(name);
        }

        public static MessageBuilder create(String namespaceCode, String componentCode, String name) {
            return new MessageBuilder(namespaceCode, componentCode, name);
        }

        public Message build() {
            Message message = new Message();

            message.setNamespaceCode(getNamespaceCode());
            message.setComponentCode(getComponentCode());
            message.setName(getName());
            message.setText(getText());
            message.setMessageTypeCode(getMessageTypeCode());
            message.setLocale(getLocale());

            return message;
        }

        public String getNamespaceCode() {
            return namespaceCode;
        }

        public void setNamespaceCode(String namespaceCode) {
            this.namespaceCode = namespaceCode;
        }

        public String getComponentCode() {
            return componentCode;
        }

        public void setComponentCode(String componentCode) {
            this.componentCode = componentCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getMessageTypeCode() {
            return messageTypeCode;
        }

        public void setMessageTypeCode(String messageTypeCode) {
            this.messageTypeCode = messageTypeCode;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }
    }
}
