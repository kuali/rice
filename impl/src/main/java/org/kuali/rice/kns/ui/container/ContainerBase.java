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

import org.kuali.rice.kns.ui.Component;
import org.kuali.rice.kns.ui.ComponentBase;
import org.kuali.rice.kns.ui.LabeledComponent;
import org.kuali.rice.kns.ui.element.Message;
import org.kuali.rice.kns.ui.field.ErrorsField;
import org.kuali.rice.kns.ui.layout.LayoutManager;
import org.kuali.rice.kns.ui.widget.Help;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ContainerBase extends ComponentBase implements Container {
	private String title;
	private String additionalErrorKeys;

	private Message titleMessage;
	private ErrorsField errors;
	private Help help;
	private LayoutManager layoutManager;

	private List<Component> items;

	public ContainerBase() {
		items = new ArrayList<Component>();
	}

	public abstract List<Class> getSupportedComponents();

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>The contained list of components is iterated over to check for
	 * instances of <code>LabeledComponent</code>. If an instance is found, the
	 * component is checked to see whether the label field should be rendered,
	 * if so the <code>LabelField</code> is retrieved from the component and
	 * added to the list of container components. The label field is placed
	 * immediately before the component in the list.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();

		List<Component> allItems = new ArrayList<Component>();
		for (Component component : items) {
			if (component instanceof LabeledComponent) {
				boolean includeLabelField = ((LabeledComponent) component).isIncludeLabelField();
				if (includeLabelField) {
					allItems.add(((LabeledComponent) component).getLabelField());
				}
			}

			allItems.add(component);
		}
		
		this.items = allItems;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAdditionalErrorKeys() {
		return this.additionalErrorKeys;
	}

	public void setAdditionalErrorKeys(String additionalErrorKeys) {
		this.additionalErrorKeys = additionalErrorKeys;
	}

	public Message getTitleMessage() {
		return this.titleMessage;
	}

	public void setTitleMessage(Message titleMessage) {
		this.titleMessage = titleMessage;
	}

	public ErrorsField getErrors() {
		return this.errors;
	}

	public void setErrors(ErrorsField errors) {
		this.errors = errors;
	}

	public Help getHelp() {
		return this.help;
	}

	public void setHelp(Help help) {
		this.help = help;
	}

	public List<Component> getItems() {
		return this.items;
	}

	public void setItems(List<Component> items) {
		this.items = items;
	}

	public LayoutManager getLayoutManager() {
		return this.layoutManager;
	}

	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

}
