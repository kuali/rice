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
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.ksb.api.KsbApiConstants;
import org.kuali.rice.ksb.api.bus.support.SoapServiceConfiguration;
import org.kuali.rice.ksb.api.registry.RegistryConfigurations;
import org.kuali.rice.ksb.api.registry.ServiceDescriptor;
import org.kuali.rice.ksb.api.registry.ServiceInfo;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;
import org.kuali.rice.ksb.api.registry.ServiceRegistryBootstrapResource;
import org.kuali.rice.ksb.impl.bus.ServiceConfigurationSerializationHandler;

/**
 * Implementation of the ServiceRegistryBootstrapResource resource. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServiceRegistryBootstrapResourceImpl implements ServiceRegistryBootstrapResource {

	private ServiceRegistry serviceRegistry;
	
	@Override
	public RegistryConfigurations getAllSoapServiceRegistryConfigurations() {
		List<SoapServiceConfiguration> serviceConfigurations = new ArrayList<SoapServiceConfiguration>();
		List<ServiceInfo> serviceInfos = serviceRegistry.getOnlineServicesByName(new QName(KsbApiConstants.Namespaces.KSB_NAMESPACE_2_0, "serviceRegistrySoap"));
		for (ServiceInfo serviceInfo : serviceInfos) {
			if (serviceInfo.getType().equals(KsbApiConstants.SOAP_SERVICE_TYPE)) {
				ServiceDescriptor serviceDescriptor = serviceRegistry.getServiceDescriptor(serviceInfo.getServiceDescriptorId());
				SoapServiceConfiguration serviceConfiguration = (SoapServiceConfiguration)ServiceConfigurationSerializationHandler.unmarshallFromXml(serviceDescriptor.getDescriptor());
				serviceConfigurations.add(serviceConfiguration);
			}
		}
		return new RegistryConfigurations(serviceConfigurations);
	}
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
