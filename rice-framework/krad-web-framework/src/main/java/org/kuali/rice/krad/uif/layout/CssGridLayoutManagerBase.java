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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycleRestriction;

import java.util.ArrayList;
import java.util.List;

/**
 * Css Grid Layout managers are a layout managers which creates div "rows" and "cells" to replicate a
 * table look by using div elements for its items.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "cssGridLayoutBase", parent = "Uif-CssGridLayoutBase")
public abstract class CssGridLayoutManagerBase extends LayoutManagerBase {
    private static final long serialVersionUID = 1830635073147703757L;

    protected static final int NUMBER_OF_COLUMNS = 12;
    protected static final String BOOTSTRAP_SPAN_PREFIX = "col-md-";

    // Cannot be set by bean
    protected List<Component> cellItems;
    protected List<String> cellCssClassAttributes;

    // Internal local variables
    protected int xsTotalSize = 0;
    protected int smTotalSize = 0;
    protected int mdTotalSize = 0;
    protected int lgTotalSize = 0;

    public CssGridLayoutManagerBase() {
        cellCssClassAttributes = new ArrayList<String>();
        cellItems = new ArrayList<Component>();
    }

    /**
     * Determines the css class(es) and based on what settings the item, defaultSizes and basicSize have
     *
     * <p>
     * Priority of what sizes to apply are as follows:
     * 1. cssGridSizes on the item itself
     * 2. Sizes in the defaultSizes object
     * 3. basicSize passed in the if the above two contain no settings, defaults to md (medium) col size
     * </p>
     *
     * @param item the item to process classes for
     * @param cellCssClasses the list of classes to add the new class string to
     * @param defaultSizes the default fallback sizes to use if items have none
     * @param basicSize the fallback md size to use if both item and default size have none
     */
    protected void calculateCssClassAndSize(Component item, List<String> cellCssClasses, CssGridSizes defaultSizes,
            int basicSize) {

        if (StringUtils.isNotBlank(item.getCssGridSizes().getCssClassString())) {
            cellCssClasses.add(0, item.getCssGridSizes().getCssClassString());

            xsTotalSize += item.getCssGridSizes().getXsSize();
            smTotalSize += item.getCssGridSizes().getTotalSmSize();
            mdTotalSize += item.getCssGridSizes().getTotalMdSize();
            lgTotalSize += item.getCssGridSizes().getTotalLgSize();
        } else if (StringUtils.isNotBlank(defaultSizes.getCssClassString())) {
            cellCssClasses.add(0, defaultSizes.getCssClassString());

            xsTotalSize += defaultSizes.getXsSize();
            smTotalSize += defaultSizes.getTotalSmSize();
            mdTotalSize += defaultSizes.getTotalMdSize();
            lgTotalSize += defaultSizes.getTotalLgSize();
        } else {
            cellCssClasses.add(0, BOOTSTRAP_SPAN_PREFIX + basicSize);

            mdTotalSize += basicSize;
        }
    }

    /**
     * Adds a class (or classeees) which will clear the left float for wrapped content at each screen size, which
     * will prevent natural float from taking available space instead of wrapping to a new "row".
     *
     * @param cellCssClasses the set of css classes to add the left clear class to
     */
    protected void addLeftClearCssClass(List<String> cellCssClasses) {
        String classString = getCellStyleClassesAsString(cellCssClasses);

        // We explicitly check for the col prefix to avoid unnecessary class additions since the clear will be
        // inherited from a smaller size screen if no size/offset has been specified for this size specifically
        // see KRAD css grid css
        if (lgTotalSize > 12) {
            if (classString.contains(CssConstants.CssGrid.LG_COL_PREFIX)) {
                cellCssClasses.add(0, CssConstants.CssGrid.LG_CLEAR_LEFT);
            }
            lgTotalSize = lgTotalSize - 12;
        }

        if (mdTotalSize > 12) {
            if (classString.contains(CssConstants.CssGrid.MD_COL_PREFIX)) {
                cellCssClasses.add(0, CssConstants.CssGrid.MD_CLEAR_LEFT);
            }
            mdTotalSize = mdTotalSize - 12;
        }

        if (smTotalSize > 12) {
            if (classString.contains(CssConstants.CssGrid.SM_COL_PREFIX)) {
                cellCssClasses.add(0, CssConstants.CssGrid.SM_CLEAR_LEFT);
            }
            smTotalSize = smTotalSize - 12;
        }

        if (xsTotalSize > 12) {
            cellCssClasses.add(0, CssConstants.CssGrid.XS_CLEAR_LEFT);
            xsTotalSize = xsTotalSize - 12;
        }
    }

    /**
     * Builds the HTML class attribute string by combining the cellStyleClasses list with a space
     * delimiter
     *
     * @param cellCssClasses list of cell CSS classes
     * @return class attribute string
     */
    protected String getCellStyleClassesAsString(List<String> cellCssClasses) {
        if (cellCssClasses != null) {
            return StringUtils.join(cellCssClasses, " ").trim();
        }

        return "";
    }

    /**
     * Get the items which will make up each "cell" divs of this css grid layout, these divs will have appropriate
     * css class applied to them based on the values stored in cellCssClassAttributes
     *
     * @return the items of this cssGrid
     */
    @ViewLifecycleRestriction
    public List<Component> getCellItems() {
        return cellItems;
    }

    /**
     * List of css class HTML attribute values ordered by the order in which the cell appears
     *
     * @return the list of css class HTML attributes for cells
     */
    public List<String> getCellCssClassAttributes() {
        return cellCssClassAttributes;
    }

}