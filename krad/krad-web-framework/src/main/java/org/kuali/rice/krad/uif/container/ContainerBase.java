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
package org.kuali.rice.krad.uif.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.core.ComponentBase;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.ErrorsField;
import org.kuali.rice.krad.uif.field.HeaderField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.widget.Help;

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

	private boolean fieldContainer;

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
	 * <li>Sorts the containers list of components</li>
	 * <li>Initializes LayoutManager</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#performInitialization(org.kuali.rice.krad.uif.container.View)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		// sort items list by the order property
		List<? extends Component> sortedItems = (List<? extends Component>) ComponentUtils.sort(getItems(),
				itemOrderingSequence);
		setItems(sortedItems);

		if (layoutManager != null) {
			layoutManager.performInitialization(view, this);
		}
	}

	/**
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#performApplyModel(org.kuali.rice.krad.uif.container.View,
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
	 * The following finalization is performed:
	 * 
	 * <ul>
	 * <li>Sets the headerText of the header Group if it is blank</li>
	 * <li>Set the messageText of the summary MessageField if it is blank</li>
	 * <li>Finalizes LayoutManager</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#performFinalize(org.kuali.rice.krad.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.core.Component)
	 */
	@Override
	public void performFinalize(View view, Object model, Component parent) {
		super.performFinalize(view, model, parent);

		// if header title not given, use the container title
		if (header != null && StringUtils.isBlank(header.getHeaderText())) {
			header.setHeaderText(this.getTitle());
		}

		// setup summary message field if necessary
		if (summaryMessageField != null && StringUtils.isBlank(summaryMessageField.getMessageText())) {
			summaryMessageField.setMessageText(summary);
		}

		if (layoutManager != null) {
			layoutManager.performFinalize(view, model, this);
		}
	}

	/**
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#getNestedComponents()
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
	 * @see org.kuali.rice.krad.uif.container.Container#getErrorsField()
	 */
	@Override
	public ErrorsField getErrorsField() {
		return this.errorsField;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#setErrorsField(org.kuali.rice.krad.uif.field.ErrorsField)
	 */
	@Override
	public void setErrorsField(ErrorsField errorsField) {
		this.errorsField = errorsField;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#getHelp()
	 */
	@Override
	public Help getHelp() {
		return this.help;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#setHelp(org.kuali.rice.krad.uif.widget.Help)
	 */
	@Override
	public void setHelp(Help help) {
		this.help = help;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#getItems()
	 */
	@Override
	public abstract List<? extends Component> getItems();

	/**
	 * Setter for the containers list of components
	 * 
	 * @param items
	 */
	public abstract void setItems(List<? extends Component> items);

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
	 * @see org.kuali.rice.krad.uif.container.Container#getLayoutManager()
	 */
	@Override
	public LayoutManager getLayoutManager() {
		return this.layoutManager;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#setLayoutManager(org.kuali.rice.krad.uif.layout.LayoutManager)
	 */
	@Override
	public void setLayoutManager(LayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#getHeader()
	 */
	@Override
	public HeaderField getHeader() {
		return this.header;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#setHeader(org.kuali.rice.krad.uif.field.HeaderField)
	 */
	@Override
	public void setHeader(HeaderField header) {
		this.header = header;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#getFooter()
	 */
	@Override
	public Group getFooter() {
		return this.footer;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#setFooter(org.kuali.rice.krad.uif.container.Group)
	 */
	@Override
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
	 * @see org.kuali.rice.krad.uif.container.Container#getSummaryMessageField()
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
	 * @see org.kuali.rice.krad.uif.container.Container#getSummaryMessageField()
	 */
	@Override
	public MessageField getSummaryMessageField() {
		return this.summaryMessageField;
	}

	/**
	 * @see org.kuali.rice.krad.uif.container.Container#setSummaryMessageField(org.kuali.rice.krad.uif.field.MessageField)
	 */
	@Override
	public void setSummaryMessageField(MessageField summaryMessageField) {
		this.summaryMessageField = summaryMessageField;
	}

	/**
	 * Gets only the attribute fields that are nested in this container.  This is a subset of
	 * what getNestedComponents() returns.
	 * 
	 * @return
	 */
	public List<AttributeField> getAttributeFields(){
		List<AttributeField> attributeFields = new ArrayList<AttributeField>();
		for(Component c: this.getNestedComponents()){
			if(c instanceof AttributeField){
				attributeFields.add((AttributeField)c);
			}
		}
		return attributeFields;
		
	}

	/**
	 * This property is true if the container is used to display a group of fields that is visually a single
	 * field.
	 * @return the fieldContainer
	 */
	public boolean isFieldContainer() {
		return this.fieldContainer;
	}

	/**
	 * @param fieldContainer the fieldContainer to set
	 */
	public void setFieldContainer(boolean fieldContainer) {
		this.fieldContainer = fieldContainer;
	}
	
	

}
