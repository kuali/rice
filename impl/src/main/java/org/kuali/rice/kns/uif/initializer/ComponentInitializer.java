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
package org.kuali.rice.kns.uif.initializer;

import java.util.Set;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.container.View;

/**
 * Provides initialization functionality for a <code>Component</code>
 * <p>
 * <code>ComponentInitializer</code> instances are configured by the component's
 * dictionary definition. They can be used to provide dynamic initialization
 * behavior for a certain type of component or all components based on the
 * getSupportedComponents method
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ComponentInitializer {

	/**
	 * Invoked within the initialization phase of the component lifecycle. This
	 * is where the <code>ComponentInitializer</code> should perform its work.
	 * Any components options are specified with the 'options' argument
	 * 
	 * @param view
	 *            - the view instance to which the component belongs
	 * @param component
	 *            - the component instance to initialize
	 */
	public void performInitialization(View view, Component component);

	/**
	 * <code>Set</code> of <code>Component</code> classes that may be sent to
	 * the initializer
	 * <p>
	 * If an empty or null list is returned, it is assumed the initializer
	 * supports all components. The returned set will be used by dictionary
	 * validators
	 * </p>
	 * 
	 * @return Set component classes
	 */
	public Set<Class> getSupportedComponents();

}
