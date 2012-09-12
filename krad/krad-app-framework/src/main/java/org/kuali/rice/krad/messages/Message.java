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

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Holds the text and metadata for a message that will be given by the system, including validation
 * messages, UI text (labels, instructions), and other text that has been externalized from the
 * system
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Message extends PersistableBusinessObjectBase {

    private String namespaceCode;
    private String componentCode;
    private String name;
    private String text;
    private String messageTypeCode;
    private String locale;

    public Message() {
        super();
    }

    /**
     * Namespace code (often an application or module code) that message is associated with, used for
     * grouping messages
     *
     * @return String namespace code
     */
    public String getNamespaceCode() {
        return namespaceCode;
    }

    /**
     * Setter for the namespace code the message should be associated with
     *
     * @param namespaceCode
     */
    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    /**
     * A code within the namespace that identifies a component or group, used for further grouping
     * of messages within the namespace
     *
     * <p>
     * Examples here could be a bean id, the class name of an object, or any application/module defined code
     * </p>
     *
     * @return String representing a component code
     */
    public String getComponentCode() {
        return componentCode;
    }

    /**
     * Setter for the component code the message should be associated with
     *
     * @param componentCode
     */
    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    /**
     * A name that identifies the message uniquely within a namespace and component
     *
     * @return String message name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the message name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Text value for the message
     *
     * <p>
     * This holds the actual text for the message which is what will be displayed. Depending on how
     * the message is being used it might contain parameters or other special syntax
     * </p>
     *
     * @return String text for the message
     */
    public String getText() {
        return text;
    }

    /**
     * Setter for the message text
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Code that represents the type of message being represented
     *
     * <p>
     * Within the framework several types of messages are externalized. A few examples of these
     * include labels, instructions, and validation messages. This code indicates to the framework how the
     * message is used and impacts processing of the message within the framework
     * </p>
     *
     * @return String message type code
     */
    public String getMessageTypeCode() {
        return messageTypeCode;
    }

    /**
     * Setter for the message type code
     *
     * @param messageTypeCode
     */
    public void setMessageTypeCode(String messageTypeCode) {
        this.messageTypeCode = messageTypeCode;
    }

    /**
     * Locale code the message is represented for, used for supporting messages in different
     * languages
     *
     * @return message locale code
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Setter for the message locale code
     *
     * @param locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Generate toString using message key fields
     *
     * @return String representing the message object
     */
    @Override
    public final String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("namespaceCode=" + this.namespaceCode);
        buffer.append(",componentCode=" + this.componentCode);
        buffer.append(",name=" + this.name);

        return buffer.toString();
    }
}
