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

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Css Grid Layout manager is a layout manager which creates div "rows" and "cells" to replicate a
 * table look by using div elements for its items.
 *
 * <p>
 * Items are added into rows based on their colSpan
 * setting, while each row has a max size of 12 columns. By default, if colSpan is not set on an
 * item, that item will take a full row.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "cssGridLayout-bean", parent = "Uif-CssGridLayout")
public class CssGridLayoutManager extends CssGridLayoutManagerBase {
    private static final long serialVersionUID = 1830635073147703757L;

    private int defaultItemColSpan;

    /**
     * CssGridLayoutManager's performFinalize method calculates and separates the items into rows
     * based on their colSpan settings and the defaultItemColSpan setting
     *
     * @see Component#performFinalize(org.kuali.rice.krad.uif.view.View, Object,
     *      org.kuali.rice.krad.uif.component.Component)
     */
    @Override
    public void performFinalize(Object model, Component component) {
        super.performFinalize(model, component);

        Container container = (Container) component;
        processNormalLayout(container);

    }

    /**
     * Separates the container's items into the appropriate number of rows and div "cells" based on
     * the defaultColSpan property settings and colSpan settings of the items
     *
     * @param container the container using this layout manager
     */
    private void processNormalLayout(Container container) {
        int rowSpaceLeft = NUMBER_OF_COLUMNS;
        int rowIndex = 0;
        boolean isOdd = true;
        List<Component> currentRow = new ArrayList<Component>();
        for (Component item : container.getItems()) {
            if (item == null) {
                continue;
            }
            isOdd = rowIndex % 2 == 0;

            // set colSpan to default setting (12 is the default)
            int colSpan = this.defaultItemColSpan;

            // if the item's set colSpan is greater than 1 set it to that number; 1 is the default colSpan for Component
            if (item.getColSpan() > 1 && item.getColSpan() <= NUMBER_OF_COLUMNS) {
                colSpan = item.getColSpan();
            }

            // determine "cell" div css
            List<String> cellCssClasses = item.getWrapperCssClasses();
            if (cellCssClasses == null) {
                item.setWrapperCssClasses(new ArrayList<String>());
                cellCssClasses = item.getWrapperCssClasses();
            }
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
                String rowCss = rowLayoutCssClass + " " + KRADUtils.generateRowCssClassString(conditionalRowCssClasses,
                        rowIndex, isOdd, null, null);
                rowCssClassAttributes.add(rowCss);

                rowIndex++;
                rowSpaceLeft = NUMBER_OF_COLUMNS - colSpan;
            } else if (rowSpaceLeft == 0) {
                // last item in row, create new row
                currentRow.add(item);
                rows.add(new ArrayList<Component>(currentRow));
                currentRow = new ArrayList<Component>();

                // determine "row" div css
                String rowCss = rowLayoutCssClass + " " + KRADUtils.generateRowCssClassString(conditionalRowCssClasses,
                        rowIndex, isOdd, null, null);
                rowCssClassAttributes.add(rowCss);

                rowIndex++;
                rowSpaceLeft = NUMBER_OF_COLUMNS;
            }
        }

        isOdd = rowIndex % 2 == 0;
        // add the last row if it wasn't full (but has items)
        if (!currentRow.isEmpty()) {
            // determine "row" div css
            String rowCss = rowLayoutCssClass + " " + KRADUtils.generateRowCssClassString(conditionalRowCssClasses,
                    rowIndex, isOdd, null, null);
            rowCssClassAttributes.add(rowCss);

            rows.add(currentRow);
        }
    }

    /**
     * The default cell colSpan to use for this layout (max setting, and the bean default, is 12)
     *
     * @return int representing the default colSpan for cells in this layout
     */
    @BeanTagAttribute(name = "defaultItemColSpan")
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
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        CssGridLayoutManager cssGridLayoutManagerCopy = (CssGridLayoutManager) component;

        cssGridLayoutManagerCopy.setDefaultItemColSpan(this.defaultItemColSpan);
    }
}
