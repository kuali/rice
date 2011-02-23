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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.CssConstants;
import org.kuali.rice.kns.uif.CssConstants.Margins;
import org.kuali.rice.kns.uif.UifConstants.Orientation;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.View;

/**
 * Layout manager that organizes components in a single row (horizontal) or
 * column (vertical)
 * 
 * <p>
 * Although a table based template could be used, setup is done to also support
 * a CSS based template. The items in the <code>Container</code> instance are
 * rendered sequentially wrapping each one with a span element. The padding
 * property can be configured to space the elements as needed. To achieve a
 * vertical orientation, the span style is set to block. Aditional styling can
 * be set for the items by using the itemSpanStyle property.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BoxLayoutManager extends LayoutManagerBase {
	private static final long serialVersionUID = 4467342272983290044L;

	private String orientation;
	private String padding;

	private String itemSpanStyle;

	public BoxLayoutManager() {
		super();

		orientation = Orientation.HORIZONTAL;
		padding = "1px";
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the itemSpanStyle</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View,org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);

		if (StringUtils.isBlank(itemSpanStyle)) {
			if (StringUtils.equals(orientation, Orientation.VERTICAL)) {
				// set span to block which will cause a line break and margin
				// bottom for padding
				itemSpanStyle = CssConstants.Displays.BLOCK;
				itemSpanStyle += CssConstants.getCssStyle(Margins.MARGIN_BOTTOM, padding);
			}
			else {
				// set margin right for padding
				itemSpanStyle = CssConstants.getCssStyle(Margins.MARGIN_RIGHT, padding);
			}
		}
	}

	/**
	 * Indicates whether the components should be rendered in a horizontal or
	 * vertical column
	 * 
	 * @return String orientation
	 * @see org.kuali.rice.kns.uif.UifConstants.Orientation
	 */
	public String getOrientation() {
		return this.orientation;
	}

	/**
	 * Setter for the orientation
	 * 
	 * @param orientation
	 */
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	/**
	 * Amount of separation between each item
	 * 
	 * <p>
	 * For horizontal orientation, this will be the right padding for each item.
	 * For vertical, it will be the bottom padding for each item. The value can
	 * be a fixed length (like px) or percentage
	 * </p>
	 * 
	 * @return
	 */
	public String getPadding() {
		return this.padding;
	}

	/**
	 * Setter for the item padding
	 * 
	 * @param padding
	 */
	public void setPadding(String padding) {
		this.padding = padding;
	}

	/**
	 * Used by the render to set the style on the span element that wraps the
	 * item. By using a wrapping span the items can be aligned based on the
	 * orientation and given the correct padding
	 * 
	 * @return String css style string
	 */
	public String getItemSpanStyle() {
		return this.itemSpanStyle;
	}

	/**
	 * Setter for the span style
	 * 
	 * @param itemSpanStyle
	 */
	public void setItemSpanStyle(String itemSpanStyle) {
		this.itemSpanStyle = itemSpanStyle;
	}

}
