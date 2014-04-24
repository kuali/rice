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

/**
 * Layout manager interface for grid layouts. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface GridLayoutManager extends LayoutManager {

    /**
     * Indicates the number of columns that should make up one row of data
     *
     * <p>
     * If the item count is greater than the number of columns, a new row will
     * be created to render the remaining items (and so on until all items are
     * placed).
     * </p>
     *
     * <p>
     * Note this does not include any generated columns by the layout manager,
     * so the final column count could be greater (if label fields are
     * separate).
     * </p>
     *
     * @return int
     */
    int getNumberOfColumns();

    /**
     * Setter for the number of columns (each row)
     *
     * @param numberOfColumns
     */
    void setNumberOfColumns(int numberOfColumns);

    /**
     * Indicates whether the number of columns for the table data should match
     * the number of fields given in the container's items list (so that each
     * field takes up one column without wrapping), this overrides the configured
     * numberOfColumns
     *
     * <p>
     * If set to true during the initialize phase the number of columns will be
     * set to the size of the container's field list, if false the configured
     * number of columns is used
     * </p>
     *
     * @return true if the column count should match the container's
     *         field count, false to use the configured number of columns
     */
    boolean isSuppressLineWrapping();

    /**
     * Setter for the suppressLineWrapping indicator
     *
     * @param suppressLineWrapping
     */
    void setSuppressLineWrapping(boolean suppressLineWrapping);

    /**
     * Indicates whether alternating row styles should be applied
     *
     * <p>
     * Indicator to layout manager templates to apply alternating row styles.
     * See the configured template for the actual style classes used
     * </p>
     *
     * @return true if alternating styles should be applied, false if
     *         all rows should have the same style
     */
    boolean isApplyAlternatingRowStyles();

    /**
     * Setter for the alternating row styles indicator
     *
     * @param applyAlternatingRowStyles
     */
    void setApplyAlternatingRowStyles(boolean applyAlternatingRowStyles);

    /**
     * Indicates whether the manager should default the cell widths
     *
     * <p>
     * If true, the manager will set the cell width by equally dividing by the
     * number of columns
     * </p>
     *
     * @return true if default cell widths should be applied, false if
     *         no defaults should be applied
     */
    boolean isApplyDefaultCellWidths();

    /**
     * Setter for the default cell width indicator
     *
     * @param applyDefaultCellWidths
     */
    void setApplyDefaultCellWidths(boolean applyDefaultCellWidths);

    /**
     * Indicates whether the first cell of each row should be rendered as a header cell (th)
     *
     * <p>
     * When this flag is turned on, the first cell for each row will be rendered as a header cell. If
     * {@link #isRenderAlternatingHeaderColumns()} is false, the remaining cells for the row will be rendered
     * as data cells, else they will alternate between cell headers
     * </p>
     *
     * @return true if first cell of each row should be rendered as a header cell
     */
    boolean isRenderRowFirstCellHeader();

    /**
     * Setter for render first row column as header indicator
     *
     * @param renderRowFirstCellHeader
     */
    void setRenderRowFirstCellHeader(boolean renderRowFirstCellHeader);

    /**
     * Indicates whether the first row of items rendered should all be rendered as table header (th) cells
     *
     * <p>
     * Generally when using a grid layout all the cells will be tds or alternating th/td (with the label in the
     * th cell). However in some cases it might be desired to display the labels in one row as table header cells (th)
     * followed by a row with the corresponding fields in td cells. When this is enabled this type of layout is
     * possible
     * </p>
     *
     * @return true if first row should be rendered as header cells
     */
    boolean isRenderFirstRowHeader();

    /**
     * Setter for the first row as header indicator
     *
     * @param renderFirstRowHeader
     */
    void setRenderFirstRowHeader(boolean renderFirstRowHeader);

    /**
     * Indicates whether header columns (th for tables) should be rendered for
     * every other item (alternating)
     *
     * <p>
     * If true the first cell of each row will be rendered as an header, with
     * every other cell in the row as a header
     * </p>
     *
     * @return true if alternating headers should be rendered, false if not
     */
    boolean isRenderAlternatingHeaderColumns();

    /**
     * Setter for the render alternating header columns indicator
     *
     * @param renderAlternatingHeaderColumns
     */
    void setRenderAlternatingHeaderColumns(boolean renderAlternatingHeaderColumns);

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
     * @see #getRowCssClasses()
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
     * @see #getRowDataAttributes()
     */
    void setRowDataAttributes(List<String> rowDataAttributes);

}
