/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package org.kuali.rice.resourceloader;

import javax.xml.namespace.QName;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * A ResourceLoader that is BeanFactoryAware and can be wired inside of Spring to provide
 * resource loading capabilities to that Spring BeanFactory.
 *
 * @author ewestfal
 */
public class SpringBeanFactoryResourceLoader extends BaseResourceLoader implements BeanFactoryAware {

	private BeanFactory beanFactory;

	public SpringBeanFactoryResourceLoader() {
		this(new QName("", "BeanFactoryResourceLoader"));
	}

	public SpringBeanFactoryResourceLoader(QName name) {
		super(name);
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	@Override
	public Object getService(QName serviceName) {
		String resolvedServiceName = resolveServiceName(serviceName);
		if (this.beanFactory.containsBean(resolvedServiceName)) {
			Object service = this.beanFactory.getBean(serviceName.toString());
			if (service != null) {
				return postProcessService(serviceName, service);
			}
		}
		return super.getService(serviceName);
	}

	/**
	 * Resolves the given QName service name to a String representation which is used
	 * to lookup the service in Spring.  Default implementation simply calls toString()
	 * on the QName.
	 */
	protected String resolveServiceName(QName serviceName) {
		return serviceName.toString();
	}



}
