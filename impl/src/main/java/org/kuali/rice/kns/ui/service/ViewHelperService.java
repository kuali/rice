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
package org.kuali.rice.kns.ui.service;

import java.util.Map;

import org.kuali.rice.kns.ui.container.View;

/**
 * Provides methods for implementing the various phases of a <code>View</code>
 * 
 * <ul>
 * <li>Initialize Phase: Invoked when the view is first requested to setup
 * necessary state</li>
 * </ul>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewHelperService {

	/**
	 * Performs the Initialization phase for the <code>View</code>. During this
	 * phase each component of the tree is invoked to setup state based on the
	 * configuration and request options.
	 * 
	 * @param view
	 *            - View instance that should be initialized
	 * @param options
	 *            - Map of options (if any), where the map key is the option
	 *            name and the map value is the option value
	 */
	public void performInitialization(View view, Map<String, String> options);

}
