/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kuali.rice.krad.uif.layout;

import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.view.View;

/**
 * Layout manager that organizes its components in a table based grid
 * 
 * <p>
 * Items are laid out from left to right (with each item taking up one column)
 * until the configured number of columns is reached. If the item count is
 * greater than the number of columns, a new row will be created to render the
 * remaining items (and so on until all items are placed). Labels for the fields
 * can be pulled out (default) and rendered as a separate column. The manager
 * also supports the column span and row span options for the field items. If
 * not specified the default is 1.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GridLayoutManager extends LayoutManagerBase {
    private static final long serialVersionUID = 1890011900375071128L;

    private int numberOfColumns;

    private boolean suppressLineWrapping;
    private boolean applyAlternatingRowStyles;
    private boolean applyDefaultCellWidths;
    private boolean renderAlternatingHeaderColumns;

    public GridLayoutManager() {
        super();
    }

    /**
     * The following finalization is performed:
     * 
     * <ul>
     * <li>If suppressLineWrapping is true, sets the number of columns to the
     * container's items list size</li>
     * </ul>
     * 
     * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#performFinalize(org.kuali.rice.krad.uif.view.View,
     *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
     */
    @Override
    public void performFinalize(View view, Object model, Container container) {
        super.performFinalize(view, model, container);

        if (suppressLineWrapping) {
            numberOfColumns = container.getItems().size();
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.ContainerAware#getSupportedContainer()
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return Group.class;
    }

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
     * @return
     */
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    /**
     * Setter for the number of columns (each row)
     * 
     * @param numberOfColumns
     */
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

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
     * @return boolean true if the column count should match the container's
     *         field count, false to use the configured number of columns
     */
    public boolean isSuppressLineWrapping() {
        return this.suppressLineWrapping;
    }

    /**
     * Setter for the suppressLineWrapping indicator
     * 
     * @param suppressLineWrapping
     */
    public void setSuppressLineWrapping(boolean suppressLineWrapping) {
        this.suppressLineWrapping = suppressLineWrapping;
    }

    /**
     * Indicates whether alternating row styles should be applied
     * 
     * <p>
     * Indicator to layout manager templates to apply alternating row styles.
     * See the configured template for the actual style classes used
     * </p>
     * 
     * @return boolean true if alternating styles should be applied, false if
     *         all rows should have the same style
     */
    public boolean isApplyAlternatingRowStyles() {
        return this.applyAlternatingRowStyles;
    }

    /**
     * Setter for the alternating row styles indicator
     * 
     * @param applyAlternatingRowStyles
     */
    public void setApplyAlternatingRowStyles(boolean applyAlternatingRowStyles) {
        this.applyAlternatingRowStyles = applyAlternatingRowStyles;
    }

    /**
     * Indicates whether the manager should default the cell widths
     * 
     * <p>
     * If true, the manager will set the cell width by equally dividing by the
     * number of columns
     * </p>
     * 
     * @return boolean true if default cell widths should be applied, false if
     *         no defaults should be applied
     */
    public boolean isApplyDefaultCellWidths() {
        return this.applyDefaultCellWidths;
    }

    /**
     * Setter for the default cell width indicator
     * 
     * @param applyDefaultCellWidths
     */
    public void setApplyDefaultCellWidths(boolean applyDefaultCellWidths) {
        this.applyDefaultCellWidths = applyDefaultCellWidths;
    }

    /**
     * Indicates whether header columns (th for tables) should be rendered for
     * every other item (alternating)
     * 
     * <p>
     * If true the first cell of each row will be rendered as an header, with
     * every other cell in the row as a header
     * </p>
     * 
     * @return boolean true if alternating headers should be rendered, false if
     *         not
     */
    public boolean isRenderAlternatingHeaderColumns() {
        return this.renderAlternatingHeaderColumns;
    }

    /**
     * Setter for the render alternating header columns indicator
     * 
     * @param renderAlternatingHeaderColumns
     */
    public void setRenderAlternatingHeaderColumns(boolean renderAlternatingHeaderColumns) {
        this.renderAlternatingHeaderColumns = renderAlternatingHeaderColumns;
    }

}
