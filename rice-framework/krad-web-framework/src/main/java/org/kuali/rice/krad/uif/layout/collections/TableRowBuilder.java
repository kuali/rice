/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.uif.layout.collections;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.container.collections.LineBuilderContext;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.field.SpaceField;
import org.kuali.rice.krad.uif.layout.CollectionLayoutUtils;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ContextUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Builds out a collection line into a table row.
 *
 * TODO: This have duplicate logic from table layout manager, the goal is to move all logic from the layout
 * manager to this class
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see org.kuali.rice.krad.uif.layout.TableLayoutManager
 * @see org.kuali.rice.krad.uif.layout.collections.TableRow
 */
public class TableRowBuilder implements Serializable {
    private static final long serialVersionUID = 5098939594340088940L;

    private CollectionGroup collectionGroup;
    private TableLayoutManager tableLayoutManager;

    private LineBuilderContext lineBuilderContext;

    /**
     * Empty Constructor.
     */
    public TableRowBuilder() {

    }

    /**
     * Constructor taking the collection group instance and context for the line.
     *
     * @param collectionGroup collection group the table row is being built for
     * @param lineBuilderContext components and other configuration for the line to build
     */
    public TableRowBuilder(CollectionGroup collectionGroup, LineBuilderContext lineBuilderContext) {
        this.collectionGroup = collectionGroup;

        if (collectionGroup != null) {
            this.tableLayoutManager = (TableLayoutManager) collectionGroup.getLayoutManager();
        }

        this.lineBuilderContext = lineBuilderContext;
    }

    /**
     * Takes the context given for the line and builds out a table row instance.
     *
     * <p>The row is built out based on a determined order of special columns (sequence, line selection) and
     * then each field from the configured items list. Since the placement of the action column is configurable,
     * it is handled by the {@link org.kuali.rice.krad.uif.layout.collections.TableRowBuilder.ColumnCollector}</p>
     *
     * @return table row instance for the line
     */
    public TableRow buildRow() {
        ColumnCollector columnCollector = new ColumnCollector(tableLayoutManager.getActionColumnIndex());

        if (tableLayoutManager.isRenderSequenceField()) {
            addSequenceColumn(columnCollector);
        }

        if (collectionGroup.isIncludeLineSelectionField()) {
            addLineSelectColumn(columnCollector);
        }

        boolean hasGrouping = (tableLayoutManager.getGroupingPropertyNames() != null) || StringUtils.isNotBlank(
                tableLayoutManager.getGroupingTitle());
        if (hasGrouping) {
            addGroupingColumn(columnCollector);
        }

        // now add field configured on the collection group
        for (Field lineField : lineBuilderContext.getLineFields()) {
            Map<String, String> fieldDataAttributes = lineField.getDataAttributes();

            // skip grouping column for now (until logic is pulled from layout manager to use this)
            boolean hasRoleAttribute = (fieldDataAttributes != null) && fieldDataAttributes.containsKey(
                    UifConstants.DataAttributes.ROLE);
            if (hasRoleAttribute && UifConstants.RoleTypes.ROW_GROUPING.equals(fieldDataAttributes.get(
                    UifConstants.DataAttributes.ROLE))) {
                continue;
            }

            columnCollector.addColumn(lineField);
        }

        columnCollector.finishRow();

        return new TableRow(columnCollector.getColumns());
    }

    /**
     * Adds the sequence column to the given column collector.
     *
     * <p>Sequence column is created with a new message component for the add line, and by copying
     * {@link org.kuali.rice.krad.uif.layout.TableLayoutManager#getSequenceFieldPrototype()} for existing rows.</p>
     *
     * @param columnCollector object collecting the columns for the row
     */
    protected void addSequenceColumn(ColumnCollector columnCollector) {
        Field sequenceField;

        if (lineBuilderContext.isAddLine()) {
            sequenceField = ComponentFactory.getMessageField();

            Message sequenceMessage = ComponentUtils.copy(collectionGroup.getAddLineLabel(),
                    lineBuilderContext.getIdSuffix());
            ((MessageField) sequenceField).setMessage(sequenceMessage);
        } else {
            sequenceField = ComponentUtils.copy(tableLayoutManager.getSequenceFieldPrototype(),
                    lineBuilderContext.getIdSuffix());

            // ignore in validation processing
            sequenceField.addDataAttribute(UifConstants.DataAttributes.VIGNORE, "yes");

            if (tableLayoutManager.isGenerateAutoSequence() && (sequenceField instanceof MessageField)) {
                ((MessageField) sequenceField).setMessageText(Integer.toString(lineBuilderContext.getLineIndex() + 1));
            }

            if (sequenceField instanceof DataBinding) {
                ((DataBinding) sequenceField).getBindingInfo().setBindByNamePrefix(lineBuilderContext.getBindingPath());
            }
        }

        // TODO: needed to convert full layout logic to use the builder
        // sequenceField.setRowSpan(rowSpan);
        //   setCellAttributes(sequenceField);

        ContextUtils.updateContextForLine(sequenceField, collectionGroup, lineBuilderContext.getCurrentLine(),
                lineBuilderContext.getLineIndex(), lineBuilderContext.getIdSuffix());

        columnCollector.addColumn(sequenceField);
    }

    /**
     * Adds the line select column to the given column collector.
     *
     * <p>The line select column is used to select rows for an action (such as lookup return).</p>
     *
     * @param columnCollector object collecting the columns for the row
     */
    protected void addLineSelectColumn(ColumnCollector columnCollector) {
        Field selectField = ComponentUtils.copy(tableLayoutManager.getSelectFieldPrototype(),
                lineBuilderContext.getIdSuffix());

        CollectionLayoutUtils.prepareSelectFieldForLine(selectField, collectionGroup,
                lineBuilderContext.getBindingPath(), lineBuilderContext.getCurrentLine());

        ContextUtils.updateContextForLine(selectField, collectionGroup, lineBuilderContext.getCurrentLine(),
                lineBuilderContext.getLineIndex(), lineBuilderContext.getIdSuffix());

        //setCellAttributes(selectField);

        columnCollector.addColumn(selectField);
    }

    /**
     * Adds the grouping column to the given column collector.
     *
     * <p>The grouping column is used when table grouping is on to render a header for the group. The data
     * tables plugin will pull the value from this column and render the header row.</p>
     *
     * @param columnCollector object collecting the columns for the row
     */
    protected void addGroupingColumn(ColumnCollector columnCollector) {
        // no grouping on add line, so just add blank field
        if (lineBuilderContext.isAddLine()) {
            SpaceField spaceField = ComponentFactory.getSpaceField();
            columnCollector.addColumn(spaceField);

            return;
        }

        MessageField groupingMessageField = ComponentFactory.getColGroupingField();

        StringBuilder groupingTitle = new StringBuilder();
        if (StringUtils.isNotBlank(tableLayoutManager.getGroupingTitle())) {
            groupingTitle.append(tableLayoutManager.getGroupingTitle());
        } else if (tableLayoutManager.getGroupingPropertyNames() != null) {
            for (String propertyName : tableLayoutManager.getGroupingPropertyNames()) {
                Object propertyValue = ObjectPropertyUtils.getPropertyValue(lineBuilderContext.getCurrentLine(),
                        propertyName);

                if (propertyValue == null) {
                    propertyValue = "Null";
                }

                if (groupingTitle.length() != 0) {
                    groupingTitle.append(", ");
                }

                groupingTitle.append(propertyValue);
            }

        }

        groupingMessageField.setMessageText(groupingTitle.toString());
        groupingMessageField.addDataAttribute(UifConstants.DataAttributes.ROLE, UifConstants.RoleTypes.ROW_GROUPING);

        columnCollector.addColumn(groupingMessageField);
    }

    /**
     * Helper class for collecting columsn that will make up a table row.
     */
    public class ColumnCollector implements Serializable {
        private static final long serialVersionUID = 7129699106011942622L;

        private int actionColumnIndex;

        private int currentIndex = 0;
        private List<Field> columns;

        /**
         * Constructor taking the column index for the action column.
         *
         * @param actionColumnIndex index for action column
         */
        public ColumnCollector(int actionColumnIndex) {
            this.actionColumnIndex = actionColumnIndex;
            this.columns = new ArrayList<Field>();
        }

        /**
         * Adds the given field instance as a column.
         *
         * <p>A check is made to see if actions should be rendered at the current position first, then the
         * field is added</p>
         *
         * @param column field instance to add
         */
        public void addColumn(Field column) {
            if (isRenderActions() && (actionColumnIndex == (currentIndex + 1))) {
                Field actionColumn = buildActionColumn();

                columns.add(actionColumn);
                currentIndex++;
            }

            columns.add(column);
            currentIndex++;
        }

        /**
         * Should be invoked after there are no more columns to add, so that the action column can be added
         * when it is configured to be the last column.
         */
        public void finishRow() {
            if (isRenderActions() && (actionColumnIndex == (currentIndex + 1)) || (actionColumnIndex == -1)) {
                Field actionColumn = buildActionColumn();

                columns.add(actionColumn);
            }
        }

        /**
         * Creates a field group instance that contains the actions for the row.
         *
         * <p>Field group is created by copying
         * {@link org.kuali.rice.krad.uif.layout.TableLayoutManager#getActionFieldPrototype()}, then the line
         * actions from the line context are moved to the field group</p>
         *
         * @return field group instance containing the actions
         */
        protected FieldGroup buildActionColumn() {
            FieldGroup lineActionsField = ComponentUtils.copy(tableLayoutManager.getActionFieldPrototype(),
                    lineBuilderContext.getIdSuffix());

            ContextUtils.updateContextForLine(lineActionsField, collectionGroup, lineBuilderContext.getCurrentLine(),
                    lineBuilderContext.getLineIndex(), lineBuilderContext.getIdSuffix());

            // lineActionsField.setRowSpan(rowSpan);
            lineActionsField.setItems(lineBuilderContext.getLineActions());

            if (lineActionsField.getWrapperCssClasses() != null && !lineActionsField.getWrapperCssClasses().contains(
                    CssConstants.Classes.ACTION_COLUMN_STYLE_CLASS)) {
                lineActionsField.getWrapperCssClasses().add(CssConstants.Classes.ACTION_COLUMN_STYLE_CLASS);
            } else {
                lineActionsField.setWrapperCssClasses(Arrays.asList(CssConstants.Classes.ACTION_COLUMN_STYLE_CLASS));
            }

            // setCellAttributes(lineActionsField);

            return lineActionsField;
        }

        /**
         * Indicates whether actions should be rendered based on the collection group configuration.
         *
         * @return boolean true if actions should be rendered, false if not
         */
        public boolean isRenderActions() {
            return collectionGroup.isRenderLineActions() && !Boolean.TRUE.equals(collectionGroup.getReadOnly());
        }

        /**
         * Returns the field instances that make up the row columns.
         *
         * @return list of fields
         */
        public List<Field> getColumns() {
            return columns;
        }
    }

}
