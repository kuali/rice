/*
 * Copyright 2006-2013 The Kuali Foundation
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
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.component.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Css Grid Layout managers are a layout managers which creates div "rows" and "cells" to replicate a
 * table look by using div elements for its items.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "cssGridLayoutBase-bean", parent = "Uif-CssGridLayoutBase")
public abstract class CssGridLayoutManagerBase extends LayoutManagerBase {
    private static final long serialVersionUID = 1830635073147703757L;

    protected static final int NUMBER_OF_COLUMNS = 12;
    protected static final String BOOTSTRAP_SPAN_PREFIX = "col-md-";

    protected Map<String, String> conditionalRowCssClasses;
    protected String rowLayoutCssClass;

    // non-settable
    protected List<List<Component>> rows;
    protected List<String> rowCssClassAttributes;
    protected List<String> cellCssClassAttributes;

    public CssGridLayoutManagerBase() {
        rows = new ArrayList<List<Component>>();
        conditionalRowCssClasses = new HashMap<String, String>();
        cellCssClassAttributes = new ArrayList<String>();
        rowCssClassAttributes = new ArrayList<String>();
    }

    /**
     * Builds the HTML class attribute string by combining the cellStyleClasses list with a space
     * delimiter
     *
     * @return class attribute string
     */
    protected String getCellStyleClassesAsString(List<String> cellCssClasses) {
        if (cellCssClasses != null) {
            return StringUtils.join(cellCssClasses, " ").trim();
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
     * The row css classes for the rows of this layout
     *
     * <p>
     * To set a css class on all rows, use "all" as a key. To set a class for even rows, use "even"
     * as a key, for odd rows, use "odd". Use a one-based index to target a specific row by index.
     * </p>
     *
     * @return a map which represents the css classes of the rows of this layout
     */
    @BeanTagAttribute(name = "conditionalRowCssClasses", type = BeanTagAttribute.AttributeType.MAPVALUE)
    public Map<String, String> getConditionalRowCssClasses() {
        return conditionalRowCssClasses;
    }

    /**
     * Set conditionalRowCssClasses
     *
     * @param conditionalRowCssClasses
     */
    public void setConditionalRowCssClasses(Map<String, String> conditionalRowCssClasses) {
        this.conditionalRowCssClasses = conditionalRowCssClasses;
    }

    /**
     * The layout css class used by the framework to represent the row as a row visually (currently
     * using a bootstrap class), which should not be manually reset in most situations
     *
     * @return the css structure class for the rows of this layout
     */
    @BeanTagAttribute(name = "rowLayoutCssClass")
    public String getRowLayoutCssClass() {
        return rowLayoutCssClass;
    }

    /**
     * Set the rowLayoutCssClass
     *
     * @param rowLayoutCssClass
     */
    public void setRowLayoutCssClass(String rowLayoutCssClass) {
        this.rowLayoutCssClass = rowLayoutCssClass;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#copyProperties(Object)
     */
    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        CssGridLayoutManagerBase cssGridLayoutManagerCopy = (CssGridLayoutManagerBase) component;

        if (this.rowLayoutCssClass != null) {
            cssGridLayoutManagerCopy.setRowLayoutCssClass(this.rowLayoutCssClass);
        }

        if (this.conditionalRowCssClasses != null) {
            cssGridLayoutManagerCopy.setConditionalRowCssClasses(new HashMap<String, String>(
                    this.conditionalRowCssClasses));
        }

        if (this.cellCssClassAttributes != null) {
            cssGridLayoutManagerCopy.cellCssClassAttributes = new ArrayList<String>(this.cellCssClassAttributes);
        }

        if (this.rowCssClassAttributes != null) {
            cssGridLayoutManagerCopy.rowCssClassAttributes = new ArrayList<String>(this.rowCssClassAttributes);
        }

        if (this.rows != null) {
            cssGridLayoutManagerCopy.rows = new ArrayList<List<Component>>();
            for (List<Component> row : this.rows) {
                List<Component> rowCopy = new ArrayList<Component>();

                if (row == null) {
                    cssGridLayoutManagerCopy.rows.add(row);
                    continue;
                }

                for (Component cellComp : row) {
                    if (cellComp == null) {
                        rowCopy.add(cellComp);
                        continue;
                    }
                    rowCopy.add((Component) cellComp.copy());
                }

                cssGridLayoutManagerCopy.rows.add(rowCopy);
            }
        }
    }
}