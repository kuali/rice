/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.core.Component;
import org.springframework.util.MethodInvoker;

/**
 * Field instance whose output is produced by invoking a method
 * 
 * <p>
 * Generated fields can be used to produce any kind of HTML output using code.
 * The properties configured for the <code>GeneratedField</code> configure the
 * class and method that should be invoked to render the component. The method
 * that will be invoked should take the <code>GeneratedField</code> instance as
 * a parameter and return a String (which can include state HTML)
 * </p>
 * 
 * <p>
 * If the renderingMethodToCall is set, it is assumed to be a method on the
 * configured <code>ViewHelperService</code>. For invoking other class methods
 * the renderMethodInvoker must be configured
 * </p>
 * 
 * <p>
 * e.g. public String sayHiInBold(GeneratedField field) { return "<b>HI!</b>"; }
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated
 */
public class GeneratedField extends FieldBase {
	private static final long serialVersionUID = 1575182633700024203L;

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneratedField.class);

	private String renderingMethodToCall;
	private MethodInvoker renderingMethodInvoker;

	private String renderOutput;

	public GeneratedField() {
		super();
	}

	/**
	 * The following finalization is done here:
	 * 
	 * <ul>
	 * <li>Setup the method invoker and invoke method to get the render output</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.core.ComponentBase#performFinalize(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.core.Component)
	 */
	@Override
	public void performFinalize(View view, Object model, Component parent) {
		super.performFinalize(view, model, parent);

		if (renderingMethodInvoker == null) {
			renderingMethodInvoker = new MethodInvoker();
		}

		// if method not set on invoker, use renderingMethodToCall
		if (StringUtils.isBlank(renderingMethodInvoker.getTargetMethod())) {
			renderingMethodInvoker.setTargetMethod(renderingMethodToCall);
		}

		// if target class or object not set, use view helper service
		if ((renderingMethodInvoker.getTargetClass() == null) && (renderingMethodInvoker.getTargetObject() == null)) {
			renderingMethodInvoker.setTargetObject(view.getViewHelperService());
		}

		// add the component instance as an argument
		Object[] arguments = new Object[1];
		arguments[0] = this;
		renderingMethodInvoker.setArguments(arguments);

		// invoke method and get render output
		try {
			LOG.debug("Invoking render method: " + renderingMethodInvoker.getTargetMethod() + " for component: "
					+ getId());
			renderingMethodInvoker.prepare();

			renderOutput = (String) renderingMethodInvoker.invoke();
		}
		catch (Exception e) {
			LOG.error("Error invoking rendering method for component: " + getId(), e);
			throw new RuntimeException("Error invoking rendering method for component: " + getId(), e);
		}
	}

	/**
	 * Name of the method that should be invoked for rendering the component
	 * (full method name, without parameters or return type)
	 * 
	 * <p>
	 * Note the method can also be set with the renderingMethodInvoker
	 * targetMethod property. If the method is on the configured
	 * <code>ViewHelperService</code>, only this property needs to be configured
	 * </p>
	 * 
	 * @return String method name
	 */
	public String getRenderingMethodToCall() {
		return this.renderingMethodToCall;
	}

	/**
	 * Setter for the rendering method
	 * 
	 * @param renderingMethodToCall
	 */
	public void setRenderingMethodToCall(String renderingMethodToCall) {
		this.renderingMethodToCall = renderingMethodToCall;
	}

	/**
	 * <code>MethodInvoker</code> instance for the method that should be invoked
	 * for rendering the component
	 * 
	 * <p>
	 * MethodInvoker can be configured to specify the class or object the method
	 * should be called on. For static method invocations, the targetClass
	 * property can be configured. For object invocations, that targetObject
	 * property can be configured
	 * </p>
	 * 
	 * @return MethodInvoker instance
	 */
	public MethodInvoker getRenderingMethodInvoker() {
		return this.renderingMethodInvoker;
	}

	/**
	 * Setter for the method invoker instance
	 * 
	 * @param renderingMethodInvoker
	 */
	public void setRenderingMethodInvoker(MethodInvoker renderingMethodInvoker) {
		this.renderingMethodInvoker = renderingMethodInvoker;
	}

	/**
	 * Rendering output for the component that will be sent as part of the
	 * response (can contain static text and HTML)
	 * 
	 * @return String render output
	 */
	public String getRenderOutput() {
		return this.renderOutput;
	}

}
