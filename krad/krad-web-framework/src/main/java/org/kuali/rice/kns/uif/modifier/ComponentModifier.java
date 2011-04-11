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
package org.kuali.rice.kns.uif.modifier;

import java.io.Serializable;
import java.util.Set;

import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.core.Component;
import org.kuali.rice.kns.uif.core.Ordered;

/**
 * Provides modification functionality for a <code>Component</code>
 * 
 * <p>
 * <code>ComponentModifier</code> instances are configured by the component's
 * dictionary definition. They can be used to provide dynamic initialization
 * behavior for a certain type of component or all components based on the
 * getSupportedComponents method. In addition they can do dynamic generation of
 * new <code>Component</code> instances, or replacement of the components or
 * their properties.
 * </p>
 * 
 * <p>
 * Modifiers provide for more usability and flexibility of component
 * configuration. For instance if a <code>Group</code> definition is already
 * configured that is close to what the developer needs, but they need to make
 * global changes of the group, then can invoke or create a
 * <code>ComponentModifier</code> for the group to apply those changes. The
 * configuration can then inherit the exiting group definition and then specify
 * the modifier to run with the component's componentModifiers property.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ComponentModifier extends Serializable, Ordered {

	/**
	 * Invoked within the configured phase of the component lifecycle. This is
	 * where the <code>ComponentModifier</code> should perform its work against
	 * the given <code>Component</code> instance
	 * 
	 * @param view
	 *            - the view instance to which the component belongs
	 * @param component
	 *            - the component instance to modify
	 */
	public void performModification(View view, Component component);

	/**
	 * <code>Set</code> of <code>Component</code> classes that may be sent to
	 * the modifier
	 * 
	 * <p>
	 * If an empty or null list is returned, it is assumed the modifier supports
	 * all components. The returned set will be used by the dictionary
	 * validation
	 * </p>
	 * 
	 * @return Set component classes
	 */
	public Set<Class<? extends Component>> getSupportedComponents();

	/**
	 * Indicates what phase of the component lifecycle the
	 * <code>ComponentModifier</code> should be invoked in (INITIALIZE,
	 * APPLY_MODEL, or FINALIZE)
	 * 
	 * @return String view lifecycle phase
	 * @see org.kuali.rice.kns.uif.UifConstants.ViewPhases
	 */
	public String getRunPhase();

	/**
	 * Conditional expression to evaluate for determining whether the component
	 * modifier should be run. If the expression evaluates to true the modifier
	 * will be executed, otherwise it will not be executed
	 * 
	 * @return String el expression that should evaluate to boolean
	 */
	public String getRunCondition();

	/**
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	public int getOrder();

	/**
	 * @see org.kuali.rice.kns.uif.core.Ordered#setOrder(int)
	 */
	public void setOrder(int order);

}
