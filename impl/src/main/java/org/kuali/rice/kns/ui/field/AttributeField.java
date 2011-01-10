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
package org.kuali.rice.kns.ui.field;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.ui.Component;
import org.kuali.rice.kns.ui.control.Control;
import org.kuali.rice.kns.ui.control.MultiValueControlBase;

/**
 * Field that encapsulates data input/output captured by an attribute within the
 * application
 * <p>
 * 
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeField extends FieldLabelBase {
	// value props
	private String defaultValue;
	protected Integer maxLength;

	private KeyValuesFinder optionsFinder;

	// binding props
	private boolean bindToModel;
	private boolean bindToForm;

	private String bindingPath;
	private String bindByNamePrefix;

	private String dictionaryAttributeName;
	private String dictionaryObjectEntry;

	// display props
	private Control control;

	private String errorMessagePlacement;
	private ErrorsField errorsField;

	// messages
	private String summary;
	protected String description;

	public AttributeField() {
		bindToModel = true;
		bindToForm = false;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>If bindingPath not given, defaulted to the field name.</li>
	 * <li>Set the control id if blank to the field id</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize(java.util.Map)
	 */
	@Override
	public void initialize(Map<String, String> options) {
		super.initialize(options);

		if (StringUtils.isBlank(bindingPath)) {
			if (StringUtils.isNotBlank(bindByNamePrefix)) {
				bindingPath = bindByNamePrefix + "." + getName();
			}
			else {
				bindingPath = getName();
			}
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
		if (StringUtils.isBlank(getLabel())) {
			setLabel(attributeDefinition.getLabel());
		}

		// short label
		if (StringUtils.isBlank(getShortLabel())) {
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
		if (StringUtils.isBlank(getSummary())) {
			setSummary(attributeDefinition.getSummary());
		}

		// description
		if (StringUtils.isBlank(getDescription())) {
			setDescription(attributeDefinition.getDescription());
		}
	}

	/**
	 * @see org.kuali.rice.kns.ui.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(control);
		components.add(errorsField);

		return components;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isBindToModel() {
		return this.bindToModel;
	}

	public void setBindToModel(boolean bindToModel) {
		this.bindToModel = bindToModel;
	}

	public boolean isBindToForm() {
		return this.bindToForm;
	}

	public void setBindToForm(boolean bindToForm) {
		this.bindToForm = bindToForm;
	}

	public String getBindingPath() {
		return this.bindingPath;
	}

	public void setBindingPath(String bindingPath) {
		this.bindingPath = bindingPath;
	}

	public String getBindByNamePrefix() {
		return this.bindByNamePrefix;
	}

	public void setBindByNamePrefix(String bindByNamePrefix) {
		this.bindByNamePrefix = bindByNamePrefix;
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

}
