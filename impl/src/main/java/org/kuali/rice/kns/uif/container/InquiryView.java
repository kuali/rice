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
 * <p>
 * Inquiry views are primarily configured by the object class they are
 * associated with. This provides the default dictionary information for the
 * fields. If more than one inquiry view is needed for the same object class,
 * the view name can be used to further identify an unique view
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class InquiryView extends FormView {
	private static final long serialVersionUID = 716926008488403616L;

	private Class<?> dataObjectClassName;

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

		getAbstractTypeClasses().put(getDefaultBindingObjectPath(), dataObjectClassName);
	}

	/**
	 * Class name for the object the inquiry applies to
	 * 
	 * <p>
	 * The object class name is used to pick up a dictionary entry which will
	 * feed the attribute field definitions and other configuration. In addition
	 * it is used to configure the <code>Inquirable</code> which will carry out
	 * the inquiry action
	 * </p>
	 * 
	 * @return Class<?> inquiry object class
	 */
	public Class<?> getDataObjectClassName() {
		return this.dataObjectClassName;
	}

	/**
	 * Setter for the object class name
	 * 
	 * @param dataObjectClassName
	 */
	public void setDataObjectClassName(Class<?> dataObjectClassName) {
		this.dataObjectClassName = dataObjectClassName;
	}

	/**
	 * <code>InquiryPresentationController</code> class that should be used for
	 * the <code>InquiryView</code> instance
	 * 
	 * <p>
	 * The presentation controller is consulted to determine component (group,
	 * field) state such as required, read-only, and hidden. The presentation
	 * controller does not take into account user permissions
	 * </p>
	 * 
	 * @return
	 */
	public Class<? extends InquiryPresentationController> getPresentationControllerClass() {
		return this.presentationControllerClass;
	}

	/**
	 * Setter for the inquiry views presentation controller
	 * 
	 * @param presentationControllerClass
	 */
	public void setPresentationControllerClass(
			Class<? extends InquiryPresentationController> presentationControllerClass) {
		this.presentationControllerClass = presentationControllerClass;
	}

	/**
	 * <code>InquiryAuthorizer</code> class that should be used for the
	 * <code>InquiryView</code> instance
	 * 
	 * <p>
	 * The authorizer class is consulted to determine component (group, field)
	 * state such as required, read-only, and hidden based on the users
	 * permissions. It typically communicates with the Kuali Identity Management
	 * system to determine roles and permissions. It is used with the
	 * presentation controller and dictionary conditional logic to determine the
	 * final component state
	 * </p>
	 * 
	 * @return
	 */
	public Class<? extends InquiryAuthorizer> getAuthorizerClass() {
		return this.authorizerClass;
	}

	/**
	 * Setter for the inquiry views authorizer class
	 * 
	 * @param authorizerClass
	 */
	public void setAuthorizerClass(Class<? extends InquiryAuthorizer> authorizerClass) {
		this.authorizerClass = authorizerClass;
	}

}
