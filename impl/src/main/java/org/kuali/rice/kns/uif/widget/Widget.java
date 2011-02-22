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

import java.util.Map;

import org.kuali.rice.kns.uif.Component;

/**
 * Components that provide a user interface function (besides the basic form
 * handing) should implement the widget interface
 * 
 * <p>
 * Widgets generally provide a special function such as a calendar or navigation
 * element. The can render one or more <code>Field</code> instances and
 * generally have associated client side code
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Widget extends Component {

	/**
	 * Options that are passed through to the Widget renderer. The Map key is
	 * the option name, with the Map value as the option value. See
	 * documentation on the particular widget render for available options.
	 * 
	 * @return Map<String, String> options
	 */
	public Map<String, String> getWidgetOptions();

	/**
	 * Setter for the widget's options
	 * 
	 * @param widgetOptions
	 */
	public void setWidgetOptions(Map<String, String> widgetOptions);

}
