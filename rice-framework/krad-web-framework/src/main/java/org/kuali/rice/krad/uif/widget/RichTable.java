/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.widget;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.control.CheckboxGroupControl;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.SelectControl;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * Decorates a HTML Table client side with various tools
 *
 * <p>
 * Decorations implemented depend on widget implementation. Examples are
 * sorting, paging and skinning.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "richTable-bean", parent = "Uif-RichTable"),
        @BeanTag(name = "pagedRichTable-bean", parent = "Uif-PagedRichTable"),
        @BeanTag(name = "scrollableRichTable-bean", parent = "Uif-ScrollableRichTable")})
public class RichTable extends WidgetBase {
    private static final long serialVersionUID = 4671589690877390070L;

    private String emptyTableMessage;
    private boolean disableTableSort;

    private boolean forceAoColumnDefsOverride;

    private Set<String> hiddenColumns;
    private Set<String> sortableColumns;

    private String ajaxSource;

    private boolean showSearchAndExportOptions = true;

    private String groupingOptionsJSString;

    public RichTable() {
        super();
        groupingOptionsJSString = "null";
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Initializes component options for empty table message</li>
     * </ul>
     */
    @Override
    public void performFinalize(View view, Object model, Component component) {
        super.performFinalize(view, model, component);

        UifFormBase formBase = (UifFormBase) model;

        if (isRender()) {
            if (StringUtils.isNotBlank(getEmptyTableMessage()) && !getTemplateOptions().containsKey(
                    UifConstants.TableToolsKeys.LANGUAGE)) {
                getTemplateOptions().put(UifConstants.TableToolsKeys.LANGUAGE,
                        "{\"" + UifConstants.TableToolsKeys.EMPTY_TABLE + "\" : \"" + getEmptyTableMessage() + "\"}");
            }

            if (!isShowSearchAndExportOptions()) {
                Object domOption = getTemplateOptions().get(UifConstants.TableToolsKeys.SDOM);
                if (domOption instanceof String) {
                    String sDomOption = (String) domOption;

                    if (StringUtils.isNotBlank(sDomOption)) {
                        sDomOption = StringUtils.remove(sDomOption, "T"); //Removes Export option
                        sDomOption = StringUtils.remove(sDomOption, "f"); //Removes search option
                        getTemplateOptions().put(UifConstants.TableToolsKeys.SDOM, sDomOption);
                    }
                }
            }

            // for add events, disable initial sorting
            if (UifConstants.ActionEvents.ADD_LINE.equals(formBase.getActionEvent()) || UifConstants.ActionEvents
                    .ADD_BLANK_LINE.equals(formBase.getActionEvent())) {
                getTemplateOptions().put(UifConstants.TableToolsKeys.AASORTING, "[]");
            }

            if ((component instanceof CollectionGroup)) {
                buildTableOptions((CollectionGroup) component);
                setTotalOptions((CollectionGroup) component);
            }

            if (isDisableTableSort()) {
                getTemplateOptions().put(UifConstants.TableToolsKeys.TABLE_SORT, "false");
            }

            if (StringUtils.isNotBlank(ajaxSource)) {
                getTemplateOptions().put(UifConstants.TableToolsKeys.SAJAX_SOURCE, ajaxSource);
            }
        }
    }

    /**
     * Builds the footer callback template option for column totals
     *
     * @param collectionGroup the collection group
     */
    private void setTotalOptions(CollectionGroup collectionGroup) {
        LayoutManager layoutManager = collectionGroup.getLayoutManager();

        if (layoutManager instanceof TableLayoutManager) {
            List<String> totalColumns = ((TableLayoutManager) layoutManager).getColumnsToCalculate();

            if (totalColumns.size() > 0) {
                String array = "[";

                for (String i : totalColumns) {
                    array = array + i + ",";
                }
                array = StringUtils.removeEnd(array, ",");
                array = array + "]";
                getTemplateOptions().put(UifConstants.TableToolsKeys.FOOTER_CALLBACK,
                        "function (nRow, aaData, iStart, iEnd, aiDisplay) {initializeTotalsFooter (nRow, aaData, iStart, iEnd, aiDisplay, "
                                + array
                                + " )}");
            }
        }
    }

    /**
     * Builds column options for sorting
     *
     * @param collectionGroup
     */
    protected void buildTableOptions(CollectionGroup collectionGroup) {
        LayoutManager layoutManager = collectionGroup.getLayoutManager();

        // if sub collection exists, don't allow the table sortable
        if (!collectionGroup.getSubCollections().isEmpty()) {
            setDisableTableSort(true);
        }

        if (!isDisableTableSort()) {
            // if rendering add line, skip that row from col sorting
            if (collectionGroup.isRenderAddLine()
                    && !collectionGroup.isReadOnly()
                    && !((layoutManager instanceof TableLayoutManager) && ((TableLayoutManager) layoutManager)
                    .isSeparateAddLine())) {
                getTemplateOptions().put(UifConstants.TableToolsKeys.SORT_SKIP_ROWS,
                        "[" + UifConstants.TableToolsValues.ADD_ROW_DEFAULT_INDEX + "]");
            }

            StringBuffer tableToolsColumnOptions = new StringBuffer("[");

            int columnIndex = 0;
            int actionIndex = -1;
            boolean actionFieldVisible = collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly();

            if (layoutManager instanceof TableLayoutManager) {
                actionIndex = ((TableLayoutManager) layoutManager).getActionColumnIndex();
            }

            if (actionIndex == 1 && actionFieldVisible) {
                String actionColOptions = constructTableColumnOptions(columnIndex, false, null, null);
                tableToolsColumnOptions.append(actionColOptions + " , ");
                columnIndex++;
            }

            if (layoutManager instanceof TableLayoutManager && ((TableLayoutManager) layoutManager)
                    .isRenderSequenceField()) {
                tableToolsColumnOptions.append("{\""
                        + UifConstants.TableToolsKeys.SORT_TYPE
                        + "\" : \""
                        + UifConstants.TableToolsValues.NUMERIC
                        + "\", "
                        + "\""
                        + UifConstants.TableToolsKeys.TARGETS
                        + "\": ["
                        + columnIndex
                        + "]}, ");
                columnIndex++;
                if (actionIndex == 2 && actionFieldVisible) {
                    String actionColOptions = constructTableColumnOptions(columnIndex, false, null, null);
                    tableToolsColumnOptions.append(actionColOptions + " , ");
                    columnIndex++;
                }
            }

            // skip select field if enabled
            if (collectionGroup.isIncludeLineSelectionField()) {
                String colOptions = constructTableColumnOptions(columnIndex, false, null, null);
                tableToolsColumnOptions.append(colOptions + " , ");
                columnIndex++;
            }

            // if data dictionary defines aoColumns, copy here and skip default sorting/visibility behaviour
            if (!StringUtils.isEmpty(getTemplateOptions().get(UifConstants.TableToolsKeys.AO_COLUMNS))) {
                // get the contents of the JS array string
                String jsArray = getTemplateOptions().get(UifConstants.TableToolsKeys.AO_COLUMNS);
                int startBrace = StringUtils.indexOf(jsArray, "[");
                int endBrace = StringUtils.lastIndexOf(jsArray, "]");
                tableToolsColumnOptions.append(StringUtils.substring(jsArray, startBrace + 1, endBrace) + ", ");

                if (actionFieldVisible && (actionIndex == -1 || actionIndex >= columnIndex)) {
                    String actionColOptions = constructTableColumnOptions(actionIndex, false, null, null);
                    tableToolsColumnOptions.append(actionColOptions);
                } else {
                    tableToolsColumnOptions = new StringBuffer(StringUtils.removeEnd(tableToolsColumnOptions.toString(),
                            ", "));
                }

                tableToolsColumnOptions.append("]");
                getTemplateOptions().put(UifConstants.TableToolsKeys.AO_COLUMNS, tableToolsColumnOptions.toString());
            } else if (!StringUtils.isEmpty(getTemplateOptions().get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS))
                    && forceAoColumnDefsOverride) {
                String jsArray = getTemplateOptions().get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS);
                int startBrace = StringUtils.indexOf(jsArray, "[");
                int endBrace = StringUtils.lastIndexOf(jsArray, "]");
                tableToolsColumnOptions.append(StringUtils.substring(jsArray, startBrace + 1, endBrace) + ", ");

                if (actionFieldVisible && (actionIndex == -1 || actionIndex >= columnIndex)) {
                    String actionColOptions = constructTableColumnOptions(actionIndex, false, null, null);
                    tableToolsColumnOptions.append(actionColOptions);
                } else {
                    tableToolsColumnOptions = new StringBuffer(StringUtils.removeEnd(tableToolsColumnOptions.toString(),
                            ", "));
                }

                tableToolsColumnOptions.append("]");
                getTemplateOptions().put(UifConstants.TableToolsKeys.AO_COLUMN_DEFS,
                        tableToolsColumnOptions.toString());
            } else if (layoutManager instanceof TableLayoutManager) {
                // build column defs from the first row of the table
                // TODO: does this handle multiple rows correctly?
                for (Component component : ((TableLayoutManager) layoutManager).getFirstRowFields()) {
                    if (actionFieldVisible && columnIndex + 1 == actionIndex) {
                        String actionColOptions = constructTableColumnOptions(columnIndex, false, null, null);
                        tableToolsColumnOptions.append(actionColOptions + " , ");
                        columnIndex++;
                    }

                    // for FieldGroup, get the first field from that group
                    if (component instanceof FieldGroup) {
                        component = ((FieldGroup) component).getItems().get(0);
                    }

                    if (component instanceof DataField) {
                        DataField field = (DataField) component;

                        // if a field is marked as invisible in hiddenColumns, append options and skip sorting
                        if (getHiddenColumns() != null && getHiddenColumns().contains(field.getPropertyName())) {
                            tableToolsColumnOptions.append("{"
                                    + UifConstants.TableToolsKeys.VISIBLE
                                    + ": "
                                    + UifConstants.TableToolsValues.FALSE
                                    + ", \""
                                    + UifConstants.TableToolsKeys.TARGETS
                                    + "\": ["
                                    + columnIndex
                                    + "]"
                                    + "}, ");
                            // if sortableColumns is present and a field is marked as sortable or unspecified
                        } else if (getSortableColumns() != null && !getSortableColumns().isEmpty()) {
                            if (getSortableColumns().contains(field.getPropertyName())) {
                                tableToolsColumnOptions.append(getDataFieldColumnOptions(columnIndex, collectionGroup,
                                        field) + ", ");
                            } else {
                                tableToolsColumnOptions.append("{'"
                                        + UifConstants.TableToolsKeys.SORTABLE
                                        + "': "
                                        + UifConstants.TableToolsValues.FALSE
                                        + ", \""
                                        + UifConstants.TableToolsKeys.TARGETS
                                        + "\": ["
                                        + columnIndex
                                        + "]"
                                        + "}, ");
                            }
                        } else {// sortable columns not defined
                            String colOptions = getDataFieldColumnOptions(columnIndex, collectionGroup, field);
                            tableToolsColumnOptions.append(colOptions + " , ");
                        }
                        columnIndex++;
                    } else if (component instanceof MessageField
                            && component.getDataAttributes().get("role") != null
                            && component.getDataAttributes().get("role").equals("grouping")) {
                        //Grouping column is never shown, so skip
                        tableToolsColumnOptions.append("{"
                                + UifConstants.TableToolsKeys.VISIBLE
                                + ": "
                                + UifConstants.TableToolsValues.FALSE
                                + ", \""
                                + UifConstants.TableToolsKeys.TARGETS
                                + "\": ["
                                + columnIndex
                                + "]"
                                + "}, ");
                        columnIndex++;
                    } else {
                        String colOptions = constructTableColumnOptions(columnIndex, false, null, null);
                        tableToolsColumnOptions.append(colOptions + " , ");
                        columnIndex++;
                    }
                }

                if (actionFieldVisible && (actionIndex == -1 || actionIndex >= columnIndex)) {
                    String actionColOptions = constructTableColumnOptions(actionIndex, false, null, null);
                    tableToolsColumnOptions.append(actionColOptions);
                } else {
                    tableToolsColumnOptions = new StringBuffer(StringUtils.removeEnd(tableToolsColumnOptions.toString(),
                            ", "));
                }

                //merge the aoColumnDefs passed in
                if (!StringUtils.isEmpty(getTemplateOptions().get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS))) {
                    String origAoOptions = getTemplateOptions().get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS).trim();
                    origAoOptions = StringUtils.removeStart(origAoOptions, "[");
                    origAoOptions = StringUtils.removeEnd(origAoOptions, "]");
                    tableToolsColumnOptions.append("," + origAoOptions);
                }

                tableToolsColumnOptions.append("]");
                getTemplateOptions().put(UifConstants.TableToolsKeys.AO_COLUMN_DEFS,
                        tableToolsColumnOptions.toString());
            }
        }
    }

    /**
     * Construct the column options for a data field
     *
     * @param collectionGroup the collectionGroup in which the data field is defined
     * @param field the field to construction options for
     * @return options as valid for datatable
     */
    protected String getDataFieldColumnOptions(int target, CollectionGroup collectionGroup, DataField field) {
        String sortType = null;

        if (!collectionGroup.isReadOnly()
                && (field instanceof InputField)
                && ((InputField) field).getControl() != null) {
            Control control = ((InputField) field).getControl();
            if (control instanceof SelectControl) {
                sortType = UifConstants.TableToolsValues.DOM_SELECT;
            } else if (control instanceof CheckboxControl || control instanceof CheckboxGroupControl) {
                sortType = UifConstants.TableToolsValues.DOM_CHECK;
            } else if (control instanceof RadioGroupControl) {
                sortType = UifConstants.TableToolsValues.DOM_RADIO;
            } else {
                sortType = UifConstants.TableToolsValues.DOM_TEXT;
            }
        } else {
            sortType = UifConstants.TableToolsValues.DOM_TEXT;
        }

        Class dataTypeClass = ObjectPropertyUtils.getPropertyType(collectionGroup.getCollectionObjectClass(),
                field.getPropertyName());

        return constructTableColumnOptions(target, true, dataTypeClass, sortType);
    }

    /**
     * Constructs the sort data type for each data table columns in a format that will be used to initialize the data
     * table widget via javascript
     *
     * @param isSortable whether a column should be marked as sortable
     * @param dataTypeClass the class type of the column value - used determine the sType option - which identifies
     * the search plugin to use
     * @param sortDataType Defines a data source type for the sorting which can be used to read realtime information
     * from the table
     * @return a formatted string with data table options for one column
     */
    protected String constructTableColumnOptions(int target, boolean isSortable, Class dataTypeClass,
            String sortDataType) {
        String colOptions = "null";

        String sortType = "";
        if (!isSortable || dataTypeClass == null || sortType == null) {
            colOptions = "\"" + UifConstants.TableToolsKeys.SORTABLE + "\" : false, \"sType\" : \"string\"";
        } else {
            if (ClassUtils.isAssignable(dataTypeClass, KualiPercent.class)) {
                sortType = UifConstants.TableToolsValues.PERCENT;
            } else if (ClassUtils.isAssignable(dataTypeClass, KualiInteger.class) || ClassUtils.isAssignable(
                    dataTypeClass, KualiDecimal.class)) {
                sortType = UifConstants.TableToolsValues.CURRENCY;
            } else if (ClassUtils.isAssignable(dataTypeClass, Timestamp.class)) {
                sortType = "date";
            } else if (ClassUtils.isAssignable(dataTypeClass, java.sql.Date.class) || ClassUtils.isAssignable(
                    dataTypeClass, java.util.Date.class)) {
                sortType = UifConstants.TableToolsValues.DATE;
            } else if (ClassUtils.isAssignable(dataTypeClass, Number.class)) {
                sortType = UifConstants.TableToolsValues.NUMERIC;
            } else {
                sortType = UifConstants.TableToolsValues.STRING;
            }

            colOptions = "\"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + sortDataType + "\"";
            colOptions += " , \"" + UifConstants.TableToolsKeys.SORT_TYPE + "\" : \"" + sortType + "\"";
        }

        if (!colOptions.equals("null")) {
            colOptions = "{" + colOptions + ", \"" + UifConstants.TableToolsKeys.TARGETS + "\": [" + target + "]}";
        } else {
            colOptions = "{" + colOptions + "}";
        }

        return colOptions;
    }

    /**
     * Returns the text which is used to display text when the table is empty
     *
     * @return empty table message
     */
    @BeanTagAttribute(name = "emptyTableMessage")
    public String getEmptyTableMessage() {
        return emptyTableMessage;
    }

    /**
     * Setter for a text to be displayed when the table is empty
     *
     * @param emptyTableMessage
     */
    public void setEmptyTableMessage(String emptyTableMessage) {
        this.emptyTableMessage = emptyTableMessage;
    }

    /**
     * Returns true if sorting is disabled
     *
     * @return the disableTableSort
     */
    @BeanTagAttribute(name = "disableTableSort")
    public boolean isDisableTableSort() {
        return this.disableTableSort;
    }

    /**
     * Enables/disables the table sorting
     *
     * @param disableTableSort the disableTableSort to set
     */
    public void setDisableTableSort(boolean disableTableSort) {
        this.disableTableSort = disableTableSort;
    }

    /**
     * Returns true if search and export options are enabled
     *
     * @return the showSearchAndExportOptions
     */
    @BeanTagAttribute(name = "showSearchAndExportOptions")
    public boolean isShowSearchAndExportOptions() {
        return this.showSearchAndExportOptions;
    }

    /**
     * Show/Hide the search and export options in tabletools
     *
     * @param showSearchAndExportOptions the showSearchAndExportOptions to set
     */
    public void setShowSearchAndExportOptions(boolean showSearchAndExportOptions) {
        this.showSearchAndExportOptions = showSearchAndExportOptions;
    }

    /**
     * Holds propertyNames for the ones meant to be hidden since columns are visible by default
     *
     * <p>Duplicate entries are ignored and the order of entries is not significant</p>
     *
     * @return a set with propertyNames of columns to be hidden
     */
    @BeanTagAttribute(name = "hiddenColumns", type = BeanTagAttribute.AttributeType.SETVALUE)
    public Set<String> getHiddenColumns() {
        return hiddenColumns;
    }

    /**
     * Setter for the hidden columns set
     *
     * @param hiddenColumns a set containing propertyNames
     */
    public void setHiddenColumns(Set<String> hiddenColumns) {
        this.hiddenColumns = hiddenColumns;
    }

    /**
     * Holds the propertyNames for columns that are to be sorted
     *
     * <p>Duplicate entries are ignored and the order of entries is not significant</p>
     *
     * @return a set of propertyNames with for columns that will be sorted
     */
    @BeanTagAttribute(name = "sortableColumns", type = BeanTagAttribute.AttributeType.SETVALUE)
    public Set<String> getSortableColumns() {
        return sortableColumns;
    }

    /**
     * Setter for sortable columns
     *
     * @param sortableColumns a set containing propertyNames of columns to be sorted
     */
    public void setSortableColumns(Set<String> sortableColumns) {
        this.sortableColumns = sortableColumns;
    }

    /**
     * Specifies a URL for acquiring the table data with ajax
     *
     * <p>
     * When the ajax source URL is specified the rich table plugin will retrieve the data by invoking the URL and
     * building the table rows from the result. This is different from the standard use of the rich table plugin
     * with uses progressive enhancement to decorate a table that has already been rendereed
     * </p>
     *
     * @return URL for ajax source
     */
    @BeanTagAttribute(name = "ajaxSource")
    public String getAjaxSource() {
        return ajaxSource;
    }

    /**
     * Setter for the Ajax source URL
     *
     * @param ajaxSource
     */
    public void setAjaxSource(String ajaxSource) {
        this.ajaxSource = ajaxSource;
    }

    /**
     * Get groupingOption
     *
     * @return
     */
    public String getGroupingOptionsJSString() {
        return groupingOptionsJSString;
    }

    /**
     * Set the groupingOptions js data.  <b>This should not be set through XML configuration.</b>
     *
     * @param groupingOptionsJSString
     */
    public void setGroupingOptionsJSString(String groupingOptionsJSString) {
        this.groupingOptionsJSString = groupingOptionsJSString;
    }

    public boolean isForceAoColumnDefsOverride() {
        return forceAoColumnDefsOverride;
    }

    public void setForceAoColumnDefsOverride(boolean forceAoColumnDefsOverride) {
        this.forceAoColumnDefsOverride = forceAoColumnDefsOverride;
    }
}
