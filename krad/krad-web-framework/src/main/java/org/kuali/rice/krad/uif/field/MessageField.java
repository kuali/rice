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
package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Message;

import java.util.List;

/**
 * Field wrapper for a Message
 *
 * <p>
 * The <code>Message</code> is used to display static text in the user
 * interface
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageField extends FieldBase {
    private static final long serialVersionUID = -7045208136391722063L;
    
    private Message message;

    public MessageField() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(message);

        return components;
    }

    /**
     * Convenience method for setting the message text
     *
     * @param messageText - text to display for the message
     */
    public void setMessageText(String messageText) {
        if (message != null) {
            message.setMessageText(messageText);
        }
    }

    /**
     * Nested @{link org.kuali.rice.krad.uif.element.Message} component wrapped in the field
     * 
     * @return Message instance
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Setter for the nested message instance
     * 
     * @param message
     */
    public void setMessage(Message message) {
        this.message = message;
    }
}
