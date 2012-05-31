/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.ksb.messaging;

import org.kuali.rice.ksb.cache.RiceCacheAdministrator;
import org.kuali.rice.ksb.cache.RiceCacheAdministratorImpl;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;


/**
 * Returns a {@link RiceCacheAdministrator}.  Starts and registers this 
 * cache with the bus.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RiceCacheExporterFactoryBean implements FactoryBean, InitializingBean {
	
	private String serviceName;
	private RiceCacheAdministratorImpl cache;
	protected RemotedServiceRegistry remotedServiceRegistry;

	public Object getObject() throws Exception {
		return cache;
	}

	public Class getObjectType() {
		return RiceCacheAdministrator.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public String getServiceName() {
	    return this.serviceName;
	}

	public void setServiceName(String serviceName) {
	    this.serviceName = serviceName;
	}

	public void afterPropertiesSet() throws Exception {
		if (cache == null) {
			cache = new RiceCacheAdministratorImpl();
			cache.setServiceName(this.getServiceName());
			cache.setForceRegistryRefresh(false);
			if (remotedServiceRegistry == null) {
				remotedServiceRegistry = KSBServiceLocator.getServiceDeployer();
			}
			cache.setRemotedServiceRegistry(remotedServiceRegistry);
			cache.start();
		}
	}
	

	/**
	 * @return the remotedServiceRegistry
	 */
	public RemotedServiceRegistry getRemotedServiceRegistry() {
		return this.remotedServiceRegistry;
	}

	/**
	 * @param remotedServiceRegistry the remotedServiceRegistry to set
	 */
	public void setRemotedServiceRegistry(
			RemotedServiceRegistry remotedServiceRegistry) {
		this.remotedServiceRegistry = remotedServiceRegistry;
	}

}
