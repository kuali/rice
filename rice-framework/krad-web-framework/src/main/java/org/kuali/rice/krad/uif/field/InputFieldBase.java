/**
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.krad.data.DataObjectUtils;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.datadictionary.state.StateMapping;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.datadictionary.validator.Validator;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.DelayedCopy;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.MultiValueControlBase;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinder;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.element.ValidationMessages;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.util.ClientValidationUtils;
import org.kuali.rice.krad.uif.util.CloneUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ConstraintStateUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.uif.widget.Suggest;

/**
 * Field that encapsulates data input/output captured by an attribute within the
 * application
 *
 * <p>
 * The {@code InputField} provides the majority of the data input/output
 * for the screen. Through these fields the model can be displayed and updated.
 * For data input, the field contains a {@link Control} instance will
 * render an HTML control element(s). The input field also contains a
 * {@link Label}, summary, and widgets such as a quickfinder (for
 * looking up values) and inquiry (for getting more information on the value).
 * {@code InputField} instances can have associated messages (errors)
 * due to invalid input or business rule failures. Security can also be
 * configured to restrict who may view the fields valnue.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "inputField-bean", parent = "Uif-InputField"),
        @BeanTag(name = "inputField-labelTop-bean", parent = "Uif-InputField-LabelTop"),
        @BeanTag(name = "inputField-labelRight-bean", parent = "Uif-InputField-LabelRight"),
        @BeanTag(name = "checkboxInputField-bean", parent = "Uif-CheckboxInputField"),
        @BeanTag(name = "dialogResponse-bean", parent = "Uif-DialogResponse"),
        @BeanTag(name = "dialogExplanation-bean", parent = "Uif-DialogExplanation"),
        @BeanTag(name = "documentNumber-bean", parent = "Uif-DocumentNumber"),
        @BeanTag(name = "documentStatus-bean", parent = "Uif-DocumentStatus"),
        @BeanTag(name = "documentInitiatorNetworkId-bean", parent = "Uif-DocumentInitiatorNetworkId"),
        @BeanTag(name = "documentCreateDate-bean", parent = "Uif-DocumentCreateDate"),
        @BeanTag(name = "documentTemplateNumber-bean", parent = "Uif-DocumentTemplateNumber"),
        @BeanTag(name = "documentDescription-bean", parent = "Uif-DocumentDescription"),
        @BeanTag(name = "documentExplaination-bean", parent = "Uif-DocumentExplaination"),
        @BeanTag(name = "organizationDocumentNumber-bean", parent = "Uif-OrganizationDocumentNumber"),
        @BeanTag(name = "selectCollectionItemField-bean", parent = "Uif-SelectCollectionItemField")})
public class InputFieldBase extends DataFieldBase implements InputField {
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

    private boolean uppercaseValue;
    private boolean disableNativeAutocomplete;

    @DelayedCopy
    private ValidationMessages validationMessages;

    // messages
    private String constraintText;
    private String instructionalText;

    private Message constraintMessage;
    private Message instructionalMessage;

    private String helperText;

    private AttributeQuery attributeQuery;

    // widgets
    private boolean enableAutoDirectInquiry;

    private QuickFinder quickfinder;
    private boolean enableAutoQuickfinder;

    private Suggest suggest;

    private boolean widgetInputOnly;

    private boolean renderInputAddonGroup;
    private List<String> postInputCssClasses;
    private List<Component> postInputAddons;

    public InputFieldBase() {
        super();

        simpleConstraint = new SimpleConstraint();

        enableAutoDirectInquiry = true;
        enableAutoQuickfinder = true;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#performInitialization(java.lang.Object)
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if (!isReadOnly()) {
            if ((StringUtils.isNotBlank(constraintText) || (getPropertyExpression("constraintText") != null)) && (
                    constraintMessage
                        == null)) {
                constraintMessage = ComponentFactory.getConstraintMessage();
            }

            if ((StringUtils.isNotBlank(instructionalText) || (getPropertyExpression("instructionalText") != null))
                    && (
                    instructionalMessage
                        == null)) {
                instructionalMessage = ComponentFactory.getInstructionalMessage();
            }
        } else {
            setValidationMessages(null);
        }

    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#performApplyModel(java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performApplyModel(Object model, Component parent) {
        super.performApplyModel(model, parent);

        // Done in apply model so we have the message text for additional rich message processing in Message
        // Sets message
        if (StringUtils.isNotBlank(instructionalText)
                && instructionalMessage != null
                && StringUtils.isBlank(instructionalMessage.getMessageText())) {
            instructionalMessage.setMessageText(instructionalText);
        }

        // Sets constraints
        if (StringUtils.isNotBlank(constraintText)
                && constraintMessage != null
                && StringUtils.isBlank(constraintMessage.getMessageText())) {
            constraintMessage.setMessageText(constraintText);
        }

        // invoke options finder if options not configured on the control
        List<KeyValue> fieldOptions = new ArrayList<KeyValue>();

        // use options directly configured on the control first
        if ((control != null) && control instanceof MultiValueControlBase) {
            MultiValueControlBase multiValueControl = (MultiValueControlBase) control;
            if ((multiValueControl.getOptions() != null) && !multiValueControl.getOptions().isEmpty()) {
                fieldOptions = multiValueControl.getOptions();
            }
        }

        // set multiLineReadOnlyDisplay to true to preserve text formatting
        if (control instanceof TextAreaControl) {
            setMultiLineReadOnlyDisplay(true);
        }

        // if options not configured on the control, invoke configured options finder
        if (fieldOptions.isEmpty() && (optionsFinder != null)) {
            if (optionsFinder instanceof UifKeyValuesFinder) {
                fieldOptions = ((UifKeyValuesFinder) optionsFinder).getKeyValues((ViewModel) model, this);

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

        if (this.enableAutoDirectInquiry && (getInquiry() == null) && !isReadOnly()) {
            buildAutomaticInquiry(model, true);
        }

        if (this.enableAutoQuickfinder && (getQuickfinder() == null)) {
            buildAutomaticQuickfinder(model);
        }

        // if read only do key/value translation if necessary (if alternative and additional properties not set)
        if (isReadOnly()
                && !fieldOptions.isEmpty()
                && StringUtils.isBlank(getReadOnlyDisplayReplacement())
                && StringUtils.isBlank(getReadOnlyDisplaySuffix())
                && StringUtils.isBlank(getReadOnlyDisplayReplacementPropertyName())
                && StringUtils.isBlank(getReadOnlyDisplaySuffixPropertyName())) {

            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());

            // TODO: can we translate Collections? (possibly combining output with delimiter
            if ((fieldValue != null) && (TypeUtils.isSimpleType(fieldValue.getClass()))) {
                for (KeyValue keyValue : fieldOptions) {
                    if (StringUtils.equals(fieldValue.toString(), keyValue.getKey())) {
                        setReadOnlyDisplayReplacement(keyValue.getValue());
                        break;
                    }
                }
            }
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#performFinalize(java.lang.Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(Object model, Component parent) {
        super.performFinalize(model, parent);

        setupIds();

        this.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.INPUT_FIELD);

        // if read only or the control is null no input can be given so no need to setup validation
        if (isReadOnly() || getControl() == null) {
            return;
        }

        if (StringUtils.isNotBlank(helperText) && (getControl() != null)) {
            getControl().getCssClasses().add(CssConstants.Classes.HAS_HELPER);
        }

        DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        if (this.getDictionaryObjectEntry() != null && this.getDictionaryAttributeName() != null) {
            AttributeDefinition ad = dataDictionaryService.getAttributeDefinition(this.getDictionaryObjectEntry(),
                    this.getDictionaryAttributeName());
            if (ad.getForceUppercase() || uppercaseValue) {
                Object currentPropertyValue = ObjectPropertyUtils.getPropertyValue(model,
                        getBindingInfo().getBindingPath());
                if (currentPropertyValue instanceof String) {
                    String uppercasedValue = ((String) currentPropertyValue).toUpperCase();
                    ObjectPropertyUtils.setPropertyValue(model, getBindingInfo().getBindingPath(), uppercasedValue);
                }
            }
        }

        // browser's native autocomplete causes issues with the suggest plugin
        if ((suggest != null) && suggest.isSuggestConfigured()) {
            setDisableNativeAutocomplete(true);
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
        
        View view = ViewLifecycle.getView();

        // special requiredness indicator handling, if this was previously not required reset its required
        // message to be ** for indicating required in the next state
        String path = view.getStateObjectBindingPath();
        Object stateObject;

        if (StringUtils.isNotBlank(path)) {
            stateObject = ObjectPropertyUtils.getPropertyValue(model, path);
        } else {
            stateObject = model;
        }
        StateMapping stateMapping = view.getStateMapping();

        if (stateMapping != null) {
            String validationState = ConstraintStateUtils.getClientViewValidationState(model, view);
            SimpleConstraint appliedSimpleConstraint = ConstraintStateUtils.getApplicableConstraint(
                    this.getSimpleConstraint(), validationState, stateMapping);

            if (appliedSimpleConstraint != null
                    && appliedSimpleConstraint.getRequired() != null
                    && appliedSimpleConstraint.getRequired()) {
                SimpleConstraint prevConstraint = ConstraintStateUtils.getApplicableConstraint(
                        this.getSimpleConstraint(), stateMapping.getCurrentState(stateObject), stateMapping);
                if (prevConstraint == null || prevConstraint.getRequired() == null || !prevConstraint.getRequired()) {
                    this.getFieldLabel().getRequiredMessage().setMessageText("**");
                }
            }
        }

        ClientValidationUtils.processAndApplyConstraints(this, view, model);

        // Generate validation messages
        if (validationMessages != null) {
            validationMessages.generateMessages(true, view, model, this);
        }
    }

    /**
     * Creates a new {@link org.kuali.rice.krad.uif.widget.QuickFinder} and then invokes the lifecycle process for
     * the quickfinder to determine if a relationship was found, if so the quickfinder is assigned to the field
     *
     * @param view view instance being processed
     * @param model object containing the view data
     */
    protected void buildAutomaticQuickfinder(Object model) {
        QuickFinder autoQuickfinder = ComponentFactory.getQuickFinder();

        ViewLifecycle.spawnSubLifecyle(model, autoQuickfinder, this);

        // if render flag is true, that means the quickfinder was able to find a relationship
        if (autoQuickfinder.isRender()) {
            this.quickfinder = autoQuickfinder;
        }
    }

    /**
     * Overrides processReadOnlyListDisplay to handle MultiValueControls by creating the list of values from values
     * instead of the keys of the options selected (makes the list "human-readable").  Otherwise it just passes the
     * list ahead as normal if this InputField does not use a MultiValueControl.
     *
     * @param model the model
     * @param originalList originalList of values
     */
    @Override
    protected void processReadOnlyListDisplay(Object model, List<?> originalList) {
        //Special handling for option based fields
        if ((control != null) && control instanceof MultiValueControlBase) {
            List<String> newList = new ArrayList<String>();
            List<KeyValue> fieldOptions = ((MultiValueControlBase) control).getOptions();

            if (fieldOptions == null || fieldOptions.isEmpty()) {
                return;
            }

            for (Object fieldValue : originalList) {
                for (KeyValue keyValue : fieldOptions) {
                    if (fieldValue != null && StringUtils.equals(fieldValue.toString(), keyValue.getKey())) {
                        newList.add(keyValue.getValue());
                        break;
                    }
                }
            }
            this.setReadOnlyDisplayReplacement(super.generateReadOnlyListDisplayReplacement(newList));
        } else {
            this.setReadOnlyDisplayReplacement(super.generateReadOnlyListDisplayReplacement(originalList));
        }
    }

    /**
     * Overriding to check quickfinder when masked is being applied. If quickfinder is configured set the component
     * to widgetInputOnly, else set to readOnly
     *
     * @see DataField#setAlternateAndAdditionalDisplayValue(org.kuali.rice.krad.uif.view.View, java.lang.Object)
     */
    @Override
    protected void setAlternateAndAdditionalDisplayValue(View view, Object model) {
        // if alternate or additional display values set don't override
        if (StringUtils.isNotBlank(getReadOnlyDisplayReplacement()) || StringUtils.isNotBlank(
                getReadOnlyDisplaySuffix())) {
            return;
        }

        if (isApplyMask()) {
            if ((this.quickfinder != null) && StringUtils.isNotBlank(this.quickfinder.getDataObjectClassName())) {
                setWidgetInputOnly(true);
            } else {
                setReadOnly(true);
            }
        }

        super.setAlternateAndAdditionalDisplayValue(view, model);
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
     *
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
        if (getAttributeQuery() != null) {
            // adjust paths on query mappings
            getAttributeQuery().updateQueryFieldMapping(getBindingInfo());
            getAttributeQuery().updateReturnFieldMapping(getBindingInfo());
            getAttributeQuery().updateQueryMethodArgumentFieldList(getBindingInfo());

            // build onblur script for field query
            String script = "executeFieldQuery('" + getControl().getId() + "',";
            script += "'" + getId() + "'," + getAttributeQuery().getQueryFieldMappingJsString() + ",";
            script += getAttributeQuery().getQueryMethodArgumentFieldsJsString() + ",";
            script += getAttributeQuery().getReturnFieldMappingJsString() + ");";

            // show the span wich will contain the info
            this.setRenderInfoMessageSpan(true);

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

        setNestedComponentIdAndSuffix(getControl(), UifConstants.IdSuffixes.CONTROL);
        setNestedComponentIdAndSuffix(getFieldLabel(), UifConstants.IdSuffixes.LABEL);
        setNestedComponentIdAndSuffix(getInstructionalMessage(), UifConstants.IdSuffixes.INSTRUCTIONAL);
        setNestedComponentIdAndSuffix(getConstraintMessage(), UifConstants.IdSuffixes.CONSTRAINT);
        setNestedComponentIdAndSuffix(getQuickfinder(), UifConstants.IdSuffixes.QUICK_FINDER);
        setNestedComponentIdAndSuffix(getSuggest(), UifConstants.IdSuffixes.SUGGEST);

        if (this.getControl() != null) {
            this.getControl().addDataAttribute(UifConstants.DataAttributes.CONTROL_FOR, this.getId());
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#copyFromAttributeDefinition(org.kuali.rice.krad.datadictionary.AttributeDefinition)
     */
    @Override
    public void copyFromAttributeDefinition(AttributeDefinition attributeDefinition) {
        super.copyFromAttributeDefinition(attributeDefinition);

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
                setRequired(Boolean.FALSE);
            }
        }

        if (getDataType() == null) {
            setDataType(attributeDefinition.getDataType());
            //Assume date if dataType is still null and using a DatePicker
            if (getDataType() == null
                    && control instanceof TextControl
                    && ((TextControl) control).getDatePicker() != null) {
                setDataType(DataType.DATE);
            }
        }

        // control
        if ((getControl() == null) && (attributeDefinition.getControlField() != null)) {
            Control control = ComponentUtils.copy(attributeDefinition.getControlField());
            setControl(control);
        }

        // constraint
        if (StringUtils.isEmpty(getConstraintText())) {
            setConstraintText(attributeDefinition.getConstraintText());

            if (constraintMessage == null) {
                constraintMessage = ComponentFactory.getConstraintMessage();
            }
            
            getConstraintMessage().setMessageText(attributeDefinition.getConstraintText());
        }

        // options
        if (getOptionsFinder() == null) {
            setOptionsFinder(attributeDefinition.getOptionsFinder());
        }

        // copy over simple constraint information because we cannot directly use simpleConstraint from
        // attributeDefinition because the settings in InputField take precedence
        if (this.getSimpleConstraint().getConstraintStateOverrides() == null) {
            this.getSimpleConstraint().setConstraintStateOverrides(
                    attributeDefinition.getSimpleConstraint().getConstraintStateOverrides());
        }

        if (this.getSimpleConstraint().getStates().isEmpty()) {
            this.getSimpleConstraint().setStates(attributeDefinition.getSimpleConstraint().getStates());
        }

        if (this.getSimpleConstraint().getMessageKey() == null) {
            this.getSimpleConstraint().setMessageKey(attributeDefinition.getSimpleConstraint().getMessageKey());
        }

        if (this.getSimpleConstraint().getApplyClientSide() == null) {
            this.getSimpleConstraint().setApplyClientSide(
                    attributeDefinition.getSimpleConstraint().getApplyClientSide());
        }

        if (this.getSimpleConstraint().getValidationMessageParams() == null) {
            this.getSimpleConstraint().setValidationMessageParams(
                    attributeDefinition.getSimpleConstraint().getValidationMessageParams());
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isInputAllowed()
     */
    @Override
    public boolean isInputAllowed() {
        return true;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getControl()
     */
    @Override
    @BeanTagAttribute(name = "control", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Control getControl() {
        return this.control;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setControl(org.kuali.rice.krad.uif.control.Control)
     */
    @Override
    public void setControl(Control control) {
        this.control = control;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getValidationMessages()
     */
    @ViewLifecycleRestriction
    @Override
    @BeanTagAttribute(name = "validationMessages", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public ValidationMessages getValidationMessages() {
        return this.validationMessages;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setValidationMessages(org.kuali.rice.krad.uif.element.ValidationMessages)
     */
    @Override
    public void setValidationMessages(ValidationMessages validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getOptionsFinder()
     */
    @Override
    @BeanTagAttribute(name = "optionsFinder", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public KeyValuesFinder getOptionsFinder() {
        return this.optionsFinder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setOptionsFinder(org.kuali.rice.krad.keyvalues.KeyValuesFinder)
     */
    @Override
    public void setOptionsFinder(KeyValuesFinder optionsFinder) {
        this.optionsFinder = optionsFinder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getOptionsFinderClass()
     */
    @Override
    @BeanTagAttribute(name = "optionsFinderClass")
    public Class<? extends KeyValuesFinder> getOptionsFinderClass() {
        if (this.optionsFinder != null) {
            return this.optionsFinder.getClass();
        } else {
            return null;
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setOptionsFinderClass(java.lang.Class)
     */
    @Override
    public void setOptionsFinderClass(Class<? extends KeyValuesFinder> optionsFinderClass) {
        this.optionsFinder = DataObjectUtils.newInstance(optionsFinderClass);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isEnableAutoDirectInquiry()
     */
    @Override
    public boolean isEnableAutoDirectInquiry() {
        return enableAutoDirectInquiry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setEnableAutoDirectInquiry(boolean)
     */
    @Override
    public void setEnableAutoDirectInquiry(boolean enableAutoDirectInquiry) {
        this.enableAutoDirectInquiry = enableAutoDirectInquiry;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getQuickfinder()
     */
    @Override
    @BeanTagAttribute(name = "quickfinder", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public QuickFinder getQuickfinder() {
        return this.quickfinder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setQuickfinder(org.kuali.rice.krad.uif.widget.QuickFinder)
     */
    @Override
    public void setQuickfinder(QuickFinder quickfinder) {
        this.quickfinder = quickfinder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isEnableAutoQuickfinder()
     */
    @Override
    public boolean isEnableAutoQuickfinder() {
        return enableAutoQuickfinder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setEnableAutoQuickfinder(boolean)
     */
    @Override
    public void setEnableAutoQuickfinder(boolean enableAutoQuickfinder) {
        this.enableAutoQuickfinder = enableAutoQuickfinder;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getSuggest()
     */
    @Override
    @BeanTagAttribute(name = "suggest", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Suggest getSuggest() {
        return suggest;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setSuggest(org.kuali.rice.krad.uif.widget.Suggest)
     */
    @Override
    public void setSuggest(Suggest suggest) {
        this.suggest = suggest;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isWidgetInputOnly()
     */
    @Override
    @BeanTagAttribute(name = "widgetInputOnly")
    public boolean isWidgetInputOnly() {
        return this.widgetInputOnly;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setWidgetInputOnly(boolean)
     */
    @Override
    public void setWidgetInputOnly(boolean widgetInputOnly) {
        this.widgetInputOnly = widgetInputOnly;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isRenderInputAddonGroup()
     */
    @Override
    public boolean isRenderInputAddonGroup() {
        return renderInputAddonGroup;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setRenderInputAddonGroup(boolean)
     */
    @Override
    public void setRenderInputAddonGroup(boolean renderInputAddonGroup) {
        this.renderInputAddonGroup = renderInputAddonGroup;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getPostInputCssClasses()
     */
    @Override
    public List<String> getPostInputCssClasses() {
        return postInputCssClasses;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getPostInputCssClassesAsString()
     */
    @Override
    public String getPostInputCssClassesAsString() {
        if (postInputCssClasses != null) {
            return StringUtils.join(postInputCssClasses, " ");
        }

        return "";
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setPostInputCssClasses(java.util.List)
     */
    @Override
    public void setPostInputCssClasses(List<String> postInputCssClasses) {
        this.postInputCssClasses = postInputCssClasses;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getPostInputAddons()
     */
    @Override
    public List<Component> getPostInputAddons() {
        return postInputAddons;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setPostInputAddons(java.util.List)
     */
    @Override
    public void setPostInputAddons(List<Component> postInputAddons) {
        this.postInputAddons = postInputAddons;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#addPostInputAddon(org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void addPostInputAddon(Component addOn) {
        if (postInputAddons == null) {
            postInputAddons = new ArrayList<Component>();
        }

        postInputAddons.add(addOn);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getInstructionalText()
     */
    @Override
    @BeanTagAttribute(name = "instructionalText")
    public String getInstructionalText() {
        return this.instructionalText;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setInstructionalText(java.lang.String)
     */
    @Override
    public void setInstructionalText(String instructionalText) {
        this.instructionalText = instructionalText;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getInstructionalMessage()
     */
    @Override
    @BeanTagAttribute(name = "instructionalMessage", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Message getInstructionalMessage() {
        return this.instructionalMessage;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setInstructionalMessage(org.kuali.rice.krad.uif.element.Message)
     */
    @Override
    public void setInstructionalMessage(Message instructionalMessage) {
        this.instructionalMessage = instructionalMessage;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getHelperText()
     */
    @Override
    public String getHelperText() {
        return helperText;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setHelperText(java.lang.String)
     */
    @Override
    public void setHelperText(String helperText) {
        this.helperText = helperText;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getConstraintText()
     */
    @Override
    @BeanTagAttribute(name = "constraintText")
    public String getConstraintText() {
        return this.constraintText;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setConstraintText(java.lang.String)
     */
    @Override
    public void setConstraintText(String constraintText) {
        this.constraintText = constraintText;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getConstraintMessage()
     */
    @Override
    @BeanTagAttribute(name = "constraintMessage", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public Message getConstraintMessage() {
        return this.constraintMessage;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setConstraintMessage(org.kuali.rice.krad.uif.element.Message)
     */
    @Override
    public void setConstraintMessage(Message constraintMessage) {
        this.constraintMessage = constraintMessage;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getValidCharactersConstraint()
     */
    @Override
    @BeanTagAttribute(name = "validCharactersConstraint", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public ValidCharactersConstraint getValidCharactersConstraint() {
        return this.validCharactersConstraint;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setValidCharactersConstraint(org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint)
     */
    @Override
    public void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint) {
        this.validCharactersConstraint = validCharactersConstraint;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getCaseConstraint()
     */
    @Override
    @BeanTagAttribute(name = "caseConstraint", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public CaseConstraint getCaseConstraint() {
        return this.caseConstraint;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setCaseConstraint(org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint)
     */
    @Override
    public void setCaseConstraint(CaseConstraint caseConstraint) {
        this.caseConstraint = caseConstraint;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getDependencyConstraints()
     */
    @Override
    @BeanTagAttribute(name = "dependencyConstraints", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<PrerequisiteConstraint> getDependencyConstraints() {
        return this.dependencyConstraints;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setDependencyConstraints(java.util.List)
     */
    @Override
    public void setDependencyConstraints(List<PrerequisiteConstraint> dependencyConstraints) {
        this.dependencyConstraints = dependencyConstraints;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getPrerequisiteConstraints()
     */
    @Override
    public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
        return dependencyConstraints;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getMustOccurConstraints()
     */
    @Override
    @BeanTagAttribute(name = "mustOccurConstraints", type = BeanTagAttribute.AttributeType.LISTBEAN)
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setMustOccurConstraints(java.util.List)
     */
    @Override
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getSimpleConstraint()
     */
    @Override
    @BeanTagAttribute(name = "simpleConstraint", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public SimpleConstraint getSimpleConstraint() {
        return this.simpleConstraint;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setSimpleConstraint(org.kuali.rice.krad.datadictionary.validation.constraint.SimpleConstraint)
     */
    @Override
    public void setSimpleConstraint(SimpleConstraint simpleConstraint) {
        this.simpleConstraint = simpleConstraint;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setDataType(org.kuali.rice.core.api.data.DataType)
     */
    @Override
    public void setDataType(DataType dataType) {
        this.simpleConstraint.setDataType(dataType);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setDataType(java.lang.String)
     */
    @Override
    public void setDataType(String dataType) {
        this.simpleConstraint.setDataType(DataType.valueOf(dataType));
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getDataType()
     */
    @Override
    @BeanTagAttribute(name = "dataType", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public DataType getDataType() {
        return this.simpleConstraint.getDataType();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getMaxLength()
     */
    @Override
    @BeanTagAttribute(name = "maxLength")
    public Integer getMaxLength() {
        return simpleConstraint.getMaxLength();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setMaxLength(java.lang.Integer)
     */
    @Override
    public void setMaxLength(Integer maxLength) {
        simpleConstraint.setMaxLength(maxLength);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getMinLength()
     */
    @Override
    @BeanTagAttribute(name = "minLength")
    public Integer getMinLength() {
        return simpleConstraint.getMinLength();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setMinLength(java.lang.Integer)
     */
    @Override
    public void setMinLength(Integer minLength) {
        simpleConstraint.setMinLength(minLength);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getRequired()
     */
    @Override
    @BeanTagAttribute(name = "required")
    public Boolean getRequired() {
        return this.simpleConstraint.getRequired();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setRequired(java.lang.Boolean)
     */
    @Override
    public void setRequired(Boolean required) {
        this.simpleConstraint.setRequired(required);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getExclusiveMin()
     */
    @Override
    @BeanTagAttribute(name = "exclusiveMin")
    public String getExclusiveMin() {
        return simpleConstraint.getExclusiveMin();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setExclusiveMin(java.lang.String)
     */
    @Override
    public void setExclusiveMin(String exclusiveMin) {
        simpleConstraint.setExclusiveMin(exclusiveMin);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getInclusiveMax()
     */
    @Override
    @BeanTagAttribute(name = "inclusiveMax")
    public String getInclusiveMax() {
        return simpleConstraint.getInclusiveMax();
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setInclusiveMax(java.lang.String)
     */
    @Override
    public void setInclusiveMax(String inclusiveMax) {
        simpleConstraint.setInclusiveMax(inclusiveMax);
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#getAttributeQuery()
     */
    @Override
    @BeanTagAttribute(name = "attributeQuery", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public AttributeQuery getAttributeQuery() {
        return attributeQuery;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setAttributeQuery(org.kuali.rice.krad.uif.field.AttributeQuery)
     */
    @Override
    public void setAttributeQuery(AttributeQuery attributeQuery) {
        this.attributeQuery = attributeQuery;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isUppercaseValue()
     */
    @Override
    @BeanTagAttribute(name = "uppercaseValue")
    public boolean isUppercaseValue() {
        return uppercaseValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setUppercaseValue(boolean)
     */
    @Override
    public void setUppercaseValue(boolean uppercaseValue) {
        this.uppercaseValue = uppercaseValue;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isDisableNativeAutocomplete()
     */
    @Override
    public boolean isDisableNativeAutocomplete() {
        return disableNativeAutocomplete;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setDisableNativeAutocomplete(boolean)
     */
    @Override
    public void setDisableNativeAutocomplete(boolean disableNativeAutocomplete) {
        this.disableNativeAutocomplete = disableNativeAutocomplete;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#isRenderFieldset()
     */
    @Override
    public boolean isRenderFieldset() {
        return super.isRenderFieldset() || (quickfinder != null
                && quickfinder.isRender()
                && quickfinder.getQuickfinderAction() != null
                && quickfinder.getQuickfinderAction().isRender());
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        InputField inputFieldCopy = (InputField) component;

        inputFieldCopy.setCustomValidatorClass(this.customValidatorClass);
        inputFieldCopy.setValidCharactersConstraint(CloneUtils.deepClone(this.validCharactersConstraint));
        inputFieldCopy.setCaseConstraint(CloneUtils.deepClone(this.caseConstraint));

        if (dependencyConstraints != null) {
            List<PrerequisiteConstraint> dependencyConstraintsCopy = new ArrayList<PrerequisiteConstraint>();

            for (PrerequisiteConstraint dependencyConstraint : dependencyConstraints) {
                dependencyConstraintsCopy.add(CloneUtils.deepClone(dependencyConstraint));
            }

            inputFieldCopy.setDependencyConstraints(dependencyConstraintsCopy);
        }

        if (mustOccurConstraints != null) {
            List<MustOccurConstraint> mustOccurConstraintsCopy = new ArrayList<MustOccurConstraint>();

            for (MustOccurConstraint mustOccurConstraint : mustOccurConstraints) {
                mustOccurConstraintsCopy.add(CloneUtils.deepClone(mustOccurConstraint));
            }

            inputFieldCopy.setMustOccurConstraints(mustOccurConstraintsCopy);
        }

        inputFieldCopy.setSimpleConstraint(CloneUtils.deepClone(this.simpleConstraint));
        inputFieldCopy.setDataType(this.dataType);

        // display props
        if (this.control != null) {
            inputFieldCopy.setControl((Control) this.control.copy());
        }

        inputFieldCopy.setOptionsFinder(this.optionsFinder);
        inputFieldCopy.setUppercaseValue(this.uppercaseValue);
        inputFieldCopy.setDisableNativeAutocomplete(this.disableNativeAutocomplete);

        if (this.validationMessages != null) {
            inputFieldCopy.setValidationMessages((ValidationMessages) this.validationMessages.copy());
        }

        // messages
        inputFieldCopy.setConstraintText(this.constraintText);
        inputFieldCopy.setInstructionalText(this.instructionalText);

        if (this.constraintMessage != null) {
            inputFieldCopy.setConstraintMessage((Message) this.constraintMessage.copy());
        }

        if (this.instructionalMessage != null) {
            inputFieldCopy.setInstructionalMessage((Message) this.instructionalMessage.copy());
        }

        inputFieldCopy.setHelperText(this.helperText);

        if (this.attributeQuery != null) {
            inputFieldCopy.setAttributeQuery((AttributeQuery) this.attributeQuery.copy());
        }

        inputFieldCopy.setEnableAutoDirectInquiry(this.enableAutoDirectInquiry);

        // widgets
        if (this.quickfinder != null) {
            inputFieldCopy.setQuickfinder((QuickFinder) this.quickfinder.copy());
        }

        inputFieldCopy.setEnableAutoQuickfinder(this.enableAutoQuickfinder);

        if (this.suggest != null) {
            inputFieldCopy.setSuggest((Suggest) this.suggest.copy());
        }

        inputFieldCopy.setWidgetInputOnly(this.widgetInputOnly);

        if (this.postInputCssClasses != null) {
            inputFieldCopy.setPostInputCssClasses(new ArrayList<String>(this.postInputCssClasses));
        }

        if (this.postInputAddons != null) {
            List<Component> postInputAddonsCopy = new ArrayList<Component>();
            for (Component addon : postInputAddons) {
                postInputAddonsCopy.add((Component) addon.copy());
            }
            inputFieldCopy.setPostInputAddons(postInputAddonsCopy);
        }
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#completeValidation(org.kuali.rice.krad.datadictionary.validator.ValidationTrace)
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that the control is set
        if (getControl() == null) {
            if (Validator.checkExpressions(this, "control")) {
                String currentValues[] = {"control =" + getConstraintText()};
                tracer.createWarning("Control should be set", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.rice.krad.uif.field.InputField#setCustomValidatorClass(java.lang.String)
     */
    @Override
    public void setCustomValidatorClass(String customValidatorClass) {
        this.customValidatorClass = customValidatorClass;
    }
}
