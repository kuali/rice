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
package org.kuali.rice.krad.uif.element;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.MessageStructureUtils;
import org.kuali.rice.krad.util.KRADConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates a text message to be displayed
 *
 * <p>
 * The <code>Message</code> is used to display static text in the user
 * interface
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "message", parent = "Uif-Message")
public class Message extends ContentElementBase {
    private static final long serialVersionUID = 4090058533452450395L;

    // This regex is a check to see if the message is a rich message and it contains potential non-inline elements
    private static Pattern blockElementCheck = Pattern.compile(
            "[\\[|\\<](?!color|action|link|css|button|input|label|select|textarea|abbr"
                    + "|strong|img|a[\\s\\]]|span[\\s\\]]|b[\\s\\]]|i[\\s\\]]|br[\\s\\]/])[^/]*?/?[\\]|\\>]");

    private String messageText;
    private String wrapperTag;
    private boolean renderWrapperTag;

    private List<Component> inlineComponents;
    private List<Component> messageComponentStructure;

    private boolean parseComponents;
    private boolean richMessage;
    private boolean containsBlockElements;

    public Message() {
        super();

        renderWrapperTag = true;
        parseComponents = true;
    }

    /**
     * Message perfom apply model parses message text for rich text functionality if the messageText contains
     * [ or ] special characters
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        //if messageText contains the special characters [] then parse and fill in the messageComponentStructure
        //but if messageComponentStructure has already been set it overrides messageText by default
        if (messageText != null && messageText.contains(KRADConstants.MessageParsing.LEFT_TOKEN) &&
                messageText.contains(KRADConstants.MessageParsing.RIGHT_TOKEN) &&
                (messageComponentStructure == null || messageComponentStructure.isEmpty())) {
            richMessage = true;

            // Check to see if message contains pontential block elements (non-inline)
            Matcher matcher = blockElementCheck.matcher(messageText);
            containsBlockElements = matcher.find();

            if (StringUtils.isBlank(wrapperTag) && containsBlockElements) {
                wrapperTag = UifConstants.WrapperTags.DIV;
            }

            messageComponentStructure = MessageStructureUtils.parseMessage(this.getId(), this.getMessageText(),
                    this.getInlineComponents(), ViewLifecycle.getView(), parseComponents);
        } else if (messageText != null && messageText.contains("<") && messageText.contains(">")) {
            // Straight inline html case
            // Check to see if message contains pontential block elements (non-inline)
            Matcher matcher = blockElementCheck.matcher(messageText);
            containsBlockElements = matcher.find();

            // Must be in a div it contains potential block elements
            if (StringUtils.isBlank(wrapperTag) && containsBlockElements) {
                wrapperTag = UifConstants.WrapperTags.DIV;
            }
        }

        // If the wrapper element is not set by the bean def or the above logic to check for block elements, default
        // to the p tag
        if (StringUtils.isBlank(wrapperTag)) {
            wrapperTag = UifConstants.WrapperTags.P;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (messageComponentStructure != null && !messageComponentStructure.isEmpty()) {
            // Message needs to be aware of its own parent because it now contains content that can have validation
            this.addDataAttribute(UifConstants.DataAttributes.PARENT, parent.getId());
        }
    }

    /**
     * Override to render only if the message text has been given or there is a conditional expression on the
     * message text
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isRender() {
        boolean render = super.isRender();

        if (render) {
            render = getPropertyExpressions().containsKey("messageText") || (StringUtils.isNotBlank(messageText)
                    && !StringUtils.equals(messageText, "&nbsp;"));
        }

        return render;
    }

    /**
     * Text that makes up the message that will be displayed.
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
     * <li>[link=&lt;href src&gt;][/link] - an easier way to create an anchor that will open in a new page to the
     * href specified after =</li>
     * <li>[action=&lt;href src&gt;][/action] - create an action link inline without having to specify a component by
     * id or index.  The options for this are as follows and MUST be in a comma seperated list in the order specified
     * (specify 1-4 always in this order):
     * <ul>
     * <li>methodToCall(String)</li>
     * <li>validateClientSide(boolean) - true if not set</li>
     * <li>ajaxSubmit(boolean) - true if not set</li>
     * <li>successCallback(js function or function declaration) - this only works when ajaxSubmit is true</li>
     * </ul>
     * The tag would look something like this [action=methodToCall]Action[/action] in most common cases.  And in more
     * complex cases [action=methodToCall,true,true,functionName]Action[/action].  <p>In addition to these settings,
     * you can also specify data to send to the server in this fashion (space is required between settings and data):
     * </p>
     * [action=&lt;action settings&gt; data={key1: 'value 1', key2: value2}]
     * </li>
     * </ul>
     * If the [] characters are needed in message text, they need to be declared with an escape character: \\[ \\]
     * </p>
     *
     * @return message text
     */
    @BeanTagAttribute
    public String getMessageText() {
        return this.messageText;
    }

    /**
     * Setter for the message text
     *
     * @param messageText
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * Defines the html tag that will wrap this message, if left blank, this will automatically be set by the framework
     * to the appropriate tag (in most cases p or div)
     *
     * @return the html tag used to wrap this message
     */
    @BeanTagAttribute
    public String getWrapperTag() {
        return wrapperTag;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Message#getWrapperTag()
     */
    public void setWrapperTag(String wrapperTag) {
        this.wrapperTag = wrapperTag;
    }

    /**
     * If true, render the wrapper element (p or div) around this message (default true).
     *
     * <p>The wrapper will be either a p tag, for when the element only contains inline elements, or a div tag, for
     * when the message might contain block level elements or undetermined html tags resulting from rich message
     * functionality.  When false, skips the wrapper generation for this
     * message - this has the additional effect the css classes/style classes will be lost for this message. </p>
     *
     * @return true if generating a wrapping span, false otherwise
     */
    @BeanTagAttribute
    public boolean isRenderWrapperTag() {
        return renderWrapperTag && wrapperTag != null;
    }

    /**
     * Sets the generate wrapper element flag
     *
     * @param renderWrapperTag
     */
    public void setRenderWrapperTag(boolean renderWrapperTag) {
        this.renderWrapperTag = renderWrapperTag;
    }

    /**
     * The message component structure is a list of components which represent the components that make up a message
     * when using rich message functionality.
     *
     * <p>The structure represents the parsed messageText when not set. Normally this structure is setup by the Message
     * class and <b>SHOULD NOT BE SET</b> in xml, unless full control over the structure is needed.  </p>
     *
     * @return list of components which represent the message structure
     */
    public List<Component> getMessageComponentStructure() {
        return messageComponentStructure;
    }

    /**
     * Set the message component structure.  This will override/ignore messageText when set. Normally
     * this <b>SHOULD NOT BE SET</b> by the xml configuration.
     *
     * @param messageComponentStructure list of components which represent the message structure
     */
    public void setMessageComponentStructure(List<Component> messageComponentStructure) {
        this.messageComponentStructure = messageComponentStructure;
    }

    /**
     * The inlineComponents are a list of components in order by index.
     *
     * <p>inlineComponents is only used when the message is using rich message functionality.  A message
     * with [0] will reference component at index 0 of this list and insert it at that place in the message,
     * and likewise [1] will reference item 1, etc.  If the index referenced is out of bounds (or list doesnt exist),
     * an error will be thrown during message parse.</p>
     *
     * @return the inlineComponents to be filled in at indexes referenced by [n] in the message
     */
    @BeanTagAttribute
    public List<Component> getInlineComponents() {
        return inlineComponents;
    }

    /**
     * Set the inlineComponents to be filled in at indexes referenced by [n] in the message
     *
     * @param inlineComponents the inlineComponents to be filled in at indexes referenced by [n] in the message
     */
    public void setInlineComponents(List<Component> inlineComponents) {
        this.inlineComponents = inlineComponents;
    }

    /**
     * Indicates if the inline components must be parsed for rich messages
     *
     * @return boolean
     */
    @BeanTagAttribute
    public boolean isParseComponents() {
        return parseComponents;
    }

    /**
     * Sets the parse components flag to indicate if inline components must be parsed for rich messages
     *
     * @param parseComponents
     */
    public void setParseComponents(boolean parseComponents) {
        this.parseComponents = parseComponents;
    }

    /**
     * If this message is considered a rich message (is using some rich message functionality with by using
     * the special [] tags), returns true
     *
     * @return return true if this message contains rich message content
     */
    public boolean isRichMessage() {
        return richMessage;
    }

    /**
     * True if the message contains block elements, or when it contains an unknown tag that may be a block element.
     *
     * @return true when the message contains block elements (non-inline elements), false otherwise
     */
    public boolean isContainsBlockElements() {
        return containsBlockElements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that text is set
        if (getMessageText() == null) {
            if (Validator.checkExpressions(this, "messageText")) {
                String currentValues[] = {"messageText  =" + getMessageText()};
                tracer.createWarning("MessageText should be set", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
