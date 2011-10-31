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
package org.kuali.rice.krad.uif.widget;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.api.util.type.KualiPercent;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.control.CheckboxControl;
import org.kuali.rice.krad.uif.control.CheckboxGroupControl;
import org.kuali.rice.krad.uif.control.RadioGroupControl;
import org.kuali.rice.krad.uif.control.SelectControl;
import org.kuali.rice.krad.uif.control.TextControl;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.layout.LayoutManager;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.sql.Timestamp;

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
public class RichTable extends WidgetBase {
    private static final long serialVersionUID = 4671589690877390070L;

    private String emptyTableMessage;
    private boolean disableTableSort;

    private boolean showSearchAndExportOptions = true;

    public RichTable() {
        super();
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
            if (StringUtils.isNotBlank(getEmptyTableMessage())) {
                getComponentOptions().put(UifConstants.TableToolsKeys.LANGUAGE,
                        "{\"" + UifConstants.TableToolsKeys.EMPTY_TABLE + "\" : \"" + getEmptyTableMessage() + "\"}");
            }

            if (!isShowSearchAndExportOptions()) {
                Object domOption = getComponentOptions().get(UifConstants.TableToolsKeys.SDOM);
                if (domOption instanceof String) {
                    String sDomOption = (String) domOption;
                    if (StringUtils.isNotBlank(sDomOption)) {
                        sDomOption = StringUtils.remove(sDomOption, "T"); //Removes Export option
                        sDomOption = StringUtils.remove(sDomOption, "f"); //Removes search option
                        getComponentOptions().put(UifConstants.TableToolsKeys.SDOM, sDomOption);
                    }
                }

            }

            // for add events, disable initial sorting
            if (UifConstants.ActionEvents.ADD_LINE.equals(formBase.getActionEvent())) {
                getComponentOptions().put(UifConstants.TableToolsKeys.AASORTING, "[]");
            }

            if (component instanceof CollectionGroup) {
                buildTableSortOptions((CollectionGroup) component);
            }

            if (isDisableTableSort()) {
                getComponentOptions().put(UifConstants.TableToolsKeys.TABLE_SORT, "false");
            }
        }
    }

    /**
     * Builds column options for sorting
     *
     * @param collectionGroup
     */
    protected void buildTableSortOptions(CollectionGroup collectionGroup) {
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
                getComponentOptions().put(UifConstants.TableToolsKeys.SORT_SKIP_ROWS,
                        "[" + UifConstants.TableToolsValues.ADD_ROW_DEFAULT_INDEX + "]");
            }

            StringBuffer tableToolsColumnOptions = new StringBuffer("[");

            if (layoutManager instanceof TableLayoutManager && ((TableLayoutManager) layoutManager)
                    .isRenderSequenceField()) {
                tableToolsColumnOptions.append(" null ,");
            }

            // skip select field if enabled
            if (collectionGroup.isRenderSelectField()) {
                String colOptions = constructTableColumnOptions(false, null, null);
                tableToolsColumnOptions.append(colOptions + " , ");
            }

            // TODO: does this handle multiple rows correctly?
            for (Component component : collectionGroup.getItems()) {
                // For FieldGroup, get the first field from that group
                if (component instanceof FieldGroup) {
                    component = ((FieldGroup) component).getItems().get(0);
                }

                if (component instanceof DataField) {
                    DataField field = (DataField) component;

                    String sortType = null;
                    if (!collectionGroup.isReadOnly() && (field instanceof InputField)
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

                    Class dataTypeClass = ObjectPropertyUtils.getPropertyType(
                            collectionGroup.getCollectionObjectClass(), field.getPropertyName());
                    String colOptions = constructTableColumnOptions(true, dataTypeClass, sortType);
                    tableToolsColumnOptions.append(colOptions + " , ");
                } else {
                    String colOptions = constructTableColumnOptions(false, null, null);
                    tableToolsColumnOptions.append(colOptions + " , ");
                }
            }

            if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly()) {
                String colOptions = constructTableColumnOptions(false, null, null);
                tableToolsColumnOptions.append(colOptions);
            } else {
                tableToolsColumnOptions = new StringBuffer(StringUtils.removeEnd(tableToolsColumnOptions.toString(),
                        ", "));
            }

            tableToolsColumnOptions.append("]");

            getComponentOptions().put(UifConstants.TableToolsKeys.AO_COLUMNS, tableToolsColumnOptions.toString());
       }
    }

    /**
     * Constructs the sort data type for each datatable columns.
     */
    protected String constructTableColumnOptions(boolean isSortable, Class dataTypeClass, String sortDataType) {
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
            }
            else {
                sortType = UifConstants.TableToolsValues.STRING;
            }

            colOptions = "\"" + UifConstants.TableToolsKeys.SORT_DATA_TYPE + "\" : \"" + sortDataType + "\"";
            colOptions += " , \"" + UifConstants.TableToolsKeys.SORT_TYPE + "\" : \"" + sortType + "\"";
        }

        colOptions = "{" + colOptions + "}";

        return colOptions;
    }

    /**
     * Returns the text which is used to display text when the table is empty
     *
     * @return empty table message
     */
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
}
