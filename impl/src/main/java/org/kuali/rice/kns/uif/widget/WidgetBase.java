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
package org.kuali.rice.kns.uif.widget;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kns.uif.ComponentBase;

/**
 * Base class for Widgets
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WidgetBase extends ComponentBase implements Widget {
	private static final long serialVersionUID = -917582902829056830L;
	
	private Map<String, String> widgetOptions;

	public WidgetBase() {
		widgetOptions = new HashMap<String, String>();
	}

	/**
	 * @see org.kuali.rice.kns.uif.Component#getComponentTypeName()
	 */
	@Override
	public String getComponentTypeName() {
		return "widget";
	}

	/**
	 * Options that are passed through to the Widget renderer. The Map key is
	 * the option name, with the Map value as the option value. See
	 * documentation on the particular widget render for available options.
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getWidgetOptions() {
		return this.widgetOptions;
	}

	public void setWidgetOptions(Map<String, String> widgetOptions) {
		this.widgetOptions = widgetOptions;
	}

	/**
	 * Builds a string from the underlying <code>Map</code> of widget options
	 * that will export that options as a JavaScript Map
	 * 
	 * @return String of widget options formatted as JS Map
	 */
	public String getWidgetOptionsJSString() {
		StringBuffer sb = new StringBuffer();

		sb.append("{");

		for (String optionKey : widgetOptions.keySet()) {
			String optionValue = widgetOptions.get(optionKey);

			if (sb.length() > 1) {
				sb.append(",");
			}

			sb.append(optionKey);
			sb.append(":");
			sb.append("\"" + optionValue + "\"");
		}

		sb.append("}");

		return sb.toString();
	}

}
