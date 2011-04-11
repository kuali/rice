/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.uif.field;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.kns.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.uif.UifConstants;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.control.Control;
import org.kuali.rice.kns.uif.control.MultiValueControlBase;
import org.kuali.rice.kns.uif.core.BindingInfo;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.core.DataBinding;
import org.kuali.rice.kns.uif.util.ClientValidationUtils;
import org.kuali.rice.kns.uif.widget.Inquiry;
import org.kuali.rice.kns.uif.widget.QuickFinder;

/**
 * Field that encapsulates data input/output captured by an attribute within the
 * application
 * 
 * <p>
 * The <code>AttributField</code> provides the majority of the data input/output
 * for the screen. Through these fields the model can be displayed and updated.
 * For data input, the field contains a <code>Control</code> instance will
 * render an HTML control element(s). The attribute field also contains a
 * <code>LabelField</code>, summary, and widgets such as a quickfinder (for
 * looking up values) and inquiry (for getting more information on the value).
 * <code>AttributeField</code> instances can have associated messages (errors)
 * due to invalid input or business rule failures. Security can also be
 * configured to restrict who may view the fields value.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeField extends FieldBase implements DataBinding {
    private static final long serialVersionUID = -3703656713706343840L;

    // value props
    private String defaultValue;

    // Constraint variables
    protected String customValidatorClass;
    protected ValidCharactersConstraint validCharactersConstraint;
    protected CaseConstraint caseConstraint;
    protected List<PrerequisiteConstraint> dependencyConstraints;
    protected List<MustOccurConstraint> mustOccurConstraints;
    protected SimpleConstraint simpleConstraint;

    private Formatter formatter;
    private KeyValuesFinder optionsFinder;

    // binding
    private String propertyName;
    private BindingInfo bindingInfo;

    private String dictionaryAttributeName;
    private String dictionaryObjectEntry;

    // display props
    private Control control;

    private String errorMessagePlacement;
    private ErrorsField errorsField;

    // messages
    private String summary;
    private String constraint;

    private String description;

    private AttributeSecurity attributeSecurity;
    private MessageField summaryMessageField;
    private MessageField constraintMessageField;

    // widgets
    private Inquiry fieldInquiry;
    private QuickFinder fieldLookup;

    public AttributeField() {
        super();
        simpleConstraint = new SimpleConstraint();
    }

    /**
     * <p>
     * The following initialization is performed:
     * <ul>
     * <li>Set defaults for binding</li>
     * <li>Default the model path if not set</li>
     * </ul>
     * </p>
     * 
     * @see org.kuali.rice.kns.uif.core.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
     */
    @Override
    public void performInitialization(View view) {
        super.performInitialization(view);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }

        // TODO: remove later, this should be done within the service lifecycle
        if ((optionsFinder != null) && (control != null) && control instanceof MultiValueControlBase) {
            MultiValueControlBase multiValueControl = (MultiValueControlBase) control;
            if ((multiValueControl.getOptions() == null) || multiValueControl.getOptions().isEmpty()) {
                multiValueControl.setOptions(optionsFinder.getKeyValues());
            }
        }
    }

    /**
     * The following actions are performed:
     * <ul>
     * <li>Sets up the client side validation for constraints on this field. In
     * addition, it sets up the messages applied to this field</li>
     * <li>Set the ids for the various attribute components</li>
     * </ul>
     * 
     * @see org.kuali.rice.kns.uif.core.ComponentBase#performFinalize(org.kuali.rice.kns.uif.container.View,
     *      java.lang.Object, org.kuali.rice.kns.uif.core.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        setupIds();

        // Sets message
        if (StringUtils.isNotBlank(summary)) {
            summaryMessageField.setMessageText(summary);
        }

        // Sets constraints
        if (StringUtils.isNotBlank(constraint)) {
            constraintMessageField.setMessageText(constraint);
        }

        // if read only or the control is not set no need to set client side
        // validation
        if (isReadOnly() || getControl() == null) {
            return;
        }

        ClientValidationUtils.processAndApplyConstraints(this, view);
    }

    /**
     * Sets the ids on all components the attribute field uses so they will all
     * contain this attribute's id in their ids. This is useful for jQuery
     * manipulation.
     */
    private void setupIds() {
        // update ids so they all match the attribute
        String id = getId();
        if (getControl() != null) {
            getControl().setId(id);
        }
        if (getErrorsField() != null) {
            getErrorsField().setId(id + UifConstants.IdSuffixes.ERRORS);
        }
        if (getLabelField() != null) {
            getLabelField().setId(id + UifConstants.IdSuffixes.LABEL);
        }
        if (getSummaryMessageField() != null) {
            getSummaryMessageField().setId(id + UifConstants.IdSuffixes.SUMMARY);
        }
        if (getConstraintMessageField() != null) {
            getConstraintMessageField().setId(id + UifConstants.IdSuffixes.CONSTRAINT);
        }
        if (getFieldLookup() != null) {
            getFieldLookup().setId(id + UifConstants.IdSuffixes.QUICK_FINDER);
        }
        setId(id + UifConstants.IdSuffixes.ATTRIBUTE);
    }

    /**
     * Defaults the properties of the <code>AttributeField</code> to the
     * corresponding properties of its <code>AttributeDefinition</code>
     * retrieved from the dictionary (if such an entry exists). If the field
     * already contains a value for a property, the definitions value is not
     * used.
     * 
     * @param attributeDefinition
     *            - AttributeDefinition instance the property values should be
     *            copied from
     */
    public void copyFromAttributeDefinition(AttributeDefinition attributeDefinition) {
        // label
        if (StringUtils.isEmpty(getLabel())) {
            setLabel(attributeDefinition.getLabel());
        }

        // short label
        if (StringUtils.isEmpty(getShortLabel())) {
            setShortLabel(attributeDefinition.getShortLabel());
        }

        // max length
        if (getMaxLength() == null) {
            setMaxLength(attributeDefinition.getMaxLength());
        }

        // max length
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
        }

        // control
        if (getControl() == null) {
            setControl(attributeDefinition.getControlField());
        }

        // summary
        if (StringUtils.isEmpty(getSummary())) {
            setSummary(attributeDefinition.getSummary());
            getSummaryMessageField().setMessageText(attributeDefinition.getSummary());
        }

        // description
        if (StringUtils.isEmpty(getDescription())) {
            setDescription(attributeDefinition.getDescription());
        }

        // security
        if (getAttributeSecurity() == null) {
            setAttributeSecurity(attributeDefinition.getAttributeSecurity());
        }

        // constraint
        if (StringUtils.isEmpty(getConstraint())) {
            setConstraint(attributeDefinition.getConstraint());
            getConstraintMessageField().setMessageText(attributeDefinition.getConstraint());
        }

    }

    /**
     * @see org.kuali.rice.kns.uif.core.ComponentBase#getNestedComponents()
     */
    @Override
    public List<Component> getNestedComponents() {
        List<Component> components = super.getNestedComponents();

        components.add(control);
        components.add(errorsField);
        components.add(fieldLookup);
        components.add(fieldInquiry);

        return components;
    }

    /**
     * @see org.kuali.rice.kns.uif.core.DataBinding#getPropertyName()
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Setter for the component's property name
     * 
     * @param propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Default value for the model property the field points to
     * 
     * <p>
     * When a new <code>View</code> instance is requested, the corresponding
     * model will be newly created. During this initialization process the value
     * for the model property will be set to the given default value (if set)
     * </p>
     * 
     * @return String default value
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Setter for the fields default value
     * 
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * <code>Formatter</code> instance that should be used when displaying and
     * accepting the field's value in the user interface
     * 
     * <p>
     * Formatters can provide conversion between datatypes in addition to
     * special string formatting such as currency display
     * </p>
     * 
     * @return Formatter instance
     * @see org.kuali.rice.kns.web.format.Formatter
     */
    public Formatter getFormatter() {
        return this.formatter;
    }

    /**
     * Setter for the field's formatter
     * 
     * @param formatter
     */
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    /**
     * @see org.kuali.rice.kns.uif.core.DataBinding#getBindingInfo()
     */
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * Setter for the field's binding info
     * 
     * @param bindingInfo
     */
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
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
     * Name of the attribute within the data dictionary the attribute field is
     * associated with
     * 
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * propertyName and parent object class the framework attempts will
     * determine the attribute definition that is associated with the field and
     * set this property. However this property can also be set in the fields
     * configuration to use another dictionary attribute.
     * </p>
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     * 
     * @return String attribute name
     */
    public String getDictionaryAttributeName() {
        return this.dictionaryAttributeName;
    }

    /**
     * Setter for the dictionary attribute name
     * 
     * @param dictionaryAttributeName
     */
    public void setDictionaryAttributeName(String dictionaryAttributeName) {
        this.dictionaryAttributeName = dictionaryAttributeName;
    }

    /**
     * Object entry name in the data dictionary the associated attribute is
     * apart of
     * 
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * parent object class the framework will determine the object entry for the
     * associated attribute. However the object entry can be set in the field's
     * configuration to use another object entry for the attribute
     * </p>
     * 
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     * 
     * @return
     */
    public String getDictionaryObjectEntry() {
        return this.dictionaryObjectEntry;
    }

    /**
     * Setter for the dictionary object entry
     * 
     * @param dictionaryObjectEntry
     */
    public void setDictionaryObjectEntry(String dictionaryObjectEntry) {
        this.dictionaryObjectEntry = dictionaryObjectEntry;
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
     * Brief statement of the field (attribute) purpose. Used to display helpful
     * information to the user on the form
     * 
     * @return String summary message
     */
    public String getSummary() {
        return this.summary;
    }

    /**
     * Setter for the summary message
     * 
     * @param summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * Full explanation of the field (attribute). Used in help contents
     * 
     * @return String description message
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Setter for the description message
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Holds security configuration for the attribute field. This triggers
     * corresponding permission checks in KIM and can result in an update to the
     * field state (such as read-only or hidden) and masking of the value
     * 
     * @return AttributeSecurity instance configured for field or Null if no
     *         restrictions are defined
     */
    public AttributeSecurity getAttributeSecurity() {
        return this.attributeSecurity;
    }

    /**
     * Setter for the AttributeSecurity instance that defines restrictions for
     * the field
     * 
     * @param attributeSecurity
     */
    public void setAttributeSecurity(AttributeSecurity attributeSecurity) {
        this.attributeSecurity = attributeSecurity;
    }

    /**
     * @see org.kuali.rice.kns.uif.core.ComponentBase#getSupportsOnLoad()
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
     * Inquiry widget for the field
     * 
     * <p>
     * The inquiry widget will render a link for the field value when read-only
     * that points to the associated inquiry view for the field. The inquiry can
     * be configured to point to a certain <code>InquiryView</code>, or the
     * framework will attempt to associate the field with a inquiry based on its
     * metadata (in particular its relationships in the model)
     * </p>
     * 
     * @return Inquiry field inquiry
     */
    public Inquiry getFieldInquiry() {
        return this.fieldInquiry;
    }

    /**
     * Setter for the inquiry widget
     * 
     * @param fieldInquiry
     */
    public void setFieldInquiry(Inquiry fieldInquiry) {
        this.fieldInquiry = fieldInquiry;
    }

    /**
     * @return the summaryField
     */
    public MessageField getSummaryMessageField() {
        return this.summaryMessageField;
    }

    /**
     * Sets the summary message field. Developers can use the setSummary method
     * which would set the summary text.
     * 
     * @param summary
     *            field to set
     * @see setSummary
     */
    public void setSummaryMessageField(MessageField summaryField) {
        this.summaryMessageField = summaryField;
    }

    /**
     * Returns the contraint set on the field
     * 
     * @return the constraint
     */
    public String getConstraint() {
        return this.constraint;
    }

    /**
     * Sets the constraint text. This text will be displayed below the
     * component.
     * 
     * @param constraint
     *            for this field
     */
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    /**
     * Sets the constraint message field. Developers can use the setContraint
     * method which would set the constraint text.
     * 
     * @param constraint
     *            field to set
     * @see setContraint
     */
    public void setConstraintMessageField(MessageField constraintMessageField) {
        this.constraintMessageField = constraintMessageField;
    }

    /**
     * Returns the contraint message field.
     * 
     * @return constraint Message Field
     */
    public MessageField getConstraintMessageField() {
        return this.constraintMessageField;
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
     * @param validCharactersConstraint
     *            the validCharactersConstraint to set
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
     * @param caseConstraint
     *            the caseConstraint to set
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
     * @param dependencyConstraints
     *            the dependencyConstraints to set
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
     * @param mustOccurConstraints
     *            the mustOccurConstraints to set
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
     * @param simpleConstraint
     *            the simpleConstraint to set
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
     * @param minLength
     *            the minLength to set
     */
    public void setMinLength(Integer minLength) {
        simpleConstraint.setMinLength(minLength);
    }

    /**
     * @see org.kuali.rice.kns.uif.core.ComponentBase#getRequired()
     */
    @Override
    public Boolean getRequired() {
        return this.simpleConstraint.getRequired();
    }

    /**
     * @see org.kuali.rice.kns.uif.core.ComponentBase#setRequired(java.lang.Boolean)
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
     * @param minValue
     *            the minValue to set
     */
    public void setExclusiveMin(String exclusiveMin) {
        simpleConstraint.setExclusiveMin(exclusiveMin);
    }

    /**
     * The inclusiveMax element determines the maximum allowable value for data
     * entry editing purposes. Value can be an integer or decimal value such as
     * -.001 or 99.
     * 
     * JSTL: This field is mapped into the field named "exclusiveMax".
     */
    public String getInclusiveMax() {
        return simpleConstraint.getInclusiveMax();
    }

    /**
     * @param maxValue
     *            the maxValue to set
     */
    public void setInclusiveMax(String inclusiveMax) {
        simpleConstraint.setInclusiveMax(inclusiveMax);
    }

}
