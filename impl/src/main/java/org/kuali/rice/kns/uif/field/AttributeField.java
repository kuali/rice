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
import org.kuali.rice.kns.web.format.Formatter;

/**
 * Field that encapsulates data input/output captured by an attribute within the
 * application
 * <p>
 * 
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeField extends FieldBase implements DataBinding {
	private static final long serialVersionUID = -3703656713706343840L;
	
	// value props
	private Object value;
	private String formattedValue;
	private String defaultValue;
	private Integer maxLength;

	private BindingInfo bindingInfo;

	private Formatter formatter;
	private KeyValuesFinder optionsFinder;

	private String dictionaryAttributeName;
	private String dictionaryObjectEntry;

	// display props
	private Control control;

	private String errorMessagePlacement;
	private ErrorsField errorsField;

	// messages
	private String summary;
	private String description;

	private AttributeSecurity attributeSecurity;

	public AttributeField() {
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
			bindingInfo.setDefaults(view, this);
		}

		if (control != null && StringUtils.isBlank(control.getId())) {
			control.setId(this.getId());
		}

		// TODO: remove later, this should be done within the service lifecycle
		if (control != null && control instanceof MultiValueControlBase) {
			((MultiValueControlBase) control).setOptions(optionsFinder.getKeyValues());
		}
	}

	/**
	 * Sets properties if blank to the corresponding property value in the
	 * <code>AttributeDefinition</code>
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

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getFormattedValue() {
		return this.formattedValue;
	}

	public void setFormattedValue(String formattedValue) {
		this.formattedValue = formattedValue;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Formatter getFormatter() {
		return this.formatter;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	public BindingInfo getBindingInfo() {
		return this.bindingInfo;
	}

	public void setBindingInfo(BindingInfo bindingInfo) {
		this.bindingInfo = bindingInfo;
	}

	public Control getControl() {
		return this.control;
	}

	public void setControl(Control control) {
		this.control = control;
	}

	public String getErrorMessagePlacement() {
		return this.errorMessagePlacement;
	}

	public void setErrorMessagePlacement(String errorMessagePlacement) {
		this.errorMessagePlacement = errorMessagePlacement;
	}

	public ErrorsField getErrorsField() {
		return this.errorsField;
	}

	public void setErrorsField(ErrorsField errorsField) {
		this.errorsField = errorsField;
	}

	public String getDictionaryAttributeName() {
		return this.dictionaryAttributeName;
	}

	public void setDictionaryAttributeName(String dictionaryAttributeName) {
		this.dictionaryAttributeName = dictionaryAttributeName;
	}

	public String getDictionaryObjectEntry() {
		return this.dictionaryObjectEntry;
	}

	public void setDictionaryObjectEntry(String dictionaryObjectEntry) {
		this.dictionaryObjectEntry = dictionaryObjectEntry;
	}

	public KeyValuesFinder getOptionsFinder() {
		return this.optionsFinder;
	}

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

}
