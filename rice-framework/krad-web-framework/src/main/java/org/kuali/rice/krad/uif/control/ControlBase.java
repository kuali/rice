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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.ContentElementBase;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;

/**
 * Base class for all {@link Control} implementations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "controlBase", parent = "Uif-ControlBase")
public abstract class ControlBase extends ContentElementBase implements Control {
    private static final long serialVersionUID = -7898244978136312663L;

    private int tabIndex;

    private boolean disabled;
    private String disabledExpression;
    private String disabledReason;
    private boolean evaluateDisabledOnKeyUp;

    private String disabledConditionJs;
    private List<String> disabledConditionControlNames;

    private List<String> disabledWhenChangedPropertyNames;
    private List<String> enabledWhenChangedPropertyNames;

    public ControlBase() {
        super();

        disabled = false;
        disabledWhenChangedPropertyNames = new ArrayList<String>();
        enabledWhenChangedPropertyNames = new ArrayList<String>();
    }

    /**
     * Sets the disabledExpression, if any, evaluates it and sets the disabled property
     *
     * @param model top level object containing the data (could be the form or a
     * top level business object, dto)
     * @param parent
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        disabledExpression = this.getPropertyExpression("disabled");
        if (disabledExpression != null) {
            ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

            disabledExpression = expressionEvaluator.replaceBindingPrefixes(ViewLifecycle.getView(), this,
                    disabledExpression);
            disabled = (Boolean) expressionEvaluator.evaluateExpression(this.getContext(), disabledExpression);
        }
    }

    /**
     * Parses the disabled expressions, if any, to equivalent javascript and evaluates the disable/enable when
     * changed property names.
     *
     * @param model top level object containing the data
     * @param parent parent component
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        ExpressionEvaluator expressionEvaluator = ViewLifecycle.getExpressionEvaluator();

        if (StringUtils.isNotEmpty(disabledExpression)
                && !disabledExpression.equalsIgnoreCase("true")
                && !disabledExpression.equalsIgnoreCase("false")) {
            disabledConditionControlNames = new ArrayList<String>();
            disabledConditionJs = expressionEvaluator.parseExpression(disabledExpression, disabledConditionControlNames,
                    this.getContext());
        }

        View view = ViewLifecycle.getView();
        List<String> adjustedDisablePropertyNames = new ArrayList<String>();
        for (String propertyName : disabledWhenChangedPropertyNames) {
            adjustedDisablePropertyNames.add(expressionEvaluator.replaceBindingPrefixes(view, this, propertyName));
        }
        disabledWhenChangedPropertyNames = adjustedDisablePropertyNames;

        List<String> adjustedEnablePropertyNames = new ArrayList<String>();
        for (String propertyName : enabledWhenChangedPropertyNames) {
            adjustedEnablePropertyNames.add(expressionEvaluator.replaceBindingPrefixes(view, this, propertyName));
        }
        enabledWhenChangedPropertyNames = adjustedEnablePropertyNames;

        // add control role
        this.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.CONTROL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getComponentTypeName() {
        return "control";
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    public int getTabIndex() {
        return this.tabIndex;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.Control#setTabIndex(int)
     */
    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.Control#isDisabled()
     */
    @BeanTagAttribute
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.Control#setDisabled(boolean)
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.Control#getDisabledReason()
     */
    @BeanTagAttribute
    public String getDisabledReason() {
        return disabledReason;
    }

    /**
     * @see org.kuali.rice.krad.uif.control.Control#setDisabledReason(java.lang.String)
     */
    public void setDisabledReason(String disabledReason) {
        this.disabledReason = disabledReason;
    }

    /**
     * Evaluate the disable condition on controls which disable it on each key up event
     *
     * @return true if evaluate on key up, false otherwise
     */
    @BeanTagAttribute
    public boolean isEvaluateDisabledOnKeyUp() {
        return evaluateDisabledOnKeyUp;
    }

    /**
     * Set evaluateDisableOnKeyUp
     *
     * @param evaluateDisabledOnKeyUp
     */
    public void setEvaluateDisabledOnKeyUp(boolean evaluateDisabledOnKeyUp) {
        this.evaluateDisabledOnKeyUp = evaluateDisabledOnKeyUp;
    }

    /**
     * Get the disable condition js derived from the springEL, cannot be set.
     *
     * @return the disableConditionJs javascript to be evaluated
     */
    public String getDisabledConditionJs() {
        return disabledConditionJs;
    }

    /**
     * Control names to add handlers to for disable functionality, cannot be set
     *
     * @return control names to add handlers to for disable
     */
    public List<String> getDisabledConditionControlNames() {
        return disabledConditionControlNames;
    }

    /**
     * Gets the property names of fields that when changed, will disable this component
     *
     * @return the property names to monitor for change to disable this component
     */
    @BeanTagAttribute
    public List<String> getDisabledWhenChangedPropertyNames() {
        return disabledWhenChangedPropertyNames;
    }

    /**
     * Sets the property names of fields that when changed, will disable this component
     *
     * @param disabledWhenChangedPropertyNames
     */
    public void setDisabledWhenChangedPropertyNames(List<String> disabledWhenChangedPropertyNames) {
        this.disabledWhenChangedPropertyNames = disabledWhenChangedPropertyNames;
    }

    /**
     * Gets the property names of fields that when changed, will enable this component
     *
     * @return the property names to monitor for change to enable this component
     */
    @BeanTagAttribute
    public List<String> getEnabledWhenChangedPropertyNames() {
        return enabledWhenChangedPropertyNames;
    }

    /**
     * Sets the property names of fields that when changed, will enable this component
     *
     * @param enabledWhenChangedPropertyNames
     */
    public void setEnabledWhenChangedPropertyNames(List<String> enabledWhenChangedPropertyNames) {
        this.enabledWhenChangedPropertyNames = enabledWhenChangedPropertyNames;
    }

    /**
     * Sets the disabled expression
     *
     * @param disabledExpression
     */
    protected void setDisabledExpression(String disabledExpression) {
        this.disabledExpression = disabledExpression;
    }

    /**
     * Sets the disabled condition javascript
     *
     * @param disabledConditionJs
     */
    protected void setDisabledConditionJs(String disabledConditionJs) {
        this.disabledConditionJs = disabledConditionJs;
    }

    /**
     * Sets the disabled condition control names
     *
     * @param disabledConditionControlNames
     */
    protected void setDisabledConditionControlNames(List<String> disabledConditionControlNames) {
        this.disabledConditionControlNames = disabledConditionControlNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        super.completeValidation(tracer.getCopy());
    }
}
