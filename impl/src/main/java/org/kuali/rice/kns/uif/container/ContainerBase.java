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
package org.kuali.rice.kns.uif.container;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.ComponentBase;
import org.kuali.rice.kns.uif.field.ErrorsField;
import org.kuali.rice.kns.uif.field.HeaderField;
import org.kuali.rice.kns.uif.field.MessageField;
import org.kuali.rice.kns.uif.layout.LayoutManager;
import org.kuali.rice.kns.uif.widget.Help;
import org.springframework.core.OrderComparator;

/**
 * Base <code>Container</code> implementation which container implementations
 * can extend
 * 
 * <p>
 * Provides properties for the basic <code>Container</code> functionality in
 * addition to default implementation of the lifecycle methods including some
 * setup of the header, items list, and layout manager
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ContainerBase extends ComponentBase implements Container {
	private static final long serialVersionUID = -4182226230601746657L;

	private int itemOrderingSequence;
	
	private String additionalMessageKeys;
	private ErrorsField errorsField;

	private Help help;
	private LayoutManager layoutManager;

	private HeaderField header;
	private Group footer;

	private String summary;
	private MessageField summaryMessageField;

	/**
	 * Default Constructor
	 */
	public ContainerBase() {
		itemOrderingSequence = 1;
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * <li>Sets the headerText of the header Group if it is blank</li>
	 * <li>Set the messageText of the summary MessageField if it is blank</li>
	 * <li>Sets the order on any components in the containers items list that do
	 * not have an order set, then sorts the list</li>
	 * <li>Initialize LayoutManager</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		// if header title not given, use the container title
		if (header != null && StringUtils.isBlank(header.getHeaderText())) {
			header.setHeaderText(this.getTitle());
		}

		// setup summary message field if necessary
		if (summaryMessageField != null && StringUtils.isBlank(summaryMessageField.getMessageText())) {
			summaryMessageField.setMessageText(summary);
		}

		// set order for any components with an order not set
		for (Component component : getItems()) {
			if (component.getOrder() == 0) {
				component.setOrder(itemOrderingSequence);
				itemOrderingSequence++;
			}
		}

		// sort the items list
		Collections.sort(getItems(), new OrderComparator());

		if (layoutManager != null) {
			layoutManager.performInitialization(view, this);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performApplyModel(View view, Object model) {
		super.performApplyModel(view, model);

		if (layoutManager != null) {
			layoutManager.performApplyModel(view, model, this);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object)
	 */
	@Override
	public void performFinalize(View view, Object model) {
		super.performFinalize(view, model);

		if (layoutManager != null) {
			layoutManager.performFinalize(view, model, this);
		}
	}

	/**
	 * @see org.kuali.rice.kns.uif.ComponentBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(header);
		components.add(footer);
		components.add(errorsField);
		components.add(help);
		components.add(summaryMessageField);

		for (Component component : getItems()) {
			components.add(component);
		}

		if (layoutManager != null) {
			components.addAll(layoutManager.getNestedComponents());
		}

		return components;
	}

	/**
	 * Additional keys that should be matching on when gathering errors or other
	 * messages for the <code>Container</code>
	 * 
	 * <p>
	 * Messages associated with the container will be displayed with the
	 * container grouping in the user interface. Typically, these are a result
	 * of problems with the containers fields or some other business logic
	 * associated with the containers information. The framework will by default
	 * include all the error keys for fields in the container, and also an
	 * errors associated with the containers id. Keys given here will be matched
	 * in addition to those defaults.
	 * </p>
	 * 
	 * <p>
	 * Multple keys can be given using the comma delimiter, the * wildcard is
	 * also allowed in the message key
	 * </p>
	 * 
	 * @return String additional message key string
	 */
	public String getAdditionalMessageKeys() {
		return this.additionalMessageKeys;
	}

	/**
	 * Setter for the components additional message key string
	 * 
	 * @param additionalMessageKeys
	 */
	public void setAdditionalMessageKeys(String additionalMessageKeys) {
		this.additionalMessageKeys = additionalMessageKeys;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#getErrorsField()
	 */
	public ErrorsField getErrorsField() {
		return this.errorsField;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#setErrorsField(org.kuali.rice.kns.uif.field.ErrorsField)
	 */
	public void setErrorsField(ErrorsField errorsField) {
		this.errorsField = errorsField;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#getHelp()
	 */
	public Help getHelp() {
		return this.help;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#setHelp(org.kuali.rice.kns.uif.widget.Help)
	 */
	public void setHelp(Help help) {
		this.help = help;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#getItems()
	 */
	public abstract List<? extends Component> getItems();

	/**
	 * For <code>Component</code> instances in the container's items list that
	 * do not have an order set, a default order number will be assigned using
	 * this property. The first component found in the list without an order
	 * will be assigned the configured initial value, and incremented by one for
	 * each component (without an order) found afterwards
	 * 
	 * @return int order sequence
	 */
	public int getItemOrderingSequence() {
		return this.itemOrderingSequence;
	}

	/**
	 * Setter for the container's item ordering sequence number (initial value)
	 * 
	 * @param itemOrderingSequence
	 */
	public void setItemOrderingSequence(int itemOrderingSequence) {
		this.itemOrderingSequence = itemOrderingSequence;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#getLayoutManager()
	 */
	public LayoutManager getLayoutManager() {
		return this.layoutManager;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#setLayoutManager(org.kuali.rice.kns.uif.layout.LayoutManager)
	 */
	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#getHeader()
	 */
	public HeaderField getHeader() {
		return this.header;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#setHeader(org.kuali.rice.kns.uif.field.HeaderField)
	 */
	public void setHeader(HeaderField header) {
		this.header = header;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#getFooter()
	 */
	public Group getFooter() {
		return this.footer;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#setFooter(org.kuali.rice.kns.uif.container.Group)
	 */
	public void setFooter(Group footer) {
		this.footer = footer;
	}

	/**
	 * Convenience setter for configuration to turn rendering of the header
	 * on/off
	 * 
	 * <p>
	 * For nested groups (like Field Groups) it is often necessary to only show
	 * the container body (the contained components). This method allows the
	 * header to not be displayed
	 * </p>
	 * 
	 * @param renderHeader
	 */
	public void setRenderHeader(boolean renderHeader) {
		if (header != null) {
			header.setRender(renderHeader);
		}
	}

	/**
	 * Convenience setter for configuration to turn rendering of the footer
	 * on/off
	 * 
	 * <p>
	 * For nested groups it is often necessary to only show the container body
	 * (the contained components). This method allows the footer to not be
	 * displayed
	 * </p>
	 * 
	 * @param renderFooter
	 */
	public void setRenderFooter(boolean renderFooter) {
		if (footer != null) {
			footer.setRender(renderFooter);
		}
	}

	/**
	 * Summary text for the container which will be used to set the summary
	 * message field
	 * 
	 * @return String summary text
	 * @see org.kuali.rice.kns.uif.container.Container#getSummaryMessageField()
	 */
	public String getSummary() {
		return this.summary;
	}

	/**
	 * Setter for the containers summary text
	 * 
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#getSummaryMessageField()
	 */
	public MessageField getSummaryMessageField() {
		return this.summaryMessageField;
	}

	/**
	 * @see org.kuali.rice.kns.uif.container.Container#setSummaryMessageField(org.kuali.rice.kns.uif.field.MessageField)
	 */
	public void setSummaryMessageField(MessageField summaryMessageField) {
		this.summaryMessageField = summaryMessageField;
	}

}
