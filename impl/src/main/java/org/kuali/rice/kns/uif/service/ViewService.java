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
package org.kuali.rice.kns.uif.service;

import java.util.Map;

import org.kuali.rice.kns.uif.container.View;

/**
 * Provides service methods for retrieving and updating <code>View</code>
 * instances. The UIF interacts with this service from the client layer to pull
 * information from the View dictionary and manage the View instance through its
 * lifecycle
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ViewService {

	/**
	 * Returns the <code>View</code> entry identified by the given id
	 * <p>
	 * The id matches the id configured for the View through the dictionary.
	 * Before the View is returned the initialize phase is performed
	 * </p>
	 * 
	 * @param viewId
	 *            - unique id for view configured on its definition
	 * @return View instance associated with the id or Null if id is not found
	 */
	public View getViewById(String viewId);

	/**
	 * Returns the <code>View</code> entry identified by the given id
	 * <p>
	 * The id matches the id configured for the View through the dictionary.
	 * Before the View is returned the initialize phase is performed
	 * </p>
	 * <p>
	 * Any configuration sent through the options Map is used to initialize the
	 * View. This map contains present options the view is aware of and will
	 * typically come from request parameters. e.g. For maintenance Views there
	 * is the maintenance type option (new, edit, copy)
	 * </p>
	 * 
	 * @param viewId
	 *            - unique id for view configured on its definition
	 * @param parameters
	 *            - Map of key values pairs that provide configuration for the
	 *            <code>View</code>, this is generally comes from the request
	 *            and can be the request parameter Map itself. Any parameters
	 *            not valid for the View wil be filtered out
	 * @return View instance associated with the id or Null if id is not found
	 */
	public View getView(String viewId, Map<String, String> parameters);

	/**
	 * Performs initialization or updating of the <code>View</code> instance
	 * based on the form (containing models) instance
	 * <p>
	 * Part of the view lifecycle that applies the model data to the view.
	 * Should be called after the model has been populated before the view is
	 * rendered. The main things that occur during this phase are:
	 * <ul>
	 * <li>Generation of dynamic fields (such as collection rows)</li>
	 * <li>Population of <code>AttributeField</code> instances from the model
	 * values</li>
	 * <li>Execution of conditional logic (hidden, read-only, required settings
	 * based on model values)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param view
	 *            - view instance the model should be applied to
	 * @param model
	 *            - Top level object containing the data (could be the form or a
	 *            top level business object, dto)
	 */
	public void applyModel(View view, Object model);
	
	public ViewHelperService getViewHelperService(String viewId);
	
	public String getInquiryViewId(String name, String modelClassName);

}
