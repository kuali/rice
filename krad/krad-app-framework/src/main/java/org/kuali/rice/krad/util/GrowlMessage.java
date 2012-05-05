package org.kuali.rice.krad.util;

import java.io.Serializable;

/**
 * Contains configuration for displaying a growl message
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GrowlMessage implements Serializable {
    private static final long serialVersionUID = 6588969539633862559L;

    private String title;
    private String messageKey;
    private String[] messageParameters;
    private String theme;

    public GrowlMessage() {

    }

    /**
     * Title for growl message (displays on top bar of growl)
     *
     * @return String title text
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the growl title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Key for the growl message with the application resources
     *
     * <p>
     * Growl message text must be externalized into a properties file where the message is given a key. This gives
     * the key for which the message can be retrieved
     * </p>
     *
     * @return String message key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Setter for the growl message key
     *
     * @param messageKey
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * One or more parameters for complete the growl message
     *
     * <p>
     * An externally defined message can contain one or more placeholders which will get completed from runtime
     * variables. This array of strings is used for completing the message. The message parameters are filled based
     * on the order or parameters within the array
     * </p>
     *
     * @return String[] array of string values to fill message parameters
     */
    public String[] getMessageParameters() {
        return messageParameters;
    }

    /**
     * Setter for the message parameters array
     *
     * @param messageParameters
     */
    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    /**
     * Name of the growl theme to use (must be setup through the view growl property
     * @{link org.kuali.rice.krad.uif.view.View#getGrowls()} )
     *
     * @return String name of growl theme
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Setter for the growl theme to use
     *
     * @param theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }
}
