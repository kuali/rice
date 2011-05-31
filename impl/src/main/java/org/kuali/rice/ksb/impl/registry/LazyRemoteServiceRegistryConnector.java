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

import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.ksb.api.registry.RegistryConfigurations;
import org.kuali.rice.ksb.api.registry.ServiceDescriptor;
import org.kuali.rice.ksb.api.registry.ServiceEndpoint;
import org.kuali.rice.ksb.api.registry.ServiceEndpointStatus;
import org.kuali.rice.ksb.api.registry.ServiceInfo;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;
import org.kuali.rice.ksb.messaging.serviceconnectors.ServiceConnector;
import org.kuali.rice.ksb.messaging.serviceconnectors.ServiceConnectorFactory;
import org.kuali.rice.ksb.util.KSBConstants;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class LazyRemoteServiceRegistryConnector implements ServiceRegistry {

	private final Object initLock = new Object();
	private volatile ServiceRegistry delegate;
	
	@Override
	public List<ServiceInfo> getOnlineServicesByName(QName serviceName)
			throws RiceIllegalArgumentException {
		return getDelegate().getOnlineServicesByName(serviceName);
	}

	@Override
	public List<ServiceInfo> getAllOnlineServices() {
		return getDelegate().getAllOnlineServices();
	}

	@Override
	public List<ServiceInfo> getAllServices() {
		return getDelegate().getAllServices();
	}

	@Override
	public ServiceDescriptor getServiceDescriptor(String serviceDescriptorId)
			throws RiceIllegalArgumentException {
		return getDelegate().getServiceDescriptor(serviceDescriptorId);
	}

	@Override
	public List<ServiceDescriptor> getServiceDescriptors(
			List<String> serviceDescriptorIds)
			throws RiceIllegalArgumentException {
		return getDelegate().getServiceDescriptors(serviceDescriptorIds);
	}

	@Override
	public void publishService(ServiceEndpoint serviceEndpoint)
			throws RiceIllegalArgumentException {
		getDelegate().publishService(serviceEndpoint);
	}

	@Override
	public void publishServices(Set<ServiceEndpoint> serviceEndpoints)
			throws RiceIllegalArgumentException {
		getDelegate().publishServices(serviceEndpoints);
	}

	@Override
	public void removeServiceEndpoint(String serviceId)
			throws RiceIllegalArgumentException {
		getDelegate().removeServiceEndpoint(serviceId);
	}

	@Override
	public void removeServiceEndpoints(Set<String> serviceIds)
			throws RiceIllegalArgumentException {
		getDelegate().removeServiceEndpoints(serviceIds);
	}

	@Override
	public void removeAndPublish(Set<String> removeServiceIds,
			Set<ServiceEndpoint> publishServiceEndpoints) {
		getDelegate().removeAndPublish(removeServiceIds, publishServiceEndpoints);
	}

	@Override
	public void updateStatus(String serviceId, ServiceEndpointStatus status)
			throws RiceIllegalArgumentException {
		getDelegate().updateStatus(serviceId, status);
	}

	@Override
	public void updateStatuses(Set<String> serviceIds,
			ServiceEndpointStatus status) throws RiceIllegalArgumentException {
		getDelegate().updateStatuses(serviceIds, status);
	}

	@Override
	public void takeInstanceOffline(String instanceId)
			throws RiceIllegalArgumentException {
		getDelegate().takeInstanceOffline(instanceId);
	}
	
	private ServiceRegistry getDelegate() {
		// double-checked locking idiom - see Effective Java, Item 71
		ServiceRegistry internalDelegate = this.delegate;
		if (internalDelegate == null) {
			synchronized (initLock) {
				internalDelegate = this.delegate;
				if (internalDelegate == null) {
					this.delegate = internalDelegate = initializeRemoteServiceRegistry();
				}
			}
		}
		return internalDelegate;
	}
	
	protected ServiceRegistry initializeRemoteServiceRegistry() {
		String registryBootstrapUrl = ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.REGISTRY_BOOTSTRAP_URL);
		if (StringUtils.isBlank(registryBootstrapUrl)) {
			throw new RiceRuntimeException("Failed to load registry bootstrap service from url: " + registryBootstrapUrl);
		}
		RegistryConfigurations registryConfigurations = null;
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(registryBootstrapUrl);
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode == HttpStatus.SC_OK) {
				JAXBContext jaxbContext = JAXBContext.newInstance(RegistryConfigurations.class);
			    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			    Object unmarshalled = unmarshaller.unmarshal(method.getResponseBodyAsStream());
			    if (unmarshalled == null) {
			    	throw new RiceRuntimeException("Failed to unmarshal registry configurations from bootstrap url '" + registryBootstrapUrl + "'");
			    } else if (!(unmarshalled instanceof RegistryConfigurations)) {
			    	throw new RiceRuntimeException("Unmarshaled object from service registry bootstrap url '" + registryBootstrapUrl + "' was not a valid instance of " + RegistryConfigurations.class.getName() + ": " + unmarshalled.getClass());
			    }
			    registryConfigurations = (RegistryConfigurations)unmarshalled;
			} else {
				throw new RiceRuntimeException("Attmpt to connect to service registry bootstrap url was unsuccessful.  HTTP status code was " + statusCode + " and page response was:\n" + method.getResponseBodyAsString());
			}
		} catch (Exception e) {
			throw new RiceRuntimeException("Failed to connect to service registry bootstrap url: " + registryBootstrapUrl, e);
		} finally {
			method.releaseConnection();
		}
		if (registryConfigurations.getSoapServiceConfigurations().isEmpty()) {
			throw new RiceRuntimeException("Failed to locate a registry configuration from bootstrap service at url: " + registryBootstrapUrl);
		}
		ServiceConnector connector = ServiceConnectorFactory.getServiceConnector(registryConfigurations.getSoapServiceConfigurations().get(0));
		Object service = connector.getService();
		if (!(service instanceof ServiceRegistry)) {
			throw new RiceRuntimeException("Connected service is not an instance of ServiceRegistry! " + service);
		}
		return (ServiceRegistry)service;
	}

}
