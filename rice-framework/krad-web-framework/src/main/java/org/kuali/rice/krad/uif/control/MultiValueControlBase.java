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

import org.kuali.rice.core.api.util.AbstractKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.KeyMessage;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.UifKeyValueLocation;
import org.kuali.rice.krad.uif.util.UifOptionGroupLabel;
import org.kuali.rice.krad.uif.util.UrlInfo;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
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

    private List<Message> internalMessageComponents;

    private boolean locationSelect = false;

    public MultiValueControlBase() {
        super();
    }

    /**
     * Process rich message content that may be in the options, by creating and initializing the richOptions
     *
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);
getStyleClassesAsString();
        if (options != null && richOptions == null) {
            richOptions = new ArrayList<KeyMessage>();
            internalMessageComponents = new ArrayList<Message>();

            for (KeyValue option : options) {

                // do this??
                if (option instanceof UifOptionGroupLabel) {
                    continue;
                }

                Message message = ComponentFactory.getMessage();

                String key = option.getKey();
                if (key.contains(UifConstants.EL_PLACEHOLDER_PREFIX)) {
                    key = (String) ViewLifecycle.getExpressionEvaluator().evaluateExpression(this.getContext(),
                            key);
                }

                String value = option.getValue();
                if (value.contains(UifConstants.EL_PLACEHOLDER_PREFIX)) {
                    value = (String) ViewLifecycle.getExpressionEvaluator().evaluateExpression(this.getContext(),
                            value);
                }

                message.setMessageText(value);
                message.setInlineComponents(inlineComponents);
                message.setRenderWrapperTag(false);

                // if the option is a sub-class of AbstractKeyValue class, then we also include the disabled attribute
                if(AbstractKeyValue.class.isAssignableFrom(option.getClass()) && ((AbstractKeyValue)option).isDisabled()) {
                    richOptions.add(new KeyMessage(key, value, message, ((AbstractKeyValue)option).isDisabled()));
                } else {
                    richOptions.add(new KeyMessage(key, value, message));
                }

                internalMessageComponents.add(message);
            }
        }
    }

    /**
     * Adds appropriate parent data to inputs internal to the controls that may be in rich content of options
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        View view = ViewLifecycle.getView();
        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        if (options != null && !options.isEmpty()) {
            for (KeyValue option : options) {
                if (option instanceof UifKeyValueLocation) {
                    locationSelect = true;

                    UrlInfo url = ((UifKeyValueLocation) option).getLocation();

                    ViewLifecycle.getExpressionEvaluator().populatePropertyExpressionsFromGraph(url, false);
                    expressionEvaluator.evaluateExpressionsOnConfigurable(view, url, view.getContext());
                }
            }
        }

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
                        c.addDataAttribute(UifConstants.DataAttributes.PARENT, parent.getId());
                    }
                }
            }
        }

    }

    /**
     * @see MultiValueControl#getOptions()
     */
    @BeanTagAttribute
    public List<KeyValue> getOptions() {
        return this.options;
    }

    /**
     * {@inheritDoc}
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
    @BeanTagAttribute
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
     * @see MultiValueControl#getRichOptions()
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

    /**
     * Used by reflection during the lifecycle to get internal message components that may be contained in options
     *
     * <p>There are no references to this method in the code, this is intentional.  DO NOT REMOVE.</p>
     *
     * @return the internal message components, if any
     */
    public List<Message> getInternalMessageComponents() {
        return internalMessageComponents;
    }

    /**
     * If true, this select represents a location select (navigate on select of option)
     *
     * @return true if this is a location select
     */
    public boolean isLocationSelect() {
        return locationSelect;
    }

    /**
     * Sets the location select (navigate on select of option)
     *
     * @param locationSelect
     */
    protected void setLocationSelect(boolean locationSelect) {
        this.locationSelect = locationSelect;
    }
}
