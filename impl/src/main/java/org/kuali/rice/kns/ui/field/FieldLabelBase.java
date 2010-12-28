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
import org.kuali.rice.kns.ui.UIFConstants.Position;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FieldLabelBase extends FieldBase implements LabeledComponent {
	private String label;
	private String shortLabel;
	private boolean showLabel;

	private boolean includeLabelField;
	private LabelField labelField;

	private Position labelPlacement;

	public FieldLabelBase() {
		showLabel = true;
		includeLabelField = false;

		labelField = new LabelField();
		labelPlacement = Position.LEFT;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the labelForComponentId to this component id</li>
	 * <li>Set the label text on the label field from the field's label property
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();

		labelField.setLabelForComponentId(this.getId());

		if (StringUtils.isBlank(labelField.getLabelText())) {
			labelField.setLabelText(label);
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

	public Position getLabelPlacement() {
		return this.labelPlacement;
	}

	public void setLabelPlacement(Position labelPlacement) {
		this.labelPlacement = labelPlacement;
	}

	public void setLabelPlacement(String labelPlacement) {
		this.labelPlacement = Position.valueOf(labelPlacement);
	}

}
