package org.kuali.rice.krad.uif.field;

import org.kuali.rice.krad.uif.element.Message;

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
