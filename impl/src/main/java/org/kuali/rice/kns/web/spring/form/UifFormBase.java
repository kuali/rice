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

import org.kuali.rice.kns.service.KNSServiceLocator;
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

	public UifFormBase() {

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
	 * Instance of the <code>ViewService</code> that can be used to retrieve
	 * <code>View</code> instances
	 * 
	 * @return ViewService implementation
	 */
	protected ViewService getViewService() {
		return KNSServiceLocator.getViewService();
	}

}
