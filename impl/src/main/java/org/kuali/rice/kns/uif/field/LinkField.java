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
package org.kuali.rice.kns.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.widget.LightBox;

/**
 * Field that encloses a link element
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LinkField extends FieldBase {
	private static final long serialVersionUID = -1908504471910271148L;

	private String linkLabel;
	private String target;
	private String hrefText;
	private LightBox lightBox;

	public LinkField() {

	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the linkLabel if blank to the Field label</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.core.ComponentBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		if (StringUtils.isBlank(linkLabel)) {
			linkLabel = this.getLabel();
		}
	}

	public String getLinkLabel() {
		return this.linkLabel;
	}

	public void setLinkLabel(String linkLabel) {
		this.linkLabel = linkLabel;
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getHrefText() {
		return this.hrefText;
	}

	public void setHrefText(String hrefText) {
		this.hrefText = hrefText;
	}

	/**
	 * @param lightBox the lightBox to set
	 */
	public void setLightBox(LightBox lightBox) {
		this.lightBox = lightBox;
	}

	/**
	 * @return the lightBox
	 */
	public LightBox getLightBox() {
		return lightBox;
	}

}
