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
package org.kuali.rice.kns.web.spring.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.service.ViewService;

/**
 * Base form class for views within the KRAD User Interface Framework
 * 
 * <p>
 * Holds properties necessary to determine the <code>View</code> instance that
 * will be used to render the UI
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifFormBase {
	protected String viewId;
	protected String viewName;
	protected String methodToCall;

	protected String selectedCollectionPath;
	protected int selectedLineIndex;

	protected View view;

	protected Map<String, Object> newCollectionLines;

	public UifFormBase() {
		newCollectionLines = new HashMap<String, Object>();
	}

	/**
	 * Calls <code>View</code> service to build a new View instance based on the
	 * given view id. The view instance is then set on the form for further
	 * processing
	 * 
	 * @param request
	 *            - request object containing the query parameters
	 */
	public void populate(HttpServletRequest request) {
		// TODO: remove once view is being retrieved by the binder
		if (StringUtils.isBlank(viewId)) {
			throw new RuntimeException("Unable to get View instance due to blank view id");
		}

		view = getViewService().getView(viewId, request.getParameterMap());
		if (view ==  null) {
			throw new RuntimeException("View not found for id:" + viewId);
		}
		
		// TODO: remove once the view is being pulled from session or reconstructed by the binding
		getViewService().updateView(view, this);
	}

	/**
	 * Unique Id for the <code>View</code> instance. This is specified for a
	 * view in its definition by setting the 'id' property.
	 * 
	 * @return String view id
	 */
	public String getViewId() {
		return this.viewId;
	}

	/**
	 * Setter for the unique view id
	 * 
	 * @param viewId
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * Name for the <code>View</code> instance. This is specified for a view in
	 * its definition by setting the 'id' property. The name is not necessary
	 * unique and cannot be used by itself to retrieve a view. Typically it is
	 * used with other parameters to identify a view with a certain type (view
	 * type)
	 * 
	 * @return String view name
	 */
	public String getViewName() {
		return this.viewName;
	}

	/**
	 * Setter for the view name
	 * 
	 * @param viewName
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Identifies the controller method that should be invoked to fulfill a
	 * request. The value will be matched up against the 'params' setting on the
	 * <code>RequestMapping</code> annotation for the controller method
	 * 
	 * @return String method to call
	 */
	public String getMethodToCall() {
		return this.methodToCall;
	}

	/**
	 * Setter for the method to call
	 * 
	 * @param methodToCall
	 */
	public void setMethodToCall(String methodToCall) {
		this.methodToCall = methodToCall;
	}

	/**
	 * Holds instances for collection add lines. The key of the Map gives the
	 * collection name the line instance applies to, the Map value is an
	 * instance of the collection object class that holds the new line data
	 * 
	 * @return Map<String, Object> new collection lines
	 */
	public Map<String, Object> getNewCollectionLines() {
		return this.newCollectionLines;
	}

	/**
	 * Setter for the new collection lines Map
	 * 
	 * @param newCollectionLines
	 */
	public void setNewCollectionLines(Map<String, Object> newCollectionLines) {
		this.newCollectionLines = newCollectionLines;
	}

	/**
	 * When an action is taken on a collection this property is set to indicate
	 * which collection the action was taken on
	 * 
	 * @return String collection path
	 */
	public String getSelectedCollectionPath() {
		return this.selectedCollectionPath;
	}

	/**
	 * Setter for the selected collection path
	 * 
	 * @param selectedCollectionPath
	 */
	public void setSelectedCollectionPath(String selectedCollectionPath) {
		this.selectedCollectionPath = selectedCollectionPath;
	}

	/**
	 * When an action is taken on a collection line this property is set to
	 * indicate which line (by index) the action was taken on
	 * 
	 * @return int selected line index
	 */
	public int getSelectedLineIndex() {
		return this.selectedLineIndex;
	}

	/**
	 * Setter for the selected line index
	 * 
	 * @param selectedLineIndex
	 */
	public void setSelectedLineIndex(int selectedLineIndex) {
		this.selectedLineIndex = selectedLineIndex;
	}

	/**
	 * View instance associated with the form. Used to render the user interface
	 * 
	 * @return View
	 */
	public View getView() {
		return this.view;
	}

	/**
	 * Setter for the view instance
	 * 
	 * @param view
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * Instance of the <code>ViewService</code> that can be used to retrieve
	 * <code>View</code> instances
	 * 
	 * @return ViewService implementation
	 */
	protected ViewService getViewService() {
		return KNSServiceLocator.getViewService();
	}

}
