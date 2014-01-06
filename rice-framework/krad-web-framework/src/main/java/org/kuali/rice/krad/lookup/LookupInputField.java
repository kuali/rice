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
package org.kuali.rice.krad.lookup;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.FilterableLookupCriteriaControl;
import org.kuali.rice.krad.uif.control.MultiValueControl;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.KeyMessage;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;

/**
 * Custom {@link InputField} for criteria fields within a lookup view that adds criteria specific options.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "lookupCriteriaInputField-bean", parent = "Uif-LookupCriteriaInputField")
public class LookupInputField extends InputField {
    private static final long serialVersionUID = -8294275596836322699L;

    private boolean disableWildcardsAndOperators;
    private boolean addControlSelectAllOption;
    private boolean ranged;

    public LookupInputField() {
        super();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Add all option if enabled and control is multi-value</li>
     * </ul>
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, Component parent) {
        super.performFinalize(model, parent);

        // if enabled add option to select all values
        if (addControlSelectAllOption && (getControl() != null) && getControl() instanceof MultiValueControl) {
            String allOptionText = KRADServiceLocatorWeb.getMessageService().getMessageText(
                    UifConstants.MessageKeys.OPTION_ALL);

            MultiValueControl multiValueControl = (MultiValueControl) getControl();
            if (multiValueControl.getOptions() != null) {
                multiValueControl.getOptions().add(0, new ConcreteKeyValue("", allOptionText));
            }

            if (multiValueControl.getRichOptions() != null) {
                Message message = ComponentFactory.getMessage();
                message.setMessageText(allOptionText);
                message.setGenerateSpan(false);

                multiValueControl.getRichOptions().add(0, new KeyMessage("", allOptionText, message));
            }
        }
    }

    /**
     * Override of InputField copy to setup properties necessary to make the field usable for inputting
     * search criteria.
     *
     * <p>Note super is not being called because we don't want to add restirctions that can cause problems
     * with the use of wildcard</p>
     *
     * {@inheritDoc}
     */
    @Override
    public void copyFromAttributeDefinition(AttributeDefinition attributeDefinition) {
        // label
        if (StringUtils.isEmpty(getLabel())) {
            setLabel(attributeDefinition.getLabel());
        }

        // short label
        if (StringUtils.isEmpty(getShortLabel())) {
            setShortLabel(attributeDefinition.getShortLabel());
        }

        // security
        if ((attributeDefinition.getAttributeSecurity() != null) && ((getDataFieldSecurity() == null) || (
                getDataFieldSecurity().getAttributeSecurity() == null))) {
            initializeComponentSecurity();

            getDataFieldSecurity().setAttributeSecurity(attributeDefinition.getAttributeSecurity());
        }

        // options
        if (getOptionsFinder() == null) {
            setOptionsFinder(attributeDefinition.getOptionsFinder());
        }

        // use control from dictionary if not specified and convert for searching
        if (getControl() == null) {
            Control control = convertControlToLookupControl(attributeDefinition);
            setControl(control);
        }

        // overwrite maxLength to allow for wildcards and ranges
        setMaxLength(100);

        // set default value for active field to true
        if (getDefaultValue() == null || (getDefaultValue() instanceof String && StringUtils.isEmpty((String)getDefaultValue()))) {
            if ((StringUtils.equals(getPropertyName(), KRADPropertyConstants.ACTIVE))) {
                setDefaultValue(KRADConstants.YES_INDICATOR_VALUE);
            }
        }
    }

    /**
     * If control definition is defined on the given attribute definition, converts to an appropriate control for
     * searching (if necessary) and returns a copy for setting on the field.
     *
     * @param attributeDefinition attribute definition instance to retrieve control from
     * @return Control instance or null if not found
     */
    protected static Control convertControlToLookupControl(AttributeDefinition attributeDefinition) {
        if (attributeDefinition.getControlField() == null) {
            return null;
        }

        Control newControl = null;

        // convert checkbox to radio with yes/no/both options
        if (CheckboxControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
            newControl = (RadioGroupControl) ComponentFactory.getNewComponentInstance(
                    ComponentFactory.CHECKBOX_CONVERTED_RADIO_CONTROL);
        }
        // text areas get converted to simple text inputs
        else if (TextAreaControl.class.isAssignableFrom(attributeDefinition.getControlField().getClass())) {
            newControl = ComponentFactory.getTextControl();
        } else {
            newControl = ComponentUtils.copy(attributeDefinition.getControlField(), "");
        }

        return newControl;
    }

    /**
     * Invoked before search is carried out to perform any necessary filtering of the criteria.
     *
     * @param searchCriteria the search criteria to be filtered
     * @return map of filtered search criteria
     */
    public Map<String, String> filterSearchCriteria(Map<String, String> searchCriteria) {
        if (getControl() instanceof FilterableLookupCriteriaControl) {
            return ((FilterableLookupCriteriaControl) getControl()).filterSearchCriteria(getPropertyName(),
                    searchCriteria);
        } else {
            return searchCriteria;
        }
    }

    /**
     * Indicates whether wildcard and other search operators should be disabled (treated as literals) for
     * the input field.
     *
     * @return boolean true if wildcards and search operators should be disabled, false if enabled
     */
    @BeanTagAttribute(name = "disableWildcardsAndOperators")
    public boolean isDisableWildcardsAndOperators() {
        return this.disableWildcardsAndOperators;
    }

    /**
     * @see LookupInputField#isDisableWildcardsAndOperators()
     */
    public void setDisableWildcardsAndOperators(boolean disableWildcardsAndOperators) {
        this.disableWildcardsAndOperators = disableWildcardsAndOperators;
    }

    /**
     * Indicates whether the option for all values (blank key, 'All' label) should be added to the lookup
     * field, note this is only supported for {@link org.kuali.rice.krad.uif.control.MultiValueControl} instance.
     *
     * @return boolean true if all option should be added, false if not
     */
    @BeanTagAttribute(name = "addControlSelectAllOption")
    public boolean isAddControlSelectAllOption() {
        return addControlSelectAllOption;
    }

    /**
     * @see LookupInputField#isAddControlSelectAllOption()
     */
    public void setAddControlSelectAllOption(boolean addControlSelectAllOption) {
        this.addControlSelectAllOption = addControlSelectAllOption;
    }

    /**
     * Indicates a field group should be created containing a from and to input field for date search
     * ranges.
     *
     * <p>
     * When this is set to true, the input field will be replaced by a field group that is created by
     * copying the prototype {@link org.kuali.rice.krad.lookup.LookupView#getRangeFieldGroupPrototype()}. Within the
     * field group, an lookup input field will be created for the from field, and this input will be used
     * as the to date field. Between the two fields a message will be rendered that can be specified using
     * {@link LookupView#getRangedToMessage()}
     * </p>
     *
     * @return boolean true if ranged field group should be created, false if not
     */
    public boolean isRanged() {
        return ranged;
    }

    /**
     * @see LookupInputField#isRanged()
     */
    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        LookupInputField lookupInputFieldCopy = (LookupInputField) component;

        lookupInputFieldCopy.setDisableWildcardsAndOperators(this.disableWildcardsAndOperators);
        lookupInputFieldCopy.setAddControlSelectAllOption(this.addControlSelectAllOption);
        lookupInputFieldCopy.setRanged(this.ranged);
    }
}
