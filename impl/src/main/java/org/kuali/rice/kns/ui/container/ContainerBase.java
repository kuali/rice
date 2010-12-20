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

import java.util.List;

import org.kuali.rice.kns.ui.Component;
import org.kuali.rice.kns.ui.ComponentBase;
import org.kuali.rice.kns.ui.element.Message;
import org.kuali.rice.kns.ui.field.ErrorsField;
import org.kuali.rice.kns.ui.widget.Help;

/**
 * This is a description of what this class does - jkneal don't forget to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public abstract class ContainerBase extends ComponentBase implements Container {
	private String title;
	private String additionalErrorKeys;

	private Message titleMessage;
	private ErrorsField errors;
	private Help help;

	private List<Component> items;

	public ContainerBase() {

	}

	public abstract List<Class> getSupportedComponents();

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

}
