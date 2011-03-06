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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.UifConstants.IdSuffixes;
import org.kuali.rice.kns.uif.UifPropertyPaths;
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
import org.kuali.rice.kns.uif.widget.TableTools;

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
	private static final long serialVersionUID = 3622267585541524208L;

	private boolean useShortLabels;
	private boolean repeatHeader;
	private LabelField headerFieldPrototype;

	private boolean renderSequenceField;
	private AttributeField sequenceFieldPrototype;

	private GroupField actionFieldPrototype;

	// internal counter for the data columns (not including sequence, action)
	private int numberOfDataColumns;

	private List<LabelField> headerFields;
	private List<Field> dataFields;

	private TableTools tableTools;

	public TableLayoutManager() {
		useShortLabels = true;
		repeatHeader = false;
		renderSequenceField = true;

		headerFields = new ArrayList<LabelField>();
		dataFields = new ArrayList<Field>();
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * <li>Sets internal count of columns to configured number</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.layout.ContainerAware#initializeFromContainer(org.kuali.rice.kns.uif.container.Container,org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);

		numberOfDataColumns = getNumberOfColumns();
	}

	/**
	 * Builds up the table by creating a row for each collection line. The table
	 * body follows a standard Grid layout and is configured by the number of
	 * columns property. The sequence and action fields (if render enabled) will
	 * be placed at each end of the line (which could span multiple rows)
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performApplyModel(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performApplyModel(View view, Object model, Container container) {
		super.performApplyModel(view, model, container);

		CollectionGroup collectionGroup = (CollectionGroup) container;

		headerFields = new ArrayList<LabelField>();
		dataFields = new ArrayList<Field>();

		buildTableHeaderRows(collectionGroup);
		buildTableDataRows(view, collectionGroup, model);
	}

	/**
	 * Sets up the final column count for rendering based on whether the
	 * sequence and action fields have been generated
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performFinalize(View view, Object model, Container container) {
		super.performFinalize(view, model, container);

		CollectionGroup collectionGroup = (CollectionGroup) container;

		int totalColumns = numberOfDataColumns;
		if (renderSequenceField) {
			totalColumns++;
		}

		if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly()) {
			totalColumns++;
		}

		setNumberOfColumns(totalColumns);
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
		int rowCount = calculateNumberOfRows(collectionGroup.getItems());

		// first column is sequence label
		if (renderSequenceField) {
			sequenceFieldPrototype.setLabelFieldRendered(true);
			sequenceFieldPrototype.setRowSpan(rowCount);
			addHeaderField(sequenceFieldPrototype, 1);
		}

		// pull out label fields from the container's items
		int cellPosition = 0;
		for (Component component : collectionGroup.getItems()) {
			Field field = (Field) component;

			cellPosition += field.getColSpan();

			field.setLabelFieldRendered(true);
			addHeaderField(field, cellPosition);

			// add action header as last column in row
			if ((cellPosition == numberOfDataColumns) && collectionGroup.isRenderLineActions()
					&& !collectionGroup.isReadOnly()) {
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
		// create add line
		if (collectionGroup.isRenderAddLine() && !collectionGroup.isReadOnly()) {
			buildAddLine(view, collectionGroup, model);
		}

		// get the collection for this group from the model
		List<Object> modelCollection = ModelUtils.getPropertyValue(model, ((DataBinding) collectionGroup)
				.getBindingInfo().getBindingPath());

		// for each collection row create a set of fields
		if (modelCollection != null) {
			for (int index = 0; index < modelCollection.size(); index++) {
				String bindingPathPrefix = collectionGroup.getBindingInfo().getBindingName() + "[" + index + "]";
				String idSuffix = "_" + index;

				buildLine(view, collectionGroup, sequenceFieldPrototype, bindingPathPrefix, idSuffix,
						collectionGroup.getLineActions(index), false);
			}
		}

	}

	/**
	 * Builds the table rows for holding the collection add line. If the text
	 * given by the collection group add line label
	 * (org.kuali.rice.kns.uif.container.CollectionGroup.getAddLineLabelField())
	 * is placed into the sequence field for the line. Furthermore the line
	 * actions are set from the add line actions configured on the collection
	 * group. Also the binding is setup for the add line and if the managed by
	 * the framework (bound to generic form Map) then the Map entry is
	 * initialized.
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param collectionGroup
	 *            - collection group the layout manager applies to
	 * @param model
	 *            - Object containing the view data, should extend UifFormBase
	 *            if using framework managed new lines
	 */
	protected void buildAddLine(View view, CollectionGroup collectionGroup, Object model) {
		String addLineBindingPath = "";
		boolean bindAddLineToForm = false;
		if (StringUtils.isNotBlank(collectionGroup.getAddLineName())) {
			addLineBindingPath = collectionGroup.getAddLineBindingInfo().getBindingPath();
		}
		else {
			addLineBindingPath = collectionGroup.initNewCollectionLine(model, false);
			collectionGroup.getAddLineBindingInfo().setBindingPath(addLineBindingPath);
			bindAddLineToForm = true;
		}

		buildLine(view, collectionGroup, collectionGroup.getAddLineLabelField(), addLineBindingPath,
				IdSuffixes.ADD_LINE, collectionGroup.getAddLineActions(), bindAddLineToForm);
	}

	/**
	 * Builds the field instances for the collection line. The given sequence
	 * field is copied for the line sequence field. The original items specified
	 * on the <code>CollectionGroup</code> are also copied and adjusted as
	 * necessary for the line (binding path, id). Finally a copy of the
	 * actionFieldPrototype is made and the given actions are set as the fields
	 * items. All fields are added to the dataFields List which is picked up by
	 * the renderer.
	 * 
	 * @param view
	 *            - view instance the collection belongs to
	 * @param collectionGroup
	 *            - collection group the layout manager applies to
	 * @param lineSequenceField
	 *            - Field instance to use as a prototype for the line sequence
	 *            field
	 * @param lineBindingPath
	 *            - binding path for the line fields (if DataBinding)
	 * @param idSuffix
	 *            - suffix to use for the field items (in order to maintain
	 *            unique ids)
	 * @param actions
	 *            - List of actions to set in the lines action column
	 * @param bindLineToForm
	 *            - whether the bindToForm property on the items bindingInfo
	 *            should be set to true (needed for add line)
	 */
	protected void buildLine(View view, CollectionGroup collectionGroup, Field lineSequenceField,
			String lineBindingPath, String idSuffix, List<ActionField> actions, boolean bindLineToForm) {
		// sequence field is always first and should span all rows for the line
		if (renderSequenceField) {
			Field sequenceField = ComponentUtils.copy(lineSequenceField);

			int rowCount = calculateNumberOfRows(collectionGroup.getItems());
			sequenceField.setRowSpan(rowCount);

			if (sequenceField instanceof AttributeField) {
				((AttributeField) sequenceField).getBindingInfo().setBindByNamePrefix(lineBindingPath);
				view.getViewIndex().addAttributeField((AttributeField) sequenceField);
			}

			dataFields.add(sequenceField);
		}

		// copy fields adding the collection line to their binding prefix
		List<? extends Field> lineFields = ComponentUtils.copyFieldList(collectionGroup.getItems(), lineBindingPath);
		ComponentUtils.updateIdsWithSuffix(lineFields, idSuffix);

		if (bindLineToForm) {
			ComponentUtils.setComponentsPropertyDeep(lineFields, UifPropertyPaths.BIND_TO_FORM, new Boolean(true));
		}
		view.getViewIndex().addFields(lineFields);

		// now add the fields in the correct position
		int cellPosition = 0;
		for (Field lineField : lineFields) {
			dataFields.add(lineField);

			cellPosition += lineField.getColSpan();

			// action field should be in last column
			if ((cellPosition == numberOfDataColumns) && collectionGroup.isRenderLineActions()
					&& !collectionGroup.isReadOnly()) {
				GroupField lineActionsField = ComponentUtils.copy(actionFieldPrototype);
				lineActionsField.setItems(actions);
				ComponentUtils.updateIdsWithSuffix(lineActionsField, idSuffix);

				dataFields.add(lineActionsField);
			}
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
	protected int calculateNumberOfRows(List<? extends Field> items) {
		int rowCount = 0;

		int cellCount = 0;
		for (Field field : items) {
			cellCount += field.getColSpan() + field.getRowSpan() - 1;
		}

		if (cellCount != 0) {
			rowCount = cellCount / numberOfDataColumns;
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
		components.addAll(headerFields);
		components.addAll(dataFields);

		return components;
	}

	/**
	 * Indicates whether the short label for the collection field should be used
	 * as the table header or the regular label
	 * 
	 * @return boolean true if short label should be used, false if long label
	 *         should be used
	 */
	public boolean isUseShortLabels() {
		return this.useShortLabels;
	}

	/**
	 * Setter for the use short label indicator
	 * 
	 * @param useShortLabels
	 */
	public void setUseShortLabels(boolean useShortLabels) {
		this.useShortLabels = useShortLabels;
	}

	/**
	 * Indicates whether the header should be repeated before each collection
	 * row. If false the header is only rendered at the beginning of the table
	 * 
	 * @return boolean true if header should be repeated, false if it should
	 *         only be rendered once
	 */
	public boolean isRepeatHeader() {
		return this.repeatHeader;
	}

	/**
	 * Setter for the repeat header indicator
	 * 
	 * @param repeatHeader
	 */
	public void setRepeatHeader(boolean repeatHeader) {
		this.repeatHeader = repeatHeader;
	}

	/**
	 * <code>LabelField</code> instance to use as a prototype for creating the
	 * tables header fields. For each header field the prototype will be copied
	 * and adjusted as necessary
	 * 
	 * @return LabelField instance to serve as prototype
	 */
	public LabelField getHeaderFieldPrototype() {
		return this.headerFieldPrototype;
	}

	/**
	 * Setter for the header field prototype
	 * 
	 * @param headerFieldPrototype
	 */
	public void setHeaderFieldPrototype(LabelField headerFieldPrototype) {
		this.headerFieldPrototype = headerFieldPrototype;
	}

	/**
	 * List of <code>LabelField</code> instances that should be rendered to make
	 * up the tables header
	 * 
	 * @return List of label field instances
	 */
	public List<LabelField> getHeaderFields() {
		return this.headerFields;
	}

	/**
	 * Indicates whether the sequence field should be rendered for the
	 * collection
	 * 
	 * @return boolean true if sequence field should be rendered, false if not
	 */
	public boolean isRenderSequenceField() {
		return this.renderSequenceField;
	}

	/**
	 * Setter for the render sequence field indicator
	 * 
	 * @param renderSequenceField
	 */
	public void setRenderSequenceField(boolean renderSequenceField) {
		this.renderSequenceField = renderSequenceField;
	}

	/**
	 * Attribute name to use as sequence value. For each collection line the
	 * value of this field on the line will be retrieved and used as the
	 * sequence value
	 * 
	 * @return String sequence property name
	 */
	public String getSequencePropertyName() {
		if (sequenceFieldPrototype != null) {
			return sequenceFieldPrototype.getPropertyName();
		}

		return null;
	}

	/**
	 * Setter for the sequence property name
	 * 
	 * @param sequencePropertyName
	 */
	public void setSequencePropertyName(String sequencePropertyName) {
		if (sequenceFieldPrototype != null) {
			sequenceFieldPrototype.setPropertyName(sequencePropertyName);
		}
	}

	/**
	 * <code>AttributeField</code> instance to serve as a prototype for the
	 * sequence field. For each collection line this instance is copied and
	 * adjusted as necessary
	 * 
	 * @return Attribute field instance
	 */
	public AttributeField getSequenceFieldPrototype() {
		return this.sequenceFieldPrototype;
	}

	/**
	 * Setter for the sequence field prototype
	 * 
	 * @param sequenceFieldPrototype
	 */
	public void setSequenceFieldPrototype(AttributeField sequenceFieldPrototype) {
		this.sequenceFieldPrototype = sequenceFieldPrototype;
	}

	/**
	 * <code>GroupField</code> instance to serve as a prototype for the actions
	 * column. For each collection line this instance is copied and adjusted as
	 * necessary. Note the actual actions for the group come from the collection
	 * groups actions List
	 * (org.kuali.rice.kns.uif.container.CollectionGroup.getActionFields()). The
	 * GroupField prototype is useful for setting styling of the actions column
	 * and for the layout of the action fields. Note also the label associated
	 * with the prototype is used for the action column header
	 * 
	 * @return GroupField instance
	 */
	public GroupField getActionFieldPrototype() {
		return this.actionFieldPrototype;
	}

	/**
	 * Setter for the action field prototype
	 * 
	 * @param actionFieldPrototype
	 */
	public void setActionFieldPrototype(GroupField actionFieldPrototype) {
		this.actionFieldPrototype = actionFieldPrototype;
	}

	/**
	 * List of <code>Field</code> instances that make up the tables body. Pulled
	 * by the layout manager template to send through the Grid layout
	 * 
	 * @return List<Field> table body fields
	 */
	public List<Field> getDataFields() {
		return this.dataFields;
	}

	/**
	 * Widget associated with the table to add functionality such as sorting,
	 * paging, and export
	 * 
	 * @return TableTools instance
	 */
	public TableTools getTableTools() {
		return this.tableTools;
	}

	/**
	 * Setter for the table tools widget
	 * 
	 * @param tableTools
	 */
	public void setTableTools(TableTools tableTools) {
		this.tableTools = tableTools;
	}

}
