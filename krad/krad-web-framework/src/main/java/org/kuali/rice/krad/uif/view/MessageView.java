package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Message;

import java.util.List;

/**
 * View that presents a message to the user (for example an application error message)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageView extends FormView {
    private static final long serialVersionUID = 5578210247236389466L;

    private String messageText;
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
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performInitialization(View, java.lang.Object)
     */
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        if (StringUtils.isNotBlank(messageText) && StringUtils.isBlank(message.getMessageText())) {
            message.setMessageText(messageText);
        }

        List<Component> newItems = (List<Component>) getPage().getItems();
        newItems.add(message);
        getPage().setItems(newItems);
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
     * Test for the message to display
     *
     * @return String message text
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     * Setter for the views message text
     *
     * @param messageText
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * Message component that will be used to display the message (used for styling and so on)
     *
     * @return Message component instance
     */
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
}
