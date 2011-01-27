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
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;

/**
 * Decorator that wraps a <code>Group</code> with a collapsible panel. To use
 * set class in the <code>List</code> of <code>ComponentDecorator</code> classes
 * for the Group. The panel group renderer will call the groups template to
 * render within the panel.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PanelGroupDecorator extends Group implements ComponentDecorator {
	private String panelText;
	private boolean defaultOpen;

	private Group decoratedGroup;

	public PanelGroupDecorator() {
		defaultOpen = true;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		// if header text on panel not given, use the panel text
		if (StringUtils.isBlank(getHeader().getHeaderText())) {
			getHeader().setHeaderText(this.getPanelText());
		}

		// if header text still blank, use title on decorated group
		if (StringUtils.isBlank(getHeader().getHeaderText()) && decoratedGroup != null) {
			getHeader().setHeaderText(getDecoratedGroup().getTitle());
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(decoratedGroup);

		return components;
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getComponentTypeName()
	 */
	@Override
	public String getComponentTypeName() {
		return "group";
	}

	@Override
	public void setDecoratedComponent(Component component) {
		this.decoratedGroup = (Group) component;
	}

	public Group getDecoratedGroup() {
		return this.decoratedGroup;
	}

	public void setDecoratedGroup(Group decoratedGroup) {
		this.decoratedGroup = decoratedGroup;
	}

	public boolean isDefaultOpen() {
		return this.defaultOpen;
	}

	public void setDefaultOpen(boolean defaultOpen) {
		this.defaultOpen = defaultOpen;
	}

	public String getPanelText() {
		return this.panelText;
	}

	public void setPanelText(String panelText) {
		this.panelText = panelText;
	}

}
