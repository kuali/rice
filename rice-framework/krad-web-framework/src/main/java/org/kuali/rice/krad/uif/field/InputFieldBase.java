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
package org.kuali.rice.krad.uif.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.krad.bo.DataObjectRelationship;
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
import org.kuali.rice.krad.uif.control.SelectControl;
import org.kuali.rice.krad.uif.control.TextAreaControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinder;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.FieldValidationMessages;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.element.ValidationMessages;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.util.ClientValidationUtils;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ConstraintStateUtils;
import org.kuali.rice.krad.uif.util.ContextUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.uif.widget.Suggest;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krad.web.form.UifFormBase;

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
@BeanTags({@BeanTag(name = "input", parent = "Uif-InputField"),
        @BeanTag(name = "inputLabelTop", parent = "Uif-InputField-LabelTop"),
        @BeanTag(name = "inputLabelRight", parent = "Uif-InputField-LabelRight"),
        @BeanTag(name = "checkboxInput", parent = "Uif-CheckboxInputField")})
public class InputFieldBase extends DataFieldBase implements InputField {
    private static final long serialVersionUID = -3703656713706343840L;

    // constraint variables
    private ValidCharactersConstraint validCharactersConstraint;
    private CaseConstraint caseConstraint;
    private List<PrerequisiteConstraint> dependencyConstraints;
    private List<MustOccurConstraint> mustOccurConstraints;
    private SimpleConstraint simpleConstraint;
    private DataType dataType;

    // display props
    private Control control;

    private boolean inlineEdit;
    private boolean ajaxInlineEdit;

    private KeyValuesFinder optionsFinder;

    private boolean uppercaseValue;
    private boolean disableNativeAutocomplete;

    @DelayedCopy
    private FieldValidationMessages validationMessages;

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
     * {@inheritDoc}
     */
    @Override
    public void performInitialization(Object model) {
        super.performInitialization(model);

        if ((StringUtils.isNotBlank(constraintText) || (getPropertyExpression("constraintText") != null)) && (
                constraintMessage
                        == null)) {
            constraintMessage = ComponentFactory.getConstraintMessage();
        }

        if ((StringUtils.isNotBlank(instructionalText) || (getPropertyExpression("instructionalText") != null)) && (
                instructionalMessage
                        == null)) {
            instructionalMessage = ComponentFactory.getInstructionalMessage();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEvaluateExpression() {
        // populate readOnly from parent before calling super, to prevent DataField
        // from forcing to true.
        if (getReadOnly() == null) {
            Component parent = ViewLifecycle.getPhase().getParent();
            setReadOnly(parent == null ? null : parent.getReadOnly());
        }

        super.afterEvaluateExpression();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        super.performApplyModel(model, parent);

        // Done in apply model so we have the message text for additional rich message processing in Message
        // Sets message
        if (StringUtils.isNotBlank(instructionalText) && instructionalMessage != null && StringUtils.isBlank(
                instructionalMessage.getMessageText())) {
            instructionalMessage.setMessageText(instructionalText);
        }

        // Sets constraints
        if (StringUtils.isNotBlank(constraintText) && constraintMessage != null && StringUtils.isBlank(
                constraintMessage.getMessageText())) {
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

        if (enableAutoDirectInquiry && this.getInquiry() == null && hasAutoInquiryRelationship()) {
            setInquiry(ComponentFactory.getInquiry());
        }

        if (enableAutoQuickfinder && this.getQuickfinder() == null && hasAutoQuickfinderRelationship()) {
            setQuickfinder(ComponentFactory.getQuickFinder());
            ContextUtils.pushAllToContextDeep(quickfinder, this.getContext());
        }

        // if read only do key/value translation if necessary (if alternative and additional properties not set)
        if (Boolean.TRUE.equals(getReadOnly())
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

        if(control != null && quickfinder != null && quickfinder.getQuickfinderAction() != null) {
            String disabledExpression = control.getPropertyExpression("disabled");
            if(StringUtils.isNotBlank(disabledExpression)) {
                quickfinder.getQuickfinderAction().getPropertyExpressions().put("disabled", disabledExpression);
            }  else {
                quickfinder.getQuickfinderAction().setDisabled(control.isDisabled());
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        setupIds();

        this.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.INPUT_FIELD);

        boolean ajaxInlineEditRefresh = ajaxInlineEdit && ((UifFormBase)model).getUpdateComponentId() != null &&
                ((UifFormBase)model).getUpdateComponentId().equals(this.getId());

        // if read only or the control is null no input can be given so no need to setup validation
        if ((Boolean.TRUE.equals(getReadOnly()) && !inlineEdit && !ajaxInlineEditRefresh) || getControl() == null) {
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

        View view = ViewLifecycle.getView();

        setupFieldQuery(view);

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
        String nextStateReqIndicator = (String) KRADServiceLocatorWeb.getDataDictionaryService().getDictionaryBean(
                UifConstants.REQUIRED_NEXT_STATE_INDICATOR_ID);

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
                    this.getFieldLabel().setRequiredIndicator(nextStateReqIndicator);
                }
            }
        }

        ClientValidationUtils.processAndApplyConstraints(this, view, model);

        if (inlineEdit || ajaxInlineEdit) {
            this.addDataAttribute(UifConstants.DataAttributes.INLINE_EDIT, "true");
        }

        // Generate validation messages
        if (validationMessages != null) {
            // Messages will not use tooltip for inline edit cases
            if (inlineEdit || ajaxInlineEdit) {
                validationMessages.setUseTooltip(false);
            }

            validationMessages.generateMessages(view, model, this);
        }

        addComponentPostMetadata();

        if (this.getHelp() != null && StringUtils.isNotBlank(this.getHelp().getExternalHelpUrl())) {
            this.setRenderInputAddonGroup(true);
        }
    }

    /**
     * Invoked during the finalize phase to capture state of the component needs to support post operations.
     */
    protected void addComponentPostMetadata() {
        ViewPostMetadata viewPostMetadata = ViewLifecycle.getViewPostMetadata();

        viewPostMetadata.getInputFieldIds().add(this.getId());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.LABEL, this.getLabel());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.PATH, this.getName());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.SIMPLE_CONSTRAINT,
                this.getSimpleConstraint());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.VALID_CHARACTER_CONSTRAINT,
                this.getValidCharactersConstraint());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.CASE_CONSTRAINT, this.getCaseConstraint());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.MUST_OCCUR_CONSTRAINTS,
                this.getMustOccurConstraints());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.PREREQ_CONSTSTRAINTS,
                this.getPrerequisiteConstraints());

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.INPUT_FIELD_ATTRIBUTE_QUERY,
                attributeQuery);

        if (this.suggest != null) {
            viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.SUGGEST, this.suggest.getPostData());
        }

        viewPostMetadata.addComponentPostData(this, UifConstants.PostMetadata.INPUT_FIELD_IS_UPPERCASE,
                isUppercaseValue());

        if ((isRender() || StringUtils.isNotBlank(getProgressiveRender())) && !isHidden() && (!Boolean.TRUE.equals(
                getReadOnly()) || inlineEdit || ajaxInlineEdit)) {
            viewPostMetadata.addAccessibleBindingPath(getBindingInfo().getBindingPath());
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
     * {@inheritDoc}
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
                String maintenanceAction = null;
                if (view.getViewTypeName().equals(UifConstants.ViewType.MAINTENANCE)) {
                    maintenanceAction =((MaintenanceDocumentForm) model).getMaintenanceAction();
                }

                if ((!view.getViewTypeName().equals(UifConstants.ViewType.LOOKUP)) &&
                    (!KRADConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction))) {
                        setReadOnly(true);
                }
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
     * Performs setup of the field attribute query and informational display properties.
     *
     * <p>Paths are adjusted to match the binding for the this field, and the necessary onblur script for
     * triggering the query client side is constructed</p>
     *
     * @param view view instance the input field is associated with
     */
    protected void setupFieldQuery(View view) {
        if (getAttributeQuery() != null) {
            getAttributeQuery().defaultQueryTarget(view.getViewHelperService());

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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public boolean isInputAllowed() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.BYTYPE)
    public Control getControl() {
        return this.control;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setControl(Control control) {
        this.control = control;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    public boolean isInlineEdit() {
        return inlineEdit;
    }

    /**
     * {@inheritDoc}
     */
    public void setInlineEdit(boolean inlineEdit) {
        this.inlineEdit = inlineEdit;
    }

    /**
     * {@inheritDoc}
     */
    @BeanTagAttribute
    public boolean isAjaxInlineEdit() {
        return ajaxInlineEdit;
    }

    /**
     * {@inheritDoc}
     */
    public void setAjaxInlineEdit(boolean ajaxInlineEdit) {
        this.ajaxInlineEdit = ajaxInlineEdit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ViewLifecycleRestriction
    @BeanTagAttribute
    public FieldValidationMessages getValidationMessages() {
        return this.validationMessages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValidationMessages(FieldValidationMessages validationMessages) {
        this.validationMessages = validationMessages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public KeyValuesFinder getOptionsFinder() {
        return this.optionsFinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOptionsFinder(KeyValuesFinder optionsFinder) {
        this.optionsFinder = optionsFinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Class<? extends KeyValuesFinder> getOptionsFinderClass() {
        if (this.optionsFinder != null) {
            return this.optionsFinder.getClass();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOptionsFinderClass(Class<? extends KeyValuesFinder> optionsFinderClass) {
        this.optionsFinder = KRADUtils.createNewObjectFromClass(optionsFinderClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isEnableAutoDirectInquiry() {
        return enableAutoDirectInquiry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableAutoDirectInquiry(boolean enableAutoDirectInquiry) {
        this.enableAutoDirectInquiry = enableAutoDirectInquiry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public QuickFinder getQuickfinder() {
        return this.quickfinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQuickfinder(QuickFinder quickfinder) {
        this.quickfinder = quickfinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isEnableAutoQuickfinder() {
        return enableAutoQuickfinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableAutoQuickfinder(boolean enableAutoQuickfinder) {
        this.enableAutoQuickfinder = enableAutoQuickfinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public Suggest getSuggest() {
        return suggest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuggest(Suggest suggest) {
        this.suggest = suggest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isWidgetInputOnly() {
        return this.widgetInputOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidgetInputOnly(boolean widgetInputOnly) {
        this.widgetInputOnly = widgetInputOnly;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isRenderInputAddonGroup() {
        return renderInputAddonGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRenderInputAddonGroup(boolean renderInputAddonGroup) {
        this.renderInputAddonGroup = renderInputAddonGroup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<String> getPostInputCssClasses() {
        return postInputCssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostInputCssClassesAsString() {
        if (postInputCssClasses != null) {
            return StringUtils.join(postInputCssClasses, " ");
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPostInputCssClasses(List<String> postInputCssClasses) {
        this.postInputCssClasses = postInputCssClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<Component> getPostInputAddons() {
        return postInputAddons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPostInputAddons(List<Component> postInputAddons) {
        this.postInputAddons = postInputAddons;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPostInputAddon(Component addOn) {
        if (postInputAddons == null) {
            postInputAddons = new ArrayList<Component>();
        }

        postInputAddons.add(addOn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getInstructionalText() {
        return this.instructionalText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstructionalText(String instructionalText) {
        this.instructionalText = instructionalText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Message getInstructionalMessage() {
        return this.instructionalMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInstructionalMessage(Message instructionalMessage) {
        this.instructionalMessage = instructionalMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getHelperText() {
        return helperText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHelperText(String helperText) {
        this.helperText = helperText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getConstraintText() {
        return this.constraintText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConstraintText(String constraintText) {
        this.constraintText = constraintText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Message getConstraintMessage() {
        return this.constraintMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setConstraintMessage(Message constraintMessage) {
        this.constraintMessage = constraintMessage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public ValidCharactersConstraint getValidCharactersConstraint() {
        return this.validCharactersConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint) {
        this.validCharactersConstraint = validCharactersConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public CaseConstraint getCaseConstraint() {
        return this.caseConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCaseConstraint(CaseConstraint caseConstraint) {
        this.caseConstraint = caseConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<PrerequisiteConstraint> getDependencyConstraints() {
        return this.dependencyConstraints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDependencyConstraints(List<PrerequisiteConstraint> dependencyConstraints) {
        this.dependencyConstraints = dependencyConstraints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
        return dependencyConstraints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public SimpleConstraint getSimpleConstraint() {
        return this.simpleConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSimpleConstraint(SimpleConstraint simpleConstraint) {
        this.simpleConstraint = simpleConstraint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public DataType getDataType() {
        return this.simpleConstraint.getDataType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataType(DataType dataType) {
        this.simpleConstraint.setDataType(dataType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataType(String dataType) {
        this.simpleConstraint.setDataType(DataType.valueOf(dataType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Integer getMaxLength() {
        return simpleConstraint.getMaxLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMaxLength(Integer maxLength) {
        simpleConstraint.setMaxLength(maxLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Integer getMinLength() {
        return simpleConstraint.getMinLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMinLength(Integer minLength) {
        simpleConstraint.setMinLength(minLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public Boolean getRequired() {
        return this.simpleConstraint.getRequired();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequired(Boolean required) {
        this.simpleConstraint.setRequired(required);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getExclusiveMin() {
        return simpleConstraint.getExclusiveMin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExclusiveMin(String exclusiveMin) {
        simpleConstraint.setExclusiveMin(exclusiveMin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public String getInclusiveMax() {
        return simpleConstraint.getInclusiveMax();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInclusiveMax(String inclusiveMax) {
        simpleConstraint.setInclusiveMax(inclusiveMax);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public AttributeQuery getAttributeQuery() {
        return attributeQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAttributeQuery(AttributeQuery attributeQuery) {
        this.attributeQuery = attributeQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isUppercaseValue() {
        return uppercaseValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUppercaseValue(boolean uppercaseValue) {
        this.uppercaseValue = uppercaseValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @BeanTagAttribute
    public boolean isDisableNativeAutocomplete() {
        return disableNativeAutocomplete;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisableNativeAutocomplete(boolean disableNativeAutocomplete) {
        this.disableNativeAutocomplete = disableNativeAutocomplete;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRenderFieldset() {
        return super.isRenderFieldset() || (quickfinder != null
                && quickfinder.isRender()
                && quickfinder.getQuickfinderAction() != null
                && quickfinder.getQuickfinderAction().isRender());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        tracer.addBean(this);

        // Checks that the control is set
        if (getControl() == null) {
            if (Validator.checkExpressions(this, "control")) {
                String currentValues[] = {"propertyName =" + getPropertyName()};
                tracer.createWarning("Control should be set", currentValues);
            }
        }


        if (getControl() != null && !(getControl() instanceof TextControl
                || getControl() instanceof TextAreaControl
                || getControl() instanceof SelectControl)){

            if (CollectionUtils.isNotEmpty(this.getPostInputAddons())) {
                String currentValues[] = {"propertyName =" + getPropertyName()};
                tracer.createWarning("Inputs which are not text or select should not use post input addons for "
                        + "user experience reasons", currentValues);
            }
        }

        super.completeValidation(tracer.getCopy());
    }


    /**
     * Determines wheter or not to create an automatic quickfinder widget for this field within the current lifecycle.
     *
     * @return True if an automatic quickfinder widget should be created for this field on the current lifecycle.
     */
    protected boolean hasAutoQuickfinderRelationship() {
        String propertyName = getBindingInfo().getBindingName();

        // get object instance and class for parent
        View view = ViewLifecycle.getView();
        Object model = ViewLifecycle.getModel();
        Object parentObject = ViewModelUtils.getParentObjectForMetadata(view, model, this);
        Class<?> parentObjectClass = null;
        if (parentObject != null) {
            parentObjectClass = parentObject.getClass();
        }

        // get relationship from metadata service
        @SuppressWarnings("deprecation")
        DataObjectRelationship relationship = null;
        if (parentObject != null) {
            relationship = KRADServiceLocatorWeb.getLegacyDataAdapter().getDataObjectRelationship(parentObject,
                    parentObjectClass, propertyName, "", true, true, false);
        }

        return relationship != null;
    }

}
