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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.GroupField;
import org.kuali.rice.krad.uif.field.LabelField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.widget.TableTools;

import java.util.ArrayList;
import java.util.List;

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
public class TableLayoutManager extends GridLayoutManager implements CollectionLayoutManager {
	private static final long serialVersionUID = 3622267585541524208L;

	private boolean useShortLabels;
	private boolean repeatHeader;
	private LabelField headerFieldPrototype;

	private boolean renderSequenceField;
	private String conditionalRenderSequenceField;
	private boolean generateAutoSequence;
	private Field sequenceFieldPrototype;

	private GroupField actionFieldPrototype;

	private GroupField subCollectionGroupFieldPrototype;

	// internal counter for the data columns (not including sequence, action)
	private int numberOfDataColumns;

	private List<LabelField> headerFields;
	private List<Field> dataFields;

	private TableTools tableTools;
	private boolean headerAdded = false;

	public TableLayoutManager() {
		useShortLabels = true;
		repeatHeader = false;
		renderSequenceField = true;
		generateAutoSequence = false;

		headerFields = new ArrayList<LabelField>();
		dataFields = new ArrayList<Field>();
	}
	
	/**
	 * The following actions are performed:
	 * 
	 * <ul>
	 * <li>Sets sequence field prototype if auto sequence is true</li>
	 * <li>Initializes the prototypes</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.krad.uif.layout.BoxLayoutManager#performInitialization(org.kuali.rice.krad.uif.container.View,
	 *      org.kuali.rice.krad.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);
		
        if (generateAutoSequence && !(sequenceFieldPrototype instanceof MessageField)) {
            sequenceFieldPrototype = ComponentFactory.getMessageField();
        }

		view.getViewHelperService().performComponentInitialization(view, headerFieldPrototype);
		view.getViewHelperService().performComponentInitialization(view, sequenceFieldPrototype);
		view.getViewHelperService().performComponentInitialization(view, actionFieldPrototype);
		view.getViewHelperService().performComponentInitialization(view, subCollectionGroupFieldPrototype);
	}

	/**
	 * Sets up the final column count for rendering based on whether the
	 * sequence and action fields have been generated
	 * 
	 * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#performFinalize(org.kuali.rice.krad.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
	 */
	@Override
	public void performFinalize(View view, Object model, Container container) {
		super.performFinalize(view, model, container);

		CollectionGroup collectionGroup = (CollectionGroup) container;

		int totalColumns = getNumberOfDataColumns();
		if (renderSequenceField) {
			totalColumns++;
		}

		if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly()) {
			totalColumns++;
		}

		setNumberOfColumns(totalColumns);

	}

	/**
	 * Assembles the field instances for the collection line. The given sequence
	 * field prototype is copied for the line sequence field. Likewise a copy of
	 * the actionFieldPrototype is made and the given actions are set as the
	 * items for the action field. Finally the generated items are assembled
	 * together into the dataFields list with the given lineFields.
	 * 
	 * @see org.kuali.rice.krad.uif.layout.CollectionLayoutManager#buildLine(org.kuali.rice.krad.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.container.CollectionGroup,
	 *      java.util.List, java.util.List, java.lang.String, java.util.List,
	 *      java.lang.String, java.lang.Object, int)
	 */
	public void buildLine(View view, Object model, CollectionGroup collectionGroup, List<Field> lineFields,
			List<GroupField> subCollectionFields, String bindingPath, List<ActionField> actions, String idSuffix,
			Object currentLine, int lineIndex) {
		boolean isAddLine = lineIndex == -1;
		
        // if add line or first line set number of data columns
        if (isAddLine || ((!collectionGroup.isRenderAddLine() || collectionGroup.isReadOnly()) && (lineIndex == 0))) {
            if (isSuppressLineWrapping()) {
                setNumberOfDataColumns(lineFields.size());
            } else {
                setNumberOfDataColumns(getNumberOfColumns());
            }
        }

		// if add line build table header first
		// TODO: implement repeat header
		if (!headerAdded) {
			headerFields = new ArrayList<LabelField>();
			dataFields = new ArrayList<Field>();

			buildTableHeaderRows(collectionGroup, lineFields);
			ComponentUtils.pushObjectToContext(headerFields, UifConstants.ContextVariableNames.LINE, currentLine);
			ComponentUtils.pushObjectToContext(headerFields, UifConstants.ContextVariableNames.INDEX, new Integer(
					lineIndex));
			headerAdded = true;
		}

		// set label field rendered to true on line fields
		for (Field field : lineFields) {
			field.setLabelFieldRendered(true);

			// don't display summary message
			// TODO: remove once we have modifier
			ComponentUtils.setComponentPropertyDeep(field, "summaryMessageField.render", new Boolean(false));
		}

		int rowCount = calculateNumberOfRows(collectionGroup.getItems());
		int rowSpan = rowCount + subCollectionFields.size();

		// sequence field is always first and should span all rows for the line
		if (renderSequenceField) {
			Field sequenceField = null;
            if (!isAddLine) {
                sequenceField = ComponentUtils.copy(sequenceFieldPrototype, idSuffix);

                if (generateAutoSequence && (sequenceField instanceof MessageField)) {
                    ((MessageField) sequenceField).setMessageText(Integer.toString(lineIndex + 1));
                }
            }
			else {
				sequenceField = ComponentUtils.copy(collectionGroup.getAddLineLabelField(), idSuffix);
			}
			sequenceField.setRowSpan(rowSpan);

			if (sequenceField instanceof AttributeField) {
				((AttributeField) sequenceField).getBindingInfo().setBindByNamePrefix(bindingPath);
			}

			ComponentUtils.updateContextForLine(sequenceField, currentLine, lineIndex);

			dataFields.add(sequenceField);
		}

		// now add the fields in the correct position
		int cellPosition = 0;
		for (Field lineField : lineFields) {
			dataFields.add(lineField);

			cellPosition += lineField.getColSpan();

			// action field should be in last column
			if ((cellPosition == getNumberOfDataColumns()) && collectionGroup.isRenderLineActions()
					&& !collectionGroup.isReadOnly()) {
				GroupField lineActionsField = ComponentUtils.copy(actionFieldPrototype, idSuffix);

				ComponentUtils.updateContextForLine(lineActionsField, currentLine, lineIndex);
				lineActionsField.setRowSpan(rowSpan);
				lineActionsField.setItems(actions);

				dataFields.add(lineActionsField);
			}
		}

		// update colspan on sub-collection fields
		for (GroupField subCollectionField : subCollectionFields) {
			subCollectionField.setColSpan(numberOfDataColumns);
		}

		// add sub-collection fields to end of data fields
		dataFields.addAll(subCollectionFields);
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
	 * @param lineFields - fields for the data columns from which the headers are pulled
	 */
	protected void buildTableHeaderRows(CollectionGroup collectionGroup, List<Field> lineFields) {
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
		for (Field field : lineFields) {
		    if (!field.isRender() && StringUtils.isEmpty(field.getProgressiveRender())) {
		        continue;
		    }
		    
			cellPosition += field.getColSpan();
			addHeaderField(field, cellPosition);

			// add action header as last column in row
			if ((cellPosition == getNumberOfDataColumns()) && collectionGroup.isRenderLineActions()
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
		LabelField headerField = ComponentUtils.copy(headerFieldPrototype, "_c" + column);
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
		
		// check flag that indicates only one row should be created
		if (isSuppressLineWrapping()) {
		    return 1;
		}

		int cellCount = 0;
		for (Field field : items) {
			cellCount += field.getColSpan() + field.getRowSpan() - 1;
		}

		if (cellCount != 0) {
			rowCount = cellCount / getNumberOfDataColumns();
		}

		return rowCount;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.ContainerAware#getSupportedContainer()
	 */
	@Override
	public Class<? extends Container> getSupportedContainer() {
		return CollectionGroup.class;
	}

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(tableTools);
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
	 * Expression language string for conditionally setting the render sequence
	 * field property
	 * 
	 * @return String el that should evaluate to boolean
	 */
	public String getConditionalRenderSequenceField() {
		return this.conditionalRenderSequenceField;
	}

	/**
	 * Setter for the conditional render sequence field string
	 * 
	 * @param conditionalRenderSequenceField
	 */
	public void setConditionalRenderSequenceField(String conditionalRenderSequenceField) {
		this.conditionalRenderSequenceField = conditionalRenderSequenceField;
	}

	/**
	 * Attribute name to use as sequence value. For each collection line the
	 * value of this field on the line will be retrieved and used as the
	 * sequence value
	 * 
	 * @return String sequence property name
	 */
    public String getSequencePropertyName() {
        if ((sequenceFieldPrototype != null) && (sequenceFieldPrototype instanceof AttributeField)) {
            return ((AttributeField) sequenceFieldPrototype).getPropertyName();
        }

        return null;
    }

    /**
     * Setter for the sequence property name
     * 
     * @param sequencePropertyName
     */
    public void setSequencePropertyName(String sequencePropertyName) {
        if ((sequenceFieldPrototype != null) && (sequenceFieldPrototype instanceof AttributeField)) {
            ((AttributeField) sequenceFieldPrototype).setPropertyName(sequencePropertyName);
        }
    }
	
    /**
     * Indicates whether the sequence field should be generated with the current
     * line number
     * 
     * <p>
     * If set to true the sequence field prototype will be changed to a message
     * field (if not already a message field) and the text will be set to the
     * current line number
     * </p>
     * 
     * @return boolean true if the sequence field should be generated from the
     *         line number, false if not
     */
    public boolean isGenerateAutoSequence() {
        return this.generateAutoSequence;
    }

    /**
     * Setter for the generate auto sequence field
     * 
     * @param generateAutoSequence
     */
    public void setGenerateAutoSequence(boolean generateAutoSequence) {
        this.generateAutoSequence = generateAutoSequence;
    }

    /**
	 * <code>Field</code> instance to serve as a prototype for the
	 * sequence field. For each collection line this instance is copied and
	 * adjusted as necessary
	 * 
	 * @return Attribute field instance
	 */
	public Field getSequenceFieldPrototype() {
		return this.sequenceFieldPrototype;
	}

	/**
	 * Setter for the sequence field prototype
	 * 
	 * @param sequenceFieldPrototype
	 */
	public void setSequenceFieldPrototype(Field sequenceFieldPrototype) {
		this.sequenceFieldPrototype = sequenceFieldPrototype;
	}

	/**
	 * <code>GroupField</code> instance to serve as a prototype for the actions
	 * column. For each collection line this instance is copied and adjusted as
	 * necessary. Note the actual actions for the group come from the collection
	 * groups actions List
	 * (org.kuali.rice.krad.uif.container.CollectionGroup.getActionFields()). The
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
	 * @see org.kuali.rice.krad.uif.layout.CollectionLayoutManager#getSubCollectionGroupFieldPrototype()
	 */
	public GroupField getSubCollectionGroupFieldPrototype() {
		return this.subCollectionGroupFieldPrototype;
	}

	/**
	 * Setter for the sub-collection field group prototype
	 * 
	 * @param subCollectionGroupFieldPrototype
	 */
	public void setSubCollectionGroupFieldPrototype(GroupField subCollectionGroupFieldPrototype) {
		this.subCollectionGroupFieldPrototype = subCollectionGroupFieldPrototype;
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

	/**
     * @return the numberOfDataColumns
     */
    public int getNumberOfDataColumns() {
    	return this.numberOfDataColumns;
    }

	/**
     * @param numberOfDataColumns the numberOfDataColumns to set
     */
    public void setNumberOfDataColumns(int numberOfDataColumns) {
    	this.numberOfDataColumns = numberOfDataColumns;
    }

}