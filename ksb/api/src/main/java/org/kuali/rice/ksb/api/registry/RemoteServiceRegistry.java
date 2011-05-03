package org.kuali.rice.ksb.api.registry;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Defines the interface for a remotely accessible service registry.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RemoteServiceRegistry {

	List<ServiceEndpoint> getServiceEndpointsByName(QName serviceName);
	
	List<ServiceEndpoint> getAllServiceEndpoints();
	
	ServiceEndpoint getServiceEndpoint(String serviceEndpointId);
	
	ServiceEndpoint getServiceEndpoints(List<String> serviceEndpointIds);
	
	List<ServiceHeader> getServiceHeaderByName(QName serviceName);
	
	List<ServiceHeader> getAllServiceHeaders();
	
	ServiceDetails getServiceDetails(String serviceEndpointId);
	
	List<ServiceDetails> getServiceDetails(List<String> serviceEndpointIds);
	
	void publishService(ServiceEndpoint serviceEndpoint);
	
	void publishServices(List<ServiceEndpoint> serviceEndpoint);
		
	void removeServiceEndpoint(String serviceEndpointId);
	
	void removeServiceEndpoints(List<String> serviceEndpointIds);
	
}
