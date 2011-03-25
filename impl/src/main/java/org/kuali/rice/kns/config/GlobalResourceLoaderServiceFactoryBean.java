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

import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Exports services in the {@link GlobalResourceLoader} as beans available to Spring.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GlobalResourceLoaderServiceFactoryBean implements FactoryBean, InitializingBean {

	private String serviceName;
	private boolean singleton;
	
	public Object getObject() throws Exception {
		return GlobalResourceLoader.getService(this.getServiceName());
	}

	public Class getObjectType() {
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

	public void afterPropertiesSet() throws Exception {
		if (this.getServiceName() == null) {
			throw new ConfigurationException("No serviceName given.");
		}
	}

}
