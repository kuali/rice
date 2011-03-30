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
package org.kuali.rice.kns.uif.layout;

import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;

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
	private boolean matchColumnsToFieldCount;

	private boolean renderLabelFieldsSeparately;
	private boolean applyAlternatingRowStyles;

	public GridLayoutManager() {
		super();
		
		numberOfColumns = 2;
		renderLabelFieldsSeparately = true;
		applyAlternatingRowStyles = false;
		matchColumnsToFieldCount = false;
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * <li>If match field count is true, sets the number of columns to the
	 * container's items list size</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performInitialization(org.kuali.rice.kns.uif.container.View,
	 *      org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);

		if (matchColumnsToFieldCount) {
			numberOfColumns = container.getItems().size();
		}
	}

	/**
	 * The following finalization is performed:
	 * 
	 * <ul>
	 * <li>Build the list of fields for the grid</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performFinalize(View view, Object model, Container container) {
		super.performFinalize(view, model, container);

		if (matchColumnsToFieldCount) {
			numberOfColumns = container.getItems().size();
		}
	}

	/**
     * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performApplyModel(org.kuali.rice.kns.uif.container.View, java.lang.Object, org.kuali.rice.kns.uif.container.Container)
     */
    @Override
    public void performApplyModel(View view, Object model, Container container) {
	    super.performApplyModel(view, model, container);

		if (matchColumnsToFieldCount) {
			numberOfColumns = container.getItems().size();
		}
    }

	/**
	 * @see org.kuali.rice.kns.uif.layout.ContainerAware#getSupportedContainer()
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
	 * field takes up one column without wrapping)
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
	public boolean isMatchColumnsToFieldCount() {
		return this.matchColumnsToFieldCount;
	}

	/**
	 * Setter for the match column count to field count indicator
	 * 
	 * @param matchColumnsToFieldCount
	 */
	public void setMatchColumnsToFieldCount(boolean matchColumnsToFieldCount) {
		this.matchColumnsToFieldCount = matchColumnsToFieldCount;
	}

	/**
	 * Indicates whether the labels for the items should be rendered as a
	 * separate column
	 * 
	 * @return boolean true if label should be separate, false if they should be
	 *         with the field
	 */
	public boolean isRenderLabelFieldsSeparately() {
		return this.renderLabelFieldsSeparately;
	}

	/**
	 * Setter for the render label field separate indicator
	 * 
	 * @param renderLabelFieldsSeparately
	 */
	public void setRenderLabelFieldsSeparately(boolean renderLabelFieldsSeparately) {
		this.renderLabelFieldsSeparately = renderLabelFieldsSeparately;
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

}
