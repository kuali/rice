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
package edu.iu.uis.eden.messaging.config;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.resourceloader.SimpleServiceLocator;

import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

public class ServicesOverrideLifecycle extends BaseLifecycle {
	
	private static final Logger LOG = Logger.getLogger(ServicesOverrideLifecycle.class);
	
	private List<ServiceHolder> overrideServices;
	
	public ServicesOverrideLifecycle(List<ServiceHolder> overrideServices) {
		this.setOverrideServices(overrideServices);
	}
	
	public void start() throws Exception {
		if (this.getOverrideServices() == null) {
			return;
		}
		SimpleServiceLocator locator = (SimpleServiceLocator)KSBResourceLoaderFactory.getRootResourceLoader().getServiceLocator();
		for (ServiceHolder serviceHolder : this.getOverrideServices()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Loading override service " + serviceHolder.getServiceName() + " " + serviceHolder.getService());
			}
			locator.addService(serviceHolder.getServiceName(), serviceHolder.getService());
		}
		setStarted(true);
	}

	public List<ServiceHolder> getOverrideServices() {
		return this.overrideServices;
	}

	public void setOverrideServices(List<ServiceHolder> overrideServices) {
		this.overrideServices = overrideServices;
	}

	
}
