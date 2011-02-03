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
	 * Creates and initializes the Map that holds context information for the
	 * <code>View</code>
	 * 
	 * <p>
	 * Context information provides parameters and other necessary state needed
	 * by the view to determine its own state and behavior. Certain views have
	 * parameters they look for and can be placed into the context Map for
	 * reference. The given parameters Map is what was sent to the view service
	 * for the view request. It can contain zero or more of the view parameters
	 * as well as key value pairs that are not applicable. The map should be
	 * filtered based on the view's needs. Additional key value pairs can also
	 * be placed into the context.
	 * </p>
	 * 
	 * @param parameters
	 *            - Map<String, String> of parameters sent to view request
	 */
	public Map<String, String> createInitialViewContext(View view, Map<String, String> parameters);

	/**
	 * Performs the Initialization phase for the <code>View</code>. During this
	 * phase each component of the tree is invoked to setup state based on the
	 * configuration and request options.
	 * 
	 * <p>
	 * Note the <code>View</code> instance also contains the context Map that
	 * was created based on the parameters sent to the view service
	 * </p>
	 * 
	 * @param view
	 *            - View instance that should be initialized
	 */
	public void performInitialization(View view);

	/**
	 * Executes the ApplyModel phase. During this phase each component of the
	 * tree if invoked to setup any state based on the given model data.
	 * 
	 * @param view
	 *            - View instance that the model should be applied to
	 * @param model
	 *            - Top level object containing the data (could be the form or a
	 *            top level business object, dto)
	 */
	public void performApplyModel(View view, Object model);

	/**
	 * 
	 * @param view
	 */
	public void performUpdateState(View view);

	/**
	 * Invoked when the add line action is chosen for a collection. The
	 * collection path gives the full path to the collection that action was
	 * selected for. Here validation can be performed on the line as well as
	 * further processing on the line such as defaults. If the action is valid
	 * the line should be added to the collection, otherwise errors should be
	 * added to the global <code>MessageMap</code>
	 * 
	 * @param view
	 *            - view instance that is being presented (the action was taken
	 *            on)
	 * @param model
	 *            - Top level object containing the view data including the
	 *            collection and new line
	 * @param collectionPath
	 *            - full path to the collection on the model
	 */
	public void processCollectionAddLine(View view, Object model, String collectionPath);

	/**
	 * Invoked when the delete line action is chosen for a collection. The
	 * collection path gives the full path to the collection that action was
	 * selected for. Here validation can be performed to make sure the action is
	 * allowed. If the action is valid the line should be deleted from the
	 * collection, otherwise errors should be added to the global
	 * <code>MessageMap</code>
	 * 
	 * @param view
	 *            - view instance that is being presented (the action was taken
	 *            on)
	 * @param model
	 *            - Top level object containing the view data including the
	 *            collection
	 * @param collectionPath
	 *            - full path to the collection on the model
	 * @param lineIndex
	 *            - index of the collection line that was selected for removal
	 */
	public void processCollectionDeleteLine(View view, Object model, String collectionPath, int lineIndex);

}
