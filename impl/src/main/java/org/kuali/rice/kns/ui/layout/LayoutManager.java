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
package org.kuali.rice.kns.ui.layout;

import java.util.Map;

/**
 * This is a description of what this class does - jkneal don't forget to fill
 * this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface LayoutManager {

	/**
	 * Should be called to initialize the layout manager
	 * <p>
	 * This is where layout managers can set defaults and setup other necessary
	 * state. The initialize method should only be called once per layout
	 * manager lifecycle and is invoked within the initialize phase of the view
	 * lifecylce.
	 * </p>
	 * 
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 * @see ViewHelperService#initializeView
	 */
	public void initialize(Map<String, String> options);

}
