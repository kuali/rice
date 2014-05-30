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
package org.kuali.rice.krad.uif.control;

import java.util.List;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;

/**
 * Represents a HTML Checkbox control. Typically used for boolean attributes (where the
 * value is either on/off, true/false)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "checkboxControl", parent = "Uif-CheckboxControl")
public class CheckboxControl extends ControlBase implements ValueConfiguredControl {
    private static final long serialVersionUID = -1397028958569144230L;

    private String value;
    private String checkboxLabel;
    private boolean checked;

    private Message richLabelMessage;
    private List<Component> inlineComponents;

    public CheckboxControl() {
        super();
    }

    /**
     * Sets up rich message content for the label, if any exists
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        if (richLabelMessage == null) {
            Message message = ComponentFactory.getMessage();
            message.setMessageText(checkboxLabel);
            message.setInlineComponents(inlineComponents);
            message.setRenderWrapperTag(false);
            this.setRichLabelMessage(message);
        }
    }

    /**
     * The value that will be submitted when the checkbox control is checked
     *
     * <p>
     * Value can be left blank, in which case the checkbox will submit a boolean value that
     * will populate a boolean property. In cases where the checkbox needs to submit another value (for
     * instance possibly in the checkbox group) the value can be set which will override the default.
     * </p>
     *
     * @return value for checkbox
     */
    @BeanTagAttribute
    public String getValue() {
        return value;
    }

    /**
     * Setter for the value that should be submitted when the checkbox is checked
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the label text for this checkbox
     *
     * @return the checkbox label text
     */
    @BeanTagAttribute
    public String getCheckboxLabel() {
        return checkboxLabel;
    }

    /**
     * Sets the label text for this checkbox
     *
     * @param checkboxLabel the label text
     */
    public void setCheckboxLabel(String checkboxLabel) {
        this.checkboxLabel = checkboxLabel;
    }

    /**
     * Sets the checked state.
     *
     * @param checked - boolean true = checked, false = not checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * Returns true if checked, false if not checked.
     * @return true if checked
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Gets the Message that represents the rich message content of the label if labelText is using rich message tags.
     * <b>DO NOT set this
     * property directly unless you need full control over the message structure.</b>
     *
     * @return Message with rich message structure, null if no rich message structure
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
     * Gets the inlineComponents used by index in the checkboxLabel that has rich message component index tags
     *
     * @return the Label's inlineComponents
     */
    @BeanTagAttribute
    public List<Component> getInlineComponents() {
        return inlineComponents;
    }

    /**
     * Sets the inlineComponents used by index in the checkboxLabel that has rich message component index tags
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

        super.completeValidation(tracer.getCopy());
    }
}
