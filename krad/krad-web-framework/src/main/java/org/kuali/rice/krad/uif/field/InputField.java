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
package org.kuali.rice.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.validation.capability.CaseConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.Formatable;
import org.kuali.rice.krad.datadictionary.validation.capability.HierarchicallyConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.LengthConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.MustOccurConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.PrerequisiteConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.RangeConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.SimpleConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.ValidCharactersConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinder;
import org.kuali.rice.krad.uif.view.FormView;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.MultiValueControlBase;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.util.ClientValidationUtils;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.DirectInquiry;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.uif.widget.Suggest;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Field that encapsulates data input/output captured by an attribute within the
 * application
 *
 * <p>
 * R
 * The <code>InputField</code> provides the majority of the data input/output
 * for the screen. Through these fields the model can be displayed and updated.
 * For data input, the field contains a {@link Control} instance will
 * render an HTML control element(s). The input field also contains a
 * {@link LabelField}, summary, and widgets such as a quickfinder (for
 * looking up values) and inquiry (for getting more information on the value).
 * <code>InputField</code> instances can have associated messages (errors)
 * due to invalid input or business rule failures. Security can also be
 * configured to restrict who may view the fields value.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InputField extends DataField implements SimpleConstrainable, CaseConstrainable, PrerequisiteConstrainable,
        MustOccurConstrainable, LengthConstrainable, RangeConstrainable, ValidCharactersConstrainable {
    private static final long serialVersionUID = -3703656713706343840L;

    // constraint variables
    private String customValidatorClass;
    private ValidCharactersConstraint validCharactersConstraint;
    private CaseConstraint caseConstraint;
    private List<PrerequisiteConstraint> dependencyConstraints;
    private List<MustOccurConstraint> mustOccurConstraints;
    private SimpleConstraint simpleConstraint;
    private DataType dataType;

    // display props
    private Control control;
    private KeyValuesFinder optionsFinder;
    private boolean performUppercase;

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
    private Boolean directInquiryRender = true;

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

            // TODO: can we translate Collections? (possibly combining output with delimiter
            if ((fieldValue != null) && (TypeUtils.isSimpleType(fieldValue.getClass()))) {
                for (KeyValue keyValue : fieldOptions) {
                    if (StringUtils.equals((String) fieldValue, keyValue.getKey())) {
                        setAlternateDisplayValue(keyValue.getValue());
                        break;
                    }
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

        ClientValidationUtils.processAndApplyConstraints(this, view);
    }

    /**
     * Adjust paths on the must occur constrain bindings
     *
     * @param mustOccurConstraints
     */
    protected void adjustMustOccurConstraintBinding(List<MustOccurConstraint> mustOccurConstraints) {
        if (mustOccurConstraints != null) {
            for (MustOccurConstraint mustOccurConstraint : mustOccurConstraints) {
                adjustPrerequisiteConstraintBinding(mustOccurConstraint.getPrerequisiteConstraints());
                adjustMustOccurConstraintBinding(mustOccurConstraint.getMustOccurConstraints());
            }
        }
    }

    /**
     * Adjust paths on the prerequisite constraint bindings
     * @param prerequisiteConstraints
     */
    protected void adjustPrerequisiteConstraintBinding(List<PrerequisiteConstraint> prerequisiteConstraints) {
        if (prerequisiteConstraints != null) {
            for (PrerequisiteConstraint prerequisiteConstraint : prerequisiteConstraints) {
                String propertyName = getBindingInfo().getPropertyAdjustedBindingPath(
                        prerequisiteConstraint.getPropertyName());
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
     * Sets the ids on all components the input field uses so they will all
     * contain this input field's id in their ids. This is useful for jQuery
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
        
        if (this.dataType == null) {
            setDataType(attributeDefinition.getDataType());
            //Assume date if dataType is still null and using a DatePicker
            if(this.dataType == null && control instanceof TextControl && ((TextControl) control).getDatePicker() != null) {
                setDataType(DataType.DATE);
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

    /**
     * Field that contains the messages (errors) for the input field. The
     * <code>ErrorsField</code> holds configuration on associated messages along
     * with information on rendering the messages in the user interface
     *
     * @return ErrorsField instance
     */
    public ErrorsField getErrorsField() {
        return this.errorsField;
    }

    /**
     * Setter for the input field's errors field
     *
     * @param errorsField
     */
    public void setErrorsField(ErrorsField errorsField) {
        this.errorsField = errorsField;
    }

    /**
     * Instance of <code>KeyValuesFinder</code> that should be invoked to
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
     * new instance to use as the finder for the input field
     *
     * @param optionsFinderClass - the options finder class to set
     */
    public void setOptionsFinderClass(Class<? extends KeyValuesFinder> optionsFinderClass) {
        this.optionsFinder = ObjectUtils.newInstance(optionsFinderClass);
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
     * @param fieldLookup - the field lookup widget to set
     */
    public void setFieldLookup(QuickFinder fieldLookup) {
        this.fieldLookup = fieldLookup;
    }

    /**
     * Suggest box widget for the input field
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
     * Setter for the fields suggest widget
     *
     * @param fieldSuggest - the field suggest widget to  set
     */
    public void setFieldSuggest(Suggest fieldSuggest) {
        this.fieldSuggest = fieldSuggest;
    }

    /**
     * Instructional text that display an explanation of the field usage
     *
     * <p>
     * Text explaining how to use the field, including things like what values should be selected
     * in certain cases and so on (instructions)
     * </p>
     *
     * @return String instructional message
     */
    public String getInstructionalText() {
        return this.instructionalText;
    }

    /**
     * Setter for the instructional message
     *
     * @param instructionalText - the instructional text to set
     */
    public void setInstructionalText(String instructionalText) {
        this.instructionalText = instructionalText;
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
     * @param instructionalMessageField - the instructional message to set
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
     * @param constraintText - the constraint text to set
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
     * @param constraintMessageField - the constrain message field to set
     */
    public void setConstraintMessageField(MessageField constraintMessageField) {
        this.constraintMessageField = constraintMessageField;
    }

    /**
     * The <code>ValideCharacterConstraint</code> that applies to this <code>InputField</code>
     *
     * @return the valid characters constraint for this input field
     */
    public ValidCharactersConstraint getValidCharactersConstraint() {
        return this.validCharactersConstraint;
    }

    /**
     * Setter for <code>validCharacterConstraint</code>
     *
     * @param validCharactersConstraint - the <code>ValidCharactersConstraint</code> to set
     */
    public void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint) {
        this.validCharactersConstraint = validCharactersConstraint;
    }

    /**
     * The <code>CaseConstraint</code> that applies to this <code>InputField</code>
     *
     * @return the case constraint for this input field
     */
    public CaseConstraint getCaseConstraint() {
        return this.caseConstraint;
    }

    /**
     * Setter for <code>caseConstraint</code>
     *
     * @param caseConstraint - the <code>CaseConstraint</code> to set
     */
    public void setCaseConstraint(CaseConstraint caseConstraint) {
        this.caseConstraint = caseConstraint;
    }

    /**
     * List of <code>PrerequisiteConstraint</code> that apply to this <code>InputField</code>
     *
     * @return the dependency constraints for this input field
     */
    public List<PrerequisiteConstraint> getDependencyConstraints() {
        return this.dependencyConstraints;
    }

    /**
     * Setter for <code>dependencyConstraints</code>
     *
     * @param dependencyConstraints - list of <code>PrerequisiteConstraint</code> to set
     */
    public void setDependencyConstraints(List<PrerequisiteConstraint> dependencyConstraints) {
        this.dependencyConstraints = dependencyConstraints;
    }

    /**
     * List of <code>MustOccurConstraint</code> that apply to this <code>InputField</code>
     *
     * @return the must occur constraints for this input field
     */
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * Setter for <code>mustOccurConstraints</code>
     *
     * @param mustOccurConstraints - list of <code>MustOccurConstraint</code> to set
     */
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * Simple constraints for the input field
     *
     * <p>
     * A simple constraint which store the values for constraints such as required,
     * min/max length, and min/max value.
     * </p>
     *
     * @return the simple constraint of the input field
     */
    public SimpleConstraint getSimpleConstraint() {
        return this.simpleConstraint;
    }

    /**
     * Setter for simple constraint
     *
     * <p>
     * When a simple constraint is set on this object ALL simple validation
     * constraints set directly will be overridden - recommended to use this or
     * the other gets/sets for defining simple constraints, not both.
     * </p>
     *
     * @param simpleConstraint - the simple constraint to set
     */
    public void setSimpleConstraint(SimpleConstraint simpleConstraint) {
        this.simpleConstraint = simpleConstraint;
    }

    /**
     * Maximum number of characters the input field value is allowed to have
     *
     * <p>
     * The maximum length determines the maximum allowable length of the value
     * for data entry editing purposes.  The maximum length is inclusive and can
     * be smaller or longer than the actual control size.  The constraint
     * is enforced on all data types (e.g. a numeric data type needs to meet the
     * maximum length constraint in which digits and symbols are counted).
     * </p>
     *
     * @return the maximum length of the input field
     */
    public Integer getMaxLength() {
        return simpleConstraint.getMaxLength();
    }

    /**
     * Setter for input field max length
     *
     * @param maxLength - the maximum length to set
     */
    public void setMaxLength(Integer maxLength) {
        simpleConstraint.setMaxLength(maxLength);
    }

    /**
     * Minimum number of characters the input field value needs to be
     *
     * <p>
     * The minimum length determines the minimum required length of the value for
     * data entry editing purposes.  The minimum length is inclusive. The constraint
     * is enforced on all data types (e.g. a numeric data type needs to meet the
     * minimum length requirement in which digits and symbols are counted).
     * </p>
     *
     * @return the minimum length of the input field
     */
    public Integer getMinLength() {
        return simpleConstraint.getMinLength();
    }

    /**
     * Setter for input field minimum length
     *
     * @param minLength - the minLength to set
     */
    public void setMinLength(Integer minLength) {
        simpleConstraint.setMinLength(minLength);
    }
    
    public Boolean getDirectInquiryRender() {
        return this.directInquiryRender;
    }
    
    public void setDirectInquiryRender(Boolean directInquiryRender) {
        this.directInquiryRender = directInquiryRender;
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
     * The exclusive minimum value for numeric or date field.
     *
     * <p>
     * The exclusiveMin element determines the minimum allowable value for data
     * entry editing purposes. This constrain is supported for numeric and
     * date fields and to be used in conjunction with the appropriate
     * {@link ValidCharactersConstraint}.
     *
     * For numeric constraint the value can be an integer or decimal such as -.001 or 99.
     * </p>
     *
     * @return the exclusive minimum numeric value of the input field
     */
    public String getExclusiveMin() {
        return simpleConstraint.getExclusiveMin();
    }

    /**
     * Setter for the field's exclusive minimum value
     *
     * @param exclusiveMin - the minimum value to set
     */
    public void setExclusiveMin(String exclusiveMin) {
        simpleConstraint.setExclusiveMin(exclusiveMin);
    }

    /**
     * The inclusive maximum value for numeric or date field.
     *
     * <p>
     * The inclusiveMax element determines the maximum allowable value for data
     * entry editing purposes. This constrain is supported for numeric and
     * date fields and to be used in conjunction with the appropriate
     * {@link ValidCharactersConstraint}.
     *
     * For numeric constraint the value can be an integer or decimal such as -.001 or 99.
     * </p>
     *
     * @return the inclusive maximum numeric value of the input field
     */
    public String getInclusiveMax() {
        return simpleConstraint.getInclusiveMax();
    }

    /**
     * Setter for the field's inclusive maximum value
     *
     * @param inclusiveMax - the maximum value to set
     */
    public void setInclusiveMax(String inclusiveMax) {
        simpleConstraint.setInclusiveMax(inclusiveMax);
    }

    /**
     * <code>DirectInquiry</code> widget for the field
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
     * Setter for the field's direct inquiry widget
     *
     * @param fieldDirectInquiry - the <code>DirectInquiry</code> to set
     */
    public void setFieldDirectInquiry(DirectInquiry fieldDirectInquiry) {
        this.fieldDirectInquiry = fieldDirectInquiry;
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
     * Setter for this field's attribute query
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
     * Setter for this field's performUppercase flag
     *
     * @param performUppercase - boolean flag
     */
    public void setPerformUppercase(boolean performUppercase) {
        this.performUppercase = performUppercase;
    }

    /**
     * Returns the full binding path (the path used in the name attribute of the input).
     * This differs from propertyName in that it uses BindingInfo to determine the path.
     * @return full binding path name
     */
    @Override
    public String getName() {
        return this.getBindingInfo().getBindingPath();
    }

    public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
        return dependencyConstraints;
    }

    /**
     * This does not have to be set, represents the DataType constraint of this field.
     * This is only checked during server side validation.
     * @param dataType the dataType to set
     */
    public void setDataType(DataType dataType) {
        this.simpleConstraint.setDataType(dataType);
    }

    public void setDataType(String dataType) {
        this.simpleConstraint.setDataType(DataType.valueOf(dataType));
    }

    /**
     * Gets the DataType of this InputField, note that DataType set to be date
     * when this field is using a date picker with a TextControl and hasnt otherwise been
     * explicitly set.
     * @return
     */
    public DataType getDataType() {
        return this.simpleConstraint.getDataType();
    }
}
