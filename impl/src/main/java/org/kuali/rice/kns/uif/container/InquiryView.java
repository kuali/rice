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
package org.kuali.rice.kns.uif.container;

import org.kuali.rice.kns.inquiry.InquiryAuthorizer;
import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.kns.uif.UifConstants.ViewType;

/**
 * Type of <code>View</code> that provides a read-only display of a record of
 * data (object instance)
 * 
 * <p>
 * The <code>InquiryView</code> provides the interface for the Inquiry
 * framework. It works with the <code>Inquirable</code> service and inquiry
 * controller. The view does render a form to support the configuration of
 * actions to perform operations on the data.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryView extends FormView {
	private static final long serialVersionUID = 716926008488403616L;

	private Class<?> objectClassName;

	private Class<? extends InquiryPresentationController> presentationControllerClass;
	private Class<? extends InquiryAuthorizer> authorizerClass;

	public InquiryView() {
		super();

		setViewTypeName(ViewType.INQUIRY);
	}

	/**
	 * <p>
	 * The following initialization is performed:
	 * <ul>
	 * <li>Set the abstractTypeClasses map for the inquiry object path</li>
	 * </ul>
	 * </p>
	 * 
	 * @see org.kuali.rice.kns.uif.container.ContainerBase#performInitialization(org.kuali.rice.kns.uif.container.View)
	 */
	@Override
	public void performInitialization(View view) {
		super.performInitialization(view);

		getAbstractTypeClasses().put(getDefaultBindingObjectPath(), objectClassName);
	}

	/**
	 * Class name for the object the inquiry applies to
	 * 
	 * <p>
	 * The object class name is used to pick up a dictionary entry which will
	 * feed the attribute field definitions and other configuration. In addition
	 * it is to configure the <code>Inquirable</code> which will carry out the
	 * inquiry action
	 * </p>
	 * 
	 * @return Class<?> inquiry object class
	 */
	public Class<?> getObjectClassName() {
		return this.objectClassName;
	}

	/**
	 * Setter for the object class name
	 * 
	 * @param objectClassName
	 */
	public void setObjectClassName(Class<?> objectClassName) {
		this.objectClassName = objectClassName;
	}

	public Class<? extends InquiryPresentationController> getPresentationControllerClass() {
		return this.presentationControllerClass;
	}

	public void setPresentationControllerClass(
			Class<? extends InquiryPresentationController> presentationControllerClass) {
		this.presentationControllerClass = presentationControllerClass;
	}

	public Class<? extends InquiryAuthorizer> getAuthorizerClass() {
		return this.authorizerClass;
	}

	public void setAuthorizerClass(Class<? extends InquiryAuthorizer> authorizerClass) {
		this.authorizerClass = authorizerClass;
	}

}
