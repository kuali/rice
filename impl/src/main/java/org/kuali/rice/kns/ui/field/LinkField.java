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
package org.kuali.rice.kns.ui.field;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LinkField extends FieldLabelBase {
	private String linkLabel;
	private String target;
	private String hrefText;

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
	 * @see org.kuali.rice.kns.ui.ComponentBase#initialize(java.util.Map)
	 */
	@Override
	public void initialize(Map<String, String> options) {
		super.initialize(options);

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

}
