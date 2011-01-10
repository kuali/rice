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
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.ui.Component;
import org.kuali.rice.kns.ui.ComponentBase;
import org.kuali.rice.kns.ui.field.ErrorsField;
import org.kuali.rice.kns.ui.field.HeaderField;
import org.kuali.rice.kns.ui.field.MessageField;
import org.kuali.rice.kns.ui.layout.LayoutManager;
import org.kuali.rice.kns.ui.widget.Help;

/**
 * Base <code>Container</code> implementation
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ContainerBase extends ComponentBase implements Container {
	private String title;

	private String additionalErrorKeys;
	private ErrorsField errorsField;

	private Help help;
	private LayoutManager layoutManager;

	private boolean renderHeader;
	private boolean renderFooter;

	private HeaderField header;
	private Group footer;

	private String summary;
	private MessageField summaryMessageField;

	private List<Component> items;

	public ContainerBase() {
		renderHeader = true;
		renderFooter = true;

		items = new ArrayList<Component>();
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Sets the headerText of the header Group if it is blank</li>
	 * <li>Set the messageText of the summary MessageField if it is blank</li>
	 * <li>Initialize LayoutManager</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize(java.util.Map)
	 */
	@Override
	public void initialize(Map<String, String> options) {
		super.initialize(options);

		// if header title not given, use the container title
		if (header != null && StringUtils.isBlank(header.getHeaderText())) {
			header.setHeaderText(this.getTitle());
		}

		// setup summary message field if necessary
		if (summaryMessageField != null && StringUtils.isBlank(summaryMessageField.getMessageText())) {
			summaryMessageField.setMessageText(summary);
		}

		if (layoutManager != null) {
			layoutManager.initialize(options);
		}
	}

	/**
	 * @see org.kuali.rice.kns.ui.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(header);
		components.add(footer);
		components.add(errorsField);
		components.add(help);
		components.add(summaryMessageField);

		for (Component component : items) {
			components.add(component);
		}

		return components;
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

	public ErrorsField getErrorsField() {
		return this.errorsField;
	}

	public void setErrorsField(ErrorsField errorsField) {
		this.errorsField = errorsField;
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

	public HeaderField getHeader() {
		return this.header;
	}

	public void setHeader(HeaderField header) {
		this.header = header;
	}

	public Group getFooter() {
		return this.footer;
	}

	public void setFooter(Group footer) {
		this.footer = footer;
	}

	public boolean isRenderHeader() {
		return this.renderHeader;
	}

	public void setRenderHeader(boolean renderHeader) {
		this.renderHeader = renderHeader;
	}

	public boolean isRenderFooter() {
		return this.renderFooter;
	}

	public void setRenderFooter(boolean renderFooter) {
		this.renderFooter = renderFooter;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public MessageField getSummaryMessageField() {
		return this.summaryMessageField;
	}

	public void setSummaryMessageField(MessageField summaryMessageField) {
		this.summaryMessageField = summaryMessageField;
	}

}
