/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kns.config;

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Exports services in the {@link org.kuali.rice.core.api.resourceloader.GlobalResourceLoader} as beans available to Spring.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GlobalResourceLoaderServiceFactoryBean implements FactoryBean<Object>, InitializingBean {

	private String serviceName;
	private boolean singleton;
	private boolean mustExist;

	// used to prevent a stack overflow when trying to get the service
	private boolean isFetchingService = false;
	
	public GlobalResourceLoaderServiceFactoryBean() {
		this.mustExist = true;
	}
	
	public Object getObject() throws Exception {
		if (isFetchingService) return null; // we already have been invoked, don't recurse, just return null.
		isFetchingService = true;
		try {
			Object service = GlobalResourceLoader.getService(this.getServiceName());
			if (mustExist && service == null) {
				throw new IllegalStateException("Service must exist and no service could be located with name: " + this.getServiceName());
			}
			return service;
		} finally {
			isFetchingService = false;
		}
	}

	public Class<?> getObjectType() {
		return Object.class;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
	public boolean isMustExist() {
		return mustExist;
	}
	
	public void setMustExist(boolean mustExist) {
		this.mustExist = mustExist;
	}
	

	public void afterPropertiesSet() throws Exception {
		if (this.getServiceName() == null) {
			throw new ConfigurationException("No serviceName given.");
		}
	}

}
