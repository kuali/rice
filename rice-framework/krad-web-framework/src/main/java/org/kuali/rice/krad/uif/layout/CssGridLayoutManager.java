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
import org.kuali.rice.krad.uif.util.LifecycleElement;

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
@BeanTag(name = "cssGridLayout", parent = "Uif-CssGridLayout")
public class CssGridLayoutManager extends CssGridLayoutManagerBase {
    private static final long serialVersionUID = 1830635073147703757L;

    private int defaultItemSize;

    private CssGridSizes defaultItemSizes;

    public CssGridLayoutManager() {
        super();

        defaultItemSizes = new CssGridSizes();
    }

    /**
     * CssGridLayoutManager's performFinalize method calculates and separates the items into rows
     * based on their colSpan settings and the defaultItemSize setting
     *
     * {@inheritDoc}
     */
    @Override
    public void performFinalize(Object model, LifecycleElement component) {
        super.performFinalize(model, component);

        Container container = (Container) component;
        cellItems = new ArrayList<Component>();
        processNormalLayout(container);

    }

    /**
     * Separates the container's items into the appropriate number of rows and div "cells" based on
     * the defaultColSpan property settings and colSpan settings of the items
     *
     * @param container the container using this layout manager
     */
    private void processNormalLayout(Container container) {
        for (Component item : container.getItems()) {
            if (item == null) {
                continue;
            }

            // set colSpan to default setting (12 is the default)
            int colSpan = this.defaultItemSize;

            // if the item's mdSize is set, use that as the col span for calculations below
            if (item.getColSpan() > 1 && item.getColSpan() <= NUMBER_OF_COLUMNS) {
                colSpan = item.getColSpan();
            }

            List<String> cellCssClasses = item.getWrapperCssClasses();
            if (cellCssClasses == null) {
                item.setWrapperCssClasses(new ArrayList<String>());
                cellCssClasses = item.getWrapperCssClasses();
            }

            // Determine "cell" div css
            calculateCssClassAndSize(item, cellCssClasses, defaultItemSizes, colSpan);

            // Add dynamic left clear classes for potential wrapping content at each screen size
            addLeftClearCssClass(cellCssClasses);

            cellCssClassAttributes.add(getCellStyleClassesAsString(cellCssClasses));
            cellItems.add(item);
        }
    }

    /**
     * The default "cell" size to use for this layout - this converts to medium size
     * (max setting, and the default, is 12)
     *
     * <p>
     * This is a quick and easy setter for default mdSize for this layout, as a common use case is to have
     * a different layout for medium devices and up, while small and extra small will consume the full screen.
     * For customizations at every screen size, use defaultItemSizes.
     * </p>
     *
     * @return int representing the default colSpan for cells in this layout
     */
    @BeanTagAttribute
    public int getDefaultItemSize() {
        return defaultItemSize;
    }

    /**
     * Set the default colSpan for this layout's items
     *
     * @param defaultItemSize
     */
    public void setDefaultItemSize(int defaultItemSize) {
        this.defaultItemSize = defaultItemSize;
    }

    /**
     * Default sizes for each item in this css grid layout, these settings will override the setting in
     * defaultItemSize,
     * but will not override item specific cssGridSizes.
     *
     * @return cssGridSizes containing the sizes of items in this group to use as default
     */
    public CssGridSizes getDefaultItemSizes() {
        return defaultItemSizes;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.CssGridLayoutManager#getDefaultItemSizes()
     */
    public void setDefaultItemSizes(CssGridSizes defaultItemSizes) {
        this.defaultItemSizes = defaultItemSizes;
    }
}
