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
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.uif.BindingInfo;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.control.Control;
import org.kuali.rice.kns.uif.control.MultiValueControlBase;
import org.kuali.rice.kns.uif.widget.QuickFinder;
import org.kuali.rice.kns.web.format.Formatter;

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
	private Integer maxLength;

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

	private QuickFinder fieldLookup;

	public AttributeField() {
		super();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set defaults for binding</li>
	 * <li>Set the control id if blank to the field id</li>
	 * <li>Default the model path if not set</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		if (bindingInfo != null) {
			bindingInfo.setDefaults(view, getPropertyName());
		}

		if (control != null && StringUtils.isBlank(control.getId())) {
			control.setId(this.getId());
		}

		if (StringUtils.isNotBlank(summary)){
			summaryMessageField.setMessageText(summary);
		}else{
			summaryMessageField.setRender(false);
		}
		
		if (StringUtils.isNotBlank(constraint)){
			constraintMessageField.setMessageText(constraint);
		}else{
			constraintMessageField.setRender(false);
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
		}

		// description
		if (StringUtils.isEmpty(getDescription())) {
			setDescription(attributeDefinition.getDescription());
		}

		// security
		if (getAttributeSecurity() == null) {
			setAttributeSecurity(attributeDefinition.getAttributeSecurity());
		}
		
		if (StringUtils.isEmpty(getConstraint())){
			setConstraint(attributeDefinition.getConstraint());
		}
		
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(control);
		components.add(errorsField);

		return components;
	}

	/**
	 * @see org.kuali.rice.kns.uif.DataBinding#getPropertyName()
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
	 * @see org.kuali.rice.kns.uif.DataBinding#getBindingInfo()
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
	 * 
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
	 * Maximum number of the characters the attribute value is allowed to have.
	 * Used to set the maxLength for supporting controls. Note this can be
	 * smaller or longer than the actual control size
	 * 
	 * @return Integer max length
	 */
	public Integer getMaxLength() {
		return this.maxLength;
	}

	/**
	 * Setter for attributes max length
	 * 
	 * @param maxLength
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
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
		if (StringUtils.isNotBlank(summary)){
			summaryMessageField.setMessageText(summary);
			summaryMessageField.setRender(true);
		}else{
			summaryMessageField.setRender(false);
	}
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
	 * @return the summaryField
	 */
	public MessageField getSummaryMessageField() {
		return this.summaryMessageField;
	}

	/**
	 * @param summaryField the summaryField to set
	 */
	public void setSummaryMessageField(MessageField summaryField) {
		this.summaryMessageField = summaryField;
	}

	/**
	 * @return the constraint
	 */
	public String getConstraint() {
		return this.constraint;
	}

	/**
	 * @param constraint the constraint to set
	 */
	public void setConstraint(String constraint) {
		this.constraint = constraint;
		if (StringUtils.isNotBlank(constraint)){
			constraintMessageField.setMessageText(constraint);
			constraintMessageField.setRender(true);
		}else{
			constraintMessageField.setRender(false);
		}
	}
	
	/**
	 * @return the constraintMessageField
	 */
	public MessageField getConstraintMessageField() {
		return this.constraintMessageField;
	}

	/**
	 * @param constraintMessageField the constraintMessageField to set
	 */
	public void setConstraintMessageField(MessageField constraintMessageField) {
		this.constraintMessageField = constraintMessageField;
	}
}

