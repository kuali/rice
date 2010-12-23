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
package org.kuali.rice.kns.ui.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.ui.LabeledComponent;
import org.kuali.rice.kns.ui.field.LabelField;
import org.kuali.rice.kns.web.ui.Field;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FieldGroup extends ContainerBase implements LabeledComponent {
	private String label;
	private String shortLabel;
	private boolean showLabel;

	private boolean includeLabelField;
	private LabelField labelField;

	public FieldGroup() {
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
	}

	/**
	 * @see org.kuali.rice.krad.web.view.container.ContainerBase#getSupportedComponents()
	 */
	@Override
	public List<Class> getSupportedComponents() {
		List<Class> supportedComponents = new ArrayList<Class>();
		supportedComponents.add(Field.class);

		return supportedComponents;
	}

	/**
	 * @see org.kuali.rice.kns.ui.Component#getComponentTypeName()
	 */
	@Override
	public String getComponentTypeName() {
		return "fieldGroup";
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

	public boolean isIncludeLabelField() {
		return this.includeLabelField;
	}

	public void setIncludeLabelField(boolean includeLabelField) {
		this.includeLabelField = includeLabelField;
	}

	public LabelField getLabelField() {
		return this.labelField;
	}

	public void setLabelField(LabelField labelField) {
		this.labelField = labelField;
	}

}
