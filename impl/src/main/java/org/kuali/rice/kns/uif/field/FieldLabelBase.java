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
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.UIFConstants.Position;
import org.kuali.rice.kns.uif.container.View;

/**
 * Base class for <code>Field</code> implementation that contain a label
 * 
 * <p>
 * Holds a nested <code>LabelField</code> with configuration for rendering the
 * label.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class FieldLabelBase extends FieldBase {
	private String label;
	private String shortLabel;
	private boolean showLabel;

	private LabelField labelField;

	private String labelPlacement;

	private boolean labelFieldRendered;

	public FieldLabelBase() {
		showLabel = true;
		labelFieldRendered = false;

		labelPlacement = Position.LEFT;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the labelForComponentId to this component id</li>
	 * <li>Set the label text on the label field from the field's label property
	 * </li>
	 * <li>Set the render property on the label's required message field if this
	 * field is marked as required</li>
	 * <li>If label placement is right, set render colon to false</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		if (labelField != null) {
			labelField.setLabelForComponentId(this.getId());

			if (StringUtils.isBlank(labelField.getLabelText())) {
				labelField.setLabelText(label);
			}

			if ((getRequired() != null) && getRequired().booleanValue()) {
				labelField.getRequiredMessageField().setRender(true);
			}
			else {
				setRequired(new Boolean(false));
				labelField.getRequiredMessageField().setRender(false);
			}

			if (StringUtils.equals(labelPlacement, Position.RIGHT)) {
				labelField.setRenderColon(false);
			}
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(labelField);

		return components;
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

	public LabelField getLabelField() {
		return this.labelField;
	}

	public void setLabelField(LabelField labelField) {
		this.labelField = labelField;
	}

	public String getLabelPlacement() {
		return this.labelPlacement;
	}

	public void setLabelPlacement(String labelPlacement) {
		this.labelPlacement = labelPlacement;
	}

	/**
	 * Indicates whether the contained <code>LabelField</code> has been rendered
	 * as part of another field and thus should not be rendered with the
	 * attribute
	 * 
	 * @return boolean true if the label field has been rendered, false if it
	 *         should be rendered with the attribute
	 */
	public boolean isLabelFieldRendered() {
		return this.labelFieldRendered;
	}

	/**
	 * Setter for the label field rendered indicator
	 * 
	 * @param labelFieldRendered
	 */
	public void setLabelFieldRendered(boolean labelFieldRendered) {
		this.labelFieldRendered = labelFieldRendered;
	}

}
