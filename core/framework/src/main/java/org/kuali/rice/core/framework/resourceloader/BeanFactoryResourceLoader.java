/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.framework.resourceloader;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.springframework.beans.factory.BeanFactory;


/**
 * Wraps a {@link BeanFactory} as a {@link ResourceLoader}.
 * 
 * Does not start or stop the {@link BeanFactory}.  Assumes this is being done 
 * by the application that started it.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class BeanFactoryResourceLoader extends BaseResourceLoader {

	private BeanFactory beanFactory;
	
	public BeanFactoryResourceLoader(QName name, BeanFactory beanFactory) {
		super(name);
		this.beanFactory = beanFactory;
	}

	@Override
	public Object getService(QName serviceName) {
		if (this.beanFactory.containsBean(serviceName.toString())) {
			return this.beanFactory.getBean(serviceName.toString());
		}
		return super.getService(serviceName);
	}
	
}
