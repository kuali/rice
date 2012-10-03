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
package org.kuali.rice.krad.uif.control;

import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.KeyMessage;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for controls that accept/display multiple values
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class MultiValueControlBase extends ControlBase implements MultiValueControl {
    private static final long serialVersionUID = -8691367056245775455L;

    private List<KeyValue> options;
    private List<KeyMessage> richOptions;
    private List<Component> inlineComponents;

    public MultiValueControlBase() {
        super();
    }

    /**
     * Process rich message content that may be in the options, by creating and initializing the richOptions
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performApplyModel(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (options != null && richOptions == null) {
            richOptions = new ArrayList<KeyMessage>();

            for (KeyValue option : options) {
                Message message = ComponentFactory.getMessage();
                view.assignComponentIds(message);
                message.setMessageText(option.getValue());
                message.setInlineComponents(inlineComponents);
                message.setGenerateSpan(false);

                view.getViewHelperService().performComponentInitialization(view, model, message);
                richOptions.add(new KeyMessage(option.getKey(), option.getValue(), message));
            }
        }
    }

    /**
     * Adds appropriate parent data to inputs internal to the controls that may be in rich content of options
     *
     * @see Component#performFinalize(org.kuali.rice.krad.uif.view.View, Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (richOptions == null || richOptions.isEmpty()) {
            return;
        }

        //Messages included in options which have have rich message content need to be aware of their parent for
        //validation purposes
        for (KeyMessage richOption : richOptions) {
            List<Component> components = richOption.getMessage().getMessageComponentStructure();

            if (components != null && !components.isEmpty()) {
                for (Component c : components) {
                    if (c instanceof Container || c instanceof InputField) {
                        c.addDataAttribute("parent", parent.getId());
                    }
                }
            }
        }

    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        if (richOptions != null) {
            for (KeyMessage richOption : richOptions) {
                components.add(richOption.getMessage());
            }
        }
        return components;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.MultiValueControl#getOptions()
     */
    public List<KeyValue> getOptions() {
        return this.options;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.MultiValueControl#setOptions(java.util.List<org.kuali.rice.core.api.util.KeyValue>)
     */
    public void setOptions(List<KeyValue> options) {
        this.options = options;
    }

    /**
     * Gets the inlineComponents which represent components that can be referenced in an option's value
     * by index
     *
     * @return the components that can be used in rich values of options
     */
    public List<Component> getInlineComponents() {
        return inlineComponents;
    }

    /**
     * Sets the inlineComponents which represent components that can be referenced in an option's value
     * by index
     *
     * @param inlineComponents
     */
    public void setInlineComponents(List<Component> inlineComponents) {
        this.inlineComponents = inlineComponents;
    }

    /**
     * Gets the richOptions which contain Message objects with the translated rich message structures, which then can
     * be used by templates to output the appropriate content.
     *
     * @return richOptions which include a message object with the translated value content
     */
    public List<KeyMessage> getRichOptions() {
        return richOptions;
    }

    /**
     * Sets the richOptions.  This will always override/ignore options if set.
     *
     * <p><b>Messages MUST be defined</b> when using this setter, do not use this setter for most cases
     * as setting options through setOptions, with a richMessage value, is appropriate in MOST cases.  This
     * setter is only available for full control.</p>
     *
     * @param richOptions with their messages predefined
     */
    public void setRichOptions(List<KeyMessage> richOptions) {
        this.richOptions = richOptions;
    }
}
