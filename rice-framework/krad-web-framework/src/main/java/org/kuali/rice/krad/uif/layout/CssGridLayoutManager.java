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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Css Grid Layout manager is a layout manager which creates div "rows" and "cells" to replicate a table look by
 * using div elements for its items.  Items are added into rows based on their colSpan setting, while each row has a max
 * size of 9 columns.  By default, if colSpan is not set on an item, that item will take a full row.
 */
public class CssGridLayoutManager extends LayoutManagerBase {
    private static final long serialVersionUID = 1830635073147703757L;

    private static final int NUMBER_OF_COLUMNS = 9;
    private static final String BOOTSTRAP_SPAN_PREFIX = "span";

    private List<List<Component>> rows;
    private List<String> rowCssClassAttributes;
    private List<String> cellCssClassAttributes;

    private Map<String, String> rowCssClasses;
    private int defaultItemColSpan;

    public CssGridLayoutManager() {
        rows = new ArrayList<List<Component>>();
        rowCssClasses = new HashMap<String, String>();
        cellCssClassAttributes = new ArrayList<String>();
        rowCssClassAttributes = new ArrayList<String>();
    }

    /**
     * CssGridLayoutManager's performFinalize method calculates and seperates the items into rows based on their
     * colSpan settings and the defaultItemColSpan setting
     *
     * @see Component#performFinalize(org.kuali.rice.krad.uif.view.View, Object, org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(View view, Object model, Container container) {
        super.performFinalize(view, model, container);

        int rowSpaceLeft = NUMBER_OF_COLUMNS;
        int rowIndex = 1;
        List<Component> currentRow = new ArrayList<Component>();
        for (Component item : container.getItems()) {
            if (item == null) {
                continue;
            }

            // set colSpan to default setting (9 is the default)
            int colSpan = this.defaultItemColSpan;

            // if the item's set colSpan is greater than 1 set it to that number; 1 is the default colSpan for Component
            if (item.getColSpan() > 1 && item.getColSpan() <= NUMBER_OF_COLUMNS) {
                colSpan = item.getColSpan();
            }

            // determine "cell" div css
            List<String> cellCssClasses = item.getCellCssClasses();
            cellCssClasses.add(0, BOOTSTRAP_SPAN_PREFIX + colSpan);
            cellCssClassAttributes.add(getCellStyleClassesAsString(cellCssClasses));

            // calculate space left in row
            rowSpaceLeft = rowSpaceLeft - colSpan;

            if (rowSpaceLeft > 0) {
                // space is left, just add item to row
                currentRow.add(item);
            } else if (rowSpaceLeft < 0) {
                // went over, add item to next new row
                rows.add(new ArrayList<Component>(currentRow));
                currentRow = new ArrayList<Component>();
                currentRow.add(item);

                // determine "row" div css
                rowCssClassAttributes.add(generateRowClassProperty(rowIndex));
                rowIndex++;
                rowSpaceLeft = NUMBER_OF_COLUMNS - colSpan;
            } else if (rowSpaceLeft == 0) {
                // last item in row, create new row
                currentRow.add(item);
                rows.add(new ArrayList<Component>(currentRow));
                currentRow = new ArrayList<Component>();

                // determine "row" div css
                rowCssClassAttributes.add(generateRowClassProperty(rowIndex));
                rowIndex++;
                rowSpaceLeft = NUMBER_OF_COLUMNS;
            }
        }

        // add the last row if it wasn't full (but has items)
        if (!currentRow.isEmpty()) {
            // determine "row" div css
            rowCssClassAttributes.add(generateRowClassProperty(rowIndex));
            rows.add(currentRow);
        }
    }

    /**
     * Generate the row's class attribute based on settings passed into the rowCssClasses map
     *
     * @param index the current row's index
     * @return String that are the class selector names seperated by spaces
     */
    private String generateRowClassProperty(int index) {
        String stringIndex = String.valueOf(index);
        String structureClass = StringUtils.isNotBlank(rowCssClasses.get("all")) ? rowCssClasses.get("all") : "";
        String evenClass = StringUtils.isNotBlank(rowCssClasses.get("even")) ? " " + rowCssClasses.get("even") : "";
        String oddClass = StringUtils.isNotBlank(rowCssClasses.get("odd")) ? " " + rowCssClasses.get("odd") : "";
        String customClass = StringUtils.isNotBlank(rowCssClasses.get(stringIndex)) ? " " + rowCssClasses.get(
                stringIndex) : "";

        if (index % 2 == 0) {
            return structureClass + evenClass + customClass;
        } else {
            return structureClass + oddClass + customClass;
        }
    }

    /**
     * Builds the HTML class attribute string by combining the cellStyleClasses list
     * with a space delimiter
     *
     * @return class attribute string
     */
    private String getCellStyleClassesAsString(List<String> cellCssClasses) {
        if (cellCssClasses != null) {
            return StringUtils.join(cellCssClasses, " ");
        }

        return "";
    }

    /**
     * Get the rows (which are a list of components each)
     *
     * @return the List of Lists of Components which represents rows for this layout
     */
    public List<List<Component>> getRows() {
        return rows;
    }

    /**
     * List of css class HTML attribute values ordered by index of row
     *
     * @return the list of css class HTML attributes for rows
     */
    public List<String> getRowCssClassAttributes() {
        return rowCssClassAttributes;
    }

    /**
     * List of css class HTML attribute values ordered by the order in which the cell appears
     *
     * @return the list of css class HTML attributes for cells
     */
    public List<String> getCellCssClassAttributes() {
        return cellCssClassAttributes;
    }

    /**
     * The default cell colSpan to use for this layout (max setting, and the bean default, is 9)
     *
     * @return int representing the default colSpan for cells in this layout
     */
    public int getDefaultItemColSpan() {
        return defaultItemColSpan;
    }

    /**
     * Set the default colSpan for this layout's items
     *
     * @param defaultItemColSpan
     */
    public void setDefaultItemColSpan(int defaultItemColSpan) {
        this.defaultItemColSpan = defaultItemColSpan;
    }

    /**
     * The row css classes for the rows of this layout
     *
     * <p>To set a css class on all rows, use "all" as a key (keep in mind you must retain the appropriate row
     * layout class when doing so, set through the bean default for the layout to behave correctly).  To set a
     * class for even rows, use "even" as a key, for odd rows, use "odd".  Use index to target a specific row
     * by index.</p>
     *
     * @return a map which represents the css classes of the rows of this layout
     */
    public Map<String, String> getRowCssClasses() {
        return rowCssClasses;
    }

    /**
     * Set rowCssClasses
     *
     * @param rowCssClasses
     */
    public void setRowCssClasses(Map<String, String> rowCssClasses) {
        this.rowCssClasses = rowCssClasses;
    }
}
