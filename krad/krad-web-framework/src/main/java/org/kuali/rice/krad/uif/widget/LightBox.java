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
package org.kuali.rice.krad.uif.widget;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

/**
 * Used for rendering a lightbox in the UI to display links in dialog popups
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LightBox extends WidgetBase {
	private static final long serialVersionUID = -4004284762546700975L;

    public LightBox() {
		super();
	}

	/**
	 * Overide to cater for passing functions to fancybox without quotes.
	 * If this is not be specific to Fancybox it should be moved to ComponentBase
	 * Builds a string from the underlying <code>Map</code> of component options
	 * that will export that options as a JavaScript Map for use in js and
	 * jQuery plugins.
     *
     * TODO: move to component base
	 * 
	 * @return String of widget options formatted as JS Map
	 */
	@Override
	public String getComponentOptionsJSString() {
		if (getComponentOptions() == null) {
			setComponentOptions(new HashMap<String, String>());
		}
		StringBuffer sb = new StringBuffer();

		sb.append("{");

		for (String optionKey : getComponentOptions().keySet()) {
			String optionValue = getComponentOptions().get(optionKey);

			if (sb.length() > 1) {
				sb.append(",");
			}

			sb.append(optionKey);
			sb.append(":");

			//If an option value starts with { or [, it would be a nested value and it should not use quotes around it
			// If value is a function script do not use quotes
			if (StringUtils.startsWith(optionValue, "{") || StringUtils.startsWith(optionValue, "[") 
					|| (StringUtils.startsWith(optionValue, "function") && StringUtils.endsWith(optionValue, "}"))
					||  optionValue.equals("true") || optionValue.equals("false")){
				sb.append(optionValue);
			}else{
				sb.append("\"" + optionValue + "\"");
			}
		}

		sb.append("}");
		return sb.toString();
	}
	
}
