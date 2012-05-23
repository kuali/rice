/*
 * Copyright 2006-2012 The Kuali Foundation
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

import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.InputField;

import java.util.List;

/**
 * Special type <code>Group</code> that presents a the content for a modal dialog
 *
 * <p>
 * This type of group will be hidden when the main view is displayed. It will be used as
 * content inside the LightBox widget when the modal dialog is displayed.
 * For convenience, this group contains some standard component properties for commonly used modal dialogs
 * <ul>
 *     <li>a prompt to display in the lightbox</li>
 *     <li>an optional text area for holding the user's textual response</li>
 *     <li>two actions. one representing an affirmative response, the other a negative response</li>
 * </ul>
 * The DialogGroup may also serve as a base class for more complex dialogs.
 * The default settings for this DialogGroup is to display a prompt message of "Are You Sure".
 * And two actions buttons labeled. OK and Cancel.  With OK on the left and Cancel on the Right.
 * The optional TextAreaControl is hidden by default.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DialogGroup extends Group {
    private static final long serialVersionUID = 1L;

    public static final String promptDefaultLabel = "Are You Sure";
    protected String promptText;

    protected Message prompt;
    protected InputField explanation;
    protected List<KeyValue> availableResponses;
    protected InputField responseInputField;

    boolean displayOrderLeftToRight = true;
    boolean displayExplanation = false;

    public DialogGroup() {
        super();
    }


    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(prompt);
        components.add(explanation);
        components.add(responseInputField);

        return components;
    }

    // Getters and Setters

    /**
     * Returns the text to be displayed as the prompt or main message in this simple dialog
     *
     * @return String containing the prompt text
     */
    public String getPromptText() {
        return promptText;
    }

    /**
     * Sets the text String to display as the main message in this dialog
     *
     * @param promptText - the String to be displayed as the main message
     */
    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    /**
     * Retrieves the Message element for this dialog
     *
     * @return Message - the text element containing the message string
     */
    public Message getPrompt() {
        return prompt;
    }

    /**
     * Sets the prompt Message for this dialog
     *
     * @param prompt - The Message element for this dialog
     */
    public void setPrompt(Message prompt) {
        this.prompt = prompt;
    }


    /**
     * Retrieves the InputField used to gather user input text from the dialog
     *
     * <p>
     * By default, the control for this input is configured as a TextAreaControl
     * </p>
     *
     * @return InputField component
     */
    public InputField getExplanation() {
        return explanation;
    }

    /**
     * Sets the InputField for gathering user text input
     *
     * @param explanation - InputField
     */
    public void setExplanation(InputField explanation) {
        this.explanation = explanation;
    }

    /**
     * determines if the explanation InputField is to be displayed in this dialog
     *
     * <p>
     *     False by default.
     * </p>
     * @return
     */
    public boolean isDisplayExplanation() {
        return displayExplanation;
    }

    /**
     * Sets whether to display the Explanation InputField on this dialog
     *
     * @param displayExplanation
     */
    public void setDisplayExplanation(boolean displayExplanation) {
        this.displayExplanation = displayExplanation;
    }

    /**
     * Gets the choices provided for user response.
     *
     * <p>
     *     A List of KeyValue pairs for each of the choices provided on this dialog.
     * </p>
     *
     * @return the List of response actions to provide the user.
     */
    public List<KeyValue> getAvailableResponses() {
        return availableResponses;
    }

    /**
     * Sets the list of user responses to provide on this dialog
     *
     * @param availableResponses
     */
    public void setAvailableResponses(List<KeyValue> availableResponses) {
        this.availableResponses = availableResponses;
    }

    /**
     * Retrieves the InputField containing the choices displayed in this dialog
     *
     * <p>
     *     By default, this InputField is configured to be a HorizontalCheckboxControl.
     *     Styling is then used to make the checkboxes appear to be buttons.
     *     The values of the availableResponses List are used as labels for the "buttons".
     * </p>
     *
     * @return InputField component within this dialog
     */
    public InputField getResponseInputField() {
        return responseInputField;
    }

    /**
     * Sets the type of InputField used to display the user choices in this dialog
     *
     * @param responseInputField
     */
    public void setResponseInputField(InputField responseInputField) {
        this.responseInputField = responseInputField;
    }

    /**
     * Determines the positioning order of the choices displayed on this dialog
     *
     * <p>
     *     Some page designers like the positive choice on the left and the negative choice on the right.
     *     Others, prefer just the opposite. This allows the order to easily be switched.
     * </p>
     *
     * @return - true if choices left to right
     *           false if choices right to left
     */
    public boolean isDisplayOrderLeftToRight() {
        return displayOrderLeftToRight;
    }

    /**
     * Sets the display order of the choices displayed on this dialog
     *
     * <p>
     *     By default, the choices are displayed left to right
     * </p>
     *
     * @param displayOrderLeftToRight
     */
    public void setDisplayOrderLeftToRight(boolean displayOrderLeftToRight) {
        this.displayOrderLeftToRight = displayOrderLeftToRight;
    }
}
