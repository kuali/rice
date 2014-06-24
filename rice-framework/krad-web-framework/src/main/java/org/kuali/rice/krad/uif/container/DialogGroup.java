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
package org.kuali.rice.krad.uif.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Special type of group that presents the content in a modal dialog.
 *
 * <p>A dialog group can be used for many different purposes. First it can be used to give a simple confirmation (
 * a prompt with ok/cancel or yes/no options). The {@link org.kuali.rice.krad.uif.element.Action} component contains
 * properties for adding a confirmation dialog. Next, a dialog can be used to prompt for a response or to gather
 * addition data on the client. In this situation, the dialog is configured either in the view or external to the view,
 * and the developers triggers the display of the dialog using the javascript method showDialog. See krad.modal.js
 * for more information. Dialogs can also be triggered from a controller method (or other piece of server code). Again
 * the dialog is configured with the view or external to the view, and the controller method triggers the show using
 * the method {@link org.kuali.rice.krad.web.controller.UifControllerBase#showDialog}.</p>
 *
 * <p>A dialog is a group and can be configured like any other general group. For building basic dialogs, there are
 * convenience properties that can be used. In addition, there are base beans provided with definitions for these
 * properties. This includes a basic prompt message and responses. Note to have responses with different action properties,
 * set the items of the dialog groups footer directly.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "dialog", parent = "Uif-DialogGroup"),
        @BeanTag(name = "dialogOkCancel", parent = "Uif-DialogGroup-OkCancel"),
        @BeanTag(name = "dialogOkCancelExpl", parent = "Uif-DialogGroup-OkCancelExpl"),
        @BeanTag(name = "dialogYesNo", parent = "Uif-DialogGroup-YesNo"),
        @BeanTag(name = "actionConfirmation", parent = "Uif-ActionConfirmation"),
        @BeanTag(name = "actionConfirmationExpl", parent = "Uif-ActionConfirmationExpl")})
public class DialogGroup extends GroupBase {
    private static final long serialVersionUID = 1L;

    private MessageField prompt;
    private InputField explanation;

    private List<KeyValue> availableResponses;

    private String dialogCssClass;

    private String onDialogResponseScript;
    private String onShowDialogScript;
    private String onHideDialogScript;

    /**
     * Default Constructor.
     */
    public DialogGroup() {
        super();
        dialogCssClass = "modal-sm";
    }

    /**
     * The following actions are performed in this phase:
     *
     * <ul>
     * <li>If property name nor binding path is set on the explanation field, sets to generic form property</li>
     * <li>Move custom dialogGroup properties prompt and explanation into items collection if the
     * items list is not already populated</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if ((explanation != null) && StringUtils.isBlank(explanation.getPropertyName()) && StringUtils.isBlank(
                explanation.getBindingInfo().getBindingPath())) {
            explanation.setPropertyName(UifPropertyPaths.DIALOG_EXPLANATIONS + "['" + getId() + "']");
            explanation.getBindingInfo().setBindToForm(true);
        }

        if ((getItems() == null) || getItems().isEmpty()) {
            List<Component> items = new ArrayList<Component>();

            if (prompt != null) {
                items.add(prompt);
            }

            if (explanation != null) {
                items.add(explanation);
            }

            setItems(items);
        }
    }

    /**
     * The following actions are performed in this phase:
     *
     * <ul>
     * <li>For each configured key value response, create an action component and add to the footer items.</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        // create action in footer for each configured key value response
        if ((availableResponses != null) && !availableResponses.isEmpty()) {
            List<Component> footerItems = new ArrayList<Component>();

            for (KeyValue keyValue : availableResponses) {
                Action responseAction = ComponentFactory.getSecondaryAction();

                responseAction.setDialogDismissOption(UifConstants.DialogDismissOption.PRESUBMIT.name());
                responseAction.setDialogResponse(keyValue.getKey());

                responseAction.setActionLabel(keyValue.getValue());

                footerItems.add(responseAction);
            }

            if (getFooter() == null) {
                setFooter(ComponentFactory.getFooter());
            }

            if (getFooter().getItems() != null) {
                footerItems.addAll(getFooter().getItems());
            }

            getFooter().setItems(footerItems);
        }
    }

    /**
     * The following actions are performed in this phase:
     *
     * <ul>
     * <li>Add data attributes for any configured event handlers</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (StringUtils.isNotBlank(this.onDialogResponseScript)) {
            addDataAttribute(UifConstants.DataAttributes.DIALOG_RESPONSE_HANDLER, this.onDialogResponseScript);
        }

        if (StringUtils.isNotBlank(this.onShowDialogScript)) {
            addDataAttribute(UifConstants.DataAttributes.DIALOG_SHOW_HANDLER, this.onShowDialogScript);
        }

        if (StringUtils.isNotBlank(this.onHideDialogScript)) {
            addDataAttribute(UifConstants.DataAttributes.DIALOG_HIDE_HANDLER, this.onHideDialogScript);
        }

        // Dialogs do not have a visual "parent" on the page so remove this data attribute
        this.getDataAttributes().remove(UifConstants.DataAttributes.PARENT);
    }

    /**
     * Text to be displayed as the prompt or main message in this simple dialog.
     *
     * <p>This is a convenience method for setting the message text on {@link DialogGroup#getPrompt()}</p>
     *
     * @return String containing the prompt text
     */

    @BeanTagAttribute
    public String getPromptText() {
        if (prompt != null) {
            return prompt.getMessage().getMessageText();
        }

        return null;
    }

    /**
     * @see DialogGroup#getPromptText()
     */
    public void setPromptText(String promptText) {
        if (prompt == null) {
            prompt = ComponentFactory.getMessageField();
        }

        prompt.setMessageText(promptText);
    }

    /**
     * Message component to use for the dialog prompt.
     *
     * @return Message component for prompt
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public MessageField getPrompt() {
        return prompt;
    }

    /**
     * @see DialogGroup#getPrompt()
     */
    public void setPrompt(MessageField prompt) {
        this.prompt = prompt;
    }

    /**
     * Input field use to gather explanation text with the dialog.
     *
     * <p>By default, the control for this input is configured as a TextAreaControl. It may be configured for
     * other types of input fields.</p>
     *
     * @return InputField component
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public InputField getExplanation() {
        return explanation;
    }

    /**
     * @see DialogGroup#getExplanation()
     */
    public void setExplanation(InputField explanation) {
        this.explanation = explanation;
    }

    /**
     * List of options that are available for the user to choice as a response to the dialog.
     *
     * <p>If given, the list of key value pairs is used to create action components that are inserted into the
     * dialog footer. The key will be used as the response value, and the value as the label for the action.</p>
     *
     * <p>Note responses can be also be created by populating the footer items with action components.</p>
     *
     * @return the List of response actions to provide the user
     */
    @BeanTagAttribute
    public List<KeyValue> getAvailableResponses() {
        return availableResponses;
    }

    /**
     * @see DialogGroup#getAvailableResponses()
     */
    public void setAvailableResponses(List<KeyValue> availableResponses) {
        this.availableResponses = availableResponses;
    }

    /**
     * Gets CSS class to use when rendering dialog (default is modal-sm).
     *
     * @return String of CSS class
     */
    @BeanTagAttribute
    public String getDialogCssClass() {
        return dialogCssClass;
    }

    public void setDialogCssClass(String dialogCssClass) {
        this.dialogCssClass = dialogCssClass;
    }

    /**
     * Script that will be invoked when the dialog response event is thrown.
     *
     * <p>The dialog group will throw a custom event type 'dialogresponse.uif' when an response action within the
     * dialog is selected. Script given here will bind to that event as a handler</p>
     *
     * <p>The event object contains:
     * event.response - response value for the action that was selected
     * event.action - jQuery object for the action element that was selected
     * event.dialogId - id for the dialog the response applies to</p>
     *
     * @return js that will execute for the response event
     */
    @BeanTagAttribute
    public String getOnDialogResponseScript() {
        return onDialogResponseScript;
    }

    /**
     * @see DialogGroup#getOnDialogResponseScript()
     */
    public void setOnDialogResponseScript(String onDialogResponseScript) {
        this.onDialogResponseScript = onDialogResponseScript;
    }

    /**
     * Script that will get invoked when the dialog group is shown.
     *
     * <p>Initially a dialog group will either be hidden in the DOM or not present at all (if retrieved via Ajax).
     * When the dialog is triggered and shown, a show event will be thrown and this script will
     * be executed</p>
     *
     * @return js code to execute when the dialog is shown
     */
    @BeanTagAttribute
    public String getOnShowDialogScript() {
        return onShowDialogScript;
    }

    /**
     * @see DialogGroup#getOnShowDialogScript()
     */
    public void setOnShowDialogScript(String onShowDialogScript) {
        this.onShowDialogScript = onShowDialogScript;
    }

    /**
     * Script that will get invoked when the dialog group is hidden.
     *
     * @return js code to execute when the dialog is hidden
     */
    public String getOnHideDialogScript() {
        return onHideDialogScript;
    }

    /**
     * @see DialogGroup#getOnHideDialogScript()
     */
    public void setOnHideDialogScript(String onHideDialogScript) {
        this.onHideDialogScript = onHideDialogScript;
    }
}
