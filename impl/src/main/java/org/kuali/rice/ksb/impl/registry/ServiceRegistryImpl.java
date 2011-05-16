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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;
import org.kuali.rice.ksb.api.registry.ServiceDescriptor;
import org.kuali.rice.ksb.api.registry.ServiceEndpoint;
import org.kuali.rice.ksb.api.registry.ServiceEndpointStatus;
import org.kuali.rice.ksb.api.registry.ServiceInfo;

/**
 * TODO... 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ServiceRegistryImpl implements ServiceRegistry {

	private ServiceRegistryDao serviceRegistryDao;

	@Override
	public List<ServiceInfo> getOnlineServicesByName(QName serviceName)
			throws RiceIllegalArgumentException {
		if (serviceName == null) {
			throw new RiceIllegalArgumentException("serviceName cannot be null");
		}
		List<ServiceInfoBo> serviceInfoBos = serviceRegistryDao.getOnlineServiceInfosByName(serviceName);
		return convertServiceInfoBoList(serviceInfoBos);
	}

	@Override
	public List<ServiceInfo> getAllOnlineServices() {
		List<ServiceInfoBo> serviceInfoBos = serviceRegistryDao.getAllOnlineServiceInfos();
		return convertServiceInfoBoList(serviceInfoBos);
	}
	
	@Override
	public List<ServiceInfo> getAllServices() {
		List<ServiceInfoBo> serviceInfoBos = serviceRegistryDao.getAllServiceInfos();
		return convertServiceInfoBoList(serviceInfoBos);
	}

	@Override
	public ServiceDescriptor getServiceDescriptor(String serviceDescriptorId)
			throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(serviceDescriptorId)) {
			throw new RiceIllegalArgumentException("serviceDescriptorId cannot be blank");
		}
		ServiceDescriptorBo serviceDescriptorBo = serviceRegistryDao.getServiceDescriptor(serviceDescriptorId);
		return ServiceDescriptorBo.to(serviceDescriptorBo);
	}

	@Override
	public List<ServiceDescriptor> getServiceDescriptors(List<String> serviceDescriptorIds)
			throws RiceIllegalArgumentException {
		if (serviceDescriptorIds == null) {
			throw new RiceIllegalArgumentException("serviceDescriptorIds cannot be null");
		}
		List<ServiceDescriptor> serviceDescriptors = new ArrayList<ServiceDescriptor>();
		for (String serviceDescriptorId : serviceDescriptorIds) {
			ServiceDescriptor serviceDescriptor = getServiceDescriptor(serviceDescriptorId);
			serviceDescriptors.add(serviceDescriptor);
		}
		return Collections.unmodifiableList(serviceDescriptors);
	}

	@Override
	public void publishService(ServiceEndpoint serviceEndpoint)
			throws RiceIllegalArgumentException {
		if (serviceEndpoint == null) {
			throw new RiceIllegalArgumentException("serviceEndpoint cannot be null");
		}
		ServiceDescriptor serviceDescriptor = serviceEndpoint.getDescriptor();
		ServiceDescriptorBo serviceDescriptorBo = ServiceDescriptorBo.from(serviceDescriptor);
		ServiceInfo serviceInfo = serviceEndpoint.getInfo();
		ServiceInfoBo serviceInfoBo = ServiceInfoBo.from(serviceInfo);
		serviceDescriptorBo = serviceRegistryDao.saveServiceDescriptor(serviceDescriptorBo);
		serviceInfoBo.setServiceDescriptorId(serviceDescriptorBo.getId());
		serviceRegistryDao.saveServiceInfo(serviceInfoBo);
	}

	@Override
	public void publishServices(Set<ServiceEndpoint> serviceEndpoints)
			throws RiceIllegalArgumentException {
		if (serviceEndpoints == null) {
			throw new RiceIllegalArgumentException("serviceEndpoints cannot be null");
		}
		for (ServiceEndpoint serviceEndpoint : serviceEndpoints) {
			publishService(serviceEndpoint);
		}
	}

	@Override
	public void removeServiceEndpoint(String serviceId)
			throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(serviceId)) {
			throw new RiceIllegalArgumentException("serviceId cannot be blank");
		}
		ServiceInfoBo serviceInfoBo = serviceRegistryDao.getServiceInfo(serviceId);
		if (serviceInfoBo != null) {
			serviceRegistryDao.removeServiceInfo(serviceInfoBo.getServiceId());
			serviceRegistryDao.removeServiceDescriptor(serviceInfoBo.getServiceDescriptorId());
		}
	}

	@Override
	public void removeServiceEndpoints(Set<String> serviceIds)
			throws RiceIllegalArgumentException {
		if (serviceIds == null) {
			throw new RiceIllegalArgumentException("serviceIds canot be null");
		}
		for (String serviceId : serviceIds) {
			removeServiceEndpoint(serviceId);
		}
	}

	@Override
	public void removeAndPublish(Set<String> removeServiceIds,
			Set<ServiceEndpoint> publishServiceEndpoints) {
		if (removeServiceIds != null) {
			removeServiceEndpoints(removeServiceIds);
		}
		if (publishServiceEndpoints != null) {
			publishServices(publishServiceEndpoints);
		}
	}

	@Override
	public void updateStatus(String serviceId, ServiceEndpointStatus status)
			throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(serviceId)) {
			throw new RiceIllegalArgumentException("serviceId cannot be blank");
		}
		if (status == null) {
			throw new RiceIllegalArgumentException("status cannot be null");
		}
		serviceRegistryDao.updateStatus(serviceId, status.getCode());
	}

	@Override
	public void updateStatuses(Set<String> serviceIds,
			ServiceEndpointStatus status) throws RiceIllegalArgumentException {
		if (serviceIds == null) {
			throw new RiceIllegalArgumentException("serviceIds canot be null");
		}
		if (status == null) {
			throw new RiceIllegalArgumentException("status cannot be null");
		}
		for (String serviceId : serviceIds) {
			updateStatus(serviceId, status);
		}
	}

	@Override
	public void takeInstanceOffline(String instanceId)
			throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(instanceId)) {
			throw new RiceIllegalArgumentException("instanceId cannot be blank");
		}
		serviceRegistryDao.updateStatusForInstanceId(instanceId, ServiceEndpointStatus.OFFLINE.getCode());
	}

	private List<ServiceInfo> convertServiceInfoBoList(List<ServiceInfoBo> serviceInfoBos) {
		List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
		if (serviceInfoBos != null) {
			for (ServiceInfoBo serviceInfoBo : serviceInfoBos) {
				serviceInfos.add(ServiceInfoBo.to(serviceInfoBo));
			}
		}
		return Collections.unmodifiableList(serviceInfos);
	}
	
	public void setServiceRegistryDao(ServiceRegistryDao serviceRegistryDao) {
		this.serviceRegistryDao = serviceRegistryDao;
	}
	
}
