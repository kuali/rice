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
import org.kuali.rice.kns.uif.UifConstants.Orientation;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.View;

/**
 * Layout manager that organizes components in a single row (horizontal) or
 * column (vertical)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BoxLayoutManager extends LayoutManagerBase {
	private static final long serialVersionUID = 4467342272983290044L;
	
	private String orientation;
	private String padding;

	private String itemSpanStyle;

	public BoxLayoutManager() {
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
				itemSpanStyle = "display: block;margin-bottom: " + padding + ";";
			}
			else {
				// set margin right for padding
				itemSpanStyle = "margin-right: " + padding + ";";
			}
		}
	}

	public String getOrientation() {
		return this.orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getPadding() {
		return this.padding;
	}

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
