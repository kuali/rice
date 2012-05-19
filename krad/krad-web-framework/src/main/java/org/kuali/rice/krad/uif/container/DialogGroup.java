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

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Message;

import java.util.List;

/**
 * Special type <code>Group</code> that presents a the content for a modal dialog
 *
 * <p>
 * This type of group will be hidden when the main view is displayed. It will be used as
 * content inside the LightBox widget when the modal dialog is displayed.
 * For convenience, this group contains some standard component properties for commonly used modal dialogs
 * <ul>
 *     <li>a message or question to display in the lightbox</li>
 *     <li>an optional text area for holding the user's textual response</li>
 *     <li>two actions. one representing an affirmative response, the other a negative response</li>
 * </ul>
 * The DialogGroup may also serve as a base class for more complex dialogs.
 * The default settings for this DialogGroup is to display a question message of "Are You Sure".
 * And two actions buttons labeled. OK and Cancel.  With OK on the left and Cancel on the Right.
 * The optional TextAreaControl is hidden by default.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DialogGroup extends Group {
    private static final long serialVersionUID = 1L;

    public static final String trueActionDefaultLabel = "OK";
    public static final String falseActionDefaultLabel = "Cancel";
    public static final String questionDefaultLabel = "Are You Sure";

    protected String questionText;
    protected String trueActionText;
    protected String falseActionText;

    protected Message question;
    protected TextAreaControl responseTextArea;
    protected Action trueAction;
    protected Action falseAction;
    
    boolean displayTrueActionFirst = true;
    boolean renderTextArea = false;

    public DialogGroup() {
        super();
    }


    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(question);
        components.add(responseTextArea);
        components.add(trueAction);
        components.add(falseAction);

        return components;
    }


    // Getters and Setters

    /**
     * Returns the text to be displayed as the question or main message in this simple dialog
     *
     * @return String containing the question text
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Sets the text String to display as the main message in this dialog
     *
     * @param questionText - the String to be displayed as the main message
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * Retrieves the  label text to be displayed on the positive action button
     *
     * @return The label to use on the positive action button
     */
    public String getTrueActionText() {
        return trueActionText;
    }

    /**
     * Sets the label text to be displayed on the positive action button
     *
     * @param trueActionText - String label for the positive button
     */
    public void setTrueActionText(String trueActionText) {
        this.trueActionText = trueActionText;
    }

    /**
     * Retrieves the  label text to be displayed on the negative action button
     *
     * @return The label to use on the nagative action button
     */
    public String getFalseActionText() {
        return falseActionText;
    }

    /**
     * Sets the label for the negative action button
     *
     * @param falseActionText - String label for the button
     */
    public void setFalseActionText(String falseActionText) {
        this.falseActionText = falseActionText;
    }

    /**
     * Retrieves the Message element for this dialog
     *
     * @return Message - the text element containing the message string
     */
    public Message getQuestion() {
        return question;
    }

    /**
     * Sets the question Message for this dialog
     *
     * @param question - The Message element for this dialog
     */
    public void setQuestion(Message question) {
        this.question = question;
    }

    /**
     * Retrieves the TextAreaControl used to gather user input text from the dialog
     *
     * @return TextAreaControl component within this dialog
     */
    public TextAreaControl getResponseTextArea() {
        return responseTextArea;
    }

    /**
     * Setter for the TextAreaControl for this basic dialog
     * @param responseTextArea
     */
    public void setResponseTextArea(TextAreaControl responseTextArea) {
        this.responseTextArea = responseTextArea;
    }

    /**
     * Gets the Action element representing an affirmative response from the user
     *
     * @return Action element for the positive response choice
     */
    public Action getTrueAction() {
        return trueAction;
    }

    /**
     * Setter for the Action element representing the affirmative or positive response from the user
     *
     * @param trueAction - Action element
     */
    public void setTrueAction(Action trueAction) {
        this.trueAction = trueAction;
    }

    /**
     * Retrieves the Action Element representing a negative response from the user
     *
     * @return Action Element for the negative response choice
     */
    public Action getFalseAction() {
        return falseAction;
    }

    /**
     * Setter for the Action element representing the negative response choice
     *
     * @param falseAction - Action element
     */
    public void setFalseAction(Action falseAction) {
        this.falseAction = falseAction;
    }

    /**
     * Determines the positioning order of the buttons
     *
     * @return - true if trueAction is to be displayed on the left (default),
     *           false if trueAction is to be displayed on the right
     */
    public boolean isDisplayTrueActionFirst() {
        return displayTrueActionFirst;
    }

    /**
     * Sets whether the true action button will be displayed on the right or left
     *
     * @param displayTrueActionFirst - boolean (true: button on left, false: button on right)
     */
    public void setDisplayTrueActionFirst(boolean displayTrueActionFirst) {
        this.displayTrueActionFirst = displayTrueActionFirst;
    }

    /**
     * Determines whether to render the optional TextAreaControl used to gather a textual
     * response from the user
     *
     * @return true if TextAreaControl is to be displayed, false if not
     */
    public boolean isRenderTextArea() {
        return renderTextArea;
    }

    /**
     * Sets the indicator used to determine whether to render the option TextAreaControl or not
     *
     * @param renderTextArea - boolean true if text area is to be rendered, false otherwise (default)
     */
    public void setRenderTextArea(boolean renderTextArea) {
        this.renderTextArea = renderTextArea;
    }

}
