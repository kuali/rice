/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.ksb.impl.registry;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.ksb.api.registry.RegistryConfigurations;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;
import org.kuali.rice.ksb.api.registry.ServiceRegistryBootstrapResource;
import org.kuali.rice.ksb.messaging.serviceconnectors.ServiceConnector;
import org.kuali.rice.ksb.messaging.serviceconnectors.ServiceConnectorFactory;
import org.kuali.rice.ksb.util.KSBConstants;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ServiceRegistryConnector extends AbstractFactoryBean<ServiceRegistry> {
	
	
	@Override
	public Class<ServiceRegistry> getObjectType() {
		return ServiceRegistry.class;
	}

	@Override
	protected ServiceRegistry createInstance() throws Exception {
		String registryUrl = ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.REGISTRY_BOOTSTRAP_URL);
		if (StringUtils.isBlank(registryUrl)) {
			throw new RiceRuntimeException("Failed to load registry bootstrap service from url: " + registryUrl);
		}
		ServiceRegistryBootstrapResource registryBootstrap = JAXRSClientFactory.create(registryUrl, ServiceRegistryBootstrapResource.class);
		if (registryBootstrap == null) {
			throw new RiceRuntimeException("Failed to load registry bootstrap service from url: " + registryUrl);
		}
		RegistryConfigurations registryConfigurations = registryBootstrap.getAllSoapServiceRegistryConfigurations();
		if (registryConfigurations.getSoapServiceConfigurations().isEmpty()) {
			throw new RiceRuntimeException("Failed to locate a registry configuration from bootstrap service at url: " + registryUrl);
		}
		ServiceConnector connector = ServiceConnectorFactory.getServiceConnector(registryConfigurations.getSoapServiceConfigurations().get(0));
		Object service = connector.getService();
		if (!(service instanceof ServiceRegistry)) {
			throw new RiceRuntimeException("Connected service is not an instance of ServiceRegistry! " + service);
		}
		return (ServiceRegistry)service;
	}

}
