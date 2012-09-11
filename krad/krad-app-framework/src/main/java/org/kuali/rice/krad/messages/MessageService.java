package org.kuali.rice.krad.messages;

import java.util.Collection;

/**
 * Message Service API
 *
 * <p>
 * Messages given within an application can be externalized to a separate repository. Those messages are
 * then retrieved with use of the message service. The API provides various retrieval methods based on how
 * the message is identified
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MessageService {

    /**
     * Gets the {@link Message} object that has the given namespace, component, name, and the default
     * system locale
     *
     * @param namespace - namespace code the message belongs to
     * @param component - component code the namespace is associated with
     * @param name - name that identifies the message within the namespace and component
     * @return Message matching message object, or null if a message was not found
     */
    public Message getMessage(String namespace, String component, String name);

    /**
     * Gets the {@link Message} object that has the given namespace, component, name, and locale
     *
     * @param namespace - namespace code the message belongs to
     * @param component - component code the namespace is associated with
     * @param name - name that identifies the message within the namespace and component
     * @param locale - locale code for the message to return
     * @return Message matching message object, or null if a message was not found
     */
    public Message getMessage(String namespace, String component, String name, String locale);

    /**
     * Gets the text for the message that has the given namespace, component, name, and the default
     * system locale
     *
     * @param namespace - namespace code the message belongs to
     * @param component - component code the namespace is associated with
     * @param name - name that identifies the message within the namespace and component
     * @return String text for the matched message, or null if no message was found
     */
    public String getMessageText(String namespace, String component, String name);

    /**
     * Gets the text for the message that has the given namespace, component, name, and locale
     *
     * @param namespace - namespace code the message belongs to
     * @param component - component code the namespace is associated with
     * @param name - name that identifies the message within the namespace and component
     * @param locale - locale code for the message to return
     * @return String text for the matched message, or null if no message was found
     */
    public String getMessageText(String namespace, String component, String name, String locale);

    /**
     * Gets the text for the message that has the given name within the default namespace, component,
     * and locale (note the defaults are determined by the service implementation)
     *
     * @param name - name that identifies the message within the default namespace and component
     * @return String text for the matched message, or null if no message was found
     */
    public String getMessageText(String name);

    /**
     * Gets the text for the message that has the given name and locale within the default namespace and
     * component (note the defaults are determined by the service implementation)
     *
     * @param name - name that identifies the message within the default namespace and component
     * @param locale - locale code for the message to return
     * @return String text for the matched message, or null if no message was found
     */
    public String getMessageText(String name, String locale);

    /**
     * Gets all message objects for the given namespace and component using the default locale
     *
     * @param namespace - namespace code the message belongs to
     * @param component - component code the namespace is associated with
     * @return Collection<Message> collection of messages that match, or empty collection if no messages
     * are found
     */
    public Collection<Message> getAllMessagesForComponent(String namespace, String component);

    /**
     * Gets all message objects for the given namespace, component, and locale
     *
     * @param namespace - namespace code the message belongs to
     * @param component - component code the namespace is associated with
     * @param locale - locale code for the message to return
     * @return Collection<Message> collection of messages that match, or empty collection if no messages
     * are found
     */
    public Collection<Message> getAllMessagesForComponent(String namespace, String component, String locale);

}
