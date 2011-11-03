/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.bo.DataObjectRelationship;
import org.kuali.rice.krad.bo.KualiCode;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.control.MultiValueControl;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinder;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.MultiValueControlBase;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.util.ClientValidationUtils;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.DirectInquiry;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.uif.widget.Suggest;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.valuefinder.ValueFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Field that encapsulates data input/output captured by an attribute within the
 * application
 *
 * <p>                                                                                                                                    R
 * The <code>AttributField</code> provides the majority of the data input/output
 * for the screen. Through these fields the model can be displayed and updated.
 * For data input, the field contains a <code>Control</code> instance will
 * render an HTML control element(s). The attribute field also contains a
 * <code>LabelField</code>, summary, and widgets such as a quickfinder (for
 * looking up values) and inquiry (for getting more information on the value).
 * <code>InputField</code> instances can have associated messages (errors)
 * due to invalid input or business rule failures. Security can also be
 * configured to restrict who may view the fields value.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InputField extends DataField {
    private static final long serialVersionUID = -3703656713706343840L;

    // constraint variables
    private String customValidatorClass;
    private ValidCharactersConstraint validCharactersConstraint;
    private CaseConstraint caseConstraint;
    private List<PrerequisiteConstraint> dependencyConstraints;
    private List<MustOccurConstraint> mustOccurConstraints;
    private SimpleConstraint simpleConstraint;

    // display props
    private Control control;
    private KeyValuesFinder optionsFinder;
    private boolean performUppercase;

    private String errorMessagePlacement;
    private ErrorsField errorsField;

    // messages
    private String constraintText;
    private String instructionalText;

    private MessageField instructionalMessageField;
    private MessageField constraintMessageField;

    private AttributeQuery fieldAttributeQuery;

    // widgets
    private QuickFinder fieldLookup;
    private DirectInquiry fieldDirectInquiry;
    private Suggest fieldSuggest;

    public InputField() {
        super();

        simpleConstraint = new SimpleConstraint();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Set the ids for the various attribute components</li>
     * <li>Sets up the client side validation for constraints on this field. In
     * addition, it sets up the messages applied to this field</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.component.ComponentBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        setupIds();

        // invoke options finder if options not configured on the control
        List<KeyValue> fieldOptions = new ArrayList<KeyValue>();

        // use options directly configured on the control first
        if ((control != null) && control instanceof MultiValueControlBase) {
            MultiValueControlBase multiValueControl = (MultiValueControlBase) control;
            if ((multiValueControl.getOptions() != null) && !multiValueControl.getOptions().isEmpty()) {
                fieldOptions = multiValueControl.getOptions();
            }
        }

        // if options not configured on the control, invoke configured options finder
        if (fieldOptions.isEmpty() && (optionsFinder != null)) {
            if (optionsFinder instanceof UifKeyValuesFinder) {
                fieldOptions = ((UifKeyValuesFinder) optionsFinder).getKeyValues((ViewModel) model);

                // check if blank option should be added
                if (((UifKeyValuesFinder) optionsFinder).isAddBlankOption()) {
                    fieldOptions.add(0, new ConcreteKeyValue("", ""));
                }
            } else {
                fieldOptions = optionsFinder.getKeyValues();
            }

            if ((control != null) && control instanceof MultiValueControlBase) {
               ((MultiValueControlBase) control).setOptions(fieldOptions);
            }
        }

        // if read only do key/value translation if necessary (if alternative and additional properties not set)
        if (isReadOnly()
                && !fieldOptions.isEmpty()
                && StringUtils.isBlank(getAlternateDisplayValue())
                && StringUtils.isBlank(getAdditionalDisplayValue())
                && StringUtils.isBlank(getAlternateDisplayPropertyName())
                && StringUtils.isBlank(getAdditionalDisplayPropertyName())) {

            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());
            for (KeyValue keyValue : fieldOptions) {
                if (StringUtils.equals((String) fieldValue, keyValue.getKey())) {
                    setAlternateDisplayValue(keyValue.getValue());
                    break;
                }
            }
        }

        // if read only or the control is null no input can be given so no need to setup validation
        if (isReadOnly() || getControl() == null) {
            return;
        }

        // Sets message
        if (StringUtils.isNotBlank(instructionalText)) {
            instructionalMessageField.setMessageText(instructionalText);
        }

        // Sets constraints
        if (StringUtils.isNotBlank(constraintText)) {
            constraintMessageField.setMessageText(constraintText);
        }

        // adjust paths on PrerequisiteConstraint property names
        adjustPrerequisiteConstraintBinding(dependencyConstraints);

        // adjust paths on MustOccurConstraints property names
        adjustMustOccurConstraintBinding(mustOccurConstraints);

        // adjust paths on CaseConstraint property names
        if (caseConstraint != null) {
            String propertyName = getBindingInfo().getPropertyAdjustedBindingPath(caseConstraint.getPropertyName());
            caseConstraint.setPropertyName(propertyName);
        }        

        setupFieldQuery();

        if (view instanceof FormView && ((FormView) view).isValidateClientSide()) {
            ClientValidationUtils.processAndApplyConstraints(this, view);
        }
    }

    protected void adjustMustOccurConstraintBinding(List<MustOccurConstraint> mustOccurConstraints) {
        if (mustOccurConstraints != null) {
            for (MustOccurConstraint mustOccurConstraint : mustOccurConstraints) {
                adjustPrerequisiteConstraintBinding(mustOccurConstraint.getPrerequisiteConstraints());
                adjustMustOccurConstraintBinding(mustOccurConstraint.getMustOccurConstraints());
            }
        }
    }

    protected void adjustPrerequisiteConstraintBinding(List<PrerequisiteConstraint> prerequisiteConstraints) {
        if (prerequisiteConstraints != null) {
            for (PrerequisiteConstraint prerequisiteConstraint : prerequisiteConstraints) {
                String propertyName = getBindingInfo().getPropertyAdjustedBindingPath(prerequisiteConstraint.getPropertyName());
                prerequisiteConstraint.setPropertyName(propertyName);
            }
        }
    }

    /**
     * Performs setup of the field attribute query and informational display properties. Paths
     * are adjusted to match the binding for the this field, and the necessary onblur script for
     * triggering the query client side is constructed
     */
    protected void setupFieldQuery() {
        if (getFieldAttributeQuery() != null) {
            // adjust paths on query mappings
            getFieldAttributeQuery().updateQueryFieldMapping(getBindingInfo());
            getFieldAttributeQuery().updateReturnFieldMapping(getBindingInfo());
            getFieldAttributeQuery().updateQueryMethodArgumentFieldList(getBindingInfo());

            // build onblur script for field query
            String script = "executeFieldQuery('" + getControl().getId() + "',";
            script += "'" + getId() + "'," + getFieldAttributeQuery().getQueryFieldMappingJsString() + ",";
            script += getFieldAttributeQuery().getQueryMethodArgumentFieldsJsString() + ",";
            script += getFieldAttributeQuery().getReturnFieldMappingJsString() + ");";

            if (StringUtils.isNotBlank(getControl().getOnBlurScript())) {
                script = getControl().getOnBlurScript() + script;
            }
            getControl().setOnBlurScript(script);
        }
    }

    /**
     * Sets the ids on all components the attribute field uses so they will all
     * contain this attribute's id in their ids. This is useful for jQuery
     * manipulation.
     */
    protected void setupIds() {
        // update ids so they all match the attribute
        if (getControl() != null) {
            getControl().setId(getId());
        }

        setNestedComponentIdAndSuffix(getErrorsField(), UifConstants.IdSuffixes.ERRORS);
        setNestedComponentIdAndSuffix(getLabelField(), UifConstants.IdSuffixes.LABEL);
        setNestedComponentIdAndSuffix(getInstructionalMessageField(), UifConstants.IdSuffixes.INSTRUCTIONAL);
        setNestedComponentIdAndSuffix(getConstraintMessageField(), UifConstants.IdSuffixes.CONSTRAINT);
        setNestedComponentIdAndSuffix(getFieldLookup(), UifConstants.IdSuffixes.QUICK_FINDER);
        setNestedComponentIdAndSuffix(getFieldDirectInquiry(), UifConstants.IdSuffixes.DIRECT_INQUIRY);
        setNestedComponentIdAndSuffix(getFieldSuggest(), UifConstants.IdSuffixes.SUGGEST);

        setId(getId() + UifConstants.IdSuffixes.ATTRIBUTE);
    }

    /**
     * Helper method for suffixing the ids of the fields nested components
     *
     * @param component - component to adjust id for
     * @param suffix - suffix to append to id
     */
    private void setNestedComponentIdAndSuffix(Component component, String suffix) {
        if (component != null) {
            String fieldId = getId();
            fieldId += suffix;

            component.setId(fieldId);
        }
    }

    /**
     * Defaults the properties of the <code>InputField</code> to the
     * corresponding properties of its <code>AttributeDefinition</code>
     * retrieved from the dictionary (if such an entry exists). If the field
     * already contains a value for a property, the definitions value is not
     * used.
     *
     * @param view - view instance the field belongs to
     * @param attributeDefinition - AttributeDefinition instance the property values should be
     * copied from
     */
    public void copyFromAttributeDefinition(View view, AttributeDefinition attributeDefinition) {
        super.copyFromAttributeDefinition(view, attributeDefinition);

        // max length
        if (getMaxLength() == null) {
            setMaxLength(attributeDefinition.getMaxLength());
        }

        // min length
        if (getMinLength() == null) {
            setMinLength(attributeDefinition.getMinLength());
        }

        // valid characters
        if (getValidCharactersConstraint() == null) {
            setValidCharactersConstraint(attributeDefinition.getValidCharactersConstraint());
        }

        if (getCaseConstraint() == null) {
            setCaseConstraint(attributeDefinition.getCaseConstraint());
        }

        if (getDependencyConstraints() == null) {
            setDependencyConstraints(attributeDefinition.getPrerequisiteConstraints());
        }

        if (getMustOccurConstraints() == null) {
            setMustOccurConstraints(attributeDefinition.getMustOccurConstraints());
        }

        // required
        if (getRequired() == null) {
            setRequired(attributeDefinition.isRequired());

            //if still null, default to false
            if (getRequired() == null) {
                setRequired(false);
            }
        }

        // control
        if ((getControl() == null) && (attributeDefinition.getControlField() != null)) {
            Control control = attributeDefinition.getControlField();
            view.assignComponentIds(control);

            setControl(ComponentUtils.copy(control));
        }

        // constraint
        if (StringUtils.isEmpty(getConstraintText())) {
            setConstraintText(attributeDefinition.getConstraintText());
            getConstraintMessageField().setMessageText(attributeDefinition.getConstraintText());
        }

        // options
        if (getOptionsFinder() == null) {
            setOptionsFinder(attributeDefinition.getOptionsFinder());
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(control);
        components.add(errorsField);
        components.add(fieldLookup);
        components.add(fieldDirectInquiry);
        components.add(fieldSuggest);

        return components;
    }

    /**
     * @see DataField#isInputAllowed()
     */
    @Override
    public boolean isInputAllowed() {
        return true;
    }

    /**
     * <code>Control</code> instance that should be used to input data for the
     * field
     *
     * <p>
     * When the field is editable, the control will be rendered so the user can
     * input a value(s). Controls typically are part of a Form and render
     * standard HTML control elements such as text input, select, and checkbox
     * </p>
     *
     * @return Control instance
     */
    public Control getControl() {
        return this.control;
    }

    /**
     * Setter for the field's control
     *
     * @param control
     */
    public void setControl(Control control) {
        this.control = control;
    }

    public String getErrorMessagePlacement() {
        return this.errorMessagePlacement;
    }

    public void setErrorMessagePlacement(String errorMessagePlacement) {
        this.errorMessagePlacement = errorMessagePlacement;
    }

    /**
     * Field that contains the messages (errors) for the attribute field. The
     * <code>ErrorsField</code> holds configuration on associated messages along
     * with information on rendering the messages in the user interface
     *
     * @return ErrorsField instance
     */
    public ErrorsField getErrorsField() {
        return this.errorsField;
    }

    /**
     * Setter for the attribute field's errors field
     *
     * @param errorsField
     */
    public void setErrorsField(ErrorsField errorsField) {
        this.errorsField = errorsField;
    }

    /**
     * Instance of <code>KeyValluesFinder</code> that should be invoked to
     * provide a List of values the field can have. Generally used to provide
     * the options for a multi-value control or to validate the submitted field
     * value
     *
     * @return KeyValuesFinder instance
     */
    public KeyValuesFinder getOptionsFinder() {
        return this.optionsFinder;
    }

    /**
     * Setter for the field's KeyValuesFinder instance
     *
     * @param optionsFinder
     */
    public void setOptionsFinder(KeyValuesFinder optionsFinder) {
        this.optionsFinder = optionsFinder;
    }

    /**
     * Setter that takes in the class name for the options finder and creates a
     * new instance to use as the finder for the attribute field
     *
     * @param optionsFinderClass
     */
    public void setOptionsFinderClass(Class<? extends KeyValuesFinder> optionsFinderClass) {
        this.optionsFinder = ObjectUtils.newInstance(optionsFinderClass);
    }

    /**
     * Text explaining how to use the field, including things like what values should be selected
     * in certain cases and so on (instructions)
     *
     * @return String instructional message
     */
    public String getInstructionalText() {
        return this.instructionalText;
    }

    /**
     * Setter for the instructional message
     *
     * @param instructionalText
     */
    public void setInstructionalText(String instructionalText) {
        this.instructionalText = instructionalText;
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getSupportsOnLoad()
     */
    @Override
    public boolean getSupportsOnLoad() {
        return true;
    }

    /**
     * Lookup finder widget for the field
     *
     * <p>
     * The quickfinder widget places a small icon next to the field that allows
     * the user to bring up a search screen for finding valid field values. The
     * <code>Widget</code> instance can be configured to point to a certain
     * <code>LookupView</code>, or the framework will attempt to associate the
     * field with a lookup based on its metadata (in particular its
     * relationships in the model)
     * </p>
     *
     * @return QuickFinder lookup widget
     */
    public QuickFinder getFieldLookup() {
        return this.fieldLookup;
    }

    /**
     * Setter for the lookup widget
     *
     * @param fieldLookup
     */
    public void setFieldLookup(QuickFinder fieldLookup) {
        this.fieldLookup = fieldLookup;
    }

    /**
     * Suggest box widget for the attribute field
     *
     * <p>
     * If enabled (by render flag), as the user inputs data into the
     * fields control a dynamic query is performed to provide the user
     * suggestions on values which they can then select
     * </p>
     *
     * <p>
     * Note the Suggest widget is only valid when using a standard TextControl
     * </p>
     *
     * @return Suggest instance
     */
    public Suggest getFieldSuggest() {
        return fieldSuggest;
    }

    /**
     * Setter for the fields Suggest widget
     *
     * @param fieldSuggest
     */
    public void setFieldSuggest(Suggest fieldSuggest) {
        this.fieldSuggest = fieldSuggest;
    }

    /**
     * Message field that displays instructional text
     *
     * <p>
     * This message field can be configured to for adjusting how the instructional text will display. Generally
     * the styleClasses property will be of most interest
     * </p>
     *
     * @return MessageField instructional message field
     */
    public MessageField getInstructionalMessageField() {
        return this.instructionalMessageField;
    }

    /**
     * Setter for the instructional text message field
     *
     * <p>
     * Note this is the setter for the field that will render the instructional text. The actual text can be
     * set on the field but can also be set using {@link #setInstructionalText(String)}
     * </p>
     *
     * @param instructionalMessageField
     */
    public void setInstructionalMessageField(MessageField instructionalMessageField) {
        this.instructionalMessageField = instructionalMessageField;
    }

    /**
     * Text that display a restriction on the value a field can hold
     *
     * <p>
     * For example when the value must be a valid format (phone number, email), certain length, min/max value and
     * so on this text can be used to indicate the constraint to the user. Generally displays with the control so
     * it is visible when the user tabs to the field
     * </p>
     *
     * @return String text to display for the constraint message
     */
    public String getConstraintText() {
        return this.constraintText;
    }

    /**
     * Setter for the constraint message text
     *
     * @param constraintText
     */
    public void setConstraintText(String constraintText) {
        this.constraintText = constraintText;
    }

    /**
     * Message field that displays constraint text
     *
     * <p>
     * This message field can be configured to for adjusting how the constrain text will display. Generally
     * the styleClasses property will be of most interest
     * </p>
     *
     * @return MessageField constraint message field
     */
    public MessageField getConstraintMessageField() {
        return this.constraintMessageField;
    }

    /**
     * Setter for the constraint text message field
     *
     * <p>
     * Note this is the setter for the field that will render the constraint text. The actual text can be
     * set on the field but can also be set using {@link #setConstraintText(String)}
     * </p>
     *
     * @param constraintMessageField
     */
    public void setConstraintMessageField(MessageField constraintMessageField) {
        this.constraintMessageField = constraintMessageField;
    }

    /**
     * Valid character constraint that defines regular expressions for the valid
     * characters for this field
     *
     * @return the validCharactersConstraint
     */
    public ValidCharactersConstraint getValidCharactersConstraint() {
        return this.validCharactersConstraint;
    }

    /**
     * @param validCharactersConstraint the validCharactersConstraint to set
     */
    public void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint) {
        this.validCharactersConstraint = validCharactersConstraint;
    }

    /**
     * @return the caseConstraint
     */
    public CaseConstraint getCaseConstraint() {
        return this.caseConstraint;
    }

    /**
     * @param caseConstraint the caseConstraint to set
     */
    public void setCaseConstraint(CaseConstraint caseConstraint) {
        this.caseConstraint = caseConstraint;
    }

    /**
     * @return the dependencyConstraints
     */
    public List<PrerequisiteConstraint> getDependencyConstraints() {
        return this.dependencyConstraints;
    }

    /**
     * @param dependencyConstraints the dependencyConstraints to set
     */
    public void setDependencyConstraints(List<PrerequisiteConstraint> dependencyConstraints) {
        this.dependencyConstraints = dependencyConstraints;
    }

    /**
     * @return the mustOccurConstraints
     */
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * @param mustOccurConstraints the mustOccurConstraints to set
     */
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * A simple constraint which store the values for required, min/max length,
     * and min/max value
     *
     * @return the simpleConstraint
     */
    public SimpleConstraint getSimpleConstraint() {
        return this.simpleConstraint;
    }

    /**
     * When a simple constraint is set on this object ALL simple validation
     * constraints set directly will be overridden - recommended to use this or
     * the other gets/sets for defining simple constraints, not both
     *
     * @param simpleConstraint the simpleConstraint to set
     */
    public void setSimpleConstraint(SimpleConstraint simpleConstraint) {
        this.simpleConstraint = simpleConstraint;
    }

    /**
     * Maximum number of the characters the attribute value is allowed to have.
     * Used to set the maxLength for supporting controls. Note this can be
     * smaller or longer than the actual control size
     *
     * @return Integer max length
     */
    public Integer getMaxLength() {
        return simpleConstraint.getMaxLength();
    }

    /**
     * Setter for attributes max length
     *
     * @param maxLength
     */
    public void setMaxLength(Integer maxLength) {
        simpleConstraint.setMaxLength(maxLength);
    }

    /**
     * @return the minLength
     */
    public Integer getMinLength() {
        return simpleConstraint.getMinLength();
    }

    /**
     * @param minLength the minLength to set
     */
    public void setMinLength(Integer minLength) {
        simpleConstraint.setMinLength(minLength);
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getRequired()
     */
    @Override
    public Boolean getRequired() {
        return this.simpleConstraint.getRequired();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#setRequired(java.lang.Boolean)
     */
    @Override
    public void setRequired(Boolean required) {
        this.simpleConstraint.setRequired(required);
    }

    /**
     * The exclusiveMin element determines the minimum allowable value for data
     * entry editing purposes. Value can be an integer or decimal value such as
     * -.001 or 99.
     */
    public String getExclusiveMin() {
        return simpleConstraint.getExclusiveMin();
    }

    /**
     * @param exclusiveMin the minValue to set
     */
    public void setExclusiveMin(String exclusiveMin) {
        simpleConstraint.setExclusiveMin(exclusiveMin);
    }

    /**
     * The inclusiveMax element determines the maximum allowable value for data
     * entry editing purposes. Value can be an integer or decimal value such as
     * -.001 or 99.
     */
    public String getInclusiveMax() {
        return simpleConstraint.getInclusiveMax();
    }

    /**
     * @param inclusiveMax the maxValue to set
     */
    public void setInclusiveMax(String inclusiveMax) {
        simpleConstraint.setInclusiveMax(inclusiveMax);
    }

    /**
     * Setter for the direct inquiry widget
     *
     * @param fieldDirectInquiry - field DirectInquiry to set
     */
    public void setFieldDirectInquiry(DirectInquiry fieldDirectInquiry) {
        this.fieldDirectInquiry = fieldDirectInquiry;
    }

    /**
     * DirectInquiry widget for the field
     *
     * <p>
     * The direct inquiry widget will render a button for the field value when
     * that field is editable. It points to the associated inquiry view for the
     * field. The inquiry can be configured to point to a certain
     * <code>InquiryView</code>, or the framework will attempt to associate the
     * field with a inquiry based on its metadata (in particular its
     * relationships in the model)
     * </p>
     *
     * @return the <code>DirectInquiry</code> field DirectInquiry
     */
    public DirectInquiry getFieldDirectInquiry() {
        return fieldDirectInquiry;
    }

    /**
     * Attribute query instance configured for this field to dynamically pull information back for
     * updates other fields or providing messages
     *
     * <p>
     * If field attribute query is not null, associated event script will be generated to trigger the
     * query from the UI. This will invoke the <code>AttributeQueryService</code> to
     * execute the query and return an instance of <code>AttributeQueryResult</code> that is then
     * read by the script to update the UI. Typically used to update informational property values or
     * other field values
     * </p>
     *
     * @return AttributeQuery instance
     */
    public AttributeQuery getFieldAttributeQuery() {
        return fieldAttributeQuery;
    }

    /**
     * Setter for this fields query
     *
     * @param fieldAttributeQuery
     */
    public void setFieldAttributeQuery(AttributeQuery fieldAttributeQuery) {
        this.fieldAttributeQuery = fieldAttributeQuery;
    }

    /**
     * Perform uppercase flag for this field to force input to uppercase.
     *
     * <p>
     * It this flag is set to true the 'text-transform' style on the field will be set to 'uppercase'
     * which will automatically change any text input into the field to uppercase.
     * </p>
     *
     * @return performUppercase flag
     */
    public boolean isPerformUppercase() {
        return performUppercase;
    }

    /**
     * Setter for this fields performUppercase flag
     *
     * @param performUppercase flag
     */
    public void setPerformUppercase(boolean performUppercase) {
        this.performUppercase = performUppercase;
    }
}
