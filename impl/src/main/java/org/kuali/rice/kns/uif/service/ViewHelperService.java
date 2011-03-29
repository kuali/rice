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
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.widget.Inquiry;

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
	 * Populates the <code>View</code> properties from the given request
	 * parameters
	 * 
	 * <p>
	 * The <code>View</code> instance is inspected for fields that have the
	 * <code>RequestParameter</code> annotation and if corresponding parameters
	 * are found in the request parameter map, the request value is used to set
	 * the view property. The Map of parameter name/values that match are placed
	 * in the view so they can be later retrieved to rebuild the view. Custom
	 * <code>ViewServiceHelper</code> implementations can add additional
	 * parameter key/value pairs to the returned map if necessary.
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.core.RequestParameter
	 */
	public void populateViewFromRequestParameters(View view, Map<String, String> parameters);

	/**
	 * Performs the Initialization phase for the <code>View</code>. During this
	 * phase each component of the tree is invoked to setup state based on the
	 * configuration and request options.
	 * 
	 * <p>
	 * The initialize phase is only called once per <code>View</code> lifecycle
	 * (when first requested)
	 * </p>
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
	 * Performs the Initialization phase for the given <code>Component</code>
	 * 
	 * <p>
	 * Can be called for component instances constructed via code or prototypes
	 * to initialize the constructed component
	 * </p>
	 * 
	 * @param view
	 *            - view instance the component belongs to
	 * @param component
	 *            - component instance that should be initialized
	 */
	public void performComponentInitialization(View view, Component component);

	/**
	 * Executes the ApplyModel phase. During this phase each component of the
	 * tree if invoked to setup any state based on the given model data
	 * 
	 * <p>
	 * Part of the view lifecycle that applies the model data to the view.
	 * Should be called after the model has been populated before the view is
	 * rendered. The main things that occur during this phase are:
	 * <ul>
	 * <li>Generation of dynamic fields (such as collection rows)</li>
	 * <li>Execution of conditional logic (hidden, read-only, required settings
	 * based on model values)</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * The update phase can be called multiple times for the view's lifecycle
	 * (typically only once per request)
	 * </p>
	 * 
	 * @param view
	 *            - View instance that the model should be applied to
	 * @param model
	 *            - Top level object containing the data (could be the form or a
	 *            top level business object, dto)
	 */
	public void performApplyModel(View view, Object model);

	/**
	 * The last phase before the view is rendered. Here final preparations can
	 * be made based on the updated view state
	 * 
	 * <p>
	 * The finalize phase runs after the apply model phase and can be called
	 * multiple times for the view's lifecylce (however typically only once per
	 * request)
	 * </p>
	 * 
	 * 
	 * @param view
	 *            - view instance that should be finalized for rendering
	 * @param model
	 *            - top level object containing the data
	 */
	public void performFinalize(View view, Object model);

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
	
	/**
	 * Invoked by the <code>Inquiry</code> widget to build the inquiry link
	 * 
	 * <p>
	 * Note this is used primarily for custom <code>Inquirable</code>
	 * implementations to customize the inquiry class or parameters for an
	 * inquiry. Instead of building the full inquiry link, implementations can
	 * make a callback to
	 * org.kuali.rice.kns.uif.widget.Inquiry.buildInquiryLink(Object, String,
	 * Class<?>, Map<String, String>) given an inquiry class and parameters to
	 * build the link field.
	 * </p>
	 * 
	 * @param dataObject
	 *            - parent object for the inquiry property
	 * @param propertyName
	 *            - name of the property the inquiry is being built for
	 * @param inquiry
	 *            - instance of the inquiry widget being built for the property
	 */
	public void buildInquiryLink(Object dataObject, String propertyName, Inquiry inquiry);

}
