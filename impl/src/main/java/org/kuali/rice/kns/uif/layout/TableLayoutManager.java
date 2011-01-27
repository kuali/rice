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
package org.kuali.rice.kns.uif.layout;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.AttributeField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.field.GroupField;
import org.kuali.rice.kns.uif.field.LabelField;
import org.kuali.rice.kns.uif.util.ComponentUtils;
import org.kuali.rice.kns.uif.util.ModelUtils;
import org.kuali.rice.kns.uif.widget.TableDecorator;

/**
 * Layout manager that works with <code>CollectionGroup</code> components and
 * renders the collection as a Table
 * 
 * <p>
 * Based on the fields defined, the <code>TableLayoutManager</code> will
 * dynamically create instances of the fields for each collection row. In
 * addition, the manager can create standard fields like the action and sequence
 * fields for each row. The manager supports options inherited from the
 * <code>GridLayoutManager</code> such as rowSpan, colSpan, and cell width
 * settings.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TableLayoutManager extends GridLayoutManager {
	private boolean useShortLabels;
	private boolean repeatHeader;
	private LabelField headerFieldPrototype;

	private boolean renderSequenceField;
	private AttributeField sequenceFieldPrototype;

	private GroupField actionFieldPrototype;

	private List<LabelField> headerFields;
	private List<Field> dataFields;

	private TableDecorator tableDecorator;

	public TableLayoutManager() {
		useShortLabels = true;
		repeatHeader = false;
		renderSequenceField = true;

		headerFields = new ArrayList<LabelField>();
		dataFields = new ArrayList<Field>();
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.ContainerAware#initializeFromContainer(org.kuali.rice.kns.uif.container.Container,org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);

	}

	/**
	 * Builds up the table by creating a row for each collection line
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performApplyModel(View view, Object model, Container container) {
		super.performApplyModel(view, model, container);

		if (!(container instanceof CollectionGroup)) {
			throw new IllegalArgumentException(
					"Only CollectionGroup containers supported by the TableLayoutManager, found container class: "
							+ container.getClass());
		}

		CollectionGroup collectionGroup = (CollectionGroup) container;

		buildTableHeaderRows(collectionGroup);
		buildTableDataRows(view, collectionGroup, model);
	}

	/**
	 * Create the <code>LabelField</code> instances that will be used to render
	 * the table header
	 * 
	 * <p>
	 * For each column, a copy of headerFieldPrototype is made that determines
	 * the label configuration. The actual label text comes from the field for
	 * which the header applies to. The first column is always the sequence (if
	 * enabled) and the last column contains the actions. Both the sequence and
	 * action header fields will span all rows for the header.
	 * </p>
	 * 
	 * <p>
	 * The headerFields list will contain the final list of header fields built
	 * </p>
	 * 
	 * @param collectionGroup
	 *            - CollectionGroup container the table applies to
	 */
	protected void buildTableHeaderRows(CollectionGroup collectionGroup) {
		// row count needed to determine the row span for the sequence and
		// action fields, since they should span all rows for the line
		int rowCount = calculateNumberOfRows((List<Field>) collectionGroup.getItems());

		int cellPosition = 0;

		// first column is sequence label
		if (renderSequenceField) {
			sequenceFieldPrototype.setLabelFieldRendered(true);
			sequenceFieldPrototype.setRowSpan(rowCount);
			addHeaderField(sequenceFieldPrototype, 1);

			cellPosition = 1;
		}

		// pull out label fields from the container's items
		for (Component component : collectionGroup.getItems()) {
			Field field = (Field) component;

			cellPosition += field.getColSpan();

			field.setLabelFieldRendered(true);
			addHeaderField(field, cellPosition);

			// add action header as last column in row
			if ((cellPosition == getNumberOfColumns()) && collectionGroup.isRenderLineActions()) {
				actionFieldPrototype.setLabelFieldRendered(true);
				actionFieldPrototype.setRowSpan(rowCount);
				addHeaderField(actionFieldPrototype, cellPosition);
			}
		}
	}

	/**
	 * Creates a new instance of the header field prototype and then sets the
	 * label to the short (if useShortLabels is set to true) or long label of
	 * the given component. After created the header field is added to the list
	 * making up the table header
	 * 
	 * @param field
	 *            - field instance the header field is being created for
	 * @param column
	 *            - column number for the header, used for setting the id
	 */
	protected void addHeaderField(Field field, int column) {
		LabelField headerField = ComponentUtils.copy(headerFieldPrototype, "_" + column);
		if (useShortLabels) {
			headerField.setLabelText(field.getLabel());
		}
		else {
			headerField.setLabelText(field.getLabel());
		}

		headerField.setRowSpan(field.getRowSpan());
		headerField.setColSpan(field.getColSpan());

		if ((field.getRequired() != null) && field.getRequired().booleanValue()) {
			headerField.getRequiredMessageField().setRender(true);
		}
		else {
			headerField.getRequiredMessageField().setRender(false);
		}

		headerFields.add(headerField);
	}

	/**
	 * Creates the <code>Field</code> instances that make up the tables body
	 * 
	 * <p>
	 * The corresponding collection is retrieved from the model and iterated
	 * over to create the necessary fields. If enabled the sequence and action
	 * fields are created for each line as well. The binding path for fields
	 * that implement <code>DataBinding</code> is adjusted to point to the
	 * collection line it is apart of. For example, field 'number' of collection
	 * 'accounts' for line 1 will be set to 'accounts[0].number', and for line 2
	 * 'accounts[1].number'. Finally parameters are set on the line's action
	 * fields to indicate what collection and line they apply to.
	 * </p>
	 * 
	 * @param view
	 *            - View instance the collection belongs to
	 * @param collectionGroup
	 *            - CollectionGroup container the table applies to
	 * @param model
	 *            - Top level object containing the data (could be the form or a
	 *            top level business object, dto)
	 */
	protected void buildTableDataRows(View view, CollectionGroup collectionGroup, Object model) {
		// get the collection for this group from the model
		List<Object> modelCollection = (List<Object>) ModelUtils.getPropertyValue(model,
				((DataBinding) collectionGroup).getBindingInfo().getBindingPath());

		// for each collection row create a set of fields
		for (int index = 0; index < modelCollection.size(); index++) {
			String bindingPathPrefix = collectionGroup.getBindingInfo().getBindingName() + "[" + index + "]";
			String idSuffix = "_" + index;

			// copy fields adding the collection line to their binding prefix
			List<Field> lineFields = ComponentUtils.copyFieldList((List<Field>) collectionGroup.getItems(),
					bindingPathPrefix);
			ComponentUtils.updateIds(lineFields, idSuffix);
			view.getViewIndex().addFields(lineFields);

			// add line fields to the dataFields List, creating the sequence and
			// action fields in the appropriate place if needed
			// sequence field should be first column
			if (renderSequenceField) {
				AttributeField lineSequenceField = ComponentUtils.copyField(sequenceFieldPrototype, bindingPathPrefix,
						idSuffix);
				view.getViewIndex().addAttributeField(lineSequenceField);
				dataFields.add(lineSequenceField);
			}

			// actions field should be last column
			int cellPosition = 0;
			for (Field lineField : lineFields) {
				dataFields.add(lineField);

				cellPosition += lineField.getColSpan();

				// action field should be in last column
				if ((cellPosition == getNumberOfColumns()) && collectionGroup.isRenderLineActions()) {
					GroupField lineActionsField = ComponentUtils.copy(actionFieldPrototype, idSuffix);

					List<ActionField> lineActions = collectionGroup.getLineActions(index);
					lineActionsField.setItems(lineActions);
					dataFields.add(lineActionsField);
				}
			}
		}

		// adjust the number of columns if the sequence or action fields were
		// generated
		if (renderSequenceField) {
			setNumberOfColumns(getNumberOfColumns() + 1);
		}

		if (collectionGroup.isRenderLineActions()) {
			setNumberOfColumns(getNumberOfColumns() + 1);
		}
	}

	/**
	 * Calculates how many rows will be needed per collection line to display
	 * the list of fields. Assumption is made that the total number of cells the
	 * fields take up is evenly divisible by the configured number of columns
	 * 
	 * @param items
	 *            - list of items that make up one collection line
	 * @return int number of rows
	 */
	protected int calculateNumberOfRows(List<Field> items) {
		int rowCount = 0;

		int cellCount = 0;
		for (Field field : items) {
			cellCount += field.getColSpan() + field.getRowSpan() - 1;
		}

		if (cellCount != 0) {
			rowCount = cellCount / getNumberOfColumns();
		}

		return rowCount;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.ContainerAware#getSupportedContainer()
	 */
	@Override
	public Class<? extends Container> getSupportedContainer() {
		return CollectionGroup.class;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(headerFieldPrototype);
		components.add(sequenceFieldPrototype);
		components.add(actionFieldPrototype);

		return components;
	}

	public boolean isUseShortLabels() {
		return this.useShortLabels;
	}

	public void setUseShortLabels(boolean useShortLabels) {
		this.useShortLabels = useShortLabels;
	}

	public boolean isRepeatHeader() {
		return this.repeatHeader;
	}

	public void setRepeatHeader(boolean repeatHeader) {
		this.repeatHeader = repeatHeader;
	}

	public LabelField getHeaderFieldPrototype() {
		return this.headerFieldPrototype;
	}

	public void setHeaderFieldPrototype(LabelField headerFieldPrototype) {
		this.headerFieldPrototype = headerFieldPrototype;
	}

	public List<LabelField> getHeaderFields() {
		return this.headerFields;
	}

	public void setHeaderFields(List<LabelField> headerFields) {
		this.headerFields = headerFields;
	}

	public boolean isRenderSequenceField() {
		return this.renderSequenceField;
	}

	public void setRenderSequenceField(boolean renderSequenceField) {
		this.renderSequenceField = renderSequenceField;
	}

	public String getSequenceAttributeName() {
		if (sequenceFieldPrototype != null) {
			return sequenceFieldPrototype.getName();
		}

		return null;
	}

	public void setSequenceAttributeName(String sequenceAttributeName) {
		if (sequenceFieldPrototype != null) {
			sequenceFieldPrototype.setName(sequenceAttributeName);
		}
	}

	public AttributeField getSequenceFieldPrototype() {
		return this.sequenceFieldPrototype;
	}

	public void setSequenceFieldPrototype(AttributeField sequenceFieldPrototype) {
		this.sequenceFieldPrototype = sequenceFieldPrototype;
	}

	public GroupField getActionFieldPrototype() {
		return this.actionFieldPrototype;
	}

	public void setActionFieldPrototype(GroupField actionFieldPrototype) {
		this.actionFieldPrototype = actionFieldPrototype;
	}

	public List<Field> getDataFields() {
		return this.dataFields;
	}

	public void setDataFields(List<Field> dataFields) {
		this.dataFields = dataFields;
	}

	public TableDecorator getTableDecorator() {
		return this.tableDecorator;
	}

	public void setTableDecorator(TableDecorator tableDecorator) {
		this.tableDecorator = tableDecorator;
	}

}
