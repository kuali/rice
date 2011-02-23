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

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.Field;

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

	private boolean renderLabelFieldsSeparately;
	private boolean applyAlternatingRowStyles;

	private List<Component> gridFields;

	public GridLayoutManager() {
		numberOfColumns = 2;
		renderLabelFieldsSeparately = true;
		applyAlternatingRowStyles = false;

		gridFields = new ArrayList<Component>();
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#performInitialization(org.kuali.rice.kns.uif.container.View,
	 *      org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);

		buildGridFieldList(view, container);
	}

	/**
	 * Refreshes the grid field list from the container
	 * 
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#refresh(org.kuali.rice.kns.uif.container.View,
	 *      org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void refresh(View view, Container container) {
		super.refresh(view, container);

		buildGridFieldList(view, container);
	}

	/**
	 * Sets the fields list that will be rendered based on the containers items.
	 * If the item field has a LabelField and renderLabelFieldsSeparately is set
	 * to true, it will be pulled out and added to the fields list separately.
	 * The <code>LabelField</code> is placed immediately before the Field it was
	 * pulled in the list.
	 * 
	 * @param view
	 *            - View instance the container belongs to
	 * @param container
	 *            - container instance the manager applies to
	 */
	protected void buildGridFieldList(View view, Container container) {
		gridFields = new ArrayList<Component>();

		for (Component item : container.getItems()) {
			if (item instanceof Field) {
				Field fieldLabel = (Field) item;

				if (renderLabelFieldsSeparately && fieldLabel.getLabelField() != null
						&& fieldLabel.getLabelField().isRender()) {
					gridFields.add(fieldLabel.getLabelField());

					// set boolean to indicate label field should not be
					// rendered with the attribute
					fieldLabel.setLabelFieldRendered(true);
				}
			}

			gridFields.add(item);
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

	/**
	 * List of components that will be placed by the layout manager. This
	 * includes the generated fields by the layout manager
	 * 
	 * @return List<Component> fields
	 */
	public List<Component> getGridFields() {
		return this.gridFields;
	}

}
