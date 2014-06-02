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
package org.kuali.rice.krad.uif.widget;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.parse.BeanTags;
import org.kuali.rice.krad.lookup.LookupView;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.ComponentBase;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.control.CheckboxGroupControl;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.SelectControl;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Decorates a HTML Table client side with various tools
 *
 * <p>
 * Decorations implemented depend on widget implementation. Examples are sorting, paging and
 * skinning.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTags({@BeanTag(name = "richTable", parent = "Uif-RichTable"),
        @BeanTag(name = "pagedRichTable", parent = "Uif-PagedRichTable"),
        @BeanTag(name = "scrollableRichTable", parent = "Uif-ScrollableRichTable")})
public class RichTable extends WidgetBase {
    private static final long serialVersionUID = 4671589690877390070L;

    private String emptyTableMessage;
    private boolean disableTableSort;

    private boolean forceAoColumnDefsOverride;

    private boolean forceLocalJsonData;
    private int nestedLevel;
    private String aaData;

    private Set<String> hiddenColumns;
    private Set<String> sortableColumns;
    private List<String> cellCssClasses;

    private String ajaxSource;

    private boolean showExportOption;

    private String groupingOptionsJSString;

    public RichTable() {
        super();
        groupingOptionsJSString = "null";
        cellCssClasses = new ArrayList<String>();
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Initializes component options for empty table message</li>
     * </ul>
     */
    @Override
    public void performFinalize(Object model, LifecycleElement parent) {
        super.performFinalize(model, parent);

        UifFormBase formBase = (UifFormBase) model;

        if (!isRender()) {
            return;
        }

        if (templateOptions.isEmpty()) {
            setTemplateOptions(new HashMap<String, String>());
        }

        if (StringUtils.isNotBlank(getEmptyTableMessage()) && !templateOptions.containsKey(
                UifConstants.TableToolsKeys.LANGUAGE)) {
            templateOptions.put(UifConstants.TableToolsKeys.LANGUAGE,
                    "{\"" + UifConstants.TableToolsKeys.EMPTY_TABLE + "\" : \"" + getEmptyTableMessage() + "\"}");
        }

        Object domOption = templateOptions.get(UifConstants.TableToolsKeys.SDOM);
        if (domOption instanceof String) {
            String sDomOption = (String) domOption;

            if (StringUtils.isNotBlank(sDomOption)) {
                if (!isShowExportOption()) {
                    sDomOption = StringUtils.remove(sDomOption, "T"); //Removes Export option
                }
                templateOptions.put(UifConstants.TableToolsKeys.SDOM, sDomOption);
            }
        }

        // for add events, disable initial sorting
        if (UifConstants.ActionEvents.ADD_LINE.equals(formBase.getActionEvent()) || UifConstants.ActionEvents
                .ADD_BLANK_LINE.equals(formBase.getActionEvent())) {
            templateOptions.put(UifConstants.TableToolsKeys.AASORTING, "[]");
        }

        if ((parent instanceof CollectionGroup)) {
            CollectionGroup collectionGroup = (CollectionGroup) parent;
            LayoutManager layoutManager = collectionGroup.getLayoutManager();

            //if useServerPaging is true, add the css cell styling to the template options so it can still be used
            //since this will not go through the grid ftl
            if (layoutManager instanceof TableLayoutManager && collectionGroup.isUseServerPaging()) {
                addCellStyling((TableLayoutManager) layoutManager);
            }

            buildTableOptions(collectionGroup);
            setTotalOptions(collectionGroup);

            View view = ViewLifecycle.getActiveLifecycle().getView();
            if (view instanceof LookupView) {
                buildSortOptions((LookupView) view, collectionGroup);
            }
        }

        if (isDisableTableSort()) {
            templateOptions.put(UifConstants.TableToolsKeys.TABLE_SORT, "false");
        }

        String kradUrl = getConfigurationService().getPropertyValueAsString(UifConstants.ConfigProperties.KRAD_URL);
        if (StringUtils.isNotBlank(ajaxSource)) {
            templateOptions.put(UifConstants.TableToolsKeys.SAJAX_SOURCE, ajaxSource);
        } else if (parent instanceof CollectionGroup && ((CollectionGroup) parent).isUseServerPaging()) {
            // enable required dataTables options for server side paging
            templateOptions.put(UifConstants.TableToolsKeys.BPROCESSING, "true");
            templateOptions.put(UifConstants.TableToolsKeys.BSERVER_SIDE, "true");

            // build sAjaxSource url to call
            templateOptions.put(UifConstants.TableToolsKeys.SAJAX_SOURCE,
                    kradUrl + ((UifFormBase) model).getControllerMapping() + "?" +
                            UifConstants.CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME + "=" +
                            UifConstants.MethodToCallNames.TABLE_JSON + "&" + UifParameters.UPDATE_COMPONENT_ID + "=" +
                            parent.getId() + "&" + UifParameters.FORM_KEY + "=" + ((UifFormBase) model).getFormKey() +
                            "&" + UifParameters.AJAX_RETURN_TYPE + "=" +
                            UifConstants.AjaxReturnTypes.UPDATECOMPONENT.getKey() + "&" + UifParameters.AJAX_REQUEST +
                            "=" + "true");

            //TODO: Figure out where to move this script file constant?
            String pushLookupSelect = "function (aoData) { "
                    +
                    "if(jQuery('table.dataTable').length > 0) {    "
                    +
                    "    var table = jQuery('table.dataTable');    "
                    +
                    "    jQuery( table.find(':input:checked')).each( function (index) {     "
                    +
                    "        aoData.push({'name': (jQuery(this)).attr('name'),'value': (jQuery(this)).attr('value')});  "
                    +
                    "    console.log(jQuery(this).attr('name') + ':' + jQuery(this).attr('value')); "
                    +
                    "    });  "
                    +
                    "}  "
                    +
                    "}";

            templateOptions.put(UifConstants.TableToolsKeys.SERVER_PARAMS, pushLookupSelect);

            // store col defs so columns can be built on paging request
            ViewLifecycle.getViewPostMetadata().addComponentPostData(parent.getId(),
                    UifConstants.TableToolsKeys.AO_COLUMN_DEFS, templateOptions.get(
                    UifConstants.TableToolsKeys.AO_COLUMN_DEFS));
        }

        // build export url to call
        templateOptions.put(UifConstants.TableToolsKeys.SDOWNLOAD_SOURCE,
                kradUrl + "/" + UifConstants.ControllerMappings.EXPORT + "?" + UifParameters.UPDATE_COMPONENT_ID + "=" +
                        parent.getId() + "&" + UifParameters.FORM_KEY + "=" + ((UifFormBase) model).getFormKey() + "&" +
                        UifParameters.AJAX_RETURN_TYPE + "=" + UifConstants.AjaxReturnTypes.UPDATECOMPONENT.getKey() +
                        "&" + UifParameters.AJAX_REQUEST + "=" + "true");
    }

    /**
     * Add the css style to the cellCssClasses by column index, later used by the aoColumnDefs
     *
     * @param manager the tableLayoutManager that contains the original fields
     */
    private void addCellStyling(TableLayoutManager manager) {
        if (!CollectionUtils.isEmpty(manager.getAllRowFields())) {
            for (int index = 0; index < manager.getNumberOfColumns(); index++) {
                String cellStyleClasses = ((ComponentBase) manager.getAllRowFields().get(index))
                        .getWrapperCssClassesAsString();
                if (StringUtils.isNotBlank(cellStyleClasses)) {
                    cellCssClasses.add(cellStyleClasses);
                }
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

                templateOptions.put(UifConstants.TableToolsKeys.FOOTER_CALLBACK,
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
        checkMutable(false);

        LayoutManager layoutManager = collectionGroup.getLayoutManager();
        final boolean useServerPaging = collectionGroup.isUseServerPaging();

        if (templateOptions.isEmpty()) {
            setTemplateOptions(new HashMap<String, String>());
        }

        // if sub collection exists, don't allow the table sortable
        if (!collectionGroup.getSubCollections().isEmpty()) {
            setDisableTableSort(true);
        }

        if (!isDisableTableSort()) {
            // if rendering add line, skip that row from col sorting
            if (collectionGroup.isRenderAddLine()
                    && !Boolean.TRUE.equals(collectionGroup.getReadOnly())
                    && !((layoutManager instanceof TableLayoutManager) && ((TableLayoutManager) layoutManager)
                    .isSeparateAddLine())) {

                templateOptions.put(UifConstants.TableToolsKeys.SORT_SKIP_ROWS,
                        "[" + UifConstants.TableToolsValues.ADD_ROW_DEFAULT_INDEX + "]");
            }

            StringBuilder tableColumnOptions = new StringBuilder("[");

            int colIndex = 0;
            int actionIndex = UifConstants.TableLayoutValues.ACTIONS_COLUMN_RIGHT_INDEX;
            boolean actionFieldVisible = collectionGroup.isRenderLineActions() && !Boolean.TRUE.equals(
                    collectionGroup.getReadOnly());

            if (layoutManager instanceof TableLayoutManager) {
                actionIndex = ((TableLayoutManager) layoutManager).getActionColumnIndex();
            }

            if (actionIndex == UifConstants.TableLayoutValues.ACTIONS_COLUMN_LEFT_INDEX && actionFieldVisible) {
                String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                tableColumnOptions.append(options + ",");
                colIndex++;
            }

            // handle sequence field
            if (layoutManager instanceof TableLayoutManager && ((TableLayoutManager) layoutManager)
                    .isRenderSequenceField()) {
                Class<?> dataTypeClass = Number.class;

                if (((TableLayoutManager) layoutManager).getSequenceFieldPrototype() instanceof DataField) {
                    DataField dataField = (DataField) ((TableLayoutManager) layoutManager).getSequenceFieldPrototype();
                    dataTypeClass = ObjectPropertyUtils.getPropertyType(collectionGroup.getCollectionObjectClass(),
                            dataField.getPropertyName());
                    // check to see if field has custom sort type
                    if (dataField.getSortAs() != null && dataField.getSortAs().length() > 0) {
                        if (dataField.getSortAs().equals(UifConstants.TableToolsValues.DATE)) {
                            dataTypeClass = java.sql.Date.class;
                        } else if (dataField.getSortAs().equals(UifConstants.TableToolsValues.NUMERIC)) {
                            dataTypeClass = Number.class;
                        } else if (dataField.getSortAs().equals(UifConstants.TableToolsValues.STRING)) {
                            dataTypeClass = String.class;
                        }
                    }
                }

                // don't allow sorting of sequence field - why?
                // auto sequence column is never sortable
                tableColumnOptions.append("{"
                        + sortable(false)
                        + ","
                        + sortType(getSortType(dataTypeClass))
                        + ","
                        + sortDataType(UifConstants.TableToolsValues.DOM_TEXT)
                        + mData(useServerPaging, colIndex)
                        + ","
                        + targets(colIndex)
                        + "},");
                colIndex++;

                if (actionIndex == 2 && actionFieldVisible) {
                    String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                    tableColumnOptions.append(options + ",");
                    colIndex++;
                }
            }

            // skip select field if enabled
            if (collectionGroup.isIncludeLineSelectionField()) {
                String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                tableColumnOptions.append(options + ",");
                colIndex++;
            }

            // if data dictionary defines aoColumns, copy here and skip default sorting/visibility behaviour
            if (!StringUtils.isEmpty(templateOptions.get(UifConstants.TableToolsKeys.AO_COLUMNS))) {
                // get the contents of the JS array string
                String jsArray = templateOptions.get(UifConstants.TableToolsKeys.AO_COLUMNS);
                int startBrace = StringUtils.indexOf(jsArray, "[");
                int endBrace = StringUtils.lastIndexOf(jsArray, "]");
                tableColumnOptions.append(StringUtils.substring(jsArray, startBrace + 1, endBrace) + ",");

                if (actionFieldVisible && (actionIndex == -1 || actionIndex >= colIndex)) {
                    String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                    tableColumnOptions.append(options);
                } else {
                    tableColumnOptions = new StringBuilder(StringUtils.removeEnd(tableColumnOptions.toString(), ","));
                }

                tableColumnOptions.append("]");
                templateOptions.put(UifConstants.TableToolsKeys.AO_COLUMNS, tableColumnOptions.toString());
            } else if (!StringUtils.isEmpty(templateOptions.get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS))
                    && forceAoColumnDefsOverride) {
                String jsArray = templateOptions.get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS);
                int startBrace = StringUtils.indexOf(jsArray, "[");
                int endBrace = StringUtils.lastIndexOf(jsArray, "]");
                tableColumnOptions.append(StringUtils.substring(jsArray, startBrace + 1, endBrace) + ",");

                if (actionFieldVisible && (actionIndex == -1 || actionIndex >= colIndex)) {
                    String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                    tableColumnOptions.append(options);
                } else {
                    tableColumnOptions = new StringBuilder(StringUtils.removeEnd(tableColumnOptions.toString(), ","));
                }

                tableColumnOptions.append("]");
                templateOptions.put(UifConstants.TableToolsKeys.AO_COLUMN_DEFS, tableColumnOptions.toString());
            } else if (layoutManager instanceof TableLayoutManager) {
                List<Field> rowFields = ((TableLayoutManager) layoutManager).getFirstRowFields();

                // build column defs from the the first row of the table
                for (Component component : rowFields) {
                    if (actionFieldVisible && colIndex + 1 == actionIndex) {
                        String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                        tableColumnOptions.append(options + ",");
                        colIndex++;
                    }

                    // for FieldGroup, get the first field from that group
                    if (component instanceof FieldGroup) {
                        component = ((FieldGroup) component).getItems().get(0);
                    }

                    if (component instanceof DataField) {
                        DataField field = (DataField) component;

                        // if a field is marked as invisible in hiddenColumns, append options and skip sorting
                        if (getHiddenColumns() != null && getHiddenColumns().contains(field.getPropertyName())) {
                            tableColumnOptions.append("{"
                                    + visible(false)
                                    + ","
                                    + mData(useServerPaging, colIndex)
                                    + targets(colIndex)
                                    + "},");
                        } else if (getSortableColumns() != null && !getSortableColumns().isEmpty()) {
                            // if specified as a column as sortable then add it
                            if (getSortableColumns().contains(field.getPropertyName())) {
                                tableColumnOptions.append(getDataFieldColumnOptions(colIndex, collectionGroup, field)
                                        + ",");
                            } else { // else designate it as not sortable
                                tableColumnOptions.append("{"
                                        + sortable(false)
                                        + ","
                                        + mData(useServerPaging, colIndex)
                                        + targets(colIndex)
                                        + "},");
                            }
                        } else { // sortable columns not defined
                            String options = getDataFieldColumnOptions(colIndex, collectionGroup, field);
                            tableColumnOptions.append(options + ",");
                        }
                        colIndex++;
                    } else if (component instanceof MessageField) {
                        if (component.getDataAttributes() != null && UifConstants.RoleTypes.ROW_GROUPING.equals(
                                component.getDataAttributes().get(UifConstants.DataAttributes.ROLE))) {
                            // Grouping column is never shown, so skip
                            tableColumnOptions.append("{"
                                    + visible(false)
                                    + ","
                                    + mData(useServerPaging, colIndex)
                                    + targets(colIndex)
                                    + "},");
                        } else {
                            String options = constructTableColumnOptions(colIndex, true, useServerPaging, String.class,
                                    UifConstants.TableToolsValues.DOM_TEXT);
                            tableColumnOptions.append(options + ",");
                        }
                        colIndex++;
                    } else if (component instanceof LinkField) {
                        LinkField linkField = (LinkField) component;

                        Class<?> dataTypeClass = String.class;
                        // check to see if field has custom sort type
                        if (linkField.getSortAs() != null && linkField.getSortAs().length() > 0) {
                            if (linkField.getSortAs().equals(UifConstants.TableToolsValues.DATE)) {
                                dataTypeClass = java.sql.Date.class;
                            } else if (linkField.getSortAs().equals(UifConstants.TableToolsValues.NUMERIC)) {
                                dataTypeClass = Number.class;
                            } else if (linkField.getSortAs().equals(UifConstants.TableToolsValues.STRING)) {
                                dataTypeClass = String.class;
                            }
                        }

                        String options = constructTableColumnOptions(colIndex, true, useServerPaging, dataTypeClass,
                                UifConstants.TableToolsValues.DOM_TEXT);
                        tableColumnOptions.append(options + ",");
                        colIndex++;
                    } else {
                        String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                        tableColumnOptions.append(options + ",");
                        colIndex++;
                    }
                }

                if (actionFieldVisible && (actionIndex == -1 || actionIndex >= colIndex)) {
                    String options = constructTableColumnOptions(colIndex, false, useServerPaging, null, null);
                    tableColumnOptions.append(options);
                } else {
                    tableColumnOptions = new StringBuilder(StringUtils.removeEnd(tableColumnOptions.toString(), ","));
                }

                // merge the aoColumnDefs passed in
                if (!StringUtils.isEmpty(templateOptions.get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS))) {
                    String origAoOptions = templateOptions.get(UifConstants.TableToolsKeys.AO_COLUMN_DEFS).trim();
                    origAoOptions = StringUtils.removeStart(origAoOptions, "[");
                    origAoOptions = StringUtils.removeEnd(origAoOptions, "]");
                    tableColumnOptions.append("," + origAoOptions);
                }

                tableColumnOptions.append("]");
                templateOptions.put(UifConstants.TableToolsKeys.AO_COLUMN_DEFS, tableColumnOptions.toString());
            }
        }
    }

    /**
     * Builds default sorting options.
     *
     * @param lookupView the view for the lookup
     * @param collectionGroup the collection group for the table
     */
    protected void buildSortOptions(LookupView lookupView, CollectionGroup collectionGroup) {
        if (!isDisableTableSort() && CollectionUtils.isNotEmpty(lookupView.getDefaultSortAttributeNames())) {
            LayoutManager layoutManager = collectionGroup.getLayoutManager();

            if (layoutManager instanceof TableLayoutManager) {
                TableLayoutManager tableLayoutManager = (TableLayoutManager) layoutManager;

                List<String> firstRowPropertyNames = getFirstRowPropertyNames(tableLayoutManager.getFirstRowFields());
                List<String> defaultSortAttributeNames = lookupView.getDefaultSortAttributeNames();
                String sortDirection = lookupView.isDefaultSortAscending() ? "'asc'" : "'desc'";
                boolean actionFieldVisible = collectionGroup.isRenderLineActions() && !Boolean.TRUE.equals(
                        collectionGroup.getReadOnly());
                int actionIndex = ((TableLayoutManager) layoutManager).getActionColumnIndex();
                int columnIndexPrefix = 0;

                if (tableLayoutManager.isRenderSequenceField()) {
                    columnIndexPrefix++;
                }

                if (tableLayoutManager.getRowDetailsGroup() != null && CollectionUtils.isNotEmpty(
                        tableLayoutManager.getRowDetailsGroup().getItems())) {
                    columnIndexPrefix++;
                }

                StringBuffer tableToolsSortOptions = new StringBuffer("[");

                for (String defaultSortAttributeName : defaultSortAttributeNames) {
                    int index = firstRowPropertyNames.indexOf(defaultSortAttributeName);
                    if (index >= 0) {
                        if (tableToolsSortOptions.length() > 1) {
                            tableToolsSortOptions.append(",");
                        }
                        int columnIndex = columnIndexPrefix + index;
                        if (actionFieldVisible && actionIndex != -1 && actionIndex <= columnIndex + 1) {
                            columnIndex++;
                        }
                        tableToolsSortOptions.append("[" + columnIndex + "," + sortDirection + "]");
                    }
                }

                tableToolsSortOptions.append("]");

                if (templateOptions.isEmpty()) {
                    setTemplateOptions(new HashMap<String, String>());
                }
                templateOptions.put(UifConstants.TableToolsKeys.AASORTING, tableToolsSortOptions.toString());
            }
        }
    }

    private List<String> getFirstRowPropertyNames(List<Field> firstRowFields) {
        return Lists.transform(firstRowFields, new Function<Field, String>() {
            @Override
            public String apply(@Nullable Field field) {
                if (field != null && field instanceof DataField) {
                    return ((DataField) field).getPropertyName();
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * Construct the column options for a data field
     *
     * @param target column index
     * @param collectionGroup the collectionGroup in which the data field is defined
     * @param field the field to construction options for
     * @return options as valid for datatable
     */
    protected String getDataFieldColumnOptions(int target, CollectionGroup collectionGroup, DataField field) {
        String sortDataType = null;

        if (!Boolean.TRUE.equals(collectionGroup.getReadOnly())
                && (field instanceof InputField)
                && ((InputField) field).getControl() != null) {
            Control control = ((InputField) field).getControl();
            if (control instanceof SelectControl) {
                sortDataType = UifConstants.TableToolsValues.DOM_SELECT;
            } else if (control instanceof CheckboxControl || control instanceof CheckboxGroupControl) {
                sortDataType = UifConstants.TableToolsValues.DOM_CHECK;
            } else if (control instanceof RadioGroupControl) {
                sortDataType = UifConstants.TableToolsValues.DOM_RADIO;
            } else {
                sortDataType = UifConstants.TableToolsValues.DOM_TEXT;
            }
        } else {
            sortDataType = UifConstants.TableToolsValues.DOM_TEXT;
        }

        Class<?> dataTypeClass = ObjectPropertyUtils.getPropertyType(collectionGroup.getCollectionObjectClass(),
                field.getPropertyName());
        // check to see if field has custom sort type
        if (field.getSortAs() != null && field.getSortAs().length() > 0) {
            if (field.getSortAs().equals(UifConstants.TableToolsValues.CURRENCY)) {
                dataTypeClass = KualiDecimal.class;
            } else if (field.getSortAs().equals(UifConstants.TableToolsValues.DATE)) {
                dataTypeClass = java.sql.Date.class;
            } else if (field.getSortAs().equals(UifConstants.TableToolsValues.NUMERIC)) {
                dataTypeClass = Number.class;
            } else if (field.getSortAs().equals(UifConstants.TableToolsValues.STRING)) {
                dataTypeClass = String.class;
            }
        }

        boolean isSortable = true;
        if (field.isApplyMask()) {
            isSortable = false;
        }

        return constructTableColumnOptions(target, isSortable, collectionGroup.isUseServerPaging(), dataTypeClass,
                sortDataType);
    }

    /**
     * Constructs the sort data type for each data table columns in a format that will be used to
     * initialize the data table widget via javascript
     *
     * @param target the column index
     * @param isSortable whether a column should be marked as sortable
     * @param isUseServerPaging is server side paging enabled?
     * @param dataTypeClass the class type of the column value - used determine the sType option -
     * which identifies the search plugin to use
     * @param sortDataType Defines a data source type for the sorting which can be used to read
     * realtime information from the table
     * @return a formatted string with data table options for one column
     */
    public String constructTableColumnOptions(int target, boolean isSortable, boolean isUseServerPaging,
            Class<?> dataTypeClass, String sortDataType) {
        String options = "null";

        if (!isSortable || dataTypeClass == null) {
            options = sortable(false) + "," + sortType(UifConstants.TableToolsValues.STRING);
        } else {
            options = sortType(getSortType(dataTypeClass));

            if (!isUseServerPaging && !this.forceLocalJsonData) {
                options += "," + sortDataType(sortDataType);
            }
        }

        if (target < cellCssClasses.size() && target >= 0) {
            options += ", \"" + UifConstants.TableToolsKeys.CELL_CLASS + "\" : \"" + cellCssClasses.get(target) + "\"";
        }

        // only use the mDataProp when using json data (only relevant for this table type)
        options += mData(isUseServerPaging, target);

        if (!options.equals("null")) {
            options = "{" + options + "," + targets(target) + "}";
        } else {
            options = "{" + options + "}";
        }

        return options;
    }

    private String sortable(boolean sortable) {
        return "\"" + UifConstants.TableToolsKeys.SORTABLE + "\": " + (sortable ? UifConstants.TableToolsValues.TRUE :
                UifConstants.TableToolsValues.FALSE);
    }

    private String sortDataType(String sortDataType) {
        return "\"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\": \"" + sortDataType + "\"";
    }

    private String getSortType(Class<?> dataTypeClass) {
        String sortType = UifConstants.TableToolsValues.STRING;
        if (ClassUtils.isAssignable(dataTypeClass, KualiPercent.class)) {
            sortType = UifConstants.TableToolsValues.PERCENT;
        } else if (ClassUtils.isAssignable(dataTypeClass, KualiInteger.class) || ClassUtils.isAssignable(dataTypeClass,
                KualiDecimal.class)) {
            sortType = UifConstants.TableToolsValues.CURRENCY;
        } else if (ClassUtils.isAssignable(dataTypeClass, Timestamp.class)) {
            sortType = "date";
        } else if (ClassUtils.isAssignable(dataTypeClass, java.sql.Date.class) || ClassUtils.isAssignable(dataTypeClass,
                java.util.Date.class)) {
            sortType = UifConstants.TableToolsValues.DATE;
        } else if (ClassUtils.isAssignable(dataTypeClass, Number.class)) {
            sortType = UifConstants.TableToolsValues.NUMERIC;
        }
        return sortType;
    }

    private String sortType(String sortType) {
        return "\"" + UifConstants.TableToolsKeys.SORT_TYPE + "\": \"" + sortType + "\"";
    }

    private String targets(int target) {
        return "\"" + UifConstants.TableToolsKeys.TARGETS + "\": [" + target + "]";
    }

    private String visible(boolean visible) {
        return "\"" + UifConstants.TableToolsKeys.VISIBLE + "\": " + (visible ? UifConstants.TableToolsValues.TRUE :
                UifConstants.TableToolsValues.FALSE);
    }

    private String mData(boolean isUseServerPaging, int target) {
        if (isUseServerPaging || this.forceLocalJsonData) {
            return ", \"" + UifConstants.TableToolsKeys.MDATA +
                    "\" : function(row,type,newVal){ return _handleColData(row,type,'c" + target + "',newVal);}";
        }
        return "";
    }

    /**
     * Returns the text which is used to display text when the table is empty
     *
     * @return empty table message
     */
    @BeanTagAttribute
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
    @BeanTagAttribute
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
     * Returns true if export option is enabled
     *
     * @return the showExportOption
     */
    @BeanTagAttribute
    public boolean isShowExportOption() {
        return this.showExportOption;
    }

    /**
     * Show/Hide the search and export option in tabletools
     *
     * @param showExportOption the showExportOptions to set
     */
    public void setShowExportOption(boolean showExportOption) {
        this.showExportOption = showExportOption;
    }

    /**
     * Holds propertyNames for the ones meant to be hidden since columns are visible by default
     *
     * <p>
     * Duplicate entries are ignored and the order of entries is not significant
     * </p>
     *
     * @return a set with propertyNames of columns to be hidden
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.SETVALUE)
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
     * <p>
     * Duplicate entries are ignored and the order of entries is not significant
     * </p>
     *
     * @return a set of propertyNames with for columns that will be sorted
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.SETVALUE)
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
     * When the ajax source URL is specified the rich table plugin will retrieve the data by
     * invoking the URL and building the table rows from the result. This is different from the
     * standard use of the rich table plugin with uses progressive enhancement to decorate a table
     * that has already been rendereed
     * </p>
     *
     * @return URL for ajax source
     */
    @BeanTagAttribute
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
     * @return grouping options as a JS string
     */
    public String getGroupingOptionsJSString() {
        return groupingOptionsJSString;
    }

    /**
     * Set the groupingOptions js data. <b>This should not be set through XML configuration.</b>
     *
     * @param groupingOptionsJSString
     */
    public void setGroupingOptionsJSString(String groupingOptionsJSString) {
        this.groupingOptionsJSString = groupingOptionsJSString;
    }

    /**
     * If set to true and the aoColumnDefs template option is explicitly defined in templateOptions,
     * those aoColumnDefs will be used for this table. Otherwise, if false, the aoColumnDefs will
     * attempt to be merged with those that are automatically generated by RichTable
     *
     * @return true if the aoColumnDefs set will completely override those that are generated
     *         automatically by RichTable
     */
    @BeanTagAttribute
    public boolean isForceAoColumnDefsOverride() {
        return forceAoColumnDefsOverride;
    }

    /**
     * Set forceAoColumnDefsOverride
     *
     * @param forceAoColumnDefsOverride
     */
    public void setForceAoColumnDefsOverride(boolean forceAoColumnDefsOverride) {
        this.forceAoColumnDefsOverride = forceAoColumnDefsOverride;
    }

    /**
     * If true, the table will automatically use row JSON data generated by this widget
     *
     * <p>
     * This forces the table backed by this RichTable to get its content from a template option
     * called aaData. This will automatically skip row generation in the template, and cause the
     * table receive its data from the aaData template option automatically generated and set by
     * this RichTable. This allows the table to take advantage of the bDeferRender option (also
     * automatically set to true) when this table is a paged table (performance increase for tables
     * that are more than one page). Note: the CollectionGroup's isUseServerPaging flag will always
     * override this functionality if it is also true.
     * </p>
     *
     * @return true if backed by the aaData option in JSON, that is generated during the ftl
     *         rendering process by this widget for this table
     */
    @BeanTagAttribute
    public boolean isForceLocalJsonData() {
        return forceLocalJsonData;
    }

    /**
     * Set the forceLocalJsonData flag to force this table to use generated row json data
     *
     * @param forceLocalJsonData
     */
    public void setForceLocalJsonData(boolean forceLocalJsonData) {
        this.forceLocalJsonData = forceLocalJsonData;
    }

    /**
     * The nestedLevel property represents how many collection tables deep this particular table is
     *
     * <p>
     * This property must be manually set if the flag forceLocalJsonData is being used and the
     * collection table this RichTable represents is a subcollection of a TABLE collection (not
     * stacked collection). If this is true, add 1 for each level deep (ex. subCollection would be
     * 1, sub-subCollection would be 2). If this property is not set javascript errors will occur on
     * the page, as this determines how deep to escape certain characters.
     * </p>
     *
     * @return the nestedLevel representing the
     */
    @BeanTagAttribute
    public int getNestedLevel() {
        return nestedLevel;
    }

    /**
     * Set the nestedLevel for this table - must be set if using forceLocalJsonData and this is a
     * subCollection of a TableCollection (also using forceLocalJsonData)
     *
     * @param nestedLevel
     */
    public void setNestedLevel(int nestedLevel) {
        this.nestedLevel = nestedLevel;
    }

    /**
     * Get the translated aaData array generated by calls to addRowToTableData by the ftl
     *
     * <p>
     * This data is in JSON format and expected to be consumed by datatables when utilizing the
     * forceLocalJsonData option. This will be populated automatically if that flag is set to true.
     * </p>
     *
     * @return the generated aaData
     */
    public String getAaData() {
        return aaData;
    }

    /**
     * Set the translated aaData array
     *
     * <p>
     * This data is in JSON format and expected to be consumed by datatables when utilizing the
     * forceLocalJsonData. This setter is required for copyProperties()
     * </p>
     *
     * @param aaData the generated aaData
     */
    protected void setAaData(String aaData) {
        this.aaData = aaData;
    }

    /**
     * Get the simple value as a string that represents the field's sortable value, to be used as
     * val in the custom uif json data object (accessed by mDataProp option on datatables -
     * automated by framework) when using the forceLocalJsonData option or the CollectionGroup's
     * isUseServerPaging option
     *
     * @param model model the current model
     * @param field the field to retrieve a sortable value from for use in custom json data
     * @return the value as a String
     */
    public String getCellValue(Object model, Field field) {
        String value = KRADUtils.getSimpleFieldValue(model, field);

        if (value == null) {
            value = "null";
        } else {
            value = KRADConstants.QUOTE_PLACEHOLDER + value + KRADConstants.QUOTE_PLACEHOLDER;
        }

        return value;
    }

    /**
     * Add row content passed from table ftl to the aaData array by converting and escaping the
     * content to an object (in an array of objects) in JSON format
     *
     * <p>
     * The data in aaData is expected to be consumed by a call by the datatables plugin using
     * sAjaxSource or aaData. The addRowToTableData generation call is additive must be made per a
     * row in the ftl.
     * </p>
     *
     * @param row the row of content with each cell content surrounded by the @quot@ token and
     * followed by a comma
     */
    public void addRowToTableData(String row) {
        String escape = "";

        if (templateOptions.isEmpty()) {
            setTemplateOptions(new HashMap<String, String>());
        }

        // if nestedLevel is set add the appropriate amount of escape characters per a level of nesting
        for (int i = 0; i < nestedLevel && forceLocalJsonData; i++) {
            escape = escape + "\\";
        }

        // remove newlines and replace quotes and single quotes with unicode characters
        row = row.trim().replace("\"", escape + "\\u0022").replace("'", escape + "\\u0027").replace("\n", "").replace(
                "\r", "");

        // remove hanging comma
        row = StringUtils.removeEnd(row, ",");

        // replace all quote placeholders with actual quote characters
        row = row.replace(KRADConstants.QUOTE_PLACEHOLDER, "\"");
        row = "{" + row + "}";

        // if first call create aaData and force defer render option, otherwise append
        if (StringUtils.isBlank(aaData)) {
            aaData = "[" + row + "]";

            if (templateOptions.get(UifConstants.TableToolsKeys.DEFER_RENDER) == null) {
                //make sure deferred rendering is forced if not explicitly set
                templateOptions.put(UifConstants.TableToolsKeys.DEFER_RENDER, UifConstants.TableToolsValues.TRUE);
            }

        } else if (StringUtils.isNotBlank(row)) {
            aaData = aaData.substring(0, aaData.length() - 1) + "," + row + "]";
        }

        //force json data use if forceLocalJsonData flag is set
        if (forceLocalJsonData) {
            templateOptions.put(UifConstants.TableToolsKeys.AA_DATA, aaData);
        }
    }

    protected ConfigurationService getConfigurationService() {
        return CoreApiServiceLocator.getKualiConfigurationService();
    }
}
