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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.ui.LabeledComponent;
import org.kuali.rice.kns.ui.control.Control;
import org.kuali.rice.kns.ui.util.Position;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeField extends FieldBase implements LabeledComponent {
	private String defaultValue;

	private String attributeName;
	private Class attributeClass;

	private String bindingPath;

	private String label;
	private String shortLabel;
	private boolean showLabel;

	private boolean includeLabelField;
	private LabelField labelField;

	private Control control;

	private Position errorMessagePlacement;
	private ErrorsField errorField;

	public AttributeField() {
		showLabel = true;
		includeLabelField = false;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the labelForComponentId to this component id</li>
	 * <li>If the label text field of the labelField property is blank it is set
	 * to the label property of the field.</li>
	 * <li>If bindingPath not given, defaulted to the field name.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		
		labelField.setLabelForComponentId(this.getId());

		if (StringUtils.isBlank(labelField.getLabel().getText())) {
			labelField.getLabel().setText(label);
		}

		if (StringUtils.isBlank(bindingPath)) {
			bindingPath = this.getName();
		}
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getShortLabel() {
		return this.shortLabel;
	}

	public void setShortLabel(String shortLabel) {
		this.shortLabel = shortLabel;
	}

	public boolean isShowLabel() {
		return this.showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Class getAttributeClass() {
		return this.attributeClass;
	}

	public void setAttributeClass(Class attributeClass) {
		this.attributeClass = attributeClass;
	}

	public LabelField getLabelField() {
		return this.labelField;
	}

	public void setLabelField(LabelField labelField) {
		this.labelField = labelField;
	}

	public String getBindingPath() {
		return this.bindingPath;
	}

	public void setBindingPath(String bindingPath) {
		this.bindingPath = bindingPath;
	}

	public Control getControl() {
		return this.control;
	}

	public void setControl(Control control) {
		this.control = control;
	}

	public Position getErrorMessagePlacement() {
		return this.errorMessagePlacement;
	}

	public void setErrorMessagePlacement(Position errorMessagePlacement) {
		this.errorMessagePlacement = errorMessagePlacement;
	}

	public ErrorsField getErrorField() {
		return this.errorField;
	}

	public void setErrorField(ErrorsField errorField) {
		this.errorField = errorField;
	}

	public boolean isIncludeLabelField() {
		return this.includeLabelField;
	}

	public void setIncludeLabelField(boolean includeLabelField) {
		this.includeLabelField = includeLabelField;
	}

}
