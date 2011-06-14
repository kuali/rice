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
package org.kuali.rice.krad.uif.service;

import java.util.Map;

import org.kuali.rice.krad.uif.container.View;

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
	 * 
	 * <p>
	 * The id matches the id configured for the View through the dictionary. The
	 * view is initialized before being returned
	 * </p>
	 * 
	 * @param viewId
	 *            - unique id for view configured on its definition
	 * @return View instance associated with the id or Null if id is not found
	 */
	public View getViewById(String viewId);

	/**
	 * Returns the <code>View</code> entry identified by the given id
	 * 
	 * <p>
	 * The id matches the id configured for the View through the dictionary. The
	 * view is initialized before being returned
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
	 *            not valid for the View will be filtered out
	 * @return View instance associated with the id or Null if id is not found
	 */
	public View getView(String viewId, Map<String, String> parameters);

	/**
	 * Retrieves the <code>View</code> instance that is of the given view type
	 * and matches the given parameters (that are applicable for that type). If
	 * more than one views exists for the type and parameters, the view type may
	 * choose a default or throw an exception
	 * 
	 * @param viewType
	 *            - name that identifies the view type
	 * @param parameters
	 *            - Map of parameter key/value pairs that are used to select the
	 *            view, the parameters allowed depend on the view type
	 * @return View instance or Null if a matching view was not found
	 */
	public View getViewByType(String viewType, Map<String, String> parameters);

	/**
	 * Applies updates to the view based on the model data. Should be called to
	 * finalize the view before rendering
	 * 
	 * <p>
	 * Performs dynamic generation of fields (such as collection rows),
	 * conditional logic, and state updating (conditional hidden, read-only,
	 * required).
	 * </p>
	 * 
	 * @param view
	 *            - view instance to update
	 * @param model
	 *            - Top level object containing the data (could be the form or a
	 *            top level business object, dto)
	 */
	public void buildView(View view, Object model);

	/**
	 * Returns the <code>View</code> entry identified by the given id and runs
	 * through the complete lifecycle. Used to rebuild the view on a post before
	 * rendering or on a session timeout
	 * 
	 * @param viewId
	 *            - unique id for view configured on its definition
	 * @param model
	 *            - Form object containing the data for the view
	 * @param viewRequestParameters
	 *            - Map of key values pairs that provide parameters for the
	 *            view. This comes from the initial request to set view
	 *            properties that have the <code>RequestParameter</code>
	 *            annotation
	 * @return View instance associated with the id or Null if id is not found
	 */
	public View rebuildView(String viewId, Object model, Map<String, String> viewRequestParameters);

	// TODO: remove once can get beans by type
	public ViewTypeService getViewTypeService(String viewType);

}
