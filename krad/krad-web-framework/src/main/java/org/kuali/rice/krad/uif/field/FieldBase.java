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
package org.kuali.rice.krad.uif.field;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants.Position;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.core.ComponentBase;

/**
 * Base class for <code>Field</code> implementations
 * 
 * <p>
 * Sets the component type name so that all field templates have a fixed
 * contract
 * </p>
 * 
 * <p>
 * Holds a nested <code>LabelField</code> with configuration for rendering the
 * label and configuration on label placement.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FieldBase extends ComponentBase implements Field {
	private static final long serialVersionUID = -5888414844802862760L;

	private String shortLabel;

	private LabelField labelField;

	private String labelPlacement;
	private boolean labelFieldRendered;

	public FieldBase() {
		labelFieldRendered = false;

		labelPlacement = Position.LEFT;
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * </ul>
	 * 
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#performInitialization(org.kuali.rice.krad.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);
	}

	/**
	 * The following finalization is performed:
	 * 
	 * <ul>
	 * <li>Set the labelForComponentId to this component id</li>
	 * <li>Set the label text on the label field from the field's label property
	 * </li>
	 * <li>Set the render property on the label's required message field if this
	 * field is marked as required</li>
	 * <li>If label placement is right, set render colon to false</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#performFinalize(org.kuali.rice.krad.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.core.Component)
	 */
	@Override
	public void performFinalize(View view, Object model, Component parent) {
		super.performFinalize(view, model, parent);

		if (labelField != null) {
			labelField.setLabelForComponentId(this.getId());

			if ((getRequired() != null) && getRequired().booleanValue()) {
				labelField.getRequiredMessageField().setRender(true);
			}
			else {
				setRequired(new Boolean(false));
				labelField.getRequiredMessageField().setRender(true);
				String prefixStyle = "";
				if(StringUtils.isNotBlank(labelField.getRequiredMessageField().getStyle())){
                    prefixStyle = labelField.getRequiredMessageField().getStyle();
                }
				labelField.getRequiredMessageField().setStyle(prefixStyle + ";" + "display: none;");
			}

			if (StringUtils.equals(labelPlacement, Position.RIGHT)) {
				labelField.setRenderColon(false);
			}
		}
	}

	/**
	 * @see org.kuali.rice.krad.uif.core.Component#getComponentTypeName()
	 */
	@Override
	public final String getComponentTypeName() {
		return "field";
	}

	/**
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(labelField);

		return components;
	}

	/**
	 * @see org.kuali.rice.krad.uif.field.Field#getLabel()
	 */
	public String getLabel() {
		if (labelField != null) {
			return labelField.getLabelText();
		}

		return null;
	}

	/**
	 * @see org.kuali.rice.krad.uif.field.Field#setLabel(java.lang.String)
	 */
	public void setLabel(String label) {
		if (labelField != null) {
			labelField.setLabelText(label);
		}
	}

	/**
	 * @see org.kuali.rice.krad.uif.field.Field#getShortLabel()
	 */
	public String getShortLabel() {
		return this.shortLabel;
	}

	/**
	 * @see org.kuali.rice.krad.uif.field.Field#setShortLabel(java.lang.String)
	 */
	public void setShortLabel(String shortLabel) {
		this.shortLabel = shortLabel;
	}

	/**
	 * Sets whether the label should be displayed
	 * 
	 * <p>
	 * Convenience method for configuration that sets the render indicator on
	 * the fields <code>LabelField</code> instance
	 * </p>
	 * 
	 * @param showLabel
	 *            boolean true if label should be displayed, false if the label
	 *            should not be displayed
	 */
	public void setShowLabel(boolean showLabel) {
		if (labelField != null) {
			labelField.setRender(showLabel);
		}
	}

	/**
	 * @see org.kuali.rice.krad.uif.field.Field#getLabelField()
	 */
	public LabelField getLabelField() {
		return this.labelField;
	}

	/**
	 * @see org.kuali.rice.krad.uif.field.Field#setLabelField(org.kuali.rice.krad.uif.field.LabelField)
	 */
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
	 * @see org.kuali.rice.krad.uif.field.Field#isLabelFieldRendered()
	 */
	public boolean isLabelFieldRendered() {
		return this.labelFieldRendered;
	}

	/**
	 * @see org.kuali.rice.krad.uif.field.Field#setLabelFieldRendered(boolean)
	 */
	public void setLabelFieldRendered(boolean labelFieldRendered) {
		this.labelFieldRendered = labelFieldRendered;
	}
}
