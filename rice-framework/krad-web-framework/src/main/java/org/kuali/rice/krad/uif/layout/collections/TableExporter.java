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

import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.element.Label;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.layout.TableLayoutManager;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class TableExporter {

    /**
     * Generates formatted table data based on the posted view results and format type.
     *
     * @param collectionGroup collection group instance that should be exported
     * @param model top level object containing the data
     * @param formatType format which the table should be generated in
     * @return generated table data
     */
    public static String buildExportTableData(CollectionGroup collectionGroup, Object model, String formatType) {
        // load table format elements used for generated particular style
        Map<String, String> exportTableFormatOptions = getExportTableFormatOptions(formatType);
        String startTable = exportTableFormatOptions.get("startTable");
        String endTable = exportTableFormatOptions.get("endTable");

        StringBuilder tableRows = new StringBuilder("");

        TableLayoutManager layoutManager = (TableLayoutManager) collectionGroup.getLayoutManager();

        List<Label> headerLabels = layoutManager.getHeaderLabels();
        List<Field> rowFields = layoutManager.getAllRowFields();
        int numberOfColumns = layoutManager.getNumberOfColumns();

        List<Integer> ignoredColumns = findIgnoredColumns(layoutManager, collectionGroup);

        // append table header data as first row
        if (!headerLabels.isEmpty()) {
            List<String> labels = new ArrayList<String>();

            for (Label label : headerLabels) {
                labels.add(label.getLabelText());
            }

            tableRows.append(buildExportTableRow(labels, exportTableFormatOptions, ignoredColumns));
        }

        // load all subsequent rows to the table
        if (!rowFields.isEmpty()) {
            List<String> columnData = new ArrayList<String>();

            for (Field field : rowFields) {
                columnData.add(KRADUtils.getSimpleFieldValue(model, field));

                if (columnData.size() >= numberOfColumns) {
                    tableRows.append(buildExportTableRow(columnData, exportTableFormatOptions, ignoredColumns));
                    columnData.clear();
                }
            }
        }

        return startTable + tableRows.toString() + endTable;
    }

    /**
     * Helper function to determine whether if column should be displayed. Used to help extract
     * columns used in screen format such as action or select that is not needed for export.
     *
     * @param layoutManager The layout manager.
     * @param collectionGroup The collection group.
     * @return Index numbers for all columns that should be ignored.
     */
    protected static List<Integer> findIgnoredColumns(TableLayoutManager layoutManager,
            CollectionGroup collectionGroup) {
        List<Integer> ignoreColumns = new ArrayList<Integer>();

        int actionColumnIndex = layoutManager.getActionColumnIndex();
        int numberOfColumns = layoutManager.getNumberOfColumns();
        boolean renderActions = collectionGroup.isRenderLineActions() && !Boolean.TRUE.equals(collectionGroup.getReadOnly());
        boolean renderSelectField = collectionGroup.isIncludeLineSelectionField();
        boolean renderSequenceField = layoutManager.isRenderSequenceField();

        if (renderActions || renderSelectField || renderSequenceField) {
            int shiftColumn = 0;

            if (renderSelectField) {
                ignoreColumns.add(shiftColumn);
                shiftColumn++;
            }
            if (renderSequenceField) {
                ignoreColumns.add(shiftColumn);
                shiftColumn++;
            }
            if (renderActions) {
                if (actionColumnIndex == 1) {
                    ignoreColumns.add(shiftColumn);
                } else if (actionColumnIndex == -1) {
                    ignoreColumns.add(numberOfColumns - 1);
                } else if (actionColumnIndex > 1) {
                    ignoreColumns.add(actionColumnIndex);
                }
            }
        }

        return ignoreColumns;
    }

    /**
     * Helper method used to build formatted table row data for export.
     *
     * @param columnData Formatted column data.
     * @param tableFormatOptions Format options: startRow and endRow are added to the row,
     * startColumn and endColumn are added to each column.
     * @param ignoredColumns Index numbers of columns to ignore.
     * @return Formatted table data for one row.
     */
    protected static String buildExportTableRow(List<String> columnData, Map<String, String> tableFormatOptions,
            List<Integer> ignoredColumns) {
        String startRow = tableFormatOptions.get("startRow");
        String endRow = tableFormatOptions.get("endRow");
        String startColumn = tableFormatOptions.get("startColumn");
        String endColumn = tableFormatOptions.get("endColumn");
        boolean appendLastColumn = Boolean.valueOf(tableFormatOptions.get("appendLastColumn"));
        int columnIndex = 0;

        StringBuilder builder = new StringBuilder();
        for (String columnItem : columnData) {
            boolean displayColumn = !ignoredColumns.contains(columnIndex);
            if (displayColumn) {
                builder.append(startColumn + columnItem + endColumn);
            }
            if (columnIndex >= columnData.size() - 1 && !appendLastColumn) {
                builder.delete(builder.length() - endColumn.length(), builder.length());
            }
            columnIndex++;
        }

        return startRow + builder.toString() + endRow;
    }

    /**
     * Identify table formatting elements based on formatType. Defaults to txt format if not found
     *
     * @param formatType The format type: csv, xls, or xml.
     * @return The format options for to use with the indicated format type.
     */
    protected static Map<String, String> getExportTableFormatOptions(String formatType) {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("contentType", "text/plain");
        map.put("formatType", "txt");
        map.put("startTable", "");
        map.put("endTable", "");
        map.put("startRow", "");
        map.put("endRow", "\n");
        map.put("startColumn", "");
        map.put("endColumn", ", ");
        map.put("appendLastColumn", "false");

        if ("csv".equals(formatType)) {
            map.put("contentType", "text/csv");
            map.put("formatType", "csv");
            map.put("startTable", "");
            map.put("endTable", "");
            map.put("startRow", "");
            map.put("endRow", "\n");
            map.put("startColumn", "");
            map.put("endColumn", ", ");
            map.put("appendLastColumn", "false");

        } else if ("xls".equals(formatType)) {
            map.put("contentType", "application/vnd.ms-excel");
            map.put("formatType", "xls");
            map.put("startTable", "");
            map.put("endTable", "");
            map.put("startRow", "");
            map.put("endRow", "\n");
            map.put("startColumn", "\"");
            map.put("endColumn", "\"\t");
            map.put("appendLastColumn", "true");

        } else if ("xml".equals(formatType)) {
            map.put("contentType", "application/xml");
            map.put("formatType", "xml");
            map.put("startTable", "<table>\n");
            map.put("endTable", "</table>\n");
            map.put("startRow", "  <row>\n");
            map.put("endRow", "  </row>\n");
            map.put("startColumn", "    <column>");
            map.put("endColumn", "</column>\n");
            map.put("appendLastColumn", "true");

        }

        return map;
    }
}
