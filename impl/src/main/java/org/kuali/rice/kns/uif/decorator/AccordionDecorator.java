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
import org.kuali.rice.kns.uif.field.HeaderField;

/**
 * Decorator that wraps a <code>Group</code> with an accordion (collapsible
 * behavior).
 * 
 * <p>
 * The accordion renderer will call the group's template to render within the
 * collapsible header. The accordionText is the text that will be displayed
 * within the header. If not set the header text from the group will be used.
 * The accordionHeader can be configured to style the header.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AccordionDecorator extends DecoratorBase {
	private static final long serialVersionUID = -2973529947669065264L;

	private String accordionText;
	private boolean defaultOpen;

	private HeaderField accordionHeader;

	public AccordionDecorator() {
		defaultOpen = true;
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set header text (if blank) on accordion header to the accordion text
	 * property</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		if (StringUtils.isBlank(accordionHeader.getHeaderText())) {
			accordionHeader.setHeaderText(accordionText);
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

		components.add(accordionHeader);

		return components;
	}

	/**
	 * Indicates whether the accordion should be open on initial display or
	 * closed. Defaults to true
	 * 
	 * @return boolean true if accordion should be open, false if it should be
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
	 * Text that should be displayed in the accordion header. This will be set
	 * as the header text in the accordion header
	 * 
	 * @return String header text
	 */
	public String getAccordionText() {
		return this.accordionText;
	}

	/**
	 * Setter for the accordion header text
	 * 
	 * @param accordionText
	 */
	public void setAccordionText(String accordionText) {
		this.accordionText = accordionText;
	}

	/**
	 * <code>HeaderField</code> for the accordion that defines the style class
	 * and other configuration for the header
	 * 
	 * @return HeaderField for accordion
	 */
	public HeaderField getAccordionHeader() {
		return this.accordionHeader;
	}

	/**
	 * Setter for the accordion header field
	 * 
	 * @param accordionHeader
	 */
	public void setAccordionHeader(HeaderField accordionHeader) {
		this.accordionHeader = accordionHeader;
	}

}
