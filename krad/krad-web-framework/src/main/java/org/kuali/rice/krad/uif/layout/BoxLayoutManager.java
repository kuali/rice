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
package org.kuali.rice.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.CssConstants;
import org.kuali.rice.krad.uif.CssConstants.Padding;
import org.kuali.rice.krad.uif.UifConstants.Orientation;
import org.kuali.rice.krad.uif.container.Container;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.Component;
import org.kuali.rice.krad.uif.field.AttributeField;

/**
 * Layout manager that organizes components in a single row (horizontal) or
 * column (vertical)
 * 
 * <p>
 * Although a table based template could be used, setup is done to also support
 * a CSS based template. The items in the <code>Container</code> instance are
 * rendered sequentially wrapping each one with a span element. The padding
 * property can be configured to space the elements as needed. To achieve a
 * vertical orientation, the span style is set to block. Additional styling can
 * be set for the items by using the itemSpanStyle property.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BoxLayoutManager extends LayoutManagerBase {
	private static final long serialVersionUID = 4467342272983290044L;

	private String orientation;
	private String padding;

	private String itemStyle;
	private boolean layoutFieldErrors;

	public BoxLayoutManager() {
		super();

		orientation = Orientation.HORIZONTAL;
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * <li>Set the itemSpanStyle</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.krad.uif.core.ComponentBase#performInitialization(org.kuali.rice.krad.uif.container.View,org.kuali.rice.krad.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);

		if(StringUtils.isBlank(itemStyle)){
			itemStyle = "";
		}
		
		if(StringUtils.isNotEmpty(padding)) {
			if (StringUtils.equals(orientation, Orientation.VERTICAL)) {
				// set item to block which will cause a line break and margin
				// bottom for padding
				itemStyle += CssConstants.getCssStyle(Padding.PADDING_BOTTOM, padding);
			}
			else {
				// set margin right for padding
				itemStyle += CssConstants.getCssStyle(Padding.PADDING_RIGHT, padding);
			}
		}

        //classes to identify this layout in jQuery and to clear the float correctly in all browsers
        this.addStyleClass("fieldLine");
        this.addStyleClass("clearfix");
        
        for (Component c : container.getItems()) {
            if (c != null) {
                if (StringUtils.equals(orientation, Orientation.HORIZONTAL)) {
                    // horizontal items get a special class
                    c.addStyleClass("boxLayoutHorizontalItem");
                    c.appendToStyle(itemStyle);

                    // in a horizontal box layout errors are placed in a div
                    // next to all fields,
                    // set the errorsField to know that we are using an
                    // alternate container for them
                    if (c instanceof AttributeField) {
                        ((AttributeField) c).getErrorsField().setAlternateContainer(true);
                        layoutFieldErrors = true;
                    }
                }

                if (container.isFieldContainer()) {
                    if (c instanceof AttributeField) {
                        ((AttributeField) c).getErrorsField().setAlternateContainer(true);
                        layoutFieldErrors = true;
                    }
                }
            }
        }
    }

	/**
	 * @see org.kuali.rice.krad.uif.layout.LayoutManagerBase#performFinalize(org.kuali.rice.krad.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.krad.uif.container.Container)
	 */
	@Override
	public void performFinalize(View view, Object model, Container container) {
		super.performFinalize(view, model, container);
	}

	/**
	 * Indicates whether the components should be rendered in a horizontal or
	 * vertical column
	 * 
	 * @return String orientation
	 * @see org.kuali.rice.krad.uif.UifConstants.Orientation
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
	public String getItemStyle() {
		return this.itemStyle;
	}

	/**
	 * Setter for the span style
	 * 
	 * @param itemSpanStyle
	 */
	public void setItemStyle(String itemStyle) {
		this.itemStyle = itemStyle;
	}

	/**
	 * @return the layoutFieldErrors
	 */
	public boolean isLayoutFieldErrors() {
		return this.layoutFieldErrors;
	}

	/**
	 * @param layoutFieldErrors the layoutFieldErrors to set
	 */
	public void setLayoutFieldErrors(boolean layoutFieldErrors) {
		this.layoutFieldErrors = layoutFieldErrors;
	}

}
