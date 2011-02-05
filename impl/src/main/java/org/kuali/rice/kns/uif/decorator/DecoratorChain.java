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
package org.kuali.rice.kns.uif.decorator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.uif.Component;

/**
 * Holds the chain of decorators and their decorated component for rendering
 * 
 * <p>
 * Built up by reading the decorators of a component. This provides sort of an
 * iterator for the UI to cycle through the decorators. As each decorator is
 * requested is in removed from the chain. Therefore each DecoratorChain
 * instance may only be used one time
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DecoratorChain implements Serializable {
	private static final long serialVersionUID = -3316638408666595224L;

	private Component decoratedComponent;
	private List<ComponentDecorator> decorators;

	public DecoratorChain() {
		decorators = new ArrayList<ComponentDecorator>();
	}

	/**
	 * Initializes the decorator chain from the given component. The chain of
	 * decorators is built up by first asking the component for its decorator,
	 * then asking the decorator for its decorator, and so on
	 * 
	 * @param component
	 *            - component for which the chain should be built
	 */
	public DecoratorChain(Component component) {
		this.decoratedComponent = component;

		decorators = new ArrayList<ComponentDecorator>();

		boolean hasDecorator = (component.getDecorator() != null) && component.getDecorator().isRender();
		if (hasDecorator) {
			decorators.add(component.getDecorator());
			ComponentDecorator previousDecorator = component.getDecorator();
			while (hasDecorator) {
				hasDecorator = (previousDecorator.getDecorator() != null)
						&& previousDecorator.getDecorator().isRender();
				if (hasDecorator) {
					decorators.add(previousDecorator.getDecorator());
					previousDecorator = previousDecorator.getDecorator();
				}
			}
		}
	}

	/**
	 * Indicates whether there are decorators left in the chain
	 * 
	 * @return boolean true if there are decorators remaining in the chain,
	 *         false if they have all been removed
	 */
	public boolean getHasDecorator() {
		return (decorators != null) && (decorators.size() > 0);
	}

	/**
	 * Returns the next decorator in the chain. The decorator is then removed
	 * from the chain
	 * 
	 * @return ComponentDecorator instance for the next in the chain, or Null if
	 *         the chain is empty
	 */
	public ComponentDecorator getNextDecorator() {
		if (getHasDecorator()) {
			ComponentDecorator decorator = decorators.get(decorators.size() - 1);
			decorators.remove(decorators.size() - 1);

			return decorator;
		}

		return null;
	}

	/**
	 * Returns the <code>Component</code> instance that is being decorated
	 * 
	 * @return Component being decorated
	 */
	public Component getDecoratedComponent() {
		return this.decoratedComponent;
	}

	/**
	 * Setter for the decorated component
	 * 
	 * @param decoratedComponent
	 */
	public void setDecoratedComponent(Component decoratedComponent) {
		this.decoratedComponent = decoratedComponent;
	}

	/**
	 * Gets the list of decorators that make up the chain
	 * 
	 * @return List of ComponentDecorator instances
	 */
	public List<ComponentDecorator> getDecorators() {
		return this.decorators;
	}

	/**
	 * Setter for the list of decorators
	 * 
	 * @param decorators
	 */
	public void setDecorators(List<ComponentDecorator> decorators) {
		this.decorators = decorators;
	}

}
