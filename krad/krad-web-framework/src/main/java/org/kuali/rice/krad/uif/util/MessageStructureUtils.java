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
package org.kuali.rice.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Rich message structure utilities for parsing message content and converting it to components/content.
 */
public class MessageStructureUtils {

    /**
     * Translate a message with special hooks described in MessageStructureUtils.parseMessage.  However, tags which
     * reference components will not be allowed/translated - only tags which can translate to string content will
     * be included for this translation.
     *
     * @see MessageStructureUtils#parseMessage
     * @param messageText messageText with only String translateable tags included (no id or component index tags)
     * @return html translation of rich messageText passed in
     */
    public static String translateStringMessage(String messageText){
        if(!StringUtils.isEmpty(messageText)){
            List<Component> components = MessageStructureUtils.parseMessage(null, messageText, null, null, false);
            if(!components.isEmpty()){
                Component message = components.get(0);
                if(message instanceof Message){
                    messageText = ((Message) message).getMessageText();
                }
            }
        }
        return messageText;
    } 
    
    /**
     * Parses the message text passed in and returns the resulting rich message component structure.
     *
     * <p>If special characters [] are detected the message is split at that location.  The types of features supported
     * by the parse are (note that &lt;&gt; are not part of the content, they specify placeholders):
     * <ul>
     * <li>[id=&lt;component id&gt;] - insert component with id specified at that location in the message</li>
     * <li>[n] - insert component at index n from the inlineComponent list</li>
     * <li>[&lt;html tag&gt;][/&lt;html tag&gt;] - insert html content directly into the message content at that
     * location,
     * without the need to escape the &lt;&gt; characters in xml</li>
     * <li>[color=&lt;html color code/name&gt;][/color] - wrap content in color tags to make text that color
     * in the message</li>
     * <li>[css=&lt;css classes&gt;][/css] - apply css classes specified to the wrapped content - same as wrapping
     * the content in span with class property set</li>
     * </ul>
     * If the [] characters are needed in message text, they need to be declared with an escape character: \\[ \\]
     * </p>
     *
     * @param messageId id of the message
     * @param messageText message text to be parsed
     * @param componentList the inlineComponent list
     * @param view the current view
     * @return list of components representing the parsed message structure
     */
    public static List<Component> parseMessage(String messageId, String messageText, List<Component> componentList,
            View view, boolean parseComponents) {

        messageText = messageText.replace("\\[", "&#91;");
        messageText = messageText.replace("\\]", "&#93;");
        messageText = messageText.replace("]", "$@$]");
        String[] messagePieces = messageText.split("[\\[|\\]]");

        List<Component> messageComponentStructure = new ArrayList<Component>();

        //current message object to concatenate to after it is generated to prevent whitespace issues and
        //creation of multiple unneeded objects
        Message currentMessageComponent = null;

        for (String s : messagePieces) {

            if (s.endsWith("$@$")) {
                s = StringUtils.removeEnd(s, "$@$");

                if (s.startsWith("id=") && parseComponents) {
                    //if there is a currentMessageComponent add it to the structure and reset it to null
                    //because component content is now interrupting the string content
                    if (currentMessageComponent != null) {
                        messageComponentStructure.add(currentMessageComponent);
                        currentMessageComponent = null;
                    }
                    //match component by id from the view
                    s = StringUtils.remove(s, "'");
                    s = StringUtils.remove(s, "\"");
                    Component component = ComponentFactory.getNewComponentInstance(StringUtils.removeStart(s, "id="));

                    if (component != null) {
                        view.assignComponentIds(component);
                        component.addStyleClass("inlineBlock");
                        messageComponentStructure.add(component);
                    }
                } else if (s.matches("^[0-9]+$") && parseComponents) {
                    //if there is a currentMessageComponent add it to the structure and reset it to null
                    //because component content is now interrupting the string content
                    if (currentMessageComponent != null) {
                        messageComponentStructure.add(currentMessageComponent);
                        currentMessageComponent = null;
                    }
                    //match component by index from the componentList passed in
                    int cIndex = Integer.parseInt(s);
                    if (componentList != null && cIndex < componentList.size() && !componentList.isEmpty()) {
                        Component component = componentList.get(cIndex);

                        if (component != null) {
                            if (component.getId() == null) {
                                view.assignComponentIds(component);
                            }
                            component.addStyleClass("inlineBlock");
                            messageComponentStructure.add(component);
                        }
                    } else {
                        throw new RuntimeException("Component with index " + cIndex +
                                " does not exist in inlineComponents of the message component with id " + messageId);
                    }
                } else if (s.startsWith("color=") || s.startsWith("/color")) {
                    if (!s.startsWith("/")) {
                        s = StringUtils.remove(s, "'");
                        s = StringUtils.remove(s, "\"");
                        s = "<span style='color: " + StringUtils.removeStart(s, "color=") + ";'>";
                    } else {
                        s = "</span>";
                    }
                    currentMessageComponent = concatenateStringMessageContent(currentMessageComponent, s, view);

                } else if (s.startsWith("css=") || s.startsWith("/css")) {
                    if (!s.startsWith("/")) {
                        s = StringUtils.remove(s, "'");
                        s = StringUtils.remove(s, "\"");
                        s = "<span class='" + StringUtils.removeStart(s, "css=") + "'>";
                    } else {
                        s = "</span>";
                    }
                    currentMessageComponent = concatenateStringMessageContent(currentMessageComponent, s, view);

                } else if (s.startsWith("link=") || s.startsWith("/link")) {
                    if (!s.startsWith("/")) {
                        //clean up href
                        s = StringUtils.removeStart(s, "link=");
                        s = StringUtils.removeStart(s, "'");
                        s = StringUtils.removeEnd(s, "'");
                        s = StringUtils.removeStart(s, "\"");
                        s = StringUtils.removeEnd(s, "\"");

                        s = "<a href='" + s + "' target='_blank'>";
                    } else {
                        s = "</a>";
                    }
                    currentMessageComponent = concatenateStringMessageContent(currentMessageComponent, s, view);
                } else if (s.startsWith("action=") || s.equals("/action")) {
                    if (!s.startsWith("/")) {
                        s = StringUtils.removeStart(s, "action=");
                        String[] splitData = s.split("data=");

                        String[] params = splitData[0].trim().split("([,]+(?=([^']*'[^']*')*[^']*$))");
                        String methodToCall = ((params.length >= 1) ? params[0] : "");
                        String validate = ((params.length >= 2) ? params[1] : "true");
                        String ajaxSubmit = ((params.length >= 3) ? params[2] : "true");
                        String successCallback = ((params.length >= 4) ? params[3] : "null");

                        String submitData = "null";
                        if (splitData.length > 1) {
                            submitData = splitData[1].trim();
                        }

                        methodToCall = StringUtils.remove(methodToCall, "'");
                        methodToCall = StringUtils.remove(methodToCall, "\"");
                        if (ajaxSubmit.equals("true")) {
                            s = "<a onclick=\"ajaxSubmitFormFullOpts("
                                    + "'"
                                    + methodToCall
                                    + "',"
                                    + successCallback
                                    + ","
                                    + submitData
                                    + ","
                                    + "null, null,"
                                    + validate
                                    + ","
                                    + "null, null); return false;\">";
                        } else {
                            s = "<a href=\" javascript:void(null)\" "
                                    + "onclick=\"submitFormFullOpts('"
                                    + methodToCall
                                    + "',"
                                    + submitData
                                    + ","
                                    + validate
                                    + ", null); return false;\">";
                        }
                    } else {
                        s = "</a>";
                    }
                    currentMessageComponent = concatenateStringMessageContent(currentMessageComponent, s, view);

                } else if (s.equals("")) {
                    //do nothing    
                } else {
                    //raw html
                    s = "<" + s + ">";
                    currentMessageComponent = concatenateStringMessageContent(currentMessageComponent, s, view);

                }
            } else {
                //raw html
                addBlanks(s);
                currentMessageComponent = concatenateStringMessageContent(currentMessageComponent, s, view);

            }
        }

        if (currentMessageComponent != null) {
            messageComponentStructure.add(currentMessageComponent);
            currentMessageComponent = null;
        }

        return messageComponentStructure;
    }

    /**
     * Concatenates string content onto the message passed in and passes it back.  If the message is null, creates
     * a new message object with the string content and passes that back.
     *
     * @param currentMessageComponent Message object
     * @param s string content to be concatenated
     * @param view the current view
     * @return resulting concatenated Message
     */
    private static Message concatenateStringMessageContent(Message currentMessageComponent, String s, View view) {
        if (currentMessageComponent == null) {
            currentMessageComponent = ComponentFactory.getMessage();
            if(view != null){
                view.assignComponentIds(currentMessageComponent);
            }
            currentMessageComponent.setMessageText(s);
            currentMessageComponent.setGenerateSpan(false);
        } else {
            currentMessageComponent.setMessageText(currentMessageComponent.getMessageText() + s);
        }

        return currentMessageComponent;
    }

    /**
     * Inserts &amp;nbsp; into the string passed in, if spaces exist at the beginning and/or end,
     * so spacing is not lost in html translation.
     *
     * @param s string to insert  &amp;nbsp;
     * @return String with  &amp;nbsp; inserted, if applicable
     */
    public static String addBlanks(String s) {
        if (s.startsWith(" ")) {
            s = "&nbsp;" + StringUtils.removeStart(s, " ");
        }
        if (s.endsWith(" ")) {
            s = StringUtils.removeEnd(s, " ") + "&nbsp;";
        }

        return s;
    }
}
