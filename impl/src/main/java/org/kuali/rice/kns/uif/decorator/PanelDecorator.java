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
package org.kuali.rice.kns.uif.decorator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.ComponentBase;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.HeaderField;

/**
 * Decorator that wraps a <code>Group</code> with a collapsible panel.
 * 
 * <p>
 * The panel renderer will call the group's template to render within the panel.
 * The panelText is the text that will be displayed within the panel. If not set
 * the header text from the group will be used. The panel header can be
 * configured to style the panel.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PanelDecorator extends ComponentBase implements ComponentDecorator {
	private static final long serialVersionUID = -2973529947669065264L;

	private String panelText;
	private boolean defaultOpen;

	private HeaderField panelHeader;

	public PanelDecorator() {
		defaultOpen = true;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set header text (if blank) on panel header to the panel text property
	 * </li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		if (StringUtils.isBlank(panelHeader.getHeaderText())) {
			panelHeader.setHeaderText(panelText);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.decorator.ComponentDecorator#getSupportedComponent()
	 */
	public Class<? extends Component> getSupportedComponent() {
		return Group.class;
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(panelHeader);

		return components;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getComponentTypeName()
	 */
	@Override
	public String getComponentTypeName() {
		return "panel";
	}

	/**
	 * Indicates whether the panel should be open on initial display or closed.
	 * Defaults to true
	 * 
	 * @return boolean true if panel should be open, false if it should be
	 *         closed
	 */
	public boolean isDefaultOpen() {
		return this.defaultOpen;
	}

	/**
	 * Setter for the default open indicator
	 * 
	 * @param defaultOpen
	 */
	public void setDefaultOpen(boolean defaultOpen) {
		this.defaultOpen = defaultOpen;
	}

	/**
	 * Text that should be displayed in the panel. This will be set as the
	 * header text in the panel header
	 * 
	 * @return String header text
	 */
	public String getPanelText() {
		return this.panelText;
	}

	/**
	 * Setter for the panel header text
	 * 
	 * @param panelText
	 */
	public void setPanelText(String panelText) {
		this.panelText = panelText;
	}

	/**
	 * <code>HeaderField</code> for the panel that defines the style class and
	 * other configuration for the header
	 * 
	 * @return HeaderField for panel
	 */
	public HeaderField getPanelHeader() {
		return this.panelHeader;
	}

	/**
	 * Setter for the panel header field
	 * 
	 * @param panelHeader
	 */
	public void setPanelHeader(HeaderField panelHeader) {
		this.panelHeader = panelHeader;
	}

}
