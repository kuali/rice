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
package org.kuali.rice.krad.uif.layout;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.util.ColumnCalculationInfo;
import org.kuali.rice.krad.uif.widget.RichTable;

/**
 * Layout manager that works with {@code CollectionGroup} components and renders the collection as a
 * Table.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface TableLayoutManager extends CollectionLayoutManager {

    /**
     * Indicates the number of columns that should make up one row of data.
     *
     * <p>If the item count is greater than the number of columns, a new row will
     * be created to render the remaining items (and so on until all items are
     * placed).</p>
     *
     * <p>Note this does not include any generated columns by the layout manager,
     * so the final column count could be greater (if label fields are
     * separate).</p>
     *
     * @return int number of columns
     */
    int getNumberOfColumns();

    /**
     * @see TableLayoutManager#getNumberOfColumns()
     */
    void setNumberOfColumns(int numberOfColumns);

    /**
     * Indicates whether the number of columns for the table data should match
     * the number of fields given in the container's items list (so that each
     * field takes up one column without wrapping), this overrides the configured
     * numberOfColumns.
     *
     * <p>If set to true during the initialize phase the number of columns will be
     * set to the size of the container's field list, if false the configured
     * number of columns is used</p>
     *
     * @return true if the column count should match the container's
     *         field count, false to use the configured number of columns
     */
    boolean isSuppressLineWrapping();

    /**
     * @see TableLayoutManager#isSuppressLineWrapping()
     */
    void setSuppressLineWrapping(boolean suppressLineWrapping);

    /**
     * Indicates whether alternating row styles should be applied.
     *
     * <p>Indicator to layout manager templates to apply alternating row styles.
     * See the configured template for the actual style classes used</p>
     *
     * @return true if alternating styles should be applied, false if
     *         all rows should have the same style
     */
    boolean isApplyAlternatingRowStyles();

    /**
     * @see TableLayoutManager#isApplyAlternatingRowStyles()
     */
    void setApplyAlternatingRowStyles(boolean applyAlternatingRowStyles);

    /**
     * Indicates whether the manager should default the cell widths.
     *
     * <p>If true, the manager will set the cell width by equally dividing by the
     * number of columns</p>
     *
     * @return true if default cell widths should be applied, false if
     *         no defaults should be applied
     */
    boolean isApplyDefaultCellWidths();

    /**
     * @see TableLayoutManager#isApplyDefaultCellWidths()
     */
    void setApplyDefaultCellWidths(boolean applyDefaultCellWidths);

    /**
     * List of styles for each row.
     *
     * <p>Each entry in the list gives the style for the row with the same index. This style will be added to
     * the <tr> tag when the table rows are rendered in the grid.tag. This is used to store the styles for newly added lines
     * and other special cases like the add item row.</p>
     *
     * @return list of styles for the rows
     */
    List<String> getRowCssClasses();

    /**
     * @see TableLayoutManager#getRowCssClasses()
     */
    void setRowCssClasses(List<String> rowCssClasses);

    /**
     * List of data attributes for each row.
     *
     * <p>Each entry in the list gives the data attributes for the row with the same index. These data attributes will be added to
     * the <tr> tag when the table rows are rendered in the grid.tag. This is used to store the data attributes for newly added lines
     * and other special cases like the add item row.</p>
     *
     * @return list of styles for the rows
     */
    List<String> getRowDataAttributes();

    /**
     * @see TableLayoutManager#getRowDataAttributes()
     */
    void setRowDataAttributes(List<String> rowDataAttributes);

    /**
     * Indicates whether the short label for the collection field should be used as the table header
     * or the regular label
     *
     * @return true if short label should be used, false if long label should be used
     */
    boolean isUseShortLabels();

    /**
     * Setter for the use short label indicator
     *
     * @param useShortLabels
     */
    void setUseShortLabels(boolean useShortLabels);

    /**
     * Indicates whether the header should be repeated before each collection row. If false the
     * header is only rendered at the beginning of the table
     *
     * @return true if header should be repeated, false if it should only be rendered once
     */
    boolean isRepeatHeader();

    /**
     * Setter for the repeat header indicator
     *
     * @param repeatHeader
     */
    void setRepeatHeader(boolean repeatHeader);

    /**
     * {@code Label} instance to use as a prototype for creating the tables header fields. For each
     * header field the prototype will be copied and adjusted as necessary
     *
     * @return Label instance to serve as prototype
     */
    Label getHeaderLabelPrototype();

    /**
     * Setter for the header field prototype
     *
     * @param headerLabelPrototype
     */
    void setHeaderLabelPrototype(Label headerLabelPrototype);

    /**
     * List of {@code Label} instances that should be rendered to make up the tables header
     *
     * @return List of label field instances
     */
    List<Label> getHeaderLabels();

    /**
     * Indicates whether the sequence field should be rendered for the collection
     *
     * @return true if sequence field should be rendered, false if not
     */
    boolean isRenderSequenceField();

    /**
     * Setter for the render sequence field indicator
     *
     * @param renderSequenceField
     */
    void setRenderSequenceField(boolean renderSequenceField);

    /**
     * Attribute name to use as sequence value. For each collection line the value of this field on
     * the line will be retrieved and used as the sequence value
     *
     * @return sequence property name
     */
    String getSequencePropertyName();

    /**
     * Setter for the sequence property name
     *
     * @param sequencePropertyName
     */
    void setSequencePropertyName(String sequencePropertyName);

    /**
     * Indicates whether the sequence field should be generated with the current line number
     *
     * <p>
     * If set to true the sequence field prototype will be changed to a message field (if not
     * already a message field) and the text will be set to the current line number
     * </p>
     *
     * @return true if the sequence field should be generated from the line number, false if not
     */
    boolean isGenerateAutoSequence();

    /**
     * Setter for the generate auto sequence field
     *
     * @param generateAutoSequence
     */
    void setGenerateAutoSequence(boolean generateAutoSequence);

    /**
     * {@code Field} instance to serve as a prototype for the sequence field. For each collection
     * line this instance is copied and adjusted as necessary
     *
     * @return Attribute field instance
     */
    Field getSequenceFieldPrototype();

    /**
     * Setter for the sequence field prototype
     *
     * @param sequenceFieldPrototype
     */
    void setSequenceFieldPrototype(Field sequenceFieldPrototype);

    /**
     * {@code FieldGroup} instance to serve as a prototype for the actions column. For each
     * collection line this instance is copied and adjusted as necessary. Note the actual actions
     * for the group come from the collection groups actions List
     * (org.kuali.rice.krad.uif.container.CollectionGroup.getActions()). The FieldGroup prototype is
     * useful for setting styling of the actions column and for the layout of the action fields.
     * Note also the label associated with the prototype is used for the action column header
     *
     * @return GroupField instance
     */
    FieldGroup getActionFieldPrototype();

    /**
     * Setter for the action field prototype
     *
     * @param actionFieldPrototype
     */
    void setActionFieldPrototype(FieldGroup actionFieldPrototype);

    /**
     * Indicates whether the add line should be rendered in a separate group, or as part of the
     * table (first line)
     *
     * <p>
     * When separate add line is enabled, the fields for the add line will be placed in the
     * {@link #getAddLineGroup()}. This group can be used to configure the add line presentation. In
     * addition to the fields, the header on the group (unless already set) will be set to
     * {@link org.kuali.rice.krad.uif.container.CollectionGroup#getAddLabel()} and the add line
     * actions will be placed into the group's footer.
     * </p>
     *
     * @return true if add line should be separated, false if it should be placed into the table
     */
    boolean isSeparateAddLine();

    /**
     * Setter for the separate add line indicator
     *
     * @param separateAddLine
     */
    void setSeparateAddLine(boolean separateAddLine);

    /**
     * List of {@link Field} instances that make up all the table's rows of data
     *
     * @return table body fields
     */
    List<Field> getAllRowFields();

    /**
     * List of {@link Field} instances that make us the table's first row of data
     *
     * @return list of field instances
     */
    List<Field> getFirstRowFields();

    /**
     * Widget associated with the table to add functionality such as sorting, paging, and export
     *
     * @return RichTable instance
     */
    RichTable getRichTable();

    /**
     * Setter for the rich table widget
     *
     * @param richTable
     */
    void setRichTable(RichTable richTable);

    /**
     * @return the numberOfDataColumns
     */
    int getNumberOfDataColumns();

    /**
     * @param numberOfDataColumns the numberOfDataColumns to set
     */
    void setNumberOfDataColumns(int numberOfDataColumns);

    /**
     * Gets a set of property names defining hidden columns.
     * 
     * @return set of property names
     * @see org.kuali.rice.krad.uif.widget.RichTable#getHiddenColumns()
     */
    Set<String> getHiddenColumns();

    /**
     * Setter for {@link #getHiddenColumns()}.
     * 
     * @param hiddenColumns set of property names
     */
    void setHiddenColumns(Set<String> hiddenColumns);

    /**
     * Gets a set of property names defining sortable columns.
     * 
     * @return set of property names
     * @see org.kuali.rice.krad.uif.widget.RichTable#getSortableColumns()
     */
    Set<String> getSortableColumns();

    /**
     * Setterfor {@link #getSortableColumns()}.
     * 
     * @param sortableColumns set of property names
     */
    void setSortableColumns(Set<String> sortableColumns);

    /**
     * Indicates the index of the action column
     *
     * @return the action column index
     */
    int getActionColumnIndex();

    /**
     * Indicates the actions column placement
     *
     * <p>
     * Valid values are 'LEFT', 'RIGHT' or any valid number. The default is 'RIGHT' or '-1'. The
     * column placement index takes all displayed columns, including sequence and selection columns,
     * into account.
     * </p>
     *
     * @return the action column placement
     */
    String getActionColumnPlacement();

    /**
     * Setter for the action column placement
     *
     * @param actionColumnPlacement action column placement string
     */
    void setActionColumnPlacement(String actionColumnPlacement);

    /**
     * The row details info group to use when using a TableLayoutManager with the a richTable.
     *
     * <p>
     * This group will be displayed when the user clicks the "Details" link/image on a row. This
     * allows extra/long data to be hidden in table rows and then revealed during interaction with
     * the table without the need to leave the page. Allows for any group content.
     * </p>
     *
     * <p>
     * Does not currently work with javascript required content.
     * </p>
     *
     * @return rowDetailsGroup component
     */
    Group getRowDetailsGroup();

    /**
     * Set the row details info group
     *
     * @param rowDetailsGroup row details group
     */
    void setRowDetailsGroup(Group rowDetailsGroup);

    /**
     * A list of all the columns to be calculated
     *
     * <p>
     * The list must contain valid column indexes. The indexes takes all displayed columns into
     * account.
     * </p>
     *
     * @return the total columns list
     */
    List<String> getColumnsToCalculate();

    /**
     * Gets showTotal. showTotal shows/calculates the total field when true, otherwise it is not
     * rendered. <br/>
     * <b>Only used when renderOnlyLeftTotalLabels is TRUE, this overrides the
     * ColumnConfigurationInfo setting. Otherwise, the ColumnConfigurationInfo setting takes
     * precedence.</b>
     *
     * @return true if showing the total, false otherwise.
     */
    boolean isShowTotal();

    /**
     * Sets showTotal. showTotal shows/calculates the total field when true, otherwise it is not
     * rendered. <br/>
     * <b>Only used when renderOnlyLeftTotalLabels is TRUE, this overrides the
     * ColumnConfigurationInfo setting. Otherwise, the ColumnConfigurationInfo setting takes
     * precedence.</b>
     *
     * @param showTotal
     */
    void setShowTotal(boolean showTotal);

    /**
     * Gets showTotal. showTotal shows/calculates the total field when true, otherwise it is not
     * rendered. <br/>
     * <b>Only used when renderOnlyLeftTotalLabels is TRUE, this overrides the
     * ColumnConfigurationInfo setting. Otherwise, the ColumnConfigurationInfo setting takes
     * precedence.</b>
     *
     * @return true if showing the page total, false otherwise.
     */
    boolean isShowPageTotal();

    /**
     * Sets showPageTotal. showPageTotal shows/calculates the total field for the page when true
     * (and only when the table actually has pages), otherwise it is not rendered. <br/>
     * <b>Only used when renderOnlyLeftTotalLabels is TRUE, this overrides the
     * ColumnConfigurationInfo setting. Otherwise, the ColumnConfigurationInfo setting takes
     * precedence.</b>
     *
     * @param showPageTotal
     */
    void setShowPageTotal(boolean showPageTotal);

    /**
     * Gets showGroupTotal. showGroupTotal shows/calculates the total field for each grouping when
     * true (and only when the table actually has grouping turned on), otherwise it is not rendered. <br/>
     * <b>Only used when renderOnlyLeftTotalLabels is TRUE, this overrides the
     * ColumnConfigurationInfo setting. Otherwise, the ColumnConfigurationInfo setting takes
     * precedence.</b>
     *
     * @return true if showing the group total, false otherwise.
     */
    boolean isShowGroupTotal();

    /**
     * Sets showGroupTotal. showGroupTotal shows/calculates the total field for each grouping when
     * true (and only when the table actually has grouping turned on), otherwise it is not rendered. <br/>
     * <b>Only used when renderOnlyLeftTotalLabels is TRUE, this overrides the
     * ColumnConfigurationInfo setting. Otherwise, the ColumnConfigurationInfo setting takes
     * precedence.</b>
     *
     * @param showGroupTotal
     */
    void setShowGroupTotal(boolean showGroupTotal);

    /**
     * The total label to use when renderOnlyLeftTotalLabels is TRUE for total. This label will
     * appear in the left most column.
     *
     * @return the totalLabel
     */
    Label getTotalLabel();

    /**
     * Sets the total label to use when renderOnlyLeftTotalLabels is TRUE for total.
     *
     * @param totalLabel
     */
    void setTotalLabel(Label totalLabel);

    /**
     * The pageTotal label to use when renderOnlyLeftTotalLabels is TRUE for total. This label will
     * appear in the left most column.
     *
     * @return the totalLabel
     */
    Label getPageTotalLabel();

    /**
     * Sets the pageTotal label to use when renderOnlyLeftTotalLabels is TRUE for total.
     *
     * @param pageTotalLabel
     */
    void setPageTotalLabel(Label pageTotalLabel);

    /**
     * The groupTotal label to use when renderOnlyLeftTotalLabels is TRUE. This label will appear in
     * the left most column.
     *
     * @return the totalLabel
     */
    Label getGroupTotalLabelPrototype();

    /**
     * Sets the groupTotal label to use when renderOnlyLeftTotalLabels is TRUE.
     *
     * @param groupTotalLabelPrototype
     */
    void setGroupTotalLabelPrototype(Label groupTotalLabelPrototype);

    /**
     * Gets the column calculations. This is a list of ColumnCalcuationInfo that when set provides
     * calculations to be performed on the columns they specify. These calculations appear in the
     * table's footer. This feature is only available when using richTable functionality.
     *
     * @return the columnCalculations to use
     */
    List<ColumnCalculationInfo> getColumnCalculations();

    /**
     * Sets the columnCalculations.
     *
     * @param columnCalculations
     */
    void setColumnCalculations(List<ColumnCalculationInfo> columnCalculations);

    /**
     * When true, labels for the totals fields will only appear in the left most column. Showing of
     * the totals is controlled by the settings on the TableLayoutManager itself when this property
     * is true.
     *
     * @return true when rendering totals footer labels in the left-most column, false otherwise
     */
    boolean isRenderOnlyLeftTotalLabels();

    /**
     * Set the renderOnlyLeftTotalLabels flag for rendring total labels in the left-most column
     *
     * @param renderOnlyLeftTotalLabels
     */
    void setRenderOnlyLeftTotalLabels(boolean renderOnlyLeftTotalLabels);

    /**
     * Gets the footer calculation components to be used by the layout. These are set by the
     * framework and cannot be set directly.
     *
     * @return the list of components for the footer
     */
    List<Component> getFooterCalculationComponents();

    /**
     * Gets the list of property names to use for grouping.
     *
     * <p>
     * When this property is set, grouping for this collection will be enabled and the lines of the
     * collection will be grouped by the propertyName(s) supplied. Supplying multiple property names
     * will cause the grouping to be on multiple fields and ordered alphabetically on
     * "propetyValue1, propertyValue2" (this is also how the group title will display for each
     * group). The property names supplied must be relative to the line, so #lp SHOULD NOT be used
     * (it is assumed automatically).
     * </p>
     *
     * @return propertyNames to group on
     */
    List<String> getGroupingPropertyNames();

    /**
     * Sets the list of property names to use for grouping.
     *
     * @param groupingPropertyNames
     */
    void setGroupingPropertyNames(List<String> groupingPropertyNames);

    /**
     * Get the groupingTitle. The groupingTitle MUST contain a SpringEL expression to uniquely
     * identify a group's line (ie it cannot be a static string because each group must be
     * identified by some value). <b>This overrides groupingPropertyNames(if set) because it
     * provides full control of grouping value used by the collection. SpringEL defined here must
     * use #lp if referencing values of the line.</b>
     *
     * @return groupingTitle to be used
     */
    String getGroupingTitle();

    /**
     * Set the groupingTitle. This will throw an exception if the title does not contain a SpringEL
     * expression.
     *
     * @param groupingTitle
     */
    void setGroupingTitle(String groupingTitle);

    /**
     * Get the groupingPrefix. The groupingPrefix is used to prefix the generated title (not used
     * when groupingTitle is set directly) when using groupingPropertyNames.
     *
     * @return String
     */
    String getGroupingPrefix();

    /**
     * Set the groupingPrefix. This is not used when groupingTitle is set directly.
     *
     * @param groupingPrefix
     */
    void setGroupingPrefix(String groupingPrefix);

    /**
     * If true, all details will be opened by default when the table loads. Can only be used on
     * tables that have row details setup.
     *
     * @return true if row details
     */
    boolean isRowDetailsOpen();

    /**
     * Set if row details should be open on table load
     *
     * @param rowDetailsOpen
     */
    void setRowDetailsOpen(boolean rowDetailsOpen);

    /**
     * If true, the toggleAllDetailsAction will be shown. This button allows all details to be
     * open/closed simultaneously.
     *
     * @return true if the action button to toggle all row details opened/closed
     */
    boolean isShowToggleAllDetails();

    /**
     * Set if the toggleAllDetailsAction should be shown
     *
     * @param showToggleAllDetails
     */
    void setShowToggleAllDetails(boolean showToggleAllDetails);

    /**
     * The toggleAllDetailsAction action component used to toggle all row details open/closed. This
     * property is set by the default configuration and should not be reset in most cases.
     *
     * @return Action component to use for the toggle action button
     */
    Action getToggleAllDetailsAction();

    /**
     * Set the toggleAllDetailsAction action component used to toggle all row details open/closed.
     * This property is set by the default configuration and should not be reset in most cases.
     *
     * @param toggleAllDetailsAction
     */
    void setToggleAllDetailsAction(Action toggleAllDetailsAction);

    /**
     * If true, when a row details open action is performed, it will get the details content from
     * the server the first time it is opened. The methodToCall will be a component "refresh" call
     * by default (this can be set on expandDetailsActionPrototype) and the additional action
     * parameters sent to the server will be those set on the expandDetailsActionPrototype
     * (lineIndex will be sent by default).
     *
     * @return true if ajax row details retrieval will be used
     */
    boolean isAjaxDetailsRetrieval();

    /**
     * Set if row details content should be retrieved fromt he server
     *
     * @param ajaxDetailsRetrieval
     */
    void setAjaxDetailsRetrieval(boolean ajaxDetailsRetrieval);

    /**
     * The Action prototype used for the row details expand link. Should be set to
     * "Uif-ExpandDetailsAction" or "Uif-ExpandDetailsImageAction". Properties can be configured to
     * allow for different methodToCall and actionParameters to be set for ajax row details
     * retrieval.
     *
     * @return the Action details link prototype
     */
    Action getExpandDetailsActionPrototype();

    /**
     * Gets the grouping column index
     *
     * @return the grouping column index
     */
    int getGroupingColumnIndex();

    /**
     * Set the expand details Action prototype link
     *
     * @param expandDetailsActionPrototype
     */
    void setExpandDetailsActionPrototype(Action expandDetailsActionPrototype);

    /**
     * The row css classes for the rows of this layout
     *
     * <p>
     * To set a css class on all rows, use "all" as a key. To set a class for even rows, use "even"
     * as a key, for odd rows, use "odd". Use a one-based index to target a specific row by index.
     * SpringEL can be used as a key and the expression will be evaluated; if evaluated to true, the
     * class(es) specified will be applied.
     * </p>
     *
     * @return a map which represents the css classes of the rows of this layout
     */
    Map<String, String> getConditionalRowCssClasses();

    /**
     * Set the conditionalRowCssClasses
     *
     * @param conditionalRowCssClasses
     */
    void setConditionalRowCssClasses(Map<String, String> conditionalRowCssClasses);

    /**
     * Gets a list of column calculation components.
     * 
     * @return list of column calculation components
     */
   List<Component> getColumnCalculationComponents();

}
