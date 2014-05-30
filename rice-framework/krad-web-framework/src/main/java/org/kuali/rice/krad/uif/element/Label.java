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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Content element that renders a label
 *
 * <p>
 * Contains options for adding a colon to the label along with a required message
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "label", parent = "Uif-Label")
public class Label extends ContentElementBase {
    private static final long serialVersionUID = -6491546893195180114L;

    private String labelText;
    private String labelForComponentId;

    private boolean renderColon;

    private String requiredIndicator;
    private boolean renderRequiredIndicator;

    private Message richLabelMessage;
    private List<Component> inlineComponents;

    public Label() {
        renderColon = true;
    }

    /**
     * Sets up rich message content for the label, if any exists
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (richLabelMessage == null && labelText != null &&
                labelText.contains(KRADConstants.MessageParsing.LEFT_TOKEN) &&
                labelText.contains(KRADConstants.MessageParsing.RIGHT_TOKEN)) {
            Message message = ComponentFactory.getMessage();
            message.setMessageText(labelText);
            message.setInlineComponents(inlineComponents);
            message.setRenderWrapperTag(false);

            this.setRichLabelMessage(message);
        }

    }

    /**
     * The following finalization is performed:
     *
     * <ul>
     * <li>If label text is blank, set render to false for field</li>
     * <li>Set the requiredIndicator</li>
     * <li>Set the label text on the label field from the field's label property</li>
     * <li>Set the render property on the label's required message field if this field is marked as required</li>
     * <li>If the label is hidden then add CSS class to render off screen for accessibility</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        if (StringUtils.isBlank(getLabelText())) {
            setRender(false);
        }

        String defaultRequiredIndicator = (String) KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                UifConstants.REQUIRED_INDICATOR_ID);

        if (requiredIndicator != null && !requiredIndicator.equals(defaultRequiredIndicator)) {
            this.addDataAttribute(UifConstants.DataAttributes.REQ_INDICATOR, requiredIndicator);
        } else if (requiredIndicator == null) {
            requiredIndicator = defaultRequiredIndicator;
        }

        if ((getRequired() != null) && getRequired().booleanValue()) {
            setRenderRequiredIndicator(true);
        }

        // if hidden then add CSS class to render label off screen for accessibility
        if (this.isHidden()) {
            this.addStyleClass("sr-only");
        }
    }

    /**
     * Indicates the id for the component the label applies to
     * <p>
     * Used for setting the labelFor attribute of the corresponding HTML
     * element. Note this gets set automatically by the framework during the
     * initialize phase
     * </p>
     *
     * @return component id
     */
    @BeanTagAttribute
    public String getLabelForComponentId() {
        return this.labelForComponentId;
    }

    /**
     * Setter for the component id the label applies to
     *
     * @param labelForComponentId
     */
    public void setLabelForComponentId(String labelForComponentId) {
        this.labelForComponentId = labelForComponentId;
    }

    /**
     * Text that will display as the label
     *
     * @return label text
     */
    @BeanTagAttribute
    public String getLabelText() {
        return this.labelText;
    }

    /**
     * Setter for the label text
     *
     * @param labelText
     */
    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    /**
     * Indicates whether a colon should be rendered after the label text,
     * generally used when the label appears to the left of the field's control
     * or value
     *
     * @return true if a colon should be rendered, false if it should not be
     */
    @BeanTagAttribute
    public boolean isRenderColon() {
        return this.renderColon;
    }

    /**
     * Setter for the render colon indicator
     *
     * @param renderColon
     */
    public void setRenderColon(boolean renderColon) {
        this.renderColon = renderColon;
    }

    /**
     * True if the indicator will be displayed when this label is first render, false otherwise.
     *
     * <p>This is set by the framework based on required constraint state, and generally should NOT
     * be set in most cases.</p>
     *
     * @return true if rendering, false otherwise
     */
    public boolean isRenderRequiredIndicator() {
        return renderRequiredIndicator;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Label#isRenderRequiredIndicator()
     *
     * @param renderRequiredIndicator
     */
    public void setRenderRequiredIndicator(boolean renderRequiredIndicator) {
        this.renderRequiredIndicator = renderRequiredIndicator;
    }

    /**
     * String indicator that will be displayed as a required indicator
     *
     * <p>
     * To indicate a field must have a value (required input) the required
     * indicator can be set to display an indicator or text along with
     * the label.
     * </p>
     *
     * @return the required indicator String to display
     */
    @BeanTagAttribute
    public String getRequiredIndicator() {
        return requiredIndicator;
    }

    /**
     * @see org.kuali.rice.krad.uif.element.Label#getRequiredIndicator()
     *
     * @param requiredIndicator
     */
    public void setRequiredIndicator(String requiredIndicator) {
        this.requiredIndicator = requiredIndicator;
    }

    /**
     * Gets the Message that represents the rich message content of the label if labelText is using rich message tags.
     * <b>DO NOT set this
     * property directly unless you need full control over the message structure.</b>
     *
     * @return rich message structure, null if no rich message structure
     */
    @BeanTagAttribute
    public Message getRichLabelMessage() {
        return richLabelMessage;
    }

    /**
     * Sets the Message that represents the rich message content of the label if it is using rich message tags.  <b>DO
     * NOT set this
     * property directly unless you need full control over the message structure.</b>
     *
     * @param richLabelMessage
     */
    public void setRichLabelMessage(Message richLabelMessage) {
        this.richLabelMessage = richLabelMessage;
    }

    /**
     * Gets the inlineComponents used by index in a Label that has rich message component index tags in its labelText
     *
     * @return the Label's inlineComponents
     */
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public List<Component> getInlineComponents() {
        return inlineComponents;
    }

    /**
     * Sets the inlineComponents used by index in a Label that has rich message component index tags in its labelText
     *
     * @param inlineComponents
     */
    public void setInlineComponents(List<Component> inlineComponents) {
        this.inlineComponents = inlineComponents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer){
        tracer.addBean(this);

        if(tracer.getValidationStage()== ValidationTrace.BUILD){
            // Checks that text is set if the component is rendered
            if(isRender() && getLabelText()==null){
                if(!Validator.checkExpressions(this, "labelText")) {
                    String currentValues [] = {"render = "+isRender(),"labelText ="+getLabelText()};
                    tracer.createError("LabelText should be set if render is true",currentValues);
                }
            }
        }

        super.completeValidation(tracer.getCopy());
    }
}
