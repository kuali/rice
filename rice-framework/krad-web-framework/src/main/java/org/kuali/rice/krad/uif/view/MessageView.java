/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.view;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.util.ComponentFactory;

import java.util.List;

/**
 * View that presents a message to the user (for example an application error message)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "messageView", parent="Uif-MessageView")
public class MessageView extends FormView {
    private static final long serialVersionUID = 5578210247236389466L;

    private Message message;

    public MessageView() {
        super();

        super.setSinglePageView(true);
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the message text onto the message component and add to the page items</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    public void performInitialization(Object model) {
        super.performInitialization(model);

        List<Component> newItems = (List<Component>) getPage().getItems();
        newItems.add(message);
        getPage().setItems(newItems);
    }

    /**
     * Message component that will be used to display the message (used for styling and so on)
     *
     * @return Message component instance
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Message getMessage() {
        return message;
    }

    /**
     * Setter for the message component
     *
     * @param message
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Message text to display in the message view.
     *
     * @return message text as string
     */
    @BeanTagAttribute
    public String getMessageText() {
        if (this.message != null) {
            return this.message.getMessageText();
        }

        return null;
    }

    /**
     * @see MessageView#getMessageText()
     */
    public void setMessageText(String messageText) {
        if (this.message == null) {
            this.message = ComponentFactory.getMessage();
        }

        this.message.setMessageText(messageText);
    }
}
