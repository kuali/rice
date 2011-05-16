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

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.ksb.api.registry.ServiceDescriptor;
import org.kuali.rice.ksb.api.registry.ServiceEndpoint;
import org.kuali.rice.ksb.api.registry.ServiceEndpointStatus;
import org.kuali.rice.ksb.api.registry.ServiceInfo;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RestServiceRegistryConnector implements ServiceRegistry {

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#getOnlineServicesByName(javax.xml.namespace.QName)
	 */
	@Override
	public List<ServiceInfo> getOnlineServicesByName(QName serviceName)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#getAllOnlineServices()
	 */
	@Override
	public List<ServiceInfo> getAllOnlineServices() {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS
		return null;
	}
	
	

	@Override
	public List<ServiceInfo> getAllServices() {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#getServiceDescriptor(java.lang.String)
	 */
	@Override
	public ServiceDescriptor getServiceDescriptor(String serviceDescriptorId)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#getServiceDescriptors(java.util.List)
	 */
	@Override
	public List<ServiceDescriptor> getServiceDescriptors(
			List<String> serviceDescriptorIds)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#publishService(org.kuali.rice.ksb.api.registry.ServiceEndpoint)
	 */
	@Override
	public void publishService(ServiceEndpoint serviceEndpoint)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#publishServices(java.util.Set)
	 */
	@Override
	public void publishServices(Set<ServiceEndpoint> serviceEndpoints)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#removeServiceEndpoint(java.lang.String)
	 */
	@Override
	public void removeServiceEndpoint(String serviceId)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#removeServiceEndpoints(java.util.Set)
	 */
	@Override
	public void removeServiceEndpoints(Set<String> serviceIds)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#removeAndPublish(java.util.Set, java.util.Set)
	 */
	@Override
	public void removeAndPublish(Set<String> removeServiceIds,
			Set<ServiceEndpoint> publishServiceEndpoints) {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#updateStatus(java.lang.String, org.kuali.rice.ksb.api.registry.ServiceEndpointStatus)
	 */
	@Override
	public void updateStatus(String serviceId, ServiceEndpointStatus status)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#updateStatuses(java.util.Set, org.kuali.rice.ksb.api.registry.ServiceEndpointStatus)
	 */
	@Override
	public void updateStatuses(Set<String> serviceIds,
			ServiceEndpointStatus status) throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.ksb.api.registry.ServiceRegistry#takeInstanceOffline(java.lang.String)
	 */
	@Override
	public void takeInstanceOffline(String instanceId)
			throws RiceIllegalArgumentException {
		// TODO ewestfal - THIS METHOD NEEDS JAVADOCS

	}

}
