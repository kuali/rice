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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GridLayoutManager extends LayoutManagerBase {
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

				if (renderLabelFieldsSeparately && fieldLabel.getLabelField() != null && fieldLabel.isShowLabel()) {
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

	public int getNumberOfColumns() {
		return this.numberOfColumns;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		this.numberOfColumns = numberOfColumns;
	}

	public boolean isRenderLabelFieldsSeparately() {
		return this.renderLabelFieldsSeparately;
	}

	public void setRenderLabelFieldsSeparately(boolean renderLabelFieldsSeparately) {
		this.renderLabelFieldsSeparately = renderLabelFieldsSeparately;
	}

	public boolean isApplyAlternatingRowStyles() {
		return this.applyAlternatingRowStyles;
	}

	public void setApplyAlternatingRowStyles(boolean applyAlternatingRowStyles) {
		this.applyAlternatingRowStyles = applyAlternatingRowStyles;
	}

	public List<Component> getGridFields() {
		return this.gridFields;
	}

	public void setGridFields(List<Component> gridFields) {
		this.gridFields = gridFields;
	}

}
