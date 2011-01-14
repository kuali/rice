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
package org.kuali.rice.kns.uif.util;

import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.decorator.ComponentDecorator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Applies any configured decorators configured for a <code>Component</code>
 * bean
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ComponentDecoratorBeanPostProcessor implements BeanPostProcessor {

	/**
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Determines if the bean is a <code>Component</code> instance. If so then
	 * applies any configured decorators for the component in the order they
	 * appear within the <code>List</code>. The returned object is the nth
	 * decorated component.
	 * 
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
	 *      java.lang.String)
	 * @see org.kuali.rice.kns.uif.Component#getDecorators
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Component) {
			Component decoratedComponent = (Component) bean;
			for (ComponentDecorator decorator : decoratedComponent.getDecorators().values()) {
				decorator.setDecoratedComponent(decoratedComponent);
				decoratedComponent = decorator;
			}

			return decoratedComponent;
		}

		return bean;
	}

}
